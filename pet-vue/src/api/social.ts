import request from '@/utils/request'

export interface Post {
  id: number
  userId: number
  title?: string
  content: string
  image: string
  images: string
  video: string
  videos: string
  tags: string
  likeCount: number
  commentCount: number
  createTime: string
  liked?: boolean
  userNickname?: string
  userAvatar?: string
}

export interface Topic {
  id: number
  name: string
  useCount: number
  createTime: string
}

export interface Comment {
  id: number
  postId: number
  userId: number
  parentId: number
  content: string
  likeCount: number
  createTime: string
}

export interface PostCreate {
  title?: string
  content: string
  image?: string
  images?: string
  video?: string
  videos?: string
  tags?: string
}

export interface CommentCreate {
  postId: number
  parentId?: number
  content: string
}

export function getPostList(page = 1, size = 10) {
  return request.get<any, { data: { records: Post[]; total: number } }>('/post/list', {
    params: { page, size },
  })
}

export function getPostById(id: number) {
  return request.get<any, { data: Post }>(`/post/${id}`)
}

export function createPost(data: PostCreate) {
  return request.post('/post', data)
}

export function deletePost(id: number) {
  return request.delete(`/post/${id}`)
}

export function likePost(id: number) {
  return request.post(`/post/${id}/like`)
}

export function unlikePost(id: number) {
  return request.delete(`/post/${id}/like`)
}

export function checkPostLiked(id: number) {
  return request.get<any, { data: boolean }>(`/post/${id}/liked`)
}

export function getComments(postId: number, page = 1, size = 10) {
  return request.get<any, { data: { records: Comment[]; total: number } }>(
    `/comment/post/${postId}`,
    { params: { page, size } }
  )
}

export function createComment(data: CommentCreate) {
  return request.post('/comment', data)
}

export function deleteComment(id: number) {
  return request.delete(`/comment/${id}`)
}

export function uploadImage(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post<any, { data: string }>('/upload/image', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}

export function uploadVideo(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post<any, { data: string }>('/upload/video', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}

export function deleteFile(url: string) {
  return request.delete('/upload', {
    params: { url },
  })
}

export function getTrendingTopics(page = 1, size = 10) {
  return request.get<any, { data: { records: Topic[]; total: number } }>('/topic/trending', {
    params: { page, size },
  })
}

export function searchTopics(keyword: string, page = 1, size = 10) {
  return request.get<any, { data: { records: Topic[]; total: number } }>('/topic/search', {
    params: { keyword, page, size },
  })
}

export function createTopic(name: string) {
  return request.post<any, { data: Topic }>('/topic', null, {
    params: { name },
  })
}

export function getMyPosts(page = 1, size = 10) {
  return request.get<any, { data: { records: Post[]; total: number } }>('/post/my', {
    params: { page, size },
  })
}

export function getPostListByUser(userId: number, page = 1, size = 100) {
  return request.get<any, { data: { records: Post[]; total: number } }>('/post/user/' + userId, {
    params: { page, size },
  })
}

export function getMyLikedPosts(page = 1, size = 10) {
  return request.get<any, { data: { records: Post[]; total: number } }>('/post/my/liked', {
    params: { page, size },
  })
}

export function searchPosts(keyword: string, page = 1, size = 20) {
  return request.get<any, { data: { records: Post[]; total: number } }>('/post/search', {
    params: { keyword, page, size },
  })
}
