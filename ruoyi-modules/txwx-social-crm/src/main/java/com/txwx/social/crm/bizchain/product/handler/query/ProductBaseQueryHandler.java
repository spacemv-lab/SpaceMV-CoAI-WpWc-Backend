package com.txwx.social.crm.bizchain.product.handler.query;

import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.ruoyi.common.core.constant.HttpStatus;
import com.ruoyi.common.core.utils.PageUtils;
import com.github.pagehelper.Page;
import com.ruoyi.common.core.utils.bean.BeanUtils;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.txwx.social.api.domain.dto.ProductDTO;
import com.txwx.social.api.domain.dto.SimpleProductDTO;
import com.txwx.social.crm.bizchain.product.context.ProductChainContext;
import com.txwx.social.crm.common.chain.AbstractChainHandler;
import com.txwx.social.crm.domain.po.TxwxProductPO;
import com.txwx.social.crm.mapper.TxwxProductMapper;
import com.txwx.social.crm.util.EntityConvertor;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductBaseQueryHandler extends AbstractChainHandler<ProductChainContext> {
    @Autowired
    private TxwxProductMapper productMapper;

    @Override
    protected void doHandle(ProductChainContext context) {
        if (!context.getQueryConfig().isQueryProduct()) {
            context.skipCurrentHandler("请求跳过查询产品基本信息");
            return;
        }

        List<Long> productIds = context.getProductIds();

        if (CollectionUtils.isEmpty(productIds)) {
            TableDataInfo dataInfo = new TableDataInfo();
            dataInfo.setCode(HttpStatus.SUCCESS);
            dataInfo.setMsg("查询成功");
            dataInfo.setRows(Lists.newArrayList()); // 当前页数据
            dataInfo.setTotal(0);
            context.setPageResult(dataInfo);
            context.setProductList(Lists.newArrayList());
            return;
        }

        PageUtils.startPage(context.getPageDomain());
        List<TxwxProductPO> productList = productMapper.selectProductList(
                EntityConvertor.convert2PO(context.getQueryParams(),
                        context.getProductIds(), false));
        // 3. 获取 Page 对象（关键！）
        Page<TxwxProductPO> page = (Page<TxwxProductPO>) productList;

        // 4. 创建 TableDataInfo
        TableDataInfo dataInfo = new TableDataInfo();
        dataInfo.setCode(HttpStatus.SUCCESS);
        dataInfo.setMsg("查询成功");
        dataInfo.setRows(page.getResult()); // 当前页数据
        dataInfo.setTotal(page.getTotal()); // 总记录数
        List<SimpleProductDTO> simpleProductDTOList = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(dataInfo.getRows())) {
            simpleProductDTOList = productList.stream()
                    .map(po -> {
                        SimpleProductDTO dto = new SimpleProductDTO();
                        BeanUtils.copyProperties(po, dto);
                        return dto;
                    })
                    .toList();
        }
        List<ProductDTO> productDTOList = Lists.newArrayList();
        simpleProductDTOList.forEach(simple -> {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setBaseInfo(simple);
            productDTOList.add(productDTO);
        });
        context.setProductList(productDTOList);
        context.setPageResult(dataInfo);
        List<Long> queryPids = simpleProductDTOList.stream().map(SimpleProductDTO::getId).toList();
        context.setProductIds(queryPids);
    }


}
