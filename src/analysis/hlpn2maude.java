package analysis;

import java.util.ArrayList;
import java.util.Vector;

import ltlparser.PropertyFormulaToPromela;
import formulaParser.ErrorMsg;
import formulaParser.Formula2Maude;
import formulaParser.Formula2Promela;
import formulaParser.Interpreter;
import formulaParser.Parse;
//import formulaParser.Printer;
import formulaParser.formulaAbsyntree.Sentence;
import pipe.dataLayer.BasicType;
import pipe.dataLayer.DataLayer;
import pipe.dataLayer.Place;
import pipe.dataLayer.Token;
import pipe.dataLayer.Transition;
import pipe.dataLayer.abToken;

/**
 * Get String output as Maude, from a DataLayer of Petri net
 * @author Reng Zeng, Zhuo Sun, 2015
 */

public class hlpn2maude {

	public DataLayer dataLayer;
	public String propertyFormula = "";
	public String sMaude = "";
	
	public hlpn2maude(DataLayer data, String formula){
		dataLayer = data;
		propertyFormula = formula;
		//Promela definition
		defineBound();		
		definePlaceDataStructure();
		definePlaceChan();
		defineNonDetPickFunc();
		
		//define transition functions
		defineIsEnabledFunc();
		defineFireFunc();
		defineTransFunc();
		
		//define process
		defineMainProcess();
		defineInitFunc();
		
		//define property formula
		defineFormula();
	}

	private void defineBound(){
		int placeSize = dataLayer.getPlacesCount();
		Place[] places = dataLayer.getPlaces();
		
		String placeName;		
		for(int placeNo = 0; placeNo < placeSize; placeNo++){
			placeName = places[placeNo].getName();
			sMaude += "#define Bound_" + placeName + " " + places[placeNo].getCapacity() + "\n";			
		}
		
		sMaude += "\n";
	}
	
	private void definePlaceDataStructure(){
		int placeSize = dataLayer.getPlacesCount();
		Place[] places = dataLayer.getPlaces();
		
		String placeName;		
		for(int placeNo = 0; placeNo < placeSize; placeNo++){
			placeName = places[placeNo].getName();
			sMaude += "typedef " + "type_" + placeName + " " + "{" + "\n";
			
			Vector<String> types = places[placeNo].getDataType().getTypes();
			
			for (int j = 0; j < types.size(); j++) {
				sMaude += "  ";
				if (types.get(j).equals("string"))
					sMaude += "short";
				else
					sMaude += types.get(j);
				
				sMaude += " " + placeName + "_field" + Integer.toString(j+1);
				
				if((j+1) != types.size())sMaude += ";\n";
			}
			
			sMaude += "\n};\n\n";
		}
	}
	
	private void definePlaceChan(){
		int placeSize = dataLayer.getPlacesCount();
		Place[] places = dataLayer.getPlaces();
		
		String placeName;
		for(int placeNo = 0; placeNo < placeSize; placeNo++){
			placeName = places[placeNo].getName();
			sMaude += "chan place_" + placeName + " = [Bound_" + placeName
			+ "] of {" + "type_" + placeName + "};\n";
		}
		
		sMaude += "\n";
	}
	
	private void defineNonDetPickFunc(){
		sMaude +="inline pick(var, place_chan, msg){\n";
		sMaude +="	var = 1;\n";
		sMaude +="	select(var:1..len(place_chan));\n";
		sMaude +="	do\n";
		sMaude +="	::(var > 1) -> place_chan?msg; place_chan!msg; var--\n";
		sMaude +="	::(var == 1) -> break\n";
		sMaude +="	od\n";
		sMaude +="}\n";
	}
	
	private void defineIsEnabledFunc(){
		int transSize = dataLayer.getTransitionsCount();
		Transition[] trans = dataLayer.getTransitions();
		String transName;
		
		for (int transNo = 0; transNo < transSize; transNo++){
			transName = trans[transNo].getName();
			sMaude += "inline is_enabled_" + transName + "() {\n";
			String else_temp = "";
			//declare local variables for input token
			ArrayList<Place> inputPlaces= trans[transNo].getPlaceInList();	
			
			//Test if all input places is empty
			for(int ipNo = 0; ipNo < inputPlaces.size(); ipNo++){
				String inPlaceName = inputPlaces.get(ipNo).getName();
				if(!inputPlaces.get(ipNo).getToken().getDataType().getPow()){
					if(ipNo == 0){
						sMaude += "  place_"+inPlaceName+"?["+inPlaceName+"]";
					}else{
						sMaude += " &&  place_"+inPlaceName+"?["+inPlaceName+"]";
					}
				}
			}
			sMaude += "\n	->\n";
			for(int ipNo = 0; ipNo < inputPlaces.size(); ipNo++){
				String inPlaceName = inputPlaces.get(ipNo).getName();
				if(!inputPlaces.get(ipNo).getToken().getDataType().getPow()){
					sMaude += "  place_"+inPlaceName+"?"+inPlaceName+";\n";
					
					if(ipNo == 0){
						else_temp += " place_"+inPlaceName+"!"+inPlaceName;
					}else{
						else_temp += ";\n		place_"+inPlaceName+"!"+inPlaceName;
					}
				}
			}
			sMaude +="	if\n";
			sMaude +="	:: ";
			
			//precondicion
			String formula = trans[transNo].getFormula();
			ErrorMsg errorMsg = new ErrorMsg(formula);
			Parse p = new Parse(formula, errorMsg);
			Sentence s = p.absyn;
			System.out.println(trans[transNo].getName());
			s.accept(new Formula2Maude(errorMsg, trans[transNo], 0));
//			s.accept(new Printer());
			
			if(!("").equals(s.strPre)){
				sMaude += "atomic{"+s.strPre+"}\n";
				sMaude += "		->"+transName+"_is_enabled = true\n";
			}else{
				sMaude += "true ->"+transName+"_is_enabled = true\n";
			}
			
			sMaude +="	:: else -> {"+ else_temp +"}\n";
			sMaude +="	fi\n";
			sMaude += "}\n";
		}
	}
	
