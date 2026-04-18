<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getAllUsers, updateUserByAdmin, deleteUser, updateUserStatus, getConfigList, updateConfig, uploadImage, type SysConfig, type UserDTO, getPetList, getPetById, addPet, updatePet, deletePet, type Pet, getAdoptionList, getAdoptionDetail, reviewAdoption, updateAdoption, type AdoptionDetail, getPostList, getPostById, deletePost, type Post, getJobList, addJob as adminAddJob, updateJob as adminUpdateJob, deleteJob as adminDeleteJob, type Job, getApplicationList, type JobApplication, getEmployeeList, approveApplication as adminApproveApplication, rejectApplication as adminRejectApplication, updateEmployee as adminUpdateEmployee, updateEmployeeStatus as adminUpdateEmployeeStatus, deleteEmployee as adminDeleteEmployee, type Employee, getStoryList, getStoryById, addStory, updateStory, deleteStory, type Story } from '@/api/admin'
import { getTipsList, getTipsById, createTips, updateTips, deleteTips, type PetTips } from '@/api/tips'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('users')
const users = ref<UserDTO[]>([])
const configs = ref<SysConfig[]>([])
const tipsList = ref<PetTips[]>([])
const petsList = ref<Pet[]>([])
const loading = ref(false)
const saving = ref(false)
const uploadingImage = ref(false)

const tabs = [
  { key: 'users', label: '用户管理' },
  { key: 'pets', label: '宠物管理' },
  { key: 'adoptions', label: '领养申请' },
  { key: 'posts', label: '帖子管理' },
  { key: 'jobs', label: '岗位管理' },
  { key: 'applications', label: '求职人员' },
  { key: 'employees', label: '管理员工' },
  { key: 'stories', label: '故事管理' },
  { key: 'about', label: '关于我们' },
  { key: 'rules', label: '领养规则' },
  { key: 'tips', label: '养宠贴士' }
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
const showTipsModal = ref(false)
const editingTips = ref<PetTips | null>(null)
const tipsForm = ref<PetTips>({
  title: '',
  category: '',
  content: ''
})
const showPetModal = ref(false)
const editingPet = ref<Pet | null>(null)
const petForm = ref<Pet>({
  name: '',
  breed: '',
  age: '',
  type: '',
  gender: '',
  image: '',
  description: '',
  healthStatus: '',
  status: 1,
  story: '',
  size: '',
  personality: ''
})

const petTypes = ['狗', '猫', '鸟', '其他']
const petGenders = ['公', '母']
const petSizes = ['小型', '中型', '大型']
const healthStatuses = ['健康', '需特殊照顾', '正在康复']

const categories = ['饮食', '行为', '健康', '环境', '训练', '其他']

const adoptionsList = ref<AdoptionDetail[]>([])
const adoptionStatusFilter = ref<number | undefined>()
const showAdoptionDetailModal = ref(false)
const viewingAdoption = ref<AdoptionDetail | null>(null)

const postsList = ref<Post[]>([])
const showPostDetailModal = ref(false)
const viewingPost = ref<Post | null>(null)

const jobsList = ref<Job[]>([])
const showJobModal = ref(false)
const editingJob = ref<Job | null>(null)
const jobForm = ref<Job>({
  title: '',
  type: '',
  location: '',
  description: '',
  requirement: '',
  status: 1
})

const jobTypes = ['全职', '兼职', '兼职/周末', '远程', '实习']

const applicationsList = ref<JobApplication[]>([])
const employeesList = ref<Employee[]>([])
const showEmployeeModal = ref(false)
const editingEmployee = ref<Employee | null>(null)
const employeeForm = ref<Employee>({
  name: '',
  phone: '',
  email: '',
  status: 1
})

const showApplicationDetailModal = ref(false)
const viewingApplication = ref<JobApplication | null>(null)

const storiesList = ref<Story[]>([])
const showStoryModal = ref(false)
const editingStory = ref<Story | null>(null)
const storyForm = ref<Story>({
  title: '',
  content: '',
  image: '',
  status: 1,
  petId: undefined,
  userId: undefined
})

const adoptedPets = computed(() => petsList.value.filter(p => p.status === 2))

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
    await loadTips()
    await loadPets()
    await loadAdoptions()
    await loadPosts()
    await loadJobs()
    await loadApplications()
    await loadEmployees()
    await loadStories()
  } catch (e) {
    console.error('加载数据失败', e)
  } finally {
    loading.value = false
  }
}

async function loadTips() {
  try {
    const res = await getTipsList(1, 100)
    tipsList.value = res.data?.records || []
  } catch (e) {
    console.error('加载贴士失败', e)
    tipsList.value = []
  }
}

async function loadPets() {
  try {
    const res = await getPetList(1, 100)
    petsList.value = res.data?.records || []
  } catch (e) {
    console.error('加载宠物失败', e)
    petsList.value = []
  }
}

async function loadAdoptions() {
  try {
    const res = await getAdoptionList(1, 100, adoptionStatusFilter.value)
    adoptionsList.value = res.data?.records || []
  } catch (e) {
    console.error('加载领养申请失败', e)
    adoptionsList.value = []
  }
}

async function loadPosts() {
  try {
    const res = await getPostList(1, 100)
    postsList.value = res.data?.records || []
  } catch (e) {
    console.error('加载帖子失败', e)
    postsList.value = []
  }
}

async function loadJobs() {
  try {
    const res = await getJobList(1, 100)
    jobsList.value = res.data?.records || []
  } catch (e) {
    console.error('加载岗位失败', e)
    jobsList.value = []
  }
}

async function loadApplications() {
  try {
    const res = await getApplicationList(1, 100)
    applicationsList.value = res.data?.records || []
  } catch (e) {
    console.error('加载求职人员失败', e)
    applicationsList.value = []
  }
}

async function loadEmployees() {
  try {
    const res = await getEmployeeList(1, 100)
    employeesList.value = res.data?.records || []
  } catch (e) {
    console.error('加载员工失败', e)
    employeesList.value = []
  }
}

async function loadStories() {
  try {
    const res = await getStoryList(1, 100)
    storiesList.value = res.data?.records || []
  } catch (e) {
    console.error('加载故事失败', e)
    storiesList.value = []
  }
}

async function handleApproveApplication(applicationId: number) {
  if (!confirm('确定要同意该求职申请吗？')) return
  try {
    await adminApproveApplication(applicationId)
    await loadApplications()
    alert('已同意申请！')
  } catch (e: any) {
    console.error('同意申请失败', e)
    alert(e.message || '操作失败，请重试')
  }
}

async function handleRejectApplication(applicationId: number) {
  if (!confirm('确定要拒绝该求职申请吗？')) return
  try {
    await adminRejectApplication(applicationId)
    await loadApplications()
    alert('已拒绝申请！')
  } catch (e: any) {
    console.error('拒绝申请失败', e)
    alert(e.message || '操作失败，请重试')
  }
}

function viewApplicationDetail(app: JobApplication) {
  viewingApplication.value = app
  showApplicationDetailModal.value = true
}

function closeApplicationDetail() {
  showApplicationDetailModal.value = false
  viewingApplication.value = null
}

async function handleApproveFromDetail() {
  const applicationId = viewingApplication.value?.id
  if (!applicationId) return
  closeApplicationDetail()
  await handleApproveApplication(applicationId)
}

async function handleRejectFromDetail() {
  const applicationId = viewingApplication.value?.id
  if (!applicationId) return
  closeApplicationDetail()
  await handleRejectApplication(applicationId)
}

function openEditEmployee(employee: Employee) {
  employeeForm.value = { ...employee }
  editingEmployee.value = employee
  showEmployeeModal.value = true
}

async function saveEmployee() {
  if (!employeeForm.value.name.trim()) {
    alert('请填写姓名！')
    return
  }

  saving.value = true
  try {
    await adminUpdateEmployee(employeeForm.value)
    showEmployeeModal.value = false
    await loadEmployees()
    alert('保存成功！')
  } catch (e) {
    console.error('保存失败', e)
    alert('保存失败，请重试')
  } finally {
    saving.value = false
  }
}

async function toggleEmployeeStatus(employee: Employee) {
  const newStatus = employee.status === 1 ? 0 : 1
  const statusText = newStatus === 1 ? '在职' : '离职'
  if (!confirm(`确定要将该员工状态改为"${statusText}"吗？`)) return
  
  try {
    await adminUpdateEmployeeStatus(employee.id!, newStatus)
    employee.status = newStatus
  } catch (e) {
    console.error('更新状态失败', e)
    alert('更新状态失败，请重试')
  }
}

