package formulaParser;

import java.util.ArrayList;
import java.util.HashSet;

import formulaParser.formulaAbsyntree.AExp;
import formulaParser.formulaAbsyntree.AndFormula;
import formulaParser.formulaAbsyntree.AtFormula;
import formulaParser.formulaAbsyntree.AtomicTerm;
import formulaParser.formulaAbsyntree.BraceTerm;
import formulaParser.formulaAbsyntree.BraceTerms;
import formulaParser.formulaAbsyntree.ComplexFormula;
import formulaParser.formulaAbsyntree.ConstantTerm;
import formulaParser.formulaAbsyntree.CpFormula;
import formulaParser.formulaAbsyntree.CpxFormula;
import formulaParser.formulaAbsyntree.Diff;
import formulaParser.formulaAbsyntree.Div;
import formulaParser.formulaAbsyntree.Empty;
import formulaParser.formulaAbsyntree.EmptyTerm;
import formulaParser.formulaAbsyntree.EqRel;
import formulaParser.formulaAbsyntree.EquivFormula;
import formulaParser.formulaAbsyntree.Exists;
import formulaParser.formulaAbsyntree.ExpTerm;
import formulaParser.formulaAbsyntree.False;
import formulaParser.formulaAbsyntree.ForAll;
import formulaParser.formulaAbsyntree.GeqRel;
import formulaParser.formulaAbsyntree.GtRel;
import formulaParser.formulaAbsyntree.IdVariable;
import formulaParser.formulaAbsyntree.Identifier;
import formulaParser.formulaAbsyntree.ImpFormula;
import formulaParser.formulaAbsyntree.In;
import formulaParser.formulaAbsyntree.InRel;
import formulaParser.formulaAbsyntree.Index;
import formulaParser.formulaAbsyntree.IndexVariable;
import formulaParser.formulaAbsyntree.LeqRel;
import formulaParser.formulaAbsyntree.LtRel;
import formulaParser.formulaAbsyntree.Minus;
import formulaParser.formulaAbsyntree.Mod;
import formulaParser.formulaAbsyntree.Mul;
import formulaParser.formulaAbsyntree.NegExp;
import formulaParser.formulaAbsyntree.NeqRel;
import formulaParser.formulaAbsyntree.Nexists;
import formulaParser.formulaAbsyntree.Nin;
import formulaParser.formulaAbsyntree.NinRel;
import formulaParser.formulaAbsyntree.NotFormula;
import formulaParser.formulaAbsyntree.Num;
import formulaParser.formulaAbsyntree.NumConstant;
import formulaParser.formulaAbsyntree.OrFormula;
import formulaParser.formulaAbsyntree.Plus;
import formulaParser.formulaAbsyntree.RExp;
import formulaParser.formulaAbsyntree.SExp;
import formulaParser.formulaAbsyntree.Sentence;
import formulaParser.formulaAbsyntree.StrConstant;
import formulaParser.formulaAbsyntree.TermRest;
import formulaParser.formulaAbsyntree.Terms;
import formulaParser.formulaAbsyntree.True;
import formulaParser.formulaAbsyntree.Union;
import formulaParser.formulaAbsyntree.UserVariable;
import formulaParser.formulaAbsyntree.VariableTerm;
import pipe.dataLayer.Arc;
import pipe.dataLayer.BasicType;
import pipe.dataLayer.DataType;
import pipe.dataLayer.Token;
import pipe.dataLayer.Transition;
import pipe.dataLayer.abToken;

public class SyntaxTreeCrawler implements Visitor{
	Transition iTransition;
	public ArrayList<String> undefVars; //record all vars in formula that has not yet defined in arc var
	private HashSet<String> definedVars;
	boolean debug = false;
	public SyntaxTreeCrawler(Transition transition){
		iTransition = transition;
		undefVars = new ArrayList<String>();
		definedVars = new HashSet<String>();
		searchDefinedVars();
	}
	
	/**
	 * add defined vars to definedVars set for checking whether vars in formula are defined by arc vars
	 */
	private void searchDefinedVars()
	{
		for(Arc a:this.iTransition.getArcList())
		{
			String[] vars = a.getVars();
			for(int i=0;i<vars.length;i++)
			{
				definedVars.add(vars[i].trim());
			}
		}
	}
	
	public void visit(AndFormula elem) {
		if(debug)System.out.println("AndFormula");
		
		elem.f1.accept(this);
		elem.f2.accept(this);
		

	}

	@Override
	public void visit(BraceTerm elem) {
		if(debug)System.out.println("BraceTerm");
		elem.t.accept(this);

	}

