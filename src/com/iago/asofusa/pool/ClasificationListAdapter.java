package com.iago.asofusa.pool;

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
import com.iago.asofusa.utils.User;

public class ClasificationListAdapter extends ArrayAdapter<User>{
	private Context context;
	List<User> clasificacion;
	List<User> original;
	List<User> fitems;
	int textViewResourceId;

	public ClasificationListAdapter(Context context, int textViewResourceId, List<User> clasificacion) {
		super(context, textViewResourceId, clasificacion);
		this.context = context;
		this.textViewResourceId = textViewResourceId;
		this.clasificacion = clasificacion;
		this.original = new ArrayList<User>(clasificacion);
		this.fitems = new ArrayList<User>(clasificacion);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ClasificacionHolder holder = null;

		if(row == null)
		{
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(textViewResourceId, parent, false);

			holder = new ClasificacionHolder();
			holder.name = (TextView)row.findViewById(R.id.userName);
			holder.points = (TextView)row.findViewById(R.id.userPuntos);
			row.setTag(holder);
		}
		else
		{
			holder = (ClasificacionHolder)row.getTag();
		}

		User user = clasificacion.get(position);
		holder.name.setText(user.getName());
		holder.points.setText(user.getPuntos());
		return row;
	}

	static class ClasificacionHolder
	{
		TextView name;
		TextView points;
	}

}
