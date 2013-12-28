package it.uniroma2.reasoner.InferenceRulesHandler;

import it.uniroma2.reasoner.ConfigurationHandler.ConfigurationParameter;
import it.uniroma2.reasoner.domain.InferenceRule;
import it.uniroma2.reasoner.utils.ValidateException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Validate parsed inferenceRules. Check consistency of inference rules and get the only inference rules that have been specified
 * into properties file.
 * 
 * @author Giovanni Lorenzo Napoleoni
 *
 */
public class Validator {

	private ConfigurationParameter configurationParameter;
	
	private final static Logger log = Logger.getLogger(Validator.class);
	
	
	public Validator(ConfigurationParameter configurationParameter) {
		
		this.configurationParameter = configurationParameter;
	}
	
	/**
	 * Execute validation operations.
	 * First: get the  only inference rules that have been specified
	 * into properties file. 
	 * 
	 * @param inferenceRules
	 * @return true if all validation operations have gone ok,false otherwise
	 * @throws ValidateException
	 */
	public boolean validate(List<InferenceRule> inferenceRules) throws ValidateException{
		
		//TODO TOCHECK
		
		//Create a Map that contains a Rule ID and the InferenceRule with the same ID.
		Map<Integer,InferenceRule> mapInferenceRule = new HashMap<Integer, InferenceRule>();
		
		//
		Map<String,InferenceRule> mapInferenceRulesName = new HashMap<String, InferenceRule>();
		//Create new InferenceRule list
		List<InferenceRule> filteredInferenceRules = new ArrayList<InferenceRule>();
		//Populate map
		for(InferenceRule inferenceRule : inferenceRules){
			if(mapInferenceRule.get(inferenceRule.getInferenceRuleID()) == null){
				mapInferenceRule.put(inferenceRule.getInferenceRuleID(), inferenceRule);
			}
			else{
				throw new ValidateException("a rule with the same ID: "+inferenceRule.getInferenceRuleID()+" already existst.");
			}
			if(mapInferenceRulesName.get(inferenceRule.getInferenceRuleName()) == null){
				mapInferenceRulesName.put(inferenceRule.getInferenceRuleName(), inferenceRule);
			}
			else{
				throw new ValidateException("a rule with the same name: "+inferenceRule.getInferenceRuleName()+" already existst");
			}
		}
		//Get the rules id specified into property file.
		String rules = configurationParameter.getProperty(ConfigurationParameter.WHICHRULEEXECUTE);
		
		log.debug("Regole da applicare(lette da configurazione): "+rules);
		
		//Check if the ids are consistent.If true, search correspondence between the ids of parsedInferenceRule and the ids
		//content into property file. Else, finish validation operation
		if (rules.length()>=1 && !rules.equals("") && !rules.equals(" ") ){
			//Split string into ids.
			String[] splittedRules = rules.split(",");
			//get the lenght of splitted string
			int count = splittedRules.length;
			//Search into mapInferenceRule the InferenceRules that have the same ID content into property file
			for(int i=0;i < count;i++){
				//if there is a correspondence,add InferenceRule into a filteredList
				if ( mapInferenceRule.get(new Integer(splittedRules[i])) != null ){

					filteredInferenceRules.add(mapInferenceRule.get(new Integer(splittedRules[i])) );
				}
			}
			//Clear the inferenceRule list passed as paramter
			inferenceRules.clear();
			//Add all inference rules filtered.
			inferenceRules.addAll(filteredInferenceRules);
		}
		

		return true;
	}

	public ConfigurationParameter getConfigurationParameter() {
		return configurationParameter;
	}

	public void setConfigurationParameter(ConfigurationParameter configurationParameter) {
		this.configurationParameter = configurationParameter;
	}
	
}
