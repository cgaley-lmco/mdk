package gov.nasa.jpl.mgss.mbee.docgen.docbook;

import java.util.ArrayList;
import java.util.List;

public abstract class DBHasContent extends DocumentElement {
	protected List<DocumentElement> children;
	
	public DBHasContent() {
		children = new ArrayList<DocumentElement>();
	}
	
	public List<DocumentElement> getChildren() {
		return children;
	}
	
	public void addElement(DocumentElement e) {
		children.add(e);
	}

	public void addElements(List<DocumentElement> es) {
		children.addAll(es);
	}
	

}