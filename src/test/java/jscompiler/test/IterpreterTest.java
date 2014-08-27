package jscompiler.test;

import java.io.IOException;
import java.io.InputStream;

import jscompiler.Main;
import jscompiler.util.ErrorMessages;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

//TODO add tests for two closures simultaneously returned
//modifying their own state
public class IterpreterTest {
	
	@Test
	public void test_1() {
		String result = Main.execute("  5+ 5");
		Assert.assertEquals("10.0", result);
	}
	
	@Test
	public void test_2() {
		String result = Main.execute("  5+ 5 / 2");
		Assert.assertEquals("7.5", result);
	}
	
	@Test
	public void test_3() {
		String result = Main.execute("  5+ 5 / 2 * 5");
		Assert.assertEquals("17.5", result);
	}
	
	@Test
	public void test_4() {
		String result = Main.execute("  5+ (5 / 2 * 5)");
		Assert.assertEquals("17.5", result);
	}
	
	@Test
	public void test_5() {
		String result = Main.execute("  (1 + 1 - 4)*3");
		Assert.assertEquals("-6.0", result);
	}

	@Test
	public void test_6() {
		String result = Main.execute("  (1 + 1 - 4)*3/6/8");
		Assert.assertEquals("-0.125", result);
	}
	

	@Test
	public void test_7() {
		String result = Main.execute("  var a = 5; a + a");
		Assert.assertEquals("10.0", result);
	}
	
	@Test
	public void test_8() {
		String result = Main.execute("  var a = 5; a + a * 2");
		Assert.assertEquals("15.0", result);
	}
	
	@Test
	public void test_9() {
		String result = Main.execute("  var a = (1 + 1 - 4)*3/6/8; a + a ");
		Assert.assertEquals("-0.25", result);
	}
	
	@Test
	public void test_10() {
		String result = Main.execute("  var a = (1 + 1 - 4)*3/6/8, " +
				"b = 2 * a; a + b ");
		Assert.assertEquals("-0.375", result);
	}
	
	@Test
	public void test_11() {
		String result = Main.execute("  var a = 5, " +
				"b = 2 * a, c = a*b; var d = c /b*a; d ");
		Assert.assertEquals("25.0", result);
	}
	
	@Test
	public void test_12() {
		String result = Main.execute("var a = 5; " +
				"function funct() {return 10;} " +
				"a + funct()");
		Assert.assertEquals("15.0", result);
	}
	
	@Test
	public void test_13() {
		String result = Main.execute("var a = 5, b = 3; " +
				"function funct(a,b,c,d) {return a + b + c + d;} " +
				"a + funct(a,2,3,b)");
		Assert.assertEquals("18.0", result);
	}
	
	@Test
	public void test_14() {
		String result = Main.execute("var a = 5, b = 3; " +
				"function funct(a,b,c,d) {return a + b + c + d;} " +
				"a + funct(a + b/8,2 * a,3,b*b)");
		Assert.assertEquals("32.375", result);
	}
	
	@Test
	public void test_15() {
		String result = Main.execute("var a = 5, b = 3; " +
				"function funct(a,b,c,d) {return a + b + c + d;return 4} " +
				"a + funct(a + b/8,2 * a,3,b*b)");
		Assert.assertEquals("32.375", result);
	}
	
	@Test
	public void test_16() {
		String result = Main.execute("var a = 5, b = 3; " +
				"function funct(a,b,c,d) {return a + b + c + d;return 4} " +
				"var last = a + funct(a + b/8,2 * a,3,b*b);" +
				"last");
		Assert.assertEquals("32.375", result);
	}
	
	@Test
	public void test_17() {
		String result = Main.execute("var a = 5, b = 3; " +
				"function funct(a,b,c,d) {return a + b + c + d;return 4} " +
				"var last = a + funct(a + b/funct(a,a,a,1),2 * a,3,b*b);" +
				"last");
		Assert.assertEquals("32.1875", result);
	}
	
	@Test
	public void test_18() {
		String result = Main.execute("var outer = 10;" +
				"function f1 () {" +
				"function f2() { function f3() {return 3 + outer;} return f3();}"+
				"return f2();} f1()");
		Assert.assertEquals("13.0", result);
	}
	
