import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "layers")
public class LayersRules {
    @ArchTest
    static final ArchRule ui_should_only_access_service =
            classes().that().resideInAPackage("..ui..")
                    .should().onlyAccessClassesThat()
                    .resideInAnyPackage(
                            "..ui..", "..service..", "java..", "javax..") // o "..java.." para cubrir todo JDK
                    .because("UI solo debe acceder a Service (además de Java estándar)");
    @ArchTest
    static final ArchRule service_should_only_access_dao =
            classes().that().resideInAPackage("..service..")
                    .should().onlyAccessClassesThat()
                    .resideInAnyPackage(
                            "..service..", "..dao..", "java..", "javax..")
                    .because("Service solo debe acceder a DAO y clases propias o del JDK");
    @ArchTest
    static final ArchRule ui_should_not_access_dao =
            noClasses().that().resideInAPackage("..ui..")
                    .should().accessClassesThat().resideInAPackage("..dao..")
                    .because("UI no debe depender directamente de DAO");
    @ArchTest
    static final ArchRule dao_should_not_access_service =
            noClasses().that().resideInAPackage("..dao..")
                    .should().accessClassesThat().resideInAPackage("..service..")
                    .because("DAO no debe depender de Service");
    @ArchTest
    static final ArchRule dao_should_not_access_ui =
            noClasses().that().resideInAPackage("..dao..")
                    .should().accessClassesThat().resideInAPackage("..ui..")
                    .because("DAO no debe depender de UI");

    @ArchTest
    static final ArchRule no_cross_dependencies_in_layers =
            slices().matching("layers.(*)..")
                    .should().beFreeOfCycles()
                    .because("No debe haber dependencias cruzadas entre capas");
}