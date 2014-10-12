package formulaParser.formulaAbsyntree;
import formulaParser.Visitor;
public class IndexVariable extends Variable{

	public Identifier i;
	public Index idx;
	public String key;
	public int index;
//	public int kind; //0 is int value; 1 is String value
//	public int int_val;
//	public String str_val;
	
	public IndexVariable(int p, Identifier i, Index idx){
		this.pos = p;
		this.i = i;
		this.idx = idx;
		this.key = i.key;
		this.index = idx.int_val;
	}
	public void accept(Visitor v){
		v.visit(this);
	}
}
