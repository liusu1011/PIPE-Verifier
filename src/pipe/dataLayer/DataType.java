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
	
	private Vector<DataType> group;
	
	public DataType()
	{
		ID = UUID.randomUUID().toString();
		types = new Vector<String>();
		Ntype = 0;
		group = null;
		isPow = false;
		isDef = false;
	}
	
	public DataType(String _name, String[] _types, boolean _isPow, Vector<DataType> _group)
	{
		ID = UUID.randomUUID().toString();
		name = _name;
		group = _group;
		isPow = _isPow;
		types = new Vector<String>();
		defineType(_types);
	}
	
	public void defineType(String[] t)
	{
		int num = 0;
		types = new Vector<String>();
		for(int i = 0; i < t.length; i ++)
		{
			if(t[i].equals("string")||t[i].equals("int"))
			{
				num ++;
			}
			else
			{
				if(group != null)
					return;
				for(int j = 0; j < group.size(); j ++)
				{
					if(group.get(j).getName().equals(t[i]))
					{
						num += group.get(j).getNumofElement();
						break;
					}
				}
			}
			types.add( t[i]);
		}
		NtranslateFrom(num);
		isDef = true;
	}
	
	public int NtranslateFrom( int num)
	{
		Ntype = 0;
		for(int i = 0; i < types.size(); i ++)
		{
			if(types.get(i).equals("string"))
			{
				Ntype += Math.pow(2, num - i - 1);
				//num --;
			}
			else if(!types.get(i).equals("int"))
			{
				for(int j = 0; j < group.size(); j ++)
				{
					if(group.get(j).ID.equals(types.get(i)))
					{
						Ntype += group.get(i).Ntype * Math.pow(2,num - i - group.get(i).getNumofElement());
						//num -= group.get(i).getNumofElement();
						break;
					}
				}
			}
		}
		NumofElement = num;
		return Ntype;
	}
	
	
	public int getTypebyIndex(int index)
	{
//		String binary = Integer.toBinaryString(Ntype);
//		
//		return Integer.parseInt(binary.substring(index, index+1)); 
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
	
	public void setGroup(Vector<DataType> _group)
	{
		group = _group;
	}
	
	public Vector<DataType> getGroup()
	{
		return this.group;
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
	
	public void setNtype(int _Ntype)
	{
		Ntype = _Ntype;
	}
	
	public int getNtype()
	{
		return Ntype;
	}

}

