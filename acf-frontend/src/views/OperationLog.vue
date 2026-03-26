<template>
  <div class="operation-log-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>操作日志</span>
          <el-button type="primary" @click="handleRefresh">刷新</el-button>
        </div>
      </template>

      <!-- 查询表单 -->
      <el-form :model="queryForm" inline class="query-form">
        <el-form-item label="LOT号">
          <el-input v-model="queryForm.lotNumber" placeholder="请输入LOT号" clearable />
        </el-form-item>
        <el-form-item label="操作类型">
          <el-select v-model="queryForm.operationType" placeholder="请选择" clearable>
            <el-option label="入库" value="INBOUND" />
            <el-option label="发料" value="OUTBOUND" />
            <el-option label="退库" value="RETURN" />
            <el-option label="报废" value="SCRAP" />
            <el-option label="生成LOT号" value="GENERATE_LOT" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作时间">
          <el-date-picker
            v-model="queryForm.timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
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
        border
        stripe
        v-loading="loading"
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="operationType" label="操作类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getOperationTypeTag(row.operationType)">
              {{ getOperationTypeText(row.operationType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lotNumber" label="LOT号" width="180" />
        <el-table-column prop="materialCode" label="料号" width="150" />
        <el-table-column prop="operator" label="操作人" width="120" />
        <el-table-column prop="operationTime" label="操作时间" width="180" />
        <el-table-column prop="operationDetail" label="操作详情" min-width="200" show-overflow-tooltip />
        <el-table-column prop="operationResult" label="操作结果" width="100">
          <template #default="{ row }">
            <el-tag :type="row.operationResult === '成功' ? 'success' : 'danger'">
              {{ row.operationResult }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ipAddress" label="IP地址" width="140" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleViewDetail(row)">详情</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="操作日志详情" width="600px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="ID">{{ currentLog.id }}</el-descriptions-item>
        <el-descriptions-item label="操作类型">
          <el-tag :type="getOperationTypeTag(currentLog.operationType)">
            {{ getOperationTypeText(currentLog.operationType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="LOT号">{{ currentLog.lotNumber }}</el-descriptions-item>
        <el-descriptions-item label="料号">{{ currentLog.materialCode }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ currentLog.operator }}</el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ currentLog.operationTime }}</el-descriptions-item>
        <el-descriptions-item label="操作详情">{{ currentLog.operationDetail }}</el-descriptions-item>
        <el-descriptions-item label="操作结果">
          <el-tag :type="currentLog.operationResult === '成功' ? 'success' : 'danger'">
            {{ currentLog.operationResult }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ currentLog.ipAddress }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentLog.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { queryLogPage, deleteLog, type OperationLog } from '@/api/operationLog'

// 查询表单
const queryForm = reactive({
  lotNumber: '',
  operationType: '',
  timeRange: [] as string[]
})

// 表格数据
const tableData = ref<OperationLog[]>([])
const loading = ref(false)

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 详情对话框
const detailVisible = ref(false)
const currentLog = ref<OperationLog>({} as OperationLog)

// 获取操作类型标签
const getOperationTypeTag = (type: string) => {
  const map: Record<string, string> = {
    INBOUND: 'success',
    OUTBOUND: 'warning',
    RETURN: 'info',
    SCRAP: 'danger',
    GENERATE_LOT: 'primary'
  }
  return map[type] || ''
}

// 获取操作类型文本
const getOperationTypeText = (type: string) => {
  const map: Record<string, string> = {
    INBOUND: '入库',
    OUTBOUND: '发料',
    RETURN: '退库',
    SCRAP: '报废',
    GENERATE_LOT: '生成LOT号'
  }
  return map[type] || type
}

// 查询数据
const fetchData = async () => {
  loading.value = true
  try {
    const [startTime, endTime] = queryForm.timeRange
    const { data } = await queryLogPage({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      lotNumber: queryForm.lotNumber || undefined,
      operationType: queryForm.operationType || undefined,
      startTime,
      endTime
    })
    tableData.value = data.records || []
    pagination.total = data.total || 0
  } catch (error) {
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

// 查询按钮
const handleQuery = () => {
  pagination.pageNum = 1
  fetchData()
}

// 重置按钮
const handleReset = () => {
  queryForm.lotNumber = ''
  queryForm.operationType = ''
  queryForm.timeRange = []
  handleQuery()
}

// 刷新按钮
const handleRefresh = () => {
  fetchData()
}

// 查看详情
const handleViewDetail = (row: OperationLog) => {
  currentLog.value = row
  detailVisible.value = true
}

// 删除
const handleDelete = async (row: OperationLog) => {
  try {
    await ElMessageBox.confirm('确定要删除这条操作日志吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteLog(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 分页改变
const handleSizeChange = (val: number) => {
  pagination.pageSize = val
  fetchData()
}

const handleCurrentChange = (val: number) => {
  pagination.pageNum = val
  fetchData()
}

// 初始化
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.operation-log-container {
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
</style>
