package com.txwx.webchat.controller;

import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.txwx.webchat.service.impl.ImportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/channelData")
@Tag(name = "01--【微信运营】--渠道构成数据")
public class ChannelController extends BaseController {

    @Autowired
    private ImportService importService;

    @PostMapping("/importExcel")
    public AjaxResult importExcel(@RequestPart("file") MultipartFile file, HttpServletResponse response) throws Exception {
        return success("导入成功!");
    }
}
