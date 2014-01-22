/*
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is SemanticTurkey.
 * 
 * The Initial Developer of the Original Code is University of Roma Tor Vergata.
 * Portions created by University of Roma Tor Vergata are Copyright (C) 2007.
 * All Rights Reserved.
 * 
 * SemanticTurkey was developed by the Artificial Intelligence Research Group
 * (art.uniroma2.it) at the University of Roma Tor Vergata (ART) Current
 * information about SemanticTurkey can be obtained at
 * http://semanticturkey.uniroma2.it
 * 
 */

if ("undefined" == typeof(Reasoner)) {
  var Reasoner = {};
};


 Components.utils.import("resource://reasonerExtensionServices/SERVICE_ReasonerConfiguration.jsm",Reasoner);
 Components.utils.import("resource://reasonerExtensionServices/SERVICE_Reasoning.jsm",Reasoner);

/**
 * Open window to display the configuration of reasoning operation
 */
 function showReasonerConfiguration() {
	 
	 //Call the service from Semantic Turkey service to retrieve the configuration parameters of the Reasoner
	 var response = Reasoner.Requests.ReasonerConfigurations.loadConfiguration();

	 var howManyTimesApllyInferenceRule;
	 var whicruleApply;
	 var produceOutput;

	 //Check the response from server.
	 //If the request failed show a popup with errore message.
	 if (response.getElementsByTagName("reply")[0].getAttribute("status") == "fail") {
		 window.alert("unable to load file, Error"+
				 response.getElementsByTagName("ConfigurationParameter")[0].getAttribute("error"));
	 }
	 //If request is ok, get the configuration parameter from response.
	 if (response.getElementsByTagName("reply")[0].getAttribute("status") == "ok") {
		 howManyTimesApllyInferenceRule = response.getElementsByTagName("ConfigurationParameter")[0].getAttribute("numberOfExecution");
		 whicruleApply = response.getElementsByTagName("ConfigurationParameter")[0].getAttribute("whichRuleApply");
		 produceOutput = response.getElementsByTagName("ConfigurationParameter")[0].getAttribute("produceOutput");
	 }
	 //Create an Array of paramters with configuration parameter of reasoner.
	 var params = {inn:{outputValue:produceOutput, cycleNumber:howManyTimesApllyInferenceRule, whicruleApply:whicruleApply}, out:null};       
	//Open the Reasoner configuration Dialog
	 window.openDialog("chrome://reasonerExtension/content/resonerConfiguration.xul",
			 "Reasoner Configuration", "chrome,centerscreen,dialog,modal",params).focus();         
	 //If user haven't chosen any parameter,stop operation, else call the reasoning service.
	callReasoningService(params);

 }
 function dismissWindow(){
	window.arguments[0].out = {save:'cancel',message:''}; 
	return true;
}
 function dismissNewTriple() {
	//create array param
	var params = {inn:{save:'false'}, out:null};      
	//Open save new triple discovered window
	window.openDialog("chrome://reasonerExtension/content/saveNewTriple.xul",
			"Reasoner Configuration", "chrome,centerscreen,dialog",params);
	return params;
}
 
