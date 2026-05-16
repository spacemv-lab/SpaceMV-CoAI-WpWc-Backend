/*
 * Copyright (c) 2026 成都天巡微小卫星科技有限责任公司
 *
 * Licensed under the MIT License.
 * See LICENSE file for details.
 */

package com.txwx.social.crm.service.impl;

import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.txwx.social.crm.domain.po.TxwxAccountPO;
import com.txwx.social.crm.domain.po.TxwxGraphicInformationImagePO;
import com.txwx.social.crm.domain.po.TxwxPermanentMaterialImagePO;
import com.txwx.social.crm.domain.vo.MediaPlatformVO;
import com.txwx.social.crm.domain.vo.WebChatGraphicInformationImageVO;
import com.txwx.social.crm.domain.vo.WebChatMaterialPermanentVO;
import com.txwx.social.crm.dto.MaterialCountResponse;
import com.txwx.social.crm.mapper.TxwxGraphicInformationImageMapper;
import com.txwx.social.crm.mapper.TxwxPermanentMaterialImageMapper;
import com.txwx.social.crm.service.IAccountService;
import com.txwx.social.crm.service.IWebChatMaterialService;
import com.txwx.social.crm.util.WebChatUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.rmi.ServerException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 微信公众号素材相关逻辑接口具体实现类
 */
@Service
public class WebChatMaterialServiceImpl implements IWebChatMaterialService {

    @Autowired
    private TxwxGraphicInformationImageMapper txwxGraphicInformationImageMapper;
    @Autowired
    private TxwxPermanentMaterialImageMapper txwxPermanentMaterialImageMapper;

    @Autowired
    private IAccountService accountService;


