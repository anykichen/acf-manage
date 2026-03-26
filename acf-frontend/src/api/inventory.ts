import request from '@/utils/request'

/**
 * 库存管理API
 */
export interface InventoryItem {
  lotNumber: string
  materialCode: string
  quantity: number
  status: string
  usageTimes: number
  inStockTime: string
  expireTime: string
  warehouseLocation: string
  createTime: string
  daysInStock: number
  daysUntilExpire: number
  isExpired: boolean
}

export interface FIFORecommendItem {
  lotNumber: string
  materialCode: string
  quantity: number
  inStockTime: string
  expireTime: string
  usageTimes: number
  warehouseLocation: string
  daysUntilExpire: number
}

/**
 * 入库
 */
export function inbound(data: {
  lotNumber: string
  warehouseLocation: string
  operator?: string
}) {
  return request({
    url: '/api/inventory/inbound',
    method: 'post',
    params: data
  })
}

/**
 * 发料
 */
export function outbound(data: {
  lotNumber: string
  quantity: number
  warehouseLocation: string
  operator?: string
}) {
  return request({
    url: '/api/inventory/outbound',
    method: 'post',
    params: data
  })
}

/**
 * 退库
 */
export function returnStock(data: {
  lotNumber: string
  quantity: number
  warehouseLocation: string
  operator?: string
}) {
  return request({
    url: '/api/inventory/return',
    method: 'post',
    params: data
  })
}

/**
 * 报废
 */
export function scrap(data: {
  lotNumber: string
  remark: string
  operator?: string
}) {
  return request({
    url: '/api/inventory/scrap',
    method: 'post',
    params: data
  })
}

/**
 * FIFO推荐
 */
export function fifoRecommend(materialCode: string, limit: number = 5) {
  return request<FIFORecommendItem[]>({
    url: '/api/inventory/fifo-recommend',
    method: 'get',
    params: { materialCode, limit }
  })
}

/**
 * 查询库存
 */
export function queryInventory(params: {
  pageNum: number
  pageSize: number
  materialCode?: string
  lotNumber?: string
  warehouseLocation?: string
  status?: string
  startTime?: string
  endTime?: string
}) {
  return request({
    url: '/api/inventory/page',
    method: 'get',
    params
  })
}

/**
 * 查询库存统计
 */
export function queryInventoryStatistics() {
  return request<Record<string, any>>({
    url: '/api/inventory/statistics',
    method: 'get'
  })
}