	@Test
	public void test_19() {
		String result = Main.execute("var outer = 10;" +
				"function f1 () {" +
				"function f2() { function f3() {return 3 + outer;}" +
				"function f3() {return 13 + outer;} return f3();}"+
				"return f2();} f1()");
		Assert.assertEquals("23.0", result);
	}
	
	
	@Test
	public void test_20() {
		String result = Main.execute("var outer = 10;" +
				"function f1() {return outer;}" +
				"function f2() {return f1;}; f2()()");
		Assert.assertEquals("10.0", result);
	}
	
	@Test
	public void test_21() {
		String result = Main.execute("var outer = 10;" +
				"function f1(a) {return outer + a;}" +
				"function f2() {return f1;} " +
				"function f3() {return f2} f3()()(3)");
		Assert.assertEquals("13.0", result);
	}
	
	@Test
	public void test_22() {
		String result = Main.execute("5 < 10");
		Assert.assertEquals("true", result);
	}
	
	@Test
	public void test_23() {
		String result = Main.execute("var a = 5; if (true) {var a = 10;} a");
		Assert.assertEquals("10.0", result);
	}
	
	@Test
	public void test_24() {
		String result = Main.execute("var a = 5; if (6 > 3) a= 4; else {a = 10;} a");
		Assert.assertEquals("4.0", result);
	}
	
	@Test
	public void test_25() {
		String result = Main.execute("var a = 5; if (a > 3) a= 4; else {a = 10;} a");
		Assert.assertEquals("4.0", result);
	}
	
	@Test
	public void test_26() {
		String result = Main.execute("function fact(x) { if (x == 1)" +
				"{ return 1; }else{ return fact(x - 1) * x}} fact(2)");
		Assert.assertEquals("2.0", result);
	}
	
	@Test
	public void test_27() {
		String result = Main.execute("function fact(x) { if (x == 1)" +
				"{ return 1; }else{ return fact(x - 1) * x}} fact(6)");
		Assert.assertEquals("720.0", result);
	}
	
	@Test
	public void test_28() {
		String result = Main.execute("function fact(x) { if (x == 1)" +
				"{ return 1; }else{ return fact(x - 1) * x}} fact(10)");
		Assert.assertEquals("3628800.0", result);
	}
	
	@Test
	public void test_29() {
		String result = Main.execute("function fact(x) { if (x == 1)" +
				"{ return 1; }else{ return fact(x - 1) * x}} fact(fact(3))");
		Assert.assertEquals("720.0", result);
	}
	
	@Test
	public void test_30() {
		String result = Main.execute("var outer = 5; " +
				"function funct() {outer = 10;} var a = funct, b = a; b(); outer");
		Assert.assertEquals("10.0", result);
	}
	
	@Test
	public void test_31() {
		try {
			String result = Main.execute("var a = 5; " +
					"function funct() {return a;} funct() = 10; a");
		} catch (Exception e) {			
			Assert.assertEquals(ErrorMessages.BAD_LEFT_HAND_SIDE, e.getMessage());
			return;
		}
		Assert.assertEquals(true, false);
	}
	
	@Test
	public void test_32() {
		try {
			String result = Main.execute("5 = 10");
		} catch (Exception e) {			
			Assert.assertEquals(ErrorMessages.BAD_LEFT_HAND_SIDE, e.getMessage());
			return;
		}
		Assert.assertEquals(true, false);
	}
	
	@Test
	public void test_33() {
		String result = Main.execute("var outer = 5; " +
				"function funct() {return outer = 15;} function funct2(a) {outer = a() + 5} funct2(funct); outer");
		Assert.assertEquals("20.0", result);
	}
	
	@Test
	public void test_34() {
		String result = Main.execute("function makeAdder(x) {" +
				"function add(y) {return x + y;} return add;" +
				"} var add5 = makeAdder(5); add5(2);");
		Assert.assertEquals("7.0", result);
	}
	
	@Test
	public void test_35() {
		String result = Main.execute("function a() {" +
				"function b(x) {" +
				"return x + o;" +
				"}" +
				"var o = 5;" +
				"return b;}" +
				"var f = a();" +
				"f(3)");
		Assert.assertEquals("8.0", result);
	}
	
	@Test
	public void test_36() {
		String result = Main.execute("function a() {" +
				"var o = 5;" +
				"function b(x) {" +
				"return x + o;" +
				"}" +
				"return b;}" +
				"var f = a();" +
				"f(3)");
		Assert.assertEquals("8.0", result);
	}
	
