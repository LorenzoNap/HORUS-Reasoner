package it.uniroma2.reasoner.domain;

import java.util.ArrayList;
import java.util.List;


/**
 * Object that stores the output of inference rules parsing. 
 * 
 * @author Giovanni Lorenzo Napoleoni
 *
 */
public class InferenceRules {
	

	private List<InferenceRule> inferenceRules;
	
	
	public List<InferenceRule> getInferenceRules() {
		if(inferenceRules == null){
			inferenceRules = new ArrayList<InferenceRule>();
		}
		return inferenceRules;
	}

	public void setInferenceRules(List<InferenceRule> inferenceRules) {
		this.inferenceRules = inferenceRules;
	}
}
