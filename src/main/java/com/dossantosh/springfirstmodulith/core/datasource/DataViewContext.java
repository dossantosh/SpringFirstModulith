package com.dossantosh.springfirstmodulith.core.datasource;

/**
 * Holds the current "data view" for the ongoing request.
 *
 * <p>
 * This is used by {@link ViewRoutingDataSource} to route JDBC connections to either the
 * PROD database or the HISTORIC database.
 * </p>
 *
 * <p>
 * We intentionally keep this request-scoped (ThreadLocal) so the choice is stable during
 * a request and does not leak across threads.
 * </p>
 */
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
