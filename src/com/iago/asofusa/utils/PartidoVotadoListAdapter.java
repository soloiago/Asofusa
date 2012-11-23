package com.iago.asofusa.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.iago.asofusa.R;

public class PartidoVotadoListAdapter extends ArrayAdapter<Partido>{
	private Context context;
	List<Partido> partidos;
	List<Partido> original;
	List<Partido> fitems;
	int textViewResourceId;


	public PartidoVotadoListAdapter(Context context, int textViewResourceId, List<Partido> partidos) {
		super(context, textViewResourceId, partidos);
		this.context = context;
		this.textViewResourceId = textViewResourceId;
		this.partidos = partidos;
		this.original = new ArrayList<Partido>(partidos);
		this.fitems = new ArrayList<Partido>(partidos);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		PartidoHolder holder = null;

		if(row == null)
		{
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(textViewResourceId, parent, false);

			holder = new PartidoHolder();
			holder.partido = (TextView)row.findViewById(R.id.partido);
			holder.resultadoVotado = (TextView)row.findViewById(R.id.resultadoVotado);
			holder.idPartido = (TextView)row.findViewById(R.id.idPartido);
			holder.fecha = (TextView)row.findViewById(R.id.fechaPartido);
			row.setTag(holder);
		}
		else
		{
			holder = (PartidoHolder)row.getTag();
		}

		Partido partido = partidos.get(position);
		holder.partido.setText(partido.getPartido());
		holder.resultadoVotado.setText(partido.getResultadoPorra());
		holder.idPartido.setText(partido.getId());
		holder.fecha.setText(partido.getFecha());
		return row;
	}

	static class PartidoHolder
	{
		TextView resultadoVotado;
		TextView idPartido;
		TextView partido;
		TextView fecha;
	}

}
