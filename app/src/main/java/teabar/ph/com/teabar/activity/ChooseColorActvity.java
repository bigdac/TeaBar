package teabar.ph.com.teabar.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import teabar.ph.com.teabar.R;
import teabar.ph.com.teabar.base.BaseActivity;
import teabar.ph.com.teabar.view.ColorPickerDialog;
import teabar.ph.com.teabar.view.ColorPlateView;
import teabar.ph.com.teabar.view.OnActivityColorPickerListener;



public class ChooseColorActvity extends BaseActivity {
    @BindView(R.id.img_hue)
    ImageView img_hue;
    @BindView(R.id.color_plate)
    ColorPlateView color_plate;
    @BindView(R.id.plate_cursor)
    ImageView plate_cursor;//小圆圈
    @BindView(R.id.hue_cursor)
    ImageView hue_cursor;
    @BindView(R.id.container)
    RelativeLayout container;
    @BindView(R.id.tv_color)
    Button tv_color;
    private OnActivityColorPickerListener mListener;
    private ColorPickerDialog mColorPickerDialog;
    private boolean supportAlpha;//颜色是否支持透明度
    private final float[] mCurrentHSV = new float[3];
    private int mAlpha;
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_choosecolor;
    }

    @Override
    public void initView(View view) {
        color_plate.setHue(getColorHue());
       int defauleColor = getResources().getColor(R.color.colorPrimary);
        defauleColor = defauleColor | 0xff000000;
        Color.colorToHSV(defauleColor,mCurrentHSV);
        mAlpha = Color.alpha(defauleColor);
        color_plate .setHue(getColorHue());
        initOnTouchListener();
        initGlobalLayoutListener(container);
//        bt_choose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mColorPickerDialog = new ColorPickerDialog(ChooseColorActvity.this,getResources().getColor(R.color.colorPrimary),supportAlpha,mOnColorPickerListener).show();
//                supportAlpha = !supportAlpha;
//            }
//        });
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void widgetClick(View v) {

    }


    /**
     * 设置色彩
     * @param color
     */
    private void setColorHue(float color){
        mCurrentHSV[0] = color;
    }

    private float getColorHue(){
        return mCurrentHSV[0];
    }
    /**
     * 设置颜色深浅
     */
    private void setColorSat(float color) {
        this.mCurrentHSV[1] = color;
    }

    private float getColorSat(){
        return this.mCurrentHSV[1];
    }

    /**
     * 设置颜色明暗
     */
    private void setColorVal(float color){
        this.mCurrentHSV[2] = color;
    }

    private float getColorVal(){
        return mCurrentHSV[2];
    }

    /**
     * 获取int颜色
     */
    private int getColor(){
        final  int argb = Color.HSVToColor(mCurrentHSV);
        return mAlpha << 24 | (argb & 0x00ffffff);
    }

    /**
     * 全局布局状态监听
     */
    private void initGlobalLayoutListener(final View view) {
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)//api 16
            @Override
            public void onGlobalLayout() {
                moveHueCursor();
                movePlateCursor();
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

            }
        });
    }

    /**
     * 触摸监听
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initOnTouchListener() {
        //色彩板的触摸监听
        img_hue.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_UP) {
                    float y = event.getY();
                    if (y < 0.f) y = 0.f;
                    if (y > img_hue.getMeasuredHeight()) y = img_hue.getMeasuredHeight() - 0.001f;
                    float colorHue = 360.f - 360.f / img_hue.getMeasuredHeight() * y;
                    if (colorHue == 360.f) colorHue = 0.f;
                    setColorHue(colorHue);
                    color_plate.setHue(colorHue);

                    moveHueCursor();

                    if (mListener != null) {
                        mListener.onColorChange(ChooseColorActvity.this, getColor());
                    }
//                    updateAlphaView();
                    return true;
                }
                return false;
            }
        });

        //颜色样板的触摸监听
        color_plate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_UP){
                    float x = event.getX();
                    float y = event.getY();
                    if (x < 0.f) x = 0.f;
                    if (x > color_plate.getMeasuredWidth()) x = color_plate.getMeasuredWidth();
                    if (y < 0.f) y = 0.f;
                    if (y > color_plate.getMeasuredHeight()) y = color_plate.getMeasuredHeight();

                    setColorSat(1.f / color_plate.getMeasuredWidth() * x);//颜色深浅
                    setColorVal(1.f - (1.f / color_plate.getMeasuredHeight() * y));//颜色明暗
                    movePlateCursor();
                    tv_color.setBackgroundColor(getColor());
                    Log.e("DDDDDDDDDDDDDDDDDDDZ", "onTouch: -->"+getColor() );
                    if (mListener!=null){
                        mListener.onColorChange(ChooseColorActvity.this,getColor());
                    }
                    return true;
                }
                return false;
            }
        });
    }


        /**
         * 移动色彩样板指针
         */
        private void moveHueCursor(){//ConstraintLayout$LayoutParams
            float y = img_hue.getMeasuredHeight() - (getColorHue() * img_hue.getMeasuredHeight() / 360.f);
            if (y == img_hue.getMeasuredHeight()) y = 0.f;
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) hue_cursor.getLayoutParams();
//            layoutParams.leftMargin = (int) (img_hue.getLeft() - Math.floor(hue_cursor.getMeasuredWidth() / 9) - container.getPaddingLeft());
            layoutParams.topMargin = (int) (img_hue.getTop() + y - Math.floor(hue_cursor.getMeasuredHeight() / 2) - container.getPaddingTop());
            hue_cursor.setLayoutParams(layoutParams);
        }

    /**
     * 移动最终颜色样板指针
     */
    private void movePlateCursor(){
        final float x = getColorSat() * color_plate.getMeasuredWidth();
        final float y = (1.f - getColorVal()) * color_plate.getMeasuredHeight();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) plate_cursor.getLayoutParams();
        layoutParams.leftMargin = (int) (color_plate.getLeft() + x - Math.floor(plate_cursor.getMeasuredWidth() / 2) - container.getPaddingLeft());
        layoutParams.topMargin = (int) (color_plate.getTop() + y - Math.floor(plate_cursor.getMeasuredHeight() / 2) - container.getPaddingTop());
        plate_cursor.setLayoutParams(layoutParams);

    }
    /**
     * 更新透明度UI
     */
    private void updateAlphaView(){
        final GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,new int[]{Color.HSVToColor(mCurrentHSV),0x0});
//        mViewAlphaOverlay.setBackgroundDrawable(gd);
    }


}
