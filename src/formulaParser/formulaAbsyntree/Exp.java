package formulaParser.formulaAbsyntree;
import formulaParser.Visitor;
public abstract class Exp extends Term{
	public int pos;
	public abstract void accept(Visitor v);
	public String placeName;
	//z3
	public String z3str;
}
