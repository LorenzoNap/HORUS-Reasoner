/**
 * Handler the reasoner configuration dialog box. 
 */

if ("undefined" == typeof(Reasoner)) {
  var Reasoner = {};
};

 Components.utils.import("resource://reasonerExtensionServices/SERVICE_ReasonerConfiguration.jsm",Reasoner);



/**
 * Populate the dialog window with paramters taken from reasoner configuration
 * @returns
 */
function onLoad(){
  // Use the arguments passed to us by the caller
  document.getElementById("produceOutputCheckBox").checked = window.arguments[0].inn.outputValue;
  document.getElementById("cycleNumberReasoning").value = window.arguments[0].inn.cycleNumber;
  document.getElementById("wichInferenceRuleAplly").value = window.arguments[0].inn.whicruleApply;
}

/**
 * Save the paramters entered by the user.
 * @returns
 */
function onOK() {
   // Return the changed arguments.
   // Notice if user clicks cancel, window.arguments[0].out remains null
   // because this function is never called
   
   window.arguments[0].out = {outputValue:document.getElementById('produceOutputCheckBox').checked,
   cycleNumber:document.getElementById('cycleNumberReasoning').value,
   whicruleApply:document.getElementById('wichInferenceRuleAplly').value};       
   
   return true;
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
	//call service from SemanticTurkey server
	var response = Reasoner.Requests.ReasonerConfigurations.sendFileToReasoner(text,"false");
	if (response.getElementsByTagName("reply")[0].getAttribute("status") == "fail") {
		window.alert("unable to upload file");
	}
	if (response.getElementsByTagName("reply")[0].getAttribute("status") == "ok") {
		window.alert("file uploaded");
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

		window.alert("Default Inference Rules File has been restored");

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
	
	txt.value = txt.value+"\n"+type+name+id+premise+conclusion;
}
