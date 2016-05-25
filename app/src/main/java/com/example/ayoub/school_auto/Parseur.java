package com.example.ayoub.school_auto;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ayoub on 06/02/2016.
 */
public class Parseur
{
    public static String[] loginstatut;
    public static int[] iddriver;
    public static final String JSON_ARRAY = "statut";
    public static final String KEY_NOM = "statut";
    public static final String KEY_DRIVER = "iddriver";

    private JSONArray statutlogin = null;

    private String json;

    public Parseur(String json)
    {
        this.json = json;
    }

    protected void parseJSON()
    {
        JSONObject jsonObject=null;
        try
        {
            jsonObject = new JSONObject(json);
            statutlogin = jsonObject.getJSONArray(JSON_ARRAY);

            loginstatut = new String[statutlogin.length()];
            iddriver = new int[statutlogin.length()];
            for(int i=0;i<statutlogin.length();i++)
            {
                JSONObject jo = statutlogin.getJSONObject(i);
                loginstatut[i] = jo.getString(KEY_NOM);
                iddriver[i] = jo.getInt(KEY_DRIVER);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}
