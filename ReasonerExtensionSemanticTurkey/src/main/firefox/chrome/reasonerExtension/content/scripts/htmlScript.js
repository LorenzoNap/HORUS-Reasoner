
if ("undefined" == typeof(Reasoner)) {
  var Reasoner = {};
};


 Components.utils.import("resource://reasonerExtensionServices/SERVICE_ReasonerConfiguration.jsm",Reasoner);
 Components.utils.import("resource://reasonerExtensionServices/SERVICE_Reasoning.jsm",Reasoner);
 Components.utils.import("resource://reasonerExtensionServices/SERVICE_GraphServices.jsm",Reasoner);

var ID_PARAGRAPH_RULE = 'paragraph_rules';
var showText;
var SHOW_GRAPH_ID = 'graphView';

var reasonerParameters_object = new resonerParamters ("","",false);

var reasoning_ouput_object = new reasoningOutputObject("","","");

var mapOfInfferedTriples = new Object(); // or var map = {};


/*
 * Retrieve the inference rules from ST.
 *
 */
function viewInferenceRulesFile(){
  
    //Disable other wiew
    showDiv(SHOW_GRAPH_ID,"none");
    showDiv("reasoner_parameters","none");
    removeStyleToShowGraph();
  
    //Call the show Rules service on SemanticTurkey server.
    var response = Reasoner.Requests.ReasonerConfigurations.getInferenceRuleFileText();
    //If the call  failed, show an error message.
    if (response.getElementsByTagName("reply")[0].getAttribute("status") == "fail") {
        createAlert("unable to load file. Error: "+response.getElementsByTagName("InferenceRuleFile")[0].getAttribute("error"),"alert-danger");
    }
    //If reply is ok, get inference rules text.
    if (response.getElementsByTagName("reply")[0].getAttribute("status") == "ok") {
	//Get the inference rules text from response
	var text = response.getElementsByTagName("InferenceRuleFile")[0].getAttribute("textInferenceRuleFile");
	showText = text;
    }
    
    var parsed = showText;
    //Split the inference rules 
    //parsed = parsed.split("&#x10;").join('<br>');
    parsed = parsed.split("<").join("&lt;");
    parsed = parsed.split(">").join("&gt;");
    parsed = parsed.split("&#x10;").join('<br>');
    //Get the reference to main_div of html file
    var frame = document.getElementById("main_div");
    //Get the reference to container of inference rules
    var div_rules = document.getElementById("reasoner_rules");
    div_rules.setAttribute("class","well well-sm");
    div_rules.setAttribute("style","display:block");
    
    //Check if the paragraph container exists: if true set the inference rule, else crete new one.
    if(document.getElementById(ID_PARAGRAPH_RULE) != null){
       document.getElementById(ID_PARAGRAPH_RULE).innerHTML = parsed;
    }
    else{
      var newParagraph = document.createElement('p');
      newParagraph.setAttribute('id',ID_PARAGRAPH_RULE);
      newParagraph.innerHTML = parsed;
      div_rules.appendChild(newParagraph);	 
    }	
}

/*
 * Edit the inferenceRules
 */
function editInferenceRules(){
  //enbale the save and discard buttons
  document.getElementById("save_rules_button").removeAttribute("disabled");
  document.getElementById("discard_rules_button").removeAttribute("disabled");
  document.getElementById("add_rules_button").removeAttribute("disabled");
  //Remove the visualization fo inference rule
  
  
  
  var div_rules = document.getElementById("reasoner_rules");
  var text = document.getElementById(ID_PARAGRAPH_RULE).innerHTML;
  
  var pH = $( "#paragraph_rules" ).height();
  var pW = $( "#paragraph_rules" ).width();
  document.getElementById(ID_PARAGRAPH_RULE).remove();
  
  //Create an "edit" mode of inference Rules
  var newParagraph = document.createElement('textarea');
  newParagraph.style = "height:"+pH+"px;width:"+pW+"px;resize:vertical;";
  newParagraph.setAttribute('id',ID_PARAGRAPH_RULE);
  newParagraph.setAttribute('class','form-control');
  var parsed = showText;
  parsed = parsed.split("&#x10;").join('\n');
  newParagraph.value = parsed;
  div_rules.appendChild(newParagraph);
}

/*
 * Add inference Rules
 */
function addInferenceRule(){
  $('#addRuleModal').modal('show');
  $('.premise').remove();
  
}

