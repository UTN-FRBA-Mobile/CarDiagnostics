package com.cardiag.activity;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.cardiag.R;
import com.cardiag.models.solutions.NoErrorSolution;
import com.cardiag.models.solutions.NoSolution;
import com.cardiag.models.solutions.Solution;

public class ErrorSearchActivity extends Activity {
    private ProgressBar progress;
    private String errorCode;
    private EditText edtSearch;
    private WebView web1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_search);

        web1=(WebView)findViewById(R.id.webSearch);
        edtSearch= (EditText) findViewById(R.id.edtSearch);
        progress = (ProgressBar) findViewById(R.id.progressBarSearch);

        Bundle bundle = getIntent().getExtras();
        NoErrorSolution dato= (NoErrorSolution) bundle.get("solution");

          errorCode= dato.getError();
        progress.setVisibility(View.GONE);

        web1.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                progress.setVisibility(View.GONE);
            }
        });
    }

    public void searchError(View v){
        progress.setVisibility(View.VISIBLE);
          web1.loadUrl("https://www.google.com.ar/search?q=" + errorCode + "+" + edtSearch.getText());
        WebSettings webSettings=web1.getSettings();
       // webSettings.setJavaScriptEnabled(true);
    }

}
