/*
 * Copyright © 2026 biopatternsg (biopatternsg@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package archtest;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.library.Architectures;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Singleton;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

class ArchitectureLayerTest {

    private static final String ROOT = "com.biopatternsg";

    private JavaClasses javaClasses;

    @BeforeEach
    void init() {
        this.javaClasses = new ClassFileImporter()
                .importPath("target/classes");
    }

    @DisplayName("The class in the layer applications should only used for others class by the same layer")
    @Test
    void layerApplicationTest() {
        Architectures.LayeredArchitecture architecture = layeredArchitecture()
                .consideringAllDependencies()
                .layer("Application").definedBy(ROOT + ".application..")
                .whereLayer("Application").mayOnlyBeAccessedByLayers("Application");
        architecture.check(this.javaClasses);

    }

    @DisplayName("The implementation of the uses cases should not invoked directly")
    @Test
    void layerApplicationUsesCasesTest() {

        Architectures.LayeredArchitecture architecture = layeredArchitecture()
                .consideringAllDependencies()
                .layer("ApplicationUsesCases").definedBy(ROOT+".application.usecase..")
                .whereLayer("ApplicationUsesCases").mayNotBeAccessedByAnyLayer();
        architecture.check(this.javaClasses);

    }
    @DisplayName("The Domain layer should not depend on Application or Infrastructure layers")
    @Test
    void domainLayerIsolationTest() {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage(ROOT + ".domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        ROOT + ".application..",
                        ROOT + ".infrastructure.."
                )
                .check(this.javaClasses);
    }

    @DisplayName("The Application layer should not depend on Infrastructure layer")
    @Test
    void applicationLayerIsolationTest() {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage(ROOT + ".application..")
                .should().dependOnClassesThat().resideInAPackage(ROOT + ".infrastructure..")
                .check(this.javaClasses);
    }
    @DisplayName("Use Cases should follow naming convention")
    @Test
    void useCasesNamingConventionTest() {
        ArchRuleDefinition.classes()
                .that().resideInAPackage("..application.usecase..")
                .should().haveSimpleNameEndingWith("UseCase")
                .check(this.javaClasses);
    }

    @DisplayName("Controllers should follow naming convention")
    @Test
    void controllersNamingConventionTest() {
        ArchRuleDefinition.classes()
                .that().resideInAPackage("..infrastructure.adapters.in.restcontrollers..")
                .should().haveSimpleNameEndingWith("Controller")
                .check(this.javaClasses);
    }

    @DisplayName("Adapters should follow naming convention")
    @Test
    void adaptersNamingConventionTest() {
        ArchRuleDefinition.classes()
                .that().resideInAPackage("..infrastructure.adapters.out..")
                .should().haveSimpleNameEndingWith("Adapter")
                .check(this.javaClasses);
    }

    @DisplayName("The Domain layer should not depend on web, CDI, MicroProfile, or persistence frameworks")
    @Test
    void domainLayerFrameworkIsolationTest() {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage(ROOT + ".domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "jakarta.ws.rs..",
                        "jakarta.enterprise..",
                        "jakarta.inject..",
                        "io.quarkus..",
                        "org.bson..",
                        "org.eclipse.microprofile.."
                )
                .check(this.javaClasses);
    }

    @DisplayName("The Application layer should not depend on delivery or persistence frameworks")
    @Test
    void applicationLayerFrameworkIsolationTest() {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage(ROOT + ".application..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "jakarta.ws.rs..",
                        "io.quarkus.mongodb..",
                        "org.bson.."
                )
                .check(this.javaClasses);
    }

    @DisplayName("The Use Cases in application should not depend on application service implementations")
    @Test
    void useCaseIsolationFromServiceImplTest() {
        ArchRuleDefinition.noClasses()
                .that().resideInAPackage(ROOT + ".application.usecase..")
                .should().dependOnClassesThat().resideInAPackage(ROOT + ".application.services.impl..")
                .check(this.javaClasses);
    }

    @DisplayName("Static mappers should be stateless final utility classes with private constructors and no CDI scopes")
    @Test
    void staticMappersShouldBeStatelessFinalUtilityClassesTest() {
        ArchCondition<JavaClass> beStatelessFinalUtility = new ArchCondition<>("be final with private constructor only") {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                if (!item.getModifiers().contains(JavaModifier.FINAL)) {
                    events.add(SimpleConditionEvent.violated(item, item.getName() + " must be declared final"));
                }
                boolean hasNonPrivateConstructor = item.getConstructors().stream()
                        .anyMatch(ctor -> !ctor.getModifiers().contains(JavaModifier.PRIVATE));
                if (item.getConstructors().isEmpty() || hasNonPrivateConstructor) {
                    events.add(SimpleConditionEvent.violated(item, item.getName() + " must declare only private constructors"));
                }
            }
        };

        ArchRuleDefinition.classes()
                .that().resideInAPackage("..mappers..")
                .and().resideOutsideOfPackage("..exceptions..")
                .and().areTopLevelClasses()
                .should(beStatelessFinalUtility)
                .andShould().notBeAnnotatedWith(ApplicationScoped.class)
                .andShould().notBeAnnotatedWith(Singleton.class)
                .andShould().notBeAnnotatedWith(RequestScoped.class)
                .check(this.javaClasses);
    }
}
