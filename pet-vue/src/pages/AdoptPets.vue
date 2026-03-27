<script setup lang="ts">
import { Search } from 'lucide-vue-next'
import PetCard from '@/components/PetCard.vue'

const pets = [
  { id: "P1", name: "小黄", breed: "金毛寻回犬", age: "2岁", image: "https://images.unsplash.com/photo-1552053831-71594a27632d?auto=format&fit=crop&w=600&q=80"},
  { id: "P2", name: "露娜", breed: "波斯猫", age: "6个月", image: "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?auto=format&fit=crop&w=600&q=80"},
  { id: "P3", name: "查理", breed: "比格混血", age: "3岁", image: "https://images.unsplash.com/photo-1537151608828-ea2b11777ee8?auto=format&fit=crop&w=600&q=80"},
  { id: "P4", name: "米洛", breed: "缅因猫", age: "4岁", image: "https://images.unsplash.com/photo-1513245543132-31f507417b26?auto=format&fit=crop&w=600&q=80"},
  { id: "P5", name: "库珀", breed: "柯基犬", age: "1岁", image: "https://images.unsplash.com/photo-1583337130417-3346a1be7dee?auto=format&fit=crop&w=600&q=80"},
  { id: "P6", name: "黛西", breed: "虎斑猫", age: "2个月", image: "https://images.unsplash.com/photo-1513360371669-4adf3dd7dff8?auto=format&fit=crop&w=600&q=80"}
]
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
          type="text" 
          placeholder="按品种、名称或年龄搜索..." 
          class="flex-1 bg-transparent py-4 text-lg border-none focus:outline-none px-4 text-foreground placeholder:text-muted-foreground"
        />
        <button class="bg-secondary text-secondary-foreground px-8 py-2.5 rounded-full font-bold m-1 hover:opacity-90 transition-opacity">
          搜索
        </button>
      </div>
    </div>

    <div class="flex flex-col md:flex-row gap-6">
      <div class="w-full md:w-64 shrink-0 flex flex-col gap-6 bg-card p-6 rounded-2xl shadow-custom border border-border h-fit">
        <h3 class="font-bold text-lg text-foreground border-b border-border pb-3">筛选条件</h3>
        
        <div class="flex flex-col gap-3">
          <label class="font-semibold text-foreground">类型</label>
          <select class="w-full bg-background border border-border p-3 rounded-xl focus:outline-primary">
            <option>全部类型</option>
            <option>狗狗</option>
            <option>猫咪</option>
            <option>鸟类</option>
            <option>其他</option>
          </select>
        </div>

        <div class="flex flex-col gap-3">
          <label class="font-semibold text-foreground">年龄</label>
          <select class="w-full bg-background border border-border p-3 rounded-xl focus:outline-primary">
            <option>任意年龄</option>
            <option>幼崽</option>
            <option>青年</option>
            <option>成年</option>
            <option>老年</option>
          </select>
        </div>
      </div>

      <div class="flex-1 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <PetCard 
          v-for="pet in pets" 
          :key="pet.id"
          :id="pet.id" 
          :name="pet.name" 
          :breed="pet.breed" 
          :age="pet.age" 
          :image="pet.image"
        />
      </div>
    </div>
  </div>
</template>
