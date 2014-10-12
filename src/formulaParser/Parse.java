package formulaParser;

import formulaParser.formulaAbsyntree.*;

public class Parse {

	  public Sentence absyn;
	  private ErrorMsg errorMsg;

	  public Parse(String source, ErrorMsg err) {
	    errorMsg = err;
//	    java.io.InputStream inp;
	    java.io.Reader inp = (java.io.Reader) (new java.io.StringReader(source));
//	    try {inp = new java.io.FileInputStream(filename);
//	    } catch (java.io.FileNotFoundException e) {
//	      throw new Error("File not found: " + filename);
//	    }
	    parser parser = new parser(new Yylex(inp,errorMsg), errorMsg);
	    try {
	      absyn = (Sentence) parser./*debug_*/parse().value;
	    } catch (Throwable e) {
	      e.printStackTrace();
	      throw new Error(e.toString());
	    } 
	    finally {
	      try {inp.close();} catch (java.io.IOException e) {}
	    }
	  }
	}
