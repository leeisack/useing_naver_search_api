package example.com.search;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class InternetWebView extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.internet_webview);

        WebView webView = (WebView) findViewById(R.id.webview);

        Intent intent = getIntent();
        String blog_url = intent.getExtras().getString("blog_url");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(blog_url);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());



    }

}