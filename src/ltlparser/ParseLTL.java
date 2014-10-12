package ltlparser;
import ltlparser.errormsg.*;
import ltlparser.ltlabsyntree.*;

public class ParseLTL {

  public LogicSentence absyn;
  private ErrorMsg errorMsg;

  public ParseLTL(java.io.File filename, ErrorMsg err) {
    errorMsg = err;
    java.io.InputStream inp;
    try {inp = new java.io.FileInputStream(filename);
    } catch (java.io.FileNotFoundException e) {
      throw new Error("File not found: " + filename);
    }

    LTLParser parser = new LTLParser(new Yylex(inp,errorMsg), errorMsg);

    try {
      if (!errorMsg.anyErrors)
        absyn = (LogicSentence) parser./*debug_*/parse().value;
    } catch (Throwable e) {
      e.printStackTrace();
      throw new Error(e.toString());
    } 
    finally {
      try {inp.close();} catch (java.io.IOException e) {}
    }
  }
  public ParseLTL(String parseStr, ErrorMsg err) {
    java.io.Reader inp;
    errorMsg = err;
    try {inp =  (java.io.Reader)(new java.io.StringReader(parseStr));
    } catch (Exception e) {
      throw new Error("String error: " + parseStr);
    }
    LTLParser parser = new LTLParser(new Yylex(inp,errorMsg));
    
    try {
      if (!errorMsg.anyErrors)
        absyn = (LogicSentence) parser.parse().value;
    } catch (Throwable e) {
      e.printStackTrace();
      throw new Error(e.toString());
    } 
    finally {
      try {inp.close();} catch (java.io.IOException e) {}
    }
  }
}

