package it.uniroma2.reasoner.utils;

import it.uniroma2.art.owlart.model.ARTStatement;
import it.uniroma2.reasoner.ReasonerHandler.Reasoner;
import it.uniroma2.reasoner.domain.InferenceRuleOutput;
import it.uniroma2.reasoner.domain.ReasoningOutput;
import it.uniroma2.reasoner.services.GraphServices;
import it.uniroma2.reasoner.utils.Graph.GraphUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import edu.uci.ics.jung.graph.Graph;

public class ServicesUtils {
	
	private static final String CUSTOM_NODE_TO_SHOW_GRAPH = "Show main graph";


	@SuppressWarnings("unchecked")
	public static String convertInferenceRuleOutputToJson(ReasoningOutput reasoningOutput){
		
		JSONArray inferenceRules = new JSONArray();
		
		for(InferenceRuleOutput inferenceRuleOutput: reasoningOutput.getInferenceRuleOutput()){
			JSONArray output = new JSONArray();
			
			JSONObject rules = new JSONObject();
			rules.put("ruleType", inferenceRuleOutput.getInferenceRule().getType());
			rules.put("ruleId", inferenceRuleOutput.getInferenceRule().getInferenceRuleID());
			rules.put("ruleName", inferenceRuleOutput.getInferenceRule().getInferenceRuleName());
			
			output.add(rules);
			
			JSONArray newTriple = new JSONArray();
			for(ARTStatement stm:inferenceRuleOutput.getNew_triple()){
				
				JSONObject triple = new JSONObject();
				triple.put("triple", stm.toString());
				triple.put("simpleTriple",OntologyUtilis.convertARTStatementToSimpleString(stm));
				newTriple.add(triple);
			}
			
			output.add(newTriple);
			
			JSONArray fromTriple = new JSONArray();
			for(ARTStatement stm:inferenceRuleOutput.getFromTriple()){
				
				JSONObject triple = new JSONObject();
				triple.put("triple", stm.toString());
				triple.put("simpleTriple",OntologyUtilis.convertARTStatementToSimpleString(stm));
				fromTriple.add(triple);
			}
			output.add(fromTriple);
			inferenceRules.add(output);
			
		}
		
		return inferenceRules.toJSONString();
	}
	
	
	/**
	 * Search,from output reasoning graph,the history generation of given triple. The history has a form of graph.
	 * @param triple convert the result graph to a json representation. 
	 * @return json string representation
	 */
	public static String searchTripleOnGraph(String triple,Graph<String,String> graph){

		if(triple.equals(CUSTOM_NODE_TO_SHOW_GRAPH)){
			JSONArray result =convertGraphToJson(graph);
			return result.toJSONString();
		}

		Graph<String, String> graphBFS = GraphUtils.BreabreadthFirstSearch(graph, triple);
		JSONArray result =convertGraphToJson(graphBFS);
		return result.toJSONString();
	}
	
	
	@SuppressWarnings("unchecked")
	public static JSONArray convertGraphToJson(Graph<String,String> graph){
		
		JSONArray nodes = new JSONArray();
		
		for(String vertex: graph.getVertices()){
			JSONObject nodo = new JSONObject();
			JSONArray list = new JSONArray();


			for (String exit : graph.getSuccessors(vertex)){
				JSONObject edge = new JSONObject();
				edge.put("nodeTo", exit);
				edge.put("nodeFrom", vertex);

				JSONObject opzioni = new JSONObject();
				opzioni.put("$type", "arrow");
				opzioni.put("$dim", 14);
				JSONArray direction = new JSONArray();
					direction.add(vertex);
				    direction.add(exit);
				opzioni.put("$direction", direction);
				edge.put("data", opzioni);

				list.add(edge);

			}
			
			JSONObject data = new JSONObject();
			if(graph.getSuccessors(vertex).size() > 0){
			data.put("$type", "circle");
                if(vertex.contains("###")){
                    data.put("$color","#FFF703");
                    vertex = vertex.replace(Reasoner.FILTER_TAG,"");
                }
                else{
                    data.put("$color","#3207BB");
                }

			data.put("$dim",4);
			nodo.put("data", data);
			}
			else{
				data.put("$dim",4);
				nodo.put("data", data);
			}
			
			nodo.put("adjacencies", list);
			
			nodo.put("id", vertex);
			nodo.put("name", vertex);

			nodes.add(nodo);

		}
		
		return nodes;
	}


	public static JSONArray getListofVertexJsonGraph(
			Graph<String, String> graph) {
		JSONArray nodes = new JSONArray();
		List<String> namesOfVertex = new ArrayList<String>();
		
		
		for(String vertex: graph.getVertices()){
			if(graph.getSuccessors(vertex).size() > 0){
				namesOfVertex.add(vertex);
			}
			

		}
		Collections.sort(namesOfVertex,String.CASE_INSENSITIVE_ORDER);
		for(String vertex: namesOfVertex){
			JSONObject nodo = new JSONObject();
			nodo.put("id",vertex);
			nodes.add(nodo);
			

		}
		JSONObject nodo = new JSONObject();
		nodo.put("id",CUSTOM_NODE_TO_SHOW_GRAPH);
		nodes.add(nodo);
		
		return nodes;
	}
	
	
	public static File[] finder(String dirName,final String extension){
    	File dir = new File(dirName);

    	return dir.listFiles(new FilenameFilter() { 
    	         public boolean accept(File dir, String filename)
    	              { return filename.endsWith(extension); }
    	} );

    }
	
	public static void createFileGraph(Graph<String,String> jsonGraph){
		try {
			
			FileWriter file = new FileWriter(GraphServices.JSON_GRAPH_EXTENSION);
			file.write(convertGraphToJson(jsonGraph).toJSONString());
			file.flush();
			file.close();
	 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
