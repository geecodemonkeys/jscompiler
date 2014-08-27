package jscompiler.ast;

import jscompiler.ast.visitor.ASTVisitor;

public class ASTTry extends ASTStatement {

	private ASTStatement tryBlock;
	private ASTIdentifier catchIdentifier;
	private ASTStatement catchBlock;
	private ASTStatement finallyBlock;

	public ASTTry(ASTStatement block, ASTIdentifier catchIdentifier,
			ASTStatement catchBlock, ASTStatement finallyBlock) {
		this.tryBlock = block;
		this.catchIdentifier = catchIdentifier;
		this.catchBlock = catchBlock;
		this.finallyBlock = finallyBlock;
	}

	public ASTIdentifier getCatchIdentifier() {
		return catchIdentifier;
	}

	public void setCatchIdentifier(ASTIdentifier catchIdentifier) {
		this.catchIdentifier = catchIdentifier;
	}

	public ASTStatement getTryBlock() {
		return tryBlock;
	}

	public void setTryBlock(ASTStatement tryBlock) {
		this.tryBlock = tryBlock;
	}

	public ASTStatement getCatchBlock() {
		return catchBlock;
	}

	public void setCatchBlock(ASTStatement catchBlock) {
		this.catchBlock = catchBlock;
	}

	public ASTStatement getFinallyBlock() {
		return finallyBlock;
	}

	public void setFinallyBlock(ASTStatement finallyBlock) {
		this.finallyBlock = finallyBlock;
	}
	
	@Override
	public void accept(ASTVisitor visitor) {
		if (!visitor.visit(this)) {
			return;
		}
		
		if (tryBlock != null) {			
			tryBlock.accept(visitor);
		}
		
		if (catchIdentifier != null) {			
			catchIdentifier.accept(visitor);
		}
		
		if (catchBlock != null) {			
			catchBlock.accept(visitor);
		}
		
		if (finallyBlock != null) {			
			finallyBlock.accept(visitor);
		}
		visitor.endVisit(this);
	}

}
