import request from '@/utils/request'

export interface Conversation {
  id: number
  user1Id: number
  user2Id: number
  lastMessage: string
  unreadCount1: number
  unreadCount2: number
  updateTime: string
  user1Nickname?: string
  user1Avatar?: string
  user2Nickname?: string
  user2Avatar?: string
}

export interface Message {
  id: number
  conversationId: number
  senderId: number
  receiverId: number
  content: string
  type: number
  status: number
  createTime: string
}

export interface MessageSend {
  receiverId: number
  content: string
  type?: number
}

export interface AiChatMessage {
  role: 'user' | 'assistant'
  content: string
}

export interface AiChatRequest {
  message: string
  sessionId?: string
}

export function getConversations() {
  return request.get<any, { data: Conversation[] }>('/chat/conversations')
}

export function getOrCreateConversation(targetUserId: number) {
  return request.get<any, { data: Conversation }>(`/chat/conversation/with/${targetUserId}`)
}

export function getMessages(conversationId: number, page = 1, size = 20) {
  return request.get<any, { data: { records: Message[]; total: number } }>(
    `/chat/messages/${conversationId}`,
    { params: { page, size } }
  )
}

export function sendMessage(data: MessageSend) {
  return request.post<any, { data: Message }>('/chat/send', data)
}

export function markAsRead(conversationId: number) {
  return request.put(`/chat/read/${conversationId}`)
}

export function getUnreadCount() {
  return request.get<any, { data: number }>('/chat/unread')
}

export function sendAiMessage(data: AiChatRequest) {
  return request.post<any, { data: string }>('/ai/chat', data)
}

export function getAiChatHistory(sessionId: string) {
  return request.get<any, { data: AiChatMessage[] }>(`/ai/history/${sessionId}`)
}

export function clearAiSession(sessionId: string) {
  return request.delete(`/ai/session/${sessionId}`)
}

export function getAiSessions() {
  return request.get<any, { data: string[] }>('/ai/sessions')
}
