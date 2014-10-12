package formulaParser.formulaAbsyntree;
import java.util.ArrayList;

import pipe.dataLayer.abToken;
import formulaParser.Visitor;
public class BraceTerms extends SetExp{
	public abToken abTok;
	public Terms ts;
	public String placeName;
	//z3
	public ArrayList<String> z3TermList = new ArrayList<String>();
	
	public BraceTerms(int p, Terms ts){
		this.pos = p;
		this.ts = ts;
	}
	public void accept(Visitor v){
		v.visit(this);
	}

}
