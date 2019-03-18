package teabar.ph.com.teabar.activity;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Request;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.util.FacebookHelper;
import teabar.ph.com.teabar.util.HttpUtils;
import teabar.ph.com.teabar.util.NetWorkUtil;
import teabar.ph.com.teabar.util.ToastUtil;


public class LoginActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    MyApplication application;
    @BindView(R.id.et_login_user)
    EditText et_login_user;
    @BindView(R.id.et_login_pasw)
    EditText et_login_pasw;
    @BindView(R.id.tv_bj1)
    TextView tv_bj1;
    SharedPreferences preferences;
    boolean isHideFirst;
    String phone;
    String password;
    LoginButton loginButton;
    CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    //    private SignInButton sign_in_button;
    private Button sign_in_button;
    private static int RC_SIGN_IN=10001;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_login;
    }

    @Override
    public void initView(View view) {
        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        preferences = getSharedPreferences("my", MODE_PRIVATE);
        et_login_user.setText(preferences.getString("phone", ""));
        et_login_pasw.setText(preferences.getString("password", ""));
        progressDialog = new ProgressDialog(this);

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        loginButton.setBackgroundResource(R.drawable.faceloginbj);
        loginButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        loginButton.setCompoundDrawablePadding(0);
        loginButton.setPadding(0, 0, 0, 0);
        loginButton.setText("face");
        // If using in a fragment
//        loginButton.setFragment(LoginActivity.this);

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.i("AlexFB", "facebook登录成功");
                if (loginResult == null) return;
                Log.i("AlexFB", "token是" + loginResult.getAccessToken());
                for (String s : loginResult.getRecentlyGrantedPermissions()) {
                    Log.i("AlexFB", "被授予的权限是::" + s);
                }
                getFacebookUserBasicInfo();//获取用户邮箱，姓名，头像等基本信息
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .build();

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .enableAutoManage(this, this)/* FragmentActivity *//* OnConnectionFailedListener */
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        sign_in_button = findViewById(R.id.sign_in_button);
        sign_in_button.setOnClickListener(this);

    }

    private void getFacebookUserBasicInfo() {
        FacebookHelper.getUserFacebookBasicInfo(new FacebookHelper.FacebookUserInfoCallback() {
            @Override
            public void onCompleted(FacebookHelper.FaceBookUserInfo userInfo) {
                Log.i("Alex", "获取到的facebook用户信息是:::" + userInfo);
                tv_bj1.setText(""+userInfo.id+"......"+userInfo.userName+"....."+userInfo.email);
                getFacebookUserImage(userInfo.id);//获取用户头像
            }

            @Override
            public void onFailed(String reason) {
                Log.i("AlexFB", "获取facebook用户信息失败::" + reason);
                FacebookHelper.signOut();
            }
        });
    }

    private void getFacebookUserImage(String facebookUserId) {
        FacebookHelper.getFacebookUserPictureAsync(facebookUserId, new FacebookHelper.FacebookUserImageCallback() {
            @Override
            public void onCompleted(String imageUrl) {
                //成功获取到了头像之后
                Log.i("Alex", "用户高清头像的下载url是" + imageUrl);
                tv_bj1.setText(""+imageUrl );
            }

            @Override
            public void onFailed(String reason) {
                FacebookHelper.signOut();//如果获取失败了，别忘了将整个登录结果回滚
                Log.i("AlexFB", reason);
            }
        });
    }





    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {


    }
    @OnClick({R.id.bt_login_ensure, R.id.sign_in_button  })
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.bt_login_ensure:
                 phone = et_login_user.getText().toString().trim();
                 password = et_login_pasw.getText().toString().trim();
//                if (TextUtils.isEmpty(phone)) {
//                    toast("账号码不能为空");
//                    break;
//                }/* else if (!Mobile.isMobile(phone)) {
//                    toast( "手机号码不合法");
//                    break;
//                }*/
//                if (TextUtils.isEmpty(password)) {
//                    ToastUtil.showShort(this, "请输入密码");
//                    break;
//                }
                boolean isConn = NetWorkUtil.isConnected(MyApplication.getContext());
                if (isConn){
                    showProgressDialog("正在登录，请稍后...");
                    Map<String, Object> params = new HashMap<>();
                    params.put("phone", "13101665957");
                    params.put("password", "123456");
                    new LoginAsynTask().execute(params);
                }else {
                    ToastUtil.showShort(this, "无网络可用，请检查网络");
                }

                break;
            case R.id.sign_in_button:
                Log.i("robin","点击了登录按钮");
                signIn();
                break;


        }

    }
    /**
     * 自定义对话框
     */
