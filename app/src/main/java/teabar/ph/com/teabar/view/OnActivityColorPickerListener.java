package teabar.ph.com.teabar.view;

import android.support.v4.app.FragmentActivity;

/**
 * Created by dingmouren on 2017/5/5.
 */

public interface OnActivityColorPickerListener {
    void onColorCancel(FragmentActivity dialog);
    void onColorChange(FragmentActivity dialog, int color);
    void onColorConfirm(FragmentActivity dialog, int color);
}
