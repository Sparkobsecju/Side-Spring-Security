# CH15 Agent 核心規範

本專案是 Spring Boot、Spring MVC 與 Spring Security 教學原型。`AGENTS.md` 只保留每次工作都需要的規則；開始修改前，依任務類型讀取下方對應文件，不要預先載入所有參考資料。

## 永遠適用

- 專案文件、程式註解、Commit 與 PR 說明預設使用繁體中文，除非使用者另有指定。
- 實作、測試、註解與規格必須保持一致；改變行為時同步更新相關內容。
- 保留使用者既有變更，不提交密碼、Token、私鑰或正式環境機密。
- 不直接提交至 `main` 或 `develop`；所有工作透過適當的短期分支與 PR 進行。
- 僅讀取本次任務需要的參考文件；若同時符合多種任務類型，合併讀取其必要文件。

## 按需讀取索引

| 任務類型 | 修改前必讀 |
| --- | --- |
| Java、HTML、XML、規格或註解 | [`docs/COMMENTING_STANDARD.md`](docs/COMMENTING_STANDARD.md) |
| Java、Security、路由、頁面、Maven 或交付驗證 | [`docs/VERIFICATION_STANDARD.md`](docs/VERIFICATION_STANDARD.md) |
| 建立分支、Commit、Push、PR、合併或發布 | [`BRANCHING.md`](BRANCHING.md) |
| MVC／Security 設定、登入登出、路由、`static` 或 `templates` | [`WEB_SECURITY_MVC_SPEC.md`](src/main/java/com/siven/ch15/config/WEB_SECURITY_MVC_SPEC.md) |
| `config.handler` 或驗證後導向行為 | 上述 Web 規格及 [`AUTHENTICATION_HANDLER_SPEC.md`](src/main/java/com/siven/ch15/config/handler/AUTHENTICATION_HANDLER_SPEC.md) |

更新參考文件時，保持本索引連結有效，並避免把詳細規格重新複製回 `AGENTS.md`。
