package formulaParser.formulaAbsyntree;
import formulaParser.Visitor;
public class In extends Domain{

	public In(int p){
		this.pos = p;
	}
	public void accept(Visitor v){
		v.visit(this);
	}
}
