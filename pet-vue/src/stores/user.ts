import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, register as registerApi, getUserInfo } from '@/api/user'
import type { LoginParams, RegisterParams, UserInfo } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(
    JSON.parse(localStorage.getItem('userInfo') || 'null')
  )
  const role = ref<number>(Number(localStorage.getItem('role')) || 0)

  async function login(params: LoginParams) {
    const res = await loginApi(params)
    token.value = res.data.token
    role.value = res.data.role
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('role', String(res.data.role))
    
    userInfo.value = {
      id: res.data.userId,
      username: res.data.username,
      nickname: res.data.nickname,
      avatar: res.data.avatar || '',
      email: '',
      phone: '',
      address: '',
      status: 1
    }
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    
    return res.data
  }

  async function register(params: RegisterParams) {
    await registerApi(params)
  }

  async function fetchUserInfo() {
    if (!token.value) return
    try {
      const res = await getUserInfo()
      userInfo.value = res.data
      localStorage.setItem('userInfo', JSON.stringify(res.data))
    } catch {
      logout()
    }
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    role.value = 0
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('role')
  }

  function isLoggedIn() {
    return !!token.value
  }

  function isAdmin() {
    return role.value === 1
  }

  return {
    token,
    userInfo,
    role,
    login,
    register,
    logout,
    fetchUserInfo,
    isLoggedIn,
    isAdmin,
  }
})
