package javalyzerx;


import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.util.List;

public class MethodAndInterfaceCounter {
	
	public int interfcaeCounter(CompilationUnit cu) {
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


}

