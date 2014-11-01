package pipe.dataLayer;

import java.awt.Container;

import javax.swing.BoxLayout;

import pipe.gui.CreateGui;
import pipe.gui.Grid;
import pipe.gui.Pipe;
import pipe.gui.Zoomer;
import pipe.gui.widgets.*;

/**
 * Simple place can only have one token.
 * @author su liu
 *
 */
public class SimplePlace extends Place {
	
	private Token token;

	public SimplePlace(double positionXInput, double positionYInput,
			String idInput, String nameInput, Double nameOffsetXInput,
			Double nameOffsetYInput, int initialMarkingInput,
			double markingOffsetXInput, double markingOffsetYInput,
			int capacityInput) {
		super(positionXInput, positionYInput, idInput, nameInput, nameOffsetXInput,
				nameOffsetYInput, initialMarkingInput, markingOffsetXInput,
				markingOffsetYInput, capacityInput);
		token = new Token();
	}

	public SimplePlace(double positionXInput, double positionYInput) {
		super(positionXInput, positionYInput);
		token = new Token();
	}
	
	   public boolean setToken(Token t)
	   {
		   if(token == null) {
			   token = t;
			   return true;
		   }
		   else
			   return false;
	   }
	   
	   public Token getToken()
	   {
		   return this.token;
	   }
	   
	   public boolean addTokenFromBasicType(BasicType[] bt)
	   {
		   if(dataType == null || this.getToken()!=null)
			   return false;
		   Token newToken = new Token(dataType);
		   
		   if(!newToken.add(bt))
			   return false;
		   
		   if(setToken(newToken))
			   return true;
		   return false;
	   }
	   
	   
	   public boolean deleteToken()
	   {
		   if(token == null)return false;
		   else{
			   token = null;
			   return true;
		   }
	   }
	   
	   public boolean tailToken(){
		   System.out.println("Simple Place tail Token not available!");
		   return true;
	   }
	   
	   public boolean receiveToken(Token t)
	   {
		   if(this.token == null)
		   {
			   setToken(t);
			   return true;
		   }else
		   {
			   System.out.println("SimplePlace.receiveToken: token is already set");
			   return false;
		   }
	   }
	   

	   
	   public Place paste(double x, double y, boolean fromAnotherView){
		   	Place copy = new SimplePlace (
		              Grid.getModifiedX(x + this.getX() + Pipe.PLACE_TRANSITION_HEIGHT/2),
		              Grid.getModifiedY(y + this.getY() + Pipe.PLACE_TRANSITION_HEIGHT/2));
		      copy.pnName.setName(this.pnName.getName()  
		                          + "(" + this.getCopyNumber() +")");
		      this.newCopy(copy);
		      copy.nameOffsetX = this.nameOffsetX;
		      copy.nameOffsetY = this.nameOffsetY;
		      copy.capacity = this.capacity;
		      copy.attributesVisible = this.attributesVisible;
		      copy.initialMarking = this.initialMarking;
		      copy.currentMarking = this.currentMarking;
		      copy.markingOffsetX = this.markingOffsetX;
		      copy.markingOffsetY = this.markingOffsetY;
		      copy.markingParameter = this.markingParameter;
		      copy.update();
		      return copy;
		   }
		   
		   
		   public Place copy(){
			   Place copy = new SimplePlace (Zoomer.getUnzoomedValue(this.getX(), zoom), 
		                              Zoomer.getUnzoomedValue(this.getY(), zoom));
		      copy.pnName.setName(this.getName());
		      copy.nameOffsetX = this.nameOffsetX;
		      copy.nameOffsetY = this.nameOffsetY;
		      copy.capacity = this.capacity;
		      copy.attributesVisible = this.attributesVisible;
		      copy.initialMarking = this.initialMarking;
		      copy.currentMarking = this.currentMarking;
		      copy.markingOffsetX = this.markingOffsetX;
		      copy.markingOffsetY = this.markingOffsetY;
		      copy.markingParameter = this.markingParameter;
		      copy.setOriginal(this);
		      return copy;
		   }   
	
}
