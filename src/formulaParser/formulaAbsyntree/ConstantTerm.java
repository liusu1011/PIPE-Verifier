package formulaParser.formulaAbsyntree;

import formulaParser.Visitor;

public class ConstantTerm extends Term{
	public Constant c;
	public boolean bool_val;
	public int int_val;
	public String str_val;
	public String var_key;
//	public int kind;//0 is boo_val, 1 is int_val, 2 is str_val;
	
	//z3
	public String z3str = "";
	public ConstantTerm(int p, Constant c){
		this.pos = p;
		this.c = c;
	}
	public void accept(Visitor v){
		v.visit(this);
	}

}
