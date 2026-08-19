# Web MVC 與 Spring Security 設定規格

本文件定義 `com.siven.ch15.config` 套件的整體設計與行為契約。此套件負責協調固定頁面路由、HTTP 存取授權、表單登入、登入結果導向與登出流程，但不負責帳號資料來源、商業邏輯或頁面內容。

Handler 的細部契約另見 [`handler/AUTHENTICATION_HANDLER_SPEC.md`](handler/AUTHENTICATION_HANDLER_SPEC.md)。

## 設計目標

- 使用 Spring Boot 4.1／Spring Security 7 支援的設定方式建立 `SecurityFilterChain`。
- 集中管理不需要 Model 或商業邏輯的固定頁面路由。
- 明確分離 MVC 頁面映射、Security 驗證授權與登入結果處理責任。
- 讓匿名頁面、受保護頁面與內部轉送資源之間的契約可被測試與維護。
- 保留未來改用 Thymeleaf、角色授權及稽核紀錄時的清楚擴充位置。

## 元件與責任

| 元件 | 主要責任 | 不應承擔的責任 |
| --- | --- | --- |
| `WebMvcConfig` | 將固定 GET 路徑映射至靜態 HTML | 驗證帳密、授權判斷、商業邏輯 |
| `WebSecurityConfig` | 建立授權、登入、登出與 CSRF 設定 | 產生頁面內容、直接實作登入結果回應 |
| `MyAuthenticationSuccessHandler` | 登入成功後導向 `/home` | 驗證密碼、建立 `Authentication` |
| `MyAuthenticationFailureHandler` | 登入失敗後導向 `/login-error` | 向瀏覽器揭露驗證例外細節 |

固定頁面路由只能由 `WebMvcConfig` 管理，不得再建立宣告相同路徑的 `PageController`，避免啟動時發生重複映射或讓維護者無法判斷實際處理來源。

## MVC 頁面映射

目前 HTML 位於 `src/main/resources/static`，因此 `WebMvcConfig` 使用伺服器內部 `forward:` 將公開路徑轉送至靜態檔案。

| HTTP 請求 | View 名稱 | 頁面用途 |
| --- | --- | --- |
| `GET /home` | `forward:/home.html` | 登入成功後的首頁 |
| `GET /resource` | `forward:/resource.html` | 必須登入才能瀏覽的示範資源 |
| `GET /login` | `forward:/login.html` | 自訂登入表單 |
| `GET /login-error` | `forward:/login-error.html` | 一般化的登入失敗說明 |

- `GET /login` 只負責顯示頁面；登入表單提交的 `POST /login` 由 Spring Security Filter 處理。
- `forward:` 不會改變瀏覽器網址，但轉送的 `*.html` 仍會再次通過 Security Filter，因此相關靜態檔案必須列入公開路徑。
- 若頁面需要伺服器端動態資料，應將 HTML 移至 `src/main/resources/templates`，改用 Thymeleaf View 名稱，並同步更新 Security 規則、測試與本文件。

## 授權規則

`WebSecurityConfig` 依下列順序套用授權規則：

1. `PUBLIC_ENDPOINTS` 內的路徑允許匿名存取。
2. 未符合公開清單的任何請求都必須完成身分驗證。

| 路徑 | 存取規則 | 原因 |
| --- | --- | --- |
| `/` | 公開 | 應用程式入口 |
| `/home`、`/home.html` | 公開 | 登入成功導向及其內部轉送檔案 |
| `/login`、`/login.html` | 公開 | 登入頁、登入處理端點及其靜態檔案 |
| `/login-error`、`/login-error.html` | 公開 | 失敗 Handler 目的地及其靜態檔案 |
| `/resource` | 需要驗證 | 受保護功能示範 |
| 其他未列出路徑 | 需要驗證 | 採預設拒絕匿名存取原則 |

新增匿名頁面時，必須同時評估公開的對外路徑與 `forward:` 使用的實體靜態路徑；不得為了排除單一問題而直接公開過大的萬用路徑。

## 表單登入流程

1. 未登入使用者請求受保護資源。
2. Spring Security 將使用者導向 `GET /login`。
3. `WebMvcConfig` 將請求轉送至 `/login.html`。
4. 使用者表單提交至 `POST /login`。
5. Spring Security 驗證帳密並保存成功的驗證狀態。
6. 驗證成功時，呼叫 `MyAuthenticationSuccessHandler` 並 Redirect 至 `/home`。
7. 驗證失敗時，呼叫 `MyAuthenticationFailureHandler` 並 Redirect 至 `/login-error`。

Security 設定必須以建構式注入取得兩個 Handler，避免在設定方法內自行建立物件，並維持元件可替換與可測試性。

## 登出流程

- 登出端點為 `POST /logout`。
- Spring Security 負責清除目前驗證狀態。
- 登出成功後 Redirect 至 `/login?logout`，讓登入頁可依查詢參數顯示完成訊息。
- 若恢復 CSRF 防護，登出表單必須提交有效的 CSRF Token。

## API 與版本限制

- 使用 `SecurityFilterChain` Bean 與 `HttpSecurity` Lambda DSL。
- 授權設定使用 `authorizeHttpRequests` 與 `requestMatchers`。
- 不得重新引入已淘汰的 `WebSecurityConfigurerAdapter`、`authorizeRequests` 或 `antMatchers`。
- Handler 使用 Spring Security 的 `AuthenticationSuccessHandler`、`AuthenticationFailureHandler` 與 `RedirectStrategy` 契約。

## CSRF 安全取捨

目前設定以 `AbstractHttpConfigurer::disable` 停用 CSRF，僅符合此教學原型的既定需求。此設定會降低使用 Session Cookie 之狀態變更請求的防護，不得直接沿用至正式環境。

正式部署或新增任何建立、修改、刪除資料的功能前，必須：

1. 移除停用 CSRF 的設定。
2. 在登入、登出及所有狀態變更表單加入 CSRF Token。
3. 補上合法 Token、缺少 Token 與錯誤 Token 的測試。
4. 確認前端請求方式與 Spring Security 的 CSRF 契約一致。

## 變更同步清單

修改路徑、導向或登入行為時，必須一起檢查：

- `WebMvcConfig` 的 View Controller 映射。
- `WebSecurityConfig.PUBLIC_ENDPOINTS` 與登入／登出設定。
- 成功與失敗 Handler 的目的地常數。
- `static` 或 `templates` 內的頁面、表單 action 與導覽連結。
- `Ch15ApplicationTests` 的 MockMvc 行為測試。
- `AGENTS.md`、本文件及 Handler 規格是否仍與實作一致。

## 驗收條件

- 匿名使用者可瀏覽 `/login`、`/home` 與 `/login-error`。
- 匿名使用者存取 `/resource` 時收到導向 `/login` 的 3xx 回應。
- 已驗證使用者可瀏覽 `/resource`，並轉送至 `/resource.html`。
- 正確帳密登入後導向 `/home`。
- 錯誤帳密登入後導向 `/login-error`。
- 登出後導向 `/login?logout`。
- Spring Context 可建立包含兩個 Handler 的 `SecurityFilterChain`。
- 修改 Java、Security 或頁面路由後，`mvn test` 必須全部通過。
