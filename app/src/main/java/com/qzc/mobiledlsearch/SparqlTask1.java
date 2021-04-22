package com.qzc.mobiledlsearch;

import android.os.AsyncTask;

import com.bordercloud.sparqlAndroid.SparqlClient;
import com.bordercloud.sparqlAndroid.SparqlClientException;
import com.bordercloud.sparqlAndroid.SparqlResult;
import com.bordercloud.sparqlAndroid.SparqlResultModel;

import java.net.URI;
import java.net.URISyntaxException;

class SparqlTask1 extends AsyncTask<String, Void, SparqlResultModel> {

    private Exception exception;

    protected SparqlResultModel doInBackground(String... params) {
        try {
            URI endpoint = new URI("https://query.wikidata.org/sparql");
            String querySelect  =
                    "PREFIX wd: <http://www.wikidata.org/entity/> \n"
                            + "PREFIX wdt: <http://www.wikidata.org/prop/direct/> \n"
                            + "select  ?population \n"
                            + "where { \n"
                            // wd:Q142{France} wdt:P1082{population} ?population .
                            + "        wd:Q142 wdt:P1082 ?population . \n"
                            + "} ";
//            URI endpoint = new URI("http://localhost:3030/Test/");
//            String querySelect  =
//                    "SELECT ?subject ?predicate ?object\n" +
//                            "WHERE {\n" +
//                            "  ?subject ?predicate ?object\n" +
//                            "}\n" +
//                            "LIMIT 25";
            SparqlClient sc = new SparqlClient(false);
            sc.setEndpointRead(endpoint);
            SparqlResult sr = sc.query(querySelect);
            //sc.printLastQueryAndResult();
            return sr.getModel();

        } catch (URISyntaxException | SparqlClientException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        return null;
    }

}