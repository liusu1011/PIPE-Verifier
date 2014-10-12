package formulaParser.formulaAbsyntree;
import formulaParser.Visitor;
public abstract class Term {
	public int pos;
	public String str = "";
	public boolean firstField = false;
	public String placeName; //in case firstField is true
	public abstract void accept(Visitor v);
}
