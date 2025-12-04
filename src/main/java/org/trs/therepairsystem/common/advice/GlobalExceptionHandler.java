package org.trs.therepairsystem.common.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.trs.therepairsystem.common.dto.ApiResponse;

import jakarta.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 * 处理各种异常并返回统一格式的错误响应
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理数据校验异常（请求体校验失败）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        logger.warn("数据校验失败: {}", errors);
        return ResponseEntity.badRequest().body(ApiResponse.badRequest("请求参数校验失败"));
    }

    /**
     * 处理数据校验异常（表单数据绑定失败）
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleBindException(BindException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        logger.warn("表单数据绑定失败: {}", errors);
        return ResponseEntity.badRequest().body(ApiResponse.badRequest("请求参数校验失败"));
    }

    /**
     * 处理实体不存在异常
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleEntityNotFoundException(EntityNotFoundException ex) {
        logger.warn("实体不存在: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.notFound(ex.getMessage()));
    }

    /**
     * 处理数据库约束异常（如唯一性约束冲突）
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        logger.warn("数据库约束异常: {}", ex.getMessage());
        String message = "数据操作失败，可能存在重复数据";
        String errorMsg = ex.getMessage() != null ? ex.getMessage().toLowerCase() : "";
        
        // 更精确的约束字段识别
        if (errorMsg.contains("username") || errorMsg.contains("users.username") || errorMsg.contains("idx_username")) {
            message = "用户名已存在";
        } else if (errorMsg.contains("phone") || errorMsg.contains("users.phone") || errorMsg.contains("idx_phone")) {
            message = "手机号已存在";
        } else if (errorMsg.contains("role_name") || errorMsg.contains("roles.role_name")) {
            message = "角色名称已存在";
        } else if (errorMsg.contains("email") || errorMsg.contains("users.email")) {
            message = "邮箱已存在";
        } else if (errorMsg.contains("name")) {
            message = "名称已存在";
        }
        
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.conflict(message));
    }

    /**
     * 处理身份认证异常（如密码错误）
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(BadCredentialsException ex) {
        logger.warn("身份认证失败: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.unauthorized("用户名或密码错误"));
    }

    /**
     * 处理访问拒绝异常（权限不足）
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        logger.warn("访问被拒绝: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.forbidden("没有权限访问该资源"));
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(org.trs.therepairsystem.common.exception.BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(org.trs.therepairsystem.common.exception.BusinessException ex) {
        logger.warn("业务异常: {}", ex.getMessage());
        
        String message = ex.getMessage();
        
        // 根据异常信息返回适当的状态码
        if (message != null) {
            if (message.contains("已存在") || message.contains("重复")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ApiResponse.conflict(message));
            } else if (message.contains("不存在") || message.contains("未找到")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.notFound(message));
            } else if (message.contains("密码错误") || message.contains("认证失败")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.unauthorized(message));
            } else if (message.contains("权限") || message.contains("无权")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.forbidden(message));
            }
        }
        
        // 其他业务异常返回400 Bad Request
        return ResponseEntity.badRequest()
                .body(ApiResponse.badRequest(message != null ? message : "业务处理失败"));
    }

    /**
     * 处理运行时异常（业务逻辑异常）
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException ex) {
        logger.warn("业务逻辑异常: {}", ex.getMessage());
        
        String message = ex.getMessage();
        
        // 根据异常信息返回适当的状态码
        if (message != null) {
            if (message.contains("已存在") || message.contains("重复")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ApiResponse.conflict(message));
            } else if (message.contains("不存在") || message.contains("未找到")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.notFound(message));
            } else if (message.contains("密码错误") || message.contains("认证失败")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.unauthorized(message));
            } else if (message.contains("权限") || message.contains("无权")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.forbidden(message));
            }
        }
        
        // 其他运行时异常返回400 Bad Request
        return ResponseEntity.badRequest()
                .body(ApiResponse.badRequest(message != null ? message : "请求处理失败"));
    }

    /**
     * 处理其他未知异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        logger.error("未知异常: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.internalServerError("系统内部错误"));
    }
}