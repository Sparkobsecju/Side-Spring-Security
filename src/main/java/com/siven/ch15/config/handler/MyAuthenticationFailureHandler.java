package com.siven.ch15.config.handler;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 處理表單登入失敗後的導向行為。
 *
 * <p>不把 {@link AuthenticationException} 的內部訊息直接回傳給瀏覽器，避免洩漏
 * 帳號是否存在、鎖定狀態或其他驗證細節；使用者只會看到一致的登入失敗頁面。</p>
 */
@Component
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {

	private static final String FAILURE_URL = "/login-error";
	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	/**
	 * 登入失敗後，以 HTTP Redirect 將使用者送往通用錯誤頁。
	 *
	 * @param request 本次登入請求
	 * @param response 用來送出重新導向的 HTTP 回應
	 * @param exception Spring Security 拒絕驗證時產生的例外；此處刻意不輸出其內容
	 * @throws IOException 當回應無法送出 Redirect 時拋出
	 * @throws ServletException 符合 AuthenticationFailureHandler 介面契約
	 */
	@Override
	public void onAuthenticationFailure(
		HttpServletRequest request,
		HttpServletResponse response,
		AuthenticationException exception) throws IOException, ServletException {
		redirectStrategy.sendRedirect(request, response, FAILURE_URL);
	}

}
