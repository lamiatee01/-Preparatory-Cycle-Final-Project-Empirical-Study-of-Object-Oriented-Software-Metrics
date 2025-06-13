package exceptionsm;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import importConflicts.ImportUtil;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
//------------------------this class gives us the final result -------------------
public class ExceptionsMetric {

    private int jdkExceptions = 0;
    private int customExceptions = 0;
    private int total;
    
    private int jdkcheckedexceptions = 0;
	private int jdkruntimexceptions = 0;
	private int jdkerrors = 0;

    private File baseClassFile;
    private CompilationUnit baseCU;
    private String sourcepath;

    public List<String> list;
    

    public ExceptionsMetric(String path) throws Exception {
        this.baseClassFile = new File(path);

        // Initialize baseCU (parse the class file)
        this.baseCU = StaticJavaParser.parse(new FileInputStream(baseClassFile));

        // Determine the source root path based on the package
        this.sourcepath = ImportUtil.getSourcePath(path,baseCU);

        // Load all custom class names from source path
        List<String> allClasses = JavaClassFinder.findFullyQualifiedClassNames(new File(sourcepath));

        // Get all exception class names used in the file
        Exceptionscounter counter = new Exceptionscounter(path);
        
        
        this.list = counter.mergeExceptionSets();
        total = this.list.size();
        
        System.out.println("Exceptions found in file:");
        for (String exceptionFQN : list) {
            System.out.println(" - " + exceptionFQN);
        }

        // Classify each exception
        for (String exceptionFQN : list) {
            if (allClasses.contains(exceptionFQN)) {
                customExceptions++;
                
                
            } else {
                jdkExceptions++;
                Class<?> clazz = Class.forName(exceptionFQN);
				if (Error.class.isAssignableFrom(clazz)) {
                    jdkerrors++;
                } else if (RuntimeException.class.isAssignableFrom(clazz)) {
                    jdkruntimexceptions++;
                }else if (Exception.class.isAssignableFrom(clazz)) {
                    jdkcheckedexceptions++;
                }
            }
        }
    }
    
    public int getJdkExceptions() {
        return jdkExceptions;
    }

    public int getCustomExceptions() {
        return customExceptions;
    }

    public int getTotal() {
        return total;
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



    public static void main(String[] args) {
        try {
            ExceptionsMetric app = new ExceptionsMetric("C:\\Users\\Lilia\\Desktop\\TP_POO_s4\\tp4\\src\\tp4\\Octet.java");
            System.out.println("JDK Exceptions: " + app.jdkExceptions);
            System.out.println("Custom Exceptions: " + app.customExceptions);
            System.out.println("jdkruntime :: "+app.jdkruntimexceptions);
    		System.out.println("jdkchecked :: "+app.jdkcheckedexceptions);
    		System.out.println("jdkerrors :: "+app.jdkerrors);
    		System.out.println("total :: "+app.total);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}







