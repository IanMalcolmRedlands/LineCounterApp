package linecounterapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LineCounter {
	HashMap<String, Integer> methodCounts; //tallies number of lines in method definitions
	HashMap<String, Integer> controlCounts; //tallies number of times each control type is used
	
	private Matcher singleCommentMatcher, multiCommentBeginMatcher, multiCommentEndMatcher;
	private Matcher whitespaceMatcher;
	private Matcher methodMatcher;
	private Matcher elseMatcher;
	private Matcher controlMatcher; //also matches methods, so should be used only if methodMatcher didn't match
	private Matcher openBracketMatcher, closeBracketMatcher, semicolonMatcher;
	
	public LineCounter() {
		controlCounts = new HashMap<String, Integer>();
		
		Pattern singleCommentPattern = Pattern.compile("^\\s*//.*$");
		singleCommentMatcher = singleCommentPattern.matcher("");
		
		Pattern multiCommentBeginPattern = Pattern.compile("^\\s*/\\*.*$");
		multiCommentBeginMatcher = multiCommentBeginPattern.matcher("");
		
		Pattern multiCommentEndPattern = Pattern.compile("^.*\\*/.*$");
		multiCommentEndMatcher = multiCommentEndPattern.matcher("");
		
		Pattern whitespacePattern = Pattern.compile("^\\s*$");
		whitespaceMatcher = whitespacePattern.matcher("");
		
		Pattern methodPattern = Pattern.compile("^\\h*.*\\w+(?<!new)\\h+(\\w+\\h*\\((?:\\w+\\h+\\w+,*\\h*)*\\)).*$");
		methodMatcher = methodPattern.matcher("");
		
		Pattern elsePattern = Pattern.compile("^\\s*else\\s*\\{?$");
		elseMatcher = elsePattern.matcher("");
		
		Pattern controlPattern = Pattern.compile("^\\s*(\\w+(?: \\w+)*)\\s*\\(.*\\).*$(?<!;)");
		controlMatcher = controlPattern.matcher("");
		
		Pattern openBracketPattern = Pattern.compile("^.*\\{.*$");
		openBracketMatcher = openBracketPattern.matcher("");
		
		Pattern closeBracketPattern = Pattern.compile("^.*\\}.*$");
		closeBracketMatcher = closeBracketPattern.matcher("");

		Pattern semicolonPattern = Pattern.compile("^.*;.*$");
		semicolonMatcher = semicolonPattern.matcher("");
	}
	
	/**
	 * Takes a .java file
	 * Counts number of lines, not including blank lines or comments.
	 * Gets method names and shows number of lines in each.
	 * Counts and returns table providing the counts of while, for, if, else if, else, and switch statements used
	 */
	public String analyzeFile(File file) {
		try {
			Scanner reader = new Scanner(file);
			return readFile(reader);
		} catch (FileNotFoundException e) {
			return "File not found: "+e.toString();
		}
	}
	
	private String readFile(Scanner reader) {
		int unclosedBraces = 0; //counts number of unclosed braces left in method definition
		boolean inMultiComment = false;
		
		int linecount = 0;
		int singleComments = 0;
		int multiCommentLines = 0;
		int emptyLines = 0;
		
		
		while (reader.hasNextLine()) {
			String line = reader.nextLine();
			
			//not-code lines
			if (inMultiComment) {
				multiCommentLines++;
				if (isMultiCommentEnd(line)) {
					inMultiComment = false;
				}
				continue;
			}
			
			else if (isWhitespace(line)) {
				emptyLines++;
				continue;
			}
			
			else if (isSingleLineComment(line)) {
				singleComments++;
				continue;
			}
			
			else if (isMultiCommentBegin(line)) {
				if (!isMultiCommentEnd(line)) {
					inMultiComment = true;
					multiCommentLines++;
				}
				continue; 
			}
			
			
			//code lines
			linecount++;
			
			if (isMethod(line)) {
				System.out.println("Method: "+methodMatcher.group(1));
				//TODO: keep track of methods
				continue;
			}
			
			if (isElse(line)) {
				if (controlCounts.get("else") == null) {
					controlCounts.put("else", 1);
				}
				else {
					controlCounts.replace("else", controlCounts.get("else")+1);
				}
				continue;
			}
			
			if (isControlStructure(line)) {
				String name = controlMatcher.group(1);
				if (controlCounts.get(name) == null) {
					controlCounts.put(name, 1);
				}
				else {
					controlCounts.replace(name, controlCounts.get(name)+1);
				}
			}
		}
		
		return "Line count: "+linecount+"\nSingle line comments: "+singleComments+
				"\nMulti line comments line count: "+multiCommentLines+
				"\nEmpty lines: "+emptyLines+
				"\nControl Structures: "+controlCounts.toString();
	}
	
	private boolean isSingleLineComment(String line) {
		singleCommentMatcher.reset(line);
		return singleCommentMatcher.matches();
	}
	
	private boolean isMultiCommentBegin(String line) {
		multiCommentBeginMatcher.reset(line);
		return multiCommentBeginMatcher.matches();
	}
	
	private boolean isMultiCommentEnd(String line) {
		multiCommentEndMatcher.reset(line);
		return multiCommentEndMatcher.matches();
	}
	
	private boolean isWhitespace(String line) {
		whitespaceMatcher.reset(line);
		return whitespaceMatcher.matches();
	}
	
	private boolean isMethod(String line) {
		methodMatcher.reset(line);
		return methodMatcher.matches();
	}
	
	private boolean isControlStructure(String line) {
		controlMatcher.reset(line);
		return controlMatcher.matches();
	}
	
	private boolean isElse(String line) {
		elseMatcher.reset(line);
		return elseMatcher.matches();
	}
	
	private boolean hasSemicolon(String line) {
		semicolonMatcher.reset(line);
		return semicolonMatcher.matches();
	}
	
	private boolean hasOpeningBracket(String line) {
		openBracketMatcher.reset(line);
		return openBracketMatcher.matches();
	}
	
	private boolean hasClosingBracket(String line) {
		closeBracketMatcher.reset(line);
		return closeBracketMatcher.matches();
	}
}
