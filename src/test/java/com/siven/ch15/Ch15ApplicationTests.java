package com.siven.ch15;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 驗證 Spring Context 與主要登入授權流程，避免修改 Security 設定後產生回歸。
 *
 * <p>測試固定帳密只存在於測試環境，不會成為正式環境帳號。</p>
 */
@SpringBootTest(properties = {
	"spring.security.user.name=test-user",
	"spring.security.user.password=test-password"
})
@AutoConfigureMockMvc
class Ch15ApplicationTests {

	private final MockMvc mockMvc;

	@Autowired
	Ch15ApplicationTests(MockMvc mockMvc) {
		this.mockMvc = mockMvc;
	}

	/** 確認所有 Bean（包含 SecurityFilterChain）能成功建立。 */
	@Test
	void contextLoads() {
	}

	/** 未登入使用者可以開啟自訂登入頁。 */
	@Test
	void loginPageIsPublic() throws Exception {
		mockMvc.perform(get("/login"))
			.andExpect(status().isOk())
			.andExpect(forwardedUrl("/login.html"));
	}

	/** 未登入使用者存取受保護資源時會被導向自訂登入頁。 */
	@Test
	void protectedPageRedirectsToLogin() throws Exception {
		mockMvc.perform(get("/resource"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/login"));
	}

	/** 已登入使用者可以進入受保護頁面。 */
	@Test
	void authenticatedUserCanOpenProtectedPage() throws Exception {
		mockMvc.perform(get("/resource").with(user("test-user")))
			.andExpect(status().isOk())
			.andExpect(forwardedUrl("/resource.html"));
	}

	/** 帳密正確且沒有先前保存的請求時，登入成功後預設前往首頁。 */
	@Test
	void validCredentialsRedirectToHome() throws Exception {
		mockMvc.perform(formLogin()
			.user("test-user")
			.password("test-password"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/home"));
	}

	/** 帳密錯誤時依 failureUrl 導向獨立錯誤頁。 */
	@Test
	void invalidCredentialsRedirectToLoginError() throws Exception {
		mockMvc.perform(formLogin()
			.user("invalid-user")
			.password("wrong-password"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/login-error"));
	}

	/** 登出完成後回到登入頁，並帶上供 UI 顯示成功訊息的 logout 參數。 */
	@Test
	void logoutRedirectsToLoginPage() throws Exception {
		mockMvc.perform(logout())
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/login?logout"));
	}

}
