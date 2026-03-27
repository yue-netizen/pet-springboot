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
}

export interface PetQuery {
  name?: string
  type?: string
  breed?: string
  age?: string
  status?: number
  page?: number
  size?: number
}

export interface AdoptionApply {
  petId: number
  reason: string
  address: string
  phone: string
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
