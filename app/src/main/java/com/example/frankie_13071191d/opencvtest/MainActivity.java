package com.example.frankie_13071191d.opencvtest;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;
import static org.opencv.core.Core.FONT_HERSHEY_COMPLEX_SMALL;
public class MainActivity extends AppCompatActivity implements CvCameraViewListener2,View.OnTouchListener {
    public boolean onTouch(View arg0, MotionEvent arg1) {
// TODO Auto-generated method stub
        mOpenCvCameraView.focusOnTouch(arg1);
        return true;
    }
    private MenuItem[] mFocusListItems;
    private SubMenu mFocusMenu;
    public boolean onCreateOptionsMenu(Menu menu) {
        List<String> mFocusList = new LinkedList<String>();
        int idx =0;
        mFocusMenu = menu.addSubMenu("Focus");
        mFocusList.add("Continuous Video");
        mFocusList.add("Infinity");
        mFocusList.add("Macro");
        mFocusList.add("Auto");
        mFocusListItems = new MenuItem[mFocusList.size()];
        ListIterator<String> FocusItr = mFocusList.listIterator();
        while(FocusItr.hasNext()){
// add the element to the mDetectorMenu submenu
            String element = FocusItr.next();
            mFocusListItems[idx] = mFocusMenu.add(1,idx,Menu.NONE,element);
            idx++;
        }
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getGroupId() == 1)
        {
            int id = item.getItemId();
            mOpenCvCameraView.setFocusMode(this,id);
        }
        return true;
    }
    private static final String TAG = "OCVSample::Activity";
    //private CameraBridgeViewBase mOpenCvCameraView;
    TextView tv1;
    TextView tv2;
    Vibrator mVibrator;
    private String s;
    private Handler mThreadHandler;
    private HandlerThread mThread;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }
    private Timer timer;
    private TimerTask timerTask;
    private MTCameraView mOpenCvCameraView;
    private int[][] reference=new int[64][4];
    private Frame header;
    private String textHeader;
    private boolean showHeader=false;
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.show_camera);
        mOpenCvCameraView =(MTCameraView)findViewById(R.id.tutorial1_activity_java_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setFocusable(true);
        mOpenCvCameraView.setOnTouchListener(MainActivity.this);
        tv1=(TextView) findViewById(R.id.textView);
        tv2=(TextView) findViewById(R.id.textView2);
        timer= new Timer();
        mVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        timerTask=new TimerTask() {
            @Override
            public void run() {
                count++;
            }
        };
        mThread = new HandlerThread("name");
        mThread.start();
        mThreadHandler=new Handler(mThread.getLooper());
    }
    ArrayList<String> list = new ArrayList<String>();
    ArrayList<Frame> objectArray;
    private String[] strings;
    private byte[] byteData;

    private void splitByNumberLastFrame(String s, int chunkSize){
        int chunkCount = (s.length() / chunkSize) + (s.length() % chunkSize == 0 ? 0 : 1);
        String[] returnVal = new String[chunkCount];

        for(int i=2;i<chunkCount;i++){
            returnVal[i] = s.substring(i*chunkSize, Math.min((i+1)*chunkSize, s.length()));
            // if (!returnVal[i].equals("0000000000000000"))
            //{
            //}
        }

        for (int i=chunkCount-1;i>=0;i--){
             if (returnVal[i].equals("00000000"))
                returnVal[i]=null;
             else
                 break;
        }
        for(int i=2;i<chunkCount;i++){
            if (returnVal[i]!=null)
            list.add(returnVal[i]);
        }
    }
    private String[] splitByNumber(String s, int chunkSize){
        //s=split(s);
        int chunkCount = (s.length() / chunkSize) + (s.length() % chunkSize == 0 ? 0 : 1);
        String[] returnVal = new String[chunkCount];

        for(int i=2;i<chunkCount;i++){
            returnVal[i] = s.substring(i*chunkSize, Math.min((i+1)*chunkSize, s.length()));
           // if (!returnVal[i].equals("0000000000000000"))
            //{
                list.add(returnVal[i]);
            //}
        }
        return returnVal;
    }

    private String split(String s){
        int chunkSize=16;
        int chunkCount = (s.length() / chunkSize) + (s.length() % chunkSize == 0 ? 0 : 1);
        ArrayList<String> list = new ArrayList<String>();
        for(int i=0;i<chunkCount;i++){
            String result = s.substring(i*chunkSize, Math.min((i+1)*chunkSize, s.length()));
            if (!result.equals("0000000000000000"))
            {
                list.add(result);
            }
        }
        StringBuilder sb = new StringBuilder();
        for (String ss : list)
        {
            sb.append(ss);
        }
        return sb.toString();
    }


    private void outputToFIle(){
        objectArray=Output.collectedFrame;
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        File file = new File(path, textHeader.split("/")[1]);
// 第二個參數為是否 append 若為 true，則新加入的文字會接續寫在文字檔的最後
        for (int i = 0; i < objectArray.size(); i++) {
//list.clear();



            Frame frame=objectArray.get(i);

            if (i==(objectArray.size()-1)&&vibrate)
                splitByNumberLastFrame(frame.result,8);
            else
            splitByNumber(frame.result,8);
        }
        strings = list.toArray(new String[0]);
        if (!flag) {
            byteData = new byte[strings.length];
            for (int j = 0; j < strings.length; j++)
                byteData[j] =Integer.valueOf(strings[j], 2).byteValue();
            for (int k = 0; k < byteData.length - 1; k += 2) {
                byte temp = byteData[k];
                byteData[k] = byteData[k + 1];
                byteData[k + 1] = temp;
            }
            try {
                OutputStream os = new FileOutputStream(file, false);
                os.write(byteData);
                os.flush();
                os.close();
                Toast.makeText(this, "finished", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                s = e.toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv1.setText(s);
                    }
                });
            }
        }else {
            int[] value = new int[strings.length];
            for (int j = 0; j < strings.length; j++)
                value[j] = Integer.parseInt(strings[j], 2);
            try {
                FileWriter os = new FileWriter(file, false);
                BufferedWriter bos = new BufferedWriter(os);
                for (int j = 0; j < strings.length; j++)
                    bos.write(Integer.toString(value[j]) + "\n");
                bos.flush();
                bos.close();
                Toast.makeText(this, "finished", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                s = e.toString();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv1.setText(s);
                    }
                });
            }
        }
    }
    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }
    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }
    public void data(View view){
        flag=!flag;
    }
    public void vibrate(View view){
        vibrate=!vibrate;
    }
    //CvCameraViewListener2
    public void onCameraViewStarted(int width, int height) {
        square = new Mat(720, 1280,CvType.CV_8UC4 ,new Scalar(0,0,0));
        overlay=new Mat();
        dummy=new Mat();
        Point p4 = new Point(0, 0); dst_pnt.add(p4);
        Point p5 = new Point(720, 0.0); dst_pnt.add(p5);
        Point p6 = new Point(720, 720.0); dst_pnt.add(p6);
        Point p7 = new Point(0, 720); dst_pnt.add(p7);
        sa1=new Scalar(40, 100, 100);
        sa2=new Scalar(80, 255, 255);
        newPoint = new MatOfPoint2f();
        mRgba=new Mat();
        mGray=new Mat();
    }
    public void onCameraViewStopped() {}
    private Mat mRgba,overlay,startM,endM,perspectiveTransform, square,outputMat,mGray,first,dummy;
    private ArrayList<MatOfPoint> mContours= new ArrayList<>();
    private ArrayList<Double> area=new ArrayList<>();
    private double temp,threshold,peri;
    private MatOfPoint2f newPoint,proint2f;
    private Point[] pArray;
    private MatOfPoint approxf1;
    private Size size = new Size(720, 720);
    private List dst_pnt = new ArrayList(), src_pnt = new ArrayList<>();
    private boolean flag=false,lastFrame=false,b=false,vibrate=false;
    private int[][] data= new int[32][32];
    private int[] xrange={0,4,0,4,8,12,8,12,0,4,0,4,8,12,8,12};
    private int[] yrange={0,0,4,4,0,0,4,4,8,8,12,12,8,8,12,12};
    private Scalar sa1,sa2;
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mContours.clear();
        area.clear();
        if (lastFrame==true) {
            if (overlay!=null)
                overlay.release();
            if (mRgba!=null)
                mRgba.release();
            if (mGray!=null)
                mGray.release();
            return inputFrame.rgba();
        }
        overlay.release();
        overlay=inputFrame.rgba().clone();
