<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { User, MapPin, Calendar, Mail, Phone } from 'lucide-vue-next'
import SocialPost from '@/components/SocialPost.vue'
import { getUserById } from '@/api/user'
import { getPostListByUser } from '@/api/social'
import { followUser, unfollowUser, checkFollowStatus as checkFollow } from '@/api/follow'
import type { Post } from '@/api/social'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()
const userId = computed(() => Number(route.params.id))

const loading = ref(true)
const user = ref<any>(null)
const posts = ref<Post[]>([])
const postsLoading = ref(false)
const isFollowing = ref(false)
const followingLoading = ref(false)

const isOwnProfile = computed(() => {
  return userStore.isLoggedIn() && userStore.userInfo?.id === userId.value
})

const loadUser = async () => {
  loading.value = true
  try {
    const res = await getUserById(userId.value)
    user.value = res.data
    if (!isOwnProfile.value) {
      await checkFollowStatus()
    }
  } catch (error) {
    console.error('加载用户信息失败', error)
  } finally {
    loading.value = false
  }
}

const checkFollowStatus = async () => {
  if (!userStore.isLoggedIn()) return
  try {
    const res = await checkFollow(userId.value)
    isFollowing.value = res.data || false
  } catch (error) {
    console.error('检查关注状态失败', error)
  }
}

const handleFollow = async () => {
  if (!userStore.isLoggedIn()) {
    alert('请先登录')
    return
  }

  followingLoading.value = true
  try {
    if (isFollowing.value) {
      await unfollowUser(userId.value)
      isFollowing.value = false
    } else {
      await followUser(userId.value)
      isFollowing.value = true
    }
  } catch (error) {
    console.error('关注操作失败', error)
    alert(isFollowing.value ? '取消关注失败' : '关注失败')
  } finally {
    followingLoading.value = false
  }
}

const loadPosts = async () => {
  postsLoading.value = true
  try {
    const res = await getPostListByUser(userId.value)
    posts.value = res.data?.records || []
  } catch (error) {
    console.error('加载用户帖子失败', error)
  } finally {
    postsLoading.value = false
  }
}

onMounted(() => {
  loadUser()
  loadPosts()
})

const formatBirthday = (birthday: string) => {
  if (!birthday) return '未设置'
  const date = new Date(birthday)
  return date.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' })
}
</script>

<template>
  <div v-if="loading" class="py-20 text-center text-muted-foreground">
    加载中...
  </div>

  <div v-else-if="!user" class="py-20 text-center text-muted-foreground">
    用户不存在
  </div>

  <div v-else class="max-w-4xl mx-auto px-4 py-8">
    <div class="bg-card rounded-3xl shadow-custom border border-border p-8 mb-6">
      <div class="flex flex-col md:flex-row items-center gap-6">
        <div class="w-28 h-28 bg-muted rounded-full flex items-center justify-center overflow-hidden border-4 border-background shadow-sm">
          <img v-if="user.avatar" :src="user.avatar" alt="用户头像" class="w-full h-full object-cover" />
          <User v-else :size="56" class="text-muted-foreground" />
        </div>

        <div class="flex-1 text-center md:text-left">
          <h1 class="text-3xl font-bold text-foreground mb-2">{{ user.nickname || '用户' + user.id }}</h1>
          <p v-if="user.region || user.address" class="text-muted-foreground flex items-center justify-center md:justify-start gap-1 mb-3">
            <MapPin :size="16" />
            {{ user.region || user.address }}
          </p>

          <div class="flex flex-wrap gap-4 justify-center md:justify-start text-sm">
            <span v-if="user.gender" class="flex items-center gap-1 text-muted-foreground">
              {{ user.gender === 'male' ? '♂ 男' : '♀ 女' }}
            </span>
            <span v-if="user.birthday" class="flex items-center gap-1 text-muted-foreground">
              <Calendar :size="14" />
              {{ formatBirthday(user.birthday) }}
            </span>
            <span v-if="user.email" class="flex items-center gap-1 text-muted-foreground">
              <Mail :size="14" />
              {{ user.email }}
            </span>
            <span v-if="user.phone" class="flex items-center gap-1 text-muted-foreground">
              <Phone :size="14" />
              {{ user.phone }}
            </span>
          </div>
        </div>

        <div class="flex flex-col items-center gap-3">
          <div class="text-center">
            <div class="text-2xl font-bold text-foreground">{{ posts.length }}</div>
            <div class="text-sm text-muted-foreground">帖子</div>
          </div>
          <button
            v-if="!isOwnProfile"
            @click="handleFollow"
            :disabled="followingLoading"
            :class="[
              'px-8 py-2.5 rounded-full font-bold text-sm transition-all',
              isFollowing
                ? 'bg-muted text-foreground hover:bg-border'
                : 'bg-primary text-primary-foreground hover:opacity-90',
              followingLoading && 'opacity-50 cursor-not-allowed'
            ]"
          >
            {{ followingLoading ? '...' : (isFollowing ? '已关注' : '+ 关注') }}
          </button>
        </div>
      </div>
    </div>

    <div>
      <h2 class="text-xl font-bold text-foreground mb-4">TA的帖子</h2>

      <div v-if="postsLoading" class="py-12 text-center text-muted-foreground">
        加载帖子中...
      </div>

      <div v-else-if="posts.length === 0" class="py-12 text-center text-muted-foreground bg-card rounded-2xl border border-border">
        该用户还没有发布帖子
      </div>

      <div v-else class="space-y-6">
        <SocialPost v-for="post in posts" :key="post.id" :post="post" />
      </div>
    </div>
  </div>
</template>
