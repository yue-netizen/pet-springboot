<script setup lang="ts">
import { RouterLink, useRoute, useRouter } from 'vue-router'
import { PawPrint, Heart, User, MessageCircle, Settings, LogOut } from 'lucide-vue-next'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const navLinks = [
  { name: "首页", path: "/" },
  { name: "宠物领养", path: "/adopt" },
  { name: "领养故事", path: "/stories" },
  { name: "关于我们", path: "/about" },
  { name: "领养规则", path: "/rules" },
  { name: "养宠贴士", path: "/tips" },
  { name: "社区", path: "/social" },
]

function handleLogout() {
  userStore.logout()
  router.push('/')
}
</script>

<template>
  <div data-cmp="Layout" class="flex flex-col min-h-screen bg-background">
    <header class="sticky top-0 z-50 w-full bg-card shadow-custom border-b border-border">
      <div class="flex items-center justify-between h-20 max-w-[1440px] mx-auto px-6 md:px-12">
        
        <RouterLink to="/" class="flex items-center gap-3 text-primary hover:opacity-80 transition-opacity">
          <PawPrint :size="32" :stroke-width="2.5" />
          <span class="text-2xl font-bold text-foreground">爪印之家</span>
        </RouterLink>

        <nav class="hidden md:flex items-center gap-8">
          <RouterLink
            v-for="link in navLinks"
            :key="link.path"
            :to="link.path"
            :class="[
              'text-sm font-medium transition-colors hover:text-primary',
              route.path === link.path ? 'text-primary border-b-2 border-primary py-1' : 'text-foreground'
            ]"
          >
            {{ link.name }}
          </RouterLink>
        </nav>

        <div class="flex items-center gap-4">
          <RouterLink to="/donation" class="hidden lg:flex items-center gap-2 bg-secondary text-secondary-foreground px-5 py-2.5 rounded-full font-medium hover:opacity-90 transition-opacity shadow-custom">
            <Heart :size="18" />
            <span>爱心捐赠</span>
          </RouterLink>
          <RouterLink to="/chat" class="p-2.5 text-foreground hover:text-primary transition-colors bg-muted rounded-full">
            <MessageCircle :size="20" />
          </RouterLink>
          <RouterLink v-if="userStore.isAdmin()" to="/admin" class="p-2.5 text-foreground hover:text-primary transition-colors bg-muted rounded-full" title="管理员后台">
            <Settings :size="20" />
          </RouterLink>
          
          <template v-if="userStore.isLoggedIn()">
            <RouterLink to="/profile" class="flex items-center gap-2 px-4 py-2 rounded-full bg-primary text-primary-foreground font-medium hover:opacity-90 transition-opacity">
              <User :size="18" />
              <span>{{ userStore.userInfo?.nickname || '用户' }}</span>
            </RouterLink>
            <button @click="handleLogout" class="p-2.5 text-muted-foreground hover:text-primary transition-colors" title="退出登录">
              <LogOut :size="20" />
            </button>
          </template>
          <template v-else>
            <RouterLink to="/login" class="px-5 py-2.5 rounded-full bg-primary text-primary-foreground font-medium hover:opacity-90 transition-opacity shadow-custom">
              登录 / 注册
            </RouterLink>
          </template>
        </div>
      </div>
    </header>

    <main class="flex-1 flex flex-col w-full max-w-[1440px] mx-auto px-6 md:px-12 py-8 md:py-12">
      <slot></slot>
    </main>

    <footer class="bg-card py-12 mt-16 border-t border-border">
      <div class="max-w-[1440px] mx-auto px-6 md:px-12 flex flex-col md:flex-row justify-between items-center gap-6">
        <div class="flex items-center gap-2 text-primary">
          <PawPrint :size="24" />
          <span class="text-xl font-bold text-foreground">爪印之家</span>
        </div>
        <div class="flex gap-6 text-sm text-muted-foreground">
          <RouterLink to="/rules" class="hover:text-primary">领养规则</RouterLink>
          <RouterLink to="/recruitment" class="hover:text-primary">志愿者招募</RouterLink>
          <RouterLink to="/about" class="hover:text-primary">联系我们</RouterLink>
        </div>
        <p class="text-sm text-muted-foreground">&copy; 2024 爪印之家. 保留所有权利.</p>
      </div>
    </footer>
  </div>
</template>
