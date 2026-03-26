<template>
  <div class="outbound-record-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>出入库记录查询</span>
          <el-button type="primary" @click="exportData">导出Excel</el-button>
        </div>
      </template>

      <!-- 查询条件 -->
      <el-form :model="queryForm" :inline="true" class="query-form">
        <el-form-item label="LOT号">
          <el-input v-model="queryForm.lotNumber" placeholder="请输入LOT号" clearable />
        </el-form-item>
        <el-form-item label="交易类型">
          <el-select v-model="queryForm.transactionType" placeholder="请选择" clearable>
            <el-option label="入库" value="INBOUND" />
            <el-option label="发料" value="OUTBOUND" />
            <el-option label="退库" value="RETURN" />
            <el-option label="报废" value="SCRAP" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table
        :data="tableData"
        v-loading="loading"
        border
        stripe
        @expand-change="handleExpandChange"
      >
        <el-table-column type="expand">
          <template #default="{ row }">
            <div class="expand-content">
              <p><strong>操作人：</strong>{{ row.operator }}</p>
              <p><strong>备注：</strong>{{ row.remark || '-' }}</p>
              <p v-if="row.transactionType === 'OUTBOUND' || row.transactionType === 'RETURN'">
                <strong>回温开始时间：</strong>{{ row.warmupStartTime || '-' }}
              </p>
              <p v-if="row.transactionType === 'RETURN'">
                <strong>回温结束时间：</strong>{{ row.warmupEndTime || '-' }}
              </p>
              <p v-if="row.transactionType === 'RETURN' && row.warmupDuration">
                <strong>回温时长：</strong>{{ row.warmupDuration }} 分钟
              </p>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="transactionNumber" label="交易单号" width="180" />
        <el-table-column prop="lotNumber" label="LOT号" width="180" />
        <el-table-column prop="transactionType" label="交易类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.transactionType)">{{ getTypeText(row.transactionType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="100" />
        <el-table-column prop="beforeQuantity" label="变动前数量" width="120" />
        <el-table-column prop="afterQuantity" label="变动后数量" width="120" />
        <el-table-column prop="transactionTime" label="交易时间" width="180" />
        <el-table-column prop="result" label="结果" width="100">
          <template #default="{ row }">
            <el-tag :type="row.result === 'SUCCESS' ? 'success' : 'danger'">
              {{ row.result === 'SUCCESS' ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleQuery"
        @current-change="handleQuery"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import * as transactionApi from '@/api/transaction'

// 查询表单
const queryForm = reactive({
  lotNumber: '',
  transactionType: '',
  startDate: '',
  endDate: ''
})

const dateRange = ref<any[]>([])

// 表格数据
const loading = ref(false)
const tableData = ref<any[]>([])

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 获取类型标签
const getTypeTag = (type: string) => {
  const tagMap: Record<string, string> = {
    INBOUND: 'success',
    OUTBOUND: 'warning',
    RETURN: 'info',
    SCRAP: 'danger'
  }
  return tagMap[type] || ''
}

// 获取类型文本
const getTypeText = (type: string) => {
  const textMap: Record<string, string> = {
    INBOUND: '入库',
    OUTBOUND: '发料',
    RETURN: '退库',
    SCRAP: '报废'
  }
  return textMap[type] || type
}

// 查询
const handleQuery = async () => {
  loading.value = true
  try {
    if (dateRange.value && dateRange.value.length === 2) {
      queryForm.startDate = dateRange.value[0]
      queryForm.endDate = dateRange.value[1]
    } else {
      queryForm.startDate = ''
      queryForm.endDate = ''
    }

    const res = await transactionApi.getTransactionPage({
      ...queryForm,
      current: pagination.current,
      size: pagination.size
    })

    if (res.code === 200) {
      tableData.value = res.data.records
      pagination.total = res.data.total
    } else {
      ElMessage.error(res.message || '查询失败')
    }
  } catch (error) {
    console.error('查询失败:', error)
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

// 重置
const handleReset = () => {
  queryForm.lotNumber = ''
  queryForm.transactionType = ''
  queryForm.startDate = ''
  queryForm.endDate = ''
  dateRange.value = []
  pagination.current = 1
  handleQuery()
}

// 展开详情
const handleExpandChange = (row: any) => {
  console.log('展开详情:', row)
}

// 导出Excel
const exportData = async () => {
  try {
    ElMessage.info('正在导出...')
    const res = await transactionApi.exportTransactionExcel(queryForm)
    
    // 创建下载链接
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `出入库记录_${new Date().getTime()}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.outbound-record-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.query-form {
  margin-bottom: 20px;
}

.expand-content {
  padding: 20px;
  background-color: #f5f7fa;
}

.expand-content p {
  margin: 8px 0;
}
</style>