	@Test
	public void test_37() {
		String result = Main.execute("function minus(x,y) {return x - y;}" +
				"function fact(x, minus) { if (x == 1)" +
				"{ return 1; }else{ return fact(minus(x,1),minus) * x}} fact(6,minus)");
		Assert.assertEquals("720.0", result);
	}
	
	@Test
	public void test_38() {
		try {
			String result = Main.execute("function a() {" +
					"var b = 5;var c = 10;" +
					"function b() {c = 20;}" +
					"b();" +
					"return c;}" +
					"a();");
		} catch (Exception e) {			
			Assert.assertEquals(ErrorMessages.NOT_A_FUNCTION, e.getMessage());
			return;
		}
		Assert.assertEquals(true, false);
	}
	
	@Test
	public void test_40() {
		String result = Main.execute("(function () {return 5;})();");
		Assert.assertEquals("5.0", result);
	}
	
	@Test
	public void test_41() {
		String result = Main.execute("function a(x,y, funct) {return funct(x,y)} " +
				"a(5,6,function (a,b) {return a + b;});");
		Assert.assertEquals("11.0", result);
	}
	
	@Test
	public void test_42() {
		String result = Main.execute("var a = 5; (function () {a = 10;})(); a");
		Assert.assertEquals("10.0", result);
	}
	
	@Test
	public void test_43() {
		String result = Main.execute("function fact(x, minus) { if (x == 1)" +
				"{ return 1; }else{ return fact(minus(x,1),minus) * x}} " +
				"fact(6,function (x,y) {return x - y;})");
		Assert.assertEquals("720.0", result);
	}
	
	@Test
	public void test_44() {
		try {
			String result = Main.execute("(function inner() {});  inner();");
		} catch (Exception e) {			
			Assert.assertEquals(
					e.getMessage().startsWith(
							ErrorMessages.REFERENCE_NOT_DEFINED), true);
			return;
		}
		Assert.assertEquals(true, false);
	}
	
	@Test
	public void test_45() {
		String result = Main.execute("(function fact(x) { if (x == 1)" +
				"{ return 1; }else{ return fact(x - 1) * x}})(6) ");
		Assert.assertEquals("720.0", result);
	}
	
	@Test
	public void test_46() {
		String result = Main.execute("'haha' + 'haha'");
		Assert.assertEquals("\"hahahaha\"", result);
	}
	
	@Test
	public void test_50() {
		String result = Main.execute("var a = { prop1: 'haha', prop2: 'hihi'}; a");
		Assert.assertEquals("Object { prop2 : \"hihi\" , prop1 : \"haha\" }", result);
	}
	
	@Test
	public void test_51() {
		String result = Main.execute("var a = { prop1: 'haha', add: function (x,y) {return x + y;}}; a");
		Assert.assertEquals("Object { prop1 : \"haha\" , add : function (){} }", result);
	}
	
	@Test
	public void test_52() {
		String result = Main.execute("var a = { prop1: 'haha', obj : {'prop2' : 5}}; a");
		Assert.assertEquals("Object { prop1 : \"haha\" , " +
				"obj : Object { prop2 : 5.0 } }", result);
	}
	
	@Test
	public void test_53() {
		String result = Main.execute("(function (){return { prop1: 'haha', obj : {'prop2' : 5}}})()");
		Assert.assertEquals("Object { prop1 : \"haha\" , " +
				"obj : Object { prop2 : 5.0 } }", result);
	}
	
	@Test
	public void test_54() {
		String result = Main.execute("var a = { prop1: 'haha', obj : {'prop2' : 5}}; a.prop1");
		Assert.assertEquals("\"haha\"", result);
	}
	
	@Test
	public void test_55() {
		String result = Main.execute("var a = { prop1: 'haha', obj : {'prop2' : 5}}; a.obj.prop2");
		Assert.assertEquals("5.0", result);
	}
	
	@Test
	public void test_56() {
		String result = Main.execute("var z = 3; " +
				"var a = { prop1: 'haha', " +
				"obj : function (x,y) {return x + y + z;}}; a.obj(1,2)");
		Assert.assertEquals("6.0", result);
	}
	
