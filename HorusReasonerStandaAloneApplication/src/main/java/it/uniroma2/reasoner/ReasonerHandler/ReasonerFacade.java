package it.uniroma2.reasoner.ReasonerHandler;

import it.uniroma2.art.owlart.exceptions.ModelAccessException;
import it.uniroma2.art.owlart.exceptions.ModelUpdateException;
import it.uniroma2.art.owlart.exceptions.QueryEvaluationException;
import it.uniroma2.art.owlart.exceptions.UnsupportedQueryLanguageException;
import it.uniroma2.art.owlart.model.ARTStatement;
import it.uniroma2.art.owlart.models.BaseRDFTripleModel;
import it.uniroma2.art.owlart.query.MalformedQueryException;
import it.uniroma2.reasoner.ConfigurationHandler.ConfigurationParameter;
import it.uniroma2.reasoner.OutputHandler.OutputHanlder;
import it.uniroma2.reasoner.domain.InferenceRules;

import java.util.List;

import org.apache.log4j.Logger;

/**
 * Facade to handle reasoning logic. 
 * 
 * 
 * @author Giovanni Lorenzo Napoleoni
 *
 */
public class ReasonerFacade {
	
	private static final Logger log = Logger.getLogger(ReasonerFacade.class);
	
	/**
	 * Execute the reasoning Logic. 
	 * 
	 * 
	 * @param inferenceRules List of inference rules to reasoning operation
	 * @param configurationParameter configuration parameter
	 * @param outputHanlder	output handler to store reasoning process output
	 * @param baseRDFTripleModel ontology where will apply the reasoning operation
	 * @return List of new information that have been generated from reasoning operation
	 * @throws UnsupportedQueryLanguageException
	 * @throws ModelAccessException
	 * @throws MalformedQueryException
	 * @throws QueryEvaluationException
	 * @throws ModelUpdateException
	 */
	public List<ARTStatement> doHandler(InferenceRules inferenceRules,
			ConfigurationParameter configurationParameter,OutputHanlder outputHanlder,BaseRDFTripleModel baseRDFTripleModel) throws UnsupportedQueryLanguageException, ModelAccessException, MalformedQueryException, QueryEvaluationException, ModelUpdateException{
		
		log.debug("Start reasoning operation");
		
		//Create new Reasoner
		Reasoner reasoner = new Reasoner(inferenceRules,baseRDFTripleModel,outputHanlder,configurationParameter);
		
		//Apply reasoning to ontology with using the inference rules
		return reasoner.reasoning();
	}


	


	

}
