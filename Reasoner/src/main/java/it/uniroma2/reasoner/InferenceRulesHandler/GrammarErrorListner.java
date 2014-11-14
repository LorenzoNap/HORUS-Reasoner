package it.uniroma2.reasoner.InferenceRulesHandler;



import it.uniroma2.reasoner.utils.GrammarException;

import java.util.Collections;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.apache.log4j.Logger;

import antr4.grammar.InferenceRulesGrammarParser;

/**
 * A custom Error Listener to catch the lexer errors of inference rules gramamr.
 * 
 * @author Giovanni Lorenzo Napoleoni
 *
 */
public class GrammarErrorListner extends BaseErrorListener{
	
	private Logger log = Logger.getLogger(GrammarErrorListner.class);
	
	private boolean thereWasAnError;
	
	private GrammarException grammarException;
	
	@Override
	public void syntaxError(Recognizer<?, ?> recognizer,Object offendingSymbol,int line, int charPositionInLine,String msg,
			RecognitionException e)
	{
		List<String> stack = ((InferenceRulesGrammarParser)recognizer).getRuleInvocationStack();
		Collections.reverse(stack);
		
		log.error("rule stack: "+stack);
		log.error("line "+line+":"+charPositionInLine+" at "+
				offendingSymbol+": "+msg);
		
		setThereWasAnError(true);
		grammarException = new GrammarException("rule stack: "+stack+"\n"+"line "+line+":"+charPositionInLine+" at "+
				offendingSymbol+": "+msg);
		
	}

	public boolean isThereWasAnError() {
		return thereWasAnError;
	}

	public void setThereWasAnError(boolean thereWasAnError) {
		this.thereWasAnError = thereWasAnError;
	}

	public GrammarException getGrammarException() {
		return grammarException;
	}

	public void setGrammarException(GrammarException grammarException) {
		this.grammarException = grammarException;
	}

	
}
