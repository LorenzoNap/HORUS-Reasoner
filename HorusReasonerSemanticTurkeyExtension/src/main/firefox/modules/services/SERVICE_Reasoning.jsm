Components.utils.import("resource://stmodules/Logger.jsm");

Components.utils.import("resource://reasonerExtensionModules/allReasonerRequest.jsm");

EXPORTED_SYMBOLS = [ "HttpMgr", "Requests" ];

var service = Requests.ReasoningService;
var serviceName = service.serviceName;


function startReasoning(params){
 var howManyTimesApllyInferenceRule = "howManyTimesApllyInferenceRule="+params.out.cycleNumber;
 var whicruleApply = "whicRulesApply="+params.out.whicruleApply;
 var produceOutput = "produceOutput="+params.out.outputValue;
 
	return HttpMgr.GET(serviceName,service.startReasoner,howManyTimesApllyInferenceRule,whicruleApply,produceOutput);
}

function searchTriple(subject,predicate,object){
  var tripleSubject = "subject="+subject;
  var triplePredicate = "predicate="+predicate;
  var tripleObject = "object="+object;
  return HttpMgr.GET(serviceName,service.searchNewTriple,tripleSubject,triplePredicate,tripleObject);
}

function removeTriple(){
  return HttpMgr.GET(serviceName,service.removeNewTriple);
}

function graphToJson(){
  return HttpMgr.GET(serviceName,service.getJsonGraph);
}

function searchTripleFromGraph(args) {
  var triple = "triple="+args
	 return HttpMgr.GET(serviceName,service.bfsGraph,triple);
}

function filterRules(rules){
    var rulesToFilter = "rules="+rules;
   return HttpMgr.GET(serviceName,service.applyFilterToOuptut,rulesToFilter);
}
service.startReasoning = startReasoning;
service.searchTriple = searchTriple;
service.removeTriple = removeTriple;
service.graphToJson = graphToJson;
service.searchTripleFromGraph = searchTripleFromGraph;
service.filterRules = filterRules;