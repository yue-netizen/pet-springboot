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
