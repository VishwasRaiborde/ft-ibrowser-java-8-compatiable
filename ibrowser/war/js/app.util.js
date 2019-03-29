var userFRs; 

function isNullOrWhiteSpace(str){
    return str === null || str.match(/^ *$/) !== null;
}

function  getHostNameURL() {
var port = window.location.port;
if (port != "80") {
	port = ":" + port;
}
 return window.location.protocol + "//" + window.location.hostname + port + "/";
 
}

function popup(url, showSave){
	if(showSave){
	 	$('#popup_save').show();
	} else {
	 	$('#popup_save').hide();
	}
	$('#popup_frame').unbind('load');
	$('#popup_frame').load(function(){
		$('#popup_frame').unbind('load');
 	 	$('#popup').dialog('open');
	 	$('#popup_frame').attr('src', url);
	});
	$('#popup_frame').attr('src',getHostNameURL() + 'loading.html');
	
	
	return false;
 }

 function closePopup(reload){
	 if (reload) {
		 loadTable();
	 }
	 
 	 $('#popup').dialog('close');
 }
 function save(){
	 document.getElementById('popup_frame').contentWindow.submitForm();
	// closePopup();
 }

 function loadTable() {
	 
 }
 
 $(function() {
		$( "#popup" ).dialog({ 
				autoOpen: false, 
				resizable: false,
				width: 1035,
				resizeStop: function( event, ui ) {
					$( "#popup_frame" ).width(ui.size.width-4);
					$( "#popup_frame" ).height(ui.size.height-62);
				},
				open: function (event, ui) {
					var h = $(window).height()*0.9;
			        $(this).height(h);
					$( "#popup_frame" ).height(h-62);
				    $(this).dialog({position: "center"});
					$('#popup').css('overflow', 'hidden'); 
				}
			});

	 });
 
 
 $(function() {
		$( "#popup" ).dialog({ 
				autoOpen: false, 
				resizable: false,
				width: 1035,
				resizeStop: function( event, ui ) {
					$( "#popup_frame" ).width(ui.size.width-4);
					$( "#popup_frame" ).height(ui.size.height-62);
				},
				open: function (event, ui) {
					var h = $(window).height()*0.9;
			        $(this).height(h);
					$( "#popup_frame" ).height(h-62);
				    $(this).dialog({position: "center"});
					$('#popup').css('overflow', 'hidden'); 
				}
			});

	 });
 
 /*
  * menu item on click change   
  */
 $(function(){
   $('#nav li a').on('click', function(){
		$('#nav li a').removeClass("selected");
		$(this).addClass('selected');
	});
 });
 
 function getDateToString(createdDate) {
	var date = new Date(createdDate);
	date.setHours(0, 0, 0, 0);
	var today = new Date();
	today.setHours(0, 0, 0, 0);
	var yesterday = new Date();
	yesterday.setDate(yesterday.getDate() - 1);
	yesterday.setHours(0, 0, 0, 0);

	var weekStart  = new Date();
	weekStart.setDate(weekStart.getDate() - weekStart.getDay());
	weekStart.setHours(0, 0, 0, 0);
	
	var weekEnd  = new Date();
	weekEnd.setDate(weekStart.getDate() - weekStart.getDay());
	weekEnd.setDate(weekStart.getDate() + 6);
	weekEnd.setHours(23, 59, 59, 999);
	
	if (date != null) {
		if (date.getTime() == today.getTime()) {
			return "Today";
		} else if (date.getTime() == yesterday.getTime()) {
			return "Yesterday";
		} else if (date.getTime() <= weekEnd.getTime() && date.getTime() >= weekStart.getTime()){
			return "This week";
		} else {
			return getStartDate(date);
		}
	}
 }
 
 /* 
  * convert dd-MM-yyyy (1/1/2014) to (01/01/2014)
 */
 function getStartDate(startDate){
		var date = new Date(startDate);
		var dd = date.getDate();
		var mm = date.getMonth()+1;
		var yyyy = date.getFullYear();
		if(dd < 10)
	    {
	        dd = '0'+dd;
	    }
	    if(mm < 10)
	    {
	        mm = '0'+mm;
	    }
	    return  dd+'/'+ mm+'/'+yyyy;	    
	}
 
 
/* Get Favourite Reports */
 
 function getFavouriteReports(){
	$.ajax({
		url: '/app/favouriteReport',
		type: 'GET',
		contentType: 'text/json',
		success: function(data) {
		userFRs = data;
		 $('#favourite_reports').html('');
		 $('#favourite_reports').append('<li class="side_nav_title"> My favourite reports </li>');
			if (data!=null && data.length > 0) {
				$.each(data, function(index, item) {
				 var row = '<li id="fav_report"><a href="/app/view-report/'+item.reportKey+'?group_code='+item.groupCode+'" target="_blank" >' + item.title + " ("+ item.groupName + ")" + ' </a><span id="'+item.reportKey+"|"+item.groupCode+'" onclick="removeFavReport(this)" class="hide_remove_fav_report">x</span></li>';
					 $('#favourite_reports').append(row);
						});
				  }else{
						$('#favourite_reports').append('<li id="report_menu"><a style="text-decoration:none"> Star some reports to add them to this list</a> </li>');
				}
			}
	});
} 
 
 function removeFavReport(obj) {
	   var idParams  = obj.id.split('|');
	   var favouriteReportKeyVal = idParams[0];
	   var groupCodeVal = idParams[1];
	   
	 	$.ajax({
			url: '/app/favouriteReport',
			type: 'POST',
		    data: JSON.stringify({key: favouriteReportKeyVal, groupCode : groupCodeVal,isFavourite : false}),
			contentType: 'text/json',
			success: function(data) {
				location.reload();
			}
		});
}
 
 $(document).on('mouseover', '#fav_report', function() {
	 $(this).children('span').get(0).className='show_remove_fav_report label label-default';
	 
	});
	$(document).on('mouseout', '#fav_report', function() {
		$(this).children('span').get(0).className='hide_remove_fav_report';		
});
