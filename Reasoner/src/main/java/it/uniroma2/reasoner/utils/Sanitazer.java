package it.uniroma2.reasoner.utils;

import it.uniroma2.art.owlart.model.ARTURIResource;

public class Sanitazer {

	/**
	 * used to remove "<" and ">"
	 */
	public static String sanitizeURI(ARTURIResource arturiResource){
		return arturiResource.getLocalName().replace("<", "").replace(">", "");
	}
	
	/**
	 * used to replace "<<" and ">>" with "<" and ">"
	 */
	public static String replaceURI(String artResourceName){
		return artResourceName.replaceAll("<<", "<").replace(">>", ">");
	}
}
