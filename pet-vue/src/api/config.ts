import request from '@/utils/request'

export interface SysConfig {
  id: number
  configKey: string
  configValue: string
  configName: string
  description: string
}

export interface UserDTO {
  id: number
  username: string
  nickname: string
  avatar: string
  email: string
  phone: string
  address: string
  status: number
  role: number
}

export function getConfigList() {
  return request.get<any, { data: SysConfig[] }>('/config/list')
}

export function getConfigByKey(key: string) {
  return request.get<any, { data: SysConfig }>(`/config/${key}`)
}

export function updateConfig(key: string, value: string) {
  return request.put(`/config/${key}`, { configValue: value })
}
