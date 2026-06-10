<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Gift, Heart, CheckCircle } from 'lucide-vue-next'
import { createDonation } from '@/api/donation'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const amount = ref<number | string>(50)
const amounts = [20, 50, 100, 200, 500]
const customAmount = ref<string>('')
const submitting = ref(false)
const showSuccessModal = ref(false)
const donatedAmount = ref<number>(0)

const benefits = [
  '20元可以喂养一只救助小狗一周。',
  '50元可以提供关键疫苗接种。',
  '100元可以覆盖紧急护理和庇护所费用。'
]

async function handleDonate() {
  if (!userStore.isLoggedIn()) {
    router.push('/login')
    return
  }

  let finalAmount: number
  if (amount.value === 'custom') {
    finalAmount = parseFloat(customAmount.value)
    if (!finalAmount || finalAmount <= 0) {
      alert('请输入有效的捐款金额')
      return
    }
    if (finalAmount < 0.01) {
      alert('捐款金额不能小于0.01元')
      return
    }
  } else {
    finalAmount = amount.value as number
  }

  submitting.value = true
  try {
    const res = await createDonation({ amount: finalAmount })
    donatedAmount.value = res.data.amount
    showSuccessModal.value = true
  } catch (e: any) {
    alert(e.message || '捐款失败，请重试')
  } finally {
    submitting.value = false
  }
}

function closeSuccess() {
  showSuccessModal.value = false
}
</script>

<template>
  <div class="flex flex-col md:flex-row gap-12 max-w-6xl mx-auto w-full bg-card p-8 md:p-16 rounded-3xl shadow-custom border border-border">
    
    <div class="flex-1 flex flex-col justify-center">
      <div class="inline-flex items-center justify-center w-16 h-16 bg-secondary/20 text-secondary rounded-full mb-6">
        <Gift :size="32" />
      </div>
      <h1 class="text-4xl md:text-5xl font-extrabold text-foreground mb-6">您的支持拯救生命</h1>
      <p class="text-lg text-muted-foreground leading-relaxed mb-6">
        爪印之家完全依靠像您这样慷慨的人士的支持。您的捐款提供紧急医疗护理、食物和安全庇护所，确保每只宠物都能获得第二次幸福的机会。
      </p>
      <ul class="flex flex-col gap-4 text-foreground font-medium mb-8">
        <li v-for="(benefit, i) in benefits" :key="i" class="flex items-center gap-3">
          <Heart class="text-primary" :size="20" /> {{ benefit }}
        </li>
      </ul>
    </div>

    <div class="flex-1 bg-background p-8 rounded-3xl border border-border shadow-inner flex flex-col">
      <h3 class="text-2xl font-bold text-foreground mb-8">选择捐款金额</h3>
      
      <div class="grid grid-cols-3 gap-4 mb-6">
        <button 
          v-for="val in amounts" 
          :key="val"
          @click="amount = val"
          :class="[
            'py-4 rounded-xl font-bold text-lg transition-colors border',
            amount === val 
              ? 'bg-primary text-primary-foreground border-primary shadow-sm' 
              : 'bg-card text-foreground border-border hover:border-primary'
          ]"
        >
          ¥{{ val }}
        </button>
        <button 
          @click="amount = 'custom'"
          :class="[
            'py-4 rounded-xl font-bold text-lg transition-colors border',
            amount === 'custom' 
              ? 'bg-primary text-primary-foreground border-primary shadow-sm' 
              : 'bg-card text-foreground border-border hover:border-primary'
          ]"
        >
          自定义
        </button>
      </div>

      <div v-if="amount === 'custom'" class="mb-6">
        <div class="relative">
          <span class="absolute left-4 top-1/2 -translate-y-1/2 text-muted-foreground text-xl font-bold">¥</span>
          <input 
            type="number" 
            v-model="customAmount"
            placeholder="输入金额"
            min="0.01"
            step="0.01"
            class="w-full bg-card border border-border p-4 pl-10 rounded-xl text-lg font-bold focus:outline-none focus:ring-2 focus:ring-primary"
          />
        </div>
      </div>
      
      <div class="mt-auto pt-6">
        <button 
          @click="handleDonate"
          :disabled="submitting"
          :class="[
            'w-full py-5 rounded-xl font-bold text-xl transition-opacity shadow-custom',
            submitting ? 'opacity-60 cursor-not-allowed' : 'hover:opacity-90 cursor-pointer',
            'bg-secondary text-secondary-foreground'
          ]"
        >
          {{ submitting ? '捐款中...' : '立即捐款' }}
        </button>
        <p class="text-center text-sm text-muted-foreground mt-4">
          感谢您的爱心捐赠，每一份善意都将温暖每一个生命。
        </p>
      </div>
    </div>
  </div>

  <div v-if="showSuccessModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50" @click.self="closeSuccess">
    <div class="bg-card rounded-2xl p-8 w-full max-w-md shadow-2xl text-center">
      <div class="inline-flex items-center justify-center w-16 h-16 bg-green-100 text-green-600 rounded-full mb-6 mx-auto">
        <CheckCircle :size="36" />
      </div>
      <h3 class="text-2xl font-bold text-foreground mb-3">捐款成功！</h3>
      <p class="text-muted-foreground mb-2">感谢您的爱心捐赠</p>
      <p class="text-3xl font-extrabold text-primary mb-6">¥{{ donatedAmount }}</p>
      <p class="text-sm text-muted-foreground leading-relaxed mb-6">
        您的善款将用于救助流浪宠物，提供医疗护理、食物和安全庇护所。<br/>感谢您为生命带来的希望！
      </p>
      <button 
        @click="closeSuccess"
        class="w-full bg-primary text-primary-foreground py-3 rounded-xl font-bold text-lg hover:opacity-90 transition-opacity"
      >
        确定
      </button>
    </div>
  </div>
</template>
