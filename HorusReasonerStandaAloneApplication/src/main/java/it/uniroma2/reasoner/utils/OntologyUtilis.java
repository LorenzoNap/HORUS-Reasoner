package it.uniroma2.reasoner.utils;

import it.uniroma2.art.owlart.model.ARTNode;
import it.uniroma2.art.owlart.model.ARTResource;
import it.uniroma2.art.owlart.model.ARTStatement;
import it.uniroma2.art.owlart.model.ARTURIResource;
import it.uniroma2.art.owlart.models.BaseRDFTripleModel;
import it.uniroma2.art.owlart.query.TupleBindings;
import it.uniroma2.reasoner.domain.InferenceRule;
import it.uniroma2.reasoner.domain.Triple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;




/**
 * Utility Class to support operation on Reasoning.
 * 
 * @author Giovanni Lorenzo Napoleoni
 *
 */
public class OntologyUtilis {

	private static final String SELECT_OPERATION = "SELECT";
	private static final String WHERE_CLAUSOLE = "WHERE";
	private static final String FILTER_CLAUSOLE = "FILTER";

	/**
	 * Convert an InferenceRule in a Sparql query
	 * 
	 * @param inferenceRule inference rule to convert
	 * @return inference rule converted
	 */
	public static String createSPARQLQueryFromInferenceRule(InferenceRule inferenceRule){
		
		StringBuilder query = new StringBuilder();
		query.append(SELECT_OPERATION);
		query.append(" ");		
		//get the variable from inference rule premises
		for(String var: getVarFromInferenceRule(inferenceRule)){
			query.append(var+" ");
			
		}
		query.append(WHERE_CLAUSOLE);	
		query.append("{ \n");
		//convert each inference rule premises into sparql where clause
		for(Triple triple: inferenceRule.getPremisisTriple()){
			query.append(triple.getSubject()+" ");
			query.append(triple.getPredicate()+" ");
			query.append(triple.getObject()+".\n");
		}
		//Check filter
		if(inferenceRule.getFilterCondition() != null){
			query.append(FILTER_CLAUSOLE+"( ");
			query.append(inferenceRule.getFilterCondition()+ ")");
		}
		query.append("}");
		//return sparql query
		return query.toString();
	}

	/**
	 * Get from inference rule all variables
	 * @param inferenceRule
	 * @return list of all variables
	 */
	private static HashSet<String> getVarFromInferenceRule(InferenceRule inferenceRule){
		
		//List to store variable
		HashSet<String> varsList = new 	HashSet<String>(); 
		
		//Search variable in each premise 
		for(Triple triple: inferenceRule.getPremisisTriple()){
			
			//For subject,predicate,object, check if they are a var. If it is true, check if the variable
			//is a new or old variable, if it is new, saves it.
			if(isVar(triple.getSubject())){
				
				if(!varsList.contains(triple.getSubject())){
					
					varsList.add(triple.getSubject());
				}
			}
			if(isVar(triple.getPredicate())){
				
				if(!varsList.contains(triple.getPredicate())){
					varsList.add(triple.getPredicate());
				}
			}
			if(isVar(triple.getObject())){
				
				if(!varsList.contains(triple.getObject())){
					varsList.add(triple.getObject());
				}
			}
		}

		return varsList;
	}
	
	
	/**
	 * Convert a tuple generated from sparql query on BaseRDFTriplemodel ontology in ARTStatments. This conversion is based
	 * on a list of triples and  bindings operation from variable on triples and variables into tuple
	 * 
	 * @param tuple query result from sparql interrogation
	 * @param triples list of where to take the binding variables
	 * @param baseRDFTripleModel ontology model
	 * @return a set of ARTStatement that represents the binding conversion of variables
	 */
	public static List<ARTStatement> tupleToArtStatament(TupleBindings tuple,List<Triple> triples, BaseRDFTripleModel baseRDFTripleModel){
		
		//List to store final results
		List<ARTStatement> statments = new ArrayList<ARTStatement>();
		//Map: associates to each variable its value
		Map<String,ARTNode> varMap = new HashMap<String, ARTNode>();
		//Populate map with variables into tuple 
		for(String string : tuple.getBindingNames()) {
			varMap.put(string, tuple.getBinding(string).getBoundValue());
		}
		//For each triple: search all the values ​​to be assigned to the variables in the triple in the values 
		//​​of variables within the tuple.
		for(Triple triple: triples){

			ARTResource subject;
			ARTURIResource predicate;
			ARTNode object;
			
			//If subject or object or predicate contains a variable, get the value from map
			if( varMap.get(triple.getSubject().replaceFirst("\\?", "")) != null ){
				subject = varMap.get(triple.getSubject().replaceFirst("\\?", "")).asResource();
				
			}
			else{
				subject = baseRDFTripleModel.createURIResource(triple.getSubject()).asResource();
			}
			if(varMap.get(triple.getPredicate().replaceFirst("\\?", "")) != null){
				predicate = varMap.get(triple.getPredicate().replaceFirst("\\?", "")).asURIResource();
			}
			else{
				predicate = baseRDFTripleModel.createURIResource(triple.getPredicate());
			}
			if(varMap.get(triple.getObject().replaceFirst("\\?", "")) != null){
			
				object = varMap.get(triple.getObject().replaceFirst("\\?", ""));
			}
			else{
				object = baseRDFTripleModel.createURIResource(triple.getObject().toString()).asResource();
						
			}
			
			statments.add(baseRDFTripleModel.createStatement(subject, predicate, object));
		}
		
		return statments;
		
	}
	
	public static  String getValueFromTripleItem(ARTNode item){
		if(item.isBlank()){
			return item.asBNode().toString();
		}
		if(item.isLiteral()){
			return item.asLiteral().getNominalValue();
		}
		if(item.isResource()){
			return item.asURIResource().getLocalName();
		}
		return item.asURIResource().getLocalName();
		
	}
	
	public static boolean isVar(String subject) {
		if(subject.startsWith("?")){
			return true;
		}
		else{
			return false;
		}
	}
	
	

	
}
