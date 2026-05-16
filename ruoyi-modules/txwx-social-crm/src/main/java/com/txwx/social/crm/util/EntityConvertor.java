/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.util;

import com.ruoyi.common.core.utils.bean.BeanUtils;
import com.ruoyi.common.core.web.domain.BaseEntity;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.txwx.social.api.domain.dto.*;
import com.txwx.social.crm.domain.po.*;
import org.apache.commons.compress.utils.Lists;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class EntityConvertor {

    public static List<AccountDTO> convertPO2DTO(List<TxwxAccountPO> accountPOList) {
        if (accountPOList == null || accountPOList.isEmpty()) {
            return Lists.newArrayList();
        }

        return accountPOList.stream()
                .map(po -> {
                    AccountDTO dto = new AccountDTO();
                    // 若依框架的BeanUtils.copyProperties
                    BeanUtils.copyProperties(po, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public static TxwxProductPO convert2PO(SimpleProductDTO simpleProductDTO, boolean fillBase) {
        TxwxProductPO po = new TxwxProductPO();
        if (simpleProductDTO != null) {
            BeanUtils.copyProperties(simpleProductDTO, po);
        }
        if (fillBase) {
            fillBaseInfo(po);
        }
        return po;
    }

    public static TxwxProductPO convert2PO(SimpleProductDTO simpleProductDTO, List<Long> productIds, boolean fillBase) {
        TxwxProductPO txwxProductPO = convert2PO(simpleProductDTO, fillBase);
        txwxProductPO.setIds(productIds);
        return txwxProductPO;
    }


    public static TxwxAccountPO convert2PO(AccountDTO accountDTO, boolean fillBase) {
        TxwxAccountPO po = new TxwxAccountPO();
        if (accountDTO != null) {
            BeanUtils.copyProperties(accountDTO, po);
        }
        if (fillBase) {
            fillBaseInfo(po);
        }
        return po;
    }

    public static AccountDTO convert2DTO(TxwxAccountPO accountPO) {
        AccountDTO dto = new AccountDTO();
        if (accountPO != null) {
            BeanUtils.copyProperties(accountPO, dto);
        }
        return dto;
    }


    public static ChannelDTO convert2DTO(TxwxChannelPO channelPO) {
        ChannelDTO dto = new ChannelDTO();
        if (channelPO != null) {
            BeanUtils.copyProperties(channelPO, dto);
        }
        return dto;
    }


    public static TxwxUserPermissionPO convert2PO(UserPermissionDTO userPermissionDTO, boolean fillBase) {
        TxwxUserPermissionPO po = new TxwxUserPermissionPO();
        if (userPermissionDTO != null) {
            BeanUtils.copyProperties(userPermissionDTO, po);
        }
        if (fillBase) {
            fillBaseInfo(po);
        }
        return po;
    }

    public static TxwxProductChannelPO convert2PO(ProductChannelDTO productChannelDTO, boolean fillBase) {
        TxwxProductChannelPO po = new TxwxProductChannelPO();
        if (productChannelDTO != null) {
            BeanUtils.copyProperties(productChannelDTO, po);
        }
        if (fillBase) {
            fillBaseInfo(po);
        }
        return po;
    }

    public static Map<Long, List<ProductChannelDTO>> convert2DTOMap(
            Map<Long, List<TxwxProductChannelPO>> prod2ChannelMap) {

        if (CollectionUtils.isEmpty(prod2ChannelMap)) {
            return new HashMap<>();
        }

        return prod2ChannelMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,  // 保持Key不变
                        entry -> convertToDTOList(entry.getValue())  // 转换Value
                ));
    }


    public static List<ProductChannelDTO> convertToDTOList(List<TxwxProductChannelPO> po) {
        if (CollectionUtils.isEmpty(po)) {
            return Lists.newArrayList();
        }
        List<ProductChannelDTO> dtoList = Lists.newArrayList();
        po.forEach( curPO -> {
            ProductChannelDTO dto = new ProductChannelDTO();
            BeanUtils.copyProperties(po, dto);
            dtoList.add(dto);
        });
        return dtoList;
    }

    public static String convertToString(List<Long> longList) {
        if (longList == null || longList.isEmpty()) {
            return "";
        }

        return longList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    public static List<Long> convertToLongList(String str) {
        if (str == null || str.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return Arrays.stream(str.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }

    public static void fillBaseInfo(BaseEntity baseEntity) {
        baseEntity.setCreateBy(SecurityUtils.getUsername());
        baseEntity.setCreateTime(new Date());
        baseEntity.setUpdateBy(SecurityUtils.getUsername());
        baseEntity.setUpdateTime(new Date());
    }


}