function addProposition(type){

  var create_form = document.createElement("form")
  create_form.setAttribute("class","form-inline triple")
  create_form.setAttribute("role","form")
  
  var div_form_group = document.createElement("div")
  div_form_group.setAttribute("class","form-group")
  var input = document.createElement("input")
  if (type != 'Filter Condition') {
    
    input.setAttribute("class","form-control subject")
    input.setAttribute("placeholder","Subject")
    input.setAttribute("style","margin:5px")
    
    div_form_group.appendChild(input)
    create_form.appendChild(div_form_group)
    
    document.getElementById(type).appendChild(create_form)
    
    
    div_form_group = document.createElement("div")
    div_form_group.setAttribute("class","form-group")
    input = document.createElement("input")
    input.setAttribute("class","form-control predicate")
    input.setAttribute("placeholder","Predicate")
    input.setAttribute("style","margin:5px")
    
    div_form_group.appendChild(input)
    create_form.appendChild(div_form_group)
    
    div_form_group = document.createElement("div")
    div_form_group.setAttribute("class","form-group")
    input = document.createElement("input")
    input.setAttribute("class","form-control object")
    input.setAttribute("placeholder","Object")
    input.setAttribute("style","margin:5px")
    
    div_form_group.appendChild(input)
  }
  else{
    input.setAttribute("class","form-control filter")
    input.setAttribute("placeholder","filter")
    input.setAttribute("style","margin:5px")
    
    div_form_group.appendChild(input)
    create_form.appendChild(div_form_group)
    
    document.getElementById(type).appendChild(create_form)
  }
  
  var minus_icon = document.createElement("span")
  minus_icon.setAttribute("class","glyphicon glyphicon-remove")
  minus_icon.setAttribute("onclick","$(this).parent().parent().remove()")
  div_form_group.appendChild(minus_icon)
  create_form.appendChild(div_form_group)

}

function hideInconstenceForm(value) {
  if (value.value == 'new rule'){
    document.getElementById('Conclusion').setAttribute('style','display:block');
  }
  else{
    document.getElementById('Conclusion').setAttribute('style','display:none');
  }
}

function saveAddRule(){
  
  var inferenceRule = ""
  
  var rule_name = "name: "+document.getElementById("add_rule_rule_name").value+"\n"
  var id_name = "id: "+document.getElementById("add_rule_id").value+"\n"
  var type_of_rule = "type: "+document.getElementById("add_rule_rule_type").value+"\n"
    
    var premises = ""
    //GET PREMISES
    var forms = document.getElementById("Premises").getElementsByClassName("triple")
    //For each forms(triple) get predicate,subject and object
    for(var i = 0; i< forms.length;i++){
      var triple = forms[i]
      var subject = triple.getElementsByClassName("subject")[0].value+" "
      var predicate = triple.getElementsByClassName("predicate")[0].value+" "
      var object = triple.getElementsByClassName("object")[0].value+""
      
      premises += "premise: "+subject+predicate+object+"\n"
      
    }
    
    //GET FILTER 
    forms = document.getElementById("Filter Condition").getElementsByClassName("triple")
     //For each forms(filter) get predicate,subject and object
    for(var i = 0; i< forms.length;i++){
      var triple = forms[i]
      var filter = triple.getElementsByClassName("filter")[0].value
      
      premises += "filter: "+filter+"\n"
      
    }
    
    //If the rule has type 'new" get the conclusion
    if (type_of_rule.contains("new")) {
	//GET CONCLUSION 
      forms = document.getElementById("Conclusion").getElementsByClassName("triple")
       //For each forms(filter) get predicate,subject and object
      for(var i = 0; i< forms.length;i++){
	var triple = forms[i]
	var subject = triple.getElementsByClassName("subject")[0].value+" "
	var predicate = triple.getElementsByClassName("predicate")[0].value+" "
	var object = triple.getElementsByClassName("object")[0].value+""
	
	premises += "conclusion: "+subject+predicate+object+"\n"
	
      }
    }
    
    //Add the rules on edit text paragraph
    var inferenceRule = "";
    inferenceRule = type_of_rule+rule_name+id_name+premises;
    document.getElementById("paragraph_rules").value = document.getElementById("paragraph_rules").value+"\n" +inferenceRule;
    
    createAlert("Rule added,pls save your file","alert-success");
 
}


