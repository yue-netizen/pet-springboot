<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { Send, Loader2, Sparkles, MessageCircle } from 'lucide-vue-next'
import { askTipsAi } from '@/api/tips'

interface Message {
  role: 'user' | 'assistant'
  content: string
  time: string
}

const breed = ref('')
const question = ref('')
const messages = ref<Message[]>([])
const loading = ref(false)
const chatContainer = ref<HTMLElement | null>(null)

const scrollToBottom = async () => {
  await nextTick()
  if (chatContainer.value) {
    chatContainer.value.scrollTop = chatContainer.value.scrollHeight
  }
}

const handleAsk = async () => {
  if (!breed.value.trim() || !question.value.trim()) return
  
  messages.value.push({
    role: 'user',
    content: `品种：${breed.value}\n问题：${question.value}`,
    time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  })
  
  const currentBreed = breed.value
  const currentQuestion = question.value
  breed.value = ''
  question.value = ''
  
  loading.value = true
  await scrollToBottom()
  
  try {
    const res = await askTipsAi({ breed: currentBreed, question: currentQuestion })
    messages.value.push({
      role: 'assistant',
      content: res.data.answer,
      time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    })
  } catch (error) {
    console.error('AI请求失败:', error)
    messages.value.push({
      role: 'assistant',
      content: '抱歉，AI助手暂时无法响应，请稍后再试～🐾',
      time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    })
  } finally {
    loading.value = false
    scrollToBottom()
  }
}
</script>

<template>
  <div class="ai-assistant-wrapper mt-8">
    <div class="ai-card">
      <div class="split-layout">
        <div class="left-panel">
          <div class="cat-scene">
            <div class="orange-cat">
              <div class="cat-ear cat-ear-l"></div>
              <div class="cat-ear cat-ear-r"></div>
              <div class="cat-head">
                <div class="cat-stripe stripe-1"></div>
                <div class="cat-stripe stripe-2"></div>
                <div class="cat-stripe stripe-3"></div>
                <div class="cat-face-area">
                  <div class="cat-eye eye-l"><div class="eye-shine"></div></div>
                  <div class="cat-eye eye-r"><div class="eye-shine"></div></div>
                  <div class="cat-nose-area">
                    <div class="nose"></div>
                    <div class="mouth-line"></div>
                  </div>
                  <div class="cat-whisker whisker-l-1"></div>
                  <div class="cat-whisker whisker-l-2"></div>
                  <div class="cat-whisker whisker-r-1"></div>
                  <div class="cat-whisker whisker-r-2"></div>
                </div>
              </div>
              <div class="cat-body-part">
                <div class="chest-fur"></div>
              </div>
              <div class="cat-leg leg-fl"></div>
              <div class="cat-leg leg-fr"></div>
              <div class="cat-tail">
                <div class="tail-stripe tail-s-1"></div>
                <div class="tail-stripe tail-s-2"></div>
              </div>
            </div>
            <div class="cat-shadow"></div>
          </div>

          <div class="panel-title">
            <h2><Sparkles :size="18" /> 智能养宠助手</h2>
            <p>输入宠物品种和问题</p>
          </div>

          <div class="input-section">
            <div class="input-group">
              <label>宠物品种</label>
              <input 
                v-model="breed"
                type="text" 
                placeholder="布偶猫、金毛、荷兰猪..."
                @keyup.enter="$el.querySelector('.question-input')?.focus()"
              />
            </div>

            <div class="input-group">
              <label>您的问题</label>
              <input 
                v-model="question"
                type="text" 
                class="question-input"
                placeholder="软便吃什么？掉毛严重怎么办？"
                @keyup.enter="handleAsk"
              />
            </div>

            <button 
              @click="handleAsk"
              :disabled="!breed.trim() || !question.trim() || loading"
              class="ask-btn"
            >
              <Loader2 v-if="loading" :size="16" class="spin" />
              <Send v-else :size="16" />
              {{ loading ? '查询中...' : '询问助手' }}
            </button>
          </div>
        </div>

        <div class="divider-line"></div>

        <div class="right-panel">
          <div class="panel-header">
            <MessageCircle :size="18" />
            <span>AI 回答</span>
          </div>

          <div class="chat-area" ref="chatContainer">
            <div v-if="messages.length === 0 && !loading" class="empty-state">
              <div class="empty-cat-icon">😺</div>
              <p>输入品种和问题后点击询问</p>
              <p class="empty-hint">AI助手将为您提供专业的养宠建议</p>
            </div>

            <template v-for="(msg, index) in messages" :key="index">
              <div :class="['chat-msg', msg.role]">
                <div class="msg-avatar">
                  <span v-if="msg.role === 'user'">你</span>
                  <span v-else class="cat-mini">😺</span>
                </div>
                <div class="msg-bubble">
                  <div class="msg-content" v-html="formatMsg(msg.content)"></div>
                  <span class="msg-time">{{ msg.time }}</span>
                </div>
              </div>
            </template>

            <div v-if="loading" class="chat-msg assistant">
              <div class="msg-avatar"><span class="cat-mini">😺</span></div>
              <div class="msg-bubble typing-bubble">
                <div class="typing-dots">
                  <span></span><span></span><span></span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
