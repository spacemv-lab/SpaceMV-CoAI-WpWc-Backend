package com.txwx.social.dashboard.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("ods_users")
public class OdsUsers {

    @Schema(description = "数据日期")
    private LocalDate refDate;

    private Integer newSource;

    private Integer newUser;

    private Integer cancelUser;
}
