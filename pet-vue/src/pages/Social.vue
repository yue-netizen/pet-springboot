<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { Image as ImageIcon, Video, Smile, User, X, Maximize2 } from 'lucide-vue-next'
import SocialPost from '@/components/SocialPost.vue'
import { getPostList, createPost, uploadImage, uploadVideo } from '@/api/social'
import type { Post } from '@/api/social'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'

const router = useRouter()
const userStore = useUserStore()

const suggestedUsers = [1, 2, 3]
const trendingTags = ["#宠物领养", "#狗狗训练", "#猫咪爱好者", "#爪印之家社区", "#救助宠物"]

const emojis = ["😀", "😂", "🥰", "😍", "🤩", "😊", "😇", "🙂", "😉", "😌", "😋", "🤤", "😎", "🤗", "😘", "😚", "😜", "🤪", "😝", "🤑", "🤔", "🤫", "🤭", "😏", "😒", "🙄", "😮", "😯", "😲", "😳", "🥺", "😢", "😭", "😤", "😠", "😡", "🤬", "😈", "👿", "💀", "☠️", "💩", "🤡", "👹", "👺", "👻", "👽", "👾", "🤖", "😺", "😸", "😹", "😻", "😼", "😽", "🙀", "😿", "😾", "🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐨", "🐯", "🦁", "🐮", "🐷", "🐸", "🐵", "🙈", "🙉", "🙊", "🐒", "🐔", "🐧", "🐦", "🐤", "🐣", "🐥", "🦆", "🦅", "🦉", "🦇", "🐺", "🐗", "🐴", "🦄", "🐝", "🐛", "🦋", "🐌", "🐞", "🐜", "🐝", "🍎", "🍐", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🍈", "🍒", "🍑", "🥭", "🍍", "🥥", "🥝", "🍅", "🍆", "🥑", "🥦", "🥬", "🥒", "🌶️", "🌽", "🥕", "🧄", "🧅", "🥔", "🍠", "🥐", "🥯", "🍞", "🥖", "🥨", "🧀", "🥚", "🍳", "🧈", "🥞", "🧇", "🥓", "🥩", "🍗", "🍖", "🌭", "🍔", "🍟", "🍕", "🥪", "🥙", "🌮", "🌯", "🥗", "🥘", "🍝", "🍜", "🍲", "🍛", "🍣", "🍱", "🥟", "🦪", "🍤", "🍙", "🍚", "🍘", "🍥", "🥠", "🍢", "🍡", "🍧", "🍨", "🍦", "🥧", "🍰", "🎂", "🍮", "🍭", "🍬", "🍫", "🍿", "🧂", "🥤", "🍵", "🍶", "🍾", "🍷", "🍸", "🍹", "🍺", "🍻", "🥂", "🥃", "🍼", "☕", "🧋", "🧃", "🧉"]

const content = ref('')
const selectedImages = ref<File[]>([])
const imagePreviews = ref<string[]>([])
const selectedVideos = ref<File[]>([])
const videoPreviews = ref<string[]>([])
const selectedTags = ref<string[]>([])
const uploading = ref(false)
const posts = ref<Post[]>([])
const loading = ref(false)
const showEmojiPicker = ref(false)
const currentEmojiPage = ref(0)
const emojisPerPage = 32
const showImageViewer = ref(false)
const currentViewerIndex = ref(0)

const emojiCategories = [
  {
    name: '表情',
    emojis: ["😀", "😂", "🥰", "😍", "🤩", "😊", "😇", "🙂", "😉", "😌", "😋", "🤤", "😎", "🤗", "😘", "😚", "😜", "🤪", "😝", "🤑", "🤔", "🤫", "🤭", "😏", "😒", "🙄", "😮", "😯", "😲", "😳", "🥺", "😢", "😭", "😤", "😠", "😡", "🤬", "😈", "👿", "💀", "☠️", "💩", "🤡", "👹", "👺", "👻", "👽", "👾", "🤖", "😺", "😸", "😹", "😻", "😼", "😽", "🙀", "😿", "😾"]
  },
  {
    name: '动物',
    emojis: ["🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐨", "🐯", "🦁", "🐮", "🐷", "🐸", "🐵", "🙈", "🙉", "🙊", "🐒", "🐔", "🐧", "🐦", "🐤", "🐣", "🐥", "🦆", "🦅", "🦉", "🦇", "🐺", "🐗", "🐴", "🦄", "🐝", "🐛", "🦋", "🐌", "🐞", "🐜"]
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

const getCurrentEmojis = computed(() => {
  const category = emojiCategories[currentCategory.value]
  const start = currentEmojiPage.value * emojisPerPage
  const end = start + emojisPerPage
  return category.emojis.slice(start, end)
})

const totalPages = computed(() => {
  const category = emojiCategories[currentCategory.value]
  return Math.ceil(category.emojis.length / emojisPerPage)
})

const nextPage = () => {
  if (currentEmojiPage.value < totalPages.value - 1) {
    currentEmojiPage.value++
  }
}

const prevPage = () => {
  if (currentEmojiPage.value > 0) {
    currentEmojiPage.value--
  }
}

const selectCategory = (index: number) => {
  currentCategory.value = index
  currentEmojiPage.value = 0
}

const handleClickOutside = (e: MouseEvent) => {
  const target = e.target as HTMLElement
  if (!target.closest('.emoji-picker-container') && !target.closest('.emoji-button')) {
    showEmojiPicker.value = false
  }
}

const loadPosts = async () => {
  loading.value = true
  try {
    const res = await getPostList(1, 20)
    posts.value = res.data.records || []
  } catch (error) {
    console.error('加载帖子失败', error)
  } finally {
    loading.value = false
  }
}

const selectImage = (e: Event) => {
  const target = e.target as HTMLInputElement
  if (target.files && target.files.length > 0) {
    const currentCount = selectedImages.value.length + selectedVideos.value.length
    const remaining = 6 - currentCount
    if (remaining <= 0) {
      alert('最多只能上传6个文件')
      return
    }
    const filesToAdd = Array.from(target.files).slice(0, remaining)
    
    for (const file of filesToAdd) {
      selectedImages.value.push(file)
      imagePreviews.value.push(URL.createObjectURL(file))
    }
  }
}

const selectVideo = (e: Event) => {
  const target = e.target as HTMLInputElement
  if (target.files && target.files.length > 0) {
    const currentCount = selectedImages.value.length + selectedVideos.value.length
    const remaining = 6 - currentCount
    if (remaining <= 0) {
      alert('最多只能上传6个文件')
      return
    }
    const filesToAdd = Array.from(target.files).slice(0, remaining)
    
    for (const file of filesToAdd) {
      selectedVideos.value.push(file)
      videoPreviews.value.push(URL.createObjectURL(file))
    }
  }
}

const removeImage = (index: number) => {
  selectedImages.value.splice(index, 1)
  imagePreviews.value.splice(index, 1)
}

const removeVideo = (index: number) => {
  selectedVideos.value.splice(index, 1)
  videoPreviews.value.splice(index, 1)
}

const toggleTag = (tag: string) => {
  const index = selectedTags.value.indexOf(tag)
  if (index > -1) {
    selectedTags.value.splice(index, 1)
  } else {
    selectedTags.value.push(tag)
  }
}

const openImageViewer = (index: number) => {
  currentViewerIndex.value = index
  showImageViewer.value = true
}

const closeImageViewer = () => {
  showImageViewer.value = false
}

const insertEmoji = (emoji: string) => {
  content.value += emoji
  showEmojiPicker.value = false
}

const handlePublish = async () => {
  if (!userStore.isLoggedIn()) {
    router.push('/login')
    return
  }

  if (!content.value.trim()) {
    alert('请输入内容')
    return
  }

  uploading.value = true
  try {
    let imageUrl = ''
    let imagesUrl = ''
    let videoUrl = ''
    let videosUrl = ''
    let tagsStr = ''

    if (selectedImages.value.length > 0) {
      const uploadPromises = selectedImages.value.map(file => uploadImage(file))
      const results = await Promise.all(uploadPromises)
      const urls = results.map(res => res.data)
      imagesUrl = urls.join(',')
      imageUrl = urls[0] || ''
    }

    if (selectedVideos.value.length > 0) {
      const uploadPromises = selectedVideos.value.map(file => uploadVideo(file))
      const results = await Promise.all(uploadPromises)
      const urls = results.map(res => res.data)
      videosUrl = urls.join(',')
      videoUrl = urls[0] || ''
    }

    if (selectedTags.value.length > 0) {
      tagsStr = selectedTags.value.join(',')
    }

    await createPost({
      content: content.value,
      image: imageUrl,
      images: imagesUrl,
      video: videoUrl,
      videos: videosUrl,
      tags: tagsStr
    })

    content.value = ''
    selectedImages.value = []
    imagePreviews.value = []
    selectedVideos.value = []
    videoPreviews.value = []
    selectedTags.value = []
    showImageViewer.value = false

    await loadPosts()
  } catch (error) {
    console.error('发布失败', error)
    alert('发布失败，请重试')
  } finally {
    uploading.value = false
  }
}

onMounted(() => {
  loadPosts()
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<template>
  <div class="flex flex-col md:flex-row gap-10 max-w-6xl mx-auto w-full">
    
    <div class="flex-1 flex flex-col gap-6">
      <div class="bg-card rounded-3xl p-6 shadow-custom border border-border w-full">
        <div class="flex items-start gap-4 mb-4">
          <div class="w-12 h-12 bg-primary/20 rounded-full flex-shrink-0 flex items-center justify-center">
            <User class="text-primary" :size="24" />
          </div>
          <textarea 
            v-model="content"
            rows="3" 
            placeholder="与社区分享您的宠物时刻..."
            class="w-full bg-transparent resize-none border-none outline-none text-foreground text-lg placeholder:text-muted-foreground"
          ></textarea>
        </div>

        <div v-if="imagePreviews.length > 0 || videoPreviews.length > 0" class="mb-4">
          <div class="grid grid-cols-3 gap-2">
            <div 
              v-for="(preview, index) in imagePreviews" 
              :key="`img-${index}`"
              class="relative group"
            >
              <img 
                :src="preview" 
                class="rounded-xl h-32 w-full object-cover cursor-pointer"
                @click="openImageViewer(index)"
              />
              <div class="absolute top-1 right-1 flex gap-1">
                <button 
                  @click.stop="openImageViewer(index)"
                  class="bg-black/50 text-white p-1 rounded-full hover:bg-black/70"
                >
                  <Maximize2 :size="14" />
                </button>
                <button 
                  @click.stop="removeImage(index)"
                  class="bg-black/50 text-white p-1 rounded-full hover:bg-black/70"
                >
                  <X :size="14" />
                </button>
              </div>
            </div>

            <div 
              v-for="(preview, index) in videoPreviews" 
              :key="`vid-${index}`"
              class="relative group col-span-3"
            >
              <video 
                :src="preview" 
                controls 
                class="rounded-xl h-48 w-full object-cover"
              />
              <button @click.stop="removeVideo(index)" class="absolute top-2 right-2 bg-black/50 text-white p-1.5 rounded-full hover:bg-black/70">
                <X :size="18" />
              </button>
            </div>
          </div>
        </div>

        <div v-if="selectedTags.length > 0" class="mb-4 flex flex-wrap gap-2">
          <span 
            v-for="tag in selectedTags" 
            :key="tag"
            class="bg-primary text-primary-foreground px-3 py-1 rounded-full text-sm font-medium cursor-pointer transition-colors"
            @click="toggleTag(tag)"
          >
            {{ tag }}
          </span>
        </div>

        <div class="flex items-center justify-between pt-4 border-t border-border">
          <div class="flex gap-2">
            <label 
              class="p-2 text-primary hover:bg-primary/10 rounded-full transition-colors flex items-center gap-2 font-medium cursor-pointer"
              :class="(selectedImages.length + selectedVideos.length) >= 6 ? 'opacity-50 cursor-not-allowed' : ''"
            >
              <ImageIcon :size="20" /> <span class="hidden sm:inline">图片</span>
              <input 
                type="file" 
                accept="image/*" 
                multiple 
                @change="selectImage" 
                class="hidden"
                :disabled="(selectedImages.length + selectedVideos.length) >= 6"
              />
            </label>
            <label 
              class="p-2 text-secondary hover:bg-secondary/10 rounded-full transition-colors flex items-center gap-2 font-medium cursor-pointer"
              :class="(selectedImages.length + selectedVideos.length) >= 6 ? 'opacity-50 cursor-not-allowed' : ''"
            >
              <Video :size="20" /> <span class="hidden sm:inline">视频</span>
              <input 
                type="file" 
                accept="video/*" 
                multiple 
                @change="selectVideo" 
                class="hidden"
                :disabled="(selectedImages.length + selectedVideos.length) >= 6"
              />
            </label>
            <div class="relative">
              <button 
                @click="showEmojiPicker = !showEmojiPicker"
                class="emoji-button p-2 text-muted-foreground hover:bg-muted rounded-full transition-colors flex items-center gap-2 font-medium"
              >
                <Smile :size="20" /> <span class="hidden sm:inline">心情</span>
              </button>
              <div v-if="showEmojiPicker" class="emoji-picker-container absolute top-full left-0 mt-2 bg-card rounded-2xl shadow-custom border border-border p-4 z-50 w-80">
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
                    @click="insertEmoji(emoji)"
                    class="text-2xl hover:bg-muted p-1 rounded-lg transition-colors"
                  >
                    {{ emoji }}
                  </button>
                </div>
                
                <div class="flex items-center justify-between text-xs text-muted-foreground">
                  <button 
                    @click="prevPage"
                    :disabled="currentEmojiPage === 0"
                    class="px-2 py-1 rounded hover:bg-muted disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    上一页
                  </button>
                  <span>{{ currentEmojiPage + 1 }} / {{ totalPages }}</span>
                  <button 
                    @click="nextPage"
                    :disabled="currentEmojiPage >= totalPages - 1"
                    class="px-2 py-1 rounded hover:bg-muted disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    下一页
                  </button>
                </div>
              </div>
            </div>
          </div>
          <button 
            @click="handlePublish" 
            :disabled="uploading || !content.trim()"
            class="bg-primary text-primary-foreground px-6 py-2.5 rounded-full font-bold hover:opacity-90 transition-opacity shadow-sm disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {{ uploading ? '发布中...' : '发布' }}
          </button>
        </div>
      </div>

      <div v-if="loading" class="text-center py-8 text-muted-foreground">
        加载中...
      </div>

      <div v-else-if="posts.length === 0" class="text-center py-8 text-muted-foreground">
        暂无帖子，快来发布第一条吧！
      </div>

      <SocialPost 
        v-for="post in posts" 
        :key="post.id"
        :post="post"
      />
    </div>

    <div class="w-full md:w-80 shrink-0 flex flex-col gap-6">
      <div class="bg-card rounded-3xl p-6 shadow-custom border border-border">
        <h3 class="text-xl font-bold text-foreground mb-6">推荐关注</h3>
        <div class="flex flex-col gap-4">
          <div v-for="val in suggestedUsers" :key="val" class="flex items-center justify-between">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 bg-muted rounded-full" />
              <div class="flex flex-col">
                <span class="font-bold text-sm text-foreground">用户 {{ val }}</span>
                <span class="text-xs text-muted-foreground">新领养者</span>
              </div>
            </div>
            <button class="text-primary font-bold text-sm hover:underline">关注</button>
          </div>
        </div>
      </div>

      <div class="bg-card rounded-3xl p-6 shadow-custom border border-border">
        <h3 class="text-xl font-bold text-foreground mb-4">热门话题</h3>
        <div class="flex flex-wrap gap-2">
          <span 
            v-for="tag in trendingTags" 
            :key="tag" 
            @click="toggleTag(tag)"
            :class="['px-3 py-1.5 rounded-full text-sm font-medium cursor-pointer transition-colors', selectedTags.includes(tag) ? 'bg-primary text-primary-foreground' : 'bg-muted text-foreground hover:bg-primary hover:text-primary-foreground']"
          >
            {{ tag }}
          </span>
        </div>
      </div>
    </div>
  </div>

  <div v-if="showImageViewer" class="fixed inset-0 bg-black/90 z-[100] flex items-center justify-center" @click="closeImageViewer">
    <button @click.stop="closeImageViewer" class="absolute top-4 right-4 text-white hover:text-gray-300">
      <X :size="32" />
    </button>
    <img :src="imagePreviews[currentViewerIndex]" class="max-h-[90vh] max-w-[90vw] object-contain" @click.stop />
  </div>
</template>
