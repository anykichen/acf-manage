<template>
  <div class="lot-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>LOT号管理</span>
          <el-button type="primary" @click="handleGenerate">生成LOT号</el-button>
        </div>
      </template>

      <!-- 查询表单 -->
      <el-form :model="queryForm" inline>
        <el-form-item label="LOT号">
          <el-input v-model="queryForm.lotNumber" placeholder="请输入LOT号" clearable />
        </el-form-item>
        <el-form-item label="料号">
          <el-input v-model="queryForm.materialCode" placeholder="请输入料号" clearable />
        </el-form-item>
        <el-form-item label="储位">
          <el-input v-model="queryForm.warehouseLocation" placeholder="请输入储位" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.lotStatus" placeholder="请选择状态" clearable>
            <el-option label="在库" value="IN_STOCK" />
            <el-option label="使用中" value="IN_USE" />
            <el-option label="报废" value="SCRAPPED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" stripe border>
        <el-table-column type="selection" width="55" />
        <el-table-column prop="lotNumber" label="LOT号" width="140" />
        <el-table-column prop="materialCode" label="料号" width="150" />
        <el-table-column prop="inboundDate" label="入库时间" width="180" />
        <el-table-column prop="expireDate" label="过期时间" width="180" />
        <el-table-column prop="currentQuantity" label="当前数量" width="100" />
        <el-table-column prop="usageTimes" label="使用次数" width="90" />
        <el-table-column prop="warehouseLocation" label="储位" width="100" />
        <el-table-column prop="lotStatus" label="状态" width="90">
          <template #default="{ row }">
            <el-tag v-if="row.lotStatus === 'IN_STOCK'" type="success">在库</el-tag>
            <el-tag v-else-if="row.lotStatus === 'IN_USE'" type="warning">使用中</el-tag>
            <el-tag v-else type="danger">报废</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleOutbound(row)">发料</el-button>
            <el-button type="success" link size="small" @click="handleReturn(row)">退库</el-button>
            <el-button type="danger" link size="small" @click="handleScrap(row)">报废</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; justify-content: flex-end"
        @size-change="handleQuery"
        @current-change="handleQuery"
      />
    </el-card>

    <!-- 生成LOT号对话框 -->
    <el-dialog v-model="generateDialogVisible" title="生成LOT号" width="500px">
      <el-form :model="generateForm" :rules="generateFormRules" ref="generateFormRef" label-width="100px">
        <el-form-item label="料号" prop="materialCode">
          <el-select
            v-model="generateForm.materialCode"
            placeholder="请选择料号"
            filterable
            style="width: 100%"
          >
            <el-option label="ACF-CP9731SA-25" value="ACF-CP9731SA-25" />
            <el-option label="ACF-CP9731SA-30" value="ACF-CP9731SA-30" />
            <el-option label="ACF-CP9731SA-35" value="ACF-CP9731SA-35" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="generateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleGenerateSubmit">生成</el-button>
      </template>
    </el-dialog>

    <!-- 发料对话框 -->
    <el-dialog v-model="outboundDialogVisible" title="发料" width="500px">
      <el-form :model="outboundForm" :rules="outboundFormRules" ref="outboundFormRef" label-width="100px">
        <el-form-item label="LOT号">
          <el-input v-model="outboundForm.lotNumber" disabled />
        </el-form-item>
        <el-form-item label="发料数量" prop="quantity">
          <el-input-number v-model="outboundForm.quantity" :min="0.1" :max="currentLot?.currentQuantity" :precision="2" />
        </el-form-item>
        <el-form-item label="储位">
          <el-input v-model="outboundForm.warehouseLocation" placeholder="请输入储位" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="outboundDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleOutboundSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getLotList, generateLotNumber, outboundLot, returnLot, scrapLot } from '@/api/lot'
import type { Lot } from '@/api/lot'

const generateFormRef = ref<FormInstance>()
const outboundFormRef = ref<FormInstance>()

const generateDialogVisible = ref(false)
const outboundDialogVisible = ref(false)
const currentLot = ref<Lot>()

const queryForm = reactive({
  lotNumber: '',
  materialCode: '',
  warehouseLocation: '',
  lotStatus: ''
})

const generateForm = reactive({
  materialCode: ''
})

const outboundForm = reactive({
  lotNumber: '',
  quantity: 1,
  warehouseLocation: ''
})

const generateFormRules: FormRules = {
  materialCode: [{ required: true, message: '请选择料号', trigger: 'change' }]
}

const outboundFormRules: FormRules = {
  quantity: [{ required: true, message: '请输入发料数量', trigger: 'blur' }]
}

const tableData = ref<Lot[]>([])
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

const handleQuery = async () => {
  try {
    // TODO: 调用实际API
    // const data = await getLotList(queryForm)
    // tableData.value = data
    ElMessage.success('查询成功')
  } catch (error) {
    ElMessage.error('查询失败')
  }
}

const handleReset = () => {
  Object.assign(queryForm, {
    lotNumber: '',
    materialCode: '',
    warehouseLocation: '',
    lotStatus: ''
  })
  handleQuery()
}

const handleGenerate = () => {
  generateDialogVisible.value = true
}

const handleGenerateSubmit = async () => {
  if (!generateFormRef.value) return

  await generateFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // TODO: 调用生成LOT号接口
        // const lotNumber = await generateLotNumber(generateForm.materialCode)
        ElMessage.success('LOT号生成成功')
        generateDialogVisible.value = false
        handleQuery()
      } catch (error) {
        ElMessage.error('生成失败')
      }
    }
  })
}

const handleOutbound = (row: Lot) => {
  currentLot.value = row
  outboundForm.lotNumber = row.lotNumber
  outboundForm.warehouseLocation = row.warehouseLocation || ''
  outboundForm.quantity = 1
  outboundDialogVisible.value = true
}

const handleOutboundSubmit = async () => {
  if (!outboundFormRef.value) return

  await outboundFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // TODO: 调用发料接口
        // await outboundLot({
        //   lotNumber: outboundForm.lotNumber,
        //   quantity: outboundForm.quantity,
        //   warehouseLocation: outboundForm.warehouseLocation,
        //   operatorId: 1,
        //   operatorName: '测试用户'
        // })
        ElMessage.success('发料成功')
        outboundDialogVisible.value = false
        handleQuery()
      } catch (error) {
        ElMessage.error('发料失败')
      }
    }
  })
}

const handleReturn = (row: Lot) => {
  ElMessageBox.prompt('请输入退库数量', '退库', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /^\d+(\.\d{1,2})?$/,
    inputErrorMessage: '请输入有效的数量'
  }).then(async ({ value }) => {
    try {
      // TODO: 调用退库接口
      // await returnLot({
      //   lotNumber: row.lotNumber,
      //   quantity: parseFloat(value),
      //   warehouseLocation: row.warehouseLocation || '',
      //   operatorId: 1,
      //   operatorName: '测试用户'
      // })
      ElMessage.success('退库成功')
      handleQuery()
    } catch (error) {
      ElMessage.error('退库失败')
    }
  })
}

const handleScrap = (row: Lot) => {
  ElMessageBox.confirm('确定要报废该LOT吗？报废后将无法恢复。', '报废确认', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async () => {
    try {
      // TODO: 调用报废接口
      // await scrapLot({
      //   lotNumber: row.lotNumber,
      //   operatorId: 1,
      //   operatorName: '测试用户'
      // })
      ElMessage.success('报废成功')
      handleQuery()
    } catch (error) {
      ElMessage.error('报废失败')
    }
  })
}

// 初始加载
handleQuery()
</script>

<style scoped>
.lot-list {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
