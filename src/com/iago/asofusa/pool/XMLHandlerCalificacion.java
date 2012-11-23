package com.iago.asofusa.pool;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.iago.asofusa.utils.User;

public class XMLHandlerCalificacion extends DefaultHandler {
	private List<User> user = null;
	private User currentUser = null;
	private StringBuilder text = null;

	public List<User> getUsers() {
		return user;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		if (currentUser != null)
			text.append(ch, start, length);
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		user = new ArrayList<User>();
		text = new StringBuilder();
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		super.endElement(uri, localName, name);

		if (user != null) {
			if (localName.equals("name")) {
				currentUser.setName(text.toString());
			} else if (localName.equals("puntos")) {
				currentUser.setPuntos(text.toString());
			} else if (localName.equals("item")) {
				user.add(currentUser);
			}

			text.setLength(0);
		}
	}

	@Override
	public void startElement(String uri, String localName,
			String name, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, name, attributes);
		if (localName.equals("item")) {
			currentUser = new User();
		}
	}

}
