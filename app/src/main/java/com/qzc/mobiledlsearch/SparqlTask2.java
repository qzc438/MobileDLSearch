package com.qzc.mobiledlsearch;

import android.os.AsyncTask;

import com.bordercloud.sparqlAndroid.SparqlClient;
import com.bordercloud.sparqlAndroid.SparqlClientException;
import com.bordercloud.sparqlAndroid.SparqlResult;
import com.bordercloud.sparqlAndroid.SparqlResultModel;

import java.net.URI;
import java.net.URISyntaxException;

class SparqlTask2 extends AsyncTask<String, Void, SparqlResultModel> {

    private Exception exception;

    protected SparqlResultModel doInBackground(String... params) {
            try {
                URI endpoint = new URI("https://query.wikidata.org/sparql");
                String querySelect  = "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>  \n"
                        + "PREFIX psv: <http://www.wikidata.org/prop/statement/value/>  \n"
                        + "PREFIX p: <http://www.wikidata.org/prop/>  \n"
                        + "PREFIX bd: <http://www.bigdata.com/rdf#>  \n"
                        + "PREFIX wikibase: <http://wikiba.se/ontology#>  \n"
                        + "PREFIX wd: <http://www.wikidata.org/entity/>   \n"
                        + "PREFIX wdt: <http://www.wikidata.org/prop/direct/>  \n"
                        + "\n"
                        + "select distinct  \n"
                        + "?lat ?long ?siteLabel ?siteDescription  \n"
                        + "?site \n"
                        + "#(replace(xsd:string(?site),  \n"
                        + "(concat(xsd:string(?image),\'?width=200\') as ?newimage) \n"
                        + "where { \n"
                        + "        ?site wdt:P31/wdt:P279* wd:Q839954 .  \n"
                        + "        ?site wdt:P17 wd:Q142 . \n"
                        + "        ?site wdt:P18 ?image . \n"
                        + "   ?site p:P625 ?coord . \n"
                        + "\n"
                        + "          ?coord   psv:P625 ?coordValue . \n"
                        + "\n"
                        + "          ?coordValue a wikibase:GlobecoordinateValue ; \n"
                        + "                        wikibase:geoLatitude ?lat ; \n"
                        + "                        wikibase:geoLongitude ?long . \n"
                        + "\n"
                        + "        SERVICE wikibase:label { \n"
                        + "             bd:serviceParam wikibase:language \"en,fr\" . \n"
                        + "        } \n"
                        + "      } \n" +
                        "LIMIT 10";
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