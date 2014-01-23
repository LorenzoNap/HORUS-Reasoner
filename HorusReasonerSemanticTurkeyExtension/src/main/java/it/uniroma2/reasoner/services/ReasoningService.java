package it.uniroma2.reasoner.services;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.w3c.dom.Element;

import it.uniroma2.art.owlart.exceptions.ModelUpdateException;
import it.uniroma2.art.owlart.model.ARTStatement;
import it.uniroma2.art.owlart.model.ARTURIResource;
import it.uniroma2.art.owlart.models.BaseRDFTripleModel;
import it.uniroma2.art.owlart.models.RDFModel;
import it.uniroma2.art.semanticturkey.exceptions.HTTPParameterUnspecifiedException;
import it.uniroma2.art.semanticturkey.plugin.extpts.ServiceAdapter;
import it.uniroma2.art.semanticturkey.project.Project;
import it.uniroma2.art.semanticturkey.project.ProjectManager;
import it.uniroma2.art.semanticturkey.servlet.Response;
import it.uniroma2.art.semanticturkey.servlet.XMLResponseREPLY;
import it.uniroma2.art.semanticturkey.servlet.ServiceVocabulary.RepliesStatus;
import it.uniroma2.art.semanticturkey.utilities.XMLHelp;
import it.uniroma2.reasoner.ConfigurationHandler.ConfigurationParameter;
import it.uniroma2.reasoner.ReasonerHandler.Reasoner;
import it.uniroma2.reasoner.start.StartReasonerFacade;
import it.uniroma2.reasoner.utils.ServicesUtils;

/**
 * exposed services from  reasoner extension of semantic turkey. The services used to handler the reasoning operation
 * and output. 
 * 
 * @author Giovanni Lorenzo Napoleoni
 *
 */
public class ReasoningService extends ServiceAdapter{
	
	// Requests definition
	private ReasonerService reasonerServiceConfiguration;
	
	private StartReasonerFacade reasonerFacade;
	
	private BaseRDFTripleModel baseRDFTripleModel;
	
	// Parameters of the requests
	public static final String startReasoning = "startReasoning";
	
	public static final String searchNewTriple = "searchNewTriple";
	
	public static final String removeNewTriple = "removeNewTriple"; 
	
	public static final String getJsonGraph = "getJsonGraph";
	
	public static final String searchTripleFromGraph = "searchTripleFromGraph";
	
	public static final String applyFilterToOuptut = "applyFilterToOuptut";
	
	
	public static final  String howManyTimesApllyInferenceRule = "howManyTimesApllyInferenceRule";
	
	public static final  String whicRulesApply = "whicRulesApply";
	
	public static final  String produceOutput = "produceOutput";
	
	public static final String subject = "subject";
	
	public static final String predicate = "predicate";
	
	public static final String object = "object";
	
	public static final String triple = "triple";
	
	public static final String rules = "rules";
	
	

	@Autowired
	public ReasoningService(@Value("reasoningService")String id) {
		super(id);
		reasonerFacade = new StartReasonerFacade();
	}

	
	@Override
	protected Response getPreCheckedResponse(String request) throws HTTPParameterUnspecifiedException {
		
		//Create new response
		Response response = null;
		//Flag to check whether at least one request was handled
		boolean almostOneRequest = false;
		if (request == null){
			return servletUtilities.createNoSuchHandlerExceptionResponse(request);
		}
		
		if (request.equals(startReasoning)) {	
			String cycleReasoning = setHttpPar(howManyTimesApllyInferenceRule);
			String idsRule = setHttpPar(whicRulesApply);
			String output = setHttpPar(produceOutput);
			response = startReasoning(cycleReasoning,idsRule,output);		
			almostOneRequest = true;
		}
		
		if(request.equals(searchNewTriple)){
			String subjectTriple = setHttpPar(subject);
			String predicateTriple = setHttpPar(predicate);
			String objectTriple = setHttpPar(object);
			response = searchTriple(subjectTriple,predicateTriple,objectTriple);
			almostOneRequest = true;
			
		}
		
		if(request.equals(removeNewTriple)){
			response = removeTriple();		
			almostOneRequest = true;
		}
		
		if(request.equals(getJsonGraph)){
			response =  getJsonGraph();		
			almostOneRequest = true;
		}
		
		if(request.equals(searchTripleFromGraph)){
			String tripleToSearch = setHttpPar(triple);
			response =  searchTripleFromGraph(tripleToSearch);
			almostOneRequest = true;
		}
		
		if(request.equals(applyFilterToOuptut)){
			String rulesToFilter = setHttpPar(rules);
			almostOneRequest = true;
			response = filterOutput(rulesToFilter);
		}
		
		if (!almostOneRequest){
			return servletUtilities.createNoSuchHandlerExceptionResponse(request);
		}
		
		

		this.fireServletEvent();
		return response;

	}
	
	
	private Response filterOutput(String rulesToFilter) {
		XMLResponseREPLY response;
		response = createReplyResponse(RepliesStatus.ok);
		Element dataElement = response.getDataElement();
		Element graphElem = XMLHelp.newElement(dataElement, "filter");
		//Populate response with history of triple
		graphElem.setAttribute("resultToList", reasonerFacade.filterOutput(rulesToFilter));
		return response;
	}


