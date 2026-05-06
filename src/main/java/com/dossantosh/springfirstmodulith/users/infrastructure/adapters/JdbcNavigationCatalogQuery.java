package com.dossantosh.springfirstmodulith.users.infrastructure.adapters;

import com.dossantosh.springfirstmodulith.users.api.ports.navigation.NavigationCatalogQuery;
import com.dossantosh.springfirstmodulith.users.api.ports.navigation.NavigationItemView;
import com.dossantosh.springfirstmodulith.users.api.ports.navigation.NavigationModuleView;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

@Component
class JdbcNavigationCatalogQuery implements NavigationCatalogQuery {

	private static final String SQL = """
			select distinct
			       coalesce(m.navigation_key, lower(m.name)) as module_key,
			       coalesce(m.label, m.name) as module_label,
			       coalesce(m.icon, 'dashboard') as module_icon,
			       m.sort_order as module_sort_order,
			       coalesce(sm.navigation_key, lower(sm.name)) as item_key,
			       coalesce(sm.label, sm.name) as item_label,
			       coalesce(sm.icon, 'radio_button_unchecked') as item_icon,
			       sm.route as item_route,
			       sm.disabled as item_disabled,
			       sm.hint as item_hint,
			       sm.sort_order as item_sort_order
			from modules m
			join submodules sm on sm.id_module = m.id_module
			join submodule_required_scopes srs on srs.id_submodule = sm.id_submodule
			join scopes sc on sc.id_scope = srs.id_scope
			where sc.name in (:scopes)
			order by module_sort_order, module_label, item_sort_order, item_label
			""";

	private final NamedParameterJdbcTemplate jdbcTemplate;

	JdbcNavigationCatalogQuery(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public List<NavigationModuleView> findVisibleNavigation(Collection<String> scopes) {
		if (scopes == null || scopes.isEmpty()) {
			return List.of();
		}

		MapSqlParameterSource params = new MapSqlParameterSource("scopes", scopes);
		List<NavigationRow> rows = jdbcTemplate.query(SQL, params, (rs, rowNum) -> new NavigationRow(
				rs.getString("module_key"), rs.getString("module_label"), rs.getString("module_icon"),
				rs.getString("item_key"), rs.getString("item_label"), rs.getString("item_icon"),
				rs.getString("item_route"), rs.getBoolean("item_disabled"), rs.getString("item_hint")));

		LinkedHashMap<String, ModuleBuilder> modules = new LinkedHashMap<>();
		for (NavigationRow row : rows) {
			modules.computeIfAbsent(row.moduleKey(),
					key -> new ModuleBuilder(key, row.moduleLabel(), row.moduleIcon())).addItem(row);
		}

		return modules.values().stream().map(ModuleBuilder::build).toList();
	}

	private record NavigationRow(String moduleKey, String moduleLabel, String moduleIcon, String itemKey,
			String itemLabel, String itemIcon, String itemRoute, boolean itemDisabled, String itemHint) {
	}

	private static final class ModuleBuilder {

		private final String key;
		private final String label;
		private final String icon;
		private final List<NavigationItemView> items = new ArrayList<>();

		private ModuleBuilder(String key, String label, String icon) {
			this.key = key;
			this.label = label;
			this.icon = icon;
		}

		private void addItem(NavigationRow row) {
			items.add(new NavigationItemView(row.itemKey(), row.itemLabel(), row.itemIcon(), row.itemRoute(),
					row.itemDisabled(), row.itemHint()));
		}

		private NavigationModuleView build() {
			return new NavigationModuleView(key, label, icon, List.copyOf(items));
		}
	}
}
