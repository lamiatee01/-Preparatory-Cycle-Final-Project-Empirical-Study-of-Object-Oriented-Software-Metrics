package importConflicts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;

public class ImportsResolver {
	//returns all imports as string
	public static ArrayList<String> getImports(CompilationUnit cu) {
		
		List<ImportDeclaration> imp = cu.findAll(ImportDeclaration.class);
		ArrayList <String> imports = new ArrayList<>();
		for(ImportDeclaration mp : imp) {
			imports.add(mp.getNameAsString());
		}
		
		return imports;
	}
		
	//return a hashset of specific imports (no dups)
	public static HashSet<String> getSpecificImports(CompilationUnit cu) {
		//previous getImports was not used because an import declaration is needed not a string of the import
		List<ImportDeclaration> imp = cu.findAll(ImportDeclaration.class);
		HashSet <String> imports = new HashSet<>();
		for(ImportDeclaration mp : imp) {
			if(!mp.isAsterisk())
				imports.add(mp.getNameAsString());
		}
		
		return imports;
	}
		
	//return a hashset of wild card imports (no dups)
	public static HashSet<String> getWildCardImports(CompilationUnit cu) {
		
		List<ImportDeclaration> imp = cu.findAll(ImportDeclaration.class);
		HashSet <String> imports = new HashSet<>();
		for(ImportDeclaration mp : imp) {
			if(mp.isAsterisk())
				imports.add(mp.getNameAsString());
		}
		
		return imports;
	}
}
