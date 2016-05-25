package com.example.ayoub.school_auto;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity
{
    //Button login
    Button login;

    //Edittext (username,password)
    EditText username,password;

    //Mettre vos données (username,password)
    String user,pass;

    //Le statut du login(true/false)
    String statutlog="";

    //Driver id por metrre l'id driver selon login/mdp
    int driverid = 0;

    //Key id_driver pour passer en intent
    String Key_Driver = "iddriver";



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login=(Button)findViewById(R.id.login);

        username=(EditText)findViewById(R.id.username);
        password=(EditText)findViewById(R.id.password);

        //Evenement  click sur le boutton login
        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                login();
            }
        });
    }

    private void login()
    {
        user = username.getText().toString();
        pass = password.getText().toString();

        if(user.trim().equals(""))
        {
            username.setError("Username is required");
        }

        else if(pass.trim().equals(""))
        {
            password.setError("Password is required");
        }

        else
        {
            connexioncoteandroid();
        }

    }

    //Connexion au serveur(cote mobile) grace au bibliotèque Volley
    public void connexioncoteandroid()
    {
        String url = "http://transport-dispatcher.esy.es/api/Driver_login.php"; //Votre url (Mettre à jour votre url selon le besoin)

        //Faire connexion avec Volley
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    //Listner en cas de terminaison du request faire ceci ci dessous
                    @Override
                    public void onResponse(String response)
                    {
                        showJSON(response);
                    }
                },

                //Listner en cas d'erreur
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        //Au cas d'un erreur au niveau de connexion ou un erreur quelconque
                        Toast.makeText(LoginActivity.this, "Erreur détécte  " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                })
                //Envoyer vos données(login,mdp) avec  le request pour tester avec celle dans la bd
        {
            @Override
            protected Map<String,String> getParams() throws AuthFailureError
            {
                HashMap hashMap = new HashMap();
                hashMap.put("username",user);
                hashMap.put("password",pass);
                return hashMap;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest); //Ajouter votre request dans le new.... pour l'execucter
    }

    //Fonction pour afficher notre Json et faire le test sur le login et mdp est ce qu'il est authentique
    private void showJSON(String json)
    {
        Parseur pj = new Parseur(json);
        pj.parseJSON();
        String[] statut = Parseur.loginstatut;
        int[] driver = Parseur.iddriver;

        for(int i = 0;i<statut.length;i++)
        {
            statutlog = statut[i].toString();
            driverid = driver[i];
        }
        if(statutlog.equals("true"))
        {
            //Lancer l'autre activity en passant l'id driver à l'autre activvity
            Intent i = new Intent(LoginActivity.this,Acceuil_Activity.class);
            i.putExtra(Key_Driver,driverid);
            startActivity(i);
        }
        else
        {
            Toast.makeText(this,"Verifier votre login et mot de passe",Toast.LENGTH_LONG).show();
        }
    }

}
