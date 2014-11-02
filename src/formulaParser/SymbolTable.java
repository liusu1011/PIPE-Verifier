package formulaParser;

import java.util.ArrayList;
import java.util.Iterator;

import pipe.dataLayer.Token;
import pipe.dataLayer.abToken;

public class SymbolTable {

	public ArrayList<Symbol> table;
	public String isAvailable;
	
	public SymbolTable(){
		 table = new ArrayList<Symbol>();
		//this.insert(isAvailable, Boolean.TRUE);
	}
	
	public SymbolTable(SymbolTable _table)
	{
		table = new ArrayList<Symbol>(_table.table);
	}
	
	//only print when value is singleton type
	public void printSymTable()
	{
		System.out.println("-----"+table.size()+"-----");
		System.out.println("Key   Value");
		for(Symbol sym:table)
		{
			String value = "";
			if(((Token)sym.getBinder()).Tlist.get(0).kind == 0)
			{
				value = Integer.toString(((Token)sym.getBinder()).Tlist.get(0).Tint);
			}else{
				value = ((Token)sym.getBinder()).Tlist.get(0).Tstring;
			}
			System.out.println(sym.getKey()+"   "+value);
		}
	}
	
	public void addSymbolTable(SymbolTable _table)
	{
		table.addAll(_table.table);
	}
	
	public void removeSymbolTable(SymbolTable _table)
	{
		for(Symbol sym:_table.table)
		{
			this.table.remove(sym);
		}
	}
	public void insert(String key, Object b){
		Symbol symbol = new Symbol(key,b);
		table.add(symbol);
	}
	
//	void insert(String key, abToken ab){
//		Symbol symbol = new Symbol(key,ab);
//		table.add(symbol);
//	}
	
	public Object lookup(String key){
//		Iterator<Symbol> myTable = table.iterator();
//		while(myTable.hasNext()){
//			Symbol tempS = myTable.next();
//			if(tempS.key == key){
//				return tempS.binder;
//			}
//		}
		
		for(Symbol s : table){
			if(key.equals(s.key))return s.binder;
		}
		return null;
	}
	
	public void update(String key, Object b){
		Symbol symbol = new Symbol(key,b);
//		for(Symbol s: table){
//			if(key.equals(s.key)){
//				table.remove(s);
//				table.add(symbol);
//			}
//		}
		
		Iterator<Symbol> itable = table.iterator();
		while(itable.hasNext()){
			Symbol tempS = itable.next();
			if(tempS.key.equals(key)){
				itable.remove();
			}
		}
		table.add(symbol);
	}
	
	public void delete(String key){
		Iterator<Symbol> itable = table.iterator();
		while(itable.hasNext()){
			Symbol tempS = itable.next();
			if(tempS.key.equals(key)){
				itable.remove();
			}
		}	
//		for(Symbol b : table){
//			if(b.key.equals(key)){
//				table.remove(b);
//			}
//		}
	}
	
	public boolean exist(String key){
		for(Symbol s:table){
			if(key.equals(s.key)){
				return true;
			}
		}
		return false;
	}
	
	public boolean containsToken(Object b)
	{
		for(Symbol s:table)
		{
			if(s.binder.equals(b))
				return true;
		}
		return false;
	}
	public void cleanTable(){
		table.clear();
	}
}
