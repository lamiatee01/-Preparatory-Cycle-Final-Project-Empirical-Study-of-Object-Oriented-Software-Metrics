package NOCMetric;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import defectsUtils.Utility;
import importConflicts.ImportUtil;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Modifier;
import java.util.*;

// la valeur du nembre children is in the attribut nbchildren

public class ChildrenNB {

    private File baseClassFile;
    private CompilationUnit baseCU;
    private File sourceFolder;
    private String baseClassName;

    private int count = 0;
    private int NOC;
    private Set<String> extendingClassNames = new HashSet<>();

    public ChildrenNB(String pathToJavaClass) throws Exception {
        this.baseClassFile = new File(pathToJavaClass);
        this.baseCU = StaticJavaParser.parse(baseClassFile);
        this.sourceFolder = new File(ImportUtil.getSourcePath(baseClassFile.getAbsolutePath(),baseCU));
        this.baseClassName = Utility.getClassName(baseClassFile.getAbsolutePath());
        
        NOC = countClassesExtending();
        
        
    }
    
    public int getNOC() {
    	return NOC;
    }



    // Récursion sur tous les fichiers .java dans le dossier pour trouver les classes qui étendent la classe de base
    private void scanFolder(File folder) {
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isDirectory()) {
                scanFolder(file); // Recurse into subdirectories
            } else if (file.getName().endsWith(".java") && !file.equals(baseClassFile)) {
                try (FileInputStream in = new FileInputStream(file)) {
                    CompilationUnit cu = StaticJavaParser.parse(in);

                    for (ClassOrInterfaceDeclaration cls : cu.findAll(ClassOrInterfaceDeclaration.class)) {
                        if (cls.getExtendedTypes().stream()
                                .anyMatch(t -> t.getNameAsString().equals(baseClassName))) {
                            count++;

                            String packageName = cu.getPackageDeclaration()
                                    .map(pd -> pd.getNameAsString() + ".")
                                    .orElse("");
                            String fullClassName = packageName + cls.getNameAsString();

                            extendingClassNames.add(fullClassName);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Failed to parse " + file.getPath() + ": " + e.getMessage());
                }
            }
        }
    }

    // Lance le comptage
    public int countClassesExtending() {
        scanFolder(sourceFolder);
        return count;
    }

    public Set<String> getExtendingClassNames() {
        return extendingClassNames;
    }

    public static boolean isFinalOrPrivate(Class<?> clazz) {
        int modifiers = clazz.getModifiers();
        return Modifier.isFinal(modifiers) || Modifier.isPrivate(modifiers);
    }

    public static void main(String[] args) {
        try {
            String path = "C:\\Users\\aicha\\Videos\\projet_pluri\\Jsoup\\Bugs\\buggy_1\\src\\main\\java\\org\\jsoup\\nodes\\Node.java";
            ChildrenNB analyzer = new ChildrenNB(path);

            System.out.println("Source folder: " + analyzer.sourceFolder.getAbsolutePath());
            System.out.println("Number of classes extending the base class: "+ analyzer.NOC );

            System.out.println("\nClasses that extend the base class:");
            for (String name : analyzer.getExtendingClassNames()) {
                System.out.println("- " + name);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}