<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { User, Heart, FileText, MapPin, LogOut, Image, MessageSquare, Trash2, Eye, Users } from 'lucide-vue-next'
import { useUserStore } from '@/stores/user'
import PetCard from '@/components/PetCard.vue'
import SocialPost from '@/components/SocialPost.vue'
import { getMyAdoptions, getMyAdoptionRecords, type AdoptionRecord, type Pet } from '@/api/pet'
import { getMyPosts, getMyLikedPosts, deletePost, type Post } from '@/api/social'
import { getMyLikedStories, type StoryDetail } from '@/api/story'
import { uploadImage } from '@/api/social'
import { updateUserInfo } from '@/api/user'
import { getMyFollows, unfollowUser } from '@/api/follow'

const router = useRouter()
const userStore = useUserStore()

const isLoggedIn = computed(() => userStore.isLoggedIn())
const userInfo = computed(() => userStore.userInfo)

const activeTab = ref('adoptions')
const loading = ref(false)
const avatarFile = ref<File | null>(null)
const uploadingAvatar = ref(false)

const adoptions = ref<Pet[]>([])
const adoptionRecords = ref<AdoptionRecord[]>([])
const myPosts = ref<Post[]>([])
const likedPosts = ref<Post[]>([])
const likedStories = ref<StoryDetail[]>([])
const follows = ref<any[]>([])

const likedActiveTab = ref('posts')

onMounted(() => {
  if (!isLoggedIn.value) {
    router.push('/login')
  } else {
    loadData()
  }
})

function handleLogout() {
  userStore.logout()
  router.push('/')
}

async function loadData() {
  if (activeTab.value === 'adoptions') {
    await loadAdoptions()
  } else if (activeTab.value === 'records') {
    await loadAdoptionRecords()
  } else if (activeTab.value === 'posts') {
    await loadMyPosts()
  } else if (activeTab.value === 'liked') {
    await loadLikedData()
  } else if (activeTab.value === 'follows') {
    await loadFollows()
  }
}

async function loadAdoptions() {
  loading.value = true
  try {
    const res = await getMyAdoptions(1, 100)
    adoptions.value = res.data.records
  } catch (e) {
    console.error('加载我的领养失败', e)
  } finally {
    loading.value = false
  }
}

async function loadAdoptionRecords() {
  loading.value = true
  try {
    const res = await getMyAdoptionRecords(1, 100)
    adoptionRecords.value = res.data.records
  } catch (e) {
    console.error('加载申请记录失败', e)
  } finally {
    loading.value = false
  }
}

async function loadMyPosts() {
  loading.value = true
  try {
    const res = await getMyPosts(1, 100)
    myPosts.value = res.data.records
  } catch (e) {
    console.error('加载我的帖子失败', e)
  } finally {
    loading.value = false
  }
}

async function loadLikedData() {
  loading.value = true
  try {
    if (likedActiveTab.value === 'posts') {
      const res = await getMyLikedPosts(1, 100)
      likedPosts.value = res.data.records
    } else {
      const res = await getMyLikedStories(1, 100)
      likedStories.value = res.data.records
    }
  } catch (e) {
    console.error('加载喜欢内容失败', e)
  } finally {
    loading.value = false
  }
}

async function loadFollows() {
  loading.value = true
  try {
    const res = await getMyFollows(1, 100)
    follows.value = res.data?.records || []
  } catch (e) {
    console.error('加载关注列表失败', e)
  } finally {
    loading.value = false
  }
}

async function handleUnfollow(userId: number) {
  if (!confirm('确定要取消关注吗？')) return
  try {
    await unfollowUser(userId)
    follows.value = follows.value.filter(u => u.id !== userId)
  } catch (e) {
    console.error('取消关注失败', e)
  }
}

async function handleDeletePost(id: number) {
  if (!confirm('确定要删除这条帖子吗？')) return
  try {
    await deletePost(id)
    await loadMyPosts()
  } catch (e) {
    console.error('删除帖子失败', e)
  }
}

