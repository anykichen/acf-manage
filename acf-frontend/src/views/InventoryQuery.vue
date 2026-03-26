<template>
  <div class="inventory-query-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>库存查询</span>
          <el-button type="primary" @click="handleRefresh">刷新</el-button>
        </div>
      </template>

      <!-- 查询表单 -->
      <el-form :model="queryForm" inline class="query-form">
        <el-form-item label="料号">
          <el-input v-model="queryForm.materialCode" placeholder="请输入料号" clearable />
        </el-form-item>
        <el-form-item label="LOT号">
          <el-input v-model="queryForm.lotNumber" placeholder="请输入LOT号" clearable />
        </el-form-item>
        <el-form-item label="储位">
          <el-input v-model="queryForm.warehouseLocation" placeholder="请输入储位" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择" clearable>
            <el-option label="在库" value="IN_STOCK" />
            <el-option label="使用中" value="IN_USE" />
            <el-option label="已报废" value="SCRAPPED" />
          </el-select>
        </el-form-item>
        <el-form-item label="入库时间">
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

      <!-- 统计卡片 -->
      <el-row :gutter="16" class="statistics-row">
        <el-col :span="6">
          <el-statistic title="总库存批次" :value="statistics.totalLots" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="总库存数量" :value="statistics.totalQuantity" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="过期批次" :value="statistics.expiredLots">
            <template #suffix>
              <el-text type="danger" v-if="statistics.expiredLots > 0">⚠️</el-text>
            </template>
          </el-statistic>
        </el-col>
        <el-col :span="6">
          <el-statistic title="预警批次" :value="statistics.alertLots">
            <template #suffix>
              <el-text type="warning" v-if="statistics.alertLots > 0">🔔</el-text>
            </template>
          </el-statistic>
        </el-col>
      </el-row>

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
        <el-table-column prop="usageTimes" label="使用次数" width="100" align="right">
          <template #default="{ row }">
            <el-text :type="row.usageTimes >= 3 ? 'danger' : ''">
              {{ row.usageTimes }}
            </el-text>
          </template>
        </el-table-column>
        <el-table-column prop="warehouseLocation" label="储位" width="120" />
        <el-table-column prop="inStockTime" label="入库时间" width="180" />
        <el-table-column prop="daysInStock" label="在库天数" width="100" align="right" />
        <el-table-column prop="expireTime" label="有效期" width="180">
          <template #default="{ row }">
            <el-text :type="row.isExpired ? 'danger' : ''">
              {{ row.expireTime }}
            </el-text>
          </template>
        </el-table-column>
        <el-table-column prop="daysUntilExpire" label="剩余天数" width="100" align="right">
          <template #default="{ row }">
            <el-tag v-if="row.daysUntilExpire < 0" type="danger" size="small">已过期</el-tag>
            <el-tag v-else-if="row.daysUntilExpire < 7" type="warning" size="small">
              {{ row.daysUntilExpire }}天
            </el-tag>
            <span v-else>{{ row.daysUntilExpire }}天</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleOutbound(row)" v-if="row.status === 'IN_STOCK'">
              发料
            </el-button>
            <el-button type="success" link @click="handleReturn(row)" v-if="row.status === 'IN_USE'">
              退库
            </el-button>
            <el-button type="danger" link @click="handleScrap(row)">
              报废
            </el-button>
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

    <!-- 发料对话框 -->
    <el-dialog v-model="outboundVisible" title="发料" width="500px">
      <el-form :model="outboundForm" label-width="100px">
        <el-form-item label="LOT号">
          <el-input v-model="outboundForm.lotNumber" disabled />
        </el-form-item>
        <el-form-item label="料号">
          <el-input v-model="outboundForm.materialCode" disabled />
        </el-form-item>
        <el-form-item label="当前库存">
          <el-input-number v-model="outboundForm.currentQuantity" disabled style="width: 100%" />
        </el-form-item>
        <el-form-item label="发料数量">
          <el-input-number v-model="outboundForm.quantity" :min="1" :max="outboundForm.currentQuantity" style="width: 100%" />
        </el-form-item>
        <el-form-item label="发料位置">
          <el-input v-model="outboundForm.warehouseLocation" placeholder="请输入发料位置" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="outboundVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmOutbound">确定</el-button>
      </template>
    </el-dialog>

    <!-- 退库对话框 -->
    <el-dialog v-model="returnVisible" title="退库" width="500px">
      <el-form :model="returnForm" label-width="100px">
        <el-form-item label="LOT号">
          <el-input v-model="returnForm.lotNumber" disabled />
        </el-form-item>
        <el-form-item label="退库数量">
          <el-input-number v-model="returnForm.quantity" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="退库储位">
          <el-input v-model="returnForm.warehouseLocation" placeholder="请输入退库储位" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="returnVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmReturn">确定</el-button>
      </template>
    </el-dialog>

    <!-- 报废对话框 -->
    <el-dialog v-model="scrapVisible" title="报废" width="500px">
      <el-form :model="scrapForm" label-width="100px">
        <el-form-item label="LOT号">
          <el-input v-model="scrapForm.lotNumber" disabled />
        </el-form-item>
        <el-form-item label="报废原因">
          <el-input v-model="scrapForm.remark" type="textarea" :rows="3" placeholder="请输入报废原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="scrapVisible = false">取消</el-button>
        <el-button type="danger" @click="handleConfirmScrap">确定报废</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { queryInventory, queryInventoryStatistics, outbound, returnStock, scrap, type InventoryItem } from '@/api/inventory'

