package com.txwx.social.crm.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.txwx.social.api.client.AccountApiClient;
import com.txwx.social.api.domain.dto.AccountDTO;
import com.txwx.social.crm.bizchain.account.service.AccountChainService;
import com.txwx.social.crm.domain.query.AccountAddOrUpdateRequest;
import com.txwx.social.crm.domain.query.AccountQueryRequest;
import com.txwx.social.crm.domain.po.TxwxAccountPO;
import com.txwx.social.crm.service.IAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.ruoyi.common.core.web.page.TableSupport.getPageDomain;

/**
 * 账号控制器（实现 AccountApiClient 接口，提供远程调用能力）
 *
 * @author txwx
 * @date 2026-04-04
 */
@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@Validated
@Tag(name = "05--【CRM】--账号管理")
public class AccountController extends BaseController implements AccountApiClient {

    @Autowired
    private IAccountService accountService;

    private final AccountChainService accountChainService;

    /**
     * 获取账号列表
     * @param request 账号查询条件
     * @return 分页结果
     */
    //TODO 角色权限分配
    //@PreAuthorize("@ss.hasPermi('system:account:list')")
    @PostMapping("/management/list")
    @Operation(summary = "查询账号列表")
    public TableDataInfo list(@RequestBody AccountQueryRequest request) {
        // 1. 调用责任链Service
        return accountChainService.selectAccountList(request.getQuery(),
                getPageDomain()
        );
    }

    /**
     * 新增账号
     * @param request 产品请求
     * @return 产品ID
     */
    //@PreAuthorize("@ss.hasPermi('system:account:add')")
    @PostMapping("/management")
    public AjaxResult add(@RequestBody AccountAddOrUpdateRequest request) {
        accountChainService.insertAccount(request.getAccountDTO());
        return AjaxResult.success("账号新增成功");
    }

    /**
     * 修改账号
     * @param request 账号主体
     * @return 结果
     */
    //@PreAuthorize("@ss.hasPermi('system:product:edit')")
    @PutMapping("/management")
    public AjaxResult edit(@RequestBody AccountAddOrUpdateRequest request) {
        accountChainService.updateAccount(request.getAccountDTO());
        return AjaxResult.success("产品修改成功");
    }

    /**
     * 删除产品
     * @param accountId 产品ID
     * @return 结果
     */
    //@PreAuthorize("@ss.hasPermi('system:product:remove')")
    @DeleteMapping("/management/{accountId}")
    public AjaxResult remove(
            @PathVariable @NotNull(message = "账号ID不能为空") Long accountId
    ) {
        accountChainService.deleteAccount(accountId);
        return AjaxResult.success("账号删除成功");
    }
    /* ========== 以下是 AccountApiClient 接口的实现 ========== */

    @Override
    @PostMapping("/list")
    @Operation(summary = "获取账号列表")
    public R<List<AccountDTO>> getAccountList(@Parameter(description = "查询条件") @RequestBody(required = false) AccountDTO query) {
        TxwxAccountPO queryPO = new TxwxAccountPO();
        if (query != null) {
            BeanUtils.copyProperties(query, queryPO);
        }
        List<TxwxAccountPO> list = accountService.selectAccountList(queryPO);
        List<AccountDTO> dtoList = list.stream()
                .map(this::convertPoToDto)
                .collect(Collectors.toList());
        return R.ok(dtoList);
    }

    @Override
    @GetMapping("/{id}")
    @Operation(summary = "按id获取账号")
    public R<AccountDTO> getAccountById(@Parameter(description = "账号ID") @PathVariable("id") Long id) {
        TxwxAccountPO po = accountService.selectAccountById(id);
        if (po == null) {
            return R.fail("数据不存在");
        }
        return R.ok(convertPoToDto(po));
    }

    @Override
    @PostMapping
    @Operation(summary = "添加账号")
    public R<Boolean> addAccount(@Parameter(description = "账号信息") @RequestBody AccountDTO account) {
        return R.ok(accountService.insertAccountDTO(account) > 0);
    }

    private void fillBaseInfo(TxwxAccountPO po) {
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

    @Override
    @PutMapping
    @Operation(summary = "更新账号")
    public R<Boolean> updateAccount(@Parameter(description = "账号信息") @RequestBody AccountDTO account) {
        TxwxAccountPO po = new TxwxAccountPO();
        BeanUtils.copyProperties(account, po);
        fillBaseInfo(po);
        return R.ok(accountService.updateAccount(po) > 0);
    }

    @Override
    @DeleteMapping("/{ids}")
    @Operation(summary = "批量删除账号")
    public R<Boolean> deleteAccountByIds(@Parameter(description = "账号ID数组") @PathVariable("ids") String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .map(Long::valueOf)
                .toList();

        if (idList.isEmpty()) {
            return R.fail("请提供要删除的ID列表");
        }
        return R.ok(accountService.deleteAccountByIds(idList) > 0);
    }

    @Override
    @GetMapping("/byProduct/{productId}")
    @Operation(summary = "通过产品id批量查询账号列表")
    public R<List<AccountDTO>> getAccountByProductId(@Parameter(description = "产品ID") @PathVariable("productId") Long productId) {
        List<TxwxAccountPO> list = accountService.selectAccountByProductId(productId);
        List<AccountDTO> dtoList = list.stream()
                .map(this::convertPoToDto)
                .collect(Collectors.toList());
        return R.ok(dtoList);
    }

    @Override
    @GetMapping("/byChannel/{channelId}")
    @Operation(summary = "通过渠道id批量查询账号列表")
    public R<List<AccountDTO>> getAccountByChannelId(@Parameter(description = "渠道ID") @PathVariable("channelId") Long channelId) {
        List<TxwxAccountPO> list = accountService.selectAccountByChannelId(channelId);
        List<AccountDTO> dtoList = list.stream()
                .map(this::convertPoToDto)
                .collect(Collectors.toList());
        return R.ok(dtoList);
    }

    /* ========== 内部转换方法 ========== */

    /**
     * 将内部 PO 对象转换为 API DTO 对象
     */
    private AccountDTO convertPoToDto(TxwxAccountPO po) {
        if (po == null) {
            return null;
        }
        AccountDTO dto = new AccountDTO();
        BeanUtils.copyProperties(po, dto);
        return dto;
    }
}