	/**
	 * Given a triple, search triple history on graph produced from reasoning
	 * @param triple triple to search
	 * @return response
	 */
	private Response searchTripleFromGraph(String triple) {
		XMLResponseREPLY response;
		response = createReplyResponse(RepliesStatus.ok);
		Element dataElement = response.getDataElement();
		Element graphElem = XMLHelp.newElement(dataElement, "bfsGraph");
		//Populate response with history of triple
		graphElem.setAttribute("json", ServicesUtils.searchTripleOnGraph(triple, reasonerFacade.getInputOutputHanlder().getGraph()));
		return response;
	}

	/**
	 * Return a json representation of reasoning output graph
	 * @return
	 */
	private Response getJsonGraph() {
		XMLResponseREPLY response;
		response = createReplyResponse(RepliesStatus.ok);
		Element dataElement = response.getDataElement();
		Element graphElem = XMLHelp.newElement(dataElement, "jsonGraph");
		graphElem.setAttribute("jsonGraph",ServicesUtils.convertGraphToJson(reasonerFacade.getInputOutputHanlder().getAlternativeGraph()).toJSONString());
		return response;
		
	}

	/**
	 * Removed the new discovered triple from ontology model.
	 * @return
	 */
	private Response removeTriple() {
		
		XMLResponseREPLY response;
		
		try {
            Project<? extends RDFModel> project= null;
            project = ProjectManager.getCurrentProject();
            baseRDFTripleModel = project.getOntModel();
			//Remove triple from the ontology model
			baseRDFTripleModel.clearRDF(baseRDFTripleModel.createURIResource(Reasoner.SUPPORT_ONTOLOGY_GRAPH));
		} catch (ModelUpdateException e) {		
			response = createReplyResponse(RepliesStatus.fail);
			Element dataElement = response.getDataElement();
			Element errorElem = XMLHelp.newElement(dataElement, "removeTriple");
			errorElem.setAttribute("error", e.toString());
			return response;
		}
		//Populate response
		response = createReplyResponse(RepliesStatus.ok);
		Element dataElement = response.getDataElement();
		Element responseElem = XMLHelp.newElement(dataElement, "removeTriple");
		responseElem.setAttribute("success", "true");
		return response;
	}



	/**
	 * Search a history of given triple on output text produced from reasoning.
	 * @param subjectTriple the subject of triple
	 * @param predicateTriple the predicate of triple
	 * @param objectTriple the object of triple
	 * @return response
	 */
	private Response searchTriple(String subjectTriple, String predicateTriple,
			String objectTriple) {
		
		//Convert string to ART resource
		ARTURIResource subj = baseRDFTripleModel.createURIResource(subjectTriple);
		ARTURIResource predicate =  baseRDFTripleModel.createURIResource(predicateTriple);
		ARTURIResource object = baseRDFTripleModel.createURIResource(objectTriple);
		//Create new statement with resources
		ARTStatement stm = baseRDFTripleModel.createStatement(subj, predicate, object);
		//Search history of statement
		String result = reasonerFacade.searchHistoryTriple(stm);
		//Create response
		XMLResponseREPLY response;
		response = createReplyResponse(RepliesStatus.ok);
		Element dataElement = response.getDataElement();
		Element searchTripleElement = XMLHelp.newElement(dataElement, "searchTriple");
		//Populate response with result of search
		searchTripleElement.setAttribute("historyOfTriple", result);
		return response;
	}

