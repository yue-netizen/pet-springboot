<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getAllUsers, updateUserByAdmin, deleteUser, updateUserStatus, getConfigList, updateConfig, uploadImage, type SysConfig, type UserDTO } from '@/api/admin'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('users')
const users = ref<UserDTO[]>([])
const configs = ref<SysConfig[]>([])
const loading = ref(false)
const saving = ref(false)
const uploadingImage = ref(false)

const tabs = [
  { key: 'users', label: '用户管理' },
  { key: 'about', label: '关于我们' },
  { key: 'rules', label: '领养规则' }
]

const aboutSections = [
  { key: 'intro', label: '简介', fields: ['about_intro', 'about_intro_image'] },
  { key: 'status', label: '当前状况', fields: ['about_status', 'about_status_stat1', 'about_status_stat1_label', 'about_status_stat2', 'about_status_stat2_label', 'about_status_stat3', 'about_status_stat3_label'] },
  { key: 'history', label: '发展历程', fields: ['about_history'] },
  { key: 'news', label: '最新动态', fields: ['about_news'] },
  { key: 'contact', label: '联系我们', fields: ['about_contact', 'about_contact_image'] }
]

const rulesSections = [
  { key: 'adoption-rules', label: '领养规则', configKey: 'adoption_rules' },
  { key: 'adoption-agreement', label: '领养协议', configKey: 'adoption_agreement' },
  { key: 'foster-rules', label: '寄养规则', configKey: 'foster_rules' },
  { key: 'foster-agreement', label: '寄养协议', configKey: 'foster_agreement' }
]

const activeAboutSection = ref('intro')
const activeRulesSection = ref('adoption-rules')
const editingUser = ref<UserDTO | null>(null)
const showEditModal = ref(false)

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

function setConfigValue(key: string, value: string) {
  const config = configs.value.find(c => c.configKey === key)
  if (config) {
    config.configValue = value
  } else {
    configs.value.push({
      id: 0,
      configKey: key,
      configValue: value,
      configName: '',
      description: ''
    })
  }
}

async function loadData() {
  loading.value = true
  try {
    const [usersRes, configsRes] = await Promise.all([
      getAllUsers(),
      getConfigList()
    ])
    users.value = usersRes.data
    configs.value = configsRes.data
  } catch (e) {
    console.error('加载数据失败', e)
  } finally {
    loading.value = false
  }
}

async function saveConfig(key: string) {
  saving.value = true
  try {
    const value = getConfigValue(key)
    await updateConfig(key, value)
    alert('保存成功！')
  } catch (e) {
    console.error('保存失败', e)
    alert('保存失败，请重试')
  } finally {
    saving.value = false
  }
}

async function saveAllInSection(sectionKey: string) {
  const section = aboutSections.find(s => s.key === sectionKey)
  if (!section) return
  
  saving.value = true
  try {
    for (const key of section.fields) {
      const value = getConfigValue(key)
      await updateConfig(key, value)
    }
    alert('保存成功！')
  } catch (e) {
    console.error('保存失败', e)
    alert('保存失败，请重试')
  } finally {
    saving.value = false
  }
}

async function saveRules() {
  saving.value = true
  try {
    const value = getConfigValue('adoption_rules')
    await updateConfig('adoption_rules', value)
    alert('保存成功！')
  } catch (e) {
    console.error('保存失败', e)
    alert('保存失败，请重试')
  } finally {
    saving.value = false
  }
}

async function saveSingleRule(configKey: string) {
  saving.value = true
  try {
    const value = getConfigValue(configKey)
    await updateConfig(configKey, value)
    alert('保存成功！')
  } catch (e) {
    console.error('保存失败', e)
    alert('保存失败，请重试')
  } finally {
    saving.value = false
  }
}

function openEditUser(user: UserDTO) {
  editingUser.value = { ...user }
  showEditModal.value = true
}

async function saveEditedUser() {
  if (!editingUser.value) return
  saving.value = true
  try {
    await updateUserByAdmin(editingUser.value)
    showEditModal.value = false
    editingUser.value = null
    await loadData()
    alert('保存成功！')
  } catch (e) {
    console.error('保存失败', e)
    alert('保存失败，请重试')
  } finally {
    saving.value = false
  }
}

async function toggleUserStatus(user: UserDTO) {
  const newStatus = user.status === 1 ? 0 : 1
  try {
    await updateUserStatus(user.id, newStatus)
    user.status = newStatus
  } catch (e) {
    console.error('更新状态失败', e)
    alert('更新状态失败，请重试')
  }
}

