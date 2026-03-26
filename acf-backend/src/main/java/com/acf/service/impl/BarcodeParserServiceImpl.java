package com.acf.service.impl;

import com.acf.entity.AcfMaterial;
import com.acf.service.AcfMaterialService;
import com.acf.service.BarcodeParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 条码解析Service实现类
 */
@Service
public class BarcodeParserServiceImpl implements BarcodeParserService {

    @Autowired
    private AcfMaterialService materialService;

    @Override
    public Map<String, String> parseBarcode(String barcode, String rule) {
        Map<String, String> result = new HashMap<>();

        if (!StringUtils.hasText(rule)) {
            result.put("raw", barcode);
            return result;
        }

        try {
            Pattern pattern = Pattern.compile(rule);
            Matcher matcher = pattern.matcher(barcode);

            if (matcher.find()) {
                // 提取所有捕获组
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    String groupName = "group" + i;
                    result.put(groupName, matcher.group(i));
                }
                result.put("raw", barcode);
                result.put("matched", "true");
            } else {
                result.put("raw", barcode);
                result.put("matched", "false");
            }
        } catch (Exception e) {
            result.put("raw", barcode);
            result.put("error", e.getMessage());
        }

        return result;
    }

    @Override
    public String matchMaterialByBarcode(String barcode) {
        // 获取所有料号
        List<AcfMaterial> materials = materialService.list();

        // 遍历料号，尝试匹配条码规则
        for (AcfMaterial material : materials) {
            String rule = material.getBarcodeRule();
            if (StringUtils.hasText(rule)) {
                if (validateBarcode(barcode, rule)) {
                    return material.getMaterialCode();
                }
            }
        }

        return null;
    }

    @Override
    public boolean validateBarcode(String barcode, String rule) {
        if (!StringUtils.hasText(rule)) {
            return false;
        }

        try {
            Pattern pattern = Pattern.compile(rule);
            Matcher matcher = pattern.matcher(barcode);
            return matcher.find();
        } catch (Exception e) {
            return false;
        }
    }
}
