Components.utils.import("resource://stmodules/Logger.jsm");

Components.utils.import("resource://reasonerExtensionModules/allReasonerRequest.jsm");

EXPORTED_SYMBOLS = [ "HttpMgr", "Requests" ];

var service = Requests.ReasonerConfigurations;
var serviceName = service.serviceName;


function sendFileToReasoner(value,deafaultRule){
 if(value.length > 3719 && deafaultRule == 'false'){
	var count = value.length;
	var appendFirst = true;
	while(count > 3719){
	var substring = value.substring(0,2000);
	value = value.substring(2000,value.length);
	var file = "file=" + substring;
	var defaultInference;
	if (appendFirst == true) {
    defaultInference = "defaultRules="+"appendFirstTime";
		appendFirst = false;
	}else{
		defaultInference = "defaultRules="+"append";
	}
	
	var response = HttpMgr.GET(serviceName,service.loadFileRequest,file,defaultInference);
	count = count - 2000;
	if (count < 0) {
		return response;
	}
	}
	if (count > 0) {
		
	var file = "file=" + value;
	var defaultInference = "defaultRules="+"append";
	return HttpMgr.GET(serviceName,service.loadFileRequest,file,defaultInference);
	}
 }
 var file;
	if (deafaultRule == 'true') {
		file = "file=" + "";
	}
	else{
		 file = "file=" + value;
	}
 
	var defaultInference = "defaultRules="+deafaultRule;
	return HttpMgr.GET(serviceName,service.loadFileRequest,file,defaultInference);
}

function getInferenceRuleFileText(){
	return HttpMgr.GET(serviceName,service.loadInferenceRuleFileText,"");
}

function loadConfiguration(){
	return HttpMgr.GET(serviceName,service.loadReasonerConfiguration,"");
}

function startReasoning(params){
 var howManyTimesApllyInferenceRule = "howManyTimesApllyInferenceRule="+params.out.cycleNumber;
 var whicruleApply = "whicRulesApply="+params.out.whicruleApply;
 var produceOutput = "produceOutput="+params.out.outputValue;
 
	return HttpMgr.GET(serviceName,service.startReasoner,howManyTimesApllyInferenceRule,whicruleApply,produceOutput);
}



service.sendFileToReasoner = sendFileToReasoner;

service.getInferenceRuleFileText = getInferenceRuleFileText;

service.loadConfiguration = loadConfiguration;

service.startReasoning = startReasoning;