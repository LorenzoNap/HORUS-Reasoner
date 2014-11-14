package it.uniroma2.runreasoner;

import java.io.File;

import it.uniroma2.OntologyModel.LoadModel;
import it.uniroma2.art.owlart.model.ARTURIResource;
import it.uniroma2.art.owlart.models.BaseRDFTripleModel;
import it.uniroma2.reasoner.ConfigurationHandler.ConfigurationParameter;
import it.uniroma2.reasoner.domain.ReasoningOutput;
import it.uniroma2.reasoner.start.StartReasonerFacade;


public class RunReasoner {

	
	
	public static void main(String[] args) throws Exception {
	
		//Create new LoadModel
		LoadModel model = new LoadModel();
		//Create new StartReasonerFacade
		StartReasonerFacade startReasoner = new StartReasonerFacade();
		//LoadRDFModel, pass as arguments a repo folder and ontology file path.
		BaseRDFTripleModel modelRDF = model.loadRDFModel(args[0],args[1]);
		//Start reasoning operation
		
		File file = new File ("inputFile\\InferenceRules.txt");
		startReasoner.startReasoner(modelRDF,file);
		//Get output
		ReasoningOutput reasoningOutput = startReasoner.getOutputList();
		//print output
		reasoningOutput.printOuput();	
		//Show console representation of new triples
		startReasoner.printGraphOfNewTriple();
		//Show graphical representation of new triples
		startReasoner.showGraphOnWindow();
		
		//TODO DEBUG
		//Create a new statement
		ARTURIResource subj = modelRDF.createURIResource("http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#Albano");
		ARTURIResource object =  modelRDF.createURIResource("http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#Ariccia");
		ARTURIResource predicate = modelRDF.createURIResource("http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#isAdiacent");
		
		//Search how the statement has been generated
		startReasoner.searchHistoryTriple(modelRDF.createStatement(subj, predicate, object));
		
		
		
		startReasoner.getOutputList().printOuput();
		
		//close model
		modelRDF.close();
		
		
	}

}
