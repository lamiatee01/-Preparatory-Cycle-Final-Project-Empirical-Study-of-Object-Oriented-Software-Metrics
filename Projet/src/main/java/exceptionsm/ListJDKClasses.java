package exceptionsm;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REMARK:
 * This class uses source code parsing on `src.zip` to extract and classify standard JDK exception classes.
 * Unlike project-specific exception databases (like in Defects4J, where each project contains its own domain-specific exceptions),
 * this approach targets the core and internal Java platform exceptions (e.g., `java.lang.Exception`, `java.io.IOException`, `sun.misc.FooException`, etc.).
 *
 * This is particularly important when analyzing or comparing Java applications from different sources or ecosystems.
 * Without this classification, two independent Java projects might have intersecting custom exception names,
 * making it hard to distinguish between built-in vs user-defined exceptions.
 *
 * By relying on the official JDK `src.zip`, this implementation ensures broader compatibility, accuracy, and avoids false positives
 * when performing exception-type analysis across varied Java environments.
 */

public class ListJDKClasses {
	
	 public static List<String> jdkclasses1() throws IOException {
	        String javaHome = System.getProperty("java.home");
	        Path srcZipPath = Paths.get(javaHome, "lib", "src.zip");
	        
	        Map<String, String> classHierarchy = new HashMap<>();
	        Set<String> allClasses = new HashSet<>();

	        try (FileSystem fs = FileSystems.newFileSystem(srcZipPath, null)) {
	            for (Path root : fs.getRootDirectories()) {
	                Files.walk(root)
	                    .filter(p -> p.toString().endsWith(".java"))
	                    .forEach(path -> {
	                        try {
	                            CompilationUnit cu = StaticJavaParser.parse(Files.newBufferedReader(path));
	                            cu.findAll(ClassOrInterfaceDeclaration.class).forEach(clazz -> {
	                                String className = getFullyQualifiedName(cu, clazz.getNameAsString());
	                                allClasses.add(className);
	                                
	                                clazz.getExtendedTypes().forEach(ext -> {
	                                    String superClass = ext.getNameAsString();
	                                    if (superClass.contains(".")) {
	                                        superClass = getFullyQualifiedName(cu, superClass);
	                                    }
	                                    classHierarchy.put(className, superClass);
	                                });
	                            });
	                        } catch (Exception e) {
	                            // Skip unparseable files
	                        }
	                    });
	            }
	        }

	        List<String> throwableClasses = new ArrayList<>();
	        Set<String> checkedClasses = new HashSet<>();
	        
	        for (String className : allClasses) {
	            if (isThrowableClass1(className, classHierarchy, checkedClasses)) {
	                throwableClasses.add(className);
	            }
	        }
	        
	        return throwableClasses;
	    }

	    private static boolean isThrowableClass1(String className, 
	                                          Map<String, String> hierarchy,
	                                          Set<String> checkedClasses) {
	        // Avoid infinite recursion and re-checking
	        if (checkedClasses.contains(className)) {
	            return false;
	        }
	        checkedClasses.add(className);

	        // Check if this is Throwable itself
	        if (className.equals("Throwable") || className.equals("java.lang.Throwable")) {
	            return true;
	        }

	        // Check hierarchy first
	        String superClass = hierarchy.get(className);
	        if (superClass != null) {
	            if (isThrowableClass1(superClass, hierarchy, checkedClasses)) {
	                return true;
	            }
	        }

	        // Skip reflection for problematic packages
	        if (className.startsWith("sun.") || className.startsWith("com.sun.")) {
	            return false;
	        }

	        // Safe reflection fallback
	        try {
	            Class<?> clazz = Class.forName(className, false, null); // Don't initialize class
	            return Throwable.class.isAssignableFrom(clazz);
	        } catch (ClassNotFoundException | NoClassDefFoundError | UnsatisfiedLinkError e) {
	            return false;
	        } catch (Throwable t) {
	            // Catch everything else to be safe
	            return false;
	        }
	    }

	    private static String getFullyQualifiedName(CompilationUnit cu, String className) {
	        return cu.getPackageDeclaration()
	                .map(pkg -> pkg.getNameAsString() + "." + className)
	                .orElse(className);
	    }

	    

    public static List<String> jdkclasses2() throws IOException {
        String javaHome = System.getProperty("java.home");
        Path srcZipPath = Paths.get(javaHome, "lib", "src.zip");
        
        Map<String, String> classHierarchy = new HashMap<>();
        Set<String> sunClasses = new HashSet<>();

        try (FileSystem fs = FileSystems.newFileSystem(srcZipPath, null)) {
            for (Path root : fs.getRootDirectories()) {
                Files.walk(root)
                    .parallel()  // Use parallel processing for better performance
                    .filter(p -> p.toString().endsWith(".java"))
                    .forEach(path -> {
                        try {
                            CompilationUnit cu = StaticJavaParser.parse(Files.newBufferedReader(path));
                            cu.findAll(ClassOrInterfaceDeclaration.class).forEach(clazz -> {
                                String className = getFullyQualifiedName(cu, clazz.getNameAsString());
                                
                                // Early filtering - only collect sun.* and com.sun.* classes
                                if (className.startsWith("sun.") || className.startsWith("com.sun.")) {
                                    sunClasses.add(className);
                                    
                                    clazz.getExtendedTypes().forEach(ext -> {
                                        String superClass = ext.getNameAsString();
                                        if (superClass.contains(".")) {
                                            superClass = getFullyQualifiedName(cu, superClass);
                                        }
                                        classHierarchy.put(className, superClass);
                                    });
                                }
                            });
                        } catch (Exception e) {
                            
                        }
                    });
            }
        } catch (ProviderNotFoundException e) {
           
        }

        Set<String> checkedClasses = new HashSet<>();
        
        return sunClasses.stream()
                .filter(className -> isThrowableClass2(className, classHierarchy, checkedClasses))
                .sorted()
                .collect(Collectors.toList());
    }

    private static boolean isThrowableClass2(String className, 
            Map<String, String> hierarchy,
            Set<String> checkedClasses) {
        // Avoid infinite recursion and re-checking
        if (checkedClasses.contains(className)) {
            return false;
        }
        checkedClasses.add(className);

        // Check if this is Throwable itself
        if (className.equals("java.lang.Throwable")) {
            return true;
        }

        // Check hierarchy first
        String superClass = hierarchy.get(className);
        if (superClass != null && isThrowableClass2(superClass, hierarchy, checkedClasses)) {
            return true;
        }

        // Safe reflection fallback
        try {
            Class<?> clazz = Class.forName(className, false, ClassLoader.getSystemClassLoader());
            return Throwable.class.isAssignableFrom(clazz);
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            return false;
        } catch (SecurityException e) {   
            return false;
        } catch (Throwable t) {
            return false;
        }
    }

}