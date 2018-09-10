package com.example.frankie_13071191d.opencvtest;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by frankie_13071191d on 26/1/2017.
 */

public class Output  {

    public static ArrayList<Frame> collectedFrame=new ArrayList<>();
    public static ArrayList<String> stringsOfCollectedFrame=new ArrayList<>();
    public Mat mat;
    boolean decoded=false;
    public Output(int[][] input){
        Frame frame=new Frame(input);
        collectedFrame.add(frame);   //create another object to store the decoded and origional data and a flag;
    }
    public Output(int[][][] input){
        Frame frame=new Frame(input);
        collectedFrame.add(frame);   //create another object to store the decoded and origional data and a flag;
    }
    public static boolean check(){
        boolean flag=true;
        Iterator<Frame> it = collectedFrame.iterator();
        while (it.hasNext()) {
            Frame mat = it.next(); // must be called before you can call i.remove()
            if (mat.decoded==false)
                flag=false;
            break;
        }
        if (flag==true){
            Iterator<Frame> tem = collectedFrame.iterator();
            while (tem.hasNext()) {
                Frame mat = tem.next(); // must be called before you can call i.remove()
               stringsOfCollectedFrame.add(mat.result);
            }
        }
        return flag;
    }

    public static void addFrame(int[][] input){
        Frame frame=new Frame(input);
        collectedFrame.add(frame);
    }

    public static void addFrame(int[][][] input){
        Frame frame=new Frame(input);
        collectedFrame.add(frame);
    }
    public static ArrayList<Frame> getCollectedFrame(){
        return collectedFrame;
    }

}
