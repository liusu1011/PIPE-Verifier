package formulaParser.formulaAbsyntree;
import pipe.dataLayer.abToken;
import formulaParser.Visitor;
public abstract class SetExp extends Exp{
	
	public abstract void accept(Visitor v);
}
