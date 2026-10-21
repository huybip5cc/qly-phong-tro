package com.qlytro.exception;

/** Nem ra khi vi pham rang buoc nghiep vu (khong phai du lieu sai, ma logic sai). */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
