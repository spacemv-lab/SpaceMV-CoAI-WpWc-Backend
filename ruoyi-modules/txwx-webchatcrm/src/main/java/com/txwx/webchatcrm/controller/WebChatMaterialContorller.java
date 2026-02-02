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
 */
package com.txwx.webchatcrm.controller;

import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.txwx.webchatcrm.domain.po.TxwxGraphicInformationImagePO;
import com.txwx.webchatcrm.domain.vo.WebChatGraphicInformationImageVO;
import com.txwx.webchatcrm.domain.vo.WebChatMaterialPermanentVO;
import com.txwx.webchatcrm.service.IWebChatMaterialService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @description: 微信公众号素材Contorller
 */
@RestController
@RequestMapping("/material")
public class WebChatMaterialContorller extends BaseController {

    @Autowired
    private IWebChatMaterialService webChatMaterialService;

    /**
     * @description: 获取永久素材列表
     */
    @GetMapping("/permanentList")
    public AjaxResult permanentList(){
        try {
            List<WebChatMaterialPermanentVO> webChatMaterialPermanentVOS = webChatMaterialService.permanentList();
            return success(webChatMaterialPermanentVOS);
        } catch (Exception e) {
            return error("获取永久素材列表失败: " + e.getMessage());
        }
    }

    /**
     * @description: 分页获取永久素材列表
     */
    @GetMapping("/permanentListByPage")
    public AjaxResult permanentListByPage(@RequestParam(defaultValue = "1") int pageNum,
                                         @RequestParam(defaultValue = "20") int pageSize){
        try {
            Map<String, Object> result = webChatMaterialService.permanentListByPage(pageNum, pageSize);
            return success(result);
        } catch (Exception e) {
            return error("分页获取永久素材列表失败: " + e.getMessage());
        }
    }

    /**
     * @description: 获取永久素材总数
     */
    @GetMapping("/permanentTotalCount")
    public AjaxResult getPermanentTotalCount(){
        try {
            int total = webChatMaterialService.getPermanentTotalCount();
            return success(total);
        } catch (Exception e) {
            return error("获取永久素材总数失败: " + e.getMessage());
        }
    }

    /**
     * @description: 上传永久素材
     */
    @PostMapping("/permanentAdd")
    public AjaxResult permanentAdd(@RequestParam("file") MultipartFile file,
                                    @RequestParam(value = "name", required = false) String name){
        try {
            WebChatMaterialPermanentVO vo = new WebChatMaterialPermanentVO();
            vo.setFile(file);
            if(StringUtils.isNotEmpty(name)){
                vo.setName(name);
            }

            WebChatMaterialPermanentVO webChatMaterialPermanentVO = webChatMaterialService.permanentAdd(vo);
            return success(webChatMaterialPermanentVO);
        } catch (Exception e) {
            return error("上传永久素材失败: " + e.getMessage());
        }
    }

    /**
     * @description: 根据mediaId删除永久素材
     */
    @DeleteMapping("/permanentDelete/{mediaId}")
    public AjaxResult permanentDelete(@PathVariable String mediaId){
        try {
            webChatMaterialService.permanentDelete(mediaId);
            return success("删除永久素材成功");
        } catch (Exception e) {
            return error("删除永久素材失败: " + e.getMessage());
        }
    }

    /**
     * @description: 获取图文消息图片列表
     */
    @GetMapping("/gInfoImgList")
    public AjaxResult GraphicInformationImageList(){
        try {
            List<TxwxGraphicInformationImagePO> list = webChatMaterialService.GraphicInformationImageList();
            return success(list);
        } catch (Exception e) {
            return error("获取图文消息图片列表失败: " + e.getMessage());
        }
    }

    /**
     * @description: 分页获取图文消息图片列表
     */
    @GetMapping("/gInfoImgListByPage")
    public AjaxResult GraphicInformationImageListByPage(@RequestParam(defaultValue = "1") int pageNum,
                                                       @RequestParam(defaultValue = "20") int pageSize){
        try {
            Map<String, Object> result = webChatMaterialService.GraphicInformationImageListByPage(pageNum, pageSize);
            return success(result);
        } catch (Exception e) {
            return error("分页获取图文消息图片列表失败: " + e.getMessage());
        }
    }

    /**
     * @description: 获取图文消息图片总数
     */
    @GetMapping("/gInfoImgTotalCount")
    public AjaxResult getGraphicInformationImageTotalCount(){
        try {
            int total = webChatMaterialService.getGraphicInformationImageTotalCount();
            return success(total);
        } catch (Exception e) {
            return error("获取图文消息图片总数失败: " + e.getMessage());
        }
    }

    /**
     * @description: 上传图文消息图片
     */
    @PostMapping("/gInfoImgAdd")
    public AjaxResult GraphicInformationImageAdd(@RequestParam("file") MultipartFile file,
                                                   @RequestParam(value = "name", required = false) String name){
        try {
            WebChatGraphicInformationImageVO vo = new WebChatGraphicInformationImageVO();
            vo.setFile(file);
            if(StringUtils.isNotEmpty(name)){
                vo.setName(name);
            }
            WebChatGraphicInformationImageVO webChatGraphicInformationImageVO = webChatMaterialService.GraphicInformationImageAdd(vo);
            return success(webChatGraphicInformationImageVO);
        } catch (Exception e) {
            return error("上传图文消息图片失败: " + e.getMessage());
        }
    }

    /**
     * @description: 根据mediaId删除图文消息图片
     */
    @DeleteMapping("/gInfoImgDelete/{mediaId}")
    public AjaxResult GraphicInformationImageDelete(@PathVariable String mediaId){
        try {
            webChatMaterialService.GraphicInformationImageDelete(mediaId);
            return success("删除图文消息图片成功");
        } catch (Exception e) {
            return error("删除图文消息图片失败: " + e.getMessage());
        }
    }
}