	@Override
	public void visit(BraceTerms elem) {
		if(debug)System.out.println("BraceTerms");
		elem.ts.accept(this);
		

	}

	@Override
	public void visit(ComplexFormula elem) {
		if(debug)System.out.println("ComplexFormula");
		elem.q.accept(this);
		elem.uv.accept(this);
		elem.d.accept(this);
		elem.v.accept(this);
	}

	@Override
	public void visit(Diff elem) {
		if(debug)System.out.println("Diff");
		elem.t1.accept(this);
		elem.t2.accept(this);


		}

	@Override
	public void visit(Div elem) {
		if(debug)System.out.println("Div");
		elem.t1.accept(this);
		elem.t2.accept(this);
		}

	@Override
	public void visit(EqRel elem) {
		if(debug)System.out.println("EqRel");
		elem.t1.accept(this);
		elem.t2.accept(this);
		}

	@Override
	public void visit(EquivFormula elem) {
		if(debug)System.out.println("EquivFormula");
		elem.f1.accept(this);
		elem.f2.accept(this);
		
	
	}

	@Override
	public void visit(Exists elem) {
		if(debug)System.out.println("Exists");
	}

	@Override
	public void visit(False elem) {
		if(debug)System.out.println("False");
		elem.bool_val = false;
	}

	@Override
	public void visit(ForAll elem) {
		if(debug)System.out.println("ForAll");
//		elem.quant_type = 0;
	}

	@Override
	public void visit(GeqRel elem) {
		if(debug)System.out.println("GeqRel");
		elem.t1.accept(this);
		elem.t2.accept(this);
		
	}

	@Override
	public void visit(GtRel elem) {
		if(debug)System.out.println("GtRel");
		elem.t1.accept(this);
		elem.t2.accept(this);
	}

	@Override
	public void visit(Identifier elem) {
		if(debug)System.out.println("Identifier");

	}

	@Override
	public void visit(IdVariable elem) {
		if(debug)System.out.println("IdVariable");
	}

	@Override
	public void visit(ImpFormula elem) {
		if(debug)System.out.println("ImpFormula");
		elem.f1.accept(this);
		elem.f2.accept(this);
	}

	@Override
	public void visit(In elem) {
		if(debug)System.out.println("In");
		elem.domain_type = 0;
	}

	@Override
	public void visit(Index elem) {
		if(debug)System.out.println("Index");
		elem.n.accept(this);
		elem.int_val = Integer.parseInt(elem.n.n);
	}

	@Override
	public void visit(IndexVariable elem) {
		if(debug)System.out.println("IndexVariable");
		elem.i.accept(this);
		elem.idx.accept(this);
		
		elem.key = elem.i.key;
		elem.index = elem.idx.int_val;
	}

	@Override
	public void visit(InRel elem) {
		if(debug)System.out.println("InRel");

		elem.t1.accept(this);
		elem.t2.accept(this);
		

	}

	@Override
	public void visit(LeqRel elem) {
		if(debug)System.out.println("LeqRel");
		elem.t1.accept(this);
		elem.t2.accept(this);
		
		
	}

	@Override
	public void visit(LtRel elem) {
		if(debug)System.out.println("LtRel");
		elem.t1.accept(this);
		elem.t2.accept(this);
	
	}

	@Override
	public void visit(Minus elem) {
		if(debug)System.out.println("Minus");
		elem.t1.accept(this);
		elem.t2.accept(this);
		
	}

	@Override
	public void visit(Mod elem) {
		if(debug)System.out.println("Mod");
		elem.t1.accept(this);
		elem.t2.accept(this);
		
		
	}

	@Override
	public void visit(Mul elem) {
		if(debug)System.out.println("Mul");
		elem.t1.accept(this);
		elem.t2.accept(this);
		
	}


	public void visit(NegExp elem) {
		if(debug)System.out.println("NegExp");
		elem.t.accept(this);
		
	}

	@Override
	public void visit(NeqRel elem) {
		if(debug)System.out.println("NeqRel");
		elem.t1.accept(this);
		elem.t2.accept(this);
		
		
	}

	@Override
	public void visit(Nexists elem) {
//		elem.quant_type = 2;
		if(debug)System.out.println("Nexists");
	}

	@Override
	public void visit(Nin elem) {
		elem.domain_type = 1;
		if(debug)System.out.println("Nin elem");
	}

	@Override
	public void visit(NinRel elem) {
		if(debug)System.out.println("NinRel");
		elem.t1.accept(this);
		elem.t2.accept(this);
		
		
	}

