package org.weitblicker.database;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.weitblicker.Options;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

@Entity
@Table( name = "campaigns" )
public class Campaign {

	Campaign() {}
	
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CUST_SEQ")
	@Column(name = "id")
    private long id;
	
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id){
    	this.id = id;
    }
	
	@Column(name = "amount")
    private float amount;
	public void setAmount(float amount){ this.amount = amount; }
	public float getAmount(){ return amount; }
	
    @Transient
    private Locale currentLanguage = Options.DEFAULT_LANGUAGE;
    
    public String getLanguage(){
    	return currentLanguage.getLanguage();
    }    
    
    public void setCurrentLanguage(Locale language){
    	currentLanguage = language;
    }
	
	@OneToOne(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
	@JoinColumn(name = "key_name")
	private LanguageString name;

	public void setName(final String name, final Locale language) {
		name().addText(language, name);
	}
	
	public void setName(String name){
		setName(name, currentLanguage);
	}
	
	public String getName(){
		return getName(currentLanguage);
	}

	public String getName(final Locale language) {
		return name().getText(language);
	}

	private LanguageString name() {
		return name != null ? name : (name = new LanguageString());
	}
    
	@OneToOne(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
	@JoinColumn(name = "key_description")
	private LanguageString desc;
	
	public void setDesc(String desc, final Locale language) {
		desc().addText(language, desc);
	}
	
	public void setDesc(String desc){
		setDesc(desc, currentLanguage);
	}
	
	public String getDesc(){
		return getDesc(currentLanguage);
	}

	public String getDesc(final Locale language) {
		return desc().getText(language);
	}

	private LanguageString desc() {
		return desc != null ? desc : (desc = new LanguageString());
	}

	@OneToOne(fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
	@JoinColumn(name = "key_abstract")
	private LanguageString abst;
	
	public void setAbst(final String abst, final Locale language) {
		abst().addText(language, abst);
	}
	
	public void setAbst(String abst){
		setAbst(abst, currentLanguage);
	}
	
	public String getAbst(){
		return getAbst(currentLanguage);
	}

	public String getAbst(final Locale language) {
		return abst().getText(language);
	}

	private LanguageString abst() {
		return abst != null ? abst : (abst = new LanguageString());
	}
	
	@Column(name = "start")
    private LocalDateTime start;
    public LocalDateTime getStart() { return start; }
    public void setStart(LocalDateTime start){ this.start = start; }
	
	@Column(name = "end")
    private LocalDateTime end;
    public LocalDateTime getEnd() { return end; }
    public void setEnd(LocalDateTime end){ this.end = end; }
    
	@ElementCollection
	@JoinColumn(name = "key_need")
	private List<Need> needs = new LinkedList<Need>();
	public List<Need> getNeeds(){ return needs; } 

}

