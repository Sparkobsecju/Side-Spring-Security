package com.siven.ch15.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 集中註冊只有固定頁面、不需要額外 Controller 邏輯的 Spring MVC 路由。
 *
 * <p>本專案的 HTML 目前放在 {@code src/main/resources/static}，因此 View 名稱使用
 * {@code forward:} 在伺服器內部轉送至實體檔案。若未來改用 Thymeleaf 動態資料，應將
 * 頁面移至 {@code templates}，並把 View 名稱改為不含 {@code forward:} 的模板名稱。</p>
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	/**
	 * 將對外網址直接對應至 View 名稱，適用於不需要組裝 Model 的純頁面。
	 *
	 * @param registry Spring MVC 用來收集 View Controller 映射的註冊器
	 */
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		// 登入成功後的公開首頁，以及必須通過 Spring Security 驗證的示範資源頁。
		registry.addViewController("/home").setViewName("forward:/home.html");
		registry.addViewController("/resource").setViewName("forward:/resource.html");

		// 僅處理 GET /login 的畫面；POST /login 仍由 Spring Security Filter 驗證帳密。
		registry.addViewController("/login").setViewName("forward:/login.html");

		// 自訂失敗 Handler 會導向此路徑；獨立頁面避免在網址中暴露驗證例外細節。
		registry.addViewController("/login-error").setViewName("forward:/login-error.html");
	}
}
