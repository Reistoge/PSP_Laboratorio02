import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packages = "layers")
public class CrossDependenciesTest {

    @ArchTest
    static final ArchRule no_cross_dependencies_in_ui =
            slices().matching("..ui.(*)")
                    .should().beFreeOfCycles()
                    .because("No debe haber dependencias cruzadas dentro de UI");

    @ArchTest
    static final ArchRule no_cross_dependencies_in_service =
            slices().matching("..service.(*)")
                    .should().beFreeOfCycles()
                    .because("No debe haber dependencias cruzadas dentro de Service");

    @ArchTest
    static final ArchRule no_cross_dependencies_in_dao =
            slices().matching("..dao.(*)")
                    .should().beFreeOfCycles()
                    .because("No debe haber dependencias cruzadas dentro de DAO");
    @ArchTest
    static final ArchRule no_cross_dependencies_in_layers =
            slices().matching("layers.(*)..")
                    .should().beFreeOfCycles()
                    .because("No debe haber dependencias cruzadas entre capas");


}