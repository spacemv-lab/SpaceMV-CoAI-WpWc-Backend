package com.txwx.social.crm.controller;

import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.txwx.social.crm.domain.po.TxwxGraphicInformationImagePO;
import com.txwx.social.crm.domain.vo.WebChatGraphicInformationImageVO;
import com.txwx.social.crm.domain.vo.WebChatMaterialPermanentVO;
import com.txwx.social.crm.service.IWebChatMaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
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
    @Operation(summary = "获取永久素材列表")
    public AjaxResult permanentList(@NotEmpty(message = "账号列表不能为空")@RequestParam("accountIds") List<Long> accountIds){
        try {
            List<WebChatMaterialPermanentVO> webChatMaterialPermanentVOS = webChatMaterialService.permanentList(accountIds);
            return success(webChatMaterialPermanentVOS);
        } catch (Exception e) {
            return error("获取永久素材列表失败: " + e.getMessage());
        }
    }

    /**
     * @description: 分页获取永久素材列表
     */
    @GetMapping("/permanentListByPage")
    @Operation(summary = "分页获取永久素材")
    public AjaxResult permanentListByPage(@Parameter(description = "pageNum") @RequestParam(defaultValue = "1") int pageNum,
                                         @Parameter(description = "pageSize")@RequestParam(defaultValue = "20") int pageSize,
                                          @NotEmpty(message = "账号列表不能为空")@RequestParam("accountIds") List<Long> accountIds){
        try {
            Map<String, Object> result = webChatMaterialService.permanentListByPage(pageNum, pageSize, accountIds);
            return success(result);
        } catch (Exception e) {
            return error("分页获取永久素材列表失败: " + e.getMessage());
        }
    }

    /**
     * @description: 获取永久素材总数
     */
    @GetMapping("/permanentTotalCount")
    @Operation(summary = "获取永久素材总数")
    public AjaxResult getPermanentTotalCount(@NotEmpty(message = "账号列表不能为空")@RequestParam("accountIds") List<Long> accountIds){
        try {
            int total = webChatMaterialService.getPermanentTotalCount(accountIds);
            return success(total);
        } catch (Exception e) {
            return error("获取永久素材总数失败: " + e.getMessage());
        }
    }

    /**
     * @description: 上传永久素材
     */
    @PostMapping("/permanentAdd")
    @Operation(summary = "上传永久素材")
    public AjaxResult permanentAdd(@Parameter(description = "文件")@RequestParam("file") MultipartFile file,
                                   @Parameter(description = "名称") @RequestParam(value = "name", required = false) String name,
                                   @NotBlank(message = "账号不能为空")@RequestParam("accountId") Long accountId){
        try {
            WebChatMaterialPermanentVO vo = new WebChatMaterialPermanentVO();
            vo.setFile(file);
            if(StringUtils.isNotEmpty(name)){
                vo.setName(name);
            }
            vo.setAccountId(accountId);
            WebChatMaterialPermanentVO webChatMaterialPermanentVO = webChatMaterialService.permanentAdd(vo);
            return success(webChatMaterialPermanentVO);
        } catch (Exception e) {
            return error("上传永久素材失败: " + e.getMessage());
        }
    }

    /**
     * @description: 根据mediaId删除永久素材
     */
    @DeleteMapping("/{accountId}/permanentDelete/{mediaId}")
    @Operation(summary = "删除永久素材")
    public AjaxResult permanentDelete(@PathVariable("accountId") Long accountId, @PathVariable("mediaId") String mediaId){
        try {
            webChatMaterialService.permanentDelete(mediaId, accountId);
            return success("删除永久素材成功");
        } catch (Exception e) {
            return error("删除永久素材失败: " + e.getMessage());
        }
    }

    /**
     * @description: 获取图文消息图片列表
     */
    @GetMapping("/gInfoImgList")
    @Operation(summary = "获取图文消息列表")
    public AjaxResult GraphicInformationImageList(@NotEmpty(message = "账号列表不能为空")@RequestParam("accountIds") List<Long> accountIds){
        try {
            List<TxwxGraphicInformationImagePO> list = webChatMaterialService.GraphicInformationImageList(accountIds);
            return success(list);
        } catch (Exception e) {
            return error("获取图文消息图片列表失败: " + e.getMessage());
        }
    }

    /**
     * @description: 分页获取图文消息图片列表
     */
    @Operation(summary = "分页获取图文消息列表")
    @GetMapping("/gInfoImgListByPage")
    public AjaxResult GraphicInformationImageListByPage(@RequestParam(defaultValue = "1") int pageNum,
                                                       @RequestParam(defaultValue = "20") int pageSize,
                                                        @NotEmpty(message = "账号列表不能为空")@RequestParam("accountIds") List<Long> accountIds){
        try {
            Map<String, Object> result = webChatMaterialService.GraphicInformationImageListByPage(pageNum, pageSize, accountIds);
            return success(result);
        } catch (Exception e) {
            return error("分页获取图文消息图片列表失败: " + e.getMessage());
        }
    }

    /**
     * @description: 获取图文消息图片总数
     */
    @GetMapping("/gInfoImgTotalCount")
    @Operation(summary = "获取图文消息总数")
    public AjaxResult getGraphicInformationImageTotalCount(@NotEmpty(message = "账号列表不能为空")@RequestParam("accountIds") List<Long> accountIds){
        try {
            int total = webChatMaterialService.getGraphicInformationImageTotalCount(accountIds);
            return success(total);
        } catch (Exception e) {
            return error("获取图文消息图片总数失败: " + e.getMessage());
        }
    }

    /**
     * @description: 上传图文消息图片
     */
    @PostMapping("/gInfoImgAdd")
    @Operation(summary = "上传图文消息列表")
    public AjaxResult GraphicInformationImageAdd(@RequestParam("file") MultipartFile file,
                                                   @RequestParam(value = "name", required = false) String name,
                                                 @NotBlank(message = "账号不能为空")@RequestParam("accountId") Long accountId){
        try {
            WebChatGraphicInformationImageVO vo = new WebChatGraphicInformationImageVO();
            vo.setFile(file);
            if(StringUtils.isNotEmpty(name)){
                vo.setName(name);
            }
            vo.setAccountId(accountId);
            WebChatGraphicInformationImageVO webChatGraphicInformationImageVO = webChatMaterialService.GraphicInformationImageAdd(vo);
            return success(webChatGraphicInformationImageVO);
        } catch (Exception e) {
            return error("上传图文消息图片失败: " + e.getMessage());
        }
    }

    /**
     * @description: 根据mediaId删除图文消息图片
     */
    @DeleteMapping("/{accountId}/gInfoImgDelete/{mediaId}")
    @Operation(summary = "查询图文消息详情")
    public AjaxResult GraphicInformationImageDelete(@PathVariable("accountId") Long accountId, @PathVariable("mediaId") String mediaId){
        try {
            webChatMaterialService.GraphicInformationImageDelete(mediaId, accountId);
            return success("删除图文消息图片成功");
        } catch (Exception e) {
            return error("删除图文消息图片失败: " + e.getMessage());
        }
    }


}
