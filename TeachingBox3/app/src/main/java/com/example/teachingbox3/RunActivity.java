package com.example.teachingbox3;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;

import java.lang.reflect.Field;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static java.lang.Boolean.TRUE;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static com.example.teachingbox3.Constant.*;
import static com.example.teachingbox3.Inv.*;
import static com.example.teachingbox3.RealclientActivity.*;
import static java.lang.Math.round;

/**
 * Created by 孟东风 on {DATE}.
 */
public class RunActivity extends Activity {
    private MySurfaceView mSurfaceView;

    Button record_begin, record_end, record_point, record_process, record_pause, record_stop;    //虚拟轨迹规划
    Button send,button_fuwei;
    Button fixture, fixture_none, fixture_hand;

    Button x_plus, y_plus, z_plus, rx_plus, ry_plus, rz_plus, x_min, y_min, z_min, rx_min, ry_min, rz_min; //末端位姿控制12个按钮
    public static TextView x, y, z, rx, ry, rz;     //末端位姿显示  //静态变量
    LinearLayout joint_select_layout;
    Button joint_select[]=new Button[7];  //7个关节选择

    int joint_activated=1;   //当前哪个关节别激活
    NumberPicker numberpicker;
    public float number_picked=0.0f;

    Button joint_min,joint_max;

    static SeekBar seekbar_select[]=new SeekBar[7];

