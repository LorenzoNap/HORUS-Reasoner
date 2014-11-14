Components.utils.import("resource://stmodules/Logger.jsm");

Components.utils.import("resource://reasonerExtensionModules/allReasonerRequest.jsm");

Components.utils.import("resource://stmodules/Context.jsm");

EXPORTED_SYMBOLS = [ "SemTurkeyHTTPLegacy", "Requests" ];

var service = Requests.ReasonerConfigurations;
var serviceName = service.serviceName;


function sendFileToReasoner(value,deafaultRule){
	if(value.length > 3719 && deafaultRule == 'false'){
		var count = value.length;
		var appendFirst = true;
		var contextAsArray = this.context.getContextValuesForHTTPGetAsArray();
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
			
			var response = SemTurkeyHTTPLegacy.GET(serviceName,service.loadFileRequest,file,defaultInference, 
					contextAsArray);
			count = count - 2000;
			if (count < 0) {
				return response;
			}
		}
		if (count > 0) {
			
			var file = "file=" + value;
			var defaultInference = "defaultRules="+"append";
			return SemTurkeyHTTPLegacy.GET(serviceName,service.loadFileRequest,file,defaultInference, 
					contextAsArray);
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
	return SemTurkeyHTTPLegacy.GET(serviceName,service.loadFileRequest,file,defaultInference);
}

function getInferenceRuleFileText(){
	var contextAsArray = this.context.getContextValuesForHTTPGetAsArray();
	return SemTurkeyHTTPLegacy.GET(serviceName,service.loadInferenceRuleFileText,"", contextAsArray);
}

function loadConfiguration(){
	var contextAsArray = this.context.getContextValuesForHTTPGetAsArray();
	return SemTurkeyHTTPLegacy.GET(serviceName,service.loadReasonerConfiguration,"", contextAsArray);
}




//this return an implementation for ReasonerConfiguration with a specified context
service.prototype.getAPI = function(specifiedContext){
	var newObj = new service();
	newObj.context = specifiedContext;
	return newObj;
}
service.prototype.sendFileToReasoner = sendFileToReasoner;
service.prototype.getInferenceRuleFileText = getInferenceRuleFileText;
service.prototype.loadConfiguration = loadConfiguration;
service.prototype.context = new Context();  // set the default context
service.constructor = service;
service.__proto__ = service.prototype;