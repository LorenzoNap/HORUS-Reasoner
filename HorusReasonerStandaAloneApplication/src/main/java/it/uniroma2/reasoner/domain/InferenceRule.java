package it.uniroma2.reasoner.domain;

import java.util.ArrayList;
import java.util.List;


/**
 * Domain object that represents the Inference Rule. Each Inference rule has an ID,name and a list of premises and conclusions.
 * 
 * @author Giovanni Lorenzo Napoleoni
 *
 */
public class InferenceRule {

	
	private String type;
	
	private int inferenceRuleID;
	
	private String inferenceRuleName;
	
	private List<Triple> premisisTriple;
	
	private List<Triple> conclusionTriple;
	
	private String filterCondition;
	
	boolean isInconsistency;
	
	public static final String TPYE_INCONSISTENCY ="new inconsistency rule";
	
	
	public List<Triple> getPremisisTriple() {
		if(premisisTriple == null ){
			premisisTriple = new ArrayList<Triple>();
		}
		return premisisTriple;
	}
	
	public void setPremisisTriple(List<Triple> premisisTriple) {
		this.premisisTriple = premisisTriple;
	}
	
	public List<Triple> getConclusionTriple() {
		if(conclusionTriple == null ){
			conclusionTriple = new ArrayList<Triple>();
		}
		return conclusionTriple;
	}
	
	public void setConclusionTriple(List<Triple> conclusionTriple) {
		this.conclusionTriple = conclusionTriple;
	}
	
	public int getInferenceRuleID() {
		return inferenceRuleID;
	}
	
	public void setInferenceRuleID(int inferenceRuleID) {
		this.inferenceRuleID = inferenceRuleID;
	}
	
	public String getInferenceRuleName() {
		return inferenceRuleName;
	}
	
	public void setInferenceRuleName(String inferenceRuleName) {
		this.inferenceRuleName = inferenceRuleName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isInconsistency() {
		return isInconsistency;
	}

	public void setInconsistency(boolean isInconsistency) {
		this.isInconsistency = isInconsistency;
	}

	public String getFilterCondition() {
		return filterCondition;
	}

	public void setFilterCondition(String filterCondition) {
		this.filterCondition = filterCondition;
	}
	
	
	
	
	
	
}
