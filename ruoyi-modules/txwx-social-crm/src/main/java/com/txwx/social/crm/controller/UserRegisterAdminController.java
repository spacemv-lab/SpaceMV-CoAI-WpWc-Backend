package com.txwx.social.crm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.txwx.social.crm.domain.query.RegisterQuery;
import com.txwx.social.crm.domain.query.UpdateStatusRequest;
import com.txwx.social.crm.domain.po.TxwxUserRegisterPO;
import com.txwx.social.crm.service.ITxwxUserRegisterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 注册用户管理控制器（管理员接口）
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

    @Autowired
    private ITxwxUserRegisterService userRegisterService;

    /**
     * 查询注册用户列表（分页）
     *
     * @param query 查询参数
     * @return 结果
     */
    @PostMapping("/list")
    @PreAuthorize("@ss.hasPermi('txwx:user:register:list')")
    @Operation(summary = "查询注册用户列表")
    public TableDataInfo list(@Valid @RequestBody RegisterQuery query) {

        LambdaQueryWrapper<TxwxUserRegisterPO> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotEmpty(query.getUserName())) {
            wrapper.eq(TxwxUserRegisterPO::getUserName, query.getUserName());
        }
        if (StringUtils.isNotEmpty(query.getPhonenumber())) {
            wrapper.eq(TxwxUserRegisterPO::getBindPhone, query.getPhonenumber());
        }
        if (StringUtils.isNotEmpty(query.getEmail())) {
            wrapper.eq(TxwxUserRegisterPO::getBindEmail, query.getEmail());
        }

        if (StringUtils.isNotEmpty(query.getStatus())) {
            wrapper.eq(TxwxUserRegisterPO::getStatus, query.getStatus());
        }

        wrapper.orderByDesc(TxwxUserRegisterPO::getRegisterTime);
        startPage();
        List<TxwxUserRegisterPO> registerPOList = userRegisterService.list(wrapper);

        return getDataTable(registerPOList);
    }


    /**
     * 查询单个注册用户详情
     *
     * @param registerId 注册ID
     * @return 结果
     */
    @GetMapping("/detail/{registerId}")
    @PreAuthorize("@ss.hasPermi('system:registerUser:list')")
    @Operation(summary = "查询单个注册用户详情")
    public R<TxwxUserRegisterPO> detail(@NotNull(message = "注册ID不能为空") @PathVariable Long registerId) {
        TxwxUserRegisterPO register = userRegisterService.getById(registerId);
        return R.ok(register);
    }

    /**
     * 修改注册用户状态（启用/停用）
     *
     * @param request 修改请求
     * @return 结果
     */
    @PutMapping("/status")
    @PreAuthorize("@ss.hasPermi('system:registerUser:list')")
    @Operation(summary = "修改注册用户状态")
    public AjaxResult updateStatus(@Valid @RequestBody UpdateStatusRequest request) {
        // 1. 查询注册信息
        TxwxUserRegisterPO register = userRegisterService.getById(request.getRegisterId());
        if (register == null) {
            return AjaxResult.error("注册用户不存在");
        }

        // 2. 更新状态
        register.setStatus(request.getStatus());
        register.setUpdateBy(request.getUpdateBy());
        register.setUpdateTime(new Date());
        userRegisterService.updateById(register);

        log.info("注册用户状态修改成功: registerId={}, status={}", request.getRegisterId(), request.getStatus());

        return AjaxResult.success("状态修改成功");
    }


}