	@Test
	public void test_57() {
		String result = Main.execute("function a() { " +
				"var inner = 3; " +
				"return {inc: function (){inner = inner + 1;}, " +
				"dec: function () {inner = inner -1}, " +
				"get: function (){return inner;}}" +
				"}" +
				"var obj = a(); " +
				"obj.inc(); obj.inc(); " +
				"obj.dec(); obj.dec(); obj.get();");
		Assert.assertEquals("3.0", result);
	}
	
	@Test
	public void test_58() {
		String result = Main.execute("var a = {funct : function (){return 'parent';}}; " +
				"var b = {__proto__ : a}; b.funct()");
		Assert.assertEquals("\"parent\"", result);
	}

	@Test
	public void test_59() {
		String result = Main.execute("var a = {funct : function (){return 'parent';}}; " +
				"var b = {__proto__ : a, funct : function (){return 'chield';}}; b.funct()");
		Assert.assertEquals("\"chield\"", result);
	}
	
	@Test
	public void test_60() {
		String result = Main.execute("var a = {funct : function (){return 'parent';}}; " +
				"var b = {funct : function (){return 'chield';}}; b.__proto__ = a; b.funct()");
		Assert.assertEquals("\"chield\"", result);
	}
	
	@Test
	public void test_61() {
		String result = Main.execute("var obj = {prop1 : null}; " +
				"obj.prop1= {prop2: null}; " +
				"obj.prop1.prop2 = { prop3: 'haha'}; " +
				"obj.prop1.prop2.prop3");
		Assert.assertEquals("\"haha\"", result);
	}
	
	@Test
	public void test_62() {
		String result = Main.execute("var obj = {prop1 : null}; " +
				"obj.prop1 = function () {return 'haha'}; obj.prop1()");
		Assert.assertEquals("\"haha\"", result);
	}
	
	@Test
	public void test_63() {
		String result = Main.execute("var obj = {}; " +
				"obj.prop1 = function () {return 'haha'}; obj.prop1()");
		Assert.assertEquals("\"haha\"", result);
	}
	
	@Test
	public void test_64() {
		String result = Main.execute("var parent = { prop1 : 5}; var obj = { __proto__: parent }; " +
				"obj.prop1 = function () {return 'haha'}; obj.prop1()");
		Assert.assertEquals("\"haha\"", result);
	}
	
	@Test
	public void test_65() {
		String result = Main.execute("var parent = { prop1 : 5, funct: function (){return 'haha'}}; " +
				"var obj = { obj: { __proto__: parent} }; " +
				" obj.obj.funct()");
		Assert.assertEquals("\"haha\"", result);
	}
	
	@Test
	public void test_66() {
		String result = Main.execute("var parent = { prop1 : 5}; var parent2 = {prop1: 'haha'};" +
				"var obj = {  __proto__: parent }; obj.__proto__ = parent2;" +
				" obj.prop1");
		Assert.assertEquals("\"haha\"", result);
	}
	
	@Test
	public void test_67() {
		String result = Main.execute("var parent = { prop1 : 'parent'}; " +
				"var parent2 = {prop1: 'haha', inner : {__proto__: parent}};" +
				"var obj = { }; obj.__proto__ = parent2.inner.__proto__;" +
				" obj.prop1");
		Assert.assertEquals("\"parent\"", result);
	}
	
	@Test
	public void test_68() {
		String result = Main.execute("function A(x) {this.x = x;} " +
				"var a = new A('haha'); a.x;");
		Assert.assertEquals("\"haha\"", result);
	}
	
	@Test
	public void test_69() {
		String result = Main.execute("function A(x) {this.x = x;} " +
				"function B(y) {this.y = y; return new A('fake')}" +
				"var a = new B('haha'); a.x;");
		Assert.assertEquals("\"fake\"", result);
	}
	
	@Test
	public void test_70() {
		String result = Main.execute("function A() {} " +
				"A.prototype.y = 'haha'; " +
				"var a = new A(); a.y;");
		Assert.assertEquals("\"haha\"", result);
	}
	
	@Test
	public void test_71() {
		String result = Main.execute("function A() {} " +
				"A.prototype.x = 'haha'; " +
				"var a = new A(); " +
				"A.prototype = {constructor: A, y: 'second_proto'}; " +
				"var b = new A(); b.y + '#' + a.x");
		Assert.assertEquals("\"second_proto#haha\"", result);
	}
	
