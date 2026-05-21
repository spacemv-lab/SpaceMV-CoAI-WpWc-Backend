package com.ruoyi.iam.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.iam.dto.LogoutQuery;
import com.ruoyi.iam.dto.vo.LogoutDetailVO;
import com.ruoyi.iam.service.IAdminLogoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * 注销管理控制器（管理员接口）
 * <p>
 * 前端 API 协议不变，底层数据从 IAM 表派生。
 * 注销状态：0=冷却中, 1=已注销（已忽略「已撤销」）
 *
 * @author txwx
 * @date 2026-04-20
 */
@RestController
@RequestMapping("/admin/logout")
@RequiredArgsConstructor
@Validated
@Tag(name = "注销管理（管理员）")
@Slf4j
public class UserLogoutAdminController extends BaseController {

    private final IAdminLogoutService adminLogoutService;

    /**
     * 查询注销申请列表（分页）
     */
    @PostMapping("/list")
    @Operation(summary = "查询注销申请列表")
    public TableDataInfo list(@Valid @RequestBody LogoutQuery query) {
        return adminLogoutService.queryLogoutList(query);
    }

    /**
     * 查询注销申请详情
     */
    @GetMapping("/detail/{logoutId}")
    @Operation(summary = "查询注销申请详情")
    public R<LogoutDetailVO> detail(@NotNull(message = "注销ID不能为空") @PathVariable Long logoutId) {
        LogoutDetailVO detail = adminLogoutService.queryLogoutDetail(logoutId);
        if (detail == null) {
            return R.fail("注销申请不存在");
        }
        return R.ok(detail);
    }
}
