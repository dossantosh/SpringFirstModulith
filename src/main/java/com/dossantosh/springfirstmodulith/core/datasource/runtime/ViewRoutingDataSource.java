package com.dossantosh.springfirstmodulith.core.datasource.runtime;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Routes connections based on the current {@link DataViewContext}.
 */
public class ViewRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return DataViewContext.get();
    }
}