async function deleteUserConfirm(user: UserDTO) {
  if (!confirm(`确定要删除用户 "${user.username}" 吗？`)) return
  try {
    await deleteUser(user.id)
    await loadData()
    alert('删除成功！')
  } catch (e) {
    console.error('删除失败', e)
    alert('删除失败，请重试')
  }
}

async function handleImageUpload(e: Event, configKey: string) {
  const target = e.target as HTMLInputElement
  if (!target.files || target.files.length === 0) return
  
  const file = target.files[0]
  uploadingImage.value = true
  
  try {
    const res = await uploadImage(file)
    setConfigValue(configKey, res.data)
    alert('图片上传成功！')
  } catch (e) {
    console.error('图片上传失败', e)
    alert('图片上传失败，请重试')
  } finally {
    uploadingImage.value = false
    target.value = ''
  }
}

function logout() {
  userStore.logout()
  router.push('/')
}

onMounted(() => {
  if (!userStore.isAdmin()) {
    router.push('/')
    return
  }
  loadData()
})
</script>

<template>
  <div class="min-h-screen bg-background flex">
    <div class="w-64 bg-card border-r border-border flex-shrink-0 flex flex-col">
      <div class="p-6 border-b border-border">
        <h1 class="text-2xl font-extrabold text-foreground">管理员后台</h1>
        <p class="text-muted-foreground mt-2 text-sm">管理用户和网站内容</p>
      </div>
      
      <nav class="p-4 space-y-2 flex-1">
        <button
          v-for="tab in tabs"
          :key="tab.key"
          @click="activeTab = tab.key"
          :class="[
            'w-full text-left px-4 py-3 rounded-xl font-medium transition-all',
            activeTab === tab.key
              ? 'bg-primary text-primary-foreground shadow-custom'
              : 'text-muted-foreground hover:bg-muted'
          ]"
        >
          {{ tab.label }}
        </button>
      </nav>
      
      <div class="p-4 border-t border-border">
        <button 
          @click="logout"
          class="w-full px-4 py-3 rounded-xl border border-border text-muted-foreground hover:bg-muted transition-colors"
        >
          退出登录
        </button>
      </div>
    </div>
    
    <div class="flex-1 p-8">
      <div v-if="loading" class="py-12 text-center text-muted-foreground">
        加载中...
      </div>

      <div v-else class="space-y-6">
          
          <div v-if="activeTab === 'users'" class="space-y-6">
            <div class="overflow-x-auto">
              <table class="w-full">
                <thead>
                  <tr class="border-b border-border">
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">ID</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">用户名</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">昵称</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">邮箱</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">角色</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">状态</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="user in users" :key="user.id" class="border-b border-border/50 hover:bg-muted/30">
                    <td class="py-3 px-4">{{ user.id }}</td>
                    <td class="py-3 px-4">{{ user.username }}</td>
                    <td class="py-3 px-4">{{ user.nickname || '-' }}</td>
                    <td class="py-3 px-4">{{ user.email || '-' }}</td>
                    <td class="py-3 px-4">
                      <span :class="[
                        'px-2 py-1 rounded-full text-xs font-medium',
                        user.role === 1 ? 'bg-primary/20 text-primary' : 'bg-muted text-muted-foreground'
                      ]">
                        {{ user.role === 1 ? '管理员' : '用户' }}
                      </span>
                    </td>
                    <td class="py-3 px-4">
                      <span :class="[
                        'px-2 py-1 rounded-full text-xs font-medium',
                        user.status === 1 ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
                      ]">
                        {{ user.status === 1 ? '正常' : '禁用' }}
                      </span>
                    </td>
                    <td class="py-3 px-4">
                      <div class="flex gap-2">
                        <button
                          @click="openEditUser(user)"
                          class="px-3 py-1 text-sm rounded-lg bg-muted hover:bg-muted/80 text-muted-foreground transition-colors"
                        >
                          编辑
                        </button>
                        <button
                          @click="toggleUserStatus(user)"
                          :class="[
                            'px-3 py-1 text-sm rounded-lg transition-colors',
                            user.status === 1 
                              ? 'bg-yellow-100 hover:bg-yellow-200 text-yellow-700' 
                              : 'bg-green-100 hover:bg-green-200 text-green-700'
                          ]"
                        >
                          {{ user.status === 1 ? '禁用' : '启用' }}
                        </button>
                        <button
                          v-if="user.role !== 1"
                          @click="deleteUserConfirm(user)"
                          class="px-3 py-1 text-sm rounded-lg bg-red-100 hover:bg-red-200 text-red-700 transition-colors"
                        >
                          删除
                        </button>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <div v-if="activeTab === 'about'" class="space-y-6">
            
            <div class="flex flex-wrap gap-2 mb-6">
              <button
                v-for="section in aboutSections"
                :key="section.key"
                @click="activeAboutSection = section.key"
                :class="[
                  'px-4 py-2 rounded-lg text-sm font-medium transition-all',
                  activeAboutSection === section.key
                    ? 'bg-primary text-primary-foreground'
                    : 'bg-muted text-muted-foreground hover:bg-muted/80'
                ]"
              >
                {{ section.label }}
              </button>
            </div>

            <div v-if="activeAboutSection === 'intro'" class="space-y-4">
              <div>
                <label class="block text-foreground font-medium mb-2">简介内容</label>
                <textarea
                  :value="getConfigValue('about_intro')"
                  @input="setConfigValue('about_intro', ($event.target as HTMLTextAreaElement).value)"
                  rows="8"
                  class="w-full p-4 rounded-xl border border-border bg-background text-foreground resize-none focus:outline-none focus:ring-2 focus:ring-primary/50"
                  placeholder="请输入简介内容..."
                ></textarea>
              </div>
              <div>
                <label class="block text-foreground font-medium mb-2">简介图片</label>
                <label class="w-full p-4 rounded-xl border border-dashed border-border bg-background text-center text-muted-foreground cursor-pointer hover:bg-muted/50 transition-colors">
                  <div class="py-4">
                    <div class="text-2xl mb-2">📷</div>
                    <div class="font-medium">点击上传图片</div>
                    <div class="text-sm mt-1">或拖拽到此处</div>
                  </div>
                  <input
                    type="file"
                    accept="image/*"
                    @change="handleImageUpload($event, 'about_intro_image')"
                    class="hidden"
                    :disabled="uploadingImage"
                  />
                </label>
                <div v-if="uploadingImage" class="mt-2 text-center text-primary">
                  上传中...
                </div>
                <div v-if="getConfigValue('about_intro_image')" class="mt-4">
                  <img :src="getConfigValue('about_intro_image')" alt="预览" class="w-full h-48 object-cover rounded-xl" />
                </div>
              </div>
              <div class="flex justify-end">
                <button
                  @click="saveAllInSection('intro')"
                  :disabled="saving"
                  class="px-8 py-3 rounded-xl bg-primary text-primary-foreground font-bold hover:opacity-90 transition-opacity shadow-custom disabled:opacity-50"
                >
                  {{ saving ? '保存中...' : '保存简介' }}
                </button>
              </div>
            </div>

            <div v-if="activeAboutSection === 'status'" class="space-y-4">
              <div>
                <label class="block text-foreground font-medium mb-2">当前状况描述</label>
                <textarea
                  :value="getConfigValue('about_status')"
                  @input="setConfigValue('about_status', ($event.target as HTMLTextAreaElement).value)"
                  rows="6"
                  class="w-full p-4 rounded-xl border border-border bg-background text-foreground resize-none focus:outline-none focus:ring-2 focus:ring-primary/50"
                  placeholder="请输入当前状况描述..."
                ></textarea>
              </div>
              <div class="grid grid-cols-3 gap-4">
                <div class="space-y-2">
                  <label class="block text-foreground font-medium text-sm">统计数字1</label>
                  <input
                    type="text"
                    :value="getConfigValue('about_status_stat1')"
                    @input="setConfigValue('about_status_stat1', ($event.target as HTMLInputElement).value)"
                    class="w-full p-3 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
                    placeholder="如: 5,000+"
                  />
                  <label class="block text-foreground font-medium text-sm">标签1</label>
                  <input
                    type="text"
                    :value="getConfigValue('about_status_stat1_label')"
                    @input="setConfigValue('about_status_stat1_label', ($event.target as HTMLInputElement).value)"
                    class="w-full p-3 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
                    placeholder="如: 成功领养"
                  />
                </div>
                <div class="space-y-2">
                  <label class="block text-foreground font-medium text-sm">统计数字2</label>
                  <input
                    type="text"
                    :value="getConfigValue('about_status_stat2')"
                    @input="setConfigValue('about_status_stat2', ($event.target as HTMLInputElement).value)"
                    class="w-full p-3 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
                    placeholder="如: 12"
                  />
                  <label class="block text-foreground font-medium text-sm">标签2</label>
                  <input
                    type="text"
                    :value="getConfigValue('about_status_stat2_label')"
                    @input="setConfigValue('about_status_stat2_label', ($event.target as HTMLInputElement).value)"
                    class="w-full p-3 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
                    placeholder="如: 收容所"
                  />
                </div>
                <div class="space-y-2">
                  <label class="block text-foreground font-medium text-sm">统计数字3</label>
                  <input
                    type="text"
                    :value="getConfigValue('about_status_stat3')"
                    @input="setConfigValue('about_status_stat3', ($event.target as HTMLInputElement).value)"
                    class="w-full p-3 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
                    placeholder="如: 200+"
                  />
                  <label class="block text-foreground font-medium text-sm">标签3</label>
                  <input
                    type="text"
                    :value="getConfigValue('about_status_stat3_label')"
                    @input="setConfigValue('about_status_stat3_label', ($event.target as HTMLInputElement).value)"
                    class="w-full p-3 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
                    placeholder="如: 志愿者"
                  />
                </div>
              </div>
              <div class="flex justify-end">
                <button
                  @click="saveAllInSection('status')"
                  :disabled="saving"
                  class="px-8 py-3 rounded-xl bg-primary text-primary-foreground font-bold hover:opacity-90 transition-opacity shadow-custom disabled:opacity-50"
                >
                  {{ saving ? '保存中...' : '保存当前状况' }}
                </button>
              </div>
            </div>

            <div v-if="activeAboutSection === 'history'" class="space-y-4">
              <div>
                <label class="block text-foreground font-medium mb-2">发展历程（每行一条记录）</label>
                <textarea
                  :value="getConfigValue('about_history')"
                  @input="setConfigValue('about_history', ($event.target as HTMLTextAreaElement).value)"
                  rows="10"
                  class="w-full p-4 rounded-xl border border-border bg-background text-foreground resize-none focus:outline-none focus:ring-2 focus:ring-primary/50"
                  placeholder="请输入发展历程，每行一条记录..."
                ></textarea>
              </div>
              <div class="flex justify-end">
                <button
                  @click="saveConfig('about_history')"
                  :disabled="saving"
                  class="px-8 py-3 rounded-xl bg-primary text-primary-foreground font-bold hover:opacity-90 transition-opacity shadow-custom disabled:opacity-50"
                >
                  {{ saving ? '保存中...' : '保存发展历程' }}
                </button>
              </div>
            </div>

            <div v-if="activeAboutSection === 'news'" class="space-y-4">
              <div>
                <label class="block text-foreground font-medium mb-2">最新动态（每条动态之间用空行分隔）</label>
                <textarea
                  :value="getConfigValue('about_news')"
                  @input="setConfigValue('about_news', ($event.target as HTMLTextAreaElement).value)"
                  rows="12"
                  class="w-full p-4 rounded-xl border border-border bg-background text-foreground resize-none focus:outline-none focus:ring-2 focus:ring-primary/50"
                  placeholder="请输入最新动态，每条动态之间用空行分隔..."
                ></textarea>
              </div>
              <div class="flex justify-end">
                <button
                  @click="saveConfig('about_news')"
                  :disabled="saving"
                  class="px-8 py-3 rounded-xl bg-primary text-primary-foreground font-bold hover:opacity-90 transition-opacity shadow-custom disabled:opacity-50"
                >
                  {{ saving ? '保存中...' : '保存最新动态' }}
                </button>
              </div>
            </div>

            <div v-if="activeAboutSection === 'contact'" class="space-y-4">
              <div>
                <label class="block text-foreground font-medium mb-2">联系我们内容（每行一条信息）</label>
                <textarea
                  :value="getConfigValue('about_contact')"
                  @input="setConfigValue('about_contact', ($event.target as HTMLTextAreaElement).value)"
                  rows="8"
                  class="w-full p-4 rounded-xl border border-border bg-background text-foreground resize-none focus:outline-none focus:ring-2 focus:ring-primary/50"
                  placeholder="请输入联系信息，每行一条..."
                ></textarea>
              </div>
              <div>
                <label class="block text-foreground font-medium mb-2">联系我们图片</label>
                <label class="w-full p-4 rounded-xl border border-dashed border-border bg-background text-center text-muted-foreground cursor-pointer hover:bg-muted/50 transition-colors">
                  <div class="py-4">
                    <div class="text-2xl mb-2">📷</div>
                    <div class="font-medium">点击上传图片</div>
                    <div class="text-sm mt-1">或拖拽到此处</div>
                  </div>
                  <input
                    type="file"
                    accept="image/*"
                    @change="handleImageUpload($event, 'about_contact_image')"
                    class="hidden"
                    :disabled="uploadingImage"
                  />
                </label>
                <div v-if="uploadingImage" class="mt-2 text-center text-primary">
                  上传中...
                </div>
                <div v-if="getConfigValue('about_contact_image')" class="mt-4">
                  <img :src="getConfigValue('about_contact_image')" alt="预览" class="w-full h-48 object-cover rounded-xl" />
                </div>
              </div>
              <div class="flex justify-end">
                <button
                  @click="saveAllInSection('contact')"
                  :disabled="saving"
                  class="px-8 py-3 rounded-xl bg-primary text-primary-foreground font-bold hover:opacity-90 transition-opacity shadow-custom disabled:opacity-50"
                >
                  {{ saving ? '保存中...' : '保存联系我们' }}
                </button>
              </div>
            </div>
          </div>

          <div v-if="activeTab === 'rules'" class="space-y-6">
            <div class="flex flex-wrap gap-2 mb-6">
              <button
                @click="activeRulesSection = 'adoption-rules'"
                :class="[
                  'px-4 py-2 rounded-lg text-sm font-medium transition-all',
                  activeRulesSection === 'adoption-rules'
                    ? 'bg-primary text-primary-foreground'
                    : 'bg-muted text-muted-foreground hover:bg-muted/80'
                ]"
              >
                领养规则
              </button>
              <button
                @click="activeRulesSection = 'adoption-agreement'"
                :class="[
                  'px-4 py-2 rounded-lg text-sm font-medium transition-all',
                  activeRulesSection === 'adoption-agreement'
                    ? 'bg-primary text-primary-foreground'
                    : 'bg-muted text-muted-foreground hover:bg-muted/80'
                ]"
              >
                领养协议
              </button>
              <button
                @click="activeRulesSection = 'foster-rules'"
                :class="[
                  'px-4 py-2 rounded-lg text-sm font-medium transition-all',
                  activeRulesSection === 'foster-rules'
                    ? 'bg-primary text-primary-foreground'
                    : 'bg-muted text-muted-foreground hover:bg-muted/80'
                ]"
              >
                寄养规则
              </button>
              <button
                @click="activeRulesSection = 'foster-agreement'"
                :class="[
                  'px-4 py-2 rounded-lg text-sm font-medium transition-all',
                  activeRulesSection === 'foster-agreement'
                    ? 'bg-primary text-primary-foreground'
                    : 'bg-muted text-muted-foreground hover:bg-muted/80'
                ]"
              >
                寄养协议
              </button>
            </div>

            <div v-if="activeRulesSection === 'adoption-rules'" class="space-y-4">
              <div>
                <label class="block text-foreground font-medium mb-2">领养规则内容</label>
                <textarea
                  :value="getConfigValue('adoption_rules')"
                  @input="setConfigValue('adoption_rules', ($event.target as HTMLTextAreaElement).value)"
                  rows="15"
                  class="w-full p-4 rounded-xl border border-border bg-background text-foreground resize-none focus:outline-none focus:ring-2 focus:ring-primary/50"
                  placeholder="请输入领养规则内容..."
                ></textarea>
              </div>
              <div class="flex justify-end">
                <button
                  @click="saveSingleRule('adoption_rules')"
                  :disabled="saving"
                  class="px-8 py-3 rounded-xl bg-primary text-primary-foreground font-bold hover:opacity-90 transition-opacity shadow-custom disabled:opacity-50"
                >
                  {{ saving ? '保存中...' : '保存领养规则' }}
                </button>
              </div>
            </div>

            <div v-if="activeRulesSection === 'adoption-agreement'" class="space-y-4">
              <div>
                <label class="block text-foreground font-medium mb-2">领养协议内容</label>
                <textarea
                  :value="getConfigValue('adoption_agreement')"
                  @input="setConfigValue('adoption_agreement', ($event.target as HTMLTextAreaElement).value)"
                  rows="15"
                  class="w-full p-4 rounded-xl border border-border bg-background text-foreground resize-none focus:outline-none focus:ring-2 focus:ring-primary/50"
                  placeholder="请输入领养协议内容..."
                ></textarea>
              </div>
              <div class="flex justify-end">
                <button
                  @click="saveSingleRule('adoption_agreement')"
                  :disabled="saving"
                  class="px-8 py-3 rounded-xl bg-primary text-primary-foreground font-bold hover:opacity-90 transition-opacity shadow-custom disabled:opacity-50"
                >
                  {{ saving ? '保存中...' : '保存领养协议' }}
                </button>
              </div>
            </div>

            <div v-if="activeRulesSection === 'foster-rules'" class="space-y-4">
              <div>
                <label class="block text-foreground font-medium mb-2">寄养规则内容</label>
                <textarea
                  :value="getConfigValue('foster_rules')"
                  @input="setConfigValue('foster_rules', ($event.target as HTMLTextAreaElement).value)"
                  rows="15"
                  class="w-full p-4 rounded-xl border border-border bg-background text-foreground resize-none focus:outline-none focus:ring-2 focus:ring-primary/50"
                  placeholder="请输入寄养规则内容..."
                ></textarea>
              </div>
              <div class="flex justify-end">
                <button
                  @click="saveSingleRule('foster_rules')"
                  :disabled="saving"
                  class="px-8 py-3 rounded-xl bg-primary text-primary-foreground font-bold hover:opacity-90 transition-opacity shadow-custom disabled:opacity-50"
                >
                  {{ saving ? '保存中...' : '保存寄养规则' }}
                </button>
              </div>
            </div>

            <div v-if="activeRulesSection === 'foster-agreement'" class="space-y-4">
              <div>
                <label class="block text-foreground font-medium mb-2">寄养协议内容</label>
                <textarea
                  :value="getConfigValue('foster_agreement')"
                  @input="setConfigValue('foster_agreement', ($event.target as HTMLTextAreaElement).value)"
                  rows="15"
                  class="w-full p-4 rounded-xl border border-border bg-background text-foreground resize-none focus:outline-none focus:ring-2 focus:ring-primary/50"
                  placeholder="请输入寄养协议内容..."
                ></textarea>
              </div>
              <div class="flex justify-end">
                <button
                  @click="saveSingleRule('foster_agreement')"
                  :disabled="saving"
                  class="px-8 py-3 rounded-xl bg-primary text-primary-foreground font-bold hover:opacity-90 transition-opacity shadow-custom disabled:opacity-50"
                >
                  {{ saving ? '保存中...' : '保存寄养协议' }}
                </button>
              </div>
            </div>
          </div>

        </div>
    </div>

    <div v-if="showEditModal && editingUser" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div class="bg-card rounded-2xl p-6 w-full max-w-md shadow-2xl">
        <h3 class="text-xl font-bold text-foreground mb-4">编辑用户</h3>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-muted-foreground mb-1">用户名</label>
            <input
              type="text"
              :value="editingUser.username"
              disabled
              class="w-full p-3 rounded-lg border border-border bg-muted text-muted-foreground"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-muted-foreground mb-1">昵称</label>
            <input
              type="text"
              v-model="editingUser.nickname"
              class="w-full p-3 rounded-lg border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-muted-foreground mb-1">邮箱</label>
            <input
              type="email"
              v-model="editingUser.email"
              class="w-full p-3 rounded-lg border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-muted-foreground mb-1">手机号</label>
            <input
              type="text"
              v-model="editingUser.phone"
              class="w-full p-3 rounded-lg border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-muted-foreground mb-1">地址</label>
            <input
              type="text"
              v-model="editingUser.address"
              class="w-full p-3 rounded-lg border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-muted-foreground mb-1">角色</label>
            <select
              v-model="editingUser.role"
              class="w-full p-3 rounded-lg border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
            >
              <option :value="0">用户</option>
              <option :value="1">管理员</option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-medium text-muted-foreground mb-1">状态</label>
            <select
              v-model="editingUser.status"
              class="w-full p-3 rounded-lg border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
            >
              <option :value="1">正常</option>
              <option :value="0">禁用</option>
            </select>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <button
            @click="showEditModal = false; editingUser = null"
            class="px-4 py-2 rounded-lg border border-border text-muted-foreground hover:bg-muted transition-colors"
          >
            取消
          </button>
          <button
            @click="saveEditedUser"
            :disabled="saving"
            class="px-6 py-2 rounded-lg bg-primary text-primary-foreground hover:opacity-90 transition-opacity disabled:opacity-50"
          >
            {{ saving ? '保存中...' : '保存' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
