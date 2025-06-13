package defectsUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Stream;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import CBO.CBOMetric;
import DitOomr.MethodCollector;
import NOCMetric.ChildrenNB;
import encapsulationMetric.EncapsulationConsoleApp;
import encapsulationMetric.EncapsulationMetrics;
import importConflicts.ImportMetric;
import importConflicts.ImportUtil;
import javalyzerx.JavalyzerxMetric;
import exceptionsm.ExceptionsMetric;



public class Utility {
	
	//main method to make the cvs files of defects4j files
	public static void WriteCsvDefects4j(String defects4jpath, String csvfolderpath, String projectname, String projectsfolder) throws Exception {
		
		String csvpathbuggy = csvfolderpath +"\\"+ projectname +"_Bugs"+ ".csv";
		String csvpathfixed = csvfolderpath +"\\"+ projectname +"_Fixed"+ ".csv";
		System.out.println(csvpathbuggy);
		String patchpath = defects4jpath + "\\framework\\projects\\" + projectname + "\\patches";
		ArrayList<String> classpaths = getBugClassesFromPatches(patchpath);
		
		FileWriter writerbugs = new FileWriter(csvpathbuggy);
		FileWriter writerfixed = new FileWriter(csvpathfixed);
		writerbugs.write(" ,ICT,ICU,ICUN,ICD,ICUJ,ICC,ICW,CBO,TA,PUBA,PRIA,PROA,DA,LCOM5,JAXTL,JAXCL,JAXCD,JAXNC,JAXID,JAXII,JAXAC,JAXAM,NOC,TNE,JEC,CEC,JCKE,RTE,ERR,DIT,TM,EDM,ORM,OLM,ORR,OLR,ORMNA,ECORM,EOLM,AOLM,OOM,OMSC\n");
		writerfixed.write(" ,ICT,ICU,ICUN,ICD,ICUJ,ICC,ICW,CBO,TA,PUBA,PRIA,PROA,DA,LCOM5,JAXTL,JAXCL,JACD,JAXNC,JAXID,JAXII,JAXAC,JAXAM,NOC,TNE,JEC,CEC,JCKE,RTE,ERR,DIT,TM,EDM,ORM,OLM,ORR,OLR,ORMNA,ECORM,EOLM,AOLM,OOM,OMSC\n");
		String fullclasspathbugs ="";
		String fullclasspathfixed ="";
		
		Map<String, Set<Integer>> deprecatedBugs = new HashMap<>();

		deprecatedBugs.put("Cli", Set.of(6));
		deprecatedBugs.put("Closure", Set.of(63, 93));
		deprecatedBugs.put("JacksonDatabind", Set.of(65, 89));
		deprecatedBugs.put("Lang", Set.of(2, 18, 25, 48));
		deprecatedBugs.put("Time", Set.of(21));
		
		
		int i =1;
		for(String path : classpaths) {
			Set<Integer> deprecated = deprecatedBugs.getOrDefault(projectname, Collections.emptySet());
		    
		    if (deprecated.contains(i)) {
		        i++;
		        continue; // Skip deprecated bug
		    }
			
			//analyze the bugs
			fullclasspathbugs = projectsfolder +"\\"+ projectname + "\\Bugs\\Buggy_" + i + "\\" + path;
			
			File filebugs = new File(fullclasspathbugs);
			
			HashSet<File> jarfilesinm2buggy = Utility.getJarsFromM2(fullclasspathbugs);
			
			CombinedTypeSolver combinedsolver = getCombinedTypeSolver(fullclasspathbugs,jarfilesinm2buggy);
			CompilationUnit cu = getCompilationUnit(fullclasspathbugs,combinedsolver);
			ImportMetric imp = new ImportMetric(cu,fullclasspathbugs);
			EncapsulationMetrics encap = EncapsulationConsoleApp.analyzeEncapsulation(filebugs);
			JavalyzerxMetric jxm = new JavalyzerxMetric(cu,fullclasspathbugs);
			CBOMetric cbom = new CBOMetric(cu,combinedsolver);
			ChildrenNB nocm = new ChildrenNB(fullclasspathbugs);
			ExceptionsMetric ex = new ExceptionsMetric(fullclasspathbugs);
			MethodCollector mc = new MethodCollector(filebugs,jarfilesinm2buggy);
			
			
			
			writerbugs.write("Buggy "+i+" : " + getClassName(path) + "," + imp.getTotalImports() + "," + imp.getUsedImports() + "," 
					+ imp.countUnusedImports(cu) +"," + imp.countDuplicateImports(cu) + "," + imp.countUnjudgedImports()
					+ "," + imp.getImportConflicts() + "," + imp.getWildCardImports() 
					+","+ cbom.getCBO()+ "," + encap.getTotal() + "," + encap.getPublic() + "," + encap.getPrivate() + "," 
					+ encap.getProtected() + "," + encap.getDefault() + "," + encap.getLCOM5()+ "," + 
					+jxm.getTotalLines() + "," +jxm.getLinesWithComments()+ "," + jxm.getClassDeclaration() +"," + jxm.getNestedClasses()
					+ "," +jxm.getInterfaceDeclaration()+ "," +jxm.getImplementedInterfaces()+ "," +jxm.getAbstractClasses()
					+ "," +jxm.getAbstractMethods()+","+nocm.getNOC()
					+"," +ex.getTotal() + "," + ex.getJdkExceptions()+ "," + ex.getCustomExceptions()+ "," + ex.getJdkCheckedExceptions() 
					+ "," +ex.getJdkRuntimeExceptions() + "," +ex.getJdkErrors() 
					+ "," +mc.getDIT()+ "," +mc.getTotalMethods()+ "," +mc.getExplictmethods() + "," + mc.getOrmethods() + "," +mc.getOlmethods()  
					+ "," +mc.getOrratio()+ "," +mc.getOlration()+ "," +mc.getOrwithnoano()+ "," +mc.getMtconstmethods()
					+ "," +mc.getExcesiveol()+ "," +mc.getAmbgmethods()+ "," +mc.getOlandor()+ "," +mc.getOrwithsuper()+"\n");
			
			
			//analyze the fixed
			fullclasspathfixed = projectsfolder +"\\"+ projectname + "\\Fixed\\Fixed_" + i + "\\" + path;
			File filefixed = new File(fullclasspathfixed);
			
			HashSet<File> jarfilesinm2fixed = Utility.getJarsFromM2(fullclasspathfixed);
			
			CombinedTypeSolver combinedsolverfixed = getCombinedTypeSolver(fullclasspathfixed,jarfilesinm2fixed);
			CompilationUnit cufixed = getCompilationUnit(fullclasspathfixed,combinedsolverfixed);
			ImportMetric impfixed = new ImportMetric(cufixed,fullclasspathfixed);
			EncapsulationMetrics encapfixed = EncapsulationConsoleApp.analyzeEncapsulation(filefixed);
			JavalyzerxMetric jxmfixed = new JavalyzerxMetric(cufixed,fullclasspathfixed);
			CBOMetric cbomfixed = new CBOMetric(cufixed,combinedsolverfixed);
			ChildrenNB nocmfixed = new ChildrenNB(fullclasspathfixed);
			ExceptionsMetric exfixed = new ExceptionsMetric(fullclasspathfixed);
			MethodCollector mcfixed = new MethodCollector(filefixed,jarfilesinm2fixed);
			
			
			writerfixed.write("Fixed "+i+" : " + getClassName(path) + "," + impfixed.getTotalImports() + "," + impfixed.getUsedImports() + "," 
					+ impfixed.countUnusedImports(cu) +"," + impfixed.countDuplicateImports(cu) + "," + impfixed.countUnjudgedImports()
					+ "," + impfixed.getImportConflicts() + "," + impfixed.getWildCardImports() 
					+","+ cbomfixed.getCBO()
					+ "," + encapfixed.getTotal() + "," + encapfixed.getPublic() + "," + encapfixed.getPrivate() + "," 
					+ encapfixed.getProtected() + "," + encapfixed.getDefault() + "," + encapfixed.getLCOM5()+ "," + 
					+jxmfixed.getTotalLines() + "," +jxmfixed.getLinesWithComments()+ "," + jxmfixed.getClassDeclaration() +"," + jxmfixed.getNestedClasses()
					+ "," +jxmfixed.getInterfaceDeclaration()+ "," +jxmfixed.getImplementedInterfaces()+ "," +jxmfixed.getAbstractClasses()
					+ "," +jxmfixed.getAbstractMethods()+","+nocmfixed.getNOC()
					+"," +exfixed.getTotal() + "," + exfixed.getJdkExceptions()+ "," + exfixed.getCustomExceptions()+ "," + exfixed.getJdkCheckedExceptions() 
					+ "," +exfixed.getJdkRuntimeExceptions() + "," +ex.getJdkErrors() 
					+ "," +mcfixed.getDIT()+ "," +mcfixed.getTotalMethods()+ "," +mcfixed.getExplictmethods() + "," + mcfixed.getOrmethods() + "," +mcfixed.getOlmethods()  
					+ "," +mcfixed.getOrratio()+ "," +mcfixed.getOlration()+ "," +mcfixed.getOrwithnoano()+ "," +mcfixed.getMtconstmethods()
					+ "," +mcfixed.getExcesiveol()+ "," +mcfixed.getAmbgmethods()+ "," +mcfixed.getOlandor()+ "," +mcfixed.getOrwithsuper()+"\n");
			
			
			i++;
		
		}
		writerbugs.close();
		writerfixed.close();
		
		System.out.println("done");
		
		
	}
	
	
	//gets the path of a class inside a project from the patch file
	public static String getPathFromPatch (String patchPath) {
		String thirdLine = "";
	     try (BufferedReader reader = new BufferedReader(new FileReader(patchPath))) {
        	reader.readLine();
        	reader.readLine();
        	thirdLine = reader.readLine(); 
        } catch (IOException e) {
            e.printStackTrace();
        }
	     
	     String path = thirdLine.substring("--- a/".length());
	     return path;
	}
	
