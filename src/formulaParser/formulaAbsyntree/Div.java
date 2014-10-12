package formulaParser.formulaAbsyntree;
import formulaParser.Visitor;

public class Div extends ArithExp{
	public int int_val;
	public Term t1, t2;
	
	//z3
	public String z3str = "";
	public Div(int p, Term t1, Term t2){
		this.pos = p;
		this.t1 = t1;
		this.t2 = t2;
	}
	public void accept(Visitor v){
		v.visit(this);
	}
}
