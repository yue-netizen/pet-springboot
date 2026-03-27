<script setup lang="ts">
import { ref, onMounted } from 'vue'
import ContentSidebar from '@/components/ContentSidebar.vue'
import { getConfigByKey } from '@/api/config'

const activeSection = ref('intro')
const aboutContent = ref('')
const loading = ref(true)

const sections = [
  { id: 'intro', label: '简介' },
  { id: 'status', label: '当前状况' },
  { id: 'history', label: '发展历程' },
  { id: 'news', label: '最新动态' },
  { id: 'contact', label: '联系我们' }
]

const stats = [
  { stat: "5,000+", label: "成功领养" },
  { stat: "12", label: "收容所" },
  { stat: "200+", label: "志愿者" }
]

async function loadAboutContent() {
  try {
    const res = await getConfigByKey('about_us')
    aboutContent.value = res.data?.configValue || ''
  } catch (e) {
    console.error('加载关于我们内容失败', e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadAboutContent()
})
</script>

<template>
  <div class="flex flex-col md:flex-row gap-10">
    
    <div class="flex-1 flex flex-col gap-8 bg-card p-8 md:p-12 rounded-3xl shadow-custom border border-border h-fit">
      <div>
        <span class="text-secondary font-bold uppercase tracking-wider mb-2 block">我们是谁</span>
        <h1 class="text-4xl font-extrabold text-foreground mb-6">关于爪印之家</h1>
        
        <div class="w-full h-[400px] rounded-2xl overflow-hidden mb-8 bg-muted">
          <img src="https://images.unsplash.com/photo-1528301721190-1c922d627ea4?auto=format&fit=crop&w=1200&q=80" alt="团队与宠物" class="w-full h-full object-cover" />
        </div>

        <div v-if="activeSection === 'intro'" class="animate-in fade-in slide-in-from-bottom-4 duration-500">
          <h3 class="text-2xl font-bold text-foreground mb-4">简介</h3>
          <div v-if="loading" class="py-8 text-center text-muted-foreground">
            加载中...
          </div>
          <p v-else class="text-muted-foreground leading-relaxed text-lg mb-4 whitespace-pre-line">
            {{ aboutContent }}
          </p>
        </div>

        <div v-if="activeSection === 'status'" class="animate-in fade-in slide-in-from-bottom-4 duration-500">
          <h3 class="text-2xl font-bold text-foreground mb-4">当前状况</h3>
          <div class="flex flex-wrap gap-6 mb-6">
            <div v-for="(s, i) in stats" :key="i" class="flex-1 bg-muted p-6 rounded-2xl text-center border border-border">
              <div class="text-3xl font-extrabold text-primary mb-2">{{ s.stat }}</div>
              <div class="text-foreground font-medium">{{ s.label }}</div>
            </div>
          </div>
          <p class="text-muted-foreground leading-relaxed text-lg">
            目前，我们正在满负荷运营，每天持续接收新动物，同时严格审核领养申请。
          </p>
        </div>

        <div v-if="['history', 'news', 'contact'].includes(activeSection)" class="animate-in fade-in py-12 text-center text-muted-foreground">
          <p class="text-lg">"{{ sections.find(s => s.id === activeSection)?.label }}"的详细内容正在更新中...</p>
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
