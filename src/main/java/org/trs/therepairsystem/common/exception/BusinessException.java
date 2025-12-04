package org.trs.therepairsystem.common.exception;

/**
 * 业务异常类
 */
public class BusinessException extends RuntimeException {

    private String code;
    private String message;

    public BusinessException(String message) {
        super(message);
        this.message = message;
        this.code = "BUSINESS_ERROR";
    }

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.code = "BUSINESS_ERROR";
    }

    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}