async function handleAvatarChange(event: Event) {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (!file) return
  
  avatarFile.value = file
  uploadingAvatar.value = true
  try {
    const res = await uploadImage(file)
    const url = res.data
    await updateUserInfo({ avatar: url })
    await userStore.fetchUserInfo()
  } catch (e) {
    console.error('上传头像失败', e)
  } finally {
    uploadingAvatar.value = false
    avatarFile.value = null
  }
}

function getStatusText(status: number) {
  const map: Record<number, string> = {
    0: '待审核',
    1: '已通过',
    2: '已拒绝'
  }
  return map[status] || '未知'
}

function getStatusColor(status: number) {
  const map: Record<number, string> = {
    0: 'text-yellow-600 bg-yellow-50',
    1: 'text-green-600 bg-green-50',
    2: 'text-red-600 bg-red-50'
  }
  return map[status] || 'text-gray-600 bg-gray-50'
}

const showEditProfile = ref(false)
const savingProfile = ref(false)
const editForm = ref({
  nickname: '',
  gender: '',
  birthday: '',
  phone: '',
  email: '',
  region: '',
  address: ''
})

function openEditProfile() {
  editForm.value = {
    nickname: userInfo.value?.nickname || '',
    gender: userInfo.value?.gender || '',
    birthday: userInfo.value?.birthday ? new Date(userInfo.value.birthday).toISOString().split('T')[0] : '',
    phone: userInfo.value?.phone || '',
    email: userInfo.value?.email || '',
    region: userInfo.value?.region || '',
    address: userInfo.value?.address || ''
  }
  showEditProfile.value = true
}

async function handleSaveProfile() {
  if (!editForm.value.nickname.trim()) {
    alert('请输入昵称')
    return
  }

  savingProfile.value = true
  try {
    await updateUserInfo({
      nickname: editForm.value.nickname,
      gender: editForm.value.gender,
      birthday: editForm.value.birthday || undefined,
      phone: editForm.value.phone,
      email: editForm.value.email,
      region: editForm.value.region,
      address: editForm.value.address
    })
    await userStore.fetchUserInfo()
    showEditProfile.value = false
    alert('保存成功')
  } catch (e) {
    console.error('保存失败', e)
    alert('保存失败，请重试')
  } finally {
    savingProfile.value = false
  }
}
</script>

