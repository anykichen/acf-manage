<template>
  <div class="inbound-register-container">
    <el-row :gutter="20">
      <!-- 左侧：扫码识别 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>来料扫码识别</span>
            </div>
          </template>

          <el-form :model="scanForm" label-width="100px">
            <el-form-item label="扫描条码">
              <el-input
                v-model="scanForm.barcode"
                placeholder="请扫描或输入条码"
                clearable
                @keyup.enter="handleScan"
                ref="barcodeInput"
              >
                <template #append>
                  <el-button @click="handleScan">识别</el-button>
                </template>
              </el-input>
              <div class="scan-tip">
                <el-icon><Monitor /></el-icon>
                <span>支持扫码枪输入，按回车键自动识别</span>
              </div>
            </el-form-item>
          </el-form>

          <!-- 识别结果 -->
          <div v-if="scanResult" class="scan-result">
            <el-divider>识别结果</el-divider>
            <el-descriptions :column="1" border>
              <el-descriptions-item label="料号">{{ scanResult.materialCode }}</el-descriptions-item>
              <el-descriptions-item label="料号名称">{{ scanResult.materialName }}</el-descriptions-item>
              <el-descriptions-item label="物料描述">{{ scanResult.materialDesc }}</el-descriptions-item>
              <el-descriptions-item label="单位">{{ scanResult.unit }}</el-descriptions-item>
              <el-descriptions-item label="厂商">{{ scanResult.manufacturer }}</el-descriptions-item>
              <el-descriptions-item label="型号">{{ scanResult.model }}</el-descriptions-item>
              <el-descriptions-item label="保存期限">{{ scanResult.shelfLifeMonths }} 个月</el-descriptions-item>
              <el-descriptions-item label="最大使用次数">{{ scanResult.maxUsageTimes }} 次</el-descriptions-item>
            </el-descriptions>
          </div>

          <div v-if="scanError" class="scan-error">
            <el-alert :title="scanError" type="error" :closable="false" />
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：确认来料 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>确认来料</span>
            </div>
          </template>

          <el-form :model="confirmForm" :rules="confirmRules" ref="confirmFormRef" label-width="100px">
            <el-form-item label="料号" prop="materialCode">
              <el-input v-model="confirmForm.materialCode" placeholder="自动填充" disabled />
            </el-form-item>
            <el-form-item label="数量" prop="quantity">
              <el-input-number
                v-model="confirmForm.quantity"
                :min="1"
                :max="999999"
                placeholder="请输入数量"
                style="width: 100%"
              />
            </el-form-item>
            <el-form-item label="操作人">
              <el-input v-model="confirmForm.operator" placeholder="请输入操作人" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleConfirm" :loading="confirming" :disabled="!scanResult">
                确认并生成LOT号
              </el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>

          <!-- LOT号生成结果 -->
          <div v-if="lotNumber" class="lot-result">
            <el-divider>LOT号生成成功</el-divider>
            <el-result
              icon="success"
              title="LOT号生成成功"
              :sub-title="lotNumber"
            >
              <template #extra>
                <el-button type="primary" @click="handlePrintLabel">打印标签</el-button>
                <el-button @click="handleViewLabel">预览标签</el-button>
              </template>
            </el-result>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 标签预览对话框 -->
    <el-dialog v-model="previewVisible" title="标签预览" width="600px">
      <div class="label-preview">
        <img :src="previewImage" alt="标签预览" v-if="previewImage" />
        <el-empty description="加载中..." v-else />
      </div>
      <template #footer>
        <el-button @click="previewVisible = false">关闭</el-button>
        <el-button type="primary" @click="handlePrintLabel" :disabled="!lotNumber">
          打印
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Monitor } from '@element-plus/icons-vue'
import { scanBarcode, confirmInbound, generateLabelData, type InboundData, type LabelData } from '@/api/inbound'
import { generateZPL, printLabel, previewLabel } from '@/api/labelPrint'

// 扫码表单
const scanForm = reactive({
  barcode: ''
})

