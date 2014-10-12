package formulaParser.formulaAbsyntree;

import formulaParser.Visitor;

public class RExp extends Exp{
	public RelExp re;
	public boolean bool_val;
	public String strPre = "";
	public String strPost = "";
	
	//z3
	public String z3str = "";
	public boolean isPostCond = false;
	public boolean isValidClause;
	
	public RExp(int p, RelExp re){
		this.pos = p;
		this.re = re;
	}
	
	public void accept(Visitor v){
		v.visit(this);
	}
}