// 查询表单
const queryForm = reactive({
  materialCode: '',
  lotNumber: '',
  warehouseLocation: '',
  status: '',
  timeRange: [] as string[]
})

// 表格数据
const tableData = ref<InventoryItem[]>([])
const loading = ref(false)

// 统计数据
const statistics = ref({
  totalLots: 0,
  totalQuantity: 0,
  expiredLots: 0,
  alertLots: 0
})

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 发料对话框
const outboundVisible = ref(false)
const outboundForm = reactive({
  lotNumber: '',
  materialCode: '',
  currentQuantity: 0,
  quantity: 0,
  warehouseLocation: ''
})

// 退库对话框
const returnVisible = ref(false)
const returnForm = reactive({
  lotNumber: '',
  quantity: 0,
  warehouseLocation: ''
})

// 报废对话框
const scrapVisible = ref(false)
const scrapForm = reactive({
  lotNumber: '',
  remark: ''
})

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
    const { data } = await queryInventory({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      materialCode: queryForm.materialCode || undefined,
      lotNumber: queryForm.lotNumber || undefined,
      warehouseLocation: queryForm.warehouseLocation || undefined,
      status: queryForm.status || undefined,
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

// 查询统计数据
const fetchStatistics = async () => {
  try {
    const { data } = await queryInventoryStatistics()
    statistics.value = data
  } catch (error) {
    console.error('查询统计失败', error)
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
  queryForm.lotNumber = ''
  queryForm.warehouseLocation = ''
  queryForm.status = ''
  queryForm.timeRange = []
  handleQuery()
}

// 刷新按钮
const handleRefresh = () => {
  fetchData()
  fetchStatistics()
}

// 发料
const handleOutbound = (row: InventoryItem) => {
  outboundForm.lotNumber = row.lotNumber
  outboundForm.materialCode = row.materialCode
  outboundForm.currentQuantity = row.quantity
  outboundForm.quantity = row.quantity
  outboundForm.warehouseLocation = row.warehouseLocation || ''
  outboundVisible.value = true
}

// 确认发料
const handleConfirmOutbound = async () => {
  try {
    await outbound({
      lotNumber: outboundForm.lotNumber,
      quantity: outboundForm.quantity,
      warehouseLocation: outboundForm.warehouseLocation
    })
    ElMessage.success('发料成功')
    outboundVisible.value = false
    fetchData()
    fetchStatistics()
  } catch (error) {
    ElMessage.error('发料失败')
  }
}

// 退库
const handleReturn = (row: InventoryItem) => {
  returnForm.lotNumber = row.lotNumber
  returnForm.quantity = row.quantity
  returnForm.warehouseLocation = row.warehouseLocation || ''
  returnVisible.value = true
}

// 确认退库
const handleConfirmReturn = async () => {
  try {
    await returnStock({
      lotNumber: returnForm.lotNumber,
      quantity: returnForm.quantity,
      warehouseLocation: returnForm.warehouseLocation
    })
    ElMessage.success('退库成功')
    returnVisible.value = false
    fetchData()
    fetchStatistics()
  } catch (error) {
    ElMessage.error('退库失败')
  }
}

// 报废
const handleScrap = (row: InventoryItem) => {
  scrapForm.lotNumber = row.lotNumber
  scrapForm.remark = ''
  scrapVisible.value = true
}

// 确认报废
const handleConfirmScrap = async () => {
  if (!scrapForm.remark) {
    ElMessage.warning('请输入报废原因')
    return
  }
  
  try {
    await ElMessageBox.confirm('确定要报废此LOT号吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await scrap({
      lotNumber: scrapForm.lotNumber,
      remark: scrapForm.remark
    })
    ElMessage.success('报废成功')
    scrapVisible.value = false
    fetchData()
    fetchStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('报废失败')
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
  fetchStatistics()
})
</script>

<style scoped>
.inventory-query-container {
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

.statistics-row {
  margin-bottom: 20px;
}
</style>
