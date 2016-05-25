package com.example.ayoub.school_auto;

/**
 * Created by Ayoub on 29/02/2016.
 */
public class Infochemin
{
    String nom_etudiant;
    String depart,arrive;
    String distance,duree;
    String chemin;

    public Infochemin(String nom_etudiant,String depart,String arrive,String distance,String duree,String chemin)
    {
        this.nom_etudiant = nom_etudiant;
        this.depart = depart;
        this.arrive = arrive;
        this.distance = distance;
        this.duree = duree;
        this.chemin = chemin;
    }

    public String toString()
    {
        return nom_etudiant+" "+depart+" "+arrive+" "+distance+" "+duree+" "+chemin;
    }
}
