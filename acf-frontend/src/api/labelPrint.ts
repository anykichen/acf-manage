import request from '@/utils/request'

/**
 * 标签打印API
 */
export type LabelData = Record<string, any>

/**
 * 生成ZPL指令
 */
export function generateZPL(labelData: LabelData) {
  return request<string>({
    url: '/api/label-print/zpl',
    method: 'post',
    data: labelData
  })
}

/**
 * 生成EPL指令
 */
export function generateEPL(labelData: LabelData) {
  return request<string>({
    url: '/api/label-print/epl',
    method: 'post',
    data: labelData
  })
}

/**
 * 打印标签
 */
export function printLabel(zpl: string, printerIp?: string, printerPort?: number) {
  return request<Record<string, any>>({
    url: '/api/label-print/print',
    method: 'post',
    data: zpl,
    params: {
      printerIp,
      printerPort
    }
  })
}

/**
 * 预览标签
 */
export function previewLabel(labelData: LabelData) {
  return request<string>({
    url: '/api/label-print/preview',
    method: 'post',
    data: labelData
  })
}
