package jscompiler;

import java.io.File;
import java.io.IOException;

import jscompiler.ast.ASTRoot;
import jscompiler.ast.visitor.IdResolveVisitor;
import jscompiler.ast.visitor.SimpleExecutionVisitor;
import jscompiler.execution.RefUtil;
import jscompiler.jsobject.JsObjectBase;
import jscompiler.jsobject.JsReference;

import org.apache.commons.io.FileUtils;

public class Main {
	
	public static String execute(String js) {
		Parser parser = new Parser(js);
		ASTRoot root = parser.parse();
		
		//sets the id bindings
		IdResolveVisitor idResolver = new IdResolveVisitor();
		root.accept(idResolver);
		SimpleExecutionVisitor visitor = new SimpleExecutionVisitor();
		root.accept(visitor);
		int length = visitor.getStack().size();
		JsObjectBase base = visitor.getStack().get(length - 1);
		if (base.getCode() == JsObjectBase.REFERENCE) {
			base = RefUtil.getRefValue((JsReference)base);
		}
		return String.valueOf(base.getRealValue());
	}

	public static void main(String[] args) throws IOException {
		String js = FileUtils.readFileToString(
				new File("/home/fi/MY_COMPILER/jsCompiler/src/test/resources/sudoku.js"));
		System.out.println(execute(js));
		/*(String js = FileUtils.readFileToString(
				new File("/home/fi/Desktop/test.js"));
		Parser parser = new Parser("5 * 5"
				);
		ASTRoot root = parser.parse();
		
		ObjectMapper objMapper = new ObjectMapper();
		ObjectWriter writer = objMapper.defaultPrettyPrintingWriter();
		String actual = writer.writeValueAsString(root);;
		System.out.println(actual);*/

	}

}
