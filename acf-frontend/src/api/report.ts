import request from '@/utils/request'

/**
 * 报表API
 */

// 获取库存统计报表
export const getInventoryReport = () => {
  return request({
    url: '/api/report/inventory',
    method: 'get'
  })
}

// 获取交易统计报表
export const getTransactionReport = (params: any) => {
  return request({
    url: '/api/report/transaction',
    method: 'get',
    params
  })
}

// 导出库存报表
export const exportInventoryReport = () => {
  return request({
    url: '/api/report/inventory/export',
    method: 'get',
    responseType: 'blob'
  })
}

// 导出交易报表
export const exportTransactionReport = (params: any) => {
  return request({
    url: '/api/report/transaction/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 获取预警报表
export const getAlertReport = () => {
  return request({
    url: '/api/report/alert',
    method: 'get'
  })
}
