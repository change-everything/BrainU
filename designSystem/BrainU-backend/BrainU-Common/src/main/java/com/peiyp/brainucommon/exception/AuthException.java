package com.peiyp.brainucommon.exception;

/**
 * @author PeiYP
 * @since 2023年05月18日 11:45
 */
public class AuthException extends RuntimeException{

    private String msg;
    private Integer code;


    public AuthException(String msg, Integer code) {
        this.msg = msg;
        this.code = code;
    }

    public AuthException(String message, String msg, Integer code) {
        super(message);
        this.msg = msg;
        this.code = code;
    }

    public AuthException(String message, Throwable cause, String msg, Integer code) {
        super(message, cause);
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
