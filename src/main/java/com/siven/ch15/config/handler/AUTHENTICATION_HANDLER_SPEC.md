# 登入驗證 Handler 規格

本文件定義 `com.siven.ch15.config.handler` 套件的責任、行為契約與擴充原則。此套件只處理 Spring Security 完成登入驗證後的 HTTP 導向，不負責驗證帳密、建立登入狀態或產生頁面內容。

## 套件責任

- 將登入成功與失敗後的處理流程拆分為可由 Spring 注入的元件。
- 統一登入結果的導向位置，避免相關 URL 分散於多個設定類別。
- 保留未來加入稽核、依角色導向或客製登入失敗流程的擴充點。
- 隔離 Spring Security 驗證結果與 MVC 頁面顯示責任。

## 元件規格

### `MyAuthenticationSuccessHandler`

- 實作 `AuthenticationSuccessHandler`，並以 `@Component` 註冊為 Spring Bean。
- Spring Security 已完成身分驗證及 Session 儲存後，呼叫 `onAuthenticationSuccess(...)`。
- 使用 `RedirectStrategy` 回傳 HTTP Redirect，目的地固定為 `/home`。
- 接收的 `Authentication` 代表已驗證使用者；現階段不修改或重建該物件。
- 不得在此重新比對密碼、手動建立 Session 或自行寫入驗證狀態。

### `MyAuthenticationFailureHandler`

- 實作 `AuthenticationFailureHandler`，並以 `@Component` 註冊為 Spring Bean。
- Spring Security 拒絕登入驗證後，呼叫 `onAuthenticationFailure(...)`。
- 使用 `RedirectStrategy` 回傳 HTTP Redirect，目的地固定為 `/login-error`。
- 接收的 `AuthenticationException` 僅供內部流程判斷；現階段不得把例外訊息、帳號存在狀態或鎖定資訊輸出到瀏覽器。
- 所有一般登入失敗情況使用一致的公開錯誤頁，降低帳號列舉與內部資訊洩漏風險。

## 輸入與輸出契約

| 情境 | Spring Security 輸入 | Handler 輸出 | 目的地 |
| --- | --- | --- | --- |
| 登入成功 | `HttpServletRequest`、`HttpServletResponse`、`Authentication` | HTTP Redirect | `/home` |
| 登入失敗 | `HttpServletRequest`、`HttpServletResponse`、`AuthenticationException` | HTTP Redirect | `/login-error` |

- Handler 不直接輸出 HTML、JSON 或錯誤堆疊。
- Redirect 發送失敗時，依介面契約向上拋出 `IOException` 或 `ServletException`，不在 Handler 內吞掉例外。
- 導向的頁面路徑必須由 MVC 與 Security 設定正確註冊；登入失敗目的地必須允許匿名存取，避免產生重複登入導向。

## 相依性與生命週期

- 兩個 Handler 都是無狀態的 Spring Singleton Bean。
- `RedirectStrategy` 使用 `DefaultRedirectStrategy`，讓 Redirect URL 依目前 Servlet Context 正確解析。
- Handler 不保存每位使用者的可變資料；若未來需要稽核資訊，應交由具明確生命週期的服務處理。
- Security 設定應使用建構式注入取得 Handler，不應在設定方法中以 `new` 建立 Handler。

## 擴充原則

- 依角色導向首頁時，優先在成功 Handler 讀取 `Authentication#getAuthorities()`，並為每條導向規則新增測試。
- 新增失敗分類時，前端訊息仍應保持一般化；詳細原因只可寫入受保護的伺服器端稽核紀錄。
- 若改為 REST API，不應沿用 Redirect 契約；應新增獨立的 API Handler，明確定義狀態碼與安全的回應格式。
- 修改成功或失敗 URL 時，必須同步更新 MVC 路由、Security 公開路徑、HTML 導覽及 MockMvc 測試。

## 驗收條件

- 正確帳密登入後收到 3xx 回應，且 `Location` 為 `/home`。
- 錯誤帳密登入後收到 3xx 回應，且 `Location` 為 `/login-error`。
- `/login-error` 可由匿名使用者瀏覽，不會再次被導向 `/login`。
- 登入失敗回應不包含 `AuthenticationException` 的內部訊息。
- 修改 Handler 行為後，`mvn test` 必須全部通過。
