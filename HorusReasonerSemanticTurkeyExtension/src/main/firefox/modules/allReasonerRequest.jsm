Components.utils.import("resource://stmodules/SemTurkeyHTTP.jsm");

EXPORTED_SYMBOLS = [ "HttpMgr", "Requests"];

/**
 * @class
 */
Requests = new Object();

Requests.ReasonerConfigurations = new Object();

Requests.ReasoningService = new Object();



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