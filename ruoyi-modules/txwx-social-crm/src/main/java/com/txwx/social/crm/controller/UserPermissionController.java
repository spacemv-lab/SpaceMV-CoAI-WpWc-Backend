package com.txwx.social.crm.controller;

import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.txwx.social.crm.domain.po.TxwxProductPO;
import com.txwx.social.crm.domain.po.TxwxUserPermissionPO;
import com.txwx.social.crm.service.ITxwxUserPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 用户权限控制器
 *
 * @author txwx
 * @date 2026-04-06
 */
@RestController
@RequestMapping("/userPermission")
@Tag(name = "06--【CRM】--用户权限管理")
public class UserPermissionController extends BaseController {

    @Autowired
    private ITxwxUserPermissionService userPermissionService;

    /**
     * 查询权限列表
     */
    @PostMapping("/list")
    @Operation(summary = "查询权限列表")
    public AjaxResult list(@Parameter(description = "查询条件") TxwxUserPermissionPO permission) {
        List<TxwxUserPermissionPO> list = userPermissionService.selectPermissionList(permission);
        return AjaxResult.success(list);
    }

    /**
     * 查询权限详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询权限详情")
    public AjaxResult get(@Parameter(description = "权限ID") @PathVariable Long id) {
        TxwxUserPermissionPO permission = userPermissionService.selectPermissionById(id);
        return AjaxResult.success(permission);
    }

    /**
     * 新增权限
     */
    @PostMapping
    @Operation(summary = "新增权限")
    public AjaxResult add(@Parameter(description = "权限信息") @RequestBody TxwxUserPermissionPO permission) {
        fillBaseInfo(permission);
        return toAjax(userPermissionService.insertPermission(permission));
    }

    /**
     * 修改权限
     */
    @PutMapping
    @Operation(summary = "修改权限")
    public AjaxResult update(@Parameter(description = "权限信息") @RequestBody TxwxUserPermissionPO permission) {
        fillBaseInfo(permission);
        return toAjax(userPermissionService.updatePermission(permission));
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除权限")
    public AjaxResult delete(@Parameter(description = "权限ID数组") @PathVariable String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .map(Long::valueOf)
                .toList();

        if (idList.isEmpty()) {
            return AjaxResult.error("请提供要删除的ID列表");
        }
        return toAjax(userPermissionService.deletePermissionByIds(idList));
    }

    /**
     * 根据用户ID查询权限列表
     */
    @GetMapping("/byUser/{userId}")
    @Operation(summary = "根据用户ID查询权限列表")
    public AjaxResult getByUserId(@Parameter(description = "用户ID") @PathVariable Long userId) {
        List<TxwxUserPermissionPO> list = userPermissionService.selectPermissionByUserId(userId);
        return AjaxResult.success(list);
    }

    /**
     * 删除用户的所有权限
     */
    @DeleteMapping("/byUser/{userId}")
    @Operation(summary = "删除用户的所有权限")
    public AjaxResult deleteByUserId(@Parameter(description = "用户ID") @PathVariable Long userId) {
        return toAjax(userPermissionService.deletePermissionByUserId(userId));
    }

    private void fillBaseInfo(TxwxUserPermissionPO po) {
        //TODO 测试用
        String operator = SecurityUtils.getUsername();
        if (StringUtils.isEmpty(operator)) {
            operator = "管理员";
        }
        po.setCreateBy(operator);
        po.setUpdateBy(operator);
        po.setCreateTime(new Date());
        po.setUpdateTime(new Date());
    }
}
