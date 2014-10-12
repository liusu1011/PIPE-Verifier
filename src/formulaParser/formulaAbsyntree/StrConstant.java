package formulaParser.formulaAbsyntree;
import formulaParser.Visitor;

public class StrConstant  extends Constant{
	public String str;
	public StrConstant(int p, String str){
		this.pos = p;
		this.str = str;
	}
	public void accept(Visitor v){
		v.visit(this);
	}
}
