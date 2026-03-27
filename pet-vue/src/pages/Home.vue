<script setup lang="ts">
import { RouterLink } from 'vue-router'
import { ArrowRight, Info, Heart, Shield } from 'lucide-vue-next'
import PetCard from '@/components/PetCard.vue'

const features = [
  { icon: Heart, title: "健康宠物", desc: "所有宠物在领养前都接受全面健康检查。" },
  { icon: Shield, title: "认证流程", desc: "我们安全的领养流程保护您和宠物的权益。" },
  { icon: Info, title: "持续支持", desc: "我们提供终身建议和社区支持。" }
]
</script>

<template>
  <div class="flex flex-col gap-16">
    <section class="relative flex flex-col md:flex-row items-center bg-card rounded-3xl overflow-hidden shadow-custom border border-border">
      <div class="flex flex-col p-10 md:p-16 flex-1 z-10">
        <span class="text-secondary font-bold tracking-wider mb-4 uppercase">寻找您的最佳伙伴</span>
        <h1 class="text-5xl md:text-6xl font-extrabold text-foreground mb-6 leading-tight">
          给宠物一个<br/>温暖的<span class="text-primary">家</span>
        </h1>
        <p class="text-muted-foreground text-lg mb-10 max-w-lg leading-relaxed">
          数以千计的可爱宠物正在等待第二次机会。今天就领养，体验无条件的爱。
        </p>
        <div class="flex flex-wrap items-center gap-4">
          <RouterLink to="/adopt" class="bg-primary text-primary-foreground px-8 py-4 rounded-full font-bold text-lg hover:opacity-90 transition-opacity shadow-custom">
            立即领养
          </RouterLink>
          <RouterLink to="/stories" class="bg-muted text-foreground px-8 py-4 rounded-full font-bold text-lg hover:bg-border transition-colors flex items-center gap-2">
            阅读故事 <ArrowRight :size="20"/>
          </RouterLink>
        </div>
      </div>
      <div class="flex-1 w-full h-[300px] md:h-auto min-h-[500px] relative">
        <img 
          src="https://images.unsplash.com/photo-1450778869180-41d0601e046e?auto=format&fit=crop&w=1200&q=80" 
          alt="快乐的狗狗" 
          class="absolute inset-0 w-full h-full object-cover object-center"
        />
        <div class="absolute inset-0 bg-gradient-to-r from-card via-card/80 to-transparent hidden md:block"></div>
      </div>
    </section>

    <section class="flex flex-col md:flex-row gap-8">
      <div 
        v-for="(feature, i) in features" 
        :key="i" 
        class="flex-1 bg-background p-8 rounded-2xl border border-border shadow-custom flex items-start gap-4"
      >
        <div class="bg-primary/10 text-primary p-4 rounded-2xl">
          <component :is="feature.icon" :size="28" />
        </div>
        <div>
          <h3 class="text-xl font-bold text-foreground mb-2">{{ feature.title }}</h3>
          <p class="text-muted-foreground">{{ feature.desc }}</p>
        </div>
      </div>
    </section>

    <section class="flex flex-col gap-8">
      <div class="flex items-end justify-between">
        <div>
          <h2 class="text-4xl font-extrabold text-foreground mb-3">推荐宠物</h2>
          <p class="text-muted-foreground">这些可爱的朋友正在等待您。</p>
        </div>
        <RouterLink to="/adopt" class="text-primary font-bold hover:underline flex items-center gap-1">
          查看全部 <ArrowRight :size="18" />
        </RouterLink>
      </div>
      <div class="flex flex-wrap gap-8">
        <div class="flex-1 min-w-[300px]">
          <PetCard name="贝拉" breed="拉布拉多混血" age="1岁" image="https://images.unsplash.com/photo-1544568100-847a948585b9?auto=format&fit=crop&w=600&q=80"/>
        </div>
        <div class="flex-1 min-w-[300px]">
          <PetCard name="露娜" breed="波斯猫" age="6个月" image="https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=600&q=80"/>
        </div>
        <div class="flex-1 min-w-[300px]">
          <PetCard name="查理" breed="比格犬" age="3岁" image="https://images.unsplash.com/photo-1537151608828-ea2b11777ee8?auto=format&fit=crop&w=600&q=80"/>
        </div>
      </div>
    </section>
  </div>
</template>
