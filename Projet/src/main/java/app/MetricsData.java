package app;

import java.io.Serializable;

public class MetricsData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String className;
    
    private int jdkExceptions; 
    private int customExceptions ;
	private int jdkcheckedexceptions ;
	private int jdkruntimexceptions ;
	private int jdkerrors ;
	private int total;
	
	private int totalImports;
	private int usedImports;
	private int unusedImports;
	private int dublicateimports;
	private int importConflicts;
	private int wildCardImports;
	private int unjudgedImports;
	
	private  int NOC;
	
	private int nestedClasses;
	private int totalLines;
	private int linesWithComments;
	private int ImplementedInterfaces;
	private int abstractMethods;
	private int abstractClasses;
	private int classDeclaration;
	private int interfaceDeclaration;
	
    private int totalenc;
    private int pub;
    private int pri;
    private int pro;
    private int def;
    private double lcom5;
  
    
    private int CBO;
    
    private int DIT;
	private int totalmethods;
	private int explictmethods;
	private int ormethods;
	private int olmethods;
	private float orratio;
	private float olration;
	private int orwithnoano;
	private int mtconstmethods;
	private int excesiveol;
	private int ambgmethods;
	private float olandor;
	private float orwithsuper;
	
	
	
	
	
    
    
    
    
	
	//exeption
	public int getJdkExceptions() {
	    return jdkExceptions;
	}

	public int getCustomExceptions() {
	    return customExceptions;
	}

	public  int getJdkCheckedExceptions() {
	    return jdkcheckedexceptions;
	}

	public int getJdkRuntimeExceptions() {
	    return jdkruntimexceptions;
	}

	public int getJdkErrors() {
	    return jdkerrors;
	}

	public int getTotal() {
	    return total;
	}
	
	
	//imports
	
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
    
	
	//noc 
	public int getNOC() {
    	return NOC;
    }
	
	
	//javalyzex
	
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
	
	//encapsulation
	public int getTotalEnc() {
    	return totalenc;
    }
    
    public int getPublic() {
    	return pub;
    }
    public int getPrivate() {
    	return pri;
    }
    public int getProtected() {
    	return pro;
    }
    public int getDefault() {
    	return def;
    }
    public double getLCOM5() {
    	return Math.round(lcom5 * 100d) / 100d;
    }
    
    
    
    public int getCBO() {
		return CBO;
	}
    
    
    //lamia
    
    public int getDIT() {
        return DIT;
    }
	
	public int getTotalMethods() {
		return totalmethods;
	}

   

    public int getExplictmethods() {
        return explictmethods;
    }

    

    public int getOrmethods() {
        return ormethods;
    }

   
    public int getOlmethods() {
        return olmethods;
    }

    

    public float getOrratio() {
        return Math.round(orratio * 100f) / 100f;
    }


    public float getOlration() {
        return Math.round(olration * 100f) / 100f;
    }


    public int getOrwithnoano() {
        return orwithnoano;
    }

    

    public int getMtconstmethods() {
        return mtconstmethods;
    }

    

    public int getExcesiveol() {
        return excesiveol;
    }

    

    public int getAmbgmethods() {
        return ambgmethods;
    }

  

    public float getOlandor() {
        return Math.round(olandor * 100f) / 100f;
    }

   

    public float getOrwithsuper() {
        return Math.round(orwithsuper * 100f) / 100f;
    }
    
    
    
    
    //setters
    public void setJdkExceptions(int jdkExceptions) {
        this.jdkExceptions = jdkExceptions;
    }

    public void setCustomExceptions(int customExceptions) {
        this.customExceptions = customExceptions;
    }

    public void setJdkCheckedExceptions(int jdkcheckedexceptions) {
        this.jdkcheckedexceptions = jdkcheckedexceptions;
    }

    public void setJdkRuntimeExceptions(int jdkruntimexceptions) {
        this.jdkruntimexceptions = jdkruntimexceptions;
    }

    public void setJdkErrors(int jdkerrors) {
        this.jdkerrors = jdkerrors;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    // imports

    public void setTotalImports(int totalImports) {
        this.totalImports = totalImports;
    }

    public void setUsedImports(int usedImports) {
        this.usedImports = usedImports;
    }

    public void setUnusedImports(int unusedImports) {
        this.unusedImports = unusedImports;
    }

    public void setDublicateimports(int dublicateimports) {
        this.dublicateimports = dublicateimports;
    }

    public void setImportConflicts(int importConflicts) {
        this.importConflicts = importConflicts;
    }

    public void setWildCardImports(int wildCardImports) {
        this.wildCardImports = wildCardImports;
    }

    public void setUnjudgedImports(int unjudgedImports) {
        this.unjudgedImports = unjudgedImports;
    }

    // noc

    public void setNOC(int NOC) {
        this.NOC = NOC;
    }

    // javalyzex

    public void setNestedClasses(int nestedClasses) {
        this.nestedClasses = nestedClasses;
    }

    public void setTotalLines(int totalLines) {
        this.totalLines = totalLines;
    }

    public void setLinesWithComments(int linesWithComments) {
        this.linesWithComments = linesWithComments;
    }

    public void setImplementedInterfaces(int ImplementedInterfaces) {
        this.ImplementedInterfaces = ImplementedInterfaces;
    }

    public void setAbstractMethods(int abstractMethods) {
        this.abstractMethods = abstractMethods;
    }

    public void setAbstractClasses(int abstractClasses) {
        this.abstractClasses = abstractClasses;
    }

    public void setClassDeclaration(int classDeclaration) {
        this.classDeclaration = classDeclaration;
    }

    public void setInterfaceDeclaration(int interfaceDeclaration) {
        this.interfaceDeclaration = interfaceDeclaration;
    }

    // encapsulation

    public void setTotalEnc(int totalenc) {
        this.totalenc = totalenc;
    }

    public void setPublic(int pub) {
        this.pub = pub;
    }

    public void setPrivate(int pri) {
        this.pri = pri;
    }

    public void setProtected(int pro) {
        this.pro = pro;
    }

    public void setDefault(int def) {
        this.def = def;
    }

    public void setLCOM5(double lcom5) {
        this.lcom5 = lcom5;
    }

    public void setCBO(int CBO) {
        this.CBO = CBO;
    }

    
    //OOMR
    public void setDIT(int DIT) {
        this.DIT = DIT;
    }

    public void setTotalMethods(int totalmethods) {
        this.totalmethods = totalmethods;
    }

    public void setExplictmethods(int explictmethods) {
        this.explictmethods = explictmethods;
    }

    public void setOrmethods(int ormethods) {
        this.ormethods = ormethods;
    }

    public void setOlmethods(int olmethods) {
        this.olmethods = olmethods;
    }

    public void setOrratio(float orratio) {
        this.orratio = orratio;
    }

    public void setOlration(float olration) {
        this.olration = olration;
    }

    public void setOrwithnoano(int orwithnoano) {
        this.orwithnoano = orwithnoano;
    }

    public void setMtconstmethods(int mtconstmethods) {
        this.mtconstmethods = mtconstmethods;
    }

    public void setExcesiveol(int excesiveol) {
        this.excesiveol = excesiveol;
    }

    public void setAmbgmethods(int ambgmethods) {
        this.ambgmethods = ambgmethods;
    }

    public void setOlandor(float olandor) {
        this.olandor = olandor;
    }

    public void setOrwithsuper(float orwithsuper) {
        this.orwithsuper = orwithsuper;
    }

	
	
	
    
    // Getters and setters
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    

}