<template>
  <div v-if="isLoggedIn" class="flex flex-col md:flex-row gap-10">
    
    <div class="w-full md:w-80 flex flex-col gap-6">
      <div class="bg-card p-8 rounded-3xl shadow-custom border border-border flex flex-col items-center text-center">
        <div class="relative w-32 h-32 bg-muted rounded-full mb-6 flex items-center justify-center overflow-hidden border-4 border-background shadow-sm group">
          <img v-if="userInfo?.avatar" :src="userInfo.avatar" alt="头像" class="w-full h-full object-cover" />
          <User v-else :size="64" class="text-muted-foreground" />
          <label class="absolute inset-0 bg-black/50 flex items-center justify-center cursor-pointer opacity-0 group-hover:opacity-100 transition-opacity">
            <Image :size="32" class="text-white" />
            <input 
              type="file" 
              accept="image/*" 
              class="hidden" 
              @change="handleAvatarChange"
              :disabled="uploadingAvatar"
            />
          </label>
          <div v-if="uploadingAvatar" class="absolute inset-0 bg-black/70 flex items-center justify-center">
            <div class="animate-spin rounded-full h-8 w-8 border-2 border-white border-t-transparent"></div>
          </div>
        </div>
        <h2 class="text-2xl font-bold text-foreground mb-1">{{ userInfo?.nickname || '用户' }}</h2>
        <p v-if="userInfo?.region || userInfo?.address" class="text-muted-foreground mb-6 flex items-center justify-center gap-2">
          <MapPin :size="16"/> {{ userInfo?.region || userInfo?.address }}
        </p>
        <p v-else class="text-muted-foreground mb-6">暂无地址信息</p>
        <div class="w-full space-y-3">
          <button @click="openEditProfile" class="block w-full bg-muted text-foreground py-2.5 rounded-xl font-semibold hover:bg-border transition-colors text-center">
            编辑资料
          </button>
          <button @click="handleLogout" class="w-full flex items-center justify-center gap-2 text-muted-foreground hover:text-destructive transition-colors py-2">
            <LogOut :size="18" />
            退出登录
          </button>
        </div>
      </div>

      <div class="bg-card shadow-custom rounded-3xl p-4 border border-border">
        <div class="flex flex-col gap-2">
          <button 
            @click="activeTab = 'adoptions'; loadData()"
            :class="['flex items-center gap-3 w-full p-4 rounded-xl text-left transition-colors font-semibold', 
              activeTab === 'adoptions' ? 'bg-primary text-primary-foreground shadow-sm' : 'text-muted-foreground hover:bg-muted hover:text-foreground']"
          >
            <Heart :size="20"/> 我的领养
          </button>
          <button 
            @click="activeTab = 'records'; loadData()"
            :class="['flex items-center gap-3 w-full p-4 rounded-xl text-left transition-colors font-semibold', 
              activeTab === 'records' ? 'bg-primary text-primary-foreground shadow-sm' : 'text-muted-foreground hover:bg-muted hover:text-foreground']"
          >
            <FileText :size="20"/> 申请记录
          </button>
          <button 
            @click="activeTab = 'posts'; loadData()"
            :class="['flex items-center gap-3 w-full p-4 rounded-xl text-left transition-colors font-semibold', 
              activeTab === 'posts' ? 'bg-primary text-primary-foreground shadow-sm' : 'text-muted-foreground hover:bg-muted hover:text-foreground']"
          >
            <MessageSquare :size="20"/> 我的帖子
          </button>
          <button 
            @click="activeTab = 'liked'; loadData()"
            :class="['flex items-center gap-3 w-full p-4 rounded-xl text-left transition-colors font-semibold', 
              activeTab === 'liked' ? 'bg-primary text-primary-foreground shadow-sm' : 'text-muted-foreground hover:bg-muted hover:text-foreground']"
          >
            <Heart :size="20"/> 我的喜欢
          </button>
          <button 
            @click="activeTab = 'follows'; loadData()"
            :class="['flex items-center gap-3 w-full p-4 rounded-xl text-left transition-colors font-semibold', 
              activeTab === 'follows' ? 'bg-primary text-primary-foreground shadow-sm' : 'text-muted-foreground hover:bg-muted hover:text-foreground']"
          >
            <Users :size="20"/> 我的关注
          </button>
        </div>
      </div>
    </div>

    <div class="flex-1 flex flex-col gap-8">
      <div class="bg-card p-8 rounded-3xl shadow-custom border border-border">
        <h2 class="text-2xl font-bold text-foreground mb-6 pb-4 border-b border-border">
          {{ 
            activeTab === 'adoptions' ? '我领养的宠物' :
            activeTab === 'records' ? '我的申请记录' :
            activeTab === 'posts' ? '我的帖子' :
            activeTab === 'follows' ? '我的关注' :
            '我的喜欢'
          }}
        </h2>

        <div v-if="loading" class="flex items-center justify-center py-10">
          <div class="animate-spin rounded-full h-10 w-10 border-2 border-primary border-t-transparent"></div>
        </div>

        <div v-else-if="activeTab === 'adoptions'">
          <div v-if="adoptions.length === 0" class="flex flex-col items-center justify-center p-10 text-muted-foreground text-center bg-background min-h-[300px] rounded-2xl">
            <Heart :size="48" class="mb-4 text-border" />
            <h3 class="text-xl font-bold text-foreground mb-2">还没有领养的宠物</h3>
            <p class="mb-6">浏览宠物，找到您的新伙伴。</p>
            <RouterLink to="/adopt" class="bg-primary text-primary-foreground px-6 py-2.5 rounded-full font-bold hover:opacity-90 transition-opacity">
              发现宠物
            </RouterLink>
          </div>
          <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            <PetCard v-for="pet in adoptions" :key="pet.id" :pet="pet" :show-adopt-button="false" />
          </div>
        </div>

        <div v-else-if="activeTab === 'records'">
          <div v-if="adoptionRecords.length === 0" class="flex flex-col items-center justify-center p-10 text-muted-foreground text-center bg-background min-h-[300px] rounded-2xl">
            <FileText :size="48" class="mb-4 text-border" />
            <h3 class="text-xl font-bold text-foreground mb-2">还没有申请记录</h3>
            <p class="mb-6">浏览宠物，提交领养申请。</p>
            <RouterLink to="/adopt" class="bg-primary text-primary-foreground px-6 py-2.5 rounded-full font-bold hover:opacity-90 transition-opacity">
              发现宠物
            </RouterLink>
          </div>
          <div v-else class="space-y-4">
            <div v-for="record in adoptionRecords" :key="record.id" class="p-6 bg-background rounded-2xl border border-border">
              <div class="flex items-start gap-4">
                <img v-if="record.petImage" :src="record.petImage" class="w-20 h-20 object-cover rounded-xl" />
                <div class="flex-1">
                  <div class="flex items-center justify-between mb-2">
                    <h3 class="text-lg font-bold text-foreground">{{ record.petName }}</h3>
                    <span :class="['px-3 py-1 rounded-full text-sm font-semibold', getStatusColor(record.status)]">
                      {{ getStatusText(record.status) }}
                    </span>
                  </div>
                  <p class="text-muted-foreground mb-2">{{ record.petBreed }} · {{ record.petType }}</p>
                  <p class="text-sm text-muted-foreground">申请时间：{{ new Date(record.createTime).toLocaleString() }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-else-if="activeTab === 'posts'">
          <div v-if="myPosts.length === 0" class="flex flex-col items-center justify-center p-10 text-muted-foreground text-center bg-background min-h-[300px] rounded-2xl">
            <MessageSquare :size="48" class="mb-4 text-border" />
            <h3 class="text-xl font-bold text-foreground mb-2">还没有发布帖子</h3>
            <p class="mb-6">去社区分享你的故事吧。</p>
            <RouterLink to="/community" class="bg-primary text-primary-foreground px-6 py-2.5 rounded-full font-bold hover:opacity-90 transition-opacity">
              去社区
            </RouterLink>
          </div>
          <div v-else class="space-y-4">
            <div v-for="post in myPosts" :key="post.id" class="relative">
              <SocialPost :post="post" />
              <button 
                @click="handleDeletePost(post.id)"
                class="absolute top-4 right-4 text-muted-foreground hover:text-destructive transition-colors z-10 bg-card/80 backdrop-blur-sm p-2 rounded-full"
                title="删除帖子"
              >
                <Trash2 :size="18" />
              </button>
            </div>
          </div>
        </div>

        <div v-else-if="activeTab === 'liked'">
          <div class="flex gap-4 mb-6">
            <button 
              @click="likedActiveTab = 'posts'; loadLikedData()"
              :class="['px-4 py-2 rounded-full font-semibold transition-colors', 
                likedActiveTab === 'posts' ? 'bg-primary text-primary-foreground' : 'bg-muted text-muted-foreground hover:bg-border']"
            >
              点赞的帖子
            </button>
            <button 
              @click="likedActiveTab = 'stories'; loadLikedData()"
              :class="['px-4 py-2 rounded-full font-semibold transition-colors', 
                likedActiveTab === 'stories' ? 'bg-primary text-primary-foreground' : 'bg-muted text-muted-foreground hover:bg-border']"
            >
              点赞的故事
            </button>
          </div>

          <div v-if="likedActiveTab === 'posts'">
            <div v-if="likedPosts.length === 0" class="flex flex-col items-center justify-center p-10 text-muted-foreground text-center bg-background min-h-[300px] rounded-2xl">
              <Heart :size="48" class="mb-4 text-border" />
              <h3 class="text-xl font-bold text-foreground mb-2">还没有点赞的帖子</h3>
              <p class="mb-6">去社区看看有什么有趣的内容。</p>
              <RouterLink to="/community" class="bg-primary text-primary-foreground px-6 py-2.5 rounded-full font-bold hover:opacity-90 transition-opacity">
                去社区
              </RouterLink>
            </div>
            <div v-else class="space-y-4">
              <SocialPost 
                v-for="post in likedPosts" 
                :key="post.id"
                :post="post"
              />
            </div>
          </div>

          <div v-else>
            <div v-if="likedStories.length === 0" class="flex flex-col items-center justify-center p-10 text-muted-foreground text-center bg-background min-h-[300px] rounded-2xl">
              <Heart :size="48" class="mb-4 text-border" />
              <h3 class="text-xl font-bold text-foreground mb-2">还没有点赞的故事</h3>
              <p class="mb-6">去看看领养故事吧。</p>
              <RouterLink to="/stories" class="bg-primary text-primary-foreground px-6 py-2.5 rounded-full font-bold hover:opacity-90 transition-opacity">
                看故事
              </RouterLink>
            </div>
            <div v-else class="space-y-6">
              <div 
                v-for="story in likedStories" 
                :key="story.id" 
                class="flex flex-col md:flex-row bg-card rounded-3xl overflow-hidden shadow-custom border border-border w-full cursor-pointer hover:shadow-lg transition-shadow"
                @click="router.push(`/story/${story.id}`)"
              >
                <div class="md:w-2/5 h-64 md:h-auto min-h-[200px] bg-muted relative">
                  <img 
                    :src="story.image || 'https://images.unsplash.com/photo-1560807707-8cc77767d783?auto=format&fit=crop&w=600&q=80'" 
                    :alt="story.title" 
                    class="w-full h-full object-cover"
                  />
                </div>
                <div class="md:w-3/5 p-6 md:p-8 flex flex-col justify-center">
                  <div class="flex items-center gap-2 text-secondary mb-4">
                    <Heart fill="currentColor" :size="20" />
                    <span class="font-bold tracking-wide">成功故事 #{{ story.id }}</span>
                  </div>
                  <h2 class="text-2xl font-bold text-foreground mb-4 line-clamp-1">{{ story.title }}</h2>
                  <p class="text-muted-foreground leading-relaxed mb-4 line-clamp-3">
                    {{ story.content }}
                  </p>
                  <div v-if="story.petName" class="flex items-center gap-3 mb-4 p-3 bg-muted rounded-xl">
                    <img 
                      :src="story.petImage || 'https://images.unsplash.com/photo-1560807707-8cc77767d783?auto=format&fit=crop&w=100&q=80'" 
                      :alt="story.petName"
                      class="w-10 h-10 rounded-full object-cover"
                    />
                    <div>
                      <div class="font-medium text-foreground">{{ story.petName }}</div>
                      <div class="text-sm text-muted-foreground">{{ story.petBreed }}</div>
                    </div>
                  </div>
                  <div class="flex items-center gap-4 text-sm text-muted-foreground">
                    <span class="flex items-center gap-1"><Heart :size="14" /> {{ story.likeCount }}</span>
                    <span class="flex items-center gap-1"><Eye :size="14" /> {{ story.viewCount }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-else-if="activeTab === 'follows'">
          <div v-if="follows.length === 0" class="flex flex-col items-center justify-center p-10 text-muted-foreground text-center bg-background min-h-[300px] rounded-2xl">
            <Users :size="48" class="mb-4 text-border" />
            <h3 class="text-xl font-bold text-foreground mb-2">还没有关注任何人</h3>
            <p class="mb-6">去社区发现有趣的用户吧。</p>
            <RouterLink to="/community" class="bg-primary text-primary-foreground px-6 py-2.5 rounded-full font-bold hover:opacity-90 transition-opacity">
              去社区
            </RouterLink>
          </div>
          <div v-else class="space-y-4">
            <div 
              v-for="u in follows" 
              :key="u.id"
              class="flex items-center gap-4 p-4 bg-background rounded-2xl border border-border hover:border-primary/30 transition-colors"
            >
              <RouterLink :to="`/user/${u.id}`" class="w-14 h-14 bg-muted rounded-full flex items-center justify-center overflow-hidden shrink-0">
                <img v-if="u.avatar" :src="u.avatar" alt="用户头像" class="w-full h-full object-cover" />
                <User v-else :size="28" class="text-muted-foreground" />
              </RouterLink>
              
              <div class="flex-1 min-w-0">
                <RouterLink :to="`/user/${u.id}`" class="font-bold text-foreground hover:text-primary truncate block">
                  {{ u.nickname || '用户' + u.id }}
                </RouterLink>
                <p v-if="u.region || u.address" class="text-sm text-muted-foreground truncate">{{ u.region || u.address }}</p>
              </div>

              <button 
                @click="handleUnfollow(u.id)"
                class="px-5 py-2 rounded-full text-sm font-semibold border border-border text-muted-foreground hover:bg-red-50 hover:text-red-500 hover:border-red-200 transition-colors shrink-0"
              >
                取消关注
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showEditProfile" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
      <div class="bg-card rounded-2xl p-6 w-full max-w-2xl max-h-[90vh] overflow-y-auto shadow-2xl">
        <div class="flex justify-between items-center mb-6">
          <h3 class="text-xl font-bold text-foreground">编辑资料</h3>
          <button @click="showEditProfile = false" class="text-muted-foreground hover:text-foreground text-2xl">✕</button>
        </div>

        <div class="space-y-4">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-muted-foreground mb-1">用户名</label>
              <input
                type="text"
                :value="userInfo?.username"
                disabled
                class="w-full px-4 py-2.5 rounded-xl border border-border bg-muted/50 text-muted-foreground"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-foreground mb-1">昵称 *</label>
              <input
                type="text"
                v-model="editForm.nickname"
                placeholder="请输入昵称"
                class="w-full px-4 py-2.5 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
              />
            </div>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div>
              <label class="block text-sm font-medium text-foreground mb-1">性别</label>
              <select
                v-model="editForm.gender"
                class="w-full px-4 py-2.5 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
              >
                <option value="">请选择</option>
                <option value="male">男</option>
                <option value="female">女</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-foreground mb-1">出生日期</label>
              <input
                type="date"
                v-model="editForm.birthday"
                class="w-full px-4 py-2.5 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-foreground mb-1">手机号</label>
              <input
                type="tel"
                v-model="editForm.phone"
                placeholder="请输入手机号"
                class="w-full px-4 py-2.5 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
              />
            </div>
          </div>

          <div>
            <label class="block text-sm font-medium text-foreground mb-1">邮箱</label>
            <input
              type="email"
              v-model="editForm.email"
              placeholder="请输入邮箱"
              class="w-full px-4 py-2.5 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
            />
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-foreground mb-1">所在地区</label>
              <input
                type="text"
                v-model="editForm.region"
                placeholder="如：北京市朝阳区"
                class="w-full px-4 py-2.5 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-foreground mb-1">详细地址</label>
              <input
                type="text"
                v-model="editForm.address"
                placeholder="请输入详细地址"
                class="w-full px-4 py-2.5 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
              />
            </div>
          </div>
        </div>

        <div class="flex justify-end gap-3 mt-6 pt-4 border-t border-border">
          <button
            @click="showEditProfile = false"
            class="px-6 py-2.5 rounded-xl border border-border hover:bg-muted transition-colors"
          >
            取消
          </button>
          <button
            @click="handleSaveProfile"
            :disabled="savingProfile"
            class="px-6 py-2.5 rounded-xl bg-primary text-primary-foreground hover:opacity-90 transition-opacity disabled:opacity-50 flex items-center gap-2"
          >
            <div v-if="savingProfile" class="animate-spin rounded-full h-4 w-4 border-2 border-white border-t-transparent"></div>
            保存
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
