package formulaParser.formulaAbsyntree;
import formulaParser.Visitor;
public class LtRel extends RelExp{
	public Term t1, t2;
	public boolean bool_val;
	public String strPre;
	public LtRel(int p, Term t1, Term t2){
		this.pos = p;
		this.t1 = t1;
		this.t2 = t2;
	}
	public void accept(Visitor v){
		v.visit(this);
	}
}
