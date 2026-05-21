package com.ruoyi.iam.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.CaptchaException;
import com.ruoyi.common.core.exception.ServiceException;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler
{
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<?> handleExpiredJwt(ExpiredJwtException e)
    {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(R.fail("Token expired"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException e)
    {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(R.fail(e.getMessage()));
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<?> handleServiceException(ServiceException e)
    {
        return ResponseEntity.status(HttpStatus.OK).body(R.fail(e.getMessage()));
    }

    /**
     * 图形验证码校验失败
     */
    @ExceptionHandler(CaptchaException.class)
    public ResponseEntity<?> handleCaptchaException(CaptchaException e)
    {
        return ResponseEntity.status(HttpStatus.OK).body(R.fail(e.getMessage()));
    }
}
