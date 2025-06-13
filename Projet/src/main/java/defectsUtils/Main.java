package defectsUtils;


//this is the main class used to create the csv files for the defects project
public class Main {
	public static void main(String[] args) throws Exception {
		
		//all the projects path's must be in this format "main_folder\project_name\Bugs\Buggy_i" for the buggy versions
		//and "main_folder\project_name\Fixed\fixed_i"  for the fixed versions , where 'i' is the number of the version 
		
		String defects4jpath = "C:\\Users\\aicha\\Videos\\defects4j";
		String projectspath = "C:\\Users\\aicha\\Videos\\projet_pluri"; 
		String csvpath = "C:\\Users\\aicha\\OneDrive - etu.usthb.dz\\Desktop\\defects_csv";
		
		
		
		
		/*
		try {
		    Utility.WriteCsvDefects4j(defects4jpath, csvpath, "Gson", projectspath);
		} catch (Exception e) {
		    System.err.println("Error processing Gson");
		    e.printStackTrace();
		}
		
		try {
		    Utility.WriteCsvDefects4j(defects4jpath, csvpath, "JxPath", projectspath);
		} catch (Exception e) {
		    System.err.println("Error processing JxPath");
		    e.printStackTrace();
		}
		
		try {
		    Utility.WriteCsvDefects4j(defects4jpath, csvpath, "JacksonCore", projectspath);
		} catch (Exception e) {
		    System.err.println("Error processing JacksonCore");
		    e.printStackTrace();
		}

		try {
		    Utility.WriteCsvDefects4j(defects4jpath, csvpath, "Compress", projectspath);
		} catch (Exception e) {
		    System.err.println("Error processing Compress");
		    e.printStackTrace();
		}
		
		try {
		    Utility.WriteCsvDefects4j(defects4jpath, csvpath, "Time", projectspath);
		} catch (Exception e) {
		    System.err.println("Error processing Time");
		    e.printStackTrace();
		}
		
		try {
		    Utility.WriteCsvDefects4j(defects4jpath, csvpath, "Mockito", projectspath);
		} catch (Exception e) {
		    System.err.println("Error processing Mockito");
		    e.printStackTrace();
		}*/
		
		
		/*try {
		    Utility.WriteCsvDefects4j(defects4jpath, csvpath, "Lang", projectspath);
		} catch (Exception e) {
		    System.err.println("Error processing Lang");
		    e.printStackTrace();
		}
		
		
		try {
		    Utility.WriteCsvDefects4j(defects4jpath, csvpath, "Math", projectspath);
		} catch (Exception e) {
		    System.err.println("Error processing Math");
		    e.printStackTrace();
		}*/

		try {
		    Utility.WriteCsvDefects4j(defects4jpath, csvpath, "JacksonDatabind", projectspath);
		} catch (Exception e) {
		    System.err.println("Error processing JacksonDatabind");
		    e.printStackTrace();
		}

		/*try {
		    Utility.WriteCsvDefects4j(defects4jpath, csvpath, "Csv", projectspath);
		} catch (Exception e) {
		    System.err.println("Error processing Csv");
		    e.printStackTrace();
		}*/

		

		
		/*try {
	    Utility.WriteCsvDefects4j(defects4jpath, csvpath, "Cli", projectspath);
		} catch (Exception e) {
		    System.err.println("Error processing Cli");
		    e.printStackTrace();
		}*/

		/*try {
		    Utility.WriteCsvDefects4j(defects4jpath, csvpath, "Closure", projectspath);
		} catch (Exception e) {
		    System.err.println("Error processing Closure");
		    e.printStackTrace();
		}*/
	
		/*try {
		    Utility.WriteCsvDefects4j(defects4jpath, csvpath, "Collections", projectspath);
		} catch (Exception e) {
		    System.err.println("Error processing Collections");
		    e.printStackTrace();
		}*/
		

		/*try {
		    Utility.WriteCsvDefects4j(defects4jpath, csvpath, "JacksonXml", projectspath);
		} catch (Exception e) {
		    System.err.println("Error processing JacksonXml");
		    e.printStackTrace();
		}*/

		

		

		/*
		try {
		    Utility.WriteCsvDefects4j(defects4jpath, csvpath, "Codec", projectspath);
		} catch (Exception e) {
		    System.err.println("Error processing Codec");
		    e.printStackTrace();
		}
		
		try {
		    Utility.WriteCsvDefects4j(defects4jpath, csvpath, "Chart", projectspath);
		} catch (Exception e) {
		    System.err.println("Error processing Chart");
		    e.printStackTrace();
		}

		try {
		    Utility.WriteCsvDefects4j(defects4jpath, csvpath, "Jsoup", projectspath);
		} catch (Exception e) {
		    System.err.println("Error processing Jsoup");
		    e.printStackTrace();
		}*/

	
	}
}
