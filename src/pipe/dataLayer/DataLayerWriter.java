package pipe.dataLayer;

//Collections
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Create DataLayerWriter object
 * @param DataLayer object containing net to save
 * @author Ben Kirby
 * @author Pere Bonet (minor changes)
 */
public class DataLayerWriter {

   /** DataLayer object passed in to save */
   private DataLayer netModel;
   
   
   /** Create a writer with the DataLayer object to save*/
   public DataLayerWriter(DataLayer currentNet) {
      netModel = currentNet;
   }
   
   
   /**
    * Save the Petri-Net
    * @param filename URI location to save file
    * @throws ParserConfigurationException
    * @throws DOMException
    * @throws TransformerConfigurationException
    * @throws TransformerException
    */
   public void savePNML(File file) throws NullPointerException, IOException,
           ParserConfigurationException, DOMException, 
           TransformerConfigurationException, TransformerException {
      
      // Error checking
      if (file == null) {
         throw new NullPointerException("Null file in savePNML");
      }
      /*    
       System.out.println("=======================================");
       System.out.println("dataLayer SAVING FILE=\"" + file.getCanonicalPath() + "\"");
       System.out.println("=======================================");
       */
      Document pnDOM = null;

      StreamSource xsltSource = null;
      Transformer transformer = null;
      try {
         // Build a Petri Net XML Document
         DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = builderFactory.newDocumentBuilder();
         pnDOM = builder.newDocument();
         
         Element PNML = pnDOM.createElement("pnml"); // PNML Top Level Element
         pnDOM.appendChild(PNML);
         
         Attr pnmlAttr = pnDOM.createAttribute("xmlns"); // PNML "xmlns" Attribute
         pnmlAttr.setValue("http://www.informatik.hu-berlin.de/top/pnml/ptNetb");
         PNML.setAttributeNode(pnmlAttr);
         
         Element NET = pnDOM.createElement("net"); // Net Element
         PNML.appendChild(NET);
         Attr netAttrId = pnDOM.createAttribute("id"); // Net "id" Attribute
         netAttrId.setValue("Net-One");
         NET.setAttributeNode(netAttrId);
         Attr netAttrType = pnDOM.createAttribute("type"); // Net "type" Attribute
         netAttrType.setValue("P/T net");
         NET.setAttributeNode(netAttrType);
         
         AnnotationNote[] labels = netModel.getLabels();
         for (int i = 0; i < labels.length; i++) {
               NET.appendChild(createAnnotationNoteElement(labels[i], pnDOM));
         }         
         
         MarkingParameter[] markingParameters = netModel.getMarkingParameters();
         for (int i = 0; i < markingParameters.length; i++) {
            NET.appendChild(createDefinition(markingParameters[i], pnDOM));
         }        
         
         RateParameter[] rateParameters = netModel.getRateParameters();
         for (int i = 0; i < rateParameters.length; i++) {
            NET.appendChild(createDefinition(rateParameters[i], pnDOM));
         }            
         
         Place[] places = netModel.getPlaces();
         for (int i = 0 ; i < places.length ; i++) {
            NET.appendChild(createPlaceElement(places[i], pnDOM));
         }

         Transition[] transitions = netModel.getTransitions();
         for (int i = 0 ; i < transitions.length ; i++) {
            NET.appendChild(createTransitionElement(transitions[i], pnDOM));
         }

         Arc[] arcs = netModel.getArcs();
         for (int i = 0 ; i < arcs.length ; i++) {
            Element newArc = createArcElement(arcs[i],pnDOM);
            
            int arcPoints = arcs[i].getArcPath().getArcPathDetails().length;
            String[][] point = arcs[i].getArcPath().getArcPathDetails();
            for (int j = 0; j < arcPoints; j++) {
               newArc.appendChild(createArcPoint(point[j][0],point[j][1],point[j][2],pnDOM,j));
            }
            NET.appendChild(newArc);
            //newArc = null;
         }
         
         InhibitorArc[] inhibitorArcs = netModel.getInhibitors();
         for (int i = 0; i < inhibitorArcs.length; i++) {
            Element newArc = createArcElement(inhibitorArcs[i],pnDOM);

            int arcPoints = inhibitorArcs[i].getArcPath().getArcPathDetails().length;
            String[][] point = inhibitorArcs[i].getArcPath().getArcPathDetails();
            for (int j = 0; j < arcPoints; j++) {
               newArc.appendChild(createArcPoint(point[j][0],
                        point[j][1],
                        point[j][2],
                        pnDOM,j));
            }
            NET.appendChild(newArc);
         }
         
         StateGroup[] stateGroups = netModel.getStateGroups();
         for(int i = 0; i< stateGroups.length; i++) {
            Element newStateGroup = createStateGroupElement(stateGroups[i], pnDOM);
            
            int numConditions = stateGroups[i].numElements();
            String[] conditions = stateGroups[i].getConditions();
            for(int j = 0; j<numConditions; j++) {
               newStateGroup.appendChild(createCondition(conditions[j], pnDOM));
            }
            NET.appendChild(newStateGroup);
         }
         //stateGroups = null;         
         
         pnDOM.normalize();
         // Create Transformer with XSL Source File
//         xsltSource = new StreamSource(Thread.currentThread().
//                 getContextClassLoader().getResourceAsStream("xslt" + 
//                 System.getProperty("file.separator") + "GeneratePNML.xsl"));
         
         xsltSource = new StreamSource(Thread.currentThread().
                 getContextClassLoader().getResourceAsStream("xslt/" + "GeneratePNML.xsl"));
         
         transformer = TransformerFactory.newInstance().newTransformer(xsltSource);
         // Write file and do XSLT transformation to generate correct PNML
         File outputObjectArrayList = file;//new File(filename); // Output for XSLT Transformation
         DOMSource source = new DOMSource(pnDOM);
         StreamResult result = new StreamResult(outputObjectArrayList);
         transformer.transform(source, result);
      } catch (ParserConfigurationException e) {
         // System.out.println("=====================================================================================");
         System.out.println("ParserConfigurationException thrown in savePNML() " +
                 ": dataLayerWriter Class : dataLayer Package: filename=\"" + 
                 file.getCanonicalPath() + "\" xslt=\"" + 
                 xsltSource.getSystemId() + "\" transformer=\"" + 
                 transformer.getURIResolver() + "\"");
         // System.out.println("=====================================================================================");
         // e.printStackTrace(System.err);
      } catch (DOMException e) {
         // System.out.println("=====================================================================");
         System.out.println("DOMException thrown in savePNML() " +
                 ": dataLayerWriter Class : dataLayer Package: filename=\"" +
                 file.getCanonicalPath() + "\" xslt=\"" +
                 xsltSource.getSystemId() + "\" transformer=\"" +
                 transformer.getURIResolver() + "\"");
         // System.out.println("=====================================================================");
         // e.printStackTrace(System.err);
      } catch (TransformerConfigurationException e) {
         // System.out.println("==========================================================================================");
         System.out.println("TransformerConfigurationException thrown in savePNML() " +
                 ": dataLayerWriter Class : dataLayer Package: filename=\"" + file.getCanonicalPath() + "\" xslt=\"" + xsltSource.getSystemId() + "\" transformer=\"" + transformer.getURIResolver() + "\"");
         // System.out.println("==========================================================================================");
         // e.printStackTrace(System.err);
      } catch (TransformerException e) {
         // System.out.println("=============================================================================");
         System.out.println("TransformerException thrown in savePNML() : dataLayerWriter Class : dataLayer Package: filename=\"" + file.getCanonicalPath() + "\" xslt=\"" + xsltSource.getSystemId() + "\" transformer=\"" + transformer.getURIResolver() + "\"" + e);
         // System.out.println("=============================================================================");
         // e.printStackTrace(System.err);
      }
   }

   
   /**
    * Creates a Place Element for a PNML Petri-Net DOM
    * @param inputPlace Input Place
    * @param document Any DOM to enable creation of Elements and Attributes
    * @return Place Element for a PNML Petri-Net DOM
    */
   private Element createPlaceElement(Place inputPlace, Document document) {
           Element placeElement = null;
      
      if (document != null) {
         placeElement = document.createElement("place");
      }
      
      if (inputPlace != null ) {
         Integer attrValue = null;
         Double positionXInput = inputPlace.getPositionXObject();
         Double positionYInput = inputPlace.getPositionYObject();
         String idInput = inputPlace.getId();
         String nameInput = inputPlace.getName();
         Double nameOffsetXInput = inputPlace.getNameOffsetXObject();
         Double nameOffsetYInput = inputPlace.getNameOffsetYObject();
         Integer initialMarkingInput = inputPlace.getCurrentMarkingObject();
         Double markingOffsetXInput = inputPlace.getMarkingOffsetXObject();
         Double markingOffsetYInput = inputPlace.getMarkingOffsetYObject();
         Integer capacityInput = inputPlace.getCapacity();
         DataType datatype = inputPlace.getDataType();
         abToken abtoken = inputPlace.getToken();
         Vector<DataType> group = inputPlace.getGroup();
         String markingParameter = "";
         if (inputPlace.getMarkingParameter() != null) {
            markingParameter = inputPlace.getMarkingParameter().getName();
         }         

         placeElement.setAttribute("positionX", (positionXInput != null ? String.valueOf(positionXInput) : ""));
         placeElement.setAttribute("positionY", (positionYInput != null ? String.valueOf(positionYInput) : ""));
         placeElement.setAttribute("name", (nameInput != null ? nameInput : (idInput != null && idInput.length() > 0? idInput : "")));
         placeElement.setAttribute("id", (idInput != null ? idInput : "error"));
         placeElement.setAttribute("nameOffsetX", (nameOffsetXInput != null ? String.valueOf(nameOffsetXInput) : ""));
         placeElement.setAttribute("nameOffsetY", (nameOffsetYInput != null ? String.valueOf(nameOffsetYInput) : ""));
         placeElement.setAttribute("initialMarking", (initialMarkingInput != null ? String.valueOf(initialMarkingInput) : "0"));
         placeElement.setAttribute("markingOffsetX", (markingOffsetXInput != null ? String.valueOf(markingOffsetXInput) : ""));
         placeElement.setAttribute("markingOffsetY", (markingOffsetYInput != null ? String.valueOf(markingOffsetYInput) : ""));
         placeElement.setAttribute("capacity", (capacityInput != null ? String.valueOf(capacityInput) : ""));
         placeElement.setAttribute("parameter", 
                 (markingParameter != null ? markingParameter : ""));
         if(datatype != null)
         {
        	 placeElement.setAttribute("dt", datatype.getName());
        	 placeElement.setAttribute("ntype", Integer.toString(datatype.getNtype()));
        	 
        	 String types = "";
        	 for(int i = 0; i < datatype.getTypes().size(); i ++)
        	 {
        			types += datatype.getTypes().get(i);
        			if(i < datatype.getTypes().size() - 1)
        				types += ",";
        	 }
        	 placeElement.setAttribute("types", types);
        	 placeElement.setAttribute("ifpow", datatype.getPow()?"T":"F");
        	 placeElement.setAttribute("numofelement", Integer.toString(datatype.getNumofElement()));
        	 placeElement.setAttribute("ifdef", datatype.getDef()?"T":"F");
        	 
         }
         if(abtoken != null)
         {
        	 placeElement.setAttribute("isDef", abtoken.getDef()?"T":"F");
        	 if(abtoken.getDef())
        	 {
        		 for(int i =0; i < abtoken.getTokenCount(); i++)
        		 {
        			 Element e = document.createElement("listToken");
        			 for(int j =0; j < datatype.getNumofElement(); j++)
            		 {
            			 Element g = document.createElement("Tlist");
            			 BasicType tempbt = abtoken.getTokenbyIndex(i).getBTbyindex(j);
            			 if(tempbt.kind == 0)
            			 {
            				 g.setAttribute("data", Integer.toString(tempbt.Tint));
            			 }
            			 else g.setAttribute("data", tempbt.Tstring);
            			 e.appendChild(g); 
            		 }
        			 placeElement.appendChild(e);
        		 }
        	 }
         }
         
         if(group != null)
         {
        	 if(group.size() > 0)
        	 {
        		 for(int i = 0; i < group.size(); i ++)
        		 {
	        		 if(group.get(i) != null)
	                 {
	        			 Element g = document.createElement("group");
	                	 g.setAttribute("group-dt", group.get(i).getName());
	                	 g.setAttribute("group-ntype", Integer.toString(group.get(i).getNtype()));
	                	 
	                	 String types = "";
	                	 for(int j = 0; j < group.get(i).getTypes().size(); j ++)
	                	 {
	                		
	                			types += group.get(i).getTypes().get(j); 
	                			if(j < group.get(i).getTypes().size() - 1)
	                				types += ",";
	                	 }
	                	 g.setAttribute("group-types", types);
	                	 g.setAttribute("group-ifpow", group.get(i).getPow()?"T":"F");
	                	 g.setAttribute("group-numofelement", Integer.toString(group.get(i).getNumofElement()));
	                	 g.setAttribute("group-ifdef", group.get(i).getDef()?"T":"F");
	                	 placeElement.appendChild(g);
	                 } 
        		 }
        	 }
         }

      }
      return placeElement;
      
   }

   
   private Element createAgentNetElement(DataLayer agentLayer, Document document){
	   Element agentNetElement = null;
	   
	   if(document != null){
		   agentNetElement = document.createElement("agentNet");
	   }
	   if(agentLayer != null){
		   AnnotationNote[] labels = agentLayer.getLabels();
	         for (int i = 0; i < labels.length; i++) {
	               agentNetElement.appendChild(createAnnotationNoteElement(labels[i], document));
	         }         
	         
	         MarkingParameter[] markingParameters = agentLayer.getMarkingParameters();
	         for (int i = 0; i < markingParameters.length; i++) {
	            agentNetElement.appendChild(createDefinition(markingParameters[i], document));
	         }        
	         
	         RateParameter[] rateParameters = agentLayer.getRateParameters();
	         for (int i = 0; i < rateParameters.length; i++) {
	            agentNetElement.appendChild(createDefinition(rateParameters[i], document));
	         }            
	         
	         Place[] places = agentLayer.getPlaces();
	         for (int i = 0 ; i < places.length ; i++) {
	            agentNetElement.appendChild(createPlaceElement(places[i], document));
	         }

	         Transition[] transitions = agentLayer.getTransitions();
	         for (int i = 0 ; i < transitions.length ; i++) {
	            agentNetElement.appendChild(createTransitionElement(transitions[i], document));
	         }

	         Arc[] arcs = agentLayer.getArcs();
	         for (int i = 0 ; i < arcs.length ; i++) {
	            Element newArc = createArcElement(arcs[i],document);
	            
	            int arcPoints = arcs[i].getArcPath().getArcPathDetails().length;
	            String[][] point = arcs[i].getArcPath().getArcPathDetails();
	            for (int j = 0; j < arcPoints; j++) {
	               newArc.appendChild(createArcPoint(point[j][0],point[j][1],point[j][2],document,j));
	            }
	            agentNetElement.appendChild(newArc);
	            //newArc = null;
	         }
	         
	         InhibitorArc[] inhibitorArcs = agentLayer.getInhibitors();
	         for (int i = 0; i < inhibitorArcs.length; i++) {
	            Element newArc = createArcElement(inhibitorArcs[i],document);

	            int arcPoints = inhibitorArcs[i].getArcPath().getArcPathDetails().length;
	            String[][] point = inhibitorArcs[i].getArcPath().getArcPathDetails();
	            for (int j = 0; j < arcPoints; j++) {
	               newArc.appendChild(createArcPoint(point[j][0],
	                        point[j][1],
	                        point[j][2],
	                        document,j));
	            }
	            agentNetElement.appendChild(newArc);
	         }
	         
	         StateGroup[] stateGroups = agentLayer.getStateGroups();
	         for(int i = 0; i< stateGroups.length; i++) {
	            Element newStateGroup = createStateGroupElement(stateGroups[i], document);
	            
	            int numConditions = stateGroups[i].numElements();
	            String[] conditions = stateGroups[i].getConditions();
	            for(int j = 0; j<numConditions; j++) {
	               newStateGroup.appendChild(createCondition(conditions[j], document));
	            }
	            agentNetElement.appendChild(newStateGroup);
	         }
	   }
	   return agentNetElement;
   }
   
