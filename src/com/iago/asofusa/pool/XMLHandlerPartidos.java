package com.iago.asofusa.pool;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.iago.asofusa.utils.Partido;

public class XMLHandlerPartidos extends DefaultHandler {
	private List<Partido> partidos = null;
	private Partido currentPartido = null;
	private StringBuilder text = null;

	public List<Partido> getPartidos() {
		return partidos;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		if (currentPartido != null)
			text.append(ch, start, length);
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		partidos = new ArrayList<Partido>();
		text = new StringBuilder();
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		super.endElement(uri, localName, name);

		if (partidos != null) {
			if (localName.equals("id")) {
				currentPartido.setId(text.toString());
			} else if (localName.equals("partido")) {
				currentPartido.setPartido(text.toString());
			} else if (localName.equals("votado")) {
				currentPartido.setVotado(text.toString());
			} else if (localName.equals("resultadoPorra")) {
				currentPartido.setResultadoPorra(text.toString());
			} else if (localName.equals("resultadoReal")) {
				currentPartido.setResultadoReal(text.toString());
			} else if (localName.equals("fecha")) {
				currentPartido.setFecha(text.toString());
			} else if (localName.equals("puntos")) {
				currentPartido.setPuntos(text.toString());
			} else if (localName.equals("item")) {
				partidos.add(currentPartido);
			}

			text.setLength(0);
		}
	}

	@Override
	public void startElement(String uri, String localName,
			String name, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, name, attributes);
		if (localName.equals("item")) {
			currentPartido = new Partido();
		}
	}

}
