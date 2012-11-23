package com.iago.asofusa.utils;

import java.util.Comparator;

public class User {
	private String name;
	private String puntos;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPuntos() {
		return puntos;
	}
	public void setPuntos(String puntos) {
		this.puntos = puntos;
	}

	public static Comparator<User> COMPARATOR = new Comparator<User>() {

		public int compare(User u1, User u2)
		{
			return (Integer.valueOf(u2.getPuntos()) - Integer.valueOf(u1.getPuntos()));
		}
	};

}