	//get the classes that have bugs in each project
	public static ArrayList<String> getBugClassesFromPatches(String PathToPatchesFolder){
		ArrayList<String> clspaths = new ArrayList<>();
		Path folderPath = Paths.get(PathToPatchesFolder);

        try (Stream<Path> paths = Files.walk(folderPath)) {
            paths
                .filter(path -> path.getFileName().toString().contains("src"))
                .sorted(Comparator.comparingInt(path -> {
                    String name = path.getFileName().toString();
                    try {
                        return Integer.parseInt(name.split("\\.")[0]); // assumes name starts with number
                    } catch (NumberFormatException e) {
                        return Integer.MAX_VALUE;
                    }
                }))
                .forEach(path -> clspaths.add(getPathFromPatch(path.toString())));
        }catch(IOException e) {
        }
		return clspaths;
	}
	
	//configures the compilation unit of a class
	public static CompilationUnit getCompilationUnit(String classPath, CombinedTypeSolver solver) throws FileNotFoundException {
	    ParserConfiguration config = new ParserConfiguration()
	            .setSymbolResolver(new JavaSymbolSolver(solver));
	    JavaParser parser = new JavaParser(config);

	    File file = new File(classPath);
	    return parser.parse(file).getResult().orElseThrow();
	}
	
	
	//gets the class name from path
	public static String getClassName (String classpath) {
		String[] parts = classpath.split("[/\\\\]");
		String[] name = parts[parts.length-1].split("\\.");
		return name[0];
	}
	

