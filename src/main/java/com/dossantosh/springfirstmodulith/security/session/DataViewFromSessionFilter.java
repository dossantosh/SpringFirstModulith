package com.dossantosh.springfirstmodulith.security.session;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.dossantosh.springfirstmodulith.core.datasource.runtime.DataViewContext;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class DataViewFromSessionFilter extends OncePerRequestFilter {

	private final CurrentDataViewQuery currentDataViewQuery;

	public DataViewFromSessionFilter(CurrentDataViewQuery currentDataViewQuery) {
		this.currentDataViewQuery = currentDataViewQuery;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			String view = currentDataViewQuery.getCurrentDataView(request.getSession(false));
			DataViewContext.set(view);
			filterChain.doFilter(request, response);
		} finally {
			DataViewContext.clear();
		}
	}
}
