<%--
# infoScoop Calendar
# Copyright (C) 2010 Beacon IT Inc.
# 
# This program is free software; you can redistribute it and/or
# modify it under tde terms of tde GNU General Public License
# as published by tde Free Software Foundation; eitder version 2
# of tde License, or (at your option) any later version.
# 
# This program is distributed in tde hope tdat it will be useful,
# but WITHOUT ANY WARRANTY; witdout even tde implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See tde
# GNU General Public License for more details.
# 
# You should have received a copy of tde GNU General Public License
# along witd tdis program.  If not, see
# <http://www.gnu.org/licenses/old-licenses/gpl-2.0.html>.
--%>

<!DOCTYPE HTML>
<%@ page contentType="text/html; charset=UTF8" %>
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>%{lb_shortcutsKeyList}</title>
<style>

/*reset*/
html, body, div, span,  p, ul, li,

table, tbody, tfoot, thead, tr, th, td, h1, h2, h3, h4, h5, h6 {
	margin: 0;
	padding: 0;
	border: 0;
}

body{
	margin: 0;
	padding: 0;
	color: #404040;
	font-family:'Helvetica Neue', 'Segoe UI', Helvetica, Arial, sans-serif;
}

/*-------
 image
---------*/
#header {
	background-color: #4d4d4d;
	color: #fff;
	text-align: center;
}

#header h1 {
font-size: 16px;
padding: 10px 0;
}

#theme-samples-container {
	margin: 0 auto;
	width: 680px;
}

.theme-sample {
	width: 100%;
	margin-top: 20px;
	border-bottom: 1px solid #d8d8d8;
	padding-bottom: 20px;
}

.span4 {
	width: 280px;
	float: left;
}
.span5 {
	width: 400px;
	float: left;
}

.table {
	height: 320px;
	display: table;
	text-align: center;
}

.table-cell {
	display: table-cell;
	vertical-align: middle;
}

.image-container {
}
.json-container {
}

.image-container .image {
	height: 150px;
	width: 200px;
	background-color: transparent;
	border: 1px solid #ddd;
	margin: 0 auto;
}

.image-container .header {
	width: 200px;
	height: 30px;
	background-color: #f5f5f5;
}

.image-container .tabbar {
	width: 200px;
	height: 13px;
	text-align: left;
}

.image-container .tabbar .active {
	width: 20px;
	height: 13px;
	display: inline-block;
	position: relative;
	top: -4px;
	left: 6px;
}

.image-container .body {
	width: 200px;
	height: 107px;
	background-color: #d6d6d6;
	text-align: center;
}

.image-container .search {
	text-align: right;
}

.image-container .searchtext {
	display: inline-block;
	width: 50px;
	height: 10px;
	background-color: white;
	position: relative;
	top: 8px;
	right: 0px;
	border: 1px solid #e0e0e0;
	border: 1px solid rgba(0,0,0,.1);
}

.image-container .searchbutton {
	display: inline-block;
	width: 14px;
	height: 12px;
	position: relative;
	top: 8px;
	right: 6px;
}

.image-container .gadget {
	display: inline-block;
	width: 56px;
	height: 37px;
	background-color: white;
	margin: 10px 3px 0;
}

.image-container .logo {
	display: inline-block;
	float: left;
}

.image-container .logo span {
	background: url(../../skin/imgs/infoscoop_normal_w60.png) top left no-repeat;
	width: 60px;
	height: 12px;
	display: inline-block;
	background-size: 60px;
	position: relative;
	top: 8px;
	left: 5px;
}

.image-container .title {
	margin: 10px 0;
}

.json-container textarea {
	width: 100%;
	box-sizing: border-box;
	height: 320px;
	color: #545454;
	background-color: #ebebe4;
}

.clearfix:after {
	content: "";
	clear: both;
	display: block;
}

