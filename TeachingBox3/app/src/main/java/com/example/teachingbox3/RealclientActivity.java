package com.example.teachingbox3;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

import static com.example.teachingbox3.Constant.*;

/**
 * Created by 孟东风 on {20190622}.
 */
public class RealclientActivity extends Activity {

    private TextView txtReceiveInfo;
    private EditText edtRemoteIP,edtRemotePort,edtSendInfo;
    private Button Connect,btnSend,btnRight,btnLeft, robot_run;
    private boolean isConnected=false,isListened=false;
    private Socket socketClient=null,socket=null;
    private ServerSocket socketServer=null;
    private String receiveInfoClient,receiveInfoServer;
    static BufferedReader bufferedReaderClient	= null,bufferedReaderServer=null;
    static PrintWriter printWriterClient = null,printWriterServer=null;
    static OutputStream Chuanshu;
    public static boolean net_connected = false;
    static String[] array = new String[320];  //存放六个数字string型
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
        setContentView(R.layout.tcp);
        Connect=(Button)findViewById(R.id.Connect);
        robot_run=(Button) findViewById(R.id.robot_run);
        txtReceiveInfo=(TextView)findViewById(R.id.textReceiveInfo);
        edtRemoteIP=(EditText)findViewById(R.id.editRemoteIP);
        edtRemotePort=(EditText)findViewById(R.id.edtRemotePort);

        robot_run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float index[] = {0,0,0,0,0,0,2};//末位2表示机器人启动
                send_to_pc(index);
            }
        });
    }
    //向PC发送数据
    public static void send_to_pc(float index[])
    {
        if(net_connected)
        {
            String dot = ",  ";
            String message_to_pc = index[0]+dot+index[1]+dot+index[2]+dot+index[3]+dot+index[4]+dot+index[5]+dot+index[6];
            try{
                Chuanshu.write(message_to_pc.getBytes());
            }catch(Exception e){
                printWriterClient.print(message_to_pc);//发送给服务器
            }
        }
    }

    //连接按钮单击事件
    public void ConnectButtonClick(View source)
    {
        if(isConnected)
        {
            isConnected=false;
            if(socketClient!=null)
            {
                try
                {
                    socketClient.close();
                    socketClient=null;
                    printWriterClient.close();
                    printWriterClient = null;
                }
                catch (IOException e){}
            }
            new tcpClientThread().interrupt();
            Connect.setText("开始连接");
            edtRemoteIP.setEnabled(true);
            edtRemotePort.setEnabled(true);
            txtReceiveInfo.setText("ReceiveInfo:\n");
        }
        else
        {
            isConnected=true;
            Connect.setText("停止连接");
            edtRemoteIP.setEnabled(false);
            edtRemotePort.setEnabled(false);
            new tcpClientThread().start();
        }
    }
    //TCP客户端线程
    private class tcpClientThread extends Thread
    {
        public void run()
        {
            try
            {
                //连接服务器
                IP = edtRemoteIP.getText()+"";
                socketClient = new Socket(edtRemoteIP.getText().toString(), Integer.parseInt(edtRemotePort.getText().toString()));
                Chuanshu = socketClient.getOutputStream();
                //取得输入、输出流
                bufferedReaderClient = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
                printWriterClient = new PrintWriter(socketClient.getOutputStream(), true);
                receiveInfoClient = "连接服务器成功!\n";
                Message msg = new Message();
                msg.what=0x123;
                handler.sendMessage(msg);
            }
            catch (Exception e)
            {
                receiveInfoClient = e.getMessage() + "\n";
                Message msg = new Message();
                msg.what=0x123;
                handler.sendMessage(msg);
                return;
            }
            char[] buffer = new char[1024];
            int count;
            while (isConnected)
            {
                net_connected = true;
                try
                {
                    if((count = bufferedReaderClient.read(buffer))>0)
                    {
                        receiveInfoClient = "接收信息 "+"\""+getInfoBuff(buffer, count)+"\""+"\n";//消息换行
                        //同时将接收到的信息转化成六个数显示在框中//将字符串用“,”分拆，转成六个string,然后再转成float
                        StringTokenizer token = new StringTokenizer(getInfoBuff(buffer, count),",");  //按照逗号进行截取
                        int i = 0;
                        while(token.hasMoreElements()){
                            array[i] = token.nextToken();
                            i++;
                        }
                        if(i<14){//通信偶尔有错，会把两次发的信息并在一起过来，容易报错，此处加以差别
                            for(int m=0; m<6; m++){
                                //if(array[m].indexOf(".",array[m].indexOf("."))==-1) //保证是浮点数，否则可能会出错
                                //{
                                joint_from_pc[m] = Float.parseFloat(array[m]);
                                endpos_from_pc[m] = Float.parseFloat(array[m + 6]);
                                //}
                            }
                        }
                        Message msg = new Message();
                        msg.what=0x123;
                        handler.sendMessage(msg);
                    }
                }
                catch (Exception e)
                {
                    receiveInfoClient = e.getMessage() + "\n";
                    Message msg = new Message();
                    msg.what=0x123;
                    handler.sendMessage(msg);
                    return;
                }
            }
        }
    };

    Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            if(msg.what==0x123)
            {
                txtReceiveInfo.append("TCPClient: "+receiveInfoClient);	// 刷新
            }
            if(msg.what==0x456)
            {
                txtReceiveInfo.append("TCPServer: "+receiveInfoServer);
            }
        }
    };
    private String getInfoBuff(char[] buff, int count) {

        char[] temp = new char[count];
        for (int i = 0; i < count; i++) {
            temp[i] = buff[i];
        }
        return new String(temp);
    }
}