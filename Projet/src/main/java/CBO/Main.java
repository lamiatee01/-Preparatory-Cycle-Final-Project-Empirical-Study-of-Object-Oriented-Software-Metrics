package CBO;

import java.io.File;
import java.io.IOException;


import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;


import importConflicts.ImportUtil;

public class Main {

	public static void main(String[] args) throws IOException {
	
		String path ="C:\\Users\\aicha\\OneDrive - etu.usthb.dz\\Desktop\\test\\ImportsResolver.java";
  
    	File file = new File(path);
    	CombinedTypeSolver combinedSolver = new CombinedTypeSolver();


        combinedSolver.add(new ReflectionTypeSolver());

        ParserConfiguration config = new ParserConfiguration()
                .setSymbolResolver(new JavaSymbolSolver(combinedSolver));
        JavaParser parser = new JavaParser(config);

        CompilationUnit cu = parser.parse(file).getResult().orElseThrow();

        String sourcePath = ImportUtil.getSourcePath(path, cu);
        if (sourcePath != null) {
            combinedSolver.add(new JavaParserTypeSolver(new File(sourcePath)));  // Add your source folder
        } else {
            System.err.println("Source path is null. Unable to add source files.");
        }

        File dependencyDir = new File("target/dependency");
        if (dependencyDir.exists()) {
            for (File jar : dependencyDir.listFiles((dir, name) -> name.endsWith(".jar"))) {
                try {
                    combinedSolver.add(new JarTypeSolver(jar));
                } catch (IOException e) {
                    System.err.println("Failed to load JAR: " + jar.getName());
                    e.printStackTrace();
                }
            }
        }
    	
        
        
        CBOMetric cbo = new CBOMetric(cu,combinedSolver);
        
        
        
		System.out.println(cbo.getCBO());
		
		
    
    
        
        
      
        
  
       
        
        

		
	}
}