   /**
    * Creates a label Element for a PNML Petri-Net DOM
    * @param inputLabel input label
    * @param document Any DOM to enable creation of Elements and Attributes
    * @return label Element for a PNML Petri-Net DOM
    */
   private Element createAnnotationNoteElement(AnnotationNote inputLabel, Document document) {
      Element labelElement = null;
      
      if (document != null) {
         labelElement = document.createElement("labels");
      }

      if (inputLabel != null ) {
         int positionXInput = inputLabel.getOriginalX();
         int positionYInput = inputLabel.getOriginalY();
         int widthInput = inputLabel.getNoteWidth();
         int heightInput = inputLabel.getNoteHeight();
         String nameInput = inputLabel.getNoteText();
         boolean borderInput = inputLabel.isShowingBorder();
         
         labelElement.setAttribute("positionX", 
                 (positionXInput >= 0.0 ? String.valueOf(positionXInput) : ""));
         labelElement.setAttribute("positionY", 
                 (positionYInput >= 0.0 ? String.valueOf(positionYInput) : ""));
         labelElement.setAttribute("width", 
                 (widthInput>=0.0? String.valueOf(widthInput):""));
         labelElement.setAttribute("height", 
                 (heightInput>=0.0? String.valueOf(heightInput):""));
         labelElement.setAttribute("border",String.valueOf(borderInput));
         labelElement.setAttribute("text", (nameInput != null ? nameInput : ""));
      }
      return labelElement;
   }

   
   /**
    * Creates a Transition Element for a PNML Petri-Net DOM
    * @param inputTransition Input Transition
    * @param document Any DOM to enable creation of Elements and Attributes
    * @return Transition Element for a PNML Petri-Net DOM
    */
   private Element createTransitionElement(Transition inputTransition,
           Document document) {
      Element transitionElement = null;
      
      if (document != null) {
         transitionElement = document.createElement("transition");
      }
      
      if (inputTransition != null ) {
         Integer attrValue = null;
         Double positionXInput = inputTransition.getPositionXObject();
         Double positionYInput = inputTransition.getPositionYObject();
         Double nameOffsetXInput = inputTransition.getNameOffsetXObject();
         Double nameOffsetYInput = inputTransition.getNameOffsetYObject();
         String idInput = inputTransition.getId();
         String nameInput = inputTransition.getName();
         double aRate = inputTransition.getRate();
         boolean timedTrans = inputTransition.isTimed();
         boolean infiniteServer = inputTransition.isInfiniteServer();
         int orientation = inputTransition.getAngle();
         int priority = inputTransition.getPriority();
         String rateParameter = "";
         if (inputTransition.getRateParameter() != null) {
            rateParameter = inputTransition.getRateParameter().getName();
         }
         
         String formula = inputTransition.getFormula();
         
         transitionElement.setAttribute("positionX", 
                 (positionXInput != null ? String.valueOf(positionXInput) : ""));
         transitionElement.setAttribute("positionY", 
                 (positionYInput != null ? String.valueOf(positionYInput) : ""));
         transitionElement.setAttribute("nameOffsetX", 
                 (nameOffsetXInput != null ? String.valueOf(nameOffsetXInput) : ""));
         transitionElement.setAttribute("nameOffsetY", 
                 (nameOffsetYInput != null ? String.valueOf(nameOffsetYInput) : ""));
         transitionElement.setAttribute("name", 
                 (nameInput != null ? nameInput : (idInput != null && idInput.length() > 0? idInput : "")));
         transitionElement.setAttribute("id", 
                 (idInput != null ? idInput : "error"));
         transitionElement.setAttribute("rate", 
                 (aRate != 1 ? String.valueOf(aRate):"1.0"));
         transitionElement.setAttribute("timed", String.valueOf(timedTrans));
         transitionElement.setAttribute("infiniteServer", 
                 String.valueOf(infiniteServer));
         transitionElement.setAttribute("angle", String.valueOf(orientation));
         transitionElement.setAttribute("priority", String.valueOf(priority));
         transitionElement.setAttribute("parameter", 
                 (rateParameter != null ? rateParameter : ""));
         transitionElement.setAttribute("formula", (String.valueOf(formula)));
      }
      return transitionElement;
   }

   
   /**
    * Creates a Arc Element for a PNML Petri-Net DOM
    * @param inputArc Input Arc
    * @param document Any DOM to enable creation of Elements and Attributes
    * @return Arc Element for a PNML Petri-Net DOM
    */
   private Element createArcElement(Arc inputArc, Document document) {
      Element arcElement = null;
      
      if (document != null) {
         arcElement = document.createElement("arc");
      }

      if (inputArc != null ) {
         Integer attrValue = null;
         double positionXInputD = (int)inputArc.getStartPositionX();
         Double positionXInput = new Double (positionXInputD);
         double positionYInputD = (int)inputArc.getStartPositionY();
         Double positionYInput = new Double (positionXInputD);
         String idInput = inputArc.getId();
         String sourceInput = inputArc.getSource().getId();
         String targetInput = inputArc.getTarget().getId();
         String var = inputArc.getVar();
         DataType datatype = inputArc.getDataType();
         
         int inscriptionInput = (inputArc != null ? inputArc.getWeight() : 1);
         // Double inscriptionPositionXInput = inputArc.getInscriptionOffsetXObject();        
         // Double inscriptionPositionYInput = inputArc.getInscriptionOffsetYObject();
         arcElement.setAttribute("id", (idInput != null ? idInput : "error"));
         arcElement.setAttribute("source", (sourceInput != null ? sourceInput : ""));
         arcElement.setAttribute("target", (targetInput != null ? targetInput : ""));
         arcElement.setAttribute("type", inputArc.getType());         
         arcElement.setAttribute("inscription", Integer.toString(inscriptionInput));
         arcElement.setAttribute("var", var);
         
         if(datatype != null)
         {
        	 arcElement.setAttribute("dt", datatype.getName());
        	 arcElement.setAttribute("ntype", Integer.toString(datatype.getNtype()));
        	 
        	 String types = "";
        	 for(int i = 0; i < datatype.getTypes().size(); i ++)
        	 {
        			types += datatype.getTypes().get(i); 
        			if(i < datatype.getTypes().size() - 1)
        				types += ",";
        	 }
        	 arcElement.setAttribute("types", types);
        	 arcElement.setAttribute("ifpow", datatype.getPow()?"T":"F");
        	 arcElement.setAttribute("numofelement", Integer.toString(datatype.getNumofElement()));
        	 arcElement.setAttribute("ifdef", datatype.getDef()?"T":"F");
        	 
         }
         // arcElement.setAttribute("inscriptionOffsetX", (inscriptionPositionXInput != null ? String.valueOf(inscriptionPositionXInput) : ""));
         // arcElement.setAttribute("inscriptionOffsetY", (inscriptionPositionYInput != null ? String.valueOf(inscriptionPositionYInput) : ""));
         
         if (inputArc instanceof NormalArc) {
            boolean tagged = ((NormalArc)inputArc).isTagged();
            arcElement.setAttribute("tagged", tagged ? "true" : "false");
         }         
      }
      return arcElement;
   }
   
