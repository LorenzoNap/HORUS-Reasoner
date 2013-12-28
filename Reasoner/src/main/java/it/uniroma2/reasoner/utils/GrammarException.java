package it.uniroma2.reasoner.utils;

/**
 * Custom excption. This Exception rappresent an error of inference rule parsing
 * 
 * @author Giovanni Lorenzo Napoleoni
 *
 */
public class GrammarException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final static String GRAMMAR_ERROR = "Error on Parsing Inference Rule: ";
	
	private String errorMessage;
	
	public GrammarException(String errorMessage){
		this.errorMessage = errorMessage;
	}

	@Override
	public String toString(){
		return GRAMMAR_ERROR+errorMessage;
	}
	
}
