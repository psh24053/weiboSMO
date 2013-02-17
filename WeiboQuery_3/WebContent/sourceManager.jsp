<%@page import="com.psh.query.model.LocalQueryTaskModel"%>
<%@page import="java.util.List"%>
<%@page import="com.psh.query.model.LocalUserQueryTaskModel"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<!-- JQuery UI JSLib -->
<script type="text/javascript" src="json2.js"></script>
<script type="text/javascript" src="jquery-1.8.1.min.js"></script>
<script type="text/javascript" src="jqueryui/js/jquery-ui-1.9.2.custom.min.js"></script>
<!-- JqGrid lib -->
<script type="text/javascript" src="js/ajax.js"></script>

<link rel="stylesheet" type="text/css" href="jqueryui/css/smoothness/jquery-ui-1.9.2.custom.min.css" />
<link type="text/css" rel="stylesheet" href="css/ui.jqgrid.css" />

<script type="text/javascript" src="js/weibo.Dialog.js"></script>
<script type="text/javascript">

function changeCity(){
	
	if($("#provSelect").val() == 34){
		$("#citySelect").html("");
		$("#citySelect").append('<option value="1000">不限</option>');
		$("#citySelect").append('<option value="1">合肥</option>');
		$("#citySelect").append('<option value="2">芜湖</option>');
		$("#citySelect").append('<option value="3">蚌埠</option>');
		$("#citySelect").append('<option value="4">淮南</option>');
		$("#citySelect").append('<option value="5">马鞍山</option>');
		$("#citySelect").append('<option value="6">淮北</option>');
		$("#citySelect").append('<option value="7">铜陵</option>');
		$("#citySelect").append('<option value="8">安庆</option>');
		$("#citySelect").append('<option value="10">黄山</option>');
		$("#citySelect").append('<option value="11">滁州</option>');
		$("#citySelect").append('<option value="12">阜阳</option>');
		$("#citySelect").append('<option value="13">宿州</option>');
		$("#citySelect").append('<option value="14">巢湖</option>');
		$("#citySelect").append('<option value="15">六安</option>');
		$("#citySelect").append('<option value="16">亳州</option>');
		$("#citySelect").append('<option value="17">池州</option>');
		$("#citySelect").append('<option value="18">宣城</option>');
		
	}else if($("#provSelect").val() == 11){
		$("#citySelect").html("");
		$("#citySelect").append('<option value="1000">不限</option>');
		$("#citySelect").append('<option value="1">东城区</option>');
		$("#citySelect").append('<option value="2">西城区</option>');
		$("#citySelect").append('<option value="3">崇文区</option>');
		$("#citySelect").append('<option value="4">宣武区</option>');
		$("#citySelect").append('<option value="5">朝阳区</option>');
		$("#citySelect").append('<option value="6">丰台区</option>');
		$("#citySelect").append('<option value="7">石景山区</option>');
		$("#citySelect").append('<option value="8">海淀区</option>');
		$("#citySelect").append('<option value="9">门头沟区</option>');
		$("#citySelect").append('<option value="11">房山区</option>');
		$("#citySelect").append('<option value="12">通州区</option>');
		$("#citySelect").append('<option value="13">顺义区</option>');
		$("#citySelect").append('<option value="14">昌平区</option>');
		$("#citySelect").append('<option value="15">大兴区</option>');
		$("#citySelect").append('<option value="16">怀柔区</option>');
		$("#citySelect").append('<option value="17">平谷区</option>');
		$("#citySelect").append('<option value="28">密云县</option>');
		$("#citySelect").append('<option value="29">延庆县</option>');
		
	}else if($("#provSelect").val() == 50){
		$("#citySelect").html("");
		$("#citySelect").append('<option value="1000">不限</option>');
		$("#citySelect").append('<option value="1">万州区</option>');
		$("#citySelect").append('<option value="2">涪陵区</option>');
		$("#citySelect").append('<option value="3">渝中区</option>');
		$("#citySelect").append('<option value="4">大渡口区</option>');
		$("#citySelect").append('<option value="5">江北区</option>');
		$("#citySelect").append('<option value="6">沙坪坝区</option>');
		$("#citySelect").append('<option value="7">九龙坡区</option>');
		$("#citySelect").append('<option value="8">南岸区</option>');
		$("#citySelect").append('<option value="9">北碚区</option>');
		$("#citySelect").append('<option value="10">万盛区</option>');
		$("#citySelect").append('<option value="11">双桥区</option>');
		$("#citySelect").append('<option value="12">渝北区</option>');
		$("#citySelect").append('<option value="13">巴南区</option>');
		$("#citySelect").append('<option value="14">黔江区</option>');
		$("#citySelect").append('<option value="15">长寿区</option>');
		$("#citySelect").append('<option value="22">綦江县</option>');
		$("#citySelect").append('<option value="23">潼南县</option>');
		$("#citySelect").append('<option value="24">铜梁县</option>');
		$("#citySelect").append('<option value="25">大足县</option>');
		$("#citySelect").append('<option value="26">荣昌县</option>');
		$("#citySelect").append('<option value="27">璧山县</option>');
		$("#citySelect").append('<option value="28">梁平县</option>');
		$("#citySelect").append('<option value="29">城口县</option>');
		$("#citySelect").append('<option value="30">丰都县</option>');
		$("#citySelect").append('<option value="31">垫江县</option>');
		$("#citySelect").append('<option value="32">武隆县</option>');
		$("#citySelect").append('<option value="33">忠县</option>');
		$("#citySelect").append('<option value="34">开县</option>');
		$("#citySelect").append('<option value="35">云阳县</option>');
		$("#citySelect").append('<option value="36">奉节县</option>');
		$("#citySelect").append('<option value="37">巫山县</option>');
		$("#citySelect").append('<option value="38">巫溪县</option>');
		$("#citySelect").append('<option value="40">石柱土家族自治县</option>');
		$("#citySelect").append('<option value="41">秀山土家族苗族自治县</option>');
		$("#citySelect").append('<option value="42">酉阳土家族苗族自治县</option>');
		$("#citySelect").append('<option value="43">彭水苗族土家族自治县</option>');
		$("#citySelect").append('<option value="81">江津区</option>');
		$("#citySelect").append('<option value="82">合川市</option>');
		$("#citySelect").append('<option value="83">永川区</option>');
		$("#citySelect").append('<option value="84">南川市</option>');
		
	}else if($("#provSelect").val() == 35){
		
		$("#citySelect").html("");
		$("#citySelect").append('<option value="1000">不限</option>');
		$("#citySelect").append('<option value="1">福州</option>');
		$("#citySelect").append('<option value="2">厦门</option>');
		$("#citySelect").append('<option value="3">莆田</option>');
		$("#citySelect").append('<option value="4">三明</option>');
		$("#citySelect").append('<option value="5">泉州</option>');
		$("#citySelect").append('<option value="6">漳州</option>');
		$("#citySelect").append('<option value="7">南平</option>');
		$("#citySelect").append('<option value="8">龙岩</option>');
		$("#citySelect").append('<option value="9">宁德</option>');
		
	}else if($("#provSelect").val() == 62){
		
		$("#citySelect").html("");
		$("#citySelect").append('<option value="1000">不限</option>');
		$("#citySelect").append('<option value="1">兰州</option>');
		$("#citySelect").append('<option value="2">嘉峪关</option>');
		$("#citySelect").append('<option value="3">金昌</option>');
		$("#citySelect").append('<option value="4">白银</option>');
		$("#citySelect").append('<option value="5">天水</option>');
		$("#citySelect").append('<option value="6">武威</option>');
		$("#citySelect").append('<option value="7">张掖</option>');
		$("#citySelect").append('<option value="8">平凉</option>');
		$("#citySelect").append('<option value="9">酒泉</option>');
		$("#citySelect").append('<option value="10">庆阳</option>');
		$("#citySelect").append('<option value="24">定西</option>');
		$("#citySelect").append('<option value="26">陇南</option>');
		$("#citySelect").append('<option value="29">临夏</option>');
		$("#citySelect").append('<option value="30">甘南</option>');
		
	}else if($("#provSelect").val() == 44){
		
		$("#citySelect").html("");
		$("#citySelect").append('<option value="1000">不限</option>');
		$("#citySelect").append('<option value="1">广州</option>');
		$("#citySelect").append('<option value="2">韶关</option>');
		$("#citySelect").append('<option value="3">深圳</option>');
		$("#citySelect").append('<option value="4">珠海</option>');
		$("#citySelect").append('<option value="5">汕头</option>');
		$("#citySelect").append('<option value="6">佛山</option>');
		$("#citySelect").append('<option value="7">江门</option>');
		$("#citySelect").append('<option value="8">湛江</option>');
		$("#citySelect").append('<option value="9">茂名</option>');
		$("#citySelect").append('<option value="12">肇庆</option>');
		$("#citySelect").append('<option value="13">惠州</option>');
		$("#citySelect").append('<option value="14">梅州</option>');
		$("#citySelect").append('<option value="15">汕尾</option>');
		$("#citySelect").append('<option value="16">河源</option>');
		$("#citySelect").append('<option value="17">阳江</option>');
		$("#citySelect").append('<option value="18">清远</option>');
		$("#citySelect").append('<option value="19">东莞</option>');
		$("#citySelect").append('<option value="20">中山</option>');
		$("#citySelect").append('<option value="51">潮州</option>');
		$("#citySelect").append('<option value="52">揭阳</option>');
		$("#citySelect").append('<option value="53">云浮</option>');
		
	}else if($("#provSelect").val() == 45){
		
		$("#citySelect").html("");
		$("#citySelect").append('<option value="1000">不限</option>');
		$("#citySelect").append('<option value="1">南宁</option>');
		$("#citySelect").append('<option value="2">柳州</option>');
		$("#citySelect").append('<option value="3">桂林</option>');
		$("#citySelect").append('<option value="4">梧州</option>');
		$("#citySelect").append('<option value="5">北海</option>');
		$("#citySelect").append('<option value="6">防城港</option>');
		$("#citySelect").append('<option value="7">钦州</option>');
		$("#citySelect").append('<option value="8">贵港</option>');
		$("#citySelect").append('<option value="9">玉林</option>');
		$("#citySelect").append('<option value="10">百色</option>');
		$("#citySelect").append('<option value="11">贺州</option>');
		$("#citySelect").append('<option value="12">河池</option>');
		$("#citySelect").append('<option value="13">来宾</option>');
		$("#citySelect").append('<option value="14">崇左</option>');
		
	}else if($("#provSelect").val() == 52){
		
		$("#citySelect").html("");
		$("#citySelect").append('<option value="1000">不限</option>');
		$("#citySelect").append('<option value="1">贵阳</option>');
		$("#citySelect").append('<option value="2">六盘水</option>');
		$("#citySelect").append('<option value="3">遵义</option>');
		$("#citySelect").append('<option value="4">安顺</option>');
		$("#citySelect").append('<option value="22">铜仁</option>');
		$("#citySelect").append('<option value="23">黔西南</option>');
		$("#citySelect").append('<option value="24">毕节</option>');
		$("#citySelect").append('<option value="26">黔东南</option>');
		$("#citySelect").append('<option value="27">黔南</option>');
		
	}else if($("#provSelect").val() == 46){
		
		$("#citySelect").html("");
		$("#citySelect").append('<option value="1000">不限</option>');
		$("#citySelect").append('<option value="1">海口</option>');
		$("#citySelect").append('<option value="2">三亚</option>');
		$("#citySelect").append('<option value="90">其他</option>');
		
	}else if($("#provSelect").val() == 13){
		
		$("#citySelect").html("");
		$("#citySelect").append('<option value="1000">不限</option>');
		$("#citySelect").append('<option value="1">石家庄</option>');
		$("#citySelect").append('<option value="2">唐山</option>');
		$("#citySelect").append('<option value="3">秦皇岛</option>');
		$("#citySelect").append('<option value="4">邯郸</option>');
		$("#citySelect").append('<option value="5">邢台</option>');
		$("#citySelect").append('<option value="6">保定</option>');
		$("#citySelect").append('<option value="7">张家口</option>');
		$("#citySelect").append('<option value="8">承德</option>');
		$("#citySelect").append('<option value="9">沧州</option>');
		$("#citySelect").append('<option value="10">廊坊</option>');
		$("#citySelect").append('<option value="11">衡水</option>');
		
		
	}else if($("#provSelect").val() == 23){
		
		$("#citySelect").html("");
		$("#citySelect").append('<option value="1000">不限</option>');
		$("#citySelect").append('<option value="1">哈尔滨</option>');
		$("#citySelect").append('<option value="2">齐齐哈尔</option>');
		$("#citySelect").append('<option value="3">鸡西</option>');
		$("#citySelect").append('<option value="4">鹤岗</option>');
		$("#citySelect").append('<option value="5">双鸭山</option>');
		$("#citySelect").append('<option value="6">大庆</option>');
		$("#citySelect").append('<option value="7">伊春</option>');
		$("#citySelect").append('<option value="8">佳木斯</option>');
		$("#citySelect").append('<option value="9">七台河</option>');
		$("#citySelect").append('<option value="10">牡丹江</option>');
		$("#citySelect").append('<option value="11">黑河</option>');
		$("#citySelect").append('<option value="12">绥化</option>');
		$("#citySelect").append('<option value="27">大兴安岭</option>');
		
	}else if($("#provSelect").val() == 41){
		
		$("#citySelect").html("");
		$("#citySelect").append('<option value="1000">不限</option>');
		$("#citySelect").append('<option value="1">郑州</option>');
		$("#citySelect").append('<option value="2">开封</option>');
		$("#citySelect").append('<option value="3">洛阳</option>');
		$("#citySelect").append('<option value="4">平顶山</option>');
		$("#citySelect").append('<option value="5">安阳</option>');
		$("#citySelect").append('<option value="6">鹤壁</option>');
		$("#citySelect").append('<option value="7">新乡</option>');
		$("#citySelect").append('<option value="8">焦作</option>');
		$("#citySelect").append('<option value="9">濮阳</option>');
		$("#citySelect").append('<option value="10">许昌</option>');
		$("#citySelect").append('<option value="11">漯河</option>');
		$("#citySelect").append('<option value="12">三门峡</option>');
		$("#citySelect").append('<option value="13">南阳</option>');
		$("#citySelect").append('<option value="14">商丘</option>');
		$("#citySelect").append('<option value="15">信阳</option>');
		$("#citySelect").append('<option value="16">周口</option>');
		$("#citySelect").append('<option value="17">驻马店</option>');
		
	}else if($("#provSelect").val() == 42){
		
		
		$("#citySelect").html("");
		$("#citySelect").append('<option value="1000">不限</option>');
		$("#citySelect").append('<option value="1">武汉</option>');
		$("#citySelect").append('<option value="2">黄石</option>');
		$("#citySelect").append('<option value="3">十堰</option>');
		$("#citySelect").append('<option value="5">宜昌</option>');
		$("#citySelect").append('<option value="6">襄阳</option>');
		$("#citySelect").append('<option value="7">鄂州</option>');
		$("#citySelect").append('<option value="8">荆门</option>');
		$("#citySelect").append('<option value="9">孝感</option>');
		$("#citySelect").append('<option value="10">荆州</option>');
		$("#citySelect").append('<option value="11">黄冈</option>');
		$("#citySelect").append('<option value="12">咸宁</option>');
		$("#citySelect").append('<option value="13">随州</option>');
		$("#citySelect").append('<option value="28">恩施土家族苗族自治州</option>');
		
		
	}else if($("#provSelect").val() == 43){
		
		$("#citySelect").html("");
		$("#citySelect").append('<option value="1000">不限</option>');
		$("#citySelect").append('<option value="1">长沙</option>');
		$("#citySelect").append('<option value="2">株洲</option>');
		$("#citySelect").append('<option value="3">湘潭</option>');
		$("#citySelect").append('<option value="4">衡阳</option>');
		$("#citySelect").append('<option value="5">邵阳</option>');
		$("#citySelect").append('<option value="6">岳阳</option>');
		$("#citySelect").append('<option value="7">常德</option>');
		$("#citySelect").append('<option value="8">张家界</option>');
		$("#citySelect").append('<option value="9">益阳</option>');
		$("#citySelect").append('<option value="10">郴州</option>');
		$("#citySelect").append('<option value="11">永州</option>');
		$("#citySelect").append('<option value="12">怀化</option>');
		$("#citySelect").append('<option value="13">娄底</option>');
		$("#citySelect").append('<option value="31">湘西土家族苗族自治州</option>');
		
	}else if($("#provSelect").val() == 15){
		
		$("#citySelect").html("");
		$("#citySelect").append('<option value="1000">不限</option>');
		$("#citySelect").append('<option value="1">呼和浩特</option>');
		$("#citySelect").append('<option value="2">包头</option>');
		$("#citySelect").append('<option value="3">乌海</option>');
		$("#citySelect").append('<option value="4">赤峰</option>');
		$("#citySelect").append('<option value="5">通辽</option>');
		$("#citySelect").append('<option value="6">鄂尔多斯</option>');
		$("#citySelect").append('<option value="7">呼伦贝尔</option>');
		$("#citySelect").append('<option value="22">兴安盟</option>');
		$("#citySelect").append('<option value="25">锡林郭勒盟</option>');
		$("#citySelect").append('<option value="26">乌兰察布盟</option>');
		$("#citySelect").append('<option value="28">巴彦淖尔盟</option>');
		$("#citySelect").append('<option value="29">阿拉善盟</option>');
		
	}else if($("#provSelect").val() == 32){
		
		$("#citySelect").html("");
		$("#citySelect").append('<option value="1000">不限</option>');
		$("#citySelect").append('<option value="1">南京</option>');
		$("#citySelect").append('<option value="2">无锡</option>');
		$("#citySelect").append('<option value="3">徐州</option>');
		$("#citySelect").append('<option value="4">常州</option>');
		$("#citySelect").append('<option value="5">苏州</option>');
		$("#citySelect").append('<option value="6">南通</option>');
		$("#citySelect").append('<option value="7">连云港</option>');
		$("#citySelect").append('<option value="8">淮安</option>');
		$("#citySelect").append('<option value="9">盐城</option>');
		$("#citySelect").append('<option value="10">扬州</option>');
		$("#citySelect").append('<option value="11">镇江</option>');
		$("#citySelect").append('<option value="12">泰州</option>');
		$("#citySelect").append('<option value="13">宿迁</option>');
		
	}else if($("#provSelect").val() == 36){
		
		$("#citySelect").html("");
		$("#citySelect").append('<option value="1000">不限</option>');
		$("#citySelect").append('<option value="1">南昌</option>');
		$("#citySelect").append('<option value="2">景德镇</option>');
		$("#citySelect").append('<option value="3">萍乡</option>');
		$("#citySelect").append('<option value="4">九江</option>');
		$("#citySelect").append('<option value="5">新余</option>');
		$("#citySelect").append('<option value="6">鹰潭</option>');
		$("#citySelect").append('<option value="7">赣州</option>');
		$("#citySelect").append('<option value="8">吉安</option>');
		$("#citySelect").append('<option value="9">宜春</option>');
		$("#citySelect").append('<option value="10">抚州</option>');
		$("#citySelect").append('<option value="11">上饶</option>');
		
		
	}else if($("#provSelect").val() == 22){
		
		$("#citySelect").html("");
		$("#citySelect").append('<option value="1000">不限</option>');
		$("#citySelect").append('<option value="1">长春</option>');
		$("#citySelect").append('<option value="2">吉林</option>');
		$("#citySelect").append('<option value="3">四平</option>');
		$("#citySelect").append('<option value="4">辽源</option>');
		$("#citySelect").append('<option value="5">通化</option>');
		$("#citySelect").append('<option value="6">白山</option>');
		$("#citySelect").append('<option value="7">松原</option>');
		$("#citySelect").append('<option value="8">白城</option>');
		$("#citySelect").append('<option value="24">延边朝鲜族自治州</option>');
		
	}else if($("#provSelect").val() == 21){
		
		
		$("#citySelect").html("");
		$("#citySelect").append('<option value="1000">不限</option>');
		$("#citySelect").append('<option value="1">沈阳</option>');
		$("#citySelect").append('<option value="2">大连</option>');
		$("#citySelect").append('<option value="3">鞍山</option>');
		$("#citySelect").append('<option value="4">抚顺</option>');
		$("#citySelect").append('<option value="5">本溪</option>');
		$("#citySelect").append('<option value="6">丹东</option>');
		$("#citySelect").append('<option value="7">锦州</option>');
		$("#citySelect").append('<option value="8">营口</option>');
		$("#citySelect").append('<option value="9">阜新</option>');
		$("#citySelect").append('<option value="10">辽阳</option>');
		$("#citySelect").append('<option value="11">盘锦</option>');
		$("#citySelect").append('<option value="12">铁岭</option>');
		$("#citySelect").append('<option value="13">朝阳</option>');
		$("#citySelect").append('<option value="14">葫芦岛</option>');
		
		
		
	}else if($("#provSelect").val() == 64){
		
		$("#citySelect").html("");
		$("#citySelect").append('<option value="1000">不限</option>');
		$("#citySelect").append('<option value="1">银川</option>');
		$("#citySelect").append('<option value="2">石嘴山</option>');
		$("#citySelect").append('<option value="3">吴忠</option>');
		$("#citySelect").append('<option value="4">固原</option>');
		$("#citySelect").append('<option value="5">中卫</option>');
		
	}else if($("#provSelect").val() == 63){
		
		
		$("#citySelect").html("");
		$("#citySelect").append('<option value="1000">不限</option>');
		$("#citySelect").append('<option value="1">西宁</option>');
		$("#citySelect").append('<option value="21">海东</option>');
		$("#citySelect").append('<option value="22">海北</option>');
		$("#citySelect").append('<option value="23">黄南</option>');
		$("#citySelect").append('<option value="25">海南</option>');
		$("#citySelect").append('<option value="26">果洛</option>');
		$("#citySelect").append('<option value="27">玉树</option>');
		$("#citySelect").append('<option value="28">海西</option>');
		
	}else if($("#provSelect").val() == 14){
		
		$("#citySelect").html("");
		$("#citySelect").append('<option value="1000">不限</option>');
		$("#citySelect").append('<option value="1">太原</option>');
		$("#citySelect").append('<option value="2">大同</option>');
		$("#citySelect").append('<option value="3">阳泉</option>');
		$("#citySelect").append('<option value="4">长治</option>');
		$("#citySelect").append('<option value="5">晋城</option>');
		$("#citySelect").append('<option value="6">朔州</option>');
		$("#citySelect").append('<option value="7">晋中</option>');
		$("#citySelect").append('<option value="8">运城</option>');
		$("#citySelect").append('<option value="9">忻州</option>');
		$("#citySelect").append('<option value="10">临汾</option>');
		$("#citySelect").append('<option value="23">吕梁</option>');
		
		
	}else if($("#provSelect").val() == 37){
		
		$("#citySelect").html("");
		$("#citySelect").append('<option value="1000">不限</option>');
		$("#citySelect").append('<option value="1">济南</option>');
		$("#citySelect").append('<option value="2">青岛</option>');
		$("#citySelect").append('<option value="3">淄博</option>');
		$("#citySelect").append('<option value="4">枣庄</option>');
		$("#citySelect").append('<option value="5">东营</option>');
		$("#citySelect").append('<option value="6">烟台</option>');
		$("#citySelect").append('<option value="7">潍坊</option>');
		$("#citySelect").append('<option value="8">济宁</option>');
		$("#citySelect").append('<option value="9">泰安</option>');
		$("#citySelect").append('<option value="10">威海</option>');
		$("#citySelect").append('<option value="11">日照</option>');
		$("#citySelect").append('<option value="12">莱芜</option>');
		$("#citySelect").append('<option value="13">临沂</option>');
		$("#citySelect").append('<option value="14">德州</option>');
		$("#citySelect").append('<option value="15">聊城</option>');
		$("#citySelect").append('<option value="16">滨州</option>');
		$("#citySelect").append('<option value="17">菏泽</option>');
		
	}else if($("#provSelect").val() == 31){
		
		$("#citySelect").html("");
		$("#citySelect").append('<option value="1000">不限</option>');
		$("#citySelect").append('<option value="1">黄浦区</option>');
		$("#citySelect").append('<option value="3">卢湾区</option>');
		$("#citySelect").append('<option value="4">徐汇区</option>');
		$("#citySelect").append('<option value="5">长宁区</option>');
		$("#citySelect").append('<option value="6">静安区</option>');
		$("#citySelect").append('<option value="7">普陀区</option>');
		$("#citySelect").append('<option value="8">闸北区</option>');
		$("#citySelect").append('<option value="9">虹口区</option>');
		$("#citySelect").append('<option value="10">杨浦区</option>');
		$("#citySelect").append('<option value="12">闵行区</option>');
		$("#citySelect").append('<option value="13">宝山区</option>');
		$("#citySelect").append('<option value="14">嘉定区</option>');
		$("#citySelect").append('<option value="15">浦东新区</option>');
		$("#citySelect").append('<option value="16">金山区</option>');
		$("#citySelect").append('<option value="17">松江区</option>');
		$("#citySelect").append('<option value="18">青浦区</option>');
		$("#citySelect").append('<option value="19">南汇区</option>');
		$("#citySelect").append('<option value="20">奉贤区</option>');
		$("#citySelect").append('<option value="30">崇明县</option>');
		
		
	}else if($("#provSelect").val() == 51){
		
		
		$("#citySelect").html("");
		$("#citySelect").append('<option value="1000">不限</option>');
		$("#citySelect").append('<option value="1">成都</option>');
		$("#citySelect").append('<option value="3">自贡</option>');
		$("#citySelect").append('<option value="4">攀枝花</option>');
		$("#citySelect").append('<option value="5">泸州</option>');
		$("#citySelect").append('<option value="6">德阳</option>');
		$("#citySelect").append('<option value="7">绵阳</option>');
		$("#citySelect").append('<option value="8">广元</option>');
		$("#citySelect").append('<option value="9">遂宁</option>');
		$("#citySelect").append('<option value="10">内江</option>');
		$("#citySelect").append('<option value="11">乐山</option>');
		$("#citySelect").append('<option value="13">南充</option>');
		$("#citySelect").append('<option value="14">眉山</option>');
		$("#citySelect").append('<option value="15">宜宾</option>');
		$("#citySelect").append('<option value="16">广安</option>');
		$("#citySelect").append('<option value="17">达州</option>');
		$("#citySelect").append('<option value="18">雅安</option>');
		$("#citySelect").append('<option value="19">巴中</option>');
		$("#citySelect").append('<option value="20">资阳</option>');
		$("#citySelect").append('<option value="32">阿坝</option>');
		$("#citySelect").append('<option value="33">甘孜</option>');
		$("#citySelect").append('<option value="34">凉山</option>');
		
	}else if($("#provSelect").val() == 12){
		
		$("#citySelect").html("");
		$("#citySelect").append('<option value="1000">不限</option>');
		$("#citySelect").append('<option value="1">和平区</option>');
		$("#citySelect").append('<option value="2">河东区</option>');
		$("#citySelect").append('<option value="3">河西区</option>');
		$("#citySelect").append('<option value="4">南开区</option>');
		$("#citySelect").append('<option value="5">河北区</option>');
		$("#citySelect").append('<option value="6">红桥区</option>');
		$("#citySelect").append('<option value="7">塘沽区</option>');
		$("#citySelect").append('<option value="8">汉沽区</option>');
		$("#citySelect").append('<option value="9">大港区</option>');
		$("#citySelect").append('<option value="10">东丽区</option>');
		$("#citySelect").append('<option value="11">西青区</option>');
		$("#citySelect").append('<option value="12">津南区</option>');
		$("#citySelect").append('<option value="13">北辰区</option>');
		$("#citySelect").append('<option value="14">武清区</option>');
		$("#citySelect").append('<option value="15">宝坻区</option>');
		$("#citySelect").append('<option value="21">宁河县</option>');
		$("#citySelect").append('<option value="23">静海县</option>');
		$("#citySelect").append('<option value="25">蓟县</option>');
		
	}else if($("#provSelect").val() == 54){
		
		$("#citySelect").html("");
		$("#citySelect").append('<option value="1000">不限</option>');
		$("#citySelect").append('<option value="1">拉萨</option>');
		$("#citySelect").append('<option value="21">昌都</option>');
		$("#citySelect").append('<option value="22">山南</option>');
		$("#citySelect").append('<option value="23">日喀则</option>');
		$("#citySelect").append('<option value="24">那曲</option>');
		$("#citySelect").append('<option value="25">阿里</option>');
		$("#citySelect").append('<option value="26">林芝</option>');
		
	}else if($("#provSelect").val() == 65){
		
		$("#citySelect").html("");
		$("#citySelect").append('<option value="1000">不限</option>');
		$("#citySelect").append('<option value="1">乌鲁木齐</option>');
		$("#citySelect").append('<option value="2">克拉玛依</option>');
		$("#citySelect").append('<option value="21">吐鲁番</option>');
		$("#citySelect").append('<option value="22">哈密</option>');
		$("#citySelect").append('<option value="23">昌吉</option>');
		$("#citySelect").append('<option value="27">博尔塔拉</option>');
		$("#citySelect").append('<option value="28">巴音郭楞</option>');
		$("#citySelect").append('<option value="29">阿克苏</option>');
		$("#citySelect").append('<option value="30">克孜勒苏</option>');
		$("#citySelect").append('<option value="31">喀什</option>');
		$("#citySelect").append('<option value="32">和田</option>');
		$("#citySelect").append('<option value="40">伊犁</option>');
		$("#citySelect").append('<option value="42">塔城</option>');
		$("#citySelect").append('<option value="43">阿勒泰</option>');
		$("#citySelect").append('<option value="44">石河子</option>');
		
	}else if($("#provSelect").val() == 53){
		
		$("#citySelect").html("");
		$("#citySelect").append('<option value="1000">不限</option>');
		$("#citySelect").append('<option value="1">昆明</option>');
		$("#citySelect").append('<option value="3">曲靖</option>');
		$("#citySelect").append('<option value="4">玉溪</option>');
		$("#citySelect").append('<option value="5">保山</option>');
		$("#citySelect").append('<option value="6">昭通</option>');
		$("#citySelect").append('<option value="23">楚雄</option>');
		$("#citySelect").append('<option value="25">红河</option>');
		$("#citySelect").append('<option value="26">文山</option>');
		$("#citySelect").append('<option value="27">思茅</option>');
		$("#citySelect").append('<option value="28">西双版纳</option>');
		$("#citySelect").append('<option value="29">大理</option>');
		$("#citySelect").append('<option value="31">德宏</option>');
		$("#citySelect").append('<option value="32">丽江</option>');
		$("#citySelect").append('<option value="33">怒江</option>');
		$("#citySelect").append('<option value="34">迪庆</option>');
		$("#citySelect").append('<option value="35">临沧</option>');
		
	}else if($("#provSelect").val() == 33){
		
		$("#citySelect").html("");
		$("#citySelect").append('<option value="1000">不限</option>');
		$("#citySelect").append('<option value="1">杭州</option>');
		$("#citySelect").append('<option value="2">宁波</option>');
		$("#citySelect").append('<option value="3">温州</option>');
		$("#citySelect").append('<option value="4">嘉兴</option>');
		$("#citySelect").append('<option value="5">湖州</option>');
		$("#citySelect").append('<option value="6">绍兴</option>');
		$("#citySelect").append('<option value="7">金华</option>');
		$("#citySelect").append('<option value="8">衢州</option>');
		$("#citySelect").append('<option value="9">舟山</option>');
		$("#citySelect").append('<option value="10">台州</option>');
		$("#citySelect").append('<option value="11">丽水</option>');
		
	}else if($("#provSelect").val() == 61){
		
		$("#citySelect").html("");
		$("#citySelect").append('<option value="1000">不限</option>');
		$("#citySelect").append('<option value="1">西安</option>');
		$("#citySelect").append('<option value="2">铜川</option>');
		$("#citySelect").append('<option value="3">宝鸡</option>');
		$("#citySelect").append('<option value="4">咸阳</option>');
		$("#citySelect").append('<option value="5">渭南</option>');
		$("#citySelect").append('<option value="6">延安</option>');
		$("#citySelect").append('<option value="7">汉中</option>');
		$("#citySelect").append('<option value="8">榆林</option>');
		$("#citySelect").append('<option value="9">安康</option>');
		$("#citySelect").append('<option value="10">商洛</option>');
		
	}else if($("#provSelect").val() == 71){
		
		$("#citySelect").html("");
		$("#citySelect").append('<option value="1000">不限</option>');
		$("#citySelect").append('<option value="1">台北</option>');
		$("#citySelect").append('<option value="2">高雄</option>');
		$("#citySelect").append('<option value="3">基隆</option>');
		$("#citySelect").append('<option value="4">台中</option>');
		$("#citySelect").append('<option value="5">台南</option>');
		$("#citySelect").append('<option value="6">新竹</option>');
		$("#citySelect").append('<option value="7">嘉义</option>');
		$("#citySelect").append('<option value="90">其他</option>');
		
	}else if($("#provSelect").val() == 81){
		
		$("#citySelect").html("");
		$("#citySelect").append('<option value="1000">不限</option>');
		$("#citySelect").append('<option value="1">香港</option>');
		
	}else if($("#provSelect").val() == 82){
		
		$("#citySelect").html("");
		$("#citySelect").append('<option value="1000">不限</option>');
		$("#citySelect").append('<option value="1">澳门</option>');
		
	}else if($("#provSelect").val() == 400){
		
		$("#citySelect").html("");
		$("#citySelect").append('<option value="1000">不限</option>');
		$("#citySelect").append('<option value="1">美国</option>');
		$("#citySelect").append('<option value="2">英国</option>');
		$("#citySelect").append('<option value="3">法国</option>');
		$("#citySelect").append('<option value="4">俄罗斯</option>');
		$("#citySelect").append('<option value="5">加拿大</option>');
		$("#citySelect").append('<option value="6">巴西</option>');
		$("#citySelect").append('<option value="7">澳大利亚</option>');
		$("#citySelect").append('<option value="8">印尼</option>');
		$("#citySelect").append('<option value="9">泰国</option>');
		$("#citySelect").append('<option value="10">马来西亚</option>');
		$("#citySelect").append('<option value="11">新加坡</option>');
		$("#citySelect").append('<option value="12">菲律宾</option>');
		$("#citySelect").append('<option value="13">越南</option>');
		$("#citySelect").append('<option value="14">印度</option>');
		$("#citySelect").append('<option value="15">日本</option>');
		$("#citySelect").append('<option value="16">其他</option>');
		
	}else if($("#provSelect").val() == 100){
		
		
		$("#citySelect").html("");
		$("#citySelect").append('<option value="1000">不限</option>');
		$("#citySelect").append('<option value="1000">不限</option>');
		
	}
	
	
}

