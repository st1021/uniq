<#include "/include/taglib.ftl" >
<html>
<head>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#inputForm").validate({
				rules: {
					amount: {required:true,digits:true,min:1},
					currency: {required:true},
					dayLimit: {digits:true},
					name: {remote: "${ctx}/sys/pro/checkName?id="+$('#id').val()}
				},
				messages: {
					amount:{ required:"please enter	"},
					currency:{ required:"please choose"},
					dayLimit: {required:"please enter"},
					name: {remote:" name already exists"}
				},    
				submitHandler: function(form){
					loading('<@spring.message code="form.submitInfo"></@spring.message>');
					form.submit();
				}
			});
		});
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
				    <span><@spring.message code="marketing.activityManagement"></@spring.message><i class="fa fa-angle-right"></i></span>
				</li>
                <li>
                    <span><@spring.message code="marketing.couponManagement"></@spring.message><i class="fa fa-angle-right"></i></span>
                </li>
                <@shiro.hasPermission name="sys:promotion:list">
	                <li>
	                    <span>
	                    	<#if obj.id??>
								<@spring.message code="form.edit"></@spring.message>
							<#else>
								<@spring.message code="form.add"></@spring.message>
							</#if>
	                    </span>
	                </li>
                </@shiro.hasPermission>
            </ul>
        </div>
        <div class="portlet light ">
	       <ul class="nav nav-tabs mb-25">
	            <li >
 					<a href="${ctx}/sys/pro/listCoupon"><@spring.message code="form.list"></@spring.message></a>
	            </li>
	            <li class="active">
					<a data-toggle="tab" href="javascript:void(0);">
						<#if obj.id??>
							<@spring.message code="form.edit"></@spring.message>
						<#else>
							<@spring.message code="form.add"></@spring.message>
						</#if>
					</a>
	            </li>
	        </ul>
            <div class="portlet-body form">
			<@form.form id="inputForm" modelAttribute="obj" action="${ctx}/sys/pro/saveCoupon" method="post" class="form-horizontal">
				<@form.hidden path="id"/>
				<@tags.message content=message! />
				 <div class="form-body">
				 	<div class="form-group">
						<label class="col-md-3 control-label"><@spring.message code="marketing.name"></@spring.message>:</label>
						<div class="col-md-4">
							<input type="text" name="name" value="${obj.name!' '}" maxlength="50"  class="form-control required"/>
						</div>
					</div>
					
				 	<div class="form-group">
						<label class="col-md-3 control-label"><@spring.message code="marketing.amount"></@spring.message>:</label>
						<div class="col-md-4">
							<input type="text" name="amount" value="${obj.amount!'1'}" maxlength="5"  class="form-control number required"/>
						</div>
					</div>
					
					<div class="form-group">
						<label class="col-md-3 control-label"><@spring.message code="marketing.currency"></@spring.message>:</label>
						<div class="col-md-4">
							<select id="currency" name="currency" class="select2 form-control input-small required">
								<#list currencies as currency>
                            	<#if obj.currency?? && obj.currency == currency>
								<option selected="selected" value="${currency}">${currency}</option>
								<#else>
								<option value="${currency}">${currency}</option>								
								</#if>
								</#list>
							</select>
						</div>
					</div>
					
					<div class="form-group">
						<label class="col-md-3 control-label"><@spring.message code="marketing.validity"></@spring.message>:</label>
						<div class="col-md-4">
							<input type="text" name="dayLimit" value="${obj.dayLimit!'1'}" maxlength="5" class="form-control number required"/>
						</div>
						<label class="control-label"><@spring.message code="marketing.day"></@spring.message></label>
					</div>
					
					<div class="form-actions">
						<div class="row">
							<div class="col-md-offset-3 col-md-4">
								<@shiro.hasPermission name="sys:coupon:addCoupon">
									<input id="btnSubmit" class="btn btn-primary" type="submit" value="<@spring.message code="form.save"></@spring.message>"/>&nbsp;
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