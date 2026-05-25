# 🤝 贡献指南 (Contributing Guide)

感谢你对 **宠物领养平台** 项目的关注！我们非常欢迎社区贡献者参与项目开发。

---

## 📋 目录

- [行为准则](#-行为准则)
- [如何贡献](#-如何贡献)
- [开发流程](#-开发流程)
- [代码规范](#-代码规范)
- [提交信息规范](#-提交信息规范)
- [Pull Request 流程](#-pull-request-流程)
- [问题反馈](#-问题反馈)

---

## 🌟 行为准则

### 我们的承诺

为了营造一个开放和友好的环境，我们作为贡献者和维护者承诺：

- ✅ 使用友好和包容的语言
- ✅ 尊重不同的观点和经验
- ✅ 建设性地接受批评
- ✅ 关注对社区最有利的事情
- ✅ 对其他社区成员表示同理心

### 不可接受的行为

❌ 以下行为是不可接受的：

- 使用性别的、性的或性取向的言语或图像
- 公开或私下的骚扰
- 发布他人的个人信息（如地址、电子邮件）
- 其他可能被合理认为不恰当或不专业的行为

---

## 🚀 如何贡献

### 贡献方式

你可以通过以下方式为项目做出贡献：

1. **🐛 报告 Bug** - 发现问题时提 Issue
2. **💡 提出新功能** - 提交功能建议
3. **📝 改进文档** - 完善文档、翻译
4. **💻 编写代码** - 修复 Bug 或实现新功能
5. **✅ 代码审查** - 帮助审查 Pull Request
6. **🎨 UI/UX改进** - 界面优化建议

### 贡献前准备

在开始之前，请确保：

1. ⭐ **Star 本项目** - 表示你的支持！
2. 🔀 **Fork 项目** - 到你自己的 GitHub 账户
3. 📥 **克隆仓库** - 到本地开发环境
4. 📖 **阅读文档** - 了解项目结构和技术栈
5. 🔧 **配置环境** - 参考 README.md 配置开发环境

```bash
# 1. Fork 并克隆
git clone https://github.com/YOUR_USERNAME/pet-adoption-platform.git
cd pet-adoption-platform

# 2. 创建特性分支
git checkout -b feature/your-feature-name

# 3. 安装依赖并配置（参考README）
cp .env.example .env
# 编辑 .env 文件...
```

---

## 🛠️ 开发流程

### 分支策略

我们采用 **Git Flow** 工作流：

```
main (生产环境)
  │
  ├── develop (开发主分支)
  │     │
  │     ├── feature/* (新功能分支)
  │     ├── bugfix/* (Bug修复分支)
  │     └── hotfix/* (紧急修复分支)
  │
  └── release/* (发布分支)
```

#### 分支命名规范

| 类型 | 前缀 | 示例 | 说明 |
|------|------|------|------|
| 新功能 | `feature/` | `feature/user-avatar-upload` | 新功能开发 |
| Bug修复 | `bugfix/` | `bugfix/login-token-expire` | 问题修复 |
| 热修复 | `hotfix/` | `hotfix/security-vulnerability` | 生产紧急修复 |
| 文档 | `docs/` | `docs/api-documentation` | 文档更新 |
| 重构 | `refactor/` | `refactor/optimize-cache` | 代码重构 |

### 开发步骤

1. **从 develop 创建新分支**
   ```bash
   git checkout develop
   git pull origin develop
   git checkout -b feature/amazing-feature
   ```

2. **编写代码**
   - 遵循代码规范（见下方）
   - 编写单元测试
   - 更新相关文档

3. **本地测试**
   ```bash
   # 运行测试
   mvn test
   
   # 检查代码风格
   mvn checkstyle:check
   
   # 构建项目
   mvn clean package -DskipTests
   ```

4. **提交代码**
   ```bash
   git add .
   git commit -m "feat(user): add avatar upload functionality"
   ```

5. **推送到你的 Fork**
   ```bash
   git push origin feature/amazing-feature
   ```

6. **创建 Pull Request**（见下方流程）

---

## 📏 代码规范

### Java 代码规范

遵循 **阿里巴巴 Java 开发手册**：

#### 1. 命名规范

```java
// ✅ 类名：大驼峰命名
public class UserService { }
public class PetAdoptionController { }

// ✅ 方法名：小驼峰命名，动词开头
public User getUserById(Long id) { }
public void savePetInfo(PetDTO petDTO) { }

// ✅ 变量名：小驼峰命名
private String userName;
private List<PetInfo> petList;

// ✅ 常量：全大写+下划线
public static final String TOKEN_PREFIX = "Bearer ";
public static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000L;
```

#### 2. 注释规范

```java
/**
 * 用户服务类
 * 
 * 功能说明：
 * - 用户注册与登录
 * - 个人信息管理
 * - 关注/粉丝关系管理
 * 
 * @author your-name
 * @since 1.0.0
 */
@Service
public class UserService {
    
    /**
     * 根据用户ID查询用户信息
     * 
     * @param userId 用户ID（不能为null）
     * @return 用户信息对象，如果不存在返回null
     * @throws IllegalArgumentException 如果userId为null
     */
    public User getUserById(Long userId) {
        // 方法实现...
    }
}
```

#### 3. 异常处理

```java
// ✅ 使用自定义业务异常
if (user == null) {
    throw new BusinessException("用户不存在");
}

// ✅ 日志记录要详细
try {
    // 业务逻辑
} catch (Exception e) {
    log.error("用户登录失败, userId={}, error={}", userId, e.getMessage(), e);
    throw new BusinessException("登录失败，请稍后重试");
}
```

#### 4. Controller 规范

```java
@RestController
@RequestMapping("/api/user")
@Api(tags = "用户接口")
@Validated
public class UserController {
    
    /**
     * 获取用户信息
     */
    @GetMapping("/{id}")
    @ApiOperation("获取用户详情")
    public Result<UserVO> getUser(
            @PathVariable Long id) {
        return Result.success(userService.getUserById(id));
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    @ApiOperation("更新用户信息")
    public Result<Void> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateDTO dto) {
        userService.updateUser(id, dto);
        return Result.success();
    }
}
```

### YAML 配置文件规范

```yaml
# ✅ 使用注释说明每个配置项
server:
  port: 9001  # 服务端口
  
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST:localhost}:3306/pet  # 数据库连接URL
    username: ${DB_USERNAME:root}  # 数据库用户名
    
# ✅ 敏感信息使用环境变量
jwt:
  secret: ${JWT_SECRET_KEY}  # JWT密钥（必须配置！）
```

### SQL 规范

```sql
-- ✅ 表名使用小写+下划线
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_name VARCHAR(50) NOT NULL COMMENT '用户名',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ✅ 关键字大写
SELECT u.id, u.user_name, r.role_name
FROM sys_user u
LEFT JOIN sys_role r ON u.role_id = r.id
WHERE u.status = 1
ORDER BY u.create_time DESC;
```

---

## 📝 提交信息规范

我们遵循 [Conventional Commits](https://www.conventionalcommits.org/) 规范：

### 格式

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Type 类型

| 类型 | 说明 |
|------|------|
| `feat` | 新功能 (feature) |
| `fix` | 修复 Bug (bug fix) |
| `docs` | 文档变更 (documentation) |
| `style` | 代码格式调整（不影响功能）|
| `refactor` | 重构（既不是新功能也不是修复）|
| `perf` | 性能优化 (performance) |
| `test` | 测试相关 |
| `chore` | 构建/工具链/辅助工具变动 |
| `ci` | CI/CD 配置变更 |
| `revert` | 回滚提交 |

### Scope 范围

常用范围：
- `user` - 用户服务
- `admin` - 管理员服务
- `pet` - 宠物服务
- `social` - 社交服务
- `chat` - 聊天服务
- `gateway` - 网关服务
- `common` - 公共模块
- `docs` - 文档

### 示例

```bash
# ✅ 好的提交信息
feat(user): add avatar upload functionality

- Support JPG/PNG format
- Max file size: 5MB
- Auto compress images

Closes #123

# ✅ 修复Bug
fix(chat): resolve WebSocket connection timeout issue

The connection was timing out after 30 seconds due to missing heartbeat configuration.
Added ping/pong mechanism to keep connection alive.

Fixes #456

# ❌ 不好的提交信息
update code
fix bug
asdf
```

---

## 🔀 Pull Request 流程

### PR 标题格式

```
[type] <brief description>

Example:
[feat] Add pet adoption workflow
[fix] Resolve login token expiration issue
[docs] Update API documentation
```

### PR 描述模板

创建 PR 时请填写以下内容（GitHub 会自动加载 `.github/PULL_REQUEST_TEMPLATE.md`）：

```markdown
## 相关Issue
Closes #(issue number)

## 变更类型
- [ ] 新功能 (feature)
- [ ] Bug修复 (bugfix)
- [ ] 文档更新 (documentation)
- [ ] 性能优化 (performance)
- [ ] 代码重构 (refactor)

## 变更描述
<!-- 详细描述你的改动 -->

## 测试说明
<!-- 说明如何测试你的改动 -->

## 截图（如果有UI变更）
<!-- 添加截图 -->

## 检查清单
- [ ] 代码遵循项目规范
- [ ] 已添加必要的注释
- [ ] 已更新相关文档
- [ ] 已通过本地测试
- [ ] 无编译警告
```

### PR 审查流程

1. **自动化检查** - CI 会自动运行：
   - ✅ 代码风格检查
   - ✅ 单元测试
   - ✅ 编译检查

2. **人工审查** - 至少需要 1 位维护者审核通过

3. **合并要求**：
   - 所有检查必须通过
   - 至少 1 个 Approval
   - 无 Change Request
   - 分支是最新的（已 rebase）

### 合并策略

- **develop → main**: 使用 Squash and Merge（压缩合并）
- **feature → develop**: 使用 Merge Commit（保留历史）

---

## 🐛 问题反馈 (Issues)

### 报告 Bug

使用模板报告 Bug：

**标题格式：** `[Bug] 简短描述`

**必填信息：**

```markdown
## 问题描述
清晰描述遇到的问题

## 复现步骤
1. 进入 '...'
2. 点击 '....'
3. 滚动到 '....'
4. 看到错误

## 期望行为
描述应该发生什么

## 截图
如果适用，添加截图

## 环境
- OS: [e.g., Windows 11]
- Browser: [e.g., Chrome 120]
- Java Version: [e.g., JDK 21]
- Project Version: [e.g., 1.0.0]

## 额外信息
其他相关信息
```

### 功能请求

**标题格式：** `[Feature] 功能名称`

**内容应包括：**

```markdown
## 功能描述
清晰描述你想要的功能

## 解决的问题
这个功能解决了什么痛点？

## 建议的解决方案
如果有想法可以分享

## 替代方案
考虑过其他方案吗？

## 额外上下文
其他相关信息、截图等
```

---

## 💡 开发技巧

### 常用 Git 命令

```bash
# 查看状态
git status

# 查看最近提交
git log --oneline -10

# 暂存修改
git stash
git stash pop

# 撤销上次提交（保留修改）
git reset --soft HEAD~1

# 修改上次提交信息
git commit --amend

# 同步远程更新
git pull --rebase origin develop

# 清理已合并的分支
git branch -d feature/xxx
```

### IDE 配置推荐

**IntelliJ IDEA:**

1. **代码格式化**
   - Settings → Editor → Code Style → Java
   - 导入 `google-java-format.xml` 或使用项目统一配置

2. **快捷键**
   - `Alt + Enter`: 快速修复
   - `Ctrl + Alt + L`: 格式化代码
   - `Shift + F6`: 重命名
   - `Ctrl + Shift + T`: 创建测试

3. **插件推荐**
   - Lombok Plugin
   - MyBatisX
   - Rainbow Brackets
   - GitToolBox

### 调试技巧

```java
// 使用日志而非 System.out
log.debug("Processing request for userId={}", userId);
log.error("Failed to process, error={}", e.getMessage(), e);

// 使用断点调试
// IDEA: 在行号左侧点击设置断点
// Debug模式运行，F8单步执行，F7进入方法
```

---

## 📞 联系方式

如果你有任何问题：

- **GitHub Issues**: [提交问题](https://github.com/your-username/pet-adoption-platform/issues)
- **Discussions**: [参与讨论](https://github.com/your-username/pet-adoption-platform/discussions)
- **Email**: your-email@example.com

---

## 🙏 致谢

所有贡献者都会被列在 [CONTRIBUTORS.md](CONTRIBUTORS.md) 中！

再次感谢你的贡献！🎉

---

<div align="center">

**Happy Coding! 让我们一起为流浪动物找到温暖的家 🐾**

</div>
