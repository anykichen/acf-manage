import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import Layout from '@/layout/MainLayout.vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', noAuth: true }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页', icon: 'DataLine' }
      },
      {
        path: 'material',
        name: 'Material',
        component: () => import('@/views/material/MaterialList.vue'),
        meta: { title: '料号管理', icon: 'Box' }
      },
      {
        path: 'label-template',
        name: 'LabelTemplate',
        component: () => import('@/views/LabelTemplate.vue'),
        meta: { title: '标签模板', icon: 'Document' }
      },
      {
        path: 'alert-rule',
        name: 'AlertRule',
        component: () => import('@/views/AlertRule.vue'),
        meta: { title: '预警规则', icon: 'AlertTriangle' }
      },
      {
        path: 'lot',
        name: 'Lot',
        component: () => import('@/views/lot/LotList.vue'),
        meta: { title: 'LOT号管理', icon: 'Tickets' }
      },
      {
        path: 'inventory',
        name: 'Inventory',
        component: () => import('@/views/inventory/InventoryQuery.vue'),
        meta: { title: '库存查询', icon: 'Search' }
      },
      {
        path: 'alert',
        name: 'Alert',
        component: () => import('@/views/alert/AlertList.vue'),
        meta: { title: '预警管理', icon: 'Warning' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  document.title = `${to.meta.title || 'ACF管控系统'} - ACF管控系统`

  const token = localStorage.getItem('token')
  if (!to.meta.noAuth && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/')
  } else {
    next()
  }
})

export default router