//    private void ShareDialog() {
//        final Dialog dialog = new Dialog(this, R.style.MyDialog);
//        View view = View.inflate(this, R.layout.dialog_forgtpassword, null);
//        dialog.setContentView(view);
//        //使得点击对话框外部不消失对话框
//        dialog.setCanceledOnTouchOutside(true);
//        //设置对话框的大小
//        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.23f));
//        Window dialogWindow = dialog.getWindow();
//        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.75f);
////        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        lp.height = (int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.40f);
//        lp.gravity = Gravity.CENTER;
//        dialogWindow.setAttributes(lp);
//        dialog.show();
//    }


    /*google 登录*/
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void handleSignInResult(GoogleSignInResult result){
        Log.i("robin", "handleSignInResult:" + result.isSuccess());
        if(result.isSuccess()){
            Log.i("robin", "成功");
            GoogleSignInAccount acct = result.getSignInAccount();
            if(acct!=null){
                Log.i("robin", "用户名是:" + acct.getDisplayName());
                Log.i("robin", "用户email是:" + acct.getEmail());
                Log.i("robin", "用户头像是:" + acct.getPhotoUrl());
                Log.i("robin", "用户Id是:" + acct.getId());//之后就可以更新UI了
                Log.i("robin", "用户IdToken是:" + acct.getIdToken());
                tv_bj1.setText("用户名是:" + acct.getDisplayName()+"\n用户email是:" + acct.getEmail()+"\n用户头像是:" + acct.getPhotoUrl()+ "\n用户Id是:" + acct.getId()+"\n用户IdToken是:" + acct.getIdToken());
            }
        }else{
            tv_bj1.setText("登录失败");
            Log.i("robin", "没有成功"+result.getStatus());
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("robin","google登录-->onConnected,bundle=="+bundle);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("robin","google登录-->onConnectionSuspended,i=="+i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("robin","google登录-->onConnectionFailed,connectionResult=="+connectionResult);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient!=null&&mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /* face he google 回调*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("robin", "requestCode==" + requestCode + ",resultCode==" + resultCode + ",data==" + data);
        if(requestCode==RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult: -->"+requestCode+"......"+resultCode +data.toString() );
    }




    @Override
    protected void onStart() {
        super.onStart();
        if(mGoogleApiClient!=null) mGoogleApiClient.connect();
        if (preferences.contains("phone") && !preferences.contains("password")) {
            String phone = preferences.getString("phone", "");
            et_login_user.setText(phone);
            et_login_pasw.setText("");
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    private ProgressDialog progressDialog;
    //显示dialog
    public void showProgressDialog(String message) {

        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();

    }



    String returnMsg;
    class LoginAsynTask extends AsyncTask<Map<String,Object>,Void,String> {

        @Override
        protected String doInBackground(Map<String, Object>... maps) {
            String code = "";
            Map<String, Object> prarms = maps[0];
            String result =   HttpUtils.postOkHpptRequest(HttpUtils.ipAddress+"/app/user/login",prarms);

            Log.e("back", "--->" + result);
            if (!ToastUtil.isEmpty(result)) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    code = jsonObject.getString("returnCode");
                    returnMsg=jsonObject.getString("returnMsg");
//                    JSONObject returnData = jsonObject.getJSONObject("returnData");
                    if ("100".equals(code)) {
                        JSONObject returnData = jsonObject.getJSONObject("returnData");
                        int sellerId = returnData.getInt("sellerId");
                        int sellerRole = returnData.getInt("sellerRole");
                        int sellerFlag = returnData.getInt("sellerFlag");
                        String sellerCoName = returnData.getString("sellerCoName");
                        String sellerName = returnData.getString("sellerName");
                        String sellerPhone = returnData.getString("sellerPhone");
                        String sellerPassword = returnData.getString("sellerPassword");
                        String sellerManagePassword = returnData.getString("sellerManagePassword");
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("phone",phone);
                        editor.putString("password",password);
                        editor.putInt("sellerId", sellerId);
                        editor.putInt("sellerRole", sellerRole);
                        editor.putInt("sellerFlag", sellerFlag);
                        editor.putString("sellerCoName", sellerCoName);
                        editor.putString("sellerName", sellerName);
                        editor.putString("sellerPhone", sellerPhone);
                        editor.putString("sellerPassword", sellerPassword);
                        editor.putString("sellerManagePassword", sellerManagePassword);
                        editor.commit();


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return code;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            switch (s) {

                case "100":
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    toast( "登录成功");

                    break;
                default:
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    toast( returnMsg);
                    break;

            }
        }
    }
}
