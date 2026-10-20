
package it.imieimondi.app;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.ValueCallback;
import android.webkit.JavascriptInterface;
import android.util.Base64;
import java.io.OutputStream;

public class MainActivity extends Activity {
    private WebView web;
    private ValueCallback<Uri[]> fileCallback;

    private static final int FILE_REQUEST = 51;
    private static final int BACKUP_REQUEST = 52;

    private byte[] pendingBackup;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        web = new WebView(this);
        web.setWebViewClient(new WebViewClient());

        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(
                WebView view,
                ValueCallback<Uri[]> callback,
                FileChooserParams params
            ) {
                if (fileCallback != null)
                    fileCallback.onReceiveValue(null);

                fileCallback = callback;

                try {
                    startActivityForResult(
                        params.createIntent(),
                        FILE_REQUEST
                    );
                    return true;
                } catch (Exception e) {
                    fileCallback = null;
                    return false;
                }
            }
        });

        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(true);

        web.addJavascriptInterface(
            new BackupBridge(),
            "AndroidBackup"
        );

        setContentView(web);

        web.loadUrl(
            "https://giovannisak00.github.io/i-miei-mondi/"
        );
    }

    public class BackupBridge {
        @JavascriptInterface
        public void saveBackup(String base64Data, String filename) {
            if (!android.util.Patterns.WEB_URL.matcher(
                web.getUrl() == null ? "" : web.getUrl()
            ).matches()) return;

            runOnUiThread(() -> {
                try {
                    pendingBackup = Base64.decode(
                        base64Data,
                        Base64.DEFAULT
                    );

                    Intent intent = new Intent(
                        Intent.ACTION_CREATE_DOCUMENT
                    );

                    intent.addCategory(
                        Intent.CATEGORY_OPENABLE
                    );

                    intent.setType("application/json");
                    intent.putExtra(
                        Intent.EXTRA_TITLE,
                        "i-miei-mondi-backup.json"
                    );

                    startActivityForResult(
                        intent,
                        BACKUP_REQUEST
                    );
                } catch (Exception e) {
                    pendingBackup = null;
                }
            });
        }
    }

    @Override
    protected void onActivityResult(
        int requestCode,
        int resultCode,
        Intent data
    ) {
        super.onActivityResult(
            requestCode,
            resultCode,
            data
        );

        if (requestCode == FILE_REQUEST
            && fileCallback != null) {

            fileCallback.onReceiveValue(
                resultCode == RESULT_OK
                ? WebChromeClient.FileChooserParams
                    .parseResult(resultCode, data)
                : null
            );

            fileCallback = null;
        }

        if (requestCode == BACKUP_REQUEST) {
            if (resultCode == RESULT_OK
                && data != null
                && pendingBackup != null) {

                try (OutputStream output =
                    getContentResolver().openOutputStream(
                        data.getData()
                    )) {

                    if (output == null)
                        throw new Exception("Output unavailable");

                    output.write(pendingBackup);
                    output.flush();

                } catch (Exception e) {
                    android.util.Log.e(
                        "I miei mondi",
                        "Errore salvataggio backup",
                        e
                    );
                }
            }

            pendingBackup = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (web.canGoBack()) {
            web.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
