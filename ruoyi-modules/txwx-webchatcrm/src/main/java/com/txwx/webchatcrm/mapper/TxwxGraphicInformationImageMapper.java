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
package com.txwx.webchatcrm.mapper;

import com.txwx.webchatcrm.domain.po.TxwxGraphicInformationImagePO;
import com.txwx.webchatcrm.domain.vo.WebChatGraphicInformationImageVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 微信公众号图文消息图片Mapper接口
 *
 * @author txwx
 * @date 2025-12-06
 */
public interface TxwxGraphicInformationImageMapper {

    /**
     * @description: 从数据库中查询图文消息图片列表
     */
    List<TxwxGraphicInformationImagePO> selectGraphicInformationImageList();

    /**
     * @description: 分页查询图文消息图片列表
     */
    List<WebChatGraphicInformationImageVO> selectGraphicInformationImageListByPage(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * @description: 查询图文消息图片总数
     */
    int selectGraphicInformationImageTotalCount();

    /**
     * @description: 向数据库中插入图文消息图片
     */
    int insertGraphicInformationImage(TxwxGraphicInformationImagePO informationImage);

    /**
     * @description: 根据mediaId删除数据库中的图文消息图片
     */
    int deleteGraphicInformationImage(@Param("mediaId") String mediaId);
}
