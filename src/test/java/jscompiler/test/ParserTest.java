package jscompiler.test;

import java.io.IOException;
import java.io.InputStream;

import jscompiler.Parser;
import jscompiler.ast.ASTRoot;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class ParserTest {
	
	static ObjectMapper objMapper;
	static {
		objMapper = new ObjectMapper();
	}
	
	private static String getJson(String fileName) throws IOException {
		InputStream stream = ParserTest.class.getResourceAsStream("/json/"+fileName);
		return IOUtils.toString(stream);
	}
	
	public void testBase(String js, String fileName) 
			throws JsonGenerationException, JsonMappingException, IOException, JSONException {
		Parser parser = new Parser(js);
		ASTRoot root = parser.parse();
		
		ObjectMapper objMapper = new ObjectMapper();
		ObjectWriter writer = objMapper.defaultPrettyPrintingWriter();
		String actual = writer.writeValueAsString(root);
		JSONAssert.assertEquals(getJson(fileName), actual, false);
	}
	
	@Test
	public void test_0() 
			throws JsonGenerationException, JsonMappingException, IOException {
		String js = " function filip(a,b,c,d) {if (a == 5 / c * 54 && a/4==f*3 || ff!=5) {}}";
		Parser parser = new Parser(js);
		ASTRoot root = parser.parse();
		
		ObjectWriter writer = objMapper.defaultPrettyPrintingWriter();
	}
	
	@Test
	public void test_1() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("function filip(a,b,c,d) {if (a == 5 / c * 54 && a/4==f*3 || ff!=5) {}}", "ast_expected_1.json");
	}
	@Test
	public void test_2() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("function filip() {if (document.someFunct() / 10 == this.prop * 5) {}}", "ast_expected_2.json");
	}
	@Test
	public void test_3() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("function filip() {if (document.someFunct() / 10 == this.prop * 5 && !true != (c | f)) {}}", "ast_expected_3.json");
	}
	@Test
	public void test_4() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("function filip() {if ((new Object()).prop1 == null  ^ (true && flag)) {}}", "ast_expected_4.json");
	}
	@Test
	public void test_5() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("function filip() {if (((new Object()).prop1 + 5 - 4 / (i++)) > this.number) {}}", "ast_expected_5.json");
	}
	@Test
	public void test_6() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("function filip() {if ((5 + 6 % 3) >= b) {}}", "ast_expected_6.json");
	}
	@Test
	public void test_7() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("function filip() {if ((((5))) >= (((b)))) {}}", "ast_expected_7.json");
	}
	@Test
	public void test_8() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("function filip() {if (!b && !c || d) {}}", "ast_expected_8.json");
	}
	@Test
	public void test_9() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("function filip() {if (a<<2>5) {}}", "ast_expected_9.json");
	}
	@Test
	public void test_10() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("function filip() {if ((-this.prop*3)<=(-this.writeln())) {}}", "ast_expected_10.json");
	}
	@Test
	public void test_11() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("var1 = 5;", "ast_expected_11.json");
	}
	@Test
	public void test_12() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("var1 = 5.6;", "ast_expected_12.json");
	}
	@Test
	public void test_13() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("var1 = \"some_text\\\"\";", "ast_expected_13.json");
	}
	@Test
	public void test_14() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("var1 = [];", "ast_expected_14.json");
	}
	@Test
	public void test_15() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("var1 = [1,2,3];", "ast_expected_15.json");
	}
	@Test
	public void test_16() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("var1 = [\"a\", \"b\", \"c\"];", "ast_expected_16.json");
	}
	@Test
	public void test_17() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("var1 = [a * b / 5, c % f];", "ast_expected_17.json");
	}
	@Test
	public void test_18() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("var1 = (function (a) {})(5);", "ast_expected_18.json");
	}
	@Test
	public void test_19() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("var1 = this.someProp.someOtherProp.prop1.prop2;", "ast_expected_19.json");
	}
	@Test
	public void test_20() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("var1 = a && b || c && !f ? trueVar : falseVar;", "ast_expected_20.json");
	}
	@Test
	public void test_21() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("var1 = --a + ++f*d++;", "ast_expected_21.json");
	}
	@Test
	public void test_22() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("var1 = (new new new new Object(a))()[1]().prop;", "ast_expected_22.json");
	}
	@Test
	public void test_23() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("var1 = +a * d--;", "ast_expected_23.json");
	}
	@Test
	public void test_24() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("var1 = typeof x==\"undefined\";", "ast_expected_24.json");
	}
	@Test
	public void test_25() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("var1 = x instanceof String;", "ast_expected_25.json");
	}
	@Test
	public void test_26() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("var a,b,c,d;", "ast_expected_26.json");
	}
	@Test
	public void test_27() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("var a = 5 * 6 / 2 * a, d = \"haha\" + \"haha\";", "ast_expected_27.json");
	}
	@Test
	public void test_28() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("while (i < 5) i = i + 1;", "ast_expected_28.json");
	}
	@Test
	public void test_29() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("while (i < 5) { var a = 5; }", "ast_expected_29.json");
	}
	@Test
	public void test_30() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("do i = i + 1; while ( i < 5);", "ast_expected_30.json");
	}
	@Test
	public void test_31() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("do{ i = i + 1;} while (i < 5);", "ast_expected_31.json");
	}
	@Test
	public void test_32() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("function filip(a) {return a + 1;}", "ast_expected_32.json");
	}
	@Test
	public void test_33() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("function filip(a) {return ;}", "ast_expected_33.json");
	}
	
	@Test
	public void test_34() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("if (a) {aa = 3;} else if (b) {bb=4;} else if (c) {cc1=5;} else {lastMostInner=10;}", "ast_expected_34.json");
	}
	@Test
	public void test_35() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("while (i < 10) {if (i * i > 50) break;}", "ast_expected_35.json");
	}
	@Test
	public void test_36() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("while (i < 10) {if (i * i > 50) continue;}", "ast_expected_36.json");
	}
	@Test
	public void test_37() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("with (a < 5) r = 5;", "ast_expected_37.json");
	}
	@Test
	public void test_38() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("with (a < 5){ f = 5; }", "ast_expected_38.json");
	}
	@Test
	public void test_39() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("label1 : while (i < 10) {if (i * i > 50) break label1;}", "ast_expected_39.json");
	}
	@Test
	public void test_40() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("label1 : while (i < 10) {if (i * i > 50) continue label1;}", "ast_expected_40.json");
	}
	@Test
	public void test_41() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("try { throw new Exception(); } catch (e) { print(\"error\"); } finally {clean();}", "ast_expected_41.json");
	}
	@Test
	public void test_42() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("try { throw new Exception(); } finally {clean();}", "ast_expected_42.json");
	}
	@Test
	public void test_43() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("try { throw new Exception(); } catch (e) { print(\"error\"); }", "ast_expected_43.json");
	}
	@Test
	public void test_44() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("var objectLiteral = {};", "ast_expected_44.json");
	}
	@Test
	public void test_45() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("var objectLiteralNotEmthy = {\"prop1\" : \"val1\", 4 : \"four\", id : 5};", "ast_expected_45.json");
	}
	@Test
	public void test_46() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("var objectLiteralNotEmthy = {id : function(){return 5;}};", "ast_expected_46.json");
	}
	@Test
	public void test_47() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("switch (a) {}", "ast_expected_47.json");
	}
	@Test
	public void test_48() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("switch (a) {case 1:case 2:case 3:}", "ast_expected_48.json");
	}
	@Test
	public void test_49() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("switch (a) {case 1:break; default:break;}", "ast_expected_49.json");
	}
	@Test
	public void test_50() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("for (var a in [1,2,3,4]) {}", "ast_expected_50.json");
	}
	@Test
	public void test_51() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("for (;;) {a = 5;}", "ast_expected_51.json");
	}
	@Test
	public void test_52() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("for (var i =0; i <5 ; i++) {}", "ast_expected_52.json");
	}
	@Test
	public void test_53() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("label1 : while (i < 10) {if (i * i > 50) break label1}", "ast_expected_53.json");
	}
	@Test
	public void test_54() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("label1 : while (i < 10) {if (i * i > 50) continue label1}", "ast_expected_54.json");
	}
	@Test
	public void test_55() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("function func(){return\n5+5}", "ast_expected_55.json");
	}
	@Test
	public void test_56() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("var a = funct\n(d +\nc\n)", "ast_expected_56.json");
	}
	@Test
	public void test_57() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("for (;\n;) {}", "ast_expected_57.json");
	}
	@Test
	public void test_58() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("if(cond)a/=5", "ast_expected_58.json");
	}
	@Test
	public void test_59() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("a=a/5", "ast_expected_59.json");
	}
	@Test
	public void test_60() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("c[0]/=5", "ast_expected_60.json");
	}
	@Test
	public void test_61() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("if(cond) /haha/.exec()", "ast_expected_61.json");
	}
	@Test
	public void test_62() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("var a = /\\w+\\/\\\\/ig", "ast_expected_62.json");
	}
	@Test
	public void test_63() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("/\\w+\\/\\\\/ig.exec()", "ast_expected_63.json");
	}
	@Test
	public void test_64() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("variable /= /\\w+\\/\\\\/.exec()", "ast_expected_64.json");
	}
	@Test
	public void test_65() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("variable /= 10 / /\\w+\\/\\\\/.exec()", "ast_expected_65.json");
	}
	@Test
	public void test_66() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("/\\n\\\\r/.exec()", "ast_expected_66.json");
	}
	@Test
	public void test_67() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("function outer() {function inner() {}}", "ast_expected_67.json");
	}
	@Test
	public void test_68() throws JsonGenerationException, 
			JsonMappingException, IOException, JSONException {
		testBase("function outer() {if (true) {function fstatement(){}}}", "ast_expected_68.json");
	}
}
