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

 function chkST_started(){
     if(document.getElementById("startSTToolBarButton").hidden == true && document.getElementById("ontPanelToolBarButton").hidden == false){
         document.getElementById('startReasonerToolbarButton').setAttribute("hidden","false");
         document.getElementById('menuReasoner').setAttribute("disabled","false");
    }
 }


Reasoner.associateEventsOnBrowserGraphicElements = function() {

document.getElementById("sd-toolbar").addEventListener("popupshowing",chkST_started, true);
document.getElementById("sd-toolbar").addEventListener("command",chkST_started, true);
// select the target node
var target = document.querySelector('#ontPanelToolBarButton');

// create an observer instance
var observer = new MutationObserver(function(mutations) {
  mutations.forEach(function(mutation) {
    //alert(mutation.type);
    chkST_started();
  });
});

 // configuration of the observer:
var config = { attributes: true, childList: true, characterData: true };

 // pass in the target node, as well as the observer options
observer.observe(target, config);

}

window.addEventListener("load", Reasoner.associateEventsOnBrowserGraphicElements, true);


/**
 * Open window to display the configuration of reasoning operation
 */
 function showReasonerConfiguration() {
	 
	 //Call the service from Semantic Turkey service to retrieve the configuration parameters of the Reasoner
	 var response = Reasoner.Requests.ReasonerConfigurations.loadConfiguration();

	 var howManyTimesApplyInferenceRule;
	 var whichRuleApply;
	 var produceOutput;

	 //Check the response from server.
	 //If the request failed show a popup with errore message.
	 if (response.getElementsByTagName("reply")[0].getAttribute("status") == "fail") {
		 window.alert("unable to load file, Error"+
				 response.getElementsByTagName("ConfigurationParameter")[0].getAttribute("error"));
	 }
	 //If request is ok, get the configuration parameter from response.
	 if (response.getElementsByTagName("reply")[0].getAttribute("status") == "ok") {
		 howManyTimesApplyInferenceRule = response.getElementsByTagName("ConfigurationParameter")[0].getAttribute("numberOfExecution");
		 whichRuleApply = response.getElementsByTagName("ConfigurationParameter")[0].getAttribute("whichRuleApply");
		 produceOutput = response.getElementsByTagName("ConfigurationParameter")[0].getAttribute("produceOutput");
	 }
	 //Create an Array of paramters with configuration parameter of reasoner.
	 var params = {inn:{outputValue:produceOutput, cycleNumber:howManyTimesApplyInferenceRule, whichRuleApply:whichRuleApply, continueOperation: false, reasoningResult: null}, out:null};
	//Open the Reasoner configuration Dialog
	window.openDialog("chrome://reasonerExtension/content/resonerConfiguration.xul",
			 "Reasoner Configuration", "chrome,centerscreen,dialog,modal",params).focus();

		if(params.out.continueOperation){
			 window.openDialog("chrome://reasonerExtension/content/showOutput.xul",
             							 "Output window", "chrome,resizable=yes,modal",params.out.reasoningResult);
        }


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
		 let prompts =Components.classes["@mozilla.org/embedcomp/prompt-service;1"].getService(Components.interfaces.nsIPromptService);
                            					 prompts.alert(window, "Error", status);
	}
	let prompts =Components.classes["@mozilla.org/embedcomp/prompt-service;1"].getService(Components.interfaces.nsIPromptService);
                                					 prompts.alert(window, "Success", "triples removed");

	window.arguments[0].out = {save:'true',message:''}; 
	return true;
}
 
 





