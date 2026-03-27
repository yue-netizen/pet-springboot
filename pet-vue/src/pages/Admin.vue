<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getConfigList, updateConfig } from '@/api/config'
import type { SysConfig } from '@/api/config'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('about_us')
const configs = ref<SysConfig[]>([])
const loading = ref(false)
const saving = ref(false)
const editContent = ref('')

const tabs = [
  { key: 'about_us', label: '关于我们' },
  { key: 'adoption_rules', label: '领养规则' }
]

async function loadConfigs() {
  loading.value = true
  try {
    const res = await getConfigList()
    configs.value = res.data
    const currentConfig = configs.value.find(c => c.configKey === activeTab.value)
    if (currentConfig) {
      editContent.value = currentConfig.configValue
    }
  } catch (e) {
    console.error('加载配置失败', e)
  } finally {
    loading.value = false
  }
}

function switchTab(key: string) {
  activeTab.value = key
  const currentConfig = configs.value.find(c => c.configKey === key)
  if (currentConfig) {
    editContent.value = currentConfig.configValue
  }
}

async function saveConfig() {
  saving.value = true
  try {
    await updateConfig(activeTab.value, editContent.value)
    const config = configs.value.find(c => c.configKey === activeTab.value)
    if (config) {
      config.configValue = editContent.value
    }
    alert('保存成功！')
  } catch (e) {
    console.error('保存失败', e)
    alert('保存失败，请重试')
  } finally {
    saving.value = false
  }
}

function goBack() {
  router.push('/')
}

onMounted(() => {
  if (!userStore.isAdmin()) {
    router.push('/')
    return
  }
  loadConfigs()
})
</script>

<template>
  <div class="min-h-screen bg-background">
    <div class="max-w-4xl mx-auto px-4 py-8">
      <div class="bg-card rounded-3xl shadow-custom border border-border p-8">
        <div class="flex items-center justify-between mb-8">
          <div>
            <h1 class="text-3xl font-extrabold text-foreground">管理员后台</h1>
            <p class="text-muted-foreground mt-2">管理网站内容和配置</p>
          </div>
          <button 
            @click="goBack"
            class="px-6 py-2 rounded-xl border border-border text-muted-foreground hover:bg-muted transition-colors"
          >
            返回首页
          </button>
        </div>

        <div class="flex gap-4 mb-8">
          <button
            v-for="tab in tabs"
            :key="tab.key"
            @click="switchTab(tab.key)"
            :class="[
              'px-6 py-3 rounded-xl font-medium transition-all',
              activeTab === tab.key
                ? 'bg-primary text-primary-foreground shadow-custom'
                : 'bg-muted text-muted-foreground hover:bg-muted/80'
            ]"
          >
            {{ tab.label }}
          </button>
        </div>

        <div v-if="loading" class="py-12 text-center text-muted-foreground">
          加载中...
        </div>

        <div v-else class="space-y-6">
          <div>
            <label class="block text-foreground font-medium mb-2">
              {{ tabs.find(t => t.key === activeTab)?.label }}内容
            </label>
            <textarea
              v-model="editContent"
              rows="12"
              class="w-full p-4 rounded-xl border border-border bg-background text-foreground resize-none focus:outline-none focus:ring-2 focus:ring-primary/50"
              placeholder="请输入内容..."
            ></textarea>
          </div>

          <div class="flex justify-end gap-4">
            <button
              @click="loadConfigs"
              class="px-6 py-3 rounded-xl border border-border text-muted-foreground hover:bg-muted transition-colors"
            >
              重置
            </button>
            <button
              @click="saveConfig"
              :disabled="saving"
              class="px-8 py-3 rounded-xl bg-primary text-primary-foreground font-bold hover:opacity-90 transition-opacity shadow-custom disabled:opacity-50"
            >
              {{ saving ? '保存中...' : '保存修改' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
