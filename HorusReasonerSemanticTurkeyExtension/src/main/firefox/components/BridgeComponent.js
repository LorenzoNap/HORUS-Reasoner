/*
 * this code is borrowed and adapted from the original code, written by David Huynh, and downloadable from:
 * http://simile.mit.edu/wiki/Java_Firefox_Extension
 * for interfacing firefox with java
 */


/*----------------------------------------------------------------------
 * The Module (instance factory)
 *----------------------------------------------------------------------
 */

const Cc = Components.classes;
const Ci = Components.interfaces;
const Cr = Components.results;
const ctor = Components.Constructor;
const Exception = Components.Exception;
const module = Components.utils.import;
const error = Components.utils.reportError;


//Components.utils.import("resource://gre/modules/XPCOMUtils.jsm");
const NS_ERROR_NO_INTERFACE = Cr.NS_ERROR_NO_INTERFACE;
const NS_ERROR_FAILURE = Cr.NS_ERROR_FAILURE;
const NS_ERROR_NO_AGGREGATION = Cr.NS_ERROR_NO_AGGREGATION;
const NS_ERROR_INVALID_ARG = Cr.NS_ERROR_INVALID_ARG;

//We are using this to xpcom registration
module("resource://gre/modules/XPCOMUtils.jsm");

//  to be removed in FF4.0 port we are using XPCOMUtils.jsm to  get registered (FF3.6 and FF4.0)
//var BridgeModule = {
//
//     
//	/*
//	 * VERY IMPORTANT: Modify these first 3 fields to make them unique to your
//	 * components. Note that these fields have nothing to do with the extension
//	 * ID, nor the IDL interface IDs that the component implements. A component
//	 * can implement several interfaces.
//	 */
//	_myComponentID : Components.ID("{964308f5-dcf2-44fa-8bb1-af4c0555757f}"),
//	_myName : "The component for bridging to Java",
//	_myContractID : "@art.uniroma2.it/semanticturkey;1",
//
//	/*
//	 * This flag specifies whether this factory will create only a single
//	 * instance of the component.
//	 */
//	_singleton : true,
//	_myFactory : {
//		createInstance : function(outer, iid) {
//			if (outer != null) {
//				throw Components.results.NS_ERROR_NO_AGGREGATION;
//			}
//
//			var instance = null;
//
//			if (this._singleton) {
//				instance = this.theInstance;
//			}
//
//			if (!(instance)) {
//				instance = new BridgeComponent(); // BridgeComponent is
//													// declared below
//			}
//
//			if (this._singleton) {
//				this.theInstance = instance;
//			}
//
//			return instance.QueryInterface(iid);
//		}
//	},
//
//	registerSelf : function(compMgr, fileSpec, location, type) {
//		compMgr = compMgr
//				.QueryInterface(Components.interfaces.nsIComponentRegistrar);
//		compMgr.registerFactoryLocation(this._myComponentID, this._myName,
//				this._myContractID, fileSpec, location, type);
//	},
//
//	unregisterSelf : function(compMgr, fileSpec, location) {
//		compMgr = compMgr
//				.QueryInterface(Components.interfaces.nsIComponentRegistrar);
//		compMgr.unregisterFactoryLocation(this._myComponentID, fileSpec);
//	},
//
//	getClassObject : function(compMgr, cid, iid) {
//		if (cid.equals(this._myComponentID)) {
//			return this._myFactory;
//		} else if (!iid.equals(Components.interfaces.nsIFactory)) {
//			throw Components.results.NS_ERROR_NOT_IMPLEMENTED;
//		}
//
//		throw Components.results.NS_ERROR_NO_INTERFACE;
//	},
//
//	canUnload : function(compMgr) {
//		/*
//		 * Do any unloading task you want here
//		 */
//		return true;
//	}
//};

//To be changed in FF4.0 port
///*
// * This function NSGetModule will be called by Firefox to retrieve the module
// * object. This function has to have that name and it has to be specified for
// * every single .JS file in the components directory of your extension.
// */
//function NSGetModule(compMgr, fileSpec) {
//	return BridgeModule;
//}



 



