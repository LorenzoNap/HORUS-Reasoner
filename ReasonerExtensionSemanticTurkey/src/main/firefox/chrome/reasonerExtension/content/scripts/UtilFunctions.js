function createAlert(text,type){
  bootstrap_alert = function() {}
    bootstrap_alert.warning = function(message) {
            $('#alert_placeholder').html('<div class="alert '+type+' alert-dismissable"><button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button><span>'+message+'</span></div>')
        }
    bootstrap_alert.warning(text);
}

function showDiv(id,attribute){
  document.getElementById(id).setAttribute("style","display:"+attribute)
}

function removeStyleToShowGraph() {
  document.getElementById("main_colum").setAttribute("style","display:")
  document.getElementById("main_div").setAttribute("style","display:")
}

function enableStyleShowGraph() {
  document.getElementById("main_colum").setAttribute("style","position: absolute; width: 100%; height: 100%;")
  document.getElementById("main_div").setAttribute("style","position:absolute;height:100%;width:100%")
  document.getElementById("graphView").setAttribute("style","position:absolute;height:100%;width:100%;")
}

function uploadFile() {
  //code
  var nsIFilePicker = Components.interfaces.nsIFilePicker;
  var fp = Components.classes["@mozilla.org/filepicker;1"].createInstance(nsIFilePicker);
  fp.init(window, "Select a File", nsIFilePicker.modeOpen);
  var res = fp.show();
  var fstream = Components.classes["@mozilla.org/network/file-input-stream;1"].createInstance(Components.interfaces.nsIFileInputStream);
  var sstream = Components.classes["@mozilla.org/scriptableinputstream;1"].createInstance(Components.interfaces.nsIScriptableInputStream);
  

  fstream.init(fp.file, 0x01, 00004, null);
  sstream.init(fstream);

  var output = sstream.read(sstream.available());

  sstream.close();
  fstream.close();
  
  return output;
}

function removeAllChildsOfDiv(id){
  var div = document.getElementById(id)
  while (div.hasChildNodes()) {
    div.removeChild(div.lastChild);
  }
}
function removeChildren (params){
    var parentId = params.parentId;
    var childName = params.childName;
    
    var childNodesToRemove = document.getElementById(parentId).getElementsByClassName(childName);
    for(var i=childNodesToRemove.length-1;i >= 0;i--){
        var childNode = childNodesToRemove[i];
        childNode.parentNode.removeChild(childNode);
    }
}