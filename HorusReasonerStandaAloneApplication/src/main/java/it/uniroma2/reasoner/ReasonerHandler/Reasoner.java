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
import it.uniroma2.reasoner.OutputHandler.OutputHandler;
import it.uniroma2.reasoner.domain.InferenceRule;
import it.uniroma2.reasoner.domain.InferenceRuleOutput;
import it.uniroma2.reasoner.domain.InferenceRuleQueryResult;
import it.uniroma2.reasoner.domain.InferenceRules;
import it.uniroma2.reasoner.utils.OntologyUtilis;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

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
    private OutputHandler inputOutputHandler;

    private ConfigurationParameter configurationParameter;
    //Object to execute query on Ontology
    private ExecuteQueryOntologyHandler executeQueryOntologyHandler;


    private static final Logger log = Logger.getLogger(Reasoner.class);

    public static final String SUPPORT_ONTOLOGY_GRAPH = "http://supportOntology";

    public static final  String FILTER_TAG = "###filter###";

    int count =0;
    public Reasoner(InferenceRules  inferenceRules, BaseRDFTripleModel baseRDFTripleModel,OutputHandler inputOutputHandler,ConfigurationParameter configurationParameter) {
        super();
        this.inferenceRules = inferenceRules;
        this.baseRDFTripleModel = baseRDFTripleModel;
        this.inputOutputHandler = inputOutputHandler;
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

        inputOutputHandler.setNumberOfIteration(0);

        //Object to store the output of execution of inferenceRules
        List<InferenceRuleOutput> inferenceRulesOutuput = new ArrayList<InferenceRuleOutput>();

        boolean produceOutput = new Boolean(configurationParameter.getProperty(ConfigurationParameter.PRODUCE_OUTPUT));
        //Get the number of execution of Reasoning.
        int numberOfExecution = Integer.parseInt(configurationParameter.getProperty(ConfigurationParameter.NUMBER_OF_EXECUTION));

        //Create a List that stores new generated Triple
        List<ARTStatement> results = new ArrayList<ARTStatement>();

        //If the user has specified "0" as number of reasoning execution, execute the reasoning operation until the reasoner will not find any new
        //inferred triples
        if(numberOfExecution == 0 ){
            numberOfExecution = 100000000;
        }
        int count ;
        //Apply all inference rules on ontology as many times as expressed by the configuration parameters
        for(count = 0;count <numberOfExecution;count++){
            //Get number of rules
            int outputRules =  inferenceRules.getInferenceRules().size();

            for(InferenceRule inferenceRule: inferenceRules.getInferenceRules() ){
                //Get the result of execution of query.
                List<InferenceRuleQueryResult> inferenceRuleQueryResults = executeQueryOntologyHandler.executeInferencerule(inferenceRule);

                //Get the size of query results
                int new_information = inferenceRuleQueryResults.size();


                //For each output that has been generated, check if the new information is already  on the ontology, if false create new output with
                //new information and add the information (triples) on graph
                for(InferenceRuleQueryResult inferenceRuleQueryResult:inferenceRuleQueryResults ){

                    //Check if a type rule is an inconsistency rule
                    if(inferenceRule.getType().equals(InferenceRule.TPYE_INCONSISTENCY)){

                        InferenceRuleOutput inferenceRuleOutput = produceOutput(produceOutput, inferenceRuleQueryResult, inferenceRule, inputOutputHandler.getAlternativeGraph(), inputOutputHandler.getAlternativeGraph());

                        if(checkInconsistency(inferenceRuleOutput))
                            //Add the output on the list of output
                            inferenceRulesOutuput.add(inferenceRuleOutput);
                        new_information--;
                    }


                    else if( !ifStatementIsOnOntology(inferenceRuleQueryResult, baseRDFTripleModel)){
                        //Add all new triples on output result.
                        results.addAll(inferenceRuleQueryResult.getTupleToConclusion());
                        //Create new object to store the output of reasoning operation
                        InferenceRuleOutput inferenceRuleOutput = produceOutput(produceOutput, inferenceRuleQueryResult, inferenceRule, inputOutputHandler.getGraph(), inputOutputHandler.getAlternativeGraph());
                        //Add the output on the list of output
                        inferenceRulesOutuput.add(inferenceRuleOutput);
                    }
                    //If new information has not been discovered, remove one unit from new_information
                    else{
                        new_information--;
                    }
                }
                //If no new information have not been discovered, remove one unit from outputofRule
                if (new_information <=0){
                    outputRules--;
                }

            }
            //Check if reasoner have to continue to iterate. If none information has been discovered, break the iteration
            if(outputRules <= 0){
                //Set the number of iteration
                inputOutputHandler.setNumberOfIteration(count+1);
                break;
            }
        }
        inputOutputHandler.setNumberOfIteration(count+1);
        //Save the results of reasoning operation
        inputOutputHandler.saveQuery(inferenceRulesOutuput);
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
            if(!inputOutputHandler.getGraphOfInconsistency().containsVertex(vertexSource)){
                inputOutputHandler.getGraphOfInconsistency().addVertex(vertexSource);
                finded = true;
            }

        }

        return finded;
    }


    /**
     *
     * @param produceOutput boolean that express if the output have to be produced
     * @param inferenceRuleQueryResult result of application of specific inference rule
     * @param inferenceRule inference rule applied
     * @param stringGraph graph of new information
     * @param alternativeStringGraph graph with filter information
     * @return InferenceRuleOutput an object that stores the output of application of given inference rule
     */
    public InferenceRuleOutput produceOutput(boolean produceOutput, InferenceRuleQueryResult inferenceRuleQueryResult,InferenceRule inferenceRule,
                                             Graph<String, String> stringGraph,Graph<String, String> alternativeStringGraph){

        if(produceOutput){
            InferenceRuleOutput inferenceRuleOutput = new InferenceRuleOutput(inferenceRuleQueryResult.getTupleToPremise(),
                    inferenceRuleQueryResult.getTupleToConclusion(),inferenceRule);
            inferenceRuleOutput.setFilterStatement(inferenceRuleQueryResult.getFilterStatement());

            addOnGraph(stringGraph, inferenceRuleOutput);
            addOnGraphWithFilter(alternativeStringGraph, inferenceRuleOutput);
            return inferenceRuleOutput;
        }
        else {
            return new InferenceRuleOutput();
        }

    }


    /**
     * Add a output of execution of inference rule on a graph. Store the new triples and the triples that have generated the new triples.
     * @param stringGraph graph of new information
     * @param inferenceRuleOutput an object that stores the output of application of inference rule
     */
    private void addOnGraph(Graph<String, String> stringGraph,InferenceRuleOutput inferenceRuleOutput) {

        for(ARTStatement statementSource : inferenceRuleOutput.getNew_triple()){

            String vertexSource = OntologyUtilis.convertARTStatementToString(statementSource);

            stringGraph.addVertex(vertexSource);

            for(ARTStatement statementTarget :inferenceRuleOutput.getFromTriple()){

                String vertexTarget = OntologyUtilis.convertARTStatementToString(statementTarget);
                stringGraph.addVertex(vertexTarget);

                stringGraph.addEdge(String.valueOf(count),vertexSource,vertexTarget, EdgeType.DIRECTED);
                count++;
            }

        }

    }

    /**
     * Add a output of execution of inference rule on a graph. Store the new triples and the triples that have generated the new triples.
     * (with filters information)
     *
     * @param alternativeStringGraph graph with filter information
     * @param inferenceRuleOutput an object that stores the output of application of inference rule
     */
    private void addOnGraphWithFilter(Graph<String, String> alternativeStringGraph,InferenceRuleOutput inferenceRuleOutput) {

        for(ARTStatement statementSource : inferenceRuleOutput.getNew_triple()){

            String vertexSource = OntologyUtilis.convertARTStatementToString(statementSource);

            if(!inferenceRuleOutput.getFilterStatement().equals("")){
               vertexSource=  vertexSource+FILTER_TAG;
            }
            alternativeStringGraph.addVertex(vertexSource);
            for(ARTStatement statementTarget :inferenceRuleOutput.getFromTriple()){

                String vertexTarget = OntologyUtilis.convertARTStatementToString(statementTarget);
                alternativeStringGraph.addVertex(vertexTarget);

                alternativeStringGraph.addEdge(String.valueOf(count),vertexSource,vertexTarget, EdgeType.DIRECTED);
                count++;
            }

        }

    }




    /**
     * Check if statement is contained in the ontology. If true, add the statement in the ontology and return true,false otherwise.
     *
     * @param model ontology
     * @return true if ontology has statement, false,otherwise
     * @throws ModelAccessException
     * @throws ModelUpdateException
     */
    private boolean ifStatementIsOnOntology(InferenceRuleQueryResult inferenceRuleQueryResult, BaseRDFTripleModel model) throws ModelAccessException, ModelUpdateException {

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




    public OutputHandler getInputOutputHandler() {
        return inputOutputHandler;
    }




    public void setInputOutputHandler(OutputHandler inputOutputHandler) {
        this.inputOutputHandler = inputOutputHandler;
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
