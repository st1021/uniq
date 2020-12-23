<#include "/include/taglib.ftl" >
<html>
<head>
	<title></title>
	<script type="text/javascript">
	function page(n,s){
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		$("#searchForm").attr("action","${ctx}/sys/pro/listCoupon").submit();
    	return false;
	}
	
	function del(id){
    	 top.jBox.confirm('<@spring.message code="sys.AreYouSureYouWantToDeleteTheRecord"></@spring.message>', '<@spring.message code="sys.prompt"></@spring.message>', function (v, h, f) {
           if (v == true) {
            	  loading('Removing, please wait ...');
            	  $.ajax({
		                 	url:"${ctx}/sys/pro/delCoupon?cid="+id,
		                 	type:"POST",
		                 	success:function(data){
		                 		top.$.jBox.tip("Congratulations, delete successfully");
		                 		window.location.href="${ctx}/sys/pro/listCoupon";
		                 	}
		          	 });
            }
            return true;
		 }, { buttons: { 'Yes': true, 'No': false} });
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
				    <span><@spring.message code="marketing.marketingManagement"></@spring.message><i class="fa fa-angle-right"></i></span>
				</li>
                <li>
                    <span><@spring.message code="marketing.couponManagement"></@spring.message></span>
                </li>
            </ul>
        </div>
        
        <div class="portlet light portlet-fit portlet-datatable ">
	       <ul class="nav nav-tabs mb-25">
	            <li class="active">
 					<a data-toggle="tab" href="javascript:;"><@spring.message code="form.list"></@spring.message></a>
	            </li>
	            <@shiro.hasPermission name="sys:coupon:addCoupon">
	            <li >
	                <a href="${ctx}/sys/pro/addCoupon"><@spring.message code="form.add"></@spring.message></a>
	            </li>
	            </@shiro.hasPermission>
	        </ul>
			<form id="searchForm"  action="${ctx}/sys/pro/listCoupon" method="post" >
					<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
					<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			</form>
        <#if (page.content?size > 0)>
	        <div class="portlet-body">
	            <div id="sample_4_wrapper" class="dataTables_wrapper">
				<table id="contentTable" class="table table-striped table-bordered table-hover table-checkable order-column dataTable">
					<tr>
						<th><@spring.message code="marketing.name"></@spring.message></th>
						<th><@spring.message code="value"></@spring.message></th>
						<th><@spring.message code="marketing.validity"></@spring.message></th>
						<th><@spring.message code="nomo.createTime"></@spring.message></th>
			            <th><@spring.message code="form.operation"></@spring.message></th>
					</tr>
					<#list page.content as obj>
						<tr>
							<td>${obj.name!''}</td>
							<td>${obj.currency!} ${obj.amount!''}</td>
							<td>${obj.dayLimit!''} <@spring.message code="marketing.day"></@spring.message> </td>
							<td>${(obj.createTime?string('yyyy-MM-dd HH:mm:ss'))!''}</td>
							 <td>
						 		<@shiro.hasPermission name="sys:coupon:addCoupon">
						 		<a  href="${ctx}/sys/pro/addCoupon?cid=${obj.id!}"><@spring.message code="form.edit"></@spring.message></a>
						 		</@shiro.hasPermission>
						 		<@shiro.hasPermission name="sys:coupon:sendCoupon"> 
						 		<a  href="${ctx}/sys/pro/sendCoupon?cid=${obj.id!}"><@spring.message code="marketing.issue"></@spring.message></a>
						 		</@shiro.hasPermission>
						 		<@shiro.hasPermission name = "sys:coupon:delCoupon">
						 			<a  href="javascript:del(${obj.id!});"><@spring.message code="form.delete"></@spring.message></a>
						 		</@shiro.hasPermission>
				            </td>
						</tr>
					</#list>
				</table>
				 <div class="pagination">
			        ${page}
			    </div>
			    <#else>
					<div class="note note-warning alert"><@spring.message code="noRecords"/></div>
				</#if>
			</div>
        </div>
    </div>
</body>
</html>