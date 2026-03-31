package com.txwx.webchat.controller;

import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.txwx.webchat.domain.entity.mysql.MediaPlatform;
import com.txwx.webchat.domain.vo.UserPlatformVo;
import com.txwx.webchat.service.IMediaPlatformService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/mediaPlatform")
@Tag(name = "02--【自媒体】--平台")
public class MediaPlatformController extends BaseController {

    @Autowired
    private IMediaPlatformService iMediaPlatformService;

    @PostMapping("/saveUpdate")
    @Operation(summary = "绑定")
    public AjaxResult saveUpdate(@RequestBody MediaPlatform mediaPlatform) {
        boolean res = iMediaPlatformService.saveUpdate(mediaPlatform);
        if (res) {
            return success("保存成功");
        }else {
            return error("保存失败");
        }
    }

    @PostMapping("/list")
    @Operation(summary = "平台列表")
    public AjaxResult list() {
        List<UserPlatformVo> res = iMediaPlatformService.selectList();
        return success(res);
    }

    @GetMapping("/dataSync")
    @Operation(summary = "数据同步")
    public AjaxResult dataSync(String appId) throws Exception {
        Boolean res = iMediaPlatformService.dataSync(appId);
        if (res) {
            return success("首次同步时间较长,预计30分钟以上,请耐心等待.");
        }else {
            return error("数据同步失败!");
        }
    }

    @DeleteMapping("/delete")
    @Operation(summary = "解绑")
    public AjaxResult delete(@RequestParam Long id) {
        boolean res = iMediaPlatformService.delete(id);
        if (res) {
            return success("解绑成功");
        }else {
            return error("解绑失败");
        }
    }
}
