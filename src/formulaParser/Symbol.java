package formulaParser;

import pipe.dataLayer.Token;
import pipe.dataLayer.abToken;

public class Symbol {

	String key;
	int type;
	Object binder;
//	Token binding = null;
//	abToken abBinding = null;
	
	public Symbol(String key, Object b){
		this.key = key;
		this.binder = b;
	}
	
	public String getKey(){
		return this.key;
	}
	
	public Object getBinder(){
		return this.binder;
	}
	
//	public Symbol(String key, Token b){
//		type = 0;
//		this.key = key;
//		this.binding = b;
//	}
//	
//	public Symbol(String key, abToken ab){
//		type = 1;
//		this.key = key;
//		this.abBinding = ab;
//	}
}
