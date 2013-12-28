package it.uniroma2.reasoner.ReasonerHandler;


import it.uniroma2.art.owlart.exceptions.ModelAccessException;
import it.uniroma2.art.owlart.exceptions.ModelUpdateException;
import it.uniroma2.art.owlart.exceptions.QueryEvaluationException;
import it.uniroma2.art.owlart.exceptions.UnsupportedQueryLanguageException;
import it.uniroma2.art.owlart.model.ARTStatement;
import it.uniroma2.art.owlart.model.NodeFilters;
import it.uniroma2.art.owlart.models.BaseRDFTripleModel;
import it.uniroma2.art.owlart.query.MalformedQueryException;
import it.uniroma2.reasoner.ConfigurationHandler.ConfigurationParameter;
import it.uniroma2.reasoner.ExecuteQueryHandler.ExecuteQueryOntologyHandler;
import it.uniroma2.reasoner.OutputHandler.OutputHanlder;
import it.uniroma2.reasoner.domain.InferenceRule;
import it.uniroma2.reasoner.domain.InferenceRuleOutput;
import it.uniroma2.reasoner.domain.InferenceRuleQueryResult;
import it.uniroma2.reasoner.domain.InferenceRules;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

/**
 * Execute reasoning operation.
 * 
 * 
 * @author Giovanni Lorenzo Napoleoni
 *
 */
public class Reasoner {
	
	
	//Object that contains list of inference rules
	private InferenceRules  inferenceRules;
	//Ontology model
	private BaseRDFTripleModel baseRDFTripleModel;
	//Output Handler
	private OutputHanlder inputOutputHanlder;
	
	private ConfigurationParameter configurationParameter;
	//Object to execute query on Ontology
	private ExecuteQueryOntologyHandler executeQueryOntologyHandler;
	
	private static final Logger log = Logger.getLogger(Reasoner.class);
	
	public static final String SUPPORT_ONTOLOGY_GRAPH = "http://supportOntology";

	int count =0;
	public Reasoner(InferenceRules  inferenceRules, BaseRDFTripleModel baseRDFTripleModel,OutputHanlder inputOutputHanlder,ConfigurationParameter configurationParameter) {
		super();
		this.inferenceRules = inferenceRules;
		this.baseRDFTripleModel = baseRDFTripleModel;
		this.inputOutputHanlder = inputOutputHanlder;
		this.configurationParameter = configurationParameter;
		executeQueryOntologyHandler = new ExecuteQueryOntologyHandler(baseRDFTripleModel);
	}




	/**
	 * Use the inference rule on ontolgy model to generate new information. 
	 * 
	 * @return List of RDF statment with new generated information
	 * @throws UnsupportedQueryLanguageException
	 * @throws ModelAccessException
	 * @throws MalformedQueryException
	 * @throws QueryEvaluationException
	 * @throws ModelUpdateException
	 */
	public List<ARTStatement> reasoning() throws UnsupportedQueryLanguageException, ModelAccessException, MalformedQueryException, QueryEvaluationException, ModelUpdateException{

		log.debug("Execute inference rules on Ontology");
		
			
		//Object to store the output of execution of inferenceRules
		List<InferenceRuleOutput> inferenceRulesOutuput = new ArrayList<InferenceRuleOutput>();
		
		boolean produceOutput = new Boolean(configurationParameter.getProperty(ConfigurationParameter.PRODUCE_OUTPUT));
		//Get the number of execution of Reasoning. 
		int numberOfExecution = Integer.parseInt(configurationParameter.getProperty(ConfigurationParameter.NUMBER_OF_EXECUTION));
		
		//Create a List that stores new generated Triple
		List<ARTStatement> results = new ArrayList<ARTStatement>();
		
		//Apply all inference rules on ontology as many times as expressed by the configuration parameters
		for(int count=0;count <numberOfExecution;count++){
			//Execute each single inference rule on ontology
			for(InferenceRule inferenceRule: inferenceRules.getInferenceRules() ){
				//Get the result of execution of query.
				List<InferenceRuleQueryResult> inferenceRuleQueryResults = executeQueryOntologyHandler.executeInferencerule(inferenceRule);
				//For each output that has been generated, check if the new information is already  on the ontology, if false create new output with 
				//new information and add the information (triples) on graph
				for(InferenceRuleQueryResult inferenceRuleQueryResult:inferenceRuleQueryResults ){
					
					//Check if a type rule is an inconsistency rule
					if(inferenceRule.getType().equals(InferenceRule.TPYE_INCONSISTENCY)){
						
						InferenceRuleOutput inferenceRuleOutput = produceOutput(produceOutput, inferenceRuleQueryResult, inferenceRule, inputOutputHanlder.getGraph());
						
						if(checkInconsistency(inferenceRuleOutput))
						//Add the output on the list of output
						inferenceRulesOutuput.add(inferenceRuleOutput);		
					}
					
					
					else if( !isStatementisOnOntology(inferenceRuleQueryResult, baseRDFTripleModel)){
						//Add all new triples on output result.
						results.addAll(inferenceRuleQueryResult.getTupleToConclusion());
						//Create new object to store the output of reasoning operation
						InferenceRuleOutput inferenceRuleOutput = produceOutput(produceOutput, inferenceRuleQueryResult, inferenceRule, inputOutputHanlder.getGraph());
						//Add the output on the list of output
						inferenceRulesOutuput.add(inferenceRuleOutput);					
					}
				}

			}

		}
		
		//Save the results of reasoning operation
		inputOutputHanlder.saveQuery(inferenceRulesOutuput);
		//Return resoning result
		return results;
	}

	
	private boolean checkInconsistency(InferenceRuleOutput inferenceRuleOutput) {
		boolean finded = false;
		
		for(ARTStatement triple: inferenceRuleOutput.getFromTriple()){
			String subject = triple.getSubject().asURIResource().getLocalName().replace("<", "").replace(">", "");
			String predicate = triple.getPredicate().asURIResource().getLocalName().replace("<", "").replace(">", "");
			String object = triple.getObject().asURIResource().getLocalName().replace("<", "").replace(">", "");

			String vertexSource = subject+" "+predicate+" "+object;
			if(!inputOutputHanlder.getGraphOfInconsistency().containsVertex(vertexSource)){
				inputOutputHanlder.getGraphOfInconsistency().addVertex(vertexSource);
				finded = true;
			}
					
		}
		
		return finded;
	}




