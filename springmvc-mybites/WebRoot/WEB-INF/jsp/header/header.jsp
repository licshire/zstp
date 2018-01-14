<%@ page language="java" pageEncoding="utf-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<title>花非花的个人博客</title>
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/resources/main/css/index.css">
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/resources/main/css/base.css">
	<script type="text/javascript" src="<%=request.getContextPath() %>/resources/plugin/jquery/jquery-1.8.3.min.js"></script>
</head>
 
<body>
	<div class="topbgline"></div>
	<div class="logo"></div>
	<nav id="topnav">
	    <ul>
			<a href="/" title="首页" class="nav_first">首页</a>
            <a class="nav_1" href="/mood/index" title="心情随笔">心情随笔</a> 
            <a class="nav_2" href="/memory/index?tag=3" title="韶华追忆">韶华追忆</a> 
            <a class="nav_3" href="/oa/zstpView" title="知识图谱">知识图谱</a> 
            <!-- <a class="nav_22" href="/archives/index" title="个人归档">个人归档</a> 
            <a class="nav_18" href="/book/index" title="书屋">书屋</a>  -->
            <a class="nav_4" href="/index/message" title="对我说">对我说</a> 
            <a class="nav_5" href="/about/index" title="关于我">关于我</a> 
		</ul>
	</nav>
		
	<script type="text/javascript">
		var head=$(".logo").height();
		$(window).scroll(function(){
			var topScr=$(window).scrollTop();
			if (topScr>head) {
				$("#topnav").addClass("fixed");
			}else{
				$("#topnav").removeClass("fixed");
			}
		})
	</script>
</body>
</html>
