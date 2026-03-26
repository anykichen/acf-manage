<template>
  <div class="label-template">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>标签模板管理</span>
          <el-button type="primary" @click="handleAdd">新增模板</el-button>
        </div>
      </template>

      <!-- 查询表单 -->
      <el-form :model="queryForm" inline>
        <el-form-item label="模板名称">
          <el-input v-model="queryForm.templateName" placeholder="请输入模板名称" clearable />
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
        <el-table-column prop="templateName" label="模板名称" width="150" />
        <el-table-column prop="templateCode" label="模板编码" width="150" />
        <el-table-column label="尺寸" width="120">
          <template #default="{ row }">
            {{ row.width }}cm × {{ row.height }}cm
          </template>
        </el-table-column>
        <el-table-column prop="printDriver" label="打印驱动" width="120" />
        <el-table-column prop="isDefault" label="默认模板" width="100">
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
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button
              v-if="row.isDefault !== 1"
              type="success"
              link
              size="small"
              @click="handleSetDefault(row)"
            >
              设为默认
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
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="120px">
        <el-form-item label="模板名称" prop="templateName">
          <el-input v-model="form.templateName" placeholder="请输入模板名称" />
        </el-form-item>
        <el-form-item label="模板编码" prop="templateCode">
          <el-input v-model="form.templateCode" placeholder="请输入模板编码" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="宽度(cm)" prop="width">
          <el-input-number v-model="form.width" :min="1" :max="20" :precision="2" />
        </el-form-item>
        <el-form-item label="高度(cm)" prop="height">
          <el-input-number v-model="form.height" :min="1" :max="30" :precision="2" />
        </el-form-item>
        <el-form-item label="打印驱动">
          <el-select v-model="form.printDriver" placeholder="请选择打印驱动" clearable>
            <el-option label="ZPL" value="ZPL" />
            <el-option label="EPL" value="EPL" />
            <el-option label="CPCL" value="CPCL" />
          </el-select>
        </el-form-item>
        <el-form-item label="字段配置">
          <el-input
            v-model="form.fieldsConfig"
            type="textarea"
            :rows="4"
            placeholder='请输入字段配置JSON，如：{"fields":["lot_number","model_spec"],"layout":"standard"}'
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="form.statusStatus" :active-value="1" :inactive-value="0" />
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
import { getTemplateList, addTemplate, updateTemplate, deleteTemplate, setDefaultTemplate } from '@/api/labelTemplate'
import type { LabelTemplate } from '@/api/labelTemplate'

const formRef = ref<FormInstance>()
const dialogVisible = ref(false)
const isEdit = ref(false)
const dialogTitle = computed(() => isEdit.value ? '编辑标签模板' : '新增标签模板')

const queryForm = reactive({
  templateName: '',
  status: undefined as number | undefined
})

const form = reactive<LabelTemplate>({
  templateName: '',
  templateCode: '',
  width: 5,
  height: 10,
  fieldsConfig: '{"fields":["lot_number","model_spec","shelf_life","usage1","usage2","usage3"],"layout":"standard"}',
  printDriver: 'ZPL',
  isDefault: 0,
  status: 1
})

const formStatusStatus = computed({
  get: () => form.status === 1,
  set: (val: boolean) => {
    form.status = val ? 1 : 0
  }
})

const formRules: FormRules = {
  templateName: [{ required: true, message: '请输入模板名称', trigger: 'blur' }],
  templateCode: [{ required: true, message: '请输入模板编码', trigger: 'blur' }],
  width: [{ required: true, message: '请输入宽度', trigger: 'blur' }],
  height: [{ required: true, message: '请输入高度', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const tableData = ref<LabelTemplate[]>([])
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

const handleQuery = async () => {
  try {
    const data = await getTemplateList({
      ...queryForm,
      current: pagination.current,
      pageSize: pagination.pageSize
    })
    tableData.value = data.records
    pagination.total = data.total
  } catch (error) {
    ElMessage.error('查询失败')
  }
}

const handleReset = () => {
  Object.assign(queryForm, {
    templateName: '',
    status: undefined
  })
  pagination.current = 1
  handleQuery()
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, {
    templateName: '',
    templateCode: '',
    width: 5,
    height: 10,
    fieldsConfig: '{"fields":["lot_number","model_spec","shelf_life","usage1","usage2","usage3"],"layout":"standard"}',
    printDriver: 'ZPL',
    isDefault: 0,
    status: 1
  })
  dialogVisible.value = true
}

const handleEdit = (row: LabelTemplate) => {
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
          await updateTemplate(form.id!, form)
        } else {
          await addTemplate(form)
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

const handleDelete = (row: LabelTemplate) => {
  ElMessageBox.confirm('确定要删除该标签模板吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await deleteTemplate(row.id!)
      ElMessage.success('删除成功')
      handleQuery()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

const handleSetDefault = (row: LabelTemplate) => {
  ElMessageBox.confirm(`确定要将"${row.templateName}"设置为默认模板吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await setDefaultTemplate(row.id!)
      ElMessage.success('设置成功')
      handleQuery()
    } catch (error) {
      ElMessage.error('设置失败')
    }
  })
}

// 初始加载
handleQuery()
</script>

<style scoped>
.label-template {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
