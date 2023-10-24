package linecounterapp;

import java.io.File;

class LineCounterTest {
	public static void main(String args[]) {
		File file = new File("testfile.java");
		LineCounter lineCounter = new LineCounter();
    	System.out.println(lineCounter.analyzeFile(file));
    	return;
	}
}