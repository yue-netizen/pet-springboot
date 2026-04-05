<script setup lang="ts">
import { RouterLink } from 'vue-router'
import { Heart, MapPin } from 'lucide-vue-next'

interface Props {
  id?: number
  name?: string
  breed?: string
  age?: string
  location?: string
  image?: string
}

const props = withDefaults(defineProps<Props>(), {
  id: 0,
  name: "小黄",
  breed: "金毛寻回犬",
  age: "2岁",
  location: "城市收容所",
  image: "https://images.unsplash.com/photo-1552053831-71594a27632d?auto=format&fit=crop&w=600&q=80"
})
</script>

<template>
  <div data-cmp="PetCard" class="flex flex-col bg-card rounded-2xl overflow-hidden shadow-custom border border-border transition-transform hover:-translate-y-1">
    <div class="relative h-64 overflow-hidden">
      <img :src="image" :alt="name" class="w-full h-full object-cover" />
      <button class="absolute top-4 right-4 p-2 bg-card/80 backdrop-blur-sm rounded-full text-muted-foreground hover:text-destructive transition-colors">
        <Heart :size="20" />
      </button>
    </div>
    <div class="flex flex-col p-6 w-full">
      <div class="flex justify-between items-start mb-2">
        <h3 class="text-2xl font-bold text-foreground">{{ name }}</h3>
        <span class="bg-primary/10 text-primary px-3 py-1 rounded-full text-xs font-semibold">
          {{ age }}
        </span>
      </div>
      <p class="text-muted-foreground font-medium mb-4">{{ breed }}</p>
      <div class="flex items-center gap-2 text-sm text-muted-foreground mb-6">
        <MapPin :size="16" />
        <span>{{ location }}</span>
      </div>
      <RouterLink :to="`/pet/${id}`" class="w-full text-center bg-primary text-primary-foreground py-3 rounded-xl font-bold hover:opacity-90 transition-opacity">
        领养 {{ name }}
      </RouterLink>
    </div>
  </div>
</template>
