package javalyzerx;

import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public class AbstractAndSubclassCounter {
	
	
	public static int countNestedClasses(CompilationUnit cu){
		int innerClassCount = 0;

	    List<ClassOrInterfaceDeclaration> classes = cu.findAll(ClassOrInterfaceDeclaration.class);
	    for(ClassOrInterfaceDeclaration cls : classes) {
	        // Check if it's NOT a top-level class (i.e., it's declared inside another type)
	    	if (cls.getParentNode().isPresent() && cls.getParentNode().get() instanceof ClassOrInterfaceDeclaration) {
	    		innerClassCount++;
    		}
	    }

	    return innerClassCount;
	}
	
}
