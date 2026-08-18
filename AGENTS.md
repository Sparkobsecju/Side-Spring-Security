# CH15 開發與註解規範

本專案是 Spring Boot、Spring MVC 與 Spring Security 的教學原型。每次修改功能時，必須同步更新能協助下一位開發者理解設計意圖的註解。

## 註解要求

- 新增或修改 Java 類別時，為類別與公開方法補上繁體中文 Javadoc，說明責任、輸入、輸出與非直覺行為。
- Security 設定必須註明公開路徑、登入／登出端點、轉址行為及安全取捨；不要只描述 API 名稱。
- HTML 頁面必須以註解說明對應路由、表單提交端點，以及與 Spring Security 或 Thymeleaf 的整合條件。
- 新增或修改 `pom.xml` 的 dependency、plugin 或重要 property 時，加入 XML 註解說明用途與 scope。
- 註解應解釋「為什麼」與必要契約，避免逐行重述容易從程式碼看出的語法。
- 修改行為時同步更新或移除過時註解，不可讓註解與實作不一致。

## 驗證要求

- 修改 Java、Security 設定、頁面路由或 Maven 相依後，執行 `mvn test`。
- 新增登入、授權、登出或轉址行為時，在 `Ch15ApplicationTests` 補上對應的 MockMvc 測試。

## Git 分支要求

- 遵循 `BRANCHING.md` 的精簡 GitFlow，不直接提交至 `main` 或 `develop`。
- 一般 Codex 工作從 `develop` 建立 `agent/<簡短說明>`，並以 Draft PR 合併回 `develop`。
- 新功能使用 `feature/*`，一般修正使用 `fix/*`，穩定版緊急修正使用 `hotfix/*`。
- 建立 PR 前確認提交範圍、執行相關測試，並在 PR 說明中記錄驗證結果。
