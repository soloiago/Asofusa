package com.iago.asofusa.utils;

import java.util.ArrayList;
import java.util.List;

public class PageSourceParser {
	private static final String patternClasificacion = "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellpading=\"0\" cellspacing=\"0\" class=\"CSSTableGenerator\" style=\"width: 100%\">";
	private static final String patternClasificacionEnd = "</table>";
	
	private static final String patternGoleadores = "<table align=\"center\" class=\"CSSTableGenerator\" style=\"width: 100%\">";
	private static final String patternGoleadoresEnd = "</table>";
	
	private static final String patternDeportividad = "Deportividad";
	private static final String patternDeportividadEnd = "</table>";
	
	private static final String patternResultados = "<table align=\"center\" class=\"CSSTableGenerator\" style=\"width: 100%\">";
	private static final String patternResultadosEnd = "</table>";
	
	private static final String patternProximaJornada = "Proxima Jornada";
	private static final String patternProximaJornadaEnd = "</table>";
	
	private static final String patternEquipo = "<table class=\"CSSTableGenerator\" style=\"width: 100%\">";
	private static final String patternEquipoEnd = "<a href=\"javascript:history.back(1)\">";
	
	private static final String patternCalendario = "<table class=\"CSSTableGenerator\" style=\"width: 100%\">";
	private static final String patternCalendarioEnd = "<a href=\"javascript:history.back(1)\">";
	
	private static final String patternListaCompeticiones= "<ul class=\"desplegablecompeticion\">";
	private static final String patternListaCompeticiones2= "competicion=";
	
	
	public static String getClasificacion (String cadena) {
		int coincidencia = cadena.indexOf(patternClasificacion);
		int end = cadena.indexOf(patternClasificacionEnd, coincidencia + patternClasificacion.length());
		String tablaClasificacion = "<html><body><table width='100%25'>" + cadena.substring(coincidencia + patternClasificacion.length(), end + patternClasificacionEnd.length());
		
		tablaClasificacion = tablaClasificacion.replace("h5", "b");

		return tablaClasificacion;
	}
	
	public static String getGoleadores (String cadena) {
		int coincidencia = cadena.indexOf(patternGoleadores);
		//Ahora no nos vale la primera coincidencia
		coincidencia = cadena.indexOf(patternGoleadores, coincidencia + patternGoleadores.length());
		int end = cadena.indexOf(patternGoleadoresEnd, coincidencia + patternGoleadores.length());
		String tablaGoleadores = "<html><body><table width='100%25'>" + cadena.substring(coincidencia + patternGoleadores.length(), end + patternGoleadoresEnd.length());

		tablaGoleadores = tablaGoleadores.replace("<td width=\"39%\">", "<td>");
		tablaGoleadores = tablaGoleadores.replace("<td width=\"54%\">", "<td>");
		tablaGoleadores = tablaGoleadores.replace("<td width=\"7%\">", "<td>");

		return tablaGoleadores;
	}
	
	public static String getDeportividad (String cadena) {
		int coincidencia = cadena.indexOf(patternDeportividad);
		int end = cadena.indexOf(patternDeportividadEnd, coincidencia + patternDeportividad.length());
		String tablaDeportividad = "<html><body><table width='100%25'><tbody><tr><td colspan=\"3\">" + cadena.substring(coincidencia, end + patternDeportividadEnd.length());

		tablaDeportividad = tablaDeportividad.replace("<th width=\"72%\">", "<td>");
		tablaDeportividad = tablaDeportividad.replace("<th width=\"14%\">", "<td>");
		
		return tablaDeportividad;
	}
	
	public static String getResultados (String cadena) {
		int coincidencia = cadena.indexOf(patternResultados);
		int end = cadena.indexOf(patternResultadosEnd, coincidencia + patternResultados.length());
		String tablaResultados = "<html><body><table width='100%25'><tbody><tr><td colspan=\"7\">" + cadena.substring(coincidencia, end + patternResultadosEnd.length());

		tablaResultados = tablaResultados.replace("<td  width=\"2%\">", "<td>");
		tablaResultados = tablaResultados.replace("<td width=\"3%\">", "<td>");
		tablaResultados = tablaResultados.replace("<td  width=\"49%\">", "<td>");
		tablaResultados = tablaResultados.replace("<td  width=\"40%\">", "<td>");
		
		
		return tablaResultados;
	}
	
