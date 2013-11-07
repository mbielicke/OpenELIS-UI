package org.openelis.ui.widget;

import org.openelis.ui.widget.Label;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.messages.Messages;
import org.openelis.ui.resources.NoteCSS;
import org.openelis.ui.resources.UIResources;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class NotesPanel extends ResizeComposite {

    private ScrollPanel   scroll;
    private VerticalPanel notes;
    private String        width;
    private DateHelper    headerDate;
    
    protected NoteCSS css;
    
    public NotesPanel() {
    	
    	css = UIResources.INSTANCE.note();
    	css.ensureInjected();
    	
        scroll = new ScrollPanel();
        notes  = new VerticalPanel();
        notes.setWidth("100%");

        headerDate = new DateHelper();
        headerDate.setBegin(Datetime.YEAR);
        headerDate.setEnd(Datetime.SECOND);
        headerDate.setPattern(Messages.get().gen_dateTimePattern());
        
        initWidget(scroll);
        scroll.setWidget(notes);
    }

    public void setWidth(String width) {
        this.width = width;
        scroll.setWidth(width);
    }

    public void setHeight(String height) {
        scroll.setHeight(height);
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
            note.getCellFormatter().setWidth(0,0,"100%");

            userDateText = new Label<String>("by " + userName + " on " + headerDate.format(time));
            note.setWidget(1,0,userDateText);
            note.getCellFormatter().setStyleName(1,0,css.noteAuthorText());
            note.getCellFormatter().setWidth(1,0,"100%");
        }
        if (text != null) {
            bodyText = new HTML("<pre>"+encode(text)+"</pre>");
            note.setWidget(2,0,bodyText);
            note.getCellFormatter().setStyleName(2,0,css.noteBodyText());
            note.getCellFormatter().setWidth(2,0,"100%");
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
