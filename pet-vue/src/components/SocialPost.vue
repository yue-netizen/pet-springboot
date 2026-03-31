<script setup lang="ts">
import { ref } from 'vue'
import { Heart, MessageSquare, Share2, User } from 'lucide-vue-next'
import type { Post } from '@/api/social'
import { likePost, unlikePost } from '@/api/social'

interface Props {
  post: Post
}

const props = defineProps<Props>()

const liked = ref(false)
const likes = ref(props.post.likeCount || 0)

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
    
    <div v-if="post.image" class="rounded-xl overflow-hidden mb-6 max-h-[400px]">
      <img :src="post.image" alt="帖子内容" class="w-full h-full object-cover" />
    </div>
    
    <div v-if="post.video" class="rounded-xl overflow-hidden mb-6 max-h-[400px]">
      <video :src="post.video" controls class="w-full h-full object-cover" />
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
</template>
