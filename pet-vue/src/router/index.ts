import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/pages/Home.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/pages/Login.vue'),
    meta: { noLayout: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/pages/Register.vue'),
    meta: { noLayout: true }
  },
  {
    path: '/about',
    name: 'About',
    component: () => import('@/pages/AboutUs.vue')
  },
  {
    path: '/adopt',
    name: 'Adopt',
    component: () => import('@/pages/AdoptPets.vue')
  },
  {
    path: '/pet/:id',
    name: 'PetDetail',
    component: () => import('@/pages/PetDetail.vue')
  },
  {
    path: '/stories',
    name: 'Stories',
    component: () => import('@/pages/Stories.vue')
  },
  {
    path: '/story/:id',
    name: 'StoryDetail',
    component: () => import('@/pages/StoryDetail.vue')
  },
  {
    path: '/rules',
    name: 'Rules',
    component: () => import('@/pages/Rules.vue')
  },
  {
    path: '/tips',
    name: 'Tips',
    component: () => import('@/pages/Tips.vue')
  },
  {
    path: '/tips/:id',
    name: 'TipsDetail',
    component: () => import('@/pages/TipsDetail.vue')
  },
  {
    path: '/recruitment',
    name: 'Recruitment',
    component: () => import('@/pages/Recruitment.vue')
  },
  {
    path: '/donation',
    name: 'Donation',
    component: () => import('@/pages/Donation.vue')
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/pages/UserCenter.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/user/:id',
    name: 'UserProfile',
    component: () => import('@/pages/UserProfile.vue')
  },
  {
    path: '/social',
    name: 'Social',
    component: () => import('@/pages/Social.vue')
  },
  {
    path: '/chat',
    name: 'Chat',
    component: () => import('@/pages/Chat.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/pages/Admin.vue'),
    meta: { requiresAdmin: true, noLayout: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  const userStore = useUserStore()
  
  if (to.meta.requiresAdmin) {
    if (!userStore.isAdmin()) {
      next('/')
      return
    }
  }
  
  if (to.meta.requiresAuth) {
    if (!userStore.isLoggedIn()) {
      next('/login')
      return
    }
  }
  
  next()
})

export default router
