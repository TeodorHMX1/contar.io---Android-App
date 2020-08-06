package io.contar.app.activities

import android.app.Activity
import android.content.ClipboardManager
import android.content.ClipboardManager.OnPrimaryClipChangedListener
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.*
import android.webkit.WebView.WebViewTransport
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.afollestad.materialdialogs.MaterialDialog
import com.warhammer.defience.R
import java.util.*

class WebsiteViewActivity : AppCompatActivity() {
    @JvmField
    @BindView(R.id.webViewGame)
    var mWebview: WebView? = null
    private var mWebviewPop: WebView? = null
    private var mContext: Context? = null
    private var url: String? = null
    private var alreadyShown = false
    private var builder: AlertDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_website_view)
        ButterKnife.bind(this)
        val webSettings = mWebview!!.settings
        webSettings.setAppCacheEnabled(true)
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.setSupportMultipleWindows(true)
        //webSettings.setAllowContentAccess(true);
        //webSettings.setAllowFileAccess(true);
        //webSettings.setDatabaseEnabled(true);
        mWebview!!.settings.saveFormData = true
        mWebview!!.webViewClient = UriWebViewClient()
        mWebview!!.webChromeClient = UriChromeClient()
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(mWebview, true)
        }

        //mWebview.loadUrl(target_url);
        mContext = this.applicationContext
        val mPrimaryChangeListener = OnPrimaryClipChangedListener {
            val clipBoard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val item = Objects.requireNonNull(clipBoard.primaryClip).getItemAt(0)
            val pasteData = item.text.toString()
            if (pasteData.contains(url!!) && pasteData.contains("/p/") && !alreadyShown) {
                alreadyShown = true
                hideKeyboard(this@WebsiteViewActivity)
                MaterialDialog.Builder(this@WebsiteViewActivity)
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
                        .onPositive { dialog, which ->
                            alreadyShown = false
                            confirmShare(pasteData)
                        }
                        .negativeText("No")
                        .onNegative { dialog, which -> alreadyShown = false }
                        .show()
            }
        }
        val clipboard = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.addPrimaryClipChangedListener(mPrimaryChangeListener)
        url = "https://contar.io"
        val appLinkIntent = intent
        val appLinkData = appLinkIntent.data

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
        });*/if (appLinkData != null) {
            mWebview!!.loadUrl(url + appLinkData.path)
        } else {
            mWebview!!.loadUrl(url)
        }
    }

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun confirmShare(pasteData: String) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out my social networks on contar.io at the following link: $pasteData")
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }

    override fun onBackPressed() {
        if (mWebview!!.canGoBack()) {
            mWebview!!.goBack()
        } else {
            finish()
        }
    }

    private inner class UriWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            val host = Uri.parse(url).host!!
            return if (url.contains("https://contar.io/")) {
                false
            } else if (host == "m.facebook.com") {
                false
            } else {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                startActivity(intent)
                true
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

        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler,
                                        error: SslError) {
            Log.d("onReceivedSslError", "onReceivedSslError")
            //super.onReceivedSslError(view, handler, error);
        }
    }

    internal inner class UriChromeClient : WebChromeClient() {
        override fun onCreateWindow(view: WebView, isDialog: Boolean,
                                    isUserGesture: Boolean, resultMsg: Message): Boolean {
            mWebviewPop = WebView(mContext)
            mWebviewPop!!.isVerticalScrollBarEnabled = false
            mWebviewPop!!.isHorizontalScrollBarEnabled = false
            mWebviewPop!!.webViewClient = UriWebViewClient()
            mWebviewPop!!.webChromeClient = UriChromeClient()
            mWebviewPop!!.settings.saveFormData = true
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
            builder = AlertDialog.Builder(this@WebsiteViewActivity).create()
            val cookieManager = CookieManager.getInstance()
            cookieManager.setAcceptCookie(true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cookieManager.setAcceptThirdPartyCookies(mWebviewPop, true)
            }
            val transport = resultMsg.obj as WebViewTransport
            transport.webView = mWebviewPop
            resultMsg.sendToTarget()
            return true
        }

        override fun onCloseWindow(window: WebView) {

            //Toast.makeText(mContext,"onCloseWindow called",Toast.LENGTH_SHORT).show();
            try {
                mWebviewPop!!.destroy()
            } catch (ignored: Exception) {
            }
            try {
                builder!!.dismiss()
            } catch (ignored: Exception) {
            }
        }
    }
}