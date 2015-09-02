package formulaParser;

import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JOptionPane;

import pipe.dataLayer.Arc;
import pipe.dataLayer.BasicType;
import pipe.dataLayer.DataType;
import pipe.dataLayer.Place;
import pipe.dataLayer.Token;
import pipe.dataLayer.Transition;
import pipe.dataLayer.abToken;
import pipe.gui.CreateGui;
import formulaParser.formulaAbsyntree.*;
import formulaParser.ErrorMsg;

/**
 * Simulation Interpreter
 * @author su-home
 *
 */
public class Interpreter implements Visitor{

	ErrorMsg errorMsg;
	SymbolTable symTable;
	Transition iTransition;
	int mode = 0;//when mode is 0, means interpreter just check pre-condition(Check Enable)
				//when mode is 1, means interpreter is processing post-condition(Fire)
	public ArrayList<String> undefVars; //record all vars in formula that has not yet defined in arc var
	private HashSet<String> definedVars;
	boolean debug = false;
	public Interpreter(ErrorMsg errorMsg, Transition transition, int mode){
		this.errorMsg = errorMsg;
		iTransition = transition;
		this.symTable = iTransition.getTransSymbolTable();
		this.mode = mode;
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
				definedVars.add(vars[i]);
			}
		}
	}
	
	@Override
	public void visit(AndFormula elem) {
		if(debug)System.out.println("AndFormula");
		
		elem.f1.accept(this);
		elem.f2.accept(this);
		
		boolean temp_f1 = false;
		boolean temp_f2 = false;
		
		if(elem.f1 instanceof AtFormula){
			temp_f1 = ((AtFormula)(elem.f1)).bool_val;
		}else if(elem.f1 instanceof CpFormula){
			temp_f1 = ((CpFormula)(elem.f1)).bool_val;
		}else if(elem.f1 instanceof CpxFormula){
			temp_f1 = ((CpxFormula)(elem.f1)).bool_val;
		}else errorMsg.error(elem.pos, "AndFormula::LHS Formula type mismatch!");
		
		if(elem.f2 instanceof AtFormula){
			temp_f2 = ((AtFormula)(elem.f2)).bool_val;
		}else if(elem.f2 instanceof CpFormula){
			temp_f2 = ((CpFormula)(elem.f2)).bool_val;
		}else if(elem.f2 instanceof CpxFormula){
			temp_f2 = ((CpxFormula)(elem.f2)).bool_val;
		}else errorMsg.error(elem.pos, "AndFormula::RHS Formula type mismatch!");		
		
		//compare
		if(temp_f1 && temp_f2){
			elem.bool_val = true;
		}else elem.bool_val = false;
	}

	@Override
	public void visit(BraceTerm elem) {
		if(debug)System.out.println("BraceTerm");
		elem.t.accept(this);
		
		System.out.println("Brace Term Visited"); //debug
		
		DataType resultType = null;
		//allocate space to a temp abToken to store result
		abToken resultTok = new abToken(resultType);
		if(elem.t instanceof VariableTerm)
		{   
			if(((VariableTerm)(elem.t)).v instanceof IdVariable)
			{
				if(symTable.lookup(((IdVariable)((VariableTerm)(elem.t)).v).key) instanceof Token){
					resultType = ((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t)).v).key))).getTokentype();
					resultTok.addToken(((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t)).v).key))));
				}
