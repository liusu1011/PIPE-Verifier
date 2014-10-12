package formulaParser.formulaAbsyntree;
import formulaParser.Visitor;

public class False extends Constant{
public boolean bool_val;
	public False(int p){
		this.pos = p;
	}
	public void accept(Visitor v){
		v.visit(this);
	}
}
