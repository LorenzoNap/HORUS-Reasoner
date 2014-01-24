package it.uniroma2.reasoner.start;

import it.uniroma2.art.owlart.exceptions.ModelAccessException;
import it.uniroma2.art.owlart.exceptions.ModelUpdateException;
import it.uniroma2.art.owlart.exceptions.QueryEvaluationException;
import it.uniroma2.art.owlart.exceptions.UnsupportedQueryLanguageException;
import it.uniroma2.art.owlart.exceptions.UnsupportedRDFFormatException;
import it.uniroma2.art.owlart.model.ARTStatement;
import it.uniroma2.art.owlart.models.BaseRDFTripleModel;
import it.uniroma2.art.owlart.query.MalformedQueryException;
import it.uniroma2.reasoner.ConfigurationHandler.ConfigurationParameter;
import it.uniroma2.reasoner.InferenceRulesHandler.ParseInferenceRulesFacade;
import it.uniroma2.reasoner.OutputHandler.OutputHandler;
import it.uniroma2.reasoner.ReasonerHandler.ReasonerFacade;
import it.uniroma2.reasoner.domain.InferenceRule;
import it.uniroma2.reasoner.domain.InferenceRules;
import it.uniroma2.reasoner.domain.ReasoningOutput;
import it.uniroma2.reasoner.utils.GrammarException;
import it.uniroma2.reasoner.utils.ValidateException;
import it.uniroma2.reasoner.utils.Graph.GraphUtils;
import it.uniroma2.reasoner.utils.Graph.GraphView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import edu.uci.ics.jung.graph.Graph;



/**
 * Entry Point of Reasoner.Given an ontology model it is possible to launcher a complete operation of reasoning. 
 * 
 * 
 * @author Giovanni Lorenzo Napoleoni
 *
 */
public class StartReasonerFacade {

	private OutputHandler inputOutputHandler;

	private InferenceRules inferenceRules ;

	private final static Logger log = Logger.getLogger(StartReasonerFacade.class);

	/**
	 * Execute reasoning operation logic.
	 * 
	 * @param inputOntology ontology to execute reasoning operation
	 * @return list of ARTStatment with new information generated from reasoning operation
	 * @throws UnsupportedRDFFormatException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws ValidateException 
	 * @throws GrammarException 
	 * @throws ModelUpdateException 
	 * @throws QueryEvaluationException 
	 * @throws MalformedQueryException 
	 * @throws ModelAccessException 
	 * @throws UnsupportedQueryLanguageException 
	 */
	public List<ARTStatement> startReasoner(BaseRDFTripleModel inputOntology, File inferenceRuleFile) throws UnsupportedRDFFormatException, FileNotFoundException, IOException, GrammarException, ValidateException, UnsupportedQueryLanguageException, ModelAccessException, MalformedQueryException, QueryEvaluationException, ModelUpdateException {


		List<ARTStatement> newInferredTriples = new ArrayList<ARTStatement>();

		//Create new configuration Parameter
		ConfigurationParameter configurationParameter = ConfigurationParameter.getIstance();
		//Create Input/Output handler
		inputOutputHandler = new OutputHandler();
		//Create new parserFacade
		ParseInferenceRulesFacade parseInferenceRulesFacade = new ParseInferenceRulesFacade(configurationParameter,inferenceRuleFile);
		//Parse inference rules from file
		inferenceRules = parseInferenceRulesFacade.doHandler(inferenceRuleFile);
		log.debug("Inference rules parsed");	    	
		//Create ReasonerFacade
		ReasonerFacade reasonerFacade = new ReasonerFacade();
		//Reasonig on ontology
		newInferredTriples = reasonerFacade.doHandler(inferenceRules,configurationParameter, inputOutputHandler,inputOntology);
		log.debug("End reasoning operation");
		//return new triple
		return newInferredTriples;

	}

	public ReasoningOutput getOutputList(){	

		return inputOutputHandler.getReasoningOutput();
	}

	public String printGraphOfNewTriple(){

		return inputOutputHandler.printGraph();
	}

	public void showGraphOnWindow(){
		 inputOutputHandler.showGraphOfNewTripleOnWindows();
	}

	/**
	 * Search how a ARTStatment has been generated and show it on a graphical representation.
	 * @param artStatement
	 */
	public void searchEdge(ARTStatement artStatement){


		String subject = artStatement.getSubject().asURIResource().getLocalName().replace("<", "").replace(">", "");
		String predicate = artStatement.getPredicate().asURIResource().getLocalName().replace("<", "").replace(">", "");
		String object = artStatement.getObject().asURIResource().getLocalName().replace("<", "").replace(">", "");

		String vertexSource = subject+" "+predicate+" "+object;

		if (inputOutputHandler.getGraph().containsVertex(vertexSource)){

			Graph<String,String> graph = GraphUtils.BreabreadthFirstSearch(inputOutputHandler.getGraph(),vertexSource);
			GraphView.showGraph(graph,"History of new triple");
		}
		log.info("Unable to see triple. Triple doesn't exist");

	}

	/**
	 * Given an triple, search how the triple has been generated 
	 * @param artStatement the triple to research
	 * @return a text representation of history
	 */
	public String searchHistoryTriple(ARTStatement artStatement){

		String subject = artStatement.getSubject().asURIResource().getLocalName().replace("<", "").replace(">", "");
		String predicate = artStatement.getPredicate().asURIResource().getLocalName().replace("<", "").replace(">", "");
		String object = artStatement.getObject().asURIResource().getLocalName().replace("<", "").replace(">", "");

		String vertexSource = subject+" "+predicate+" "+object;
		//If the output graph contains the triple, return history, else return an empty string		
		if (inputOutputHandler.getGraph().containsVertex(vertexSource)){

			return GraphUtils.BreabreadthFirstSearchHistoryTriple(inputOutputHandler.getGraph(),vertexSource);
		}
		return "";
	}


	public void setParameter(String key,String value) throws FileNotFoundException, IOException{

		//Create new configuration Parameter
		ConfigurationParameter configurationParameter = ConfigurationParameter.getIstance();
		configurationParameter.modifyProperties(key, value);

	}

	public String getConfigurationParameter(String key) throws FileNotFoundException, IOException {

		//Create new configuration Parameter
		ConfigurationParameter configurationParameter = ConfigurationParameter.getIstance();
		return configurationParameter.getProperty(key);

	}	

	public String getInferenceRulesName(){
		
		StringBuilder builder = new StringBuilder();
		
		for(InferenceRule inferenceRule: inferenceRules.getInferenceRules()){
			builder.append(inferenceRule.getInferenceRuleName()+",");
		}
		return builder.toString();
		
	}
	
	
	public String filterOutput(String filters){
		
		return inputOutputHandler.applyFiltersToList(filters);
	}

	public OutputHandler getInputOutputHandler() {
		return inputOutputHandler;
	}

	public void setInputOutputHandler(OutputHandler inputOutputHandler) {
		this.inputOutputHandler = inputOutputHandler;
	}
	
	
	
}
