<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Heart, MessageSquare, Share2, User, Maximize2, X, Smile } from 'lucide-vue-next'
import type { Post, Comment } from '@/api/social'
import { likePost, unlikePost, getComments, createComment, checkPostLiked } from '@/api/social'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'

const router = useRouter()
const userStore = useUserStore()

interface Props {
  post: Post
}

const props = defineProps<Props>()

const liked = ref(props.post.liked || false)
const likes = ref(props.post.likeCount || 0)
const showImageViewer = ref(false)
const currentViewerIndex = ref(0)
const showComments = ref(false)
const comments = ref<Comment[]>([])
const commentsLoading = ref(false)
const commentContent = ref('')
const submittingComment = ref(false)
const showCommentEmojiPicker = ref(false)
const currentCommentPage = ref(1)
const commentsPerPage = 5
const totalComments = ref(0)

const emojiCategories = [
  {
    name: '表情',
    emojis: ["😀", "😂", "🥰", "😍", "🤩", "😊", "😇", "🙂", "😉", "😌", "😋", "🤤", "😎", "🤗", "😘", "😚", "😜", "🤪", "😝", "🤑", "🤔", "🤫", "🤭", "😏", "😒", "🙄", "😮", "😯", "😲", "😳", "🥺", "😢", "😭", "😤", "😠", "😡", "🤬", "😈", "👿", "💀", "☠️", "💩", "🤡", "👹", "👺", "👻", "👽", "👾", "🤖", "😺", "😸", "😹", "😻", "😼", "😽", "🙀", "😿", "�"]
  },
  {
    name: '动物',
    emojis: ["�🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐨", "🐯", "🦁", "🐮", "🐷", "🐸", "🐵", "🙈", "🙉", "🙊", "🐒", "🐔", "🐧", "🐦", "🐤", "🐣", "🐥", "🦆", "🦅", "🦉", "🦇", "🐺", "🐗", "🐴", "🦄", "🐝", "🐛", "🦋", "🐌", "🐞", "🐜"]
  },
  {
    name: '食物',
    emojis: ["🍎", "🍐", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🍈", "🍒", "🍑", "🥭", "🍍", "🥥", "🥝", "🍅", "🍆", "🥑", "🥦", "🥬", "🥒", "🌶️", "🌽", "🥕", "🧄", "🧅", "🥔", "🍠", "🥐", "🥯", "🍞", "🥖", "🥨", "🧀", "🥚", "🍳", "🧈", "🥞", "🧇", "🥓", "🥩", "🍗", "🍖", "🌭", "🍔", "🍟", "🍕", "🥪", "🥙", "🌮", "🌯", "🥗", "🥘", "🍝", "🍜", "🍲", "🍛", "🍣", "🍱", "🥟", "🦪", "🍤", "🍙", "🍚", "🍘", "🍥", "🥠", "🍢", "🍡"]
  },
  {
    name: '饮品',
    emojis: ["🍧", "🍨", "🍦", "🥧", "🍰", "🎂", "🍮", "🍭", "🍬", "🍫", "🍿", "🧂", "🥤", "🍵", "🍶", "🍾", "🍷", "🍸", "🍹", "🍺", "🍻", "🥂", "🥃", "🍼", "☕", "🧋", "🧃", "🧉"]
  }
]

const currentCategory = ref(0)
const emojisPerPageInPicker = 32
const currentEmojiPage = ref(0)

const getCurrentEmojis = computed(() => {
  const category = emojiCategories[currentCategory.value]
  const start = currentEmojiPage.value * emojisPerPageInPicker
  const end = start + emojisPerPageInPicker
  return category.emojis.slice(start, end)
})

const totalPagesInPicker = computed(() => {
  const category = emojiCategories[currentCategory.value]
  return Math.ceil(category.emojis.length / emojisPerPageInPicker)
})

const nextEmojiPage = () => {
  if (currentEmojiPage.value < totalPagesInPicker.value - 1) {
    currentEmojiPage.value++
  }
}

const prevEmojiPage = () => {
  if (currentEmojiPage.value > 0) {
    currentEmojiPage.value--
  }
}

const selectCategory = (index: number) => {
  currentCategory.value = index
  currentEmojiPage.value = 0
}

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

const currentPageComments = computed(() => {
  const start = 0
  const end = currentCommentPage.value * commentsPerPage
  return comments.value.slice(start, end)
})

const hasMoreComments = computed(() => {
  return currentPageComments.value.length < comments.value.length
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

const toggleComments = async () => {
  if (!showComments.value) {
    showComments.value = true
    currentCommentPage.value = 1
    await loadComments()
  } else {
    showComments.value = false
  }
}

const loadComments = async () => {
  commentsLoading.value = true
  try {
    const res = await getComments(props.post.id, 1, 100)
    comments.value = res.data.records || []
    totalComments.value = res.data.total || comments.value.length
  } catch (error) {
    console.error('加载评论失败', error)
  } finally {
    commentsLoading.value = false
  }
}

const loadMoreComments = () => {
  currentCommentPage.value++
}

const insertCommentEmoji = (emoji: string) => {
  commentContent.value += emoji
  showCommentEmojiPicker.value = false
}

const submitComment = async () => {
  if (!userStore.isLoggedIn()) {
    router.push('/login')
    return
  }
  
  if (!commentContent.value.trim()) {
    alert('请输入评论内容')
    return
  }
  
  submittingComment.value = true
  try {
    await createComment({
      postId: props.post.id,
      content: commentContent.value
    })
    commentContent.value = ''
    currentCommentPage.value = 1
    await loadComments()
  } catch (error) {
    console.error('发布评论失败', error)
    alert('发布评论失败，请重试')
  } finally {
    submittingComment.value = false
  }
}

const handleClickOutside = (e: MouseEvent) => {
  const target = e.target as HTMLElement
  if (!target.closest('.comment-emoji-picker-container') && !target.closest('.comment-emoji-button')) {
    showCommentEmojiPicker.value = false
  }
}

const loadLikedStatus = async () => {
  if (!userStore.isLoggedIn()) {
    return
  }
  try {
    const res = await checkPostLiked(props.post.id)
    liked.value = res.data
  } catch (error) {
    console.error('检查点赞状态失败', error)
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
  loadLikedStatus()
})
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
    
    <h2 v-if="post.title" class="text-xl font-bold text-foreground mb-2">{{ post.title }}</h2>
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
      <button 
        @click="toggleComments"
        class="flex items-center gap-2 text-muted-foreground hover:text-primary transition-colors"
      >
        <MessageSquare :size="20" />
        <span class="font-medium">评论</span>
      </button>
      <button class="flex items-center gap-2 text-muted-foreground hover:text-primary transition-colors ml-auto">
        <Share2 :size="20" />
      </button>
    </div>

    <div v-if="showComments" class="mt-6 pt-6 border-t border-border">
      <div class="flex items-start gap-3 mb-6">
        <div class="w-10 h-10 bg-primary/20 rounded-full flex-shrink-0 flex items-center justify-center">
          <User class="text-primary" :size="20" />
        </div>
        <div class="flex-1">
          <textarea
            v-model="commentContent"
            rows="2"
            placeholder="写下你的评论..."
            class="w-full p-3 rounded-xl border border-border bg-background text-foreground resize-none focus:outline-none focus:ring-2 focus:ring-primary/50"
          ></textarea>
          <div class="flex items-center justify-between mt-2">
            <div class="relative">
              <button
                @click.stop="showCommentEmojiPicker = !showCommentEmojiPicker"
                class="comment-emoji-button p-2 text-muted-foreground hover:bg-muted rounded-full transition-colors"
              >
                <Smile :size="20" />
              </button>
              <div v-if="showCommentEmojiPicker" class="comment-emoji-picker-container absolute top-full left-0 mt-2 bg-card rounded-2xl shadow-custom border border-border p-4 z-50 w-80">
                <div class="flex gap-1 mb-3 border-b border-border pb-2">
                  <button 
                    v-for="(category, index) in emojiCategories" 
                    :key="index"
                    @click="selectCategory(index)"
                    :class="['text-xs px-2 py-1 rounded-full transition-colors', currentCategory === index ? 'bg-primary text-primary-foreground' : 'text-muted-foreground hover:bg-muted']"
                  >
                    {{ category.name }}
                  </button>
                </div>
                
                <div class="grid grid-cols-8 gap-2 mb-3">
                  <button 
                    v-for="(emoji, index) in getCurrentEmojis" 
                    :key="`${currentCategory}-${index}-${emoji}`"
                    @click="insertCommentEmoji(emoji)"
                    class="text-2xl hover:bg-muted p-1 rounded-lg transition-colors"
                  >
                    {{ emoji }}
                  </button>
                </div>
                
                <div class="flex items-center justify-between text-xs text-muted-foreground">
                  <button 
                    @click="prevEmojiPage"
                    :disabled="currentEmojiPage === 0"
                    class="px-2 py-1 rounded hover:bg-muted disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    上一页
                  </button>
                  <span>{{ currentEmojiPage + 1 }} / {{ totalPagesInPicker }}</span>
                  <button 
                    @click="nextEmojiPage"
                    :disabled="currentEmojiPage >= totalPagesInPicker - 1"
                    class="px-2 py-1 rounded hover:bg-muted disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    下一页
                  </button>
                </div>
              </div>
            </div>
            <button
              @click="submitComment"
              :disabled="submittingComment"
              class="px-6 py-2 rounded-xl bg-primary text-primary-foreground font-medium hover:opacity-90 transition-opacity disabled:opacity-50"
            >
              {{ submittingComment ? '发布中...' : '发布' }}
            </button>
          </div>
        </div>
      </div>

      <div v-if="commentsLoading" class="text-center text-muted-foreground py-8">
        加载评论中...
      </div>
      
      <div v-else-if="comments.length === 0" class="text-center text-muted-foreground py-8">
        暂无评论，快来第一个评论吧~
      </div>
      
      <div v-else class="space-y-4">
        <div v-for="comment in currentPageComments" :key="comment.id" class="flex gap-3">
          <div class="w-10 h-10 bg-muted rounded-full flex items-center justify-center text-muted-foreground flex-shrink-0">
            <User :size="20" />
          </div>
          <div class="flex-1">
            <div class="flex items-center gap-2 mb-1">
              <span class="font-semibold text-foreground">用户{{ comment.userId }}</span>
              <span class="text-xs text-muted-foreground">{{ formatTime(comment.createTime) }}</span>
            </div>
            <p class="text-foreground">{{ comment.content }}</p>
          </div>
        </div>
        
        <div v-if="hasMoreComments" class="text-center">
          <button
            @click="loadMoreComments"
            class="px-6 py-2 rounded-xl bg-muted text-foreground font-medium hover:bg-muted/70 transition-colors"
          >
            查看更多评论
          </button>
        </div>
      </div>
    </div>
  </div>

  <div v-if="showImageViewer" class="fixed inset-0 bg-black/90 z-[100] flex items-center justify-center" @click="closeImageViewer">
    <button @click.stop="closeImageViewer" class="absolute top-4 right-4 text-white hover:text-gray-300">
      <X :size="32" />
    </button>
    <img :src="postImages[currentViewerIndex]" class="max-h-[90vh] max-w-[90vw] object-contain" @click.stop />
  </div>
</template>