	@Test
	public void test_72() {
		String result = Main.execute("function A() {} " +
				"A.prototype.y = 'haha'; " +
				"var a = new A(); a.constructor;");
		Assert.assertEquals("function A(){}", result);
	}
	
	@Test
	public void test_73() {
		String result = Main.execute("function A() {} " +
				"A.prototype.y = 'haha'; " +
				"var a = new A(); a.constructor.prototype.y;");
		Assert.assertEquals("\"haha\"", result);
	}
	
	@Test
	public void test_74() {
		String result = Main.execute("(function (){" +
				"function A(x) {this.x = x;} " +
				"var a = new A('AA'); " +
				"return a.x;" +
				"})();");
		Assert.assertEquals("\"AA\"", result);
	}
	
	@Test
	public void test_75() {
		String result = Main.execute("a = 'AA'; global.a");
		Assert.assertEquals("\"AA\"", result);
	}
	
	@Test
	public void test_76() {
		String result = Main.execute("a = 'AA'; a");
		Assert.assertEquals("\"AA\"", result);
	}
	
	@Test
	public void test_77() {
		String result = Main.execute("global.name = 'global'; function foo() { " +
				"function bar() { " +
				"return this.name; " +
				"} return bar(); } foo()");
		Assert.assertEquals("\"global\"", result);
	}
	
	@Test
	public void test_78() {
		String result = Main.execute("global.name = 'global'; var foo = { " +
				"name: 'foo', " +
				"bar: function () { " +
				"return this.name;" +
				"}}; foo.bar()");
		Assert.assertEquals("\"foo\"", result);
	}
	
	@Test
	public void test_79() {
		String result = Main.execute("global.name = 'global'; var foo = { " +
				"name: 'foo', " +
				"bar: function () { " +
				"return this.name;" +
				"}}; (foo.bar)()");
		Assert.assertEquals("\"foo\"", result);
	}
	
	@Test
	public void test_80() {
		String result = Main.execute("global.name = 'global'; var foo = { " +
				"name: 'foo', " +
				"bar: function () { " +
				"return this.name;" +
				"}}; (foo.bar = foo.bar)()");
		Assert.assertEquals("\"global\"", result);
	}
	
	@Test
	public void test_81() {
		String result = Main.execute("global.name = 'global'; " +
				"(function () {return this.name })();");
		Assert.assertEquals("\"global\"", result);
	}
	
	@Test
	public void test_82() {
		String result = Main.execute("function foo() {return this.field;} " +
				"var x = {field: 'x'}; var y = {field: 'y'}; " +
				"x.funct = foo; y.funct = foo; x.funct() + '#' + y.funct()");
		Assert.assertEquals("\"x#y\"", result);
	}
	
	@Test
	public void test_83() {
		String result = Main.execute(" 16 % 7");
		Assert.assertEquals("2.0", result);
	}
	
	@Test
	public void test_84() {
		String result = Main.execute(" 'aaa' < 'aaaaa'");
		Assert.assertEquals("true", result);
	}
	
	@Test
	public void test_85() {
		String result = Main.execute(" 'aaa' > 'aaaaa'");
		Assert.assertEquals("false", result);
	}
	
	@Test
	public void test_86() {
		String result = Main.execute(" 'aaa' >= 'aaaaa'");
		Assert.assertEquals("false", result);
	}
	
	@Test
	public void test_87() {
		String result = Main.execute(" 'aaa' <= 'aaaaa'");
		Assert.assertEquals("true", result);
	}
	
	@Test
	public void test_88() {
		String result = Main.execute(" 'aaa' <= 'aab'");
		Assert.assertEquals("true", result);
	}
	
	@Test
	public void test_89() {
		String result = Main.execute(" 'aac' <= 'aab'");
		Assert.assertEquals("false", result);
	}
	
	@Test
	public void test_90() {
		String result = Main.execute(" 'aaa' < 'aab'");
		Assert.assertEquals("true", result);
	}
	
	@Test
	public void test_91() {
		String result = Main.execute(" 'aac' < 'aab'");
		Assert.assertEquals("false", result);
	}
	
	@Test
	public void test_92() {
		String result = Main.execute(" 'aaa' <= 'aab'");
		Assert.assertEquals("true", result);
	}
	
	@Test
	public void test_93() {
		String result = Main.execute(" 'aac' >= 'aab'");
		Assert.assertEquals("true", result);
	}
	
