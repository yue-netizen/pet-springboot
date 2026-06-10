<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { PawPrint } from 'lucide-vue-next'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const form = ref({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  email: '',
  phone: ''
})

const loading = ref(false)
const errorMsg = ref('')

async function handleRegister() {
  if (!form.value.username || !form.value.password || !form.value.nickname) {
    errorMsg.value = '请填写必填项（用户名、密码、昵称）'
    return
  }
  
  if (form.value.password !== form.value.confirmPassword) {
    errorMsg.value = '两次输入的密码不一致'
    return
  }
  
  if (form.value.password.length < 6) {
    errorMsg.value = '密码长度至少6位'
    return
  }
  
  loading.value = true
  errorMsg.value = ''
  
  try {
    await userStore.register({
      username: form.value.username,
      password: form.value.password,
      nickname: form.value.nickname,
      email: form.value.email || undefined,
      phone: form.value.phone || undefined
    })
    alert('注册成功！请登录')
    router.push('/login')
  } catch (e: any) {
    errorMsg.value = e.response?.data?.message || '注册失败，请重试'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen bg-background flex items-center justify-center px-4 py-8">
    <div class="w-full max-w-md">
      <div class="bg-card p-8 md:p-10 rounded-3xl shadow-custom border border-border">
        <div class="text-center mb-8">
          <RouterLink to="/" class="inline-flex items-center gap-2 text-primary mb-4">
            <PawPrint :size="36" :stroke-width="2.5" />
          </RouterLink>
          <h1 class="text-2xl font-bold text-foreground">创建账号</h1>
          <p class="text-muted-foreground mt-2">加入爪印之家大家庭</p>
        </div>

        <form @submit.prevent="handleRegister" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-foreground mb-2">
              用户名 <span class="text-destructive">*</span>
            </label>
            <input
              v-model="form.username"
              type="text"
              placeholder="请输入用户名"
              class="w-full px-4 py-3 rounded-xl border border-border bg-background text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary/50 transition-all"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-foreground mb-2">
              昵称 <span class="text-destructive">*</span>
            </label>
            <input
              v-model="form.nickname"
              type="text"
              placeholder="请输入昵称"
              class="w-full px-4 py-3 rounded-xl border border-border bg-background text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary/50 transition-all"
            />
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-foreground mb-2">
                密码 <span class="text-destructive">*</span>
              </label>
              <input
                v-model="form.password"
                type="password"
                placeholder="请输入密码"
                class="w-full px-4 py-3 rounded-xl border border-border bg-background text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary/50 transition-all"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-foreground mb-2">
                确认密码 <span class="text-destructive">*</span>
              </label>
              <input
                v-model="form.confirmPassword"
                type="password"
                placeholder="请再次输入密码"
                class="w-full px-4 py-3 rounded-xl border border-border bg-background text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary/50 transition-all"
              />
            </div>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-foreground mb-2">邮箱</label>
              <input
                v-model="form.email"
                type="email"
                placeholder="请输入邮箱"
                class="w-full px-4 py-3 rounded-xl border border-border bg-background text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary/50 transition-all"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-foreground mb-2">手机号</label>
              <input
                v-model="form.phone"
                type="tel"
                placeholder="请输入手机号"
                class="w-full px-4 py-3 rounded-xl border border-border bg-background text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary/50 transition-all"
              />
            </div>
          </div>

          <div v-if="errorMsg" class="p-3 rounded-xl bg-destructive/10 text-destructive text-sm">
            {{ errorMsg }}
          </div>

          <button
            type="submit"
            :disabled="loading"
            class="w-full bg-primary text-primary-foreground py-3 rounded-xl font-bold hover:opacity-90 transition-opacity shadow-custom disabled:opacity-50"
          >
            {{ loading ? '注册中...' : '注册' }}
          </button>
        </form>

        <div class="mt-6 text-center">
          <p class="text-muted-foreground">
            已有账号？
            <RouterLink to="/login" class="text-primary font-medium hover:underline">
              立即登录
            </RouterLink>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>
