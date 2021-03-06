/**
 * Exhibit A - UIRF Open-source Based Public Software License.
 * 
 * The contents of this file are subject to the UIRF Open-source Based Public
 * Software License(the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * openelis.uhl.uiowa.edu
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * The Original Code is OpenELIS code.
 * 
 * The Initial Developer of the Original Code is The University of Iowa.
 * Portions created by The University of Iowa are Copyright 2006-2008. All
 * Rights Reserved.
 * 
 * Contributor(s): ______________________________________.
 * 
 * Alternatively, the contents of this file marked "Separately-Licensed" may be
 * used under the terms of a UIRF Software license ("UIRF Software License"), in
 * which case the provisions of a UIRF Software License are applicable instead
 * of those above.
 */
package org.openelis.ui.screen;

import java.util.ArrayList;
import java.util.HashMap;

import org.openelis.ui.common.DataBaseUtil;
import org.openelis.ui.common.Datetime;
import org.openelis.ui.common.OptionListItem;
import org.openelis.ui.common.Prompt;
import org.openelis.ui.common.ReportStatus;
import org.openelis.ui.common.data.Query;
import org.openelis.ui.common.data.QueryData;
import org.openelis.ui.messages.Messages;
import org.openelis.ui.resources.GeneralCSS;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.screen.Screen;
import org.openelis.ui.widget.Button;
import org.openelis.ui.widget.CheckBox;
import org.openelis.ui.widget.DateHelper;
import org.openelis.ui.widget.DoubleHelper;
import org.openelis.ui.widget.Dropdown;
import org.openelis.ui.widget.IntegerHelper;
import org.openelis.ui.widget.Item;
import org.openelis.ui.widget.Label;
import org.openelis.ui.widget.StringHelper;
import org.openelis.ui.widget.TextBox;
import org.openelis.ui.widget.WidgetHelper;
import org.openelis.ui.widget.WindowInt;
import org.openelis.ui.widget.TextBase.Case;
import org.openelis.ui.widget.calendar.Calendar;
import org.openelis.ui.widget.table.Column;
import org.openelis.ui.widget.table.Table;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class provides the basic framework for calling a report. Each report
 * should extend this class and specify the report servlet service to get report
 * prompts and run the report.
 */
public abstract class ReportScreen<T> extends Screen {

	protected ArrayList<Prompt> reportParameters;

	protected Button            runReportButton, resetButton;

	protected String            name, attachmentName, runReportInterface,
			                    promptsInterface;

	protected static String defaultPrinter, defaultBarcodePrinter;
	
	protected WindowInt window;
	
	protected HashMap<String,Widget> widgets;
	
	protected GeneralCSS css;
	
	protected VerticalPanel main;
	
	
	protected ReportScreen(WindowInt window) throws Exception {
		this.window = window;
		widgets = new HashMap<String,Widget>();
		
		name = null;
		attachmentName = null;
		runReportInterface = "runReport";
		promptsInterface = "getPrompts";
		reportParameters = new ArrayList<Prompt>();
	
		css = UIResources.INSTANCE.general();
		css.ensureInjected();
		
	    main = new VerticalPanel();
	    main.setStyleName(css.WhiteContentPanel());
	    
	    LayoutPanel layout = new LayoutPanel();
	    layout.add(main);

	    initWidget(layout);
		
		initialize();

	}

	protected void initialize() {
		getReportParameters();
		window.setName(name);
	}

	/**
	 * Gets/sets the name (window title) for this report window
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets/sets the attachment filename for files that are returned to the
	 * browser
	 */
	public String getAttachmentName() {
		return attachmentName;
	}

	public void setAttachmentName(String name) {
		this.attachmentName = name;
	}

	/*
	 * Gets/sets the report's getPrompt interface method name
	 */
	public String getPromptsInterface() {
		return promptsInterface;
	}

	public void setPromptsInterface(String promptsInterface) {
		this.promptsInterface = promptsInterface;
	}

	/*
	 * Gets/sets the run report interface method name
	 */
	public String getRunReportInterface() {
		return runReportInterface;
	}

	public void setRunReportInterface(String runReportInterface) {
		this.runReportInterface = runReportInterface;
	}

