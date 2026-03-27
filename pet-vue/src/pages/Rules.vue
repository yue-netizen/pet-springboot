<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import ContentSidebar from '@/components/ContentSidebar.vue'
import { getConfigByKey } from '@/api/config'

const activeSection = ref('adoption-rules')
const rulesContent = ref('')
const loading = ref(true)

const sections = [
  { id: 'adoption-rules', label: '领养规则' },
  { id: 'adoption-agreement', label: '领养协议' },
  { id: 'foster-rules', label: '寄养规则' },
  { id: 'foster-agreement', label: '寄养协议' }
]

const rules = computed(() => {
  return rulesContent.value.split('\n').filter(r => r.trim())
})

async function loadRulesContent() {
  try {
    const res = await getConfigByKey('adoption_rules')
    rulesContent.value = res.data?.configValue || ''
  } catch (e) {
    console.error('加载领养规则失败', e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadRulesContent()
})
</script>

<template>
  <div class="flex flex-col md:flex-row gap-10">
    
    <div class="flex-1 flex flex-col gap-8 bg-card p-8 md:p-12 rounded-3xl shadow-custom border border-border h-fit">
      <div>
        <span class="text-primary font-bold uppercase tracking-wider mb-2 block">指南</span>
        <h1 class="text-4xl font-extrabold text-foreground mb-8">规则与协议</h1>
        
        <div class="animate-in fade-in slide-in-from-bottom-4 duration-500">
          <h3 class="text-2xl font-bold text-foreground mb-6">
            {{ sections.find(s => s.id === activeSection)?.label }}
          </h3>
          
          <div class="bg-muted p-8 rounded-2xl border border-border max-h-[600px] overflow-y-auto">
            <p class="text-foreground leading-relaxed mb-6 font-medium">
              请在继续之前仔细阅读以下{{ sections.find(s => s.id === activeSection)?.label }}。遵守这些指南可确保宠物的安全和福祉。
            </p>
            <div v-if="loading" class="py-8 text-center text-muted-foreground">
              加载中...
            </div>
            <ul v-else class="flex flex-col gap-4 text-muted-foreground list-disc pl-6 leading-relaxed">
              <li v-for="(rule, i) in rules" :key="i">{{ rule }}</li>
            </ul>
          </div>
          
          <div class="mt-8 flex justify-end">
            <button class="bg-primary text-primary-foreground px-8 py-3 rounded-xl font-bold hover:opacity-90 transition-opacity shadow-custom">
              确认并接受
            </button>
          </div>
        </div>
      </div>
    </div>

    <ContentSidebar 
      title="指南"
      :items="sections"
      :active-item="activeSection"
      @item-click="activeSection = $event"
    />
  </div>
</template>
