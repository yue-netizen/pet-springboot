<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { getTipsById, type PetTips } from '@/api/tips'
import { BookOpen, ArrowLeft, Calendar, Tag } from 'lucide-vue-next'
import { useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const tips = ref<PetTips | null>(null)
const loading = ref(false)

const loadTips = async () => {
  try {
    loading.value = true
    const id = Number(route.params.id)
    const res = await getTipsById(id)
    tips.value = res.data || null
  } catch (error) {
    console.error('加载贴士详情失败', error)
    tips.value = null
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadTips()
})

const formatDate = (dateStr: string | undefined) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}年${month}月${day}日`
}
</script>

<template>
  <div class="min-h-screen bg-background">
    <div class="max-w-4xl mx-auto px-4 py-8">
      <button 
        @click="router.back()"
        class="flex items-center gap-2 text-muted-foreground hover:text-foreground mb-8 transition-colors"
      >
        <ArrowLeft :size="20" />
        <span>返回</span>
      </button>

      <div v-if="loading" class="flex justify-center py-20">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
      </div>

      <div v-else-if="tips" class="space-y-8">
        <div class="text-center">
          <BookOpen :size="48" class="text-primary mx-auto mb-4" />
          <h1 class="text-3xl md:text-4xl font-extrabold text-foreground mb-4">{{ tips.title }}</h1>
          
          <div class="flex flex-wrap justify-center gap-6 text-muted-foreground">
            <div class="flex items-center gap-2">
              <Tag :size="18" />
              <span>{{ tips.category }}</span>
            </div>
            <div class="flex items-center gap-2">
              <Calendar :size="18" />
              <span>{{ formatDate(tips.createTime) }}</span>
            </div>
          </div>
        </div>

        <div 
          v-if="tips.coverImage"
          class="rounded-2xl overflow-hidden aspect-video bg-secondary/20"
        >
          <img 
            :src="tips.coverImage" 
            :alt="tips.title"
            class="w-full h-full object-cover"
          />
        </div>

        <div 
          class="prose prose-lg max-w-none"
          v-html="tips.content"
        ></div>
      </div>

      <div v-else class="text-center py-20 text-muted-foreground">
        <BookOpen :size="48" class="mx-auto mb-4 opacity-50" />
        <p>贴士不存在</p>
      </div>
    </div>
  </div>
</template>
