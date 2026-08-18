package com.siven.ch15;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * CH15 Spring Boot 應用程式進入點。
 *
 * <p>{@link SpringBootApplication} 啟用自動設定與元件掃描；
 * {@link RestController} 讓本類別同時提供最小的 REST 示範端點。</p>
 */
@SpringBootApplication
@RestController
public class Ch15Application {

	/**
	 * 啟動內嵌 Web Server 與 Spring Application Context。
	 *
	 * @param args 啟動時傳入的命令列參數
	 */
	public static void main(String[] args) {
		SpringApplication.run(Ch15Application.class, args);
	}

	/**
	 * 提供不需登入即可呼叫的健康檢查／入門示範端點。
	 *
	 * @return 確認應用程式與 Spring Security 已啟動的文字
	 */
	@GetMapping("/")
	public String hello() {
		return "Hello, Spring Security!";
	}

}
