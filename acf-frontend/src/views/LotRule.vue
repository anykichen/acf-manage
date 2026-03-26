<template>
  <div class="lot-rule-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>LOT号规则管理</span>
          <el-button type="primary" @click="handleAdd">新增规则</el-button>
        </div>
      </template>

      <!-- 查询表单 -->
      <el-form :model="queryForm" inline class="query-form">
        <el-form-item label="规则名称">
          <el-input v-model="queryForm.ruleName" placeholder="请输入规则名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
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
        @selection-change="handleSelectionChange"
        style="width: 100%"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="ruleName" label="规则名称" width="150" />
        <el-table-column prop="ruleDescription" label="规则描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="textDescriptionFormat" label="LOT号格式" width="250" show-overflow-tooltip />
        <el-table-column prop="sequenceStart" label="序列号起始" width="110" />
        <el-table-column prop="sequenceLength" label="序列号长度" width="110" />
        <el-table-column prop="isDefault" label="是否默认" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isDefault === 1 ? 'success' : 'info'">
              {{ row.isDefault === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button
              v-if="row.isDefault !== 1"
              type="success"
              link
              @click="handleSetDefault(row)"
            >
              设为默认
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 批量操作 -->
      <div class="batch-actions" v-if="selectedRows.length > 0">
        <el-button type="danger" @click="handleBatchDelete">批量删除</el-button>
        <span style="margin-left: 10px">已选择 {{ selectedRows.length }} 项</span>
      </div>

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

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="120px">
        <el-form-item label="规则名称" prop="ruleName">
          <el-input v-model="formData.ruleName" placeholder="请输入规则名称" />
        </el-form-item>
        <el-form-item label="规则描述" prop="ruleDescription">
          <el-input
            v-model="formData.ruleDescription"
            type="textarea"
            :rows="3"
            placeholder="请输入规则描述"
          />
        </el-form-item>
        <el-form-item label="LOT号格式" prop="textDescriptionFormat">
          <el-input
            v-model="formData.textDescriptionFormat"
            placeholder="例如: {materialCode}-{date}-{seq}"
          />
          <div class="form-tip">
            可用变量: {materialCode}, {year}, {month}, {day}, {date}, {seq}
          </div>
        </el-form-item>
        <el-form-item label="序列号起始" prop="sequenceStart">
          <el-input-number v-model="formData.sequenceStart" :min="0" />
        </el-form-item>
        <el-form-item label="序列号长度" prop="sequenceLength">
          <el-input-number v-model="formData.sequenceLength" :min="1" :max="10" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 预览LOT号对话框 -->
    <el-dialog v-model="previewVisible" title="LOT号预览" width="500px">
      <el-form label-width="100px">
        <el-form-item label="料号">
          <el-input v-model="previewMaterialCode" placeholder="请输入料号" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleGeneratePreview">生成</el-button>
        </el-form-item>
        <el-form-item label="LOT号">
          <el-input v-model="generatedLotNumber" readonly />
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { queryRulePage, createRule, updateRule, deleteRule, setDefaultRule, batchDeleteRules, generateLotNumber, type LotRule } from '@/api/lotRule'

// 查询表单
const queryForm = reactive({
  ruleName: '',
  status: undefined as number | undefined
})

// 表格数据
const tableData = ref<LotRule[]>([])
const loading = ref(false)
const selectedRows = ref<LotRule[]>([])

// 分页
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新增规则')
const formRef = ref()

// 表单数据
const formData = reactive({
  id: undefined as number | undefined,
  ruleName: '',
  ruleDescription: '',
  qrCodeFormat: '',
  textDescriptionFormat: '',
  sequenceStart: 1,
  sequenceLength: 6,
  status: 1
})

// 表单验证规则
const formRules = {
  ruleName: [{ required: true, message: '请输入规则名称', trigger: 'blur' }],
  textDescriptionFormat: [{ required: true, message: '请输入LOT号格式', trigger: 'blur' }],
  sequenceStart: [{ required: true, message: '请输入序列号起始', trigger: 'blur' }],
  sequenceLength: [{ required: true, message: '请输入序列号长度', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 预览
const previewVisible = ref(false)
const previewMaterialCode = ref('')
const generatedLotNumber = ref('')

// 查询数据
const fetchData = async () => {
  loading.value = true
  try {
    const { data } = await queryRulePage({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ruleName: queryForm.ruleName || undefined,
      status: queryForm.status
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
  queryForm.ruleName = ''
  queryForm.status = undefined
  handleQuery()
}

// 选择改变
const handleSelectionChange = (rows: LotRule[]) => {
  selectedRows.value = rows
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增规则'
  Object.assign(formData, {
    id: undefined,
    ruleName: '',
    ruleDescription: '',
    qrCodeFormat: '',
    textDescriptionFormat: '',
    sequenceStart: 1,
    sequenceLength: 6,
    status: 1
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row: LotRule) => {
  dialogTitle.value = '编辑规则'
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 对话框关闭
const handleDialogClose = () => {
  formRef.value?.resetFields()
}

// 提交表单
const handleSubmit = async () => {
  await formRef.value?.validate()
  try {
    if (formData.id) {
      await updateRule(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await createRule(formData)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error(formData.id ? '更新失败' : '新增失败')
  }
}

// 设为默认
const handleSetDefault = async (row: LotRule) => {
  try {
    await ElMessageBox.confirm('确定要将此规则设为默认吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await setDefaultRule(row.id)
    ElMessage.success('设置成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('设置失败')
    }
  }
}

// 删除
const handleDelete = async (row: LotRule) => {
  if (row.isDefault === 1) {
    ElMessage.warning('默认规则不能删除')
    return
  }
  try {
    await ElMessageBox.confirm('确定要删除此规则吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteRule(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 条规则吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const ids = selectedRows.value.map(item => item.id)
    await batchDeleteRules(ids)
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

// 生成预览
const handleGeneratePreview = async () => {
  if (!previewMaterialCode.value) {
    ElMessage.warning('请输入料号')
    return
  }
  try {
    const { data } = await generateLotNumber(previewMaterialCode.value)
    generatedLotNumber.value = data
  } catch (error) {
    ElMessage.error('生成失败')
  }
}

// 初始化
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.lot-rule-container {
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

.batch-actions {
  margin-top: 15px;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}
</style>
