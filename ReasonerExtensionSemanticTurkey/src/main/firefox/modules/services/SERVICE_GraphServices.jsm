Components.utils.import("resource://stmodules/Logger.jsm");

Components.utils.import("resource://reasonerExtensionModules/allReasonerRequest.jsm");

Components.utils.import("resource://stmodules/Context.jsm");

EXPORTED_SYMBOLS = [ "SemTurkeyHTTPLegacy", "Requests" ];

var service = Requests.GraphRequests;
var serviceName = service.serviceName;

function loadGraphRequest(fileToLoad){
    var fileName = "fileName="+fileToLoad;
    var contextAsArray = this.context.getContextValuesForHTTPGetAsArray();
    return SemTurkeyHTTPLegacy.GET(serviceName,service.loadGraph,fileName, contextAsArray);
}

function loadListOfFile() {
	var contextAsArray = this.context.getContextValuesForHTTPGetAsArray();
    return SemTurkeyHTTPLegacy.GET(serviceName,service.loadListOfAviableGraph, contextAsArray);
}


//this return an implementation for GraphService with a specified context
service.prototype.getAPI = function(specifiedContext){
	var newObj = new service();
	newObj.context = specifiedContext;
	return newObj;
}
service.prototype.loadGraphRequest = loadGraphRequest;
service.prototype.loadListOfFile = loadListOfFile;
service.prototype.context = new Context();  // set the default context
service.constructor = service;
service.__proto__ = service.prototype;