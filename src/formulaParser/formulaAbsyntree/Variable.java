package formulaParser.formulaAbsyntree;
import formulaParser.Visitor;
public abstract class Variable{
	public int pos;
	public String maude = "";
	public abstract void accept(Visitor v);
}
