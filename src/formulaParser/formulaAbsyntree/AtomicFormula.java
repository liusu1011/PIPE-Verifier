package formulaParser.formulaAbsyntree;
import formulaParser.Visitor;

public abstract class AtomicFormula extends Formula{
	public int pos;
	public String strPre = "";
	public String strPost = "";
	public int treeLevel;
	public abstract void accept(Visitor v);
}
