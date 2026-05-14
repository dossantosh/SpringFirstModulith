package com.dossantosh.springfirstmodulith.navigation.api.ports;

import java.util.Collection;
import java.util.List;

public interface NavigationCatalogQuery {

	List<NavigationModuleView> findVisibleNavigation(Collection<String> scopes);
}
