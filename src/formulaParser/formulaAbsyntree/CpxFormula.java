package formulaParser.formulaAbsyntree;

import formulaParser.Visitor;

public class CpxFormula extends Formula{

	public ComplexFormula cpf;
	public boolean bool_val;
	public String strPre = "";
	public String strPost = "";
	public int treeLevel; // start from 1
	public int complexLevel; // start from 0, maximum supported is 2
	
	public CpxFormula(int p, ComplexFormula cpf){
		this.pos = p;
		this.cpf = cpf;
	}
	
	public void accept(Visitor v){
		v.visit(this);
	}

}