	@Test
	public void test_94() {
		String result = Main.execute(" 'aaa' > 'aab'");
		Assert.assertEquals("false", result);
	}
	
	@Test
	public void test_95() {
		String result = Main.execute(" 'aac' > 'aab'");
		Assert.assertEquals("true", result);
	}
	
	@Test
	public void test_96() {
		String result = Main.execute(" 'aaa' >= 'aaa'");
		Assert.assertEquals("true", result);
	}
	
	@Test
	public void test_97() {
		String result = Main.execute(" 'aaa' <= 'aaa'");
		Assert.assertEquals("true", result);
	}
	
	@Test
	public void test_98() {
		String result = Main.execute(" 2 <= 2");
		Assert.assertEquals("true", result);
	}
	
	@Test
	public void test_99() {
		String result = Main.execute(" 3 <= 2");
		Assert.assertEquals("false", result);
	}
	
	@Test
	public void test_100() {
		String result = Main.execute(" 2 >= 2");
		Assert.assertEquals("true", result);
	}
	
	@Test
	public void test_101() {
		String result = Main.execute(" 99.0e250 * 99.0e250 >= 2");
		Assert.assertEquals("true", result);
	}
	
	@Test
	public void test_102() {
		String result = Main.execute(" 2 < 2");
		Assert.assertEquals("false", result);
	}
	
	@Test
	public void test_103() {
		String result = Main.execute(" 2 > 2");
		Assert.assertEquals("false", result);
	}
	
	@Test
	public void test_104() {
		String result = Main.execute(" var i = 0; while ((i = i + 1) < 2) i ");
		Assert.assertEquals("1.0", result);
	}
	
	@Test
	public void test_105() {
		String result = Main.execute(" var i = 0; while ((i = i + 1) < 11) i ");
		Assert.assertEquals("10.0", result);
	}
	
	@Test
	public void test_106() {
		String result = Main.execute(" var i = 0; while ((i = i + 1) < 11) {} i ");
		Assert.assertEquals("11.0", result);
	}
	
	@Test
	public void test_107() {
		String result = Main.execute(" var i = 0; i != 0 ? 'true' : 'false'");
		Assert.assertEquals("\"false\"", result);
	}
	
	@Test
	public void test_108() {
		String result = Main.execute(" true && false");
		Assert.assertEquals("false", result);
	}
	
	@Test
	public void test_109() {
		String result = Main.execute(" true && true");
		Assert.assertEquals("true", result);
	}
	
	@Test
	public void test_110() {
		String result = Main.execute(" true || false");
		Assert.assertEquals("true", result);
	}
	
	@Test
	public void test_111() {
		String result = Main.execute(" false || false");
		Assert.assertEquals("false", result);
	}
	
	@Test
	public void test_112() {
		String result = Main.execute(" false || {}");
		Assert.assertEquals("Object {  }", result);
	}
	
	@Test
	public void test_113() {
		String result = Main.execute(" !false");
		Assert.assertEquals("true", result);
	}
	
	@Test
	public void test_114() {
		String result = Main.execute(" 5 === '5'");
		Assert.assertEquals("false", result);
	}
	
	@Test
	public void test_115() {
		String result = Main.execute(" '' === ''");
		Assert.assertEquals("true", result);
	}
	
	@Test
	public void test_116() {
		String result = Main.execute(" false === false");
		Assert.assertEquals("true", result);
	}
	
	@Test
	public void test_117() {
		String result = Main.execute(" var a = {prop:''}, b = a; a === b");
		Assert.assertEquals("true", result);
	}
	
	@Test
	public void test_118() {
		String result = Main.execute(" var a = {prop:1}; a.prop++;a.prop;");
		Assert.assertEquals("2.0", result);
	}
	
	@Test
	public void test_119() {
		String result = Main.execute(" var a = {prop:1}; a.prop++");
		Assert.assertEquals("1.0", result);
	}
	
	@Test
	public void test_120() {
		String result = Main.execute(" var a = {prop:2}; a.prop--;a.prop;");
		Assert.assertEquals("1.0", result);
	}
	
	@Test
	public void test_121() {
		String result = Main.execute(" var a = {prop:2}; a.prop--");
		Assert.assertEquals("2.0", result);
	}
	
