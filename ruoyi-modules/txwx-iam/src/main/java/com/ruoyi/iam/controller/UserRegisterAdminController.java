package com.ruoyi.iam.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.iam.dto.RegisterQuery;
import com.ruoyi.iam.dto.UpdateStatusRequest;
import com.ruoyi.iam.dto.vo.RegisterDetailVO;
import com.ruoyi.iam.service.IAdminRegisterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * 注册用户管理控制器（管理员接口）
 * <p>
 * 前端 API 协议不变，底层数据从 IAM 表派生
 *
 * @author txwx
 * @date 2026-04-20
 */
@RestController
@RequestMapping("/admin/register")
@RequiredArgsConstructor
@Validated
@Tag(name = "注册用户管理（管理员）")
@Slf4j
public class UserRegisterAdminController extends BaseController {

    private final IAdminRegisterService adminRegisterService;

    /**
     * 查询注册用户列表（分页）
     */
    @PostMapping("/list")
    @Operation(summary = "查询注册用户列表")
    public TableDataInfo list(@Valid @RequestBody RegisterQuery query) {
        return adminRegisterService.queryRegisterList(query);
    }

    /**
     * 查询单个注册用户详情
     */
    @GetMapping("/detail/{registerId}")
    @Operation(summary = "查询单个注册用户详情")
    public R<RegisterDetailVO> detail(@NotNull(message = "注册ID不能为空") @PathVariable Long registerId) {
        RegisterDetailVO detail = adminRegisterService.queryRegisterDetail(registerId);
        if (detail == null) {
            return R.fail("注册用户不存在");
        }
        return R.ok(detail);
    }

    /**
     * 修改注册用户状态（启用/停用）
     */
    @PutMapping("/status")
    @Operation(summary = "修改注册用户状态")
    public AjaxResult updateStatus(@Valid @RequestBody UpdateStatusRequest request) {
        return adminRegisterService.updateStatus(request);
    }
}
