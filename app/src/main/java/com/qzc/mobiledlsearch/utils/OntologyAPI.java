package com.qzc.mobiledlsearch.utils;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.qzc.mobiledlsearch.SampleActivity;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class OntologyAPI {

    public static final String baseURL = "http://118.139.92.31:8080/OntologyAPI/api";

    public static String findTextResult(String strURL) {
        URL url = null;
        HttpURLConnection connection = null;
        StringBuffer textResult = new StringBuffer();
        try {
            url = new URL(strURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            Scanner scanner = new Scanner(connection.getInputStream());
            while (scanner.hasNextLine()) {
                textResult.append(scanner.nextLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return textResult.toString();
    }

    public static String addObject(String json, String strURL) {
        URL url = null;
        HttpURLConnection connection = null;
        StringBuffer textResult = new StringBuffer();
        try {
            // Gson gson = new Gson();
            // String jsonPerson = gson.toJson(object).replace("[", "").replace("]","");
            //url = new URL("http://118.138.64.159:8080/MyMovieMemoir/webresources/moviememoir.person/");
            url = new URL(strURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setFixedLengthStreamingMode(json.getBytes().length);
            connection.setRequestProperty("Content-Type", "application/json");
            PrintWriter out = new PrintWriter(connection.getOutputStream());
            out.print(json);
            out.flush();
            out.close();
            Scanner scanner = new Scanner(connection.getInputStream());
            while (scanner.hasNextLine()) {
                textResult.append(scanner.nextLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return textResult.toString();
    }

    // login
    public static String findPersonByUsernameAndPassword() {
        String methodURL = "/login/username=admin&password=admin";
        String strURL = baseURL + methodURL;
        return findTextResult(strURL);
    }

    // application domain
    public static String getApplicationDomain() {
        String methodURL = "/filter/getApplicationDomain";
        String strURL = baseURL + methodURL;
        return findTextResult(strURL);
    }

    // application area
    public static String getApplicationArea(String applicationDomain) {
        String methodURL = "/filter/getApplicationArea/applicationDomain=" + applicationDomain;
        String strURL = baseURL + methodURL;
        return findTextResult(strURL);
    }

    // data source type
    public static String getDataSourceType() {
        String methodURL = "/filter/getDataSourceType";
        String strURL = baseURL + methodURL;
        return findTextResult(strURL);
    }

    // model type
    public static String getModelType() {
        String methodURL = "/filter/getModelType";
        String strURL = baseURL + methodURL;
        return findTextResult(strURL);
    }

    // layer type
    public static String getLayerType() {
        String methodURL = "/filter/getLayerType";
        String strURL = baseURL + methodURL;
        return findTextResult(strURL);
    }

    // core layer type
    public static String getCoreLayerType() {
        String methodURL = "/filter/getCoreLayerType";
        String strURL = baseURL + methodURL;
        return findTextResult(strURL);
    }

    // functional layer type
    public static String getFunctionalLayerType() {
        String methodURL = "/filter/getFunctionalLayerType";
        String strURL = baseURL + methodURL;
        return findTextResult(strURL);
    }

    // find overview information
    public static String getOverviewInformation(String jsonParameterBean){
        String methodURL = "/search/getOverviewInformation";
        String strURL = baseURL + methodURL;
        return addObject(jsonParameterBean, strURL);
    }

    // find data detail information
    public static String getDataDetail(String dataID){
        String methodURL = "/detail/getDataDetail/dataID="+dataID;
        String strURL = baseURL + methodURL;
        return findTextResult(strURL);
    }

    // find model detail information
    public static String getModelDetail(String modelID){
        String methodURL = "/detail/getModelDetail/modelID="+modelID;
        String strURL = baseURL + methodURL;
        return findTextResult(strURL);
    }

    //find layer detail information
    public static String getLayerDetail(String modelID){
        String methodURL = "/detail/getLayerDetail/modelID="+modelID;
        String strURL = baseURL + methodURL;
        return findTextResult(strURL);
    }

}
