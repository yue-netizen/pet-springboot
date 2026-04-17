<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPetById, applyAdoption, type Pet, type AdoptionApply } from '@/api/pet'
import { useUserStore } from '@/stores/user'
import { ArrowLeft, Heart, Share2, CheckCircle2 } from 'lucide-vue-next'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const pet = ref<Pet | null>(null)
const loading = ref(false)
const showAdoptionModal = ref(false)
const submitting = ref(false)

const adoptionForm = ref<AdoptionApply & {
  name: string
  email: string
  applicantAge: number
  housingType: string
  hasPetExperience: string
  familyStatus: string
  agreeHealthCheck: boolean
  agreeNeuter: boolean
  agreeGoodEnvironment: boolean
  agreeTimelyMedical: boolean
}>({
  petId: 0,
  reason: '',
  address: '',
  phone: '',
  name: '',
  email: '',
  applicantAge: 0,
  housingType: '',
  hasPetExperience: '',
  familyStatus: '',
  agreeHealthCheck: false,
  agreeNeuter: false,
  agreeGoodEnvironment: false,
  agreeTimelyMedical: false
})

const housingOptions = ['公寓', '别墅', '平房', '其他']
const experienceOptions = ['有', '无']

const loadPet = async () => {
  try {
    loading.value = true
    const id = Number(route.params.id)
    const res = await getPetById(id)
    pet.value = res.data
    adoptionForm.value.petId = id
  } catch (error) {
    console.error('加载宠物详情失败', error)
  } finally {
    loading.value = false
  }
}

const openAdoptionModal = () => {
  if (!userStore.isLoggedIn()) {
    router.push('/login')
    return
  }
  showAdoptionModal.value = true
}

const submitAdoption = async () => {
  const form = adoptionForm.value

  if (!form.name || !form.name.trim()) {
    alert('请填写您的姓名')
    return
  }
  if (!form.phone || !form.phone.trim()) {
    alert('请填写您的联系电话')
    return
  }
  if (!form.email || !form.email.trim()) {
    alert('请填写您的邮箱地址')
    return
  }
  if (!form.applicantAge || form.applicantAge <= 0) {
    alert('请填写您的年龄')
    return
  }
  if (!form.address || !form.address.trim()) {
    alert('请填写您的居住地址')
    return
  }
  if (!form.housingType) {
    alert('请选择居住类型')
    return
  }
  if (!form.hasPetExperience) {
    alert('请选择是否有养宠物经验')
    return
  }
  if (!form.familyStatus || !form.familyStatus.trim()) {
    alert('请填写家庭成员情况')
    return
  }
  if (!form.reason || !form.reason.trim()) {
    alert('请填写领养原因')
    return
  }
  if (!form.agreeHealthCheck) {
    alert('请勾选：定期带宠物去做健康检查和接种疫苗')
    return
  }
  if (!form.agreeNeuter) {
    alert('请勾选：适龄宠物进行绝育手术')
    return
  }
  if (!form.agreeGoodEnvironment) {
    alert('请勾选：提供良好的生活环境，不虐待、不遗弃')
    return
  }
  if (!form.agreeTimelyMedical) {
    alert('请勾选：在宠物生病时及时就医')
    return
  }

  try {
    submitting.value = true
    await applyAdoption(adoptionForm.value)
    alert('申请提交成功！我们会尽快审核您的申请。')
    showAdoptionModal.value = false
    adoptionForm.value = {
      petId: pet.value?.id || 0,
      reason: '',
      address: '',
      phone: '',
      name: '',
      email: '',
      applicantAge: 0,
      housingType: '',
      hasPetExperience: '',
      familyStatus: '',
      agreeHealthCheck: false,
      agreeNeuter: false,
      agreeGoodEnvironment: false,
      agreeTimelyMedical: false
    }
  } catch (error: any) {
    alert(error.message || '申请提交失败，请重试')
  } finally {
    submitting.value = false
  }
}

const formatDate = (date: any) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' })
}

onMounted(() => {
  loadPet()
})
</script>

