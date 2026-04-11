<script setup lang="ts">
import { ref, onMounted, watch, nextTick, computed } from 'vue'
import { useRoute } from 'vue-router'
import { Send, Users, MoreVertical, Search, MessageCircle, Smile, Image as ImageIcon, X } from 'lucide-vue-next'
import {
  getConversations,
  getOrCreateConversation,
  getMessages,
  sendMessage,
  markAsRead,
  type Conversation,
  type Message
} from '@/api/chat'
import { getUserById } from '@/api/user'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()

const conversations = ref<Conversation[]>([])
const messages = ref<Message[]>([])
const currentConversationId = ref<number | null>(null)
const currentChatUser = ref<any>(null)
const currentUserInfo = ref<any>(null)
const newMessage = ref('')
const loading = ref(false)
const messagesLoading = ref(false)
const sendingMessage = ref(false)
const searchKeyword = ref('')

const showEmojiPicker = ref(false)
const showImageUpload = ref(false)
const showChatInfo = ref(false)
const selectedImage = ref<File | null>(null)
const imagePreview = ref<string>('')
const imageInputRef = ref<HTMLInputElement | null>(null)

const emojis = ['😀', '😂', '🥰', '😍', '🤩', '😊', '😇', '🙂', '😉', '😌', '😋', '🤤', '😎', '🤗', '😘', '😚', '😜', '🤪', '😝', '🤑', '🤔', '🤫', '🤭', '😏', '😒', '🙄', '😮', '😯', '😲', '😳', '🥺', '😢', '😭', '😤', '😠', '😡', '🤬', '😈', '👿', '💀', '☠️', '💩', '🤡', '👹', '👺', '👻', '👽', '👾', '🤖', '❤️', '💕', '💖', '💗', '💓', '💝', '💘', '💞', '👍', '👎', '👏', '🙌', '🤝', '✌️', '🤞', '🤟', '🤘', '👌', '🤙', '💪', '🎉', '🎊', '🎁', '🏆', '⭐', '🌟', '✨', '💫', '🔥', '💯']

const currentUserId = userStore.userInfo?.id
const myAvatar = computed(() => userStore.userInfo?.avatar || '')
const myNickname = computed(() => userStore.userInfo?.nickname || '我')

const loadConversations = async () => {
  if (!userStore.isLoggedIn()) return
  loading.value = true
  try {
    const res = await getConversations()
    conversations.value = res.data || []
  } catch (error) {
    console.error('加载会话列表失败', error)
  } finally {
    loading.value = false
  }
}

const loadMessages = async (conversationId: number) => {
  if (!conversationId) return
  messagesLoading.value = true
  try {
    const res = await getMessages(conversationId, 1, 50)
    messages.value = res.data?.records || []
    markAsRead(conversationId).catch(e => console.error('标记已读失败', e))
    await nextTick()
    scrollToBottom()
  } catch (error) {
    console.error('加载消息失败', error)
  } finally {
    messagesLoading.value = false
  }
}

const selectConversation = async (conversation: Conversation) => {
  currentConversationId.value = conversation.id
  const otherUserId = conversation.user1Id === currentUserId ? conversation.user2Id : conversation.user1Id
  const isUser1 = conversation.user1Id !== currentUserId
  
  if (isUser1 && (conversation.user1Nickname || conversation.user1Avatar)) {
    currentChatUser.value = {
      id: conversation.user1Id,
      nickname: conversation.user1Nickname,
      avatar: conversation.user1Avatar
    }
  } else if (!isUser1 && (conversation.user2Nickname || conversation.user2Avatar)) {
    currentChatUser.value = {
      id: conversation.user2Id,
      nickname: conversation.user2Nickname,
      avatar: conversation.user2Avatar
    }
  } else {
    try {
      const res = await getUserById(otherUserId)
      currentChatUser.value = res.data
    } catch (e) {
      currentChatUser.value = { id: otherUserId, nickname: '用户' + otherUserId }
    }
  }
  loadMessages(conversation.id)
}

const compressImage = (file: File, maxWidth: number = 800, quality: number = 0.7): Promise<string> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = (e) => {
      const img = new Image()
      img.onload = () => {
        const canvas = document.createElement('canvas')
        let width = img.width
        let height = img.height
        if (width > maxWidth) {
          height = (height * maxWidth) / width
          width = maxWidth
        }
        canvas.width = width
        canvas.height = height
        const ctx = canvas.getContext('2d')
        ctx?.drawImage(img, 0, 0, width, height)
        resolve(canvas.toDataURL('image/jpeg', quality))
      }
      img.onerror = reject
      img.src = e.target?.result as string
    }
    reader.onerror = reject
    reader.readAsDataURL(file)
  })
}