// 识别结果
const scanResult = ref<InboundData | null>(null)
const scanError = ref('')
const barcodeInput = ref()

// 确认表单
const confirmForm = reactive({
  materialCode: '',
  quantity: 1,
  operator: ''
})

const confirmRules = {
  materialCode: [{ required: true, message: '料号不能为空', trigger: 'blur' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }]
}

const confirmFormRef = ref()
const confirming = ref(false)

// LOT号
const lotNumber = ref('')

// 标签预览
const previewVisible = ref(false)
const previewImage = ref('')
const labelData = ref<LabelData | null>(null)

// 扫码识别
const handleScan = async () => {
  if (!scanForm.barcode) {
    ElMessage.warning('请先扫描或输入条码')
    return
  }

  try {
    const { data } = await scanBarcode(scanForm.barcode)
    
    if (data.success) {
      scanResult.value = data
      scanError.value = ''
      
      // 自动填充确认表单
      confirmForm.materialCode = data.materialCode || ''
      
      ElMessage.success('识别成功')
    } else {
      scanResult.value = null
      scanError.value = data.message || '识别失败'
    }
  } catch (error) {
    scanResult.value = null
    scanError.value = '扫码识别失败'
    ElMessage.error('扫码识别失败')
  }
}

// 确认来料
const handleConfirm = async () => {
  await confirmFormRef.value?.validate()
  
  confirming.value = true
  try {
    const { data } = await confirmInbound({
      materialCode: confirmForm.materialCode,
      quantity: confirmForm.quantity,
      operator: confirmForm.operator || 'system'
    })
    
    lotNumber.value = data
    ElMessage.success('LOT号生成成功: ' + data)
  } catch (error) {
    ElMessage.error('LOT号生成失败')
  } finally {
    confirming.value = false
  }
}

// 查看标签
const handleViewLabel = async () => {
  if (!lotNumber.value) {
    ElMessage.warning('请先生成LOT号')
    return
  }

  previewVisible.value = true
  previewImage.value = ''

  try {
    // 获取标签数据
    const { data: labelResponse } = await generateLabelData(lotNumber.value)
    
    if (labelResponse.success && labelResponse.labelData) {
      labelData.value = labelResponse.labelData
      
      // 生成预览图
      const { data: base64Image } = await previewLabel(labelData.value)
      previewImage.value = base64Image
    }
  } catch (error) {
    ElMessage.error('标签预览失败')
    previewVisible.value = false
  }
}

// 打印标签
const handlePrintLabel = async () => {
  if (!lotNumber.value) {
    ElMessage.warning('请先生成LOT号')
    return
  }

  try {
    // 获取标签数据
    const { data: labelResponse } = await generateLabelData(lotNumber.value)
    
    if (labelResponse.success && labelResponse.labelData) {
      const labelDataToPrint = labelResponse.labelData
      
      // 生成ZPL指令
      const { data: zpl } = await generateZPL(labelDataToPrint)
      
      // 打印（模拟）
      await printLabel(zpl)
      
      ElMessage.success('打印指令已发送')
    }
  } catch (error) {
    ElMessage.error('打印失败')
  }
}

// 重置
const handleReset = () => {
  scanForm.barcode = ''
  scanResult.value = null
  scanError.value = ''
  
  confirmForm.materialCode = ''
  confirmForm.quantity = 1
  confirmForm.operator = ''
  
  lotNumber.value = ''
  labelData.value = null
  
  nextTick(() => {
    barcodeInput.value?.focus()
  })
}

// 聚焦输入框
nextTick(() => {
  barcodeInput.value?.focus()
})
</script>

<style scoped>
.inbound-register-container {
  padding: 20px;
}

.card-header {
  font-size: 16px;
  font-weight: bold;
}

.scan-tip {
  display: flex;
  align-items: center;
  gap: 5px;
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
}

.scan-result {
  margin-top: 20px;
}

.scan-error {
  margin-top: 20px;
}

.lot-result {
  margin-top: 20px;
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
