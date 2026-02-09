package com.dossantosh.springfirstmodulith.core.datasource.runtime;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Reads the selected data view (prod/historic) from the HTTP session and
 * sets {@link DataViewContext} for the duration of the request.
 *
 * <p>
 * IMPORTANT: This filter is intentionally NOT a @Component, so it is NOT registered
 * as a servlet container filter. It must be added to Spring Security's filter chain.
 * </p>
 */
public class DataViewFromSessionFilter extends OncePerRequestFilter {

    /**
     * Session attribute name holding the current data view.
     *
     * <p>
     * Allowed values: "prod" (default) or "historic".
     * </p>
     */
    public static final String SESSION_KEY = "DATA_VIEW";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String view = "prod";
            HttpSession session = request.getSession(false);
            if (session != null) {
                Object raw = session.getAttribute(SESSION_KEY);
                if ("historic".equals(raw)) {
                    view = "historic";
                }
            }

            DataViewContext.set(view);
            filterChain.doFilter(request, response);
        } finally {
            DataViewContext.clear();
        }
    }
}
