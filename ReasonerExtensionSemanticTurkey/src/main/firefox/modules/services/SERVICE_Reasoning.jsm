Components.utils.import("resource://stmodules/Logger.jsm");

Components.utils.import("resource://reasonerExtensionModules/allReasonerRequest.jsm");

Components.utils.import("resource://stmodules/Context.jsm");

EXPORTED_SYMBOLS = [ "SemTurkeyHTTPLegacy", "Requests" ];

var service = Requests.ReasoningService;
var serviceName = service.serviceName;


function startReasoning(params){
	var howManyTimesApplyInferenceRule = "howManyTimesApplyInferenceRule="+params.iteration;
	var whichRuleApply = "whichRuleApply="+params.inferenceRules;
	var produceOutput = "produceOutput="+params.produceOutput;
	var contextAsArray = this.context.getContextValuesForHTTPGetAsArray();
	 
	return SemTurkeyHTTPLegacy.GET(serviceName, service.startReasoner, howManyTimesApplyInferenceRule,
			whichRuleApply, produceOutput, contextAsArray);
}

function searchTriple(subject,predicate,object){
	var tripleSubject = "subject="+subject;
	var triplePredicate = "predicate="+predicate;
	var tripleObject = "object="+object;
	var contextAsArray = this.context.getContextValuesForHTTPGetAsArray();
	return SemTurkeyHTTPLegacy.GET(serviceName, service.searchNewTriple, tripleSubject, triplePredicate,
			tripleObject, contextAsArray);
}

function removeTriple(){
	var contextAsArray = this.context.getContextValuesForHTTPGetAsArray();
	return SemTurkeyHTTPLegacy.GET(serviceName,service.removeNewTriple, contextAsArray);
}

function graphToJson(){
	var contextAsArray = this.context.getContextValuesForHTTPGetAsArray();
	return SemTurkeyHTTPLegacy.GET(serviceName,service.getJsonGraph, contextAsArray);
}

function searchTripleFromGraph(args) {
	var triple = "triple="+args
	var contextAsArray = this.context.getContextValuesForHTTPGetAsArray();
	return SemTurkeyHTTPLegacy.GET(serviceName, service.bfsGraph, triple, contextAsArray);
}

function filterRules(rules){
    var rulesToFilter = "rules="+rules;
    var contextAsArray = this.context.getContextValuesForHTTPGetAsArray();
    return SemTurkeyHTTPLegacy.GET(serviceName, service.applyFilterToOuptut, rulesToFilter, contextAsArray);
}

//this return an implementation for Reasoning with a specified context
service.prototype.getAPI = function(specifiedContext){
	var newObj = new service();
	newObj.context = specifiedContext;
	return newObj;
}
service.prototype.startReasoning = startReasoning;
service.prototype.searchTriple = searchTriple;
service.prototype.removeTriple = removeTriple;
service.prototype.graphToJson = graphToJson;
service.prototype.searchTripleFromGraph = searchTripleFromGraph;
service.prototype.filterRules = filterRules;
service.prototype.context = new Context();  // set the default context
service.constructor = service;
service.__proto__ = service.prototype;