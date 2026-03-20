package com.dossantosh.springfirstmodulith.core.datasource.runtime;

public final class DataViewContext {

	private static final ThreadLocal<String> CURRENT = new ThreadLocal<>();

	public static void set(String view) {
		CURRENT.set(view);
	}

	public static String get() {
		return CURRENT.get();
	}

	public static void clear() {
		CURRENT.remove();
	}

	private DataViewContext() {
	}
}