	@Override
	public void visit(NotFormula elem) {
		if(debug)System.out.println("NotFormula");
		elem.f.accept(this);
		
		if(elem.f instanceof AtFormula){
			if(((AtFormula)(elem.f)).bool_val == true){
				elem.bool_val = false;
			}else elem.bool_val = true;
		}else if(elem.f instanceof CpFormula){
			if(((CpFormula)(elem.f)).bool_val == true){
				elem.bool_val = false;
			}else elem.bool_val = true;
		}else if(elem.f instanceof CpxFormula){
			if(((CpxFormula)(elem.f)).bool_val == true){
				elem.bool_val = false;
			}else elem.bool_val = true;
		}
		
	}

	@Override
	public void visit(NumConstant elem) {
		if(debug)System.out.println("NumConstant");
		elem.num.accept(this);
		elem.int_val = Integer.parseInt(elem.num.n);
	}

	@Override
	public void visit(Num elem) {
		if(debug)System.out.println("Num");
		//elem.d = Double.parseDouble(elem.n);
	}

	@Override
	public void visit(OrFormula elem) {
		if(debug)System.out.println("OrFormula");
		elem.f1.accept(this);
		elem.f2.accept(this);
		
	}

	@Override
	public void visit(Plus elem) {
		if(debug)System.out.println("Plus");
		elem.t1.accept(this);
		elem.t2.accept(this);

		
	}

	@Override
	public void visit(TermRest elem) {
		// TODO Auto-generated method stub
		if(debug)System.out.println("TermRest");
		elem.t.accept(this);
	}

	@Override
	public void visit(Terms elem) {
		// TODO Auto-generated method stub
		if(debug)System.out.println("Terms");
		elem.t.accept(this);
		
		
	}

	@Override
	public void visit(True elem) {
		if(debug)System.out.println("True");
		elem.bool_val = true;
	}

	@Override
	
	public void visit(Union elem) {
		if(debug)System.out.println("Union");
		elem.t1.accept(this);
		elem.t2.accept(this);

		
	}

	@Override
	public void visit(UserVariable elem) {
		if(debug)System.out.println("UserVariable");
	}

	@Override
	public void visit(ConstantTerm elem) {
		if(debug)System.out.println("ConstantTerm");
		elem.c.accept(this);
		
		
	}

	@Override
	public void visit(ExpTerm elem) {
		if(debug)System.out.println("ExpTerm");
		elem.e.accept(this);
		
		if(elem.e instanceof AExp){
			elem.int_val = ((AExp)(elem.e)).int_val;
		}else if(elem.e instanceof RExp){
			elem.bool_val = ((RExp)(elem.e)).bool_val;
		}else if(elem.e instanceof SExp){
			elem.abTok = ((SExp)(elem.e)).abTok;
		}
	}

	@Override
	public void visit(VariableTerm elem) {
		if(debug)System.out.println("VariableTerm");
		elem.v.accept(this);
		if(elem.v instanceof IdVariable){
			elem.var_key = ((IdVariable)elem.v).key;
			elem.kind = 0;
		}else if(elem.v instanceof IndexVariable){
			elem.var_key = ((IndexVariable)elem.v).key;
			elem.index = ((IndexVariable)elem.v).index;
			elem.kind = 1;
		}
		if(!definedVars.contains(elem.var_key))
		{
			this.undefVars.add(elem.var_key);
		}
	}
	
	@Override
	public void visit(StrConstant elem) {
		if(debug)System.out.println("StrConstant");
	}
	
	public void visit(AExp elem){
		if(debug)System.out.println("AExp");
		elem.ae.accept(this);
		
		
	}
	
	public void visit(RExp elem){
		if(debug)System.out.println("RExp");
		elem.re.accept(this);
		
		
	}
	
	public void visit(SExp elem){
		if(debug)System.out.println("SExp");
		elem.se.accept(this);
		
		
	}

	@Override
	public void visit(AtomicTerm elem) {
		if(debug)System.out.println("AtomicTerm");
		elem.t.accept(this);
		
	}

	@Override
	public void visit(AtFormula elem) {
		if(debug)System.out.println("AtFormula");
		elem.af.accept(this);
		
	}

	@Override
	public void visit(CpFormula elem) {
		if(debug)System.out.println("CpFormula");
		elem.cf.accept(this);
			
	}

	@Override
	public void visit(CpxFormula elem) {
		if(debug)System.out.println("CpxFormula");
		elem.cpf.accept(this);
		
	}

	@Override
	public void visit(Sentence elem) {
		if(debug)System.out.println("Sentence");
		elem.f.accept(this);
		
	}
	@Override
	public void visit(Empty elem) {
		if(debug)System.out.println("Empty");
	}
	@Override
	public void visit(EmptyTerm elem) {
		if(debug)System.out.println("EmptyTerm");
		elem.e.accept(this);
		
	}
}
