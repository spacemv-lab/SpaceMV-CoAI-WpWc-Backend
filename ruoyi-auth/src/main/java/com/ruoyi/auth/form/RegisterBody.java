/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.ruoyi.auth.form;

/**
 * 用户注册对象
 * 
 * @author ruoyi
 */
public class RegisterBody extends LoginBody
{
    private String Source;

    private String phonenumber;

    private String email;

    private String phoneVerifyCode;

    private String emailVerifyCode;



    public String getSource() {
        return Source;
    }

    public void setSource(String source) {
        Source = source;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneVerifyCode() {
        return phoneVerifyCode;
    }

    public void setPhoneVerifyCode(String phoneVerifyCode) {
        this.phoneVerifyCode = phoneVerifyCode;
    }

    public String getEmailVerifyCode() {
        return emailVerifyCode;
    }

    public void setEmailVerifyCode(String emailVerifyCode) {
        this.emailVerifyCode = emailVerifyCode;
    }
}
