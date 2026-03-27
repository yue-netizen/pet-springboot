import request from '@/utils/request'

export interface Donation {
  id: number
  userId: number
  amount: number
  paymentMethod: string
  status: number
  transactionId: string
  remark: string
  createTime: string
}

export interface DonationCreate {
  amount: number
  paymentMethod?: string
  remark?: string
}

export function getDonationList(page = 1, size = 10) {
  return request.get<any, { data: { records: Donation[]; total: number } }>('/donation/list', {
    params: { page, size },
  })
}

export function createDonation(data: DonationCreate) {
  return request.post<any, { data: Donation }>('/donation', data)
}

export function getTotalAmount() {
  return request.get<any, { data: number }>('/donation/total')
}

export function getMyDonations(page = 1, size = 10) {
  return request.get<any, { data: { records: Donation[]; total: number } }>('/donation/my', {
    params: { page, size },
  })
}
