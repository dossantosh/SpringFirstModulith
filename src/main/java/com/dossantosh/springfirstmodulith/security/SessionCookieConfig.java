package com.dossantosh.springfirstmodulith.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * Central place to harden the session cookie.
 *
 * <p>
 * For a standalone Angular frontend you typically want to keep requests on the same
 * "site" by routing {@code /api} through the same origin (e.g., nginx), which allows
 * {@code SameSite=Lax} and avoids third-party cookie issues.
 * </p>
 *
 * <p>
 * If you truly deploy frontend and backend on different sites/domains, you must use
 * {@code SameSite=None} and {@code Secure=true} (over HTTPS) or the browser will not send
 * the session cookie.
 * </p>
 */
@Configuration
public class SessionCookieConfig {

    /**
     * SameSite value for the session cookie.
     * Typical values: Lax (default), Strict, None.
     */
    @Value("${app.security.session-cookie.same-site:Lax}")
    private String sameSite;

    /**
     * Whether the session cookie should be marked Secure.
     *
     * <p>
     * In production behind HTTPS this should be true.
     * In local development over HTTP it must be false or the browser will ignore the cookie.
     * </p>
     */
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
