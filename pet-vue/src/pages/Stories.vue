<script setup lang="ts">
import { Heart } from 'lucide-vue-next'

const stories = [1, 2, 3, 4]
</script>

<template>
  <div class="flex flex-col gap-12">
    <div class="text-center max-w-3xl mx-auto">
      <h1 class="text-4xl md:text-5xl font-extrabold text-foreground mb-6">领养故事</h1>
      <p class="text-lg text-muted-foreground leading-relaxed">
        没有什么比看到我们救助的宠物在永久家园中茁壮成长更让我们感动的了。阅读这些充满爱的励志故事。
      </p>
    </div>

    <div class="flex flex-wrap gap-8">
      <div v-for="item in stories" :key="item" class="flex flex-col md:flex-row bg-card rounded-3xl overflow-hidden shadow-custom border border-border w-full">
        <div class="md:w-2/5 h-64 md:h-auto min-h-[300px] bg-muted">
          <img 
            :src="`https://images.unsplash.com/photo-1560807707-8cc77767d783?auto=format&fit=crop&w=600&q=80`" 
            alt="幸福的家庭" 
            class="w-full h-full object-cover"
          />
        </div>
        <div class="md:w-3/5 p-8 md:p-12 flex flex-col justify-center">
          <div class="flex items-center gap-2 text-secondary mb-4">
            <Heart fill="currentColor" :size="20" />
            <span class="font-bold tracking-wide">成功故事 #{{ item }}</span>
          </div>
          <h2 class="text-3xl font-bold text-foreground mb-4">洛奇的新开始</h2>
          <p class="text-muted-foreground text-lg leading-relaxed mb-6">
            "我们发现洛奇时，它只是一只胆小的小狗。爪印之家的团队确保它完全康复。把它带回家给我们带来了巨大的快乐。它不仅仅是宠物，它是我们的家人。"
          </p>
          <div class="flex items-center gap-4 mt-auto">
            <div class="w-12 h-12 bg-muted rounded-full overflow-hidden">
              <img src="https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=200&q=80" alt="头像" class="w-full h-full object-cover" />
            </div>
            <div>
              <div class="font-bold text-foreground">约翰逊一家</div>
              <div class="text-sm text-muted-foreground">3个月前领养</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
