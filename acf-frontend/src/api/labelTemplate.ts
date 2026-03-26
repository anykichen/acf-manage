import request from '@/utils/request'

/**
 * 标签模板接口
 */
export interface LabelTemplate {
  id?: number
  templateName: string
  templateCode: string
  width: number
  height: number
  fieldsConfig: string
  printDriver?: string
  isDefault: number
  status: number
  createTime?: string
  updateTime?: string
}

/**
 * 分页参数
 */
export interface PageParams {
  current?: number
  pageSize?: number
}

/**
 * 分页响应
 */
export interface PageResponse<T> {
  records: T[]
  total: number
  current: number
  size: number
  pages: number
}

/**
 * 查询参数
 */
export interface TemplateQuery extends PageParams {
  templateName?: string
  status?: number
}

/**
 * 查询标签模板列表
 */
export function getTemplateList(params?: TemplateQuery) {
  return request.get<PageResponse<LabelTemplate>>('/label-template/list', { params })
}

/**
 * 根据ID查询标签模板
 */
export function getTemplateById(id: number) {
  return request.get<LabelTemplate>(`/label-template/${id}`)
}

/**
 * 获取默认标签模板
 */
export function getDefaultTemplate() {
  return request.get<LabelTemplate>('/label-template/default')
}

/**
 * 新增标签模板
 */
export function addTemplate(data: LabelTemplate) {
  return request.post<void>('/label-template', data)
}

/**
 * 更新标签模板
 */
export function updateTemplate(id: number, data: LabelTemplate) {
  return request.put<void>(`/label-template/${id}`, data)
}

/**
 * 删除标签模板
 */
export function deleteTemplate(id: number) {
  return request.delete<void>(`/label-template/${id}`)
}

/**
 * 批量删除标签模板
 */
export function batchDeleteTemplates(ids: number[]) {
  return request.delete<void>('/label-template/batch', { data: ids })
}

/**
 * 设置默认模板
 */
export function setDefaultTemplate(id: number) {
  return request.put<void>(`/label-template/${id}/set-default`)
}