function removeNewTriple(){
	//send request to remove new triple to semantic turkey server
	var response = Reasoner.Requests.ReasoningService.removeTriple();
	//If remove operation fail, get the error and upadate the params window, else return true
	if (response.getElementsByTagName("reply")[0].getAttribute("status") == "fail") {
		var status = response.getElementsByTagName("removeTriple")[0].getAttribute("error");
		window.arguments[0].out = {save:'cancel',message:''}; 
		alert(status);
	}
	alert("triples removed");
	window.arguments[0].out = {save:'true',message:''}; 
	return true;
}
 
 
 /**
  * Call the reasoning service and show the output of operation on window.
  * @param params the params of reasoning operation
  */
 function callReasoningService(params){
	 	 if (params.out) {
		 //Check the parameters entered by the user 
		 var goAhead = true;
		 //If cycle number parameter is a string show an error popup
		 if (isNaN (params.out.cycleNumber)){
			 let prompts =Cc["@mozilla.org/embedcomp/prompt-service;1"].getService(Ci.nsIPromptService);
			 prompts.alert(window, "Configuration Error", "You have inserted a wrong parameter on Cycle number box");
			 goAhead = false;
		 }
		 else {
			 //Check the id of inference rule box.
			 //Split the ids entered by the user. For each splitted id, check if it is a valid input, else show
			 //a popup error
			 var split = params.out.whicruleApply.split(",");
			 for (var i = 0; i < split.length; i++) {

				 if (isNaN (split[i])){
					 let prompts =Cc["@mozilla.org/embedcomp/prompt-service;1"].getService(Ci.nsIPromptService);
					 prompts.alert(window, "Configuration Error", "You have inserted a wrong id on inference rule id box");
					 goAhead = false;
					 break;
				 }
			 } 

		 }
		 //Check if the all controls on parameters  are going ok.
		 if (goAhead) {
			
			 //Call the reasoning service from SemantickTurkey server.
			 var response = Reasoner.Requests.ReasoningService.startReasoning(params);
			 //If the reply is a warning show a popup with warn message.
			 if (response.getElementsByTagName("reply")[0].getAttribute("status") == "warning") {
				 let prompts =Cc["@mozilla.org/embedcomp/prompt-service;1"].getService(Ci.nsIPromptService);
				 prompts.alert(window, "Reasoner",response.getElementsByTagName("startReasoning")[0].getAttribute("warning"));
			 }
			//If the reply is failed show a popup with error message.
			 if (response.getElementsByTagName("reply")[0].getAttribute("status") == "fail") {
				 let prompts =Cc["@mozilla.org/embedcomp/prompt-service;1"].getService(Ci.nsIPromptService);
				 prompts.alert(window, "Reasoner", "Unable to start reasoning.Error: "+
						 response.getElementsByTagName("startReasoning")[0].getAttribute("error"));
			 }
			 //If the error is ok, show the output of reasoning operation
			 if (response.getElementsByTagName("reply")[0].getAttribute("status") == "ok") {
				 
				 //Get the information from response
				 
				 //Number of new triples discovered
				 var triple_discovered = response.getElementsByTagName("startReasoning")[0].getAttribute("numberOfTriple");
				 //Boolean to represent if output has been produced
				 var output = response.getElementsByTagName("startReasoning")[0].getAttribute("produceOutput");
				 //The text output of reasoning
				 var print = response.getElementsByTagName("startReasoning")[0].getAttribute("printOutput");
				 //Get names of inferenceRules
				 var names =  response.getElementsByTagName("startReasoning")[0].getAttribute("inferenceRulesNames");
				 
				 var JsonInference = response.getElementsByTagName("startReasoning")[0].getAttribute("jsonInference");
				 
				 var numberOfIterationOfReasoning = response.getElementsByTagName("startReasoning")[0].getAttribute("numberOfIteration");
				 //Create an array of parameters with the information taken from the server response
				 var parameters = {inn:{newTriple:triple_discovered, produceOutput:output,printOutput:print,nameRules:names,jsonInference:JsonInference,iterations:numberOfIterationOfReasoning }, out:null};
				 //If the output has not produced, show the dialog box to save the new triples discovered, else show the window output
				 
				 document.getElementById("deleteTriple").setAttribute("disabled","false");
				 
				 if (output == 'false') {
					 let prompts =Cc["@mozilla.org/embedcomp/prompt-service;1"].getService(Ci.nsIPromptService);
				 prompts.alert(window, "Reasoner", "Number of discovered triples: "+
						 (triple_discovered));
				 }
				 else{
					 
					if(params.out.numberOfIterationOfReasoning <  params.out.cycleNumber ||  params.out.cycleNumber == 0){
						 let prompts =Cc["@mozilla.org/embedcomp/prompt-service;1"].getService(Ci.nsIPromptService);
						 prompts.alert(window,"Reasoner","Reasoner iteration (value chosen by the user): "+params.out.cycleNumber+"\n"+"Effective iteration: "+numberOfIterationOfReasoning);
					}
					 window.openDialog("chrome://reasonerExtension/content/showOutput.xul",
							 "Output window", "chrome,resizable=yes,modal",parameters);
				 }
			 }

		 }

	 }
 }
        