	/**
	 * Gets the prompts from the report
	 */
	protected void getReportParameters() {
		//window.setBusy(consts.get("gettingReportParam"));

        try {
            reportParameters = getPrompts();
            createReportWindow();
            window.setDone("loadCompleteMessage");
        } catch (Exception e) {
        	e.printStackTrace();
            window.close();
            Window.alert("Failed to get parameters for " + name);
        }      
	}
	
	public abstract ArrayList<Prompt> getPrompts() throws Exception;

	/**
	 * Draws the prompts and fields in the report window
	 */
	protected void createReportWindow() {
		int i;
		FlexTable tp;
		HorizontalPanel hp;
		Prompt p;
		WidgetHelper f;
		Widget w;


		tp = new FlexTable();
		tp.setStyleName(css.Form());
		main.add(tp);

		// for (Prompt p : reportParameters) {
		for (i = 0; i < reportParameters.size(); i++) {
			p = reportParameters.get(i);
			//
			// decode and create component objects
			//
			if (p.isHidden())
				continue;

			switch (p.getType()) {
			case ARRAY:
			case ARRAYMULTI:
				w = createDropdown(p);
				((Dropdown<String>) w).setEnabled(true);
				if(p.getType() == Prompt.Type.ARRAYMULTI)
					((Dropdown<String>)w).setQueryMode(true);
				break;
			case CHECK:
				w = createCheckBox(p);
				((CheckBox) w).setEnabled(true);
				break;
			case STRING:
				f = new StringHelper();
				w = createTextBox(f, p);
				((TextBox) w).setEnabled(true);
			case SHORT:
			case INTEGER:
				f = new IntegerHelper();
				w = createTextBox(f, p);
				((TextBox) w).setEnabled(true);
				break;
			case FLOAT:
			case DOUBLE:
				f = new DoubleHelper();
				w = createTextBox(f, p);
				((TextBox) w).setEnabled(true);
				break;
			case DATETIME:
				w = createCalendar(p);
				((Calendar) w).setEnabled(true);
				break;
			default:
				w = null;
				Window.alert("Error: Type " + p.getType()
						+ " not supported; Please notify IT");
			}

			if (w != null) {
				widgets.put(p.getName(),w);
				addLabelAndWidget(p, tp, w);
			}
		}

		hp = new HorizontalPanel();
		hp.setHeight("10px");
		main.add(hp);

		hp = new HorizontalPanel();
		runReportButton = createButton(Messages.get().btn_runReport());
		runReportButton.setEnabled(true);
		hp.add(runReportButton);
		widgets.put("run",runReportButton);

		runReportButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				runReport();
			}
		});

		resetButton = createButton(Messages.get().btn_reset());
		resetButton.setEnabled(true);
		hp.add(resetButton);
		widgets.put("reset",resetButton);

		resetButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				reset();
			}
		});

		main.add(hp);

		// this is done to adjust the width of the window so that it can display
		// the
		// message associated with the key "correctErrors" without being
		// expanded when
		// the message shows at its bottom
		if (tp.getOffsetWidth() < 335)
			main.setWidth("335px");

		main.setCellHorizontalAlignment(hp, HasAlignment.ALIGN_CENTER);
	}

	/**
	 * Builds a query array from the specified parameters and calls the report's
	 * run method.
	 */
	protected void runReport() {
		Query query;

		if (validate().getStatus() != Validation.Status.VALID) {
			window.setError(Messages.get().msg_correctErrors());
			return;
		}

		query = new Query();
		query.setFields(getQueryFields());
		runReport((T)query);		
	}
	
    public abstract void runReport(T rpc, AsyncCallback<ReportStatus> callback);
    
    /**
     * Provides a more generic interface to run reports so that screens not 
     * implementing ReportScreen can utilize this functionality too
     */
    public void runReport(T rpc) {
        window.setBusy("genReportMessage");

        runReport(rpc, new AsyncCallback<ReportStatus>() {
            public void onSuccess(ReportStatus status) {
                String url;

                if (status.getStatus() == ReportStatus.Status.SAVED) {
                    url = "/timetracker/report?file=" + status.getMessage();
                    if (attachmentName != null)
                        url += "&attachment=" + attachmentName;

                    Window.open(URL.encode(url), name, null);
                    window.setDone("Generated file " + status.getMessage());
                } else {
                    window.setDone(status.getMessage());
                }
            }

            public void onFailure(Throwable caught) {
                window.setError("Failed");
                Window.alert(caught.getMessage());
            }
        });
    }

	/**
	 * Resets all the fields to their original report specified values
	 */
	protected void reset() {
		Dropdown<String> dd;
		TextBox tb;
		Calendar cl;
		ArrayList<Item<String>> data;

		for (String key : widgets.keySet()) {
			if (widgets.get(key) instanceof Dropdown) {
				dd = getWidget(key);
				dd.clearExceptions();
				data = dd.getModel();
				for (Prompt p : reportParameters) {
					if (key.equals(p.getName())) {
						resetDropdown(p, data, dd);
						break;
					}
				}
			} else if (getWidget(key) instanceof Calendar) {
				cl =  getWidget(key);
				cl.setValue(null);
				cl.clearExceptions();
			} else if (getWidget(key) instanceof TextBox) {
				tb = getWidget(key);
				tb.setValue("");
				tb.clearExceptions();
			}
			
		}

		window.clearStatus();
	}

	/**
	 * Resets the dropdown to prompt specified value
	 */
	protected void resetDropdown(Prompt p, ArrayList<Item<String>> l,
	    Dropdown<String> d) {
		String key;
		
		
		if(p.getType() == Prompt.Type.ARRAYMULTI) {
			d.setQuery(new QueryData());
			return;
		}
		
		
		if (p.getDefaultValue() != null)
			d.setValue(p.getDefaultValue());
		else if ("PRINTER".equals(p.getName()) && defaultPrinter != null)
			d.setValue(defaultPrinter);
		else if ("BARCODE".equals(p.getName()) && defaultBarcodePrinter != null)
			d.setValue(defaultBarcodePrinter);
		else {
			d.setValue(null);
			//if (l.size() > 0)
				//d.setValue((String) l.get(0).getKey());
		}
	}

	/**
	 * Returns the value of all the prompts in query format
	 */
	public ArrayList<QueryData> getQueryFields() {
		ArrayList<QueryData> list;
		QueryData field;

		list = new ArrayList<QueryData>();
		for (String key : widgets.keySet()) {
			if (getWidget(key) instanceof Dropdown)
				field = getQuery((Dropdown<String>)getWidget(key), key);
			else if (getWidget(key) instanceof TextBox)
				field = getQuery((TextBox)getWidget(key), key);
			else if (getWidget(key) instanceof Calendar)
				field = getQuery((Calendar)getWidget(key), key);
			else
				continue;
			if (field != null)
				list.add(field);
		}
		return list;
	}

	/*
	 * Returns the field specific query object
	 */
	protected QueryData getQuery(Dropdown<String> dd, String key) {
		ArrayList<Item<String>> sel;
		QueryData qd;
		boolean needComma;
		
		qd = (QueryData)dd.getQuery();
		qd.setKey(key);
		
		/*
		 * remember the last printer & barcode printer they selected
		 */
		if ("PRINTER".equals(key))
			defaultPrinter = qd.getQuery();
		else if ("BARCODE".equals(key))
			defaultBarcodePrinter = qd.getQuery();

		return qd;
	}

	protected QueryData getQuery(TextBox tb, String key) {
		QueryData qd;
		
		if (tb.getValue() == null)
			return null;
		
		qd = new QueryData();
		qd = (QueryData)tb.getQuery();
		qd.setKey(key);
		
		return qd;
	}

	protected QueryData getQuery(Calendar c, String key) {
		QueryData qd;
		
		if (c.getValue() == null)
			return null;
		
		qd = (QueryData)c.getQuery();
		qd.setKey(key);
		
		return qd;
	}

	protected Dropdown<String> createDropdown(Prompt p) {
		Dropdown<String> d;
		Column c;
		Table t;
		ArrayList<Item<String>> l;
		int w;

		w = (p.getWidth() != null && p.getWidth() > 0) ? p.getWidth() : 100;

		//
		// create a new dropdown
		//
		d = new Dropdown<String>();
		d.setRequired(p.isRequired());
		d.setWidth(w + "px");


		t = new Table.Builder(10).column(new Column.Builder(w).build()).build();

		d.setPopupContext(t);
		
		l = new ArrayList<Item<String>>();
		for (OptionListItem o : p.getOptionList())
			l.add(new Item<String>(o.getKey(), o.getLabel()));
		d.setModel(l);

		resetDropdown(p, l, d);

		return d;
	}

	protected CheckBox createCheckBox(Prompt p) {
		CheckBox cb;

		cb = new CheckBox();
		if (p.getWidth() != null && p.getWidth() > 0)
			cb.setWidth(p.getWidth() + "px");

		return cb;
	}

	protected Button createButton(String label) {
		Button b;

		b = new Button(null,label);

		return b;
	}

	protected TextBox createTextBox(WidgetHelper f, Prompt p) {
		TextBox t;

		t = new TextBox();
		t.setRequired(p.isRequired());
		t.setHelper(f);

		if (p.getMask() != null)
			t.setMask(p.getMask());
		if (p.getLength() != null)
			t.setMaxLength(p.getLength());
		if (p.getCase() == Prompt.Case.LOWER)
			t.setCase(Case.LOWER);
		else if (p.getCase() == Prompt.Case.UPPER)
			t.setCase(Case.UPPER);
		if (p.getWidth() != null && p.getWidth() > 0)
			t.setWidth(p.getWidth() + "px");
		else
			t.setWidth("100px");
		t.setValue(p.getDefaultValue());

		return t;
	}

	protected Calendar createCalendar(Prompt p) {
		byte s, e;
		Calendar c;
		DateTimeFormat format;
		DateHelper h;

		s = getDatetimeCode(p.getDatetimeStartCode());
		e = getDatetimeCode(p.getDatetimeEndCode());

		h = new DateHelper();
		h.setBegin(s);
		h.setEnd(e);
		h.setPattern("yyyy-MM-dd");

		c = new Calendar();
		c.setHelper(h);

		c.setRequired(p.isRequired());

		if (p.getWidth() != null && p.getWidth() > 0)
			c.setWidth(p.getWidth() + "px");
		else
			c.setWidth("100px");
        if (p.getDefaultValue() != null) {
            if (e > Datetime.DAY)
                format = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm");
            else
                format = DateTimeFormat.getFormat("yyyy-MM-dd");
            
            try {
                c.setValue(Datetime.getInstance(s, e, format.parse(p.getDefaultValue())));
            } catch (IllegalArgumentException iargE) {
                // we don't set a default if we cannot parse it
            }
        }
		return c;
	}

	/**
	 * Places the specified widget after the label.
	 */
	protected void addLabelAndWidget(Prompt p, FlexTable tp, Widget w) {
		int row;
		Label pr;
		HorizontalPanel hp;

		row = tp.getRowCount();
		//
		// add a label and widget if both are present
		//
		if (!DataBaseUtil.isEmpty(p.getPrompt())) {
			pr = new Label(p.getPrompt());
			pr.setStyleName(css.Prompt());
			tp.setWidget(row, 0, pr);
			hp = new HorizontalPanel();
			hp.add(w);
			tp.setWidget(row, 1, hp);
		} else if (row > 0) {
			//
			// add the widget to the previous row's list of widgets
			//
			hp = (HorizontalPanel) tp.getWidget(row - 1, 1);
			hp.insert(w, hp.getWidgetCount());
		} else {
			//
			// special case; if first row doesn't have a label
			//
			tp.setWidget(row, 1, w);
		}
	}

	/**
	 * Converts the Prompt's date-time code to Datetime class's values
	 */
	protected byte getDatetimeCode(Prompt.Datetime code) {
		switch (code) {
		case YEAR:
			return Datetime.YEAR;
		case MONTH:
			return Datetime.MONTH;
		case DAY:
			return Datetime.DAY;
		case HOUR:
			return Datetime.HOUR;
		case MINUTE:
			return Datetime.MINUTE;
		case SECOND:
			return Datetime.SECOND;
		}

		return 0;
	}
	
    public <T> T getWidget(String key) {
        return (T)widgets.get(key);
    }
}