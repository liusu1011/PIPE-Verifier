package formulaParser.formulaAbsyntree;
import formulaParser.Visitor;
public abstract class Constant{
	public int pos;
	public abstract void accept(Visitor v);
}
