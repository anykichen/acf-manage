<template>
  <div class="alert-management-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>预警管理</span>
          <div>
            <el-button type="primary" @click="handleTriggerCheck">触发预警检查</el-button>
            <el-button type="success" @click="checkExpire">检查过期预警</el-button>
            <el-button type="warning" @click="checkUsage">检查使用次数预警</el-button>
          </div>
        </div>
      </template>

      <!-- 统计卡片 -->
      <el-row :gutter="20" style="margin-bottom: 20px">
        <el-col :span="6">
          <el-statistic title="待处理预警" :value="statistics.pending" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="高危预警" :value="statistics.high" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="中危预警" :value="statistics.medium" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="已处理预警" :value="statistics.resolved" />
        </el-col>
      </el-row>

      <!-- 预警列表 -->
      <el-table
        :data="tableData"
        v-loading="loading"
        border
        stripe
      >
        <el-table-column prop="alertTime" label="预警时间" width="180" />
        <el-table-column prop="alertType" label="预警类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getAlertTypeTag(row.alertType)">{{ getAlertTypeText(row.alertType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="alertLevel" label="预警级别" width="100">
          <template #default="{ row }">
            <el-tag :type="getAlertLevelTag(row.alertLevel)">{{ getAlertLevelText(row.alertLevel) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lotNumber" label="LOT号" width="180" />
        <el-table-column prop="materialCode" label="料号" width="150" />
        <el-table-column prop="alertMessage" label="预警消息" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'PENDING' ? 'warning' : 'success'">
              {{ row.status === 'PENDING' ? '待处理' : '已处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 'PENDING'"
              type="primary"
              size="small"
              @click="handleResolve(row)"
            >
              标记已处理
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import * as alertApi from '@/api/alert'

// 统计数据
const statistics = reactive({
  pending: 0,
  high: 0,
  medium: 0,
  resolved: 0
})

// 表格数据
const loading = ref(false)
const tableData = ref<any[]>([])

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

// 获取预警级别标签
const getAlertLevelTag = (level: string) => {
  const tagMap: Record<string, string> = {
    HIGH: 'danger',
    MEDIUM: 'warning',
    LOW: 'info'
  }
  return tagMap[level] || ''
}

// 获取预警级别文本
const getAlertLevelText = (level: string) => {
  const textMap: Record<string, string> = {
    HIGH: '高危',
    MEDIUM: '中危',
    LOW: '低危'
  }
  return textMap[level] || level
}

// 获取待处理预警
const fetchPendingAlerts = async () => {
  loading.value = true
  try {
    const res = await alertApi.getPendingAlerts()
    if (res.code === 200) {
      tableData.value = res.data

      // 统计数据
      statistics.pending = tableData.value.filter((item: any) => item.status === 'PENDING').length
      statistics.high = tableData.value.filter((item: any) => item.alertLevel === 'HIGH').length
      statistics.medium = tableData.value.filter((item: any) => item.alertLevel === 'MEDIUM').length
      statistics.resolved = tableData.value.filter((item: any) => item.status === 'RESOLVED').length
    } else {
      ElMessage.error(res.message || '获取预警列表失败')
    }
  } catch (error) {
    console.error('获取预警列表失败:', error)
    ElMessage.error('获取预警列表失败')
  } finally {
    loading.value = false
  }
}

// 触发预警检查
const handleTriggerCheck = async () => {
  try {
    const res = await alertApi.triggerAlertCheck()
    if (res.code === 200) {
      ElMessage.success('预警检查已触发')
      fetchPendingAlerts()
    } else {
      ElMessage.error(res.message || '触发预警检查失败')
    }
  } catch (error) {
    console.error('触发预警检查失败:', error)
    ElMessage.error('触发预警检查失败')
  }
}

// 检查过期预警
const checkExpire = async () => {
  try {
    const res = await alertApi.checkExpireAlert()
    if (res.code === 200) {
      ElMessage.success(`检测到 ${res.data.length} 条过期预警`)
      fetchPendingAlerts()
    } else {
      ElMessage.error(res.message || '检查过期预警失败')
    }
  } catch (error) {
    console.error('检查过期预警失败:', error)
    ElMessage.error('检查过期预警失败')
  }
}

// 检查使用次数预警
const checkUsage = async () => {
  try {
    const res = await alertApi.checkUsageAlert()
    if (res.code === 200) {
      ElMessage.success(`检测到 ${res.data.length} 条使用次数预警`)
      fetchPendingAlerts()
    } else {
      ElMessage.error(res.message || '检查使用次数预警失败')
    }
  } catch (error) {
    console.error('检查使用次数预警失败:', error)
    ElMessage.error('检查使用次数预警失败')
  }
}

// 标记已处理
const handleResolve = async (row: any) => {
  try {
    const res = await alertApi.markAlertAsResolved(row.id)
    if (res.code === 200) {
      ElMessage.success('标记成功')
      fetchPendingAlerts()
    } else {
      ElMessage.error(res.message || '标记失败')
    }
  } catch (error) {
    console.error('标记失败:', error)
    ElMessage.error('标记失败')
  }
}

onMounted(() => {
  fetchPendingAlerts()
})
</script>

<style scoped>
.alert-management-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
