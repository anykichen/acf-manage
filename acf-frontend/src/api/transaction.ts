import request from '@/utils/request'

/**
 * 交易记录API
 */

// 分页查询交易记录
export const getTransactionPage = (params: any) => {
  return request({
    url: '/api/transaction/page',
    method: 'get',
    params
  })
}

// 按LOT号查询交易记录
export const getTransactionsByLot = (lotNumber: string) => {
  return request({
    url: `/api/transaction/lot/${lotNumber}`,
    method: 'get'
  })
}

// 统计交易记录
export const getTransactionStatistics = (params: any) => {
  return request({
    url: '/api/transaction/statistics',
    method: 'get',
    params
  })
}

// 导出交易记录Excel
export const exportTransactionExcel = (params: any) => {
  return request({
    url: '/api/transaction/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}
