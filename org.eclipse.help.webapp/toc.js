// Common scripts for IE and Mozilla.

var isMozilla = navigator.userAgent.toLowerCase().indexOf('mozilla') != -1 && parseInt(navigator.appVersion) >= 5;
var isIE = navigator.userAgent.toLowerCase().indexOf('msie') != -1;

/**
 * Returns the node with specified tag
 */
function getChildNode(parent, childTag)
{
	var list = parent.childNodes;
	if (list == null) return null;
	for (var i=0; i<list.length; i++)
		if (list.item(i).tagName == childTag)
			return list.item(i);
	return null;
}


/**
 * Returns the contained UL if any
 */

function getContentNode(node) {

  if (node.nodeType == 3)   //"Node.TEXT_NODE") 
    return getChildNode(node.parentNode.parentNode.parentNode, "UL");
  else if (node.tagName == "LI") 
    return getChildNode(node, "UL");
  else if (node.tagName == "A") 
    return getChildNode(node.parentNode, "UL");
  else if (node.tagName == "NOBR")
  	return getChildNode(node.parentNode.parentNode, "UL");
  else if (node.tagName == "UL") 
    return node;

  return null;
}

/**
 * Returns the anchor of this click
 * NOTE: MOZILLA BUG WITH A:focus and A:active styles
 */
function getAnchorNode(node) {
  if (node.nodeType == 3)  //"Node.TEXT_NODE") 
    //return getChildNode(node.parentNode.parentNode, "A");
	return node.parentNode.parentNode;
  else if (node.tagName == "NOBR")
  	return node.parentNode;
  else if (node.tagName == "LI") 
    return getChildNode(node, "A");
  else if (node.tagName == "A") 
    return node;
  else if (node.tagName == "UL")
  	return getChildNode(node.parentNode, "A");
  return null;
}

/**
 * Collapses a tree rooted at the specified element
 */
function collapse(node) {
  node.className = "collapsed";
}

/**
 * Expands a tree rooted at the specified element
 */
function expand(node) {
  node.className = "expanded";
}

/**
 * Returns true when this is an expanded tree node
 */
function isExpanded(node) {
  return node.className == "expanded";
}

/**
 * Returns true when this is a collapsed tree node
 */
function isCollapsed(node) {
  return  node.className == "collapsed";
}

// NOTE: MOZILLA BUG WITH A:focus and A:active styles
var oldActive;


/**
 * display topic label in the status line on mouse over topic
 */
function mouseMoveHandler(e) {
  var overNode;
  if (isMozilla)
  	overNode = e.target;
  else if (isIE)
   	overNode = window.event.srcElement;
  else 
  	return;
  	
  overNode = getAnchorNode(overNode);
  if (overNode == null)
   return;
 
  if (isMozilla)
     e.cancelBubble = false;
     
  window.status = overNode.firstChild.innerHTML;
}

/**
 * handler for expanding / collapsing topic tree
 */
function mouseClickHandler(e) {
  var clickedNode;
  if (isMozilla)
  	clickedNode = e.target;
  else if (isIE)
   	clickedNode = window.event.srcElement;
  else 
  	return;
  	
  var treeNode = getContentNode(clickedNode);

  if (treeNode != null && treeNode.parentNode.tagName != "BODY") {
    // mozilla adds styled margin on top of built-in margin.
    if (isMozilla) treeNode.style.marginLeft = -18;
    
    if (isCollapsed(treeNode)) {
   	 expand(treeNode);
  	}
  	else if (isExpanded(treeNode)) {
  	  collapse(treeNode);
 	}
  }
  
  highlightTopic(clickedNode);

  if (isMozilla)
  	e.cancelBubble = true;
}


// This takes *forever*
function expandAll() {
  var ulNodes = document.getElementsByTagName('ul');
  var max = ulNodes.length;
  for (var i = 0; i < max; i++) {
    var nodeObj = ulNodes.item(i);
    expand(nodeObj);
  }
}

/**
 * Expands the nodes from root to the specified node
 */
function expandPathTo(node)
{
	var parent = node.parentNode;
	if (parent == null)
		return;
	if (isCollapsed(parent))
		expand(parent);
	expandPathTo(parent);
}

/**
 * Highlights link
 */
function highlightTopic(topic)
{
  var a = getAnchorNode(topic); 
  if (a != null)
  {
  	a.className = "active";
  	if (oldActive && oldActive != a) {
    	oldActive.className="";
  	}
  	oldActive = a;
  }
}

/**
 * Selects a topic in the tree: expand tree and highlight it
 */
function selectTopic(topic)
{
	var links = document.getElementsByTagName("a");

	for (var i=0; i<links.length; i++)
	{
		if (topic == links[i].href)
		{
			expandPathTo(links[i]);
			highlightTopic(links[i]);
			return true;
		}
	}
	return false;
}

function adjustMargins()
{
	// little change for mozilla margins
	if (isMozilla)
	{
		var ul = document.body.childNodes;
		for (var i=0; i<ul.length; i++)
			if (ul[i].tagName == "UL")
				ul[i].style.marginLeft = -18;
	}
}
	
/**
 * Handles the onload event
 */
function onloadHandler(title)
{
	// little change for mozilla margins
	if (isMozilla)
		adjustMargins();
 		
	parent.tocTitle=title;
	if(parent.currentNavFrame=="toc")
	{
		parent.setToolbarTitle(title);
	}
}

// listen for clicks
if (isMozilla) {
  document.addEventListener('click', mouseClickHandler, true);
  document.addEventListener('mousemove', mouseMoveHandler, true);
}
else if (isIE){
  document.onclick = mouseClickHandler;
  document.onmousemove = mouseMoveHandler;
}
