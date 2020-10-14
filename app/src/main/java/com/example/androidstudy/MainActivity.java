package com.example.androidstudy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.androidstudy.Key.KeyCol;
import com.example.androidstudy.Led.LedCol;
import com.example.androidstudy.Serial.SerialCol;
import com.hikvision.audio.AudioCodecParam;
import com.hikvision.audio.AudioEngine;
import com.hikvision.audio.AudioEngineCallBack;
import com.hikvision.netsdk.ExceptionCallBack;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_COMPRESSION_AUDIO;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.hikvision.netsdk.PTZCommand;
import com.hikvision.netsdk.RealPlayCallBack;
import com.hikvision.netsdk.VoiceDataCallBack;

import org.MediaPlayer.PlayM4.Player;

/**
 * <pre>
 *  ClassName  DemoActivity Class
 * </pre>
 *
 * @author zhuzhenlei
 * @version V1.0
 * @modificationHistory
 */
public class MainActivity extends Activity implements Callback {
    private Button leftButton = null;
    private Button rightButton = null;
    private Button upButton = null;
    private Button downButton = null;
    private Button zoomInButton = null;
    private Button zoomOutButton = null;
    private Button irisOpenButton = null;
    private Button irisCloseButton = null;
    private Button moveInButton = null;
    private Button moveOutButton = null;
    private Button voiceButton = null;

    private Button previewButton = null;
    private Button loginButton = null;
    private Button admit = null;
    private SurfaceView surface = null;

    private static String IPAddress = "192.168.137.37";
    private static String port = "8001";
    private static String user = "admin";
    private static String password = "iris2020";

    private static VoiceDataCallBack TalkCbf = null;
    private static AudioEngine audio = null;
    private static int m_iVoiceTalkID = -1;
    private static int iRet = -1;
    private static AudioEngineCallBack.RecordDataCallBack AudioCbf = null;
    private static boolean isTalk = false;

    public static void setIPAddress(String IPAddr) {
        IPAddress = IPAddr;
    }

    public static void setport(String Port) {
        port = Port;
    }

    public static void setuser(String User) {
        user = User;
    }

    public static void setpassword(String Psd) {
        password = Psd;
    }

    private NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = null;

    private int loginID = -1; // return by NET_DVR_Login_v30
    private int playID = -1; // return by NET_DVR_RealPlay_V30
    private int playbackID = -1; // return by NET_DVR_PlayBackByTime

    private int playPort = -1; // play port
    private int startChannel = 0; // start channel no
    private int channelNumber = 0; // channel number

    private final String TAG = "DemoActivity";

    private boolean needDecode = true;
    private boolean stopPlayback = false;


    private SerialCol serialCol;
    private Thread serialth;
    private LedCol ledCol;
    private Thread ledth;
    private KeyCol keyCol;
    private Thread keyth;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!initeSdk()) {

        }

        serialCol=new SerialCol("/dev/ttySAC0",115200);
        serialth =new Thread(serialCol);
        serialth.start();
        ledCol =new LedCol();
