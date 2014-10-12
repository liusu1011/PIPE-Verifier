package formulaParser.formulaAbsyntree;

import formulaParser.Visitor;

public class VariableTerm extends Term{
	public Variable v;
	public String var_key;
	public int index = 0;
//	public int int_val;
//	public String str_val;
	public int kind; //0 is id_variabe; 1 is index_variable
	public boolean postcond = false;
	public boolean isPowerSet = false;
	public boolean isUserVariable = false;
	public int size;
	public String pVarName;
	
	//z3
	public String z3str = "";
//	public String placeName = "";
	public boolean isPostCond;
	
	public VariableTerm(int p, Variable v){
		this.pos = p;
		this.v = v;
	}
	public void accept(Visitor v){
		v.visit(this);
	}
	
}
