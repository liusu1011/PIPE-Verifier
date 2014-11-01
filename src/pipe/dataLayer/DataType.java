package pipe.dataLayer;

import java.util.Vector;
import java.util.UUID;


public class DataType 
{
	private String ID;
	private String name;
	public int Ntype;
	private Vector<String> types;
	private boolean isPow;
	
	private int NumofElement;
	private boolean isDef;
		
	public DataType()
	{
		ID = UUID.randomUUID().toString();
		types = new Vector<String>();
		Ntype = 0;
		isPow = false;
		isDef = false;
	}
	
	public DataType(String _name, String[] _types, boolean _isPow)
	{
		ID = UUID.randomUUID().toString();
		name = _name;
		isPow = _isPow;
		types = new Vector<String>();
		defineType(_types);
	}
	
	public boolean defineType(String[] t)
	{
		for(int i=0;i<t.length;i++)
		{
			if(!(t[i].equalsIgnoreCase("string") || t[i].equalsIgnoreCase("int")))
			{
				System.out.println("definetype error: not string or int type input!");
				return false;
			}
			
			types.add(t[i]);
		}
		isDef = true;
		return true;
	}
	
	public int getTypebyIndex(int index)
	{
		int type = 0;
		if(types.get(index).equals("int"))type = 0;
		else if(types.get(index).equals("string"))type = 1;
		return type;
	}
	
	public void setNumofElement(int num)
	{
		NumofElement = num;
	}
	
	public int getNumofElement()
	{
		return NumofElement;
	}
	
	public void setPow(boolean _ispow)
	{
		isPow = _ispow;
	}
	
	public boolean getPow()
	{
		return isPow;
	}
	
	public void setName(String n)
	{
		name = n;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setTypes(Vector<String> _types)
	{
		types = _types;
	}
	
	public Vector<String> getTypes()
	{
		return types;
	}
	
	public void setDef(boolean _isDef)
	{
		isDef = _isDef;
	}
	
	public boolean getDef()
	{
		return isDef;
	}

}

