import request from '@/utils/request'

// LOT号接口
export interface Lot {
  id?: number
  lotNumber: string
  materialCode: string
  qrCode?: string
  inboundDate: string
  expireDate: string
  initialQuantity: number
  currentQuantity: number
  usageTimes: number
  warehouseLocation?: string
  lotStatus: string
  remark?: string
  createTime?: string
  updateTime?: string
}

// 查询参数
export interface LotQuery {
  lotNumber?: string
  materialCode?: string
  lotStatus?: string
  warehouseLocation?: string
}

/**
 * 查询LOT列表
 */
export function getLotList(params?: LotQuery) {
  return request.get<Lot[]>('/lot/list', { params })
}

/**
 * 根据LOT号查询
 */
export function getLotByNumber(lotNumber: string) {
  return request.get<Lot>(`/lot/${lotNumber}`)
}

/**
 * 生成LOT号
 */
export function generateLotNumber(materialCode: string) {
  return request.get<string>('/lot/generate', { params: { materialCode } })
}

/**
 * FIFO推荐LOT号
 */
export function getFifoRecommendations(materialCode: string, limit: number = 5) {
  return request.get<Lot[]>('/lot/fifo', { params: { materialCode, limit } })
}

/**
 * 发料（出库）
 */
export function outboundLot(data: {
  lotNumber: string
  quantity: number
  warehouseLocation?: string
  operatorId: number
  operatorName: string
}) {
  return request.post<void>('/lot/outbound', null, { params: data })
}

/**
 * 退库
 */
export function returnLot(data: {
  lotNumber: string
  quantity: number
  warehouseLocation: string
  operatorId: number
  operatorName: string
}) {
  return request.post<void>('/lot/return', null, { params: data })
}

/**
 * 报废
 */
export function scrapLot(data: {
  lotNumber: string
  remark?: string
  operatorId: number
  operatorName: string
}) {
  return request.post<void>('/lot/scrap', null, { params: data })
}

/**
 * 获取库存预警LOT
 */
export function getAlertLots() {
  return request.get<Lot[]>('/lot/alert')
}