// XPCOMUtils will generate a QueryInterface for us
///*
// * nsISupports.QueryInterface
// */
//BridgeComponent.prototype.QueryInterface = function(iid) {	 
//	/*
//	 * This code specifies that the component supports 2 interfaces:
//	 * nsISemanticTurkey and nsISupports.
//	 */
//	if (!iid.equals(Components.interfaces.nsISemanticTurkey)
//			&& !iid.equals(Components.interfaces.nsISupports)) {
//		throw Components.results.NS_ERROR_NO_INTERFACE;
//	}
//	return this;
//};

function BridgeComponent() {
	/*
	 * This is a XPCOM-in-Javascript trick: Clients using an XPCOM implemented
	 * in Javascript can access its wrappedJSObject field and then from there,
	 * access its Javascript methods that are not declared in any of the IDL
	 * interfaces that it implements.
	 * 
	 * Being able to call directly the methods of a Javascript-based XPCOM
	 * allows clients to pass to it and receive from it objects of types not
	 * supported by IDL.
	 */
	this.wrappedJSObject = this;
	this._initialized = false;
	this._packages = null;
}
BridgeComponent.prototype={
    //XPCOMUtils.jsm needs these 
    classDescription:"BridgeComponent for SemanticTurkey",
    contractID:"@art.uniroma2.it/semanticturkey;1",
    classID: Components.ID("{bd38239a-2ccb-4d06-ac61-fb3aa82cadea}"),    
    QueryInterface: XPCOMUtils.generateQI([Components.interfaces.nsISupport,Components.interfaces.nsISemanticTurkey]),
    _xpcom_categories: [{
        category: 'profile-after-change'
    }]
    
    
}
/*
 * Initializes this component, including loading JARs.
 */
BridgeComponent.prototype.initialize = function(packageLoader, trace) {
	if (this._initialized) {
		this._trace("BridgeComponent.initialize already called before");
		return true;
	}

	this._traceFlag = (trace);

	this._trace("initializing js-java BridgeComponent...");
	try {
		this._packageLoader = packageLoader;

		var extensionPath = this._getExtensionPath("semantic-turkey");

		/*
		 * Enumerate URLs to our JARs and class directories
		 */
//		var javaPath = extensionPath + "components/lib/";
//		var jarFilepaths = [
//		    				javaPath + "classes/", // semantic turkey classes, when deployed in a folder
//		    				javaPath + "resources/", // semantic turkey resources (ex: logj4 property file)
//		    				javaPath + "st-core-framework.jar", // semantic turkey classes, when embedded in a jar
//		    				javaPath + "org.apache.felix.main-2.0.1.jar",
//		    				javaPath + "google-collections-1.0-rc1.jar",
//		    				javaPath + "javax.servlet-5.1.12.jar",
//		    				javaPath + "jetty-5.1.10.jar",
//		    				javaPath + "log4j-1.2.14.jar",
//		    				javaPath + "owlart-api-2.0.2.jar",
//		    				javaPath + "secondstring-2006.06.15.jar",
//		    				javaPath + "servlet-api-2.4.jar",
//		    				javaPath + "slf4j-api-1.5.6.jar",
//		    				javaPath + "slf4j-log4j12-1.5.6.jar",
//		    				javaPath + "jcl-over-slf4j-1.5.6.jar",
//		    				javaPath + "json-20090211.jar"
//		];
		var ios = Components.classes["@mozilla.org/network/io-service;1"].  
        getService(Components.interfaces.nsIIOService);

		var extensionDirectory = ios.newURI(extensionPath, null, null).QueryInterface(Components.interfaces.nsIFileURL).file;
		
		var libDirectory = extensionDirectory.clone();
		libDirectory.append("components");
		libDirectory.append("lib");

		var jarFilepaths = [];

		var classesDirectory = libDirectory.clone();	// semantic turkey classes, when deployed in a folder
		classesDirectory.append("classes");

		var resourcesDirectory = libDirectory.clone();	// semantic turkey resources (ex: logj4 property file)
		resourcesDirectory.append("resources");

		jarFilepaths.push(ios.newFileURI(classesDirectory).spec);
		jarFilepaths.push(ios.newFileURI(resourcesDirectory).spec);

		var entries = libDirectory.directoryEntries;

		while (entries.hasMoreElements()) {
		    var e = entries.getNext().QueryInterface(Components.interfaces.nsILocalFile);
		    
		    if (e.isFile() && e.leafName.match(/\.jar$/) != null) {
		        jarFilepaths.push(ios.newFileURI(e).spec);
		    }
		}
		
		
		this._packages = this._packageLoader(jarFilepaths, this._traceFlag);

		/*
		 * Initialization through a static method
		 */
		this._trace("Initialize: "
			+ this._packages.getClass("it.uniroma2.art.semanticturkey.launcher.SemanticTurkeyLauncher").m("initialize")("")); // before Spring was (extensionPath) instead of ""

		/*
		 * Create a sample Java object
		 */
		// this._test =
		// this._packages.getClass("edu.mit.simile.javaFirefoxExtension.Test").n(42);
		// this._trace("Environment Variable PATH = " +
		// this._test.getEnvironmentVariable("PATH"));
		this._initialized = true;
	} catch (e) {
		this._fail(e);
		this._trace(this.error);
	}
	// NScarpato 28/04/2008 add start function for check repository and baseuri
	try {
		
		var stloader = Components.classes["@mozilla.org/moz/jssubscript-loader;1"].getService(Components.interfaces.mozIJSSubScriptLoader);
		this._trace("Java BridgeComponent: dynamically loading system-start.js");
		stloader.loadSubScript('chrome://semantic-turkey/content/system-start/system-start.js');
		this._trace("Java BridgeComponent: starting ST");
		//art_semanticturkey.startST();
		this._trace("Java BridgeComponent: ST started");

	} catch (e) {
		this._trace("javascript error during Bridge Component initialization procedure");
		this._fail(e);
		// art_semanticturkey.Logger.debug.printException(e);
		this._trace(this.error);
	}
	this._trace("} BridgeComponent.initialized");
	return this._initialized;
};