	//configures the typesolver
	public static CombinedTypeSolver getCombinedTypeSolver(String fullclasspath, HashSet<File> jarfilesinm2) throws IOException {
	    File javaFile = new File(fullclasspath);
	    CombinedTypeSolver combinedSolver = new CombinedTypeSolver();
	    
	    //add jdk classes
	    combinedSolver.add(new ReflectionTypeSolver());

	    ParserConfiguration config = new ParserConfiguration()
	            .setSymbolResolver(new JavaSymbolSolver(combinedSolver));
	    JavaParser parser = new JavaParser(config);

	    CompilationUnit cu = parser.parse(javaFile).getResult().orElseThrow(() ->
        		new RuntimeException("Failed to parse: " + fullclasspath));


	    String sourcePath = ImportUtil.getSourcePath(fullclasspath, cu);

	    if (sourcePath != null) {
	    	File srcDir = new File(sourcePath);
	        System.out.println("src dir:" + srcDir.getAbsolutePath());
	        if (srcDir.exists()) {
	            combinedSolver.add(new JavaParserTypeSolver(srcDir));
	        } else {
	            combinedSolver.add(new JavaParserTypeSolver(javaFile.getParentFile()));
	        }
	    } else {
	        System.err.println("Source path is null. Using file's parent as source root.");
	        combinedSolver.add(new JavaParserTypeSolver(javaFile.getParentFile()));
	    }
	    
	    File projectRoot = findProjectRoot(javaFile);
	    // Add project JARs
	    loadAllJarsInProject(projectRoot, combinedSolver);

	    if(!jarfilesinm2.isEmpty()) {
	    	for(File jar : jarfilesinm2) {
	    		combinedSolver.add(new JarTypeSolver(jar));
	    	}
	    }


	    return combinedSolver;
	}
	
	//finds the root of a project 
	public static File findProjectRoot(File javaFile) {
		File current = javaFile.isDirectory() ? javaFile : javaFile.getParentFile();
	    while (current != null) {
	        if (new File(current, "lib").exists() ||
	            new File(current, "build").exists() ||
	            new File(current, "target").exists() ||
	            new File(current, "pom.xml").exists() ||    // Maven
	            new File(current, "build.gradle").exists())  // Gradle
	        {
	            return current;
	        }
	        current = current.getParentFile();
	    }
	    return javaFile.getParentFile(); // fallback to file's parent
	}
	
	
	//adds project .jar files into typesolver
	public static void loadAllJarsInProject(File projectRoot, CombinedTypeSolver typeSolver) {
        List<File> jarFiles = new ArrayList<>();
        findJarFilesRecursively(projectRoot, jarFiles);

        for (File jar : jarFiles) {
            try {
                typeSolver.add(new JarTypeSolver(jar));
                System.out.println("Loaded JAR: " + jar.getAbsolutePath());
            } catch (Exception e) {
                System.err.println("Failed to load JAR: " + jar.getAbsolutePath());
                e.printStackTrace();
            }
        }

        if (jarFiles.isEmpty()) {
            System.out.println("No .jar files found in project: " + projectRoot.getAbsolutePath());
        }
    }
	
