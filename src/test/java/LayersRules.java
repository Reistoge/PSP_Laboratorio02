import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = {"dao", "service", "ui"})
public class LayersRules {

    @ArchTest
    static final ArchRule ui_should_only_access_service =
            classes().that().resideInAPackage("ui")
                    .should().onlyAccessClassesThat()
                    .resideInAnyPackage("ui", "service", "java..", "javax..")
                    .because("La capa UI sólo debe acceder a la capa Service y a clases propias del JDK");

    @ArchTest
    static final ArchRule service_should_only_access_dao =
            classes().that().resideInAPackage("service")
                    .should().onlyAccessClassesThat()
                    .resideInAnyPackage("service", "dao", "java..", "javax..")
                    .because("La capa Service sólo debe acceder a la capa DAO y a clases propias del JDK");

    @ArchTest
    static final ArchRule ui_should_not_access_dao =
            noClasses().that().resideInAPackage("ui")
                    .should().accessClassesThat().resideInAPackage("dao")
                    .because("La capa UI no debe depender directamente de la capa DAO");

    @ArchTest
    static final ArchRule dao_should_not_access_service =
            noClasses().that().resideInAPackage("dao")
                    .should().accessClassesThat().resideInAPackage("service")
                    .because("La capa DAO no debe depender de la capa Service");

    @ArchTest
    static final ArchRule dao_should_not_access_ui =
            noClasses().that().resideInAPackage("dao")
                    .should().accessClassesThat().resideInAPackage("ui")
                    .because("La capa DAO no debe depender de la capa UI");

    @ArchTest
    static final ArchRule no_cross_dependencies_in_layers =
            slices().matching("(dao|service|ui)..")
                    .should().beFreeOfCycles()
                    .because("No debe haber dependencias cíclicas entre las capas DAO, Service y UI");
}
