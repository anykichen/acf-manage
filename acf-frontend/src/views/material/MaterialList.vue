<template>
  <div class="material-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>料号管理</span>
          <el-button type="primary" @click="handleAdd">新增料号</el-button>
        </div>
      </template>

      <!-- 查询表单 -->
      <el-form :model="queryForm" inline>
        <el-form-item label="料号">
          <el-input v-model="queryForm.materialCode" placeholder="请输入料号" clearable />
        </el-form-item>
        <el-form-item label="料号名称">
          <el-input v-model="queryForm.materialName" placeholder="请输入料号名称" clearable />
        </el-form-item>
        <el-form-item label="厂商">
          <el-input v-model="queryForm.manufacturer" placeholder="请输入厂商" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
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
      <el-table :data="tableData" stripe border>
        <el-table-column type="selection" width="55" />
        <el-table-column prop="materialCode" label="料号" width="150" />
        <el-table-column prop="materialName" label="料号名称" width="200" />
        <el-table-column prop="manufacturer" label="厂商" width="100" />
        <el-table-column prop="model" label="型号" width="120" />
        <el-table-column prop="unit" label="单位" width="60" />
        <el-table-column prop="shelfLifeMonths" label="保存期限" width="90">
          <template #default="{ row }">
            {{ row.shelfLifeMonths }}个月
          </template>
        </el-table-column>
        <el-table-column prop="maxUsageTimes" label="最大使用次数" width="110" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button
              :type="row.status === 1 ? 'warning' : 'success'"
              link
              size="small"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '停用' : '启用' }}
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="120px">
        <el-form-item label="料号" prop="materialCode">
          <el-input v-model="form.materialCode" placeholder="请输入料号" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="料号名称" prop="materialName">
          <el-input v-model="form.materialName" placeholder="请输入料号名称" />
        </el-form-item>
        <el-form-item label="物料描述">
          <el-input v-model="form.materialDesc" type="textarea" :rows="2" placeholder="请输入物料描述" />
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="form.unit" placeholder="请输入单位（如：卷）" />
        </el-form-item>
        <el-form-item label="条码解析规则">
          <el-input v-model="form.barcodeRule" placeholder="请输入正则表达式" />
        </el-form-item>
        <el-form-item label="厂商">
          <el-input v-model="form.manufacturer" placeholder="请输入厂商" />
        </el-form-item>
        <el-form-item label="型号">
          <el-input v-model="form.model" placeholder="请输入型号" />
        </el-form-item>
        <el-form-item label="保存期限(月)" prop="shelfLifeMonths">
          <el-input-number v-model="form.shelfLifeMonths" :min="1" :max="24" />
        </el-form-item>
        <el-form-item label="最大使用次数" prop="maxUsageTimes">
          <el-input-number v-model="form.maxUsageTimes" :min="1" :max="10" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getMaterialList, addMaterial, updateMaterial, deleteMaterial, updateMaterialStatus } from '@/api/material'
import type { Material } from '@/api/material'

const formRef = ref<FormInstance>()
const dialogVisible = ref(false)
const isEdit = ref(false)
const dialogTitle = computed(() => isEdit.value ? '编辑料号' : '新增料号')

const queryForm = reactive({
  materialCode: '',
  materialName: '',
  manufacturer: '',
  status: undefined as number | undefined
})

const form = reactive<Material>({
  materialCode: '',
  materialName: '',
  unit: '卷',
  shelfLifeMonths: 6,
  maxUsageTimes: 3,
  status: 1
})

const formRules: FormRules = {
  materialCode: [{ required: true, message: '请输入料号', trigger: 'blur' }],
  materialName: [{ required: true, message: '请输入料号名称', trigger: 'blur' }],
  unit: [{ required: true, message: '请输入单位', trigger: 'blur' }],
  shelfLifeMonths: [{ required: true, message: '请输入保存期限', trigger: 'blur' }],
  maxUsageTimes: [{ required: true, message: '请输入最大使用次数', trigger: 'blur' }]
}

const tableData = ref<Material[]>([])
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

const handleQuery = async () => {
  try {
    // TODO: 调用实际API
    // const data = await getMaterialList(queryForm)
    // tableData.value = data
    ElMessage.success('查询成功')
  } catch (error) {
    ElMessage.error('查询失败')
  }
}

const handleReset = () => {
  Object.assign(queryForm, {
    materialCode: '',
    materialName: '',
    manufacturer: '',
    status: undefined
  })
  handleQuery()
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, {
    materialCode: '',
    materialName: '',
    unit: '卷',
    shelfLifeMonths: 6,
    maxUsageTimes: 3,
    status: 1
  })
  dialogVisible.value = true
}

const handleEdit = (row: Material) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEdit.value) {
          // TODO: 调用更新接口
          // await updateMaterial(form.id!, form)
        } else {
          // TODO: 调用新增接口
          // await addMaterial(form)
        }
        ElMessage.success(isEdit.value ? '更新成功' : '新增成功')
        dialogVisible.value = false
        handleQuery()
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }
  })
}

const handleDelete = (row: Material) => {
  ElMessageBox.confirm('确定要删除该料号吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      // TODO: 调用删除接口
      // await deleteMaterial(row.id!)
      ElMessage.success('删除成功')
      handleQuery()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

const handleToggleStatus = async (row: Material) => {
  try {
    // TODO: 调用状态更新接口
    // await updateMaterialStatus(row.id!, row.status === 1 ? 0 : 1)
    ElMessage.success('状态更新成功')
    handleQuery()
  } catch (error) {
    ElMessage.error('状态更新失败')
  }
}

// 初始加载
handleQuery()
</script>

<style scoped>
.material-list {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
