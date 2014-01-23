package it.uniroma2.reasoner.ExecuteQueryHandler;

import it.uniroma2.art.owlart.exceptions.ModelAccessException;
import it.uniroma2.art.owlart.exceptions.QueryEvaluationException;
import it.uniroma2.art.owlart.exceptions.UnsupportedQueryLanguageException;
import it.uniroma2.art.owlart.model.ARTStatement;
import it.uniroma2.art.owlart.models.BaseRDFTripleModel;
import it.uniroma2.art.owlart.query.MalformedQueryException;
import it.uniroma2.art.owlart.query.QueryLanguage;
import it.uniroma2.art.owlart.query.TupleBindings;
import it.uniroma2.art.owlart.query.TupleBindingsIterator;
import it.uniroma2.art.owlart.query.TupleQuery;
import it.uniroma2.reasoner.domain.InferenceRule;
import it.uniroma2.reasoner.domain.InferenceRuleQueryResult;
import it.uniroma2.reasoner.utils.OntologyUtilis;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Transform an Inference Rule object in a query to execute on ontology and create a correspondence between premises and conclusions
 * in the inference rule and the results of query execution. 
 * 
 * @author Giovanni Lorenzo Napoleoni
 *
 */
public class ExecuteQueryOntologyHandler {
	
	private BaseRDFTripleModel baseRDFTripleModel;

	private static final  Logger log = Logger.getLogger(ExecuteQueryOntologyHandler.class);
	
	public ExecuteQueryOntologyHandler(BaseRDFTripleModel baseRDFTripleModel) {
		this.baseRDFTripleModel = baseRDFTripleModel;
	}

	/**
	 *Execute a single inference rule on ontology. From result, set the conclusions expressed into the inference rules
	 * and returns the reasoner.
	 * 
	 * @param inferenceRule inference rule to execute.
	 * @return list of new generated information
	 * @throws UnsupportedQueryLanguageException
	 * @throws ModelAccessException
	 * @throws MalformedQueryException
	 * @throws QueryEvaluationException
	 */
	public List<InferenceRuleQueryResult> executeInferencerule(InferenceRule inferenceRule) throws UnsupportedQueryLanguageException, ModelAccessException, MalformedQueryException, QueryEvaluationException {

		log.debug("Execute Queries");
		
		//Create a list to store query result
		List<InferenceRuleQueryResult> inferenceRuleQueryResults = new ArrayList<InferenceRuleQueryResult>();		
		//Build sparql query from inferenceRule	
		String query = OntologyUtilis.createSPARQLQueryFromInferenceRule(inferenceRule);
		//Create sparql query on ontology model
		TupleQuery tupleQuery = baseRDFTripleModel.createTupleQuery(QueryLanguage.SPARQL,query,
				baseRDFTripleModel.getBaseURI());
		//Execute query on ontology
		TupleBindingsIterator tupleIter = tupleQuery.evaluate(false); 
			//Get all results from query
			while (tupleIter.hasNext()) {
				//Get row from results
				TupleBindings tuple = tupleIter.next();        
				//Convert result in ARTStatment from premisis of inferencerule
				List<ARTStatement> tupleToPremise = OntologyUtilis.tupleToArtStatament(tuple, inferenceRule.getPremisisTriple(),baseRDFTripleModel);
				//Convert result in ARTStatment from conclusions of inference rule
				List<ARTStatement> tupleToConclusion = OntologyUtilis.tupleToArtStatament(tuple, inferenceRule.getConclusionTriple(),baseRDFTripleModel);

                String filterCondition = OntologyUtilis.getFilterCondition(tuple, inferenceRule.getFilterCondition());

				InferenceRuleQueryResult inferenceRuleQueryResult = new InferenceRuleQueryResult();				
				//Add conclusions to final Result
				inferenceRuleQueryResult.getTupleToConclusion().addAll(tupleToConclusion);
				inferenceRuleQueryResult.getTupleToPremise().addAll(tupleToPremise);
                inferenceRuleQueryResult.setFilterStatement(filterCondition);
				
				inferenceRuleQueryResults.add(inferenceRuleQueryResult);
			}
			//Close di tuple iteration
			tupleIter.close(); 

		//return inference rules conclusion results
		return inferenceRuleQueryResults;
	}
	
	

	public BaseRDFTripleModel getBaseRDFTripleModel() {
		return baseRDFTripleModel;
	}

	public void setBaseRDFTripleModel(BaseRDFTripleModel baseRDFTripleModel) {
		this.baseRDFTripleModel = baseRDFTripleModel;
	}

}
