import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import Layout from '@/layout/MainLayout.vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login_updated.vue'),
    meta: { title: '登录' }
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
        path: 'lot',
        name: 'Lot',
        component: () => import('@/views/lot/LotList.vue'),
        meta: { title: 'LOT号管理', icon: 'Tickets' }
      },
      {
        path: 'inventory-query',
        name: 'InventoryQuery',
        component: () => import('@/views/InventoryQuery.vue'),
        meta: { title: '库存查询', icon: 'Search' }
      },
      {
        path: 'alert',
        name: 'Alert',
        component: () => import('@/views/AlertRule.vue'),
        meta: { title: '预警规则', icon: 'Warning' }
      },
      {
        path: 'inbound-register',
        name: 'InboundRegister',
        component: () => import('@/views/InboundRegister.vue'),
        meta: { title: '来料登记', icon: 'Download' }
      },
      {
        path: 'inbound-history',
        name: 'InboundHistory',
        component: () => import('@/views/InboundHistory.vue'),
        meta: { title: '来料记录', icon: 'Tickets' }
      },
      {
        path: 'label-template',
        name: 'LabelTemplate',
        component: () => import('@/views/LabelTemplate.vue'),
        meta: { title: '标签模板', icon: 'Postcard' }
      },
      {
        path: 'lot-rule',
        name: 'LotRule',
        component: () => import('@/views/LotRule.vue'),
        meta: { title: 'LOT号规则', icon: 'Operation' }
      },
      {
        path: 'operation-log',
        name: 'OperationLog',
        component: () => import('@/views/OperationLog.vue'),
        meta: { title: '操作日志', icon: 'Document' }
      },
      {
        path: 'outbound-record',
        name: 'OutboundRecord',
        component: () => import('@/views/OutboundRecord.vue'),
        meta: { title: '出入库记录', icon: 'List' }
      },
      {
        path: 'alert-management',
        name: 'AlertManagement',
        component: () => import('@/views/AlertManagement.vue'),
        meta: { title: '预警管理', icon: 'Bell' }
      },
      {
        path: 'report-management',
        name: 'ReportManagement',
        component: () => import('@/views/ReportManagement.vue'),
        meta: { title: '报表管理', icon: 'DataAnalysis' }
      },
      {
        path: 'large-screen',
        name: 'LargeScreen',
        component: () => import('@/views/LargeScreenDashboard.vue'),
        meta: { title: '大屏看板', icon: 'Monitor' }
      },
      {
        path: 'business-status',
        name: 'BusinessStatus',
        component: () => import('@/views/BusinessStatusDashboard.vue'),
        meta: { title: '业务状态', icon: 'TrendCharts' }
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
  if (to.path !== '/login' && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/')
  } else {
    next()
  }
})

export default router
