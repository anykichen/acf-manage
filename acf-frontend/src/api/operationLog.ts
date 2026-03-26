import request from '@/utils/request'

/**
 * 操作日志API
 */
export interface OperationLog {
  id: number
  operationType: string
  lotNumber: string
  materialCode: string
  operator: string
  operationTime: string
  operationDetail: string
  operationResult: string
  ipAddress: string
  createTime: string
}

/**
 * 分页查询操作日志
 */
export function queryLogPage(params: {
  pageNum: number
  pageSize: number
  lotNumber?: string
  operationType?: string
  startTime?: string
  endTime?: string
}) {
  return request({
    url: '/api/operation-log/page',
    method: 'get',
    params
  })
}

/**
 * 查询最近的操作日志
 */
export function queryRecentLogs(limit: number = 10) {
  return request({
    url: '/api/operation-log/recent',
    method: 'get',
    params: { limit }
  })
}

/**
 * 根据LOT号查询操作日志
 */
export function queryLogsByLotNumber(lotNumber: string) {
  return request({
    url: `/api/operation-log/lot/${lotNumber}`,
    method: 'get'
  })
}

/**
 * 统计操作日志数量
 */
export function countByOperationType(params?: {
  startTime?: string
  endTime?: string
}) {
  return request({
    url: '/api/operation-log/count',
    method: 'get',
    params
  })
}

/**
 * 根据ID查询日志详情
 */
export function queryLogById(id: number) {
  return request({
    url: `/api/operation-log/${id}`,
    method: 'get'
  })
}

/**
 * 删除操作日志
 */
export function deleteLog(id: number) {
  return request({
    url: `/api/operation-log/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除操作日志
 */
export function batchDeleteLogs(ids: number[]) {
  return request({
    url: '/api/operation-log/batch',
    method: 'delete',
    data: ids
  })
}
