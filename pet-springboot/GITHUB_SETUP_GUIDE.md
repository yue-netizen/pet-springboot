# 🛡️ GitHub 仓库保护规则配置指南

本文档说明如何为 **宠物领养平台** 项目设置 GitHub 分支保护规则，确保代码质量和项目安全。

---

## 📋 目录

- [为什么需要分支保护](#为什么需要分支保护)
- [推荐的保护规则](#推荐的保护规则)
- [配置步骤](#配置步骤)
- [CI/CD 集成建议](#cicd-集成建议)
- [常见问题](#常见问题)

---

## 🎯 为什么需要分支保护

### 核心目标

✅ **代码质量保障**
- 确保所有合并的代码都经过审查
- 自动运行测试和检查
- 防止有问题的代码进入主分支

🔒 **安全防护**
- 防止直接推送到主分支
- 强制使用 Pull Request 流程
- 保护关键分支不被误操作

📊 **可追溯性**
- 所有变更都有完整的审核记录
- 便于问题追踪和回滚

---

## ⚙️ 推荐的保护规则

### 🌟 main 分支（生产环境）

**严格级别：高** 🔴

```
✅ Require a pull request before merging
   - 启用: 是
   - Dismiss stale pull request approvals when new commits are pushed: ✅ 启用
   - Require approvals: 1 (至少1人审批)
   - Disallow dismissals of pull request reviews by users who created the pull request: ✅ 启用
   - Require review from CODEOWNERS: 可选
   - Restrict who can push to matching branches:
     * Only allow: Administrators, Maintainers

✅ Require status checks to pass before merging
   - 启用: 是
   - Required status checks (选择全部):
     * build (Maven 编译)
     * test (单元测试)
     * lint (代码风格检查)
     * security (安全扫描) - 如果有

✅ Require signed commits
   - 启用: 推荐（可选）
   
✅ Do not allow bypassing the above settings
   - 启用: 是
   
✅ Require linear history
   - 启用: 是（强制使用 Squash and Merge）

❌ Allow force pushes
   - 启用: 否
   
❌ Allow deletions
   - 启用: 否
```

### 🟢 develop 分支（开发环境）

**严格级别：中** 🟡

```
✅ Require a pull request before merging
   - 启用: 是
   - Require approvals: 1
   - Dismiss stale reviews: ✅ 启用

✅ Require status checks to pass before merging
   - 启用: 是
   - Required: build, test

✅ Require linear history
   - 启用: 是

❌ Allow force pushes: 否
❌ Allow deletions: 否
```

### 🔵 feature/* 分支（功能分支）

**严格级别：低** 🟢

```
✅ Require a pull request before merging: 可选
✅ Require status checks: 仅 build
✅ Allow force pushes: 可以（开发阶段允许）
```

---

## 🚀 配置步骤

### 方式一：通过 GitHub Web UI（推荐新手）

#### 步骤 1：打开设置页面

1. 访问你的 GitHub 仓库
2. 点击 **Settings** (⚙️)
3. 左侧菜单选择 **Branches**

#### 步骤 2：添加保护规则

1. 点击 **Add branch protection rule**
2. 在 "Branch name pattern" 输入框输入：`main`
3. 按照上面的推荐规则勾选选项
4. 点击 **Create**

#### 步骤 3：为 develop 分支添加规则

重复步骤 2，但这次输入 `develop` 并应用中等严格度的规则。

#### 配置截图示例

```
┌─────────────────────────────────────────────┐
│  Branch protection rules                    │
├─────────────────────────────────────────────┤
│                                              │
│  Branch name pattern                         │
│  ┌──────────────────────────┐               │
│  │ main                      │               │
│  └──────────────────────────┘               │
│                                              │
│  ☑ Require a pull request before merging    │
│    ├─ ☑ Dismiss stale PR approvals          │
│    ├─ Require approvals: [1]                 │
│    └─ ☑ Disallow self-review dismissal      │
│                                              │
│  ☑ Require status checks to pass            │
│    └─ ☑ build                              │
│    └─ ☑ test                               │
│    └─ ☑ lint                               │
│                                              │
│  ☑ Do not allow bypass settings             │
│  ☐ Require signed commits                   │
│  ☑ Require linear history                  │
│  ☐ Allow force pushes                       │
│  ☐ Allow deletions                          │
│                                              │
│  [Cancel]  [Create]                          │
└─────────────────────────────────────────────┘
```

### 方式二：使用 GitHub API（自动化脚本）

如果你需要批量管理多个仓库，可以使用 API：

```bash
#!/bin/bash
# setup-protection.sh - 设置分支保护规则

# 配置
REPO="your-username/pet-adoption-platform"
TOKEN="your-github-personal-access-token"
API="https://api.github.com/repos/$REPO"

# 为 main 分支设置保护规则
curl -X PUT \
  "$API/branches/main/protection" \
  -H "Authorization: token $TOKEN" \
  -H "Accept: application/vnd.github.v3+json" \
  -d '{
    "required_status_checks": {
      "strict": true,
      "contexts": ["build", "test", "lint"]
    },
    "enforce_admins": true,
    "required_pull_request_reviews": {
      "dismiss_stale_reviews": true,
      "require_code_owner_reviews": false,
      "required_approving_review_count": 1,
      "dismissal_restrictions": {
        "users": [],
        "teams": []
      }
    },
    "restrictions": null,
    "required_linear_history": true,
    "allow_force_pushes": false,
    "allow_deletions": false
  }'

echo "✅ Main 分支保护规则已设置！"
```

**使用方法：**

```bash
chmod +x setup-protection.sh
./setup-protection.sh
```

### 方式三：使用 GitHub CLI（gh 命令行工具）

```bash
# 安装 GitHub CLI
# Windows: winget install GitHub.cli
# Mac: brew install gh
# Linux: sudo apt install gh

# 登录
gh auth login

# 为 main 分支设置保护规则
gh api repos/your-username/pet-adoption-platform/branches/main/protection \
  --method PUT \
  -f required_status_checks='{"strict":true,"contexts":["build","test"]}' \
  -f enforce_admins=true \
  -f required_pull_request_reviews='{"dismiss_stale_reviews":true,"required_approving_review_count":1}' \
  -f required_linear_history=true \
  -f allow_force_pushes=false \
  -f allow_deletions=false
```

---

## 🔄 CI/CD 集成建议

### 推荐的 GitHub Actions 工作流

在 `.github/workflows/ci.yml` 中配置：

```yaml
name: CI Pipeline

on:
  pull_request:
    branches: [main, develop]
  push:
    branches: [main, develop]

jobs:
  # Job 1: 编译检查
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven
      
      - name: Build with Maven
        run: mvn clean compile -B
        
  # Job 2: 单元测试
  test:
    runs-on: ubuntu-latest
    needs: build
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven
      
      - name: Run tests
        run: mvn test -B
        
      - name: Upload test results
        uses: actions/upload-artifact@v4
        if: always()
        with:
          name: test-results
          path: target/surefire-reports/
          
  # Job 3: 代码质量检查
  lint:
    runs-on: ubuntu-latest
    needs: build
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          
      - name: Check style
        run: mvn checkstyle:check -B || true
        
  # Job 4: 安全扫描（可选）
  security:
    runs-on: ubuntu-latest
    needs: test
    steps:
      - uses: actions/checkout@v4
      
      - name: Run Trivy vulnerability scanner
        uses: aquasecurity/trivy-action@master
        with:
          scan-type: 'fs'
          scan-ref: '.'
          severity: 'CRITICAL,HIGH'
```

### Status Checks 说明

当你在分支保护规则中选择 "Require status checks to pass" 时，需要确保以下检查存在：

| 检查名称 | 来源 | 用途 |
|---------|------|------|
| `build` | CI Pipeline | Maven 编译成功 |
| `test` | CI Pipeline | 所有单元测试通过 |
| `lint` | CI Pipeline | 代码风格检查通过 |
| `security` | Trivy/Snyk | 无严重安全漏洞 |

---

## 📝 其他推荐设置

### 1. CODEOWNERS 文件

创建 `.github/CODEOWNERS` 文件：

```markdown
# 默认所有文件由 @your-team/reviewers 审查
* @your-team/reviewers

# 特定目录由专人负责
pet-gateway/** @alice
pet-user/** @bob
pet-common/** @charlie

# 文档任何人可以修改
docs/** @your-team/docs-team
*.md @everyone
```

### 2. Issue Templates

创建 `.github/ISSUE_TEMPLATE/bug_report.md`：

```markdown
---
name: Bug 报告
about: 创建一个Bug报告
title: '[Bug] '
labels: bug
assignees: ''
---
<!-- 请填写以下信息 -->

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
```

### 3. PR Template

创建 `.github/PULL_REQUEST_TEMPLATE.md`：

```markdown
## 相关Issue
Closes #(issue number)

## 变更类型
- [ ] 新功能 (feature)
- [ ] Bug修复 (bugfix)
- [ ] 文档更新 (documentation)
- [ ] 性能优化 (performance)

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

### 4. 自动化标签

创建 `.github/label-config.yml`（需安装标签管理工具）：

```yaml
labels:
  - name: bug
    color: d73a4a
    description: "Bug报告"

  - name: feature
    color: a2eeef
    description: "新功能请求"

  - name: documentation
    color: 0075ca
    description: "文档改进"

  - name: good first issue
    color: 7057ff
    description: "适合新贡献者的问题"

  - name: help wanted
    color: 008672
        description: "需要帮助"

  - name: security
    color: ee0701
    description: "安全问题（紧急）"
```

---

## ❓ 常见问题

### Q1: 为什么不能直接 push 到 main？

**A:** 这是为了防止意外提交未经过审查的代码。所有变更必须经过 PR 流程，确保：
- 至少一人审查代码
- 自动化测试通过
- 有完整的变更记录

### Q2: 如何临时绕过保护规则？

**A:** 作为管理员，你可以：
1. Settings → Branches → 找到对应规则 → Edit
2. 取消勾选 "Do not allow bypassing..."
3. 但这仅限紧急情况！

**更好的做法：** 使用 hotfix 分支 + 快速 PR

### Q3: Status check 一直失败怎么办？

**A:** 检查以下几点：
1. CI 配置文件是否正确（`.github/workflows/*.yml`）
2. 是否有语法错误或依赖缺失
3. 查看 Actions 页面的详细日志
4. 本地运行 `mvn test` 确认测试通过

### Q4: 可以针对特定文件类型设置规则吗？

**A:** 可以！使用路径过滤：

```yaml
# 只对 Java 文件变更要求审查
on:
  pull_request:
    paths:
      - '**.java'
```

### Q5: 如何平衡严格性和开发效率？

**A:** 建议：
- **main**: 最严格（生产环境）
- **develop**: 中等（日常开发）
- **feature/***: 宽松（快速迭代）
- 定期根据团队反馈调整规则

---

## 🎓 最佳实践总结

### ✅ 推荐做法

1. **渐进式启用** - 先从宽松开始，逐步收紧
2. **文档先行** - 先写好 CONTRIBUTING.md 和 PR 模板
3. **CI 成熟后再加限制** - 确保 CI 稳定可靠
4. **定期审查规则** - 每季度评估是否需要调整
5. **培训团队成员** - 确保大家都理解流程

### ❌ 应该避免

1. **一次性设置过多规则** - 可能阻碍开发
2. **忽略 CI 错误** - 会降低规则效果
3. **频繁更改规则** - 造成混乱
4. **不通知团队就启用** - 导致大量 PR 被阻塞

---

## 📚 相关资源

- [GitHub 官方文档 - 分支保护](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches/about-protected-branches)
- [GitHub Actions 文档](https://docs.github.com/en/actions)
- [CODEOWNERS 文件格式](https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/customizing-your-repository/about-code-owners)
- [Conventional Commits 规范](https://www.conventionalcommits.org/)

---

## 💬 获取帮助

如果在配置过程中遇到问题：

1. 查看 GitHub 官方文档
2. 在项目的 Discussions 中提问
3. 提交 Issue 寻求帮助

---

<div align="center">

**🎉 正确配置分支保护规则是项目成功的基石！**

*最后更新时间：2024年XX月XX日*

</div>
