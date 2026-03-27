<script setup lang="ts">
import { Image as ImageIcon, Video, Smile } from 'lucide-vue-next'
import SocialPost from '@/components/SocialPost.vue'

const suggestedUsers = [1, 2, 3]
const trendingTags = ["#宠物领养", "#狗狗训练", "#猫咪爱好者", "#爪印之家社区", "#救助宠物"]
</script>

<template>
  <div class="flex flex-col md:flex-row gap-10 max-w-6xl mx-auto w-full">
    
    <div class="flex-1 flex flex-col gap-6">
      <div class="bg-card rounded-3xl p-6 shadow-custom border border-border w-full">
        <div class="flex items-start gap-4 mb-4">
          <div class="w-12 h-12 bg-primary/20 rounded-full flex-shrink-0" />
          <textarea 
            rows="3" 
            placeholder="与社区分享您的宠物时刻..."
            class="w-full bg-transparent resize-none border-none outline-none text-foreground text-lg placeholder:text-muted-foreground"
          ></textarea>
        </div>
        <div class="flex items-center justify-between pt-4 border-t border-border">
          <div class="flex gap-2">
            <button class="p-2 text-primary hover:bg-primary/10 rounded-full transition-colors flex items-center gap-2 font-medium">
              <ImageIcon :size="20" /> <span class="hidden sm:inline">图片</span>
            </button>
            <button class="p-2 text-secondary hover:bg-secondary/10 rounded-full transition-colors flex items-center gap-2 font-medium">
              <Video :size="20" /> <span class="hidden sm:inline">视频</span>
            </button>
            <button class="p-2 text-muted-foreground hover:bg-muted rounded-full transition-colors flex items-center gap-2 font-medium">
              <Smile :size="20" /> <span class="hidden sm:inline">心情</span>
            </button>
          </div>
          <button class="bg-primary text-primary-foreground px-6 py-2.5 rounded-full font-bold hover:opacity-90 transition-opacity shadow-sm">
            发布
          </button>
        </div>
      </div>

      <SocialPost 
        author="莎拉健康" 
        time="3小时前" 
        content="刚刚完成了我们的第一次训练课程！露娜表现得非常好。它几乎立刻就学会了「坐下」。🐾"
        image="https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&w=800&q=80"
      />
      
      <SocialPost 
        author="马克戴维斯" 
        time="5小时前" 
        content="有人有推荐给重度咬咬者的耐用玩具吗？我的金毛猎犬这周已经摧毁了三个所谓的「不可摧毁」玩具。"
        image=""
      />

      <SocialPost 
        author="艾米丽" 
        time="1天前" 
        content="公园日落散步。有这个小冒险家在身边，生活真美好。🌅🐶"
        image="https://images.unsplash.com/photo-1517849845537-4d257902454a?auto=format&fit=crop&w=800&q=80"
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
            class="bg-muted text-foreground px-3 py-1.5 rounded-full text-sm font-medium hover:bg-primary hover:text-primary-foreground cursor-pointer transition-colors"
          >
            {{ tag }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>
