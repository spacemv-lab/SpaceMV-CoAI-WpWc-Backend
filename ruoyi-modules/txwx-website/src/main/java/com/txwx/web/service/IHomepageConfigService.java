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
package com.txwx.web.service;

import com.txwx.web.domain.*;

import java.util.List;

/**
 * 首页配置Service接口
 * 
 * @author txwx
 * @date 2025-12-06
 */
public interface IHomepageConfigService 
{
    /**
     * @description: 正式环境展示首页配置
     */
    TxwxHomePage displayConfig();

    /**
     * @description: 预览首页配置，从临时表中查询数据
     */
    TxwxHomePageTemp previewConfig();

    /**
     * @description: 保存首页配置到临时表
     */
    int saveHomePageTemp(TxwxHomePageTemp txwxHomePageTemp);

    /**
     * @description: 发布首页配置到正式表
     */
    int publishHomePage();
}