package it.uniroma2.reasoner.services;

import it.uniroma2.art.owlart.models.RDFModel;
import it.uniroma2.art.semanticturkey.exceptions.HTTPParameterUnspecifiedException;
import it.uniroma2.art.semanticturkey.plugin.extpts.ServiceAdapter;
import it.uniroma2.art.semanticturkey.project.Project;
import it.uniroma2.art.semanticturkey.project.ProjectManager;
import it.uniroma2.art.semanticturkey.servlet.Response;
import it.uniroma2.art.semanticturkey.servlet.XMLResponseREPLY;
import it.uniroma2.art.semanticturkey.servlet.ServiceVocabulary.RepliesStatus;
import it.uniroma2.art.semanticturkey.utilities.XMLHelp;
import it.uniroma2.reasoner.utils.ServicesUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;












import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.w3c.dom.Element;

public class GraphServices extends ServiceAdapter {

	private static final String LOAD_GRAPH = "loadGraphRequest";
	
	public static final String FILE_NAME_PARAMETERS = "fileName";
	
	
	private static final String LOAD_AVAIABLE_GRAPHS = "loadListOfAviableGraph";
	public static final String JSON_GRAPH_FILE_PATH_DIRECTORY = "ProducedGraphs";
	public static final String JSON_GRAPH_EXTENSION = ".json";

	@Autowired
	public GraphServices(@Value("reasonerGraphService") String id) {
		super(id);
	}

	@Override
	protected Logger getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Response getPreCheckedResponse(String request)
			throws HTTPParameterUnspecifiedException {
		// Create new response
		Response response = null;

		if (request == null) {
			return servletUtilities.createNoSuchHandlerExceptionResponse(null);
		}

		if (request.equals(LOAD_GRAPH)) {
			String fileName = setHttpPar(FILE_NAME_PARAMETERS);
			System.out.println("FILE NAME"+fileName);
			return loadGraph(fileName);

		}
		
		if (request.equals(LOAD_AVAIABLE_GRAPHS)){
			return loadListOfAviableGraph();
		}
		this.fireServletEvent();
		return response;
	}
	
	private Response loadListOfAviableGraph(){
		
		
		//Controllo se la directory che contiene i files dei Grafi esiste
		File file = new File(JSON_GRAPH_FILE_PATH_DIRECTORY);
		if (!file.exists()) {
			if (file.mkdir()) {
				System.out.println("Directory is created!");
			} else {
				XMLResponseREPLY response;
				response = createReplyResponse(RepliesStatus.fail);
				Element dataElement = response.getDataElement();
				Element graphElem = XMLHelp.newElement(dataElement, "aviableListOfGraphs");
				graphElem.setAttribute("files","unable to create directory");
				return response;
			}
		}
		
		
		//Cerco tutti i file disponibili
		File[] files = ServicesUtils.finder("./"+JSON_GRAPH_FILE_PATH_DIRECTORY+"/",".json");
		if(files.length > 0){
			JSONArray graphs = new JSONArray();
			for(int i = 0; i < files.length;i++){
				JSONObject graph = new JSONObject();
				try {
					graph.put("fileName",files[i].getName());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					XMLResponseREPLY response;
					response = createReplyResponse(RepliesStatus.fail);
					Element dataElement = response.getDataElement();
					Element graphElem = XMLHelp.newElement(dataElement, "aviableListOfGraphs");
					graphElem.setAttribute("files",e.toString());
					return response;
				}
				graphs.add(graph);
			}
			
			XMLResponseREPLY response;
			response = createReplyResponse(RepliesStatus.ok);
			Element dataElement = response.getDataElement();
			Element graphElem = XMLHelp.newElement(dataElement, "aviableListOfGraphs");
			graphElem.setAttribute("files",graphs.toJSONString());
			return response;
			
		}
		else{
			XMLResponseREPLY response;
			response = createReplyResponse(RepliesStatus.warning);
			Element dataElement = response.getDataElement();
			Element graphElem = XMLHelp.newElement(dataElement, "aviableListOfGraphs");
			graphElem.setAttribute("files","No Aviable Graphs");
			return response;
		}
		
		
	} 

	private Response loadGraph(String fileName) {
		//Controllo se la cartella che memorizza i grafi esiste.
		File file = new File(JSON_GRAPH_FILE_PATH_DIRECTORY);
		if (!file.exists()) {
			if (file.mkdir()) {
				System.out.println("Directory is created!");
			} else {
				XMLResponseREPLY response;
				response = createReplyResponse(RepliesStatus.fail);
				Element dataElement = response.getDataElement();
				Element graphElem = XMLHelp.newElement(dataElement, "aviableListOfGraphs");
				graphElem.setAttribute("files","unable to create directory");
				return response;
			}
		}
		System.out.println("GRAPH SEARCHED: "+fileName);
		if (fileName.equals("current.json")){
			//fileName = ProjectManager.getCurrentProject().getName()+JSON_GRAPH_EXTENSION; // old
			fileName = getProject().getName()+JSON_GRAPH_EXTENSION;
			System.out.println("GRAPH SEARCHED CURRENT: "+fileName);
		}
		//Search the specific file Graph
		File[] files = ServicesUtils.finder("./"+JSON_GRAPH_FILE_PATH_DIRECTORY+"/",fileName);
		if(files.length > 0){
			JSONArray graphs = new JSONArray();
			for(int i = 0; i < files.length;i++){
				JSONParser parser = new JSONParser();
				Object obj;
				JSONArray jsonObject = null;
				try {
					obj = parser.parse(new FileReader(files[i]));
					 jsonObject = (JSONArray) obj;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					XMLResponseREPLY response;
					response = createReplyResponse(RepliesStatus.fail);
					Element dataElement = response.getDataElement();
					Element graphElem = XMLHelp.newElement(dataElement, "jsonGraph");
					graphElem.setAttribute("jsonGraph",e.toString());
					return response;
				} 
				
				
				
				XMLResponseREPLY response;
				response = createReplyResponse(RepliesStatus.ok);
				Element dataElement = response.getDataElement();
				Element graphElem = XMLHelp.newElement(dataElement, "jsonGraph");
				graphElem.setAttribute("jsonGraph",jsonObject.toJSONString());
				return response;
			}
			
		}
		else{
			XMLResponseREPLY response;
			response = createReplyResponse(RepliesStatus.warning);
			Element dataElement = response.getDataElement();
			Element graphElem = XMLHelp.newElement(dataElement, "aviableListOfGraphs");
			graphElem.setAttribute("files","no graph");
			return response;
		}
		return null;
		

	}

}
