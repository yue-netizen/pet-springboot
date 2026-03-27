<script setup lang="ts">
import { ref } from 'vue'
import { Heart, MessageSquare, Share2, User } from 'lucide-vue-next'

interface Props {
  author?: string
  time?: string
  content?: string
  image?: string
}

const props = withDefaults(defineProps<Props>(), {
  author: "宠物爱好者99",
  time: "2小时前",
  content: "刚刚领养了这只小家伙！欢迎回家，小宝贝。🐶❤️",
  image: "https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=800&q=80"
})

const liked = ref(false)
const likes = ref(24)

const handleLike = () => {
  liked.value = !liked.value
  likes.value = liked.value ? likes.value + 1 : likes.value - 1
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
          <span class="font-bold text-foreground">{{ author }}</span>
          <span class="text-xs text-muted-foreground">{{ time }}</span>
        </div>
      </div>
      <button class="bg-muted text-foreground px-4 py-1.5 rounded-full text-sm font-semibold hover:bg-primary hover:text-primary-foreground transition-colors">
        关注
      </button>
    </div>
    
    <p class="text-foreground mb-4 leading-relaxed">{{ content }}</p>
    
    <div v-if="image" class="rounded-xl overflow-hidden mb-6 max-h-[400px]">
      <img :src="image" alt="帖子内容" class="w-full h-full object-cover" />
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
