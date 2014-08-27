jscompiler
==========

This is a simple JavaScript Interepreter written in Java.
It is not a full implementation but it is a rather some kind of toy implementation.
It contains a top down recursive descent parser in Parser.java. It contains an Abstract
syntax tree classes with prefix AST. The parser can be used just to parse some JavaScript file and to generate
its Abstract Syntax Tree. The AST is executed using the visitor pattern. It is implemented in
SimpleExecutionVisitor.java. You can see who the interpreter is used in the tests. See 
InterpreterTest.java. There you can see the features the interpeter supports. Even there is
a simple sudoku implementation in sudoku.js used as a test case.
