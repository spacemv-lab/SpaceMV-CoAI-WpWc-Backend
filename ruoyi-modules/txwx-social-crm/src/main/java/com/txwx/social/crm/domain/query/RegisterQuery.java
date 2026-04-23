package com.txwx.social.crm.domain.query;

import lombok.Data;

@Data
public class RegisterQuery {

    private String userName;

    private String phonenumber;

    private String email;

    private String status;
}
