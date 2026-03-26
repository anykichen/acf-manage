<template>
  <div class="alert-rule">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>预警规则管理</span>
          <el-button type="primary" @click="handleAdd">新增规则</el-button>
        </div>
      </template>

      <!-- 查询表单 -->
      <el-form :model="queryForm" inline>
        <el-form-item label="规则名称">
          <el-input v-model="queryForm.ruleName" placeholder="请输入规则名称" clearable />
        </el-form-item>
        <el-form-item label="规则类型">
          <el-select v-model="queryForm.ruleType" placeholder="请选择规则类型" clearable>
            <el-option label="过期预警" value="EXPIRE" />
            <el-option label="库存预警" value="STOCK" />
            <el-option label="使用次数预警" value="USAGE" />
          </el-select>
        </el-form-item>
        <el-form-item label="激活状态">
          <el-select v-model="queryForm.isActive" placeholder="请选择激活状态" clearable>
            <el-option label="已激活" :value="1" />
            <el-option label="未激活" :value="0" />
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
        <el-table-column prop="ruleName" label="规则名称" width="150" />
        <el-table-column prop="ruleType" label="规则类型" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.ruleType === 'EXPIRE'" type="danger">过期预警</el-tag>
            <el-tag v-else-if="row.ruleType === 'STOCK'" type="warning">库存预警</el-tag>
            <el-tag v-else-if="row.ruleType === 'USAGE'" type="info">使用次数预警</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="alertDaysBeforeExpire" label="过期预警天数" width="120">
          <template #default="{ row }">
            {{ row.alertDaysBeforeExpire || '-' }}天
          </template>
        </el-table-column>
        <el-table-column prop="minStockQuantity" label="最小库存" width="100">
          <template #default="{ row }">
            {{ row.minStockQuantity || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="maxUsageTimes" label="最大使用次数" width="120">
          <template #default="{ row }">
            {{ row.maxUsageTimes || '-' }}次
          </template>
        </el-table-column>
        <el-table-column prop="alertMethod" label="预警方式" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.alertMethod === 'SYSTEM'" type="success">系统</el-tag>
            <el-tag v-else-if="row.alertMethod === 'EMAIL'" type="info">邮件</el-tag>
            <el-tag v-else type="warning">短信</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isActive" label="激活状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isActive === 1 ? 'success' : 'danger'">
              {{ row.isActive === 1 ? '已激活' : '未激活' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button
              :type="row.isActive === 1 ? 'warning' : 'success'"
              link
              size="small"
              @click="handleToggleActive(row)"
            >
              {{ row.isActive === 1 ? '停用' : '激活' }}
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
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="130px">
        <el-form-item label="规则名称" prop="ruleName">
          <el-input v-model="form.ruleName" placeholder="请输入规则名称" />
        </el-form-item>
        <el-form-item label="规则类型" prop="ruleType">
          <el-select v-model="form.ruleType" placeholder="请选择规则类型" @change="handleRuleTypeChange">
            <el-option label="过期预警" value="EXPIRE" />
            <el-option label="库存预警" value="STOCK" />
            <el-option label="使用次数预警" value="USAGE" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.ruleType === 'EXPIRE'" label="过期预警天数" prop="alertDaysBeforeExpire">
          <el-input-number v-model="form.alertDaysBeforeExpire" :min="1" :max="30" />
          <span style="margin-left: 10px">天</span>
        </el-form-item>
        <el-form-item v-if="form.ruleType === 'STOCK'" label="最小库存数量" prop="minStockQuantity">
          <el-input-number v-model="form.minStockQuantity" :min="1" :precision="2" />
        </el-form-item>
        <el-form-item v-if="form.ruleType === 'USAGE'" label="最大使用次数" prop="maxUsageTimes">
          <el-input-number v-model="form.maxUsageTimes" :min="1" :max="10" />
          <span style="margin-left: 10px">次</span>
        </el-form-item>
        <el-form-item label="预警方式" prop="alertMethod">
          <el-select v-model="form.alertMethod" placeholder="请选择预警方式">
            <el-option label="系统提醒" value="SYSTEM" />
            <el-option label="邮件通知" value="EMAIL" />
            <el-option label="短信通知" value="SMS" />
          </el-select>
        </el-form-item>
        <el-form-item label="激活状态">
          <el-switch v-model="form.isActiveStatus" :active-value="1" :inactive-value="0" />
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
import { getAlertRuleList, addAlertRule, updateAlertRule, deleteAlertRule, activateAlertRule, deactivateAlertRule } from '@/api/alertRule'
import type { AlertRule } from '@/api/alertRule'

const formRef = ref<FormInstance>()
const dialogVisible = ref(false)
const isEdit = ref(false)
const dialogTitle = computed(() => isEdit.value ? '编辑预警规则' : '新增预警规则')

const queryForm = reactive({
  ruleName: '',
  ruleType: '',
  isActive: undefined as number | undefined
})

const form = reactive<AlertRule>({
  ruleName: '',
  ruleType: 'EXPIRE',
  alertDaysBeforeExpire: 7,
  minStockQuantity: 10,
  maxUsageTimes: 3,
  alertMethod: 'SYSTEM',
  isActive: 1
})

const formIsActiveStatus = computed({
  get: () => form.isActive === 1,
  set: (val: boolean) => {
    form.isActive = val ? 1 : 0
  }
})

const formRules: FormRules = {
  ruleName: [{ required: true, message: '请输入规则名称', trigger: 'blur' }],
  ruleType: [{ required: true, message: '请选择规则类型', trigger: 'change' }],
  alertMethod: [{ required: true, message: '请选择预警方式', trigger: 'change' }]
}

const tableData = ref<AlertRule[]>([])
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

const handleRuleTypeChange = (value: string) => {
  // 根据规则类型设置默认值
  if (value === 'EXPIRE') {
    form.alertDaysBeforeExpire = 7
    form.minStockQuantity = undefined
    form.maxUsageTimes = undefined
  } else if (value === 'STOCK') {
    form.alertDaysBeforeExpire = undefined
    form.minStockQuantity = 10
    form.maxUsageTimes = undefined
  } else if (value === 'USAGE') {
    form.alertDaysBeforeExpire = undefined
    form.minStockQuantity = undefined
    form.maxUsageTimes = 3
  }
}

const handleQuery = async () => {
  try {
    const data = await getAlertRuleList({
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
    ruleName: '',
    ruleType: '',
    isActive: undefined
  })
  pagination.current = 1
  handleQuery()
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, {
    ruleName: '',
    ruleType: 'EXPIRE',
    alertDaysBeforeExpire: 7,
    minStockQuantity: undefined,
    maxUsageTimes: undefined,
    alertMethod: 'SYSTEM',
    isActive: 1
  })
  dialogVisible.value = true
}

const handleEdit = (row: AlertRule) => {
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
          await updateAlertRule(form.id!, form)
        } else {
          await addAlertRule(form)
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

const handleDelete = (row: AlertRule) => {
  ElMessageBox.confirm('确定要删除该预警规则吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await deleteAlertRule(row.id!)
      ElMessage.success('删除成功')
      handleQuery()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

const handleToggleActive = async (row: AlertRule) => {
  try {
    if (row.isActive === 1) {
      await deactivateAlertRule(row.id!)
      ElMessage.success('规则已停用')
    } else {
      await activateAlertRule(row.id!)
      ElMessage.success('规则已激活')
    }
    handleQuery()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 初始加载
handleQuery()
</script>

<style scoped>
.alert-rule {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
