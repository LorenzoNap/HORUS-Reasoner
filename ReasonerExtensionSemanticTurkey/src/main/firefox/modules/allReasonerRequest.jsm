Components.utils.import("resource://stmodules/SemTurkeyHTTPLegacy.jsm");

EXPORTED_SYMBOLS = [ "SemTurkeyHTTPLegacy", "Requests"];

/**
 * @class
 */
Requests = function(){};

Requests.ReasonerConfigurations = function(){};

Requests.ReasoningService = function(){};

Requests.GraphRequests = function(){};



//REASONER CONFIGURATION REQUEST
Requests.ReasonerConfigurations.serviceName = "reasonerService";

Requests.ReasonerConfigurations.loadFileRequest = "loadFileRequest";

Requests.ReasonerConfigurations.loadInferenceRuleFileText = "showInferenceRules";

Requests.ReasonerConfigurations.loadReasonerConfiguration = "loadReasonerConfiguration";

Requests.ReasonerConfigurations.startReasoner = "startReasoning";



//REASONING REQUEST
Requests.ReasoningService.serviceName = "reasoningService";

Requests.ReasoningService.startReasoner = "startReasoning";

Requests.ReasoningService.searchNewTriple = "searchNewTriple";

Requests.ReasoningService.removeNewTriple = "removeNewTriple";

Requests.ReasoningService.getJsonGraph = "getJsonGraph";

Requests.ReasoningService.bfsGraph= "searchTripleFromGraph";

Requests.ReasoningService.applyFilterToOuptut = "applyFilterToOuptut";


//GRAPH REQUEST
Requests.GraphRequests.serviceName ="reasonerGraphService"
Requests.GraphRequests.loadGraph = "loadGraphRequest" 
Requests.GraphRequests.loadListOfAviableGraph = "loadListOfAviableGraph"