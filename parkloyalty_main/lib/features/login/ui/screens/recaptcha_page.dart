import 'package:flutter/material.dart';
import 'package:webview_flutter/webview_flutter.dart';

class RecaptchaPage extends StatefulWidget {
  const RecaptchaPage({super.key});

  @override
  State<RecaptchaPage> createState() => _RecaptchaPageState();
}

class _RecaptchaPageState extends State<RecaptchaPage> {
  late WebViewController controller;

  @override
  void initState() {
    super.initState();

    controller = WebViewController()
      ..setJavaScriptMode(JavaScriptMode.unrestricted)
      ..addJavaScriptChannel(
        'Recaptcha',
        onMessageReceived: (msg) {
          Navigator.pop(context, msg.message);
        },
      )
      ..loadHtmlString(_html);
  }

  String get _html => '''
<!DOCTYPE html>
<html>
  <head>
    <script src="https://www.google.com/recaptcha/api.js"></script>
    <script>
      function onSubmit(token) {
        Recaptcha.postMessage(token);
      }
    </script>
  </head>
  <body>
    <form>
      <button class="g-recaptcha"
        data-sitekey="YOUR_SITE_KEY"
        data-callback="onSubmit">
        Verify
      </button>
    </form>
  </body>
</html>
''';

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Verify")),
      body: WebViewWidget(controller: controller),
    );
  }
}
