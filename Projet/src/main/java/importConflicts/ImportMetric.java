package importConflicts;

import com.github.javaparser.ast.CompilationUnit;

import java.io.IOException;
import java.util.*;


public class ImportMetric {
	private ArrayList<String> used = new ArrayList<>();
	private ArrayList<String> unused = new ArrayList<>();
	private ArrayList<String> notclassified = new ArrayList<>();
	
	private int totalImports,usedImports,unusedImports,dublicateimports,importConflicts,wildCardImports,unjudgedImports;
	
	public ImportMetric(CompilationUnit cu, String classpath) throws IOException {
		totalImports = countImports(cu);
		filterImports(cu,classpath);
		usedImports = countUsedImports(cu);
		unusedImports = countUnusedImports(cu);
		dublicateimports = countDuplicateImports(cu);
		importConflicts = countImportConflicts(cu,classpath);
		wildCardImports = countWildCardImports(cu);
		unjudgedImports = countUnjudgedImports();
	}
	

	public int getTotalImports() {
	    return totalImports;
	}

	public int getUsedImports() {
	    return usedImports;
	}

	public int getUnusedImports() {
	    return unusedImports;
	}

	public int getDublicateimports() {
	    return dublicateimports;
	}

	public int getImportConflicts() {
	    return importConflicts;
	}

	public int getWildCardImports() {
	    return wildCardImports;
	}

	public int getUnjudgedImports() {
	    return unjudgedImports;
	}

	
	public ArrayList<String> getUsed() {
		return used;
	}
	
	public ArrayList<String> getUnused(){
		return unused;
	}
	
	public ArrayList<String> getNotClassified(){
		return notclassified;
	}
	
	public int countImports(CompilationUnit cu) {
		return ImportsResolver.getImports(cu).size();
	}
	
	public void filterImports(CompilationUnit cu,String Path) throws IOException {
		String name;
		for(String imp: ImportsResolver.getSpecificImports(cu)) {
			name = ImportUtil.getImportedClassName(imp);
			if(!ImportUtil.getImportConflictsSpecific(cu).contains(name)) { //we can judge if an import is used or not only if it's not involved in a conflict
				if(ImportUtil.isFound(cu,name) ) 
					used.add(ImportUtil.getImportedClassName(imp));
				else 
					unused.add(ImportUtil.getImportedClassName(imp));
			}else { 
				notclassified.add(ImportUtil.getImportedClassName(imp));
			}
			
		}
		
		for(String imp: ImportsResolver.getWildCardImports(cu)) {
			if(ImportUtil.isUsedWildCard(cu, imp,Path))//the logic of is used deals with conflicts
				used.add(imp);
			else if(ImportUtil.IsUnusedWildCard(cu, imp,Path))
				unused.add(imp);
			else 
				notclassified.add(ImportUtil.getImportedClassName(imp));
		}
	}
	
	public int countUsedImports (CompilationUnit cu){
		return used.size();	
	}	
	
	public int countUnusedImports (CompilationUnit cu){
		return unused.size();	
	}	
	
	//so far the duplicates are the ones repeated multiple times 
	public int countDuplicateImports(CompilationUnit cu) {	
		return ImportUtil.getDuplicateImports(cu).size();
	}
	
	//if a class is imported from diffrent packages with the same name it is count as one conflict
	public int countImportConflicts(CompilationUnit cu,String classPath) throws IOException {	
		if(ImportUtil.getAllAccessibleClassesSpecific(cu).containsAll(ImportUtil.getImportConflictsWildCard(cu,classPath)))
			return ImportUtil.getImportConflictsSpecific(cu).size();
		else
			return ImportUtil.getImportConflictsSpecific(cu).size() + ImportUtil.getImportConflictsWildCard(cu,classPath).size();
	}
	
	//seperate from the rest of the imports (it is included in total imports)
	public int countWildCardImports(CompilationUnit cu) {
		return ImportsResolver.getWildCardImports(cu).size();
	}
	
	
	public int countUnjudgedImports () {
		return notclassified.size();
	}
	
	
	
	
}



