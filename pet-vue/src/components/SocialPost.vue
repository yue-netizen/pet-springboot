<script setup lang="ts">
import { ref, computed } from 'vue'
import { Heart, MessageSquare, Share2, User, Maximize2, X } from 'lucide-vue-next'
import type { Post } from '@/api/social'
import { likePost, unlikePost } from '@/api/social'

interface Props {
  post: Post
}

const props = defineProps<Props>()

const liked = ref(false)
const likes = ref(props.post.likeCount || 0)
const showImageViewer = ref(false)
const currentViewerIndex = ref(0)

const postImages = computed(() => {
  if (props.post.images) {
    return props.post.images.split(',').filter(Boolean)
  }
  if (props.post.image) {
    return [props.post.image]
  }
  return []
})

const postVideos = computed(() => {
  if (props.post.videos) {
    return props.post.videos.split(',').filter(Boolean)
  }
  if (props.post.video) {
    return [props.post.video]
  }
  return []
})

const postTags = computed(() => {
  if (props.post.tags) {
    return props.post.tags.split(',').filter(Boolean)
  }
  return []
})

const formatTime = (timeStr: string) => {
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  const minutes = Math.floor(diff / (1000 * 60))
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  return `${days}天前`
}

const handleLike = async () => {
  try {
    if (liked.value) {
      await unlikePost(props.post.id)
      likes.value--
    } else {
      await likePost(props.post.id)
      likes.value++
    }
    liked.value = !liked.value
  } catch (error) {
    console.error('点赞失败', error)
  }
}

const openImageViewer = (index: number) => {
  currentViewerIndex.value = index
  showImageViewer.value = true
}

const closeImageViewer = () => {
  showImageViewer.value = false
}
</script>

<template>
  <div data-cmp="SocialPost" class="bg-card rounded-2xl p-6 shadow-custom border border-border w-full mb-6">
    <div class="flex items-center justify-between mb-4">
      <div class="flex items-center gap-3">
        <div class="w-12 h-12 bg-muted rounded-full flex items-center justify-center text-muted-foreground overflow-hidden">
          <User :size="24" />
        </div>
        <div class="flex flex-col">
          <span class="font-bold text-foreground">用户{{ post.userId }}</span>
          <span class="text-xs text-muted-foreground">{{ formatTime(post.createTime) }}</span>
        </div>
      </div>
      <button class="bg-muted text-foreground px-4 py-1.5 rounded-full text-sm font-semibold hover:bg-primary hover:text-primary-foreground transition-colors">
        关注
      </button>
    </div>
    
    <p class="text-foreground mb-4 leading-relaxed">{{ post.content }}</p>
    
    <div class="mb-4">
      <div class="grid grid-cols-3 gap-2">
        <div 
          v-for="(img, index) in postImages" 
          :key="`img-${index}`"
          class="relative group cursor-pointer"
          @click="openImageViewer(index)"
        >
          <img 
            :src="img" 
            :alt="`帖子图片${index + 1}`" 
            class="w-full h-40 object-cover rounded-xl"
          />
          <div class="absolute inset-0 bg-black/0 group-hover:bg-black/30 rounded-xl transition-colors flex items-center justify-center">
            <button class="opacity-0 group-hover:opacity-100 transition-opacity bg-black/50 text-white p-2 rounded-full">
              <Maximize2 :size="20" />
            </button>
          </div>
        </div>

        <div 
          v-for="(vid, index) in postVideos" 
          :key="`vid-${index}`"
          class="relative group col-span-3"
        >
          <video 
            :src="vid" 
            controls 
            class="w-full h-48 object-cover rounded-xl"
          />
        </div>
      </div>
    </div>
    
    <div v-if="postTags.length > 0" class="flex flex-wrap gap-2 mb-4">
      <span 
        v-for="tag in postTags" 
        :key="tag"
        class="bg-muted text-foreground px-3 py-1 rounded-full text-sm font-medium"
      >
        {{ tag }}
      </span>
    </div>
    
    <div class="flex items-center gap-6 pt-4 border-t border-border">
      <button 
        @click="handleLike"
        :class="['flex items-center gap-2 transition-colors', liked ? 'text-destructive' : 'text-muted-foreground hover:text-destructive']"
      >
        <Heart :size="20" :fill="liked ? 'currentColor' : 'none'" />
        <span class="font-medium">{{ likes }}</span>
      </button>
      <button class="flex items-center gap-2 text-muted-foreground hover:text-primary transition-colors">
        <MessageSquare :size="20" />
        <span class="font-medium">评论</span>
      </button>
      <button class="flex items-center gap-2 text-muted-foreground hover:text-primary transition-colors ml-auto">
        <Share2 :size="20" />
      </button>
    </div>
  </div>

  <div v-if="showImageViewer" class="fixed inset-0 bg-black/90 z-[100] flex items-center justify-center" @click="closeImageViewer">
    <button @click.stop="closeImageViewer" class="absolute top-4 right-4 text-white hover:text-gray-300">
      <X :size="32" />
    </button>
    <img :src="postImages[currentViewerIndex]" class="max-h-[90vh] max-w-[90vw] object-contain" @click.stop />
  </div>
</template>
