package it.uniroma2.reasoner.InferenceRulesHandler;

import it.uniroma2.reasoner.domain.InferenceRule;
import it.uniroma2.reasoner.utils.GrammarException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.log4j.Logger;

import antr4.grammar.InferenceRulesGrammarBaseListener;
import antr4.grammar.InferenceRulesGrammarLexer;
import antr4.grammar.InferenceRulesGrammarParser;
import antr4.grammar.InferenceRulesGrammarParser.ParseInferenceRuleContext;

/**
 * Execute parsing of inference rules file.
 * 
 * @author Giovanni Lorenzo Napoleoni
 *
 */
public class Parser {

	
	private Logger log = Logger.getLogger(Parser.class);
	
	/**
	 * Use ANTLR parser to parsing inferenceRules file. 
	 * 
	 * @param inferenceRulesFile File that contains the definition of the rules of inference.
	 * @return list of inferenceRule object
	 * @throws GrammarException
	 * @throws IOException
	 */
	public List<InferenceRule> parsingInferenceRulesFile (File inferenceRulesFile) throws GrammarException, IOException {

		try{

			log.debug("ANTLR parsing file");
			
			//Create new input stream
			//InputStream fileInputStream = fileUtils.getFileInputStreamFromFile(inferenceRulesFile);	
			FileInputStream fileInputStream = new FileInputStream(inferenceRulesFile);
			
			//Create new ANTLR input stream on file
			ANTLRInputStream antlrInputStream = new ANTLRInputStream(fileInputStream);
			//Create Lexer
			InferenceRulesGrammarLexer lexer = new InferenceRulesGrammarLexer(antlrInputStream);
			//Get token stream from lexer
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			//Create new parser from token stream.
			InferenceRulesGrammarParser parser = new InferenceRulesGrammarParser(tokens);
			// remove ConsoleErrorListener
			parser.removeErrorListeners(); 
			// add custom ErrorListener
			GrammarErrorListner grammarErrorListner = new GrammarErrorListner();
			parser.addErrorListener(grammarErrorListner); 
			//Create new tree parsing explorer.
			ParseTreeWalker walker = new ParseTreeWalker();
			//Create new GrammarListner
			InferenceRulesGrammarBaseListener listener = new InferenceRulesGrammarBaseListener(); 
			//create new LoadParser
			LoaderParser loaderParser = new LoaderParser();
			//Execute the parsing operation
			ParseInferenceRuleContext inferenceRuleContext = parser.parseInferenceRule();
			//if there was an error on parsing operation, throw a new Grammar exception,else explore
			//parsing tree
			if (grammarErrorListner.isThereWasAnError()){
				
				throw grammarErrorListner.getGrammarException();
			}
						
			//Explore parsing tree
			walker.walk(loaderParser, inferenceRuleContext);
			//Add listner to parser
			parser.addParseListener(listener);
			//Get the inference rule parsed
			return loaderParser.getInferenceRules();


		}
		catch(IllegalArgumentException e){

			throw new GrammarException(e.getMessage());
		} 
	
	}
}
