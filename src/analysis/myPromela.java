package analysis;

import java.util.ArrayList;
import java.util.Vector;

import ltlparser.PropertyFormulaToPromela;

import formulaParser.ErrorMsg;
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
 * Get String output as Promela, from a DataLayer of Petri net
 * @author Zhuo Sun, 2010
 * @modified by Su Liu 2012
 */

public class myPromela {

	public DataLayer dataLayer;
	public String propertyFormula = "";
	public String sPromela = "";
	
	public myPromela(DataLayer data, String formula){
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
			sPromela += "#define Bound_" + placeName + " " + places[placeNo].getCapacity() + "\n";			
		}
		
		sPromela += "\n";
	}
	
	private void definePlaceDataStructure(){
		int placeSize = dataLayer.getPlacesCount();
		Place[] places = dataLayer.getPlaces();
		
		String placeName;		
		for(int placeNo = 0; placeNo < placeSize; placeNo++){
			placeName = places[placeNo].getName();
			sPromela += "typedef " + "type_" + placeName + " " + "{" + "\n";
			
			Vector<String> types = places[placeNo].getDataType().getTypes();
			
			for (int j = 0; j < types.size(); j++) {
				sPromela += "  ";
				if (types.get(j).equals("string"))
					sPromela += "short";
				else
					sPromela += types.get(j);
				
				sPromela += " " + placeName + "_field" + Integer.toString(j+1);
				
				if((j+1) != types.size())sPromela += ";\n";
			}
			
			sPromela += "\n};\n\n";
		}
	}
	
	private void definePlaceChan(){
		int placeSize = dataLayer.getPlacesCount();
		Place[] places = dataLayer.getPlaces();
		
		String placeName;
		for(int placeNo = 0; placeNo < placeSize; placeNo++){
			placeName = places[placeNo].getName();
			sPromela += "chan place_" + placeName + " = [Bound_" + placeName
			+ "] of {" + "type_" + placeName + "};\n";
		}
		
		sPromela += "\n";
	}
	
	private void defineNonDetPickFunc(){
		sPromela +="inline pick(var, place_chan, msg){\n";
		sPromela +="	var = 1;\n";
		sPromela +="	select(var:1..len(place_chan));\n";
		sPromela +="	do\n";
		sPromela +="	::(var > 1) -> place_chan?msg; place_chan!msg; var--\n";
		sPromela +="	::(var == 1) -> break\n";
		sPromela +="	od\n";
		sPromela +="}\n";
	}
	
	private void defineIsEnabledFunc(){
		int transSize = dataLayer.getTransitionsCount();
		Transition[] trans = dataLayer.getTransitions();
		String transName;
		
		for (int transNo = 0; transNo < transSize; transNo++){
			transName = trans[transNo].getName();
			sPromela += "inline is_enabled_" + transName + "() {\n";
			String else_temp = "";
			//declare local variables for input token
			ArrayList<Place> inputPlaces= trans[transNo].getPlaceInList();	
			
			//Test if all input places is empty
			for(int ipNo = 0; ipNo < inputPlaces.size(); ipNo++){
				String inPlaceName = inputPlaces.get(ipNo).getName();
				if(!inputPlaces.get(ipNo).getToken().getDataType().getPow()){
					if(ipNo == 0){
						sPromela += "  place_"+inPlaceName+"?["+inPlaceName+"]";
					}else{
						sPromela += " &&  place_"+inPlaceName+"?["+inPlaceName+"]";
					}
				}
			}
			sPromela += "\n	->\n";
			for(int ipNo = 0; ipNo < inputPlaces.size(); ipNo++){
				String inPlaceName = inputPlaces.get(ipNo).getName();
				if(!inputPlaces.get(ipNo).getToken().getDataType().getPow()){
					sPromela += "  place_"+inPlaceName+"?"+inPlaceName+";\n";
					
					if(ipNo == 0){
						else_temp += " place_"+inPlaceName+"!"+inPlaceName;
					}else{
						else_temp += ";\n		place_"+inPlaceName+"!"+inPlaceName;
					}
				}
			}
			sPromela +="	if\n";
			sPromela +="	:: ";
			
			//precondicion
			String formula = trans[transNo].getFormula();
			ErrorMsg errorMsg = new ErrorMsg(formula);
			Parse p = new Parse(formula, errorMsg);
			Sentence s = p.absyn;
			System.out.println(trans[transNo].getName());
			s.accept(new Formula2Promela(errorMsg, trans[transNo], 0));
//			s.accept(new Printer());
			
			if(!("").equals(s.strPre)){
				sPromela += "atomic{"+s.strPre+"}\n";
				sPromela += "		->"+transName+"_is_enabled = true\n";
			}else{
				sPromela += "true ->"+transName+"_is_enabled = true\n";
			}
			
			sPromela +="	:: else -> {"+ else_temp +"}\n";
			sPromela +="	fi\n";
			sPromela += "}\n";
		}
	}
	
	private void defineFireFunc(){
		int transSize = dataLayer.getTransitionsCount();
		Transition[] trans = dataLayer.getTransitions();
		String transName;
		for (int transNo = 0; transNo < transSize; transNo++){
			transName = trans[transNo].getName();
			sPromela += "inline fire_" + transName + "() {\n";
			
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
			s.accept(new Formula2Promela(errorMsg, trans[transNo], 0));
			sPromela += s.strPost;
			
			ArrayList<Place> otPlaces= trans[transNo].getPlaceOutList();			
			for(int opNo = 0; opNo < otPlaces.size(); opNo++){
				String otPlaceName = otPlaces.get(opNo).getName();
				if(!otPlaces.get(opNo).getToken().getDataType().getPow()){
					sPromela += "  place_" + otPlaceName + "!" +otPlaceName+";\n";
				}
			}
			sPromela += "  "+transName+"_is_enabled = false\n";
			sPromela += "}\n";
		}

	}
	
	private void defineTransFunc(){
		int transSize = dataLayer.getTransitionsCount();
		Transition[] trans = dataLayer.getTransitions();
		String transName;
		for (int transNo = 0; transNo < transSize; transNo++){
			transName = trans[transNo].getName();
			sPromela += "inline " + transName + "() {\n";
			sPromela += "  is_enabled_"+transName+"();\n";
			sPromela += "  if\n";
			sPromela += "  ::  "+transName+"_is_enabled -> atomic{fire_"+transName+"()}\n";
			sPromela += "  ::  else -> skip\n";
			sPromela += "  fi\n";
			
			sPromela += "}\n";
		}
	}
	
	private void defineMainProcess(){

		int transSize = dataLayer.getTransitionsCount();
		Transition[] trans = dataLayer.getTransitions();
		String transName;
		
		sPromela += "proctype "+"Main() {\n";
		for (int transNo = 0; transNo < transSize; transNo++){
			transName = trans[transNo].getName();
			sPromela += "  bool "  + transName + "_is_enabled = false;\n";
		}
		
		//define local structure
		int placeSize = dataLayer.getPlacesCount();
		Place[] places = dataLayer.getPlaces();
		String placeName;		
		for(int placeNo = 0; placeNo < placeSize; placeNo++){
			placeName = places[placeNo].getName();
			sPromela += "  type_" + placeName + " " +placeName+";\n";
			boolean isPowerSet = places[placeNo].getToken().getDataType().getPow();
			if(isPowerSet){
				sPromela += "	int var_"+placeName+"=1;\n";
			}
		}
		
		sPromela += "\n  do\n";
		for (int transNo = 0; transNo < transSize; transNo++) {
			transName = trans[transNo].getName();
			sPromela += "  :: atomic{ " + transName + "() }\n";
		}
		sPromela += "  od\n";
		sPromela += "}\n";
	}
	
	private void defineInitFunc(){
		sPromela += "init {\n";
		
		int placeSize = dataLayer.getPlacesCount();
		Place[] places = dataLayer.getPlaces();
		
		String placeName;
		for(int placeNo = 0; placeNo < placeSize; placeNo++){
			placeName = places[placeNo].getName();
			sPromela += "  type_" + placeName + " " + placeName+";\n";
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
					sPromela += "  "+placeName+"."+placeName+"_field"+Integer.toString(j+1)+
						"="+value+";\n";
				}
				sPromela += "  place_"+placeName+"!"+placeName+";\n";
			}
		}
		
		sPromela += "run Main()\n";
		sPromela += "}\n";
	}
	
	private void defineFormula() {
//		if(!"".equals(propertyFormula)){
//		ltlparser.errormsg.ErrorMsg errorMsg = new ltlparser.errormsg.ErrorMsg(propertyFormula);
//		ltlparser.ParseLTL p = new ltlparser.ParseLTL(propertyFormula, errorMsg);
//		ltlparser.ltlabsyntree.LogicSentence s = p.absyn;
//		s.accept(new PropertyFormulaToPromela(errorMsg));
//		sPromela += s.formula;
//		}
		sPromela += "ltl f{"+ this.propertyFormula + "}";
	}
	
	public String getPromela()
	{
		return sPromela;
	}
}
