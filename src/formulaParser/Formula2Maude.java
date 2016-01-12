package formulaParser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import pipe.dataLayer.Arc;
import pipe.dataLayer.BasicType;
import pipe.dataLayer.DataType;
import pipe.dataLayer.Place;
import pipe.dataLayer.Token;
import pipe.dataLayer.Transition;
import pipe.dataLayer.abToken;
import formulaParser.formulaAbsyntree.*;
import formulaParser.ErrorMsg;

public class Formula2Maude implements Visitor{

	ErrorMsg errorMsg;
	SymbolTable symTable;
	Transition iTransition;
	int mode = 0;
	
	
	public Formula2Maude(ErrorMsg errorMsg, Transition transition, int mode){
		this.errorMsg = errorMsg;
		iTransition = transition;
		this.symTable = iTransition.getTransSymbolTable();
		this.mode = mode;
	}
	
	@Override
	public void visit(AndFormula elem) {
		
		elem.f1.accept(this);
		elem.f2.accept(this);
		if (elem.f2.maude.isEmpty())
			elem.maude = elem.f1.maude;
		else
			elem.maude = elem.f1.maude + " /\\ " + elem.f2.maude;

		
	}

	@Override
	public void visit(BraceTerm elem) {
		elem.t.accept(this);
		

	}

	@Override
	public void visit(BraceTerms elem) {
		elem.ts.accept(this);
	}

	@Override
	public void visit(ComplexFormula elem) {
		
		elem.q.accept(this);
		elem.uv.accept(this);
		elem.d.accept(this);
		elem.v.accept(this);

	}

	@Override
	public void visit(Diff elem) {
		elem.t1.accept(this);
		elem.t2.accept(this);
	}

	@Override
	public void visit(Div elem) {
		elem.t1.accept(this);
		elem.t2.accept(this);
		
	}

	@Override
	public void visit(EqRel elem) {
		elem.t1.accept(this);
		elem.t2.accept(this);
		
		boolean postcond = false;
		
		if (elem.t1 instanceof VariableTerm)
		{
			postcond = ((VariableTerm)(elem.t1)).postcond;
		}
		
		if (!elem.t1.maude.isEmpty() && !elem.t2.maude.isEmpty())
		{
			if (!postcond)
				elem.maude = elem.t1.maude + " = " + elem.t2.maude;
			else
				elem.maude = elem.t1.maude + " := " + elem.t2.maude;
		}
			
	}

	@Override
	public void visit(EquivFormula elem) {
		elem.f1.accept(this);
		elem.f2.accept(this);
		
	}

	@Override
	public void visit(Exists elem) {

	}

	@Override
	public void visit(False elem) {

	}

	@Override
	public void visit(ForAll elem) {

	}

	@Override
	public void visit(GeqRel elem) {
		elem.t1.accept(this);
		elem.t2.accept(this);
		
		if (!elem.t1.maude.isEmpty() && !elem.t2.maude.isEmpty())
			elem.maude = elem.t1.maude + " >= " + elem.t2.maude;
				
		
	}

	@Override
	public void visit(GtRel elem) {
		elem.t1.accept(this);
		elem.t2.accept(this);
		
		if (!elem.t1.maude.isEmpty() && !elem.t2.maude.isEmpty())
			elem.maude = elem.t1.maude + " > " + elem.t2.maude;
	}

	@Override
	public void visit(Identifier elem) {

	}

	@Override
	public void visit(IdVariable elem) {
		elem.maude = elem.key + "_f1";
	}

	@Override
	public void visit(ImpFormula elem) {
		elem.f1.accept(this);
		elem.f2.accept(this);

	
	}

	@Override
	public void visit(In elem) {

	}

	@Override
	public void visit(Index elem) {
		elem.n.accept(this);
	}

	@Override
	public void visit(IndexVariable elem) {
		elem.i.accept(this);
		elem.idx.accept(this);
		
		elem.maude = elem.i.key + "_f" + elem.idx.int_val;
	}

	@Override
	public void visit(InRel elem) {

		elem.t1.accept(this);
		elem.t2.accept(this);
		 
	}

	@Override
	public void visit(LeqRel elem) {
		elem.t1.accept(this);
		elem.t2.accept(this);

		if (!elem.t1.maude.isEmpty() && !elem.t2.maude.isEmpty())
			elem.maude = elem.t1.maude + " <= " + elem.t2.maude;
	}

	@Override
	public void visit(LtRel elem) {
		elem.t1.accept(this);
		elem.t2.accept(this);
		
		if (!elem.t1.maude.isEmpty() && !elem.t2.maude.isEmpty())
			elem.maude = elem.t1.maude + " < " + elem.t2.maude;

	}

	@Override
	public void visit(Minus elem) {
		elem.t1.accept(this);
		elem.t2.accept(this);
		
		elem.maude = elem.t1.maude + " - " + elem.t2.maude;

	}

