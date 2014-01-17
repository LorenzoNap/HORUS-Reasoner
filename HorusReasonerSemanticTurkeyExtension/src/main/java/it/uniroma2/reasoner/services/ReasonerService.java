package it.uniroma2.reasoner.services;

import it.uniroma2.art.semanticturkey.exceptions.HTTPParameterUnspecifiedException;
import it.uniroma2.art.semanticturkey.plugin.extpts.ServiceAdapter;
import it.uniroma2.art.semanticturkey.servlet.Response;
import it.uniroma2.art.semanticturkey.servlet.ServiceVocabulary.RepliesStatus;
import it.uniroma2.art.semanticturkey.servlet.XMLResponseREPLY;
import it.uniroma2.art.semanticturkey.utilities.XMLHelp;
import it.uniroma2.reasoner.ConfigurationHandler.ConfigurationParameter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.w3c.dom.Element;

/**
 * exposed services from  reasoner extension of semantic turkey. The services used to configure the reasoning operation
 * parameters.
 * 
 * @author Giovanni Lorenzo Napoleoni
 *
 */
public class ReasonerService extends ServiceAdapter{


	private static Logger logger = LoggerFactory.getLogger(ReasonerService.class);
	// Requests definition
	public static final String loadFileRequest = "loadFileRequest";

	public static final String showInferenceRules = "showInferenceRules";

	public static final String loadReasonerConfiguration = "loadReasonerConfiguration";

	public static final String startReasoning = "startReasoning";

	// Parameters of the requests.
	static final public String file = "file";

	static final public String howManyTimesApllyInferenceRule = "howManyTimesApllyInferenceRule";

	static final public String whicRulesApply = "whicRulesApply";

	static final public String produceOutput = "produceOutput";

	static final public String defaultRules ="defaultRules";

	//Store the inferenceRuleFile
	private File inferenceRuleFile;



	@Autowired
	public ReasonerService(@Value("reasonerService") String id) {

		super(id);
		loadDefaultInferenceRuleFile();
		logger.debug("ENTRATO");
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
		
		
		if (request.equals(loadFileRequest)) {
			String value = setHttpPar(file);
			checkRequestParametersAllNotNull(file);
			String defaultValue = setHttpPar(defaultRules);
			checkRequestParametersAllNotNull(defaultRules);
			response = saveFile(value,defaultValue);
			almostOneRequest = true;
		}

		if (request.equals(showInferenceRules)) {
			response = getInferenceRuleFiles();
			almostOneRequest = true;
		}


		if (request.equals(loadReasonerConfiguration)) {		
			response = loadInitialConfiguration();
			almostOneRequest = true;
		}

		if (!almostOneRequest){
			return servletUtilities.createNoSuchHandlerExceptionResponse(request);
		}

		this.fireServletEvent();
		return response;
	}

	/**
	 * Load the preloaded inference rules file from reasoner configuration.
	 */
	private void loadDefaultInferenceRuleFile() {
		//Create new file
		inferenceRuleFile = new File("inference_rule.txt");

		StringBuffer textInferenceRule= new StringBuffer();
		BufferedReader br = null;
		//Get the stream from default rule file
		InputStream stream = getClass().getClassLoader().getResourceAsStream("default_rule_file.txt");

		String sCurrentLine;

		br = new BufferedReader(new InputStreamReader(stream));

		OutputStream outputStream = null;
		//Read and save the inference rules file tex
		try {
			outputStream = new FileOutputStream(inferenceRuleFile);
			outputStream.write((new String()).getBytes());
			while ((sCurrentLine = br.readLine()) != null) {
				textInferenceRule.append(sCurrentLine);
				outputStream.write((sCurrentLine.toString()+"\n").getBytes());

			}

			outputStream.close();
			br.close();
			stream.close();
		} catch (FileNotFoundException e) {
			logger.error(e.toString());
		} catch (IOException e) {
			logger.error(e.toString());
		}
	}

	
	/**
	 * Load the default configuration of reasoning operation
	 * @return
	 */
	private Response loadInitialConfiguration() {
		XMLResponseREPLY response;

		try {
			//Get parameters
			String whichRuleApply = ConfigurationParameter.getIstance().getProperty(ConfigurationParameter.WHICHRULEEXECUTE);
			String numberOfExecution = ConfigurationParameter.getIstance().getProperty(ConfigurationParameter.NUMBER_OF_EXECUTION);
			String produceOutput = ConfigurationParameter.getIstance().getProperty(ConfigurationParameter.PRODUCE_OUTPUT);
			//Create response
			response = createReplyResponse(RepliesStatus.ok);
			Element dataElement = response.getDataElement();
			Element configurationElem = XMLHelp.newElement(dataElement, "ConfigurationParameter");
			//Populate response
			configurationElem.setAttribute("whichRuleApply", whichRuleApply);
			configurationElem.setAttribute("numberOfExecution", numberOfExecution);
			configurationElem.setAttribute("produceOutput", produceOutput);

			return response;

		} catch (FileNotFoundException e) {

			response = createReplyResponse(RepliesStatus.fail);
			Element dataElement = response.getDataElement();
			Element errorElem = XMLHelp.newElement(dataElement, "ConfigurationParameter");
			errorElem.setAttribute("error", e.getMessage());
			return response;

		} catch (IOException e) {
			response = createReplyResponse(RepliesStatus.fail);
			Element dataElement = response.getDataElement();
			Element errorElem = XMLHelp.newElement(dataElement, "ConfigurationParameter");
			errorElem.setAttribute("error", e.getMessage());
			return response;
		}


	}