	/**
	 * Check if the output must be produced
	 * 
	 * @param produceOutput
	 * @param inferenceRuleQueryResult
	 * @param inferenceRule
	 * @param stringGraph
	 * @return
	 */
	public InferenceRuleOutput produceOutput(boolean produceOutput, InferenceRuleQueryResult inferenceRuleQueryResult,InferenceRule inferenceRule,
			Graph<String, String> stringGraph){
				
		if(produceOutput){
			InferenceRuleOutput inferenceRuleOutput = new InferenceRuleOutput(inferenceRuleQueryResult.getTupleToPremise(),
					inferenceRuleQueryResult.getTupleToConclusion(),inferenceRule);
			
			addOnGraph(stringGraph,inferenceRuleOutput);
			return inferenceRuleOutput;
		}
		else {
			return new InferenceRuleOutput();
		}
		
	}


	/**
	 * Add a output of execution of inference rule on a graph. Store the new triples and the triples that have generated the new triples.
	 * @param stringGraph
	 * @param inferenceRuleOutput
	 */
	private void addOnGraph(Graph<String, String> stringGraph,InferenceRuleOutput inferenceRuleOutput) {
		
		for(ARTStatement statementSource : inferenceRuleOutput.getNew_triple()){

			String subject = statementSource.getSubject().asURIResource().getLocalName().replace("<", "").replace(">", "");
			String predicate = statementSource.getPredicate().asURIResource().getLocalName().replace("<", "").replace(">", "");
			String object = statementSource.getObject().asURIResource().getLocalName().replace("<", "").replace(">", "");

			String vertexSource = subject+" "+predicate+" "+object;
		//	vertexSource = vertexSource.replace("<", "").replace(">", "");
			
			stringGraph.addVertex(vertexSource);
			
			for(ARTStatement statementTarget :inferenceRuleOutput.getFromTriple()){
				
				subject = statementTarget.getSubject().asURIResource().getLocalName().replace("<", "").replace(">", "");
				predicate = statementTarget.getPredicate().asURIResource().getLocalName().replace("<", "").replace(">", "");
				object = statementTarget.getObject().asURIResource().getLocalName().replace("<", "").replace(">", "");

				String vertexTarget = subject+" "+predicate+" "+object;
				stringGraph.addVertex(vertexTarget);
				
				stringGraph.addEdge(String.valueOf(count),vertexSource,vertexTarget, EdgeType.DIRECTED);
				count++;
			}

		}

	}


	

	/**
	 * Check if statment is contained in the ontology. If true, add the statment in the ontology and return true,false otherwise. 
	 * 
	 * @param statement RDF statment to check
	 * @param model ontology 
	 * @return true if onotlogy has statement, false,otherwise
	 * @throws ModelAccessException
	 * @throws ModelUpdateException
	 */
	private boolean isStatementisOnOntology(InferenceRuleQueryResult inferenceRuleQueryResult,BaseRDFTripleModel model) throws ModelAccessException, ModelUpdateException {

		boolean isOnOntology = true;
		
		List<ARTStatement> filteredConclusion = new ArrayList<ARTStatement>();
		filteredConclusion.addAll( inferenceRuleQueryResult.getTupleToConclusion());
		
		for(ARTStatement statement : inferenceRuleQueryResult.getTupleToConclusion()){
			
			if(model.hasStatement(statement, false, NodeFilters.MAINGRAPH) || model.hasStatement(statement, false, model.createURIResource(SUPPORT_ONTOLOGY_GRAPH)) ){
				filteredConclusion.remove(statement);				
			}
			else {		

				model.addStatement(statement,model.createURIResource(SUPPORT_ONTOLOGY_GRAPH) );

				isOnOntology = false;
			}	
		}
		inferenceRuleQueryResult.setTupleToConclusion(filteredConclusion);
		return isOnOntology;


	}




	public OutputHanlder getInputOutputHanlder() {
		return inputOutputHanlder;
	}




	public void setInputOutputHanlder(OutputHanlder inputOutputHanlder) {
		this.inputOutputHanlder = inputOutputHanlder;
	}




	public BaseRDFTripleModel getBaseRDFTripleModel() {
		return baseRDFTripleModel;
	}




	public void setBaseRDFTripleModel(BaseRDFTripleModel baseRDFTripleModel) {
		this.baseRDFTripleModel = baseRDFTripleModel;
	}




	public ConfigurationParameter getConfigurationParameter() {
		return configurationParameter;
	}




	public void setConfigurationParameter(
			ConfigurationParameter configurationParameter) {
		this.configurationParameter = configurationParameter;
	}




	public InferenceRules getInferenceRules() {
		return inferenceRules;
	}




	public void setInferenceRules(InferenceRules inferenceRules) {
		this.inferenceRules = inferenceRules;
	}
}
