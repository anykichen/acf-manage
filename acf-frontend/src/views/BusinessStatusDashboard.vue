<template>
  <div class="business-status-dashboard">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>业务状态看板</span>
          <el-button type="primary" @click="refreshData">刷新数据</el-button>
        </div>
      </template>

      <!-- 业务概览 -->
      <el-row :gutter="20" class="business-overview">
        <el-col :span="8">
          <el-statistic title="今日入库量" :value="businessData.todayInboundQuantity">
            <template #prefix>
              <el-icon color="#67C23A"><Download /></el-icon>
            </template>
          </el-statistic>
        </el-col>
        <el-col :span="8">
          <el-statistic title="今日出库量" :value="businessData.todayOutboundQuantity">
            <template #prefix>
              <el-icon color="#E6A23C"><Upload /></el-icon>
            </template>
          </el-statistic>
        </el-col>
        <el-col :span="8">
          <el-statistic title="入库出库比" :value="businessData.inboundOutboundRatio">
            <template #suffix>
              <span style="font-size: 14px">倍</span>
            </template>
          </el-statistic>
        </el-col>
      </el-row>

      <!-- 统计卡片 -->
      <el-row :gutter="20" style="margin-top: 30px">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-card-content">
              <div class="stat-title">本周入库量</div>
              <div class="stat-value">{{ businessData.weekInboundQuantity }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-card-content">
              <div class="stat-title">本周出库量</div>
              <div class="stat-value">{{ businessData.weekOutboundQuantity }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-card-content">
              <div class="stat-title">本月入库量</div>
              <div class="stat-value">{{ businessData.monthInboundQuantity }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-card-content">
              <div class="stat-title">本月出库量</div>
              <div class="stat-value">{{ businessData.monthOutboundQuantity }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 业务繁忙指数 -->
      <el-row :gutter="20" style="margin-top: 30px">
        <el-col :span="12">
          <el-card class="busyness-card">
            <div class="busyness-content">
              <div class="busyness-title">业务繁忙指数</div>
              <div class="busyness-value" :class="busynessClass">
                {{ businessData.busynessLevel }}
              </div>
              <div class="busyness-desc">
                基于今日交易次数判断系统繁忙程度
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="trend-card">
            <template #header>
              <span>近7天交易趋势</span>
            </template>
            <div ref="trendChartRef" class="chart-container"></div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted, computed } from 'vue'
import * as echarts from 'echarts'
import { Download, Upload } from '@element-plus/icons-vue'
import * as dashboardApi from '@/api/dashboard'

// 业务数据
const businessData = reactive({
  todayInboundQuantity: 0,
  weekInboundQuantity: 0,
  monthInboundQuantity: 0,
  todayOutboundQuantity: 0,
  weekOutboundQuantity: 0,
  monthOutboundQuantity: 0,
  inboundOutboundRatio: '0',
  busynessLevel: '正常'
})

// 图表引用
const trendChartRef = ref()

// 图表实例
let trendChart: any = null

// 业务繁忙指数样式
const busynessClass = computed(() => {
  switch (businessData.busynessLevel) {
    case '繁忙':
      return 'busyness-busy'
    case '空闲':
      return 'busyness-idle'
    default:
      return 'busyness-normal'
  }
})

// 初始化趋势图表
const initTrendChart = (data: any) => {
  if (!trendChartRef.value) return
  
  trendChart = echarts.init(trendChartRef.value)
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['入库量', '出库量']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
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
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
            { offset: 1, color: 'rgba(103, 194, 58, 0.05)' }
          ])
        },
        itemStyle: {
          color: '#67C23A'
        }
      },
      {
        name: '出库量',
        type: 'line',
        smooth: true,
        data: data.outboundQuantities,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(230, 162, 60, 0.3)' },
            { offset: 1, color: 'rgba(230, 162, 60, 0.05)' }
          ])
        },
        itemStyle: {
          color: '#E6A23C'
        }
      }
    ]
  }
  
  trendChart.setOption(option)
}

// 刷新数据
const refreshData = async () => {
  // 加载业务状态数据
  const statusRes = await dashboardApi.getBusinessStatusData()
  if (statusRes.code === 200) {
    Object.assign(businessData, statusRes.data)
  }
  
  // 加载趋势数据
  const trendRes = await dashboardApi.getTransactionTrend(7)
  if (trendRes.code === 200) {
    initTrendChart(trendRes.data)
  }
}

// 窗口大小变化时重新调整图表
const handleResize = () => {
  trendChart?.resize()
}

onMounted(async () => {
  await refreshData()
  
  // 每60秒自动刷新
  setInterval(refreshData, 60000)
  
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  trendChart?.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.business-status-dashboard {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.business-overview {
  margin-bottom: 20px;
}

.business-overview :deep(.el-statistic__head) {
  color: #666;
  font-size: 16px;
  margin-bottom: 10px;
}

.business-overview :deep(.el-statistic__content) {
  font-size: 36px;
  font-weight: bold;
}

.stat-card {
  border-radius: 8px;
  border: none;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.stat-card-content {
  text-align: center;
  padding: 20px 0;
}

.stat-title {
  color: #666;
  font-size: 14px;
  margin-bottom: 10px;
}

.stat-value {
  color: #333;
  font-size: 32px;
  font-weight: bold;
}

.busyness-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: #fff;
  height: 280px;
}

.busyness-content {
  text-align: center;
  padding: 40px 20px;
}

.busyness-title {
  font-size: 18px;
  margin-bottom: 20px;
}

.busyness-value {
  font-size: 64px;
  font-weight: bold;
  margin-bottom: 15px;
}

.busyness-normal {
  color: #67C23A;
}

.busyness-busy {
  color: #F56C6C;
}

.busyness-idle {
  color: #909399;
}

.busyness-desc {
  font-size: 14px;
  opacity: 0.8;
}

.trend-card {
  height: 280px;
}

.chart-container {
  height: 200px;
}
</style>
