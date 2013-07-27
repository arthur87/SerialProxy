SerialProxy
===========
シリアルポートとアプリケーションの間でソケット通信を行います。

インストール
------------
Macの場合

/lib/内のRXTXcomm.jarとlibrxtxSerial.jnilibを/Library/Java/Extensions/にコピーします。
/var/に移動しlockディレクトリを作成し、lockディレクトリのパーミッションを777に設定します。


Windowsの場合

/lib/内のRXTXcomm.jarをJavaディレクトリ（デフォルトのインストール先 C://Program Files/Java）内のjdk/jre/lib/ext/にコピーします。
/lib/内のrxtxSerial.dllとrxtxParallel.dllをJavaディレクトリ（デフォルトのインストール先 C://Program Files/Java）内のjdk/bin/にコピーします。


RXTXについて
----------
http://rxtx.qbang.org/wiki/index.php/Download



