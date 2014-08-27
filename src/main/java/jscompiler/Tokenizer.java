package jscompiler;

import jscompiler.token.Token;

public abstract class Tokenizer {
	
	protected String program;
	
	public Tokenizer(String program) {
		this.program = program;
	}
	
	public abstract int getTokenIdx();
	public abstract Token getAtIndex(int index);
	
	public abstract Token next();
	public abstract Token nextOrEOL();
	
	public abstract Token peek();
	
	public abstract void back();
	
	public abstract int getCurrentLine();

	public abstract Token eatRegExConst();

}
