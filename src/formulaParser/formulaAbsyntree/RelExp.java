package formulaParser.formulaAbsyntree;
import formulaParser.Visitor;
public abstract class RelExp extends Exp{
	
	public abstract void accept(Visitor v);
}