	@Override
	public void visit(Mod elem) {
		elem.t1.accept(this);
		elem.t2.accept(this);
		
		elem.maude = elem.t1.maude + " rem " + elem.t2.maude;
	}

	@Override
	public void visit(Mul elem) {
		elem.t1.accept(this);
		elem.t2.accept(this);
		
	}


	public void visit(NegExp elem) {
		elem.t.accept(this);
	}

	@Override
	public void visit(NeqRel elem) {
		elem.t1.accept(this);
		elem.t2.accept(this);
		
		if (!elem.t1.maude.isEmpty() && !elem.t2.maude.isEmpty())
			elem.maude = elem.t1.maude + " =/= " + elem.t2.maude;
		
	}

	@Override
	public void visit(Nexists elem) {

	}

	@Override
	public void visit(Nin elem) {

	}

	@Override
	public void visit(NinRel elem) {
		elem.t1.accept(this);
		elem.t2.accept(this);
	}



	@Override
	public void visit(NumConstant elem) {
		elem.num.accept(this);
	}

	@Override
	public void visit(Num elem) {
		
	}

	@Override
	public void visit(OrFormula elem) {
		elem.f1.accept(this);
		elem.f2.accept(this);
		
	}

	@Override
	public void visit(Plus elem) {
		elem.t1.accept(this);
		elem.t2.accept(this);
		
		
		elem.maude = elem.t1.maude + " + " + elem.t2.maude ;
		
	}

	@Override
	public void visit(TermRest elem) {
		elem.t.accept(this);
		elem.maude = elem.t.maude;
	}

	@Override
	public void visit(Terms elem) {
		elem.t.accept(this);
		elem.maude = elem.t.maude;
		
	}

	@Override
	public void visit(True elem) {

	}

	@Override
	public void visit(Union elem) {
		elem.t1.accept(this);
		elem.t2.accept(this);

	}

	@Override
	public void visit(UserVariable elem) {

	}

	@Override
	public void visit(ConstantTerm elem) {
		elem.c.accept(this);
		if (elem.c instanceof NumConstant)
		{
			elem.maude = ((NumConstant)(elem.c)).num.n;
			elem.var_key = ((NumConstant)(elem.c)).num.n;
		}
		else if (elem.c instanceof StrConstant)
		{
			elem.maude = "\"" + ((StrConstant)(elem.c)).str + "\"";
			elem.var_key = ((StrConstant)(elem.c)).str;
		}
		
	}

	@Override
	public void visit(ExpTerm elem) {
		elem.e.accept(this);
		elem.maude = elem.e.maude;
		
	}

	@Override
	public void visit(VariableTerm elem) {
		elem.v.accept(this);
		elem.maude = elem.v.maude;
		
		boolean isInArcOutVarList = false;
		String var_key = "";

		if (elem.v instanceof IdVariable) {
			var_key = ((IdVariable) elem.v).key;

			for (String s : iTransition.getArcOutVarList()) {
				if (s.equals(var_key) || (s.replace('{', ' ').replace('}', ' ').trim()+",").contains(var_key+","))
					isInArcOutVarList = true;
			}
		}

		if (elem.v instanceof IndexVariable) {
			var_key = ((IndexVariable) elem.v).key;
			for (String s : iTransition.getArcOutVarList()) {
				if ((s.replace('{', ' ').replace('}', ' ').trim()+",").contains(var_key+","))
					isInArcOutVarList = true;
			}
		}
		
		elem.postcond = isInArcOutVarList;
	}
	
	@Override
	public void visit(StrConstant elem) {
		
	}
	
	public void visit(AExp elem){
		elem.ae.accept(this);
		
		elem.maude = "(" + elem.ae.maude + ")";
	}
	
	public void visit(RExp elem){
		elem.re.accept(this);
		elem.maude = elem.re.maude;
	}
	
	public void visit(SExp elem){
		elem.se.accept(this);
		elem.maude = elem.se.maude;
	}

	@Override
	public void visit(AtomicTerm elem) {
		elem.t.accept(this);
		
		elem.maude = elem.t.maude;
	}
	
	@Override
	public void visit(NotFormula elem) {
		elem.f.accept(this);
		System.out.println("ERROR: Formula2Promela: NotFormula not implemented.");
		
	}

	@Override
	public void visit(AtFormula elem) {
		elem.af.treeLevel = elem.treeLevel;
		elem.af.accept(this);
		

		elem.maude = elem.af.maude;
	}



	@Override
	public void visit(CpFormula elem) {
		
		elem.cf.treeLevel = elem.treeLevel;
		elem.cf.accept(this);
		
		elem.maude = elem.cf.maude;
		
	}

	@Override
	public void visit(CpxFormula elem) {
		elem.cpf.treeLevel = elem.treeLevel;
		elem.cpf.accept(this);
		
	}

	@Override
	public void visit(Sentence elem) {
		
		elem.f.accept(this);
		elem.maude = elem.f.maude;
	}

	@Override
	public void visit(Empty elem) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(EmptyTerm elem) {
		// TODO Auto-generated method stub
		
	}
	

}
