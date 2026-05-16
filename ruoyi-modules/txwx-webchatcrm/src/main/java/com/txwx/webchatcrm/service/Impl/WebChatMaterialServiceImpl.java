/*
 * Copyright 2026 the original author or authors.
 *
 * Licensed under the MIT License;
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 
 * the copywrite is belongs to  SpaceMV  team
 */
package com.txwx.webchatcrm.service.Impl;

import com.ruoyi.common.core.utils.DateUtils;
import com.txwx.webchatcrm.domain.po.TxwxGraphicInformationImagePO;
import com.txwx.webchatcrm.domain.vo.WebChatGraphicInformationImageVO;
import com.txwx.webchatcrm.domain.vo.WebChatMaterialPermanentVO;
import com.txwx.webchatcrm.dto.MaterialCountResponse;
import com.txwx.webchatcrm.mapper.TxwxGraphicInformationImageMapper;
import com.txwx.webchatcrm.service.IWebChatMaterialService;
import com.txwx.webchatcrm.util.WebChatUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    @Value("${weixin.appId}")
    private String appId;

    @Value("${weixin.secret}")
    private String secret;

    @Override
    public List<WebChatMaterialPermanentVO> permanentList() {
        try {
            String accessToken = WebChatUtil.getAccessToken(appId, secret);
            List<WebChatMaterialPermanentVO> materialList = WebChatUtil.searchMaterialPermanentList(accessToken, 0, 20);

            return materialList;
        } catch (Exception e) {
            throw new RuntimeException("获取永久素材列表失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> permanentListByPage(int pageNum, int pageSize) {
        try {
            String accessToken = WebChatUtil.getAccessToken(appId, secret);

            // 微信API的offset从0开始,pageSize最大20
            int offset = (pageNum - 1) * pageSize;
            if (pageSize > 20) {
                pageSize = 20;
            }

            // 查询总数
            MaterialCountResponse countResponse = WebChatUtil.getMaterialCount(accessToken);
            int totalCount = countResponse.getImage_count();

            // 查询列表
            List<WebChatMaterialPermanentVO> list = WebChatUtil.searchMaterialPermanentList(accessToken, offset, pageSize);

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
    public int getPermanentTotalCount() {
        try {
            String accessToken = WebChatUtil.getAccessToken(appId, secret);
            MaterialCountResponse countResponse = WebChatUtil.getMaterialCount(accessToken);
            return countResponse.getImage_count();
        } catch (Exception e) {
            throw new RuntimeException("获取永久素材总数失败: " + e.getMessage(), e);
        }
    }

    @Override
    public WebChatMaterialPermanentVO permanentAdd(WebChatMaterialPermanentVO webChatMaterialPermanent) {
        try {
            String accessToken = WebChatUtil.getAccessToken(appId, secret);
            MultipartFile file = (MultipartFile) webChatMaterialPermanent.getFile();
            if (file == null || file.isEmpty()) {
                throw new RuntimeException("上传文件不能为空");
            }

            return WebChatUtil.uploadMaterialPermanent(accessToken, file);
        } catch (Exception e) {
            throw new RuntimeException("上传永久素材失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void permanentDelete(String mediaId) {
        try {
            String accessToken = WebChatUtil.getAccessToken(appId, secret);
            WebChatUtil.deleteMaterialPermanent(accessToken, mediaId);
        } catch (Exception e) {
            throw new RuntimeException("删除永久素材失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TxwxGraphicInformationImagePO> GraphicInformationImageList() {
        try {
            return txwxGraphicInformationImageMapper.selectGraphicInformationImageList();
        } catch (Exception e) {
            throw new RuntimeException("获取图文消息图片列表失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> GraphicInformationImageListByPage(int pageNum, int pageSize) {
        try {
            int offset = (pageNum - 1) * pageSize;

            // 查询总数
            int totalCount = txwxGraphicInformationImageMapper.selectGraphicInformationImageTotalCount();

            // 查询列表
            List<WebChatGraphicInformationImageVO> list = txwxGraphicInformationImageMapper.selectGraphicInformationImageListByPage(offset, pageSize);

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
    public int getGraphicInformationImageTotalCount() {
        try {
            return txwxGraphicInformationImageMapper.selectGraphicInformationImageTotalCount();
        } catch (Exception e) {
            throw new RuntimeException("获取图文消息图片总数失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public WebChatGraphicInformationImageVO GraphicInformationImageAdd(WebChatGraphicInformationImageVO informationImage) {
        try {
            String accessToken = WebChatUtil.getAccessToken(appId, secret);
            MultipartFile file = (MultipartFile) informationImage.getFile();
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
            int result = txwxGraphicInformationImageMapper.deleteGraphicInformationImage(mediaId);
            if (result == 0) {
                throw new RuntimeException("删除图文消息图片失败: 未找到对应的记录");
            }
        } catch (Exception e) {
            throw new RuntimeException("删除图文消息图片失败: " + e.getMessage(), e);
        }
    }
}