	//finds all .jar files in a folder
    private static void findJarFilesRecursively(File dir, List<File> jarFiles) {
        if (dir == null || !dir.exists()) return;

        File[] files = dir.listFiles();
        if (files == null) return;

        for (File f : files) {
            if (f.isDirectory()) {
                findJarFilesRecursively(f, jarFiles);
            } else if (f.getName().toLowerCase().endsWith(".jar")) {
                jarFiles.add(f);
            }
        }
    }
    
    
    public static HashSet<File> getJarsFromM2 (String fullclasspath){
    	File javaFile = new File(fullclasspath);
    	File projectRoot = findProjectRoot(javaFile);
    	
    	HashSet<File> jarfiles = new HashSet<>(); 
    	
    	 try {
 	    	//builds the project to add all the jar dependencies into the .m2 repository
 	    	String[] command2 = {"cmd.exe", "/c", 
 		    "mvn clean install"};
 	    	ProcessBuilder pb2 = new ProcessBuilder(command2);
 	        pb2.directory(projectRoot);
 	        //pb2.start().waitFor();
 	       pb2.redirectErrorStream(true);

 	      try {
 	          Process process = pb2.start();

 	          // Read combined output (stdout + stderr)
 	          try (BufferedReader reader = new BufferedReader(
 	                  new InputStreamReader(process.getInputStream()))) {
 	              String line;
 	              while ((line = reader.readLine()) != null) {
 	                  System.out.println(line); // or log to a file
 	              }
 	          }

 	          int exitCode = process.waitFor();
 	          if (exitCode != 0) {
 	              System.err.println("❌ Maven build failed with exit code: " + exitCode);
 	          } else {
 	              System.out.println("✅ Maven build completed successfully.");
 	          }

 	      } catch (IOException | InterruptedException e) {
 	          e.printStackTrace();
 	      }

 	        
 	        
 	    	//gets the paths of the jar dependencies used in the project from the .m2 repository
 	    	String[] command1 = {"cmd.exe", "/c", 
 	    		    "mvn dependency:build-classpath -Dmdep.outputFile=cp.txt"};
 	    	ProcessBuilder pb1 = new ProcessBuilder(command1);
 	        pb1.directory(projectRoot);
 	        //pb1.start().waitFor();
 	        
 	       pb1.redirectErrorStream(true);

 	      try {
 	          Process process1 = pb1.start();

 	          // Read the combined output (stdout + stderr)
 	          try (BufferedReader reader = new BufferedReader(
 	                  new InputStreamReader(process1.getInputStream()))) {
 	              String line;
 	              while ((line = reader.readLine()) != null) {
 	                  System.out.println(line); // or save it to a log file if needed
 	              }
 	          }

 	          int exitCode = process1.waitFor();
 	          if (exitCode != 0) {
 	              System.err.println("❌ Failed to build Maven classpath. Exit code: " + exitCode);
 	          } else {
 	              System.out.println("✅ Maven classpath generated successfully.");
 	          }

 	      } catch (IOException | InterruptedException e) {
 	          e.printStackTrace();
 	      }
 	        
 	        
 	        File cpFile = new File(projectRoot, "cp.txt");
 	        if (cpFile.exists()) {
 	            BufferedReader reader = new BufferedReader(new FileReader(cpFile));
 	            String line = reader.readLine(); // Should be only one line
 	            if (line != null) {
 	                StringTokenizer st = new StringTokenizer(line, System.getProperty("path.separator"));
 	                while (st.hasMoreTokens()) {
 	                    String jarPath = st.nextToken();
 	                    File jarFile = new File(jarPath);
 	                    if (jarFile.exists() && jarFile.getName().endsWith(".jar")) {
 	                        jarfiles.add(jarFile);
 	                    }
 	                }
 	            }
 	            reader.close();
 	        }
 	    } catch (Exception e) {
 	        System.err.println("Failed to load Maven classpath JARs: " + e.getMessage());
 	        e.printStackTrace();    
 	    }
    	 
    	 return jarfiles;
    }
	
	
	
	
	
}
