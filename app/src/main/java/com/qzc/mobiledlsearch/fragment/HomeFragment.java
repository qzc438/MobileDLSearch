package com.qzc.mobiledlsearch.fragment;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.qzc.mobiledlsearch.R;
import com.qzc.mobiledlsearch.utils.OntologyAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private static final String EXTRA_TEXT = "text";
    private TextView fragmentText;
    public static List<String> applicationDomainList;
    public static List<String> applicationAreaList;
    public static List<String> dataSensorTypeList;
    public static List<String> dataFeatureList;
    public static List<String> modelBackendList;
    public static List<String> modelTypeList;
    public static List<String> modelLossFunctionList;
    public static List<String> modelOptimiserList;
    public static List<String> layerTypeList;
    public static List<String> coreLayerTypeList;
    public static List<String> functionalLayerTypeList;

    public static HomeFragment createFor(String text) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        String text = args != null ? args.getString(EXTRA_TEXT) : "";
        fragmentText = view.findViewById(R.id.fragment_text);
        fragmentText.setText(text);
        // find application domain
        new AsyncApplicationDomain().execute();
        // find application area
        new AsyncApplicationArea().execute();
        // find data sensor type
        new AsyncDataSourceType().execute();
        // find data feature type
        new AsyncDataFeature().execute();
        // find model backend
        new AsyncModelBackend().execute();
        // find model type
        new AsyncModelType().execute();
        // find model loss function
        new AsyncModelLossFunction().execute();
        // find model optimiser
        new AsyncModelOptimiser().execute();
        // find layer type
        new AsyncLayerType().execute();
        // find core layer type
        new AsyncCoreLayerType().execute();
        // find functional layer type
        new AsyncFunctionalLayerType().execute();

    }

    // async application domain function
    private class AsyncApplicationDomain extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = OntologyAPI.getApplicationDomain();
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONObject json;
            List<String> myList;
            try {
                json = new JSONObject(result);
                JSONArray jsonArray = json.getJSONObject("results").getJSONArray("bindings");
                myList = new ArrayList<String>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    if (jsonObject.has("applicationDomain")) {
                        String a = jsonObject.getJSONObject("applicationDomain").getString("value").split("#")[1];
                        myList.add(a);
                    }
                }
                applicationDomainList = myList;
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    // async application area function
    private class AsyncApplicationArea extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
