package com.example.frankie_13071191d.opencvtest;

import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;

public class Main2Activity extends AppCompatActivity {
    private int[][] arrayReceived;
    private int[][][] temp=new int[16][4][4];
    private int frameNo=0;
    ArrayList<Frame> objectArray;
    ArrayList<String> list = new ArrayList<String>();
    ArrayList<String> stringObjectArray=new ArrayList<>();
    private  String[] strings;
    private byte[] data;
    public void data(View view){
        frameNo++;


    }
    private Button tv1;
    TableLayout my_group_tablelayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        //objectArray = (ArrayList<Frame>) getIntent().getExtras().getSerializable("key_array_array");
        my_group_tablelayout= (TableLayout) findViewById(R.id.tableLayout1);

        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String str =  mPrefs.getString("MyObject", "");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>(){}.getType();

        stringObjectArray = gson.fromJson(str, type);


        //new Thread(new Runnable() {
        //    @Override
        //    public void run() {
        //        for (int i = 0; i < stringObjectArray.size(); i++) {
        //            //Frame frame=objectArray.get(i);
        //            splitByNumber(stringObjectArray.get(i), 8);
        //        }
        //        strings = list.toArray(new String[0]);
        //        data = new byte[strings.length];
        //        for (int i = 0; i < strings.length; i++)
        //            data[i] = Byte.parseByte(strings[i], 2);
        //        for (int i = 0; i < data.length - 1; i += 2) {
        //            byte temp = data[i];
        //            data[i] = data[i + 1];
        //            data[i + 1] = temp;
        //        }
        //        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        //        File file = new File(path, "test.txt");
        //        try {
        //            OutputStream os = new FileOutputStream(file, false);    // 第二個參數為是否 append
        //            // 若為 true，則新加入的文字會接續寫在文字檔的最後
        //            os.write(data);
        //            os.close();
        //            runOnUiThread(new Runnable() {
        //                @Override
        //                public void run() {
        //                    Toast.makeText(Main2Activity.this, ByteOrder.nativeOrder().toString(), Toast.LENGTH_SHORT).show();
        //                }
        //            });
        //        } catch (IOException e) {
        //            e.printStackTrace();
        //        }
        //    }}).start();



        if (stringObjectArray!=null)
      fillTable(my_group_tablelayout);

    }
    private String[] splitByNumber(String s, int chunkSize){
        int chunkCount = (s.length() / chunkSize) + (s.length() % chunkSize == 0 ? 0 : 1);
        String[] returnVal = new String[chunkCount];
        for(int i=0;i<chunkCount;i++){
            returnVal[i] = s.substring(i*chunkSize, Math.min((i+1)*chunkSize, s.length()));
            if (!returnVal[i].equals("00000000"))
            {
                list.add(returnVal[i]);

            }

        }
        return returnVal;
    }
    private void fillTable(TableLayout table) {
        table.removeAllViews();

       for (int i = 0; i <stringObjectArray.size(); i++) {
                String text=stringObjectArray.get(i);
                String[] textArray = splitByNumber(text, 8);

                    for (int k = 0; k < textArray.length;k++)
                    {
                        if (textArray[k].equals("00000000"))
                        {
                            textArray[k] = null;
                        }
                    }

            for (int j=0;j<textArray.length;j++){

            TableRow row = new TableRow(Main2Activity.this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            EditText edit = new EditText(Main2Activity.this);
            edit.setInputType(InputType.TYPE_CLASS_TEXT);
            edit.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            edit.setText(textArray[j]);
            edit.setKeyListener(null);
            row.addView(edit);
            table.addView(row);
            }

       }

       // strings = list.toArray(new String[0]);
       // data= new byte[strings.length];
       // for (int i=0;i<strings.length;i++)
       // data[i]=Byte.parseByte(strings[i], 2);
////
       // for (int i=0;i<data.length-1;i+=2){
       //     byte temp=data[i];
       //     data[i]=data[i+1];
       //     data[i+1]=temp;
       // }
////
////
       // File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
       // File file = new File(path, "test.txt");
       // try{
       //     OutputStream os = new FileOutputStream(file, false);    // 第二個參數為是否 append
       //     // 若為 true，則新加入的文字會接續寫在文字檔的最後
       //     os.write(data);
       //     os.close();
       //     Toast.makeText(this, ByteOrder.nativeOrder().toString(),Toast.LENGTH_SHORT).show();
       // }catch(IOException e){
       //     e.printStackTrace();
////
       // }
//
        /*try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write(data);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();

        }*/
    }

    private String inverseQAM(int i,int q){
        String output;
        switch (i){
            case 3:output="10";break;
            case 1:output="11";break;
            case -1:output="01";break;
            case -3:output="00";break;
            default:output="-1";
        }
        switch (q){
            case 3:output.concat("00");break;
            case 1:output.concat("01");break;
            case -1:output.concat("11");break;
            case -3:output.concat("10");break;
        }
        return output;
    }
    private String decode(int[][] input){
        String output;
        output=inverseQAM(input[0][0],input[1][1]);
        output.concat(inverseQAM(input[2][0],input[3][1]));
        output.concat(inverseQAM(input[0][2],input[1][3]));
        output.concat(inverseQAM(input[2][2],input[3][3]));
        return output;
    }
    private String test(){
        String output=new String("");
        int[] xrange={0,4,0,4,8,12,8,12,0,4,0,4,8,12,8,12};
        int[] yrange={0,0,4,4,0,0,4,4,8,8,12,12,8,8,12,12};
        for(int i=0;i<16;i++){
            for (int j =0; j <4; j++) {
                temp[i][j] = Arrays.copyOfRange(arrayReceived[yrange[i]+j], xrange[i],xrange[i]+4);
            }
            output.concat(decode(temp[i]));
        }
        return output;
    }

}