package importConflicts;


import java.util.HashSet;
import java.util.List;



import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;


public class TypeUsageAnalyser {
	//gets the type of all  attributes of a class
	public static HashSet<String> getAttributesType(CompilationUnit cu) {
		List<FieldDeclaration> attrbs = cu.findAll(FieldDeclaration.class);
	    HashSet<String> attributes = new HashSet<>();

	    for (FieldDeclaration attrb : attrbs) {
	        if (attrb.getElementType().isClassOrInterfaceType()) {
	            ClassOrInterfaceType type = attrb.getElementType().asClassOrInterfaceType();

	            if (type.getScope().isPresent()) {
	                // Fully qualified: e.g., java.util.Date
	                attributes.add(type.getScope().get().toString() + "." + type.getNameAsString());
	            } else {
	                // Simple name: e.g., Date
	                attributes.add(type.getNameAsString());
	            }
	        } else {
	            attributes.add(attrb.getElementType().asString());
	        }
	    }

	    return attributes;
	}
	

	
	
	
		
	//gets types of all methods
	public static HashSet<String> getMethodsType(CompilationUnit cu) {
	    List<MethodDeclaration> methods = cu.findAll(MethodDeclaration.class);
	    HashSet<String> types = new HashSet<>();

	    for (MethodDeclaration meth : methods) {
	        if (meth.getType().isClassOrInterfaceType()) {
	            ClassOrInterfaceType type = meth.getType().asClassOrInterfaceType();

	            if (type.getScope().isPresent()) {
	                // Fully qualified: java.util.List
	                types.add(type.getScope().get().toString() + "." + type.getNameAsString());
	            } else {
	                // Simple class/interface name like List or Date
	                types.add(type.getNameAsString());
	            }
	        } else {
	            // Primitive types, void, or other types (like arrays)
	            types.add(meth.getType().asString());
	        }
	    }

	    return types;
	}

		
	//gets type of all the parameters of all methods
	public static HashSet<String> getParametersType(CompilationUnit cu) {
	    List<MethodDeclaration> methods = cu.findAll(MethodDeclaration.class);
	    HashSet<String> parameterstype = new HashSet<>();

	    for (MethodDeclaration meth : methods) {
	        for (Parameter para : meth.getParameters()) {
	            if (para.getType().isClassOrInterfaceType()) {
	                ClassOrInterfaceType type = para.getType().asClassOrInterfaceType();

	                if (type.getScope().isPresent()) {
	                    // Fully qualified: e.g., java.util.List
	                    parameterstype.add(type.getScope().get().toString() + "." + type.getNameAsString());
	                } else {
	                    // Simple name: e.g., List
	                    parameterstype.add(type.getNameAsString());
	                }
	            } else {
	                // Primitive, array, or other types
	                parameterstype.add(para.getType().asString());
	            }
	        }
	    }

	    return parameterstype;
	}

	
	public static HashSet<String> getLocalVarsType(CompilationUnit cu) {
	    List<MethodDeclaration> methods = cu.findAll(MethodDeclaration.class);
	    HashSet<String> localvartypes = new HashSet<>();

	    for (MethodDeclaration meth : methods) {
	        for (VariableDeclarator var : meth.findAll(VariableDeclarator.class)) {
	            if (var.getType().isClassOrInterfaceType()) {
	                ClassOrInterfaceType type = var.getType().asClassOrInterfaceType();

	                if (type.getScope().isPresent()) {
	                    // Fully qualified class name (e.g., java.util.Date)
	                    localvartypes.add(type.getScope().get().toString() + "." + type.getNameAsString());
	                } else {
	                    // Simple class name (e.g., Date)
	                    localvartypes.add(type.getNameAsString());
	                }
	            } else {
	                // Primitive, array, etc.
	                localvartypes.add(var.getType().asString());
	            }
	        }
	    }

	    return localvartypes;
	}

		
	//gets all the types of the casts
	//it was added in case of using general type "var" as checking  local variables will not be enough
	public static HashSet<String> getCastsType(CompilationUnit cu) {
	    List<CastExpr> casts = cu.findAll(CastExpr.class);
	    HashSet<String> caststype = new HashSet<>();

	    for (CastExpr cast : casts) {
	        if (cast.getType().isClassOrInterfaceType()) {
	            ClassOrInterfaceType type = cast.getType().asClassOrInterfaceType();

	            if (type.getScope().isPresent()) {
	                // Fully qualified name like java.util.Date
	                caststype.add(type.getScope().get().toString() + "." + type.getNameAsString());
	            } else {
	                // Simple name like Date
	                caststype.add(type.getNameAsString());
	            }
	        } else {
	            // Primitive types or others
	            caststype.add(cast.getType().asString());
	        }
	    }

	    return caststype;
	}

