import request from '@/utils/request'

export interface Story {
  id: number
  userId: number
  petId: number
  title: string
  content: string
  image: string
  likeCount: number
  viewCount: number
  createTime: string
}

export interface StoryCreate {
  petId?: number
  title: string
  content: string
  image?: string
}

export function getStoryList(page = 1, size = 10) {
  return request.get<any, { data: { records: Story[]; total: number } }>('/story/list', {
    params: { page, size },
  })
}

export function getStoryById(id: number) {
  return request.get<any, { data: Story }>(`/story/${id}`)
}

export function createStory(data: StoryCreate) {
  return request.post('/story', data)
}

export function likeStory(id: number) {
  return request.post(`/story/${id}/like`)
}

export function getMyStories(page = 1, size = 10) {
  return request.get<any, { data: { records: Story[]; total: number } }>('/story/my', {
    params: { page, size },
  })
}
