package formulaParser.formulaAbsyntree;
import formulaParser.Visitor;
public class NotFormula extends AtomicFormula{
	public Formula f;
	public boolean bool_val;
	public String str = "";
	
	public NotFormula(int p, Formula f){
		pos = p;
		this.f  = f;
	}
	public void accept(Visitor v){
		v.visit(this);
	}
}
