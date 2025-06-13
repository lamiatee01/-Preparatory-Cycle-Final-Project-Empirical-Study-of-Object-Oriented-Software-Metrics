package exceptionsm;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// --------this class get all the java classes in a folder with their fullname ------------
public class JavaClassFinder {

 

    // Finds all fully qualified class names in a folder
    public static List<String> findFullyQualifiedClassNames(File rootFolder) throws FileNotFoundException {
        List<String> result = new ArrayList<>();
        collectClassNames(rootFolder, rootFolder, result);
        return result;
    }

    // Recursive method to traverse the folder
    public static void collectClassNames(File root, File current, List<String> result) throws FileNotFoundException {
        if (current.isDirectory()) {
            for (File file : current.listFiles()) {
                collectClassNames(root, file, result);
            }
        } else if (current.getName().endsWith(".java")) {
            String packageName = getPackageName(current);
            String className = current.getName().replace(".java", "");
            if (!packageName.isEmpty()) {
                result.add(packageName + "." + className);
            } else {
                result.add(className); // default package
            }
        }
    }

    // Extracts the package name from a Java source file
    public static String getPackageName(File file) throws FileNotFoundException {
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.startsWith("package ")) {
                    return line.substring(8, line.indexOf(';')).trim();
                }
            }
        }
        return ""; // default package
    }

    // Prints the list of class names
    public static void printClassNames(List<String> classNames) {
        System.out.println("Fully qualified class names found:");
        for (String name : classNames) {
            System.out.println(name);
        }
    }
}