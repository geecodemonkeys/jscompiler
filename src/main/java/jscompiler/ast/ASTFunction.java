package jscompiler.ast;

import java.util.List;

public interface ASTFunction {
	
	final static int DECLARATION = 1;
	final static int EXPRESSION = 1;

	List<ASTIdentifier> getArguments();
	List<ASTNode> getBody();
	ASTIdentifier getName();
	int getCode();
}
