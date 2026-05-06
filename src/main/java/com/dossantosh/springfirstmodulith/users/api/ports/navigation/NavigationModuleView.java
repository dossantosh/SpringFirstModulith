package com.dossantosh.springfirstmodulith.users.api.ports.navigation;

import java.util.List;

public record NavigationModuleView(String key, String label, String icon, List<NavigationItemView> items) {
}
