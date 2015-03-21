package jplag;
/*
 * Created on 22.01.2005
 */


/**
 * @author Emeric Kwemou
 */
public abstract class Parser {
  protected ProgramI program;
  protected int errors    = 0;
  private   int errorsSum = 0;
  
  public boolean getErrors() {
	  return (errors!=0);
  }
  
  public int errorsCount() {
	  return errorsSum;	
  }
  
  protected void parseEnd() {
	  errorsSum+=errors;	
  }

  public ProgramI getProgram() {
	  return program;
  }
  
  public void setProgram(ProgramI prog) {
	  this.program=prog;
  }
}