//      ledth=new Thread(ledCol);
//      ledth.start();
        keyCol=new KeyCol();
        keyth=new Thread(keyCol);
        keyth.start();



        loginButton = (Button) findViewById(R.id.btn_Login);
        previewButton = (Button) findViewById(R.id.btn_Preview);
        leftButton = (Button)findViewById(R.id.btn_PTZ_left);
        rightButton = (Button)findViewById(R.id.btn_PTZ_right);
        upButton = (Button)findViewById(R.id.btn_PTZ_up);
        downButton = (Button)findViewById(R.id.btn_PTZ_down);
        irisOpenButton = (Button)findViewById(R.id.btn_iris_open);
        irisCloseButton = (Button)findViewById(R.id.btn_iris_close);
        zoomInButton = (Button)findViewById(R.id.btn_zoom_in);
        zoomOutButton = (Button)findViewById(R.id.btn_zoom_out);
        moveInButton = (Button)findViewById(R.id.btn_move_in);
        moveOutButton = (Button)findViewById(R.id.btn_move_out);
        voiceButton = (Button)findViewById(R.id.btn_voice);




        surface = (SurfaceView) findViewById(R.id.Sur_Player);
        admit = (Button)findViewById(R.id.btn_admit);

        admit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, login.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                try {
                    if (loginID < 0) {
                        // login on the device
                        loginID = loginDevice();
                        if (loginID < 0) {
                            Log.e(TAG, "This device logins failed!");
                            return;
                        } else {
                            System.out.println("loginID=" + loginID);
                        }
                        // get instance of exception callback and set
                        ExceptionCallBack oexceptionCbf = getExceptiongCbf();
                        if (oexceptionCbf == null) {
                            Log.e(TAG, "ExceptionCallBack object is failed!");
                            return;
                        }

                        if (!HCNetSDK.getInstance().NET_DVR_SetExceptionCallBack(
                                oexceptionCbf)) {
                            Log.e(TAG, "NET_DVR_SetExceptionCallBack is failed!");
                            return;
                        }
                        Log.i(TAG,
                                "Login sucess ****************************1***************************");
                    } else {
                        // whether we have logout
                        if (!HCNetSDK.getInstance().NET_DVR_Logout_V30(loginID)) {
                            Log.e(TAG, " NET_DVR_Logout is failed!");
                            return;
                        }
                        loginID = -1;
                    }
                } catch (Exception err) {
                    Log.e(TAG, "error: " + err.toString());
                }
            }
        });

        previewButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                try {
                    if (loginID < 0) {
                        Log.e(TAG, "please login on device first");
                        return;
                    }
                    if (needDecode) {
//                        if (channelNumber > 1)// preview more than a channel
//                        {
//                            if (!m_bMultiPlay) {
//                                m_bMultiPlay = true;
//                                previewButton.setText("停止");
//                            } else {
//                                stopMultiPreview();
//                                m_bMultiPlay = false;
//                                previewButton.setText("播放");
//                            }
//                        } else // preivew a channel
//                        {
                        if (playID < 0) {
                            startSinglePreview();
                        } else {
                            stopSinglePreview();
                            previewButton.setText("播放");
                        }
                    }
//                    } else {
//
//                    }
                } catch (Exception err) {
                    Log.e(TAG, "error: " + err.toString());
                }
            }
        });

        leftButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                try {
                    if (loginID < 0) {
                        Log.e(TAG, "please login on a device first");
                        return false;
                    }
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(
                                loginID, startChannel, PTZCommand.PAN_LEFT, 0)) {
//                            Log.e(TAG,
//                                    "start PAN_LEFT failed with error code: "
//                                            + HCNetSDK.getInstance()
//                                            .NET_DVR_GetLastError());
                        } else {
//                            Log.i(TAG, "start PAN_LEFT succ");
                        }
                    }else if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                        if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(
                                loginID, startChannel, PTZCommand.PAN_RIGHT, 1)) {
//                                Log.e(TAG, "stop PAN_LEFT failed with error code: "
//                                        + HCNetSDK.getInstance()
//                                        .NET_DVR_GetLastError());
                        } else {
//                                Log.i(TAG, "stop PAN_LEFT succ");
                        }
                    }
                    return true;
                }catch (Exception err) {
                    Log.e(TAG, "error: " + err.toString());
                    return false;
                }
            }
        });

        rightButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                try {
                    if (loginID < 0) {
                        Log.e(TAG, "please login on a device first");
                        return false;
                    }
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(
                                loginID, startChannel, PTZCommand.PAN_RIGHT, 0)) {
//                            Log.e(TAG,
//                                    "start PAN_LEFT failed with error code: "
//                                            + HCNetSDK.getInstance()
//                                            .NET_DVR_GetLastError());
                        } else {
//                            Log.i(TAG, "start PAN_LEFT succ");
                        }
                    }else if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                        if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(
                                loginID, startChannel, PTZCommand.PAN_LEFT, 1)) {
//                                Log.e(TAG, "stop PAN_LEFT failed with error code: "
//                                        + HCNetSDK.getInstance()
//                                        .NET_DVR_GetLastError());
                        } else {
//                                Log.i(TAG, "stop PAN_LEFT succ");
                        }
                    }
                    return true;
                }catch (Exception err) {
                    Log.e(TAG, "error: " + err.toString());
                    return false;
                }
            }
        });

        upButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                try {
                    if (loginID < 0) {
                        Log.e(TAG, "please login on a device first");
                        return false;
                    }
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(
                                loginID, startChannel, PTZCommand.TILT_UP, 0)) {
//                            Log.e(TAG,
//                                    "start PAN_LEFT failed with error code: "
//                                            + HCNetSDK.getInstance()
//                                            .NET_DVR_GetLastError());
                        } else {
//                            Log.i(TAG, "start PAN_LEFT succ");
                        }
                    }else if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                        if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(
                                loginID, startChannel, PTZCommand.TILT_DOWN, 1)) {
//                                Log.e(TAG, "stop PAN_LEFT failed with error code: "
//                                        + HCNetSDK.getInstance()
//                                        .NET_DVR_GetLastError());
                        } else {
//                                Log.i(TAG, "stop PAN_LEFT succ");
                        }
                    }
                    return true;
                }catch (Exception err) {
                    Log.e(TAG, "error: " + err.toString());
                    return false;
                }
            }
        });

        downButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                try {
                    if (loginID < 0) {
                        Log.e(TAG, "please login on a device first");
                        return false;
                    }
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(
                                loginID, startChannel, PTZCommand.TILT_DOWN, 0)) {
//                            Log.e(TAG,
//                                    "start PAN_LEFT failed with error code: "
//                                            + HCNetSDK.getInstance()
//                                            .NET_DVR_GetLastError());
                        } else {
//                            Log.i(TAG, "start PAN_LEFT succ");
                        }
                    }else if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                        if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(
                                loginID, startChannel, PTZCommand.TILT_UP, 1)) {
//                                Log.e(TAG, "stop PAN_LEFT failed with error code: "
//                                        + HCNetSDK.getInstance()
//                                        .NET_DVR_GetLastError());
                        } else {
//                                Log.i(TAG, "stop PAN_LEFT succ");
                        }
                    }
                    return true;
                }catch (Exception err) {
                    Log.e(TAG, "error: " + err.toString());
                    return false;
                }
            }
        });

        irisOpenButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                try {
                    if (loginID < 0) {
                        Log.e(TAG, "please login on a device first");
                        return false;
                    }
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(
                                loginID, startChannel, PTZCommand.IRIS_OPEN, 0)) {
//                            Log.e(TAG,
//                                    "start PAN_LEFT failed with error code: "
//                                            + HCNetSDK.getInstance()
//                                            .NET_DVR_GetLastError());
                        } else {
//                            Log.i(TAG, "start PAN_LEFT succ");
                        }
                    }else if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                        if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(
                                loginID, startChannel, PTZCommand.IRIS_CLOSE, 1)) {
//                                Log.e(TAG, "stop PAN_LEFT failed with error code: "
//                                        + HCNetSDK.getInstance()
//                                        .NET_DVR_GetLastError());
                        } else {
//                                Log.i(TAG, "stop PAN_LEFT succ");
                        }
                    }
                    return true;
                }catch (Exception err) {
                    Log.e(TAG, "error: " + err.toString());
                    return false;
                }
            }
        });

        irisCloseButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                try {
                    if (loginID < 0) {
                        Log.e(TAG, "please login on a device first");
                        return false;
                    }
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(
                                loginID, startChannel, PTZCommand.IRIS_CLOSE, 0)) {
//                            Log.e(TAG,
//                                    "start PAN_LEFT failed with error code: "
//                                            + HCNetSDK.getInstance()
//                                            .NET_DVR_GetLastError());
                        } else {
//                            Log.i(TAG, "start PAN_LEFT succ");
                        }
                    }else if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                        if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(
                                loginID, startChannel, PTZCommand.IRIS_OPEN, 1)) {
//                                Log.e(TAG, "stop PAN_LEFT failed with error code: "
//                                        + HCNetSDK.getInstance()
//                                        .NET_DVR_GetLastError());
                        } else {
//                                Log.i(TAG, "stop PAN_LEFT succ");
                        }
                    }
                    return true;
                }catch (Exception err) {
                    Log.e(TAG, "error: " + err.toString());
                    return false;
                }
            }
        });

        moveInButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                try {
                    if (loginID < 0) {
                        Log.e(TAG, "please login on a device first");
                        return false;
                    }
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(
                                loginID, startChannel, PTZCommand.FOCUS_NEAR, 0)) {
//                            Log.e(TAG,
//                                    "start PAN_LEFT failed with error code: "
//                                            + HCNetSDK.getInstance()
//                                            .NET_DVR_GetLastError());
                        } else {
//                            Log.i(TAG, "start PAN_LEFT succ");
                        }
                    }else if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                        if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(
                                loginID, startChannel, PTZCommand.FOCUS_FAR, 1)) {
//                                Log.e(TAG, "stop PAN_LEFT failed with error code: "
//                                        + HCNetSDK.getInstance()
//                                        .NET_DVR_GetLastError());
                        } else {
//                                Log.i(TAG, "stop PAN_LEFT succ");
                        }
                    }
                    return true;
                }catch (Exception err) {
                    Log.e(TAG, "error: " + err.toString());
                    return false;
                }
            }
        });

        moveOutButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                try {
                    if (loginID < 0) {
                        Log.e(TAG, "please login on a device first");
                        return false;
                    }
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(
                                loginID, startChannel, PTZCommand.FOCUS_FAR, 0)) {
//                            Log.e(TAG,
//                                    "start PAN_LEFT failed with error code: "
//                                            + HCNetSDK.getInstance()
//                                            .NET_DVR_GetLastError());
                        } else {
//                            Log.i(TAG, "start PAN_LEFT succ");
                        }
                    }else if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                        if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(
                                loginID, startChannel, PTZCommand.FOCUS_NEAR, 1)) {
//                                Log.e(TAG, "stop PAN_LEFT failed with error code: "
//                                        + HCNetSDK.getInstance()
//                                        .NET_DVR_GetLastError());
                        } else {
//                                Log.i(TAG, "stop PAN_LEFT succ");
                        }
                    }
                    return true;
                }catch (Exception err) {
                    Log.e(TAG, "error: " + err.toString());
                    return false;
                }
            }
        });

        zoomInButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                try {
                    if (loginID < 0) {
                        Log.e(TAG, "please login on a device first");
                        return false;
                    }
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(
                                loginID, startChannel, PTZCommand.ZOOM_IN, 0)) {
//                            Log.e(TAG,
//                                    "start PAN_LEFT failed with error code: "
//                                            + HCNetSDK.getInstance()
//                                            .NET_DVR_GetLastError());
                        } else {
//                            Log.i(TAG, "start PAN_LEFT succ");
                        }
                    }else if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                        if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(
                                loginID, startChannel, PTZCommand.ZOOM_OUT, 1)) {
//                                Log.e(TAG, "stop PAN_LEFT failed with error code: "
//                                        + HCNetSDK.getInstance()
//                                        .NET_DVR_GetLastError());
                        } else {
//                                Log.i(TAG, "stop PAN_LEFT succ");
                        }
                    }
                    return true;
                }catch (Exception err) {
                    Log.e(TAG, "error: " + err.toString());
                    return false;
                }
            }
        });

        zoomOutButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                try {
                    if (loginID < 0) {
                        Log.e(TAG, "please login on a device first");
                        return false;
                    }
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(
                                loginID, startChannel, PTZCommand.ZOOM_OUT, 0)) {
//                            Log.e(TAG,
//                                    "start PAN_LEFT failed with error code: "
//                                            + HCNetSDK.getInstance()
//                                            .NET_DVR_GetLastError());
                        } else {
//                            Log.i(TAG, "start PAN_LEFT succ");
                        }
                    }else if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                        if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(
                                loginID, startChannel, PTZCommand.ZOOM_IN, 1)) {
//                                Log.e(TAG, "stop PAN_LEFT failed with error code: "
//                                        + HCNetSDK.getInstance()
//                                        .NET_DVR_GetLastError());
                        } else {
//                                Log.i(TAG, "stop PAN_LEFT succ");
                        }
                    }
                    return true;
                }catch (Exception err) {
                    Log.e(TAG, "error: " + err.toString());
                    return false;
                }
            }
        });

        voiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(HCNetSDK.getInstance().NET_DVR_GetCurrentAudioCompress(loginID,new NET_DVR_COMPRESSION_AUDIO())){
//                    HCNetSDK.getInstance().NET_DVR_StartVoiceCom_MR_V30(loginID, startChannel, new VoiceDataCallBack() {
//                        @Override
//                        public void fVoiceDataCallBack(int voiceHandle, byte[] audios, int audioSize, int flag) {
//                            if(HCNetSDK.getInstance().NET_DVR_VoiceComSendData(voiceHandle, audios, audioSize)){
//                                Toast.makeText(MainActivity.this, "打开语音对讲成功", Toast.LENGTH_SHORT).show();
//                            }else{
//                                Toast.makeText(MainActivity.this, "语音对讲失败", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//
//                }else{
//                    Toast.makeText(MainActivity.this, "语音对讲失败", Toast.LENGTH_SHORT).show();
//                };
                try {
                    if (isTalk == false) {
                        if (startVoiceTalk(loginID) >= 0) {
                            isTalk = true;
                            voiceButton.setText("Stop");
                        }
                    } else {
                        if (stopVoiceTalk()) {
                            isTalk = false;
                            voiceButton.setText("Talk");
                        }
                    }
                } catch (Exception err) {
                    Log.e(TAG, "error: " + err.toString());
                }

            }
        });
    }

    // @Override
    public void surfaceCreated(SurfaceHolder holder) {
        surface.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        Log.i(TAG, "surface is created" + playPort);
        if (-1 == playPort) {
            return;
        }
        Surface surface = holder.getSurface();
        if (true == surface.isValid()) {
            if (false == Player.getInstance()
                    .setVideoWindow(playPort, 0, holder)) {
                Log.e(TAG, "Player setVideoWindow failed!");
            }
        }
    }

    // @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    // @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "Player setVideoWindow release!" + playPort);
        if (-1 == playPort) {
            return;
        }
        if (true == holder.getSurface().isValid()) {
            if (false == Player.getInstance().setVideoWindow(playPort, 0, null)) {
                Log.e(TAG, "Player setVideoWindow failed!");
            }
        }
    }


    private boolean initeSdk() {
        // init net sdk
        if (!HCNetSDK.getInstance().NET_DVR_Init()) {
            Log.e(TAG, "HCNetSDK init is failed!");
            return false;
        }
        HCNetSDK.getInstance().NET_DVR_SetLogToFile(3, "/mnt/sdcard/sdklog/",
                true);
        return true;
    }

    private void startSinglePreview() {
        if (playbackID >= 0) {
            Log.i(TAG, "Please stop palyback first");
            return;
        }
        RealPlayCallBack fRealDataCallBack = getRealPlayerCbf();
        if (fRealDataCallBack == null) {
            Log.e(TAG, "fRealDataCallBack object is failed!");
            return;
        }
        Log.i(TAG, "startChannel:" + startChannel);

        NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        previewInfo.lChannel = startChannel;
        previewInfo.dwStreamType = 0; // substream
        previewInfo.bBlocked = 1;

        playID = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(loginID,
                previewInfo, fRealDataCallBack);
        if (playID < 0) {
            Log.e(TAG, "NET_DVR_RealPlay is failed!Err:"
                    + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return;
        }

        Log.i(TAG,
                "NetSdk Play sucess ***********************3***************************");
        previewButton.setText("Stop");
    }


    private void stopMultiPreview() {
        int i = 0;
        for (i = 0; i < 4; i++) {
            //playView[i].stopPreview();
        }
        playID = -1;
    }

    private void stopSinglePreview() {
        if (playID < 0) {
            Log.e(TAG, "playID < 0");
            return;
        }

        // net sdk stop preview
        if (!HCNetSDK.getInstance().NET_DVR_StopRealPlay(playID)) {
            Log.e(TAG, "StopRealPlay is failed!Err:"
                    + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return;
        }

        playID = -1;
        stopSinglePlayer();
    }

    private void stopSinglePlayer() {
        Player.getInstance().stopSound();
        // player stop play
        if (!Player.getInstance().stop(playPort)) {
            Log.e(TAG, "stop is failed!");
            return;
        }

        if (!Player.getInstance().closeStream(playPort)) {
            Log.e(TAG, "closeStream is failed!");
            return;
        }
        if (!Player.getInstance().freePort(playPort)) {
            Log.e(TAG, "freePort is failed!" + playPort);
            return;
        }
        playPort = -1;
    }

    /**
     * @fn loginNormalDevice
     * @author zhuzhenlei
     * @brief login on device
     *            [out]
     * @return login ID
     */
    private int loginNormalDevice() {
        // get instance
        m_oNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();
        if (null == m_oNetDvrDeviceInfoV30) {
            Log.e(TAG, "HKNetDvrDeviceInfoV30 new is failed!");
            return -1;
        }
        String strIP = IPAddress;
        int nPort = Integer.parseInt(port);
        String strUser = user;
        String strPsd = password;
        // call NET_DVR_Login_v30 to login on, port 8000 as default
        int iLogID = HCNetSDK.getInstance().NET_DVR_Login_V30(strIP, nPort,
                strUser, strPsd, m_oNetDvrDeviceInfoV30);
        if (iLogID < 0) {
            Log.e(TAG, "NET_DVR_Login is failed!Err:"
                    + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return -1;
        }
        if (m_oNetDvrDeviceInfoV30.byChanNum > 0) {
            startChannel = m_oNetDvrDeviceInfoV30.byStartChan;
            channelNumber = m_oNetDvrDeviceInfoV30.byChanNum;
        } else if (m_oNetDvrDeviceInfoV30.byIPChanNum > 0) {
            startChannel = m_oNetDvrDeviceInfoV30.byStartDChan;
            channelNumber = m_oNetDvrDeviceInfoV30.byIPChanNum
                    + m_oNetDvrDeviceInfoV30.byHighDChanNum * 256;
        }
        Log.i(TAG, "NET_DVR_Login is Successful!");

        return iLogID;
    }



    private int loginDevice() {
        int iLogID = -1;

        iLogID = loginNormalDevice();

        return iLogID;
    }

    private ExceptionCallBack getExceptiongCbf() {
        ExceptionCallBack oExceptionCbf = new ExceptionCallBack() {
            public void fExceptionCallBack(int iType, int iUserID, int iHandle) {
                System.out.println("recv exception, type:" + iType);
            }
        };
        return oExceptionCbf;
    }

    private RealPlayCallBack getRealPlayerCbf() {
        RealPlayCallBack cbf = new RealPlayCallBack() {
            public void fRealDataCallBack(int iRealHandle, int iDataType,
                                          byte[] pDataBuffer, int iDataSize) {
                // player channel 1
                processRealData(1, iDataType, pDataBuffer,
                        iDataSize, Player.STREAM_REALTIME);
            }
        };
        return cbf;
    }


    public void processRealData(int iPlayViewNo, int iDataType,
                                byte[] pDataBuffer, int iDataSize, int iStreamMode) {
        if (!needDecode) {
            // Log.i(TAG, "iPlayViewNo:" + iPlayViewNo + ",iDataType:" +
            // iDataType + ",iDataSize:" + iDataSize);
        } else {
            if (HCNetSDK.NET_DVR_SYSHEAD == iDataType) {
                if (playPort >= 0) {
                    return;
                }
                playPort = Player.getInstance().getPort();
                if (playPort == -1) {
                    Log.e(TAG, "getPort is failed with: "
                            + Player.getInstance().getLastError(playPort));
                    return;
                }
                Log.i(TAG, "getPort succ with: " + playPort);
                if (iDataSize > 0) {
                    if (!Player.getInstance().setStreamOpenMode(playPort,
                            iStreamMode)) // set stream mode
                    {
                        Log.e(TAG, "setStreamOpenMode failed");
                        return;
                    }
                    if (!Player.getInstance().openStream(playPort, pDataBuffer,
                            iDataSize, 2 * 1024 * 1024)) // open stream
                    {
                        Log.e(TAG, "openStream failed");
                        return;
                    }
                    if (!Player.getInstance().play(playPort,
                            surface.getHolder())) {
                        Log.e(TAG, "play failed");
                        return;
                    }
                    if (!Player.getInstance().playSound(playPort)) {
                        Log.e(TAG, "playSound failed with error code:"
                                + Player.getInstance().getLastError(playPort));
                        return;
                    }
                }
            } else {
                if (!Player.getInstance().inputData(playPort, pDataBuffer,
                        iDataSize)) {
                    // Log.e(TAG, "inputData failed with: " +
                    // Player.getInstance().getLastError(playPort));
                    for (int i = 0; i < 4000 && playbackID >= 0
                            && !stopPlayback; i++) {
                        if (Player.getInstance().inputData(playPort,
                                pDataBuffer, iDataSize)) {
                            break;

                        }

                        if (i % 100 == 0) {
                            Log.e(TAG, "inputData failed with: "
                                    + Player.getInstance()
                                    .getLastError(playPort) + ", i:" + i);
                        }

                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                        }
                    }
                }

            }
        }

    }

    public static int startVoiceTalk(int iUserID)//only support G711A/U and G722, but G722 Non publication function
    {
        //get the device current valid audio type
        NET_DVR_COMPRESSION_AUDIO compressAud = new NET_DVR_COMPRESSION_AUDIO();
        if(!HCNetSDK.getInstance().NET_DVR_GetCurrentAudioCompress(iUserID, compressAud))
        {
            System.out.println("NET_DVR_GetCurrentAudioCompress failed, error:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return -1;
        }

        AudioCodecParam AudioParam = new AudioCodecParam();
        AudioParam.nVolume = 100; //the volume is between 0~100
        AudioParam.nChannel = AudioCodecParam.AudioChannel.AUDIO_CHANNEL_MONO;
        AudioParam.nBitWidth = AudioCodecParam.AudioBitWidth.AUDIO_WIDTH_16BIT;
        if(compressAud.byAudioEncType == 1)//G711_U
        {
            AudioParam.nCodecType = AudioCodecParam.AudioEncodeType.AUDIO_TYPE_G711U;
            AudioParam.nSampleRate = AudioCodecParam.AudioSampleRate.AUDIO_SAMPLERATE_8K;
            AudioParam.nBitRate = AudioCodecParam.AudioBitRate.AUDIO_BITRATE_16K;
        }
        else if(compressAud.byAudioEncType == 2)//G711_A
        {
            AudioParam.nCodecType = AudioCodecParam.AudioEncodeType.AUDIO_TYPE_G711A;
            AudioParam.nSampleRate = AudioCodecParam.AudioSampleRate.AUDIO_SAMPLERATE_8K;
            AudioParam.nBitRate = AudioCodecParam.AudioBitRate.AUDIO_BITRATE_16K;
        }
        else if(compressAud.byAudioEncType == 0) //G722
        {
            AudioParam.nCodecType = AudioCodecParam.AudioEncodeType.AUDIO_TYPE_G722;
            AudioParam.nSampleRate = AudioCodecParam.AudioSampleRate.AUDIO_SAMPLERATE_16K;
            AudioParam.nBitRate = AudioCodecParam.AudioBitRate.AUDIO_BITRATE_16K;
        }
        else
        {
            System.out.println("the device audio type is not support by AudioEngineSDK for android ,type:" + compressAud.byAudioEncType);
            return -1;
        }
        //start AudioEngine
        if(!startAudioEngine(AudioParam))
        {
            return -1;
        }
        //start HCNetSDK
        if (TalkCbf==null)
        {
            TalkCbf = new VoiceDataCallBack()
            {
                public void fVoiceDataCallBack(int lVoiceComHandle, byte[] pDataBuffer, int iDataSize, int iAudioFlag)
                {
                    processDeviceVoiceData(lVoiceComHandle, pDataBuffer, iDataSize, iAudioFlag);
                }
            };
        }
        m_iVoiceTalkID = HCNetSDK.getInstance().NET_DVR_StartVoiceCom_MR_V30(iUserID, 1, TalkCbf);
        if (-1 == m_iVoiceTalkID)
        {
            stopVoiceTalk();
            System.out.println("NET_DVR_StartVoiceCom_MR_V30 failed,error:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
        }
        return m_iVoiceTalkID;
    }

    private static boolean startAudioEngine(AudioCodecParam AudioParam)
    {
        if(audio == null)
        {
            audio = new AudioEngine(AudioEngine.CAE_INTERCOM);
        }
        //open audio engine
        iRet =audio.open();
        if(iRet != 0)
        {
            System.out.println("audio engine open failed, error:" + iRet);
            return false;
        }
        //set parameter
        iRet = audio.setAudioParam(AudioParam, AudioEngine.PARAM_MODE_PLAY);
        if(iRet != 0)
        {
            System.out.println("audio.setAudioParam PARAM_MODE_PLAY failed, error:" + iRet);
            audio.close();
            return false;
        }
        iRet = audio.setAudioParam(AudioParam, AudioEngine.PARAM_MODE_RECORDE);
        if(iRet != 0)
        {
            System.out.println("audio.setAudioParam PARAM_MODE_RECORDE failed, error:" + iRet);
            audio.close();
            return false;
        }
        //set callback
        if(AudioCbf == null)
        {
            AudioCbf = new AudioEngineCallBack.RecordDataCallBack()
            {
                public void onRecordDataCallBack(byte[] buf, int size)
                {
                    processLocalVoiceData(buf, size);
                }
            };
        }
        iRet = audio.setAudioCallBack(AudioCbf, AudioEngine.RECORDE_DATA_CALLBACK);
        if(iRet != 0)
        {
            System.out.println("audio.setAudioCallBack RECORDE_DATA_CALLBACK failed, error:" + iRet);
            audio.close();
            return false;
        }
        iRet = audio.startPlay();
        if(iRet != 0)
        {
            System.out.println("audio.startPlay failed, error:" + iRet);
            audio.close();
            return false;
        }
        iRet = audio.startRecord();
        if(iRet != 0)
        {
            System.out.println("audio.startRecord failed, error:" + iRet);
            audio.stopPlay();
            audio.close();
            return false;
        }
        return true;
    }

    private static void processDeviceVoiceData(int lVoiceComHandle, byte[] pDataBuffer, int iDataSize, int iAudioFlag)
    {
        audio.inputData(pDataBuffer, iDataSize)	;
    }

    public static boolean stopVoiceTalk()
    {
        HCNetSDK.getInstance().NET_DVR_StopVoiceCom(m_iVoiceTalkID);
        audio.stopRecord();
        audio.stopPlay();
        audio.close();
        return true;
    }

    private static void processLocalVoiceData(byte[] buf, int size)
    {
        HCNetSDK.getInstance().NET_DVR_VoiceComSendData(m_iVoiceTalkID, buf, size);
    }
}
