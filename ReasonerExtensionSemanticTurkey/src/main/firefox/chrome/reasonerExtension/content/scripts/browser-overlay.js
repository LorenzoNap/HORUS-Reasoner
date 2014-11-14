/*

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
    gBrowser.selectedTab = gBrowser.addTab("chrome://reasonerExtension/content/HORUSHtml/index.html");
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






