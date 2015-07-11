package formulaParser.formulaAbsyntree;

import formulaParser.Visitor;
public abstract class Formula {
	public int pos;
	public String z3str;
	public String maude = "";
	public abstract void accept(Visitor v);
}
