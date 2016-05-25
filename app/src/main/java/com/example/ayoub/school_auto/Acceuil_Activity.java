package com.example.ayoub.school_auto;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Acceuil_Activity extends Activity implements DirectionCallback, LocationListener
{

    // GoogleMap class
    private GoogleMap googleMap;

    //Driver id recu par l'autre activity
    int iddriver;

    //LatLng du Driver
    LatLng driver;

    //Tableau d'images marqueurs
    int[] tabicone;

    //Button pour accéeder à l'autre intent
    Button info;

    //Tableau contient noms des etudiant
    String noms[];

    //Tabeau qui va prend les latitudes et longitudes
    String tablatlng[];

    //Arrayliste donnée marqueurs
    ArrayList<String> datamarker;

    //___________ partie amine ________________

    public static final String URL = "http://transport-dispatcher.esy.es/api/";
    public static final String LAT = "lat";
    public static final String LNG = "lng";
    public static final String ID_BUS = "id_bus";

    public static final long MIN_TIME = 2000;
    public static final float MIN_DISTANCE = 10;

    private LocationManager locationManager;
    private Location userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceuil);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);

        //Donnée recevez par l'Intent
        Intent i = getIntent();
        iddriver = i.getIntExtra("iddriver",0);

        //Ce tableau contient tous les marquerus numérotés
        tabicone = new int[]{R.drawable.a,R.drawable.b,R.drawable.c,R.drawable.d,R.drawable.e,R.drawable.f,R.drawable.g,R.drawable.h,R.drawable.i,R.drawable.j,R.drawable.h,R.drawable.i,R.drawable.j,R.drawable.k,R.drawable.l,R.drawable.m,R.drawable.n,R.drawable.o,R.drawable.p,R.drawable.q,R.drawable.r,R.drawable.s,R.drawable.t,R.drawable.u,R.drawable.v,R.drawable.w,R.drawable.x,R.drawable.z,R.drawable.aa,R.drawable.bb,R.drawable.ff,R.drawable.cc};

        //Button pour lancer l'autre intent qui contient les infos sur chemins
        info = (Button)findViewById(R.id.infochemin);
        info.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(Acceuil_Activity.this,InformationChemin.class);
                i.putExtra("tablechemins",tablatlng);
                i.putExtra("tablenoms",noms);
                startActivity(i);

            }
        });

        //Initialisation d'array des données marqueurs
        datamarker = new ArrayList<String>();

        //Création du map
        try
        {
            if (googleMap == null)
            {
                googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            }
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            googleMap.setMyLocationEnabled(true);

            //The Camera Position
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(34.251971, -6.589106))
                    .bearing(6)
                    .zoom(15)
                    .tilt(1)
                    .build();

            //Animate a map with the CameraPosition
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            //LatLng du chauffeur
            driver = new LatLng(34.251971, -6.589106);

            //Ajouter le chauffeur sur la carte
            googleMap.addMarker(new MarkerOptions()
                    .position(driver)
                    .title("Driver")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

            //Visualisation des etudiants sur la carte
            envoyerdemande();

            //Evénement click sur le marqueur
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
            {
                @Override
                public boolean onMarkerClick(Marker marker)
                {
                    Markerclick(marker);
                    return false;
                }
            });
        }
      catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    //Fonction pour connecter au Etudiant.php qui est aussi connecter au bd
    public void envoyerdemande()
    {
        final String url = "http://transport-dispatcher.esy.es/api/Etudiant.php?id_driver="+iddriver; //Notre url
        StringRequest stringRequest = new StringRequest(url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                    affiche_etudiant(response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        //Au cas d'un erreur au niveau de connexion ou un erreur quelconque
                        //Toast.makeText(Acceuil_Activity.this, "Erreur détécte  " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
        Volley.newRequestQueue(this).add(stringRequest); //Ajouter votre request dans le new.... pour l'execucter
    }


    //Afficher les etudiants d'un bus affecter à un driver et l'ajouter au map
    public void affiche_etudiant(String objectjson)
    {
        //Recu data sour format Json
        try
        {

            //Notre Object Json
            JSONObject object = new JSONObject(objectjson);

            //Notre Arrayd'objet recu
            JSONArray array = object.getJSONArray("students");

            //Création de notre tableau qui contient les latslngs departarrive
            tablatlng = new String[array.length()+1];

            //Creation des tableaux contenant les infos de chaque etuiant (nom,prenom,lat,lng)
            noms       = new String[array.length()];
            String prenoms[]    = new String[array.length()];
            double latitudes[]  = new double[array.length()];
            double longitudes[] = new double[array.length()];

            //Remplissage de vos tableaux
            for(int i = 0 ; i<array.length() ; i++)
            {
                JSONObject jsonObject = array.getJSONObject(i);
                noms[i]       = jsonObject.getString("firstname");
                prenoms[i]    = jsonObject.getString("lastname");
                latitudes[i]  = jsonObject.getDouble("latitude");
                longitudes[i] = jsonObject.getDouble("longitude");


            }

            //Affectation des marqueurs à votre map et le numéroter
            LatLng origin;
            LatLng destination;
            for(int i = 0 ; i<=array.length() ; i++)
            {
                if(i!=array.length())
                {
                    MarkerOptions marker = new MarkerOptions().position(new LatLng(latitudes[i], longitudes[i])).title(noms[i]+prenoms[i]).flat(true).icon(BitmapDescriptorFactory.fromResource(tabicone[i]));
                    googleMap.addMarker(marker);
                }
                if(i>0 && i!=array.length()-1 && i!=array.length())
                {
                    origin = new LatLng(latitudes[i-1],longitudes[i-1]);
                    destination = new LatLng(latitudes[i], longitudes[i]);
                    tablatlng[i] =latitudes[i-1]+","+longitudes[i-1]+";"+latitudes[i]+","+longitudes[i];
                }
                else if(i==array.length()-1)
                {
                    origin = new LatLng(latitudes[i-1],longitudes[i-1]);
                    destination = new LatLng(latitudes[i],longitudes[i]);
                    tablatlng[i] =latitudes[i-1]+","+longitudes[i-1]+";"+latitudes[i]+","+longitudes[i];
                }
                else if(i==array.length())
                {
                    origin = new LatLng(latitudes[i-1],longitudes[i-1]);
                    destination = new LatLng(driver.latitude, driver.longitude);
                    tablatlng[i] =latitudes[i-1]+","+longitudes[i-1]+";"+driver.latitude+","+driver.longitude;
                }
                else
                {
                    origin = new LatLng(driver.latitude, driver.longitude);
                    destination = new LatLng(latitudes[i],longitudes[i]);
                    tablatlng[i] =driver.latitude+","+driver.longitude+";"+latitudes[i]+","+longitudes[i];
                }


                //Dessiner l'iténéraire
                routing(origin,destination);

            }

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
    //Fonction fait le routage
    public void routing(LatLng origin,LatLng destination)
    {
        GoogleDirection.withServerKey("AIzaSyDjme2dixa7N-Yuwepwg4t18vcQqKed7U0")
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(this);
    }
    @Override
    public void onDirectionSuccess(Direction direction)
    {
        if (direction.isOK())
        {
            //Toast.makeText(this,"Routing passer ",Toast.LENGTH_LONG).show();
            ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
            googleMap.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 5, Color.RED));
        }
    }

    @Override
    public void onDirectionFailure(Throwable throwable)
    {
       // Toast.makeText(this, "Routing non passer ", Toast.LENGTH_LONG).show();
    }

    public void Markerclick(final Marker marker)
    {
        final boolean[] test = {false};

        //Teste dans l'array si le marqueur cliquer est existe dans le tableau
        for(int i = 0 ; i < datamarker.size() ; i++)
        {
            String idmarker = marker.getId().toString();
            if(datamarker.get(i).equals(idmarker))
            {
                test[0] = true;
            }
        }

        if(test[0] == true)
        {
            //Toast.makeText(Acceuil_Activity.this,"Rien à faire "+marker.getTitle()+" dèja importer",Toast.LENGTH_LONG).show();
        }

        else
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            // set title
            alertDialogBuilder.setTitle("Importation etudiant");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Click Oui pour importer")
                    .setCancelable(false)
                    .setPositiveButton("Oui",new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog,int id)
                        {
                            // if this button is clicked, close
                            // current activity

                                String nom = marker.getTitle().toString(); // récupérer le Nom etudiant avant de mettre à jour le marqueur

                            //Traitment
                                marker.setTitle(marker.getTitle()+" est importer");
                                //testcondition = marker.getTitle().toString();
                                String val = marker.getId();
                                datamarker.add(val);
                                dialog.cancel();
                                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                marker.hideInfoWindow();
                                marker.showInfoWindow();
                            //MAJ dans la Bd

                            updatestatutetudiant(nom);
                        }
                    })
                    .setNegativeButton("Non",new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog,int id)
                        {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();

        }

    }

    //Pour modifier le statut de l'étudiant dans la Bd s'il est importer ou pas
    public void updatestatutetudiant(final String nometudiant)
    {
        //Toast.makeText(this,"Le nom est "+nometudiant,Toast.LENGTH_LONG).show();

        String url = "http://transport-dispatcher.esy.es/api/Etudiant.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,

         new Response.Listener<String>()
            {
            @Override
            public void onResponse(String response)
            {
              //Toast.makeText(Acceuil_Activity.this,"Etat d'update est "+response,Toast.LENGTH_LONG).show();
            }
        },

         new Response.ErrorListener()
            {
            @Override
            public void onErrorResponse(VolleyError error)
            {
            //Toast.makeText(Acceuil_Activity.this,"Erreur détecté",Toast.LENGTH_LONG).show();
            }
        })
        //Envoyer vos données(nom etudiant) avec  le request pour tester avec celle dans la bd
        {
            @Override
            protected Map<String,String> getParams() throws AuthFailureError
            {
                HashMap hashMap = new HashMap();
                hashMap.put("nometudiant",nometudiant);
                return hashMap;
            }
        };
     Volley.newRequestQueue(this).add(stringRequest);
    }

    @Override
    public void onLocationChanged(Location location) {

         userLocation = location;
//        Database_Manager db = new Database_Manager(this);
//        db.addElement(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));

        Toast.makeText(Acceuil_Activity.this, "true", Toast.LENGTH_SHORT).show();

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

        sendData();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    public void sendData(){

        StringRequest stringRequest =  new StringRequest(Request.Method.POST, URL + "updateLocation.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        Toast.makeText(Acceuil_Activity.this, response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(Acceuil_Activity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();

                params.put(LAT, String.valueOf(userLocation.getLatitude()));
                params.put(LNG, String.valueOf(userLocation.getLongitude()));
                //params.put(ID_BUS,))); mnin tdekhl id bus f login l SharedPreference jebdo w dekhlo hnaya yeb9a yemchi f requete HTTP ook
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}

