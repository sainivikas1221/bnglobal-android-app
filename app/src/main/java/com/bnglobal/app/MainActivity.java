package com.bnglobal.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity {

    private static final String TARGET_URL = "https://www.bnglobal.com.au/";
    private static final String TARGET_HOST_FRAGMENT = "bnglobal.com.au";

    private WebView webView;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayout loadingContainer;
    private ProgressBar progressSpinner;
    private TextView statusTitle;
    private TextView statusMessage;
    private Button retryButton;

    private boolean webViewHasLoadedOnce = false;

    private final BroadcastReceiver connectivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isNetworkAvailable() && !webViewHasLoadedOnce) {
                attemptLoad();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        loadingContainer = findViewById(R.id.loadingContainer);
        progressSpinner = findViewById(R.id.progressSpinner);
        statusTitle = findViewById(R.id.statusTitle);
        statusMessage = findViewById(R.id.statusMessage);
        retryButton = findViewById(R.id.retryButton);

        setupWebView();

        retryButton.setOnClickListener(v -> attemptLoad());
        swipeRefresh.setOnRefreshListener(() -> {
            if (isNetworkAvailable()) {
                webView.reload();
            } else {
                swipeRefresh.setRefreshing(false);
                showNoInternetState();
            }
        });

        // The loading/splash screen is always shown first, even with no internet.
        attemptLoad();
    }

    private void setupWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                String host = uri.getHost();
                if (host != null && host.contains(TARGET_HOST_FRAGMENT)) {
                    return false; // keep in-app
                }
                // External links (e.g. social media) open in the system browser
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                } catch (Exception ignored) {
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webViewHasLoadedOnce = true;
                showWebViewState();
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (request.isForMainFrame()) {
                    webViewHasLoadedOnce = false;
                    swipeRefresh.setRefreshing(false);
                    showLoadErrorState();
                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                // Do not proceed on SSL errors; keep default (blocking) behavior.
                handler.cancel();
                webViewHasLoadedOnce = false;
                showLoadErrorState();
            }
        });
    }

    private void attemptLoad() {
        if (isNetworkAvailable()) {
            showLoadingSpinnerState();
            webView.loadUrl(TARGET_URL);
        } else {
            showNoInternetState();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    // ---- UI state helpers ----

    private void showLoadingSpinnerState() {
        loadingContainer.setVisibility(View.VISIBLE);
        swipeRefresh.setVisibility(View.GONE);
        progressSpinner.setVisibility(View.VISIBLE);
        statusTitle.setVisibility(View.GONE);
        statusMessage.setVisibility(View.GONE);
        retryButton.setVisibility(View.GONE);
    }

    private void showNoInternetState() {
        loadingContainer.setVisibility(View.VISIBLE);
        swipeRefresh.setVisibility(View.GONE);
        progressSpinner.setVisibility(View.GONE);
        statusTitle.setText(R.string.no_internet_title);
        statusMessage.setText(R.string.no_internet_message);
        statusTitle.setVisibility(View.VISIBLE);
        statusMessage.setVisibility(View.VISIBLE);
        retryButton.setVisibility(View.VISIBLE);
    }

    private void showLoadErrorState() {
        loadingContainer.setVisibility(View.VISIBLE);
        swipeRefresh.setVisibility(View.GONE);
        progressSpinner.setVisibility(View.GONE);
        statusTitle.setText(R.string.no_internet_title);
        statusMessage.setText(R.string.load_error_message);
        statusTitle.setVisibility(View.VISIBLE);
        statusMessage.setVisibility(View.VISIBLE);
        retryButton.setVisibility(View.VISIBLE);
    }

    private void showWebViewState() {
        loadingContainer.setVisibility(View.GONE);
        swipeRefresh.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (swipeRefresh.getVisibility() == View.VISIBLE && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectivityReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(connectivityReceiver);
        } catch (IllegalArgumentException ignored) {
        }
    }
}