	private void defineFireFunc(){
		int transSize = dataLayer.getTransitionsCount();
		Transition[] trans = dataLayer.getTransitions();
		String transName;
		for (int transNo = 0; transNo < transSize; transNo++){
			transName = trans[transNo].getName();
			sMaude += "inline fire_" + transName + "() {\n";
			
//			//declare local variables for output token
//			ArrayList<Place> outputPlaces= trans[transNo].getPlaceOutList();			
//			for(int opNo = 0; opNo < outputPlaces.size(); opNo++){
//				String outPlaceName = outputPlaces.get(opNo).getName();
//				sPromela += "  type_" + outPlaceName + " " +outPlaceName+";\n";
//			}
			
			//post condition
			String formula = trans[transNo].getFormula();
			ErrorMsg errorMsg = new ErrorMsg(formula);
			Parse p = new Parse(formula, errorMsg);
			Sentence s = p.absyn;
//			System.out.println(trans[transNo].getName());
			s.accept(new Formula2Maude(errorMsg, trans[transNo], 0));
			sMaude += s.strPost;
			
			ArrayList<Place> otPlaces= trans[transNo].getPlaceOutList();			
			for(int opNo = 0; opNo < otPlaces.size(); opNo++){
				String otPlaceName = otPlaces.get(opNo).getName();
				if(!otPlaces.get(opNo).getToken().getDataType().getPow()){
					sMaude += "  place_" + otPlaceName + "!" +otPlaceName+";\n";
				}
			}
			sMaude += "  "+transName+"_is_enabled = false\n";
			sMaude += "}\n";
		}

	}
	
	private void defineTransFunc(){
		int transSize = dataLayer.getTransitionsCount();
		Transition[] trans = dataLayer.getTransitions();
		String transName;
		for (int transNo = 0; transNo < transSize; transNo++){
			transName = trans[transNo].getName();
			sMaude += "inline " + transName + "() {\n";
			sMaude += "  is_enabled_"+transName+"();\n";
			sMaude += "  if\n";
			sMaude += "  ::  "+transName+"_is_enabled -> atomic{fire_"+transName+"()}\n";
			sMaude += "  ::  else -> skip\n";
			sMaude += "  fi\n";
			
			sMaude += "}\n";
		}
	}
	
	private void defineMainProcess(){

		int transSize = dataLayer.getTransitionsCount();
		Transition[] trans = dataLayer.getTransitions();
		String transName;
		
		sMaude += "proctype "+"Main() {\n";
		for (int transNo = 0; transNo < transSize; transNo++){
			transName = trans[transNo].getName();
			sMaude += "  bool "  + transName + "_is_enabled = false;\n";
		}
		
		//define local structure
		int placeSize = dataLayer.getPlacesCount();
		Place[] places = dataLayer.getPlaces();
		String placeName;		
		for(int placeNo = 0; placeNo < placeSize; placeNo++){
			placeName = places[placeNo].getName();
			sMaude += "  type_" + placeName + " " +placeName+";\n";
			boolean isPowerSet = places[placeNo].getToken().getDataType().getPow();
			if(isPowerSet){
				sMaude += "	int var_"+placeName+"=1;\n";
			}
		}
		
		sMaude += "\n  do\n";
		for (int transNo = 0; transNo < transSize; transNo++) {
			transName = trans[transNo].getName();
			sMaude += "  :: atomic{ " + transName + "() }\n";
		}
		sMaude += "  od\n";
		sMaude += "}\n";
	}
	
	private void defineInitFunc(){
		sMaude += "init {\n";
		
		int placeSize = dataLayer.getPlacesCount();
		Place[] places = dataLayer.getPlaces();
		
		String placeName;
		for(int placeNo = 0; placeNo < placeSize; placeNo++){
			placeName = places[placeNo].getName();
			sMaude += "  type_" + placeName + " " + placeName+";\n";
			Vector<Token> tokenList = places[placeNo].getToken().listToken;
			for(int i=0; i<tokenList.size(); i++){
				Token tempTok = tokenList.get(i);
				Vector<BasicType> btList = tempTok.Tlist;
				for(int j=0; j<btList.size(); j++){
					BasicType bt = btList.get(j);
					String value = "";
					if(bt.kind == 0){
						value = Integer.toString(bt.Tint);
					}else if(bt.kind == 1){
						value = bt.Tstring;
					}else System.out.println("Get basic type kind error!");
					sMaude += "  "+placeName+"."+placeName+"_field"+Integer.toString(j+1)+
						"="+value+";\n";
				}
				sMaude += "  place_"+placeName+"!"+placeName+";\n";
			}
		}
		
		sMaude += "run Main()\n";
		sMaude += "}\n";
	}
	
	private void defineFormula() {
//		if(!"".equals(propertyFormula)){
//		ltlparser.errormsg.ErrorMsg errorMsg = new ltlparser.errormsg.ErrorMsg(propertyFormula);
//		ltlparser.ParseLTL p = new ltlparser.ParseLTL(propertyFormula, errorMsg);
//		ltlparser.ltlabsyntree.LogicSentence s = p.absyn;
//		s.accept(new PropertyFormulaToPromela(errorMsg));
//		sPromela += s.formula;
//		}
		sMaude += "ltl f{"+ this.propertyFormula + "}";
	}
	
	public String getPromela()
	{
		return sMaude;
	}
}
