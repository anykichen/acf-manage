import request from '@/utils/request'

/**
 * LOT号规则API
 */
export interface LotRule {
  id: number
  ruleName: string
  ruleDescription: string
  qrCodeFormat: string
  textDescriptionFormat: string
  sequenceStart: number
  sequenceLength: number
  isDefault: number
  status: number
  createdBy: string
  createTime: string
  updatedBy: string
  updateTime: string
}

/**
 * 分页查询规则
 */
export function queryRulePage(params: {
  pageNum: number
  pageSize: number
  ruleName?: string
  status?: number
}) {
  return request({
    url: '/api/lot-rule/page',
    method: 'get',
    params
  })
}

/**
 * 查询所有规则
 */
export function queryAllRules() {
  return request({
    url: '/api/lot-rule/list',
    method: 'get'
  })
}

/**
 * 查询启用的规则
 */
export function queryActiveRules() {
  return request({
    url: '/api/lot-rule/active',
    method: 'get'
  })
}

/**
 * 获取默认规则
 */
export function getDefaultRule() {
  return request({
    url: '/api/lot-rule/default',
    method: 'get'
  })
}

/**
 * 根据ID查询规则
 */
export function queryRuleById(id: number) {
  return request({
    url: `/api/lot-rule/${id}`,
    method: 'get'
  })
}

/**
 * 新增规则
 */
export function createRule(rule: LotRule) {
  return request({
    url: '/api/lot-rule',
    method: 'post',
    data: rule
  })
}

/**
 * 更新规则
 */
export function updateRule(id: number, rule: LotRule) {
  return request({
    url: `/api/lot-rule/${id}`,
    method: 'put',
    data: rule
  })
}

/**
 * 删除规则
 */
export function deleteRule(id: number) {
  return request({
    url: `/api/lot-rule/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除规则
 */
export function batchDeleteRules(ids: number[]) {
  return request({
    url: '/api/lot-rule/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 设置默认规则
 */
export function setDefaultRule(id: number) {
  return request({
    url: `/api/lot-rule/${id}/set-default`,
    method: 'put'
  })
}

/**
 * 生成LOT号
 */
export function generateLotNumber(materialCode: string) {
  return request({
    url: '/api/lot-rule/generate',
    method: 'get',
    params: { materialCode }
  })
}
