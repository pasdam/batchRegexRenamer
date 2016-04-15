package com.pasdam.regexren.gui.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.regex.Pattern;

import org.junit.Test;

import com.pasdam.regexren.model.FileModelItem;
import com.pasdam.test.TestFileReader;

public class TestReplaceFactory {
	
	private static final String TEST_FOLDER = "src" + File.separator 
											+ "test" + File.separator
											+ "resources" + File.separator
											+ "com" + File.separator
											+ "pasdam" + File.separator
											+ "regexren" + File.separator
											+ "gui" + File.separator
											+ "rules";
	private static final String TEST_FILE = TEST_FOLDER + File.separator + "replace.tst";

	@Test
	public void test() {
		try {
			File testData = new File(TEST_FILE);
			TestFileReader reader = new TestFileReader(testData, Pattern.compile(Pattern.quote(";")));
			
			String[] parts;
			while ((parts = reader.readAndSplitLine()).length > 0) {
				// create and configure rule factory
				ReplaceFactory factory = new ReplaceFactory();
				factory.setTextToReplace(parts[1]);
				factory.setTextToInsert(parts[2]);
				factory.setStartIndex(Integer.parseInt(parts[3]));
				factory.setEndIndex(Integer.parseInt(parts[4]));
				factory.setTarget(Integer.parseInt(parts[5]));
				factory.setMatchCase(Integer.parseInt(parts[6]) == 1);
				factory.setRegex(Integer.parseInt(parts[7]) == 1);
				
				// create rule
				Rule rule = factory.createConfiguredRule();
				
				// test result
				assertEquals(parts[8], rule.apply(new FileModelItem(new File(parts[0]))).getNewFullName());
			}
			
		} catch (Exception exception) {
			exception.printStackTrace();
			fail(exception.getMessage());
		}
	}
}
