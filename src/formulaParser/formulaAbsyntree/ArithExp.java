package formulaParser.formulaAbsyntree;
import formulaParser.Visitor;

public abstract class ArithExp extends Exp{
	public abstract void accept(Visitor v);
}
