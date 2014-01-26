/**
 * Handler the reasoner configuration dialog box. 
 */

if ("undefined" == typeof(Reasoner)) {
  var Reasoner = {};
};

 Components.utils.import("resource://reasonerExtensionServices/SERVICE_ReasonerConfiguration.jsm",Reasoner);

const Cc = Components.classes;
const Ci = Components.interfaces;
const Cu = Components.utils;
const Cr = Components.results;

/**
 * Populate the dialog window with paramters taken from reasoner configuration
 * @returns
 */
function onLoad(){
  // Use the arguments passed to us by the caller
  document.getElementById("produceOutputCheckBox").checked = window.arguments[0].inn.outputValue;
  document.getElementById("cycleNumberReasoning").value = window.arguments[0].inn.cycleNumber;
  document.getElementById("wichInferenceRuleAplly").value = window.arguments[0].inn.whichRuleApply;
}


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
			 var split = params.out.whichRuleApply.split(",");
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
				alert(response.getElementsByTagName("startReasoning")[0].getAttribute("warning"));
				   //Open the Reasoner configuration Dialog
				   return false;
			 }
			//If the reply is failed show a popup with error message.
			 if (response.getElementsByTagName("reply")[0].getAttribute("status") == "fail") {
				 let prompts =Cc["@mozilla.org/embedcomp/prompt-service;1"].getService(Ci.nsIPromptService);
				 prompts.alert(window, "Reasoner", "Unable to start reasoning.Error: "+
						 response.getElementsByTagName("startReasoning")[0].getAttribute("error"));
						   //Open the Reasoner configuration Dialog
                                     	 window.openDialog("chrome://reasonerExtension/content/resonerConfiguration.xul",
                                     			 "Reasoner Configuration", "chrome,centerscreen,dialog,modal",params).focus();

                                     	return false;
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
					params.out.reasoningResult = parameters;
							 return true;
				 }
			 }

		 }
		 else{
		    return false;
		 }

	 }
	 return false;
 }



/**
 * Save the paramters entered by the user.
 * @returns
 */
function onAccept() {
   // Return the changed arguments.
   // Notice if user clicks cancel, window.arguments[0].out remains null
   // because this function is never called
   
   window.arguments[0].out = {outputValue:document.getElementById('produceOutputCheckBox').checked,
   cycleNumber:document.getElementById('cycleNumberReasoning').value,
   whichRuleApply:document.getElementById('wichInferenceRuleAplly').value};
//CONFALSE NON SI CHIUDE

    window.arguments[0].out.continueOperation = callReasoningService(window.arguments[0])
return window.arguments[0].out.continueOperation;
//return;
   //return
}

function onCancel() {
   // Return the changed arguments.
   // Notice if user clicks cancel, window.arguments[0].out remains null
   // because this function is never called
   return false;
}

/**
 * Load from file system a txt file with inference rules definition.
 * @param defaultValue if value is true, load a default inference rule file, else load the inference rules file uploaded from the user.
 * @returns
 */
function loadInferenceRuleFile(defaultValue) {
	
	//Opent the window file picker
	var nsIFilePicker = Components.interfaces.nsIFilePicker;
	var fp = Components.classes["@mozilla.org/filepicker;1"]
	.createInstance(nsIFilePicker);
	fp.init(window, "Select a File", nsIFilePicker.modeOpen);
	var res = fp.show();
	if (res == nsIFilePicker.returnOK) {
		
		var txbox = document.getElementById("srcLocalFile");	
		txbox = fp.file.path;
	}
	//Convert file into stream input
	var fstream = Components.classes["@mozilla.org/network/file-input-stream;1"].createInstance(Components.interfaces.nsIFileInputStream); 
	var cstream = Components.classes["@mozilla.org/intl/converter-input-stream;1"].createInstance(Components.interfaces.nsIConverterInputStream);  

	fstream.init(fp.file, -1, 0, 0); 
	cstream.init(fstream, "UTF-8", 0, 0); 
	//Convert stream input into string
	var data = "";
	let (str = {}) { 
		let read = 0; 
		do { 
			read = cstream.readString(0xffffffff, str); 
			data += str.value; 
		} while (read != 0); 
	}
	cstream.close(); 
	//Call the uploadFileService from SemantickTurkey Server.
	var response = Reasoner.Requests.ReasonerConfigurations.sendFileToReasoner(data,defaultValue);
	//If reply is failed show an error popup
	if (response.getElementsByTagName("reply")[0].getAttribute("status") == "fail") {
		window.alert("unable to load file");
	}
	//If reply is ok show an success popup
	if (response.getElementsByTagName("reply")[0].getAttribute("status") == "ok") {
		document.getElementById("load_inference_rule_label").value = "file loaded";
	}


}
/**
 * Show inference rules on window
 * @returns
 */
