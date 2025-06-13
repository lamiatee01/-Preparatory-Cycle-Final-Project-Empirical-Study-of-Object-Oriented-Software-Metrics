package importConflicts;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

public class ImportUtil {
	
	//get name of class imported
	public static String getImportedClassName(String importname) {
		
			String [] parts = importname.split("\\.");
			String name = parts[parts.length-1];;
		
		return name;
	}
	
	
	public static String getSourcePath(String classPath ,CompilationUnit cu) {
	    String[] classparts = classPath.split("[/\\\\]");
	    try {
	    	PackageDeclaration pkgdec = cu.getPackageDeclaration().get();
	    	String pkg = pkgdec.getNameAsString();
	    	String[] pkgparts = pkg.split("\\.");
	    
	    	int sourceLength = classparts.length - pkgparts.length - 1; // -1 for the class file

	    	StringBuilder source = new StringBuilder();
	    	for (int i = 0; i < sourceLength; i++) {
	    		source.append(classparts[i]).append("\\");
	    	}
	    	
	    	return source.toString();
	    	
	    }catch(Exception e) {
	    	File file = new File(classPath);
	    	
	    	System.out.println("no package" +file.getParent());
	    	return file.getParent();
	    }
	    
	}
	
	public static boolean isUserPackage(String classpath,CompilationUnit cu, String imppkg) throws IOException {
		HashSet<Path> javaFiles = new HashSet<>();
		JavaParser parser = new JavaParser();
		Path source = Paths.get(ImportUtil.getSourcePath(classpath, cu));//get source folder
		
		
        
        
        Stream<Path> stream = Files.walk(source); //get all .java files in source
        Iterator<Path> iterator = stream.iterator();
        while (iterator.hasNext()) {
            Path file = iterator.next();
            if (file.toString().endsWith(".java")) {
                javaFiles.add(file);
            }
        }
        stream.close();
        //use java parser on all classes in package
        for (Path file : javaFiles) {

            CompilationUnit cup = parser.parse(file).getResult().orElseThrow();

            // get the package of the current class in the package
            PackageDeclaration p = cup.getPackageDeclaration().orElse(null);
            String filePackage = "";
            if (p != null) {
                filePackage = p.getNameAsString();
            }


            // if we find a file with the same package then that package is user made
            if (filePackage.equals(imppkg)) {
                	return true;
            }
		}
        
        return false;
	}

	
	
	//return a hashset of all the classes name inside a package
	public static HashSet<String> getClassesInPackage(String pkg) {
		
		HashSet<String> classesname = new HashSet<>(); 
		ClassGraph classGraph = new ClassGraph();
		
		classGraph = classGraph.acceptPackages(pkg).enableSystemJarsAndModules();
		ScanResult scanResult = classGraph.scan();
		ClassInfoList classes = scanResult.getAllClasses(); 
		 
		for(ClassInfo cls: classes) {
			classesname.add(cls.getName());
		}
		
		scanResult.close();
		 
		return classesname;
		 
	}
	
	
	public static HashSet<String> getClassesInUserPackage(String classPath, String targetPackage,CompilationUnit cu) throws IOException {
        HashSet<String> classes = new HashSet<>();
        JavaParser parser = new JavaParser();
        Path sourceRoot = Paths.get(getSourcePath(classPath,cu));
        HashSet<Path> javaFiles = new HashSet<>();
        
      //get all the .java in source folder
        Stream<Path> stream = Files.walk(sourceRoot); 
        Iterator<Path> iterator = stream.iterator();
        while (iterator.hasNext()) {
            Path file = iterator.next();
            if (file.toString().endsWith(".java")) {
                javaFiles.add(file);
            }
        }
        stream.close(); 

        //use java parser on all .java files
        for (Path file : javaFiles) {
            CompilationUnit cup = parser.parse(file).getResult().orElseThrow();
            // get the package of the current file in the source
            PackageDeclaration p = cup.getPackageDeclaration().orElse(null);
            String filePackage = "";
            if (p != null) {
                filePackage = p.getNameAsString();
            }
            // if the package of the file is the same as package as the package we want to get all it's classes then we save it's name
            if (filePackage.equals(targetPackage)) {
                List<ClassOrInterfaceDeclaration> classDecls = cup.findAll(ClassOrInterfaceDeclaration.class);
                for (ClassOrInterfaceDeclaration cls : classDecls) {
                    classes.add(cls.getNameAsString());
                }
            }
        }

        return classes;
    }
 	
	
	
	
	public static HashSet<String> getDuplicateImports(CompilationUnit cu) {
		HashMap<String,Integer> duplicates = new HashMap<>();
		HashSet<String> dups = new HashSet<>();
		
		for (String impname : ImportsResolver.getImports(cu)) {
			duplicates.put(impname, duplicates.getOrDefault(impname, 0)+1);
		}
		
		
		for(Map.Entry<String,Integer> entry : duplicates.entrySet()) {
			if(entry.getValue()>1) {
				dups.add(entry.getKey());
			}
		}
			
		return dups;
	}
	
	//if a class is imported from diffrent packages with the same name it is count as one conflict
	public static HashSet<String> getImportConflictsWildCard(CompilationUnit cu,String classPath) throws IOException {
		HashMap<String,Integer> duplicates = new HashMap<>();
		HashSet<String> conflicts = new HashSet<>();
		
		for (String name : getAllAccessibleClassesWildCard(cu,classPath)) {
			duplicates.put(name, duplicates.getOrDefault(name, 0)+1);
		}
		
		
		for(Map.Entry<String,Integer> entry : duplicates.entrySet()) {
			if(entry.getValue()>1) {
				conflicts.add(entry.getKey());
			}
		}
		

		return conflicts;
	}
	
