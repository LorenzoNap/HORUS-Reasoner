package it.uniroma2.reasoner.domain;

import it.uniroma2.art.owlart.model.ARTStatement;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.reasoner.utils.OntologyUtilis;
import org.apache.log4j.Logger;

/**
 * Stores the output of reasoning process. 
 * 
 * @author Giovanni Lorenzo Napoleoni
 *
 */
public class ReasoningOutput {
	
	private final static Logger log = Logger.getLogger(ReasoningOutput.class);
	
	private List<InferenceRuleOutput> inferenceRuleOutput;

	

	public String printOuput(){
		StringBuilder output = new StringBuilder();
		
		for(InferenceRuleOutput inferenceRuleOutput: getInferenceRuleOutput()){
			
			output.append("########### Triple ##############"+"\n");
			output.append("Rule type: "+inferenceRuleOutput.getInferenceRule().getType()+"\n");
			output.append("Rule name: "+inferenceRuleOutput.getInferenceRule().getInferenceRuleName()+"\n");
			output.append("Rule ID: "+inferenceRuleOutput.getInferenceRule().getInferenceRuleID()+"\n");
			output.append("Result: ");
            /* Debug */
			log.debug("###################Output#####");
			log.debug("Rule name: "+inferenceRuleOutput.getInferenceRule().getInferenceRuleName());
			log.debug("Rule ID: "+inferenceRuleOutput.getInferenceRule().getInferenceRuleID());
			log.debug("Result: ");
			 /* Debug */
			if(inferenceRuleOutput.getInferenceRule().getType() != null && 
					inferenceRuleOutput.getInferenceRule().getType().equals(InferenceRule.TPYE_INCONSISTENCY)){
				output.append("inconsistency generated from: "+"\n");
	    		log.debug("inconsistency generated from: ");
	    		for (ARTStatement stmt :inferenceRuleOutput.getFromTriple()){
	    			log.debug(stmt);
	    			output.append(OntologyUtilis.convertARTStatementToString(stmt)+"\n");
	    		}
			}
			else{
				for (ARTStatement stmt :inferenceRuleOutput.getNew_triple()){
	    			output.append(OntologyUtilis.convertARTStatementToString(stmt)+"\n");
	    			log.debug(stmt);
	    		  }
				
				output.append("generated from: "+"\n");
	    		for (ARTStatement stmt :inferenceRuleOutput.getFromTriple()){
	    			log.debug(stmt);
	    			output.append(OntologyUtilis.convertARTStatementToString(stmt)+"\n");
	    		}	
	    		
    		
			}
    		
    	}
		return output.toString();		
	}
	
	public String printSingleRule(InferenceRuleOutput inferenceRuleOutput){
		StringBuilder output = new StringBuilder();
		output.append("########### Triple ##############"+"\n");
		output.append("Rule type: "+inferenceRuleOutput.getInferenceRule().getType()+"\n");
		output.append("Rule name: "+inferenceRuleOutput.getInferenceRule().getInferenceRuleName()+"\n");
		output.append("Rule ID: "+inferenceRuleOutput.getInferenceRule().getInferenceRuleID()+"\n");
		output.append("Result: ");
		if(inferenceRuleOutput.getInferenceRule().getType() != null && inferenceRuleOutput.getInferenceRule().getType().equals(InferenceRule.TPYE_INCONSISTENCY)){
			output.append("inconsistency generated from: "+"\n");
    		log.debug("inconsistency generated from: ");
    		for (ARTStatement stmt :inferenceRuleOutput.getFromTriple()){
    			log.debug(stmt);
    			output.append(OntologyUtilis.convertARTStatementToString(stmt)+"\n");
    		}
		}
		else{
			for (ARTStatement stmt :inferenceRuleOutput.getNew_triple()){
    			output.append(OntologyUtilis.convertARTStatementToString(stmt)+"\n");
    			log.debug(stmt);
    		  }
			
			output.append("generated from: "+"\n");
    		for (ARTStatement stmt :inferenceRuleOutput.getFromTriple()){
    			log.debug(stmt);
    			output.append(OntologyUtilis.convertARTStatementToString(stmt)+"\n");
    		}	
		}
		return output.toString();
	}
	
	public List<InferenceRuleOutput> getInferenceRuleOutput() {
		if(inferenceRuleOutput == null){
			inferenceRuleOutput = new ArrayList<InferenceRuleOutput>();
		}
		return inferenceRuleOutput;
	}

	public void setInferenceRuleOutput(List<InferenceRuleOutput> inferenceRuleOutput) {
		this.inferenceRuleOutput = inferenceRuleOutput;
	}
}
