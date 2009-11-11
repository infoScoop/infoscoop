AjaxPool 
[概要]
Ajaxを利用したプログラミング際に、通信オブジェクトの多用により
（１）メモリリークの発生
（２）パフォーマンスの低下
など問題を回避するため、Ajax通信オブジェクトのPoolingを実装します。

[利用ガイド]

AjaxPoolのオプション設定：

AjaxPoolが設定可能なオプションは下記の三つになります。

（１）poolSize: AjaxPoolのサイズを設定します。要するに同時接続可能な数をコントロールします。
（２）retryInterval: 全て接続が占有されている場合に、待ち時間を設定します。
（３）timeout: １つのRequestのTimeout時間を設定します。

AjaxPoolのAPI説明：

　AjaxPoolに公開するメソッドは、基本的にInvokeメソッドのみになります。
Invokeメソッド：
　構文：
　　AjaxRequest.invoke(url, method, isAsync, onComplete, headers, parameters, contents)

  
   (1)　url ：request先のURLを設定します。
   (2)  method: POST または　GETメソッドを設定します。
　 (3)  isAsyanc: true - 同期通信
　　　　　　　　　false - 非同期通信
   (4)  onComplete: CallBackメソッドへのポイント
　　　　　AjaxRequestは通信を行い、CallBackする仕組みになります。
　　　　　下記のデータ構造のデータをパラメータとしてCallBackを呼び出します。
　　　　　{ success: true, text: responseText, xml: responseXML, json: null, status: responseStatus, message: errorMessage}
          success: true - 通信が成功した
　　　　　　　　　 false - 通信が失敗した
　　　　　text : レスポンスのテクスト部
　　　　　xml : レスポンスのXML部
　　　　　json : レスポンスのcontent-typeがapplication\/x-javascriptの場合に、jsonオブジェクトに変換した結果
　　　　　status: 通信のStatus
　　　　　message: エラーが起きる場合に、エラーメッセージ

　 (5) headers: Reuestに追加するHeader情報
   (6) parameters: GETメソッドを使用する際に、クエリーストリング
   (7) contents: POSTメソッドを使用する際に、Postデータ

例：

AjaxPoolを利用した非同期通信

			function onCompleteService (result) {
				var div = $("console");
				if ( result.status != 200) {

					div.innerHTML +=  "status : " + result.status  + "<br/>";
				}else {
					div.innerHTML += result.text + "<br/>";
		        }
			
			}
			function testCustomerService() {
				AjaxRequest.invoke ( "/ajaxlet.sandbox/service", "get", true, onCompleteService, null, "name=CustomerService"  );  
			}
