package formulaParser.formulaAbsyntree;
import formulaParser.Visitor;
import java.util.Vector;

public class TermRestList {
	public Vector<TermRest> list;
	
	public TermRestList(){
		list = new Vector<TermRest>();
	}
	
	public void addElement(TermRest tr){
		list.addElement(tr);
	}
	
	public TermRest elementAt(int i){
		return list.elementAt(i);
	}
	   
	public int size() { 
      return list.size(); 
    }
}
