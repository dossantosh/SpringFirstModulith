package com.dossantosh.springfirstmodulith.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
public class SessionCookieConfig {

	@Value("${app.security.session-cookie.same-site:Lax}")
	private String sameSite;

	@Value("${app.security.session-cookie.secure:false}")
	private boolean secure;

	@Bean
	public CookieSerializer cookieSerializer() {
		DefaultCookieSerializer serializer = new DefaultCookieSerializer();
		serializer.setSameSite(sameSite);
		serializer.setUseHttpOnlyCookie(true);
		serializer.setUseSecureCookie(secure);
		serializer.setCookiePath("/");
		return serializer;
	}
}
