package org.openelis.ui.widget;

import org.openelis.ui.widget.Label;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.messages.Messages;
import org.openelis.ui.resources.NoteCSS;
import org.openelis.ui.resources.UIResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class NotesPanel extends ResizeComposite {
    
    @UiTemplate("NotesPanel.ui.xml")
    interface NotesPanelUiBinder extends UiBinder<LayoutPanel,NotesPanel>{};
    public static final NotesPanelUiBinder uiBinder = GWT.create(NotesPanelUiBinder.class);

    @UiField
    protected ScrollPanel   scroll;
    
    @UiField 
    protected VerticalPanel notes;
    
    private DateHelper    headerDate;
    
    protected NoteCSS css;
    
    public NotesPanel() {
    	
    	css = UIResources.INSTANCE.noteCSS();
    	css.ensureInjected();
    	
    	initWidget(uiBinder.createAndBindUi(this));
    	
        notes.setWidth("100%");
        notes.setHeight("100%");

        headerDate = new DateHelper();
        headerDate.setBegin(Datetime.YEAR);
        headerDate.setEnd(Datetime.SECOND);
        headerDate.setPattern(Messages.get().gen_dateTimePattern());
        
    }

    public void addNote(String subject, String userName, String text, Datetime time) {
        Label<String> subjectText, userDateText;
        HTML bodyText;
        FlexTable note;
        
        if (subject == null && text == null)
            return;
        
        note = new FlexTable();
        note.setWidth("100%");
        note.addStyleName(css.noteTableRow());

        if (subject != null) {
            subjectText = new Label<String>(subject);
            note.setWidget(0,0,subjectText);
            note.getCellFormatter().setStyleName(0, 0, css.noteSubjectText());

            userDateText = new Label<String>(userName + " " + headerDate.format(time));
            note.setWidget(0,1,userDateText);
        }
        if (text != null) {
            bodyText = new HTML(encode(text));
            note.setWidget(2,0,bodyText);
            note.getCellFormatter().setStyleName(2,0,css.noteBodyText());
        }
        notes.add(note);
    }

    public void clearNotes() {
        notes.clear();
    }
    
    private String encode(String text) {
        return text.replaceAll("<", "&lt;");
    }
    
    public void setCSS(NoteCSS css) {
    	css.ensureInjected();
    	for(Widget note : notes) {
    		note.setStyleName(css.noteTableRow());
    		for(int i = 0; i < ((VerticalPanel)note).getWidgetCount(); i++) {
    			
    		}
    	}
    }

}
