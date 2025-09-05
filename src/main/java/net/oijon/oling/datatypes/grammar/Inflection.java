package net.oijon.oling.datatypes.grammar;

import net.oijon.osca.Rule;

public class Inflection {

	
	// note that this is and logic, not or
	private Gloss[] glosses;
	
	private Rule[] soundChanges;
	
	public Inflection(Gloss[] glosses, Rule[] soundChanges) {
		this.glosses = glosses;
		this.soundChanges = soundChanges;
	}
	
	public Inflection(Gloss g, Rule[] soundChanges) {
		this(new Gloss[] {g}, soundChanges);
	}
	
	public Inflection(Gloss[] glosses) {
		this(glosses, new Rule[0]);
	}
	
	public Inflection(Gloss g) {
		this(g, new Rule[0]);
	}
	
	
}
