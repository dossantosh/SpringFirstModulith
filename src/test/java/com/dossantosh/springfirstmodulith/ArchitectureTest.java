package com.dossantosh.springfirstmodulith;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ArchitectureTest {

	private static final String BASE_PACKAGE = "com.dossantosh.springfirstmodulith";

	private static final JavaClasses IMPORTED_CLASSES = new ClassFileImporter()
			.withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS).importPackages(BASE_PACKAGE);

	@Test
	void verifiesModuleStructure() {

		ApplicationModules.of(com.dossantosh.springfirstmodulith.SpringfirstmodulithApplication.class).verify();
	}

	@Test
	void keepsProductionCodeInsideDeclaredModules() {

		classes().that().resideInAPackage(BASE_PACKAGE + "..").and()
				.doNotHaveFullyQualifiedName(SpringfirstmodulithApplication.class.getName()).and()
				.doNotHaveSimpleName("package-info").should()
				.resideInAnyPackage(BASE_PACKAGE + ".auth..", BASE_PACKAGE + ".core..", BASE_PACKAGE + ".perfumes..",
						BASE_PACKAGE + ".security..", BASE_PACKAGE + ".users..")
	}

	@Test
	void keepsDomainIndependentFromOuterLayers() {

		noClasses().that().resideInAPackage("..domain..").should().dependOnClassesThat()
				.resideInAnyPackage("..api..", "..application..", "..infrastructure..")
				.because("domain code must not depend on outer layers").check(IMPORTED_CLASSES);
	}

	@Test
	void keepsApplicationFreeFromWebAndPersistenceDetails() {

		noClasses().that().resideInAPackage("..application..").should().dependOnClassesThat()
				.resideInAnyPackage("..api..", "..infrastructure..")
				.because("application code should coordinate use cases through ports, not web or persistence details")
				.check(IMPORTED_CLASSES);
	}

	@Test
	void preventsInfrastructureLeakageOutsideInfrastructure() {

		noClasses().that().resideOutsideOfPackage("..infrastructure..").should().dependOnClassesThat()
				.resideInAPackage("..infrastructure..")
				.because("adapters and repositories should stay behind the infrastructure boundary")
				.check(IMPORTED_CLASSES);
	}
}
