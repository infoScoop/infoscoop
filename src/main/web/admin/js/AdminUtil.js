ISA_Admin.initIndicator = function() {
	var indicatorDiv = document.createElement("div");
	indicatorDiv.id = "indicatorDiv"
	document.body.appendChild(indicatorDiv);
	ISA_Admin.indicatorDiv = indicatorDiv;
	
	var overlay = document.createElement("div");
	overlay.className = "indicatorOverlay";
	overlay.id = "drag-overlay";
	indicatorDiv.appendChild(overlay);
	ISA_Admin.overlay = overlay;
	
	LoadingDiv = document.createElement("div");
	LoadingDiv.id = "divOverlay";
	LoadingDiv.className = "nowLoading";
	indicatorDiv.appendChild(LoadingDiv);
}

ISA_Admin.startIndicator = function() {
	var indicatorDiv = document.getElementById('indicatorDiv');
	if(!indicatorDiv)
		ISA_Admin.initIndicator();
	ISA_Admin.indicatorDiv.show();
}

ISA_Admin.stopIndicator = function() {
	if(!ISA_Admin.indicatorDiv)
		ISA_Admin.initIndicator();
	var indicatorDiv = ISA_Admin.indicatorDiv;
	indicatorDiv.hide();
}