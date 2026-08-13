package net.oijon.oling.datatypes.phonology.optimality.constraint;

import java.util.ArrayList;

public abstract class Constraint {

	public abstract int countViolations(String input);
	public abstract ArrayList<String> generatePossibleViolations(String input);
}
