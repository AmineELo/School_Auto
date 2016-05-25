package com.example.ayoub.school_auto;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ayoub on 04/03/2016.
 */

public class InformationCheminAdapter extends RecyclerView.Adapter<InformationCheminAdapter.InfoCheminHolder>
{
    List<Infochemin> infoChemins;

    public InformationCheminAdapter(List<Infochemin> infoChemins)
    {
        this.infoChemins = infoChemins;
    }

    @Override
    public InfoCheminHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View row = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.information_chemin,viewGroup,false);
        InfoCheminHolder holder = new InfoCheminHolder(row);
        return holder;
    }

    @Override
    public void onBindViewHolder(InfoCheminHolder infoCheminHolder, int i)
    {
        Infochemin infochemin = infoChemins.get(i);
        infoCheminHolder.nometud.setText(infochemin.nom_etudiant);
        infoCheminHolder.depart.setText(infochemin.depart);
        infoCheminHolder.arrive.setText(infochemin.arrive);
        infoCheminHolder.distance.setText(infochemin.distance);
        infoCheminHolder.duree.setText(infochemin.duree);
        infoCheminHolder.chemin.setText(infochemin.chemin);
    }

    @Override
    public int getItemCount()
    {
        return infoChemins.size();
    }

    class InfoCheminHolder extends RecyclerView.ViewHolder
    {
        TextView nometud;
        TextView depart,arrive;
        TextView distance,duree;
        TextView chemin;

        public InfoCheminHolder(View itemView)
        {
            super(itemView);
            nometud = (TextView)itemView.findViewById(R.id.nometudiant);
            depart = (TextView)itemView.findViewById(R.id.dadresse);
            arrive = (TextView)itemView.findViewById(R.id.aadresse);
            distance = (TextView)itemView.findViewById(R.id.distance);
            duree = (TextView)itemView.findViewById(R.id.dure);
            chemin = (TextView)itemView.findViewById(R.id.chemin);


        }
    }
}
