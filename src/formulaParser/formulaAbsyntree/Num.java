package formulaParser.formulaAbsyntree;
import formulaParser.Visitor;
public class Num {
	public String n;
	public double d;
	public Num(String n){
		this.n = n;
	}
	public void accept(Visitor v){
		v.visit(this);
	}
}