export default {
  methods: {
    formatMsg(content: string) {
      return content
        .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
        .replace(/\*(.*?)\*/g, '<em>$1</em>')
        .replace(/\n/g, '<br/>')
    }
  }
}
</script>

<style scoped>
.ai-assistant-wrapper {
  width: 100%;
}

.ai-card {
  background: linear-gradient(145deg, #fffcf5 0%, #fff8ed 50%, #fff3e0 100%);
  border-radius: 24px;
  border: 2px solid rgba(251, 191, 36, 0.15);
  box-shadow: 0 4px 24px rgba(251, 146, 60, 0.08), 0 1px 3px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.split-layout {
  display: flex;
  height: 480px;
  max-height: 520px;
}

.left-panel {
  width: 340px;
  min-width: 320px;
  padding: 28px 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  background: rgba(255, 255, 255, 0.35);
  border-right: 1px dashed rgba(251, 146, 60, 0.15);
  overflow-y: auto;
}

.cat-scene {
  position: relative;
  width: 110px;
  height: 110px;
  flex-shrink: 0;
}

.orange-cat {
  position: absolute;
  width: 88px;
  height: 82px;
  left: 11px;
  top: 10px;
  animation: catBounce 3s ease-in-out infinite;
}

.cat-ear {
  position: absolute;
  width: 24px;
  height: 28px;
  background: linear-gradient(160deg, #f97316 0%, #ea580c 70%, #c2410c 100%);
  clip-path: polygon(50% 95%, 5% 0%, 95% 0%);
  top: -13px;
}
.cat-ear::after {
  content: '';
  position: absolute;
  width: 13px;
  height: 15px;
  background: #fef3c7;
  clip-path: polygon(50% 90%, 8% 5%, 92% 5%);
  top: 7px; left: 6px;
}
.cat-ear-l { left: 4px; transform: rotate(-12deg); animation: earMoveL 5s ease-in-out infinite; }
.cat-ear-r { right: 4px; transform: rotate(12deg); animation: earMoveR 5s ease-in-out infinite; }

.cat-head {
  position: absolute;
  width: 74px;
  height: 62px;
  background: linear-gradient(160deg, #fb923c 0%, #f97316 40%, #ea580c 100%);
  border-radius: 50% 50% 45% 45%;
  top: 7px; left: 7px;
  box-shadow: inset -3px -3px 10px rgba(180, 83, 9, 0.2), inset 3px 3px 10px rgba(255, 237, 213, 0.4);
  overflow: hidden;
}

.cat-stripe {
  position: absolute;
  background: linear-gradient(180deg, #c2410c 0%, #9a3412 100%);
  opacity: 0.35;
  border-radius: 2px;
}
.stripe-1 { width: 4px; height: 20px; top: 2px; left: 17px; transform: rotate(-8deg); }
.stripe-2 { width: 4px; height: 24px; top: 0; left: 35px; }
.stripe-3 { width: 4px; height: 20px; top: 2px; right: 17px; transform: rotate(8deg); }

.cat-face-area { position: relative; width: 100%; height: 100%; }

.cat-eye {
  position: absolute;
  width: 15px; height: 15px;
  background: radial-gradient(circle at 40% 35%, #4ade80 0%, #166534 60%, #052e16 100%);
  border-radius: 50%;
  top: 22px;
  box-shadow: inset 0 0 3px rgba(0,0,0,0.3);
  animation: blink 4s ease-in-out infinite;
}
.cat-eye::before {
  content: '';
  position: absolute;
  width: 15px; height: 15px;
  border-radius: 50%; border: 2px solid #1a1a1a;
  top: -1px; left: -1px;
}
.eye-shine {
  position: absolute;
  width: 5px; height: 5px;
  background: white; border-radius: 50%;
  top: 2px; left: 3px;
}
.eye-l { left: 14px; }
.eye-r { right: 14px; }

.cat-nose-area {
  position: absolute;
  bottom: 12px; left: 50%;
  transform: translateX(-50%);
}
.nose {
  width: 9px; height: 6px;
  background: #ec4899;
  border-radius: 50% 50% 45% 45%;
  margin: 0 auto;
}
.mouth-line {
  width: 13px; height: 5px;
  border-bottom: 2px solid #be185d;
  border-left: 1.5px solid #be185d;
  border-right: 1.5px solid #be185d;
  border-radius: 0 0 50% 50%;
  margin-top: 2px;
}

.cat-whisker {
  position: absolute;
  width: 20px; height: 1.5px;
  background: rgba(120, 80, 40, 0.4);
  border-radius: 1px; top: 37px;
}
.whisker-l-1 { left: -9px; transform: rotate(-8deg); }
.whisker-l-2 { left: -11px; top: 41px; transform: rotate(3deg); }
.whisker-r-1 { right: -9px; transform: rotate(8deg); }
.whisker-r-2 { right: -11px; top: 41px; transform: rotate(-3deg); }

.cat-body-part {
  position: absolute;
  width: 56px; height: 32px;
  background: linear-gradient(160deg, #fb923c 0%, #f97316 100%);
  border-radius: 50% 50% 40% 40%;
  top: 52px; left: 16px; z-index: 1;
}
.chest-fur {
  position: absolute;
  width: 38px; height: 26px;
  background: linear-gradient(180deg, #fefce8 0%, #fef3c7 100%);
  border-radius: 50%;
  top: 4px; left: 9px;
}

.cat-leg {
  position: absolute;
  width: 17px; height: 20px;
  background: linear-gradient(160deg, #fb923c 0%, #f97316 100%);
  border-radius: 8px 8px 10px 10px;
  top: 72px; z-index: 2;
}
.leg-fl { left: 20px; }
.leg-fr { right: 20px; }

.cat-tail {
  position: absolute;
  width: 9px; height: 34px;
  background: linear-gradient(180deg, #f97316 0%, #ea580c 60%, #c2410c 100%);
  border-radius: 4px;
  bottom: 10px; right: -5px;
  transform-origin: top center;
  animation: tailSwing 2s ease-in-out infinite;
  z-index: 0;
}
.tail-stripe {
  position: absolute;
  width: 100%; height: 5px;
  background: rgba(154, 52, 18, 0.3);
  border-radius: 2px;
}
.tail-s-1 { top: 9px; }
.tail-s-2 { top: 19px; }

.cat-shadow {
  position: absolute;
  width: 60px; height: 11px;
  background: radial-gradient(ellipse, rgba(200, 150, 100, 0.25) 0%, transparent 70%);
  border-radius: 50%;
  bottom: 2px; left: 25px;
  animation: shadowPulse 3s ease-in-out infinite;
}

.panel-title {
  text-align: center;
  width: 100%;
}
.panel-title h2 {
  font-size: 1.2rem;
  font-weight: 800;
  color: #9a3412;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin: 0 0 4px;
}
.panel-title p {
  color: #b45309;
  font-size: 0.82rem;
  margin: 0;
  opacity: 0.65;
}

.input-section {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.input-group label {
  font-size: 0.75rem;
  font-weight: 700;
  color: #c2410c;
  letter-spacing: 0.03em;
}
.input-group input {
  width: 100%;
  padding: 11px 14px;
  border: 2px solid rgba(251, 146, 60, 0.2);
  border-radius: 10px;
  background: white;
  font-size: 0.88rem;
  color: #444;
  outline: none;
  transition: all 0.25s;
}
.input-group input:focus {
  border-color: #f97316;
  box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.1);
}
.input-group input::placeholder { color: #bbb; }

.ask-btn {
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 0.9rem;
  font-weight: 700;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 7px;
  transition: all 0.25s;
  box-shadow: 0 3px 12px rgba(234, 88, 12, 0.3);
}
.ask-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 5px 16px rgba(234, 88, 12, 0.4);
}
.ask-btn:disabled {
  background: #d1d5db;
  cursor: not-allowed;
  box-shadow: none;
}

.divider-line {
  width: 1px;
  align-self: stretch;
  background: linear-gradient(to bottom,
    transparent 0%,
    rgba(251, 146, 60, 0.15) 15%,
    rgba(251, 146, 60, 0.15) 85%,
    transparent 100%
  );
}

.right-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  max-height: 100%;
  background: rgba(255, 255, 255, 0.2);
  overflow: hidden;
}

.panel-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 18px 24px 12px;
  color: #9a3412;
  font-weight: 700;
  font-size: 0.95rem;
  border-bottom: 1px dashed rgba(251, 146, 60, 0.12);
}

.chat-area {
  flex: 1;
  overflow-y: auto;
  min-height: 0;
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.chat-area::-webkit-scrollbar { width: 4px; }
.chat-area::-webkit-scrollbar-thumb { background: rgba(249, 115, 22, 0.2); border-radius: 4px; }

.empty-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #b45309;
  opacity: 0.55;
}
.empty-cat-icon {
  font-size: 3rem;
  opacity: 0.4;
  animation: floatSlow 3s ease-in-out infinite;
}
.empty-state p {
  font-size: 0.88rem;
  margin: 0;
}
.empty-hint {
  font-size: 0.78rem !important;
  opacity: 0.6;
}

.chat-msg {
  display: flex;
  gap: 8px;
  animation: msgIn 0.3s ease-out;
}
.chat.msg.user { flex-direction: row-reverse; }

.msg-avatar {
  width: 30px; height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.68rem;
  font-weight: 700;
  flex-shrink: 0;
}
.user .msg-avatar { background: #f97316; color: white; }
.assistant .msg-avatar { background: #fef3c7; }
.cat-mini { font-size: 0.92rem; }

.msg-bubble {
  max-width: 82%;
  padding: 10px 14px;
  border-radius: 14px;
  font-size: 0.84rem;
  line-height: 1.6;
}
.user .msg-bubble {
  background: linear-gradient(135deg, #f97316, #ea580c);
  color: white;
  border-radius: 14px 14px 4px 14px;
}
.assistant .msg-bubble {
  background: white;
  color: #374151;
  border: 1.5px solid rgba(251, 146, 60, 0.1);
  border-radius: 14px 14px 14px 4px;
  box-shadow: 0 1px 6px rgba(0,0,0,0.03);
}
.msg-content { word-break: break-word; }
.msg-content :deep(strong) { color: #c2410c; }
.msg-time {
  display: block;
  font-size: 0.64rem;
  margin-top: 4px;
  opacity: 0.45;
}
.user .msg-time { text-align: right; }

.typing-bubble {
  min-height: 38px;
  display: flex;
  align-items: center;
}
.typing-dots {
  display: flex;
  gap: 5px;
}
.typing-dots span {
  width: 6px; height: 6px;
  background: #fbbf24;
  border-radius: 50%;
  animation: dotBounce 1.2s ease-in-out infinite;
}
.typing-dots span:nth-child(2) { animation-delay: 0.15s; }
.typing-dots span:nth-child(3) { animation-delay: 0.3s; }

.spin { animation: spin 1s linear infinite; }

@keyframes catBounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
}
@keyframes blink {
  0%, 46%, 54%, 100% { transform: scaleY(1); }
  50% { transform: scaleY(0.05); }
}
@keyframes earMoveL {
  0%, 88%, 100% { transform: rotate(-12deg); }
  91% { transform: rotate(-22deg); }
  94% { transform: rotate(-12deg); }
}
@keyframes earMoveR {
  0%, 82%, 100% { transform: rotate(12deg); }
  85% { transform: rotate(22deg); }
  88% { transform: rotate(12deg); }
}
@keyframes tailSwing {
  0%, 100% { transform: rotate(-20deg); }
  50% { transform: rotate(30deg); }
}
@keyframes shadowPulse {
  0%, 100% { opacity: 0.6; transform: scale(1); }
  50% { opacity: 0.3; transform: scale(0.92); }
}
@keyframes floatSlow {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}
@keyframes msgIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}
@keyframes dotBounce {
  0%, 80%, 100% { transform: translateY(0); }
  40% { transform: translateY(-8px); }
}
@keyframes spin { to { transform: rotate(360deg); } }
</style>
