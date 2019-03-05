package com.example.jill.firsttry.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.jill.firsttry.Adapter.VpAdapter;
import com.example.jill.firsttry.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class GuideAcitivity  extends AppCompatActivity implements ViewPager.OnPageChangeListener  {
    private static  int[] imgs = {R.drawable.t1,R.drawable.t2, R.drawable.t3};   //讲三个引导图片存入数组。
    private ArrayList<ImageView> imageViews; //图片数组
    private ImageView[] dotViews;//小圆点

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        ViewPager vPager = findViewById(R.id.guide_ViewPager);
        initImages();  //初始化图片
        initDots(); //初始化点
        VpAdapter vpAdapter = new VpAdapter(imageViews);
        vPager.setAdapter(vpAdapter);
        vPager.addOnPageChangeListener(this);   //监听页面变化
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initImages() {
        ViewPager.LayoutParams mParams = new ViewPager.LayoutParams();//设置每一张图片都填充窗口
        imageViews = new ArrayList<>();

        for (int i = 0; i < imgs.length; i++) {
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(mParams);//设置布局  
            iv.setImageResource(imgs[i]);//为ImageView添加图片资源  
            iv.setScaleType(ImageView.ScaleType.FIT_XY);//这里也是一个图片的适配  
            imageViews.add(iv);
            if (i == imgs.length - 1) {
                iv.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        Intent toMainActivity = new Intent(GuideAcitivity.this, MainActivity.class);
                        startActivity(toMainActivity);
                        return true;
                    }
                });
            }
        }
    }
    private void initDots() {
        LinearLayout layout = findViewById(R.id.dot_Layout);
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(20, 20);
        mParams.setMargins(10, 0, 10,0);//设置小圆点左右之间的间隔
        dotViews = new ImageView[imgs.length];
        //判断小圆点的数量，从0开始，0表示第一个
        for(int i = 0; i < imageViews.size(); i++)
        {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(mParams);
            imageView.setImageResource(R.drawable.dotselecter);
           if(i== 0)
            {
                imageView.setSelected(true);//默认启动时，选中第一个小圆点
            }
            else {
                imageView.setSelected(false);
            }
            dotViews[i] = imageView;//得到每个小圆点的引用，用于滑动页面时，（onPageSelected方法中）更改它们的状态。
            layout.addView(imageView);//添加到布局里面显示
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for(int i = 0; i < dotViews.length; i++)
        {
            if(position == i)
            {
                dotViews[i].setSelected(true);
            }
            else {
                dotViews[i].setSelected(false);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            exitBy2Click();      //调用双击退出函数
        }
        return false;
    }
    /**
     * 双击退出函数
     */
    private static Boolean isExit = false;
    private void exitBy2Click() {
        Timer tExit;
        if (!isExit) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            System.exit(0);
        }
    }
}