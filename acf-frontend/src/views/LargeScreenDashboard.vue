<template>
  <div class="large-screen-dashboard" :class="{ fullscreen: isFullscreen }">
    <div class="dashboard-header">
      <h1>ACF管控系统 - 实时监控大屏</h1>
      <div class="header-actions">
        <el-button type="primary" @click="toggleFullscreen">
          {{ isFullscreen ? '退出全屏' : '全屏显示' }}
        </el-button>
        <div class="current-time">{{ currentTime }}</div>
      </div>
    </div>

    <el-row :gutter="20" class="dashboard-content">
      <!-- 左侧 -->
      <el-col :span="6">
        <el-card class="chart-card">
          <template #header>
            <span>库存分布</span>
          </template>
          <div ref="inventoryPieRef" class="chart-container"></div>
        </el-card>

        <el-card class="chart-card">
          <template #header>
            <span>预警统计</span>
          </template>
          <div ref="alertBarRef" class="chart-container"></div>
        </el-card>
      </el-col>

      <!-- 中间 -->
      <el-col :span="12">
        <el-row :gutter="20" class="statistics-cards">
          <el-col :span="6">
            <el-statistic title="总库存批次" :value="dashboardData.totalBatchCount">
              <template #prefix>
                <el-icon><Tickets /></el-icon>
              </template>
            </el-statistic>
          </el-col>
          <el-col :span="6">
            <el-statistic title="在库批次" :value="dashboardData.inStockCount">
              <template #prefix>
                <el-icon><Box /></el-icon>
              </template>
            </el-statistic>
          </el-col>
          <el-col :span="6">
            <el-statistic title="使用中" :value="dashboardData.inUseCount">
              <template #prefix>
                <el-icon><Loading /></el-icon>
              </template>
            </el-statistic>
          </el-col>
          <el-col :span="6">
            <el-statistic title="待处理预警" :value="dashboardData.pendingAlertCount">
              <template #prefix>
                <el-icon><Bell /></el-icon>
              </template>
            </el-statistic>
          </el-col>
        </el-row>

        <el-card class="chart-card main-chart">
          <template #header>
            <span>交易趋势（近7天）</span>
          </template>
          <div ref="trendLineRef" class="chart-container large-chart"></div>
        </el-card>

        <el-row :gutter="20" class="bottom-cards">
          <el-col :span="12">
            <el-card class="chart-card">
              <template #header>
                <span>今日入库批次</span>
              </template>
              <el-statistic :value="dashboardData.todayInboundCount" class="center-statistic">
                <template #prefix>
                  <el-icon><Download /></el-icon>
                </template>
              </el-statistic>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card class="chart-card">
              <template #header>
                <span>今日发料批次</span>
              </template>
              <el-statistic :value="dashboardData.todayOutboundCount" class="center-statistic">
                <template #prefix>
                  <el-icon><Upload /></el-icon>
                </template>
              </el-statistic>
            </el-card>
          </el-col>
        </el-row>
      </el-col>

      <!-- 右侧 -->
      <el-col :span="6">
        <el-card class="chart-card">
          <template #header>
            <span>料号使用量TOP10</span>
          </template>
          <div ref="materialBarRef" class="chart-container"></div>
        </el-card>

        <el-card class="chart-card">
          <template #header>
            <span>实时预警列表</span>
          </template>
          <div class="alert-list">
            <div v-for="(alert, index) in alertList" :key="index" class="alert-item">
              <el-tag :type="getAlertTypeTag(alert.alertType)" size="small">
                {{ getAlertTypeText(alert.alertType) }}
              </el-tag>
              <span class="alert-message">{{ alert.alertMessage }}</span>
              <span class="alert-time">{{ formatTime(alert.alertTime) }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { Tickets, Box, Loading, Bell, Download, Upload } from '@element-plus/icons-vue'
import * as dashboardApi from '@/api/dashboard'
import * as alertApi from '@/api/alert'

// 全屏状态
const isFullscreen = ref(false)

// 当前时间
const currentTime = ref('')

// 看板数据
const dashboardData = reactive({
  totalBatchCount: 0,
  inStockCount: 0,
  inUseCount: 0,
  scrappedCount: 0,
  todayInboundCount: 0,
  todayOutboundCount: 0,
  todayReturnCount: 0,
  pendingAlertCount: 0,
  materialCount: 0,
  transactionCount: 0
})

// 预警列表
const alertList = ref<any[]>([])

// 图表引用
const inventoryPieRef = ref()
const alertBarRef = ref()
const trendLineRef = ref()
const materialBarRef = ref()

// 图表实例
let inventoryPieChart: any = null
let alertBarChart: any = null
let trendLineChart: any = null
let materialBarChart: any = null

// 定时器
let timer: any = null

// 切换全屏
const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
    isFullscreen.value = true
  } else {
    if (document.exitFullscreen) {
      document.exitFullscreen()
      isFullscreen.value = false
    }
  }
}

// 更新当前时间
const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 获取预警类型标签
const getAlertTypeTag = (type: string) => {
  const tagMap: Record<string, string> = {
    EXPIRE_SOON: 'danger',
    USAGE_HIGH: 'warning'
  }
  return tagMap[type] || ''
}

// 获取预警类型文本
const getAlertTypeText = (type: string) => {
  const textMap: Record<string, string> = {
    EXPIRE_SOON: '即将过期',
    USAGE_HIGH: '使用次数高'
  }
  return textMap[type] || type
}

