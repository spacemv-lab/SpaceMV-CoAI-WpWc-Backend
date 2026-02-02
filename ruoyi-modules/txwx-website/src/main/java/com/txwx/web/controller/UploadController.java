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
package com.txwx.web.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.api.RemoteFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;

/**
 * 文件上传Controller
 * 
 * @author txwx
 * @date 2025-12-06
 */
@RestController
@RequestMapping("/upload")
public class UploadController
{
    @Autowired
    private RemoteFileService remoteFileService;

    /**
     * 上传按钮图片
     */
    @Log(title = "按钮图片上传", businessType = BusinessType.UPLOAD)
    @PostMapping("/image")
    public AjaxResult uploadButtonImage(@RequestParam("file") MultipartFile file)
    {
        try
        {
            // 调用远程文件服务上传图片
            R<String> result = remoteFileService.uploadImage(file);
            if (result.getCode() == 200)
            {
                AjaxResult ajax = AjaxResult.success();
                ajax.put("url", result.getData());
                return ajax;
            }
            else
            {
                return AjaxResult.error(result.getMsg());
            }
        }
        catch (Exception e)
        {
            return AjaxResult.error("上传失败：" + e.getMessage());
        }
    }
}