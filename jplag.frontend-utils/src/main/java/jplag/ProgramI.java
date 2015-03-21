package jplag;

public interface ProgramI {
	public void addError(String errorMsg) ;
	/**
	 * 
	 * @param normalMsg
	 * @param longMsg Message for verbose mode
	 */
	public void print(String normalMsg, String longMsg);
}
