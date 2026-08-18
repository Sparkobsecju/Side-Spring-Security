package com.siven.ch15.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 將對外公開的乾淨網址轉送到 {@code src/main/resources/static} 下的 HTML 頁面。
 *
 * <p>例如瀏覽器請求 {@code /login} 時，伺服器內部會轉送至 {@code /login.html}；
 * 網址列不會改變。若未來頁面改用 Thymeleaf 動態資料，應將檔案移至 templates
 * 並把回傳值改成對應的 View 名稱。</p>
 */
@Controller
public class PageController {

	/** 顯示自訂登入表單。 */
	@GetMapping("/login")
	public String login() {
		return "forward:/login.html";
	}

	/** 顯示登入驗證失敗頁面。 */
	@GetMapping("/login-error")
	public String loginError() {
		return "forward:/login-error.html";
	}

	/** 顯示首頁；也是沒有先前受保護請求時的預設登入成功頁。 */
	@GetMapping("/home")
	public String home() {
		return "forward:/home.html";
	}

	/** 顯示需要登入才能存取的示範資源頁面。 */
	@GetMapping("/resource")
	public String resource() {
		return "forward:/resource.html";
	}

}
