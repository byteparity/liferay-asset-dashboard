<%@ page import="com.byteparity.assetsstatistics.constants.LiferayAssetsStatisticsPortletKeys"%>

<script type="text/javascript">

AUI().ready('liferay-portlet-url', function(A) {
	$(".portlet-title-text.portlet-title-editable").empty();
	getAssetsList();
});

function getAssetsList(){
	addSpinner($('#tableDiv'));
	$.ajax({
		url :"${getAssetsListURL}",
		success: function(data){
			var content= JSON.parse(data);
		    $("#dashboard").empty();
		    removeSpinner($('#tableDiv'));
		    jQuery(content).each(function(index, jsonObject){
		    	var index = 0;
		    	if(Object.keys(jsonObject).length > 0){
		    		$.each(jsonObject, function(key,val){
			    		if(key != 'true'){
				    		var resourceURL = Liferay.PortletURL.createResourceURL();
				            resourceURL.setDoAsGroupId('true');
				            resourceURL.setPortletId('<%= LiferayAssetsStatisticsPortletKeys.PORTLET_ID %>');
				            if(key == "Blog"){
				            	resourceURL.setResourceId('/get_blogs');	
				            }else if(key == "Forms"){
				            	resourceURL.setResourceId('/get_forms');
				            }else if(key == "Wiki"){
				            	resourceURL.setResourceId('/get_wikies');
				            }else if(key == "Document & Media"){
				            	resourceURL.setResourceId('/get_document_media');
				            }else if(key == "Web Content"){
				            	resourceURL.setResourceId('/get_webcontent');
				            }else if(key == "Users"){
				            	resourceURL.setResourceId('/get_users');
				            }
					    	createAssetsDisplayCards(index,key,val,resourceURL);
					    	index ++;
				    	}else{
				    		$(".alert.alert-info").text("Please select assets from configuration.");
				    	}
			        });
		    	} else {
		    		$(".alert.alert-info").text("Please select assets from configuration.");
		    	}
		    	
		    }); 
		    return false;	
		}
	});
}

function createAssetsDisplayCards(index,key,val,resourceURL){
	 
	var aLink = document.createElement('a');
	aLink.href = "javascript:void(0);";
	aLink.id = "card-"+index;
	aLink.onclick = function(){
		getAssetsStatistics(resourceURL,index);
	}
	
	var mainDiv = document.createElement('div');
	mainDiv.className = "col-md-4 col-sm-6 col-xs-12";
	
	var cardDiv = document.createElement('div');
	cardDiv.className = "asset-card card";
				    		
	var headerDiv = document.createElement('div');
	headerDiv.className = "head bg-success";
	headerDiv.innerHTML = "<h2>"+key+"</h2>";
	
	var containtDiv = document.createElement('div');
	containtDiv.className = "containt";
	containtDiv.innerHTML = "<p>"+ val + "</p>";
	
	cardDiv.appendChild(headerDiv);
	cardDiv.appendChild(containtDiv);
	mainDiv.appendChild(cardDiv);
	aLink.appendChild(mainDiv);
	
	$("#dashboard").append(aLink);
}

function getAssetsStatistics(resourceURL,index){
	
	$(".asset-card").removeClass("activeCard");
	$(".head").removeClass("bg-dark");
	$("#card-"+index+" .asset-card").addClass("activeCard");
	$("#card-"+index+" .asset-card .head").addClass("bg-dark");
	
	$("#tableDiv").empty();
	$("#headerDiv").empty();
	
	addSpinner($('#tableDiv'));
	
	if(resourceURL != ""){
		$.ajax({
			"url": resourceURL,
			contentType: false,
		    processData: false,
			cache: false,
	        "success": function(json) {
	        	removeSpinner($('#tableDiv'));
	        	$(".alert.alert-info").addClass("hide");
	        	$("#headerDiv").empty();
	        	var headerDiv = document.createElement('div');
	        	var line = document.createElement('hr');
	        	
	        	if(json.header.status != null){
	        		headerDiv.appendChild(line);
	        	}
	        	$( "#headerDiv" ).append(headerDiv);
	        	if(json.header.status != null){
	        		$.each(json.header.status, function(i, val){
	            		$( "#headerDiv" ).append(val);
	            	});
	        	}
	        	
	           var tableHeaders = "";
	           $.each(json.fields, function(i, val){
	                tableHeaders += "<th>" + val + "</th>";
	           });
	        	$("#tableDiv").empty();
	      		$("#tableDiv").append('<table id="displayTable" class="display responsive nowrap" cellspacing="0" width="100%"><thead><tr>' + tableHeaders + '</tr></thead></table>');	                	 
	           	$('#displayTable').dataTable({
	           		data: json.content,
	           		"scrollX": true,
	           		"autoWidth" : true,
	           		"processing": true,
	           	 	"language": {
			            "emptyTable": "No data found :(",
			            "sLoadingRecords" : '<span style="width:100%;"><img src="http://www.snacklocal.com/images/ajaxload.gif"></span>'
			        },
	           	 	select: true,
		           	columnDefs: [
		            	{ targets: "Rating", width: '100px' },
		            	{
		            		targets: 1,
		                	render: function ( data, type, row) {
		                		if(data.startsWith("<img src=")){
		                			return data;
		                		} else {
		                			if(data.length > 20){
			                			return '<span style="cursor:pointer" title="'+data+'">'+data.substr( 0, 20 ) + "..."+'</span>';	
			                		}else{
			                			return '<span style="cursor:pointer" title="'+data+'">'+data+'</span>';
			                		}		                			
		                		}  
		                	}
		            	}
		            ],
		            createdRow: function(row, data, dataIndex){
		                initRating(row);
		            } 
	      		});
	        }, 
	        "dataType": "json"
		});
	}else{
		$(".alert.alert-info").removeClass("hide");
		$("#headerDiv").empty();
		$("#tableDiv").empty();
	} 
}

//
//Initializes jQuery Raty control
//
function initRating(container){
	 $('span.rating', container).raty({
	     half: true,
	     starHalf: '<%=request.getContextPath()%>/images/star-half.png',
	     starOff: '<%=request.getContextPath()%>/images/star-off.png',
	     starOn: '<%=request.getContextPath()%>/images/star-on.png',
	     readOnly: true,
	     score: function() {
	         return $(this).attr('data-score');
	     }
	 });
} 
</script>