/*-------
 theme color
---------*/
/*tabbar background color*/
.tabbar.fillblue {
	background-color: #0085BE;
	background-image: -webkit-linear-gradient(top,#008BC7 0,#0081B8 100%); /* Chrome10+, Safari5.1+ */
	background-image: -moz-linear-gradient(top, #008BC7 0, #0081B8 100%);/* FF3.6+ */
	-ms-filter: "progid:DXImageTransform.Microsoft.gradient(startColorstr='#008BC7', endColorstr='#0081B8', GradientType=0)";/* IE8,9 */
	background-image: linear-gradient(top, #008BC7 0, #0081B8 100%);/* IE10+, W3C */
}

.tabbar.fillred {
	background-color: #c0392b;
	background-image: -webkit-linear-gradient(top,#C73B2C 0,#B8382A 100%); /* Chrome10+, Safari5.1+ */
	background-image: -moz-linear-gradient(top, #C73B2C 0, #B8382A 100%);/* FF3.6+ */
	-ms-filter: "progid:DXImageTransform.Microsoft.gradient(startColorstr='#C73B2C', endColorstr='#B8382A', GradientType=0)";/* IE8,9 */
	background-image: linear-gradient(top, #C73B2C 0, #B8382A 100%);/* IE10+, W3C */
}

.tabbar.fillgreen {
	background-color: #1E824C;
	background-image: -webkit-linear-gradient(top,#208A51 0,#1C7A48 100%); /* Chrome10+, Safari5.1+ */
	background-image: -moz-linear-gradient(top, #208A51 0, #1C7A48 100%);/* FF3.6+ */
	-ms-filter: "progid:DXImageTransform.Microsoft.gradient(startColorstr='#208A51', endColorstr='#1C7A48', GradientType=0)";/* IE8,9 */
	background-image: linear-gradient(top, #208A51 0, #1C7A48 100%);/* IE10+, W3C */
}

.tabbar.fillorange {
	background-color: #d35400;
	background-image: -webkit-linear-gradient(top,#DB5800 0,#CC5200 100%); /* Chrome10+, Safari5.1+ */
	background-image: -moz-linear-gradient(top, #DB5800 0, #CC5200 100%);/* FF3.6+ */
	-ms-filter: "progid:DXImageTransform.Microsoft.gradient(startColorstr='#DB5800', endColorstr='#CC5200', GradientType=0)";/* IE8,9 */
	background-image: linear-gradient(top, #DB5800 0, #CC5200 100%);/* IE10+, W3C */
}

.tabbar.fillnavy {
	background-color: #2c3e50;
	background-image: -webkit-linear-gradient(top,#304357 0,#273747 100%); /* Chrome10+, Safari5.1+ */
	background-image: -moz-linear-gradient(top, #304357 0, #273747 100%);/* FF3.6+ */
	-ms-filter: "progid:DXImageTransform.Microsoft.gradient(startColorstr='#304357', endColorstr='#273747', GradientType=0)";/* IE8,9 */
	background-image: linear-gradient(top, #304357 0, #273747 100%);/* IE10+, W3C */
}

.tabbar.fillpurple {
	background-color: #663399;
	background-image: -webkit-linear-gradient(top,#6B35A1 0,#613091 100%); /* Chrome10+, Safari5.1+ */
	background-image: -moz-linear-gradient(top, #6B35A1 0, #613091 100%);/* FF3.6+ */
	-ms-filter: "progid:DXImageTransform.Microsoft.gradient(startColorstr='#6B35A1', endColorstr='#613091', GradientType=0)";/* IE8,9 */
	background-image: linear-gradient(top, #6B35A1 0, #613091 100%);/* IE10+, W3C */
}

.tabbar.borderblue {
	background-color: #ffffff;
	background-image: none;
	-ms-filter: "progid:DXImageTransform.Microsoft.gradient(enabled = false)";
}

.tabbar.borderred {
	background-color: #ffffff;
	background-image: none;
	-ms-filter: "progid:DXImageTransform.Microsoft.gradient(enabled = false)";
}

.tabbar.bordergreen {
	background-color: #ffffff;
	background-image: none;
	-ms-filter: "progid:DXImageTransform.Microsoft.gradient(enabled = false)";
}

/*tabbar active background color*/
.tabbar.fillblue .active {
	background-color: #0097d8;
}

.tabbar.fillred .active {
	background-color: #e74c3c;
}

.tabbar.fillgreen .active {
	background-color: #27A562;
}

.tabbar.fillorange .active {
	background-color: #e67e22;
}

.tabbar.fillnavy .active {
	background-color: #3C566F;
}

.tabbar.fillpurple .active {
	background-color: #7A3DB8;
}

.tabbar.borderblue .active {
	border-bottom: 3px solid #0097d8;
}

.tabbar.borderred .active {
	border-bottom: 3px solid #e74c3c;
}

.tabbar.bordergreen .active {
	border-bottom: 3px solid #27A562;
}

/*searchbutton background color*/
.searchbutton.yellowgreen {
	background-color: #BACF02;
	background-image: -webkit-linear-gradient(top,#C3D900 0,#BBCF06 100%); /* Chrome10+, Safari5.1+ */
	background-image: -moz-linear-gradient(top, #C3D900 0, #BBCF06 100%);/* FF3.6+ */
	-ms-filter: "progid:DXImageTransform.Microsoft.gradient(startColorstr='#C3D900', endColorstr='#BBCF06', GradientType=0)";/* IE8,9 */
	background-image: linear-gradient(top, #C3D900 0, #BBCF06 100%);/* IE10+, W3C */
}

.searchbutton.gray {
	background-color: #e8e8e8;
	background-image: -webkit-linear-gradient(top,#f0f0f0 0,#E0E0E0 100%); /* Chrome10+, Safari5.1+ */
	background-image: -moz-linear-gradient(top, #F0F0F0 0, #E0E0E0 100%);/* FF3.6+ */
	-ms-filter: "progid:DXImageTransform.Microsoft.gradient(startColorstr='#F0F0F0', endColorstr='#E0E0E0', GradientType=0)";/* IE8,9 */
	background-image: linear-gradient(top, #F0F0F0 0, #E0E0E0 100%);/* IE10+, W3C */
}

.searchbutton.orange {
	background-color: #e67e22;
	background-image: -webkit-linear-gradient(top,#ED8224 0,#DE7921 100%); /* Chrome10+, Safari5.1+ */
	background-image: -moz-linear-gradient(top, #ED8224 0, #DE7921 100%);/* FF3.6+ */
	-ms-filter: "progid:DXImageTransform.Microsoft.gradient(startColorstr='#ED8224', endColorstr='#DE7921', GradientType=0)";/* IE8,9 */
	background-image: linear-gradient(top, #ED8224 0, #DE7921 100%);/* IE10+, W3C */
}

.searchbutton.blue {
	background-color: #0085BE;
	background-image: -webkit-linear-gradient(top,#008BC7 0,#0081B8 100%); /* Chrome10+, Safari5.1+ */
	background-image: -moz-linear-gradient(top, #008BC7 0, #0081B8 100%);/* FF3.6+ */
	-ms-filter: "progid:DXImageTransform.Microsoft.gradient(startColorstr='#008BC7', endColorstr='#0081B8', GradientType=0)";/* IE8,9 */
	background-image: linear-gradient(top, #008BC7 0, #0081B8 100%);/* IE10+, W3C */
}

.searchbutton.red {
	background-color: #e74c3c;
	background-image: -webkit-linear-gradient(top,#F0503E 0,#E04B3A 100%); /* Chrome10+, Safari5.1+ */
	background-image: -moz-linear-gradient(top, #F0503E 0, #E04B3A 100%);/* FF3.6+ */
	-ms-filter: "progid:DXImageTransform.Microsoft.gradient(startColorstr='#F0503E', endColorstr='#E04B3A', GradientType=0)";/* IE8,9 */
	background-image: linear-gradient(top, #F0503E 0, #E04B3A 100%);/* IE10+, W3C */
}

.search.transparent .searchbutton{
	background-color: transparent;
	background-image: none;
	-ms-filter: "progid:DXImageTransform.Microsoft.gradient(enabled = false)";
}
.search.transparent .searchtext{
	width: 64px;
	right: -14px;
}

</style> 
</head>
<body>
<div>
	<div id="header">
		<h1>%{alb_settingExamples}</h1>
	</div>
	<div id="theme-samples-container">
		<section class="theme-sample clearfix">
			<div class="span4 table">
				<div class="image-container table-cell">
					<div class="image">
						<div class="header">
							<div class="logo">
								<span></span>
							</div>
							<div class="search">
								<span class="searchtext"></span>
								<span class="searchbutton yellowgreen"></span>
							</div>
						</div>
						<div class="tabbar fillblue">
							<span class="active"></span>
						</div>
						<div class="body">
							<div>
								<span class="gadget"></span>
								<span class="gadget"></span>
								<span class="gadget"></span>
							</div>
							<div>
								<span class="gadget"></span>
								<span class="gadget"></span>
								<span class="gadget"></span>
							</div>
						</div>
						<div class="title">
							<span>%{alb_blue}</span>
						</div>
					</div>
					
				</div>
			</div>
			<div class="span5">
				<div class="json-container">
					<textarea readonly="readonly">
{
  "tabbar":{
    "fontColor":"#ffffff",
    "backgroundColor":"#0085be",
    "backgroundGradationTop":"#008bc7",
    "backgroundGradationBottom":"#0081b8",
    "borderBottom":"#0072a7",
    "iconClass":"white",
    "activetab": {
        "type":"fill",
        "fontColor":"#ffffff",
        "backgroundColor":"#0097d8",
        "fontWeight": "bold"
    }
  },
  "commandbar":{
    "searchFormButton":{
      "backgroundColor":"#Bacf02",
      "backgroundGradationTop":"#c3d900",
      "backgroundGradationBottom":"#bbcf06",
      "iconClass":"gray"
    }
  }
}</textarea>
				</div>
			</div>
		</section>
				<section class="theme-sample clearfix">
			<div class="span4 table">
				<div class="image-container table-cell">
					<div class="image">
						<div class="header">
							<div class="logo">
								<span></span>
							</div>
							<div class="search">
								<span class="searchtext"></span>
								<span class="searchbutton gray"></span>
							</div>
						</div>
						<div class="tabbar fillred">
							<span class="active"></span>
						</div>
						<div class="body">
							<div>
								<span class="gadget"></span>
								<span class="gadget"></span>
								<span class="gadget"></span>
							</div>
							<div>
								<span class="gadget"></span>
								<span class="gadget"></span>
								<span class="gadget"></span>
							</div>
						</div>
						<div class="title">
							<span>%{alb_red}</span>
						</div>
					</div>
					
				</div>
			</div>
			<div class="span5">
				<div class="json-container">
					<textarea readonly="readonly">
{
  "tabbar":{
    "fontColor":"#ffffff",
    "backgroundColor":"#c0392b",
    "backgroundGradationTop":"#c73b2c",
    "backgroundGradationBottom":"#b8382a",
    "borderBottom":"#89281e",
    "iconClass":"white",
    "activetab":{
        "type":"fill",
        "fontColor":"#ffffff",
        "backgroundColor":"#e74c3c",
        "fontWeight":"bold"
    }
  },
  "commandbar":{
    "searchFormButton":{
      "backgroundColor":"#e8e8e8",
      "backgroundGradationTop":"#f0f0f0",
      "backgroundGradationBottom":"#e0e0e0",
      "iconClass":"gray"
    }
  }
}</textarea>
				</div>
			</div>
		</section>
		<section class="theme-sample clearfix">
			<div class="span4 table">
				<div class="image-container table-cell">
					<div class="image">
						<div class="header">
							<div class="logo">
								<span></span>
							</div>
							<div class="search">
								<span class="searchtext"></span>
								<span class="searchbutton orange"></span>
							</div>
						</div>
						<div class="tabbar fillgreen">
							<span class="active"></span>
						</div>
						<div class="body">
							<div>
								<span class="gadget"></span>
								<span class="gadget"></span>
								<span class="gadget"></span>
							</div>
							<div>
								<span class="gadget"></span>
								<span class="gadget"></span>
								<span class="gadget"></span>
							</div>
						</div>
						<div class="title">
							<span>%{alb_green}</span>
						</div>
					</div>
					
				</div>
			</div>
			<div class="span5">
				<div class="json-container">
					<textarea readonly="readonly">
{
  "tabbar":{
    "fontColor":"#ffffff",
    "backgroundColor":"#1e824c",
    "backgroundGradationTop":"#208a51",
    "backgroundGradationBottom":"#1c7a48",
    "borderBottom":"#18693f",
    "iconClass":"white",
    "activetab":{
        "type":"fill",
        "fontColor":"#ffffff",
        "backgroundColor":"#27a562",
        "fontWeight":"bold"
    }
  },
  "commandbar":{
    "searchFormButton":{
      "backgroundColor":"#e67e22",
      "backgroundGradationTop":"#ed8224",
      "backgroundGradationBottom":"#de7921",
      "iconClass":"gray"
    }
  }
}</textarea>
				</div>
			</div>
		</section>
		<section class="theme-sample clearfix">
			<div class="span4 table">
				<div class="image-container table-cell">
					<div class="image">
						<div class="header">
							<div class="logo">
								<span></span>
							</div>
							<div class="search">
								<span class="searchtext"></span>
								<span class="searchbutton blue"></span>
							</div>
						</div>
						<div class="tabbar fillorange">
							<span class="active"></span>
						</div>
						<div class="body">
							<div>
								<span class="gadget"></span>
								<span class="gadget"></span>
								<span class="gadget"></span>
							</div>
							<div>
								<span class="gadget"></span>
								<span class="gadget"></span>
								<span class="gadget"></span>
							</div>
						</div>
						<div class="title">
							<span>%{alb_orange}</span>
						</div>
					</div>
					
				</div>
			</div>
			<div class="span5">
				<div class="json-container">
					<textarea readonly="readonly">
{
  "tabbar":{
    "fontColor":"#ffffff",
    "backgroundColor":"#d35400",
    "backgroundGradationTop":"#db5800",
    "backgroundGradationBottom":"#cc5200",
    "borderBottom":"#994000",
    "iconClass":"white",
    "activetab":{
        "type":"fill",
        "fontColor":"#ffffff",
        "backgroundColor":"#e67e22",
        "fontWeight":"bold"
    }
  },
  "commandbar":{
    "searchFormButton":{
      "backgroundColor":"##0085be",
      "backgroundGradationTop":"#008bc7",
      "backgroundGradationBottom":"#0081b8",
      "iconClass":"gray"
    }
  }
}</textarea>
				</div>
			</div>
		</section>
		<section class="theme-sample clearfix">
			<div class="span4 table">
				<div class="image-container table-cell">
					<div class="image">
						<div class="header">
							<div class="logo">
								<span></span>
							</div>
							<div class="search">
								<span class="searchtext"></span>
								<span class="searchbutton red"></span>
							</div>
						</div>
						<div class="tabbar fillnavy">
							<span class="active"></span>
						</div>
						<div class="body">
							<div>
								<span class="gadget"></span>
								<span class="gadget"></span>
								<span class="gadget"></span>
							</div>
							<div>
								<span class="gadget"></span>
								<span class="gadget"></span>
								<span class="gadget"></span>
							</div>
						</div>
						<div class="title">
							<span>%{alb_navy}</span>
						</div>
					</div>
					
				</div>
			</div>
			<div class="span5">
				<div class="json-container">
					<textarea readonly="readonly">
{
  "tabbar":{
    "fontColor":"#ffffff",
    "backgroundColor":"#2c3e50",
    "backgroundGradationTop":"#304357",
    "backgroundGradationBottom":"#273747",
    "borderBottom":"#11161c",
    "iconClass":"white",
    "activetab":{
        "type":"fill",
        "fontColor":"#ffffff",
        "backgroundColor":"#3c566f",
        "fontWeight":"bold"
    }
  },
  "commandbar":{
    "searchFormButton":{
      "backgroundColor":"#e74c3c",
      "backgroundGradationTop":"#f0503e",
      "backgroundGradationBottom":"#e04b3a",
      "iconClass":"gray"
    }
  }
}</textarea>
				</div>
			</div>
		</section>
		<section class="theme-sample clearfix">
			<div class="span4 table">
				<div class="image-container table-cell">
					<div class="image">
						<div class="header">
							<div class="logo">
								<span></span>
							</div>
							<div class="search">
								<span class="searchtext"></span>
								<span class="searchbutton yellowgreen"></span>
							</div>
						</div>
						<div class="tabbar fillpurple">
							<span class="active"></span>
						</div>
						<div class="body">
							<div>
								<span class="gadget"></span>
								<span class="gadget"></span>
								<span class="gadget"></span>
							</div>
							<div>
								<span class="gadget"></span>
								<span class="gadget"></span>
								<span class="gadget"></span>
							</div>
						</div>
						<div class="title">
							<span>%{alb_purple}</span>
						</div>
					</div>
					
				</div>
			</div>
			<div class="span5">
				<div class="json-container">
					<textarea readonly="readonly">
{
  "tabbar":{
    "fontColor":"#ffffff",
    "backgroundColor":"#663399",
    "backgroundGradationTop":"#6b35a1",
    "backgroundGradationBottom":"#613091",
    "borderBottom":"#472369",
    "iconClass":"white",
    "activetab":{
        "type":"fill",
        "fontColor":"#ffffff",
        "backgroundColor":"#7a3db8",
        "fontWeight":"bold"
    }
  },
  "commandbar":{
    "searchFormButton":{
      "backgroundColor":"#bacf02",
      "backgroundGradationTop":"#c3d900",
      "backgroundGradationBottom":"#bbcf06",
      "iconClass":"gray"
    }
  }
}</textarea>
				</div>
			</div>
		</section>
		<section class="theme-sample clearfix">
			<div class="span4 table">
				<div class="image-container table-cell">
					<div class="image">
						<div class="header">
							<div class="logo">
								<span></span>
							</div>
							<div class="search transparent">
								<span class="searchtext"></span>
								<span class="searchbutton"></span>
							</div>
						</div>
						<div class="tabbar borderblue">
							<span class="active"></span>
						</div>
						<div class="body">
							<div>
								<span class="gadget"></span>
								<span class="gadget"></span>
								<span class="gadget"></span>
							</div>
							<div>
								<span class="gadget"></span>
								<span class="gadget"></span>
								<span class="gadget"></span>
							</div>
						</div>
						<div class="title">
							<span>%{alb_simpleBlue}</span>
						</div>
					</div>
					
				</div>
			</div>
			<div class="span5">
				<div class="json-container">
					<textarea readonly="readonly">
{
  "tabbar":{
    "fontColor":"#404040",
    "backgroundColor":"#ffffff",
    "backgroundGradationTop":"",
    "backgroundGradationBottom":"",
    "borderBottom":"#ffffff",
    "iconClass":"gray",
    "activetab":{
        "type":"border",
        "fontColor":"#404040",
        "backgroundColor":"#0097d8",
        "fontWeight":"bold"
    }
  },
  "commandbar":{
    "searchFormButton":{
      "backgroundColor":"transparent",
      "backgroundGradationTop":"",
      "backgroundGradationBottom":"",
      "iconClass":"gray"
    }
  }
}</textarea>
				</div>
			</div>
		</section>
		<section class="theme-sample clearfix">
			<div class="span4 table">
				<div class="image-container table-cell">
					<div class="image">
						<div class="header">
							<div class="logo">
								<span></span>
							</div>
							<div class="search transparent">
								<span class="searchtext"></span>
								<span class="searchbutton"></span>
							</div>
						</div>
						<div class="tabbar borderred">
							<span class="active"></span>
						</div>
						<div class="body">
							<div>
								<span class="gadget"></span>
								<span class="gadget"></span>
								<span class="gadget"></span>
							</div>
							<div>
								<span class="gadget"></span>
								<span class="gadget"></span>
								<span class="gadget"></span>
							</div>
						</div>
						<div class="title">
							<span>%{alb_simpleRed}</span>
						</div>
					</div>
					
				</div>
			</div>
			<div class="span5">
				<div class="json-container">
					<textarea readonly="readonly">
{
  "tabbar":{
    "fontColor":"#404040",
    "backgroundColor":"#ffffff",
    "backgroundGradationTop":"",
    "backgroundGradationBottom":"",
    "borderBottom":"#ffffff",
    "iconClass":"gray",
    "activetab":{
        "type":"border",
        "fontColor":"#404040",
        "backgroundColor":"#e74c3c",
        "fontWeight":"bold"
    }
  },
  "commandbar":{
    "searchFormButton":{
      "backgroundColor":"transparent",
      "backgroundGradationTop":"",
      "backgroundGradationBottom":"",
      "iconClass":"gray"
    }
  }
}</textarea>
				</div>
			</div>
		</section>
		<section class="theme-sample clearfix">
			<div class="span4 table">
				<div class="image-container table-cell">
					<div class="image">
						<div class="header">
							<div class="logo">
								<span></span>
							</div>
							<div class="search transparent">
								<span class="searchtext"></span>
								<span class="searchbutton"></span>
							</div>
						</div>
						<div class="tabbar bordergreen">
							<span class="active"></span>
						</div>
						<div class="body">
							<div>
								<span class="gadget"></span>
								<span class="gadget"></span>
								<span class="gadget"></span>
							</div>
							<div>
								<span class="gadget"></span>
								<span class="gadget"></span>
								<span class="gadget"></span>
							</div>
						</div>
						<div class="title">
							<span>%{alb_simpleGreen}</span>
						</div>
					</div>
				</div>
			</div>
			<div class="span5">
				<div class="json-container">
					<textarea readonly="readonly">
{
  "tabbar":{
    "fontColor":"#404040",
    "backgroundColor":"#ffffff",
    "backgroundGradationTop":"",
    "backgroundGradationBottom":"",
    "borderBottom":"#ffffff",
    "iconClass":"gray",
    "activetab":{
        "type":"border",
        "fontColor":"#404040",
        "backgroundColor":"#27a562",
        "fontWeight":"bold"
    }
  },
  "commandbar":{
    "searchFormButton":{
      "backgroundColor":"transparent",
      "backgroundGradationTop":"",
      "backgroundGradationBottom":"",
      "iconClass":"gray"
    }
  }
}</textarea>
				</div>
			</div>
		</section>
	</div>
</div>
</body>
</html>
