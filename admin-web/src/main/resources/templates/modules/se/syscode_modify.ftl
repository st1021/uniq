<#include "/include/taglib.ftl" >
<html>
<head>
	<title>充电柜信息修改</title>
	
	<script type="text/javascript">
	
		$(document).ready(function() {
			
			$("#inputForm").validate({
				rules: {
					sysCode: {remote: "${ctx}/sys/sysCode/checkCode"}
				},
				messages: {
					sysCode: {
						remote:"<@spring.message code='nomo.alrightExit'></@spring.message>"
					}
				},    
				submitHandler: function(form){
					loading('<@spring.message code="form.submitInfo"></@spring.message>');
					form.submit();
				}
			});
		
		});
 		
 		
 		function addLatAndLon() {
			top.jBox("iframe:${ctx}/sys/seller/selectLonAndLat",{ 
                title: "<@spring.message code='nomo.selectLat'></@spring.message>",
                width: 900,
                height: 500,
                submit: function() {
	                var obj = $(parent.document.body).find('#jbox-iframe').contents().find('#google_location input');
	              	console.log(obj.val());
	              	var result = obj.val();
	              	
					$("#location_lat").val(result.split(":")[0]);
					$("#location_lon").val(result.split(":")[1]);
					$("#locationAddress").val(result.split(":")[2]);
					$("#lonAndLat").html(result.split(":")[2]);
                }
       		});
		}
		
	</script>
</head>
<body>
    <div class="page-container-custom">
        <div class="page-bar">
            <ul class="page-breadcrumb">
                <li>
                    <i class="icon-home"></i>
                    <a target="_parent" href="${ctx}"><@spring.message code="home"></@spring.message></a>
                    <i class="fa fa-angle-right"></i>
                </li>
                
                <li>
				    <span><@spring.message code="role.nomoBox"></@spring.message><i class="fa fa-angle-right"></i></span>
				</li>
                <li>
				    <span><@spring.message code="role.nomoBox"></@spring.message></span>
				</li>
				
            </ul>
        </div>
        <div class="portlet light ">
	       <ul class="nav nav-tabs mb-25">
	            <li >
 					<a  href="${ctx}/sys/cabinet/list"><@spring.message code="form.list"></@spring.message></a>
	            </li>
	            <li class="active">
					<a data-toggle="tab" href="javascript:void(0);">
						<#if info.id??>
								<@spring.message code="form.edit"></@spring.message>
							<#else>
								<@spring.message code="form.add"></@spring.message>
							</#if>
					</a>
	            </li>
	        </ul>
            <div class="portlet-body form">
			<@form.form id="inputForm" modelAttribute="info" action="${ctx}/sys/sysCode/saveOne" method="post" class="form-horizontal">
				<@form.hidden path="id"/>
				 <div class="form-body">
					<div class="form-group">
						<label class="col-md-3 control-label">BoxId :</label>
						<div class="col-md-4">
							<input type="text" name="sysCode" value="${info.sysCode!}" class="form-control"/>
						</div>
					</div>
					
					 
					<div class="form-actions">
						<div class="row">
							<div class="col-md-offset-3 col-md-4">
								<@shiro.hasPermission name="sys:inteRule:list">
									<input id="btnSubmit" class="btn btn-primary" type="submit" value="<@spring.message code="form.save"></@spring.message> "/>&nbsp;
								</@shiro.hasPermission>
								<input id="btnCancel" class="btn red" type="button" value="<@spring.message code="form.goback"></@spring.message>" onclick="history.go(-1)"/>
							</div>
						</div>
					</div>
				</div>
			</@form.form>
            </div>
        </div>
	</div>
</body>
</html>