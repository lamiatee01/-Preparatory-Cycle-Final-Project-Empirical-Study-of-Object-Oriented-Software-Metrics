package CBO;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.resolution.TypeSolver;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;

public class CBOMetric {
	
	private int CBO;
	
	public CBOMetric(CompilationUnit cu, TypeSolver typeSolver) throws IOException {
		CBO = countCBO(cu,typeSolver);
	}
	
	public int getCBO() {
		return CBO;
	}
	
	
	
	
	private static HashSet<String> getStaticClassesNames(CompilationUnit cu) {
	    HashSet<String> result = new HashSet<>();
	    List<MethodCallExpr> methodCalls = cu.findAll(MethodCallExpr.class);

	    // gets the name of local variables and parameters to exclude them from the result 
	    HashSet<String> localNames = new HashSet<>();

	    List<Parameter> parameters = cu.findAll(Parameter.class);
	    
	    for (Parameter para:parameters) {
	        localNames.add(para.getNameAsString());
	    }

	    List<VariableDeclarator> variables = cu.findAll(VariableDeclarator.class);
	    for (VariableDeclarator var: variables) {
	        localNames.add(var.getNameAsString());
	    }

	    for (MethodCallExpr call: methodCalls) {
	        if (call.getScope().isPresent()) {
	            Expression scope = call.getScope().get();

	            // Check if it's a simple name (like str, Math, etc.)
	            if (scope.isNameExpr()) {
	                String name = scope.asNameExpr().getNameAsString();
	                if (!localNames.contains(name)) {
	                	try {
	                        ResolvedType resolvedType = scope.asNameExpr().calculateResolvedType();
	                        if (resolvedType.isReferenceType()) 
	                            result.add(resolvedType.asReferenceType().getQualifiedName());
	                        else 
	                        	result.add(name);
	                    } catch (Exception e) {
	                    	result.add(name);
	                    }
	                } 	
	            }
	        }
	    }
	    
	    
	    List<FieldAccessExpr> fields = cu.findAll(FieldAccessExpr.class);
	    for(FieldAccessExpr field : fields) {
	    	Expression scope = field.getScope();
	        if (scope.isNameExpr()) {
	            String name = scope.asNameExpr().getNameAsString();
	            if (!localNames.contains(name)) {
	                try {
	                    ResolvedType resolvedType = scope.asNameExpr().calculateResolvedType();
	                    if (resolvedType.isReferenceType()) 
	                        result.add(resolvedType.asReferenceType().getQualifiedName());
	                    else
	                    	result.add(field.getNameAsString());
	                } catch (Exception e) {
	                	result.add(field.getNameAsString());
	                }
	            }
	        }
	    }

	    return result;
	}
	
	
	private static  HashSet<String> getMostTypes(CompilationUnit cu) {
	    HashSet<String> cls = new HashSet<>();

	    List<ClassOrInterfaceType> allTypes = cu.findAll(ClassOrInterfaceType.class);
	    List<ClassOrInterfaceType> clas = new ArrayList<>();

	    for (ClassOrInterfaceType t : allTypes) {
	        Node parent = t.getParentNode().orElse(null);
	        if (parent == null || !(parent instanceof ClassOrInterfaceType)) {
	            clas.add(t);
	        }
	    }
	    

	    for (ClassOrInterfaceType c : clas) {
	       try {
	            ResolvedType resolvedType = c.resolve();
	            if (resolvedType.isReferenceType()) 
	                cls.add(resolvedType.asReferenceType().getQualifiedName());
	            else 
	                cls.add(c.getNameAsString());
	            
	        } catch (Exception e) {
	            cls.add(c.getNameAsString());
	        }
	    }
	    

	    return cls;
	}
	
	private static HashSet<String> getObjectsType(CompilationUnit cu,TypeSolver typeSolver) {
	    List<ObjectCreationExpr> objects = cu.findAll(ObjectCreationExpr.class);
	    HashSet<String> objectTypes = new HashSet<>();
	    
	    for (ObjectCreationExpr obj : objects) {
	        try {
	        	ResolvedType type = JavaParserFacade.get(typeSolver).getType(obj);

	        	if (type.isReferenceType()) 
	        	    objectTypes.add(type.asReferenceType().getQualifiedName());
	        	else 
	        		objectTypes.add(obj.getTypeAsString());

	        } catch (Exception e) {
	        	objectTypes.add(obj.getTypeAsString());
	        }
	    	
	    	
	    }
	    	
	    return objectTypes;
	}
	
	
	private static HashSet<String> getGenericsType (CompilationUnit cu){
		HashSet<String> generics = new HashSet<>();

        List<ClassOrInterfaceType> types = cu.findAll(ClassOrInterfaceType.class);

        for (ClassOrInterfaceType type : types) {
            NodeList<Type> typeArgs = type.getTypeArguments().orElse(null);
            if (typeArgs != null) {
                for (Type arg: typeArgs) {
                	try {
         	            ResolvedType resolvedType = arg.resolve();
         	            if (resolvedType.isReferenceType()) 
         	                generics.add(resolvedType.asReferenceType().getQualifiedName());
         	            else 
         	                generics.add(arg.asString());
         	            
         	        } catch (Exception e) {
         	            generics.add(arg.asString());
         	        }
                }
            }
        }
		
		return generics;
	}
	
	
	//try to write this in a simpler way
	private  static HashSet<String> getAllJDKClasses() throws IOException {
	    String javaHome = System.getProperty("java.home");
	    Path srcZipPath = Paths.get(javaHome, "lib", "src.zip");

	    HashSet<String> allClasses = new HashSet<>();

	    try (FileSystem fs = FileSystems.newFileSystem(srcZipPath, null)) {
	        for (Path root : fs.getRootDirectories()) {
	            Files.walk(root)
	               .filter(p -> p.toString().endsWith(".java"))
	                .forEach(path -> {
	                     allClasses.add(getPackageName(path.toString()));      
	                    
	                });
	        }
	    }

	    return allClasses;
	}
	
	private static String getPackageName(String s) {
		String[] parts = s.split("/");
		String res="";
		for (int i=2; i<parts.length; i++) {
			res += parts[i];
			if(i<= parts.length-2)
			res+= ".";
		}
		res = res.substring(0, res.length() - ".java".length());
		
		return res;
	}
	
	
	private static  HashSet<String> calculateCBO(CompilationUnit cu,TypeSolver typeSolver) throws IOException {
		HashSet<String> CBO = new HashSet<>();
		for(String str : getGenericsType(cu)) {
			if(!getAllJDKClasses().contains(str))
				CBO.add(str);
		}
		
		for(String str : getStaticClassesNames(cu)) {
			if(!getAllJDKClasses().contains(str))
				CBO.add(str);
		}
		
		for(String str : getMostTypes(cu)) {
			if(!getAllJDKClasses().contains(str))
				CBO.add(str);
		}
		
		for(String str : getObjectsType(cu,typeSolver)) {
			if(!getAllJDKClasses().contains(str))
				CBO.add(str);
		}
		
		return CBO;
		
			
	}
	
	private static int countCBO(CompilationUnit cu,TypeSolver typeSolver) throws IOException{
		return calculateCBO(cu, typeSolver).size();
	}
	
}
