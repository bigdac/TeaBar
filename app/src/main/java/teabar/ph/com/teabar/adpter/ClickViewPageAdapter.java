package teabar.ph.com.teabar.adpter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.base.BaseFragment;


public class ClickViewPageAdapter extends FragmentPagerAdapter {

    private List<BaseFragment> fragments;
    final int PAGE_COUNT=3;
    private Context context;
    public ClickViewPageAdapter(FragmentManager fm, List<BaseFragment> fragments, Context context) {
        super(fm);
        this.fragments = fragments;
        this.context=context;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public View getCustomView(int position){
        View view= LayoutInflater.from(context).inflate(R.layout.item_memu,null);
        ImageView iv= (ImageView) view.findViewById(R.id.tab_iv);
        TextView tv= (TextView) view.findViewById(R.id.tab_tv);
        switch (position){
            case 0:
                //drawable代码在文章最后贴出
                iv.setImageDrawable(context.getResources().getDrawable(R.drawable.rb_memu_icon1));
                tv.setText("首页");
                break;
            case 1:
                iv.setImageDrawable(context.getResources().getDrawable(R.drawable.rb_memu_icon2));
                tv.setText("维修");
                break;
            case 2:
                iv.setImageDrawable(context.getResources().getDrawable(R.drawable.rb_memu_icon3));
                tv.setText("我的");
                break;

        }
        return view;
    }


}
