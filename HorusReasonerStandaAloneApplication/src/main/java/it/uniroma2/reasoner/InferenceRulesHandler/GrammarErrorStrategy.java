package it.uniroma2.reasoner.InferenceRulesHandler;

import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;

/**
 * Custom Error Strategy to get error from parsing operation on inference rules file.
 * 
 * @author Giovanni Lorenzo Napoleoni
 *
 */
public class GrammarErrorStrategy extends DefaultErrorStrategy {

	@Override
	public void recover(Parser recognizer, RecognitionException e) {
		throw new IllegalArgumentException(e);
	}

	@Override
	protected Token getMissingSymbol(Parser arg0) {
		 throw new IllegalArgumentException(new InputMismatchException(arg0));
	}

	@Override
	public Token recoverInline(Parser recognizer) throws RecognitionException {
		  throw new IllegalArgumentException(new InputMismatchException(recognizer));
	}

	@Override
	public void sync(Parser arg0) {
		super.sync(arg0);
	}

	



 
}
