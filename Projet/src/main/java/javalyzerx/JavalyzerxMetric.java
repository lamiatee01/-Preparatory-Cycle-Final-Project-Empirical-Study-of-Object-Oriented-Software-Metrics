package javalyzerx;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public class JavalyzerxMetric {
	private int nestedClasses,totalLines,linesWithComments,ImplementedInterfaces,abstractMethods,abstractClasses,classDeclaration,interfaceDeclaration;
	
	public JavalyzerxMetric(CompilationUnit cu,String classpath) throws IOException {
		nestedClasses = countNestedClasses(cu);
		totalLines = countNonEmptyLines(classpath);
		linesWithComments = countComments(cu);
		ImplementedInterfaces = countImplmentedIterfaces(cu);
		abstractMethods = countAbstractMethods(cu);
		abstractClasses = countAbstractClasses(cu);
		classDeclaration = countClassDeclarations(cu);
		interfaceDeclaration = countIntefaceDeclarations(cu);
		
	}
	
	
	
	public int getNestedClasses() {
	    return nestedClasses;
	}

	public int getTotalLines() {
	    return totalLines;
	}

	public int getLinesWithComments() {
	    return linesWithComments;
	}

	public int getImplementedInterfaces() {
	    return ImplementedInterfaces;
	}

	public int getAbstractMethods() {
	    return abstractMethods;
	}

	public int getAbstractClasses() {
	    return abstractClasses;
	}

	public int getClassDeclaration() {
	    return classDeclaration;
	}

	public int getInterfaceDeclaration() {
	    return interfaceDeclaration;
	}

	
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
	
	
	public static int countTotalLines(String path) throws IOException {      
        Path sourcePath = Paths.get(path);
        long lineCount = Files.lines(sourcePath).count();   
        
        return Math.toIntExact(lineCount);
	}
	
	
	public static int countNonEmptyLines(String classPath) throws IOException {
		int linecount = 0;
		BufferedReader reader = new BufferedReader(new FileReader(classPath));
		String line;
		while ((line = reader.readLine()) != null) {
		    if (!line.trim().isEmpty()) {
		        linecount++;
		    }
		}
		reader.close();
		return linecount;
		
	}
	
	
	
	public static int countComments(CompilationUnit cu) {
		List<Comment> comments = cu.getAllContainedComments();
	    
	    int totalCommentLines = 0;

	    for (Comment comment : comments) {
	        if (comment.getRange().isPresent()) {
	            Range range = comment.getRange().get();
	            int linesSpanned = range.end.line - range.begin.line + 1;
	            totalCommentLines += linesSpanned;
	        }
	    }

	    return totalCommentLines;
	}
	
	
	public int countImplmentedIterfaces(CompilationUnit cu) {
		List<ClassOrInterfaceDeclaration> classOrInterface = cu.findAll(ClassOrInterfaceDeclaration.class);
		int interfacecount = 0;
		for(ClassOrInterfaceDeclaration clazz : classOrInterface) {
            if (!clazz.isInterface()) {
                List<ClassOrInterfaceType> implementedInterfaces = clazz.getImplementedTypes();
                interfacecount += implementedInterfaces.size();
            }
        }
		return interfacecount;
	}
	
	
	public int countAbstractMethods(CompilationUnit cu) {
		List<ClassOrInterfaceDeclaration> classOrInterface = cu.findAll(ClassOrInterfaceDeclaration.class);
		int abstractmethodcounter = 0;
		for(ClassOrInterfaceDeclaration clazz : classOrInterface) {
			List<MethodDeclaration> methods = clazz.getMethods();
            if (!clazz.isInterface()) {
                for(MethodDeclaration method : methods) {
                	if(method.isAbstract()) 
                		abstractmethodcounter += 1 ;	
                }
            }else
            	abstractmethodcounter += methods.size();
            
        }
		return abstractmethodcounter;
	}
	
	
	public int countAbstractClasses(CompilationUnit cu) {
		List<ClassOrInterfaceDeclaration> classOrInterface = cu.findAll(ClassOrInterfaceDeclaration.class);
		int abstractClassCount = 0;
		for(ClassOrInterfaceDeclaration clazz : classOrInterface) {
            if(clazz.isAbstract()) {
            	abstractClassCount += 1;
            }
        }
		return abstractClassCount;
	}
	
	public int countClassDeclarations(CompilationUnit cu) {
		List<ClassOrInterfaceDeclaration> classOrInterface = cu.findAll(ClassOrInterfaceDeclaration.class);
		int classcount = 0;
		for (ClassOrInterfaceDeclaration clazz : classOrInterface) {
			if(!clazz.isInterface())
				classcount += 1;
		}
		return classcount;
	}
	
	
	public int countIntefaceDeclarations(CompilationUnit cu) {
		List<ClassOrInterfaceDeclaration> classOrInterface = cu.findAll(ClassOrInterfaceDeclaration.class);
		int interfaceCount = 0;
		for (ClassOrInterfaceDeclaration clazz : classOrInterface) {
			if(clazz.isInterface())
				interfaceCount += 1;
		}
		return interfaceCount;
	}
	
	
	
	
}
