package com.acf;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * ACF管控系统启动类
 *
 * @author ACF Team
 * @since 2026-03-26
 */
@SpringBootApplication
@MapperScan("com.acf.mapper")
@EnableCaching
@EnableScheduling
public class AcfApplication {

    public static void main(String[] args) {
        SpringApplication.run(AcfApplication.class, args);
        System.out.println("""

                ========================================
                  ACF管控系统启动成功！
                  后端地址: http://localhost:8080/api
                  API文档: http://localhost:8080/api/swagger-ui.html
                ========================================
                """);
    }
}