	/**
	 * Start reasoning operation with parameters given by the user
	 * @param cycleReasoning number of how much time execute reasoning operation
	 * @param idsRule the ids number of inference rules to apply
	 * @param output flag to produce output
	 * @return
	 */
	private Response startReasoning(String cycleReasoning, String idsRule,String output) {
		XMLResponseREPLY response;
		//Retrieve inference rules file
		File inferenceRuleFile = reasonerServiceConfiguration.getInferenceRuleFile();
		
		Project<? extends RDFModel> project= null;
		try {
			//Set parameters of reasoning operation
			reasonerFacade.setParameter(ConfigurationParameter.NUMBER_OF_EXECUTION, cycleReasoning);
			reasonerFacade.setParameter(ConfigurationParameter.PRODUCE_OUTPUT, output);
			reasonerFacade.setParameter(ConfigurationParameter.WHICHRULEEXECUTE, idsRule);
			//Get current project to apply resoning operation
			project = ProjectManager.getCurrentProject();
			//check if project is exist. If true do reasoning, else return a warning message
			if (project  == null){
				response = createReplyResponse(RepliesStatus.warning);
				Element dataElement = response.getDataElement();
				Element errorElem = XMLHelp.newElement(dataElement, "startReasoning");
				errorElem.setAttribute("warning", "you must select almost one project");
				return response;
			}
			//Get ontology model from reasoner
			baseRDFTripleModel = project.getOntModel();
			//Apply reasoning on model
			List<ARTStatement> new_triple = reasonerFacade.startReasoner(baseRDFTripleModel,inferenceRuleFile);
			//Check if new triple has been discovered. If it is false, return a warning message, else return a positive response
			if (new_triple != null && new_triple.size() == 0){
				response = createReplyResponse(RepliesStatus.warning);
				Element dataElement = response.getDataElement();
				Element warnElem = XMLHelp.newElement(dataElement, "startReasoning");
				warnElem.setAttribute("warning", "new triple have not been found");
				return response;				
			}
			//Create response
			response = createReplyResponse(RepliesStatus.ok);
			Element dataElement = response.getDataElement();
			Element reasonerElem = XMLHelp.newElement(dataElement, "startReasoning");
			//Populate response
			reasonerElem.setAttribute("produceOutput", output);
			reasonerElem.setAttribute("numberOfIteration", String.valueOf(reasonerFacade.getInputOutputHanlder().getNumberOfIteration()));
			reasonerElem.setAttribute("numberOfTriple", Integer.toString(new_triple.size()));
			reasonerElem.setAttribute("printOutput", reasonerFacade.getOutputList().printOutput());
			reasonerElem.setAttribute("inferenceRulesNames", reasonerFacade.getInferenceRulesName());
			reasonerElem.setAttribute("jsonInference", ServicesUtils.convertInferenceRuleOutputToJson(reasonerFacade.getOutputList()));
			//return response
			return response;
			
		} catch (Exception e) {
			response = createReplyResponse(RepliesStatus.fail);
			Element dataElement = response.getDataElement();
			Element errorElem = XMLHelp.newElement(dataElement, "startReasoning");
			errorElem.setAttribute("error", e.toString());
			return response;
		}

	}
	
	

	public ReasonerService getReasonerServiceConfiguration() {
		return reasonerServiceConfiguration;
	}

	public void setReasonerServiceConfiguration(
			ReasonerService reasonerServiceConfiguration) {
		this.reasonerServiceConfiguration = reasonerServiceConfiguration;
	}
	
	@Override
	protected Logger getLogger() {
		return null;
	}

}
