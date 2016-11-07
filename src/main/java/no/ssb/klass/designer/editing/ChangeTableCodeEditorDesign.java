package no.ssb.klass.designer.editing;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import no.ssb.klass.designer.editing.codetables.ChangesCodeTable;
import no.ssb.klass.designer.editing.codetables.ReadOnlyCodeTableFilterWrapper;

/** 
 * !! DO NOT EDIT THIS FILE !!
 * 
 * This class is generated by Vaadin Designer and will be overwritten.
 * 
 * Please make a subclass with logic and additional interfaces as needed,
 * e.g class LoginView extends LoginDesign implements View { }
 */
@DesignRoot
@AutoGenerated
@SuppressWarnings("serial")
public class ChangeTableCodeEditorDesign extends VerticalLayout {
    protected Button importButton;
    protected Button exportButton;
    protected Label sourceVersionName;
    protected Button descriptionButton;
    protected Label targetVersionName;
    protected ReadOnlyCodeTableFilterWrapper sourceVersion;
    protected ChangesCodeTable correspondenceMap;
    protected Button addNewCorrespondence;
    protected ReadOnlyCodeTableFilterWrapper targetVersion;

    public ChangeTableCodeEditorDesign() {
        Design.read(this);
    }
}