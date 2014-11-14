var labelType, useGradients, nativeTextSupport, animate;
var jsonResponse =[];

if ("undefined" == typeof(Reasoner)) {
	var Reasoner = {};
};

Components.utils.import("resource://reasonerExtensionServices/SERVICE_Reasoning.jsm",Reasoner);

(function() {
	var ua = navigator.userAgent,
	iStuff = ua.match(/iPhone/i) || ua.match(/iPad/i),
	typeOfCanvas = typeof HTMLCanvasElement,
	nativeCanvasSupport = (typeOfCanvas == 'object' || typeOfCanvas == 'function'),
	textSupport = nativeCanvasSupport 
	&& (typeof document.createElement('canvas').getContext('2d').fillText == 'function');
	labelType = (!nativeCanvasSupport || (textSupport && !iStuff))? 'Native' : 'HTML';
	nativeTextSupport = labelType == 'Native';
	useGradients = nativeCanvasSupport;
	animate = !(iStuff || !nativeCanvasSupport);
})();

var Log = {
		elem: false,
		write: function(text){
			if (!this.elem) 
				this.elem = document.getElementById('log');
			//this.elem.innerHTML = text;
			//this.elem.style.left = (500 - this.elem.offsetWidth / 2) + 'px';
			  this.elem.innerHTML = "<image src='chrome://reasonerExtension/skin/images/graph_nodes.png'/>"+text;
   //this.elem.style.left = (100 - this.elem.offsetWidth / 2) + 'px';
		}
};



var fd;
var CUSTOM_NODE_TO_SHOW_GRAPH = "Show main graph";

