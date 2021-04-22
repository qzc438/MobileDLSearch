package com.qzc.mobiledlsearch;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.bordercloud.sparqlAndroid.SparqlResultModel;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class SparqlActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.qzc.mobiledlsearch.R.layout.activity_sparql);

        try {
            AsyncTask<String, Void, SparqlResultModel> sr = new com.qzc.mobiledlsearch.SparqlTask1().execute();
            SparqlResultModel rows_queryPopulationInFrance = sr.get();
            if (rows_queryPopulationInFrance.getRowCount() > 0) {
                Log.d("MyApp", "Result population in France: " + rows_queryPopulationInFrance.getRows().get(0).get("population"));
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            AsyncTask<String, Void, SparqlResultModel> sr = new com.qzc.mobiledlsearch.SparqlTask2().execute();
            SparqlResultModel res2 = sr.get();
            printResult(res2, 30);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    public static void printResult(SparqlResultModel rs , int size) {
        StringBuilder text = new StringBuilder();
        text.append("\n");
        for (String variable : rs.getVariables()) {
            text.append(String.format("%-"+size+"."+size+"s", variable ) + " | ");
        }
        text.append("\n");
        for (HashMap<String, Object> row : rs.getRows()) {
            for (String variable : rs.getVariables()) {
                text.append(String.format("%-"+size+"."+size+"s", row.get(variable)) + " | ");
            }
            text.append("\n");
        }
        Log.d("MyApp",text.toString());
    }
}

