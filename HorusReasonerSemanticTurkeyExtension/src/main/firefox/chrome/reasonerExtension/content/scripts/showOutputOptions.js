/**
 * Handler the output of reasoning operation
 */

if ("undefined" == typeof(Reasoner)) {
	var Reasoner = {};
};

Components.utils.import("resource://reasonerExtensionServices/SERVICE_Reasoning.jsm",Reasoner);





function onLoad() {
	document.getElementById("Output_informations").value = "New triple discoverd: "+ window.arguments[0].inn.newTriple;
	showTextOutput();
	document.getElementById("text_box").value = "";
	loadInferenceRulesName(window.arguments[0].inn.nameRules);
	showTextOutput();
	
	
	
	
}

//function loadTable(jsonString) {
//	var json  = JSON.parse(jsonstring)
//	
//	for(var i=0; i < json.length; i++){
//		
//	}
//}

function loadInferenceRulesName(nameRules){
	var aBox = document.getElementById("rules_filter");
	while (aBox.firstChild){
		aBox.removeChild(aBox.firstChild);
	}
	var captionFilter = document.createElement("caption");
	captionFilter.setAttribute("label","Rules Filter");
	aBox.appendChild(captionFilter);
	var splittedName = nameRules.split(",");
	for (var i=0; i < splittedName.length; i++){
		if (splittedName[i] != "" && splittedName[i] != undefined ) {
		var checkBox = document.createElement("checkbox");
		checkBox.setAttribute("label",splittedName[i]);
		checkBox.setAttribute("id",splittedName[i]);
		checkBox.setAttribute("checked","true");
		aBox.appendChild(checkBox);
		}
    
	}
 var button = document.createElement("button");
 button.setAttribute("label","Apply Filters");
 button.setAttribute("oncommand","applyFilters(window.arguments[0].inn.nameRules);");
 aBox.appendChild(button);
}

function applyFilters(nameRules) {
	var splittedName = nameRules.split(",");
	var rules=""
	for (var i=0; i <= splittedName.length; i++){
	if(splittedName[i] != "" && splittedName[i] != undefined &&
		 document.getElementById(splittedName[i]).getAttribute("checked") == "true"){
		rules = rules +splittedName[i]+",";
	 }
	}
	var response = Reasoner.Requests.ReasoningService.filterRules(rules);
	 window.arguments[0].inn.printOutput = response.getElementsByTagName("filter")[0].getAttribute("resultToList");
	 document.getElementById("text_box").value =  window.arguments[0].inn.printOutput;
	 
}
/**
 * Show the output in a text box
 */
function showTextOutput(){
	//Take the reference to html box
	var aBox = document.getElementById("show_box");
	//Check if the graph wiew box is enable, if true close it and show text box
	if( document.getElementById("graph_frame") != null){
		var graph= document.getElementById("graph_frame");
		aBox.removeChild(graph);
	}
	
	if( document.getElementById("rules_filter") == null){
		var main = document.getElementById("main_groupbox");
		var filter_box = document.createElement("groupbox");
		filter_box.setAttribute("id","rules_filter");
		main.appendChild(filter_box);
		loadInferenceRulesName(window.arguments[0].inn.nameRules);
	}
	//Check if text box element exist. If not, create new text box element
	if (document.getElementById("text_rules") == null) {

		var vbox = document.createElement("vbox");
		vbox.setAttribute("id","text_rules");
		aBox.appendChild(vbox);
		
		var textBox = document.createElement("textbox");
		textBox.setAttribute("id","text_box");
		textBox.setAttribute("style","background-color: transparent;");
		textBox.setAttribute("flex","1");
		textBox.setAttribute("multiline","true");
		textBox.setAttribute("width","800");
		textBox.setAttribute("height","600");
		vbox.appendChild(textBox);
	}
	//Show the search box element
	document.getElementById("gb_triple").setAttribute("style","display:block");
	document.getElementById("TripleSearchBox").setAttribute("placeholder","subject,predicate,object");
	//append output text to text box
	document.getElementById("text_box").value =  window.arguments[0].inn.printOutput;
}

/**
 * Search triple function
 */
function searchTriple(){
	//Get the text from search triple box
	var triple = document.getElementById("TripleSearchBox").value;
	//chek the input of user
	if (triple == "" || triple.length <=1 || triple == undefined ) {
		window.alert("wrong input");
	}
	else {
		//Split input in three pats. One for triple subject, One for triple predicate, one for triple object
		var txt = triple.replace("(","");
		txt = txt.replace(")","");
		txt = txt.split(",");
		var subject = txt[0];
		var predicate = txt[1];
		var object = txt[2];
		//check the spiltted string
		if (subject == undefined || predicate == undefined || object == undefined ) {
			window.alert(" check your input");
		}
		else{
			//Call the searchTriple service
			var response = Reasoner.Requests.ReasoningService.searchTriple(subject.trim(),predicate.trim(),object.trim());
			//If reply is ok, check the value of response
			if (response.getElementsByTagName("reply")[0].getAttribute("status") == "ok") {
				//get response
				var history =  response.getElementsByTagName("searchTriple")[0].getAttribute("historyOfTriple");
				//If response is empty show a warn message.
				if (history == "") {
					document.getElementById("text_box").value = "the triple does not exist"+"\n"+"Your input: "+triple;
				}
				//Show the history of triple
				else{
					document.getElementById("text_box").value =  history;
				}

			}
		}
	}

}




function dismissWindow(){
	window.arguments[0].out = {save:'cancel',message:''}; 
	return true;
}

function dismissButtonNewTriple() {

	var params = {inn:{save:'false'}, out:null};      
	//Open save new triple discovered window
	window.openDialog("chrome://reasonerExtension/content/saveNewTriple.xul",
			"Reasoner Configuration", "chrome,centerscreen,dialog,modal",params);
	if (params.out.save == 'true') {
		this.close();
	}
}

/**
 * Open graph box
 */
function openGraph(){
	//Remove from window the search triple box
	document.getElementById("gb_triple").setAttribute("style","display:none");
	//Get reference to box to show graph view
	var aBox = document.getElementById("show_box");
	//Check if text box view is open. If true, remove it from window.
	if (document.getElementById("text_box") !=null) {

		var vbox = document.getElementById("text_rules");
		vbox.removeChild(document.getElementById("text_box"));
		aBox.removeChild(vbox);
	}
	
	if( document.getElementById("rules_filter") != null){
		var main = document.getElementById("main_groupbox");
		var filter_box = document.getElementById("rules_filter");
		main.removeChild(filter_box);
	}
	
	//Chek if graph view is open. If true, create new graph view, else reload box
	if (document.getElementById("graph_frame") == null) {
		var graph= document.createElement("iframe");
		graph.setAttribute("id","graph_frame");
		graph.setAttribute("src","chrome://reasonerExtension/content/showGraph.html");
		graph.setAttribute("type","content");
		graph.setAttribute("width","800");
		graph.setAttribute("height","600");
		aBox.appendChild(graph);
	}

	else{
		var graph= document.getElementById("graph_frame");
		aBox.removeChild(graph);
		var graph= document.createElement("iframe");
		graph.setAttribute("id","graph_frame");
		graph.setAttribute("src","chrome://reasonerExtension/content/showGraph.html");
		graph.setAttribute("type","content");
		graph.setAttribute("width","800");
		graph.setAttribute("height","600");
		aBox.appendChild(graph)

	}
}

function onClose(){
	var windowOutput = document.getElementById("show_rules_window");
	this.close();
}


