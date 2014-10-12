package formulaParser.formulaAbsyntree;
import formulaParser.Visitor;

public class AndFormula extends CompoundFormula{
	public Formula f1,f2;
	public boolean bool_val;

	public AndFormula(int p, Formula af1, Formula af2){
		this.pos = p;
		f1=af1;
		f2=af2;
	}

	public void accept(Visitor v){
		v.visit(this);
	}
}
