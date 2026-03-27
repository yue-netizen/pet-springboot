<script setup lang="ts">
import { Send, Users, MoreVertical, Phone, Search } from 'lucide-vue-next'

const chats = [
  { name: "客服团队", msg: "我们已收到您的申请！", time: "上午10:30", active: true, unread: 1 },
  { name: "莎拉健康", msg: "谢谢您关于玩具的建议。", time: "昨天", active: false },
  { name: "纽约领养中心", msg: "您方便通话吗？", time: "周一", active: false }
]
</script>

<template>
  <div class="flex flex-col md:flex-row h-[75vh] min-h-[600px] w-full bg-card rounded-3xl shadow-custom border border-border overflow-hidden">
    
    <div class="w-full md:w-80 flex flex-col border-r border-border bg-background">
      <div class="p-6 border-b border-border">
        <h2 class="text-2xl font-bold text-foreground flex items-center gap-2 mb-4">
          <Users :size="24" class="text-primary" /> 消息
        </h2>
        <div class="relative">
          <Search class="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground" :size="18" />
          <input type="text" placeholder="搜索聊天..." class="w-full bg-card border border-border rounded-xl py-2.5 pl-10 pr-4 text-sm focus:outline-none focus:ring-2 focus:ring-primary" />
        </div>
      </div>
      <div class="flex-1 overflow-y-auto">
        <div 
          v-for="(chat, i) in chats" 
          :key="i" 
          :class="[
            'flex items-start gap-3 p-4 border-b border-border cursor-pointer transition-colors',
            chat.active ? 'bg-primary/5' : 'hover:bg-muted/50'
          ]"
        >
          <div class="w-12 h-12 rounded-full bg-muted flex-shrink-0" />
          <div class="flex-1 min-w-0">
            <div class="flex justify-between items-baseline mb-1">
              <h4 class="font-bold text-foreground text-sm truncate">{{ chat.name }}</h4>
              <span class="text-xs text-muted-foreground whitespace-nowrap ml-2">{{ chat.time }}</span>
            </div>
            <div class="flex justify-between items-center">
              <p class="text-sm text-muted-foreground truncate">{{ chat.msg }}</p>
              <span v-if="chat.unread" class="w-5 h-5 flex items-center justify-center bg-primary text-primary-foreground text-xs font-bold rounded-full ml-2 shrink-0">
                {{ chat.unread }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="flex-1 flex flex-col bg-card relative">
      <div class="flex items-center justify-between p-6 border-b border-border bg-card">
        <div class="flex items-center gap-4">
          <div class="w-12 h-12 rounded-full bg-primary/20 flex-shrink-0" />
          <div>
            <h3 class="font-bold text-foreground text-lg">客服团队</h3>
            <p class="text-xs text-primary font-medium">在线</p>
          </div>
        </div>
        <div class="flex items-center gap-4 text-muted-foreground">
          <button class="hover:text-primary transition-colors"><Phone :size="20" /></button>
          <button class="hover:text-primary transition-colors"><MoreVertical :size="20" /></button>
        </div>
      </div>

      <div class="flex-1 overflow-y-auto p-6 flex flex-col gap-6 bg-background/50">
        <div class="flex w-full">
          <div class="flex items-start gap-3 max-w-[80%]">
            <div class="w-8 h-8 rounded-full bg-primary/20 flex-shrink-0 mt-auto" />
            <div class="bg-card border border-border p-4 rounded-2xl rounded-bl-sm text-foreground shadow-sm">
              您好，亚历克斯！感谢您申请领养巴迪。我们目前正在审核您的申请。
              <div class="text-[10px] text-muted-foreground mt-2 text-right">上午10:28</div>
            </div>
          </div>
        </div>
        
        <div class="flex w-full justify-end">
          <div class="bg-primary text-primary-foreground p-4 rounded-2xl rounded-br-sm shadow-sm max-w-[80%]">
            嗨！这真是太好了。如果需要我提供任何额外信息，请告诉我。
            <div class="text-[10px] text-primary-foreground/80 mt-2 text-right">上午10:30</div>
          </div>
        </div>

        <div class="flex w-full">
          <div class="flex items-start gap-3 max-w-[80%]">
            <div class="w-8 h-8 rounded-full bg-primary/20 flex-shrink-0 mt-auto" />
            <div class="bg-card border border-border p-4 rounded-2xl rounded-bl-sm text-foreground shadow-sm">
              我们已收到您的申请！请确保在明天之前上传您的家庭参观视频。
              <div class="text-[10px] text-muted-foreground mt-2 text-right">上午10:30</div>
            </div>
          </div>
        </div>
      </div>

      <div class="p-4 border-t border-border bg-card">
        <div class="flex items-center gap-3 bg-background border border-border rounded-full p-2 pr-3">
          <input 
            type="text" 
            placeholder="输入您的消息..." 
            class="flex-1 bg-transparent border-none outline-none px-4 text-foreground text-sm"
          />
          <button class="w-10 h-10 bg-primary text-primary-foreground rounded-full flex items-center justify-center hover:opacity-90 transition-opacity">
            <Send :size="18" class="ml-1" />
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
