package org.trs.therepairsystem.common.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.trs.therepairsystem.common.dto.ApiResponse;

/**
 * 全局响应处理器
 * 自动包装所有Controller返回的响应为统一格式
 */
@RestControllerAdvice
public class GlobalResponseAdvice implements ResponseBodyAdvice<Object> {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 如果已经是ApiResponse类型，则不需要再包装
        if (returnType.getParameterType().equals(ApiResponse.class)) {
            return false;
        }
        
        // 如果是ResponseEntity类型，也不需要包装
        if (returnType.getParameterType().equals(ResponseEntity.class)) {
            return false;
        }
        
        // 排除Spring Doc OpenAPI相关的控制器
        String declaringClassName = returnType.getDeclaringClass().getName();
        if (declaringClassName.contains("springdoc") || 
            declaringClassName.contains("OpenApiResource") ||
            declaringClassName.contains("SwaggerResource")) {
            return false;
        }
        
        // 其他情况都需要包装
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                 Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                 ServerHttpRequest request, ServerHttpResponse response) {
        
        // 排除Spring Doc OpenAPI相关的端点
        String path = request.getURI().getPath();
        if (path.contains("/v3/api-docs") || 
            path.contains("/swagger") || 
            path.contains("/webjars/springdoc")) {
            return body;
        }
        
        // 如果已经是ApiResponse类型，直接返回
        if (body instanceof ApiResponse) {
            return body;
        }
        
        // 如果是字符串类型（通常是String类型的返回值），需要特殊处理
        if (returnType.getParameterType().equals(String.class)) {
            try {
                return objectMapper.writeValueAsString(ApiResponse.success(body));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("序列化响应失败", e);
            }
        }
        
        // 其他类型直接包装
        return ApiResponse.success(body);
    }
}