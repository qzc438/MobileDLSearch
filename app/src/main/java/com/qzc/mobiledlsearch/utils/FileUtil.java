package com.qzc.mobiledlsearch.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;


public class FileUtil {

    public static void writeToFile(String data, Context context, String label) {
        try {
            String orginalText = readFromFile(context, label);
            data = orginalText + data + "\r\n";
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(label + ".txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("File Write", "File write failed: " + e.toString());
        }
    }

    public static void clearFile(Context context, String label) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(label + ".txt", Context.MODE_PRIVATE));
            outputStreamWriter.write("");
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("File Clean", "File clean failed: " + e.toString());
        }
    }

    public static String readFromFile(Context context, String label) {
        String str = "";
        try {
            InputStream inputStream = context.openFileInput(label + ".txt");
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ( (line = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(line).append("\r\n");
                }
                inputStream.close();
                str = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("File Read", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("File Read", "Can not read file: " + e.toString());
        }
        return str;
    }

}
