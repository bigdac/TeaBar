package teabar.ph.com.teabar.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import me.jessyan.autosize.utils.ScreenUtils;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.adpter.ClickViewPageAdapter;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.base.BaseFragment;
import teabar.ph.com.teabar.base.MyApplication;
import teabar.ph.com.teabar.fragment.MainFragment;
import teabar.ph.com.teabar.view.NoSrcollViewPage;

public class MainActivity extends BaseActivity {
    @BindView(R.id.main_viewPage)
    NoSrcollViewPage main_viewPage;
    @BindView(R.id.main_tabLayout)
    TabLayout main_tabLayout;
    @BindView(R.id.tv_main_1)
    TextView tv_main_1;
    List<String> mainMemu = new ArrayList<>();
    List<BaseFragment> fragmentList = new ArrayList<>();
    MainFragment mainFragment;
    MyApplication application;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        setSteepStatusBar(true);
        return R.layout.activity_main;
    }

    @Override
    public void initView(View view) {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                ScreenUtils.getStatusBarHeight());
        tv_main_1.setLayoutParams(params);

        if (application == null) {
            application = (MyApplication) getApplication();
        }
        application.addActivity(this);
        initView();
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }
    private void initView() {
        mainMemu.add("首页");
        mainMemu.add("维修");
        mainMemu.add("我的");
        mainFragment=new MainFragment();
        MainFragment mainFragment1=new MainFragment();
        MainFragment mainFragment2=new MainFragment();
        fragmentList.add(mainFragment);
        fragmentList.add(mainFragment1);
        fragmentList.add(mainFragment2);
        ClickViewPageAdapter tabAdapter = new ClickViewPageAdapter(getSupportFragmentManager(), fragmentList, this);
        main_viewPage.setAdapter(tabAdapter);
        main_tabLayout.setupWithViewPager(main_viewPage);
        for (int i = 0; i < mainMemu.size(); i++) {
            TabLayout.Tab tab = main_tabLayout.getTabAt(i);
            //注意！！！这里就是添加我们自定义的布局
            tab.setCustomView(tabAdapter.getCustomView(i));
            //这里是初始化时，默认item0被选中，setSelected（true）是为了给图片和文字设置选中效果，代码在文章最后贴出
//                ((ImageView) tab.getCustomView().findViewById(R.id.tab_iv)).setSelected(true);
//                ((TextView) tab.getCustomView().findViewById(R.id.tab_tv)).setTextColor(Color.parseColor("#33c62b"));
        }


        main_tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
//              选择时候调用
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //没有选择时候调用
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
