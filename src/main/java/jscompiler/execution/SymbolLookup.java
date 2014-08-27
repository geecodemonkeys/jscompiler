package jscompiler.execution;

public class SymbolLookup {
	
	private int depth;
	private int offset;
	public SymbolLookup(int depth, int offset) {
		super();
		this.depth = depth;
		this.offset = offset;
	}
	public int getDepth() {
		return depth;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}

}
