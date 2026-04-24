package com.dossantosh.springfirstmodulith.security.session;

import jakarta.servlet.http.HttpSession;

public interface CurrentDataViewQuery {

	String getCurrentDataView(HttpSession session);
}
