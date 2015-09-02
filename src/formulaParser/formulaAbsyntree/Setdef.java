package formulaParser.formulaAbsyntree;
import pipe.dataLayer.abToken;
import formulaParser.Visitor;
public class Setdef extends SetExp{
	public abToken abTok;
	public UserVariable u;
	public Formula sf;
	
	//z3
	public String z3str = "";
	public boolean isPostCond = false;
	
	public Setdef(int p, UserVariable u, Formula sf){
		this.pos = p;
		this.u = u;
		this.sf = sf;
	}
	public void accept(Visitor v){
		v.visit(this);
	}
}
