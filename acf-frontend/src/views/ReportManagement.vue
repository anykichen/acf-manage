<template>
  <div class="report-management-container">
    <el-row :gutter="20">
      <!-- 库存统计报表 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>库存统计报表</span>
              <el-button type="primary" @click="handleExportInventory">导出Excel</el-button>
            </div>
          </template>

          <el-descriptions :column="2" border>
            <el-descriptions-item label="总库存批次">{{ inventoryReport.totalBatchCount }}</el-descriptions-item>
            <el-descriptions-item label="总库存数量">{{ inventoryReport.totalQuantity }}</el-descriptions-item>
            <el-descriptions-item label="过期批次">
              <el-tag type="danger">{{ inventoryReport.expiredBatchCount }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="预警批次(7天内)">
              <el-tag type="warning">{{ inventoryReport.warningBatchCount }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="今日入库批次" :span="2">
              <el-tag type="success">{{ inventoryReport.todayInboundCount }}</el-tag>
            </el-descriptions-item>
          </el-descriptions>

          <div v-if="inventoryReport.materialQuantityMap" style="margin-top: 20px">
            <h4>各料号库存</h4>
            <el-table :data="materialQuantityData" border stripe>
              <el-table-column prop="materialCode" label="料号" />
              <el-table-column prop="quantity" label="库存数量" />
            </el-table>
          </div>
        </el-card>
      </el-col>

      <!-- 交易统计报表 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>交易统计报表</span>
              <el-button type="primary" @click="handleExportTransaction">导出Excel</el-button>
            </div>
          </template>

          <el-form :inline="true">
            <el-form-item label="日期范围">
              <el-date-picker
                v-model="dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="YYYY-MM-DD"
                @change="handleQueryTransaction"
              />
            </el-form-item>
          </el-form>

          <el-descriptions :column="2" border v-if="transactionReport.totalTransactions">
            <el-descriptions-item label="总交易次数">{{ transactionReport.totalTransactions }}</el-descriptions-item>
            <el-descriptions-item label="总交易数量">{{ transactionReport.totalQuantity }}</el-descriptions-item>
            <el-descriptions-item label="失败交易次数">
              <el-tag type="danger">{{ transactionReport.failedTransactions }}</el-tag>
            </el-descriptions-item>
          </el-descriptions>

          <div v-if="transactionReport.typeCountMap" style="margin-top: 20px">
            <h4>各类型交易次数</h4>
            <el-table :data="transactionTypeData" border stripe>
              <el-table-column prop="type" label="交易类型">
                <template #default="{ row }">
                  <el-tag>{{ row.type }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="count" label="次数" />
            </el-table>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import * as reportApi from '@/api/report'

// 日期范围
const dateRange = ref<any[]>([])

// 库存报表
const inventoryReport = reactive({
  totalBatchCount: 0,
  totalQuantity: 0,
  expiredBatchCount: 0,
  warningBatchCount: 0,
  todayInboundCount: 0,
  materialQuantityMap: {} as Record<string, number>
})

// 交易报表
const transactionReport = reactive({
  totalTransactions: 0,
  totalQuantity: 0,
  failedTransactions: 0,
  typeCountMap: {} as Record<string, number>,
  typeQuantityMap: {} as Record<number, number>
})

// 料号库存数据
const materialQuantityData = computed(() => {
  return Object.entries(inventoryReport.materialQuantityMap).map(([materialCode, quantity]) => ({
    materialCode,
    quantity
  }))
})

// 交易类型数据
const transactionTypeData = computed(() => {
  return Object.entries(transactionReport.typeCountMap).map(([type, count]) => ({
    type,
    count
  }))
})

// 获取库存报表
const fetchInventoryReport = async () => {
  try {
    const res = await reportApi.getInventoryReport()
    if (res.code === 200) {
      Object.assign(inventoryReport, res.data)
    } else {
      ElMessage.error(res.message || '获取库存报表失败')
    }
  } catch (error) {
    console.error('获取库存报表失败:', error)
    ElMessage.error('获取库存报表失败')
  }
}

// 查询交易报表
const handleQueryTransaction = async () => {
  if (!dateRange.value || dateRange.value.length !== 2) {
    ElMessage.warning('请选择日期范围')
    return
  }

  try {
    const res = await reportApi.getTransactionReport({
      startDate: dateRange.value[0],
      endDate: dateRange.value[1]
    })
    if (res.code === 200) {
      Object.assign(transactionReport, res.data)
    } else {
      ElMessage.error(res.message || '获取交易报表失败')
    }
  } catch (error) {
    console.error('获取交易报表失败:', error)
    ElMessage.error('获取交易报表失败')
  }
}

// 导出库存报表
const handleExportInventory = async () => {
  try {
    ElMessage.info('正在导出...')
    const res = await reportApi.exportInventoryReport()
    
    // 创建下载链接
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `库存报表_${new Date().getTime()}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

// 导出交易报表
const handleExportTransaction = async () => {
  if (!dateRange.value || dateRange.value.length !== 2) {
    ElMessage.warning('请选择日期范围')
    return
  }

  try {
    ElMessage.info('正在导出...')
    const res = await reportApi.exportTransactionReport({
      startDate: dateRange.value[0],
      endDate: dateRange.value[1]
    })
    
    // 创建下载链接
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `交易报表_${dateRange.value[0]}_${dateRange.value[1]}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

onMounted(() => {
  fetchInventoryReport()
  
  // 默认查询本月数据
  const now = new Date()
  const firstDay = new Date(now.getFullYear(), now.getMonth(), 1)
  dateRange.value = [
    firstDay.toISOString().split('T')[0],
    now.toISOString().split('T')[0]
  ]
  handleQueryTransaction()
})
</script>

<style scoped>
.report-management-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

h4 {
  margin: 20px 0 10px 0;
  color: #333;
}
</style>
