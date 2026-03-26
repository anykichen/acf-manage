import request from '@/utils/request'

/**
 * 看板API
 */

// 获取大屏看板数据
export const getDashboardData = () => {
  return request({
    url: '/api/dashboard/data',
    method: 'get'
  })
}

// 获取业务状态看板数据
export const getBusinessStatusData = () => {
  return request({
    url: '/api/dashboard/business-status',
    method: 'get'
  })
}

// 获取库存分布数据（饼图）
export const getInventoryDistribution = () => {
  return request({
    url: '/api/dashboard/inventory-distribution',
    method: 'get'
  })
}

// 获取交易趋势数据（折线图）
export const getTransactionTrend = (days: number = 7) => {
  return request({
    url: '/api/dashboard/transaction-trend',
    method: 'get',
    params: { days }
  })
}

// 获取预警统计数据（柱状图）
export const getAlertStatistics = () => {
  return request({
    url: '/api/dashboard/alert-statistics',
    method: 'get'
  })
}

// 获取料号使用量对比数据
export const getMaterialUsage = () => {
  return request({
    url: '/api/dashboard/material-usage',
    method: 'get'
  })
}
