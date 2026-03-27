<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import ContentSidebar from '@/components/ContentSidebar.vue'
import { getConfigList } from '@/api/config'
import type { SysConfig } from '@/api/config'

const activeSection = ref('intro')
const configs = ref<SysConfig[]>([])
const loading = ref(true)

const sections = [
  { id: 'intro', label: '简介' },
  { id: 'status', label: '当前状况' },
  { id: 'history', label: '发展历程' },
  { id: 'news', label: '最新动态' },
  { id: 'contact', label: '联系我们' }
]

const configMap = computed(() => {
  const map: Record<string, string> = {}
  configs.value.forEach(c => {
    map[c.configKey] = c.configValue
  })
  return map
})

function getConfigValue(key: string): string {
  return configMap.value[key] || ''
}

const stats = computed(() => [
  { stat: getConfigValue('about_status_stat1'), label: getConfigValue('about_status_stat1_label') },
  { stat: getConfigValue('about_status_stat2'), label: getConfigValue('about_status_stat2_label') },
  { stat: getConfigValue('about_status_stat3'), label: getConfigValue('about_status_stat3_label') }
])

const historyItems = computed(() => {
  const history = getConfigValue('about_history')
  if (!history) return []
  return history.split('\n').filter(line => line.trim())
})

const newsItems = computed(() => {
  const news = getConfigValue('about_news')
  if (!news) return []
  return news.split('\n\n').filter(item => item.trim())
})

const contactLines = computed(() => {
  const contact = getConfigValue('about_contact')
  if (!contact) return []
  return contact.split('\n').filter(line => line.trim())
})

async function loadConfigs() {
  loading.value = true
  try {
    const res = await getConfigList()
    configs.value = res.data
  } catch (e) {
    console.error('加载配置失败', e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadConfigs()
})
</script>

<template>
  <div class="flex flex-col md:flex-row gap-10">
    
    <div class="flex-1 flex flex-col gap-8 bg-card p-8 md:p-12 rounded-3xl shadow-custom border border-border h-fit">
      <div>
        <span class="text-secondary font-bold uppercase tracking-wider mb-2 block">我们是谁</span>
        <h1 class="text-4xl font-extrabold text-foreground mb-6">关于爪印之家</h1>
        
        <div v-if="activeSection === 'intro'" class="animate-in fade-in slide-in-from-bottom-4 duration-500">
          <div class="w-full h-[400px] rounded-2xl overflow-hidden mb-8 bg-muted">
            <img v-if="getConfigValue('about_intro_image')" :src="getConfigValue('about_intro_image')" alt="团队与宠物" class="w-full h-full object-cover" />
          </div>
          <h3 class="text-2xl font-bold text-foreground mb-4">简介</h3>
          <div v-if="loading" class="py-8 text-center text-muted-foreground">
            加载中...
          </div>
          <p v-else class="text-muted-foreground leading-relaxed text-lg mb-4 whitespace-pre-line">
            {{ getConfigValue('about_intro') }}
          </p>
        </div>

        <div v-if="activeSection === 'status'" class="animate-in fade-in slide-in-from-bottom-4 duration-500">
          <h3 class="text-2xl font-bold text-foreground mb-4">当前状况</h3>
          <div class="flex flex-wrap gap-6 mb-6">
            <div v-for="(s, i) in stats" :key="i" class="flex-1 bg-muted p-6 rounded-2xl text-center border border-border min-w-[120px]">
              <div class="text-3xl font-extrabold text-primary mb-2">{{ s.stat }}</div>
              <div class="text-foreground font-medium">{{ s.label }}</div>
            </div>
          </div>
          <p class="text-muted-foreground leading-relaxed text-lg whitespace-pre-line">
            {{ getConfigValue('about_status') }}
          </p>
        </div>

        <div v-if="activeSection === 'history'" class="animate-in fade-in slide-in-from-bottom-4 duration-500">
          <h3 class="text-2xl font-bold text-foreground mb-6">发展历程</h3>
          <div v-if="loading" class="py-8 text-center text-muted-foreground">
            加载中...
          </div>
          <div v-else class="relative border-l-2 border-primary/30 pl-8 space-y-6">
            <div v-for="(item, index) in historyItems" :key="index" class="relative">
              <div class="absolute -left-[2.1rem] w-4 h-4 bg-primary rounded-full border-4 border-background"></div>
              <p class="text-muted-foreground leading-relaxed">{{ item }}</p>
            </div>
          </div>
        </div>

        <div v-if="activeSection === 'news'" class="animate-in fade-in slide-in-from-bottom-4 duration-500">
          <h3 class="text-2xl font-bold text-foreground mb-6">最新动态</h3>
          <div v-if="loading" class="py-8 text-center text-muted-foreground">
            加载中...
          </div>
          <div v-else class="space-y-6">
            <div v-for="(item, index) in newsItems" :key="index" class="bg-muted p-6 rounded-2xl border border-border">
              <p class="text-muted-foreground leading-relaxed whitespace-pre-line">{{ item }}</p>
            </div>
          </div>
        </div>

        <div v-if="activeSection === 'contact'" class="animate-in fade-in slide-in-from-bottom-4 duration-500">
          <h3 class="text-2xl font-bold text-foreground mb-6">联系我们</h3>
          <div v-if="loading" class="py-8 text-center text-muted-foreground">
            加载中...
          </div>
          <div v-else class="space-y-6">
            <div class="w-full h-[250px] rounded-2xl overflow-hidden bg-muted">
              <img v-if="getConfigValue('about_contact_image')" :src="getConfigValue('about_contact_image')" alt="联系我们" class="w-full h-full object-cover" />
            </div>
            <div class="bg-muted p-6 rounded-2xl border border-border">
              <div v-for="(line, index) in contactLines" :key="index" class="text-muted-foreground py-1">
                {{ line }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <ContentSidebar 
      title="关于我们"
      :items="sections"
      :active-item="activeSection"
      @item-click="activeSection = $event"
    />
  </div>
</template>
