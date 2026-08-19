package com.siven.ch15.config.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 處理表單登入成功後的導向行為。
 *
 * <p>Spring Security 完成身分驗證與 Session 儲存後會呼叫此 Handler；本類別只負責
 * 將瀏覽器導向公開首頁，不重複處理密碼或建立驗證狀態。</p>
 */
@Component
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private static final String SUCCESS_URL = "/home";
	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	/**
	 * 登入成功後，以 HTTP Redirect 將使用者送往首頁。
	 *
	 * @param request 本次登入請求
	 * @param response 用來送出重新導向的 HTTP 回應
	 * @param authentication 已完成驗證的使用者資訊，保留供未來稽核或客製邏輯使用
	 * @throws IOException 當回應無法送出 Redirect 時拋出
	 * @throws ServletException 符合 AuthenticationSuccessHandler 介面契約
	 */
	@Override
	public void onAuthenticationSuccess(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {
		redirectStrategy.sendRedirect(request, response, SUCCESS_URL);
	}

}