	@Test
	public void test_122() {
		String result = Main.execute(" var a = 1; a++;a;");
		Assert.assertEquals("2.0", result);
	}
	
	@Test
	public void test_123() {
		String result = Main.execute(" var a = 1; a++");
		Assert.assertEquals("1.0", result);
	}
	
	@Test
	public void test_124() {
		String result = Main.execute(" var a = 2; a--;a;");
		Assert.assertEquals("1.0", result);
	}
	
	@Test
	public void test_125() {
		String result = Main.execute(" var a = 1; a--");
		Assert.assertEquals("1.0", result);
	}
	
	@Test
	public void test_126() {
		String result = Main.execute(" var a = 1; -a");
		Assert.assertEquals("-1.0", result);
	}
	
	@Test
	public void test_127() {
		String result = Main.execute(" 1++");
		Assert.assertEquals("1.0", result);
	}
	
	@Test
	public void test_128() {
		String result = Main.execute(" var a = [1,2,3,4,5,6,7]; a");
		Assert.assertEquals("[1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0]", result);
	}
	
	@Test
	public void test_129() {
		String result = Main.execute(" var a = [6,2,3,4,5,6,7]; a[0]");
		Assert.assertEquals("6.0", result);
	}
	
	@Test
	public void test_130() {
		String result = Main.execute(" var a = [1,2,3]; a[3] = 4; a");
		Assert.assertEquals("[1.0, 2.0, 3.0, 4.0]", result);
	}
	
	@Test
	public void test_131() {
		String result = Main.execute(" var a = []; a[\"a\"] = 4; a.a");
		Assert.assertEquals("4.0", result);
	}
	
	@Test
	public void test_132() {
		String result = Main.execute(" var a = [[1,2], [3,4]]; a");
		Assert.assertEquals("[[1.0, 2.0], [3.0, 4.0]]", result);
	}
	
	@Test
	public void test_133() {
		String result = Main.execute(" var a = true; if (a) {'haha';}");
		Assert.assertEquals("\"haha\"", result);
	}
	
	@Test
	public void test_134() throws IOException {
		InputStream stream = ParserTest.class.getResourceAsStream("/sudoku.js");
		String sudokuJs= IOUtils.toString(stream);
		String result = Main.execute(sudokuJs);
		Assert.assertEquals("[[2.0, 4.0, 1.0, 9.0, 7.0, 5.0, 3.0, 6.0, 8.0], "
				+ "[5.0, 6.0, 7.0, 3.0, 8.0, 4.0, 1.0, 2.0, 9.0], "
				+ "[9.0, 8.0, 3.0, 2.0, 1.0, 6.0, 7.0, 4.0, 5.0], "
				+ "[4.0, 9.0, 5.0, 6.0, 3.0, 1.0, 8.0, 7.0, 2.0], "
				+ "[6.0, 3.0, 8.0, 7.0, 9.0, 2.0, 4.0, 5.0, 1.0], "
				+ "[1.0, 7.0, 2.0, 4.0, 5.0, 8.0, 9.0, 3.0, 6.0], "
				+ "[7.0, 5.0, 4.0, 1.0, 2.0, 9.0, 6.0, 8.0, 3.0], "
				+ "[8.0, 1.0, 6.0, 5.0, 4.0, 3.0, 2.0, 9.0, 7.0], "
				+ "[3.0, 2.0, 9.0, 8.0, 6.0, 7.0, 5.0, 1.0, 4.0]]", result);
	}
	
	
	//TODO This tests are failing
		/*
		@Test
		public void test_47() {
			String result = Main.execute("'haha' + 5");
			Assert.assertEquals("haha5", result);
		}
		
		@Test
		public void test_39() {
			String result = Main.execute("function returnNothing(){} " +
					"function a(x, a) {return x * a} a(returnNothing(), 5);");
			Assert.assertEquals("720.0", result);
		}
		
		@Test
		public void test_48() {
			String result = Main.execute("'haha' + NaN");
			Assert.assertEquals("hahaNaN", result);
		}
		
		@Test
		public void test_49() {
			String result = Main.execute("'haha' + undefined");
			Assert.assertEquals("hahaundefined", result);
		}
		*/
//	@Test
//	public void test_83() {
//		String result = Main.execute("importClass('java.io.File'); " +
//				"var file = new File('/home/fi')");
//		Assert.assertEquals("\"x#y\"", result);
//	}
}