const handleSendImage = async () => {
  if (!selectedImage.value || !currentConversationId.value || sendingMessage.value) return
  
  sendingMessage.value = true
  try {
    const base64 = await compressImage(selectedImage.value, 800, 0.7)
    
    const receiverId = getReceiverId()
    
    await sendMessage({ receiverId, content: base64, type: 2 })
    selectedImage.value = null
    imagePreview.value = ''
    showImageUpload.value = false
    loadMessages(currentConversationId.value!)
    loadConversations()
  } catch (error) {
    console.error('发送图片失败', error)
  } finally {
    sendingMessage.value = false
  }
}

const handleSendText = async () => {
  if (!newMessage.value.trim() || !currentConversationId.value || sendingMessage.value) return
  
  sendingMessage.value = true
  try {
    const receiverId = getReceiverId()
    
    await sendMessage({ receiverId, content: newMessage.value.trim() })
    newMessage.value = ''
    loadMessages(currentConversationId.value!)
    loadConversations()
  } catch (error) {
    console.error('发送消息失败', error)
  } finally {
    sendingMessage.value = false
  }
}

const handleSend = () => {
  if (selectedImage.value) {
    handleSendImage()
  } else {
    handleSendText()
  }
}

const getReceiverId = () => {
  return conversations.value.find(c => c.id === currentConversationId.value)?.user1Id === currentUserId
    ? conversations.value.find(c => c.id === currentConversationId.value)!.user2Id
    : conversations.value.find(c => c.id === currentConversationId.value)!.user1Id
}

const selectEmoji = (emoji: string) => {
  newMessage.value += emoji
  showEmojiPicker.value = false
}

const handleImageSelect = async (event: Event) => {
  const input = event.target as HTMLInputElement
  if (input.files && input.files[0]) {
    selectedImage.value = input.files[0]
    try {
      const compressed = await compressImage(input.files[0], 400, 0.6)
      imagePreview.value = compressed
    } catch {
      const reader = new FileReader()
      reader.onload = (e) => { imagePreview.value = e.target?.result as string }
      reader.readAsDataURL(input.files[0])
    }
    showImageUpload.value = true
  }
}

const removeSelectedImage = () => {
  selectedImage.value = null
  imagePreview.value = ''
  showImageUpload.value = false
}

const triggerImageSelect = () => {
  if (imageInputRef.value) {
    imageInputRef.value.value = ''
    imageInputRef.value.click()
  }
}

const scrollToBottom = () => {
  const container = document.getElementById('messages-container')
  if (container) {
    container.scrollTop = container.scrollHeight
  }
}

const formatTimeShort = (timeStr: string) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

const formatDateFull = (timeStr: string) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  if (diff < 86400000 && date.getDate() === now.getDate()) {
    return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  } else if (diff < 172800000) {
    return '昨天 ' + date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  } else {
    return date.toLocaleDateString('zh-CN', { month: 'numeric', day: 'numeric' }) + ' ' + 
           date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  }
}

const formatTime = (timeStr: string) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  if (diff < 86400000 && date.getDate() === now.getDate()) {
    return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  } else if (diff < 172800000) {
    return '昨天'
  } else {
    return date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
  }
}

const shouldShowTimeDivider = (index: number): boolean => {
  if (index === 0) return true
  const currentTime = new Date(messages.value[index].createTime).getTime()
  const prevTime = new Date(messages.value[index - 1].createTime).getTime()
  return currentTime - prevTime > 300000
}

const clearMessages = () => {
  if (confirm('确定要清空聊天记录吗？')) {
    messages.value = []
    showChatInfo.value = false
  }
}

onMounted(async () => {
  await loadConversations()
  
  const targetUserId = route.query.userId as string | undefined
  if (targetUserId && currentUserId) {
    try {
      const res = await getOrCreateConversation(Number(targetUserId))
      if (res.data) {
        const existingIndex = conversations.value.findIndex(c => c.id === res.data.id)
        if (existingIndex >= 0) {
          conversations.value[existingIndex] = res.data
        } else {
          conversations.value.unshift(res.data)
        }
        selectConversation(res.data)
      }
    } catch (e) {
      console.error('获取或创建会话失败', e)
    }
  } else if (conversations.value.length > 0) {
    selectConversation(conversations.value[0])
  }
})

