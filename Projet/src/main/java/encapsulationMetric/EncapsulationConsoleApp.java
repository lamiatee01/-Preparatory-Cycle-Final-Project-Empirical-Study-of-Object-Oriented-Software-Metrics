package encapsulationMetric;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.NameExpr;

public class EncapsulationConsoleApp {

    public static void main(String[] args) {
        File projectFolder = new File("PATH TO PROJECT!"); // ← Change this to your local folder path
        String result = analyzeProject(projectFolder);
        System.out.println(result);
    }

    private static String analyzeProject(File folder) {
        StringBuilder txtResult = new StringBuilder();
        List<File> javaFiles = listJavaFiles(folder);

        txtResult.append("Project Path: ").append(folder.getAbsolutePath()).append("\n");
        txtResult.append("Classes Analyzed: ").append(javaFiles.size()).append("\n\n");

        if (javaFiles.isEmpty()) {
            txtResult.append("No Java files found in the selected folder.");
            return txtResult.toString();
        }

        for (File javaFile : javaFiles) {
            EncapsulationMetrics metrics = analyzeEncapsulation(javaFile);
            txtResult.append(metrics.toText()).append("\n----------------------\n");
        }

        txtResult.append("\n Analysis complete!");
        return txtResult.toString();
    }

    public static EncapsulationMetrics analyzeEncapsulation(File javaFile) {
        try {
            JavaParser parser = new JavaParser();
            ParseResult<CompilationUnit> parseResult = parser.parse(javaFile);

            if (parseResult.isSuccessful() && parseResult.getResult().isPresent()) {
                CompilationUnit cu = parseResult.getResult().get();
                String className = cu.getTypes().isEmpty()
                        ? javaFile.getName()
                        : cu.getType(0).getNameAsString();

                List<FieldDeclaration> fields = cu.findAll(FieldDeclaration.class);
                int total = 0, pub = 0, pri = 0, pro = 0, def = 0;

                for (FieldDeclaration field : fields) {
                    int count = field.getVariables().size();
                    total += count;
                    if (field.isPublic()) pub += count;
                    else if (field.isPrivate()) pri += count;
                    else if (field.isProtected()) pro += count;
                    else def += count;
                }

                // LCOM5 calculation
                List<VariableDeclarator> variables = cu.findAll(VariableDeclarator.class);
                List<MethodDeclaration> methods = cu.findAll(MethodDeclaration.class);

                int M = methods.size();
                int A = variables.size();
                int sumMF = 0;

                for (VariableDeclarator var : variables) {
                    String varName = var.getNameAsString();
                    int count = 0;

                    for (MethodDeclaration method : methods) {
                        List<NameExpr> names = method.findAll(NameExpr.class);
                        for (NameExpr nameExpr : names) {
                            if (nameExpr.getNameAsString().equals(varName)) {
                                count++;
                                break;
                            }
                        }
                    }

                    sumMF += count;
                }

                double lcom5 = (M > 0 && A > 0) ? 1.0 - ((double) sumMF / (M * A)) : -1.0;

                return new EncapsulationMetrics(className, total, pub, pri, pro, def, lcom5);
            }
        } catch (IOException e) {
            return new EncapsulationMetrics("❌ Error: " + javaFile.getName(), 0, 0, 0, 0, 0, -1.0);
        }

        return new EncapsulationMetrics("❌ Failed: " + javaFile.getName(), 0, 0, 0, 0, 0, -1.0);
    }

    private static List<File> listJavaFiles(File folder) {
        List<File> javaFiles = new ArrayList<>();
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    javaFiles.addAll(listJavaFiles(file));
                } else if (file.getName().endsWith(".java")) {
                    javaFiles.add(file);
                }
            }
        }
        return javaFiles;
    }

   /* static class EncapsulationMetrics {
        String className;
        int total, pub, pri, pro, def;
        double lcom5;

        public EncapsulationMetrics(String className, int total, int pub, int pri, int pro, int def ,double lcom5) {
            this.className = className;
            this.total = total;
            this.pub = pub;
            this.pri = pri;
            this.pro = pro;
            this.def = def;
            this.lcom5 = lcom5;
        }

        private double getPublicRate() {
            return total == 0 ? 0 : (pub * 100.0) / total;
        }

        private double getPrivateRate() {
            return total == 0 ? 0 : (pri * 100.0) / total;
        }

        private double getProtectedRate() {
            return total == 0 ? 0 : (pro * 100.0) / total;
        }

        private double getPackagePrivateRate() {
            return total == 0 ? 0 : (def * 100.0) / total;
        }

        public String toText() {
            return String.format(
                "🎀 Class: %s\n" +
                "✨ Total Attributes: %d\n" +
                "🔓 Public: %d (%.1f%%)\n" +
                "🔐 Private: %d (%.1f%%)\n" +
                "🛡️ Protected: %d (%.1f%%)\n" +
                "📦 Package-Private: %d (%.1f%%)\n" +
                "📊 LCOM5: %.3f",
                className, total,
                pub, getPublicRate(),
                pri, getPrivateRate(),
                pro, getProtectedRate(),
                def, getPackagePrivateRate(),
                lcom5
            );
        }
    }*/
}
