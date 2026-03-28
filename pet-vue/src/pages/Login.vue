<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { PawPrint } from 'lucide-vue-next'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const username = ref('')
const password = ref('')
const loading = ref(false)
const errorMsg = ref('')

async function handleLogin() {
  if (!username.value || !password.value) {
    errorMsg.value = '请输入用户名和密码'
    return
  }
  
  loading.value = true
  errorMsg.value = ''
  
  try {
    await userStore.login({
      username: username.value,
      password: password.value
    })
    if (userStore.isAdmin()) {
      router.push('/admin')
    } else {
      router.push('/')
    }
  } catch (e: any) {
    errorMsg.value = e.response?.data?.message || '登录失败，请重试'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen bg-background flex items-center justify-center px-4">
    <div class="w-full max-w-md">
      <div class="bg-card p-8 md:p-10 rounded-3xl shadow-custom border border-border">
        <div class="text-center mb-8">
          <RouterLink to="/" class="inline-flex items-center gap-2 text-primary mb-4">
            <PawPrint :size="36" :stroke-width="2.5" />
          </RouterLink>
          <h1 class="text-2xl font-bold text-foreground">欢迎回来</h1>
          <p class="text-muted-foreground mt-2">登录您的爪印之家账号</p>
        </div>

        <form @submit.prevent="handleLogin" class="space-y-5">
          <div>
            <label class="block text-sm font-medium text-foreground mb-2">用户名</label>
            <input
              v-model="username"
              type="text"
              placeholder="请输入用户名"
              class="w-full px-4 py-3 rounded-xl border border-border bg-background text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary/50 transition-all"
            />
          </div>

          <div>
            <label class="block text-sm font-medium text-foreground mb-2">密码</label>
            <input
              v-model="password"
              type="password"
              placeholder="请输入密码"
              class="w-full px-4 py-3 rounded-xl border border-border bg-background text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-primary/50 transition-all"
            />
          </div>

          <div v-if="errorMsg" class="p-3 rounded-xl bg-destructive/10 text-destructive text-sm">
            {{ errorMsg }}
          </div>

          <button
            type="submit"
            :disabled="loading"
            class="w-full bg-primary text-primary-foreground py-3 rounded-xl font-bold hover:opacity-90 transition-opacity shadow-custom disabled:opacity-50"
          >
            {{ loading ? '登录中...' : '登录' }}
          </button>
        </form>

        <div class="mt-6 text-center">
          <p class="text-muted-foreground">
            还没有账号？
            <RouterLink to="/register" class="text-primary font-medium hover:underline">
              立即注册
            </RouterLink>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>
