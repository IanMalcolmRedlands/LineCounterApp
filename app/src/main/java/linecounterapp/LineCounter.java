package linecounterapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


/**
 * Class to analyze .java files and store data about their contents.
 */
public class LineCounter {
	HashMap<String, Integer> methodCounts; //tallies number of lines in method definitions
	HashMap<String, Integer> controlCounts; //tallies number of times each control type is used

	int linecount;
	int nonCodeLines;
	
	private Matcher singleCommentMatcher, multiCommentBeginMatcher, multiCommentEndMatcher;
	private Matcher whitespaceMatcher;
	private Matcher methodMatcher;
	private Matcher elseMatcher;
	private Matcher controlMatcher; //also matches methods, so should be used only if methodMatcher didn't match
	private Matcher openBracketMatcher, openBracketInStringMatcher;
	private Matcher closeBracketMatcher, closeBracketInStringMatcher;
	private Matcher semicolonMatcher;
	
	public LineCounter() {
		methodCounts = new HashMap<String, Integer>();
		controlCounts = new HashMap<String, Integer>();
		
		linecount = 0;
		nonCodeLines = 0;
		
		Pattern singleCommentPattern = Pattern.compile("^\\s*//.*$");
		singleCommentMatcher = singleCommentPattern.matcher("");
		
		Pattern multiCommentBeginPattern = Pattern.compile("^\\s*/\\*.*$");
		multiCommentBeginMatcher = multiCommentBeginPattern.matcher("");
		
		Pattern multiCommentEndPattern = Pattern.compile("^.*\\*/.*$");
		multiCommentEndMatcher = multiCommentEndPattern.matcher("");
		
		Pattern whitespacePattern = Pattern.compile("^\\s*$");
		whitespaceMatcher = whitespacePattern.matcher("");
		
		Pattern methodPattern = Pattern.compile("^\\h*.*[\\w <>,]+(?<!new)\\h+(\\w+\\h*\\((?:[\\w <>,]+\\h+\\w+,?\\h*)*\\)).*$");
		methodMatcher = methodPattern.matcher("");
		
		Pattern elsePattern = Pattern.compile("^\\s*else\\s*\\{?$");
		elseMatcher = elsePattern.matcher("");
		
		Pattern controlPattern = Pattern.compile("^\\s*(\\w+(?: \\w+)*)\\s*\\(.*\\).*$(?<!;)");
		controlMatcher = controlPattern.matcher("");
		
		Pattern openBracketPattern = Pattern.compile("^.*\\{.*$");
		openBracketMatcher = openBracketPattern.matcher("");
		Pattern openBracketInStringPattern = Pattern.compile("^.*\\\".*\\{.*\\\".*$");
		openBracketInStringMatcher = openBracketInStringPattern.matcher("");
		
		Pattern closeBracketPattern = Pattern.compile("^.*\\}.*$");
		closeBracketMatcher = closeBracketPattern.matcher("");
		Pattern closeBracketInStringPattern = Pattern.compile("^.*\\\".*\\}.*\\\".*$");
		closeBracketInStringMatcher = closeBracketInStringPattern.matcher("");

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
	
	/**
	 * This code is arguably still readable
	 * @param reader
	 * @return
	 */
	private String readFile(Scanner reader) {
		//Variables to keep track of methods
		String currentMethod = "";
		int unclosedBraces = 0; //counts number of unclosed braces left in method definition
		boolean expectingMethodOpeningBracket = false;
		boolean withinMethod = false;
		
		boolean withinMultiComment = false;
		
		
		while (reader.hasNextLine()) {
			String line = reader.nextLine();
			
			//not-code lines
			if (withinMultiComment) {
				nonCodeLines++;
				if (isMultiCommentEnd(line)) {
					withinMultiComment = false;
				}
				continue;
			}
			
			else if (isWhitespace(line)) {
				nonCodeLines++;
				continue;
			}
			
			else if (isSingleLineComment(line)) {
				nonCodeLines++;
				continue;
			}
			
			else if (isMultiCommentBegin(line)) {
				nonCodeLines++;
				if (!isMultiCommentEnd(line)) {
					withinMultiComment = true;
				}
				continue; 
			}
			
			
			//code lines
			linecount++;
			
			if (expectingMethodOpeningBracket) {
				if (hasOpeningBracket(line)) {
					unclosedBraces = 1;
					expectingMethodOpeningBracket = false;
					withinMethod = true;
				}
				continue;
			}
			
			if (withinMethod) {
				if (hasOpeningBracket(line)) {
					unclosedBraces++;
				}
				if (hasClosingBracket(line)) {
					unclosedBraces--;
					if (unclosedBraces < 1) {
						withinMethod = false;
					}
				}
				methodCounts.replace(currentMethod, methodCounts.get(currentMethod)+1);
			}
			
			else if (isMethod(line)) {
				currentMethod = methodMatcher.group(1);
				methodCounts.put(currentMethod, 0);
				
				if (hasSemicolon(line)) {
					continue;
				}
				else if (hasOpeningBracket(line)) {
					unclosedBraces = 1;
					withinMethod = true;
				}
				else {
					expectingMethodOpeningBracket = true;
				}
				
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
			
			else if (isControlStructure(line)) {
				String name = controlMatcher.group(1);
				if (controlCounts.get(name) == null) {
					controlCounts.put(name, 1);
				}
				else {
					controlCounts.replace(name, controlCounts.get(name)+1);
				}
			}
		}
		
		return "Stripped Line Count: "+linecount+
				"\nEmpty Lines: "+nonCodeLines;
	}
	
	/**
	 * Returns an ObservableList&ltCountValue&gt of the methods in the file. Meant for use with javafx tables.
	 * @return Counts of method lengths in the form of an ObservableList&ltCountValue&gt
	 */
	public ObservableList<CountValue> getMethodsList() {
		ArrayList<CountValue> list = new ArrayList<CountValue>();
		
		ArrayList<String> keys = new ArrayList<String>(methodCounts.keySet());
		Collections.sort(keys);
		
		for (String key : keys) {
			list.add(new CountValue(key, methodCounts.get(key)));
		}
		
		return FXCollections.observableArrayList(list);
	}
	
	/**
	 * Returns an ObservableList&ltCountValue&gt of the counts of control structures in the file. Meant for use with javafx tables.
	 * @return Counts of control structures in the form of an ObservableList&ltCountValue&gt
	 */
	public ObservableList<CountValue> getControlList() {
		ArrayList<CountValue> list = new ArrayList<CountValue>();
		
		ArrayList<String> keys = new ArrayList<String>(controlCounts.keySet());
		Collections.sort(keys);
		
		for (String key : keys) {
			list.add(new CountValue(key, controlCounts.get(key)));
		}
		
		return FXCollections.observableArrayList(list);
	}
	
	/**
	 * Creates a text table of a count hashmap.
	 * @param label1 Title of first column (the names)
	 * @param label2 Title of second column (the counts)
	 * @param map
	 * @return
	 */
	private String formatCountHashmap(String label1, String label2, HashMap<String, Integer> map) {
		String table = String.format("%-50s | %-10s", label1.toUpperCase(), label2.toUpperCase());
		
		ArrayList<String> keys = new ArrayList<String>(map.keySet());
		Collections.sort(keys);
		
		for (String key : keys) {
			table += String.format("\n%-50s | %-10s", key, map.get(key));
		}
		
		return table;
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
		openBracketInStringMatcher.reset(line);
		return openBracketMatcher.matches() && !openBracketInStringMatcher.matches();
	}
	
	private boolean hasClosingBracket(String line) {
		closeBracketMatcher.reset(line);
		closeBracketInStringMatcher.reset(line);
		return closeBracketMatcher.matches() && !closeBracketInStringMatcher.matches();
	}
}
