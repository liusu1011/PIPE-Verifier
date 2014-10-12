package formulaParser;

import java.util.ArrayList;
import java.util.Iterator;

import pipe.dataLayer.Token;
import pipe.dataLayer.abToken;

public class SymbolTable {

	public ArrayList<Symbol> table = new ArrayList<Symbol>();
	public String isAvailable;
	
	public SymbolTable(){
		//this.insert(isAvailable, Boolean.TRUE);
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
	public void cleanTable(){
		table.clear();
	}
}
