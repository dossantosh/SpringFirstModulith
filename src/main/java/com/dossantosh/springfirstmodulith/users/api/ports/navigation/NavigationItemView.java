package com.dossantosh.springfirstmodulith.users.api.ports.navigation;

public record NavigationItemView(String key, String label, String icon, String route, boolean disabled, String hint) {
}
