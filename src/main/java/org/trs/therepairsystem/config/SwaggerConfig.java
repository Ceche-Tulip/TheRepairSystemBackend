package org.trs.therepairsystem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI 配置类
 * 配置API文档的基本信息、安全认证和通用响应格式
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("在线报修系统 API")
                        .description("基于Spring Boot的在线报修系统后端API文档\n\n" +
                                "## 统一响应格式\n" +
                                "所有API响应都遵循统一格式：\n" +
                                "```json\n" +
                                "{\n" +
                                "  \"code\": 200,\n" +
                                "  \"message\": \"success\",\n" +
                                "  \"data\": {},\n" +
                                "  \"timestamp\": 1701594000000\n" +
                                "}\n" +
                                "```\n\n" +
                                "## HTTP状态码说明\n" +
                                "- 200: 查询/更新成功\n" +
                                "- 201: 创建成功\n" +
                                "- 204: 删除成功\n" +
                                "- 400: 请求参数错误\n" +
                                "- 401: 未认证或Token过期\n" +
                                "- 403: 权限不足\n" +
                                "- 404: 资源不存在\n" +
                                "- 409: 资源冲突（如重复创建）\n" +
                                "- 500: 服务器内部错误")
                        .version("v2.0")
                        .contact(new Contact()
                                .name("开发团队")
                                .email("dev@therepairsystem.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("请在此处输入JWT token，格式: Bearer {token}"))
                        // 添加通用响应格式示例
                        .addResponses("UnauthorizedError", new ApiResponse()
                                .description("认证失败")
                                .content(new Content()
                                        .addMediaType("application/json", new MediaType()
                                                .schema(new Schema<>()
                                                        .example("{\n" +
                                                                "  \"code\": 401,\n" +
                                                                "  \"message\": \"Token无效或已过期\",\n" +
                                                                "  \"data\": null,\n" +
                                                                "  \"timestamp\": 1701594000000\n" +
                                                                "}"))))))
                .addSecurityItem(new SecurityRequirement()
                        .addList("Bearer Authentication"));
    }
}