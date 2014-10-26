package pipe.dataLayer;

import java.util.Vector;

/**
 * PowersetPlace can have a list of tokens.
 * @author Su Liu
 *
 */
public class PowersetPlace extends Place {
	Vector<Token> tokens;

	public PowersetPlace(double positionXInput, double positionYInput,
			String idInput, String nameInput, Double nameOffsetXInput,
			Double nameOffsetYInput, int initialMarkingInput,
			double markingOffsetXInput, double markingOffsetYInput,
			int capacityInput) {
		super(positionXInput, positionYInput, idInput, nameInput, nameOffsetXInput,
				nameOffsetYInput, initialMarkingInput, markingOffsetXInput,
				markingOffsetYInput, capacityInput);
		tokens = new Vector<Token>();
	}
	
	public PowersetPlace(double positionXInput, double positionYInput) {
		super(positionXInput, positionYInput);
		tokens = new Vector<Token>();
	}
	
	   public boolean setToken(Vector<Token> _tokens)
	   {
		   if(tokens.capacity() == 0){
			   tokens = _tokens;
			   return true;
		   }else{
			   System.out.println("PwersetPlace: token list not null!");
			   return false;
		   }
	   }
	   
	   public Vector<Token> getToken()
	   {
		   return tokens;
	   }
	   
	   public boolean addToken(BasicType[] bt)
	   {
		   if(dataType == null)
			   return false;
		   
		   Token newtoken = new Token(dataType);
		   
		   if(!newtoken.add(bt))
			   return false;
		   
		   if(tokens.add(newtoken))
			   return true;
		   
		   return false;
	   }
	   
	   
	   public boolean deleteToken(Token _token)
	   {
		   return tokens.remove(_token);
	   }
	   
	   public boolean tailToken(){
			   Token ft = tokens.firstElement();
			   if(ft!=null){
				   tokens.remove(ft);
				   tokens.add(ft);
				   return true;
			  }
			   return false;
	   }

}
