package it.uniroma2.reasoner.InferenceRulesHandler;

import it.uniroma2.reasoner.ConfigurationHandler.ConfigurationParameter;
import it.uniroma2.reasoner.domain.InferenceRule;
import it.uniroma2.reasoner.domain.InferenceRules;
import it.uniroma2.reasoner.utils.GrammarException;
import it.uniroma2.reasoner.utils.ValidateException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * Facade to handle the parsing logic of inference rules. The inference rules are specified into a file text.
 * 
 * @author Giovanni Lorenzo Napoleoni
 *
 */
public class ParseInferenceRulesFacade {
	
	//Components of Parsing Logic
	
	private Parser parser;
	
	private Validator validator;
	
	private ConfigurationParameter configurationParameter;
	
	private Logger log = Logger.getLogger(ParseInferenceRulesFacade.class);
	
	private File inferenceRuleFile;

	private static final String VALIDATE_ERROR = "validate error";
	
	
	public ParseInferenceRulesFacade(ConfigurationParameter configurationParameter, File inferenceRuleFile) {
		
		this.configurationParameter = configurationParameter;
		this.inferenceRuleFile	= inferenceRuleFile;
	}

	/**
	 * Execute the parsing logic of inference rules file.
	 * 
	 * 
	 * @return the inference rules parsed
	 * @throws GrammarException
	 * @throws IOException
	 * @throws ValidateException
	 */
	public InferenceRules doHandler(File inferenceRulesFile) throws GrammarException, IOException, ValidateException {

		
		log.debug("Parsing inference Rules file");
		
		//Create an empty list of InferenceRules
		List<InferenceRule> inferenceRules = new ArrayList<InferenceRule>();

		//Get file ,from path specified into ConfigurationParameter, with the inference rules
		//FileUtil fileUtil = new FileUtil();
		//File inferenceRulesFile = fileUtil.getFileFromPath(configurationParameter.getProperty(ConfigurationParameter.FILE_PATH_RULES));
		
		
		//Create new Parser
		parser = new Parser();
		//Parsing inference rules file
		inferenceRules = parser.parsingInferenceRulesFile(inferenceRulesFile);
		//Create new validator
		Validator validator = new Validator(configurationParameter);
		//Validate inferenceRules
		boolean passValidation = validator.validate(inferenceRules);
		//If validation is OK, create a new InferenceRules Object and insert inference rules parsed on it and return the InferenceRule
		//Object. If validation is KO, throw new Validate Exception
		log.debug("Valdation rules");
		if (passValidation == true){
			//Create new InferenceRulesObject
			InferenceRules inferenceRulesObject = new InferenceRules();
			//Insert parsed inference rules
			inferenceRulesObject.setInferenceRules(inferenceRules);
			//return inferenceRules
			log.debug("Valdation OK");
			return inferenceRulesObject;
		}
		else {
			log.debug("Valdation KO");
			log.error(VALIDATE_ERROR);
			throw new ValidateException(VALIDATE_ERROR);
		}


	}

	public Parser getParser() {
		return parser;
	}

	public void setParser(Parser parser) {
		this.parser = parser;
	}

	public Validator getValidator() {
		return validator;
	}

	public void setValidator(Validator validator) {
		this.validator = validator;
	}

	public File getInferenceRuleFile() {
		return inferenceRuleFile;
	}

	public void setInferenceRuleFile(File inferenceRuleFile) {
		this.inferenceRuleFile = inferenceRuleFile;
	}

}
