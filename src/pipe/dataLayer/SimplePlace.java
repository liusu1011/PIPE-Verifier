package pipe.dataLayer;

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
	
}