//Save modifed Inference Rule file
function saveInferenceRules(){
  //take the text edit
  var text = document.getElementById(ID_PARAGRAPH_RULE).value;
  //call service from SemanticTurkey server
  var response = Reasoner.Requests.ReasonerConfigurations.sendFileToReasoner(text,"false");
  //Check the response
  if (response.getElementsByTagName("reply")[0].getAttribute("status") == "fail") {
    createAlert("unable to upload file","alert-danger")
  }
  if (response.getElementsByTagName("reply")[0].getAttribute("status") == "ok") {
    createAlert("File has been uploaded.","alert-success")
    document.getElementById("save_rules_button").setAttribute("disabled","disabled")
    document.getElementById("discard_rules_button").setAttribute("disabled","disabled")
    document.getElementById(ID_PARAGRAPH_RULE).remove();
    //Call the visualization of inference rule
    viewInferenceRulesFile()
    }  
}

//Discard the changes of inference rules
function discardInferenceRules() {
  document.getElementById("save_rules_button").setAttribute("disabled","disabled");
  document.getElementById("discard_rules_button").setAttribute("disabled","disabled");
  document.getElementById("add_rules_button").setAttribute("disabled","disabled");
  document.getElementById(ID_PARAGRAPH_RULE).remove();
  viewInferenceRulesFile();
}


function uploadInferenceRulesFile() {
	
	//Call the file reader
	var output = uploadFile()	
	//Call the uploadFileService from SemantickTurkey Server.
	var response = Reasoner.Requests.ReasonerConfigurations.sendFileToReasoner(output,'false');
	//If reply is failed show an error popup
	if (response.getElementsByTagName("reply")[0].getAttribute("status") == "fail") {
		createAlert("Unable to load file","alert-danger");
	}
	//If reply is ok show an success popup
	if (response.getElementsByTagName("reply")[0].getAttribute("status") == "ok") {
		createAlert("File uploaded","alert-success")
	}


}

function showPopupRules(){
    var text=""
     //Call the show Rules service on SemanticTurkey server.
    var response = Reasoner.Requests.ReasonerConfigurations.getInferenceRuleFileText();
    //If the call  failed, show an error message.
    if (response.getElementsByTagName("reply")[0].getAttribute("status") == "fail") {
      text = "Unable to retrieve inference rules"
    }
    //If reply is ok, get inference rules text.
    if (response.getElementsByTagName("reply")[0].getAttribute("status") == "ok") {
	//Get the inference rules text from response
	var text_output = response.getElementsByTagName("InferenceRuleFile")[0].getAttribute("textInferenceRuleFile");
	var parsed = text_output;
	parsed = parsed.split("<").join("&lt;");
    	parsed = parsed.split(">").join("&gt;");
	parsed = parsed.split("&#x10;").join('<br>');
	text = parsed;
    }
  
  var newParagraph = document.getElementById("paragrahp_rule_modal")
  
  
      newParagraph.innerHTML = text;
}

function setReasonerParameters(){
  
  showDiv("reasoner_rules","none")
  showDiv(SHOW_GRAPH_ID,"none")
  showDiv("infferedTriples","none")
  removeStyleToShowGraph()
  showDiv("reasoner_parameters","block")
   $('[data-toggle="tooltip"]').tooltip({
                                'placement': 'right'
                            });
                            $('[data-toggle="popover"]').popover({
                                trigger: 'hover',
                                    'placement': 'right'
                            });
  
}

function saveReasonerParameters(){
  
  var iteration = document.getElementById("executions_reasoner").value;
  var inferenceRules = document.getElementById("rules_to_apply").value;
  var produceOutput = document.getElementById("produce_output").checked;
  
  var unset_iteration = document.getElementById("unset_iteration_check_box").checked
  if (unset_iteration) {
    //code
    iteration = "0"
  }
  
  var use_all_rules_checkbox = document.getElementById("use_all_rules_checkbox").checked
  if (use_all_rules_checkbox) {
    //code
    inferenceRules = ""
  }
  
  var goAhead = true
  if (isNaN (iteration) ||  iteration == "" ||  iteration == " "){
    createAlert("You have inserted a wrong parameter in iterations box","alert-danger");
    goAhead = false;
  }
  else {
    //if (inferenceRules == "" || inferenceRules == " ") {
    //  //code
    //  createAlert("You have inserted a wrong id on inference rule id box","alert-danger");
    //  goAhead = false;
    //}
    if (!inferenceRules == "") {
      //code
      var split = inferenceRules.split(",");
      for (var i = 0; i < split.length; i++) {
      if (isNaN (split[i])){
	createAlert("You have inserted a wrong id on inference rule id box","alert-danger");
	goAhead = false;
	break;
      }
    }
    }
    
  }
  
  if (goAhead) {
    reasonerParameters_object.inferenceRules = inferenceRules
    reasonerParameters_object.iteration = iteration
    reasonerParameters_object.produceOutput = produceOutput
    createAlert("Parameters saved","alert-success")
  }
   
}