   private Element createArcPoint(String x, String y, String type, 
           Document document, int id) {
      Element arcPoint = null;
      
      if (document != null) {
         arcPoint = document.createElement("arcpath");
      }
      String pointId = String.valueOf(id);
      if (pointId.length() < 3) {
         pointId = "0" + pointId;
      }
      if (pointId.length() < 3) { 
         pointId = "0" + pointId;
      } 	
      arcPoint.setAttribute("id", pointId);
      arcPoint.setAttribute("xCoord", x);
      arcPoint.setAttribute("yCoord", y);
      arcPoint.setAttribute("arcPointType", type);
      
      return arcPoint;
   }
   

   private Node createDefinition(RateParameter inputParameter, Document document) {
      Element labelElement = null;
      
      if (document != null) {
         labelElement = document.createElement("definitions");
      }

      if (inputParameter != null ) {
         
         int positionXInput = inputParameter.getOriginalX();//getX()
         int positionYInput = inputParameter.getOriginalY();//getY()
         int widthInput = inputParameter.getNoteWidth();
         int heightInput = inputParameter.getNoteHeight();
         double valueInput = inputParameter.getValue();
         String nameInput = inputParameter.getNoteText();
         String idInput = inputParameter.getName();
         boolean borderInput = inputParameter.isShowingBorder();
         labelElement.setAttribute("defType", "real");
         labelElement.setAttribute("expression", String.valueOf(valueInput));
         labelElement.setAttribute("id", idInput);
         labelElement.setAttribute("name", idInput);
         labelElement.setAttribute("type", "text");
         labelElement.setAttribute("positionX", 
                 (positionXInput >= 0.0 ? String.valueOf(positionXInput) : ""));
         labelElement.setAttribute("positionY", 
                 (positionYInput >= 0.0 ? String.valueOf(positionYInput) : ""));

         labelElement.setAttribute("width", 
                 (widthInput>=0.0? String.valueOf(widthInput):""));
         labelElement.setAttribute("height", 
                 (heightInput>=0.0? String.valueOf(heightInput):""));
         labelElement.setAttribute("border",String.valueOf(borderInput));      
      }
      return labelElement;
   }
   
   
   private Node createDefinition(MarkingParameter inputParameter,
           Document document) {
      Element labelElement = null;
      
      if (document != null) {
         labelElement = document.createElement("definitions");
      }

      if (inputParameter != null ) {
         
         int positionXInput = inputParameter.getOriginalX();
         int positionYInput = inputParameter.getOriginalY();
         int widthInput = inputParameter.getNoteWidth();
         int heightInput = inputParameter.getNoteHeight();
         int valueInput = inputParameter.getValue();
         String nameInput = inputParameter.getNoteText();
         String idInput = inputParameter.getName();
         boolean borderInput = inputParameter.isShowingBorder();
         labelElement.setAttribute("defType", "int");
         labelElement.setAttribute("expression", String.valueOf(valueInput));
         labelElement.setAttribute("id", idInput);
         labelElement.setAttribute("name", idInput);
         labelElement.setAttribute("type", "text");
         labelElement.setAttribute("positionX", 
                 (positionXInput >= 0.0 ? String.valueOf(positionXInput) : ""));
         labelElement.setAttribute("positionY", 
                 (positionYInput >= 0.0 ? String.valueOf(positionYInput) : ""));    
      }
      return labelElement;
   }   
   
   
   /**
    * Creates a State Group Element for a PNML Petri-Net DOM
    *
    * @param inputStateGroup Input State Group
    * @param document Any DOM to enable creation of Elements and Attributes
    * @return State Group Element for a PNML Petri-Net DOM
    * @author Barry Kearns, August 2007
    */
   private Element createStateGroupElement(StateGroup inputStateGroup, Document document){
      Element stateGroupElement = null;
      
      if(document != null) {
         stateGroupElement = document.createElement("stategroup");
      }
      
      if(inputStateGroup != null ) {
         String idInput = inputStateGroup.getId();
         String nameInput = inputStateGroup.getName();
         
         stateGroupElement.setAttribute("name", 
                 (nameInput != null ? nameInput 
                                    : (idInput != null && idInput.length() > 0? idInput : "")));
         stateGroupElement.setAttribute("id", (idInput != null ? idInput : "error"));
      }
      return stateGroupElement;
   }

   
   private Element createCondition(String condition, Document document) {
      Element stateCondition = null;
      
      if (document != null) { 
         stateCondition = document.createElement("statecondition");
      }
      
      stateCondition.setAttribute("condition", condition);
      return stateCondition;
   }
   
}
