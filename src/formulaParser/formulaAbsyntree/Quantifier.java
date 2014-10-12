package formulaParser.formulaAbsyntree;
import formulaParser.Visitor;
public abstract class Quantifier {
	public int pos;
	
	public abstract void accept(Visitor v);
}
