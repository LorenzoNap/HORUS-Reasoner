package it.uniroma2.reasoner.domain;

/**
 * Triple domain object. Each Triple has subject,predicate and object as an RDFTriple
 * 
 * @author Giovanni Lorenzo Napoleoni
 *
 */
public class Triple {

	private String subject;
	private String predicate;
	private String object;
	
	public Triple(String subject, String predicate, String object) {
		super();
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getPredicate() {
		return predicate;
	}
	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}
	public String getObject() {
		return object;
	}
	public void setObject(String object) {
		this.object = object;
	}
	
	
}