// 格式化时间
const formatTime = (time: string) => {
  const date = new Date(time)
  return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

// 初始化库存分布饼图
const initInventoryPieChart = (data: any) => {
  if (!inventoryPieRef.value) return
  
  inventoryPieChart = echarts.init(inventoryPieRef.value)
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center'
    },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 20,
            fontWeight: 'bold'
          }
        },
        data: data.categories.map((cat: string, i: number) => ({
          value: data.values[i],
          name: cat
        }))
      }
    ]
  }
  
  inventoryPieChart.setOption(option)
}

// 初始化预警统计柱状图
const initAlertBarChart = (data: any) => {
  if (!alertBarRef.value) return
  
  alertBarChart = echarts.init(alertBarRef.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    xAxis: {
      type: 'category',
      data: data.types
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        data: data.counts,
        type: 'bar',
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#83bff6' },
            { offset: 0.5, color: '#188df0' },
            { offset: 1, color: '#188df0' }
          ])
        }
      }
    ]
  }
  
  alertBarChart.setOption(option)
}

// 初始化交易趋势折线图
const initTrendLineChart = (data: any) => {
  if (!trendLineRef.value) return
  
  trendLineChart = echarts.init(trendLineRef.value)
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['入库量', '出库量']
    },
    xAxis: {
      type: 'category',
      data: data.dates
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '入库量',
        type: 'line',
        smooth: true,
        data: data.inboundQuantities,
        itemStyle: {
          color: '#67C23A'
        }
      },
      {
        name: '出库量',
        type: 'line',
        smooth: true,
        data: data.outboundQuantities,
        itemStyle: {
          color: '#E6A23C'
        }
      }
    ]
  }
  
  trendLineChart.setOption(option)
}

// 初始化料号使用量柱状图
const initMaterialBarChart = (data: any) => {
  if (!materialBarRef.value) return
  
  materialBarChart = echarts.init(materialBarRef.value)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    xAxis: {
      type: 'value'
    },
    yAxis: {
      type: 'category',
      data: data.materials.reverse()
    },
    series: [
      {
        type: 'bar',
        data: data.usages.reverse(),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#83bff6' },
            { offset: 1, color: '#188df0' }
          ])
        }
      }
    ]
  }
  
  materialBarChart.setOption(option)
}

// 加载看板数据
const loadDashboardData = async () => {
  try {
    const res = await dashboardApi.getDashboardData()
    if (res.code === 200) {
      Object.assign(dashboardData, res.data)
    }
  } catch (error) {
    console.error('加载看板数据失败:', error)
  }
}

// 加载预警列表
const loadAlertList = async () => {
  try {
    const res = await alertApi.getPendingAlerts()
    if (res.code === 200) {
      alertList.value = res.data.slice(0, 5) // 只显示前5条
    }
  } catch (error) {
    console.error('加载预警列表失败:', error)
  }
}

// 刷新所有数据
const refreshAllData = async () => {
  await loadDashboardData()
  await loadAlertList()
  
  // 刷新图表数据
  const inventoryDist = await dashboardApi.getInventoryDistribution()
  if (inventoryDist.code === 200) {
    initInventoryPieChart(inventoryDist.data)
  }
  
  const trendData = await dashboardApi.getTransactionTrend(7)
  if (trendData.code === 200) {
    initTrendLineChart(trendData.data)
  }
  
  const alertStats = await dashboardApi.getAlertStatistics()
  if (alertStats.code === 200) {
    initAlertBarChart(alertStats.data)
  }
  
  const materialUsage = await dashboardApi.getMaterialUsage()
  if (materialUsage.code === 200) {
    initMaterialBarChart(materialUsage.data)
  }
}

// 窗口大小变化时重新调整图表
const handleResize = () => {
  inventoryPieChart?.resize()
  alertBarChart?.resize()
  trendLineChart?.resize()
  materialBarChart?.resize()
}

onMounted(async () => {
  updateTime()
  timer = setInterval(updateTime, 1000)
  
  await refreshAllData()
  
  // 每30秒自动刷新数据
  setInterval(refreshAllData, 30000)
  
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
  
  inventoryPieChart?.dispose()
  alertBarChart?.dispose()
  trendLineChart?.dispose()
  materialBarChart?.dispose()
  
  window.removeEventListener('resize', handleResize)
  
  if (document.fullscreenElement) {
    document.exitFullscreen()
  }
})
</script>

<style scoped>
.large-screen-dashboard {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
  padding: 20px;
  color: #fff;
}

.large-screen-dashboard.fullscreen {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9999;
  overflow-y: auto;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.dashboard-header h1 {
  margin: 0;
  font-size: 28px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 15px;
}

.current-time {
  font-size: 18px;
  font-weight: bold;
}

.dashboard-content {
  margin-top: 20px;
}

.chart-card {
  background: rgba(255, 255, 255, 0.9);
  margin-bottom: 20px;
  border-radius: 8px;
}

.chart-card :deep(.el-card__header) {
  background: rgba(102, 126, 234, 0.1);
  font-weight: bold;
  color: #333;
}

.chart-container {
  height: 250px;
}

.chart-container.large-chart {
  height: 350px;
}

.statistics-cards {
  margin-bottom: 20px;
}

.statistics-cards :deep(.el-statistic__head) {
  color: #333;
  font-size: 14px;
}

.statistics-cards :deep(.el-statistic__content) {
  color: #667eea;
  font-size: 28px;
  font-weight: bold;
}

.bottom-cards {
  margin-top: 20px;
}

.center-statistic {
  text-align: center;
}

.alert-list {
  max-height: 300px;
  overflow-y: auto;
}

.alert-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  border-bottom: 1px solid #eee;
}

.alert-item:last-child {
  border-bottom: none;
}

.alert-message {
  flex: 1;
  color: #666;
  font-size: 13px;
}

.alert-time {
  color: #999;
  font-size: 12px;
}
</style>
