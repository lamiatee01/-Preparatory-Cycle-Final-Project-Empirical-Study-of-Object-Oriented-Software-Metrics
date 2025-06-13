package exceptionsm;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Exceptionscounter {

    static File file;
    private static CompilationUnit cu;
    private List<String> allExceptions;
    static int total=0;
    
    public void setTotal(int v) {
    	total=v;
    } 
    

    public Exceptionscounter(String pathToJavaClass) throws Exception {
        Exceptionscounter.file = new File(pathToJavaClass);

        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + pathToJavaClass);
        }

        String sourceRoot = getSourceRoot();
        System.out.println("Source root: " + sourceRoot);

      
        
        CombinedTypeSolver typeSolver = new CombinedTypeSolver();
        typeSolver.add(new ReflectionTypeSolver()); // Built-in classes like IOException
        typeSolver.add(new JavaParserTypeSolver(new File(sourceRoot))); // Your own classes

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        ParserConfiguration config = new ParserConfiguration()
                .setSymbolResolver(symbolSolver);
        StaticJavaParser.setConfiguration(config);

        cu = StaticJavaParser.parse(file);
        this.allExceptions = mergeExceptionSets();
    }

    public CompilationUnit getCompilationUnit() {
        return cu;
    }

    public List<String> getAllExceptions() {
        return this.allExceptions;
    }

    // throw new
    public static List<String> getThrownExceptions() {
        List<String> exceptions = new ArrayList<>();
        cu.findAll(ObjectCreationExpr.class).forEach(expr -> {
            if (expr.getParentNode().isPresent() && expr.getParentNode().get().toString().contains("throw")) {
                try {
                    ResolvedReferenceType resolvedType = expr.getType().resolve().asReferenceType();
                    exceptions.add(resolvedType.getQualifiedName());
                    total++;
                } catch (Exception ignored) {}
            }
        });
        return exceptions;
    }

    public static List<String> getCaughtExceptions() {
        List<String> exceptions = new ArrayList<>();
        cu.findAll(CatchClause.class).forEach(catchClause -> {
            try {
                ResolvedReferenceType resolvedType = catchClause.getParameter().getType().resolve().asReferenceType();
                exceptions.add(resolvedType.getQualifiedName());
                total++;
            } catch (Exception ignored) {}
        });
        return exceptions;
    }
   
    public static List<String> getDeclaredThrownExceptions() {
        List<String> exceptions = new ArrayList<>();
        
        // Check regular methods
        cu.findAll(ConstructorDeclaration.class).forEach(constructor -> {
        	constructor.getThrownExceptions().forEach(t -> {
                try {
                    ResolvedReferenceType resolvedType = t.resolve().asReferenceType();
                    exceptions.add(resolvedType.getQualifiedName());
                    total++;
                } catch (Exception ignored) {}
            });
        });
        
        // Check constructors too
        cu.findAll(MethodDeclaration.class).forEach(method -> {
            method.getThrownExceptions().forEach(t -> {
                try {
                    ResolvedReferenceType resolvedType = t.resolve().asReferenceType();
                    exceptions.add(resolvedType.getQualifiedName());
                    total++;
                } catch (Exception ignored) {}
            });
        });
        
        return exceptions;
    }
    
    
    public List<String> mergeExceptionSets() {
        List<String> merged = new ArrayList<>();
        merged.addAll(getThrownExceptions());
        merged.addAll(getDeclaredThrownExceptions());
        merged.addAll(getCaughtExceptions());
        return merged;
    }

    // Package utility methods
    public static String getPackageName(File file) throws IOException {
        Pattern pattern = Pattern.compile("^\\s*package\\s+([a-zA-Z0-9_.]+)\\s*;");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
        }
        return ""; // default package
    }

    public String getSourceRoot() throws IOException {
        String packageName = getPackageName(Exceptionscounter.file);
        Path fullPath = file.toPath().toAbsolutePath();
        Path parentPath = fullPath.getParent();
        int depthToGoUp = packageName.isEmpty() ? 0 : packageName.split("\\.").length;
        for (int i = 0; i < depthToGoUp; i++) {
            parentPath = parentPath.getParent();
        }
        return parentPath.toString();
    }

}