watch(() => route.query.userId, async (newVal) => {
  if (newVal && currentUserId) {
    try {
      const res = await getOrCreateConversation(Number(newVal))
      if (res.data) {
        const existingIndex = conversations.value.findIndex(c => c.id === res.data.id)
        if (existingIndex >= 0) {
          conversations.value.splice(existingIndex, 1)
        }
        conversations.value.unshift(res.data)
        selectConversation(res.data)
      }
    } catch (e) {
      console.error('获取或创建会话失败', e)
    }
  }
})

const closeAllPopups = () => {
  showEmojiPicker.value = false
}
</script>

<template>
  <div class="flex flex-col md:flex-row h-[75vh] min-h-[600px] w-full bg-card rounded-3xl shadow-custom border border-border overflow-hidden relative" @click="closeAllPopups">
    
    <div class="w-full md:w-80 flex flex-col border-r border-border bg-background">
      <div class="p-6 border-b border-border">
        <h2 class="text-2xl font-bold text-foreground flex items-center gap-2 mb-4">
          <Users :size="24" class="text-primary" /> 消息
        </h2>
        <div class="relative">
          <Search class="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground" :size="18" />
          <input 
            v-model="searchKeyword"
            type="text" 
            placeholder="搜索聊天..." 
            class="w-full bg-card border border-border rounded-xl py-2.5 pl-10 pr-4 text-sm focus:outline-none focus:ring-2 focus:ring-primary" 
          />
        </div>
      </div>
      
      <div v-if="loading" class="flex-1 flex items-center justify-center text-muted-foreground">
        加载中...
      </div>
      
      <div v-else-if="conversations.length === 0" class="flex-1 flex items-center justify-center text-muted-foreground text-sm">
        暂无消息
      </div>
      
      <div v-else class="flex-1 overflow-y-auto">
        <div 
          v-for="conv in conversations.filter(c => !searchKeyword || 
            ((c.user1Id === currentUserId ? c.user2Nickname : c.user1Nickname) || '').includes(searchKeyword)
          )" 
          :key="conv.id" 
          @click="selectConversation(conv); showChatInfo = false"
          :class="[
            'flex items-start gap-3 p-4 border-b border-border cursor-pointer transition-colors',
            currentConversationId === conv.id ? 'bg-primary/5' : 'hover:bg-muted/50'
          ]"
        >
          <div class="w-12 h-12 rounded-full bg-primary/20 flex-shrink-0 flex items-center justify-center overflow-hidden">
            <img 
              v-if="conv.user1Id === currentUserId ? conv.user2Avatar : conv.user1Avatar" 
              :src="conv.user1Id === currentUserId ? conv.user2Avatar : conv.user1Avatar" 
              alt="" 
              class="w-full h-full object-cover" 
            />
            <Users v-else :size="24" class="text-primary" />
          </div>
          <div class="flex-1 min-w-0">
            <div class="flex justify-between items-baseline mb-1">
              <h4 class="font-bold text-foreground text-sm truncate">
                {{ conv.user1Id === currentUserId ? (conv.user2Nickname || '用户' + conv.user2Id) : (conv.user1Nickname || '用户' + conv.user1Id) }}
              </h4>
              <span class="text-xs text-muted-foreground whitespace-nowrap ml-2">{{ formatTime(conv.updateTime) }}</span>
            </div>
            <div class="flex justify-between items-center">
              <p class="text-sm text-muted-foreground truncate">{{ conv.lastMessage }}</p>
              <span 
                v-if="(conv.user1Id === currentUserId ? conv.unreadCount1 : conv.unreadCount2) > 0" 
                class="w-5 h-5 flex items-center justify-center bg-primary text-primary-foreground text-xs font-bold rounded-full ml-2 shrink-0"
              >
                {{ conv.user1Id === currentUserId ? conv.unreadCount1 : conv.unreadCount2 }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="flex-1 flex flex-col bg-card relative">
      <template v-if="currentConversationId && currentChatUser">
        <div class="flex items-center justify-between px-6 py-4 border-b border-border bg-card">
          <div class="flex items-center gap-3">
            <div class="w-10 h-10 rounded-full bg-primary/20 flex-shrink-0 overflow-hidden">
              <img v-if="currentChatUser.avatar" :src="currentChatUser.avatar" alt="" class="w-full h-full object-cover" />
              <Users v-else :size="20" class="m-auto mt-2.5 text-primary" />
            </div>
            <div>
              <h3 class="font-bold text-foreground">{{ currentChatUser.nickname || '用户' + currentChatUser.id }}</h3>
            </div>
          </div>
          <button @click.stop="showChatInfo = !showChatInfo" class="p-2 hover:bg-muted rounded-full transition-colors text-muted-foreground hover:text-foreground">
            <MoreVertical :size="20" />
          </button>
        </div>

        <div id="messages-container" class="flex-1 overflow-y-auto px-4 py-4 flex flex-col bg-background/50 space-y-3">
          <div v-if="messagesLoading" class="text-center py-8 text-muted-foreground">加载消息中...</div>
          
          <div v-else-if="messages.length > 0">
            <div v-for="(msg, index) in messages" :key="msg.id">
              
              <div v-if="shouldShowTimeDivider(index)" class="text-center mb-2">
                <span class="text-xs text-muted-foreground bg-muted/50 px-3 py-1 rounded-full inline-block">
                  {{ formatDateFull(msg.createTime) }}
                </span>
              </div>

              <div :class="['flex w-full mb-1', msg.senderId === currentUserId ? 'justify-end' : 'justify-start']">
                
                <div v-if="msg.senderId !== currentUserId" class="flex items-end gap-2 max-w-[70%]">
                  <div class="w-9 h-9 rounded-full bg-primary/20 flex-shrink-0 overflow-hidden">
                    <img v-if="currentChatUser?.avatar" :src="currentChatUser.avatar" alt="" class="w-full h-full object-cover" />
                    <Users v-else :size="18" class="m-auto mt-2 text-primary" />
                  </div>
                  
                  <div v-if="msg.type === 2" class="bg-card border border-border rounded-2xl rounded-bl-sm overflow-hidden shadow-sm max-w-[240px]">
                    <img :src="msg.content" alt="图片" class="w-full h-auto max-h-[200px] object-cover" />
                  </div>
                  <div v-else class="bg-card border border-border px-4 py-2.5 rounded-2xl rounded-bl-sm text-foreground shadow-sm">
                    <span class="text-[15px] leading-relaxed whitespace-pre-wrap break-words">{{ msg.content }}</span>
                  </div>
                </div>

                <div v-else class="flex items-end gap-2 max-w-[70%]">
                  
                  <div v-if="msg.type === 2" class="bg-primary rounded-2xl rounded-br-sm overflow-hidden shadow-sm max-w-[240px]">
                    <img :src="msg.content" alt="图片" class="w-full h-auto max-h-[200px] object-cover" />
                  </div>
                  <div v-else class="bg-primary text-primary-foreground px-4 py-2.5 rounded-2xl rounded-br-sm shadow-sm">
                    <span class="text-[15px] leading-relaxed whitespace-pre-wrap break-words">{{ msg.content }}</span>
                  </div>
                  
                  <div class="w-9 h-9 rounded-full bg-primary/30 flex-shrink-0 overflow-hidden">
                    <img v-if="myAvatar" :src="myAvatar" alt="" class="w-full h-full object-cover" />
                    <Users v-else :size="18" class="m-auto mt-2 text-primary-foreground" />
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div v-else class="text-center py-12 text-muted-foreground text-sm flex flex-col items-center gap-2">
            <div class="w-16 h-16 rounded-full bg-primary/10 flex items-center justify-center mb-2">
              <MessageCircle :size="28" class="text-primary/40" />
            </div>
            暂无消息，开始聊天吧~
          </div>
        </div>

        <div v-if="imagePreview" class="px-4 py-2 bg-card border-t border-border">
          <div class="relative inline-block">
            <img :src="imagePreview" alt="预览" class="h-16 w-16 object-cover rounded-lg border border-border" />
            <button @click="removeSelectedImage" class="absolute -top-2 -right-2 w-5 h-5 bg-destructive text-white rounded-full flex items-center justify-center text-xs hover:bg-destructive/80">
              <X :size="12" />
            </button>
          </div>
        </div>

        <div class="p-4 border-t border-border bg-card">
          <div class="flex items-end gap-2 bg-background border border-border rounded-2xl px-3 py-2">
            
            <div class="relative">
              <button @click.stop="showEmojiPicker = !showEmojiPicker" class="p-2 hover:bg-muted rounded-full transition-colors text-muted-foreground hover:text-foreground">
                <Smile :size="22" />
              </button>
              
              <div v-if="showEmojiPicker" @click.stop class="absolute bottom-12 left-0 bg-card border border-border rounded-2xl shadow-lg p-3 w-72 z-50" style="display: grid; grid-template-columns: repeat(10, 1fr); gap: 4px;">
                <button
                  v-for="emoji in emojis"
                  :key="emoji"
                  @click="selectEmoji(emoji)"
                  class="w-7 h-7 flex items-center justify-center text-lg hover:bg-muted rounded-md transition-colors"
                >
                  {{ emoji }}
                </button>
              </div>
            </div>

            <button @click="triggerImageSelect" class="p-2 hover:bg-muted rounded-full transition-colors text-muted-foreground hover:text-foreground cursor-pointer">
              <ImageIcon :size="22" />
            </button>
            <input ref="imageInputRef" type="file" accept="image/*" class="hidden" @change="handleImageSelect" />

            <input 
              v-model="newMessage"
              type="text" 
              placeholder="输入您的消息..." 
              class="flex-1 bg-transparent border-none outline-none px-2 text-foreground text-sm min-h-[36px]"
              @keyup.enter="handleSendText"
              :disabled="sendingMessage"
            />
            
            <button 
              @click="handleSend"
              :disabled="(!newMessage.trim() && !selectedImage) || sendingMessage"
              class="w-9 h-9 bg-primary text-primary-foreground rounded-full flex items-center justify-center hover:opacity-90 transition-opacity disabled:opacity-50 disabled:cursor-not-allowed shrink-0"
            >
              <Send :size="16" class="-mr-0.5" />
            </button>
          </div>
        </div>
      </template>

      <div v-else class="flex-1 flex flex-col items-center justify-center gap-4 text-muted-foreground">
        <MessageCircle :size="48" class="opacity-30" />
        <p>选择一个对话或从用户主页发起私信</p>
      </div>
    </div>

    <div v-if="showChatInfo && currentChatUser" @click.stop class="absolute top-0 right-0 w-72 h-full bg-card border-l border-border shadow-lg z-40 flex flex-col">
      <div class="p-6 border-b border-border text-center">
        <div class="w-20 h-20 rounded-full bg-primary/20 mx-auto mb-3 overflow-hidden">
          <img v-if="currentChatUser.avatar" :src="currentChatUser.avatar" alt="" class="w-full h-full object-cover" />
          <Users v-else :size="40" class="mt-7 text-primary" />
        </div>
        <h3 class="font-bold text-foreground text-lg">{{ currentChatUser.nickname || '用户' + currentChatUser.id }}</h3>
        <p class="text-xs text-muted-foreground mt-1">ID: {{ currentChatUser.id }}</p>
      </div>

      <div class="flex-1 overflow-y-auto">
        <div class="py-3 px-6 border-b border-border">
          <button class="w-full flex items-center justify-between py-2 text-left hover:text-primary transition-colors group">
            <span class="text-sm text-foreground">查找聊天内容</span>
            <Search :size="16" class="text-muted-foreground group-hover:text-primary" />
          </button>
        </div>
        
        <div class="py-3 px-6 border-b border-border">
          <label class="w-full flex items-center justify-between py-2 cursor-pointer">
            <span class="text-sm text-foreground">消息免打扰</span>
            <div class="relative w-11 h-6 bg-muted rounded-full transition-colors">
              <div class="absolute left-1 top-1 w-4 h-4 bg-white rounded-full shadow transition-transform"></div>
            </div>
          </label>
        </div>
        
        <div class="py-3 px-6 border-b border-border">
          <label class="w-full flex items-center justify-between py-2 cursor-pointer">
            <span class="text-sm text-foreground">置顶聊天</span>
            <div class="relative w-11 h-6 bg-muted rounded-full transition-colors">
              <div class="absolute left-1 top-1 w-4 h-4 bg-white rounded-full shadow transition-transform"></div>
            </div>
          </label>
        </div>
        
        <div class="py-3 px-6 mt-4">
          <button @click="clearMessages" class="w-full text-center py-2 text-sm text-destructive hover:bg-destructive/5 rounded-lg transition-colors">
            清空聊天记录
          </button>
        </div>
      </div>
    </div>
  </div>
</template>