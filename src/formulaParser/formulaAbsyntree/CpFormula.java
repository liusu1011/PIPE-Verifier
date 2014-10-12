package formulaParser.formulaAbsyntree;

import formulaParser.Visitor;

public class CpFormula extends Formula{

	public CompoundFormula cf;
	public boolean bool_val;
	public String strPre = "";
	public String strPost = "";
	public int treeLevel;
	
	public CpFormula(int p, CompoundFormula cf){
		this.pos = p;
		this.cf = cf;
	}
	
	public void accept(Visitor v){
		v.visit(this);
	}

}