	public static String getProximaJornada (String cadena) {
		int coincidencia = cadena.indexOf(patternProximaJornada);
		int end = cadena.indexOf(patternProximaJornadaEnd, coincidencia + patternProximaJornada.length());
		String tablaProximaJornada = "<html><body><table width='100%25'><tbody><tr><td colspan=\"10\">" + cadena.substring(coincidencia, end + patternProximaJornadaEnd.length()) + "</html>";

		tablaProximaJornada = tablaProximaJornada.replace("%", "%25");
		/*tablaProximaJornada = tablaProximaJornada.replace("<td width=\"1%\">", "");
		tablaProximaJornada = tablaProximaJornada.replace("<td width=\"2%\">", "<td>");
		tablaProximaJornada = tablaProximaJornada.replace("<td width=\"17%\">", "<td>");
		tablaProximaJornada = tablaProximaJornada.replace("<td width=\"7%\">", "");
		tablaProximaJornada = tablaProximaJornada.replace("<td width=\"30%\">", "");
		tablaProximaJornada = tablaProximaJornada.replace("<td width=\"13%\">", "<td width=50>");
		tablaProximaJornada = tablaProximaJornada.replace("<td width=\"27%\">", "<td>");*/
		
		
		return tablaProximaJornada;
	}

	public static String getEquipo(String cadena) {
		int coincidencia = cadena.indexOf(patternEquipo);
		int end = cadena.indexOf(patternEquipoEnd, coincidencia + patternEquipo.length());
		String tablaEquipo = "<html><body><table width='100%25'><tbody><tr><td colspan=\"10\">" + cadena.substring(coincidencia + patternEquipo.length(), end) + "</td></tr></tbody></table>";

		tablaEquipo = tablaEquipo.replace("<td colspan=\"2\" height=\"11%\" scope=\"col\">", "<td colspan=\"2\" scope=\"col\">");
		tablaEquipo = tablaEquipo.replace("<td scope=\"col\" width=\"40%\">", "<td>");
		tablaEquipo = tablaEquipo.replace("<td scope=\"col\" width=\"20%\">", "<td>");
		tablaEquipo = tablaEquipo.replace("<td scope=\"col\" width=\"10%\">", "<td>");
		tablaEquipo = tablaEquipo.replace("<td height=\"22%\" width=\"2%\">", "<td>");
		
		return tablaEquipo;
	}
	
	public static String getCalendario(String cadena) {
		int coincidencia = cadena.indexOf(patternCalendario);
		int end = cadena.indexOf(patternCalendarioEnd, coincidencia + patternCalendario.length());
		String tablaCalendario = "<html><body><table style=\"font-size:4;\" width='100%25'><tbody><tr><td colspan=\"10\">" + cadena.substring(coincidencia + patternCalendario.length(), end) + "</td></tr></tbody></table>";
		
		tablaCalendario = tablaCalendario.replace("%", "%25");
		
		return tablaCalendario;
	}

	public static List<String[]> getListaCompeticiones(String cadena) {
		List<String[]> listaCompeticiones = new ArrayList<String[]>();
		
		int coincidencia = cadena.indexOf(patternListaCompeticiones);
		int end = cadena.indexOf("</ul>", coincidencia);

		coincidencia = cadena.indexOf(patternListaCompeticiones2, coincidencia + patternListaCompeticiones2.length());
		while (coincidencia != -1 && coincidencia < end) {
			int end1 = cadena.indexOf(">", coincidencia);
			int end2 = cadena.indexOf("<", end1);
			
			listaCompeticiones.add(new String[] {cadena.substring(coincidencia, end1 - 1), cadena.substring(end1 + 1, end2).trim()});
			coincidencia = cadena.indexOf(patternListaCompeticiones2, end2);
		}
		
		return listaCompeticiones;
	}

}
