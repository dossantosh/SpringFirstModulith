package com.dossantosh.springfirstmodulith;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

/**
 * Verifies the application module boundaries and dependencies.
 */
class ArchitectureTest {

    /**
     * Ensures no illegal cross-module dependencies exist.
     */
    @Test
    void verifiesModuleStructure() {
        
        ApplicationModules.of(com.dossantosh.springfirstmodulith.SpringfirstmodulithApplication.class).verify();
    }
}