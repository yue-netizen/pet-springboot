import request from '@/utils/request'

export interface Job {
  id: number
  title: string
  type: string
  location: string
  description: string
  requirement: string
  salaryMin: number
  salaryMax: number
  status: number
  createTime: string
}

export interface JobApplication {
  id: number
  jobId: number
  userId: number
  name: string
  phone: string
  email: string
  resume: string
  introduction: string
  status: number
  createTime: string
}

export interface JobQuery {
  title?: string
  type?: string
  location?: string
  status?: number
  page?: number
  size?: number
}

export interface JobApplyParams {
  jobId: number
  name: string
  phone: string
  email: string
  resume?: string
  introduction?: string
}

export function getJobList(params: JobQuery) {
  return request.get<any, { data: { records: Job[]; total: number } }>('/job/list', {
    params,
  })
}

export function getJobById(id: number) {
  return request.get<any, { data: Job }>(`/job/${id}`)
}

export function applyJob(data: JobApplyParams) {
  return request.post('/job/apply', data)
}

export function getMyApplications(page = 1, size = 10) {
  return request.get<any, { data: { records: JobApplication[]; total: number } }>(
    '/job/my-applications',
    { params: { page, size } }
  )
}
