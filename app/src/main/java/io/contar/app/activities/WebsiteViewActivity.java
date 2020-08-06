package io.contar.app.activities;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.warhammer.defience.R;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebsiteViewActivity extends AppCompatActivity
{

    @BindView(R.id.webViewGame)
    WebView mWebview;

    private WebView mWebviewPop;
    private Context mContext;
    private String url;
    private boolean alreadyShown = false;
    private AlertDialog builder;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_website_view);
        ButterKnife.bind(this);

        WebSettings webSettings = mWebview.getSettings();
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        //webSettings.setAllowContentAccess(true);
        //webSettings.setAllowFileAccess(true);
        //webSettings.setDatabaseEnabled(true);

        mWebview.getSettings().setSaveFormData(true);
        mWebview.setWebViewClient(new UriWebViewClient());
        mWebview.setWebChromeClient(new UriChromeClient());


        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            cookieManager.setAcceptThirdPartyCookies(mWebview, true);
        }

        //mWebview.loadUrl(target_url);

        mContext = this.getApplicationContext();

        ClipboardManager.OnPrimaryClipChangedListener mPrimaryChangeListener = new ClipboardManager.OnPrimaryClipChangedListener()
        {
            public void onPrimaryClipChanged()
            {

                ClipboardManager clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData.Item item = Objects.requireNonNull(clipBoard.getPrimaryClip()).getItemAt(0);
                final String pasteData = item.getText().toString();

                if (pasteData.contains(url) && pasteData.contains("/p/") && !alreadyShown)
                {
                    alreadyShown = true;
                    hideKeyboard(WebsiteViewActivity.this);
                    new MaterialDialog.Builder(WebsiteViewActivity.this)
                            .title("Share your profile")
                            .content("It looks like you copied your profile link. Would you like to share it?")
                            .positiveText("Yes. Share It!")
                            .canceledOnTouchOutside(false)
                            .cancelable(false)
                            .titleColor(Color.parseColor("#212121"))
                            .contentColor(Color.parseColor("#212121"))
                            .negativeColor(Color.parseColor("#212121"))
                            .positiveColor(Color.parseColor("#2196F3"))
                            .backgroundColor(Color.parseColor("#ffffff"))
                            .onPositive(new MaterialDialog.SingleButtonCallback()
                            {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
                                {
                                    alreadyShown = false;
                                    confirmShare(pasteData);
                                }
                            })
                            .negativeText("No")
                            .onNegative(new MaterialDialog.SingleButtonCallback()
                            {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which)
                                {
                                    alreadyShown = false;
                                }
                            })
                            .show();
                }

            }
        };

        ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.addPrimaryClipChangedListener(mPrimaryChangeListener);


        url = "https://contar.io";

        Intent appLinkIntent = getIntent();
        Uri appLinkData = appLinkIntent.getData();

        /*
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        WebSettings webSettings = webViewGame.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);

        webViewGame.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                System.out.println("your current url when webpage loading.." + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                System.out.println("your current url when webpage loading.. finish" + url);
                super.onPageFinished(view, url);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onLoadResource(view, url);
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("urlClicked", url.toString());
                if (url.contains("https://contar.io/")) {
                    return false;
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
            }
        });*/
        if (appLinkData != null)
        {
            mWebview.loadUrl(url + appLinkData.getPath());
        } else
        {
            mWebview.loadUrl(url);
        }
    }

    public void hideKeyboard(Activity activity)
    {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null)
        {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void confirmShare(String pasteData)
    {

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out my social networks on contar.io at the following link: " + pasteData);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);

    }

    @Override
    public void onBackPressed()
    {
        if (mWebview.canGoBack())
        {
            mWebview.goBack();
        } else
        {
            finish();
        }
    }

    private class UriWebViewClient extends WebViewClient
    {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {

            String host = Uri.parse(url).getHost();
            assert host != null;
            if (url.contains("https://contar.io/"))
            {
                return false;
            } else if (host.equals("m.facebook.com"))
            {
                return false;
            } else
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                return true;
            }
            //Log.d("shouldOverrideUrlLoading", url);
            /*if (host.equals(target_url_prefix))
            {
                // This is my web site, so do not override; let my WebView load
                // the page
                if(mWebviewPop!=null)
                {
                    mWebviewPop.setVisibility(View.GONE);
                    mContainer.removeView(mWebviewPop);
                    mWebviewPop=null;
                }
                return false;
            }*/
        }


        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                       SslError error)
        {
            Log.d("onReceivedSslError", "onReceivedSslError");
            //super.onReceivedSslError(view, handler, error);
        }
    }

    class UriChromeClient extends WebChromeClient
    {

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog,
                                      boolean isUserGesture, Message resultMsg)
        {
            mWebviewPop = new WebView(mContext);
            mWebviewPop.setVerticalScrollBarEnabled(false);
            mWebviewPop.setHorizontalScrollBarEnabled(false);
            mWebviewPop.setWebViewClient(new UriWebViewClient());
            mWebviewPop.setWebChromeClient(new UriChromeClient());
            mWebviewPop.getSettings().setSaveFormData(true);
            //mWebviewPop.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            // create an AlertDialog.Builder
            //the below did not give me .dismiss() method . See : https://stackoverflow.com/questions/14853325/how-to-dismiss-alertdialog-in-android

//            AlertDialog.Builder builder;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
//            } else {
//                builder = new AlertDialog.Builder(MainActivity.this);
//            }

            // set the WebView as the AlertDialog.Builder’s view

            builder = new AlertDialog.Builder(WebsiteViewActivity.this).create();


            builder.setTitle("");
            builder.setView(mWebviewPop);

            builder.show();
            Objects.requireNonNull(builder.getWindow()).clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);


            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                cookieManager.setAcceptThirdPartyCookies(mWebviewPop, true);
            }

            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(mWebviewPop);
            resultMsg.sendToTarget();

            return true;
        }


        @Override
        public void onCloseWindow(WebView window)
        {

            //Toast.makeText(mContext,"onCloseWindow called",Toast.LENGTH_SHORT).show();


            try
            {
                mWebviewPop.destroy();
            } catch (Exception ignored)
            {

            }

            try
            {
                builder.dismiss();

            } catch (Exception ignored)
            {

            }


        }

    }

}
