<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Briefcase, X, CheckCircle2, Loader2, User, Mail, Phone, MapPin, Calendar, FileText } from 'lucide-vue-next'
import { applyJob, getJobList, type Job } from '@/api/recruitment'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const jobs = ref<Job[]>([])
const loading = ref(false)

const loadJobs = async () => {
  loading.value = true
  try {
    const res = await getJobList({ status: 1 })
    jobs.value = res.data?.records || []
  } catch (e) {
    console.error('加载岗位失败', e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadJobs()
})

const showApplyModal = ref(false)
const selectedJob = ref<Job | null>(null)
const submitting = ref(false)

const applyForm = ref({
  name: '',
  phone: '',
  email: '',
  age: '',
  address: '',
  experience: '',
  motivation: '',
  availability: '',
  agreeTerms: false
})

const openApplyModal = (job: typeof jobs[0]) => {
  if (!userStore.isLoggedIn()) {
    alert('请先登录后再申请岗位')
    router.push('/login')
    return
  }
  selectedJob.value = job
  showApplyModal.value = true
}

const closeModal = () => {
  showApplyModal.value = false
  selectedJob.value = null
  resetForm()
}

const resetForm = () => {
  applyForm.value = {
    name: '',
    phone: '',
    email: '',
    age: '',
    address: '',
    experience: '',
    motivation: '',
    availability: '',
    agreeTerms: false
  }
}

const submitApplication = async () => {
  const form = applyForm.value

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
  if (!form.age || form.age <= 0) {
    alert('请填写您的年龄')
    return
  }
  if (!form.address || !form.address.trim()) {
    alert('请填写您的居住地址')
    return
  }
  if (!form.experience || !form.experience.trim()) {
    alert('请填写相关经验')
    return
  }
  if (!form.motivation || !form.motivation.trim()) {
    alert('请填写申请理由')
    return
  }
  if (!form.availability || !form.availability.trim()) {
    alert('请填写可工作时间')
    return
  }
  if (!form.agreeTerms) {
    alert('请同意志愿者服务条款')
    return
  }

  try {
    submitting.value = true
    
    await applyJob({
      jobId: selectedJob.value?.id || 0,
      name: form.name,
      phone: form.phone,
      email: form.email,
      age: Number(form.age),
      address: form.address,
      resume: form.experience,
      introduction: form.motivation,
      availability: form.availability
    })
    
    alert(`申请提交成功！感谢您申请${selectedJob.value?.title}岗位，我们会尽快与您联系。`)
    closeModal()
  } catch (error: any) {
    alert(error.message || '申请提交失败，请重试')
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="flex flex-col gap-10 max-w-5xl mx-auto w-full">
    <div class="text-center mb-6">
      <h1 class="text-4xl md:text-5xl font-extrabold text-foreground mb-4">加入我们的团队</h1>
      <p class="text-lg text-muted-foreground">
        成为爪印之家大家庭的一员。帮助我们改变无数动物的生活。
      </p>
    </div>

    <div class="flex flex-col gap-6">
      <div v-if="loading" class="text-center py-12 text-muted-foreground">
        加载中...
      </div>

      <div v-else-if="jobs.length === 0" class="text-center py-12 text-muted-foreground">
        暂无招聘中的岗位
      </div>

      <div
        v-else
        v-for="(job, i) in jobs" 
        :key="i" 
        class="flex flex-col md:flex-row items-center justify-between bg-card p-8 rounded-2xl shadow-custom border border-border"
      >
        <div class="flex items-start gap-6 mb-4 md:mb-0">
          <div class="p-4 bg-muted text-foreground rounded-2xl hidden sm:block">
            <Briefcase :size="32" />
          </div>
          <div class="flex flex-col">
            <h3 class="text-2xl font-bold text-foreground mb-2">{{ job.title }}</h3>
            <p class="text-sm text-muted-foreground mb-2">{{ job.description }}</p>
            <div class="flex flex-wrap items-center gap-4 text-sm text-muted-foreground">
              <span class="bg-primary/10 text-primary px-3 py-1 rounded-full font-medium">{{ job.type }}</span>
              <span>{{ job.location }}</span>
            </div>
          </div>
        </div>
        <button 
          @click="openApplyModal(job)"
          class="w-full md:w-auto bg-foreground text-background px-8 py-3 rounded-xl font-bold hover:opacity-80 transition-opacity"
        >
          立即申请
        </button>
      </div>
    </div>

    <!-- 申请表单弹窗 -->
    <div v-if="showApplyModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4 overflow-y-auto">
      <div class="bg-card rounded-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto shadow-2xl">
        <!-- 弹窗头部 -->
        <div class="p-6 border-b border-border flex items-center justify-between sticky top-0 bg-card z-10">
          <div>
            <h3 class="text-xl font-bold text-foreground">申请 {{ selectedJob?.title }}</h3>
            <p class="text-sm text-muted-foreground mt-1">{{ selectedJob?.type }} · {{ selectedJob?.location }}</p>
          </div>
          <button @click="closeModal" class="text-muted-foreground hover:text-foreground transition-colors">
            <X :size="24" />
          </button>
        </div>

        <!-- 表单内容 -->
        <div class="p-6 space-y-5">
          <!-- 基本信息 -->
          <div class="space-y-4">
            <h4 class="font-semibold text-foreground flex items-center gap-2">
              <User :size="18" />
              基本信息
            </h4>
            
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-foreground mb-2">姓名 *</label>
                <input 
                  v-model="applyForm.name" 
                  type="text" 
                  placeholder="您的姓名" 
                  class="w-full p-3 border border-border rounded-xl bg-background focus:outline-primary focus:border-primary transition-colors"
                />
              </div>
              <div>
                <label class="block text-sm font-medium text-foreground mb-2">年龄 *</label>
                <input 
                  v-model.number="applyForm.age" 
                  type="number" 
                  placeholder="您的年龄" 
                  class="w-full p-3 border border-border rounded-xl bg-background focus:outline-primary focus:border-primary transition-colors"
                />
              </div>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-foreground mb-2 flex items-center gap-1">
                  <Phone :size="14" /> 电话 *
                </label>
                <input 
                  v-model="applyForm.phone" 
                  type="tel" 
                  placeholder="您的联系电话" 
                  class="w-full p-3 border border-border rounded-xl bg-background focus:outline-primary focus:border-primary transition-colors"
                />
              </div>
              <div>
                <label class="block text-sm font-medium text-foreground mb-2 flex items-center gap-1">
                  <Mail :size="14" /> 邮箱 *
                </label>
                <input 
                  v-model="applyForm.email" 
                  type="email" 
                  placeholder="您的邮箱地址" 
                  class="w-full p-3 border border-border rounded-xl bg-background focus:outline-primary focus:border-primary transition-colors"
                />
              </div>
            </div>

            <div>
              <label class="block text-sm font-medium text-foreground mb-2 flex items-center gap-1">
                <MapPin :size="14" /> 居住地址 *
              </label>
              <input 
                v-model="applyForm.address" 
                type="text" 
                placeholder="您的详细居住地址" 
                class="w-full p-3 border border-border rounded-xl bg-background focus:outline-primary focus:border-primary transition-colors"
              />
            </div>
          </div>

          <!-- 申请详情 -->
          <div class="space-y-4 pt-4 border-t border-border">
            <h4 class="font-semibold text-foreground flex items-center gap-2">
              <FileText :size="18" />
              申请详情
            </h4>

            <div>
              <label class="block text-sm font-medium text-foreground mb-2">相关经验 *</label>
              <textarea 
                v-model="applyForm.experience" 
                placeholder="请描述您相关的志愿者或工作经验（如有）" 
                rows="3" 
                class="w-full p-3 border border-border rounded-xl bg-background focus:outline-primary focus:border-primary resize-none transition-colors"
              ></textarea>
            </div>

            <div>
              <label class="block text-sm font-medium text-foreground mb-2">申请理由 *</label>
              <textarea 
                v-model="applyForm.motivation" 
                placeholder="请说明您为什么想加入我们，以及您能为动物们带来什么" 
                rows="4" 
                class="w-full p-3 border border-border rounded-xl bg-background focus:outline-primary focus:border-primary resize-none transition-colors"
              ></textarea>
            </div>

            <div>
              <label class="block text-sm font-medium text-foreground mb-2 flex items-center gap-1">
                <Calendar :size="14" /> 可工作时间 *
              </label>
              <input 
                v-model="applyForm.availability" 
                type="text" 
                placeholder="例如：周末全天、工作日晚上等" 
                class="w-full p-3 border border-border rounded-xl bg-background focus:outline-primary focus:border-primary transition-colors"
              />
            </div>
          </div>

          <!-- 服务条款 -->
          <div class="pt-4 border-t border-border">
            <label class="flex items-start gap-3 cursor-pointer">
              <input 
                type="checkbox" 
                v-model="applyForm.agreeTerms" 
                class="mt-1 w-4 h-4 text-primary accent-primary"
              />
              <span class="text-sm text-muted-foreground">
                我已阅读并同意爪印之家的志愿者服务条款，承诺认真履行志愿者职责，爱护动物，遵守组织规定。
              </span>
            </label>
          </div>

          <!-- 提交按钮 -->
          <div class="pt-6 flex justify-end gap-3">
            <button 
              @click="closeModal"
              class="px-6 py-3 rounded-xl font-bold border border-border text-foreground hover:bg-muted transition-colors"
            >
              取消
            </button>
            <button 
              @click="submitApplication"
              :disabled="submitting"
              class="bg-primary text-primary-foreground px-8 py-3 rounded-xl font-bold hover:opacity-90 transition-opacity flex items-center gap-2 disabled:opacity-50"
            >
              <Loader2 v-if="submitting" :size="18" class="animate-spin" />
              <CheckCircle2 v-else :size="18" />
              {{ submitting ? '提交中...' : '提交申请' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>