function showRules(){
	//Call the show Rules service on SemanticTurkey server.
	var response = Reasoner.Requests.ReasonerConfigurations.getInferenceRuleFileText();
	//If the call  failed, show an error message.
	if (response.getElementsByTagName("reply")[0].getAttribute("status") == "fail") {
		window.alert("unable to load file. Error: "+response.getElementsByTagName("InferenceRuleFile")[0].getAttribute("error"));
		showText = "error";
	}
	//If reply is ok, get inference rules text.
	if (response.getElementsByTagName("reply")[0].getAttribute("status") == "ok") {
		//Get the inference rules text from response
		var text = response.getElementsByTagName("InferenceRuleFile")[0].getAttribute("textInferenceRuleFile")
		showText = text;
	}
	//Open inference rules window.
	window.openDialog("chrome://reasonerExtension/content/showRules.xul", "Show Inference rules Window",
			"chrome,width=800,height=600,resizable=yes",{text: showText});

}

function showHelpWindow(){
	window.openDialog("chrome://reasonerExtension/content/helpWindow.xul", "Show Help window",
	"chrome,width=800,height=600,resizable=no");
}

/**
 * Upload the modify inference rule file from user.
 * @returns
 */
function onUpdate() {

	//take the text edit
	var text = document.getElementById("text_box").value;


    let prompts =
       Components.classes["@mozilla.org/embedcomp/prompt-service;1"].
        getService(Components.interfaces.nsIPromptService);

    if (prompts.confirm(window, "Save inference rules file", "Before upload the modified inference rule files, do you want save it?")) {
      saveInferenceRuleFile(text);
    }

	//call service from SemanticTurkey server
	var response = Reasoner.Requests.ReasonerConfigurations.sendFileToReasoner(text,"false");
	if (response.getElementsByTagName("reply")[0].getAttribute("status") == "fail") {
	     let prompts =Cc["@mozilla.org/embedcomp/prompt-service;1"].getService(Ci.nsIPromptService);
        						 prompts.alert(window,"Error","unable to upload file");
	}
	if (response.getElementsByTagName("reply")[0].getAttribute("status") == "ok") {
		let prompts =Cc["@mozilla.org/embedcomp/prompt-service;1"].getService(Ci.nsIPromptService);
                						 prompts.alert(window,"Success","File has been uploaded.");
	}

}

function saveInferenceRuleFile(fileToSave) {

	//Opent the window file picker
	var nsIFilePicker = Components.interfaces.nsIFilePicker;
	var fp = Components.classes["@mozilla.org/filepicker;1"]
	.createInstance(nsIFilePicker);
	fp.init(window, "Save inference rules files...", nsIFilePicker.modeSave);
	fp.appendFilter("TXT File","*.txt");
    fp.defaultString="inference_rule_file.txt";
	var res = fp.show();
    if(res == nsIFilePicker.returnOK || res == nsIFilePicker.returnReplace){
        var file = fp.file;
        if(file.exists() == false){//create as necessary
                file.create( Components.interfaces.nsIFile.NORMAL_FILE_TYPE, 420 );
            }
            var outputStream = Components.classes["@mozilla.org/network/file-output-stream;1"]
                                                  .createInstance( Components.interfaces.nsIFileOutputStream );
            outputStream.init( file, 0x04 | 0x08 | 0x20, 640, 0 );
            var result = outputStream.write( fileToSave, fileToSave.length );
            outputStream.close();
            	let prompts =Cc["@mozilla.org/embedcomp/prompt-service;1"].getService(Ci.nsIPromptService);
                            						 prompts.alert(window,"Success","File has been saved.");
    }
}


/**
 * Restore the default inference rules file.
 * @returns
 */
function restoreDefaultRuleFiles(){
	var response = Reasoner.Requests.ReasonerConfigurations.sendFileToReasoner("",'true');
	if (response.getElementsByTagName("reply")[0].getAttribute("status") == "fail") {
		let prompts =Cc["@mozilla.org/embedcomp/prompt-service;1"].getService(Ci.nsIPromptService);
		prompts.alert(window, "Configuration Error", "unable to load file");
	}
	if (response.getElementsByTagName("reply")[0].getAttribute("status") == "ok") {

		document.getElementById("load_inference_rule_label").value = "default file loaded";
        let prompts =Cc["@mozilla.org/embedcomp/prompt-service;1"].getService(Ci.nsIPromptService);
                                    						 prompts.alert(window,"Success","Default Inference Rules File has been restored");


	}
}

function openAddRuleWindow(){
var params = {inn:{txt_box:document.getElementById("text_box")}, out:null};     
	window.openDialog("chrome://reasonerExtension/content/addRule.xul", "Add Rule Window",
			"chrome,width=800,height=600,resizable=yes",params);
}

