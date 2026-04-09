import request from '@/utils/request'

export function followUser(targetUserId: number) {
  return request.post('/follow', { targetUserId })
}

export function unfollowUser(targetUserId: number) {
  return request.delete(`/follow/${targetUserId}`)
}

export function checkFollowStatus(targetUserId: number) {
  return request.get<any, { data: boolean }>('/follow/check', {
    params: { targetUserId }
  })
}

export function getMyFollows(page = 1, size = 20) {
  return request.get<any, { data: { records: any[]; total: number } }>('/follow/my', {
    params: { page, size }
  })
}
