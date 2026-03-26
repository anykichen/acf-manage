<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <el-statistic title="总库存数" :value="statistics.totalQuantity">
            <template #suffix>卷</template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <el-statistic title="预警物料" :value="statistics.alertCount">
            <template #suffix>批</template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <el-statistic title="今日入库" :value="statistics.todayInbound">
            <template #suffix>卷</template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <el-statistic title="今日发料" :value="statistics.todayOutbound">
            <template #suffix>卷</template>
          </el-statistic>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card title="库存分布">
          <template #header>
            <span>库存分布（按料号）</span>
          </template>
          <div ref="stockChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>使用次数统计</span>
          </template>
          <div ref="usageChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>预警物料列表</span>
          </template>
          <el-table :data="alertLots" stripe>
            <el-table-column prop="lotNumber" label="LOT号" width="180" />
            <el-table-column prop="materialCode" label="料号" width="150" />
            <el-table-column prop="currentQuantity" label="库存数量" width="100" />
            <el-table-column prop="usageTimes" label="使用次数" width="100" />
            <el-table-column prop="expireDate" label="过期时间" width="180" />
            <el-table-column prop="warehouseLocation" label="储位" width="120" />
            <el-table-column label="预警类型">
              <template #default="{ row }">
                <el-tag v-if="getDaysUntilExpire(row) < 0" type="danger">已过期</el-tag>
                <el-tag v-else-if="getDaysUntilExpire(row) <= 7" type="warning">即将过期</el-tag>
                <el-tag v-else-if="row.usageTimes >= 3" type="warning">使用次数超限</el-tag>
                <el-tag v-else type="success">正常</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import dayjs from 'dayjs'
import { getAlertLots } from '@/api/lot'
import type { Lot } from '@/api/lot'

const stockChartRef = ref<HTMLDivElement>()
const usageChartRef = ref<HTMLDivElement>()
const alertLots = ref<Lot[]>([])

const statistics = ref({
  totalQuantity: 156,
  alertCount: 8,
  todayInbound: 12,
  todayOutbound: 8
})

const getDaysUntilExpire = (row: Lot) => {
  return dayjs(row.expireDate).diff(dayjs(), 'day')
}

const initStockChart = () => {
  if (!stockChartRef.value) return

  const chart = echarts.init(stockChartRef.value)
  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '库存分布',
        type: 'pie',
        radius: '60%',
        data: [
          { value: 52, name: 'ACF-CP9731SA-25' },
          { value: 38, name: 'ACF-CP9731SA-30' },
          { value: 35, name: 'ACF-CP9731SA-35' },
          { value: 18, name: 'ACF-DE6891SA-25' },
          { value: 13, name: 'ACF-DE6891SA-30' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
  chart.setOption(option)
}

const initUsageChart = () => {
  if (!usageChartRef.value) return

  const chart = echarts.init(usageChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['使用0次', '使用1次', '使用2次', '使用3次']
    },
    xAxis: {
      type: 'category',
      data: ['ACF-CP9731SA-25', 'ACF-CP9731SA-30', 'ACF-CP9731SA-35', 'ACF-DE6891SA-25', 'ACF-DE6891SA-30']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '使用0次',
        type: 'bar',
        stack: 'total',
        data: [12, 15, 10, 8, 5]
      },
      {
        name: '使用1次',
        type: 'bar',
        stack: 'total',
        data: [8, 10, 12, 5, 3]
      },
      {
        name: '使用2次',
        type: 'bar',
        stack: 'total',
        data: [10, 8, 8, 3, 3]
      },
      {
        name: '使用3次',
        type: 'bar',
        stack: 'total',
        data: [22, 5, 5, 2, 2]
      }
    ]
  }
  chart.setOption(option)
}

const loadAlertLots = async () => {
  try {
    // TODO: 调用实际API
    // const data = await getAlertLots()
    // alertLots.value = data
  } catch (error) {
    console.error('加载预警物料失败', error)
  }
}

onMounted(() => {
  initStockChart()
  initUsageChart()
  loadAlertLots()

  window.addEventListener('resize', () => {
    stockChartRef.value && echarts.getInstanceByDom(stockChartRef.value)?.resize()
    usageChartRef.value && echarts.getInstanceByDom(usageChartRef.value)?.resize()
  })
})
</script>

<style scoped>
.dashboard {
  width: 100%;
}

.stat-card {
  text-align: center;
}

.stat-card :deep(.el-card__body) {
  padding: 20px;
}
</style>
