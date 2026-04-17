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

export interface Job {
  id?: number
  title: string
  type: string
  location: string
  description: string
  requirement: string
  salaryMin?: number
  salaryMax?: number
  status: number
}

export function getJobList(page: number = 1, size: number = 100) {
  return request.get<any, { data: { records: Job[], total: number } }>(`/admin/job/list?page=${page}&size=${size}`)
}

export function getJobById(id: number) {
  return request.get<any, { data: Job }>(`/admin/job/${id}`)
}

export function addJob(job: Job) {
  return request.post('/admin/job', job)
}

export function updateJob(job: Job) {
  return request.put('/admin/job', job)
}

export function deleteJob(id: number) {
  return request.delete(`/admin/job/${id}`)
}

export interface JobApplication {
  id?: number
  jobId?: number
  userId?: number
  name: string
  phone: string
  email: string
  age?: number
  address?: string
  resume?: string
  introduction?: string
  availability?: string
  status: number
  createTime?: string
}

export function getApplicationList(page: number = 1, size: number = 100) {
  return request.get<any, { data: { records: JobApplication[], total: number } }>(`/admin/employee/applications?page=${page}&size=${size}`)
}

export interface Employee {
  id?: number
  userId?: number
  jobId?: number
  name: string
  phone: string
  email: string
  age?: number
  address?: string
  resume?: string
  introduction?: string
  availability?: string
  position?: string
  department?: string
  hireDate?: string
  status: number
}

export function getEmployeeList(page: number = 1, size: number = 100) {
  return request.get<any, { data: { records: Employee[], total: number } }>(`/admin/employee/list?page=${page}&size=${size}`)
}

export function getEmployeeById(id: number) {
  return request.get<any, { data: Employee }>(`/admin/employee/${id}`)
}

export function approveApplication(applicationId: number) {
  return request.put(`/admin/employee/approve/${applicationId}`)
}

export function rejectApplication(applicationId: number) {
  return request.put(`/admin/employee/reject/${applicationId}`)
}

export function updateEmployee(employee: Employee) {
  return request.put('/admin/employee', employee)
}

export function updateEmployeeStatus(id: number, status: number) {
  return request.put(`/admin/employee/status/${id}?status=${status}`)
}

export function deleteEmployee(id: number) {
  return request.delete(`/admin/employee/${id}`)
}

