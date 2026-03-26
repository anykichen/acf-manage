<template>
  <div class="inbound-history-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>来料记录</span>
          <el-button type="primary" @click="handleRefresh">刷新</el-button>
        </div>
      </template>

      <!-- 查询表单 -->
      <el-form :model="queryForm" inline class="query-form">
        <el-form-item label="料号">
          <el-input v-model="queryForm.materialCode" placeholder="请输入料号" clearable />
        </el-form-item>
        <el-form-item label="来料时间">
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
        <el-table-column prop="lotNumber" label="LOT号" width="180" fixed />
        <el-table-column prop="materialCode" label="料号" width="150" />
        <el-table-column prop="quantity" label="数量" width="100" align="right" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="usageTimes" label="使用次数" width="100" align="right" />
        <el-table-column prop="inStockTime" label="入库时间" width="180" />
        <el-table-column prop="expireTime" label="有效期" width="180" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleViewLabel(row)">查看标签</el-button>
            <el-button type="success" link @click="handlePrintLabel(row)">打印标签</el-button>
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

    <!-- 标签预览对话框 -->
    <el-dialog v-model="previewVisible" title="标签预览" width="600px">
      <div class="label-preview">
        <img :src="previewImage" alt="标签预览" v-if="previewImage" />
        <el-empty description="加载中..." v-else />
      </div>
      <template #footer>
        <el-button @click="previewVisible = false">关闭</el-button>
        <el-button type="primary" @click="handlePrintCurrent" :disabled="!currentRow">
          打印
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { queryInboundPage, generateLabelData, type LabelData } from '@/api/inbound'
import { printLabel, generateZPL, previewLabel } from '@/api/labelPrint'

// 查询表单
const queryForm = reactive({
  materialCode: '',
  timeRange: [] as string[]
})

// 表格数据
const tableData = ref<any[]>([])
const loading = ref(false)

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 标签预览
const previewVisible = ref(false)
const previewImage = ref('')
const currentRow = ref<any>(null)

// 获取状态标签
const getStatusTag = (status: string) => {
  const map: Record<string, string> = {
    IN_STOCK: 'success',
    IN_USE: 'warning',
    SCRAPPED: 'danger'
  }
  return map[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    IN_STOCK: '在库',
    IN_USE: '使用中',
    SCRAPPED: '已报废'
  }
  return map[status] || status
}

// 查询数据
const fetchData = async () => {
  loading.value = true
  try {
    const [startTime, endTime] = queryForm.timeRange
    const { data } = await queryInboundPage({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      materialCode: queryForm.materialCode || undefined,
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
  queryForm.materialCode = ''
  queryForm.timeRange = []
  handleQuery()
}

// 刷新按钮
const handleRefresh = () => {
  fetchData()
}

// 查看标签
const handleViewLabel = async (row: any) => {
  currentRow.value = row
  previewVisible.value = true
  previewImage.value = ''

  try {
    const { data } = await generateLabelData(row.lotNumber)
    
    if (data.success && data.labelData) {
      const { data: base64Image } = await previewLabel(data.labelData)
      previewImage.value = base64Image
    }
  } catch (error) {
    ElMessage.error('标签预览失败')
    previewVisible.value = false
  }
}

// 打印标签
const handlePrintLabel = async (row: any) => {
  try {
    const { data } = await generateLabelData(row.lotNumber)
    
    if (data.success && data.labelData) {
      const { data: zpl } = await generateZPL(data.labelData)
      await printLabel(zpl)
      ElMessage.success('打印指令已发送')
    }
  } catch (error) {
    ElMessage.error('打印失败')
  }
}

// 打印当前预览
const handlePrintCurrent = async () => {
  if (!currentRow.value) return
  await handlePrintLabel(currentRow.value)
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
.inbound-history-container {
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

.label-preview {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
  background: #f5f7fa;
  border-radius: 4px;
}

.label-preview img {
  max-width: 100%;
  max-height: 600px;
}
</style>