function addProposition(type){
	var btn = document.getElementById(type);
	var hbox = document.createElement("hbox");
	
	var subject = document.createElement("label")
	subject.setAttribute("value","subject");
	var subject_text = document.createElement("textbox");
	hbox.appendChild(subject);
	hbox.appendChild(subject_text);
	
	var object = document.createElement("label")
		object.setAttribute("value","object");
	var object_text = document.createElement("textbox");
	hbox.appendChild(object);
	hbox.appendChild(object_text);
	
	var predicate = document.createElement("label")
		predicate.setAttribute("value","predicate");
	var predicate_text = document.createElement("textbox");
	hbox.appendChild(predicate);
	hbox.appendChild(predicate_text);
	
	btn.appendChild(hbox);
}

function addFilterCondition(){
    if(document.getElementById("filter_condition_hbox") == null){
	var btn = document.getElementById("button_filter");
    	var hbox = document.createElement("hbox");
        hbox.setAttribute("id","filter_condition_hbox");
    	var labelFilter = document.createElement("label")
        	labelFilter.setAttribute("value","Filter condition");
        	var filterText= document.createElement("textbox");
        	hbox.appendChild(labelFilter);
        	hbox.appendChild(filterText);
        	btn.appendChild(hbox);
        	}
}

function removeFilterCondition(){
    if(document.getElementById("filter_condition_hbox") != null){
        var btn = document.getElementById("button_filter");
        var hbox = document.getElementById("filter_condition_hbox");
        btn.removeChild(hbox);
    }
}

function addConclusion(){
	var btn = document.getElementById("button_conclusion");
	var premise = document.createElement("textbox");
	
	btn.appendChild(premise);
}

function removePremise(){
	var element = document.getElementById("button_premise");
  
	if(element.lastChild.getAttribute("id") != "premise_box"){
    element.removeChild(element.lastChild);
	}
  
}

function removeConclusion(){
	var element = document.getElementById("button_conclusion");
  
	if(element.lastChild.getAttribute("id") != "conclusion_box"){
    element.removeChild(element.lastChild);
	}
}

function disableConlcusion(value) {
	
 document.getElementById("btn_add").setAttribute("disabled",value);
 document.getElementById("btn_remove").setAttribute("disabled",value);
}

function refresh(){
	document.getElementById("rule_name").value = "";
	document.getElementById("rule_ID").value = "";
	removeProposition();
}

function removeProposition(){
	var element = document.getElementById("button_premise");
  while(element.hasChildNodes()){
    if(element.lastChild.getAttribute("id") != "premise_box"){
    element.removeChild(element.lastChild);
	   }
		 else{
			break;
		 }
  }
	
	var element = document.getElementById("button_conclusion");
  while(element.hasChildNodes()){
    if(element.lastChild.getAttribute("id") != "conclusion_box"){
    element.removeChild(element.lastChild);
	   }
		 else{
			break;
		 }
  }
}

function addRule(){
	var type;
	if (document.getElementById("nr").selected) {
		type = "type: new rule\n";
	}
	else{
		type = "type: inconsistency\n";
	}
	var name = "name: "+document.getElementById("rule_name").value+"\n";
	var id = "id: "+document.getElementById("rule_ID").value+"\n";
	var premise = "";
	var element = document.getElementById("button_premise");
  while(element.hasChildNodes()){
    if(element.lastChild.getAttribute("id") != "premise_box"){
			var hbox = element.lastChild;
			hbox.children[0]
			
			
			premise = premise+"premise: "+"subject: "+hbox.children[1].value
			+" predicate: "+hbox.children[3].value+" object: "+hbox.children[5].value
			+"\n";
    element.removeChild(element.lastChild);
	   }
		 else{
			break;
		 }
  }
  var filter = "";
   if(document.getElementById("filter_condition_hbox") != null){
   filter = "filter: ";
   var box = document.getElementById("filter_condition_hbox")
   filter = filter+box.children[1].value+"\n";
   }

	var conclusion = "";
	if (document.getElementById("nr").selected) {
		var element = document.getElementById("button_conclusion");
  while(element.hasChildNodes()){
    if(element.lastChild.getAttribute("id") != "conclusion_box"){
				var hbox = element.lastChild;
			hbox.children[0]
			
			
			conclusion = conclusion+"conclusion: "+"subject: "+hbox.children[1].value
			+" predicate: "+hbox.children[3].value+" object: "+hbox.children[5].value
			+"\n";
    element.removeChild(element.lastChild);
	   }
		 else{
			break;
		 }
  }
	}
	else{
		conclusion = "conclusion: false";
	}
	
	var txt = window.arguments[0].inn.txt_box;
	
	txt.value = txt.value+"\n"+type+name+id+premise+filter+conclusion;
}
