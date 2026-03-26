import request from '@/utils/request'

/**
 * 预警API
 */

// 检查过期预警
export const checkExpireAlert = () => {
  return request({
    url: '/api/alert/check-expire',
    method: 'post'
  })
}

// 检查使用次数预警
export const checkUsageAlert = () => {
  return request({
    url: '/api/alert/check-usage',
    method: 'post'
  })
}

// 获取待处理的预警
export const getPendingAlerts = () => {
  return request({
    url: '/api/alert/pending',
    method: 'get'
  })
}

// 标记预警为已处理
export const markAlertAsResolved = (alertId: number) => {
  return request({
    url: `/api/alert/${alertId}/resolve`,
    method: 'put'
  })
}

// 手动触发预警检查
export const triggerAlertCheck = () => {
  return request({
    url: '/api/alert/trigger',
    method: 'post'
  })
}
