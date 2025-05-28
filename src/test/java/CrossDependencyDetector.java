import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class CrossDependencyDetector {

    private static final List<String> LAYERS = Arrays.asList("dao", "service", "ui");
    private static final String BASE_PACKAGE = "java";

    public static void main(String[] args) throws IOException {
        Path basePath = Paths.get("src/main/java");

        for (String layer : LAYERS) {
            System.out.println("🔍 Buscando ciclos en capa: " + layer.toUpperCase());

            Map<String, Set<String>> graph = new HashMap<>();
            Map<String, List<DependencyInfo>> dependencyDetails = new HashMap<>();

            buildDependencyGraph(basePath.resolve(layer), BASE_PACKAGE + "." + layer, graph, dependencyDetails);
            List<List<String>> cycles = findCycles(graph);

            if (cycles.isEmpty()) {
                System.out.println("✅ Sin ciclos detectados en la capa " + layer + ".\n");
            } else {
                System.out.println("⚠️  Ciclos encontrados en " + layer + ":");
                for (List<String> cycle : cycles) {
                    System.out.println("  - " + String.join(" -> ", cycle) + " -> " + cycle.get(0));

                    for (int i = 0; i < cycle.size(); i++) {
                        String from = cycle.get(i);
                        String to = cycle.get((i + 1) % cycle.size());

                        List<DependencyInfo> infoList = dependencyDetails.getOrDefault(from, Collections.emptyList());
                        for (DependencyInfo info : infoList) {
                            if (info.usedClass.equals(to)) {
                                System.out.println("     ❌ " + info);
                            }
                        }
                    }
                }
                System.out.println();
            }
        }
    }

    private static void buildDependencyGraph(Path dir, String packagePrefix,
                                             Map<String, Set<String>> graph,
                                             Map<String, List<DependencyInfo>> details) throws IOException {

        Map<String, String> simpleNameToFullName = new HashMap<>();

        // Mapeo nombres simples a nombres completos
        Files.walk(dir)
                .filter(p -> p.toString().endsWith(".java"))
                .forEach(p -> {
                    try {
                        CompilationUnit cu = StaticJavaParser.parse(p);
                        String className = cu.getPrimaryTypeName().orElse(p.getFileName().toString().replace(".java", ""));
                        String fullClassName = packagePrefix + "." + className;
                        simpleNameToFullName.put(className, fullClassName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        // Construcción del grafo y detalles
        Files.walk(dir)
                .filter(p -> p.toString().endsWith(".java"))
                .forEach(p -> {
                    try {
                        CompilationUnit cu = StaticJavaParser.parse(p);
                        String className = cu.getPrimaryTypeName().orElse(p.getFileName().toString().replace(".java", ""));
                        String fullClassName = packagePrefix + "." + className;

                        Set<String> deps = new HashSet<>();
                        List<DependencyInfo> infos = new ArrayList<>();

                        cu.findAll(ClassOrInterfaceType.class).forEach(t -> {
                            String used = t.getNameAsString();
                            if (simpleNameToFullName.containsKey(used) && !used.equals(className)) {
                                String fq = simpleNameToFullName.get(used);
                                deps.add(fq);

                                String kind = "uso";
                                if (t.getParentNode().isPresent()) {
                                    var parent = t.getParentNode().get();
                                    kind = parent.getClass().getSimpleName().replace("Declaration", "").toLowerCase();
                                }

                                int line = t.getBegin().map(p1 -> p1.line).orElse(-1);
                                infos.add(new DependencyInfo(fq, p, line, kind));
                            }
                        });

                        graph.put(fullClassName, deps);
                        details.put(fullClassName, infos);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    private static List<List<String>> findCycles(Map<String, Set<String>> graph) {
        List<List<String>> cycles = new ArrayList<>();
        Set<String> visited = new HashSet<>();

        for (String node : graph.keySet()) {
            detectCycleDFS(graph, node, new ArrayList<>(), new HashSet<>(), cycles);
        }

        return cycles;
    }

    private static List<String> normalizeCycle(List<String> cycle) {
        // Find the minimal string (lexicographically) to rotate on
        int minIndex = 0;
        for (int i = 1; i < cycle.size(); i++) {
            if (cycle.get(i).compareTo(cycle.get(minIndex)) < 0) {
                minIndex = i;
            }
        }

        // Rotate the cycle so it always starts at the minimal element
        List<String> normalized = new ArrayList<>();
        for (int i = 0; i < cycle.size(); i++) {
            normalized.add(cycle.get((minIndex + i) % cycle.size()));
        }

        return normalized;
    }
    //----------------------------------------------------------------------
    private static void detectCycleDFS(Map<String, Set<String>> graph, String current,
                                       List<String> path, Set<String> visitedInPath,
                                       List<List<String>> cycles) {
        if (visitedInPath.contains(current)) {
            int idx = path.indexOf(current);
            if (idx != -1) {
                //cycles.add(new ArrayList<>(path.subList(idx, path.size())));
                //----------------------------
                List<String> rawCycle = new ArrayList<>(path.subList(idx, path.size()));
                List<String> normalized = normalizeCycle(rawCycle);

                // SI NO ESTA YA EL NORMALIZADO EN CYCLES, SE INGRESA
                if (!cycles.contains(normalized)) {
                    cycles.add(normalized);
                }
                //----------------------------
            }
            return;
        }

        visitedInPath.add(current);
        path.add(current);

        for (String neighbor : graph.getOrDefault(current, Collections.emptySet())) {
            detectCycleDFS(graph, neighbor, path, new HashSet<>(visitedInPath), cycles);
        }

        path.remove(path.size() - 1);
    }
}

// 👇 Clase auxiliar para guardar detalles de cada dependencia
class DependencyInfo {
    String usedClass;
    Path file;
    int line;
    String kind;

    DependencyInfo(String usedClass, Path file, int line, String kind) {
        this.usedClass = usedClass;
        this.file = file;
        this.line = line;
        this.kind = kind;
    }

    @Override
    public String toString() {
        return "Uso de " + usedClass + " en " + file.getFileName() + ":" + line + " (" + kind + ")";
    }
}