//				{term}, term should not be abToken
//				else if(symTable.lookup(((IdVariable)((VariableTerm)(elem.t)).v).key) instanceof abToken){
//					resultType = ((abToken)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t)).v).key))).getDataType();
//				}
			}else if(((VariableTerm)(elem.t)).v instanceof IndexVariable){
				if(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t)).v).key) instanceof Token){
					if(((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t)).v).key))).Tlist.elementAt(((VariableTerm)elem.t).index - 1).kind == 0){
						String[] str = {"int"};
						resultType = new DataType("intTok",str,true,null);
					}else if(((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t)).v).key))).Tlist.elementAt(((VariableTerm)elem.t).index - 1).kind == 1){
						String[] str = {"string"};
						resultType = new DataType("StrTok",str,true,null);
					}
					resultTok.addToken(((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t)).v).key))));
				}
			} 
		} else if((ConstantTerm) elem.t instanceof ConstantTerm)   //added by He - 8/5/15
		{   
			
			switch (((ConstantTerm) elem.t).kind) {
			case 0: 
						System.out.println("Wrong type");
						break;
			case 1:		String [] str2 = {"int"};
						resultType = new DataType("intTok",str2, true, null);
						Token tk = new Token(resultType);
						BasicType tval = new BasicType(0, ((ConstantTerm) elem.t).int_val, "");
						BasicType [] btval = {tval};
						if (tk.add(btval)) {
							resultTok.addToken((Token) tk);
						}
						break;
			case 2:     String [] str3 = {"string"};
						resultType = new DataType("StrTok",str3, true, null);
						Token tk1 = new Token(resultType);
						BasicType tval1 = new BasicType(1, 0, ((ConstantTerm) elem.t).str_val);
						BasicType [] btval1 = {tval1};
						if (tk1.add(btval1)) {
							resultTok.addToken((Token) tk1);
						}
						break;
			}
		}

		//changed by He - 8/5/15
		//if(((VariableTerm)(elem.t)).v instanceof IdVariable){
		//	if(symTable.lookup(((IdVariable)((VariableTerm)(elem.t)).v).key) instanceof Token){
		//		resultTok.addToken(((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t)).v).key))));
		//	}
		//}
		//assign the result token to absyntree
		elem.abTok = resultTok;
	}

	@Override
	public void visit(BraceTerms elem) {
		if(debug)System.out.println("BraceTerms");
		elem.ts.accept(this);
		
		DataType resultType = elem.ts.Tok.getTokentype();
		abToken resultTok = new abToken(resultType);
		resultTok.addToken(elem.ts.Tok);
		elem.abTok = resultTok;
	}

	@Override
	public void visit(ComplexFormula elem) {
		if(debug)System.out.println("ComplexFormula");
		elem.q.accept(this);
		elem.uv.accept(this);
		elem.d.accept(this);
		elem.v.accept(this);
		
		
		//changed by He - 8/3/15
		if(elem.q instanceof ForAll){
			elem.bool_val = true;
			
			for(Token t : ((abToken)(symTable.lookup(((IdVariable)(elem.v)).key))).listToken)
			{
				if(elem.bool_val == false)break;
				symTable.insert(elem.uv.s, t, 0);
				elem.f.accept(this);
				if(elem.f instanceof AtFormula)
				{
					if(((AtFormula)(elem.f)).bool_val == false)
					{
						elem.bool_val = false;
					}
				}else if(elem.f instanceof CpFormula)
				{
					if(((CpFormula)(elem.f)).bool_val == false)
					{
						elem.bool_val = false;
					}
				}else if(elem.f instanceof CpxFormula)
				{
					if(((CpxFormula)(elem.f)).bool_val == false)
					{
						elem.bool_val = false;
					}
				}
				//if (!elem.bool_val)   //Modified 8/7/15
				symTable.delete(elem.uv.s);
			}	
		}else if(elem.q instanceof Exists)
		{
			elem.bool_val = false;
			
			for(Token t : ((abToken)(symTable.lookup(((IdVariable)(elem.v)).key))).listToken)
			{
				if(elem.bool_val == true)break;
				if(symTable.exist(elem.uv.s))
				{
					symTable.update(elem.uv.s, t, 0);
				}else symTable.insert(elem.uv.s, t, 0);
		
				elem.f.accept(this);
				if(elem.f instanceof AtFormula)
				{
					if(((AtFormula)(elem.f)).bool_val == true){
						elem.bool_val = true;
					}
				}else if(elem.f instanceof CpFormula)
				{
					if(((CpFormula)(elem.f)).bool_val == true){
						elem.bool_val = true;
					}
				}else if(elem.f instanceof CpxFormula)
				{
					if(((CpxFormula)(elem.f)).bool_val == true){
						elem.bool_val = true;
					}
				}
				if (!elem.bool_val) symTable.delete(elem.uv.s);
			}
		}
			else if(elem.q instanceof Nexists){
			elem.bool_val = true;
			
			for(Token t : ((abToken)(symTable.lookup(((IdVariable)(elem.v)).key))).listToken)
			{
				if(elem.bool_val == false)break;
				
				symTable.insert(elem.uv.s, t, 0);
				elem.f.accept(this);
				if(elem.f instanceof AtFormula){
					if(((AtFormula)(elem.f)).bool_val == true){
						elem.bool_val = false;
					}
				}else if(elem.f instanceof CpFormula){
					if(((CpFormula)(elem.f)).bool_val == true){
						elem.bool_val = false;
					}
				}else if(elem.f instanceof CpxFormula){
					if(((CpxFormula)(elem.f)).bool_val == true){
						elem.bool_val = false;
					}
				}
				symTable.delete(elem.uv.s);
			}		
		}else errorMsg.error(elem.pos, "ComplexFormula::Quantifier type mismatch!"); 
	}

	@Override
	public void visit(Diff elem) {
		if(debug)System.out.println("Diff");
		elem.t1.accept(this);
		elem.t2.accept(this);

		DataType resultType = null;
				
		//find datatype of the result token;
		if(elem.t1 instanceof VariableTerm){
			if(((VariableTerm)(elem.t1)).v instanceof IdVariable){
				if(symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key) instanceof Token){
					resultType = ((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key))).getTokentype();
				}else if(symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key) instanceof abToken){
					resultType = ((abToken)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key))).getDataType();
				}
			}else if(((VariableTerm)(elem.t1)).v instanceof IndexVariable){
				if(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key) instanceof Token){
					if(((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.elementAt(((VariableTerm)elem.t1).index - 1).kind == 0){
						String[] str = {"int"};
						resultType = new DataType("intTok",str,true,null);
					}else if(((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.elementAt(((VariableTerm)elem.t1).index - 1).kind == 1){
						String[] str = {"string"};
						resultType = new DataType("StrTok",str,true,null);
					}
				}
			}
		}else if(elem.t1 instanceof ExpTerm){
			resultType = ((SExp)((ExpTerm)(elem.t1)).e).abTok.getDataType();
		}else errorMsg.error(elem.pos, "Union::Tree type mismatch!"); 
		
		//allocate space to a temp abToken to store result
			abToken resultTok = new abToken(resultType);
				
		//add left term to result token
		if(elem.t1 instanceof VariableTerm){
		if(((VariableTerm)(elem.t1)).v instanceof IdVariable){
			if(symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key) instanceof Token){
				resultTok.addToken(((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key))));
			}else if(symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key) instanceof abToken){
				for(Token t : ((abToken)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key))).listToken){
					resultTok.addToken(t);
				}
			}
		}else if(((VariableTerm)(elem.t1)).v instanceof IndexVariable){
			if(((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.elementAt(((VariableTerm)elem.t1).index - 1).kind == 0){
				Token temp_tok = new Token(resultType);
				temp_tok.Tlist.firstElement().Tint = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.elementAt(((VariableTerm)elem.t1).index - 1).Tint;
				resultTok.addToken(temp_tok);
			}else if(((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.elementAt(((VariableTerm)elem.t1).index - 1).kind == 1){
				Token temp_tok = new Token(resultType);
				temp_tok.Tlist.firstElement().Tstring = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.elementAt(((VariableTerm)elem.t1).index - 1).Tstring;
				resultTok.addToken(temp_tok);
			}
		}
		}else if(elem.t1 instanceof ExpTerm){
			if(((ExpTerm)(elem.t1)).e instanceof SExp){
				for(Token t : ((SExp)((ExpTerm)(elem.t1)).e).abTok.listToken){		
					resultTok.addToken(t);
				}
			}
		}
		
		//delete right term from result token
		if (elem.t2 instanceof VariableTerm){
		if(((VariableTerm)(elem.t2)).v instanceof IdVariable){
			if(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key) instanceof Token){
				resultTok.deleteToken(((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key))));
			}else if(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key) instanceof abToken){
				for(Token t : ((abToken)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key))).listToken){
					resultTok.deleteToken(t);
				}
			}
		}else if(((VariableTerm)(elem.t2)).v instanceof IndexVariable){
			if(((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.elementAt(((VariableTerm)elem.t2).index - 1).kind == 0){
				Token temp_tok = new Token(resultType);
				temp_tok.Tlist.firstElement().Tint = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.elementAt(((VariableTerm)elem.t2).index - 1).Tint;
				resultTok.deleteToken(temp_tok);
			}else if(((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.elementAt(((VariableTerm)elem.t2).index - 1).kind == 1){
				Token temp_tok = new Token(resultType);
				temp_tok.Tlist.firstElement().Tstring = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.elementAt(((VariableTerm)elem.t2).index - 1).Tstring;
				resultTok.deleteToken(temp_tok);
			}
		}
	}else if(elem.t2 instanceof ExpTerm){
		if(((ExpTerm)(elem.t2)).e instanceof SExp){
			for(Token t : ((SExp)((ExpTerm)(elem.t2)).e).abTok.listToken){
				resultTok.deleteToken(t);
			}
		}
	}
		//assign the result token to absyntree
		elem.abTok = resultTok;
	}

	//added He 8/17/15
	public void visit(Setdef elem){
		if(debug)System.out.println("Set Definition");
		elem.u.accept(this);
		elem.sf.accept(this);	
		
		DataType resultType = null;
				
		//find the datatype of the result token;
		if(((ComplexFormula) ((CpxFormula) elem.sf).cpf).v instanceof IdVariable){
			if(symTable.lookup(((IdVariable) ((ComplexFormula) ((CpxFormula) (elem.sf)).cpf).v).key) instanceof abToken){
				resultType = ((abToken) symTable.lookup(((IdVariable) ((ComplexFormula) ((CpxFormula) (elem.sf)).cpf).v).key)).getDataType();
			}else errorMsg.error(elem.pos, "SetDef::Tree type mismatch!"); 
		}
		
		//allocate space to a temp abToken to store result
			abToken resultTok = new abToken(resultType);
		//find and add tokens
		if(elem.sf instanceof CpxFormula){
			//if (elem.u.s.equals(((ComplexFormula) ((CpxFormula) elem.sf).cpf).uv.s)) {
				if(((ComplexFormula) ((CpxFormula) elem.sf).cpf).v instanceof IdVariable){
					if(symTable.lookup(((IdVariable) ((ComplexFormula) ((CpxFormula) (elem.sf)).cpf).v).key) instanceof abToken){
						for(Token t : ((abToken)(symTable.lookup(((IdVariable)((ComplexFormula)((CpxFormula)(elem.sf)).cpf).v).key))).listToken)
						{
							symTable.update(elem.u.s, t, 0);
							//symTable.update(((ComplexFormula)((CpxFormula) elem.sf).cpf).uv.s, t, 0);
							((ComplexFormula) ((CpxFormula) elem.sf).cpf).f.accept(this);
							if(((ComplexFormula) ((CpxFormula) elem.sf).cpf).f instanceof AtFormula)
							{
								if(((AtFormula)((ComplexFormula)((CpxFormula) elem.sf).cpf).f).bool_val == true)
									resultTok.addToken(t);
							}else if(elem.sf instanceof CpFormula)
							{
								if(((CpFormula)((ComplexFormula)((CpxFormula) elem.sf).cpf).f).bool_val == true)
									resultTok.addToken(t);
							}else if(elem.sf instanceof CpxFormula)
							{
								if(((CpxFormula)((ComplexFormula)((CpxFormula) elem.sf).cpf).f).bool_val == true)
									resultTok.addToken(t);
							}
							symTable.delete(elem.u.s);
							//symTable.delete(((ComplexFormula) ((CpxFormula) elem.sf).cpf).uv.s);
						}	
						
					}
				}
			} else errorMsg.error(elem.pos, "SetDef::Incorrect Set Definition!"); 
		//}
		//assign the result token to absyntree
		elem.abTok = resultTok;
	}
	
	@Override
	public void visit(Div elem) {
		if(debug)System.out.println("Div");
		elem.t1.accept(this);
		elem.t2.accept(this);
		
		int Lint_val = 0;
		int Rint_val = 0;
		//deal with LHS term
		if(elem.t1 instanceof ConstantTerm){			
			if(((ConstantTerm)(elem.t1)).c instanceof NumConstant){
				Lint_val = ((ConstantTerm)(elem.t1)).int_val;
			}
		}else if(elem.t1 instanceof VariableTerm){
			if(((VariableTerm)(elem.t1)).v instanceof IdVariable){
					Lint_val = ((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.firstElement().Tint;
			}else if(((VariableTerm)(elem.t1)).v instanceof IndexVariable){
				Lint_val = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.elementAt(((VariableTerm)elem.t1).index - 1).Tint;
			}
		}else if(elem.t1 instanceof ExpTerm){
				Lint_val = ((AExp)(((ExpTerm)(elem.t1)).e)).int_val;
		}else errorMsg.error(elem.pos, "Div::Tree type mismatch!"); 
		
		//deal with RHS term
		if(elem.t2 instanceof ConstantTerm){			
			if(((ConstantTerm)(elem.t2)).c instanceof NumConstant){
				Rint_val = ((ConstantTerm)(elem.t2)).int_val;
			}
		}else if(elem.t2 instanceof VariableTerm){
			if(((VariableTerm)(elem.t2)).v instanceof IdVariable){
					Rint_val = ((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.firstElement().Tint;
			}else if(((VariableTerm)(elem.t2)).v instanceof IndexVariable){
				Rint_val = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.elementAt(((VariableTerm)elem.t2).index - 1).Tint;
			}
		}else if(elem.t2 instanceof ExpTerm){
				Rint_val = ((AExp)(((ExpTerm)(elem.t2)).e)).int_val;
		}else errorMsg.error(elem.pos, "Div::Tree type mismatch!"); 
		
		//Execute
		if(Rint_val == 0){
			errorMsg.error(elem.pos, "Div::Divided by 0!");
		}else{
			elem.int_val = Lint_val / Rint_val;
		}
		
	}

	@Override
	public void visit(EqRel elem) {
		if(debug)System.out.println("EqRel");
		elem.t1.accept(this);
		elem.t2.accept(this);
		int Ltype = 0;//1 is bool;2 is int; 3 is string;;;LHS type
		int Rtype = 0;//1 is bool;2 is int; 3 is string; 4 is empty;;RHS type
		
		boolean Lbool_val = false;
		boolean Rbool_val = false;
		
		int Lint_val = 0;
		int Rint_val = 0;
		String Lstr_val = "";
		String Rstr_val = "";

		
		//whether the LHS variable belongs to arcOutVarList
		boolean isInArcOutVarList = false;
		if(elem.t1 instanceof VariableTerm)
		{
			if(((VariableTerm)(elem.t1)).v instanceof IdVariable){
				String var_key = ((IdVariable)((VariableTerm)(elem.t1)).v).key;
				for(Arc ao : iTransition.getArcOutList()){
					   Place po = (Place)(ao.getTarget());
					   if(!po.getToken().getDataType().getPow())
					   {
						   String svar = ao.getVar();
						   if(svar.equals(var_key))isInArcOutVarList = true;
					   }else{
						   String[] vars = ao.getVars();
						   for(int i=0;i<vars.length;i++)
						   {
							   if(vars[i].equals(var_key))isInArcOutVarList = true;
						   }
					   }			   
				   }
			}
			
			if(((VariableTerm)(elem.t1)).v instanceof IndexVariable){
				String var_key = ((IndexVariable)((VariableTerm)(elem.t1)).v).key;
				for(Arc ao : iTransition.getArcOutList()){
					   Place po = (Place)(ao.getTarget());
					   if(!po.getToken().getDataType().getPow())
					   {
						   String svar = ao.getVar();
						   if(svar.equals(var_key))isInArcOutVarList = true;
					   }else{
						   String[] vars = ao.getVars();
						   for(int i=0;i<vars.length;i++)
						   {
							   if(vars[i].equals(var_key))isInArcOutVarList = true;
						   }
					   }   
				   }
				}
			}
		
		//added by He 7/30/15
		boolean isInArcInVarList = false;
		if(elem.t1 instanceof VariableTerm)
		{
			if(((VariableTerm)(elem.t1)).v instanceof IdVariable){
				String var_key = ((IdVariable)((VariableTerm)(elem.t1)).v).key;
				for(Arc ai : iTransition.getArcInList()){
					   Place pi = (Place)(ai.getSource());
					   if(!pi.getToken().getDataType().getPow())
					   {
						   String svar = ai.getVar();
						   if(svar.equals(var_key))isInArcInVarList = true;
					   }else{
						   String[] vars = ai.getVars();
						   for(int i=0;i<vars.length;i++)
						   {
							   if(vars[i].equals(var_key))isInArcInVarList = true;
						   }
					   }			   
				   }
			}
			
			if(((VariableTerm)(elem.t1)).v instanceof IndexVariable){
				String var_key = ((IndexVariable)((VariableTerm)(elem.t1)).v).key;
				for(Arc ai : iTransition.getArcInList()){
					   Place pi = (Place)(ai.getSource());
					   if(!pi.getToken().getDataType().getPow())
					   {
						   String svar = ai.getVar();
						   if(svar.equals(var_key))isInArcInVarList = true;
					   }else{
						   String[] vars = ai.getVars();
						   for(int i=0;i<vars.length;i++)
						   {
							   if(vars[i].equals(var_key))isInArcInVarList = true;
						   }
					   }   
				   }
				}
			}

		
		//newly modified to deal with the condition when isInArcOutVarList is true and mode is 1;
		if(isInArcOutVarList && mode == 1)
		{
			if(elem.t1 instanceof VariableTerm){
				if(((VariableTerm)(elem.t1)).v instanceof IdVariable)
				{
					if(elem.t2 instanceof VariableTerm){
					if(((VariableTerm)(elem.t2)).v instanceof IdVariable){
						//modified by He to handle set variable - 8/5/15
						int vType = symTable.getType(((IdVariable)((VariableTerm)(elem.t2)).v).key);
						symTable.update(((IdVariable)((VariableTerm)(elem.t1)).v).key, symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key), vType);
						
					}else if(((VariableTerm)(elem.t2)).v instanceof IndexVariable)
					{
						if(((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.elementAt(((VariableTerm)elem.t2).index - 1).kind == 0)
						{
							Rint_val = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.elementAt(((VariableTerm)elem.t2).index - 1).Tint;
							Token tempTok = (Token) symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key);
							tempTok.Tlist.firstElement().Tint = Rint_val;
							symTable.update(((IdVariable)((VariableTerm)(elem.t1)).v).key, tempTok, 0);
						}else if(((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.elementAt(((VariableTerm)elem.t2).index - 1).kind == 1)
						{
							Rstr_val = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.elementAt(((VariableTerm)elem.t2).index - 1).Tstring;
							Token tempTok = (Token) symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key);
							tempTok.Tlist.firstElement().Tstring = Rstr_val;
							symTable.update(((IdVariable)((VariableTerm)(elem.t1)).v).key,  tempTok, 0);
						}
					}
					}else if(elem.t2 instanceof ExpTerm){//only t2 can be ExpTerm
						if(((ExpTerm)(elem.t2)).e instanceof AExp){  //added by He - 7/25/2015
							Token tempTok = (Token) symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key);
							tempTok.Tlist.firstElement().Tint = ((AExp) ((ExpTerm)(elem.t2)).e).int_val;
							symTable.update(((IdVariable)((VariableTerm)(elem.t1)).v).key,  tempTok, 0);
						}else if(((ExpTerm)(elem.t2)).e instanceof RExp){//added by He - 7/25/2015
							 Rbool_val = ((RExp) ((ExpTerm)(elem.t2)).e).bool_val;
							 System.out.println("Boolean valued token is not supported, token is default to 0");
						}else if((((ExpTerm)(elem.t2)).e instanceof SExp)){//set_exp     //changed by He - 8/4/15
								symTable.update(((IdVariable)((VariableTerm)(elem.t1)).v).key, ((SExp)((ExpTerm)(elem.t2)).e).abTok, 1);
					       
								//System.out.println("No of Tokens" + ((abToken) ((SExp)((ExpTerm)(elem.t2)).e).abTok).getTokenCount());
						}
					}else if(elem.t2 instanceof ConstantTerm){
						if(((ConstantTerm)(elem.t2)).c instanceof True){
							Rbool_val = true;
							System.out.println("Boolean valued token is not supported, token is default to 0"); //added by He - 7/25/2015
						}else if(((ConstantTerm)(elem.t2)).c instanceof False){
							Rbool_val = false;
							 System.out.println("Boolean valued token is not supported, token is default to 0"); //added by He - 7/25/2015
						}else if(((ConstantTerm)(elem.t2)).c instanceof NumConstant){
							Rint_val = ((ConstantTerm)(elem.t2)).int_val;
							Token tempTok = (Token) symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key);
							tempTok.Tlist.firstElement().Tint = Rint_val;
							symTable.update(((IdVariable)((VariableTerm)(elem.t1)).v).key,  tempTok, 0);
						}else if(((ConstantTerm)(elem.t2)).c instanceof StrConstant){
							Rstr_val = ((ConstantTerm)(elem.t2)).str_val;
							Token tempTok = (Token) symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key);
							tempTok.Tlist.firstElement().Tstring = Rstr_val;
							symTable.update(((IdVariable)((VariableTerm)(elem.t1)).v).key,  tempTok, 0);
						}
					}
				}
				
				if(((VariableTerm)(elem.t1)).v instanceof IndexVariable)
				{
					if(elem.t2 instanceof VariableTerm){
					if(((VariableTerm)(elem.t2)).v instanceof IdVariable){
						if(((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.firstElement().kind == 0){
							Rint_val = ((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.firstElement().Tint;
							Token tempTok = (Token) symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key);
							tempTok.Tlist.elementAt(((VariableTerm)elem.t1).index - 1).Tint = Rint_val;
							symTable.update(((IndexVariable)((VariableTerm)(elem.t1)).v).key,  tempTok, 0);
						}else if(((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.firstElement().kind == 1){
							Rstr_val = ((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.firstElement().Tstring;
							Token tempTok = (Token) symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key);
							tempTok.Tlist.elementAt(((VariableTerm)elem.t1).index - 1).Tstring = Rstr_val;
							symTable.update(((IndexVariable)((VariableTerm)(elem.t1)).v).key,  tempTok, 0);
						}
					}else if(((VariableTerm)(elem.t2)).v instanceof IndexVariable){
						if(((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.elementAt(((VariableTerm)elem.t2).index - 1).kind == 0){
							Rint_val = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.elementAt(((VariableTerm)elem.t2).index - 1).Tint;
							
							Token tempTok = (Token) symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key);
							tempTok.Tlist.elementAt(((VariableTerm)elem.t1).index - 1).Tint = Rint_val;
							symTable.update(((IndexVariable)((VariableTerm)(elem.t1)).v).key,  tempTok, 0);
						}else if(((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.elementAt(((VariableTerm)elem.t2).index - 1).kind == 1){
							Rstr_val = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.elementAt(((VariableTerm)elem.t2).index - 1).Tstring;
							
							Token tempTok = (Token) symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key);
							tempTok.Tlist.elementAt(((VariableTerm)elem.t1).index - 1).Tstring = Rstr_val;
							symTable.update(((IndexVariable)((VariableTerm)(elem.t1)).v).key,  tempTok, 0);
						}
					}
				}else if(elem.t2 instanceof ExpTerm){//only t2 can be ExpTerm
					if(((ExpTerm)(elem.t2)).e instanceof AExp){//added by He - 7/25/2015
						Token tempTok = (Token) symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key);
						tempTok.Tlist.elementAt(((VariableTerm)elem.t1).index - 1).Tint = ((AExp) ((ExpTerm)(elem.t2)).e).int_val;
						symTable.update(((IndexVariable)((VariableTerm)(elem.t1)).v).key,  tempTok, 0);
					}else if(((ExpTerm)(elem.t2)).e instanceof RExp){//added by He - 7/25/2015
						Rbool_val = ((RExp) ((ExpTerm)(elem.t2)).e).bool_val;
						System.out.println("Boolean valued token is not supported, token is default to 0");
					}else if((((ExpTerm)(elem.t2)).e instanceof SExp)){//set_exp
						if(symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key) instanceof abToken){
							symTable.update(((IdVariable)((VariableTerm)(elem.t1)).v).key, ((SExp)((ExpTerm)(elem.t2)).e).abTok, 1);
						} else{
							symTable.update(((IdVariable)((VariableTerm)(elem.t1)).v).key, ((SExp)((ExpTerm)(elem.t2)).e).abTok.listToken.firstElement(),1);
						}
					}
				}else if(elem.t2 instanceof ConstantTerm){
					if(((ConstantTerm)(elem.t2)).c instanceof True){
						Rbool_val = true;
						System.out.println("Boolean valued token is not supported, token is default to 0");
					}else if(((ConstantTerm)(elem.t2)).c instanceof False){
						Rbool_val = false;
						System.out.println("Boolean valued token is not supported, token is default to 0");
					}else if(((ConstantTerm)(elem.t2)).c instanceof NumConstant){
						Rint_val = ((ConstantTerm)(elem.t2)).int_val;
						Token tempTok = (Token) symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key);
						tempTok.Tlist.elementAt(((VariableTerm)elem.t1).index - 1).Tint = Rint_val;
						symTable.update(((IndexVariable)((VariableTerm)(elem.t1)).v).key,  tempTok, 0);
					}else if(((ConstantTerm)(elem.t2)).c instanceof StrConstant){
						Rstr_val = ((ConstantTerm)(elem.t2)).str_val;
						Token tempTok = (Token) symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key);
						tempTok.Tlist.elementAt(((VariableTerm)elem.t1).index - 1).Tstring = Rstr_val;
						symTable.update(((IndexVariable)((VariableTerm)(elem.t1)).v).key,  tempTok, 0);
					}
				}
				}
			}
		}
		if(!isInArcOutVarList || isInArcInVarList)       
		{
		//deal with LHS term
		if(elem.t1 instanceof ConstantTerm)
		{
			if(((ConstantTerm)(elem.t1)).c instanceof True){
				Lbool_val = true;
				Ltype = 1;
			}else if(((ConstantTerm)(elem.t1)).c instanceof False){
				Lbool_val = false;
				Ltype = 1;				
			}else if(((ConstantTerm)(elem.t1)).c instanceof NumConstant){
				Lint_val = ((ConstantTerm)(elem.t1)).int_val;
				Ltype = 2;
			}else if(((ConstantTerm)(elem.t1)).c instanceof StrConstant){
				Lstr_val = ((ConstantTerm)(elem.t1)).str_val;
				Ltype = 3;
			}
		} else if(elem.t1 instanceof VariableTerm){
			if(((VariableTerm)(elem.t1)).v instanceof IdVariable){
				if(((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.firstElement().kind == 0){
					Lint_val = ((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.firstElement().Tint;
					Ltype = 2;
				}else if(((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.firstElement().kind == 1){
					Lstr_val = ((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.firstElement().Tstring;
					Ltype = 3;
				}
			}else if(((VariableTerm)(elem.t1)).v instanceof IndexVariable){
				if(((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.elementAt(((VariableTerm)elem.t1).index - 1).kind == 0){
					Lint_val = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.elementAt(((VariableTerm)elem.t1).index - 1).Tint;
					Ltype = 2;
				}else if(((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.elementAt(((VariableTerm)elem.t1).index - 1).kind == 1){
					Lstr_val = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.elementAt(((VariableTerm)elem.t1).index - 1).Tstring;
					Ltype = 3;
				}
			}
		}else if(elem.t1 instanceof ExpTerm){
			if(((ExpTerm)(elem.t1)).e instanceof RExp){
				Lbool_val = ((RExp)(((ExpTerm)(elem.t1)).e)).bool_val;
				Ltype = 1;
			}else if(((ExpTerm)(elem.t1)).e instanceof AExp){
				Lint_val = ((AExp)(((ExpTerm)(elem.t1)).e)).int_val;
				Ltype = 2;
			}
		}else errorMsg.error(elem.pos, "EqRel::Tree type mismatch!"); 
		
		//deal with RHS term
		if(elem.t2 instanceof ConstantTerm){
			if(((ConstantTerm)(elem.t2)).c instanceof True){
				Rbool_val = true;
				Rtype = 1;
			}else if(((ConstantTerm)(elem.t2)).c instanceof False){
				Rbool_val = false;
				Rtype = 1;				
			}else if(((ConstantTerm)(elem.t2)).c instanceof NumConstant){
				Rint_val = ((ConstantTerm)(elem.t2)).int_val;
				Rtype = 2;
			}else if(((ConstantTerm)(elem.t2)).c instanceof StrConstant){
				Rstr_val = ((ConstantTerm)(elem.t2)).str_val;
				Rtype = 3;
			}
		}else if(elem.t2 instanceof VariableTerm){
			if(((VariableTerm)(elem.t2)).v instanceof IdVariable){
				if(((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.firstElement().kind == 0){
					Rint_val = ((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.firstElement().Tint;
					Rtype = 2;
				}else if(((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.firstElement().kind == 1){
					Rstr_val = ((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.firstElement().Tstring;
					Rtype = 3;
				}
			}else if(((VariableTerm)(elem.t2)).v instanceof IndexVariable){
				if(((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.elementAt(((VariableTerm)elem.t2).index - 1).kind == 0){
					Rint_val = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.elementAt(((VariableTerm)elem.t2).index - 1).Tint;
					Rtype = 2;
				}else if(((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.elementAt(((VariableTerm)elem.t2).index - 1).kind == 1){
					Rstr_val = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.elementAt(((VariableTerm)elem.t2).index - 1).Tstring;
					Rtype = 3;
				}
			}
		}else if(elem.t2 instanceof ExpTerm){
			if(((ExpTerm)(elem.t2)).e instanceof RExp){
				Rbool_val = ((RExp)(((ExpTerm)(elem.t2)).e)).bool_val;
				Rtype = 1;
			}else if(((ExpTerm)(elem.t2)).e instanceof AExp){
				Rint_val = ((AExp)(((ExpTerm)(elem.t2)).e)).int_val;
				Rtype = 2;
			}
		}else if(elem.t2 instanceof EmptyTerm){
			 Rtype = 4;
		}else errorMsg.error(elem.pos, "EqRel::Tree type mismatch!");
		}   

		//Compare LHS and RHS
		if(!isInArcOutVarList || isInArcInVarList)
		{
		if(Ltype == 1 && Rtype == 1){
			if(Lbool_val == Rbool_val){
				elem.bool_val = true;
			}else elem.bool_val = false;
		}else if(Ltype == 2 && Rtype == 2){
			if(Lint_val == Rint_val){
				elem.bool_val = true;
			}else elem.bool_val = false;
		}else if(Ltype == 3 && Rtype == 3){
			if(Lstr_val.equals(Rstr_val)){
				elem.bool_val = true;
			}else elem.bool_val = false;
		}else if(Ltype == 2 && Rtype == 4){
			if(Lint_val == 0)elem.bool_val = true;
			else elem.bool_val = false;
		}else if(Ltype == 3 && Rtype == 4){
			if(Lstr_val.isEmpty())elem.bool_val = true;
			else elem.bool_val = false;
		}
		else errorMsg.error(elem.pos, "EqRel::LHS type does not match the type of RHS!"); 
		} else elem.bool_val = true;
	}

	@Override
	public void visit(EquivFormula elem) {
		if(debug)System.out.println("EquivFormula");
		elem.f1.accept(this);
		elem.f2.accept(this);
		
		boolean temp_f1 = false;
		boolean temp_f2 = false;
		
		if(elem.f1 instanceof AtFormula){
			temp_f1 = ((AtFormula)(elem.f1)).bool_val;
		}else if(elem.f1 instanceof CpFormula){
			temp_f1 = ((CpFormula)(elem.f1)).bool_val;
		}else if(elem.f1 instanceof CpxFormula){
			temp_f1 = ((CpxFormula)(elem.f1)).bool_val;
		}else errorMsg.error(elem.pos, "EquivFormula::LHS Formula type mismatch!");
		
		if(elem.f2 instanceof AtFormula){
			temp_f2 = ((AtFormula)(elem.f2)).bool_val;
		}else if(elem.f2 instanceof CpFormula){
			temp_f2 = ((CpFormula)(elem.f2)).bool_val;
		}else if(elem.f2 instanceof CpxFormula){
			temp_f2 = ((CpxFormula)(elem.f2)).bool_val;
		}else errorMsg.error(elem.pos, "EquivFormula::RHS Formula type mismatch!");		
		
		//compare
		if(temp_f1 && temp_f2){
			elem.bool_val = true;
		}else if(!temp_f1 && !temp_f2){
			elem.bool_val = true;
		}else{
			elem.bool_val = false;
		}
	}

	@Override
	public void visit(Exists elem) {
		if(debug)System.out.println("Exists");
//		elem.quant_type = 1;
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
		
		int Lint_val = 0;
		int Rint_val = 0;
		
		//deal with LHS term
		if(elem.t1 instanceof ConstantTerm){			
			if(((ConstantTerm)(elem.t1)).c instanceof NumConstant){
				Lint_val = ((ConstantTerm)(elem.t1)).int_val;
			}
		}else if(elem.t1 instanceof VariableTerm){
			if(((VariableTerm)(elem.t1)).v instanceof IdVariable){
					Lint_val = ((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.firstElement().Tint;
			}else if(((VariableTerm)(elem.t1)).v instanceof IndexVariable){
				Lint_val = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.elementAt(((VariableTerm)elem.t1).index - 1).Tint;
			}
		}else if(elem.t1 instanceof ExpTerm){
				Lint_val = ((AExp)(((ExpTerm)(elem.t1)).e)).int_val;
		}else errorMsg.error(elem.pos, "GeqRel::Tree type mismatch!"); 
		
		//deal with RHS term
		if(elem.t2 instanceof ConstantTerm){			
			if(((ConstantTerm)(elem.t2)).c instanceof NumConstant){
				Rint_val = ((ConstantTerm)(elem.t2)).int_val;
			}
		}else if(elem.t2 instanceof VariableTerm){
			if(((VariableTerm)(elem.t2)).v instanceof IdVariable){
					Rint_val = ((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.firstElement().Tint;
			}else if(((VariableTerm)(elem.t2)).v instanceof IndexVariable){
				Rint_val = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.elementAt(((VariableTerm)elem.t2).index - 1).Tint;
			}
		}else if(elem.t2 instanceof ExpTerm){
				Rint_val = ((AExp)(((ExpTerm)(elem.t2)).e)).int_val;
		}else errorMsg.error(elem.pos, "GeqRel::Tree type mismatch!"); 
		
		//Compare LHS and RHS
		if(Lint_val >= Rint_val){
			elem.bool_val = true;
		}else elem.bool_val = false;
	}

	@Override
	public void visit(GtRel elem) {
		if(debug)System.out.println("GtRel");
		elem.t1.accept(this);
		elem.t2.accept(this);
		
		int Lint_val = 0;
		int Rint_val = 0;
		
		//deal with LHS term
		if(elem.t1 instanceof ConstantTerm){			
			if(((ConstantTerm)(elem.t1)).c instanceof NumConstant){
				Lint_val = ((ConstantTerm)(elem.t1)).int_val;
			}
		}else if(elem.t1 instanceof VariableTerm){
			if(((VariableTerm)(elem.t1)).v instanceof IdVariable){
					Lint_val = ((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.firstElement().Tint;
			}else if(((VariableTerm)(elem.t1)).v instanceof IndexVariable){
				Lint_val = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.elementAt(((VariableTerm)elem.t1).index - 1).Tint;
			}
		}else if(elem.t1 instanceof ExpTerm){
				Lint_val = ((AExp)(((ExpTerm)(elem.t1)).e)).int_val;
		}else errorMsg.error(elem.pos, "GtRel::Tree type mismatch!"); 
		
		//deal with RHS term
		if(elem.t2 instanceof ConstantTerm){			
			if(((ConstantTerm)(elem.t2)).c instanceof NumConstant){
				Rint_val = ((ConstantTerm)(elem.t2)).int_val;
			}
		}else if(elem.t2 instanceof VariableTerm){
			if(((VariableTerm)(elem.t2)).v instanceof IdVariable){
					Rint_val = ((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.firstElement().Tint;
			}else if(((VariableTerm)(elem.t2)).v instanceof IndexVariable){
				Rint_val = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.elementAt(((VariableTerm)elem.t2).index - 1).Tint;
			}
		}else if(elem.t2 instanceof ExpTerm){
				Rint_val = ((AExp)(((ExpTerm)(elem.t2)).e)).int_val;
		}else errorMsg.error(elem.pos, "GtRel::Tree type mismatch!"); 
		
		//Compare LHS and RHS
		if(Lint_val > Rint_val){
			elem.bool_val = true;
		}else elem.bool_val = false;
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

		boolean temp_f1 = false;
		boolean temp_f2 = false;
		
		if(elem.f1 instanceof AtFormula){
			temp_f1 = ((AtFormula)(elem.f1)).bool_val;
		}else if(elem.f1 instanceof CpFormula){
			temp_f1 = ((CpFormula)(elem.f1)).bool_val;
		}else if(elem.f1 instanceof CpxFormula){
			temp_f1 = ((CpxFormula)(elem.f1)).bool_val;
		}else errorMsg.error(elem.pos, "ImpFormula::LHS Formula type mismatch!");
		
		if(elem.f2 instanceof AtFormula){
			temp_f2 = ((AtFormula)(elem.f2)).bool_val;
		}else if(elem.f2 instanceof CpFormula){
			temp_f2 = ((CpFormula)(elem.f2)).bool_val;
		}else if(elem.f2 instanceof CpxFormula){
			temp_f2 = ((CpxFormula)(elem.f2)).bool_val;
		}else errorMsg.error(elem.pos, "ImpFormula::RHS Formula type mismatch!");		
		
		//compare
		if((!temp_f1) || temp_f2){
			elem.bool_val = true;
		}else elem.bool_val = false;		
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
		
		int Ltype=0;//1 is int; 2 is str;  3 is tok
		
		String Lstr_val="";
		int Lint_val = 0;
		Token Ltok = new Token();
		
		//deal with LHS term
		if(elem.t1 instanceof ConstantTerm){
			if(((ConstantTerm)(elem.t1)).c instanceof NumConstant){
				Lint_val = ((ConstantTerm)(elem.t1)).int_val;
				Ltype = 1;
			}else if(((ConstantTerm)(elem.t1)).c instanceof StrConstant){
				Lstr_val = ((ConstantTerm)(elem.t1)).str_val;
				Ltype = 2;
			}
		}else if(elem.t1 instanceof VariableTerm){
			if(((VariableTerm)(elem.t1)).v instanceof IdVariable){
					Ltok = (Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key));
					Ltype = 3;
			}else if(((VariableTerm)(elem.t1)).v instanceof IndexVariable){
				if(((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.elementAt(((VariableTerm)elem.t1).index - 1).kind == 0){
					Lint_val = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.elementAt(((VariableTerm)elem.t1).index - 1).Tint;
					Ltype = 1;
				}else if(((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.elementAt(((VariableTerm)elem.t1).index - 1).kind == 1){
					Lstr_val = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.elementAt(((VariableTerm)elem.t1).index - 1).Tstring;
					Ltype = 2;
				}
			}
		}else errorMsg.error(elem.pos, "InRel::Tree type mismatch!"); 

		//RHS tackled inside compare part
		
		//Compare LHS and RHS
		elem.bool_val = false;
		if(Ltype == 1){
			
			if(((VariableTerm)(elem.t2)).v instanceof IdVariable){
				int size = ((abToken)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key))).listToken.size();
				for(int i = 0;i<size;i++){
					if(Lint_val == ((abToken)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key))).listToken.elementAt(i).Tlist.firstElement().Tint){
						elem.bool_val = true;
					}
				}	
			}else if(((VariableTerm)(elem.t2)).v instanceof IndexVariable){
				int size = ((abToken)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).listToken.size();
				for(int i = 0;i<size;i++){
					if(Lint_val == ((abToken)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).listToken.elementAt(i).Tlist.elementAt(((VariableTerm)elem.t2).index - 1).Tint){
						elem.bool_val = true;
					}
				}	
			}
			
		}else if(Ltype == 2){
			
			if(((VariableTerm)(elem.t2)).v instanceof IdVariable){
				int size = ((abToken)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key))).listToken.size();
				for(int i = 0;i<size;i++){
					if(Lstr_val == ((abToken)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key))).listToken.elementAt(i).Tlist.firstElement().Tstring){
						elem.bool_val = true;
					}
				}	
			}else if(((VariableTerm)(elem.t2)).v instanceof IndexVariable){
				int size = ((abToken)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).listToken.size();
				for(int i = 0;i<size;i++){
					if(Lstr_val == ((abToken)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).listToken.elementAt(i).Tlist.elementAt(((VariableTerm)elem.t2).index - 1).Tstring){
						elem.bool_val = true;
					}
				}	
			}			
		}else if(Ltype == 3){
			
			if(((VariableTerm)(elem.t2)).v instanceof IdVariable){
				int size = ((abToken)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key))).listToken.size();
				for(int i = 0;i<size;i++){
					if(((abToken)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key))).listToken.elementAt(i).Tlist.equals(Ltok)){
						elem.bool_val = true;
					}
				}	
			}	
		}else errorMsg.error(elem.pos, "InRel::LHS type cannot compare with RHS!"); 
	}

	@Override
	public void visit(LeqRel elem) {
		if(debug)System.out.println("LeqRel");
		elem.t1.accept(this);
		elem.t2.accept(this);
		
		int Lint_val = 0;
		int Rint_val = 0;
		
		//deal with LHS term
		if(elem.t1 instanceof ConstantTerm){			
			if(((ConstantTerm)(elem.t1)).c instanceof NumConstant){
				Lint_val = ((ConstantTerm)(elem.t1)).int_val;
			}
		}else if(elem.t1 instanceof VariableTerm){
			if(((VariableTerm)(elem.t1)).v instanceof IdVariable){
					Lint_val = ((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.firstElement().Tint;
			}else if(((VariableTerm)(elem.t1)).v instanceof IndexVariable){
				Lint_val = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.elementAt(((VariableTerm)elem.t1).index - 1).Tint;
			}
		}else if(elem.t1 instanceof ExpTerm){
				Lint_val = ((AExp)(((ExpTerm)(elem.t1)).e)).int_val;
		}else errorMsg.error(elem.pos, "LeqRel::Tree type mismatch!"); 
		
		//deal with RHS term
		if(elem.t2 instanceof ConstantTerm){			
			if(((ConstantTerm)(elem.t2)).c instanceof NumConstant){
				Rint_val = ((ConstantTerm)(elem.t2)).int_val;
			}
		}else if(elem.t2 instanceof VariableTerm){
			if(((VariableTerm)(elem.t2)).v instanceof IdVariable){
					Rint_val = ((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.firstElement().Tint;
			}else if(((VariableTerm)(elem.t2)).v instanceof IndexVariable){
				Rint_val = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.elementAt(((VariableTerm)elem.t2).index - 1).Tint;
			}
		}else if(elem.t2 instanceof ExpTerm){
				Rint_val = ((AExp)(((ExpTerm)(elem.t2)).e)).int_val;
		}else errorMsg.error(elem.pos, "LeqRel::Tree type mismatch!"); 
		
		//Compare LHS and RHS
		if(Lint_val <= Rint_val){
			elem.bool_val = true;
		}else elem.bool_val = false;
	}

	@Override
	public void visit(LtRel elem) {
		if(debug)System.out.println("LtRel");
		elem.t1.accept(this);
		elem.t2.accept(this);
		
		int Lint_val = 0;
		int Rint_val = 0;
		
		//deal with LHS term
		if(elem.t1 instanceof ConstantTerm){			
			if(((ConstantTerm)(elem.t1)).c instanceof NumConstant){
				Lint_val = ((ConstantTerm)(elem.t1)).int_val;
			}
		}else if(elem.t1 instanceof VariableTerm){
			if(((VariableTerm)(elem.t1)).v instanceof IdVariable){
					Lint_val = ((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.firstElement().Tint;
			}else if(((VariableTerm)(elem.t1)).v instanceof IndexVariable){
				Lint_val = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.elementAt(((VariableTerm)elem.t1).index - 1).Tint;
			}
		}else if(elem.t1 instanceof ExpTerm){
				Lint_val = ((AExp)(((ExpTerm)(elem.t1)).e)).int_val;
		}else errorMsg.error(elem.pos, "LtRel::Tree type mismatch!"); 
		
		//deal with RHS term
		if(elem.t2 instanceof ConstantTerm){			
			if(((ConstantTerm)(elem.t2)).c instanceof NumConstant){
				Rint_val = ((ConstantTerm)(elem.t2)).int_val;
			}
		}else if(elem.t2 instanceof VariableTerm){
			if(((VariableTerm)(elem.t2)).v instanceof IdVariable){
					Rint_val = ((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.firstElement().Tint;
			}else if(((VariableTerm)(elem.t2)).v instanceof IndexVariable){
				Rint_val = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.elementAt(((VariableTerm)elem.t2).index - 1).Tint;
			}
		}else if(elem.t2 instanceof ExpTerm){
				Rint_val = ((AExp)(((ExpTerm)(elem.t2)).e)).int_val;
		}else errorMsg.error(elem.pos, "LtRel::Tree type mismatch!"); 
		
		//Compare LHS and RHS
		if(Lint_val < Rint_val){
			elem.bool_val = true;
		}else elem.bool_val = false;
	}

	@Override
	public void visit(Minus elem) {
		if(debug)System.out.println("Minus");
		elem.t1.accept(this);
		elem.t2.accept(this);
		
		int Lint_val = 0;
		int Rint_val = 0;
		
		//deal with LHS term
		if(elem.t1 instanceof ConstantTerm){			
			if(((ConstantTerm)(elem.t1)).c instanceof NumConstant){
				Lint_val = ((ConstantTerm)(elem.t1)).int_val;
			}
		}else if(elem.t1 instanceof VariableTerm){
			if(((VariableTerm)(elem.t1)).v instanceof IdVariable){
					Lint_val = ((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.firstElement().Tint;
			}else if(((VariableTerm)(elem.t1)).v instanceof IndexVariable){
				Lint_val = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.elementAt(((VariableTerm)elem.t1).index - 1).Tint;
			}
		}else if(elem.t1 instanceof ExpTerm){
				Lint_val = ((AExp)(((ExpTerm)(elem.t1)).e)).int_val;
		}else errorMsg.error(elem.pos, "Minus::Tree type mismatch!"); 
		
		//deal with RHS term
		if(elem.t2 instanceof ConstantTerm){			
			if(((ConstantTerm)(elem.t2)).c instanceof NumConstant){
				Rint_val = ((ConstantTerm)(elem.t2)).int_val;
			}
		}else if(elem.t2 instanceof VariableTerm){
			if(((VariableTerm)(elem.t2)).v instanceof IdVariable){
					Rint_val = ((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.firstElement().Tint;
			}else if(((VariableTerm)(elem.t2)).v instanceof IndexVariable){
				Rint_val = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.elementAt(((VariableTerm)elem.t2).index - 1).Tint;
			}
		}else if(elem.t2 instanceof ExpTerm){
				Rint_val = ((AExp)(((ExpTerm)(elem.t2)).e)).int_val;
		}else errorMsg.error(elem.pos, "Minus::Tree type mismatch!"); 
		
		//Execute
		elem.int_val = Lint_val - Rint_val;
	}

	@Override
	public void visit(Mod elem) {
		if(debug)System.out.println("Mod");
		elem.t1.accept(this);
		elem.t2.accept(this);
		
		int Lint_val = 0;
		int Rint_val = 0;
		
		//deal with LHS term
		if(elem.t1 instanceof ConstantTerm){			
			if(((ConstantTerm)(elem.t1)).c instanceof NumConstant){
				Lint_val = ((ConstantTerm)(elem.t1)).int_val;
			}
		}else if(elem.t1 instanceof VariableTerm){
			if(((VariableTerm)(elem.t1)).v instanceof IdVariable){
					Lint_val = ((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.firstElement().Tint;
			}else if(((VariableTerm)(elem.t1)).v instanceof IndexVariable){
				Lint_val = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.elementAt(((VariableTerm)elem.t1).index - 1).Tint;
			}
		}else if(elem.t1 instanceof ExpTerm){
				Lint_val = ((AExp)(((ExpTerm)(elem.t1)).e)).int_val;
		}else errorMsg.error(elem.pos, "Mod::Tree type mismatch!"); 
		
		//deal with RHS term
		if(elem.t2 instanceof ConstantTerm){			
			if(((ConstantTerm)(elem.t2)).c instanceof NumConstant){
				Rint_val = ((ConstantTerm)(elem.t2)).int_val;
			}
		}else if(elem.t2 instanceof VariableTerm){
			if(((VariableTerm)(elem.t2)).v instanceof IdVariable){
					Rint_val = ((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.firstElement().Tint;
			}else if(((VariableTerm)(elem.t2)).v instanceof IndexVariable){
				Rint_val = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.elementAt(((VariableTerm)elem.t2).index - 1).Tint;
			}
		}else if(elem.t2 instanceof ExpTerm){
				Rint_val = ((AExp)(((ExpTerm)(elem.t2)).e)).int_val;
		}else errorMsg.error(elem.pos, "Mod::Tree type mismatch!"); 
		
		//Execute
		if(Rint_val == 0){
			errorMsg.error(elem.pos, "Mod:: Mod by 0!!"); 
		}else{
			elem.int_val = Lint_val % Rint_val;
		}
	}

	@Override
	public void visit(Mul elem) {
		if(debug)System.out.println("Mul");
		elem.t1.accept(this);
		elem.t2.accept(this);
		
		int Lint_val = 0;
		int Rint_val = 0;
		
		//deal with LHS term
		if(elem.t1 instanceof ConstantTerm){			
			if(((ConstantTerm)(elem.t1)).c instanceof NumConstant){
				Lint_val = ((ConstantTerm)(elem.t1)).int_val;
			}
		}else if(elem.t1 instanceof VariableTerm){
			if(((VariableTerm)(elem.t1)).v instanceof IdVariable){
					Lint_val = ((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.firstElement().Tint;
			}else if(((VariableTerm)(elem.t1)).v instanceof IndexVariable){
				Lint_val = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.elementAt(((VariableTerm)elem.t1).index - 1).Tint;
			}
		}else if(elem.t1 instanceof ExpTerm){
				Lint_val = ((AExp)(((ExpTerm)(elem.t1)).e)).int_val;
		}else errorMsg.error(elem.pos, "Mul::Tree type mismatch!"); 
		
		//deal with RHS term
		if(elem.t2 instanceof ConstantTerm){			
			if(((ConstantTerm)(elem.t2)).c instanceof NumConstant){
				Rint_val = ((ConstantTerm)(elem.t2)).int_val;
			}
		}else if(elem.t2 instanceof VariableTerm){
			if(((VariableTerm)(elem.t2)).v instanceof IdVariable){
					Rint_val = ((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.firstElement().Tint;
			}else if(((VariableTerm)(elem.t2)).v instanceof IndexVariable){
				Rint_val = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.elementAt(((VariableTerm)elem.t2).index - 1).Tint;
			}
		}else if(elem.t2 instanceof ExpTerm){
				Rint_val = ((AExp)(((ExpTerm)(elem.t2)).e)).int_val;
		}else errorMsg.error(elem.pos, "Mul::Tree type mismatch!"); 
		
		//Execute
		elem.int_val = Lint_val * Rint_val;
	}


	public void visit(NegExp elem) {
		if(debug)System.out.println("NegExp");
		elem.t.accept(this);
		
		int val = 0;
		if(elem.t instanceof ConstantTerm){			
			if(((ConstantTerm)(elem.t)).c instanceof NumConstant){
				val = ((ConstantTerm)(elem.t)).int_val;
			}
		}else if(elem.t instanceof VariableTerm){
			if(((VariableTerm)(elem.t)).v instanceof IdVariable){
					val = ((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t)).v).key))).Tlist.firstElement().Tint;
			}else if(((VariableTerm)(elem.t)).v instanceof IndexVariable){
				val = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t)).v).key))).Tlist.elementAt(((VariableTerm)elem.t).index - 1).Tint;
			}
		}else if(elem.t instanceof ExpTerm){
				val = ((AExp)(((ExpTerm)(elem.t)).e)).int_val;
		}else errorMsg.error(elem.pos, "NegExp::Tree type mismatch!"); 	
		
		//Execute
		elem.int_val = -val;
	}

	@Override
	public void visit(NeqRel elem) {
		if(debug)System.out.println("NeqRel");
		elem.t1.accept(this);
		elem.t2.accept(this);
		
		int Ltype = 0;//1 is bool;2 is int; 3 is string;;;LHS type
		int Rtype = 0;//1 is bool;2 is int; 3 is string;;;RHS type
		
		boolean Lbool_val=false;
		boolean Rbool_val = false;
		int Lint_val = 0;
		int Rint_val = 0;
		String Lstr_val = "";
		String Rstr_val = "";

		//deal with LHS term
		if(elem.t1 instanceof ConstantTerm){
			if(((ConstantTerm)(elem.t1)).c instanceof True){
				Lbool_val = true;
				Ltype = 1;
			}else if(((ConstantTerm)(elem.t1)).c instanceof False){
				Lbool_val = false;
				Ltype = 1;				
			}else if(((ConstantTerm)(elem.t1)).c instanceof NumConstant){
				Lint_val = ((ConstantTerm)(elem.t1)).int_val;
				Ltype = 2;
			}else if(((ConstantTerm)(elem.t1)).c instanceof StrConstant){
				Lstr_val = ((ConstantTerm)(elem.t1)).str_val;
				Ltype = 3;
			}
		}else if(elem.t1 instanceof VariableTerm){
			if(((VariableTerm)(elem.t1)).v instanceof IdVariable){
				if(((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.firstElement().kind == 0){
					Lint_val = ((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.firstElement().Tint;
					Ltype = 2;
				}else if(((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.firstElement().kind == 1){
					Lstr_val = ((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.firstElement().Tstring;
					Ltype = 3;
				}
			}else if(((VariableTerm)(elem.t1)).v instanceof IndexVariable){
				if(((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.elementAt(((VariableTerm)elem.t1).index - 1).kind == 0){
					Lint_val = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.elementAt(((VariableTerm)elem.t1).index - 1).Tint;
					Ltype = 2;
				}else if(((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.elementAt(((VariableTerm)elem.t1).index - 1).kind == 1){
					Lstr_val = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.elementAt(((VariableTerm)elem.t1).index - 1).Tstring;
					Ltype = 3;
				}
			}
		}else if(elem.t1 instanceof ExpTerm){
			if(((ExpTerm)(elem.t1)).e instanceof RExp){
				Lbool_val = ((RExp)(((ExpTerm)(elem.t1)).e)).bool_val;
				Ltype = 1;
			}else if(((ExpTerm)(elem.t1)).e instanceof AExp){
				Lint_val = ((AExp)(((ExpTerm)(elem.t1)).e)).int_val;
				Ltype = 2;
			}
		}else errorMsg.error(elem.pos, "NeqRel::Tree type mismatch!"); 
		
		//deal with RHS term
		if(elem.t2 instanceof ConstantTerm){
			if(((ConstantTerm)(elem.t2)).c instanceof True){
				Rbool_val = true;
				Rtype = 1;
			}else if(((ConstantTerm)(elem.t2)).c instanceof False){
				Rbool_val = false;
				Rtype = 1;				
			}else if(((ConstantTerm)(elem.t2)).c instanceof NumConstant){
				Rint_val = ((ConstantTerm)(elem.t2)).int_val;
				Rtype = 2;
			}else if(((ConstantTerm)(elem.t2)).c instanceof StrConstant){
				Rstr_val = ((ConstantTerm)(elem.t2)).str_val;
				Rtype = 3;
			}
		}else if(elem.t2 instanceof VariableTerm){
			if(((VariableTerm)(elem.t2)).v instanceof IdVariable){
				if(((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.firstElement().kind == 0){
					Rint_val = ((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.firstElement().Tint;
					Rtype = 2;
				}else if(((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.firstElement().kind == 1){
					Rstr_val = ((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.firstElement().Tstring;
					Rtype = 3;
				}
			}else if(((VariableTerm)(elem.t2)).v instanceof IndexVariable){
				if(((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.elementAt(((VariableTerm)elem.t2).index - 1).kind == 0){
					Rint_val = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.elementAt(((VariableTerm)elem.t2).index - 1).Tint;
					Rtype = 2;
				}else if(((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.elementAt(((VariableTerm)elem.t2).index - 1).kind == 1){
					Rstr_val = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.elementAt(((VariableTerm)elem.t2).index - 1).Tstring;
					Rtype = 3;
				}
			}
		}else if(elem.t2 instanceof ExpTerm){
			if(((ExpTerm)(elem.t2)).e instanceof RExp){
				Rbool_val = ((RExp)(((ExpTerm)(elem.t2)).e)).bool_val;
				Rtype = 1;
			}else if(((ExpTerm)(elem.t2)).e instanceof AExp){
				Rint_val = ((AExp)(((ExpTerm)(elem.t2)).e)).int_val;
				Rtype = 2;
			}
		}else errorMsg.error(elem.pos, "NeqRel::Tree type mismatch!"); 

		//Compare LHS and RHS
		if(Ltype == 1 && Rtype == 1){
			if(Lbool_val == Rbool_val){
				elem.bool_val = false;
			}else elem.bool_val = true;
		}else if(Ltype == 2 && Rtype == 2){
			if(Lint_val == Rint_val){
				elem.bool_val = false;
			}else elem.bool_val = true;
		}else if(Ltype == 3 && Rtype == 3){
			if(Lstr_val.equals(Rstr_val)){
				elem.bool_val = false;
			}else elem.bool_val = true;
		}else errorMsg.error(elem.pos, "NeqRel::LHS type cannot compare with RHS!"); 
		
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
		
		int Ltype=0;//1 is int; 2 is str;  3 is tok
		
		String Lstr_val="";
		int Lint_val = 0;
		Token Ltok = new Token();
		
		//deal with LHS term
		if(elem.t1 instanceof ConstantTerm){
			if(((ConstantTerm)(elem.t1)).c instanceof NumConstant){
				Lint_val = ((ConstantTerm)(elem.t1)).int_val;
				Ltype = 1;
			}else if(((ConstantTerm)(elem.t1)).c instanceof StrConstant){
				Lstr_val = ((ConstantTerm)(elem.t1)).str_val;
				Ltype = 2;
			}
		}else if(elem.t1 instanceof VariableTerm){
			if(((VariableTerm)(elem.t1)).v instanceof IdVariable){
					Ltok = (Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key));
					Ltype = 3;
			}else if(((VariableTerm)(elem.t1)).v instanceof IndexVariable){
				if(((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.elementAt(((VariableTerm)elem.t1).index - 1).kind == 0){
					Lint_val = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.elementAt(((VariableTerm)elem.t1).index - 1).Tint;
					Ltype = 1;
				}else if(((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.elementAt(((VariableTerm)elem.t1).index - 1).kind == 1){
					Lstr_val = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.elementAt(((VariableTerm)elem.t1).index - 1).Tstring;
					Ltype = 2;
				}
			}
		}else errorMsg.error(elem.pos, "InRel::Tree type mismatch!"); 

		//RHS tackled inside compare part
		
		//Compare LHS and RHS
		elem.bool_val = true;
		if(Ltype == 1){
			
			if(((VariableTerm)(elem.t2)).v instanceof IdVariable){
				int size = ((abToken)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key))).listToken.size();
				for(int i = 0;i<size;i++){
					if(Lint_val == ((abToken)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key))).listToken.elementAt(i).Tlist.firstElement().Tint){
						elem.bool_val = false;
					}
				}	
			}else if(((VariableTerm)(elem.t2)).v instanceof IndexVariable){
				int size = ((abToken)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).listToken.size();
				for(int i = 0;i<size;i++){
					if(Lint_val == ((abToken)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).listToken.elementAt(i).Tlist.elementAt(((VariableTerm)elem.t2).index - 1).Tint){
						elem.bool_val = false;
					}
				}	
			}
			
		}else if(Ltype == 2){
			
			if(((VariableTerm)(elem.t2)).v instanceof IdVariable){
				int size = ((abToken)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key))).listToken.size();
				for(int i = 0;i<size;i++){
					if(Lstr_val == ((abToken)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key))).listToken.elementAt(i).Tlist.firstElement().Tstring){
						elem.bool_val = true;
					}
				}	
			}else if(((VariableTerm)(elem.t2)).v instanceof IndexVariable){
				int size = ((abToken)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).listToken.size();
				for(int i = 0;i<size;i++){
					if(Lstr_val == ((abToken)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).listToken.elementAt(i).Tlist.elementAt(((VariableTerm)elem.t2).index - 1).Tstring){
						elem.bool_val = true;
					}
				}	
			}			
		}else if(Ltype == 3){
			
			if(((VariableTerm)(elem.t2)).v instanceof IdVariable){
				int size = ((abToken)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key))).listToken.size();
				for(int i = 0;i<size;i++){
					if(((abToken)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key))).listToken.elementAt(i).Tlist.equals(Ltok)){
						elem.bool_val = true;
					}
				}	
			}	
		}else errorMsg.error(elem.pos, "InRel::LHS type cannot compare with RHS!"); 
		
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
		boolean temp_f1 = false;
		boolean temp_f2 = false;
		
		if(elem.f1 instanceof AtFormula){
			temp_f1 = ((AtFormula)(elem.f1)).bool_val;
		}else if(elem.f1 instanceof CpFormula){
			temp_f1 = ((CpFormula)(elem.f1)).bool_val;
		}else if(elem.f1 instanceof CpxFormula){
			temp_f1 = ((CpxFormula)(elem.f1)).bool_val;
		}else errorMsg.error(elem.pos, "OrFormula::LHS Formula type mismatch!");
		
		if(elem.f2 instanceof AtFormula){
			temp_f2 = ((AtFormula)(elem.f2)).bool_val;
		}else if(elem.f2 instanceof CpFormula){
			temp_f2 = ((CpFormula)(elem.f2)).bool_val;
		}else if(elem.f2 instanceof CpxFormula){
			temp_f2 = ((CpxFormula)(elem.f2)).bool_val;
		}else errorMsg.error(elem.pos, "OrFormula::RHS Formula type mismatch!");		
		
		//compare
		if(temp_f1 || temp_f2){
			elem.bool_val = true;
		}else elem.bool_val = false;
	}

	@Override
	public void visit(Plus elem) {
		if(debug)System.out.println("Plus");
		elem.t1.accept(this);
		elem.t2.accept(this);

		int Lint_val = 0;
		int Rint_val = 0;
		
		//deal with LHS term
		if(elem.t1 instanceof ConstantTerm){			
			if(((ConstantTerm)(elem.t1)).c instanceof NumConstant){
				Lint_val = ((ConstantTerm)(elem.t1)).int_val;
			}
		}else if(elem.t1 instanceof VariableTerm){
			if(((VariableTerm)(elem.t1)).v instanceof IdVariable){
					Lint_val = ((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.firstElement().Tint;
			}else if(((VariableTerm)(elem.t1)).v instanceof IndexVariable){
				Lint_val = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.elementAt(((VariableTerm)elem.t1).index - 1).Tint;
			}
		}else if(elem.t1 instanceof ExpTerm){
				Lint_val = ((AExp)(((ExpTerm)(elem.t1)).e)).int_val;
		}else errorMsg.error(elem.pos, "Plus::Tree type mismatch!"); 
		
		//deal with RHS term
		if(elem.t2 instanceof ConstantTerm){			
			if(((ConstantTerm)(elem.t2)).c instanceof NumConstant){
				Rint_val = ((ConstantTerm)(elem.t2)).int_val;
			}
		}else if(elem.t2 instanceof VariableTerm){
			if(((VariableTerm)(elem.t2)).v instanceof IdVariable){
					Rint_val = ((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.firstElement().Tint;
			}else if(((VariableTerm)(elem.t2)).v instanceof IndexVariable){
				Rint_val = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.elementAt(((VariableTerm)elem.t2).index - 1).Tint;
			}
		}else if(elem.t2 instanceof ExpTerm){
				Rint_val = ((AExp)(((ExpTerm)(elem.t2)).e)).int_val;
		}else errorMsg.error(elem.pos, "Plus::Tree type mismatch!"); 
		
		//Execute
		elem.int_val = Lint_val + Rint_val;
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
		
		Token tok = new Token();
		BasicType bt = new BasicType();
		BasicType[] BT = new BasicType[elem.tr.size()+1];
		
		if(elem.t instanceof VariableTerm){
		if(((VariableTerm)(elem.t)).v instanceof IndexVariable){
			if(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t)).v).key) instanceof Token){
				if(((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t)).v).key))).Tlist.elementAt(((VariableTerm)elem.t).index - 1).kind == 0){
//					String[] str = {"int"};
//					resultType = new DataType("intTok",str,true,null);
					bt.kind = 0;
					bt.Tint = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t)).v).key))).Tlist.elementAt(((VariableTerm)elem.t).index - 1).Tint;
				}else if(((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t)).v).key))).Tlist.elementAt(((VariableTerm)elem.t).index - 1).kind == 1){
					bt.kind = 1;
					bt.Tstring = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t)).v).key))).Tlist.elementAt(((VariableTerm)elem.t).index - 1).Tstring;
				
				}
			}
		}
		}else if(elem.t instanceof ConstantTerm){
			if(((ConstantTerm)(elem.t)).c instanceof NumConstant){
				bt.kind = 0;
				bt.Tint = ((ConstantTerm)(elem.t)).int_val;
			}else if(((ConstantTerm)(elem.t)).c instanceof StrConstant){
				bt.kind = 1;
				bt.Tstring = ((ConstantTerm)(elem.t)).str_val;
			}
		
		}else if(elem.t instanceof ExpTerm){
			//to be finish
		}
		
		//add term to the first basic type of the token;
		BT[0] = bt;	
		
		for(int i=0; i<elem.tr.size(); i++){
			elem.tr.elementAt(i).accept(this);
			BasicType btRest = new BasicType();
			if(elem.tr.elementAt(i).t instanceof VariableTerm){
				if(((VariableTerm)(elem.tr.elementAt(i).t)).v instanceof IndexVariable){
					if(symTable.lookup(((IndexVariable)((VariableTerm)(elem.tr.elementAt(i).t)).v).key) instanceof Token){
						if(((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.tr.elementAt(i).t)).v).key))).Tlist.elementAt(((VariableTerm)elem.tr.elementAt(i).t).index - 1).kind == 0){
//							String[] str = {"int"};
//							resultType = new DataType("intTok",str,true,null);
							btRest.kind = 0;
							btRest.Tint = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.tr.elementAt(i).t)).v).key))).Tlist.elementAt(((VariableTerm)elem.tr.elementAt(i).t).index - 1).Tint;
						}else if(((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.tr.elementAt(i).t)).v).key))).Tlist.elementAt(((VariableTerm)elem.tr.elementAt(i).t).index - 1).kind == 1){
							btRest.kind = 1;
							btRest.Tstring = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.tr.elementAt(i).t)).v).key))).Tlist.elementAt(((VariableTerm)elem.tr.elementAt(i).t).index - 1).Tstring;
						
						}
					}
				}
				}else if(elem.tr.elementAt(i).t instanceof ConstantTerm){
					if(((ConstantTerm)(elem.tr.elementAt(i).t)).c instanceof NumConstant){
						btRest.kind = 0;
//						btRest.Tint = ((Token)(symTable.lookup(((IndexVariable)((ConstantTerm)(elem.tr.elementAt(i).t)).v).key))).Tlist.elementAt(((ConstantTerm)elem.tr.elementAt(i).t).index - 1).Tint;
						btRest.Tint = ((ConstantTerm)(elem.tr.elementAt(i).t)).int_val;
					}else if(((ConstantTerm)(elem.tr.elementAt(i).t)).c instanceof StrConstant){
						btRest.kind = 1;
						btRest.Tstring = ((ConstantTerm)(elem.tr.elementAt(i).t)).str_val;
					}
					
				}else if(elem.tr.elementAt(i).t instanceof ExpTerm){
					btRest.kind = 0;
					btRest.Tint = ((ExpTerm)(elem.tr.elementAt(i).t)).int_val;
				}
			
			BT[i+1] = btRest;
		}
		tok.add(BT);
		tok.UpdateDataTypeByTlist();
		elem.Tok = tok;
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

		DataType resultType = null;
		//allocate space to a temp abToken to store result
		abToken resultTok = new abToken(resultType);
		
		//find datatype of the result token;
		if(elem.t1 instanceof VariableTerm){
			if(((VariableTerm)(elem.t1)).v instanceof IdVariable){
				if(symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key) instanceof Token){
					resultType = ((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key))).getTokentype();
				}else if(symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key) instanceof abToken){
					resultType = ((abToken)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key))).getDataType();
				}
			}else if(((VariableTerm)(elem.t1)).v instanceof IndexVariable){
				if(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key) instanceof Token){
					if(((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.elementAt(((VariableTerm)elem.t1).index - 1).kind == 0){
						String[] str = {"int"};
						resultType = new DataType("intTok",str,true,null);
					}else if(((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.elementAt(((VariableTerm)elem.t1).index - 1).kind == 1){
						String[] str = {"string"};
						resultType = new DataType("StrTok",str,true,null);
					}
				}
			}
		}else if(elem.t1 instanceof ExpTerm){
			resultType = ((SExp)((ExpTerm)(elem.t1)).e).abTok.getDataType();
		}else errorMsg.error(elem.pos, "Union::Tree type mismatch!"); 
		
		//union left term
		if(elem.t1 instanceof VariableTerm){
			if(((VariableTerm)(elem.t1)).v instanceof IdVariable){
			if(symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key) instanceof Token){
				resultTok.addToken(((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key))));
			}else if(symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key) instanceof abToken){
				for(Token t : ((abToken)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t1)).v).key))).listToken){
					resultTok.addToken(t);
				}
			}
			}else if(((VariableTerm)(elem.t1)).v instanceof IndexVariable){
				if(((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.elementAt(((VariableTerm)elem.t1).index - 1).kind == 0){
					Token temp_tok = new Token(resultType);
					temp_tok.Tlist.firstElement().Tint = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.elementAt(((VariableTerm)elem.t1).index - 1).Tint;
					resultTok.addToken(temp_tok);
				}else if(((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.elementAt(((VariableTerm)elem.t1).index - 1).kind == 1){
					Token temp_tok = new Token(resultType);
					temp_tok.Tlist.firstElement().Tstring = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t1)).v).key))).Tlist.elementAt(((VariableTerm)elem.t1).index - 1).Tstring;
					resultTok.addToken(temp_tok);
				}
			}
		}else if(elem.t1 instanceof ExpTerm){
			if(((ExpTerm)(elem.t1)).e instanceof SExp){
				for(Token t : ((SExp)((ExpTerm)(elem.t1)).e).abTok.listToken){
					resultTok.addToken(t);
				}
			}
		}
		
		//union right term
		if(elem.t2 instanceof VariableTerm){
			if(((VariableTerm)(elem.t2)).v instanceof IdVariable){
				if(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key) instanceof Token){
				resultTok.addToken(((Token)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key))));
				}else if(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key) instanceof abToken){
					for(Token t : ((abToken)(symTable.lookup(((IdVariable)((VariableTerm)(elem.t2)).v).key))).listToken){
						resultTok.addToken(t);
					}
				}
			}else if(((VariableTerm)(elem.t2)).v instanceof IndexVariable){
				if(((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.elementAt(((VariableTerm)elem.t2).index - 1).kind == 0){
					Token temp_tok = new Token(resultType);
					temp_tok.Tlist.firstElement().Tint = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.elementAt(((VariableTerm)elem.t2).index - 1).Tint;
					resultTok.addToken(temp_tok);
				}else if(((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.elementAt(((VariableTerm)elem.t2).index - 1).kind == 1){
					Token temp_tok = new Token(resultType);
					temp_tok.Tlist.firstElement().Tstring = ((Token)(symTable.lookup(((IndexVariable)((VariableTerm)(elem.t2)).v).key))).Tlist.elementAt(((VariableTerm)elem.t2).index - 1).Tstring;
					resultTok.addToken(temp_tok);
				}
			}
		}else if(elem.t2 instanceof ExpTerm){
			if(((ExpTerm)(elem.t2)).e instanceof SExp){
				for(Token t : ((SExp)((ExpTerm)(elem.t2)).e).abTok.listToken){
				resultTok.addToken(t);
				}
			}else if(((ExpTerm)(elem.t2)).e instanceof AExp){
			//when meet AExp at right, the result of AExp is int and left elem is an abtoken (a set), so the int from AExp is added to abToken, the type has to be [int]
				Token temp_tok = new Token(resultType);
				BasicType tempBt = new BasicType();
				tempBt.kind = 0;
				tempBt.Tint = ((AExp)((ExpTerm)(elem.t2)).e).int_val;
				temp_tok.Tlist.add(tempBt);
				resultTok.addToken(temp_tok);
			}
		}
		//assign the result token to absyntree
		elem.abTok = resultTok;
	}

	@Override
	public void visit(UserVariable elem) {
		if(debug)System.out.println("UserVariable");
	}

	@Override
	public void visit(ConstantTerm elem) {
		if(debug)System.out.println("ConstantTerm");
		elem.c.accept(this);
		
		if(elem.c instanceof True){
			elem.bool_val = ((True)elem.c).bool_val;
			elem.kind = 0;
		}else if(elem.c instanceof False){
			elem.bool_val = ((False)elem.c).bool_val;
			elem.kind = 0;
		}else if(elem.c instanceof NumConstant){
			elem.int_val  = ((NumConstant)elem.c).int_val;
			elem.kind = 1;
		}else if(elem.c instanceof StrConstant){
			elem.str_val = ((StrConstant)elem.c).str;
			elem.kind = 2;
		}
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
		}else errorMsg.error(elem.pos, "Variable can only be instance of IdVariable or IndexVariable");
		
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
		
		if(elem.ae instanceof Minus){
			elem.int_val = ((Minus)(elem.ae)).int_val;
		}else if(elem.ae instanceof Plus){
			elem.int_val = ((Plus)(elem.ae)).int_val;
		}else if(elem.ae instanceof Mul){
			elem.int_val = ((Mul)(elem.ae)).int_val;
		}else if(elem.ae instanceof Mod){
			elem.int_val = ((Mod)(elem.ae)).int_val;
		}else if(elem.ae instanceof NegExp){
			elem.int_val = ((NegExp)(elem.ae)).int_val;
		}else if(elem.ae instanceof Div){    //added by He - 7/25/2015
			elem.int_val = ((Div)(elem.ae)).int_val;
		}else errorMsg.error(elem.pos, "AExp::tree type mismatch!");
	}
	
	public void visit(RExp elem){
		if(debug)System.out.println("RExp");
		elem.re.accept(this);
		
		if(elem.re instanceof EqRel){
			elem.bool_val = ((EqRel)(elem.re)).bool_val;
		}else if(elem.re instanceof NeqRel){
			elem.bool_val = ((NeqRel)(elem.re)).bool_val;
		}else if(elem.re instanceof GtRel){
			elem.bool_val = ((GtRel)(elem.re)).bool_val;
		}else if(elem.re instanceof LtRel){
			elem.bool_val = ((LtRel)(elem.re)).bool_val;
		}else if(elem.re instanceof GeqRel){
			elem.bool_val = ((GeqRel)(elem.re)).bool_val;
		}else if(elem.re instanceof LeqRel){
			elem.bool_val = ((LeqRel)(elem.re)).bool_val;
		}else if(elem.re instanceof InRel){
			elem.bool_val = ((InRel)(elem.re)).bool_val;
		}else if(elem.re instanceof NinRel){
			elem.bool_val = ((NinRel)(elem.re)).bool_val;
		}else errorMsg.error(elem.pos, "RExp::tree type mismatch!");
	}
	
	public void visit(SExp elem){
		if(debug)System.out.println("SExp");
		elem.se.accept(this);
		
		if(elem.se instanceof Union){
			elem.abTok = ((Union)(elem.se)).abTok;
		}else if(elem.se instanceof Diff){
			elem.abTok = ((Diff)(elem.se)).abTok;
		}else if(elem.se instanceof BraceTerm){
			elem.abTok = ((BraceTerm)(elem.se)).abTok;
		}else if(elem.se instanceof BraceTerms){
			elem.abTok = ((BraceTerms)(elem.se)).abTok;
		}else if (elem.se instanceof Setdef){       //added by He - 8/19/15
			elem.abTok = ((Setdef) (elem.se)).abTok;
		}
	}

	@Override
	public void visit(AtomicTerm elem) {
		if(debug)System.out.println("AtomicTerm");
		elem.t.accept(this);
		
		if(elem.t instanceof ConstantTerm){
			elem.bool_val = ((ConstantTerm)(elem.t)).bool_val;
		}else if(elem.t instanceof ExpTerm){
			elem.bool_val = ((ExpTerm)(elem.t)).bool_val;
		}else errorMsg.error(elem.pos, "AtomicTerm::Cannot be VariableTerm or tree type mismatch!");
		
	}

	@Override
	public void visit(AtFormula elem) {
		if(debug)System.out.println("AtFormula");
		elem.af.accept(this);
		
		if(elem.af instanceof NotFormula){
			elem.bool_val = ((NotFormula)(elem.af)).bool_val;
		}else if(elem.af instanceof AtomicTerm){
			elem.bool_val = ((AtomicTerm)(elem.af)).bool_val;
		}else errorMsg.error(elem.pos, "AtFormula::tree type mismatch!");
	}

	@Override
	public void visit(CpFormula elem) {
		if(debug)System.out.println("CpFormula");
		elem.cf.accept(this);
		
		if(elem.cf instanceof AndFormula){
			elem.bool_val = ((AndFormula)(elem.cf)).bool_val;
		}else if(elem.cf instanceof OrFormula){
			elem.bool_val = ((OrFormula)(elem.cf)).bool_val;
		}else if(elem.cf instanceof ImpFormula){
			elem.bool_val = ((ImpFormula)(elem.cf)).bool_val;
		}else if(elem.cf instanceof EquivFormula){
			elem.bool_val = ((EquivFormula)(elem.cf)).bool_val;
		}else errorMsg.error(elem.pos, "CpFormula::tree type mismatch!");		
	}

	@Override
	public void visit(CpxFormula elem) {
		if(debug)System.out.println("CpxFormula");
		elem.cpf.accept(this);
		
		elem.bool_val = elem.cpf.bool_val;
	}

	@Override
	public void visit(Sentence elem) {
		if(debug)System.out.println("Sentence");
		elem.f.accept(this);
		if(elem.f instanceof AtFormula){
			elem.bool_val = ((AtFormula)(elem.f)).bool_val;
		}else if(elem.f instanceof CpFormula){
			elem.bool_val = ((CpFormula)(elem.f)).bool_val;
		}else if(elem.f instanceof CpxFormula){
			elem.bool_val = ((CpxFormula)(elem.f)).bool_val;
		}
		
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
