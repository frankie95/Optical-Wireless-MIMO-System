package com.example.frankie_13071191d.opencvtest;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by frankie_13071191d on 31/3/2017.
 */

public class Frame implements Serializable {


    public int[][] frame;
    public int[][][] _3Dframe;
    public String result;
    private int[][][] tem = new int[64][4][4];
    public byte[] decodedFrame;

    public boolean decoded = false;

    public Frame(int[][] input) {
        frame = input;
        new Thread(new Runnable() {
            @Override
            public void run() {
                decodeFrame();
            }
        }).start();

    }

    public Frame(int[][][] input) {
        _3Dframe=input;
        new Thread(new Runnable() {
            @Override
            public void run() {
                _decodeFrame();
            }
        }).start();

    }
    public Frame(int[][][] input,int i) {
        _3Dframe=input;
        _decodeFrame();


    }

    public void _decodeFrame() {
        result=_test();
        decoded=true;
        frame=null;
        _3Dframe=null;
    }
    private String _test(){

        StringBuilder output = new StringBuilder();
        for(int i=0;i<64;i++){
            output.append(decode(_3Dframe[i]));
        }
        return output.toString();
    }

    public void decodeFrame() {
       result=test();
       decoded=true;
       frame=null;
        _3Dframe=null;
    }
    private static String inverseQAM(int i,int q){
        StringBuilder output = new StringBuilder();
        switch (i){
            case 3:output.append("10");break;
            case 1:output.append("11");break;
            case -1:output.append("01");break;
            case -3:output.append("00");break;
            default:output.append("00");
        }
        switch (q){
            case 3:output.append("00");break;
            case 1:output.append("01");break;
            case -1:output.append("11");break;
            case -3:output.append("10");break;
        }
        return output.toString();
    }
    public static String decode(int[][] input){
        StringBuilder output = new StringBuilder();
        output.append(inverseQAM(input[0][0],input[1][1]));
        output.append(inverseQAM(input[0][2],input[1][3]));
        output.append(inverseQAM(input[2][0],input[3][1]));
        output.append(inverseQAM(input[2][2],input[3][3]));
        return output.toString();
    }

    private int[] xrange = {0, 4, 0, 4, 8, 12, 8, 12, 0, 4, 0, 4, 8, 12, 8, 12};
    private int[] yrange = {0, 0, 4, 4, 0, 0, 4, 4, 8, 8, 12, 12, 8, 8, 12, 12};
    private String test(){

        StringBuilder output = new StringBuilder();
        for(int i=0;i<16;i++){
            for (int j =0; j <4; j++) {
                tem[i][j] = Arrays.copyOfRange(frame[xrange[i]+j], yrange[i],yrange[i]+4);
            }
            output.append(decode(tem[i]));
        }
        return output.toString();
    }
}
