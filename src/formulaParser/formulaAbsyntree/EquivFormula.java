package formulaParser.formulaAbsyntree;
import formulaParser.Visitor;
public class EquivFormula extends CompoundFormula{
	
	public Formula f1,f2;
	public boolean bool_val;
	
	public EquivFormula(int p, Formula ef1, Formula ef2){
		pos = p;
		f1=ef1;
		f2=ef2;
	}
	public void accept(Visitor v){
		v.visit(this);
	}
}