//            SharedPreferences sharedPreferences= HomeFragment.this.getActivity().getSharedPreferences("filter", HomeFragment.this.getActivity().MODE_PRIVATE);
//            String domain =sharedPreferences.getString("Domain", "");
            String result = OntologyAPI.getApplicationArea("Healthcare");
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONObject json;
            List<String> myList;
            try {
                json = new JSONObject(result);
                JSONArray jsonArray = json.getJSONObject("results").getJSONArray("bindings");
                myList = new ArrayList<String>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    if (jsonObject.has("applicationArea")) {
                        String a = jsonObject.getJSONObject("applicationArea").getString("value").split("#")[1];
                        myList.add(a);
                    }
                }
                applicationAreaList = myList;
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    // async data source type
    private class AsyncDataSourceType extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = OntologyAPI.getDataSourceType();
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONObject json;
            List<String> myList;
            try {
                json = new JSONObject(result);
                JSONArray jsonArray = json.getJSONObject("results").getJSONArray("bindings");
                myList = new ArrayList<String>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    if (jsonObject.has("dataSourceType")) {
                        String a = jsonObject.getJSONObject("dataSourceType").getString("value").split("#")[1];
                        myList.add(a);
                    }
                }
                dataSensorTypeList = myList;
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    // async data feature
    private class AsyncDataFeature extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = OntologyAPI.getDataFeature();
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONObject json;
            List<String> myList;
            try {
                json = new JSONObject(result);
                JSONArray jsonArray = json.getJSONObject("results").getJSONArray("bindings");
                myList = new ArrayList<String>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    if (jsonObject.has("dataFeature")) {
                        String a = jsonObject.getJSONObject("dataFeature").getString("value");
                        String[] as = a.split(",");
                        for (int j = 0; j < as.length; j++) {
                            myList.add(as[j]);
                        }

                    }
                }
                dataFeatureList = myList;
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    // async model backend
    private class AsyncModelBackend extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = OntologyAPI.getModelBackend();
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONObject json;
            List<String> myList;
            try {
                json = new JSONObject(result);
                JSONArray jsonArray = json.getJSONObject("results").getJSONArray("bindings");
                myList = new ArrayList<String>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    if (jsonObject.has("backendName")) {
                        String a = jsonObject.getJSONObject("backendName").getString("value");
                        myList.add(a);
                    }
                }
                modelBackendList = myList;
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    // async model type
    private class AsyncModelType extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = OntologyAPI.getModelType();
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONObject json;
            List<String> myList;
            try {
                json = new JSONObject(result);
                JSONArray jsonArray = json.getJSONObject("results").getJSONArray("bindings");
                myList = new ArrayList<String>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    if (jsonObject.has("modelType")) {
                        String a = jsonObject.getJSONObject("modelType").getString("value").split("#")[1];
                        myList.add(a);
                    }
                }
                modelTypeList = myList;
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    // async model loss function
    private class AsyncModelLossFunction extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = OntologyAPI.getModelLossFunction();
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONObject json;
            List<String> myList;
            try {
                json = new JSONObject(result);
                JSONArray jsonArray = json.getJSONObject("results").getJSONArray("bindings");
                myList = new ArrayList<String>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    if (jsonObject.has("lossFunctionName")) {
                        String a = jsonObject.getJSONObject("lossFunctionName").getString("value");
                        myList.add(a);
                    }
                }
                modelLossFunctionList = myList;
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    // async model optimiser
    private class AsyncModelOptimiser extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = OntologyAPI.getModelOptimiser();
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONObject json;
            List<String> myList;
            try {
                json = new JSONObject(result);
                JSONArray jsonArray = json.getJSONObject("results").getJSONArray("bindings");
                myList = new ArrayList<String>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    if (jsonObject.has("optimiserName")) {
                        String a = jsonObject.getJSONObject("optimiserName").getString("value");
                        myList.add(a);
                    }
                }
                modelOptimiserList = myList;
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    // async layer type
    private class AsyncLayerType extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = OntologyAPI.getLayerType();
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONObject json;
            List<String> myList;
            try {
                json = new JSONObject(result);
                JSONArray jsonArray = json.getJSONObject("results").getJSONArray("bindings");
                myList = new ArrayList<String>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    if (jsonObject.has("layerType")) {
                        String a = jsonObject.getJSONObject("layerType").getString("value").split("#")[1];
                        myList.add(a);
                    }
                }
                layerTypeList = myList;
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    // async core layer type
    private class AsyncCoreLayerType extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = OntologyAPI.getCoreLayerType();
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONObject json;
            List<String> myList;
            try {
                json = new JSONObject(result);
                JSONArray jsonArray = json.getJSONObject("results").getJSONArray("bindings");
                myList = new ArrayList<String>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    if (jsonObject.has("coreLayerType")) {
                        String a = jsonObject.getJSONObject("coreLayerType").getString("value").split("#")[1];
                        myList.add(a);
                    }
                }
                coreLayerTypeList = myList;
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    // async functional layer type
    private class AsyncFunctionalLayerType extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = OntologyAPI.getFunctionalLayerType();
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            JSONObject json;
            List<String> myList;
            try {
                json = new JSONObject(result);
                JSONArray jsonArray = json.getJSONObject("results").getJSONArray("bindings");
                myList = new ArrayList<String>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    if (jsonObject.has("functionalLayerType")) {
                        String a = jsonObject.getJSONObject("functionalLayerType").getString("value").split("#")[1];
                        myList.add(a);
                    }
                }
                functionalLayerTypeList = myList;
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
