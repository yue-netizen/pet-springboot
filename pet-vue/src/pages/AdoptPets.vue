<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Search, RefreshCw } from 'lucide-vue-next'
import PetCard from '@/components/PetCard.vue'
import { getPetList, type Pet } from '@/api/pet'

const pets = ref<Pet[]>([])
const loading = ref(false)
const loadingMore = ref(false)
const hasMore = ref(true)
const currentPage = ref(1)
const total = ref(0)
const pageSize = 9
const searchKeyword = ref('')
const selectedType = ref('')
const minAge = ref<number | ''>('')
const maxAge = ref<number | ''>('')

const loadPets = async (reset = true) => {
  if (loading.value || loadingMore.value) return
  if (reset) {
    currentPage.value = 1
    hasMore.value = true
    pets.value = []
  }
  if (!hasMore.value && !reset) return
  
  try {
    if (reset) loading.value = true
    else loadingMore.value = true
    
    const params: any = { 
      page: currentPage.value, 
      size: pageSize, 
      status: 1 
    }
    if (searchKeyword.value) {
      params.name = searchKeyword.value
      params.breed = searchKeyword.value
    }
    if (selectedType.value && selectedType.value !== '全部类型') {
      if (selectedType.value === '狗狗') params.type = '狗'
      else if (selectedType.value === '猫咪') params.type = '猫'
      else if (selectedType.value === '鸟类') params.type = '鸟'
      else params.type = selectedType.value
    }
    if (minAge.value !== '') {
      params.minAge = minAge.value
    }
    if (maxAge.value !== '') {
      params.maxAge = maxAge.value
    }
    const res = await getPetList(params)
    const newPets = res.data?.records || []
    total.value = res.data?.total || 0
    
    if (reset) {
      pets.value = newPets
    } else {
      pets.value = [...pets.value, ...newPets]
    }
    
    hasMore.value = newPets.length >= pageSize
  } catch (error) {
    console.error('加载宠物列表失败', error)
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

const loadMore = () => {
  currentPage.value++
  loadPets(false)
}

const handleSearch = () => {
  loadPets(true)
}

const handleRefresh = () => {
  loadPets(true)
}

onMounted(() => {
  loadPets(true)
})
</script>

<template>
  <div class="flex flex-col gap-8">
    <div class="bg-card w-full p-8 md:p-12 rounded-3xl shadow-custom flex flex-col items-center text-center border border-border">
      <h1 class="text-4xl md:text-5xl font-extrabold text-foreground mb-4">寻找您的完美伙伴</h1>
      <p class="text-muted-foreground max-w-2xl text-lg mb-8">
        准备好敞开家门了吗？浏览我们可爱的宠物，它们正等待立即领养。
      </p>
      
      <div class="flex items-center w-full max-w-3xl bg-background rounded-full border border-border overflow-hidden px-4 shadow-sm focus-within:ring-2 focus-within:ring-primary transition-shadow">
        <Search class="text-muted-foreground ml-2" :size="24" />
        <input 
          v-model="searchKeyword"
          type="text" 
          placeholder="按品种、名称或年龄搜索..." 
          @keyup.enter="handleSearch"
          class="flex-1 bg-transparent py-4 text-lg border-none focus:outline-none px-4 text-foreground placeholder:text-muted-foreground"
        />
        <button @click="handleSearch" class="bg-secondary text-secondary-foreground px-8 py-2.5 rounded-full font-bold m-1 hover:opacity-90 transition-opacity">
          搜索
        </button>
      </div>
    </div>

    <div class="flex flex-col md:flex-row gap-6">
      <div class="w-full md:w-64 shrink-0 flex flex-col gap-6 bg-card p-6 rounded-2xl shadow-custom border border-border h-fit">
        <div class="flex items-center justify-between border-b border-border pb-3">
          <h3 class="font-bold text-lg text-foreground">筛选条件</h3>
          <button 
            @click="handleRefresh" 
            class="flex items-center gap-1 px-3 py-1.5 bg-primary text-primary-foreground rounded-lg hover:opacity-90 transition-all active:scale-95"
          >
            <RefreshCw :size="16" />
            <span class="font-medium">刷新</span>
          </button>
        </div>
        
        <div class="flex flex-col gap-3">
          <label class="font-semibold text-foreground">类型</label>
          <select v-model="selectedType" class="w-full bg-background border border-border p-3 rounded-xl focus:outline-primary">
            <option>全部类型</option>
            <option>狗狗</option>
            <option>猫咪</option>
            <option>鸟类</option>
            <option>其他</option>
          </select>
        </div>

        <div class="flex flex-col gap-3">
          <label class="font-semibold text-foreground">年龄范围（岁）</label>
          <div class="flex items-center gap-2">
            <input
              type="number"
              v-model="minAge"
              placeholder="最小"
              min="0"
              class="w-full bg-background border border-border p-3 rounded-xl focus:outline-primary"
            />
            <span class="text-muted-foreground">-</span>
            <input
              type="number"
              v-model="maxAge"
              placeholder="最大"
              min="0"
              class="w-full bg-background border border-border p-3 rounded-xl focus:outline-primary"
            />
          </div>
        </div>
      </div>

      <div class="flex-1">
        <div v-if="loading" class="flex justify-center py-20">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
        </div>
        <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <PetCard 
            v-for="pet in pets" 
            :key="pet.id"
            :id="pet.id" 
            :name="pet.name" 
            :breed="pet.breed" 
            :age="pet.age" 
            :image="pet.image"
            location="城市收容所"
          />
        </div>
        <div v-if="!loading && pets.length === 0" class="text-center py-20 text-muted-foreground">
          暂无宠物数据
        </div>
        <div v-if="hasMore && !loading && pets.length > 0" class="flex justify-center py-8">
          <button
            @click="loadMore"
            :disabled="loadingMore"
            class="px-8 py-3 rounded-xl border border-border text-muted-foreground hover:bg-muted transition-colors disabled:opacity-50"
          >
            {{ loadingMore ? '加载中...' : '加载更多' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