    @Override
    public List<WebChatMaterialPermanentVO> permanentList(List<Long> accountIds) {
        try {
            //TODO 这部分批量暂未改造
            String accessToken = buildAccessToken(accountIds.get(0));

            return WebChatUtil.searchMaterialPermanentList(accessToken, 0, 20);
        } catch (Exception e) {
            throw new RuntimeException("获取永久素材列表失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> permanentListByPage(int pageNum, int pageSize, List<Long> accountIds) {
        try {

            int offset = (pageNum - 1) * pageSize;

            // 查询总数
            int totalCount = txwxPermanentMaterialImageMapper.selectPermanentMaterialImageTotalCount(accountIds);
            // 查询列表
            List<WebChatMaterialPermanentVO> list = txwxPermanentMaterialImageMapper.selectPermanentMaterialImageListByPage(offset, pageSize, accountIds);

            Map<String, Object> result = new HashMap<>();
            result.put("list", list);
            result.put("total", totalCount);
            result.put("pageNum", pageNum);
            result.put("pageSize", pageSize);

            return result;
        } catch (Exception e) {
            throw new RuntimeException("分页获取永久素材列表失败: " + e.getMessage(), e);
        }
    }


    @Override
    public int getPermanentTotalCount(List<Long> accountIds) {
        try {
            String accessToken = buildAccessToken(accountIds.get(0));
            MaterialCountResponse countResponse = WebChatUtil.getMaterialCount(accessToken);
            return countResponse.getImage_count();
        } catch (Exception e) {
            throw new RuntimeException("获取永久素材总数失败: " + e.getMessage(), e);
        }
    }

    @Override
    public WebChatMaterialPermanentVO permanentAdd(WebChatMaterialPermanentVO webChatMaterialPermanent) {
        try {
//            String accessToken = WebChatUtil.getAccessToken(appId, secret);
            // 根据产品和平台获取 appid + secret
            String accessToken = buildAccessToken(webChatMaterialPermanent.getAccountId());
            if (accessToken == null) {
                throw new ServerException("appId及secret验证失败!");
            }
            MultipartFile file = (MultipartFile) webChatMaterialPermanent.getFile();
            if (file == null || file.isEmpty()) {
                throw new RuntimeException("上传文件不能为空");
            }

            // return WebChatUtil.uploadMaterialPermanent(accessToken, file);
            WebChatMaterialPermanentVO uploadRes = WebChatUtil.uploadMaterialPermanent(accessToken, file);

            TxwxPermanentMaterialImagePO entity = new TxwxPermanentMaterialImagePO();
            entity.setMediaId(uploadRes.getMediaId());
            if (StringUtils.isEmpty(webChatMaterialPermanent.getName())) {
                entity.setName(file.getOriginalFilename());
            }else {
                entity.setName(webChatMaterialPermanent.getName());
            }
            entity.setUpdateTime(DateUtils.getNowDate());
            entity.setUrl(uploadRes.getUrl());
            entity.setCreateTime(DateUtils.getNowDate());

            entity.setAccountId(webChatMaterialPermanent.getAccountId());

            txwxPermanentMaterialImageMapper.insertPermanentMaterialImage(entity);

            return uploadRes;
        } catch (Exception e) {
            throw new RuntimeException("上传永久素材失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void permanentDelete(String mediaId) {
        try {
            TxwxPermanentMaterialImagePO permanentMaterialImagePO = txwxPermanentMaterialImageMapper.selectPermanentMaterialImageByMediaId(mediaId);
            if (permanentMaterialImagePO == null) {
                return;
            }
            String accessToken = buildAccessToken(permanentMaterialImagePO.getAccountId());

            // 先删除微信远程的数据
            WebChatUtil.deleteMaterialPermanent(accessToken, mediaId);

            int result = txwxPermanentMaterialImageMapper.deletePermanentMaterialImage(mediaId);
            if (result == 0) {
                throw new RuntimeException("删除永久素材失败: 未找到对应的记录");
            }
        } catch (Exception e) {
            throw new RuntimeException("删除永久素材失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TxwxGraphicInformationImagePO> GraphicInformationImageList(List<Long> accountIds) {
        try {
            return txwxGraphicInformationImageMapper.selectGraphicInformationImageList(accountIds);
        } catch (Exception e) {
            throw new RuntimeException("获取图文消息图片列表失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> GraphicInformationImageListByPage(int pageNum, int pageSize, List<Long> accountIds) {
        try {

            int offset = (pageNum - 1) * pageSize;

            // 查询总数
            int totalCount = txwxGraphicInformationImageMapper.selectGraphicInformationImageTotalCount(accountIds);

            // 查询列表
            List<WebChatGraphicInformationImageVO> list = txwxGraphicInformationImageMapper.selectGraphicInformationImageListByPage(offset, pageSize, accountIds);

            Map<String, Object> result = new HashMap<>();
            result.put("list", list);
            result.put("total", totalCount);
            result.put("pageNum", pageNum);
            result.put("pageSize", pageSize);

            return result;
        } catch (Exception e) {
            throw new RuntimeException("分页获取图文消息图片列表失败: " + e.getMessage(), e);
        }
    }

    @Override
    public int getGraphicInformationImageTotalCount(List<Long> accountIds) {
        try {
            return txwxGraphicInformationImageMapper.selectGraphicInformationImageTotalCount(accountIds);
        } catch (Exception e) {
            throw new RuntimeException("获取图文消息图片总数失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public WebChatGraphicInformationImageVO GraphicInformationImageAdd(WebChatGraphicInformationImageVO informationImage) {
        try {

            String accessToken = buildAccessToken(informationImage.getAccountId());
            MultipartFile file = informationImage.getFile();
            if (file == null || file.isEmpty()) {
                throw new RuntimeException("上传文件不能为空");
            }

            String url = WebChatUtil.uploadGraphicInformationImage(accessToken, file);
            if(StringUtils.isEmpty(url)){
                throw new RuntimeException("上传微信后台失败.");
            }

            TxwxGraphicInformationImagePO entity = new TxwxGraphicInformationImagePO();
            entity.setMediaId(String.valueOf(System.currentTimeMillis()));
            if(StringUtils.isEmpty(informationImage.getName())){
                entity.setName(file.getOriginalFilename());
            }else {
                entity.setName(informationImage.getName());
            }

            entity.setUpdateTime(DateUtils.getNowDate());
            entity.setUrl(url);
            entity.setAccountId(informationImage.getAccountId());
            txwxGraphicInformationImageMapper.insertGraphicInformationImage(entity);

            WebChatGraphicInformationImageVO result = new WebChatGraphicInformationImageVO();
            result.setUrl(url);

            return result;
        } catch (Exception e) {
            throw new RuntimeException("上传图文消息图片失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void GraphicInformationImageDelete(String mediaId) {
        try {
            //TODO 这里还没有做完，还没有去删除微信侧的
            int result = txwxGraphicInformationImageMapper.deleteGraphicInformationImage(mediaId);
            if (result == 0) {
                throw new RuntimeException("删除图文消息图片失败: 未找到对应的记录");
            }
        } catch (Exception e) {
            throw new RuntimeException("删除图文消息图片失败: " + e.getMessage(), e);
        }
    }

    private String buildAccessToken(Long articleVO) throws Exception {
        TxwxAccountPO accountPO = accountService.selectAccountById(articleVO);
        return WebChatUtil.getAccessToken(accountPO.getAppId(), accountPO.getSecret());
    }
}
