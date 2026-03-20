package com.dossantosh.springfirstmodulith.core.datasource.runtime;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class DataViewFromSessionFilter extends OncePerRequestFilter {

	public static final String SESSION_KEY = "DATA_VIEW";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
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
