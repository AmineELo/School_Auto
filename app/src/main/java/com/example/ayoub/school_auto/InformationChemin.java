package com.example.ayoub.school_auto;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class InformationChemin extends AppCompatActivity
{

    //Déclaration du classe infochemin
    Infochemin infochemin;

    //Déclaration du latitude depart,arrive et longitude depart arrive
    double latitudedepart,longitudedepart;
    double latitudearrive,longitudearrive;

    //Tableaux des noms etuditans
    String nomsetud[];

    //Arraylist des infochemins
    ArrayList<Infochemin> arrayList;

    //Variable global
    int j;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_chemin);

        //Arraylist déclaration
        arrayList = new ArrayList<Infochemin>();

        //Recycler View
        final RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        j=0;

        //Button de test
        Button btn = (Button)findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                InformationCheminAdapter informationCheminAdapter = new InformationCheminAdapter(arrayList);
                recyclerView.setAdapter(informationCheminAdapter);
            }
        });

        //Récupérer l'intent
        Intent intent = getIntent();
        String[] latlngall = intent.getStringArrayExtra("tablechemins");
        nomsetud = intent.getStringArrayExtra("tablenoms");

        //Décomposer le tableau recevoir par l'intent
        for(int i =0 ; i < latlngall.length ; i++)
        {
            String chemin = latlngall[i].toString();


            int firstlatlng = chemin.indexOf(";",0);

            String latlngdep = latlngall[i].substring(0,firstlatlng).toString();
            String latlngarr = latlngall[i].substring(firstlatlng+1,latlngall[i].length()).toString();

            int latfisrst = latlngdep.indexOf(",",0);
            latitudedepart = Double.valueOf(latlngdep.substring(0,latfisrst).toString());
            longitudedepart = Double.valueOf(latlngdep.substring(latfisrst + 1, latlngdep.length()).toString());

            int latend = latlngarr.indexOf(",",0);

            latitudearrive = Double.valueOf(latlngarr.substring(0,latend).toString());
            longitudearrive =Double.valueOf(latlngarr.substring(latend + 1, latlngarr.length()).toString());

            //Récupération chemin
            Chemin(latitudedepart, longitudedepart, latitudearrive, longitudearrive);

        }



    }

    //Récupérer le chemin entre 2 points en donnant lat et lng de chacun d'eux
    public void Chemin(double latd,double lngd,double lata,double lnga)
    {
        String url = "http://maps.googleapis.com/maps/api/directions/json?origin="+latd+","+lngd+"&destination="+lata+","+lnga+"&sensor=false&mode=DRIVING&language=fr";
        sendrequest(url);
    }

    private void sendrequest(String url)
    {
        //Send request  //Connecter au serveur pour envoyer le latlng depart et arrive dont le but de récupérer le chemin
        StringRequest stringRequest = new StringRequest(url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        parseurinfochemin(response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        //Au cas d'un erreur au niveau de connexion ou un erreur quelconque
                        Toast.makeText(InformationChemin.this, "Erreur détécte  " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
        Volley.newRequestQueue(this).add(stringRequest); //Ajouter votre request dans le new.... pour l'execuction
        //Fin Send Request
    }

    public void parseurinfochemin(String objectresponse)
    {
        //Recu data sour format Json
        try {
            //Notre Object Json recu
            JSONObject object = new JSONObject(objectresponse);

            //Crétion de notre Array routes
            JSONArray routes = object.getJSONArray("routes");

            //Création de notre array legs
            JSONArray legs = routes.getJSONObject(0).getJSONArray("legs");

            //Création de notre array steps
            JSONArray steps = legs.getJSONObject(0).getJSONArray("steps");

            //Récupérer l'adresse de départ
            String adressedepart = legs.getJSONObject(0).getString("start_address");

            //Récupérer l'adresse d'arrivé
            String adressearrive = legs.getJSONObject(0).getString("end_address");

            //Récupérer la distance entre le départ et l'arrivé
            String distance = legs.getJSONObject(0).getJSONObject("distance").get("value").toString();

            //Récupérer le temps à faire entre le départ et l'arrivé
            String dure = legs.getJSONObject(0).getJSONObject("duration").get("text").toString();

            String infoiteneraire = "";

            for (int i = 0; i < steps.length(); i++)
            {
                //Html.fromHtml pour éliminer les instructions html comme <b> etc.
                Spanned chemin = Html.fromHtml(steps.getJSONObject(i).get("html_instructions").toString());

                //Récupérer le chemin
                infoiteneraire += chemin.toString()+"\n";
            }
            if(j== object.length())
            {
                j=0;
            }
            infochemin = new Infochemin(nomsetud[j].toString(), adressedepart, adressearrive, distance, dure, infoiteneraire);
            arrayList.add(infochemin);

            j=j+1;

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

}

