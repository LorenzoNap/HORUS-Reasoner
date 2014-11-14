package it.uniroma2.OntologyModel;

import java.io.File;




import it.uniroma2.art.owlart.exceptions.ModelCreationException;
import it.uniroma2.art.owlart.io.RDFFormat;
import it.uniroma2.art.owlart.model.NodeFilters;
import it.uniroma2.art.owlart.models.OWLArtModelFactory;
import it.uniroma2.art.owlart.models.OWLModel;
import it.uniroma2.art.owlart.models.RDFModel;
import it.uniroma2.art.owlart.sesame2impl.factory.ARTModelFactorySesame2Impl;
import it.uniroma2.art.owlart.sesame2impl.models.conf.Sesame2ModelConfiguration;
import it.uniroma2.art.owlart.sesame2impl.models.conf.Sesame2NonPersistentInMemoryModelConfiguration;

public class LoadModel {

	
	
	public RDFModel loadRDFModel(String repo, String ontology) throws Exception{
		// creates the specific ModelFactory for Sesame2 implementation
		// usual creation of a Sesame2 model factory  
		ARTModelFactorySesame2Impl factImpl = new ARTModelFactorySesame2Impl(); 
		 
		// a model configuration is created for the Sesame2 implementation. Note that there are two contraints here: 
		// first it has been created from a Sesame2 model factory 
		// the configuration is a "parameters bundle" especially suited for "non persisting" "in-memory" sesame repositories. 
		Sesame2ModelConfiguration modelConf = 
		    factImpl.createModelConfigurationObject(Sesame2NonPersistentInMemoryModelConfiguration.class); 
		 
		
		 
		// a factory is created in the usual way. Note that it is possible to contrain the factory to only 
		// accept configurations of a given type. This is useful for static code to lessen chances of 
		// configuration misuse. The java generics constraint is however erased at runtime 
		OWLArtModelFactory<Sesame2ModelConfiguration> fact = OWLArtModelFactory.createModelFactory(factImpl); 
		
		// the third argument here passed to the loadXXXModel method specifies that the configuration created above will 
		// be used to determine the nature of the model being created
		OWLModel model = fact.loadOWLModel("http://wine", repo, modelConf);
		
		model.addRDF(new File(ontology), "", RDFFormat.RDFXML, NodeFilters.MAINGRAPH);
		
		return model;
	}
	
	public OWLModel loadOWLModel() throws ModelCreationException{
		ARTModelFactorySesame2Impl factImpl = new ARTModelFactorySesame2Impl();
		OWLArtModelFactory<Sesame2ModelConfiguration> fact = OWLArtModelFactory.createModelFactory(factImpl); 
		return fact.loadOWLModel("exportedplant‎", "C:\\prova");
	}
	
}
