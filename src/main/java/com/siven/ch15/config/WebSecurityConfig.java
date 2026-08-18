package com.siven.ch15.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 集中管理網站的身分驗證、頁面授權與登出行為。
 *
 * <p>本專案使用 Spring Security 7，因此以 {@link SecurityFilterChain} Bean
 * 取代舊版的 {@code WebSecurityConfigurerAdapter#configure(HttpSecurity)}。</p>
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	/**
	 * 不需要登入即可瀏覽的路徑。
	 *
	 * <p>{@code *.html} 是 MVC Controller 轉送至 static 目錄時使用的實體檔案路徑。</p>
	 */
	private static final String[] PUBLIC_ENDPOINTS = {
		"/", "/home", "/login", "/login-error",
		"/home.html", "/login.html", "/login-error.html"
	};

	/**
	 * 建立整個應用程式共用的 Security Filter Chain。
	 *
	 * @param http Spring Security 提供的 HTTP 安全設定建構器
	 * @return 已完成設定的安全過濾器鏈
	 * @throws Exception 當 Spring Security 無法建立設定時拋出
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			// Spring Security 7 使用 authorizeHttpRequests + requestMatchers，
			// 取代舊版 authorizeRequests + antMatchers API。
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers(PUBLIC_ENDPOINTS).permitAll()
				.anyRequest().authenticated()
			)
			// GET /login 由 PageController 顯示頁面；POST /login 由 Security Filter 處理。
			.formLogin(form -> form
				.loginPage("/login")
				.loginProcessingUrl("/login")
				.defaultSuccessUrl("/home")
				.failureUrl("/login-error")
				.permitAll()
			)
			// POST /logout 會清除驗證狀態，完成後回到登入頁並附帶 logout 參數。
			.logout(logout -> logout
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login?logout")
				.permitAll()
			)
			// 教學原型依需求停用 CSRF；正式環境應移除此行並在表單加入 CSRF Token。
			.csrf(AbstractHttpConfigurer::disable);

		return http.build();
	}

}
