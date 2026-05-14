package com.dossantosh.springfirstmodulith.navigation.api.ports;

import java.util.List;

public record NavigationModuleView(String key, String label, String icon, List<NavigationItemView> items) {
}
