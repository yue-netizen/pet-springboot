<script setup lang="ts">
import { Briefcase } from 'lucide-vue-next'

const jobs = [
  { role: "兽医", type: "全职", location: "城市收容所总部" },
  { role: "领养协调员", type: "全职", location: "市中心分部" },
  { role: "动物护理志愿者", type: "兼职/周末", location: "所有地点" },
  { role: "社交媒体运营", type: "远程", location: "远程办公" },
]
</script>

<template>
  <div class="flex flex-col gap-10 max-w-5xl mx-auto w-full">
    <div class="text-center mb-6">
      <h1 class="text-4xl md:text-5xl font-extrabold text-foreground mb-4">加入我们的团队</h1>
      <p class="text-lg text-muted-foreground">
        成为爪印之家大家庭的一员。帮助我们改变无数动物的生活。
      </p>
    </div>

    <div class="flex flex-col gap-6">
      <div 
        v-for="(job, i) in jobs" 
        :key="i" 
        class="flex flex-col md:flex-row items-center justify-between bg-card p-8 rounded-2xl shadow-custom border border-border"
      >
        <div class="flex items-start gap-6 mb-4 md:mb-0">
          <div class="p-4 bg-muted text-foreground rounded-2xl hidden sm:block">
            <Briefcase :size="32" />
          </div>
          <div class="flex flex-col">
            <h3 class="text-2xl font-bold text-foreground mb-2">{{ job.role }}</h3>
            <div class="flex flex-wrap items-center gap-4 text-sm text-muted-foreground">
              <span class="bg-primary/10 text-primary px-3 py-1 rounded-full font-medium">{{ job.type }}</span>
              <span>{{ job.location }}</span>
            </div>
          </div>
        </div>
        <button class="w-full md:w-auto bg-foreground text-background px-8 py-3 rounded-xl font-bold hover:opacity-80 transition-opacity">
          立即申请
        </button>
      </div>
    </div>
  </div>
</template>
