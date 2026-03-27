import request from '@/utils/request'

export interface Conversation {
  id: number
  user1Id: number
  user2Id: number
  lastMessage: string
  unreadCount1: number
  unreadCount2: number
  updateTime: string
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

export function getConversations() {
  return request.get<any, { data: Conversation[] }>('/chat/conversations')
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