function resonerParamters (inferenceRules,iteration,produceOutput) {
    this.inferenceRules = inferenceRules;
    this.iteration = iteration;
    this.produceOutput = produceOutput
}


function reasoningOutputObject(infferedTriple,jsonGraph,inferenceRulesNames) {
  this.infferedTriple = infferedTriple
  this.jsonGraph = jsonGraph
  this.inferenceRulesNames = inferenceRulesNames
}

function startReasoning(){
  
  showDiv("reasoner_rules","none")
  showDiv("reasoner_parameters","none")
  showDiv(SHOW_GRAPH_ID,"none")
  removeStyleToShowGraph()
  
  
  if ( reasonerParameters_object.iteration == "") {
    createAlert("Before to proceed you must insert the reasoning parameters","alert-danger");
  }
  else {
     //Call the reasoning service from SemantickTurkey server.
    var response = Reasoner.Requests.ReasoningService.startReasoning(reasonerParameters_object);
    //If the reply is a warning show a popup with warn message.
    if (response.getElementsByTagName("reply")[0].getAttribute("status") == "warning") {
      createAlert(response.getElementsByTagName("startReasoning")[0].getAttribute("warning"),"alert-danger");
    }
    else if (response.getElementsByTagName("reply")[0].getAttribute("status") == "fail") {
      createAlert("Unable to start reasoning.Error: "+ response.getElementsByTagName("startReasoning")[0].getAttribute("error"),"alert-danger");
					
      }
    else {
	setReasoningOuput(response.getElementsByTagName("startReasoning")[0])
    }
    
  }
}





function setReasoningOuput(outputs){
  
    var triple_discovered = outputs.getAttribute("numberOfTriple");
    //Boolean to represent if output has been produced
    var output = outputs.getAttribute("produceOutput");
    //The text output of reasoning
    var print = outputs.getAttribute("printOutput");
    //Get names of inferenceRules
    var names = outputs.getAttribute("inferenceRulesNames");

    var JsonInference = outputs.getAttribute("jsonInference");

    var numberOfIterationOfReasoning = outputs.getAttribute("numberOfIteration");
    //Create an array of parameters with the information taken from the server response
    var parameters = {inn:{newTriple:triple_discovered, produceOutput:output,printOutput:print,nameRules:names,jsonInference:JsonInference,iterations:numberOfIterationOfReasoning }, out:null};
    //If the output has not produced, show the dialog box to save the new triples discovered, else show the window output
    if (output == 'false') {
     createAlert("Reasoning success.Number of discovered triples: "+ (triple_discovered),"alert-success");
     }
    else{
      if(numberOfIterationOfReasoning <  reasonerParameters_object.iteration ||  reasonerParameters_object.iteration == 0){
	createAlert("Reasoner iteration (value chosen by the user): "+reasonerParameters_object.iteration+"\n"+"Effective iteration: "+numberOfIterationOfReasoning,"alert-success");
      }
	
	
      
    }
    document.getElementById("view_discovered_id_element").setAttribute("class","");
    document.getElementById("view_discovered_id_element").setAttribute("onclick","viewDiscoverdTriple()")
    
    document.getElementById("show_current_project_graph").setAttribute("class","");
    document.getElementById("show_current_project_graph").setAttribute("onclick","showGraph()")

    reasoning_ouput_object.infferedTriple = JsonInference
    reasoning_ouput_object.inferenceRulesNames = names
    viewDiscoverdTriple()
   
}

