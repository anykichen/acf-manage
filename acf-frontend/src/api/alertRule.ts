import request from '@/utils/request'
import type { PageResponse } from './labelTemplate'

/**
 * 预警规则接口
 */
export interface AlertRule {
  id?: number
  ruleName: string
  ruleType: string
  alertDaysBeforeExpire?: number
  minStockQuantity?: number
  maxUsageTimes?: number
  alertMethod: string
  isActive: number
  createTime?: string
  updateTime?: string
}

/**
 * 查询参数
 */
export interface RuleQuery {
  current?: number
  pageSize?: number
  ruleName?: string
  ruleType?: string
  isActive?: number
}

/**
 * 查询预警规则列表
 */
export function getAlertRuleList(params?: RuleQuery) {
  return request.get<PageResponse<AlertRule>>('/alert-rule/list', { params })
}

/**
 * 根据规则类型查询
 */
export function getAlertRuleByType(ruleType: string) {
  return request.get<AlertRule[]>(`/alert-rule/type/${ruleType}`)
}

/**
 * 根据ID查询预警规则
 */
export function getAlertRuleById(id: number) {
  return request.get<AlertRule>(`/alert-rule/${id}`)
}

/**
 * 新增预警规则
 */
export function addAlertRule(data: AlertRule) {
  return request.post<void>('/alert-rule', data)
}

/**
 * 更新预警规则
 */
export function updateAlertRule(id: number, data: AlertRule) {
  return request.put<void>(`/alert-rule/${id}`, data)
}

/**
 * 删除预警规则
 */
export function deleteAlertRule(id: number) {
  return request.delete<void>(`/alert-rule/${id}`)
}

/**
 * 批量删除预警规则
 */
export function batchDeleteAlertRules(ids: number[]) {
  return request.delete<void>('/alert-rule/batch', { data: ids })
}

/**
 * 激活规则
 */
export function activateAlertRule(id: number) {
  return request.put<void>(`/alert-rule/${id}/activate`)
}

/**
 * 停用规则
 */
export function deactivateAlertRule(id: number) {
  return request.put<void>(`/alert-rule/${id}/deactivate`)
}
