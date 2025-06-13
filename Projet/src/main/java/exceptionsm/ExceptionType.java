package exceptionsm;


import java.io.IOException;


public class ExceptionType {
	
	private int jdkExceptions = 0, customExceptions = 0;
	private int jdkcheckedexceptions = 0;
	private int jdkruntimexceptions = 0;
	private int jdkerrors = 0;
	private int total = 0 ;
	
	public int getJdkExceptions() {
	    return jdkExceptions;
	}

	public int getCustomExceptions() {
	    return customExceptions;
	}

	public int getJdkCheckedExceptions() {
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
	
	
	public void setJdkExceptions(int value) {
	    jdkExceptions = value;
	}

	public void setCustomExceptions(int value) {
	    customExceptions = value;
	}

	public void setJdkCheckedExceptions(int value) {
	    jdkcheckedexceptions = value;
	}

	public void setJdkRuntimeExceptions(int value) {
	    jdkruntimexceptions = value;
	}

	public void setJdkErrors(int value) {
	    jdkerrors = value;
	}

	public void setTotal(int value) {
	    total = value;
	}


	
	
	public ExceptionType(String path) throws Exception,IOException {
		Exceptionscounter x = new Exceptionscounter(path);
		total = Exceptionscounter.total;
		for(String exceptionname : x.mergeExceptionSets()) {
			if(ListJDKClasses.jdkclasses1().contains(exceptionname)) {
				jdkExceptions++;
				Class<?> clazz = Class.forName(exceptionname);
				if (Error.class.isAssignableFrom(clazz)) {
                    jdkerrors++;
                } else if (RuntimeException.class.isAssignableFrom(clazz)) {
                    jdkruntimexceptions++;
                } else if (Exception.class.isAssignableFrom(clazz)) {
                    jdkcheckedexceptions++;
                }
			}else if(ListJDKClasses.jdkclasses2().contains(exceptionname)) {
				jdkExceptions++;
				Class<?> clazz = Class.forName(exceptionname);
				if (Error.class.isAssignableFrom(clazz)) {
                    jdkerrors++;
                } else if (RuntimeException.class.isAssignableFrom(clazz)) {
                    jdkruntimexceptions++;
                } else if (Exception.class.isAssignableFrom(clazz)) {
                    jdkcheckedexceptions++;
                }
			}else {
				customExceptions++;
			}
			x.setTotal(0);
			
		
	}}
	
	public static void classifyExceptions() {
		
	}
	
	public static void main(String[] args)throws Exception,IOException  {
		ExceptionType ex = new ExceptionType("C:\\Users\\Lilia\\Desktop\\TP_POO_s4\\tp4\\src\\tp4\\Octet.java");

		System.out.println("jdk :: "+ex.jdkExceptions);
		System.out.println("custom :: "+ex.customExceptions);
		System.out.println("jdkruntime :: "+ex.jdkruntimexceptions);
		System.out.println("jdkchecked :: "+ex.jdkcheckedexceptions);
		System.out.println("jdkerrors :: "+ex.jdkerrors);
		System.out.println("total :: "+ex.total);
			
	}
	
}

