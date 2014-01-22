package it.uniroma2.reasoner.OutputHandler;

import it.uniroma2.reasoner.domain.InferenceRuleOutput;
import it.uniroma2.reasoner.domain.ReasoningOutput;
import it.uniroma2.reasoner.utils.Graph.GraphView;

import java.util.List;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;









/**
 * Manage the output of reasoning operation process.
 * 
 * 
 * @author Giovanni Lorenzo Napoleoni
 *
 */
public class OutputHanlder {
	//Graph of reasoning operation
	private Graph<String, String> graph;
	//Graph of inconsistency triple
	private Graph<String, String> graphOfInconsistency;
	//Object to store output of reasoning process
	private ReasoningOutput reasoningOutput;
	
	private int numberOfIteration;
	
	private static final Logger log = Logger.getLogger(OutputHanlder.class);
	
		
	public String printGraph(){
		
        log.debug("##### Print Graph ###########");
        log.debug(getGraph().toString());
        log.debug("##### Print Graph ###########");
        return getGraph().toString();
	}
	
	public void showGraphOfNewTripleOnWindows(){

		
		 GraphView.showGraph(getGraph(),"Triple graph View");

	}

	
	public void showGraphOfSelectedTripleOnWindow(DirectedGraph<String, String> tripleGraph){
		
		GraphView applet = new GraphView();
		//applet.init(tripleGraph);
		 JFrame frame = new JFrame();
	        frame.getContentPane().add(applet);
	        frame.setTitle("Selected Triple");
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.pack();
	        frame.setVisible(true);
	}
	
	public String applyFiltersToList(String filters){
		StringBuilder string = new StringBuilder();
		String[] rulesName = filters.split(",");
		List<InferenceRuleOutput> inferenceRuleOutputs = reasoningOutput.getInferenceRuleOutput();
		
		for (int i = 0; i < rulesName.length;i++){
			for(InferenceRuleOutput inferenceRuleOutput: inferenceRuleOutputs){
				if(inferenceRuleOutput.getInferenceRule().getInferenceRuleName().equals(rulesName[i])){
					string.append(reasoningOutput.printSingleRule(inferenceRuleOutput));
				}
			}
		}
		return string.toString();
	}
	
	/**
	 * Save the inference rule output query result
	 * @param inferenceRulesOutuput inference rules output to save
	 */
	public void saveQuery(List<InferenceRuleOutput> inferenceRulesOutuput){
		
		getReasoningOutput().getInferenceRuleOutput().addAll(inferenceRulesOutuput);
	}
	

	public ReasoningOutput getReasoningOutput() {
		if(reasoningOutput == null){
			reasoningOutput = new ReasoningOutput();
		}
		return reasoningOutput;
	}

	public void setReasoningOutput(ReasoningOutput reasoningOutput) {
		this.reasoningOutput = reasoningOutput;
	}

	public Graph<String,String> getGraph() {
		if (graph == null){
			graph = new DirectedSparseMultigraph<String, String>();
		}
		return graph;
	}

	public void setGraph(Graph<String, String> stringGraph) {
		this.graph = stringGraph;
	}

	public Graph<String, String> getGraphOfInconsistency() {
		if (graphOfInconsistency == null){
			graphOfInconsistency = new DirectedSparseMultigraph<String, String>();
		}
		return graphOfInconsistency;
	}

	public void setGraphOfInconsistency(Graph<String, String> graphOfInconsistency) {
		this.graphOfInconsistency = graphOfInconsistency;
	}

	public int getNumberOfIteration() {
		return numberOfIteration;
	}

	public void setNumberOfIteration(int numberOfIteration) {
		this.numberOfIteration = numberOfIteration;
	}

  
}
