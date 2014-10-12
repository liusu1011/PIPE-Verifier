package pipe.dataLayer;

public class BasicType 
{
	public int kind;//0 is int, 1 is string
	public int Tint;
	public String Tstring;
	
	public BasicType(){
		
	}
	
	public BasicType(int _kind, int _Tint, String _Tstring){
		this.kind = _kind;
		this.Tint = _Tint;
		this.Tstring = _Tstring;
	}
}
