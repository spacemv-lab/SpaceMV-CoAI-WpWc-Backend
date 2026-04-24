package com.ruoyi.system.api.domain;


public class TxUser extends SysUser {

    private static final long serialVersionUID = 1L;

    private String txPhonenumber;

    private String txEmail;

    private String phoneVerifyCode;

    private String emailVerifyCode;

    // 添加无参构造器
    public TxUser() {
        super();
    }


    public String getTxPhonenumber() {
        return txPhonenumber;
    }

    public void setTxPhonenumber(String txPhonenumber) {
        this.txPhonenumber = txPhonenumber;
    }

    public String getTxEmail() {
        return txEmail;
    }

    public void setTxEmail(String txEmail) {
        this.txEmail = txEmail;
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