//Timing timings=new Timing(TAG,"test");
        mRgba.release();
        mGray.release();
        mRgba = overlay.clone();
        mGray = overlay.clone();
        Imgproc.cvtColor(mRgba, mRgba, Imgproc.COLOR_RGB2HSV, mRgba.channels());
        Core.inRange(mRgba, sa1,sa2, mRgba);
        Imgproc.findContours(mRgba, mContours, dummy , Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        Iterator<MatOfPoint> it = mContours.iterator();
        while (it.hasNext()) {
            MatOfPoint mat = it.next(); // must be called before you can call i.remove()
            temp = (Imgproc.contourArea(mat));
            if (temp > 5000) {
                area.add(temp);
            } else {
                it.remove();
            }
        }
        if (area.size() == 2) {
            threshold = Collections.max(area);
            area.remove(threshold);
            it = mContours.iterator();
            while (it.hasNext()) {
                MatOfPoint mat = it.next();
                temp = (Imgproc.contourArea(mat));
                if (temp == threshold) {
                    it.remove();
                }
            }
            proint2f=new MatOfPoint2f(mContours.get(0).toArray());
            Imgproc.approxPolyDP(proint2f, newPoint, 0.02 * Imgproc.arcLength(proint2f, true), true);
            approxf1 = new MatOfPoint();
            newPoint.convertTo(approxf1, CvType.CV_32S);
            mContours.add(approxf1);
            pArray = mContours.get(1).toArray();
            if (pArray.length == 4) {
                pArray = sorting(pArray);
                src_pnt.clear();
                src_pnt.add(pArray[0]);
                src_pnt.add(pArray[1]);
                src_pnt.add(pArray[2]);
                src_pnt.add(pArray[3]);
                startM = Converters.vector_Point2f_to_Mat(src_pnt);
                endM = Converters.vector_Point2f_to_Mat(dst_pnt);
                perspectiveTransform = Imgproc.getPerspectiveTransform(startM, endM);
                outputMat = new Mat();
                Imgproc.warpPerspective(mGray, outputMat, perspectiveTransform, size);
                mRgba=inputFrame.rgba();
                Mat dst_roi = mRgba.submat(new Rect(280, 0, 720, 720));
                displayText();
                if (!b) {
                    timer.scheduleAtFixedRate(timerTask,100, 100);
                    b=!b;
                }
                outputMat.copyTo(dst_roi);

                return mRgba;
                //return inputFrame.rgba();
            }
        }
//timings.addSplit("work A");
//timings.dumpToLog();
        return inputFrame.rgba();
    }
    private int[][][] tem=new int[64][4][4];
    private int count=0;
    private Scalar sa;
    public int[][] copy(int[][] input) {
        int[][] target = new int[input.length][];
        for (int i=0; i <input.length; i++) {
            target[i] = Arrays.copyOf(input[i], input[i].length);
        }
        return target;
    }
    private int[] tempRe=new int[4];
    private void displayText() {
        for (float i = 1; i < 720; i += 22.5)
            for (float j = 1; j < 720; j += 22.5) {
                first = outputMat.submat((int) j + 10, (int) j + 12, (int) i + 10, (int) i + 12);
                Imgproc.cvtColor(first, first, Imgproc.COLOR_RGB2YUV, first.channels());
                sa = Core.mean(first);
                data[(int) ((i - 1) / 22.5)][(int) ((j - 1) / 22.5)] = (int) sa.val[0];
                Imgproc.cvtColor(first, first, Imgproc.COLOR_YUV2RGB, 3);
                //if (!vibrate) {
                    //if ((int) ((i - 1) / 22.5 + (j - 1) / 22.5) % 2 == 1)
                    //    continue;
                    //Imgproc.putText(outputMat, "" + ((int) sa.val[0] / 10), new Point(i, j + 20), FONT_HERSHEY_COMPLEX_SMALL, 1, new Scalar(255), 1);
                //}
            }
        if (count==0) {
            for (int i = 0; i < 64; i++) {
                for (int j = 0; j < 4; j++) {
                    tem[i][j] = Arrays.copyOfRange(data[(i / 8) * 4 + j], (i % 8) * 4, (i % 8) * 4 + 4);
                }
                reference[i][0]=(tem[i][1][1]+tem[i][3][1])/2;
                reference[i][1]=(tem[i][0][2]+tem[i][2][2])/2;
                reference[i][2]=(tem[i][1][3]+tem[i][3][3])/2;
                reference[i][3]=(tem[i][0][0]+tem[i][2][0])/2;
            }
        }else  if (count==1) {
            for (int i=0;i<4;i++)
                tempRe[i]=reference[0][i];

            for (int i = 0; i < 64; i++) {
                for (int j = 0; j < 4; j++) {
                    tem[i][j] = Arrays.copyOfRange(data[(i / 8) * 4 + j], (i % 8) * 4, (i % 8) * 4 + 4);
                }

                if ((reference[i][0]-reference[i][1])>15 && (reference[i][2]-reference[i][3])>15) {
                    if (((tem[i][1][1] + tem[i][3][1]) / 2 - ((tem[i][0][2] + tem[i][2][2]) / 2)) > 15){
                    reference[i][0] = ((tem[i][1][1] + tem[i][3][1]) / 2 + reference[i][0]) / 2;
                    reference[i][1] = ((tem[i][0][2] + tem[i][2][2]) / 2 + reference[i][1]) / 2;
                    reference[i][2] = ((tem[i][1][3] + tem[i][3][3]) / 2 + reference[i][2]) / 2;
                    reference[i][3] = ((tem[i][0][0] + tem[i][2][0]) / 2 + reference[i][3]) / 2;
                }
                }else {

                    if (((tem[i][1][1] + tem[i][3][1]) / 2 - ((tem[i][0][2] + tem[i][2][2]) / 2)) > 15){
                        reference[i][0]=(tem[i][1][1]+tem[i][3][1])/2;
                        reference[i][1]=(tem[i][0][2]+tem[i][2][2])/2;
                        reference[i][2]=(tem[i][1][3]+tem[i][3][3])/2;
                        reference[i][3]=(tem[i][0][0]+tem[i][2][0])/2;
                    }else {
                        reference[i][0]=230;
                        reference[i][1]=170;
                        reference[i][2]=130;
                        reference[i][3]=60;
                    }

                }

            }


        }else{

                for (int i = 0; i < 64; i++) {


                    for (int j = 0; j < 4; j++) {
                        tem[i][j] = Arrays.copyOfRange(data[(i/8)*4+j], (i%8)*4, (i%8)*4 + 4);
                    }

                    //temp = (tem[i][0][1] + tem[i][0][3] + tem[i][1][0] + tem[i][1][2] + tem[i][2][1] + tem[i][2][3] + tem[i][3][0] + tem[i][3][2]) / 8;
                    for (int n = 0; n < 4; n++)
                        for (int m = 0; m < 4; m++) {
                            int k = 0;
                            int x = tem[i][n][m];
                            if (x >=( (reference[i][0]-reference[i][1])/2+reference[i][1]))
                                k = 3;
                            else if (x < ( (reference[i][0]-reference[i][1])/2+reference[i][1]) && x > ( (reference[i][1]-reference[i][2])/2+reference[i][2]))
                                k = 1;
                            else if (x <= ( (reference[i][1]-reference[i][2])/2+reference[i][2])&& x > ( (reference[i][2]-reference[i][3])/2+reference[i][3]))
                                k = -1;
                            else if (x <= ( (reference[i][2]-reference[i][3])/2+reference[i][3]))
                                k = -3;
                            tem[i][n][m] = k;
                        }
                    tem[i][0][1] = tem[i][0][3] =tem[i][1][0] = tem[i][1][2] =tem[i][2][1] = tem[i][2][3] = tem[i][3][0] = tem[i][3][2]=0;
                    }

             frameNumber = Integer.parseInt(Frame.decode(tem[0]), 2);

            if (countFrame>EOF&&lastFrame==false){
                lastFrame=true;
                while (!Output.check());

                mThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        outputToFIle();
                    }
                });
            }
            else {
                if (frameNumber == countFrame) {
                    countFrame++;
                    if (frameNumber == 1) {
                        header = new Frame(tem, 1);
                                    String result = header.result;
                                    int chunkSize = 8;
                                    int chunkCount = (result.length() / chunkSize) + (result.length() % chunkSize == 0 ? 0 : 1);
                                    ArrayList<String> list = new ArrayList<String>();
                                    for (int i = 2; i < chunkCount; i++) {
                                        String s = result.substring(i * chunkSize, Math.min((i + 1) * chunkSize, result.length()));
                                        if (!s.equals("00000000")) {
                                            list.add(s);
                                        }
                                    }
                                    String[] returnVal = list.toArray(new String[0]);
                                    byte[] _byte = new byte[returnVal.length];
                                    for (int j = 0; j < returnVal.length; j++)
                                        _byte[j] = Integer.valueOf(returnVal[j], 2).byteValue();
                                    textHeader = new String(_byte);
                                    EOF = Integer.parseInt(textHeader.split("/")[0]);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            tv2.setText(textHeader);
                                        }
                                    });
                    }
                    else {
                        Output.addFrame(tem);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv1.setText(""+countFrame);
                            }
                        });
                    }
                }


            }
            for (int i = 0; i < 64; i++)
                for (int j = 0; j < 4; j++)
                    for (int k = 0; k < 4; k++)
                        data[(i/8)*4+ j][(i%8)*4+k] = tem[i][j][k];

            for (float i = 1; i < 720; i += 22.5)
                for (float j = 1; j < 720; j += 22.5) {
                    if ((int)((i - 1) / 22.5+(j - 1) / 22.5)%2==1)
                        continue;
                    Imgproc.putText(outputMat, "" + data[(int)((i - 1) / 22.5)][(int)((j - 1) / 22.5)], new Point(i, j+20), FONT_HERSHEY_COMPLEX_SMALL, 1, new Scalar(255), 1);
                }
        }
    }

    private int countFrame=1;
    private int EOF=1000000000;
    private int frameNumber;
    private Point[] sorting(Point[] input){
        Point[] pArray=new Point[4];
        double mintemp=input[0].x+input[0].y;
        pArray[0]=input[0].clone();
        for(int i=0;i<input.length;i++){
            if((input[i].x+input[i].y)<mintemp) {
                mintemp = input[i].x + input[i].y;
                pArray[0]=input[i].clone();
            }
        }
        double maxtemp=input[0].x+input[0].y;
        pArray[3]=input[0].clone();
        for(int i=0;i<input.length;i++){
            if((input[i].x+input[i].y)>maxtemp) {
                maxtemp = input[i].x + input[i].y;
                pArray[2]=input[i].clone();
            }
        }
        Point[] temp=new Point[2];
        int j=0;
        for(int i=0;i<input.length;i++){
            if ((input[i].x+input[i].y)!=maxtemp&&(input[i].x+input[i].y)!=mintemp) {
                temp[j] = input[i].clone();
                j++;
            }
        }
        if (temp[0].x>temp[1].x){
            pArray[1]=temp[0];
            pArray[3]=temp[1];
        }else {
            pArray[1]=temp[1];
            pArray[3]=temp[0];
        }
        return pArray;
    }
}
class Timing {
    private String mTag;
    private String mLabel;
    private boolean mDisabled;
    ArrayList<Long> mSplits;
    ArrayList<String> mSplitLabels;
    public Timing(String tag, String label) {
        reset(tag, label);
    }
    public void reset(String tag, String label) {
        mTag = tag;
        mLabel = label;
        reset();
    }
    public void reset() {
        mDisabled = false;
        if (mDisabled) return;
        if (mSplits == null) {
            mSplits = new ArrayList<Long>();
            mSplitLabels = new ArrayList<String>();
        } else {
            mSplits.clear();
            mSplitLabels.clear();
        }
        addSplit(null);
    }
    public void addSplit(String splitLabel) {
        if (mDisabled) return;
        long now = SystemClock.elapsedRealtime();
        mSplits.add(now);
        mSplitLabels.add(splitLabel);
    }
    public void dumpToLog() {
        if (mDisabled) return;
        Log.d(mTag, mLabel + ": begin");
        final long first = mSplits.get(0);
        long now = first;
        for (int i = 1; i < mSplits.size(); i++) {
            now = mSplits.get(i);
            final String splitLabel = mSplitLabels.get(i);
            final long prev = mSplits.get(i - 1);
            Log.i(mTag, mLabel + ": " + (now - prev) + " ms, " + splitLabel);
        }
        Log.i(mTag, mLabel + ": end, " + (now - first) + " ms");
    }
}