package com.cardiag.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.cardiag.R;
import com.cardiag.models.solutions.NoErrorSolution;
import com.cardiag.models.solutions.NoSolution;
import com.cardiag.models.solutions.Solution;

public class ErrorSearchActivity extends AppCompatActivity {
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
        web1.setBackgroundColor(Color.TRANSPARENT);

        web1.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                progress.setVisibility(View.GONE);
            }
        });
        getSupportActionBar().setTitle(getString(R.string.app_name));
    }

    public void searchError(View v){
        progress.setVisibility(View.VISIBLE);
          web1.loadUrl("http://www.google.com.ar/search?q=" + errorCode + "+" + edtSearch.getText());
        web1.setBackgroundColor(Color.TRANSPARENT);
        WebSettings webSettings=web1.getSettings();
       // webSettings.setJavaScriptEnabled(true);

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
