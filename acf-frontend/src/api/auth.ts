import request from '@/utils/request'
import type { ApiResponse } from '@/utils/request'

/**
 * 登录请求
 */
export interface LoginRequest {
  username: string
  password: string
}

/**
 * 用户信息
 */
export interface UserInfo {
  id: number
  username: string
  realName: string
  email?: string
  phone?: string
  department?: string
  userType: string
  status: number
  lastLoginTime?: string
}

/**
 * 登录响应
 */
export interface LoginResponse {
  token: string
  userInfo: UserInfo
}

/**
 * 用户登录
 */
export function login(data: LoginRequest) {
  return request.post<LoginResponse>('/auth/login', data)
}

/**
 * 用户登出
 */
export function logout() {
  return request.post<void>('/auth/logout')
}

/**
 * 获取当前用户信息
 */
export function getCurrentUserInfo() {
  return request.get<UserInfo>('/auth/user-info')
}
