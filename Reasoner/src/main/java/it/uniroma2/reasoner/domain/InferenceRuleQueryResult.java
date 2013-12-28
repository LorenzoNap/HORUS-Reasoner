package it.uniroma2.reasoner.domain;

import it.uniroma2.art.owlart.model.ARTStatement;

import java.util.ArrayList;
import java.util.List;

public class InferenceRuleQueryResult {
	
	private List<ARTStatement> tupleToPremise;
	
	private List<ARTStatement> tupleToConclusion;

	
	public InferenceRuleQueryResult(){
		tupleToPremise = new ArrayList<ARTStatement>();
		tupleToConclusion = new ArrayList<ARTStatement>();
	}
	
	public List<ARTStatement> getTupleToPremise() {
		return tupleToPremise;
	}

	public void setTupleToPremise(List<ARTStatement> tupleToPremise) {
		this.tupleToPremise = tupleToPremise;
	}

	public List<ARTStatement> getTupleToConclusion() {
		return tupleToConclusion;
	}

	public void setTupleToConclusion(List<ARTStatement> tupleToConclusion) {
		this.tupleToConclusion = tupleToConclusion;
	}
	
	
	

}
