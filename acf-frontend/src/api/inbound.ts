import request from '@/utils/request'

/**
 * 来料管理API
 */
export interface InboundData {
  success: boolean
  materialCode?: string
  materialName?: string
  materialDesc?: string
  unit?: string
  manufacturer?: string
  model?: string
  shelfLifeMonths?: number
  maxUsageTimes?: number
  barcode?: string
  parseResult?: Record<string, any>
  message?: string
}

export interface LabelData {
  lotNumber: string
  qrCode: string
  materialCode: string
  materialName: string
  materialDesc: string
  model: string
  manufacturer: string
  inStockDate: string
  expireDate: string
  shelfLifeMonths: number
  maxUsageTimes: number
  templateName: string
  templateCode: string
  fieldsConfig: Record<string, any>
}

/**
 * 扫码识别来料
 */
export function scanBarcode(barcode: string) {
  return request<InboundData>({
    url: '/api/inbound/scan',
    method: 'post',
    params: { barcode }
  })
}

/**
 * 确认来料并生成LOT号
 */
export function confirmInbound(data: {
  materialCode: string
  quantity: number
  operator?: string
}) {
  return request<string>({
    url: '/api/inbound/confirm',
    method: 'post',
    params: data
  })
}

/**
 * 生成标签数据
 */
export function generateLabelData(lotNumber: string) {
  return request<{ success: boolean; labelData?: LabelData; message?: string }>({
    url: `/api/inbound/label/${lotNumber}`,
    method: 'get'
  })
}

/**
 * 分页查询来料记录
 */
export function queryInboundPage(params: {
  pageNum: number
  pageSize: number
  materialCode?: string
  startTime?: string
  endTime?: string
}) {
  return request({
    url: '/api/inbound/page',
    method: 'get',
    params
  })
}
