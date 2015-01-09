package ch.hevs.aislab.magpie.support;

public class Rule {

	private long id;
	
	private String prologRule;
	
	public Rule() {
		
	}
	
	public Rule(String prologRule) {
		this.setPrologRule(prologRule);
	}

	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getPrologRule() {
		return prologRule;
	}

	public void setPrologRule(String prologRule) {
		this.prologRule = prologRule;
	}
}
