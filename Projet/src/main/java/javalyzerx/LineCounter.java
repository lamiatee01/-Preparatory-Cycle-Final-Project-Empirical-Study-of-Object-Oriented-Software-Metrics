package javalyzerx;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.comments.Comment;

public class LineCounter {
	
	
	
	public static long countTotalLines(String path) throws IOException {      
        Path sourcePath = Paths.get(path);
        long lineCount = Files.lines(sourcePath).count();   
        
        return lineCount;
	}
	
	
	public static int countNonEmptyLines(String classPath) throws IOException {
		int linecount = 0;
		BufferedReader reader = new BufferedReader(new FileReader(classPath));
		String line;
		while ((line = reader.readLine()) != null) {
		    if (!line.trim().isEmpty()) {
		        linecount++;
		    }
		}
		reader.close();
		return linecount;
		
	}
	
	
	
	public static int countComments(CompilationUnit cu) {
		List<Comment> comments = cu.getAllContainedComments();
	    
	    int totalCommentLines = 0;

	    for (Comment comment : comments) {
	        if (comment.getRange().isPresent()) {
	            Range range = comment.getRange().get();
	            int linesSpanned = range.end.line - range.begin.line + 1;
	            totalCommentLines += linesSpanned;
	        }
	    }

	    return totalCommentLines;
	}
	

}
