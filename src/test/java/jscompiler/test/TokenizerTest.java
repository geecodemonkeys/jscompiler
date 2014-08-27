package jscompiler.test;

import jscompiler.Tokenizer;
import jscompiler.TokenizerImpl;

import org.junit.Test;

public class TokenizerTest {
	
	@Test
	public void test_number_0() {
		String input = " /*com */\t\t\t\t \n\r\n 04564  " +
				"4564  0xFFFfA89F  0.000123 3.45e2 " +
				"0.0345e-2 0.0345e+2 0.0345e+0 0.0345e+01";
		Tokenizer tokenizer = new TokenizerImpl(input);
		TokenizerTestUtils.matchSequence(tokenizer, "@@@04564" , "@@@4564", 
				"@@@0xFFFfA89F",  "@@@0.000123", "@@@3.45e2", "@@@0.0345e-2",
				"@@@0.0345e+2",
				"@@@0.0345e+0",
				"@@@0.0345e+01");
	}
	
	@Test
	public void test_number_1() {
		String input = " /*com */ 04564  0xfff9  456.001e-56 /*hahah*/ " +
				"45.34e56 //comment \r\n 056  ";
		Tokenizer tokenizer = new TokenizerImpl(input);
		TokenizerTestUtils.matchSequence(tokenizer, "@@@04564", 
				"@@@0xfff9", 
				"@@@456.001e-56",
				"@@@45.34e56",
				"@@@056");
	}
	
	@Test
	public void test_number_2() {
		String input = " 0.0e+  0.2e- 0.1e 0.1eeabc 0.1e+";
		Tokenizer tokenizer = new TokenizerImpl(input);
		TokenizerTestUtils.matchSequence(tokenizer, "@@@@0.0e+",
				"@@@@0.2e-",
				"@@@@0.1e",
				"@@@@0.1eeabc",
				"@@@@0.1e+");
	}
	
	//TODO test for exception wrong format for 0.08e--9
	
	@Test
	public void test_0() {
		String input = " 5+8-8/5*3";
		Tokenizer tokenizer = new TokenizerImpl(input);
		TokenizerTestUtils.matchSequence(tokenizer, "@@@5", 
				"+", "@@@8", "-", "@@@8", "/", "@@@5", "*", "@@@3");
	}
	
	@Test
	public void test_1() {
		String input = "4 + 5";
		Tokenizer tokenizer = new TokenizerImpl(input);
		TokenizerTestUtils.matchSequence(tokenizer, "@@@4", "+", "@@@5");
	}
	
	@Test
	public void test_2() {
		String input = "var x = 0; // just a comment";
		Tokenizer tokenizer = new TokenizerImpl(input);
		TokenizerTestUtils.matchSequence(tokenizer, "var", "@x", "=", "@@@0", ";");
	}
	
	@Test
	public void test_3() {
		String input = "function(){return x*x<<(a?-5:5);}";
		Tokenizer tokenizer = new TokenizerImpl(input);
		TokenizerTestUtils.matchSequence(tokenizer, "function", "(", ")", "{", "return",
				"@x", "*", "@x", "<<", "(", "@a", "?", "-", "@@@5", ":", 
				"@@@5", ")", ";", "}");
	}
	
	@Test
	public void test_4() {
		String input = "x&&y||z^ /*jdhdh*/4";
		Tokenizer tokenizer = new TokenizerImpl(input);
		TokenizerTestUtils.matchSequence(tokenizer, "@x", "&&", "@y", "||",
				"@z", "^", "@@@4");
	}
	
	@Test
	public void test_5() {
		String input = "x<x>=4==a";
		Tokenizer tokenizer = new TokenizerImpl(input);
		TokenizerTestUtils.matchSequence(tokenizer, "@x", "<", "@x", ">=", "@@@4", "==", "@a");
	}
	
	@Test
	public void test_6() {
		String input = " \"just'i\"+'haha\"'";
		Tokenizer tokenizer = new TokenizerImpl(input);
		TokenizerTestUtils.matchSequence(tokenizer, "@@just'i", "+", "@@haha\"");
	}
	
	@Test
	public void test_7() {
		String input = "   \"just\n\"haha\r\n'just'";
		Tokenizer tokenizer = new TokenizerImpl(input);
		TokenizerTestUtils.matchSequence(tokenizer, "@@@@just", "@@@@haha", "@@just");
	}
	
	
}
