# 📋 变更日志 (Changelog)

本文件记录项目的所有重要变更。

格式基于 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.0.0/)，
版本号遵循 [语义化版本](https://semver.org/lang/zh-CN/)。

---

## [Unreleased]

### 计划中
- [ ] 前端界面开发（Vue.js / React）
- [ ] 移动端适配（响应式布局）
- [ ] Docker 容器化部署
- [ ] Kubernetes 编排配置
- [ ] 国际化支持（i18n）
- [ ] 单元测试覆盖率提升至 80%+
- [ ] API 性能优化
- [ ] 消息推送功能（邮件、短信）
- [ ] 数据统计与可视化仪表盘

### 待修复问题
- #123: 聊天服务在并发量高时可能出现消息丢失
- #456: 文件上传大文件时内存占用过高
- #789: 部分接口缺少参数校验

---

## [1.0.0] - 2024-XX-XX

### ✨ 新增 (Added)

#### 核心功能
- **微服务架构搭建**
  - 基于 Spring Cloud 2023.x 的 10 个微服务
  - Spring Cloud Gateway 统一网关（端口 9000）
  - Nacos 服务注册与发现中心

- **用户服务 (pet-user)**
  - 用户注册与登录系统
  - JWT Token 身份认证
  - 个人信息管理（头像、昵称等）
  - 关注/粉丝关系管理
  - 用户权限控制（普通用户/管理员）

- **宠物服务 (pet-pet)**
  - 宠物信息 CRUD 管理
  - 宠物列表展示与搜索
  - 宠物分类筛选（狗/猫/鸟/其他）
  - 宠物收藏功能
  - 宠物详情展示

- **领养系统**
  - 在线领养申请提交
  - 领养审核流程（待审核/已通过/已拒绝）
  - 领养记录查询
  - 领养协议确认机制

- **社交服务 (pet-social)**
  - 社交帖子发布（支持图文混排）
  - 评论与回复功能
  - 点赞功能
  - 话题标签系统
  - 热门话题推荐
  - 文件上传（阿里云 OSS）

- **聊天服务 (pet-chat)**
  - WebSocket 实时聊天
  - 私信功能
  - 群聊支持（未来扩展）
  - 聊天记录存储
  - AI 智能问答集成（通义千问大模型）

- **故事服务 (pet-story)**
  - 宠物故事分享
  - 故事评论互动
  - 故事列表展示
  - 温馨故事推荐

- **捐赠服务 (pet-donation)**
  - 爱心捐赠功能
  - 捐赠记录管理
  - 捐赠金额统计
  - 捐赠者感谢展示

- **招聘服务 (pet-recruitment)**
  - 职位信息发布
  - 求职申请提交
  - 职位搜索与筛选
  - 招聘状态管理

- **养宠贴士服务 (pet-tips)**
  - 养宠知识文章分享
  - 分类浏览（饮食/医疗/训练等）
  - AI 智能问答（基于向量数据库检索）
  - 向量化知识库管理
  - 相似度搜索推荐

- **管理员后台 (pet-admin)**
  - 用户管理系统
  - 宠物信息审核
  - 领养申请审批
  - 内容管理（帖子、评论）
  - 系统配置管理
  - 数据统计面板

#### 技术特性
- **安全认证体系**
  - 全局 JWT Token 认证过滤器
  - 白名单路径免认证机制
  - Token 自动刷新（24小时有效期）
  - 支持环境变量配置密钥

- **AI 能力集成**
  - 阿里云 DashScope 大模型接入
  - 通义千问智能问答
  - 文本向量化嵌入（text-embedding-v3）
  - 向量数据库相似度检索
  - RAG（检索增强生成）架构

- **基础设施**
  - MySQL 8.0 数据库（15+ 张表）
  - Redis 缓存集群
  - RabbitMQ 消息队列
  - Druid 数据库连接池
  - MyBatis-Plus ORM 框架
  - Knife4j API 文档自动生成

### 🔒 安全改进 (Security)

- **敏感信息外部化** ⭐⭐⭐
  - 所有密码支持环境变量配置（DB、Redis、RabbitMQ）
  - JWT 密钥支持多级配置（系统属性 > 环境变量 > 默认值）
  - 内网 IP 地址移除，改为可配置项
  - 添加 `.gitignore` 防止敏感文件泄露
  - 提供 `.env.example` 配置模板

- **代码安全审计**
  - 移除所有硬编码密码和密钥
  - 添加详细的安全警告注释
  - 生产环境配置检查清单

### 📝 文档完善 (Documentation)

- **README.md** - 项目完整说明文档
  - 项目简介与功能特性
  - 系统架构图（ASCII 图）
  - 技术栈详细说明
  - 快速开始指南（6 步上手）
  - API 接口使用示例
  - 配置说明文档
  - 安全注意事项

- **CONTRIBUTING.md** - 贡献者指南
  - 行为准则
  - 开发流程规范
  - 代码风格指南
  - 提交信息规范
  - PR 流程说明
  - Issue 反馈模板

- **配置文件注释**
  - 所有 application.yaml 添加中文注释
  - 每个配置项都有详细说明
  - 标注生产环境必改项

- **SQL 初始化脚本**
  - `sql/init.sql` 完整的数据库建表脚本
  - 包含表结构、索引、注释

### 🐛 Bug 修复 (Fixed)

- **初始版本，无已知 Bug**

### 🔧 技术债务 (Technical Debt)

- [ ] 单元测试覆盖率较低（建议提升至 80%+）
- [ ] 部分服务缺少集成测试
- [ ] 日志系统需要统一规范
- [ ] 异常处理需要更细粒度
- [ ] 缓存策略需要优化
- [ ] API 接口需要限流保护

### 📦 依赖更新 (Dependencies)

#### 核心依赖版本
```xml
<java.version>17</java.version>
<spring-boot.version>3.2.x</spring-boot.version>
<spring-cloud.version>2023.x</spring-cloud.version>
<mybatis-plus.version>3.5.x</mybatis-plus.version>
<knife4j.version>4.x</knife4j.version>
```

#### 主要依赖
- Spring Boot 3.2.x
- Spring Cloud 2023.x (Gateway, OpenFeign, LoadBalancer)
- Nacos 2.3.x (Discovery & Config)
- MyBatis-Plus 3.5.x
- Knife4j 4.x (API Documentation)
- Lombok (Code Generation)
- Hutool (Utility Library)
- JJWT (JWT Library)
- Alibaba Druid (Connection Pool)
- Spring AI (AI Integration)
- Aliyun OSS SDK
- RabbitMQ AMQP Client

---

## 版本说明

### 版本号规则

本项目遵循 [语义化版本](https://semver.org/lang/zh-CN/) 2.0.0 规范：

```
主版本号.次版本号.修订号 (MAJOR.MINOR.PATCH)
```

- **MAJOR（主版本）**：不兼容的 API 修改
- **MINOR（次版本）**：向下兼容的功能性新增
- **PATCH（修订号）**：向下兼容的问题修正

### 发布计划

| 版本 | 计划日期 | 主要内容 |
|------|---------|---------|
| v1.0.0 | 2024-Q1 | 初始版本发布 |
| v1.1.0 | 2024-Q2 | 前端界面、Docker 支持 |
| v1.2.0 | 2024-Q3 | 移动端适配、性能优化 |
| v2.0.0 | 2025-Q1 | 架构升级、新功能模块 |

### 变更类型说明

- **新增 (Added)**: 新功能
- **变更 (Changed)**: 已有功能的变更
- **弃用 (Deprecated)**: 即将删除的功能
- **移除 (Removed)**: 已删除的功能
- **修复 (Fixed)**: Bug 修复
- **安全 (Security)**: 安全相关改动

---

## 如何查看变更历史

### Git 命令

```bash
# 查看某个版本的变更
git log v1.0.0..v1.1.0 --oneline

# 查看某个文件的变更历史
git log --follow -p README.md

# 查看贡献者统计
git shortlog -sn --since="2024-01-01"
```

### GitHub 功能

- **Releases 页面**: https://github.com/your-username/pet-adoption-platform/releases
- **Commits 页面**: https://github.com/your-username/pet-adoption-platform/commits/main
- **Contributors**: https://github.com/your-username/pet-adoption-platform/contributors

---

## 致谢

感谢所有为项目做出贡献的开发者和社区成员！

特别感谢：
- Spring 团队提供优秀的框架
- 阿里云提供 Nacos 和 DashScope 服务
- 所有 Issue 提交者和 PR 贡献者

---

<div align="center">

**🐾 让我们共同打造最好的宠物领养平台！**

*最后更新时间：2024年XX月XX日*

</div>

[Unreleased]: https://github.com/your-username/pet-adoption-platform/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/your-username/pet-adoption-platform/releases/tag/v1.0.0
