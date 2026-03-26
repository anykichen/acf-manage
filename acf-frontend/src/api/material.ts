import request from '@/utils/request'
import type { ApiResponse } from '@/utils/request'

// 料号接口
export interface Material {
  id?: number
  materialCode: string
  materialName: string
  materialDesc?: string
  unit: string
  barcodeRule?: string
  manufacturer?: string
  model?: string
  shelfLifeMonths: number
  maxUsageTimes: number
  status: number
  remark?: string
  createTime?: string
  updateTime?: string
}

// 分页参数
export interface PageParams {
  current?: number
  pageSize?: number
}

// 查询参数
export interface MaterialQuery extends PageParams {
  materialCode?: string
  materialName?: string
  manufacturer?: string
  status?: number
}

/**
 * 查询料号列表
 */
export function getMaterialList(params?: MaterialQuery) {
  return request.get<Material[]>('/material/list', { params })
}

/**
 * 根据ID查询料号
 */
export function getMaterialById(id: number) {
  return request.get<Material>(`/material/${id}`)
}

/**
 * 根据料号查询
 */
export function getMaterialByCode(materialCode: string) {
  return request.get<Material>(`/material/code/${materialCode}`)
}

/**
 * 新增料号
 */
export function addMaterial(data: Material) {
  return request.post<void>('/material', data)
}

/**
 * 更新料号
 */
export function updateMaterial(id: number, data: Material) {
  return request.put<void>(`/material/${id}`, data)
}

/**
 * 删除料号
 */
export function deleteMaterial(id: number) {
  return request.delete<void>(`/material/${id}`)
}

/**
 * 批量删除料号
 */
export function batchDeleteMaterials(ids: number[]) {
  return request.delete<void>('/material/batch', { data: ids })
}

/**
 * 更新料号状态
 */
export function updateMaterialStatus(id: number, status: number) {
  return request.put<void>(`/material/${id}/status`, null, { params: { status } })
}