<template>
  <div class="min-h-screen bg-background py-8 px-4 md:px-8">
    <div class="max-w-6xl mx-auto">
      <button @click="router.back()" class="flex items-center gap-2 text-muted-foreground hover:text-foreground mb-6 transition-colors">
        <ArrowLeft :size="20" />
        <span>返回</span>
      </button>

      <div v-if="loading" class="flex justify-center py-20">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
      </div>

      <div v-else-if="pet" class="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <div class="relative">
          <img :src="pet.image" :alt="pet.name" class="w-full aspect-square object-cover rounded-2xl shadow-lg" />
        </div>

        <div class="bg-card p-8 rounded-2xl shadow-custom border border-border">
          <h1 class="text-4xl font-bold text-foreground mb-4">{{ pet.name }}</h1>
          
          <div class="flex flex-wrap gap-3 mb-6">
            <span class="px-3 py-1 bg-secondary text-secondary-foreground rounded-full text-sm font-medium">
              {{ pet.type === '狗' ? '狗' : pet.type === '猫' ? '猫' : pet.type }}
            </span>
            <span class="px-3 py-1 bg-secondary text-secondary-foreground rounded-full text-sm font-medium">
              {{ pet.age }}
            </span>
            <span class="px-3 py-1 bg-secondary text-secondary-foreground rounded-full text-sm font-medium">
              {{ pet.gender === '公' ? '公' : pet.gender === '母' ? '母' : pet.gender }}
            </span>
            <span class="px-3 py-1 bg-green-100 text-green-700 rounded-full text-sm font-medium" v-if="pet.status === 1">
              可领养
            </span>
          </div>

          <div class="space-y-4 mb-8">
            <div class="flex items-center justify-between py-3 border-b border-border">
              <span class="text-muted-foreground font-medium">体型：</span>
              <span class="text-foreground">{{ pet.size || '-' }}</span>
            </div>
            <div class="flex items-center justify-between py-3 border-b border-border">
              <span class="text-muted-foreground font-medium">健康状况：</span>
              <span class="text-foreground">{{ pet.healthStatus || '-' }}</span>
            </div>
            <div class="flex items-center justify-between py-3 border-b border-border">
              <span class="text-muted-foreground font-medium">性格特点：</span>
              <span class="text-foreground">{{ pet.personality || '-' }}</span>
            </div>
            <div class="flex items-center justify-between py-3 border-b border-border">
              <span class="text-muted-foreground font-medium">所在救助站：</span>
              <span class="text-primary">爱心救助站</span>
            </div>
            <div class="flex items-center justify-between py-3 border-b border-border">
              <span class="text-muted-foreground font-medium">发布时间：</span>
              <span class="text-foreground">{{ formatDate(pet.createTime) }}</span>
            </div>
          </div>

          <div class="flex gap-4">
            <button 
              @click="openAdoptionModal" 
              class="flex-1 bg-primary text-primary-foreground py-4 px-6 rounded-xl font-bold hover:opacity-90 transition-opacity flex items-center justify-center gap-2"
              :disabled="pet.status !== 1"
            >
              <CheckCircle2 :size="20" />
              {{ pet.status === 1 ? '申请领养' : '已被领养' }}
            </button>
            <button class="px-6 py-4 border border-border rounded-xl font-medium hover:bg-secondary transition-colors flex items-center justify-center gap-2">
              <Heart :size="20" />
              收藏
            </button>
            <button class="px-6 py-4 border border-border rounded-xl font-medium hover:bg-secondary transition-colors flex items-center justify-center gap-2">
              <Share2 :size="20" />
              分享
            </button>
          </div>
        </div>
      </div>

      <div v-if="pet && pet.story" class="mt-12 bg-card p-8 rounded-2xl shadow-custom border border-border">
        <h2 class="text-2xl font-bold text-foreground mb-6 pb-3 border-b-2 border-primary inline-block">宠物故事</h2>
        <div class="prose prose-lg max-w-none text-muted-foreground leading-relaxed whitespace-pre-line">
          {{ pet.story }}
        </div>
      </div>
    </div>

    <div v-if="showAdoptionModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4 overflow-y-auto">
      <div class="bg-card rounded-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto shadow-2xl">
        <div class="p-6 border-b border-border flex items-center justify-between sticky top-0 bg-card">
          <h3 class="text-xl font-bold text-foreground">申请领养 {{ pet?.name }}</h3>
          <button @click="showAdoptionModal = false" class="text-muted-foreground hover:text-foreground">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
          </button>
        </div>

        <div class="p-6 space-y-5">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
            <div>
              <label class="block text-sm font-medium text-foreground mb-2">姓名</label>
              <input v-model="adoptionForm.name" type="text" placeholder="您的姓名" class="w-full p-3 border border-border rounded-xl bg-background focus:outline-primary" />
            </div>
            <div>
              <label class="block text-sm font-medium text-foreground mb-2">电话</label>
              <input v-model="adoptionForm.phone" type="text" placeholder="您的联系电话" class="w-full p-3 border border-border rounded-xl bg-background focus:outline-primary" />
            </div>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
            <div>
              <label class="block text-sm font-medium text-foreground mb-2">邮箱</label>
              <input v-model="adoptionForm.email" type="email" placeholder="您的邮箱地址" class="w-full p-3 border border-border rounded-xl bg-background focus:outline-primary" />
            </div>
            <div>
              <label class="block text-sm font-medium text-foreground mb-2">年龄</label>
              <input v-model.number="adoptionForm.applicantAge" type="number" placeholder="您的年龄" class="w-full p-3 border border-border rounded-xl bg-background focus:outline-primary" />
            </div>
          </div>

          <div>
            <label class="block text-sm font-medium text-foreground mb-2">居住地址</label>
            <input v-model="adoptionForm.address" type="text" placeholder="您的详细居住地址" class="w-full p-3 border border-border rounded-xl bg-background focus:outline-primary" />
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
            <div>
              <label class="block text-sm font-medium text-foreground mb-2">居住类型</label>
              <select v-model="adoptionForm.housingType" class="w-full p-3 border border-border rounded-xl bg-background focus:outline-primary">
                <option value="">请选择</option>
                <option v-for="opt in housingOptions" :key="opt" :value="opt">{{ opt }}</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-foreground mb-2">是否有养宠物经验</label>
              <select v-model="adoptionForm.hasPetExperience" class="w-full p-3 border border-border rounded-xl bg-background focus:outline-primary">
                <option value="">请选择</option>
                <option v-for="opt in experienceOptions" :key="opt" :value="opt">{{ opt }}</option>
              </select>
            </div>
          </div>

          <div>
            <label class="block text-sm font-medium text-foreground mb-2">家庭成员情况</label>
            <textarea v-model="adoptionForm.familyStatus" placeholder="请描述您的家庭成员情况，是否有老人、小孩等" rows="3" class="w-full p-3 border border-border rounded-xl bg-background focus:outline-primary resize-none"></textarea>
          </div>

          <div>
            <label class="block text-sm font-medium text-foreground mb-2">领养原因</label>
            <textarea v-model="adoptionForm.reason" placeholder="请说明您想领养这只宠物的原因" rows="4" class="w-full p-3 border border-border rounded-xl bg-background focus:outline-primary resize-none"></textarea>
          </div>

          <div class="pt-4">
            <p class="text-sm font-medium text-foreground mb-3">您是否能做到以下几点？</p>
            <div class="space-y-3">
              <label class="flex items-start gap-3 cursor-pointer">
                <input type="checkbox" v-model="adoptionForm.agreeHealthCheck" class="mt-1 w-4 h-4 text-primary" />
                <span class="text-muted-foreground">定期带宠物去做健康检查和接种疫苗</span>
              </label>
              <label class="flex items-start gap-3 cursor-pointer">
                <input type="checkbox" v-model="adoptionForm.agreeNeuter" class="mt-1 w-4 h-4 text-primary" />
                <span class="text-muted-foreground">适龄宠物进行绝育手术</span>
              </label>
              <label class="flex items-start gap-3 cursor-pointer">
                <input type="checkbox" v-model="adoptionForm.agreeGoodEnvironment" class="mt-1 w-4 h-4 text-primary" />
                <span class="text-muted-foreground">提供良好的生活环境，不虐待、不遗弃</span>
              </label>
              <label class="flex items-start gap-3 cursor-pointer">
                <input type="checkbox" v-model="adoptionForm.agreeTimelyMedical" class="mt-1 w-4 h-4 text-primary" />
                <span class="text-muted-foreground">在宠物生病时及时就医</span>
              </label>
            </div>
          </div>

          <div class="pt-6 flex justify-end">
            <button 
              @click="submitAdoption" 
              :disabled="submitting"
              class="bg-primary text-primary-foreground px-8 py-3 rounded-xl font-bold hover:opacity-90 transition-opacity flex items-center gap-2"
            >
              <CheckCircle2 :size="18" />
              {{ submitting ? '提交中...' : '提交领养申请' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