function viewDiscoverdTriple() {

  showDiv("reasoner_rules","none")
  showDiv("reasoner_parameters","none")
  showDiv(SHOW_GRAPH_ID,"none")
  removeStyleToShowGraph()
  showDiv("infferedTriples","block")
  
  removeChildren({parentId:'infferedTriples',childName:'new_triples_discovered'})
  
  var json = JSON.parse(reasoning_ouput_object.infferedTriple);
  var div = document.getElementById("infferedTriples");
    
  //var select = document.createElement("select")
  //select.setAttribute("class","form-control")
  //select.setAttribute("id","new_triples")
  //select.setAttribute("style","width: 40%; margin-bottom: 30px;")
  
  //Adde defualt options to filter select
  var select_ref = document.getElementById("infferedTriplesFilter")
  var select_ref_inference_rules = document.getElementById("inference_rules_filter")  
  var option = document.createElement("option");
  option.text = " ";
  option.value = "None";
  select_ref.add(option)
  
  var option2 = document.createElement("option");
  option2.text = " ";
  option2.value = "None";
  select_ref_inference_rules.add(option2)
  
  
  var splittedName = reasoning_ouput_object.inferenceRulesNames.split(",");
	for (var i=0; i < splittedName.length; i++){
		if (splittedName[i] != "" && splittedName[i] != undefined ) {
		    var option = document.createElement("option");
		    option.text = splittedName[i];
		    option.value = splittedName[i];
		    select_ref_inference_rules.add(option)
		}
    
	}
  
  
  //div.appendChild(select)
    var map_count = 0
    
    
    var string = "";
    for(var i=0;i<json.length;i++){
        var obj = json[i];
	var attribute = "<table class='table table-bordered new_triples_discovered'>"
	attribute = attribute + "<tr><th colspan='3'>Rule</th></tr>"
	
	attribute = attribute +"<tr><td>Id</td><td>Type</td><td>Name</td></tr>"
	attribute =  attribute+ "<tr><td>"+obj[0].ruleId+"</td><td>"+obj[0].ruleName+"</td><td >"+obj[0].ruleType+"</td></tr><tr>"
	attribute = attribute + "<tr><td colspan='3'>Triple Discovered<br></td></tr>"
        for(var j= 0; j< obj[1].length;j++){
	  
	  
	  
	  var triple = obj[1][j].simpleTriple
	  mapOfInfferedTriples[map_count] = obj[1][j].triple
	  attribute = attribute +"<tr><td colspan='3'><a href='#' onclick='searchTriple("+map_count+")'>"+triple+"</a></td></tr>"
	  
	  //create listbox element
	  var select_ref = document.getElementById("infferedTriplesFilter")  
	  var option = document.createElement("option");
	  option.text = triple;
	  option.value = triple;
	  select_ref.add(option);
	  
	  
	  map_count++
	  
	  
	  
	}
	attribute = attribute + "<tr><td colspan='3'>Derived from</td></tr>"
	for(var j= 0; j< obj[2].length;j++){
	  var triple = obj[2][j].triple
	  mapOfInfferedTriples[map_count] = triple
	  attribute = attribute +"<tr><td colspan='3'><a href='#' onclick='searchTriple("+map_count+")'>"+obj[2][j].simpleTriple+"</a></td></tr>"
	  
	}
	map_count++
	attribute = attribute +"</table>"
	div.innerHTML = div.innerHTML+attribute
    }
  
}

function filterDiscoveredTriple(){
  removeChildren({parentId:'infferedTriples',childName:'new_triples_discovered'})
  var searched_triple = document.getElementById('infferedTriplesFilter').value
  var searched_rule = document.getElementById('inference_rules_filter').value
  var go_ahead = false
  var map_count = 0
  var div = document.getElementById("infferedTriples");
  var json = JSON.parse(reasoning_ouput_object.infferedTriple);
  
  
  for(var i=0;i<json.length;i++){
    var obj = json[i];
    if (obj[0].ruleName == searched_rule || searched_rule == "None" || searched_rule == "Reset") {
      var obj = json[i];
      var attribute = ""
      for(var j= 0; j< obj[1].length;j++){
      if (obj[1][j].simpleTriple == searched_triple || searched_triple == "Reset" || searched_triple == "None"){
	go_ahead = true
	attribute = "<table class='table table-bordered new_triples_discovered'>"
	attribute = attribute + "<tr><th colspan='3'>Rule</th></tr>"
	
	attribute = attribute +"<tr><td>Id</td><td>Type</td><td>Name</td></tr>"
	attribute =  attribute+ "<tr><td>"+obj[0].ruleId+"</td><td>"+obj[0].ruleName+"</td><td >"+obj[0].ruleType+"</td></tr><tr>"
	attribute = attribute + "<tr><td colspan='3'>Triple Discovered<br></td></tr>"
	
	 var triple = obj[1][j].simpleTriple
	 mapOfInfferedTriples[map_count] = obj[1][j].triple
	 attribute = attribute +"<tr><td colspan='3'><a href='#' onclick='searchTriple("+map_count+")'>"+triple+"</a></td></tr>"
	
	 map_count++
      }
    }
    if (go_ahead) {
      go_ahead = false
      attribute = attribute + "<tr><td colspan='3'>Derived from</td></tr>"
	for(var j= 0; j< obj[2].length;j++){
	  var triple = obj[2][j].triple
	  mapOfInfferedTriples[map_count] = triple
	  attribute = attribute +"<tr><td colspan='3'><a href='#' onclick='searchTriple("+map_count+")'>"+obj[2][j].simpleTriple+"</a></td></tr>"
	  
	}
	map_count++
	attribute = attribute +"</table>"
	div.innerHTML = div.innerHTML+attribute
      }
    }
  } 
  //Re-add the selected values
  document.getElementById('infferedTriplesFilter').value  = searched_triple
  document.getElementById('inference_rules_filter').value = searched_rule
}



