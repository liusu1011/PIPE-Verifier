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
	
	//For ComplexFormula
	String cpx_v1, cpx_v2;
	String cpx_PlaceName1, cpx_PlaceName2;
	int cpx_index = 0; //v1 and v2 are both vacant, 1 means v1 occupied
	                   //2 means v1 and v2 both occupied
	public ArrayList<VarDef> arrVar;
	public ArrayList<UserVarType> arrUserVar;
	
	private String tempPlaceName = "";
	
	String pname_union = ""; // in case "union", set place name for "!"
	boolean pname_union_status = false;  //once true, indicating output "!" for union
	
	public Formula2Maude(ErrorMsg errorMsg, Transition transition, int mode){
		this.errorMsg = errorMsg;
		iTransition = transition;
		this.symTable = iTransition.getTransSymbolTable();
		this.mode = mode;
		arrVar = new ArrayList<VarDef>();
		arrUserVar = new ArrayList<UserVarType>();
	}
	
	private void add_arrVar(VarDef vd)
	{
		boolean found = false;
		Iterator<VarDef> itr = arrVar.iterator();
		while(itr.hasNext()){
			found = itr.next().equals(vd);
			if (found == true) break;
		}
		if (found == false)
			arrVar.add(vd);
	}
	@Override
	public void visit(AndFormula elem) {
		
		elem.f1.accept(this);
		elem.f2.accept(this);
		String leftPre = "";
		String rightPre = "";
		String leftPost = "";
		String rightPost = "";
		
		if(elem.f1 instanceof AtFormula){
			leftPre = ((AtFormula)(elem.f1)).strPre;
			leftPost = ((AtFormula)(elem.f1)).strPost;
		}else if(elem.f1 instanceof CpFormula){
			leftPre = ((CpFormula)(elem.f1)).strPre;
			leftPost = ((CpFormula)(elem.f1)).strPost;
		}else if(elem.f1 instanceof CpxFormula){
			//TODO: CpxFormula
		}
		
		if(elem.f2 instanceof AtFormula){
			rightPre = ((AtFormula)(elem.f2)).strPre;
			rightPost = ((AtFormula)(elem.f2)).strPost;
		}else if(elem.f2 instanceof CpFormula){
			rightPre = ((CpFormula)(elem.f2)).strPre;
			rightPost = ((CpFormula)(elem.f2)).strPost;
		}else if(elem.f2 instanceof CpxFormula){
			//TODO: CpxFormula
		}
		
		if(("").equals(leftPre))leftPre = "true";
		if("".equals(rightPre))rightPre = "true";		
		elem.strPre = leftPre+" && "+rightPre;
		
		if(!("").equals(leftPost) && !("").equals(rightPost)){
			elem.strPost = leftPost+rightPost;
		}else if(("").equals(leftPost)){
			elem.strPost = rightPost;
		}else if(("").equals(rightPost)){
			elem.strPost = leftPost;
		}else elem.strPost = "";

		
//		String strTmp = "";
//		if (elem.treeLevel == 1) {
//			strTmp = "inline is_enabled_" + iTransition.getName()
//					+ "(){\n";
//			Iterator<VarDef> itr = arrVar.iterator();
//			while(itr.hasNext()){
//				strTmp += itr.next();
//			}
//			strTmp += "\n" + ((AtFormula)(elem.f1)).strPre +"\n";
//			if (elem.f2 instanceof CpxFormula) {
//			strTmp += "  if\n" 
//					+ "  :: " + ((CpxFormula)(elem.f2)).strPre + " ->\n"
//					+ iTransition.getName() + "_is_enabled = true\n"
//					+ "  :: else -> skip\n"
//					+ "  fi\n"
//			    	+ "}\n";
//			}
//			else if (elem.f2 instanceof AtFormula) {
//				strTmp += "\n" +  ((AtFormula)(elem.f2)).strPre;
//				strTmp += "-> " + iTransition.getName()
//						+ "_is_enabled = true\n}\n";
//			}
//			else if (elem.f2 instanceof CpFormula) {
//				strTmp += "  if\n" 
//						+ "  :: " + ((CpFormula)(elem.f2)).strPre + " ->\n"
//						+ iTransition.getName() + "_is_enabled = true\n"
//						+ "  :: else -> skip\n"
//						+ "  fi\n"
//				    	+ "}\n";
//			}
//			
//			elem.strPre = strTmp;
//			String strPost1 = "", strPost2 = "";
//			if (elem.f1 instanceof CpFormula) {
//				strPost1 = ((CpFormula)(elem.f1)).strPost;
//			}
//			else if (elem.f1 instanceof AtFormula) {
//				strPost1 = ((AtFormula)(elem.f1)).strPost;
//			}
//			if (elem.f2 instanceof CpFormula) {
//				strPost2 = ((CpFormula)(elem.f2)).strPost;
//			}
//			if (elem.f2 instanceof CpxFormula) {
//				strPost2 = ((CpxFormula)(elem.f2)).strPost;
//			}
//			else if (elem.f2 instanceof AtFormula) {
//				strPost2 = ((AtFormula)(elem.f2)).strPost;
//			}
//				
//			elem.strPost = strPost1 + ";\n" + strPost2;
//			elem.strPost = "inline fire_" + iTransition.getName() + "(){\n"
//					+ elem.strPost 
//					+ iTransition.getName() + "_is_enabled = false\n"
//					+ "}\n";
//		} 
//		else {
//			String strPre1 = "", strPre2 = "", strPost1 = "", strPost2 = "";
//			if (elem.f1 instanceof CpFormula) {
//				strPre1 = ((CpFormula)(elem.f1)).strPre;
//				strPost1 = ((CpFormula)(elem.f1)).strPost;
//			}
//			else if (elem.f1 instanceof CpxFormula) {
//				strPre1 = ((CpxFormula)(elem.f1)).strPre;
//				strPost1 = ((CpxFormula)(elem.f1)).strPost;
//			}
//			else if (elem.f1 instanceof AtFormula) {
//				strPre1 = ((AtFormula)(elem.f1)).strPre;
//				strPost1 = ((AtFormula)(elem.f1)).strPost;
//			}
//			if (elem.f2 instanceof CpFormula) {
//				strPre2 = ((CpFormula)(elem.f2)).strPre;
//				strPost2 = ((CpFormula)(elem.f2)).strPost;
//			}
//			else if (elem.f2 instanceof CpxFormula) {
//				strPre2 = ((CpxFormula)(elem.f2)).strPre;
//				strPost2 = ((CpxFormula)(elem.f2)).strPost;
//			}
//			else if (elem.f2 instanceof AtFormula) {
//				strPre2 = ((AtFormula)(elem.f2)).strPre;
//				strPost2 = ((AtFormula)(elem.f2)).strPost;
//			}
//			
//			if (strPre2.isEmpty() == false)
//				elem.strPre = strPre1 + " && " + strPre2;
//			else
//				elem.strPre = strPre1;
//			if (strPost2.isEmpty() == false)
//				elem.strPost = strPost1 + ";\n " + strPost2;
//			else
//				elem.strPost = strPost1 + ";\n ";
//		}
	}

	@Override
	public void visit(BraceTerm elem) {
		elem.t.accept(this);
		
		if(elem.t instanceof VariableTerm){
			elem.varName = ((VariableTerm)(elem.t)).pVarName;
			elem.isUserVariable = ((VariableTerm)(elem.t)).isUserVariable;
			elem.placeName = ((VariableTerm)(elem.t)).placeName;
		}

	}

	@Override
	public void visit(BraceTerms elem) {
		elem.ts.accept(this);
		elem.str = elem.ts.str;
		elem.placeName = elem.ts.placeName;
	}

	@Override
	public void visit(ComplexFormula elem) {
		
		elem.q.accept(this);
		elem.uv.accept(this);
		elem.d.accept(this);
		elem.v.accept(this);
	

		String userVariable = ((UserVariable)(elem.uv)).s;
		String powerSetVariable = ((IdVariable)(elem.v)).key;
		String vPlaceName = "";
		//find user variable matched place
		Iterator<Arc> itr = iTransition.getArcInList().iterator();//The powerset must be in input places
		while(itr.hasNext()){
			Arc thisArc = itr.next();
			if (thisArc.getVar().equals(((IdVariable)(elem.v)).key)) {
				vPlaceName = thisArc.getSource().getName();

				UserVarType tempUserVar = new UserVarType(userVariable, powerSetVariable, vPlaceName);
				this.arrUserVar.add(tempUserVar);
				break;
			}
		}
//		if (cpx_index == 0) {
//			cpx_PlaceName1 = vPlaceName;
//			cpx_v1 = ((UserVariable)(elem.uv)).s;
//			cpx_index++;
//		}
//		else if (cpx_index == 1) {
//			cpx_PlaceName2 = vPlaceName;
//			cpx_v2 = ((UserVariable)(elem.uv)).s;
//			cpx_index++;
//		}
		 
		elem.f.accept(this);
		
//		cpx_index--;
		
		String formulaPre = "";
		String formulaPost = "";
		
		if(elem.f instanceof CpxFormula){
			formulaPre = ((CpxFormula)(elem.f)).strPre;
			formulaPost = ((CpxFormula)(elem.f)).strPost;
		}
		else if (elem.f instanceof CpFormula){
			formulaPre = ((CpFormula)(elem.f)).strPre;
			formulaPost = ((CpFormula)(elem.f)).strPost;
		}
		
		//write promela for complex formula
		elem.strPre = "atomic{\n";
		elem.strPre += "	pick(var_"+vPlaceName+", place_"+vPlaceName
			+", "+vPlaceName+");\n";
		elem.strPre += "	place_"+vPlaceName+"?<"+vPlaceName+">;\n";
		elem.strPre += "	"+formulaPre+"\n";
		elem.strPre += "	}";
		
		elem.strPost = formulaPost;
	}

	@Override
	public void visit(Diff elem) {
		elem.t1.accept(this);
		elem.t2.accept(this);
		elem.str = "";
		String t1PlaceName = "";
		String t2PlaceName = "";
//		assert pname_union_status;
		
		if(elem.t1 instanceof VariableTerm){
			t1PlaceName = ((VariableTerm)(elem.t1)).placeName;
			if(elem.t2 instanceof ExpTerm){
				t2PlaceName = ((ExpTerm)(elem.t2)).placeName;
				if(t1PlaceName.equalsIgnoreCase(t2PlaceName) && !("").equals(t1PlaceName)){
					elem.str += "	place_"+t1PlaceName+"?"+t1PlaceName+";\n";
				}
				
			}
		}
		
		if(!("").equals(t1PlaceName)){
			this.tempPlaceName = t1PlaceName;
			elem.placeName = t1PlaceName;
		}
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
		
		//judge eq is post condition or a eq relation, 
		//determined only by t1
		boolean postcond = false;
		
		if (elem.t1 instanceof VariableTerm)
		{
			postcond = ((VariableTerm)(elem.t1)).postcond;
		}
		else if (elem.t1 instanceof ExpTerm)
		{
			postcond = ((ExpTerm)(elem.t1)).postcond;
		}
		
		/**
		 * written by Su Liu
		 */
		//when eq means relation
		if(postcond == false){
			if((elem.t1 instanceof VariableTerm) && (elem.t2 instanceof VariableTerm)){
				elem.strPre = ((VariableTerm)(elem.t1)).pVarName + " == " + 
					((VariableTerm)(elem.t2)).pVarName;
			}else if((elem.t1 instanceof VariableTerm) && (elem.t2 instanceof ConstantTerm)){
				elem.strPre = ((VariableTerm)(elem.t1)).pVarName + " == " + 
					((ConstantTerm)(elem.t2)).var_key;
			}else if((elem.t1 instanceof VariableTerm) && (elem.t2 instanceof ExpTerm)){
				elem.strPre = ((VariableTerm)(elem.t1)).pVarName + " == " + 
				((ExpTerm)(elem.t2)).str;
			}
		}
		
		//when eq means post assignment
		if (postcond == true)
		{
			
			//t1=t2, each field of t2 assign to t1
//			elem.strPost = elem.t1.placeName+"="+elem.t2.placeName+"\n";
			elem.strPost = "";
			Iterator<Arc> itr_out = iTransition.getArcOutList().iterator();
			Place thisPlace = null;
			while (itr_out.hasNext()) {//get this place
				Arc thisArc = itr_out.next();
				if(thisArc.getTarget().getName().equalsIgnoreCase(elem.t1.placeName)){
					thisPlace = (Place) thisArc.getTarget();
					break;
				}
			}
			
			
			if(elem.t1 instanceof VariableTerm){
				if(((VariableTerm)(elem.t1)).kind == 0){//left  hand side is idVariable
					if(elem.t2 instanceof VariableTerm){
						if(((VariableTerm)(elem.t2)).kind == 0){//right is idVariable
							if(thisPlace != null){
								Vector<String> types = thisPlace.getDataType().getTypes();
								for (int j = 0; j < types.size(); j++){
									elem.strPost += "  "+elem.t1.placeName+"."+elem.t1.placeName+"_field"
										+Integer.toString(j+1)+" = "+elem.t2.placeName+"."
										+elem.t2.placeName+"_field"+Integer.toString(j+1)+";\n";
								}
							}
						}else if(((VariableTerm)(elem.t2)).kind == 1){//right is index Variable
							if(thisPlace != null){
								elem.strPost += "  "+elem.t1.placeName+"."+elem.t1.placeName+"_field1"
									+" = "+((VariableTerm)(elem.t2)).pVarName+";\n";
							}
						}
					}
					else if(elem.t2 instanceof ExpTerm){
						if(thisPlace != null){
							elem.strPost += "  "+elem.t1.placeName+"."+elem.t1.placeName+"_field1"
									+" = "+((ExpTerm)(elem.t2)).str+";\n";
						}
					}else if(elem.t2 instanceof ConstantTerm){
						if(thisPlace != null){
							elem.strPost += "  "+elem.t1.placeName+"."+elem.t1.placeName+"_field1"
							+" = "+((ConstantTerm)(elem.t2)).var_key+";\n";
						}
					}
				}else if(((VariableTerm)(elem.t1)).kind == 1){
					if(elem.t2 instanceof VariableTerm){
						if(((VariableTerm)(elem.t2)).kind == 0){//right is idVariable
							if(thisPlace != null){
								elem.strPost += "		"+((VariableTerm)(elem.t1)).pVarName+"="
										+((VariableTerm)(elem.t2)).pVarName+";\n";
							}
						}else if(((VariableTerm)(elem.t2)).kind == 1){//right is index Variable
							if(thisPlace != null){
								elem.strPost += "		"+((VariableTerm)(elem.t1)).pVarName+" = "
									+((VariableTerm)(elem.t2)).pVarName+";\n";
							}
						}
					}
					else if(elem.t2 instanceof ExpTerm){
						if(thisPlace != null){
							elem.strPost += "		"+((VariableTerm)(elem.t1)).pVarName+" = "
									+((ExpTerm)(elem.t2)).str+";\n";
						}
					}else if(elem.t2 instanceof ConstantTerm){
						if(thisPlace != null){
							elem.strPost += "		"+((VariableTerm)(elem.t1)).pVarName
							+" = "+((ConstantTerm)(elem.t2)).var_key+";\n";
						}
					}
				}
			}
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
		
		boolean postcond = false;
		
		if (elem.t1 instanceof VariableTerm)
		{
			postcond = ((VariableTerm)(elem.t1)).postcond;
		}
		else if (elem.t1 instanceof ExpTerm)
		{
			postcond = ((ExpTerm)(elem.t1)).postcond;
		}
		
		if (postcond == false)
		{
			//precondition
			elem.strPre = elem.t1.str + " >= " + elem.t2.str;
			
		}
		else {
			//postcondition


		}
		
	}

	@Override
	public void visit(GtRel elem) {
		elem.t1.accept(this);
		elem.t2.accept(this);
		
		String left = "";
		String right = "";
		String placeName = "";
		
		if(elem.t1 instanceof VariableTerm){
			//if it is idVariable not index variable
			placeName = ((VariableTerm)(elem.t1)).placeName;
			left = placeName+"."+placeName+"_field1";
		}else if(elem.t1 instanceof ConstantTerm){
			left = ((ConstantTerm)(elem.t1)).var_key;
		}else if(elem.t1 instanceof ExpTerm){
			//TODO: expterm
		}else if(elem.t1 instanceof EmptyTerm){
			//TODO: emptyterm
		}
		
		if(elem.t2 instanceof VariableTerm){
			placeName = ((VariableTerm)(elem.t2)).placeName;
			right = placeName+"."+placeName+"_field1";
		}else if(elem.t2 instanceof ConstantTerm){
			right = ((ConstantTerm)(elem.t2)).var_key;
		}else if(elem.t2 instanceof ExpTerm){
			//TODO: expterm
		}else if(elem.t2 instanceof EmptyTerm){
			//TODO: emptyterm
		}
		
		elem.strPre = left+" > "+right;
	}

	@Override
	public void visit(Identifier elem) {

	}

	@Override
	public void visit(IdVariable elem) {
		Iterator<Arc> itr = iTransition.getArcList().iterator();
		Arc relatedArc = null;
		while(itr.hasNext()){
			Arc thisArc = itr.next();
			if (thisArc.getVar().equals(elem.key)) {
				relatedArc = thisArc;
				break;
			}
		}
		int place_size = 0;
		Vector<String> types;
		boolean isPowerset = false;
		if (relatedArc != null){
			if (relatedArc.getSource() instanceof Place) {
				types =  ((Place) relatedArc.getSource()).getDataType().getTypes();
				place_size = types.size();
				isPowerset=((Place) relatedArc.getSource()).getDataType().getPow();
			}
			else //(relatedArc.getTarget() instanceof Place)
			{
				types = ((Place) relatedArc.getTarget()).getDataType().getTypes();
				place_size = types.size();
				isPowerset=((Place) relatedArc.getTarget()).getDataType().getPow();
			}
			
			if (isPowerset == false)
				for (int i = 1; i <= place_size; i++) { // chang i=2 to i=1
					VarDef vd = new VarDef();
					vd.strVar = elem.key + "_field" + i;
					if (types.get(i - 1).equals("string"))
						vd.type = PromelaType.pSHORT;
					else
						vd.type = PromelaType.pINT;
					add_arrVar(vd);
				}
		}
		else {
			// it can be a user variable in complex formula 
			// e.g. m belongs A, then m cannot be found in arcs
			// the user variable of complex formula is stored into cpx_v1, cpx_v2
		}

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
		
		Iterator<Arc> itr = iTransition.getArcList().iterator();
		Arc relatedArc = null;
		while(itr.hasNext()){
			Arc thisArc = itr.next();
			if (thisArc.getVar().equals(elem.i.key)) {
				relatedArc = thisArc;
				break;
			}
		}
		int place_size = 0;
		Vector<String> types;
		if (relatedArc != null){
			if (relatedArc.getSource() instanceof Place) {
				types =  ((Place) relatedArc.getSource()).getDataType().getTypes();
				place_size = types.size();
			}
			else //(relatedArc.getTarget() instanceof Place)
			{
				types = ((Place) relatedArc.getTarget()).getDataType().getTypes();
				place_size = types.size();
			}
			
			for (int i=2; i<=place_size; i++){
				VarDef vd = new VarDef();
				vd.strVar = elem.i.key + "_field" + i; 
				if (types.get(i-1).equals("string"))
					vd.type = PromelaType.pSHORT;
				else
					vd.type = PromelaType.pINT;
				add_arrVar(vd);
			}
		}
		else {
			// it can be a user variable in complex formula 
			// e.g. m belongs A, then m cannot be found in arcs
			// the user variable of complex formula is stored into cpx_v1, cpx_v2
		}

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

	}

	@Override
	public void visit(LtRel elem) {
		elem.t1.accept(this);
		elem.t2.accept(this);

	}

	@Override
	public void visit(Minus elem) {
		elem.t1.accept(this);
		elem.t2.accept(this);
		elem.str = elem.t1.str + " - " + elem.t2.str;

	}

	@Override
	public void visit(Mod elem) {
		elem.t1.accept(this);
		elem.t2.accept(this);
		String left = "";
		String right = "";
		
		if(elem.t1 instanceof VariableTerm){
			left = ((VariableTerm)(elem.t1)).pVarName;
		}else if(elem.t1 instanceof ConstantTerm){
			left = ((ConstantTerm)(elem.t1)).var_key;
		}else if(elem.t1 instanceof ExpTerm){
			left = ((ExpTerm)(elem.t1)).str;
		}else if(elem.t1 instanceof EmptyTerm){
			//TODO: emptyterm
		}
		
		if(elem.t2 instanceof VariableTerm){
			right = ((VariableTerm)(elem.t2)).pVarName;
		}else if(elem.t2 instanceof ConstantTerm){
			right = ((ConstantTerm)(elem.t2)).var_key;
		}else if(elem.t2 instanceof ExpTerm){
			right = ((ExpTerm)(elem.t2)).str;
		}else if(elem.t2 instanceof EmptyTerm){
			//TODO: emptyterm
		}
		
		elem.str = left+ " % " + right;
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
		
		boolean postcond = false;
		
		if((elem.t1 instanceof VariableTerm) && (elem.t2 instanceof VariableTerm)){
			elem.strPre = ((VariableTerm)(elem.t1)).pVarName + " != " + 
				((VariableTerm)(elem.t2)).pVarName;
		}else if((elem.t1 instanceof VariableTerm) && (elem.t2 instanceof ConstantTerm)){
			elem.strPre = ((VariableTerm)(elem.t1)).pVarName + " != " + 
				((ConstantTerm)(elem.t2)).var_key;
		}else if((elem.t1 instanceof VariableTerm) && (elem.t2 instanceof ExpTerm)){
			elem.strPre = ((VariableTerm)(elem.t1)).pVarName + " != " + 
			((ExpTerm)(elem.t2)).str;
		}
		
//		if (elem.t1 instanceof VariableTerm)
//		{
//			postcond = ((VariableTerm)(elem.t1)).postcond;
//		}
//		else if (elem.t1 instanceof ExpTerm)
//		{
//			postcond = ((ExpTerm)(elem.t1)).postcond;
//		}
//		
//		if (postcond == false)
//		{
//			//precondition
//			elem.strPre = elem.t1.str + " != " + elem.t2.str;
//			
//		}
//		else {
//			//postcondition
//
//
//		}
		
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
		
		String leftPre = "";
		String rightPre = "";
		String leftPost = "";
		String rightPost = "";
		
		if(elem.f1 instanceof AtFormula){
			leftPre = ((AtFormula)(elem.f1)).strPre;
			leftPost = ((AtFormula)(elem.f1)).strPost;
		}else if(elem.f1 instanceof CpFormula){
			leftPre = ((CpFormula)(elem.f1)).strPre;
			leftPost = ((CpFormula)(elem.f1)).strPost;
		}else if(elem.f1 instanceof CpxFormula){
			//TODO: CpxFormula
		}
		
		if(elem.f2 instanceof AtFormula){
			rightPre = ((AtFormula)(elem.f2)).strPre;
			rightPost = ((AtFormula)(elem.f2)).strPost;
		}else if(elem.f2 instanceof CpFormula){
			rightPre = ((CpFormula)(elem.f2)).strPre;
			rightPost = ((CpFormula)(elem.f2)).strPost;
		}else if(elem.f2 instanceof CpxFormula){
			//TODO: CpxFormula
		}
		
		if(("").equals(leftPre))leftPre = "false";
		else if("".equals(rightPre))rightPre = "false";	
		
		elem.strPre = "("+leftPre+" || "+rightPre+")";
		
		if(!("").equals(leftPost)){
			elem.strPost = leftPost;
		}else if(!("").equals(rightPost)){
			elem.strPost = rightPost;
		}else elem.strPost = "";
	}

	@Override
	public void visit(Plus elem) {
		elem.t1.accept(this);
		elem.t2.accept(this);
		
		String left = "";
		String right = "";
		
		if(elem.t1 instanceof VariableTerm){
			left = ((VariableTerm)(elem.t1)).pVarName;
		}else if(elem.t1 instanceof ConstantTerm){
			left = ((ConstantTerm)(elem.t1)).var_key;
		}else if(elem.t1 instanceof ExpTerm){
			left = ((ExpTerm)(elem.t1)).str;
		}else if(elem.t1 instanceof EmptyTerm){
			//TODO: emptyterm
		}
		
		if(elem.t2 instanceof VariableTerm){
			right = ((VariableTerm)(elem.t2)).pVarName;
		}else if(elem.t2 instanceof ConstantTerm){
			right = ((ConstantTerm)(elem.t2)).var_key;
		}else if(elem.t2 instanceof ExpTerm){
			right = ((ExpTerm)(elem.t2)).str;
		}else if(elem.t2 instanceof EmptyTerm){
			//TODO: emptyterm
		}
		
		elem.str = "("+left+ " + " + right+")";
		
	}

	@Override
	public void visit(TermRest elem) {
		elem.t.accept(this);
		elem.str = elem.t.str;
	}

	@Override
	public void visit(Terms elem) {
		elem.t.accept(this);
		elem.str = "";
		String tsPlaceName = this.tempPlaceName; //get the related Place type of this braced terms
		elem.placeName = tsPlaceName;
		String tPlaceVarName;
		String tConstantKey;
		String fieldCount = "1";
//		elem.str = elem.t.str;
		if(elem.t instanceof VariableTerm){
			tPlaceVarName = ((VariableTerm)(elem.t)).pVarName;
			if(!("").equals(tsPlaceName)){
				if(((VariableTerm)(elem.t)).isUserVariable){
					elem.str += "	"+tsPlaceName+"."+tsPlaceName+"_field1 = "
									+tPlaceVarName+";\n";
				}
			}
		}else if(elem.t instanceof ConstantTerm){
			tConstantKey = ((ConstantTerm)(elem.t)).var_key;
			if(!("").equals(tsPlaceName)){
				elem.str += "	"+tsPlaceName+"."+tsPlaceName+"_field1 = "
						+tConstantKey+";\n";
			}
		}
		
		int i=0;
		for (i=0; i<elem.tr.size(); i++)
		{
			((TermRest)(elem.tr.list.get(i))).accept(this);
//			elem.str += "," + ((TermRest)(elem.tr.list.get(i))).str;
			Term t = elem.tr.list.get(i).t;
			if(t instanceof VariableTerm){
				tPlaceVarName = ((VariableTerm)t).pVarName;
				if(!("").equals(tsPlaceName)){
					if(((VariableTerm)t).isUserVariable){
						 fieldCount = Integer.toString(i+2);
						elem.str += "	"+tsPlaceName+"."+tsPlaceName+"_field"+fieldCount+" = "
										+tPlaceVarName+";\n";
					}
				}
			}else if(t instanceof ConstantTerm){
				tConstantKey = ((ConstantTerm)t).var_key;
				if(!("").equals(tsPlaceName)){
					 fieldCount = Integer.toString(i+2);
					elem.str += "	"+tsPlaceName+"."+tsPlaceName+"_field"+fieldCount+" = "
							+tConstantKey+";\n";
				}
			}
		}
	}

	@Override
	public void visit(True elem) {

	}

	@Override
	public void visit(Union elem) {
		elem.t1.accept(this);
		elem.t2.accept(this);
		elem.str = "";
		String t1PlaceName = "";
		String t2PlaceName = "";
		
		if(elem.t1 instanceof ExpTerm){
			t1PlaceName = ((ExpTerm)(elem.t1)).placeName;
			if(elem.t2 instanceof ExpTerm){
				t2PlaceName = ((ExpTerm)(elem.t2)).placeName;
				
				elem.str = ((ExpTerm)(elem.t1)).str;
				elem.str += ((ExpTerm)(elem.t2)).str;
				elem.str += "	place_"+t1PlaceName+"!"+t2PlaceName+";\n";
			}
		}
		
		if(!("").equals(t1PlaceName))
		elem.placeName = t1PlaceName;
		
//		assert pname_union_status;
//		
//		elem.str = elem.t1.str + pname_union + "!" + elem.t2.str + ";\n";
//		pname_union_status = false; //as we are done with t2

	}

	@Override
	public void visit(UserVariable elem) {

	}

	@Override
	public void visit(ConstantTerm elem) {
		elem.c.accept(this);
		if (elem.c instanceof NumConstant)
		{
			elem.str = ((NumConstant)(elem.c)).num.n;
			elem.var_key = ((NumConstant)(elem.c)).num.n;
		}
		else if (elem.c instanceof StrConstant)
		{
			elem.str = ((StrConstant)(elem.c)).str;
			elem.var_key = ((StrConstant)(elem.c)).str;
		}
		
	}

	@Override
	public void visit(ExpTerm elem) {
		elem.e.accept(this);
		if (elem.e instanceof RExp) {
			elem.strPre = ((RExp)(elem.e)).strPre;
			elem.strPost = ((RExp)(elem.e)).strPost;
		}
		else if (elem.e instanceof SExp) {
			elem.varName = ((SExp)(elem.e)).varName;
			elem.isUserVariable = ((SExp)(elem.e)).isUserVariable;
//			if(((SExp)(elem.e)).isUserVariable){
//				elem.placeName  = ((SExp)(elem.e)).placeName;
//			}
			elem.placeName = ((SExp)(elem.e)).placeName;
			elem.str = ((SExp)(elem.e)).str;
		}
		else if (elem.e instanceof AExp) {
			elem.str = ((AExp)(elem.e)).str;
		}
	}

	@Override
	public void visit(VariableTerm elem) {
		elem.v.accept(this);
		boolean isInArcOutVarList = false;
		boolean isPowerSet = false;
		int size = 0;
		String var_key = "";

		if (elem.v instanceof IdVariable) {
			var_key = ((IdVariable) elem.v).key;

			for (String s : iTransition.getArcOutVarList()) {
				if (s.equals(var_key))
					isInArcOutVarList = true;
			}
		}

		if (elem.v instanceof IndexVariable) {
			var_key = ((IndexVariable) elem.v).key;
			for (String s : iTransition.getArcOutVarList()) {
				if (s.equals(var_key))
					isInArcOutVarList = true;
			}
		}
		
		elem.postcond = isInArcOutVarList;
		

		if (elem.v instanceof IndexVariable)
		{
			elem.kind = 1;
			elem.str = ((IndexVariable) (elem.v)).key;
			elem.index = ((IndexVariable) (elem.v)).index;
			
			String vPlaceName = "";
			Iterator<Arc> itr_out = iTransition.getArcOutList().iterator();
			while (itr_out.hasNext()) {
				Arc thisArc = itr_out.next();
				if (thisArc.getVar().equals(
						((IndexVariable) (elem.v)).key)) {
					//For A2', its target is a place, source is a transition
					vPlaceName = thisArc.getTarget().getName(); 
					break;
				}
			}
			
			Iterator<Arc> itr_in = iTransition.getArcInList().iterator();
			while (itr_in.hasNext()) {
				Arc thisArc = itr_in.next();
				if (thisArc.getVar().equals(
						((IndexVariable) (elem.v)).key)) {
					//For A2', its target is a place, source is a transition
					vPlaceName = thisArc.getSource().getName(); 
					break;
				}
			}
			
			if(!("").equals(vPlaceName)){
				elem.placeName = vPlaceName;
				elem.pVarName = elem.placeName+"."+elem.placeName+"_field"
					+Integer.toString(elem.index);
			}else {
				// when vPlaceName is null, means the var is user variable
				elem.isUserVariable = true;
				elem.placeName = getPlaceNameByUserVariable(elem.str);
				elem.pVarName = elem.placeName+"."+elem.placeName+"_field"
						+Integer.toString(elem.index);
			}
		}
		
		else if (elem.v instanceof IdVariable) {
			elem.kind = 0;
			elem.str = ((IdVariable) (elem.v)).key;
			elem.var_key = ((IdVariable) (elem.v)).key;
			String vPlaceName = "";
			Iterator<Arc> itr_out = iTransition.getArcOutList().iterator();
			while (itr_out.hasNext()) {
				Arc thisArc = itr_out.next();
				if (thisArc.getVar().equals(
						((IdVariable) (elem.v)).key)) {
					//For A2', its target is a place, source is a transition
					vPlaceName = thisArc.getTarget().getName(); 
					isPowerSet = ((Place)(thisArc.getTarget())).getDataType().getPow();
					if(isPowerSet){
						size = ((Place)(thisArc.getTarget())).getToken().getTokenCount();
					}
					break;
				}
			}
			
			Iterator<Arc> itr_in = iTransition.getArcInList().iterator();
			while (itr_in.hasNext()) {
				Arc thisArc = itr_in.next();
				if (thisArc.getVar().equals(
						((IdVariable) (elem.v)).key)) {
					//For A2', its target is a place, source is a transition
					vPlaceName = thisArc.getSource().getName(); 
					isPowerSet = ((Place)(thisArc.getSource())).getDataType().getPow();
					if(isPowerSet){
						size = ((Place)(thisArc.getSource())).getToken().getTokenCount();
					}
					break;
				}
			}
			
			if(!("").equals(vPlaceName)){
				elem.placeName = vPlaceName;
				elem.pVarName = elem.placeName+"."+elem.placeName+"_field1";
				elem.isPowerSet = isPowerSet;
				elem.size = size;
			}else{
				// when vPlaceName is null, means the var is user variable
				elem.isUserVariable = true;
				elem.placeName = getPlaceNameByUserVariable(elem.str);
				elem.pVarName = elem.placeName+"."+elem.placeName+"_field1";
//				elem.isPowerSet = isPowerSet;
			}
				
		}
	}
	
	@Override
	public void visit(StrConstant elem) {
		
	}
	
	public void visit(AExp elem){
		elem.ae.accept(this);
		
		if(elem.ae instanceof Minus){
			elem.str = ((Minus)(elem.ae)).str;
		}else if(elem.ae instanceof Plus){
			elem.str = ((Plus)(elem.ae)).str;
		}else if(elem.ae instanceof Mul){
			elem.str = ((Mul)(elem.ae)).str;
		}else if(elem.ae instanceof Div){
			elem.str = ((Div)(elem.ae)).str;
		}else if(elem.ae instanceof Mod){
			elem.str = ((Mod)(elem.ae)).str;
		}else if(elem.ae instanceof NegExp){
			elem.str = ((NegExp)(elem.ae)).str;
		}
	}
	
	public void visit(RExp elem){
		elem.re.accept(this);
		if (elem.re instanceof EqRel) {
			elem.strPre = ((EqRel)(elem.re)).strPre;
			elem.strPost = ((EqRel)(elem.re)).strPost;
		}
		else if (elem.re instanceof NeqRel) {
			elem.strPre = ((NeqRel)(elem.re)).strPre;
			elem.strPost = ((NeqRel)(elem.re)).strPost;
		}else if (elem.re instanceof GeqRel) {
			elem.strPre = ((GeqRel)(elem.re)).strPre;
			elem.strPost = ((GeqRel)(elem.re)).strPost;
		}else if (elem.re instanceof GtRel) {
			elem.strPre = ((GtRel)(elem.re)).strPre;
//			elem.strPost = ((GtRel)(elem.re)).strPost;
		}else if (elem.re instanceof LtRel) {
			elem.strPre = ((LtRel)(elem.re)).strPre;
//			elem.strPost = ((LtRel)(elem.re)).strPost;
		}else if (elem.re instanceof LeqRel) {
			elem.strPre = ((LeqRel)(elem.re)).strPre;
//			elem.strPost = ((LeqRel)(elem.re)).strPost;
		}else if (elem.re instanceof InRel) {
			elem.strPre = ((InRel)(elem.re)).strPre;
		}else if (elem.re instanceof NinRel) {
			elem.strPre = ((NinRel)(elem.re)).strPre;
		}
	}
	
	public void visit(SExp elem){
		elem.se.accept(this);
//		elem.str = elem.se.str;

		if(elem.se instanceof BraceTerm){
			elem.varName = ((BraceTerm)(elem.se)).varName;
			elem.placeName = ((BraceTerm)(elem.se)).placeName;
			elem.str = ((BraceTerm)(elem.se)).str;
		}else if(elem.se  instanceof BraceTerms){
			elem.placeName = ((BraceTerms)(elem.se)).placeName;
			elem.str = ((BraceTerms)(elem.se)).str;
		}else if(elem.se instanceof Diff){
			elem.placeName = ((Diff)(elem.se)).placeName;
			elem.str = ((Diff)(elem.se)).str;
		}else if(elem.se instanceof Union){
			elem.placeName = ((Union)(elem.se)).placeName;
			elem.str = ((Union)(elem.se)).str;
		}
	}

	@Override
	public void visit(AtomicTerm elem) {
		elem.t.accept(this);
		
		if(elem.t instanceof ConstantTerm){
			elem.strPre = ((ConstantTerm)(elem.t)).str;
		}else if(elem.t instanceof ExpTerm){
			elem.strPre = ((ExpTerm)(elem.t)).strPre;
			elem.strPost = ((ExpTerm)(elem.t)).strPost;
		}else errorMsg.error(elem.pos, "AtomicTerm::Cannot be VariableTerm or tree type mismatch!");
		
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
		
//		elem.strPre = elem.af.strPre;
//		elem.strPost = elem.af.strPost;
		
		if(elem.af instanceof NotFormula){
			elem.str = ((NotFormula)(elem.af)).str;
		}else if(elem.af instanceof AtomicTerm){
			elem.strPre = ((AtomicTerm)(elem.af)).strPre;
			elem.strPost = ((AtomicTerm)(elem.af)).strPost;
		}else errorMsg.error(elem.pos, "AtFormula::tree type mismatch!");
	}



	@Override
	public void visit(CpFormula elem) {
		
		elem.cf.treeLevel = elem.treeLevel;
		elem.cf.accept(this);
		
//		elem.strPre = elem.cf.strPre;
//		elem.strPost = elem.cf.strPost;
	
		if(elem.cf instanceof AndFormula){
			elem.strPre = ((AndFormula)(elem.cf)).strPre;
			elem.strPost = ((AndFormula)(elem.cf)).strPost;
		}else if(elem.cf instanceof OrFormula){
			elem.strPre = ((OrFormula)(elem.cf)).strPre;
			elem.strPost = ((OrFormula)(elem.cf)).strPost;
		}else if(elem.cf instanceof ImpFormula){
			elem.strPre = ((ImpFormula)(elem.cf)).strPre;
			elem.strPost = ((ImpFormula)(elem.cf)).strPost;
		}else if(elem.cf instanceof EquivFormula){
			elem.strPre = ((EquivFormula)(elem.cf)).strPre;
			elem.strPost = ((EquivFormula)(elem.cf)).strPost;
		}else errorMsg.error(elem.pos, "CpFormula::tree type mismatch!");		
	}

	@Override
	public void visit(CpxFormula elem) {
		elem.cpf.treeLevel = elem.treeLevel;
		elem.cpf.accept(this);
		
		elem.strPre = elem.cpf.strPre;
		elem.strPost = elem.cpf.strPost;
//		if(elem.cpf instanceof AtFormula){
//			
//		}
	}

	@Override
	public void visit(Sentence elem) {
//		if (elem.f instanceof AtFormula) {
//			((AtFormula) (elem.f)).treeLevel = 1;
//		} else if (elem.f instanceof CpFormula) {
//			((CpFormula) (elem.f)).treeLevel = 1;
//		} else if (elem.f instanceof CpxFormula) {
//			((CpxFormula) (elem.f)).treeLevel = 1;
//		}
		
		elem.f.accept(this);
		
		if(elem.f instanceof AtFormula){
			elem.strPre = ((AtFormula)(elem.f)).strPre;
			elem.strPost = ((AtFormula)(elem.f)).strPost;
		}else if(elem.f instanceof CpFormula){
			elem.strPre = ((CpFormula)(elem.f)).strPre;
			elem.strPost = ((CpFormula)(elem.f)).strPost;
		}else if(elem.f instanceof CpxFormula){
			elem.strPre = ((CpxFormula)(elem.f)).strPre;
			elem.strPost = ((CpxFormula)(elem.f)).strPost;
		}
	}

	@Override
	public void visit(Empty elem) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(EmptyTerm elem) {
		// TODO Auto-generated method stub
		
	}
	
	private String getPlaceNameByUserVariable(String userVar){
		String placeName = "";
		
		Iterator<UserVarType> itr_uv = this.arrUserVar.iterator();
		while(itr_uv.hasNext()){
			UserVarType uv = itr_uv.next();
			if(uv.getUserVariable().equals(userVar)){
				placeName = uv.getPlaceName();
			}
		}
		
		return placeName;
	}

}
