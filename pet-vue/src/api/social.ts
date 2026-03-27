import request from '@/utils/request'

export interface Post {
  id: number
  userId: number
  content: string
  image: string
  likeCount: number
  commentCount: number
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
  content: string
  image?: string
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
