import request from '@/utils/request'

export interface LoginParams {
  username: string
  password: string
}

export interface RegisterParams {
  username: string
  password: string
  nickname: string
  email?: string
  phone?: string
}

export interface UserInfo {
  id: number
  username: string
  nickname: string
  avatar: string
  email: string
  phone: string
  address: string
  status: number
}

export interface LoginResult {
  userId: number
  username: string
  nickname: string
  avatar: string
  token: string
  role: number
}

export function login(data: LoginParams) {
  return request.post<any, { data: LoginResult }>('/auth/login', null, {
    params: data,
  })
}

export function register(data: RegisterParams) {
  return request.post('/auth/register', data)
}

export function getUserInfo() {
  return request.get<any, { data: UserInfo }>('/user/info')
}

export function updateUserInfo(data: Partial<UserInfo>) {
  return request.put('/user', data)
}

export function updatePassword(oldPassword: string, newPassword: string) {
  return request.put('/user/password', null, {
    params: { oldPassword, newPassword },
  })
}
