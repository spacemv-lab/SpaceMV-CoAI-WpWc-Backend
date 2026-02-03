<div align="center">
<h1 style="margin: 30px 0 30px; font-weight: bold;">SpaceMV-CoAI-Wp/Wc Backend: Industrial Planet Operations AI Agent Server-side</h1>
<a href="./LICENSE"><img src="https://img.shields.io/github/license/mashape/apistatus.svg" alt="License"></a> <a href="https://github.com/tianxunweixiao/SpaceMV-CoAI-WpWc-Backend"><img src="https://img.shields.io/badge/Maintained%3F-yes-green.svg" alt="Maintenance"></a>
</div>
<div align="center">
<a href="./README.md"><strong>简体中文</strong></a> | <a href="./README_EN.md"><strong>English</strong></a>
</div>
&nbsp;&nbsp;
<div align="center">
<img width="800" height="450" alt="SpaceMV-COO" src="./public/spacemv_coo.png" />
</div>


[SpaceMV-CoAI-Wp/Wc](https://github.com/tianxunweixiao/SpaceMV-CoAI) is an Industrial Planet Operations AI Agent developed by Chengdu Tianxun Microsatellite Technology Co., Ltd.(hereinafter referred to as "Tianxun Micro Company"), integrating official website content management, one-stop self-media creation and publishing, and operational data analysis. It aims to solve the problems faced by enterprise content operations, such as time-consuming multi-platform content distribution, scattered operational data that is difficult to insight into, and complex and inefficient content review processes.

[SpaceMV-CoAI-Wp/Wc](https://github.com/tianxunweixiao/SpaceMV-CoAI) serves as the enterprise content hub, featuring official website layout customization, quick article content update and publishing functions. It can complete self-media creation and multi-platform distribution in one stop, with review mechanisms to ensure content security, and can provide support for operational decision-making through multi-dimensional data analysis, helping to efficiently carry out brand operations.

`SpaceMV-CoAI-Wp/Wc Backendend` As the core backend component of the platform, it provides logical implementation and interactive interfaces for the frontend.



## **Table of Contents**

* [Core Modules](#core-modules)
* [Technical Architecture](#technical-architecture)
* [Features](#features)
* [Quick Start](#quick-start)
* [Contribution Guide](#contribution-guide)
* [Contact](#contact)
* [Contributors](#contributors)
* [To-Do List](#to-do-list)


## **Core Modules**

SpaceMV-CoAI-Wp/Wc Backend is developed based on the master branch of [RuoYi-Cloud](https://gitee.com/y_project/RuoYi-Cloud). Building upon the Ruoyi framework, we have expanded the following four modules:

| Module | Directory | Description |
| :---- | :---- | :---- |
| **Clickhouse public service** | ruoyi-common\_ruoyi-common-clickhouse | Clickhouse database integration |
| **http public service** | ruoyi-common\_ruoyi-common-http | http invoke integration |
| **Official Website Content Management** | ruoyi-modules\_txwx-website | Customize the official website content based on the design of Tianxun Micro Company's website. |
| **WeChat Official Account Content Management** | ruoyi-modules\_txwx-webchatcrm | Content editing, review, and publishing for WeChat Official Account. |

## **Technical Architecture**

### **Directory Structure**

~~~
server
├── docker                // Service build scripts     
├── ruoyi-ui              // Frontend build package [80]
├── ruoyi-gateway         // Gateway module [8080]
├── ruoyi-auth            // Authentication center [9200]
├── ruoyi-api             // Interface module
│       └── ruoyi-api-system                          // System interface
├── ruoyi-common          // Common module
│       └── ruoyi-common-clickhouse                   // ClickhouseEncapsulation
│       └── ruoyi-common-core                         // Core module
│       └── ruoyi-common-datascope                    // Permission scope
│       └── ruoyi-common-datasource                   // Multiple data sources
│       └── ruoyi-common-http                         // HTTP call encapsulation
│       └── ruoyi-common-log                          // Log recording
│       └── ruoyi-common-redis                        // Cache service
│       └── ruoyi-common-seata                        // Distributed transactions
│       └── ruoyi-common-security                     // Security module
│       └── ruoyi-common-sensitive                    // Data masking
│       └── ruoyi-common-swagger                      // System interface
├── ruoyi-modules         // Business module
│       └── ruoyi-system                              // System module [9201]
│       └── ruoyi-gen                                 // Code generation [9202]
│       └── ruoyi-job                                 // Scheduled tasks [9203]
│       └── ruoyi-file                                // File service [9300]
│       └── txwx-website                              // Official website content management service [9203]
│       └── txwx-webchatcrm                           // WeChat official account service [9205]
├── ruoyi-visual          // Graphical management module
│       └── ruoyi-visual-monitor                      // Monitoring center [9100]
├──pom.xml                // Common dependencies
~~~


### **Technology Stack**

| Domain | Technology Selection |
| :--- | :--- |
| **Programming Language** | **java** |
| **Database** | **ClickHouse** |
| **OSS** | **MinIO** | 


## **Features**

### **1. System Management Module**

* **Menu Management**: Support menu creation, editing, permission control and other functions.
* **Department Management**: Support department hierarchy management.
* **Position Management**: Support position creation, editing, allocation and other functions.
* **User Management**: Support user CRUD, role assignment, password reset and other functions.
* **Role Management**: Support role creation, permission allocation, user association and other functions.
* **Dictionary Management**: Support management of various dictionary data in the system, such as predefined enumeration values like gender, status, etc.


### **2. Content Management Module**

* **Company Information**: Manage company basic information and display content.
* **Homepage Content**: Manage website homepage display content.
* **Product Management**: Manage company product information and display.
* **Top Button**: Manage website top button configuration.


### **3. Article Management Module**

* **Article List**: Manage all article lists and statuses.  
* **Article Editing**: Support rich text editor for easy article content editing.  

### **4. Material Management Module**

* **Image-Text Materials**: Manage image-text combined material content.  
* **Permanent Materials**: Manage reusable permanent materials.  
* **Material Upload**: Support multiple format material uploads.
 


## **Quick Start**

### **Precondition**
* **JDK 17+** 
* **Docker** 
* **Docker Compose**  
* **Maven V3+**  
* **MinIO** 

### **System Deployment and Startup**
```bash
# Clone repository  
git clone https://github.com/tianxunweixiao/SpaceMV-CRM-backend.git   
cd SpaceMV-CRM-backend

# Build
mvn clean install

# Pre-deployment preparation
(1)Place the front-endBuildofficial websitedistpackage into the code projectruoyi-ui/distdirectory

(2)Place the front-endBuildadmin paneldistpackage into the code projectruoyi-ui/dist-consoledirectory

(3)Execute deployment package copy
cd ./docker
sh copy.sh

# BuildbaseDockerimage(mysql、redis)and start initialization
sh deploy.sh base

# Buildnacos Dockerimageand start initialization
sh deploy.sh nacos

# BuildserviceDockerimage(includingtxwx-website、txwx-webchatcrm)and start initialization
sh deploy.sh modules

# Open the host machineport
sh deploy.sh port

```


For the SpaceMV-CoAI-Wp/Wc frontend repository, please refer to[SpaceMV-CoAI-Wp/Wc-frontend](https://github.com/tianxunweixiao/SpaceMV-CoAI-WpWc-Frontend)


## **Contribution Guide**

We warmly welcome community developers to participate in the construction of SpaceMV-CoAI-Wp/Wc Backend! If you have any improvement suggestions or found bugs, please follow the following process:

1. **Fork this repository**: Click the Fork button in the upper right corner to copy the project to your GitHub account.  
2. **Create branch**: Create a new branch from main branch for development.  
   git checkout \-b feature/AmazingFeature  
3. **Commit changes**: Ensure code style is consistent and write clear Commit Message.  
   git commit \-m 'feat: Add some AmazingFeature'  
4. **Push branch**:  
   git push origin feature/AmazingFeature  
5. **Submit Pull Request**: Initiate PR on GitHub and describe your changes in detail.

**Development Suggestions**：

* When adding a new microservice, please place it under the ruoyi-modules directory.
* Please place common service components under the ruoyi-common directory.


## **License**

This project is licensed under the MIT License

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

## **Contact**

For any questions, suggestions or business cooperation needs, please contact the project maintenance team.

* **Email**: code@spacemv.com  
* **Issues**: [GitHub Issues](https://github.com/tianxunweixiao/SpaceMV-ScAI-backend/issues)
* **Discussions**: [GitHub Discussions](https://github.com/tianxunweixiao/SpaceMV-CoAI-WpWc-Backend/discussions)

For more information, you can follow the company's WeChat official account:

<img width="106" height="106" alt="image" src="https://github.com/user-attachments/assets/69a02ad0-422c-422a-bf5f-9b7890cf31ab" />


## **Contributors**

<div>
  <img src="./public/github.png" style="cursor: pointer;" width="50" height="50" alt="spacemvpy" title="spacemvpy" />
  &nbsp;
  <img src="./public/github.png" style="cursor: pointer;" width="50" height="50" alt="spacemvwnn" title="spacemvwnn" />
</div>

## **To-Do List**

<table>
  <tr>
    <th style="width: 150px;">Process/Category</th>
    <th style="width: 180px;">Feature Milestone</th>
    <th style="width: 450px;">Core Content</th>
    <th style="width: 120px; white-space: nowrap;">Estimated Release Date</th>
  </tr>
  <tr>
    <td>Production</td>
    <td>Embedding Interactive Data Reports in Posts</td>
    <td>Supports editing and publishing self-media posts with embedded interactive data reports, enabling one-click generation of data visualization content.</td>
    <td>Early April 2026</td>
  </tr>
  <tr>
    <td>Production</td>
    <td>Plugin-based Official Website Layout</td>
    <td>Enables fully customizable official website layouts, supporting plugin imports and editable/customizable page layouts/styles and content.</td>
    <td>May 2026</td>
  </tr>
  <tr>
    <td>Monitoring (Traffic Monitoring/Tamper-proofing, etc.)</td>
    <td>Monitoring Tool/Agent</td>
    <td>Provides security capabilities such as traffic monitoring and page tamper-proofing in the form of tools/agents. Can collect and analyze traffic behavior in real-time, verify page integrity, and automatically trigger alerts upon detecting anomalies.</td>
    <td>TBD</td>
  </tr>
  <tr>
    <td>Statistics</td>
    <td>Data Dashboard</td>
    <td>Supports data dashboards for official website traffic analysis and self-media post traffic data analysis, including audience analysis.</td>
    <td>February 2026</td>
  </tr>
  <tr>
    <td>Analysis (Diagnostics)</td>
    <td>Content Analysis Tool/Agent</td>
    <td>Based on content and operational data, provides automated analysis of article dissemination patterns, user interaction attribution, and identification of high-value content features in the form of tools/agents, offering data support for content strategy optimization.</td>
    <td>TBD</td>
  </tr>
  <tr>
    <td>Feedback (Quality/Performance Evaluation)</td>
    <td>Quality/Performance Evaluation Tool/Agent</td>
    <td>Provides evaluation of article quality (originality, compliance, dissemination power, etc.) and generates quantitative performance scores for operational personnel based on data such as publication volume, interaction volume, and conversion effectiveness in the form of tools/agents, offering data support for content optimization and personnel assessment.</td>
    <td>TBD</td>
  </tr>
  <tr>
    <td>Ecosystem</td>
    <td>Xiaohongshu Deep Integration</td>
    <td>Supports integration with the Xiaohongshu platform and its data dashboard analysis, achieving comprehensive coverage of mainstream self-media platforms.</td>
    <td>March 2026</td>
  </tr>
</table>

