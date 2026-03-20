package com.dossantosh.springfirstmodulith;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ArchitectureTest {

	@Test
	void verifiesModuleStructure() {

		ApplicationModules.of(com.dossantosh.springfirstmodulith.SpringfirstmodulithApplication.class).verify();
	}
}
