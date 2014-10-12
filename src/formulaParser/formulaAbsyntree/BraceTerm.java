package formulaParser.formulaAbsyntree;
import pipe.dataLayer.abToken;
import formulaParser.Visitor;

public class BraceTerm extends SetExp{
	public abToken abTok;
	public Term t;
	public String placeName;
	public String varName;
	public boolean isUserVariable = false;
	//z3
	public String z3str = "";
	
	public BraceTerm(int p, Term t){
		this.pos = p;
		this.t = t;
	}
	public void accept(Visitor v){
		v.visit(this);
	}
}
