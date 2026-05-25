## 相关Issue
Closes #(issue number) <!-- 如果没有关联 Issue 可以删除此行 -->

## 变更类型

请勾选适用的类型（可多选）：

- [ ] 🆕 新功能 (feature) - 新增功能或能力
- [ ] 🐛 Bug修复 (bugfix) - 修复已知问题
- [ ] 📝 文档更新 (documentation) - 文档、注释、README等
- [ ] 🎨 样式调整 (style) - 代码格式化（不影响逻辑）
- [ ] ♻️ 重构 (refactor) - 代码重构（非新功能非修复）
- [ ] ⚡ 性能优化 (performance) - 性能提升
- [ ] 🧪 测试相关 (test) - 测试用例或测试工具
- [ ] 🔧 构建/工具 (chore) - 构建脚本、依赖更新等
- [ ] 🔒 安全修复 (security) - 安全漏洞修复

## 变更描述

### 这个PR做了什么？

<!-- 用2-3句话简要描述本次变更 -->

### 详细变更列表

<!-- 列出所有主要变更点 -->

#### 1. 功能/模块名称
- 变更点 1
- 变更点 2

#### 2. 功能/模块名称
- 变更点 1
- 变更点 2

### 技术细节

**涉及的文件：**

```diff
# 可选：贴出关键的 diff 片段
+ 新增的代码行
- 删除的代码行
~ 修改的代码行
```

**数据库变更（如有）：**

```sql
-- DDL/DML 语句
ALTER TABLE xxx ADD COLUMN yyy VARCHAR(100);
```

**配置文件变更（如有）：**

```yaml
# 配置项修改说明
key: old_value -> new_value
```

## 🧪 测试说明

### 如何测试这个PR？

请提供清晰的测试步骤：

#### 前置条件
- [ ] 已启动 Nacos (localhost:8848)
- [ ] 已启动 Redis (localhost:6379)
- [ ] 已启动 RabbitMQ (localhost:5672)
- [ ] 已初始化数据库 (sql/init.sql)
- [ ] 已配置 .env 文件

#### 测试步骤

**场景 1：正常流程**

```bash
# 1. 启动相关微服务
# 2. 执行以下命令/操作
curl -X POST http://localhost:9000/api/xxx \
  -H "Content-Type: application/json" \
  -d '{...}'

# 3. 预期结果
# 应该返回 { code: 200, data: {...} }
```

**场景 2：边界条件**

```bash
# 测试边界值、空值、异常输入
```

**场景 3：错误处理**

```bash
# 测试异常情况的处理
```

#### 自动化测试

- [ ] 单元测试已添加并通过 (`mvn test`)
- [ ] 集成测试已添加并通过
- [ ] 测试覆盖率：__% (运行 `mvn jacoco:report` 查看)

**新增测试文件（如有）：**

```
src/test/java/com/pet/xxx/XxxTest.java
```

## 📸 截图/GIF演示（强烈推荐）

如果涉及 UI 变更或复杂流程，请附上截图或录屏：

### 变更前（Before）
<!-- 截图 -->

### 变更后（After）
<!-- 截图 -->

### 操作演示（GIF/视频）
<!-- 录屏链接或GIF -->

## ✅ 检查清单

请确保完成以下所有检查项：

### 代码质量
- [ ] 代码遵循项目编码规范（见 CONTRIBUTING.md）
- [ ] 无编译错误和警告（`mvn clean compile` 通过）
- [ ] 代码格式统一（IDE 格式化）
- [ ] 移除了调试代码（System.out.println、debugger 等）
- [ ] 无 TODO/FIXME/HACK 注释（除非必要并已记录）

### 文档与注释
- [ ] 公共 API 添加了 Javadoc 注释
- [ ] 复杂逻辑添加了行内注释
- [ ] 更新了相关的 README 或文档
- [ ] 配置文件的变更已注释说明
- [ ] SQL 脚本有完整注释

### 测试验证
- [ ] 本地测试通过（手动 + 自动化）
- [ ] 新增功能包含单元测试
- [ ] 修改了已有功能并回归测试
- [ ] 边界条件和异常情况已测试
- [ ] 未破坏现有功能

### 安全检查
- [ ] 无敏感信息泄露（密码、密钥、Token）
- [ ] 输入参数已做校验（@Valid、@NotNull）
- [ ] SQL 注入防护（使用 MyBatis-Plus 参数绑定）
- [ ] XSS 防护（前端转义）
- [ ] 权限校验正确（@PreAuthorize 或自定义注解）

### 性能考虑
- [ ] 无明显的性能问题（N+1查询、大事务等）
- [ ] 数据库查询有必要的索引
- [ ] 缓存策略合理（如适用）
- [ ] 大数据处理使用了分页

### 兼容性
- [ ] 向后兼容（不破坏现有 API）
- [ ] 如有不兼容变更，已在 CHANGELOG 中注明
- [ ] 数据库迁移脚本已提供（如有 Schema 变更）

## 🔍 Review 重点

希望 reviewers 特别关注的部分：

- [ ] 特定模块的实现逻辑
- [ ] 安全性方面的考量
- [ ] 性能优化是否合理
- [ ] 代码风格是否符合规范
- [ ] 是否有更好的实现方式

## 📊 影响范围

**影响的模块/服务：**

- [ ] pet-gateway
- [ ] pet-user
- [ ] pet-admin
- [ ] pet-social
- [ ] pet-chat
- [ ] pet-pet
- [ ] pet-donation
- [ ] pet-recruitment
- [ ] pet-story
- [ ] pet-tips
- [ ] pet-common

**Breaking Changes（破坏性变更）：**

<!-- 如果有不兼容变更，请详细说明 -->
- [ ] 无 Breaking Changes
- [ ] 有（请列出）：
  1. API `/api/xxx` 的返回结构变更
  2. ...

## 📝 补充说明

任何其他想告诉 reviewers 的信息：

---

**感谢你的贡献！** 🎉

<!--
Reviewers 审查指南：
1. 先看整体架构和设计是否合理
2. 再看代码质量和规范性
3. 最后关注安全性和性能
4. 使用 GitHub Review 功能逐行评论
5. Approval 前确保 CI 全部通过
-->