async function deleteEmployeeConfirm(employee: Employee) {
  if (!confirm(`确定要删除员工 "${employee.name}" 吗？`)) return
  try {
    await adminDeleteEmployee(employee.id!)
    await loadEmployees()
    alert('删除成功！')
  } catch (e) {
    console.error('删除失败', e)
    alert('删除失败，请重试')
  }
}

function getPetName(petId?: number) {
  if (!petId) return ''
  const pet = petsList.value.find(p => p.id === petId)
  return pet ? `${pet.name} (${pet.breed})` : ''
}

function openAddStory() {
  editingStory.value = null
  storyForm.value = { title: '', content: '', image: '', status: 1, petId: undefined, userId: undefined }
  showStoryModal.value = true
}

function openEditStory(story: Story) {
  editingStory.value = story
  storyForm.value = { ...story }
  showStoryModal.value = true
}

async function handleStoryImageUpload(e: Event) {
  const target = e.target as HTMLInputElement
  if (!target.files || target.files.length === 0) return

  const file = target.files[0]
  uploadingImage.value = true

  try {
    const res = await uploadImage(file)
    storyForm.value.image = res.data
    alert('图片上传成功！')
  } catch (e) {
    console.error('图片上传失败', e)
    alert('图片上传失败，请重试')
  } finally {
    uploadingImage.value = false
    target.value = ''
  }
}

async function handleSaveStory() {
  if (!storyForm.value.title.trim() || !storyForm.value.content.trim()) {
    alert('请填写标题和内容')
    return
  }
  if (!storyForm.value.userId) {
    alert('请选择领养用户')
    return
  }
  if (!storyForm.value.petId) {
    alert('请选择关联宠物')
    return
  }
  if (!storyForm.value.image.trim()) {
    alert('请上传故事图片')
    return
  }
  saving.value = true
  try {
    if (editingStory.value?.id) {
      await updateStory(storyForm.value)
    } else {
      await addStory(storyForm.value)
    }
    showStoryModal.value = false
    await loadStories()
    alert(editingStory.value ? '更新成功！' : '添加成功！')
  } catch (e) {
    console.error('保存故事失败', e)
    alert('保存失败，请重试')
  } finally {
    saving.value = false
  }
}

async function deleteStoryConfirm(story: Story) {
  if (!confirm(`确定要删除故事 "${story.title}" 吗？`)) return
  try {
    await deleteStory(story.id!)
    await loadStories()
    alert('删除成功！')
  } catch (e) {
    console.error('删除失败', e)
    alert('删除失败，请重试')
  }
}

async function handleDeletePost(id: number) {
  if (!confirm('确定要删除这个帖子吗？')) return
  
  try {
    await deletePost(id)
    await loadPosts()
    alert('删除成功！')
  } catch (e) {
    console.error('删除帖子失败', e)
    alert('删除失败，请重试')
  }
}

function viewPostDetail(post: Post) {
  viewingPost.value = post
  showPostDetailModal.value = true
}

function formatDate(dateStr: string) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
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

function openCreateTips() {
  tipsForm.value = {
    title: '',
    category: '',
    content: ''
  }
  editingTips.value = null
  showTipsModal.value = true
}

function openEditTips(tips: PetTips) {
  tipsForm.value = { ...tips }
  editingTips.value = tips
  showTipsModal.value = true
}

async function saveTips() {
  if (!tipsForm.value.title.trim() || !tipsForm.value.category.trim() || !tipsForm.value.content.trim()) {
    alert('请填写完整信息！')
    return
  }
  
  saving.value = true
  try {
    if (editingTips.value) {
      await updateTips(editingTips.value.id!, tipsForm.value)
    } else {
      await createTips(tipsForm.value)
    }
    showTipsModal.value = false
    await loadTips()
    alert('保存成功！')
  } catch (e) {
    console.error('保存失败', e)
    alert('保存失败，请重试')
  } finally {
    saving.value = false
  }
}

async function deleteTipsConfirm(tips: PetTips) {
  if (!confirm(`确定要删除贴士 "${tips.title}" 吗？`)) return
  try {
    await deleteTips(tips.id!)
    await loadTips()
    alert('删除成功！')
  } catch (e) {
    console.error('删除失败', e)
    alert('删除失败，请重试')
  }
}

function openCreatePet() {
  petForm.value = {
    name: '',
    breed: '',
    age: '',
    type: '',
    gender: '',
    image: '',
    description: '',
    healthStatus: '',
    status: 1,
    story: '',
    size: '',
    personality: ''
  }
  editingPet.value = null
  showPetModal.value = true
}

function openEditPet(pet: Pet) {
  petForm.value = { ...pet }
  editingPet.value = pet
  showPetModal.value = true
}

async function savePet() {
  if (!petForm.value.name.trim() || !petForm.value.type.trim() || !petForm.value.image.trim()) {
    alert('请填写必填信息！')
    return
  }
  
  saving.value = true
  try {
    if (editingPet.value) {
      await updatePet(petForm.value)
    } else {
      await addPet(petForm.value)
    }
    showPetModal.value = false
    await loadPets()
    alert('保存成功！')
  } catch (e) {
    console.error('保存失败', e)
    alert('保存失败，请重试')
  } finally {
    saving.value = false
  }
}

async function deletePetConfirm(pet: Pet) {
  if (!confirm(`确定要删除宠物 "${pet.name}" 吗？`)) return
  try {
    await deletePet(pet.id!)
    await loadPets()
    alert('删除成功！')
  } catch (e) {
    console.error('删除失败', e)
    alert('删除失败，请重试')
  }
}

function openCreateJob() {
  jobForm.value = {
    title: '',
    type: '',
    location: '',
    description: '',
    requirement: '',
    status: 1
  }
  editingJob.value = null
  showJobModal.value = true
}

function openEditJob(job: Job) {
  jobForm.value = { ...job }
  editingJob.value = job
  showJobModal.value = true
}

async function saveJob() {
  if (!jobForm.value.title.trim() || !jobForm.value.type.trim()) {
    alert('请填写岗位名称和类型！')
    return
  }

  saving.value = true
  try {
    if (editingJob.value) {
      await adminUpdateJob(jobForm.value)
    } else {
      await adminAddJob(jobForm.value)
    }
    showJobModal.value = false
    await loadJobs()
    alert('保存成功！')
  } catch (e) {
    console.error('保存失败', e)
    alert('保存失败，请重试')
  } finally {
    saving.value = false
  }
}

async function deleteJobConfirm(job: Job) {
  if (!confirm(`确定要删除岗位 "${job.title}" 吗？`)) return
  try {
    await adminDeleteJob(job.id!)
    await loadJobs()
    alert('删除成功！')
  } catch (e) {
    console.error('删除失败', e)
    alert('删除失败，请重试')
  }
}

async function toggleJobStatus(job: Job) {
  const newStatus = job.status === 1 ? 0 : 1
  try {
    await adminUpdateJob({ ...job, status: newStatus })
    job.status = newStatus
  } catch (e) {
    console.error('更新状态失败', e)
    alert('更新状态失败，请重试')
  }
}

async function handlePetImageUpload(e: Event) {
  const target = e.target as HTMLInputElement
  if (!target.files || target.files.length === 0) return
  
  const file = target.files[0]
  uploadingImage.value = true
  
  try {
    const res = await uploadImage(file)
    petForm.value.image = res.data
    alert('图片上传成功！')
  } catch (e) {
    console.error('图片上传失败', e)
    alert('图片上传失败，请重试')
  } finally {
    uploadingImage.value = false
    target.value = ''
  }
}

function openAdoptionDetail(adoption: AdoptionDetail) {
  viewingAdoption.value = { ...adoption }
  showAdoptionDetailModal.value = true
}

