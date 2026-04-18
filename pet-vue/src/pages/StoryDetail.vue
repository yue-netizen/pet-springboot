<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Heart, Eye, ArrowLeft, MessageSquare, Send, Trash2 } from 'lucide-vue-next'
import { getStoryById, likeStory, getStoryComments, createStoryComment, deleteStoryComment, type StoryDetail, type StoryCommentDetail } from '@/api/story'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const story = ref<StoryDetail | null>(null)
const comments = ref<StoryCommentDetail[]>([])
const loading = ref(false)
const commentsLoading = ref(false)
const liking = ref(false)
const submitting = ref(false)
const commentContent = ref('')
const isLiked = ref(false)

const loadStory = async () => {
  const id = Number(route.params.id)
  if (!id) return

  loading.value = true
  try {
    const res = await getStoryById(id)
    story.value = res.data
    isLiked.value = res.data.isLiked || false
  } catch (err) {
    console.error('加载故事详情失败', err)
  } finally {
    loading.value = false
  }
}

const loadComments = async () => {
  const id = Number(route.params.id)
  if (!id) return

  commentsLoading.value = true
  try {
    const res = await getStoryComments(id, 1, 100)
    comments.value = res.data.records
  } catch (err) {
    console.error('加载评论失败', err)
  } finally {
    commentsLoading.value = false
  }
}

const handleLike = async () => {
  if (!story.value || liking.value) return

  liking.value = true
  try {
    await likeStory(story.value.id)
    isLiked.value = !isLiked.value
    story.value.likeCount += isLiked.value ? 1 : -1
  } catch (err) {
    console.error('点赞失败', err)
  } finally {
    liking.value = false
  }
}

const handleSubmitComment = async () => {
  if (!story.value || !commentContent.value.trim() || submitting.value) return

  submitting.value = true
  try {
    await createStoryComment({
      storyId: story.value.id,
      content: commentContent.value.trim()
    })
    commentContent.value = ''
    await loadComments()
  } catch (err) {
    console.error('发表评论失败', err)
  } finally {
    submitting.value = false
  }
}

const handleDeleteComment = async (id: number) => {
  if (!confirm('确定要删除这条评论吗？')) return

  try {
    await deleteStoryComment(id)
    await loadComments()
  } catch (err) {
    console.error('删除评论失败', err)
  }
}

const formatDate = (dateStr: string) => {
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  loadStory()
  loadComments()
})
</script>

