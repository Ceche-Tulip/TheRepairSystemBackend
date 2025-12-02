package org.trs.therepairsystem.web.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 统一响应格式 */
    private ResponseEntity<Map<String, Object>> buildResponse(int code, String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", code);
        response.put("message", message);
        response.put("data", data);
        return ResponseEntity.status(code).body(response);
    }

    /** 处理：用户名重复、违反唯一约束 */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateKey(DataIntegrityViolationException ex) {
        return buildResponse(409, "数据唯一约束冲突（可能是用户名已存在）", null);
    }

    /** 处理：登录失败（密码错误/用户不存在） */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        return buildResponse(401, "用户名或密码错误", null);
    }

    /** 处理：实体未找到异常 */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFound(EntityNotFoundException ex) {
        return buildResponse(404, ex.getMessage() != null ? ex.getMessage() : "资源不存在", null);
    }

    /** 处理：权限不足异常 */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        return buildResponse(403, "权限不足", null);
    }

    /** 处理：请求参数验证失败 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationError(MethodArgumentNotValidException ex) {
        StringBuilder message = new StringBuilder("请求参数验证失败: ");
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            message.append(error.getField()).append(" ").append(error.getDefaultMessage()).append("; ");
        });
        return buildResponse(400, message.toString(), null);
    }

    /** 处理：非法参数异常 */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildResponse(400, ex.getMessage() != null ? ex.getMessage() : "参数错误", null);
    }

    /** 处理：运行时异常 */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        return buildResponse(500, ex.getMessage() != null ? ex.getMessage() : "服务器内部错误", null);
    }
}

