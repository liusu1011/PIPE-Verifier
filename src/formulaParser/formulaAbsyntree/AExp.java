package formulaParser.formulaAbsyntree;

import formulaParser.Visitor;

public class AExp extends Exp{
	public ArithExp ae;
	public int int_val;
	
	//z3
	public String z3str = "";
	
	public AExp(int p, ArithExp ae){
		this.pos = p;
		this.ae = ae;
	}
	
	public void accept(Visitor v){
		v.visit(this);
	}
}