	/**
	 * Get the inference rules from inference rules file.
	 * @return
	 */
	private Response getInferenceRuleFiles() {
		XMLResponseREPLY response;

		//Save the text readed from file
		StringBuffer textInferenceRule= new StringBuffer();
		BufferedReader br = null;

		try {
			String sCurrentLine;
			br = new BufferedReader(new InputStreamReader(new FileInputStream(inferenceRuleFile)));
			//For each line read append the line and and a new line character
			while ((sCurrentLine = br.readLine()) != null) {
				textInferenceRule.append(sCurrentLine);
				textInferenceRule.append("&#x10;");
			}


		} catch (IOException e) {
			response = createReplyResponse(RepliesStatus.fail);
			Element dataElement = response.getDataElement();
			Element errorEleme = XMLHelp.newElement(dataElement, "InferenceRuleFile");
			errorEleme.setAttribute("error", e.toString());
			return response;
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				response = createReplyResponse(RepliesStatus.fail);
				Element dataElement = response.getDataElement();
				Element errorElem = XMLHelp.newElement(dataElement, "InferenceRuleFile");
				errorElem.setAttribute("error", ex.toString());
				return response;
			}
		}

		//create new response 
		response = createReplyResponse(RepliesStatus.ok);
		Element dataElement = response.getDataElement();
		Element inferenceRuleElem = XMLHelp.newElement(dataElement, "InferenceRuleFile");
		//Add read file to response
		inferenceRuleElem.setAttribute("textInferenceRuleFile", textInferenceRule.toString());
	
		//return response
		return response;
	}

	/**
	 * Save file uploaded from user or restore the default inference rules file
	 * 
	 * @param inferenceRulesText
	 * @param defaultRule if true restore the default inference rule file
	 * @return response
	 */
	private Response saveFile(String inferenceRulesText,String defaultRule) {
		
		XMLResponseREPLY response;
		//If default rule is true, load the deafult inference rules file and save it to current inference rules file.
		if(defaultRule.equals("true")){

			StringBuffer textInferenceRule= new StringBuffer();
			BufferedReader br = null;
			//Load default inference rules file
			InputStream stream = getClass().getClassLoader().getResourceAsStream("startRules.txt");

			String sCurrentLine;
			br = new BufferedReader(new InputStreamReader(stream));
			OutputStream outputStream = null;

			try {
				outputStream = new FileOutputStream(inferenceRuleFile);
				//Flush the currente inference rules file.
				outputStream.write((new String()).getBytes());
				//Write into current inference rules file the default inference rules file
				while ((sCurrentLine = br.readLine()) != null) {
					textInferenceRule.append(sCurrentLine);
					outputStream.write((sCurrentLine.toString()+"\n").getBytes());

				}
				//Close the streams
				outputStream.close();
				br.close();
				stream.close();
			} catch (FileNotFoundException e2) {
				response = createReplyResponse(RepliesStatus.fail);
				Element dataElement = response.getDataElement();
				Element errorElem = XMLHelp.newElement(dataElement, "SaveFile");
				errorElem.setAttribute("error", e2.toString());
				return response;
			} catch (IOException e) {
				response = createReplyResponse(RepliesStatus.fail);
				Element dataElement = response.getDataElement();
				Element errorElem = XMLHelp.newElement(dataElement, "SaveFile");
				errorElem.setAttribute("error", e.toString());
				return response;
			}

		}
		//If default rule is false, load into current inference rules file the file uploaded by the user
		else if(defaultRule.equals("false")){
			OutputStream outputStream = null;
			try {
				outputStream = new FileOutputStream(inferenceRuleFile);
				//Flush the current file
				outputStream.write((new String()).getBytes());
				//Write into current file the user file
				outputStream.write((inferenceRulesText).getBytes());
				outputStream.close();
			} catch (FileNotFoundException e) {
				response = createReplyResponse(RepliesStatus.fail);
				Element dataElement = response.getDataElement();
				Element errorElem = XMLHelp.newElement(dataElement, "SaveFile");
				errorElem.setAttribute("error", e.toString());
				return response;
			} catch (IOException e) {
				response = createReplyResponse(RepliesStatus.fail);
				Element dataElement = response.getDataElement();
				Element errorElem = XMLHelp.newElement(dataElement, "SaveFile");
				errorElem.setAttribute("error", e.toString());
				return response;
			}

		}
		
		if(defaultRule.startsWith("append")){
			
			OutputStream outputStream = null;
			try {
				
				//Append into current file the user file
				if(defaultRule.equals("appendFirstTime")){
					outputStream = new FileOutputStream(inferenceRuleFile,false);
					outputStream.write((new String()).getBytes());
					outputStream.close();
				}
				outputStream = new FileOutputStream(inferenceRuleFile,true);
				outputStream.write((inferenceRulesText).getBytes());
				outputStream.close();
			} catch (FileNotFoundException e) {
				response = createReplyResponse(RepliesStatus.fail);
				Element dataElement = response.getDataElement();
				Element errorElem = XMLHelp.newElement(dataElement, "SaveFile");
				errorElem.setAttribute("error", e.toString());
				return response;
			} catch (IOException e) {
				response = createReplyResponse(RepliesStatus.fail);
				Element dataElement = response.getDataElement();
				Element errorElem = XMLHelp.newElement(dataElement, "SaveFile");
				errorElem.setAttribute("error", e.toString());
				return response;
			}
		}

		response = createReplyResponse(RepliesStatus.ok);
		return response;

	}

	public File getInferenceRuleFile() {
		return inferenceRuleFile;
	}

	public void setInferenceRuleFile(File inferenceRuleFile) {
		this.inferenceRuleFile = inferenceRuleFile;
	}


	@Override
	protected Logger getLogger() {
		return null;
	}

}