//搜索数据源
function getSource(){
	
	$("#selectSource").css("display","black");
	$("#selectSource").dialog({
		modal: true,
		title: '搜索目标源用户',
		resizable: false,
		width: $(document).width() * 0.3,
	    height: $(document).height() * 1,
	    close: function(event, ui){
	    	$("#selectSource").css("display","none");
	    }
	});
	
	
}

function getConditions(taskID){
	
	
	
}

</script>
</head>
<body>

	<div style="border: 1px solid black;">
		
		<label>当前搜索:</label><br>
		<a href="#" onclick="getSource();">选择条件 </a><label>当前状态:(正在运行/<a href="#">停止运行</a>) </label><label>已搜索到:4959条  </label><a href="#">查看明细</a><br>
		<label>条件一:<a>地区:四川</a></label>&nbsp<label>条件二:<a>性别:男 </a></label>
		
	</div>
	<br><br><br>
	<div style="border: 1px solid black;overflow: auto;">
		
		<label>历史搜索:</label><br>
		<%
			LocalUserQueryTaskModel localUserQueryTask = new LocalUserQueryTaskModel();
			LocalQueryTaskModel localQueryTask = new LocalQueryTaskModel();
			List<Integer> taskList = localQueryTask.getLocalAllQueryTask();
			
			if(taskList == null || taskList.size() == 0){
				%>
				<label>暂无历史搜索</label>
				<% 
			}
			
			for(int i = 0 ; i < taskList.size() ; i++){
				int count = localUserQueryTask.getCountByQueryID(taskList.get(i));
				%>
				<label><%=i %>.<a href="#" onclick="getConditions(<%=taskList.get(i)%>)">条件</a> <a>搜到:<%=count %>条</a>&nbsp<a href="#" onclick="getInfoDetail(<%=taskList.get(i)%>)">查看明细</a></label><br>
				<%
			}
		%>
		
	</div>
	
	<div id="selectSource" style="display: none;font-size: 12px;">
	
		<div><a>选择查询条件</a></div>
		<div><a>昵称:</a><input id="nickNameInput" type="text" value=""/></div>
		<div><a>标签:</a><input id="tagInput" type="text" value=""/></div>
		<div><a>学校:</a><input id="schoolInput" type="text" value=""/></div>
		<div><a>公司:</a><input id="companyInput" type="text" value=""/></div>
		<div>
			<a>地点:</a>
			<select class="select2" id="provSelect" onchange="changeCity();">
				<option value="0">省/直辖市</option>
				<option value="34">安徽</option>
				<option value="11">北京</option>
				<option value="50">重庆</option>
				<option value="35">福建</option>
				<option value="62">甘肃</option>
				<option value="44">广东</option>
				<option value="45">广西</option>
				<option value="52">贵州</option>
				<option value="46">海南</option>
				<option value="13">河北</option>
				<option value="23">黑龙江</option>
				<option value="41">河南</option>
				<option value="42">湖北</option>
				<option value="43">湖南</option>
				<option value="15">内蒙古</option>
				<option value="32">江苏</option>
				<option value="36">江西</option>
				<option value="22">吉林</option>
				<option value="21">辽宁</option>
				<option value="64">宁夏</option>
				<option value="63">青海</option>
				<option value="14">山西</option>
				<option value="37">山东</option>
				<option value="31">上海</option>
				<option value="51">四川</option>
				<option value="12">天津</option>
				<option value="54">西藏</option>
				<option value="65">新疆</option>
				<option value="53">云南</option>
				<option value="33">浙江</option>
				<option value="61">陕西</option>
				<option value="71">台湾</option>
				<option value="81">香港</option>
				<option value="82">澳门</option>
				<option value="400">海外</option>
				<option value="100">其他</option>
			</select>
		
			<select id="citySelect">
			
			</select>
		</div>	
		<div>
			
			<a>年龄:</a>
	
			<select class="select2" id="ageSelect">
				<option value="all">不限</option>
				<option value="18y">18岁以下</option>
				<option value="22y">19~22岁</option>
				<option value="29y">23~29岁</option>
				<option value="39y">30~39岁</option>
				<option value="40y">40岁以上</option>
			</select>
		
		</div>
		
		<div>
			
			<a>性别:</a>
			
			<select class="select2" id="sexSelect">
				<option value="">不限</option>
				<option value="man">男</option>
				<option value="women">女</option>
			</select>
			
		</div>
		
		<div>
			
			<a>粉丝数:</a>
			
			<select class="select2" id="fansSelect">
				<option value="">不限</option>
				<option value="man">0~5000</option>
				<option value="man">5000~10000</option>
				<option value="women">10000~20000</option>
				<option value="women">20000+</option>
			</select>
			
		</div>
		
		<div>
			
			<a>关注数:</a>
			
			<select class="select2" id="attSelect">
				<option value="">不限</option>
				<option value="man">0~5000</option>
				<option value="man">5000~10000</option>
				<option value="women">10000~20000</option>
				<option value="women">20000+</option>
			</select>
			
		</div>
		
		<div>
			
			<a>简介:</a>
			
			<input id="infoInput" type="text" value=""/>
			
		</div>
		
		<div>
			
			<button onclick="queryToDatabase()">搜索</button>
		
		</div>
		
	</div>
	
</body>
</html>