package com.ruoyi.file.controller;

import com.ruoyi.system.api.domain.SysFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.file.FileUtils;
import com.ruoyi.file.service.ISysFileService;

/**
 * 按钮图片上传控制器
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/image")
public class ImageController
{
    private static final Logger log = LoggerFactory.getLogger(ImageController.class);

    @Autowired
    @Qualifier("minioSysFileServiceImpl")
    private ISysFileService sysFileService;

    /**
     * 按钮图片上传接口
     */
    @PostMapping("/upload")
    public R<String> uploadImage(@RequestParam("file") MultipartFile file)
    {
        try
        {
            // 验证文件类型
            if (!isValidImageType(file.getContentType()))
            {
                return R.fail("只支持图片格式文件上传（jpg, jpeg, png, gif, webp）");
            }

            // 上传并返回访问地址
            String url = sysFileService.uploadFile(file);

            // 返回包含详细信息的响应
            return R.ok(url);
        }
        catch (Exception e)
        {
            log.error("按钮图片上传失败", e);
            return R.fail("图片上传失败：" + e.getMessage());
        }
    }

    /**
     * 验证是否为有效的图片类型
     */
    private boolean isValidImageType(String contentType)
    {
        if (StringUtils.isEmpty(contentType))
        {
            return false;
        }
        
        return contentType.startsWith("image/") && 
               (contentType.contains("jpeg") || 
                contentType.contains("jpg") || 
                contentType.contains("png") || 
                contentType.contains("gif") || 
                contentType.contains("webp"));
    }
}