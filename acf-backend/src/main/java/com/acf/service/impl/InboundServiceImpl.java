package com.acf.service.impl;

import com.acf.entity.AcfLabelTemplate;
import com.acf.entity.AcfLot;
import com.acf.entity.AcfMaterial;
import com.acf.service.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 来料管理Service实现类
 */
@Service
public class InboundServiceImpl implements InboundService {

    @Autowired
    private BarcodeParserService barcodeParserService;

    @Autowired
    private AcfMaterialService materialService;

    @Autowired
    private AcfLotService lotService;

    @Autowired
    private AcfLabelTemplateService labelTemplateService;

    @Autowired
    private AcfOperationLogService operationLogService;

    @Autowired
    private AcfLotRuleService lotRuleService;

    @Autowired
    private QrCodeService qrCodeService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> scanBarcode(String barcode) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 匹配料号
            String materialCode = barcodeParserService.matchMaterialByBarcode(barcode);
            
            if (materialCode == null) {
                result.put("success", false);
                result.put("message", "未找到匹配的料号");
                return result;
            }

            // 查询料号信息
            AcfMaterial material = materialService.queryByMaterialCode(materialCode);
            if (material == null) {
                result.put("success", false);
                result.put("message", "料号信息不存在");
                return result;
            }

            // 解析条码详情
            String rule = material.getBarcodeRule();
            Map<String, String> parseResult = barcodeParserService.parseBarcode(barcode, rule);

            result.put("success", true);
            result.put("materialCode", material.getMaterialCode());
            result.put("materialName", material.getMaterialName());
            result.put("materialDesc", material.getMaterialDesc());
            result.put("unit", material.getUnit());
            result.put("manufacturer", material.getManufacturer());
            result.put("model", material.getModel());
            result.put("shelfLifeMonths", material.getShelfLifeMonths());
            result.put("maxUsageTimes", material.getMaxUsageTimes());
            result.put("barcode", barcode);
            result.put("parseResult", parseResult);

            return result;

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "条码解析失败: " + e.getMessage());
            return result;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String confirmInbound(String materialCode, Integer quantity, String operator) {
        try {
            // 查询料号
            AcfMaterial material = materialService.queryByMaterialCode(materialCode);
            if (material == null) {
                throw new RuntimeException("料号不存在");
            }

            // 生成LOT号
            String lotNumber = lotRuleService.generateLotNumber(materialCode);

            // 创建LOT记录
            AcfLot lot = new AcfLot();
            lot.setLotNumber(lotNumber);
            lot.setMaterialCode(materialCode);
            lot.setQuantity(quantity);
            lot.setStatus("IN_STOCK");
            lot.setUsageTimes(0);
            lot.setInStockTime(LocalDateTime.now());

            // 计算有效期（默认为料号的保存期限）
            LocalDateTime expireTime = LocalDateTime.now().plusMonths(material.getShelfLifeMonths());
            lot.setExpireTime(expireTime);

            lot.setCreatedBy(operator);
            lot.setCreateTime(LocalDateTime.now());

            lotService.save(lot);

            // 记录操作日志
            operationLogService.logOperation("INBOUND", lotNumber, materialCode,
                    operator, "来料入库，数量: " + quantity, "成功", null);

            return lotNumber;

        } catch (Exception e) {
            throw new RuntimeException("来料确认失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> generateLabelData(String lotNumber) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 查询LOT信息
            AcfLot lot = lotService.queryByLotNumber(lotNumber);
            if (lot == null) {
                result.put("success", false);
                result.put("message", "LOT号不存在");
                return result;
            }

            // 查询料号信息
            AcfMaterial material = materialService.queryByMaterialCode(lot.getMaterialCode());
            if (material == null) {
                result.put("success", false);
                result.put("message", "料号信息不存在");
                return result;
            }

            // 获取默认标签模板
            AcfLabelTemplate template = labelTemplateService.getDefaultTemplate();
            if (template == null) {
                result.put("success", false);
                result.put("message", "未配置标签模板");
                return result;
            }

            // 生成二维码
            String qrCode = qrCodeService.generateQrCodeBase64(lotNumber, 200, 200);

            // 格式化时间（曼谷时间）
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String inStockDate = lot.getInStockTime().format(formatter);
            String expireDate = lot.getExpireTime().format(formatter);

            // 解析模板字段配置
            Map<String, Object> fieldsConfig = objectMapper.readValue(
                    template.getFieldsConfig(),
                    new TypeReference<Map<String, Object>>() {}
            );

            // 构建标签数据
            Map<String, Object> labelData = new HashMap<>();
            labelData.put("lotNumber", lotNumber);
            labelData.put("qrCode", qrCode);
            labelData.put("materialCode", material.getMaterialCode());
            labelData.put("materialName", material.getMaterialName());
            labelData.put("materialDesc", material.getMaterialDesc());
            labelData.put("model", material.getModel());
            labelData.put("manufacturer", material.getManufacturer());
            labelData.put("inStockDate", inStockDate);
            labelData.put("expireDate", expireDate);
            labelData.put("shelfLifeMonths", material.getShelfLifeMonths());
            labelData.put("maxUsageTimes", material.getMaxUsageTimes());
            labelData.put("templateName", template.getTemplateName());
            labelData.put("templateCode", template.getTemplateCode());
            labelData.put("fieldsConfig", fieldsConfig);

            result.put("success", true);
            result.put("labelData", labelData);

            return result;

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "生成标签数据失败: " + e.getMessage());
            return result;
        }
    }

    @Override
    public Page<Map<String, Object>> queryInboundPage(int pageNum, int pageSize, String materialCode,
                                                       LocalDateTime startTime, LocalDateTime endTime) {
        Page<AcfLot> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<AcfLot> wrapper = new LambdaQueryWrapper<>();

        // 只查询入库状态的记录
        wrapper.eq(AcfLot::getStatus, "IN_STOCK");

        if (materialCode != null && !materialCode.isEmpty()) {
            wrapper.like(AcfLot::getMaterialCode, materialCode);
        }
        if (startTime != null) {
            wrapper.ge(AcfLot::getCreateTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(AcfLot::getCreateTime, endTime);
        }

        wrapper.orderByDesc(AcfLot::getCreateTime);

        Page<AcfLot> lotPage = lotService.page(page, wrapper);

        // 转换为Map
        List<Map<String, Object>> records = new ArrayList<>();
        for (AcfLot lot : lotPage.getRecords()) {
            Map<String, Object> map = new HashMap<>();
            map.put("lotNumber", lot.getLotNumber());
            map.put("materialCode", lot.getMaterialCode());
            map.put("quantity", lot.getQuantity());
            map.put("status", lot.getStatus());
            map.put("usageTimes", lot.getUsageTimes());
            map.put("inStockTime", lot.getInStockTime());
            map.put("expireTime", lot.getExpireTime());
            map.put("createTime", lot.getCreateTime());
            records.add(map);
        }

        Page<Map<String, Object>> resultPage = new Page<>(pageNum, pageSize);
        resultPage.setRecords(records);
        resultPage.setTotal(lotPage.getTotal());
        
        return resultPage;
    }
}