async function reviewAdoptionConfirm(id: number, status: number) {
  const statusText = status === 1 ? '通过' : '拒绝'
  if (!confirm(`确定要${statusText}该领养申请吗？`)) return
  try {
    await reviewAdoption(id, status)
    await loadAdoptions()
    alert(`审核${statusText}成功！`)
    if (viewingAdoption.value?.id === id) {
      viewingAdoption.value.status = status
    }
  } catch (e) {
    console.error('审核失败', e)
    alert('审核失败，请重试')
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
  <div class="min-h-screen bg-background">
    <div class="w-64 bg-card border-r border-border fixed left-0 top-0 bottom-0 flex flex-col z-10">
      <div class="p-6 border-b border-border">
        <h1 class="text-2xl font-extrabold text-foreground">管理员后台</h1>
        <p class="text-muted-foreground mt-2 text-sm">管理用户和网站内容</p>
      </div>

      <nav class="p-4 space-y-2 flex-1 overflow-y-auto">
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

    <div class="ml-64 p-8 min-h-screen">
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
                  <img :src="getConfigValue('about_intro_image')" alt="预览" class="w-full h-auto object-contain rounded-xl" />
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
                  <img :src="getConfigValue('about_contact_image')" alt="预览" class="w-full h-auto object-contain rounded-xl" />
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

          <div v-if="activeTab === 'tips'" class="space-y-6">
            <div class="flex justify-between items-center">
              <h2 class="text-xl font-bold text-foreground">养宠贴士管理</h2>
              <button
                @click="openCreateTips"
                class="px-4 py-2 rounded-lg bg-primary text-primary-foreground font-medium hover:opacity-90 transition-opacity"
              >
                + 新增贴士
              </button>
            </div>

            <div class="overflow-x-auto">
              <table class="w-full">
                <thead>
                  <tr class="border-b border-border">
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">ID</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">标题</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">分类</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">创建时间</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="tips in tipsList" :key="tips.id" class="border-b border-border/50 hover:bg-muted/30">
                    <td class="py-3 px-4">{{ tips.id }}</td>
                    <td class="py-3 px-4">{{ tips.title }}</td>
                    <td class="py-3 px-4">
                      <span class="bg-secondary/20 text-secondary-foreground px-2 py-1 rounded-full text-xs">
                        {{ tips.category }}
                      </span>
                    </td>
                    <td class="py-3 px-4 text-muted-foreground text-sm">
                      {{ tips.createTime ? new Date(tips.createTime).toLocaleString('zh-CN') : '-' }}
                    </td>
                    <td class="py-3 px-4">
                      <div class="flex gap-2">
                        <button
                          @click="openEditTips(tips)"
                          class="px-3 py-1 text-sm rounded-lg bg-muted hover:bg-muted/80 text-muted-foreground transition-colors"
                        >
                          编辑
                        </button>
                        <button
                          @click="deleteTipsConfirm(tips)"
                          class="px-3 py-1 text-sm rounded-lg bg-red-100 hover:bg-red-200 text-red-700 transition-colors"
                        >
                          删除
                        </button>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>

              <div v-if="tipsList.length === 0 && !loading" class="text-center py-12 text-muted-foreground">
                暂无养宠贴士
              </div>
            </div>
          </div>

          <div v-if="activeTab === 'pets'" class="space-y-6">
            <div class="flex justify-between items-center">
              <h2 class="text-xl font-bold text-foreground">宠物管理</h2>
              <button
                @click="openCreatePet"
                class="px-4 py-2 rounded-lg bg-primary text-primary-foreground font-medium hover:opacity-90 transition-opacity"
              >
                + 新增宠物
              </button>
            </div>

            <div class="overflow-x-auto">
              <table class="w-full">
                <thead>
                  <tr class="border-b border-border">
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">ID</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">图片</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">名字</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">品种</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">类型</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">健康状态</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">状态</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="pet in petsList" :key="pet.id" class="border-b border-border/50 hover:bg-muted/30">
                    <td class="py-3 px-4">{{ pet.id }}</td>
                    <td class="py-3 px-4">
                      <img v-if="pet.image" :src="pet.image" :alt="pet.name" class="w-12 h-12 rounded-lg object-cover" />
                    </td>
                    <td class="py-3 px-4">{{ pet.name }}</td>
                    <td class="py-3 px-4">{{ pet.breed || '-' }}</td>
                    <td class="py-3 px-4">
                      <span class="bg-secondary/20 text-secondary-foreground px-2 py-1 rounded-full text-xs">
                        {{ pet.type }}
                      </span>
                    </td>
                    <td class="py-3 px-4">{{ pet.healthStatus || '-' }}</td>
                    <td class="py-3 px-4">
                      <span :class="[
                        'px-2 py-1 rounded-full text-xs font-medium',
                        pet.status === 1 ? 'bg-green-100 text-green-700' : 'bg-yellow-100 text-yellow-700'
                      ]">
                        {{ pet.status === 1 ? '可领养' : '已领养' }}
                      </span>
                    </td>
                    <td class="py-3 px-4">
                      <div class="flex gap-2">
                        <button
                          @click="openEditPet(pet)"
                          class="px-3 py-1 text-sm rounded-lg bg-muted hover:bg-muted/80 text-muted-foreground transition-colors"
                        >
                          编辑
                        </button>
                        <button
                          @click="deletePetConfirm(pet)"
                          class="px-3 py-1 text-sm rounded-lg bg-red-100 hover:bg-red-200 text-red-700 transition-colors"
                        >
                          删除
                        </button>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>

              <div v-if="petsList.length === 0 && !loading" class="text-center py-12 text-muted-foreground">
                暂无宠物
              </div>
            </div>
          </div>

          <div v-if="activeTab === 'adoptions'" class="space-y-6">
            <div class="flex justify-between items-center">
              <h2 class="text-xl font-bold text-foreground">领养申请管理</h2>
              <div class="flex gap-2">
                <select
                  v-model="adoptionStatusFilter"
                  @change="loadAdoptions"
                  class="px-4 py-2 rounded-lg border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
                >
                  <option :value="undefined">全部状态</option>
                  <option :value="0">待审核</option>
                  <option :value="1">已通过</option>
                  <option :value="2">已拒绝</option>
                </select>
              </div>
            </div>

            <div class="overflow-x-auto">
              <table class="w-full">
                <thead>
                  <tr class="border-b border-border">
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">ID</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">申请人</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">宠物</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">联系方式</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">状态</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">申请时间</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="adoption in adoptionsList" :key="adoption.id" class="border-b border-border/50 hover:bg-muted/30">
                    <td class="py-3 px-4">{{ adoption.id }}</td>
                    <td class="py-3 px-4">{{ adoption.username || '-' }}</td>
                    <td class="py-3 px-4">{{ adoption.petName || '-' }}</td>
                    <td class="py-3 px-4">{{ adoption.phone || '-' }}</td>
                    <td class="py-3 px-4">
                      <span :class="[
                        'px-2 py-1 rounded-full text-xs font-medium',
                        adoption.status === 0 ? 'bg-yellow-100 text-yellow-700' :
                        adoption.status === 1 ? 'bg-green-100 text-green-700' :
                        'bg-red-100 text-red-700'
                      ]">
                        {{ adoption.status === 0 ? '待审核' : adoption.status === 1 ? '已通过' : '已拒绝' }}
                      </span>
                    </td>
                    <td class="py-3 px-4 text-muted-foreground text-sm">
                      {{ adoption.createTime ? new Date(adoption.createTime).toLocaleString('zh-CN') : '-' }}
                    </td>
                    <td class="py-3 px-4">
                      <div class="flex gap-2">
                        <button
                          @click="openAdoptionDetail(adoption)"
                          class="px-3 py-1 text-sm rounded-lg bg-muted hover:bg-muted/80 text-muted-foreground transition-colors"
                        >
                          查看详情
                        </button>
                        <button
                          v-if="adoption.status === 0"
                          @click="reviewAdoptionConfirm(adoption.id, 1)"
                          class="px-3 py-1 text-sm rounded-lg bg-green-100 hover:bg-green-200 text-green-700 transition-colors"
                        >
                          通过
                        </button>
                        <button
                          v-if="adoption.status === 0"
                          @click="reviewAdoptionConfirm(adoption.id, 2)"
                          class="px-3 py-1 text-sm rounded-lg bg-red-100 hover:bg-red-200 text-red-700 transition-colors"
                        >
                          拒绝
                        </button>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>

              <div v-if="adoptionsList.length === 0 && !loading" class="text-center py-12 text-muted-foreground">
                暂无领养申请
              </div>
            </div>
          </div>

          <div v-if="activeTab === 'posts'" class="space-y-6">
            <div class="flex justify-between items-center">
              <h2 class="text-xl font-bold text-foreground">帖子管理</h2>
            </div>

            <div class="overflow-x-auto">
              <table class="w-full">
                <thead>
                  <tr class="border-b border-border">
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">ID</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">标题</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">内容</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">点赞/评论</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">发布时间</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="post in postsList" :key="post.id" class="border-b border-border/50 hover:bg-muted/30">
                    <td class="py-3 px-4">{{ post.id }}</td>
                    <td class="py-3 px-4 font-medium">{{ post.title || '无标题' }}</td>
                    <td class="py-3 px-4 max-w-xs truncate">{{ post.content }}</td>
                    <td class="py-3 px-4">
                      <span class="text-muted-foreground">{{ post.likeCount }} / {{ post.commentCount }}</span>
                    </td>
                    <td class="py-3 px-4 text-muted-foreground text-sm">
                      {{ formatDate(post.createTime) }}
                    </td>
                    <td class="py-3 px-4">
                      <div class="flex gap-2">
                        <button
                          @click="viewPostDetail(post)"
                          class="px-3 py-1 text-sm rounded-lg bg-muted hover:bg-muted/80 text-muted-foreground transition-colors"
                        >
                          查看
                        </button>
                        <button
                          @click="handleDeletePost(post.id)"
                          class="px-3 py-1 text-sm rounded-lg bg-red-100 hover:bg-red-200 text-red-700 transition-colors"
                        >
                          删除
                        </button>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>

              <div v-if="postsList.length === 0 && !loading" class="text-center py-12 text-muted-foreground">
                暂无帖子
              </div>
            </div>
          </div>

          <div v-if="activeTab === 'jobs'" class="space-y-6">
            <div class="flex justify-between items-center">
              <h2 class="text-xl font-bold text-foreground">岗位管理</h2>
              <button
                @click="openCreateJob"
                class="bg-primary text-primary-foreground px-4 py-2 rounded-lg font-medium hover:opacity-90 transition-opacity"
              >
                + 新增岗位
              </button>
            </div>

            <div class="overflow-x-auto">
              <table class="w-full">
                <thead>
                  <tr class="border-b border-border">
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">ID</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">岗位名称</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">类型</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">地点</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">状态</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">创建时间</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="job in jobsList" :key="job.id" class="border-b border-border/50 hover:bg-muted/30">
                    <td class="py-3 px-4">{{ job.id }}</td>
                    <td class="py-3 px-4 font-medium">{{ job.title }}</td>
                    <td class="py-3 px-4">
                      <span class="bg-primary/10 text-primary px-2 py-1 rounded-full text-xs font-medium">{{ job.type }}</span>
                    </td>
                    <td class="py-3 px-4 text-muted-foreground">{{ job.location || '-' }}</td>
                    <td class="py-3 px-4">
                      <span :class="[
                        'px-2 py-1 rounded-full text-xs font-medium',
                        job.status === 1 ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
                      ]">
                        {{ job.status === 1 ? '招聘中' : '已关闭' }}
                      </span>
                    </td>
                    <td class="py-3 px-4 text-muted-foreground text-sm">
                      {{ formatDate(job.createTime) }}
                    </td>
                    <td class="py-3 px-4">
                      <div class="flex gap-2">
                        <button
                          @click="openEditJob(job)"
                          class="px-3 py-1 text-sm rounded-lg bg-muted hover:bg-muted/80 text-muted-foreground transition-colors"
                        >
                          编辑
                        </button>
                        <button
                          @click="toggleJobStatus(job)"
                          :class="[
                            'px-3 py-1 text-sm rounded-lg transition-colors',
                            job.status === 1 ? 'bg-yellow-100 hover:bg-yellow-200 text-yellow-700' : 'bg-green-100 hover:bg-green-200 text-green-700'
                          ]"
                        >
                          {{ job.status === 1 ? '关闭' : '开启' }}
                        </button>
                        <button
                          @click="deleteJobConfirm(job)"
                          class="px-3 py-1 text-sm rounded-lg bg-red-100 hover:bg-red-200 text-red-700 transition-colors"
                        >
                          删除
                        </button>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>

              <div v-if="jobsList.length === 0 && !loading" class="text-center py-12 text-muted-foreground">
                暂无岗位
              </div>
            </div>
          </div>

          <div v-if="activeTab === 'applications'" class="space-y-6">
            <h2 class="text-xl font-bold text-foreground">求职人员管理</h2>

            <div class="overflow-x-auto">
              <table class="w-full">
                <thead>
                  <tr class="border-b border-border">
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">ID</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">姓名</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">手机号</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">邮箱</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">申请职位</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">状态</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="app in applicationsList" :key="app.id" class="border-b border-border/50 hover:bg-muted/30">
                    <td class="py-3 px-4">{{ app.id }}</td>
                    <td class="py-3 px-4 font-medium">{{ app.name }}</td>
                    <td class="py-3 px-4">{{ app.phone }}</td>
                    <td class="py-3 px-4">{{ app.email }}</td>
                    <td class="py-3 px-4">
                      <span class="bg-primary/10 text-primary px-2 py-1 rounded-full text-xs font-medium">岗位ID: {{ app.jobId || '-' }}</span>
                    </td>
                    <td class="py-3 px-4">
                      <span :class="[
                        'px-2 py-1 rounded-full text-xs font-medium',
                        app.status === 0 ? 'bg-yellow-100 text-yellow-700' : 
                        app.status === 1 ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
                      ]">
                        {{ app.status === 0 ? '待审核' : app.status === 1 ? '已通过' : '已拒绝' }}
                      </span>
                    </td>
                    <td class="py-3 px-4">
                      <div class="flex gap-2">
                        <button
                          @click="viewApplicationDetail(app)"
                          class="px-3 py-1 text-sm rounded-lg bg-blue-100 hover:bg-blue-200 text-blue-700 transition-colors"
                        >
                          详情
                        </button>
                        <button
                          v-if="app.status === 0"
                          @click="handleApproveApplication(app.id)"
                          class="px-3 py-1 text-sm rounded-lg bg-green-100 hover:bg-green-200 text-green-700 transition-colors"
                        >
                          同意
                        </button>
                        <button
                          v-if="app.status === 0"
                          @click="handleRejectApplication(app.id)"
                          class="px-3 py-1 text-sm rounded-lg bg-red-100 hover:bg-red-200 text-red-700 transition-colors"
                        >
                          拒绝
                        </button>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>

              <div v-if="applicationsList.length === 0 && !loading" class="text-center py-12 text-muted-foreground">
                暂无求职申请
              </div>
            </div>
          </div>

          <div v-if="showApplicationDetailModal && viewingApplication" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4 overflow-y-auto">
            <div class="bg-card rounded-2xl max-w-3xl w-full max-h-[90vh] overflow-y-auto shadow-2xl">
              <div class="p-6 border-b border-border flex items-center justify-between sticky top-0 bg-card z-10">
                <h3 class="text-xl font-bold text-foreground">求职申请详情</h3>
                <button @click="closeApplicationDetail" class="text-muted-foreground hover:text-foreground transition-colors">
                  ✕
                </button>
              </div>

              <div class="p-6 space-y-6">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div class="space-y-2">
                    <label class="text-sm text-muted-foreground">申请ID</label>
                    <div class="font-medium text-foreground">{{ viewingApplication.id }}</div>
                  </div>

                  <div class="space-y-2">
                    <label class="text-sm text-muted-foreground">姓名</label>
                    <div class="font-medium text-foreground">{{ viewingApplication.name }}</div>
                  </div>

                  <div class="space-y-2">
                    <label class="text-sm text-muted-foreground">手机号</label>
                    <div class="font-medium text-foreground">{{ viewingApplication.phone }}</div>
                  </div>

                  <div class="space-y-2">
                    <label class="text-sm text-muted-foreground">邮箱</label>
                    <div class="font-medium text-foreground">{{ viewingApplication.email }}</div>
                  </div>

                  <div class="space-y-2">
                    <label class="text-sm text-muted-foreground">年龄</label>
                    <div class="font-medium text-foreground">{{ viewingApplication.age || '-' }} 岁</div>
                  </div>

                  <div class="space-y-2">
                    <label class="text-sm text-muted-foreground">可工作时间</label>
                    <div class="font-medium text-foreground">{{ viewingApplication.availability || '-' }}</div>
                  </div>
                </div>

                <div class="space-y-2">
                  <label class="text-sm text-muted-foreground">居住地址</label>
                  <div class="font-medium text-foreground bg-muted/30 p-3 rounded-lg">{{ viewingApplication.address || '未填写' }}</div>
                </div>

                <div class="space-y-2">
                  <label class="text-sm text-muted-foreground">工作经验/简历</label>
                  <div class="font-medium text-foreground bg-muted/30 p-4 rounded-lg whitespace-pre-wrap">{{ viewingApplication.resume || '未填写' }}</div>
                </div>

                <div class="space-y-2">
                  <label class="text-sm text-muted-foreground">自我介绍/申请理由</label>
                  <div class="font-medium text-foreground bg-muted/30 p-4 rounded-lg whitespace-pre-wrap">{{ viewingApplication.introduction || '未填写' }}</div>
                </div>

                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div class="space-y-2">
                    <label class="text-sm text-muted-foreground">申请状态</label>
                    <span :class="[
                      'inline-block px-3 py-1 rounded-full text-sm font-medium',
                      viewingApplication.status === 0 ? 'bg-yellow-100 text-yellow-700' : 
                      viewingApplication.status === 1 ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
                    ]">
                      {{ viewingApplication.status === 0 ? '待审核' : viewingApplication.status === 1 ? '已通过' : '已拒绝' }}
                    </span>
                  </div>

                  <div class="space-y-2">
                    <label class="text-sm text-muted-foreground">申请时间</label>
                    <div class="font-medium text-foreground">{{ formatDate(viewingApplication.createTime) }}</div>
                  </div>
                </div>

                <div v-if="viewingApplication.reviewNote" class="space-y-2">
                  <label class="text-sm text-muted-foreground">审核备注</label>
                  <div class="font-medium text-foreground bg-muted/30 p-3 rounded-lg">{{ viewingApplication.reviewNote }}</div>
                </div>
              </div>

              <div class="p-6 border-t border-border flex justify-end gap-3">
                <button
                  v-if="viewingApplication.status === 0"
                  @click="handleApproveFromDetail()"
                  class="px-6 py-2 rounded-lg bg-green-500 text-white font-medium hover:bg-green-600 transition-colors"
                >
                  同意申请
                </button>
                <button
                  v-if="viewingApplication.status === 0"
                  @click="handleRejectFromDetail()"
                  class="px-6 py-2 rounded-lg bg-red-500 text-white font-medium hover:bg-red-600 transition-colors"
                >
                  拒绝申请
                </button>
                <button
                  @click="closeApplicationDetail"
                  class="px-6 py-2 rounded-lg border border-border text-muted-foreground hover:bg-muted transition-colors"
                >
                  关闭
                </button>
              </div>
            </div>
          </div>

          <div v-if="activeTab === 'employees'" class="space-y-6">
            <div class="flex justify-between items-center">
              <h2 class="text-xl font-bold text-foreground">管理员工</h2>
            </div>

            <div class="overflow-x-auto">
              <table class="w-full">
                <thead>
                  <tr class="border-b border-border">
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">ID</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">姓名</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">手机号</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">邮箱</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">职位</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">部门</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">入职日期</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">状态</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="emp in employeesList" :key="emp.id" class="border-b border-border/50 hover:bg-muted/30">
                    <td class="py-3 px-4">{{ emp.id }}</td>
                    <td class="py-3 px-4 font-medium">{{ emp.name }}</td>
                    <td class="py-3 px-4">{{ emp.phone }}</td>
                    <td class="py-3 px-4">{{ emp.email }}</td>
                    <td class="py-3 px-4">
                      <span class="bg-primary/10 text-primary px-2 py-1 rounded-full text-xs font-medium">{{ emp.position || '-' }}</span>
                    </td>
                    <td class="py-3 px-4 text-muted-foreground">{{ emp.department || '-' }}</td>
                    <td class="py-3 px-4 text-muted-foreground text-sm">
                      {{ formatDate(emp.hireDate) }}
                    </td>
                    <td class="py-3 px-4">
                      <span :class="[
                        'px-2 py-1 rounded-full text-xs font-medium',
                        emp.status === 1 ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-700'
                      ]">
                        {{ emp.status === 1 ? '在职' : '离职' }}
                      </span>
                    </td>
                    <td class="py-3 px-4">
                      <div class="flex gap-2">
                        <button
                          @click="openEditEmployee(emp)"
                          class="px-3 py-1 text-sm rounded-lg bg-muted hover:bg-muted/80 text-muted-foreground transition-colors"
                        >
                          编辑
                        </button>
                        <button
                          @click="toggleEmployeeStatus(emp)"
                          :class="[
                            'px-3 py-1 text-sm rounded-lg transition-colors',
                            emp.status === 1 ? 'bg-yellow-100 hover:bg-yellow-200 text-yellow-700' : 'bg-green-100 hover:bg-green-200 text-green-700'
                          ]"
                        >
                          {{ emp.status === 1 ? '离职' : '复职' }}
                        </button>
                        <button
                          @click="deleteEmployeeConfirm(emp)"
                          class="px-3 py-1 text-sm rounded-lg bg-red-100 hover:bg-red-200 text-red-700 transition-colors"
                        >
                          删除
                        </button>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>

              <div v-if="employeesList.length === 0 && !loading" class="text-center py-12 text-muted-foreground">
                暂无员工
              </div>
            </div>
          </div>

          <div v-if="activeTab === 'stories'" class="space-y-6">
            <div class="flex justify-between items-center">
              <h2 class="text-xl font-bold text-foreground">故事管理</h2>
              <button
                @click="openAddStory"
                class="flex items-center gap-2 px-4 py-2 bg-primary text-primary-foreground rounded-lg font-medium hover:opacity-90 transition-opacity"
              >
                + 新增故事
              </button>
            </div>

            <div class="overflow-x-auto">
              <table class="w-full">
                <thead>
                  <tr class="border-b border-border">
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">ID</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">图片</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">标题</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">关联宠物</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">内容</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">点赞数</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">浏览量</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">状态</th>
                    <th class="text-left py-3 px-4 text-muted-foreground font-medium">操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="story in storiesList" :key="story.id" class="border-b border-border/50 hover:bg-muted/30">
                    <td class="py-3 px-4">{{ story.id }}</td>
                    <td class="py-3 px-4">
                      <img v-if="story.image" :src="story.image" :alt="story.title" class="w-12 h-12 rounded-lg object-cover" />
                      <span v-else class="text-muted-foreground">-</span>
                    </td>
                    <td class="py-3 px-4 font-medium max-w-[200px] truncate">{{ story.title }}</td>
                    <td class="py-3 px-4">
                      {{ getPetName(story.petId) || '-' }}
                    </td>
                    <td class="py-3 px-4 text-muted-foreground max-w-[300px] truncate">{{ story.content }}</td>
                    <td class="py-3 px-4">{{ story.likeCount || 0 }}</td>
                    <td class="py-3 px-4">{{ story.viewCount || 0 }}</td>
                    <td class="py-3 px-4">
                      <span :class="[
                        'px-2 py-1 rounded-full text-xs font-medium',
                        story.status === 1 ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
                      ]">{{ story.status === 1 ? '正常' : '禁用' }}</span>
                    </td>
                    <td class="py-3 px-4">
                      <div class="flex gap-2">
                        <button
                          @click="openEditStory(story)"
                          class="px-3 py-1 text-sm rounded-lg bg-muted hover:bg-muted/80 text-muted-foreground transition-colors"
                        >
                          编辑
                        </button>
                        <button
                          @click="deleteStoryConfirm(story)"
                          class="px-3 py-1 text-sm rounded-lg bg-red-100 hover:bg-red-200 text-red-700 transition-colors"
                        >
                          删除
                        </button>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>

              <div v-if="storiesList.length === 0 && !loading" class="text-center py-12 text-muted-foreground">
                暂无故事
              </div>
            </div>
          </div>


        </div>
      </div>

    <div v-if="showPostDetailModal && viewingPost" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div class="bg-card rounded-2xl p-6 w-full max-w-3xl shadow-2xl max-h-[90vh] overflow-y-auto">
        <div class="flex justify-between items-center mb-6">
          <h3 class="text-xl font-bold text-foreground">帖子详情</h3>
          <button @click="showPostDetailModal = false; viewingPost = null" class="text-muted-foreground hover:text-foreground">
            ✕
          </button>
        </div>

        <div class="space-y-6">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="space-y-1">
              <label class="text-sm text-muted-foreground">帖子ID</label>
              <div class="font-medium">{{ viewingPost.id }}</div>
            </div>
            <div class="space-y-1">
              <label class="text-sm text-muted-foreground">发布时间</label>
              <div class="font-medium">{{ formatDate(viewingPost.createTime) }}</div>
            </div>
          </div>

          <div class="space-y-2">
            <label class="text-sm text-muted-foreground">标题</label>
            <div class="font-medium text-lg">{{ viewingPost.title || '无标题' }}</div>
          </div>

          <div class="space-y-2">
            <label class="text-sm text-muted-foreground">内容</label>
            <div class="bg-muted p-4 rounded-xl whitespace-pre-wrap">{{ viewingPost.content }}</div>
          </div>

          <div v-if="viewingPost.image || viewingPost.images" class="space-y-2">
            <label class="text-sm text-muted-foreground">图片</label>
            <div class="grid grid-cols-3 gap-2">
              <img v-if="viewingPost.image" :src="viewingPost.image" class="w-full h-32 object-cover rounded-lg" />
              <template v-if="viewingPost.images">
                <img v-for="(img, idx) in viewingPost.images.split(',')" :key="idx" :src="img" class="w-full h-32 object-cover rounded-lg" />
              </template>
            </div>
          </div>

          <div v-if="viewingPost.tags" class="space-y-2">
            <label class="text-sm text-muted-foreground">标签</label>
            <div class="flex flex-wrap gap-2">
              <span v-for="(tag, idx) in viewingPost.tags.split(',')" :key="idx" class="px-3 py-1 bg-primary/10 text-primary rounded-full text-sm">
                #{{ tag }}
              </span>
            </div>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div class="space-y-1">
              <label class="text-sm text-muted-foreground">点赞数</label>
              <div class="font-medium">{{ viewingPost.likeCount }}</div>
            </div>
            <div class="space-y-1">
              <label class="text-sm text-muted-foreground">评论数</label>
              <div class="font-medium">{{ viewingPost.commentCount }}</div>
            </div>
          </div>

          <div class="flex justify-end gap-3 pt-4 border-t border-border">
            <button
              @click="showPostDetailModal = false; viewingPost = null"
              class="px-4 py-2 rounded-xl border border-border hover:bg-muted transition-colors"
            >
              关闭
            </button>
            <button
              @click="handleDeletePost(viewingPost.id); showPostDetailModal = false; viewingPost = null"
              class="px-4 py-2 rounded-xl bg-red-500 text-white hover:bg-red-600 transition-colors"
            >
              删除帖子
            </button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showTipsModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div class="bg-card rounded-2xl p-6 w-full max-w-2xl shadow-2xl max-h-[90vh] overflow-y-auto">
        <h3 class="text-xl font-bold text-foreground mb-4">
          {{ editingTips ? '编辑贴士' : '新增贴士' }}
        </h3>
        <div class="space-y-4">
          <div>
            <label class="block text-foreground font-medium mb-2">标题</label>
            <input
              type="text"
              v-model="tipsForm.title"
              class="w-full p-3 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
              placeholder="请输入贴士标题..."
            />
          </div>
          <div>
            <label class="block text-foreground font-medium mb-2">分类</label>
            <select
              v-model="tipsForm.category"
              class="w-full p-3 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
            >
              <option value="">请选择分类</option>
              <option v-for="cat in categories" :key="cat" :value="cat">{{ cat }}</option>
            </select>
          </div>
          <div>
            <label class="block text-foreground font-medium mb-2">内容</label>
            <textarea
              v-model="tipsForm.content"
              rows="15"
              class="w-full p-4 rounded-xl border border-border bg-background text-foreground resize-none focus:outline-none focus:ring-2 focus:ring-primary/50"
              placeholder="请输入贴士内容..."
            ></textarea>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <button
            @click="showTipsModal = false"
            class="px-4 py-2 rounded-lg border border-border text-muted-foreground hover:bg-muted transition-colors"
          >
            取消
          </button>
          <button
            @click="saveTips"
            :disabled="saving"
            class="px-6 py-2 rounded-lg bg-primary text-primary-foreground hover:opacity-90 transition-opacity disabled:opacity-50"
          >
            {{ saving ? '保存中...' : '保存' }}
          </button>
        </div>
      </div>
    </div>

    <div v-if="showStoryModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div class="bg-card rounded-2xl p-6 w-full max-w-2xl shadow-2xl max-h-[90vh] overflow-y-auto">
        <h3 class="text-xl font-bold text-foreground mb-4">
          {{ editingStory?.id ? '编辑故事' : '新增故事' }}
        </h3>
        <div class="space-y-4">
          <div>
            <label class="block text-foreground font-medium mb-2">标题 *</label>
            <input
              type="text"
              v-model="storyForm.title"
              class="w-full p-3 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
              placeholder="请输入故事标题"
            />
          </div>
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-foreground font-medium mb-2">领养用户 *</label>
              <select
                v-model="storyForm.userId"
                class="w-full p-3 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
              >
                <option :value="undefined" disabled>请选择领养用户</option>
                <option v-for="user in users" :key="user.id" :value="user.id">{{ user.nickname || user.username }}</option>
              </select>
            </div>
            <div>
              <label class="block text-foreground font-medium mb-2">关联宠物 *</label>
              <select
                v-model="storyForm.petId"
                class="w-full p-3 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
              >
                <option :value="undefined" disabled>请选择已领养的宠物</option>
                <option v-for="pet in adoptedPets" :key="pet.id" :value="pet.id">{{ pet.name }} ({{ pet.breed }})</option>
              </select>
            </div>
          </div>
          <div>
            <label class="block text-foreground font-medium mb-2">内容 *</label>
            <textarea
              v-model="storyForm.content"
              rows="5"
              class="w-full p-3 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50 resize-none"
              placeholder="请输入故事内容"
            ></textarea>
          </div>
          <div>
            <label class="block text-foreground font-medium mb-2">图片 *</label>
            <div class="flex flex-col md:flex-row gap-4 items-start">
              <div class="w-full md:w-40 h-40 rounded-xl border border-dashed border-border bg-background flex items-center justify-center overflow-hidden">
                <img v-if="storyForm.image" :src="storyForm.image" alt="预览" class="w-full h-full object-cover" />
                <div v-else class="text-center text-muted-foreground">
                  <div class="text-3xl mb-1">📷</div>
                  <div class="text-sm">暂无图片</div>
                </div>
              </div>
              <div class="flex-1">
                <label class="inline-block px-6 py-3 rounded-xl bg-primary text-primary-foreground font-medium cursor-pointer hover:opacity-90 transition-opacity">
                  点击上传图片
                  <input
                    type="file"
                    accept="image/*"
                    @change="handleStoryImageUpload"
                    class="hidden"
                    :disabled="uploadingImage"
                  />
                </label>
                <div v-if="uploadingImage" class="mt-2 text-primary">
                  上传中...
                </div>
              </div>
            </div>
          </div>
          <div>
            <label class="block text-foreground font-medium mb-2">状态</label>
            <select
              v-model="storyForm.status"
              class="w-full p-3 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
            >
              <option :value="1">正常</option>
              <option :value="0">禁用</option>
            </select>
          </div>
        </div>

        <div class="flex justify-end gap-3 mt-6">
          <button
            @click="showStoryModal = false"
            class="px-4 py-2 rounded-lg border border-border text-muted-foreground hover:bg-muted transition-colors"
          >
            取消
          </button>
          <button
            @click="handleSaveStory"
            :disabled="saving"
            class="px-6 py-2 rounded-lg bg-primary text-primary-foreground hover:opacity-90 transition-opacity disabled:opacity-50"
          >
            {{ saving ? '保存中...' : '保存' }}
          </button>
        </div>
      </div>
    </div>

    <div v-if="showJobModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div class="bg-card rounded-2xl p-6 w-full max-w-2xl shadow-2xl max-h-[90vh] overflow-y-auto">
        <h3 class="text-xl font-bold text-foreground mb-4">
          {{ editingJob ? '编辑岗位' : '新增岗位' }}
        </h3>
        <div class="space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-foreground font-medium mb-2">岗位名称 *</label>
              <input
                type="text"
                v-model="jobForm.title"
                class="w-full p-3 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
                placeholder="如：兽医、领养协调员"
              />
            </div>
            <div>
              <label class="block text-foreground font-medium mb-2">类型 *</label>
              <select
                v-model="jobForm.type"
                class="w-full p-3 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
              >
                <option value="">请选择类型</option>
                <option v-for="t in jobTypes" :key="t" :value="t">{{ t }}</option>
              </select>
            </div>
          </div>

          <div>
            <label class="block text-foreground font-medium mb-2">工作地点</label>
            <input
              type="text"
              v-model="jobForm.location"
              class="w-full p-3 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
              placeholder="如：城市收容所总部、远程办公"
            />
          </div>

          <div>
            <label class="block text-foreground font-medium mb-2">岗位描述</label>
            <textarea
              v-model="jobForm.description"
              rows="3"
              class="w-full p-3 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50 resize-none"
              placeholder="请描述该岗位的主要职责..."
            ></textarea>
          </div>

          <div>
            <label class="block text-foreground font-medium mb-2">任职要求</label>
            <textarea
              v-model="jobForm.requirement"
              rows="3"
              class="w-full p-3 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50 resize-none"
              placeholder="请描述该岗位的任职要求..."
            ></textarea>
          </div>

          <div>
            <label class="block text-foreground font-medium mb-2">状态</label>
            <select
              v-model="jobForm.status"
              class="w-full p-3 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
            >
              <option :value="1">招聘中</option>
              <option :value="0">已关闭</option>
            </select>
          </div>
        </div>

        <div class="flex justify-end gap-3 mt-6">
          <button
            @click="showJobModal = false"
            class="px-4 py-2 rounded-lg border border-border text-muted-foreground hover:bg-muted transition-colors"
          >
            取消
          </button>
          <button
            @click="saveJob"
            :disabled="saving"
            class="px-6 py-2 rounded-lg bg-primary text-primary-foreground hover:opacity-90 transition-opacity disabled:opacity-50"
          >
            {{ saving ? '保存中...' : '保存' }}
          </button>
        </div>
      </div>
    </div>

    <div v-if="showEmployeeModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div class="bg-card rounded-2xl p-6 w-full max-w-2xl shadow-2xl max-h-[90vh] overflow-y-auto">
        <h3 class="text-xl font-bold text-foreground mb-4">
          编辑员工
        </h3>
        <div class="space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-foreground font-medium mb-2">姓名 *</label>
              <input
                type="text"
                v-model="employeeForm.name"
                class="w-full p-3 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
                placeholder="请输入姓名"
              />
            </div>
            <div>
              <label class="block text-foreground font-medium mb-2">手机号 *</label>
              <input
                type="text"
                v-model="employeeForm.phone"
                class="w-full p-3 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
                placeholder="请输入手机号"
              />
            </div>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-foreground font-medium mb-2">邮箱 *</label>
              <input
                type="email"
                v-model="employeeForm.email"
                class="w-full p-3 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
                placeholder="请输入邮箱"
              />
            </div>
            <div>
              <label class="block text-foreground font-medium mb-2">职位</label>
              <input
                type="text"
                v-model="employeeForm.position"
                class="w-full p-3 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
                placeholder="如：兽医、领养协调员"
              />
            </div>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-foreground font-medium mb-2">部门</label>
              <input
                type="text"
                v-model="employeeForm.department"
                class="w-full p-3 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
                placeholder="如：医疗部、运营部"
              />
            </div>
            <div>
              <label class="block text-foreground font-medium mb-2">状态</label>
              <select
                v-model="employeeForm.status"
                class="w-full p-3 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
              >
                <option :value="1">在职</option>
                <option :value="0">离职</option>
              </select>
            </div>
          </div>
        </div>

        <div class="flex justify-end gap-3 mt-6">
          <button
            @click="showEmployeeModal = false"
            class="px-4 py-2 rounded-lg border border-border text-muted-foreground hover:bg-muted transition-colors"
          >
            取消
          </button>
          <button
            @click="saveEmployee"
            :disabled="saving"
            class="px-6 py-2 rounded-lg bg-primary text-primary-foreground hover:opacity-90 transition-opacity disabled:opacity-50"
          >
            {{ saving ? '保存中...' : '保存' }}
          </button>
        </div>
      </div>
    </div>

    <div v-if="showPetModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div class="bg-card rounded-2xl p-6 w-full max-w-3xl shadow-2xl max-h-[90vh] overflow-y-auto">
        <h3 class="text-xl font-bold text-foreground mb-4">
          {{ editingPet ? '编辑宠物' : '新增宠物' }}
        </h3>
        <div class="space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-foreground font-medium mb-2">名字 *</label>
              <input
                type="text"
                v-model="petForm.name"
                class="w-full p-3 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
                placeholder="请输入宠物名字..."
              />
            </div>
            <div>
              <label class="block text-foreground font-medium mb-2">品种</label>
              <input
                type="text"
                v-model="petForm.breed"
                class="w-full p-3 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
                placeholder="请输入品种..."
              />
            </div>
          </div>
          
          <div class="grid grid-cols-3 gap-4">
            <div>
              <label class="block text-foreground font-medium mb-2">类型 *</label>
              <select
                v-model="petForm.type"
                class="w-full p-3 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
              >
                <option value="">请选择类型</option>
                <option v-for="t in petTypes" :key="t" :value="t">{{ t }}</option>
              </select>
            </div>
            <div>
              <label class="block text-foreground font-medium mb-2">性别</label>
              <select
                v-model="petForm.gender"
                class="w-full p-3 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
              >
                <option value="">请选择性别</option>
                <option v-for="g in petGenders" :key="g" :value="g">{{ g }}</option>
              </select>
            </div>
            <div>
              <label class="block text-foreground font-medium mb-2">年龄</label>
              <input
                type="text"
                v-model="petForm.age"
                class="w-full p-3 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
                placeholder="如: 2岁"
              />
            </div>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-foreground font-medium mb-2">体型</label>
              <select
                v-model="petForm.size"
                class="w-full p-3 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
              >
                <option value="">请选择体型</option>
                <option v-for="s in petSizes" :key="s" :value="s">{{ s }}</option>
              </select>
            </div>
            <div>
              <label class="block text-foreground font-medium mb-2">健康状态</label>
              <select
                v-model="petForm.healthStatus"
                class="w-full p-3 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
              >
                <option value="">请选择健康状态</option>
                <option v-for="h in healthStatuses" :key="h" :value="h">{{ h }}</option>
              </select>
            </div>
          </div>

          <div>
            <label class="block text-foreground font-medium mb-2">性格</label>
            <input
              type="text"
              v-model="petForm.personality"
              class="w-full p-3 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
              placeholder="请输入性格特点..."
            />
          </div>

          <div>
            <label class="block text-foreground font-medium mb-2">图片 *</label>
            <div class="flex flex-col md:flex-row gap-4 items-start">
              <div class="w-full md:w-40 h-40 rounded-xl border border-dashed border-border bg-background flex items-center justify-center overflow-hidden">
                <img v-if="petForm.image" :src="petForm.image" alt="预览" class="w-full h-full object-cover" />
                <div v-else class="text-center text-muted-foreground">
                  <div class="text-3xl mb-1">📷</div>
                  <div class="text-sm">暂无图片</div>
                </div>
              </div>
              <div class="flex-1">
                <label class="inline-block px-6 py-3 rounded-xl bg-primary text-primary-foreground font-medium cursor-pointer hover:opacity-90 transition-opacity">
                  点击上传图片
                  <input
                    type="file"
                    accept="image/*"
                    @change="handlePetImageUpload"
                    class="hidden"
                    :disabled="uploadingImage"
                  />
                </label>
                <div v-if="uploadingImage" class="mt-2 text-primary">
                  上传中...
                </div>
              </div>
            </div>
          </div>

          <div>
            <label class="block text-foreground font-medium mb-2">描述</label>
            <textarea
              v-model="petForm.description"
              rows="4"
              class="w-full p-4 rounded-xl border border-border bg-background text-foreground resize-none focus:outline-none focus:ring-2 focus:ring-primary/50"
              placeholder="请输入宠物描述..."
            ></textarea>
          </div>

          <div>
            <label class="block text-foreground font-medium mb-2">故事</label>
            <textarea
              v-model="petForm.story"
              rows="6"
              class="w-full p-4 rounded-xl border border-border bg-background text-foreground resize-none focus:outline-none focus:ring-2 focus:ring-primary/50"
              placeholder="请输入宠物故事..."
            ></textarea>
          </div>

          <div>
            <label class="block text-foreground font-medium mb-2">状态</label>
            <select
              v-model="petForm.status"
              class="w-full p-3 rounded-xl border border-border bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary/50"
            >
              <option :value="1">可领养</option>
              <option :value="0">已领养</option>
            </select>
          </div>
        </div>
        <div class="flex justify-end gap-3 mt-6">
          <button
            @click="showPetModal = false"
            class="px-4 py-2 rounded-lg border border-border text-muted-foreground hover:bg-muted transition-colors"
          >
            取消
          </button>
          <button
            @click="savePet"
            :disabled="saving"
            class="px-6 py-2 rounded-lg bg-primary text-primary-foreground hover:opacity-90 transition-opacity disabled:opacity-50"
          >
            {{ saving ? '保存中...' : '保存' }}
          </button>
        </div>
      </div>
    </div>

    <div v-if="showAdoptionDetailModal && viewingAdoption" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div class="bg-card rounded-2xl p-6 w-full max-w-3xl shadow-2xl max-h-[90vh] overflow-y-auto">
        <div class="flex justify-between items-center mb-6">
          <h3 class="text-xl font-bold text-foreground">领养申请详情</h3>
          <button @click="showAdoptionDetailModal = false; viewingAdoption = null" class="text-muted-foreground hover:text-foreground">
            ✕
          </button>
        </div>

        <div class="space-y-6">
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="space-y-1">
              <label class="text-sm text-muted-foreground">申请ID</label>
              <div class="font-medium">{{ viewingAdoption.id }}</div>
            </div>
            <div class="space-y-1">
              <label class="text-sm text-muted-foreground">申请状态</label>
              <span :class="[
                'px-2 py-1 rounded-full text-xs font-medium',
                viewingAdoption.status === 0 ? 'bg-yellow-100 text-yellow-700' :
                viewingAdoption.status === 1 ? 'bg-green-100 text-green-700' :
                'bg-red-100 text-red-700'
              ]">
                {{ viewingAdoption.status === 0 ? '待审核' : viewingAdoption.status === 1 ? '已通过' : '已拒绝' }}
              </span>
            </div>
            <div class="space-y-1">
              <label class="text-sm text-muted-foreground">申请时间</label>
              <div class="font-medium">{{ viewingAdoption.createTime ? new Date(viewingAdoption.createTime).toLocaleString('zh-CN') : '-' }}</div>
            </div>
          </div>

          <div class="border-t border-border pt-4">
            <h4 class="font-semibold text-foreground mb-3">申请人信息</h4>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="space-y-1">
                <label class="text-sm text-muted-foreground">用户名</label>
                <div class="font-medium">{{ viewingAdoption.username || '-' }}</div>
              </div>
              <div class="space-y-1">
                <label class="text-sm text-muted-foreground">邮箱</label>
                <div class="font-medium">{{ viewingAdoption.userEmail || '-' }}</div>
              </div>
              <div class="space-y-1">
                <label class="text-sm text-muted-foreground">手机号</label>
                <div class="font-medium">{{ viewingAdoption.userPhone || '-' }}</div>
              </div>
              <div class="space-y-1">
                <label class="text-sm text-muted-foreground">申请人年龄</label>
                <div class="font-medium">{{ viewingAdoption.applicantAge || '-' }}岁</div>
              </div>
              <div class="space-y-1">
                <label class="text-sm text-muted-foreground">住房类型</label>
                <div class="font-medium">{{ viewingAdoption.housingType || '-' }}</div>
              </div>
              <div class="space-y-1">
                <label class="text-sm text-muted-foreground">养宠经验</label>
                <div class="font-medium">{{ viewingAdoption.hasPetExperience || '-' }}</div>
              </div>
              <div class="space-y-1">
                <label class="text-sm text-muted-foreground">家庭情况</label>
                <div class="font-medium">{{ viewingAdoption.familyStatus || '-' }}</div>
              </div>
            </div>
          </div>

          <div class="border-t border-border pt-4">
            <h4 class="font-semibold text-foreground mb-3">申请信息</h4>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div class="space-y-1">
                <label class="text-sm text-muted-foreground">手机号</label>
                <div class="font-medium">{{ viewingAdoption.phone || '-' }}</div>
              </div>
              <div class="space-y-1">
                <label class="text-sm text-muted-foreground">邮箱</label>
                <div class="font-medium">{{ viewingAdoption.email || '-' }}</div>
              </div>
              <div class="space-y-1 md:col-span-2">
                <label class="text-sm text-muted-foreground">地址</label>
                <div class="font-medium">{{ viewingAdoption.address || '-' }}</div>
              </div>
              <div class="space-y-1 md:col-span-2">
                <label class="text-sm text-muted-foreground">领养理由</label>
                <div class="font-medium">{{ viewingAdoption.reason || '-' }}</div>
              </div>
            </div>
            <div class="mt-4 grid grid-cols-2 gap-4">
              <div class="flex items-center gap-2">
                <span :class="viewingAdoption.agreeHealthCheck ? 'text-green-600' : 'text-red-600'">
                  {{ viewingAdoption.agreeHealthCheck ? '✓' : '✗' }}
                </span>
                <span class="text-sm text-muted-foreground">同意健康检查</span>
              </div>
              <div class="flex items-center gap-2">
                <span :class="viewingAdoption.agreeNeuter ? 'text-green-600' : 'text-red-600'">
                  {{ viewingAdoption.agreeNeuter ? '✓' : '✗' }}
                </span>
                <span class="text-sm text-muted-foreground">同意绝育</span>
              </div>
              <div class="flex items-center gap-2">
                <span :class="viewingAdoption.agreeGoodEnvironment ? 'text-green-600' : 'text-red-600'">
                  {{ viewingAdoption.agreeGoodEnvironment ? '✓' : '✗' }}
                </span>
                <span class="text-sm text-muted-foreground">提供良好环境</span>
              </div>
              <div class="flex items-center gap-2">
                <span :class="viewingAdoption.agreeTimelyMedical ? 'text-green-600' : 'text-red-600'">
                  {{ viewingAdoption.agreeTimelyMedical ? '✓' : '✗' }}
                </span>
                <span class="text-sm text-muted-foreground">及时就医</span>
              </div>
            </div>
          </div>

          <div class="border-t border-border pt-4">
            <h4 class="font-semibold text-foreground mb-3">宠物信息</h4>
            <div class="flex gap-4 items-start">
              <img v-if="viewingAdoption.petImage" :src="viewingAdoption.petImage" :alt="viewingAdoption.petName" class="w-24 h-24 rounded-xl object-cover" />
              <div class="grid grid-cols-2 gap-4 flex-1">
                <div class="space-y-1">
                  <label class="text-sm text-muted-foreground">宠物名</label>
                  <div class="font-medium">{{ viewingAdoption.petName || '-' }}</div>
                </div>
                <div class="space-y-1">
                  <label class="text-sm text-muted-foreground">品种</label>
                  <div class="font-medium">{{ viewingAdoption.petBreed || '-' }}</div>
                </div>
                <div class="space-y-1">
                  <label class="text-sm text-muted-foreground">年龄</label>
                  <div class="font-medium">{{ viewingAdoption.petAge || '-' }}</div>
                </div>
              </div>
            </div>
          </div>

          <div v-if="viewingAdoption.status === 0" class="flex justify-end gap-3 pt-4 border-t border-border">
            <button
              @click="showAdoptionDetailModal = false; viewingAdoption = null"
              class="px-4 py-2 rounded-lg border border-border text-muted-foreground hover:bg-muted transition-colors"
            >
              关闭
            </button>
            <button
              @click="reviewAdoptionConfirm(viewingAdoption.id, 2)"
              class="px-4 py-2 rounded-lg bg-red-100 text-red-700 hover:bg-red-200 transition-colors"
            >
              拒绝
            </button>
            <button
              @click="reviewAdoptionConfirm(viewingAdoption.id, 1)"
              class="px-4 py-2 rounded-lg bg-primary text-primary-foreground hover:opacity-90 transition-opacity"
            >
              通过
            </button>
          </div>

          <div v-else class="flex justify-end pt-4 border-t border-border">
            <button
              @click="showAdoptionDetailModal = false; viewingAdoption = null"
              class="px-4 py-2 rounded-lg border border-border text-muted-foreground hover:bg-muted transition-colors"
            >
              关闭
            </button>
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
