package it.uniroma2.reasoner.utils;

import it.uniroma2.art.owlart.model.ARTStatement;
import it.uniroma2.reasoner.domain.InferenceRuleOutput;
import it.uniroma2.reasoner.domain.ReasoningOutput;
import it.uniroma2.reasoner.utils.Graph.GraphUtils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import edu.uci.ics.jung.graph.Graph;

public class ServicesUtils {
	
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
				newTriple.add(triple);
			}
			
			output.add(newTriple);
			
			JSONArray fromTriple = new JSONArray();
			for(ARTStatement stm:inferenceRuleOutput.getFromTriple()){
				
				JSONObject triple = new JSONObject();
				triple.put("triple", stm.toString());
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
                    vertex = vertex.replace("###","");
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
	
}
