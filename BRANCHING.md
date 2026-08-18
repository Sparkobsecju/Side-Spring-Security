# Git 分支管理規範

本專案採用精簡版 GitFlow，讓穩定版本、日常整合與各項開發工作彼此隔離。所有長期分支都應透過 Pull Request 接收變更，避免直接提交。

## 長期分支

### `main`

- 只保存已驗證、可發布的穩定版本。
- 一般發布由 `develop` 建立 PR 合併至 `main`。
- 正式環境緊急修復可由 `hotfix/*` 建立 PR 合併至 `main`。
- 合併發布版本後，建議建立語意化版本標籤，例如 `v1.0.0`。

### `develop`

- 作為下一個版本的日常整合分支。
- `feature/*`、`fix/*` 與 `agent/*` 預設都從此分支建立，並透過 PR 合併回此分支。
- 準備發布時，由 `develop` 建立 PR 合併至 `main`。

## 短期工作分支

| 類型 | 命名方式 | 起始分支 | PR 目標 | 用途 |
| --- | --- | --- | --- | --- |
| 新功能 | `feature/<簡短說明>` | `develop` | `develop` | 新增使用者可見功能或主要能力 |
| 一般修正 | `fix/<簡短說明>` | `develop` | `develop` | 修正尚未發布或非緊急問題 |
| Codex 工作 | `agent/<簡短說明>` | `develop` | `develop` | 由 Codex 實作、重構、文件或測試工作 |
| 緊急修復 | `hotfix/<簡短說明>` | `main` | `main` | 修復穩定版本的緊急問題；完成後須同步回 `develop` |
| 發布準備（選用） | `release/<版本>` | `develop` | `main` | 需要獨立驗收、版本號或發行說明時使用 |

分支名稱使用小寫 kebab-case，例如 `feature/user-registration`、`fix/login-redirect` 或 `agent/add-security-tests`。

## 標準工作流程

1. 開始工作前同步基準分支：`git switch develop`、`git pull --ff-only`。
2. 建立短期分支：`git switch -c feature/<簡短說明>`。
3. 小步提交並維持清楚的 Commit 訊息。
4. 推送分支：`git push -u origin feature/<簡短說明>`。
5. 建立 Draft PR 至 `develop`，完成測試與自我 review 後再標記 Ready for review。
6. 合併後刪除短期分支，並重新同步本機 `develop`。

## 合併與發布原則

- 功能 PR 建議使用 Squash merge，讓 `develop` 歷史保持清楚。
- PR 必須列出變更目的、影響範圍及驗證方式。
- Java、Security、頁面路由或 Maven 相依變更必須通過 `mvn test`。
- 禁止把密碼、Token、私鑰或正式環境機密提交至任何分支。
- `hotfix/*` 合併至 `main` 後，必須再將相同修正合併或 cherry-pick 至 `develop`，避免下次發布遺失修正。
