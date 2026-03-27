<script setup lang="ts">
import { RouterLink } from 'vue-router'
import { ChevronRight } from 'lucide-vue-next'

interface SidebarItem {
  id: string
  label: string
}

interface Props {
  title?: string
  items?: SidebarItem[]
  activeItem?: string
}

const props = withDefaults(defineProps<Props>(), {
  title: "导航",
  items: () => [{ id: "1", label: "项目 1" }],
  activeItem: "1"
})

const emit = defineEmits<{
  (e: 'update:activeItem', value: string): void
  (e: 'itemClick', id: string): void
}>()

const handleItemClick = (id: string) => {
  emit('update:activeItem', id)
  emit('itemClick', id)
}
</script>

<template>
  <aside data-cmp="ContentSidebar" class="w-full md:w-72 shrink-0">
    <div class="bg-card shadow-custom rounded-2xl p-6 border border-border sticky top-28">
      <h2 class="text-xl font-bold text-foreground mb-6 pb-4 border-b border-border">{{ title }}</h2>
      <div class="flex flex-col gap-2">
        <button
          v-for="item in items"
          :key="item.id"
          @click="handleItemClick(item.id)"
          :class="[
            'flex items-center justify-between w-full p-4 rounded-xl text-left transition-all',
            activeItem === item.id 
              ? 'bg-primary text-primary-foreground font-semibold shadow-custom' 
              : 'bg-muted text-muted-foreground hover:bg-muted/80 hover:text-foreground'
          ]"
        >
          <span>{{ item.label }}</span>
          <ChevronRight v-if="activeItem === item.id" :size="18" />
        </button>
      </div>
      <div class="mt-8">
        <RouterLink to="/chat" class="flex items-center justify-center w-full py-4 rounded-xl bg-secondary text-secondary-foreground font-bold hover:opacity-90 transition-opacity shadow-custom">
          联系客服
        </RouterLink>
      </div>
    </div>
  </aside>
</template>