function searchTriple(key) {
  var value = mapOfInfferedTriples[key]
  var txt = value.replace("(","");
  txt = txt.replace(")","");
  txt = txt.split(",");
  var subject = txt[0];
  var predicate = txt[1];
  var object = txt[2];
     
  //Call the searchTriple service
  var response = Reasoner.Requests.ReasoningService.searchTriple(subject.trim(),predicate.trim(),object.trim());
  //If reply is ok, check the value of response
  if (response.getElementsByTagName("reply")[0].getAttribute("status") == "ok") {
    //get response
    var history =  response.getElementsByTagName("searchTriple")[0].getAttribute("historyOfTriple");
    
    var json = JSON.parse(history);
    var string = ""
    for(var i=0;i<json.length;i++){
      var obj = json[i]
      if (obj.searched_node != undefined) {
	string = string +"Main node: "+obj.searched_node+"<br>"
      }
      else{
	string = string +"Inferred from: "+obj.geenrated+"<br>"
      }
    }
    //If response is empty show a warn message.
    $('#showSearchTripleResult').modal('show');
    var parsed = history.split("&#x10;").join('<br>');
    document.getElementById("resultSearchTriple").innerHTML = string
    //createAlert(history,"alert-success");
  }
  
}


function loadAvailableGraph() {
  //code
  var response = Reasoner.Requests.GraphRequests.loadListOfFile();
  if (response.getElementsByTagName("reply")[0].getAttribute("status") == "warning") {
    createAlert(response.getElementsByTagName("aviableListOfGraphs")[0].getAttribute("files"),"alert-danger");
   }
  else if (response.getElementsByTagName("reply")[0].getAttribute("status") == "fail"){
    createAlert(response.getElementsByTagName("aviableListOfGraphs")[0].getAttribute("files"),"alert-danger");
    
  }
  else{
    var string = response.getElementsByTagName("aviableListOfGraphs")[0].getAttribute("files")
    var jsonFiles = JSON.parse(string);
    
    removeAllChildsOfDiv("modal_body_load_different_graphs")
    
    
    //Parse the files
    for(var j= 0; j< jsonFiles.length;j++){
      var file = jsonFiles[j]
      var name = file.fileName
      var name = name.replace(".json", "");
      var div = document.getElementById("modal_body_load_different_graphs")
      var element = document.createElement("a")
      element.setAttribute("id","fileNameGraph")
      element.setAttribute("onclick","loadRequestedGraph('"+file.fileName+"')")
      element.setAttribute("href","#")
      element.innerHTML = name
      div.appendChild(element)
      div.appendChild(document.createElement("br"))
      
    }
    $('#laodGraphModal').modal('show')
  }
  
}

function loadRequestedGraph(name) {
  //code
  $('#laodGraphModal').modal('hide')
  showDiv("reasoner_rules","none")
  showDiv("reasoner_parameters","none")
  
  enableStyleShowGraph()
  showDiv("infferedTriples","none")
  document.getElementById('graph_view_iframe').src = "chrome://reasonerextension/content/showGraph.html?graphName="+name;
}



function showGraph() {
   
  showDiv("reasoner_rules","none")
  showDiv("reasoner_parameters","none")
  
  enableStyleShowGraph()
  showDiv("infferedTriples","none")   
   document.getElementById('graph_view_iframe').src = "chrome://reasonerextension/content/showGraph.html?graphName=current.json";
   
}

function removeDiscoveredTriple(){
  
  
  //send request to remove new triple to semantic turkey server
  var response = Reasoner.Requests.ReasoningService.removeTriple();
  //If remove operation fail, get the error and upadate the params window, else return true
  if (response.getElementsByTagName("reply")[0].getAttribute("status") == "fail") {
    var status = response.getElementsByTagName("removeTriple")[0].getAttribute("error");
    createAlert("Error: "+ status,"alert-danger");
  }
  else{
  createAlert("Triples removed","alert-success");
  window.location.reload()
  }
  
}