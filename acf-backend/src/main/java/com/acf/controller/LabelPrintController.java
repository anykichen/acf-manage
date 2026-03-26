package com.acf.controller;

import com.acf.common.Result;
import com.acf.common.ResultCode;
import com.acf.service.LabelPrintService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 标签打印Controller
 */
@Tag(name = "标签打印", description = "标签打印相关接口")
@RestController
@RequestMapping("/api/label-print")
public class LabelPrintController {

    @Autowired
    private LabelPrintService labelPrintService;

    @Operation(summary = "生成ZPL指令")
    @PostMapping("/zpl")
    public Result<String> generateZPL(@RequestBody Map<String, Object> labelData) {
        try {
            String zpl = labelPrintService.generateZPL(labelData);
            return Result.success(zpl);
        } catch (Exception e) {
            return Result.error(ResultCode.FAILED, "生成ZPL失败: " + e.getMessage());
        }
    }

    @Operation(summary = "生成EPL指令")
    @PostMapping("/epl")
    public Result<String> generateEPL(@RequestBody Map<String, Object> labelData) {
        try {
            String epl = labelPrintService.generateEPL(labelData);
            return Result.success(epl);
        } catch (Exception e) {
            return Result.error(ResultCode.FAILED, "生成EPL失败: " + e.getMessage());
        }
    }

    @Operation(summary = "打印标签")
    @PostMapping("/print")
    public Result<Map<String, Object>> printLabel(
            @Parameter(description = "ZPL指令") @RequestBody String zpl,
            @Parameter(description = "打印机IP") @RequestParam(required = false) String printerIp,
            @Parameter(description = "打印机端口") @RequestParam(required = false) Integer printerPort) {

        Map<String, Object> result = labelPrintService.printLabel(zpl, printerIp, printerPort);
        if ((Boolean) result.get("success")) {
            return Result.success(result);
        } else {
            return Result.error(ResultCode.FAILED, (String) result.get("message"));
        }
    }

    @Operation(summary = "预览标签")
    @PostMapping("/preview")
    public Result<String> previewLabel(@RequestBody Map<String, Object> labelData) {
        try {
            String base64Image = labelPrintService.previewLabel(labelData);
            return Result.success(base64Image);
        } catch (Exception e) {
            return Result.error(ResultCode.FAILED, "预览失败: " + e.getMessage());
        }
    }
}
