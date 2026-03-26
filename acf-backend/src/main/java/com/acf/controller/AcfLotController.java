package com.acf.controller;

import com.acf.common.Result;
import com.acf.entity.AcfLot;
import com.acf.service.AcfLotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * LOT号Controller
 *
 * @author ACF Team
 */
@Tag(name = "LOT号管理", description = "ACF LOT号管理接口")
@RestController
@RequestMapping("/lot")
@RequiredArgsConstructor
@Validated
public class AcfLotController {

    private final AcfLotService lotService;

    @Operation(summary = "查询LOT列表")
    @GetMapping("/list")
    public Result<List<AcfLot>> list(
            @RequestParam(required = false) String lotNumber,
            @RequestParam(required = false) String materialCode,
            @RequestParam(required = false) String lotStatus,
            @RequestParam(required = false) String warehouseLocation) {

        // TODO: 实现分页查询和条件筛选
        List<AcfLot> list = lotService.list();
        return Result.success(list);
    }

    @Operation(summary = "根据LOT号查询")
    @GetMapping("/{lotNumber}")
    public Result<AcfLot> getByLotNumber(@PathVariable String lotNumber) {
        AcfLot lot = lotService.getByLotNumber(lotNumber);
        return Result.success(lot);
    }

    @Operation(summary = "生成LOT号")
    @GetMapping("/generate")
    public Result<String> generateLotNumber(@RequestParam String materialCode) {
        String lotNumber = lotService.generateLotNumber(materialCode);
        return Result.success(lotNumber);
    }

    @Operation(summary = "FIFO推荐LOT号")
    @GetMapping("/fifo")
    public Result<List<AcfLot>> getFifoRecommendations(
            @RequestParam String materialCode,
            @RequestParam(defaultValue = "5") int limit) {
        List<AcfLot> lots = lotService.getFifoRecommendations(materialCode, limit);
        return Result.success(lots);
    }

    @Operation(summary = "发料（出库）")
    @PostMapping("/outbound")
    public Result<Void> outbound(
            @RequestParam String lotNumber,
            @RequestParam BigDecimal quantity,
            @RequestParam(required = false) String warehouseLocation,
            @RequestParam Long operatorId,
            @RequestParam String operatorName) {
        lotService.outbound(lotNumber, quantity, warehouseLocation, operatorId, operatorName);
        return Result.success("发料成功");
    }

    @Operation(summary = "退库")
    @PostMapping("/return")
    public Result<Void> returnStock(
            @RequestParam String lotNumber,
            @RequestParam BigDecimal quantity,
            @RequestParam String warehouseLocation,
            @RequestParam Long operatorId,
            @RequestParam String operatorName) {
        lotService.returnStock(lotNumber, quantity, warehouseLocation, operatorId, operatorName);
        return Result.success("退库成功");
    }

    @Operation(summary = "报废")
    @PostMapping("/scrap")
    public Result<Void> scrap(
            @RequestParam String lotNumber,
            @RequestParam(required = false) String remark,
            @RequestParam Long operatorId,
            @RequestParam String operatorName) {
        lotService.scrap(lotNumber, remark, operatorId, operatorName);
        return Result.success("报废成功");
    }

    @Operation(summary = "获取库存预警LOT")
    @GetMapping("/alert")
    public Result<List<AcfLot>> getAlertLots() {
        List<AcfLot> lots = lotService.getAlertLots();
        return Result.success(lots);
    }
}
