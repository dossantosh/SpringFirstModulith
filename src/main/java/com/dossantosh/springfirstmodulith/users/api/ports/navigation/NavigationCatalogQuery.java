package com.dossantosh.springfirstmodulith.users.api.ports.navigation;

import java.util.Collection;
import java.util.List;

public interface NavigationCatalogQuery {

	List<NavigationModuleView> findVisibleNavigation(Collection<String> scopes);
}
