package formulaParser.formulaAbsyntree;
import formulaParser.Visitor;

public class IdVariable extends Variable{
		public String key;
	 
	  public IdVariable(int p, String as) { 
	    pos=p; key=as;
	  }
	  
	  
		public void accept(Visitor v){
			v.visit(this);
		}
}
