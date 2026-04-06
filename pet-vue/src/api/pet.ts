import request from '@/utils/request'

export interface Pet {
  id: number
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
  createTime: string
}

export interface PetQuery {
  name?: string
  type?: string
  breed?: string
  age?: string
  minAge?: number
  maxAge?: number
  status?: number
  page?: number
  size?: number
}

export interface AdoptionApply {
  petId: number
  reason: string
  address: string
  phone: string
  name?: string
  email?: string
  applicantAge?: number
  housingType?: string
  hasPetExperience?: string
  familyStatus?: string
  agreeHealthCheck?: boolean
  agreeNeuter?: boolean
  agreeGoodEnvironment?: boolean
  agreeTimelyMedical?: boolean
}

export function getPetList(params: PetQuery) {
  return request.get<any, { data: { records: Pet[]; total: number } }>('/pet/list', {
    params,
  })
}

export function getPetById(id: number) {
  return request.get<any, { data: Pet }>(`/pet/${id}`)
}

export function applyAdoption(data: AdoptionApply) {
  return request.post('/adoption/apply', data)
}

export function getMyAdoptions(page = 1, size = 10) {
  return request.get<any, { data: { records: Pet[]; total: number } }>('/adoption/my', {
    params: { page, size },
  })
}

export interface AdoptionRecord {
  id: number
  userId: number
  petId: number
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
  updateTime: string
  petName: string
  petImage: string
  petBreed: string
  petType: string
}

export function getMyAdoptionRecords(page = 1, size = 10) {
  return request.get<any, { data: { records: AdoptionRecord[]; total: number } }>('/adoption/my/records', {
    params: { page, size },
  })
}
