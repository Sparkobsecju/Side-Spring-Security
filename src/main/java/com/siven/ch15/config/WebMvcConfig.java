package com.siven.ch15.config;

import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 集中註冊只有固定頁面、不需要額外 Controller 邏輯的 Spring MVC 路由。
 *
 * <p>{@code home}、{@code resource} 與 {@code login} 是 View 名稱；啟用此設定後，
 * Spring MVC 會交由 View Resolver 尋找對應頁面。若使用 Thymeleaf，頁面通常應放在
 * {@code src/main/resources/templates}。</p>
 *
 * <p><strong>目前狀態：</strong>本類別尚未標註 {@code @Configuration}，因此不會自動
 * 註冊至 Spring Application Context。既有 {@code PageController} 也已處理相同路徑；
 * 未來若要啟用本類別，應先統一路由策略並移除重複映射，避免維護者誤判實際處理來源。</p>
 */
public class WebMvcConfig implements WebMvcConfigurer {

	/**
	 * 將對外網址直接對應至 View 名稱，適用於不需要組裝 Model 的純頁面。
	 *
	 * @param registry Spring MVC 用來收集 View Controller 映射的註冊器
	 */
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		// 登入成功後的公開首頁，以及必須通過 Spring Security 驗證的資源頁。
		registry.addViewController("/home").setViewName("home");
		registry.addViewController("/resource").setViewName("resource");

		// 僅處理 GET /login 的畫面；POST /login 仍由 Spring Security Filter 驗證帳密。
		registry.addViewController("/login").setViewName("login");
	}
}
