package teabar.ph.com.teabar.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.MessageDialog;
import com.facebook.share.widget.ShareDialog;

import butterknife.BindView;
import butterknife.OnClick;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.util.ToastUtil;

public class ShareActivity extends BaseActivity {

    @BindView(R.id.bt_share)
    Button   bt_share;
    @BindView(R.id.bt_share1)
    Button   bt_share1;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_share;
    }

    @Override
    public void initView(View view) {
        shareDialog = new ShareDialog(this);
        // this part is optional
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {


            @Override
            public void onSuccess(Sharer.Result result) {
               ToastUtil.show(ShareActivity.this,"分享成功",Toast.LENGTH_SHORT) ;
            }

            @Override
            public void onCancel() {
                toast("取消分享");
            }

            @Override
            public void onError(FacebookException error) {
                toast("分享失败");
            }
        });


    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }

    ShareLinkContent linkContent;
    @OnClick({R.id.bt_share,R.id.bt_share1})
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.bt_share:
               Intent fenxiang = new Intent(Intent.ACTION_SEND);
               fenxiang.setType("text/plain");
               fenxiang.putExtra(Intent.EXTRA_TEXT, "http://www.baidu.com");
               startActivity(Intent.createChooser(fenxiang, "P99"));
            break;
           case R.id.bt_share1:
               if (ShareDialog.canShow(ShareLinkContent.class)) {
                   ShareLinkContent linkContent = new ShareLinkContent.Builder()
                           .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
                           .setShareHashtag(new ShareHashtag.Builder().setHashtag("#ConnectTheWorld").build())
                           .setQuote("Connect on a global scale.")
                           .build();
                   shareDialog.show(linkContent);
               }

               break;
       }
    }
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        callbackManager.onActivityResult(requestCode, resultCode, data);
//        startActivity(ShareActivity.class);
    }


}
