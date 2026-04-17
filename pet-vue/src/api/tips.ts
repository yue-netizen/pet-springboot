import request from '@/utils/request'

export interface PetTips {
  id?: number
  title: string
  category: string
  content: string
  coverImage?: string
  createTime?: string
  updateTime?: string
}

export interface TipsListResponse {
  records: PetTips[]
  total: number
  size: number
  current: number
  pages: number
}

export function getTipsList(page = 1, size = 10, category?: string) {
  return request.get<any, { data: TipsListResponse }>('/tips/list', {
    params: { page, size, category }
  })
}

export function getTipsById(id: number) {
  return request.get<any, { data: PetTips }>(`/tips/${id}`)
}

export function createTips(tips: Omit<PetTips, 'id' | 'createTime' | 'updateTime'>) {
  return request.post('/tips', tips)
}

export function updateTips(id: number, tips: Omit<PetTips, 'id' | 'createTime' | 'updateTime'>) {
  return request.put(`/tips/${id}`, tips)
}

export function deleteTips(id: number) {
  return request.delete(`/tips/${id}`)
}

export interface TipsAiRequest {
  breed: string
  question: string
}

export interface TipsAiResponse {
  answer: string
}

export function askTipsAi(data: TipsAiRequest) {
  return request.post<any, { data: TipsAiResponse }>('/tips-ai/ask', data)
}
