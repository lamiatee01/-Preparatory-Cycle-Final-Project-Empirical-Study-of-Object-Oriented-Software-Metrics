package importConflicts;

import java.io.File;

import java.io.IOException;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;



public class Main {
    public static void main(String[] args) throws IOException {
    	
    	String path ="your path";
  
    	File file = new File(path);
		CompilationUnit cu = StaticJavaParser.parse(file);
		
		ImportMetric imp = new ImportMetric(cu,path);
		

		System.out.println("Total Imports: " + imp.getTotalImports());
		System.out.println("Imports conflicts: " + imp.getImportConflicts());
		System.out.println("Used Imports: " + imp.getUsed());
		System.out.println("Unused Imports: " + imp.getUnused());
		System.out.println("Dublicate Imports: " + imp.countDuplicateImports(cu));
		System.out.println("Wild card imports: " + imp.countWildCardImports(cu));
		System.out.println("Cannot be classified: "+imp.getUnjudgedImports());
		
		
		
		
		
		
		       
		    
		
		 
		
    }
}
