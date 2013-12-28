package it.uniroma2.reasoner.domain;

import it.uniroma2.art.owlart.model.ARTStatement;

import java.util.ArrayList;
import java.util.List;

/**
 * InferenceRuleOutput stores the output of inference rule application on ontology. The object stores the new information that
 * have been generated,the triple that have generated the new information and the inference rule.
 * 
 * @author Giovanni Lorenzo Napoleoni
 *
 */
public class InferenceRuleOutput {

	
	
	private  List<ARTStatement> fromTriple;
	
	private List<ARTStatement> new_triple;

	private InferenceRule inferenceRule;
	
	
	public InferenceRuleOutput(){
		
	}
	
	public InferenceRuleOutput(List<ARTStatement> fromTriple,
			List<ARTStatement> new_triple, InferenceRule inferenceRule) {
		super();
		this.fromTriple = fromTriple;
		this.new_triple = new_triple;
		this.inferenceRule = inferenceRule;
	}

	public List<ARTStatement> getNew_triple() {
		if(new_triple == null){
			new_triple = new ArrayList<ARTStatement>();
		}
		return new_triple;
	}

	public void setNew_triple(List<ARTStatement> new_triple) {
		this.new_triple = new_triple;
	}

	public InferenceRule getInferenceRule() {
		if(inferenceRule == null){
			inferenceRule = new InferenceRule();
		}
		return inferenceRule;
	}

	public void setInferenceRule(InferenceRule inferenceRule) {
		this.inferenceRule = inferenceRule;
	}

	public List<ARTStatement> getFromTriple() {
		if(fromTriple == null){
			fromTriple = new ArrayList<ARTStatement>();
		}
		return fromTriple;
	}

	public void setFromTriple(List<ARTStatement> fromTriple) {
		this.fromTriple = fromTriple;
	}
	
	
	
}