	public static HashSet<String> getImportConflictsSpecific(CompilationUnit cu) {
		HashMap<String,Integer> duplicates = new HashMap<>();
		HashSet<String> conflicts = new HashSet<>();		
		
		for (String name : getAllAccessibleClassesSpecific(cu)) {
			duplicates.put(name, duplicates.getOrDefault(name, 0)+1);
		}
		
		for(Map.Entry<String,Integer> entry : duplicates.entrySet()) {
			if(entry.getValue()>1) {
				conflicts.add(entry.getKey());
			}
		}
		
		return conflicts;
	}
	
	
	
	public static ArrayList<String> getAllAccessibleClassesWildCard (CompilationUnit cu,String classPath) throws IOException{
		ArrayList<String> classes = new ArrayList<>();
		HashSet<String> clsinpkg = new HashSet<>();
		for (String imp : ImportsResolver.getWildCardImports(cu)) {
			
			if(isUserPackage(classPath,cu,imp))//check if package is user made or not
				clsinpkg = getClassesInUserPackage(classPath,imp,cu);
			else 
				clsinpkg = getClassesInPackage(imp);
			
			for(String cls :clsinpkg)
				if(isFound(cu, getImportedClassName(cls)) && !hasSpecificImport(cu,getImportedClassName(cls),imp))
					classes.add(getImportedClassName(cls));
		}
		return classes;
	}
	
	
	public static ArrayList<String> getAllAccessibleClassesSpecific (CompilationUnit cu){
		ArrayList<String> classes = new ArrayList<>();
		
		for(String imp : ImportsResolver.getSpecificImports(cu)) {
			if(isFound(cu, getImportedClassName(imp)))
				classes.add(getImportedClassName(imp));
		}
		
		return classes;
	}
	
	//checks if a class from a wild import package has a specific import 
	public static boolean hasSpecificImport(CompilationUnit cu,String classname, String wildcardimport) {
		String fullname = wildcardimport +"."+ classname;
		
		if(ImportsResolver.getSpecificImports(cu).contains(fullname)) {
			return true;
		}
		
		return false;
	}
	
	
	//checks if a wildcard import is used
	public static boolean isUsedWildCard (CompilationUnit cu, String wildcardimp,String classPath) throws IOException {
		HashSet<String> classesinpkg = new HashSet<>();
		
		if(isUserPackage(classPath,cu,wildcardimp))//check if package is user made or not
			classesinpkg = getClassesInUserPackage(classPath,wildcardimp,cu);
		else 	
			classesinpkg = getClassesInPackage(wildcardimp);
		
		HashSet<String> conflicts = getImportConflictsWildCard(cu,classPath);
		//conflicts.addAll(getImportConflictsSpecific(cu));
		ArrayList<String> specific = getAllAccessibleClassesSpecific(cu);
		String name;
		//boolean unused = true;
		for(String cls : classesinpkg) {
			name = getImportedClassName(cls);
			//hes specific import checks same package specific contains checks diffrenet packages
			if(isFound(cu,name) && !conflicts.contains(name)  && !specific.contains(name))
				return true;
		
		}
		
		return false;
	
	}
	
	public static boolean IsUnusedWildCard(CompilationUnit cu, String wildcardimp,String classPath) throws IOException {
		HashSet<String> classesinpkg = new HashSet<>();
		
		if(isUserPackage(classPath,cu,wildcardimp))//check if package is user made or not
			classesinpkg = getClassesInUserPackage(classPath,wildcardimp,cu);
		else 	
			classesinpkg = getClassesInPackage(wildcardimp);
		
		ArrayList<String> specific = getAllAccessibleClassesSpecific(cu);
	    boolean unused = true;
	    HashSet<String> conflicts = getImportConflictsWildCard(cu,classPath);

	    for (String cls : classesinpkg) {
	        String name = getImportedClassName(cls);  

	        if (isFound(cu, name) && !specific.contains(name)) {
	            if (conflicts.contains(name)) {
	                return false;
	            } else {
	                unused = false;
	            }
	        }
	    }

	    return unused;
	}
	
	public static HashSet<String> getExternalImports (){
		HashSet<String> external = new HashSet<>();
		
		return external;
	}
	
	//checks if class name is mentioned in one of the elements of the code
	public static boolean isFound(CompilationUnit cu, String importedclassname) {
		
		if(TypeUsageAnalyser.getGenericsType(cu).contains(importedclassname)) {
			return true;
		}else if(TypeUsageAnalyser.getStaticClassesNames(cu).contains(importedclassname)){
			return true;
		}else if(TypeUsageAnalyser.getAttributesType(cu).contains(importedclassname)) {
			return true;
		}else if (TypeUsageAnalyser.getMethodsType(cu).contains(importedclassname)) {
			return true;
		}else if (TypeUsageAnalyser.getParametersType(cu).contains(importedclassname)) {
			return true;
		}else if (TypeUsageAnalyser.getLocalVarsType(cu).contains(importedclassname)) {
			return true;
		}else if (TypeUsageAnalyser.getCastsType(cu).contains(importedclassname)) {
			return true;
		}else if (TypeUsageAnalyser.getInheiritedAndEmplementedClassesType(cu).contains(importedclassname)) {
			return true;
		}else if (TypeUsageAnalyser.getThrowsType(cu).contains(importedclassname)) {
			return true;
		}else if (TypeUsageAnalyser.getObjectsType(cu).contains(importedclassname)) {
			return true;
		}
			
		
		return false;
	}
	
	
}
