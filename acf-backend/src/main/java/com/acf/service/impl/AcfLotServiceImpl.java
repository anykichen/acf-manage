package com.acf.service.impl;

import com.acf.common.BusinessException;
import com.acf.common.ResultCode;
import com.acf.entity.AcfLot;
import com.acf.entity.AcfMaterial;
import com.acf.entity.AcfTransaction;
import com.acf.mapper.AcfLotMapper;
import com.acf.service.AcfLotService;
import com.acf.service.AcfMaterialService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * LOT号Service实现类
 *
 * @author ACF Team
 */
@Service
@RequiredArgsConstructor
public class AcfLotServiceImpl extends ServiceImpl<AcfLotMapper, AcfLot> implements AcfLotService {

    private final AcfMaterialService materialService;

    private static final AtomicInteger seqCounter = new AtomicInteger(0);

    @Override
    public AcfLot getByLotNumber(String lotNumber) {
        return getOne(new LambdaQueryWrapper<AcfLot>()
                .eq(AcfLot::getLotNumber, lotNumber)
                .eq(AcfLot::getDeleted, 0));
    }

    @Override
    public String generateLotNumber(String materialCode) {
        // 检查料号是否存在
        AcfMaterial material = materialService.getByMaterialCode(materialCode);
        if (material == null) {
            throw new BusinessException(ResultCode.MATERIAL_NOT_EXISTS);
        }

        // 生成LOT号：LOT + 日期(YYYYMMDD) + 序号(4位)
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int seq = seqCounter.incrementAndGet() % 10000;
        return "LOT" + dateStr + String.format("%04d", seq);
    }

    @Override
    public List<AcfLot> getFifoRecommendations(String materialCode, int limit) {
        return baseMapper.getFifoLots(materialCode, limit);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void outbound(String lotNumber, BigDecimal quantity, String warehouseLocation,
                         Long operatorId, String operatorName) {
        // 检查LOT号是否存在
        AcfLot lot = getByLotNumber(lotNumber);
        if (lot == null) {
            throw new BusinessException(ResultCode.LOT_NOT_EXISTS);
        }

        // 检查库存是否充足
        if (lot.getCurrentQuantity().compareTo(quantity) < 0) {
            throw new BusinessException(ResultCode.STOCK_INSUFFICIENT);
        }

        // 检查使用次数
        AcfMaterial material = materialService.getByMaterialCode(lot.getMaterialCode());
        if (lot.getUsageTimes() >= material.getMaxUsageTimes()) {
            throw new BusinessException(ResultCode.USAGE_EXCEEDED);
        }

        // 扣减库存
        lot.setCurrentQuantity(lot.getCurrentQuantity().subtract(quantity));
        lot.setUsageTimes(lot.getUsageTimes() + 1);
        lot.setUpdateTime(LocalDateTime.now());
        updateById(lot);

        // 记录交易
        AcfTransaction transaction = new AcfTransaction();
        transaction.setTransactionNo(generateTransactionNo("OUTBOUND"));
        transaction.setLotNumber(lotNumber);
        transaction.setTransactionType("OUTBOUND");
        transaction.setQuantity(quantity);
        transaction.setWarehouseLocation(warehouseLocation);
        transaction.setOperatorId(operatorId);
        transaction.setOperatorName(operatorName);
        transaction.setWarmupStartTime(LocalDateTime.now());
        transaction.setTransactionTime(LocalDateTime.now());
        transaction.setCreateTime(LocalDateTime.now());
        transaction.setDeleted(0);
        // TODO: 保存transaction
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void returnStock(String lotNumber, BigDecimal quantity, String warehouseLocation,
                           Long operatorId, String operatorName) {
        // 检查LOT号是否存在
        AcfLot lot = getByLotNumber(lotNumber);
        if (lot == null) {
            throw new BusinessException(ResultCode.LOT_NOT_EXISTS);
        }

        // 恢复库存
        lot.setCurrentQuantity(lot.getCurrentQuantity().add(quantity));
        lot.setWarehouseLocation(warehouseLocation);
        lot.setUpdateTime(LocalDateTime.now());
        updateById(lot);

        // 记录交易
        AcfTransaction transaction = new AcfTransaction();
        transaction.setTransactionNo(generateTransactionNo("RETURN"));
        transaction.setLotNumber(lotNumber);
        transaction.setTransactionType("RETURN");
        transaction.setQuantity(quantity);
        transaction.setWarehouseLocation(warehouseLocation);
        transaction.setOperatorId(operatorId);
        transaction.setOperatorName(operatorName);
        transaction.setWarmupEndTime(LocalDateTime.now());
        transaction.setTransactionTime(LocalDateTime.now());
        transaction.setCreateTime(LocalDateTime.now());
        transaction.setDeleted(0);
        // TODO: 保存transaction
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void scrap(String lotNumber, String remark, Long operatorId, String operatorName) {
        // 检查LOT号是否存在
        AcfLot lot = getByLotNumber(lotNumber);
        if (lot == null) {
            throw new BusinessException(ResultCode.LOT_NOT_EXISTS);
        }

        // 更新LOT状态为报废
        lot.setLotStatus("SCRAPPED");
        lot.setRemark(remark);
        lot.setUpdateTime(LocalDateTime.now());
        updateById(lot);

        // 记录交易
        AcfTransaction transaction = new AcfTransaction();
        transaction.setTransactionNo(generateTransactionNo("SCRAPPED"));
        transaction.setLotNumber(lotNumber);
        transaction.setTransactionType("SCRAPPED");
        transaction.setQuantity(lot.getCurrentQuantity());
        transaction.setOperatorId(operatorId);
        transaction.setOperatorName(operatorName);
        transaction.setRemark(remark);
        transaction.setTransactionTime(LocalDateTime.now());
        transaction.setCreateTime(LocalDateTime.now());
        transaction.setDeleted(0);
        // TODO: 保存transaction
    }

    @Override
    public List<AcfLot> getAlertLots() {
        return baseMapper.getAlertLots();
    }

    private String generateTransactionNo(String type) {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return type + "_" + dateStr + String.format("%04d", (int) (Math.random() * 10000));
    }
}
