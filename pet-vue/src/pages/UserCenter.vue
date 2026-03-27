<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { User, Settings, Heart, FileText, MapPin, LogOut } from 'lucide-vue-next'
import { useUserStore } from '@/stores/user'
import PetCard from '@/components/PetCard.vue'

const router = useRouter()
const userStore = useUserStore()

const isLoggedIn = computed(() => userStore.isLoggedIn())
const userInfo = computed(() => userStore.userInfo)

onMounted(() => {
  if (!isLoggedIn.value) {
    router.push('/login')
  }
})

function handleLogout() {
  userStore.logout()
  router.push('/')
}
</script>

<template>
  <div v-if="isLoggedIn" class="flex flex-col md:flex-row gap-10">
    
    <div class="w-full md:w-80 flex flex-col gap-6">
      <div class="bg-card p-8 rounded-3xl shadow-custom border border-border flex flex-col items-center text-center">
        <div class="w-32 h-32 bg-muted rounded-full mb-6 flex items-center justify-center overflow-hidden border-4 border-background shadow-sm">
          <img v-if="userInfo?.avatar" :src="userInfo.avatar" alt="头像" class="w-full h-full object-cover" />
          <User v-else :size="64" class="text-muted-foreground" />
        </div>
        <h2 class="text-2xl font-bold text-foreground mb-1">{{ userInfo?.nickname || '用户' }}</h2>
        <p v-if="userInfo?.address" class="text-muted-foreground mb-6 flex items-center justify-center gap-2">
          <MapPin :size="16"/> {{ userInfo.address }}
        </p>
        <p v-else class="text-muted-foreground mb-6">暂无地址信息</p>
        <div class="w-full space-y-3">
          <RouterLink to="/profile/edit" class="block w-full bg-muted text-foreground py-2.5 rounded-xl font-semibold hover:bg-border transition-colors text-center">
            编辑资料
          </RouterLink>
          <button @click="handleLogout" class="w-full flex items-center justify-center gap-2 text-muted-foreground hover:text-destructive transition-colors py-2">
            <LogOut :size="18" />
            退出登录
          </button>
        </div>
      </div>

      <div class="bg-card shadow-custom rounded-3xl p-4 border border-border">
        <div class="flex flex-col gap-2">
          <button class="flex items-center gap-3 w-full p-4 rounded-xl text-left bg-primary text-primary-foreground font-semibold shadow-sm">
            <Heart :size="20"/> 我的领养
          </button>
          <button class="flex items-center gap-3 w-full p-4 rounded-xl text-left text-muted-foreground hover:bg-muted hover:text-foreground transition-colors">
            <FileText :size="20"/> 申请记录
          </button>
          <button class="flex items-center gap-3 w-full p-4 rounded-xl text-left text-muted-foreground hover:bg-muted hover:text-foreground transition-colors">
            <Settings :size="20"/> 设置
          </button>
        </div>
      </div>
    </div>

    <div class="flex-1 flex flex-col gap-8">
      <div class="bg-card p-8 rounded-3xl shadow-custom border border-border">
        <h2 class="text-2xl font-bold text-foreground mb-6 pb-4 border-b border-border">我领养的宠物</h2>
        <div class="flex flex-wrap gap-6">
          <div class="flex-1 min-w-[280px] border-2 border-dashed border-border rounded-2xl flex flex-col items-center justify-center p-10 text-muted-foreground text-center bg-background min-h-[300px]">
            <Heart :size="48" class="mb-4 text-border" />
            <h3 class="text-xl font-bold text-foreground mb-2">准备好迎接爱了吗？</h3>
            <p class="mb-6">浏览宠物，找到您的新伙伴。</p>
            <RouterLink to="/adopt" class="bg-primary text-primary-foreground px-6 py-2.5 rounded-full font-bold hover:opacity-90 transition-opacity">
              发现宠物
            </RouterLink>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
