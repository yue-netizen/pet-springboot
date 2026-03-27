<script setup lang="ts">
import { BookOpen, ChevronRight } from 'lucide-vue-next'

const articles = [
  { title: "幼犬营养指南", cat: "饮食", date: "2023年10月12日" },
  { title: "如何与救助宠物建立信任", cat: "行为", date: "2023年9月28日" },
  { title: "家庭美容要点", cat: "健康", date: "2023年9月15日" },
  { title: "为猫咪创建安全的室内空间", cat: "环境", date: "2023年8月22日" },
  { title: "了解狗狗的肢体语言", cat: "行为", date: "2023年8月10日" },
  { title: "疫苗接种时间表解读", cat: "健康", date: "2023年7月5日" },
]
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

    <div class="flex flex-wrap gap-6">
      <div 
        v-for="(article, i) in articles" 
        :key="i" 
        class="flex-1 min-w-[350px] bg-card p-8 rounded-2xl shadow-custom border border-border hover:shadow-lg transition-transform hover:-translate-y-1 group cursor-pointer"
      >
        <div class="flex justify-between items-start mb-4">
          <span class="bg-secondary/20 text-secondary-foreground text-secondary font-bold px-3 py-1 rounded-lg text-xs">
            {{ article.cat }}
          </span>
          <span class="text-sm text-muted-foreground">{{ article.date }}</span>
        </div>
        <h3 class="text-2xl font-bold text-foreground mb-4 line-clamp-2">{{ article.title }}</h3>
        <div class="flex items-center gap-2 text-primary font-bold group-hover:underline">
          阅读更多 <ChevronRight :size="18" />
        </div>
      </div>
    </div>
  </div>
</template>
