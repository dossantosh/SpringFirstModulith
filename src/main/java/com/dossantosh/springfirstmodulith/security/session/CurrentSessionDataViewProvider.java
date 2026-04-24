package com.dossantosh.springfirstmodulith.security.session;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
public class CurrentSessionDataViewProvider implements CurrentDataViewQuery {

	private static final String SESSION_KEY = "DATA_VIEW";
	private static final String PROD = "prod";
	private static final String HISTORIC = "historic";

	@Override
	public String getCurrentDataView(HttpSession session) {
		if (session == null) {
			return PROD;
		}

		Object raw = session.getAttribute(SESSION_KEY);
		return normalize(raw == null ? null : raw.toString());
	}

	public void storeCurrentDataView(HttpSession session, String dataView) {
		session.setAttribute(SESSION_KEY, normalize(dataView));
	}

	private String normalize(String dataView) {
		return HISTORIC.equals(dataView) ? HISTORIC : PROD;
	}
}
