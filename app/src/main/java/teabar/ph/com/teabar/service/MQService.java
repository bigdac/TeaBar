package teabar.ph.com.teabar.service;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MQService extends Service {

    private String host = "tcp://47.98.131.11:1883";
    /**
     * 主机名称
     */
    private String userName = "admin";
    /**
     * 用户名
     */
    private String passWord = "Xr7891122";
    /**
     * 密码
     */
    private Context mContext = this;
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private MqttClient client;
    private MqttConnectOptions options;
    String clientId = "";
    LocalBinder binder = new LocalBinder();
    /***
     * 头码
     */
    private int headCode = 0X90;

    /***
     * 商业模块00：忽略；11：按水流量租
     凭；22：按时间租赁；33：
     按售水量售水型；FF：常规
     机型
     */
    private int[] bussinessmodule = {0X00, 0X11, 0X22, 0X33, 0XFF};


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("MQService", "-->onCreate");
        init();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("MQService", "-->onStartCommand");
        connect();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     * 初始化MQTT
     */
    private void init() {
        try {
            //host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存

            clientId = UUID.randomUUID().toString();
            client = new MqttClient(host, clientId,
                    new MemoryPersistence());
            //MQTT的连接设置
            options = new MqttConnectOptions();
            //设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(true);
            //设置连接的用户名
            options.setUserName(userName);
            //设置连接的密码
            options.setPassword(passWord.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(15);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(60);


            //设置回调
            client.setCallback(new MqttCallback() {

                @Override
                public void connectionLost(Throwable cause) {
                    //连接丢失后，一般在这里面进行重连
                    System.out.println("connectionLost----------");
                    startReconnect();
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    //publish后会执行到这里
                    System.out.println("deliveryComplete---------"
                            + token.isComplete());
                }

                @Override
                public void messageArrived(String topicName, MqttMessage message) {
                    try {
                        Log.i("topicName", "topicName:" + topicName);
                        String msg = message.toString();
                        new LoadAsyncTask().execute(topicName, message.toString());
                        Log.i("message", "message:" + msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getDate(String mac, int funCode) {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            int headCode = 0x55;
            int checkCode = (headCode + funCode) % 256;
            int endCode = 0x88;
            jsonArray.put(0, headCode);
            jsonArray.put(1, funCode);
            jsonArray.put(2, checkCode);
            jsonArray.put(3, endCode);
            jsonObject.put("WPurifier", jsonArray);
            String topicName = "p99/wPurifier1/" + mac + "/set";
            String payLoad = jsonObject.toString();
            publish(topicName, 1, payLoad);
            Log.e("getData", "getDate: -->");
            Log.e("FFFDDDD", "getDate:获取数据 " + mac);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 功能码为
     * 0x11:基本功能查询
     * 0x23:基础设置
     * 0x31:周一定时设置查询
     * 0x32:周二定时设置查询
     * 0x33:周三定时设置查询
     * 0x34:周四定时设置查询
     * 0x35:周五定时设置查询
     * 0x36:周六定时设置查询
     * 0x37:周七定时设置查询
     *
     * @param mac
     * @param funCode
     */
    public void getData(String mac, int funCode) {
        try {
            int headCode = 0x55;

            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(0, headCode);
            jsonArray.put(1, funCode);
            int sum = 0;
            for (int i = 0; i < 2; i++) {
                sum += jsonArray.getInt(i);
            }
            int checkCode = sum % 256;
            jsonArray.put(2, checkCode);
            int endCode = 0x88;
            jsonArray.put(3, endCode);
            jsonObject.put("WPurifier", jsonArray);
            String topicName = "p99/wPurifier1/" + mac + "/set";
            String payLoad = jsonObject.toString();
            boolean success = publish(topicName, 1, payLoad);
            if (!success)
                publish(topicName, 1, payLoad);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送基本功能
     *
     * @param
     */
    public void sendData() {


        try {
            JSONArray jsonArray = new JSONArray();

            int sum = 0;
            for (int i = 0; i < 23; i++) {
                sum += jsonArray.getInt(i);
            }

            String topicName = "p99/wPurifier1/set";
            String payLoad = ""/*jsonObject.toString()*/;
            boolean success = publish(topicName, 1, payLoad);
            if (!success)
                success = publish(topicName, 1, payLoad);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("StaticFieldLeak")
    class LoadAsyncTask extends AsyncTask<String, Void, Object> {

        @Override
        protected Object doInBackground(String... strings) {

            String topicName = strings[0];/**收到的主题*/
            String message = strings[1];/**收到的消息*/
            Log.i("topicName", "-->:" + topicName);
            String macAddress = null;
            if (topicName.startsWith("p99/wPurifier1")) {
                macAddress = topicName.substring(15, topicName.lastIndexOf("/"));
            }
            JSONArray messageJsonArray = null;
            JSONObject messageJsonObject = null;
//            Equipment equipment = null;

//            equipment=new Equipment();
            try {
                if (!TextUtils.isEmpty(message) && message.startsWith("{") && message.endsWith("}")) {
                    messageJsonObject = new JSONObject(message);
                }
                if (messageJsonObject != null && messageJsonObject.has("WPurifier")) {
                    messageJsonArray = messageJsonObject.getJSONArray("WPurifier");
                }

                int funCode = -1;
                int week = -1;

                if (topicName.contains("transfer")){
                    if (   messageJsonArray != null && messageJsonArray.getInt(2) == 0x11) {
                        Log.e("hasData", "getDate: -->");





                    }
                }


              /*  if (MainActivity.isRunning) {
                    Intent mqttIntent = new Intent("MainActivity");
                    mqttIntent.putExtra("msg", macAddress);
                    mqttIntent.putExtra("msg1", equipment);
                    sendBroadcast(mqttIntent);
                }  */
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    public List<String> getTopicNames() {
        List<String> list = new ArrayList<>();
        String onlineTopicName = "";
        String offlineTopicName = "";


//        String macAddress="1234567890";
//        onlineTopicName = "p99/wPurifier1/" + macAddress + "/transfer";
//        offlineTopicName = "p99/wPurifier1/" + macAddress + "/lwt";
//        list.add(onlineTopicName);
//        list.add(offlineTopicName);
        return list;
    }

    /***
     * 连接MQTT
     */
    public void connect() {
        try {
            if (client != null && !client.isConnected()) {
                client.connect(options);
            }
            List<String> topicNames = getTopicNames();
            new ConAsync().execute(topicNames);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重新连接MQTT
     */
    private void startReconnect() {

        scheduler.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (!client.isConnected()) {
                    connect();
                }
            }
        }, 0 * 1000, 1 * 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * 发送MQTT主题
     */

    public boolean publish(String topicName, int qos, String payload) {
        boolean flag = false;
        if (client != null && client.isConnected()) {
            try {
                MqttMessage message = new MqttMessage(payload.getBytes("utf-8"));
                qos = 1;
                //设置保留消息
                if (topicName.contains("friend")) {
                    message.setRetained(true);
                }
                if (topicName.contains("clockuniversal")) {
                    message.setRetained(true);
                }
                message.setQos(qos);
                client.publish(topicName, message);
                flag = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    /**
     * 订阅MQTT主题
     *
     * @param topicName
     * @param qos
     * @return
     */
    public boolean subscribe(String topicName, int qos) {
        boolean flag = false;
        if (client != null && client.isConnected()) {
            try {

                client.subscribe(topicName, 1);
                flag = true;
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }


    public class LocalBinder extends Binder {

        public MQService getService() {
            Log.i("Binder", "Binder");
            return MQService.this;
        }
    }


    /**
     * @param topicName
     */
    public void unsubscribe(String topicName) {
        if (client != null && client.isConnected()) {
            try {
                client.unsubscribe(topicName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    class ConAsync extends AsyncTask<List<String>, Void, Void> {

        @Override
        protected Void doInBackground(List<String>... lists) {
            try {

                List<String> topicNames = getTopicNames();
                boolean sss = client.isConnected();
                Log.i("sss", "-->" + sss);
                if (client.isConnected() && !topicNames.isEmpty()) {
                    for (String topicName : topicNames) {
                        if (!TextUtils.isEmpty(topicName)) {
                            client.subscribe(topicName, 1);
                            Log.i("client", "-->" + topicName);
                            Log.e("FFFDDDD", "doInBackground: 订阅-->" + topicName);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
