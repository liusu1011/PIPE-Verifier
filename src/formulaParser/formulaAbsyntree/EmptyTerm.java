package formulaParser.formulaAbsyntree;

import formulaParser.Visitor;

public class EmptyTerm extends Term{
	public Empty e;
	
	public EmptyTerm(int p, Empty e){
		this.pos = p;
		this.e = e;
	}
	public void accept(Visitor v){
		v.visit(this);
	}

}
