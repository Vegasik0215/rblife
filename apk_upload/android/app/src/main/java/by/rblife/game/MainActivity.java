package by.rblife.game;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/** RB Life — приложение открывает игру с сайта (GitHub Pages). */
public class MainActivity extends Activity {

    private static final String GAME_URL = "https://vegasik0215.github.io/rblife/";
    private static final String HOST = "vegasik0215.github.io";
    private WebView web;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        Window w = getWindow();
        if (Build.VERSION.SDK_INT >= 21) {
            w.setStatusBarColor(Color.parseColor("#14171d"));
            w.setNavigationBarColor(Color.parseColor("#12151b"));
        }

        web = new WebView(this);
        web.setBackgroundColor(Color.parseColor("#0e1014"));
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);          // здесь хранится вход в аккаунт
        s.setDatabaseEnabled(true);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        s.setTextZoom(100);
        s.setUserAgentString(s.getUserAgentString() + " RBLifeApp/1.0");

        web.setWebChromeClient(new WebChromeClient());   // окна подтверждения (confirm)
        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest req) {
                return openOutside(req.getUrl().toString());
            }

            @Override
            @SuppressWarnings("deprecation")
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return openOutside(url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError err) {
                if (req.isForMainFrame()) showOffline();
            }

            @Override
            @SuppressWarnings("deprecation")
            public void onReceivedError(WebView view, int code, String desc, String url) {
                showOffline();
            }
        });

        setContentView(web);
        if (state != null) web.restoreState(state);
        else web.loadUrl(GAME_URL);
    }

    /** Ссылки на другие сайты (например, t.me на бота) открываем в Telegram/браузере. */
    private boolean openOutside(String url) {
        Uri uri = Uri.parse(url);
        if (HOST.equals(uri.getHost())) return false;
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (Exception ignored) {
        }
        return true;
    }

    private void showOffline() {
        String html = "<html><head><meta name='viewport' content='width=device-width,initial-scale=1'></head>"
                + "<body style='margin:0;background:#0e1014;color:#eef1f6;font-family:sans-serif;display:flex;"
                + "align-items:center;justify-content:center;height:100vh;text-align:center'>"
                + "<div><div style='font-size:56px'>📡</div><h2>Нет интернета</h2>"
                + "<p style='color:#8b93a3'>Проверь подключение и попробуй снова</p>"
                + "<button onclick=\"location.href='" + GAME_URL + "'\" style='background:#f5c542;border:0;"
                + "border-radius:12px;padding:14px 28px;font-size:16px;font-weight:bold'>Повторить</button>"
                + "</div></body></html>";
        web.loadDataWithBaseURL(GAME_URL, html, "text/html", "utf-8", null);
    }

    @Override
    public void onBackPressed() {
        if (web != null && web.canGoBack()) web.goBack();
        else super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        if (web != null) web.saveState(out);
    }
}
