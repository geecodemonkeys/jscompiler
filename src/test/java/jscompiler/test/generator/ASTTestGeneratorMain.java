package jscompiler.test.generator;

import java.io.File;
import java.io.IOException;

import jscompiler.Parser;
import jscompiler.ast.ASTRoot;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

public class ASTTestGeneratorMain {
	
	//modify to your full path
	private static String PATTERN = "src/test/resources/pattern.txt";
	private static final String OUTPUT_FOLDER =  "src/test/resources/output";
	private static final String INPUT_FILE = "src/test/resources/input.txt";
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException {
		String pattern = FileUtils.readFileToString(new File(PATTERN));
		String input = FileUtils.readFileToString(new File(INPUT_FILE));
		ObjectMapper objMapper = new ObjectMapper();
		String[] tokens = input.split("###");
		int i = 1;
		StringBuilder sb = new StringBuilder();
		for (String js : tokens) {
			if (js.trim().length() == 0) {
				continue;
			}
			System.out.println("Test " + i + " of " + tokens.length);
			Parser parser = new Parser(js.trim());
			ASTRoot root = parser.parse();
			ObjectWriter writer = objMapper.defaultPrettyPrintingWriter();
			String json = writer.writeValueAsString(root);
			FileUtils.write(new File(new File(OUTPUT_FOLDER),
					"ast_expected_" + i + ".json"), json);
			sb.append(pattern.replace("@@", i + "").replace("###", escapeQuote(js.trim())));
			i++;
		}
		FileUtils.writeStringToFile(
				new File(new File(OUTPUT_FOLDER), "out.txt"), 
				sb.toString());

	}

	private static CharSequence escapeQuote(String js) {
		return js.replace("\"", "\\\"");
	}

}
