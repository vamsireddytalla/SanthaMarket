package com.talla.santhamarket.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.material.snackbar.Snackbar;
import com.talla.santhamarket.R;
import com.talla.santhamarket.databinding.ActivityWebViewBinding;
import com.talla.santhamarket.models.OrderModel;
import com.talla.santhamarket.utills.CheckInternet;

public class WebViewActivity extends AppCompatActivity
{
  private ActivityWebViewBinding binding;
  private OrderModel orderModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityWebViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding.backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Bundle bundle=getIntent().getExtras();
        if (bundle!=null)
        {
            orderModel= (OrderModel) bundle.getSerializable(getResources().getString(R.string.order_status));
            binding.webview.getSettings().setJavaScriptEnabled(true);
            binding.webview.getSettings().setBuiltInZoomControls(true);
            binding.webview.getSettings().setDisplayZoomControls(false);
            binding.webview.setWebChromeClient(new WebChromeClient());
            String url=orderModel.getWebUrl();
            if (url!=null && !url.isEmpty())
            {
                binding.webview.loadUrl(url);
                binding.webview.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        binding.webview.loadUrl("javascript:(function() { " +
                                "document.querySelector('[role=\"toolbar\"]').remove();})()");
                        if (view.getTitle().equals(""))
                        {
                            reloadWeb();
                        }
                        else {
                            binding.progressCircle.setVisibility(View.GONE);
                        }
                    }
                });
            }else {
                binding.webview.setVisibility(View.GONE);
                binding.progressCircle.setVisibility(View.GONE);
                binding.lottie.setVisibility(View.VISIBLE);
                binding.toolbarTitle.setText("Order Packing");
            }


        }
    }



    public void reloadWeb()
    {
        if (CheckInternet.checkInternet(this))
        {
            binding.webview.clearCache(true);
            binding.webview.reload();
            binding.webview.loadUrl(orderModel.getWebUrl());
        }
        else {
            Snackbar.make(findViewById(android.R.id.content), "Check Internet", Snackbar.LENGTH_LONG).show();

        }
    }



}