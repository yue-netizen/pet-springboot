import request from '@/utils/request'

export interface StoryDetail {
  id: number
  userId: number
  petId?: number
  title: string
  content: string
  image: string
  likeCount: number
  viewCount: number
  status: number
  createTime: string
  updateTime: string
  petName?: string
  petImage?: string
  petBreed?: string
  username?: string
  userAvatar?: string
  isLiked?: boolean
}

export interface StoryCommentDetail {
  id: number
  storyId: number
  userId: number
  parentId: number
  content: string
  likeCount: number
  status: number
  createTime: string
  updateTime: string
  username?: string
  userAvatar?: string
}

export interface StoryCommentCreate {
  storyId: number
  parentId?: number
  content: string
}

export interface StoryCreate {
  petId?: number
  title: string
  content: string
  image?: string
}

export function getStoryList(page = 1, size = 10) {
  return request.get<any, { data: { records: StoryDetail[]; total: number } }>('/story/list', {
    params: { page, size },
  })
}

export function getStoryById(id: number) {
  return request.get<any, { data: StoryDetail }>(`/story/${id}`)
}

export function createStory(data: StoryCreate) {
  return request.post('/story', data)
}

export function likeStory(id: number) {
  return request.post(`/story/${id}/like`)
}

export function getMyStories(page = 1, size = 10) {
  return request.get<any, { data: { records: StoryDetail[]; total: number } }>('/story/my', {
    params: { page, size },
  })
}

export function getMyLikedStories(page = 1, size = 10) {
  return request.get<any, { data: { records: StoryDetail[]; total: number } }>('/story/my/liked', {
    params: { page, size },
  })
}

export function getStoryComments(storyId: number, page = 1, size = 10) {
  return request.get<any, { data: { records: StoryCommentDetail[]; total: number } }>(`/story-comment/story/${storyId}`, {
    params: { page, size },
  })
}

export function createStoryComment(data: StoryCommentCreate) {
  return request.post('/story-comment', data)
}

export function deleteStoryComment(id: number) {
  return request.delete(`/story-comment/${id}`)
}
