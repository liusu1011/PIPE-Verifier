package pipe.dataLayer;
import java.util.*;

public class Token 
{
	DataType tokenType;
	public Vector<BasicType> Tlist; 
	boolean isDef;
	
	public Token()
	{
		DataType newType = new DataType();
		definetype(newType);
	}
	
	public Token(DataType input)
	{
		definetype(input);
	}
	
//	public Token(boolean[] kind, boolean _isPow)
//	{
//		definetype(kind, _isPow);
//	}
	
	public void definetype(DataType input)
	{
		tokenType = input;
		isDef = true;
		Tlist = new Vector<BasicType>();
//		System.out.println("The size of Tlist is: "+Tlist.size());
//		defineTlist(tokenType);
	}
	
	/**
	 * To initialize Tlist when creating new Vector of BasicType, otherwise the vector is null.
	 * Called by Transition.getToken, so that the null token in symbol table get from arc out places can be used as a token;
	 * @param tokenType
	 */
	public void defineTlist(DataType tokenType){
		for(int i=0;i<tokenType.getNumofElement();i++){
			if(tokenType.getTypes().get(i).equals("int")){
				BasicType bt = new BasicType();
				bt.kind = 0;
				this.Tlist.add(bt);
//				Tlist.get(i).kind = 0;
			}else if(tokenType.getTypes().get(i).equals("string")){
				BasicType bt = new BasicType();
				bt.kind = 1;
				this.Tlist.add(bt);
			}
		}
		
		
	}
	
//	public void definetype(boolean[] kind, boolean _isPow)
//	{
//		tokenType = new DataType();
//		tokenType.NtranslateFrom(kind);
//		tokenType.setPow(_isPow);
//		isDef = true;
//		Tlist = new Vector(kind.length);
//	}
	
	public boolean checkElementType(int index)
	{
		if(tokenType.getTypebyIndex(index) != Tlist.get(index).kind)
			return false;
		return true;
	}
	
	
//	public boolean add(BasicType[] bt)
//	{
//		
//		if(!isDef)
//			return false;
////		if(bt.length != tokenType.getNumofElement())
////			return false;
//		System.out.println(isDef);
//		for(int i = 0; i < bt.length; i ++)
//		{
////			if(tokenType.getTypebyIndex(i) != bt[i].kind)
////				return false;
//			Tlist.add(i, bt[i]);
//		}
//		return true;
//	}
	
	public boolean add(BasicType[] bt){
		
		for(int i=0;i<bt.length;i++){
			Tlist.add(bt[i]);
		}
		
		return true;
	}
	
	public void delete(int index)
	{
		if(index < Tlist.size())
			Tlist.remove(index);
	}
	
	public void delete()
	{
		for(int i = 0; i < Tlist.size(); i++ )
			delete(i);
	}
	
	public DataType getTokentype()
	{
		return tokenType;
	}
	
	public String displayToken()
	{
		String a = "<";
		for(int i = 0; i < Tlist.size(); i ++)
		{
			if(Tlist.get(i).kind == 0)
			{
				a += Tlist.get(i).Tint;
			}
			if(Tlist.get(i).kind == 1)
			{
				a += Tlist.get(i).Tstring;
			}
			if(i < Tlist.size() - 1)
				a += ",";
		}
		a += ">";
		return a;
	}
	
	public BasicType getBTbyindex(int index)
	{
		return Tlist.get(index);
	}
	
	public void UpdateDataTypeByTlist(){
		
		for(BasicType bt: Tlist){
			if(bt.kind == 0){
				tokenType.getTypes().add("int");
			}else tokenType.getTypes().add("string");
			tokenType.setNumofElement(tokenType.getNumofElement()+1);
		}
	}
}