	public static HashSet<String> getGenericsType(CompilationUnit cu) {
	    List<ClassOrInterfaceType> generics = cu.findAll(ClassOrInterfaceType.class);
	    HashSet<String> genericstype = new HashSet<>();

	    for (ClassOrInterfaceType gene : generics) {
	        if (gene.getScope().isPresent()) {
	            // Fully qualified generic type (e.g., java.util.List)
	            genericstype.add(gene.getScope().get().toString() + "." + gene.getNameAsString());
	        } else {
	            // Simple name (e.g., List)
	            genericstype.add(gene.getNameAsString());
	        }
	    }

	    return genericstype;
	}

	
	public static HashSet<String> getInheiritedAndEmplementedClassesType(CompilationUnit cu) {
	    List<ClassOrInterfaceDeclaration> classes = cu.findAll(ClassOrInterfaceDeclaration.class);
	    HashSet<String> inherited = new HashSet<>();

	    for (ClassOrInterfaceDeclaration cls : classes) {
	        for (ClassOrInterfaceType ext : cls.getExtendedTypes()) {
	            if (ext.getScope().isPresent()) {
	                inherited.add(ext.getScope().get().toString() + "." + ext.getNameAsString());
	            } else {
	                inherited.add(ext.getNameAsString());
	            }
	        }
	        for (ClassOrInterfaceType impl : cls.getImplementedTypes()) {
	            if (impl.getScope().isPresent()) {
	                inherited.add(impl.getScope().get().toString() + "." + impl.getNameAsString());
	            } else {
	                inherited.add(impl.getNameAsString());
	            }
	        }
	    }

	    return inherited;
	}

	
	public static HashSet<String> getThrowsType(CompilationUnit cu) {
	    List<MethodDeclaration> methods = cu.findAll(MethodDeclaration.class);
	    HashSet<String> throwntypes = new HashSet<>();

	    for (MethodDeclaration method : methods) {
	        for (ReferenceType thrown : method.getThrownExceptions()) {
	            if (thrown.isClassOrInterfaceType()) {
	                ClassOrInterfaceType type = thrown.asClassOrInterfaceType();

	                if (type.getScope().isPresent()) {
	                    throwntypes.add(type.getScope().get().toString() + "." + type.getNameAsString());
	                } else {
	                    throwntypes.add(type.getNameAsString());
	                }
	            } else {
	                throwntypes.add(thrown.asString()); // fallback (e.g., arrays or generic types)
	            }
	        }
	    }

	    return throwntypes;
	}

	
	public static HashSet<String> getObjectsType(CompilationUnit cu) {
	    List<ObjectCreationExpr> objects = cu.findAll(ObjectCreationExpr.class);
	    HashSet<String> objectTypes = new HashSet<>();

	    for (ObjectCreationExpr obj : objects) {
	        ClassOrInterfaceType type = obj.getType();

	        if (type.getScope().isPresent()) {
	            // e.g., java.util.Date → scope: java.util, name: Date
	            objectTypes.add(type.getScope().get().toString() + "." + type.getNameAsString());
	        } else {
	            // Just a simple type like "Date"
	            objectTypes.add(type.getNameAsString());
	        }
	    }

	    return objectTypes;
	}

		
	
		
		public static HashSet<String> getStaticClassesNames(CompilationUnit cu) {
		    HashSet<String> result = new HashSet<>();
		    List<MethodCallExpr> methodCalls = cu.findAll(MethodCallExpr.class);
		    

		    // Collect parameter and variable names to exclude
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

		                // Exclude variables, keep likely class references
		                if (!localNames.contains(name)) {
		                    result.add(name);
		                }
		            }
		        }
		    }
		    
		    List<FieldAccessExpr> fields = cu.findAll(FieldAccessExpr.class);
		    
		    for (FieldAccessExpr field: fields) {
		        if (field.getScope().isNameExpr()) {
		            Expression scope = field.getScope();

		            // Check if it's a simple name (like str, Math, etc.)
		            if (scope.isNameExpr()) {
		                String name = scope.asNameExpr().getNameAsString();

		                // Exclude variables, keep likely class references
		                if (!localNames.contains(name)) {
		                    result.add(name);
		                }
		            }
		        }
		    }
		    

		    return result;
		}
		
		
		
}
