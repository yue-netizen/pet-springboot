<script setup lang="ts">
import { BookOpen, ChevronRight } from 'lucide-vue-next'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getTipsList, type PetTips } from '@/api/tips'
import PetAiAssistant from '@/components/PetAiAssistant.vue'

const router = useRouter()
const tipsList = ref<PetTips[]>([])
const loading = ref(false)

const loadTips = async () => {
  try {
    loading.value = true
    const res = await getTipsList(1, 20)
    tipsList.value = res.data?.records || []
  } catch (error) {
    console.error('加载贴士失败', error)
    tipsList.value = []
  } finally {
    loading.value = false
  }
}

const formatDate = (dateStr: string | undefined) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}年${month}月${day}日`
}

const goToDetail = (id: number | undefined) => {
  if (id) {
    router.push(`/tips/${id}`)
  }
}

onMounted(() => {
  loadTips()
})
</script>

<template>
  <div class="flex flex-col gap-10">
    <div class="bg-primary/10 rounded-3xl p-10 flex flex-col items-center text-center">
      <BookOpen :size="48" class="text-primary mb-6" />
      <h1 class="text-4xl font-extrabold text-foreground mb-4">养宠贴士</h1>
      <p class="text-lg text-muted-foreground max-w-2xl">
        关于照顾毛茸茸朋友的一切知识。探索我们动物健康专家撰写的指南。
      </p>
    </div>

    <div v-if="loading" class="flex justify-center py-20">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
    </div>

    <div v-else-if="tipsList.length > 0" class="flex flex-wrap gap-6">
      <div 
        v-for="tips in tipsList" 
        :key="tips.id" 
        class="flex-1 min-w-[350px] bg-card p-8 rounded-2xl shadow-custom border border-border hover:shadow-lg transition-transform hover:-translate-y-1 group cursor-pointer"
        @click="goToDetail(tips.id)"
      >
        <div class="flex justify-between items-start mb-4">
          <span class="bg-secondary/20 text-secondary-foreground text-secondary font-bold px-3 py-1 rounded-lg text-xs">
            {{ tips.category }}
          </span>
          <span class="text-sm text-muted-foreground">{{ formatDate(tips.createTime) }}</span>
        </div>
        <h3 class="text-2xl font-bold text-foreground mb-4 line-clamp-2">{{ tips.title }}</h3>
        <div class="flex items-center gap-2 text-primary font-bold group-hover:underline">
          阅读更多 <ChevronRight :size="18" />
        </div>
      </div>
    </div>

    <div v-else class="text-center py-20 text-muted-foreground">
      <BookOpen :size="48" class="mx-auto mb-4 opacity-50" />
      <p>暂无养宠贴士</p>
    </div>

    <PetAiAssistant />
  </div>
</template>