/*
 * Returns the packages of all the JARs that this component has loaded.
 */
BridgeComponent.prototype.getPackages = function() {
	this._trace("BridgeComponent.getPackages");
	return this._packages;
};

/*
 * Returns the Test object instantiated by default.
 */
BridgeComponent.prototype.getTest = function() {
	this._trace("BridgeComponent.getTest");
	return this._test;
};

BridgeComponent.prototype._fail = function(e) {
	if (e.getMessage) {
		this.error = e + ": " + e.getMessage() + "\n";
		while (e.getCause() != null) {
			e = e.getCause();
			this.error += "caused by " + e + ": " + e.getMessage() ;
		}
	} else {
		this.error = e;
	}
};

BridgeComponent.prototype._trace = function(msg) {
	Components.classes["@mozilla.org/consoleservice;1"]
	       			.getService(Components.interfaces.nsIConsoleService)
	       			.logStringMessage(msg);
};

/*
 * Get the file path to the installation directory of this extension.
 */
BridgeComponent.prototype._getExtensionPath = function(extensionName) {
	var chromeRegistry = Components.classes["@mozilla.org/chrome/chrome-registry;1"]
			.getService(Components.interfaces.nsIChromeRegistry);

	var uri = Components.classes["@mozilla.org/network/standard-url;1"]
			.createInstance(Components.interfaces.nsIURI);

	uri.spec = "chrome://" + extensionName + "/content/";

	var path = chromeRegistry.convertChromeURL(uri);
	if (typeof(path) == "object") {
		path = path.spec;
	}

	path = path.substring(0, path.indexOf("/chrome/") + 1);

	return path;
};

/*
 * Retrieve the file path to the user's profile directory. We don't really use
 * it here but it might come in handy for you.
 */
BridgeComponent.prototype._getProfilePath = function() {
	var fileLocator = Components.classes["@mozilla.org/file/directory_service;1"]
			.getService(Components.interfaces.nsIProperties);

	var path = escape(fileLocator.get("ProfD", Components.interfaces.nsIFile).path
			.replace(/\\/g, "/"))
			+ "/";
	if (path.indexOf("/") == 0) {
		path = 'file://' + path;
	} else {
		path = 'file:///' + path;
	}

	return path;
};


if (XPCOMUtils.generateNSGetFactory) {  //we have 2 entry points from FF depending on FF version
    var NSGetFactory = XPCOMUtils.generateNSGetFactory([BridgeComponent]); 
}//we are in FF4.0
else {
function NSGetModule() XPCOMUtils.generateModule([BridgeComponent]);
//
} //we are in FF3.6