<template>
  <div class="min-h-screen py-12">
    <div v-if="loading" class="flex justify-center py-24">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
    </div>

    <div v-else-if="!story" class="text-center py-24 text-muted-foreground">
      故事不存在
    </div>

    <div v-else class="max-w-4xl mx-auto">
      <button 
        @click="router.back()" 
        class="flex items-center gap-2 text-muted-foreground hover:text-foreground mb-8 transition-colors"
      >
        <ArrowLeft :size="20" />
        返回
      </button>

      <article class="bg-card rounded-3xl overflow-hidden shadow-custom border border-border mb-8">
        <div class="relative h-80 md:h-96">
          <img 
            :src="story.image || 'https://images.unsplash.com/photo-1560807707-8cc77767d783?auto=format&fit=crop&w=1200&q=80'" 
            :alt="story.title"
            class="w-full h-full object-cover"
          />
          <div class="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent"></div>
          <div class="absolute bottom-0 left-0 right-0 p-8 md:p-12 text-white">
            <h1 class="text-3xl md:text-5xl font-bold mb-4">{{ story.title }}</h1>
            <div class="flex items-center gap-4">
              <div class="flex items-center gap-3">
                <div class="w-12 h-12 bg-white/20 rounded-full overflow-hidden backdrop-blur shrink-0">
                  <img 
                    v-if="story.userAvatar"
                    :src="story.userAvatar" 
                    :alt="story.username || '用户'"
                    class="w-full h-full object-cover"
                  />
                  <div v-else class="w-full h-full flex items-center justify-center text-white/80 text-lg font-bold">
                    {{ (story.username || '用').charAt(0) }}
                  </div>
                </div>
                <div>
                  <div class="font-bold">{{ story.username || '用户' + story.userId }}</div>
                  <div class="text-sm opacity-80">{{ formatDate(story.createTime) }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="p-8 md:p-12">
          <div v-if="story.petName" class="flex items-center gap-4 p-4 bg-muted rounded-2xl mb-8">
            <img 
              :src="story.petImage || 'https://images.unsplash.com/photo-1560807707-8cc77767d783?auto=format&fit=crop&w=100&q=80'" 
              :alt="story.petName"
              class="w-16 h-16 rounded-full object-cover"
            />
            <div>
              <div class="font-bold text-lg text-foreground">{{ story.petName }}</div>
              <div class="text-muted-foreground">{{ story.petBreed }}</div>
            </div>
          </div>

          <div class="prose prose-lg max-w-none">
            <p class="text-lg text-foreground leading-relaxed whitespace-pre-wrap">{{ story.content }}</p>
          </div>

          <div class="flex items-center justify-between mt-12 pt-8 border-t border-border">
            <div class="flex items-center gap-6">
              <button 
                @click="handleLike"
                :disabled="liking"
                class="flex items-center gap-2 text-muted-foreground hover:text-red-500 transition-colors disabled:opacity-50"
              >
                <Heart :size="24" :class="{ 'fill-red-500 text-red-500': isLiked }" />
                <span class="font-medium">{{ story.likeCount }}</span>
              </button>
              <div class="flex items-center gap-2 text-muted-foreground">
                <Eye :size="24" />
                <span class="font-medium">{{ story.viewCount }}</span>
              </div>
              <div class="flex items-center gap-2 text-muted-foreground">
                <MessageSquare :size="24" />
                <span class="font-medium">{{ comments.length }}</span>
              </div>
            </div>
          </div>
        </div>
      </article>

      <div class="bg-card rounded-3xl shadow-custom border border-border p-8">
        <h2 class="text-2xl font-bold mb-6 flex items-center gap-2">
          <MessageSquare :size="24" />
          评论 ({{ comments.length }})
        </h2>

        <div v-if="userStore.isLoggedIn()" class="mb-8">
          <div class="flex gap-4">
            <div class="w-10 h-10 bg-muted rounded-full overflow-hidden flex-shrink-0">
              <img
                v-if="userStore.userInfo?.avatar"
                :src="userStore.userInfo.avatar"
                alt="头像"
                class="w-full h-full object-cover"
              />
              <div v-else class="w-full h-full flex items-center justify-center text-muted-foreground text-sm font-bold">
                {{ (userStore.userInfo?.nickname || userStore.userInfo?.username || '用').charAt(0) }}
              </div>
            </div>
            <div class="flex-1">
              <textarea
                v-model="commentContent"
                placeholder="写下你的评论..."
                class="w-full p-4 bg-muted rounded-xl border border-border resize-none focus:outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary transition-all"
                rows="3"
              ></textarea>
              <div class="flex justify-end mt-3">
                <button
                  @click="handleSubmitComment"
                  :disabled="!commentContent.trim() || submitting"
                  class="flex items-center gap-2 px-6 py-2 bg-primary text-primary-foreground rounded-xl font-medium hover:bg-primary/90 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  <Send :size="18" />
                  {{ submitting ? '发送中...' : '发表评论' }}
                </button>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="mb-8 p-4 bg-muted rounded-xl text-center">
          <p class="text-muted-foreground">请先登录后再发表评论</p>
        </div>

        <div v-if="commentsLoading" class="flex justify-center py-8">
          <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
        </div>

        <div v-else-if="comments.length === 0" class="text-center py-8 text-muted-foreground">
          暂无评论，快来发表第一条评论吧！
        </div>

        <div v-else class="space-y-6">
          <div v-for="comment in comments" :key="comment.id" class="flex gap-4">
            <div class="w-10 h-10 bg-muted rounded-full overflow-hidden flex-shrink-0">
              <img
                v-if="comment.userAvatar"
                :src="comment.userAvatar"
                :alt="comment.username || '用户'"
                class="w-full h-full object-cover"
              />
              <div v-else class="w-full h-full flex items-center justify-center text-muted-foreground text-sm font-bold">
                {{ (comment.username || '用').charAt(0) }}
              </div>
            </div>
            <div class="flex-1">
              <div class="flex items-center justify-between mb-1">
                <span class="font-medium text-foreground">{{ comment.username || '用户' + comment.userId }}</span>
                <div class="flex items-center gap-3">
                  <span class="text-sm text-muted-foreground">{{ formatDate(comment.createTime) }}</span>
                  <button
                    v-if="userStore.isLoggedIn() && userStore.userInfo?.id === comment.userId"
                    @click="handleDeleteComment(comment.id)"
                    class="text-muted-foreground hover:text-red-500 transition-colors"
                  >
                    <Trash2 :size="16" />
                  </button>
                </div>
              </div>
              <p class="text-foreground leading-relaxed">{{ comment.content }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
