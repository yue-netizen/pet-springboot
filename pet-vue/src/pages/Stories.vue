<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Heart, Eye, ArrowRight } from 'lucide-vue-next'
import { useRouter } from 'vue-router'
import { getStoryList, type StoryDetail } from '@/api/story'

const router = useRouter()
const stories = ref<StoryDetail[]>([])
const loading = ref(false)
const page = ref(1)
const total = ref(0)

const loadStories = async () => {
  loading.value = true
  try {
    const res = await getStoryList(page.value, 10)
    stories.value = res.data.records
    total.value = res.data.total
  } catch (err) {
    console.error('加载故事失败', err)
  } finally {
    loading.value = false
  }
}

const goToDetail = (id: number) => {
  router.push(`/story/${id}`)
}

const formatDate = (dateStr: string) => {
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  if (days === 0) return '今天'
  if (days === 1) return '昨天'
  if (days < 7) return `${days}天前`
  return date.toLocaleDateString('zh-CN')
}

onMounted(() => {
  loadStories()
})
</script>

<template>
  <div class="flex flex-col gap-12 py-12">
    <div class="text-center max-w-3xl mx-auto">
      <h1 class="text-4xl md:text-5xl font-extrabold text-foreground mb-6">领养故事</h1>
      <p class="text-lg text-muted-foreground leading-relaxed">
        没有什么比看到我们救助的宠物在永久家园中茁壮成长更让我们感动的了。阅读这些充满爱的励志故事。
      </p>
    </div>

    <div v-if="loading" class="flex justify-center py-12">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
    </div>

    <div v-else-if="stories.length === 0" class="text-center py-12 text-muted-foreground">
      暂无故事
    </div>

    <div v-else class="flex flex-wrap gap-8">
      <div 
        v-for="story in stories" 
        :key="story.id" 
        class="flex flex-col md:flex-row bg-card rounded-3xl overflow-hidden shadow-custom border border-border w-full cursor-pointer hover:shadow-lg transition-shadow"
        @click="goToDetail(story.id)"
      >
        <div class="md:w-2/5 h-64 md:h-auto min-h-[300px] bg-muted relative">
          <img 
            :src="story.image || 'https://images.unsplash.com/photo-1560807707-8cc77767d783?auto=format&fit=crop&w=600&q=80'" 
            :alt="story.title" 
            class="w-full h-full object-cover"
          />
        </div>
        <div class="md:w-3/5 p-8 md:p-12 flex flex-col justify-center">
          <div class="flex items-center gap-2 text-secondary mb-4">
            <Heart fill="currentColor" :size="20" />
            <span class="font-bold tracking-wide">成功故事 #{{ story.id }}</span>
          </div>
          <h2 class="text-3xl font-bold text-foreground mb-4 line-clamp-1">{{ story.title }}</h2>
          <p class="text-muted-foreground text-lg leading-relaxed mb-6 line-clamp-3">
            {{ story.content }}
          </p>

          <div v-if="story.petName" class="flex items-center gap-3 mb-4 p-3 bg-muted rounded-xl">
            <img 
              :src="story.petImage || 'https://images.unsplash.com/photo-1560807707-8cc77767d783?auto=format&fit=crop&w=100&q=80'" 
              :alt="story.petName"
              class="w-12 h-12 rounded-full object-cover"
            />
            <div>
              <div class="font-medium text-foreground">{{ story.petName }}</div>
              <div class="text-sm text-muted-foreground">{{ story.petBreed }}</div>
            </div>
          </div>

          <div class="flex items-center justify-between mt-auto">
            <div class="flex items-center gap-4">
              <div class="w-12 h-12 bg-muted rounded-full overflow-hidden">
                <img 
                  :src="story.userAvatar || 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=200&q=80'" 
                  :alt="story.username || '用户'" 
                  class="w-full h-full object-cover" 
                />
              </div>
              <div>
                <div class="font-bold text-foreground">{{ story.username || '匿名用户' }}</div>
                <div class="text-sm text-muted-foreground">{{ formatDate(story.createTime) }}</div>
              </div>
            </div>
            <div class="flex items-center gap-4 text-muted-foreground">
              <div class="flex items-center gap-1">
                <Heart :size="18" />
                <span>{{ story.likeCount }}</span>
              </div>
              <div class="flex items-center gap-1">
                <Eye :size="18" />
                <span>{{ story.viewCount }}</span>
              </div>
            </div>
          </div>

          <div class="mt-6 flex items-center gap-2 text-primary font-medium">
            阅读更多 <ArrowRight :size="20" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
