package formulaParser.formulaAbsyntree;

import formulaParser.Visitor;

public class AtomicTerm extends AtomicFormula{
	public boolean bool_val;
	public Term t;
	public String strPre = "";
	public String strPost = "";
	
	//z3
	public boolean isPostCond;
	public boolean isValidClause;
	public String z3str = "";
	
	public AtomicTerm(int p, Term t){
		this.pos = p;
		this.t = t;
	}
	
	public void accept(Visitor v){
		v.visit(this);
	}
}