function init(){
	
   var elem = document.getElementById('list_box_select');
	
   //this.elem.style.left = (500 - this.elem.offsetWidth / 2) + 'px';
 
 
  //  var response = Reasoner.Requests.ReasoningService.graphToJson();
  //  var string = response.getElementsByTagName("jsonGraph")[0].getAttribute("listOfVertices");

   var graphToLoad = document.URL
   graphToLoad = graphToLoad.split('graphName=')[1]
   if (graphToLoad != "default") {
       var response = Reasoner.Requests.GraphRequests.loadGraphRequest(graphToLoad)
       if (response.getElementsByTagName("reply")[0].getAttribute("status") == "warning") {
	  // alert("Error: "+response.getElementsByTagName("files")[0].getAttribute("jsonGraph"))
       }
       else if (response.getElementsByTagName("reply")[0].getAttribute("status") == "fail") {
	   alert("Error: "+response.getElementsByTagName("jsonGraph")[0].getAttribute("jsonGraph"))
       }
       else{
          var string = response.getElementsByTagName("jsonGraph")[0].getAttribute("jsonGraph")
          var json = JSON.parse(string);
	if(graphToLoad == "current.json"){
		
	  var response = Reasoner.Requests.ReasoningService.graphToJson();
	  var list_of_vertex = JSON.parse(response.getElementsByTagName("jsonGraph")[0].getAttribute("listOfVertices"));	
	  var rich_list_box = document.getElementById("vertex_list_box");
	  document.getElementById("listbox_vertex").setAttribute("style","margin: 0px auto; position: relative;display:block");	
		for (var i in list_of_vertex) {
		    var id = list_of_vertex[i].id;
		    var option = document.createElement("option");
		    option.text = id.replace("###filter###","");
		    option.value = id;
		    elem.add(option, null);
		}
	}
	else{
	  document.getElementById("listbox_vertex").setAttribute("style","display:none");
	}


  fd = new $jit.ForceDirected({  
  //id of the visualization container  
  injectInto: 'infovis',  
  //Enable zooming and panning  
  //with scrolling and DnD  
  Navigation: {  
    enable: true,  
    type: 'Native',  
    //Enable panning events only if we're dragging the empty  
    //canvas (and not a node).  
    panning: 'avoid nodes',  
    zooming: 10 //zoom speed. higher is more sensible  
  },  
  // Change node and edge styles such as  
  // color and width.  
  // These properties are also set per node  
  // with dollar prefixed data-properties in the  
  // JSON structure.  
  Node: {  
    overridable: true,  
    dim: 7  
  },  
  Edge: {  
    overridable: true,  
    color: '#23A4FF',  
    lineWidth: 0.4  
  },  
  // Add node events  
  Events: {  
    enable: true,  
    type: 'Native',  
    //Change cursor style when hovering a node  
    onMouseEnter: function() {  
      fd.canvas.getElement().style.cursor = 'move';  
    },  
    onMouseLeave: function() {  
      fd.canvas.getElement().style.cursor = '';  
    },  
    //Update node positions when dragged  
    onDragMove: function(node, eventInfo, e) {  
      var pos = eventInfo.getPos();  
      node.pos.setc(pos.x, pos.y);  
      fd.plot();  
    },  
    //Implement the same handler for touchscreens  
    onTouchMove: function(node, eventInfo, e) {  
      $jit.util.event.stop(e); //stop default touchmove event  
      this.onDragMove(node, eventInfo, e);  
    }  
  },  
  //Number of iterations for the FD algorithm  
  iterations: 200,  
  //Edge length  
  levelDistance: 130,  
  // This method is only triggered  
  // on label creation and only for DOM labels (not native canvas ones).  
  onCreateLabel: function(domElement, node){  
    // Create a 'name' and 'close' buttons and add them  
    // to the main node label  
    var nameContainer = document.createElement('span'),  
       // closeButton = document.createElement('span'),
			  closeButton = document.createElement('img');
				closeButton.setAttribute('scr','chrome://reasonerExtension/skin/images/Gear.png');
	style = nameContainer.style;  
    nameContainer.className = 'name';  
    nameContainer.innerHTML = node.name;  
    closeButton.className = 'close';  
    closeButton.innerHTML = '<img style="cursor:pointer;width: 15px;" src="chrome://reasonerExtension/skin/images/magnifying_glass.png"/>';  
    domElement.appendChild(nameContainer);  
    domElement.appendChild(closeButton);  
    style.fontSize = "0.8em";  
    style.color = "#272727";  
    //Fade the node and its connections when  
    //clicking the close button  
    closeButton.onclick = function() {  
      //node.setData('alpha', 0, 'end');  
      //node.eachAdjacency(function(adj) {  
      //  adj.setData('alpha', 0, 'end');  
      //});  
      //fd.fx.animate({  
      //  modes: ['node-property:alpha',  
      //          'edge-property:alpha'],  
      //  duration: 500  
      //});
			
			var response = Reasoner.Requests.ReasoningService.searchTripleFromGraph(node.id);
				var string = response.getElementsByTagName("bfsGraph")[0].getAttribute("json");
				jsonResponse =  JSON.parse(string);
				fd.loadJSON(jsonResponse);
				// compute positions incrementally and animate.
				fd.computeIncremental({
					iter: 40,
					property: 'end',
					onStep: function(perc){
						Log.write(perc + '% loaded...');
					},
					onComplete: function(){
						Log.write(' ');
						fd.animate({
							modes: ['linear'],
							transition: $jit.Trans.Elastic.easeOut,
							duration: 2500
						});
					}
				});
    };  
    //Toggle a node selection when clicking  
    //its name. This is done by animating some  
    //node styles like its dimension and the color  
    //and lineWidth of its adjacencies.  
    nameContainer.onclick = function() {  
      //set final styles  
      fd.graph.eachNode(function(n) {  
	if(n.id != node.id) delete n.selected;  
	n.setData('dim', 4, 'end');  
	n.eachAdjacency(function(adj) {  
	  adj.setDataset('end', {  
	    lineWidth: 0.4,  
	    color: '#23a4ff'  
	  });  
	});  
      });  
      if(!node.selected) {  
	node.selected = true;  
	node.setData('dim', 17, 'end');  
	node.eachAdjacency(function(adj) {  
	  adj.setDataset('end', {  
	    lineWidth: 3,  
	    color: '#36acfb'  
	  });  
	});  
      } else {  
	delete node.selected;  
      }  
      //trigger animation to final styles  
      fd.fx.animate({  
	modes: ['node-property:dim',  
		'edge-property:lineWidth:color'],  
	duration: 500  
      });  
      // Build the right column relations list.  
      // This is done by traversing the clicked node connections.  
      var html = "<h4>" + node.name + "</h4><b> connections:</b><ul><li>",  
	  list = [];  
      node.eachAdjacency(function(adj){  
	if(adj.getData('alpha')) list.push(adj.nodeTo.name);  
      });  
      //append connections information  
      $jit.id('inner-details').innerHTML = html + list.join("</li><li>") + "</li></ul>";  
    };  
  },  
  // Change node styles when DOM labels are placed  
  // or moved.  
  onPlaceLabel: function(domElement, node){  
    var style = domElement.style;  
    var left = parseInt(style.left);  
    var top = parseInt(style.top);  
    var w = domElement.offsetWidth;  
    style.left = (left - w / 2) + 'px';  
    style.top = (top + 10) + 'px';  
    style.display = '';
  }  
});  
// load JSON data.  
fd.loadJSON(json);  
// compute positions incrementally and animate.  
fd.computeIncremental({  
  iter: 40,  
  property: 'end',  
  onStep: function(perc){  
    Log.write(perc + '% loaded...');  
  },  
  onComplete: function(){  
    Log.write(' ');  
    fd.animate({  
      modes: ['linear'],  
      transition: $jit.Trans.Elastic.easeOut,  
      duration: 2500  
    });  
  }  
});
       }
	}
	
	

}

function searchTripleOnGraph(triple){
	
	
	var response = Reasoner.Requests.ReasoningService.searchTripleFromGraph(triple.value);
	var string = response.getElementsByTagName("bfsGraph")[0].getAttribute("json");
	jsonResponse =  JSON.parse(string);
	fd.loadJSON(jsonResponse);
	// compute positions incrementally and animate.
	fd.computeIncremental({
		iter: 40,
		property: 'end',
		onStep: function(perc){
			Log.write(perc + '% loaded...');
		},
		onComplete: function(){
			Log.write(' ');
			fd.animate({
				modes: ['linear'],
				transition: $jit.Trans.Elastic.easeOut,
				duration: 2500
			});
		}
	});
	
	
}


