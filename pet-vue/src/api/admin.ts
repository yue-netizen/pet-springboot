import request from '@/utils/request'
import type { UserDTO, SysConfig } from './config'

export function getAllUsers() {
  return request.get<any, { data: UserDTO[] }>('/admin/user/list')
}

export function updateUserByAdmin(user: UserDTO) {
  return request.put<any, { data: UserDTO }>('/admin/user', user)
}

export function deleteUser(id: number) {
  return request.delete(`/admin/user/${id}`)
}

export function updateUserStatus(id: number, status: number) {
  return request.put(`/admin/user/status/${id}?status=${status}`)
}

export function getConfigList() {
  return request.get<any, { data: SysConfig[] }>('/admin/config/list')
}

export function getConfigByKey(key: string) {
  return request.get<any, { data: SysConfig }>(`/admin/config/${key}`)
}

export function updateConfig(key: string, value: string) {
  return request.put(`/admin/config/${key}`, { configValue: value })
}

export function uploadImage(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post<any, { data: string }>('/admin/upload/image', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}

export interface Pet {
  id?: number
  name: string
  breed: string
  age: string
  type: string
  gender: string
  image: string
  description: string
  healthStatus: string
  status: number
  story: string
  size: string
  personality: string
}

export function getPetList(page: number = 1, size: number = 100) {
  return request.get<any, { data: { records: Pet[], total: number } }>(`/admin/pet/list?page=${page}&size=${size}`)
}

export function getPetById(id: number) {
  return request.get<any, { data: Pet }>(`/admin/pet/${id}`)
}

export function addPet(pet: Pet) {
  return request.post('/admin/pet', pet)
}

export function updatePet(pet: Pet) {
  return request.put('/admin/pet', pet)
}

export function deletePet(id: number) {
  return request.delete(`/admin/pet/${id}`)
}

export interface AdoptionDetail {
  id: number
  userId: number
  username: string
  userEmail: string
  userPhone: string
  petId: number
  petName: string
  petBreed: string
  petAge: string
  petImage: string
  status: number
  reason: string
  address: string
  phone: string
  email: string
  applicantAge: number
  housingType: string
  hasPetExperience: string
  familyStatus: string
  agreeHealthCheck: boolean
  agreeNeuter: boolean
  agreeGoodEnvironment: boolean
  agreeTimelyMedical: boolean
  createTime: string
}

export function getAdoptionList(page: number = 1, size: number = 10, status?: number) {
  let url = `/admin/adoption/list?page=${page}&size=${size}`
  if (status !== undefined) {
    url += `&status=${status}`
  }
  return request.get<any, { data: { records: AdoptionDetail[], total: number } }>(url)
}

export function getAdoptionDetail(id: number) {
  return request.get<any, { data: AdoptionDetail }>(`/admin/adoption/${id}`)
}

export function reviewAdoption(id: number, status: number) {
  return request.put(`/admin/adoption/review/${id}?status=${status}`)
}

export function updateAdoption(adoption: any) {
  return request.put('/admin/adoption', adoption)
}

export interface Post {
  id: number
  userId: number
  title: string
  content: string
  image: string
  images: string
  video: string
  videos: string
  tags: string
  likeCount: number
  commentCount: number
  status: number
  createTime: string
  updateTime: string
}

export function getPostList(page: number = 1, size: number = 100) {
  return request.get<any, { data: { records: Post[], total: number } }>(`/admin/post/list?page=${page}&size=${size}`)
}

export function getPostById(id: number) {
  return request.get<any, { data: Post }>(`/admin/post/${id}`)
}

export function deletePost(id: number) {
  return request.delete(`/admin/post/${id}`)
}
