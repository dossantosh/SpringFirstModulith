package com.dossantosh.springfirstmodulith.navigation.api.ports;

public record NavigationItemView(String key, String label, String icon, String route, boolean disabled, String hint) {
}
