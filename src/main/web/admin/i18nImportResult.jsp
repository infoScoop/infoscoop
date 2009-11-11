<!doctype HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="org.infoscoop.admin.web.I18NImport" %>

<%
	List errorList = (List)request.getAttribute("errorList");
	Map countMap = (Map)request.getAttribute("countMap");
	String errorMessage = (String)request.getAttribute("errorMessage");
%>

<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Cache-Control" content="no-cache">
    <meta http-equiv="Expires" content="Thu, 01 Dec 1994 16:00:00 GMT">

	<title>import result</title>
	<link rel="stylesheet" type="text/css" href="./skin/admin.css">
	
	<style>
		.errorHeader{
			background-color:#DCDCDC;
			border: 1px solid #FFF;
			text-align:center;
			float:left;
		}
		.errorRow{
			float:left;
			overflow:hidden;
			word-break:break-all;
		}
	</style>
</head>
<body>
	<div>
		<%
			if(errorList.size() > 0){	// 失敗時
		%>
				<div style="width:100%;">エラーが検出されたためインポート処理は実行されませんでした。</div>
				<br>
				
				<!-- エラー一覧のヘッダ -->
				<div style="width:100%;">
					<div class="errorHeader" style="width:7%;">行</div>
					<div class="errorHeader" style="width:30%;">ID</div>
					<div class="errorHeader" style="width:60%;">メッセージ</div>
				</div>
		<%
			}else{	// 成功時
		%>
				</br>
				<div style="width:100%;"><%= countMap.get("insertCount") %>件のデータが登録されました。</div>
				<div style="width:100%;"><%= countMap.get("updateCount") %>件のデータが更新されました。</div>
				</br>
		<%
			}
		%>
		
		<%
			// エラー一覧の出力
			I18NImport importObj;
			for(Iterator ite=errorList.iterator();ite.hasNext();){
				importObj = (I18NImport)ite.next();
				
				int lineNumber = importObj.getLineNumber();
				String id = importObj.getId();
				String messageId = importObj.getStatusMessageId();
				String resultMessage = "";
				if(I18NImport.I18N_IMPORT_MESSAGE_EMPTY.equals(messageId)){
					resultMessage = "メッセージが空のため登録されませんでした。";
				}
				else if(I18NImport.I18N_IMPORT_ID_EMPTY.equals(messageId)){
					resultMessage = "メッセージIDが空のため登録されませんでした。";
				}
				else if(I18NImport.I18N_IMPORT_ID_INVALID.equals(messageId)){
					resultMessage = "メッセージIDに使用できる文字は半角英数とアンダースコア「_」のみです。";
				}
				else if(I18NImport.I18N_IMPORT_DEFAULT_NOTFOUND.equals(messageId)){
					resultMessage = "メッセージIDがデフォルトロケール(ALL_ALL)に登録されていないため、登録されませんでした。";
				}
				else if(I18NImport.I18N_IMPORT_ID_INVALID_LENGTH.equals(messageId)){
					resultMessage = "メッセージIDの長さが制限を越えています。最大512バイトまでです。";
				}
				else if(I18NImport.I18N_IMPORT_MESSAGE_INVALID_LENGTH.equals(messageId)){
					resultMessage = "メッセージの長さが制限を越えています。最大2048バイトまでです。";
				}
		%>
		
		<div style="width:100%;">
			<div class="errorRow" style="text-align:center;width:7%;"title="<%= lineNumber %>"><%= lineNumber %></div>
			<div class="errorRow" style="text-align:center;width:30%;" title="<%= id %>"><%= id %>&nbsp;</div>
			<div class="errorRow" style="width:60%;" title="<%= resultMessage %>"><%= resultMessage %></div>
		</div>
		
		<%
			}
		%>
	</div>
</body>
</html>