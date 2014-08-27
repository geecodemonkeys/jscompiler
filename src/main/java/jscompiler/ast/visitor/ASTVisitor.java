package jscompiler.ast.visitor;

import jscompiler.ast.ASTArrayConstant;
import jscompiler.ast.ASTAssignmentExpression;
import jscompiler.ast.ASTBlockStatement;
import jscompiler.ast.ASTBreak;
import jscompiler.ast.ASTCase;
import jscompiler.ast.ASTConstant;
import jscompiler.ast.ASTContinue;
import jscompiler.ast.ASTDoWhile;
import jscompiler.ast.ASTEmthyExpression;
import jscompiler.ast.ASTEmthyStatement;
import jscompiler.ast.ASTExpression;
import jscompiler.ast.ASTFor;
import jscompiler.ast.ASTForIn;
import jscompiler.ast.ASTFunctionCall;
import jscompiler.ast.ASTFunctionDeclaration;
import jscompiler.ast.ASTFunctionExpression;
import jscompiler.ast.ASTIdentifier;
import jscompiler.ast.ASTIfStatement;
import jscompiler.ast.ASTIndexExpression;
import jscompiler.ast.ASTLabel;
import jscompiler.ast.ASTList;
import jscompiler.ast.ASTNameValuePair;
import jscompiler.ast.ASTNewExpression;
import jscompiler.ast.ASTNode;
import jscompiler.ast.ASTObjectConst;
import jscompiler.ast.ASTParenExpression;
import jscompiler.ast.ASTPostfixExpression;
import jscompiler.ast.ASTPropertyAccess;
import jscompiler.ast.ASTRegEx;
import jscompiler.ast.ASTReturn;
import jscompiler.ast.ASTRoot;
import jscompiler.ast.ASTStatement;
import jscompiler.ast.ASTSwitch;
import jscompiler.ast.ASTTernaryCondition;
import jscompiler.ast.ASTThrow;
import jscompiler.ast.ASTTry;
import jscompiler.ast.ASTTwoOperandExpression;
import jscompiler.ast.ASTUnaryExpression;
import jscompiler.ast.ASTVariableDeclaration;
import jscompiler.ast.ASTWhile;
import jscompiler.ast.ASTWith;

public abstract class ASTVisitor {
	public boolean visit( ASTArrayConstant  node){ return true; }
	public boolean visit( ASTAssignmentExpression  node){ return true; }
	public boolean visit( ASTBlockStatement  node){ return true; }
	public boolean visit( ASTBreak  node){ return true; }
	public boolean visit( ASTCase  node){ return true; }
	public boolean visit( ASTConstant  node){ return true; }
	public boolean visit( ASTContinue  node){ return true; }
	public boolean visit( ASTDoWhile  node){ return true; }
	public boolean visit( ASTEmthyExpression  node){ return true; }
	public boolean visit( ASTEmthyStatement  node){ return true; }
	public boolean visit( ASTExpression  node){ return true; }
	public boolean visit( ASTForIn  node){ return true; }
	public boolean visit( ASTFor  node){ return true; }
	public boolean visit( ASTFunctionCall  node){ return true; }
	public boolean visit( ASTFunctionDeclaration  node){ return true; }
	public boolean visit( ASTFunctionExpression  node){ return true; }
	public boolean visit( ASTIdentifier  node){ return true; }
	public boolean visit( ASTIfStatement  node){ return true; }
	public boolean visit( ASTIndexExpression  node){ return true; }
	public boolean visit( ASTLabel  node){ return true; }
	public boolean visit( ASTList  node){ return true; }
	public boolean visit( ASTNameValuePair  node){ return true; }
	public boolean visit( ASTNewExpression  node){ return true; }
	public boolean visit( ASTNode  node){ return true; }
	public boolean visit( ASTObjectConst  node){ return true; }
	public boolean visit( ASTParenExpression  node){ return true; }
	public boolean visit( ASTPostfixExpression  node){ return true; }
	public boolean visit( ASTPropertyAccess  node){ return true; }
	public boolean visit( ASTRegEx  node){ return true; }
	public boolean visit( ASTReturn  node){ return true; }
	public boolean visit( ASTRoot  node){ return true; }
	public boolean visit( ASTStatement  node){ return true; }
	public boolean visit( ASTSwitch  node){ return true; }
	public boolean visit( ASTTernaryCondition  node){ return true; }
	public boolean visit( ASTThrow  node){ return true; }
	public boolean visit( ASTTry  node){ return true; }
	public boolean visit( ASTTwoOperandExpression  node){ return true; }
	public boolean visit( ASTUnaryExpression  node){ return true; }
	public boolean visit( ASTVariableDeclaration  node){ return true; }
	public boolean visit( ASTWhile  node){ return true; }
	public boolean visit( ASTWith  node) { return true; } 
	
	public void endVisit( ASTArrayConstant  node){  }
	public void endVisit( ASTAssignmentExpression  node){  }
	public void endVisit( ASTBlockStatement  node){  }
	public void endVisit( ASTBreak  node){  }
	public void endVisit( ASTCase  node){  }
	public void endVisit( ASTConstant  node){  }
	public void endVisit( ASTContinue  node){  }
	public void endVisit( ASTDoWhile  node){  }
	public void endVisit( ASTEmthyExpression  node){  }
	public void endVisit( ASTEmthyStatement  node){  }
	public void endVisit( ASTExpression  node){  }
	public void endVisit( ASTForIn  node){  }
	public void endVisit( ASTFor  node){  }
	public void endVisit( ASTFunctionCall  node){  }
	public void endVisit( ASTFunctionDeclaration  node){  }
	public void endVisit( ASTFunctionExpression  node){  }
	public void endVisit( ASTIdentifier  node){  }
	public void endVisit( ASTIfStatement  node){  }
	public void endVisit( ASTIndexExpression  node){  }
	public void endVisit( ASTLabel  node){  }
	public void endVisit( ASTList  node){  }
	public void endVisit( ASTNameValuePair  node){  }
	public void endVisit( ASTNewExpression  node){  }
	public void endVisit( ASTNode  node){  }
	public void endVisit( ASTObjectConst  node){  }
	public void endVisit( ASTParenExpression  node){  }
	public void endVisit( ASTPostfixExpression  node){  }
	public void endVisit( ASTPropertyAccess  node){  }
	public void endVisit( ASTRegEx  node){  }
	public void endVisit( ASTReturn  node){  }
	public void endVisit( ASTRoot  node){  }
	public void endVisit( ASTStatement  node){  }
	public void endVisit( ASTSwitch  node){  }
	public void endVisit( ASTTernaryCondition  node){  }
	public void endVisit( ASTThrow  node){  }
	public void endVisit( ASTTry  node){  }
	public void endVisit( ASTTwoOperandExpression  node){  }
	public void endVisit( ASTUnaryExpression  node){  }
	public void endVisit( ASTVariableDeclaration  node){  }
	public void endVisit( ASTWhile  node){  }
	public void endVisit( ASTWith  node) {  } 

}