    TextView joint_left_bottom;
    Button button_right_bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move);

        mSurfaceView=new MySurfaceView(this);
        mSurfaceView.requestFocus();
        mSurfaceView.setFocusableInTouchMode(true);

        LinearLayout model=findViewById(R.id.model);
        model.addView(mSurfaceView);

        init();
        initNumberPicker();

    }

    private void Reset(){
        a_horizontal=0;//视角回到初始位置
        seekbar_select[0].setProgress(2000);seekbar_select[1].setProgress(900);seekbar_select[2].setProgress(3600);seekbar_select[3].setProgress(1800);
        seekbar_select[4].setProgress(3600);seekbar_select[5].setProgress(3600);seekbar_select[6].setProgress(3600);

        joint_target[0] = 0; joint_target[1] = -90; joint_target[2] = 0;  joint_target[3] = 0; joint_target[4] = 0; joint_target[5] = 0;joint_target[6]=0;

        Endpos_Calculate();
        number_Change();
    }

    private void init() {

        //语音id

        //六个关节角的度数显示
        //末端位姿显示
        x = (TextView) findViewById(R.id.x); y = (TextView) findViewById(R.id.y); z = (TextView) findViewById(R.id.z);
        ry = (TextView) findViewById(R.id.ry);rx = (TextView) findViewById(R.id.rx); rz = (TextView) findViewById(R.id.rz);

        //开始，记录，结束，执行，暂停，停止
        record_begin = (Button) findViewById(R.id.Record_begin);
        record_point = (Button) findViewById(R.id.Record_point);
        record_end = (Button) findViewById(R.id.Record_end);
        record_process = (Button) findViewById(R.id.Record_process);
        record_pause = (Button) findViewById(R.id.Record_pause);
        record_stop = (Button) findViewById(R.id.Record_stop);

        //复位
        button_fuwei = (Button) findViewById(R.id.Reset);
        //向pc端发送数据
        send = (Button) findViewById(R.id.Send);

        //夹具有无
        fixture = (Button) findViewById(R.id.Fixture);   //夹具
        fixture_none = (Button) findViewById(R.id.Fixture_gone);
        fixture_hand = (Button) findViewById(R.id.Fixture_hand);

        x_plus = findViewById(R.id.X_PLUS);
        y_plus = (Button) findViewById(R.id.Y_PLUS);
        z_plus = (Button) findViewById(R.id.Z_PLUS);
        rx_plus = (Button) findViewById(R.id.RX_PLUS);
        ry_plus = (Button) findViewById(R.id.RY_PLUS);
        rz_plus = (Button) findViewById(R.id.RZ_PLUS);
        x_min = (Button) findViewById(R.id.X_MIN);
        y_min = (Button) findViewById(R.id.Y_MIN);
        z_min = (Button) findViewById(R.id.Z_MIN);
        rx_min = (Button) findViewById(R.id.RX_MIN);
        ry_min = (Button) findViewById(R.id.RY_MIN);
        rz_min = (Button) findViewById(R.id.RZ_MIN);

        //关节布局选择按钮
        joint_select_layout = (LinearLayout) findViewById(R.id.joint_select_layout);
        //关节选择按钮
        joint_select[0] = (Button) findViewById(R.id.joint1_select);
        joint_select[1] = (Button) findViewById(R.id.joint2_select);
        joint_select[2] = (Button) findViewById(R.id.joint3_select);
        joint_select[3] = (Button) findViewById(R.id.joint4_select);
        joint_select[4] = (Button) findViewById(R.id.joint5_select);
        joint_select[5] = (Button) findViewById(R.id.joint6_select);
        joint_select[6] = (Button) findViewById(R.id.joint7_select);

        //数字选择器及两端加减按钮
        numberpicker = (NumberPicker) findViewById(R.id.numberpicker);
        joint_min = (Button) findViewById(R.id.joint_min);
        joint_max = (Button) findViewById(R.id.joint_max);

        //控制机器人的7个流动条
        seekbar_select[0] = (SeekBar) findViewById(R.id.seekBar1);
        seekbar_select[1] = (SeekBar) findViewById(R.id.seekBar2);
        seekbar_select[2] = (SeekBar) findViewById(R.id.seekBar3);
        seekbar_select[3] = (SeekBar) findViewById(R.id.seekBar4);
        seekbar_select[4] = (SeekBar) findViewById(R.id.seekBar5);
        seekbar_select[5] = (SeekBar) findViewById(R.id.seekBar6);
        seekbar_select[6] = (SeekBar) findViewById(R.id.seekBar7);

        //流动条两侧控件
        joint_left_bottom = (TextView) findViewById(R.id.joint_left_bottom);
        button_right_bottom = (Button) findViewById(R.id.button_right_bottom);

        //下面是控件的设定
        //机器人在初始状态下的末端位姿
        x.setText("0.0");
        y.setText("0.0");
        z.setText("0.0");
        rx.setText("0.0");
        ry.setText("90.0");
        rz.setText("0.0");

        //滚动条属性设置，由于在seekbar中不能设置小数，只此在此将其全部乘以10，在计算和显示的时候再除以10
        //各关节角度范围 关节一：正负200  关节二：0到-180（初始-90） 关节三：80到240（初始180） 关节四：正负180 关节五：正负115  关节六：正负360
        seekbar_select[0].setMax(4000);
        seekbar_select[0].setProgress(2000); //三个参数分别为最大值，初始显示数据以及初始位置
        seekbar_select[1].setMax(1800);
        seekbar_select[1].setProgress(900);
        seekbar_select[2].setMax(7200);
        seekbar_select[2].setProgress(3600);
        seekbar_select[3].setMax(3600);
        seekbar_select[3].setProgress(1800);
        seekbar_select[4].setMax(7200);
        seekbar_select[4].setProgress(3600);
        seekbar_select[5].setMax(7200);
        seekbar_select[5].setProgress(3600);
        seekbar_select[6].setMax(7200);
        seekbar_select[6].setProgress(3600);

        numberpicker.setMaxValue(100);
        numberpicker.setMinValue(1);
        numberpicker.setValue(50);   //设置初始值
        numberpicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);//禁止打开键盘，关闭编辑模式

        numberpicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                number_picked = abs((float) ((float) (newVal * 0.1) - 0.1));
            }
        });

        joint_min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  //判断语句，判断减小后是否会超过左极限
                joint_target[joint_activated - 1] = (joint_target[joint_activated - 1] - number_picked) < joint_left_extreme[joint_activated - 1] ? joint_left_extreme[joint_activated - 1] : (joint_target[joint_activated - 1] - number_picked);
                number_Change();
            }
        });

        joint_max.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joint_target[joint_activated-1]=(joint_target[joint_activated-1]+number_picked)>joint_right_extreme[joint_activated-1]?joint_right_extreme[joint_activated-1]:(joint_target[joint_activated-1]+number_picked);
                number_Change();
            }
        });

        //示教再现功能的实现

        //开始记录
        record_begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float index[] = {0,0,0,0,0,0,11};
                send_to_pc(index);
            }
        });

        //虚拟轨迹规划
        //打点
        record_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float index[] = {joint_target[0],joint_target[1],joint_target[2],joint_target[3],joint_target[4],joint_target[5],12};
                send_to_pc(index);
            }
        });
        //结束
        record_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float index[] = {0,0,0,0,0,0,13};
                send_to_pc(index);
            }
        });
        //执行
        record_process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float index[] = {0,0,0,0,0,0,14};
                send_to_pc(index);
            }
        });

        //暂停
        record_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float index[] = {0,0,0,0,0,0,15};
                send_to_pc(index);
            }
        });
        //停止
        record_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float index[] = {0,0,0,0,0,0,16};
                send_to_pc(index);
            }
        });



        //右下角按钮控制六个关节是不是需要显示
        button_right_bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(joint_select_layout.getVisibility()==GONE) {
                    joint_select_layout.setVisibility(VISIBLE);
                }
                else{
                    joint_select_layout.setVisibility(GONE);
                }
            }
        });

        joint_select[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_right_bottom.setText("关节一");   Color_change(1);
            }
        });
        joint_select[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_right_bottom.setText("关节二");    Color_change(2);
            }
        });
        joint_select[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_right_bottom.setText("关节三");   Color_change(3);
            }
        });
        joint_select[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_right_bottom.setText("关节四");   Color_change(4);
            }
        });
        joint_select[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_right_bottom.setText("关节五");  Color_change(5);
            }
        });
        joint_select[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_right_bottom.setText("关节六");   Color_change(6);
            }
        });
        joint_select[6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_right_bottom.setText("关节七");   Color_change(7);
            }
        });


        //末端位置的加减控制按钮
        x_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //末端位姿反解后再赋给机械臂
                float endpos_target[]=endpose;
                endpos_target[0]+=number_picked;
                inv_Solve(endpos_target); //求反解
                number_Change();
            }
        });

        x_min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float endpos_target[]= endpose;
                endpos_target[0]-=number_picked;
                inv_Solve(endpos_target); //求反解
                number_Change();
            }
        });

        y_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float endpos_target[]= endpose;
                endpos_target[1]+=number_picked;
                inv_Solve(endpos_target); //求反解
                number_Change();
            }
        });
        y_min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float endpos_target[]= endpose;
                endpos_target[1]-=number_picked;
                inv_Solve(endpos_target); //求反解
                number_Change();
            }
        });
        z_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float endpos_target[]= endpose;
                endpos_target[2]+=number_picked;
                inv_Solve(endpos_target); //求反解
                number_Change();
            }
        });
        z_min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float endpos_target[]= endpose;
                endpos_target[2]-=number_picked;
                inv_Solve(endpos_target); //求反解,可以求得各个关节的角度
                number_Change();
            }
        });

        rx_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DD_Calculate(number_picked,0,0);
                number_Change();
            }
        });

        rx_min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DD_Calculate(-number_picked,0,0);
                number_Change();
            }
        });

        ry_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DD_Calculate(0,number_picked,0);
                number_Change();
            }
        });
        ry_min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DD_Calculate(0,-number_picked,0);
                number_Change();
            }
        });
        rz_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DD_Calculate(0,0,number_picked);
                number_Change();
            }
        });rz_min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DD_Calculate(0,0,-number_picked);
                number_Change();
            }
        });


        //夹具更换监听器
        fixture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fixture_hand.getVisibility()== GONE)
                {//隐藏的话设置为显示
                    fixture_hand.setVisibility(VISIBLE);  fixture_none.setVisibility(VISIBLE);
                }
                else
                {
                    fixture_hand.setVisibility(GONE); fixture_none.setVisibility(GONE);
                }
            }
        });

        fixture_hand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//夹手
                jiaju_index=1;
                fixture_hand.setVisibility(GONE);   fixture_none.setVisibility(GONE);
                onCreate(null);
                fixture.setText("夹手");
            }
        });

        fixture_none.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jiaju_index=0;
                fixture_hand.setVisibility(GONE);    fixture_none.setVisibility(GONE);
                onCreate(null);
                fixture.setText("无夹具");
            }
        });


        button_fuwei.setOnClickListener(new View.OnClickListener() {//回到初始位置
            @Override
            public void onClick(View v) {
                Reset();
            }
        });

        //调节滚动条一
        seekbar_select[0].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                joint_target[0]=(float) (i-2000)/10;
                number_Change();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {  //该方法拖动进度条开始拖动的时候调用。
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {    //该方法拖动进度条停止拖动的时候调用。
                joint_target[0]=(float) (seekBar.getProgress()-2000)/10;
                number_Change();
            }
        });

        seekbar_select[1].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                joint_target[1] = (float)(i-1800)/10;
                number_Change();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {    }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                joint_target[1] = (float)(seekBar.getProgress()-1800)/10;
                number_Change();
            }
        });

        seekbar_select[2].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                joint_target[2] = (float)(i-3600)/10;
                number_Change();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                joint_target[2] = (float)(seekBar.getProgress()-3600)/10;
                number_Change();
            }
        });

        seekbar_select[3].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                joint_target[3] = (float)(i-1800)/10;
                number_Change();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                joint_target[3] = (float)(seekBar.getProgress()-1800)/10;
                number_Change();
            }
        });

        seekbar_select[4].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                joint_target[4] = (float)(i-3600)/10;
                number_Change();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                joint_target[4] = (float)(seekBar.getProgress()-3600)/10;
                number_Change();
            }
        });

        seekbar_select[5].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                joint_target[5] = (float)(i-3600)/10;
                number_Change();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                joint_target[5] = (float)(seekBar.getProgress()-3600)/10;
                number_Change();
            }
        });

        seekbar_select[6].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                joint_target[6] = (float)(i-3600)/10;
                number_Change();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                joint_target[6] = (float)(seekBar.getProgress()-3600)/10;
            }
        });
    }



    //改变关节选择按钮颜色
    public void Color_change(int index){
        joint_activated=index;   //第Index个关节被激活了
        for(int i=0;i<7;i++){
            joint_select[i].setBackgroundColor(Color.parseColor("#000000"));
            seekbar_select[i].setVisibility(GONE);
        }
        joint_select[index-1].setBackgroundColor(Color.parseColor("#4c74e7"));
        seekbar_select[index-1].setVisibility(VISIBLE);

        joint_left_bottom.setText((float)(round(joint_target[joint_activated-1]*10))/10+"");   //调节左端的edittext ，round是四舍五入的意思
    }

    //集中处理数据更新显示工作
    public void number_Change(){
        Endpos_Calculate();
        joint_select[0].setText("     关节一   "+(float)(round(joint_target[0]*10))/10);
        joint_select[1].setText("     关节二   "+(float)(round(joint_target[1]*10))/10);
        joint_select[2].setText("     关节三   "+(float)(round(joint_target[2]*10))/10);
        joint_select[3].setText("     关节四   "+(float)(round(joint_target[3]*10))/10);
        joint_select[4].setText("     关节五   "+(float)(round(joint_target[4]*10))/10);
        joint_select[5].setText("     关节六   "+(float)(round(joint_target[5]*10))/10);
        joint_select[6].setText("     关节七   "+(float)(round(joint_target[6]*10))/10);
        joint_left_bottom.setText((float)(round(joint_target[joint_activated-1]*10))/10+"");
    }

    public static void Endpos_Calculate(){  //末端位姿计算
        float m_theta[]=new float[7];
        for(int i=0;i<7;i++){
            m_theta[i]=(float)(joint_target[i]*PI/180+theta[i]+ theta_0[i]);  //各关节目标值+theta值+theta初始值
        }
        float t10[][],t21[][],t32[][], t43[][], t54[][], t65[][], t76[][];   //调用inv中的DH_mat方法计算连续变化矩阵
        t10 = DH_mat(a[0], d[0], alpha[0], m_theta[0]); t21 = DH_mat(a[1], d[1], alpha[1], m_theta[1]);
        t32 = DH_mat(a[2], d[2], alpha[2], m_theta[2]); t43 = DH_mat(a[3], d[3], alpha[3], m_theta[3]);
        t54 = DH_mat(a[4], d[4], alpha[4], m_theta[4]); t65 = DH_mat(a[5], d[5], alpha[5], m_theta[5]);
        t76 = DH_mat(a[6], d[6], alpha[6], m_theta[6]);

        float t20[][],t30[][],t40[][], t50[][], t60[][],t70[][], t_end[][];   //连续变换矩阵相乘得到关节变化矩阵
        t20 = MetMuti44(t10, t21); t30 = MetMuti44(t20, t32); t40 = MetMuti44(t30, t43);
        t50 = MetMuti44(t40, t54); t60 = MetMuti44(t50, t65); t70 = MetMuti44(t60, t76);
        t_end = MetMuti44(t70,tt7);  //此中tt6即为原始矩阵
        endpose = Mat_to_pos(t_end);  //矩阵转化为位姿
        //位姿输出
        x.setText((float)(round(endpose[0]*10))/10+""); y.setText((float)(round(endpose[1]*10))/10+""); z.setText((float)(round(endpose[2]*10))/10+"");
        rx.setText((float)(round(endpose[3]*10))/10+"");ry.setText((float)(round(endpose[4]*10))/10+""); rz.setText((float)(round(endpose[5]*10))/10+"");
    }

    //初始化数字选择滚动框布局
    private void initNumberPicker(){
        numberpicker.setFocusable(false);
        numberpicker.setFocusableInTouchMode(false);
        numberpicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        setNumberPickerDividerColor(numberpicker);
    }

    //自定义滚动框分割线颜色
    private void setNumberPickerDividerColor(NumberPicker number){
        Field[] pickerFields=NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (TRUE) {
                pf.setAccessible(true);
                try {
                    pf.set(number, new ColorDrawable(ContextCompat.getColor(this, R.color.gree)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }

    }

    public static void seekbar_change(int i){    //进行末端位姿控制时seekbar不能动，否则程序会认为是人在拖动，会干扰计算
        if(i==1)
        {
            seekbar_select[0].setProgress(Float.valueOf(joint_target[0]*10).intValue()+2000);
            seekbar_select[1].setProgress(Float.valueOf(joint_target[1]*10).intValue()+1800);
            seekbar_select[2].setProgress(Float.valueOf(joint_target[2]*10).intValue()+3600);
            seekbar_select[3].setProgress(Float.valueOf(joint_target[3]*10).intValue()+1800);
            seekbar_select[4].setProgress(Float.valueOf(joint_target[4]*10).intValue()+3600);
            seekbar_select[5].setProgress(Float.valueOf(joint_target[5]*10).intValue()+3600);
            seekbar_select[6].setProgress(Float.valueOf(joint_target[6]*10).intValue()+3600);
        }
    }
}
