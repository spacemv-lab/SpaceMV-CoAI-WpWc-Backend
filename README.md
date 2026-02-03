<div align="center">
<h1 style="margin: 30px 0 30px; font-weight: bold;">SpaceMV-CoAI-Wp/Wc Backend: 产业星球运营智能体服务端</h1>
<a href="./LICENSE"><img src="https://img.shields.io/github/license/mashape/apistatus.svg" alt="License"></a> <a href="https://github.com/tianxunweixiao/SpaceMV-CoAI-WpWc-Backend"><img src="https://img.shields.io/badge/Maintained%3F-yes-green.svg" alt="Maintenance"></a> 
</div>
<div align="center">
<a href="./README.md"><strong>简体中文</strong></a> | <a href="./README-EN.md"><strong>English</strong></a>
</div>
&nbsp;&nbsp;
<div align="center">
<img width="800" height="450" alt="SpaceMV-COO" src="./public/spacemv_coo.png" />
</div>

[SpaceMV-CoAI-Wp/Wc](https://github.com/tianxunweixiao/SpaceMV-CoAI)是由成都天巡微小卫星科技有限责任公司(以下简称天巡微小公司)研发的一款集官网内容管理、自媒体一站式创作发布、运营数据分析于一体的产业星球运营智能体，旨在解决企业内容运营面临的多平台内容分发耗时耗力、运营数据分散难以洞察、内容审核流程复杂低效的问题。

[SpaceMV-CoAI-Wp/Wc](https://github.com/tianxunweixiao/SpaceMV-CoAI)作为企业内容中枢，具备官网版面定制、文章内容快速更新发布功能，可一站式完成自媒体创作与多平台分发，且有审核机制保障内容安全，还能通过多维数据分析，为运营决策提供支撑，助力高效开展品牌运营。

`SpaceMV-CoAI-Wp/Wc Backend`作为平台的核心后端组件，为前端提供逻辑实现和交互接口。


## **📖 目录**

* [核心模块](#-核心模块)
* [技术架构](#-技术架构)
* [功能特性](#-功能特性)
* [快速开始](#-快速开始)
* [贡献指南](#-贡献指南)
* [许可证](#-许可证)
* [联系方式](#-联系方式)
* [贡献者](#-贡献者)
* [待办事项](#-待办事项)

## **🧩 核心模块**

SpaceMV-CoAI-Wp/Wc Backend 基于 [RuoYi-Cloud](https://gitee.com/y_project/RuoYi-Cloud)的master分支开发，在若依框架基础上，我们拓展了以下四个模块：

| 模块 | 目录 | 说明 |
| :---- | :---- | :---- |
| **Clickhouse公共服务** | ruoyi-common\_ruoyi-common-clickhouse | Clickhouse数据库集成。 |
| **http公共服务** | ruoyi-common\_ruoyi-common-http | http调用集成。 |
| **官网网页内容管理** | ruoyi-modules\_txwx-website | 按照天巡微小公司官网样式进行官网网页内容自定义。 |
| **微信公众号内容管理** | ruoyi-modules\_txwx-webchatcrm | 微信公众号内容编辑、审核和发布。 |

## **🏗 技术架构**

### **目录结构**

~~~
server
├── docker                // 服务构建脚本     
├── ruoyi-ui              // 前端构建包 [80]
├── ruoyi-gateway         // 网关模块 [8080]
├── ruoyi-auth            // 认证中心 [9200]
├── ruoyi-api             // 接口模块
│       └── ruoyi-api-system                          // 系统接口
├── ruoyi-common          // 通用模块
│       └── ruoyi-common-clickhouse                   // Clickhouse封装
│       └── ruoyi-common-core                         // 核心模块
│       └── ruoyi-common-datascope                    // 权限范围
│       └── ruoyi-common-datasource                   // 多数据源
│       └── ruoyi-common-http                         // http调用封装
│       └── ruoyi-common-log                          // 日志记录
│       └── ruoyi-common-redis                        // 缓存服务
│       └── ruoyi-common-seata                        // 分布式事务
│       └── ruoyi-common-security                     // 安全模块
│       └── ruoyi-common-sensitive                    // 数据脱敏
│       └── ruoyi-common-swagger                      // 系统接口
├── ruoyi-modules         // 业务模块
│       └── ruoyi-system                              // 系统模块 [9201]
│       └── ruoyi-gen                                 // 代码生成 [9202]
│       └── ruoyi-job                                 // 定时任务 [9203]
│       └── ruoyi-file                                // 文件服务 [9300]
│       └── txwx-website                              // 官网内容管理服务 [9203]
│       └── txwx-webchatcrm                           // 微信公众号服务 [9205]
├── ruoyi-visual          // 图形化管理模块
│       └── ruoyi-visual-monitor                      // 监控中心 [9100]
├──pom.xml                // 公共依赖
~~~


### 技术栈

| 领域 | 技术选型 |
| :--- | :--- |
| **编程语言** | **java** |
| **数据库** | **ClickHouse** |
| **对象存储** | **MinIO** | 


## **✨ 功能特性**

### 1. 系统管理模块

* **菜单管理**: 支持菜单的创建、编辑、权限控制等功能。
* **部门管理**: 支持部门的层级结构管理。
* **岗位管理**: 支持岗位的创建、编辑、分配等功能。
* **用户管理**: 支持用户的增删改查、角色分配、密码重置等功能。
* **角色管理**: 支持角色的创建、权限分配、用户关联等功能。
* **字典管理**: 支持系统中各种字典数据的管理，如性别、状态等预定义枚举值。


### 2. 内容管理模块

* **公司信息**: 管理公司的基本信息和展示内容。
* **主页内容**: 管理网站主页的展示内容。
* **产品管理**: 管理公司产品的信息和展示。
* **顶部按钮**: 管理网站顶部按钮的配置。


### 3. 文章管理模块

* **文章列表**: 管理所有文章的列表和状态。  
* **文章编辑**: 支持富文本编辑器，方便文章内容的编辑。  

### 4. 素材管理模块

* **图文素材**: 管理图文结合的素材内容。  
* **永久素材**: 管理可重复使用的永久素材。  
* **素材上传**: 支持多种格式的素材上传。  


## **🚀 快速开始**

### **前置条件**
* **JDK 17+** 
* **Docker** 
* **Docker Compose**  
* **Maven V3+**  
* **MinIO** 

### **系统部署和启动**
```bash
# 克隆仓库  
git clone https://github.com/tianxunweixiao/SpaceMV-CRM-backend.git   
cd SpaceMV-CRM-backend

# 构建
mvn clean install

# 部署前准备
(1)将前端构建的官网dist包放到代码工程中的ruoyi-ui/dist目录下

(2)将前端构建的后台管理dist包放到代码工程中的ruoyi-ui/dist-console目录下

(3)执行部署包的拷贝
cd ./docker
sh copy.sh

# 构建基础Docker镜像(mysql、redis)以及启动并初始化
sh deploy.sh base

# 构建nacos Docker镜像以及启动并初始化
sh deploy.sh nacos

# 构建服务Docker镜像(含txwx-website、txwx-webchatcrm)以及启动并初始化
sh deploy.sh modules

# 打开宿主机端口
sh deploy.sh port

```


SpaceMV-CoAI-Wp/Wc 前端仓库可参考[SpaceMV-CoAI-Wp/Wc-frontend](https://github.com/tianxunweixiao/SpaceMV-CoAI-WpWc-Frontend)


## **🤝 贡献指南**

我们非常欢迎社区开发者参与 SpaceMV-CoAI-Wp/Wc Backend 的建设！如果您有任何改进建议或发现了 Bug，请遵循以下流程：

1. **Fork 本仓库**：点击右上角的 Fork 按钮将项目复制到您的 GitHub 账户。  
2. **创建分支**：从 main 分支切出一个新分支用于开发。  
   git checkout \-b feature/AmazingFeature  
3. **提交更改**：确保代码风格统一，并撰写清晰的 Commit Message。  
   git commit \-m 'feat: Add some AmazingFeature'  
4. **推送分支**：  
   git push origin feature/AmazingFeature  
5. **提交 Pull Request**：在 GitHub 上发起 PR，并详细描述您的更改内容。

**开发建议**：

* 添加新的微服务时，请放置在ruoyi-modules下  
* 公共服务组件请放置在ruoyi-common下


## **📄 许可证**

本项目采用 **MIT** 许可证。

* Copyright (c) 2018 RuoYi
* Copyright (c) 2026 成都天巡微小卫星科技有限责任公司

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

## **📮 联系方式**

如有任何问题、建议或商务合作需求，请联系项目维护团队。

* **Email**: code@spacemv.com  
* **Issues**: [GitHub Issues](https://github.com/tianxunweixiao/SpaceMV-ScAI-backend/issues)
* **Discussions**: [GitHub Discussions](https://github.com/tianxunweixiao/SpaceMV-CoAI-WpWc-Backend/discussions)

更多信息可关注公司微信公众号：

<img width="106" height="106" alt="image" src="https://github.com/user-attachments/assets/69a02ad0-422c-422a-bf5f-9b7890cf31ab" />


## **✨ 贡献者**

<div>
  <img src="./public/github.png" style="cursor: pointer;" width="50" height="50" alt="spacemvpy" title="spacemvpy" />
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  <img src="./public/github.png" style="cursor: pointer;" width="50" height="50" alt="spacemvwnn" title="spacemvwnn" />
</div>

## ✅ 待办事项

<table>
  <tr>
    <th style="width: 150px;">流程/类别</th>
    <th style="width: 180px;">功能里程碑</th>
    <th style="width: 450px;">核心内容</th>
    <th style="width: 120px; white-space: nowrap;">预计发布时间</th>
  </tr>
  <tr>
    <td>生产</td>
    <td>可交互数据报表嵌入推文</td>
    <td>支持编辑和发布可嵌入交互式数据报表的自媒体推文，实现数据可视化内容一键生成</td>
    <td>2026年4月上旬</td>
  </tr>
  <tr>
    <td>生产</td>
    <td>官网版面插件化</td>
    <td>实现官网版面布局的完全可定制化，支持插件导入、页面布局/样式+内容的可编辑可定制</td>
    <td>2026年5月</td>
  </tr>
  <tr>
    <td>监测 （流量监测/防篡改等安全监测）</td>
    <td>监测工具/Agent</td>
    <td>以工具/Agent形式提供流量监测、页面防篡改等安全能力，可实时采集并分析流量行为、校验页面完整性，发现异常时自动触发告警</td>
    <td>待定</td>
  </tr>
  <tr>
    <td>统计</td>
    <td>数据看板</td>
    <td>支持官网浏览数据看板和自媒体推文流量数据分析、看客数据分析</td>
    <td>2026年2月</td>
  </tr>
  <tr>
    <td>分析（诊断）</td>
    <td>内容分析工具/Agent</td>
    <td>基于内容与运营数据，以工具/Agent形式，提供自动分析文章传播规律、用户互动归因，定位高价值内容特征等功能，为内容策略优化提供数据依据</td>
    <td>待定</td>
  </tr>
  <tr>
    <td>反馈（质量/绩效评价）</td>
    <td>质量/绩效评价工具/Agent</td>
    <td>以工具/Agent形式，提供评估文章质量（原创度、合规性、传播力等），并针对运营人员生成基于发布量、互动量、转化效果等数据的量化绩效评分，为内容优化与人员评估提供数据支撑</td>
    <td>待定</td>
  </tr>
  <tr>
    <td>生态</td>
    <td>小红书深度集成</td>
    <td>支持小红书平台集成及数据看板分析，全面覆盖主流自媒体平台</td>
    <td>2026年3月</td>
  </tr>
</table>
