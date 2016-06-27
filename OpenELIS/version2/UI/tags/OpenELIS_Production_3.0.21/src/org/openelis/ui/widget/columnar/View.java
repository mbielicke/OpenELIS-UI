package org.openelis.ui.widget.columnar;

import org.openelis.ui.resources.ColumnarCSS;
import org.openelis.ui.resources.TableCSS;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.widget.CSSUtils;
import org.openelis.ui.widget.table.CellRenderer;
import org.openelis.ui.widget.table.FlexTable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.NativeVerticalScrollbar;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.UIObject;

public class View extends ResizeComposite {
    
    @UiTemplate("View.ui.xml")
    interface ViewUiBinder extends UiBinder<SplitLayoutPanel,View>{};
    public static final ViewUiBinder uiBinder = GWT.create(ViewUiBinder.class);
    
    @UiField
    protected ScrollPanel scrollView;
    
    @UiField
    protected FlexTable   flexTable;
    
    @UiField(provided = true)
    protected Header      header;
    
    @UiField
    protected LayoutPanel           inner;
    
    @UiField(provided = true)
    protected SplitLayoutPanel      splitPanel;
    
    @UiField(provided=true)
    protected Legend      legend;
    
    protected boolean     attached, sized;
    
    protected int cellHeight;
    
    protected Columnar columnar;
    
    protected ColumnarCSS               css;

    protected View             source   = this;
    
    public View(Columnar clmnr) {
        header = new Header(clmnr);
        legend = new Legend(clmnr);
        splitPanel = new SplitLayoutPanel(3) {
            @Override
            public void onResize() {
                super.onResize();
                
                Element svEl = inner.getWidgetContainerElement(scrollView);
                
                if (CSSUtils.getWidth(svEl) > 0) {

                    if (scrollView.getMaximumVerticalScrollPosition() == 0)
                        columnar.setWidth(CSSUtils.getWidth(svEl) - 1);
                    else
                        columnar.setWidth(CSSUtils.getWidth(svEl) -
                                       NativeVerticalScrollbar.getNativeScrollbarWidth() - 1);

                    scrollView.setWidth(CSSUtils.getWidth(svEl) -
                                        CSSUtils.getAddedBorderWidth(columnar.getElement()) + "px");

                    if (CSSUtils.getHeight(inner) > 0) {
                        int height = CSSUtils.getHeight(inner) -  CSSUtils.getHeight(header) -
                                        CSSUtils.getAddedBorderHeight(columnar.getElement());
                        /*
                         * This check is here only for Unit Testing.  If not done Unit test on the
                         * table will fail here with assertion check from the widget.
                         */
                        if(height > 0)
                            scrollView.setHeight(height + "px");
                    }

                }
            }
        };
        
        this.columnar = clmnr;
        
        initWidget(uiBinder.createAndBindUi(this));
        
        setCSS(UIResources.INSTANCE.columnar());
        
        
        
        scrollView.addScrollHandler(new ScrollHandler() {

            @Override
            public void onScroll(ScrollEvent event) {
                DOM.setStyleAttribute(header.getElement(),
                                      "left",
                                      (0 - scrollView.getHorizontalScrollPosition()) + "px");
            }
        });
        
       
    }
    
    @UiHandler("flexTable")
    protected void cellClick(ClickEvent event) {
        int r,c;
     
        // if x < 0 the user moused out of table before letting up button
        // ignore event in this case
        if (event.getClientX() < 0)
            return;
        
        r = flexTable.getRowForEvent(event.getNativeEvent());
        c = flexTable.getColForEvent(event.getNativeEvent());

        columnar.selectItemAt(c,event.getNativeEvent());
        //if (columnar.fireBeforeSelCellClickedEvent(r, c, event.isControlKeyDown(), event.isShiftKeyDown()))
          //  table.startEditing(r, c, event.getNativeEvent());
    }
    
    protected void layout() {
        Node colgroup;

        /*
         * If View is not attached to DOM yet get out. onAttach will call
         * layout() the first time this widget attached.
         */

        if ( !attached)
            return;
        
        colgroup = flexTable.getElement().getElementsByTagName("colgroup").getItem(0);
        
        if(colgroup != null) {
            while(colgroup.getChildCount() > columnar.getDataItemCount())
                colgroup.removeChild(colgroup.getChild(0));
        }

        for (int c = 0; c < columnar.getDataItemCount(); c++ ) {
            flexTable.getColumnFormatter().setWidth(c, columnar.getDataItemAt(c).getWidth() + "px");
            if (columnar.getDataItemAt(c).getStyle() != null)
                flexTable.getColumnFormatter().setStyleName(c, columnar.getDataItemAt(c).getStyle());
        }
        
       
        
        flexTable.setWidth(columnar.getTotalColumnWidth() + "px");
        flexTable.setHeight("1px");
        DOM.setStyleAttribute(flexTable.getElement(), "backgroundColor", "transparent");
        
        legend.layout();

        // ********** Create and attach Header **************
        if (columnar.hasHeader()) {
            UIObject.setVisible(inner.getWidgetContainerElement(header), true);
            header.setVisible(true);
            header.layout();
            inner.setWidgetTopBottom(scrollView, CSSUtils.getHeight(header), Unit.PX, 0, Unit.PX);
        } else {
            UIObject.setVisible(inner.getWidgetContainerElement(header), false);
            header.setVisible(false);
            inner.setWidgetTopBottom(scrollView, 0, Unit.PX, 0, Unit.PX);
        }
    }
    
    /**
     * This method is called when a column width is changed. It will resize the
     * columns to there currently set width.
     */
    protected void resize() {

        for (int c = 0; c < columnar.getDataItemCount(); c++ )
            flexTable.getColumnFormatter().setWidth(c, columnar.getDataItemAt(c).getWidth() + "px");
        flexTable.setWidth(columnar.getTotalColumnWidth() + "px");
    }
    
    /**
     * Will create the the necessary visible rows for the flexTable table
     * depending on what is needed at the time. If model.size() < visibleRows
     * then the number of rows created will equal model.size() else the number
     * visibleRows will be created for the flexTable table.
     */
    private void createDataItem(int rc) {
        for(int i = 0; i < columnar.getLineCount(); i++) {
            flexTable.insertCell(i,rc);
            flexTable.getCellFormatter().setHeight(i, rc, columnar.getLineHeight()+"px");
        }
            
    }
    
    /**
     * This method will redraw the table from the startRow to the endRow that
     * are passed in as params. Rows are passed as -1,-1 the entire view will be
     * drawn.
     * 
     * @param smr
     * @param emr
     */
    protected void renderView(int smr, int emr) {
        int r, startMax;


        if (smr < 0)
            smr = 0;

        if (emr < 0)
            emr = columnar.getDataItemCount() - 1;

        /*
         * Create/Load Rows in the flexTable table
         */
        for (r = smr; r <= emr; r++ ) {
            /*
             * Create table row if needed
             */
            if (r >= flexTable.getCellCount(0)) {
                createDataItem(flexTable.getCellCount(0));
                /*
                 * ColumnFormatter is not available until first row is inserted
                 * so call resize after that
                 */
                if (r == 0)
                    resize();
            }

            for (int c = 0; c < columnar.getLineCount(); c++ )
                renderCell(r, c);

        }

        while (flexTable.getCellCount(0) > columnar.getDataItemCount()) {
            for(int i = 0; i < columnar.getLineCount(); i++)
                flexTable.removeCell(i, columnar.getDataItemCount());
        }

        /*
        if ( !table.fixScrollBar && !sized) {
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    if (flexTable.getOffsetHeight() < scrollView.getOffsetHeight())
                        table.getParent().setHeight(CSSUtils.getHeight(flexTable) + "px");
                    sized = true;
                }

            });
        }
        */

    }
    
    protected void renderCell(int d, int l) {
        CellRenderer renderer;

        renderer = columnar.getLineAt(l).getCellRenderer();

        //if (table.getQueryMode())
        //    renderer.renderQuery(flexTable, r, c, (QueryData)table.getValueAt(r, c));
        //else
        renderer.render(flexTable, l, d, columnar.getValueAt(d, l));
        
        /*
        if (table.hasExceptions(r, c)) {
            flexTable.getCellFormatter().addStyleName(r, c, Balloon.isWarning(table.getEndUserExceptions(r, c), table.getValidateExceptions(r, c)) ? css.InputWarning() : css.InputError());
            flexTable.addCellMouseOverHandler(new CellMouseOverEvent.Handler(r, c) {
                @Override
                public void onCellMouseOver(CellMouseOverEvent event) {
                    table.drawExceptions(event.getRow(), event.getCol(), event.getX(), event.getY());
                }

            });
        } else {
        
            flexTable.getCellFormatter().removeStyleName(r, c, css.InputError());
            flexTable.getCellFormatter().removeStyleName(r, c, css.InputWarning());
            flexTable.removeHandler(r, c);
        }
        */

        //flexTable.getCellFormatter().setVisible(l, d, table.getColumnAt(c).isDisplayed());
    }
    
    protected void applySelectionStyle(int index) {
        for(int i = 0; i < columnar.getLineCount(); i++)
            flexTable.getCellFormatter().addStyleName(i,index, css.Selection());
        flexTable.getColumnFormatter().addStyleName(index, css.Selection());
    }
    
    protected void applyUnselectionStyle(int index) {
        for(int i = 0; i < columnar.getLineCount(); i++)
            flexTable.getCellFormatter().removeStyleName(i,index, css.Selection());
    }
    
    public void addLine(int index) {
        flexTable.insertRow(index);
        layout();
        renderView(-1,-1);
    }
    
    public void removeLine(int index) {
        flexTable.removeRow(index);
        layout();
    }
    
    public void addDataItem(int index) {
        for(int i = 0; i < columnar.getLineCount(); i++) {
            flexTable.insertCell(i, index);
        }
        layout();
        renderView(index,index);
    }
    
    public void removeDataItem(int index) {
        for(int i = 0; i < columnar.getLineCount(); i++) {
            flexTable.removeCell(i,index);
        }
        layout();
    }
    
    public void removeAllDataItems() {
        flexTable.removeAllRows();
    }
    
    /**
     * Method is overridden from Composite so that layout() can be deferred
     * until it is attached to the DOM
     */
    @Override
    protected void onAttach() {

        super.onAttach();

        if(attached)
            return;
            
        attached = true;
        layout();
        flexTable.setVisible(false);
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

            @Override
            public void execute() {
                flexTable.setVisible(true);
                Element svEl = inner.getWidgetContainerElement(scrollView);
                
                if (scrollView.getMaximumVerticalScrollPosition() == 0)
                    columnar.setWidth(CSSUtils.getWidth(svEl) - 1);
                else
                    columnar.setWidth(CSSUtils.getWidth(svEl) -
                                   NativeVerticalScrollbar.getNativeScrollbarWidth() - 1);
                
                if (CSSUtils.getWidth( (svEl)) > 0) {
                    columnar.setWidth(CSSUtils.getWidth(svEl) - 1);
                    scrollView.setWidth(CSSUtils.getWidth(svEl) -
                                        CSSUtils.getAddedBorderWidth(columnar.getElement()) + "px");
                }

                if (CSSUtils.getHeight(svEl) > 0) {
                    scrollView.setHeight(CSSUtils.getHeight(svEl) - CSSUtils.getHeight(header) -
                                         CSSUtils.getAddedBorderHeight(columnar.getElement()) + "px");

                }
            }
        });

    }
    
    /**
     * Returns the Header for this view
     * 
     * @return
     */
    protected Header getHeader() {
        return header;
    }
    
    public void setCSS(ColumnarCSS css) {
        css.ensureInjected();

        for (int i = 0; i < flexTable.getRowCount(); i++ ) {
            /*
            if (flexTable.getRowFormatter().getStyleName(i).contains(this.css.Selection())) {
                flexTable.getRowFormatter().removeStyleName(i, this.css.Selection());
                flexTable.getRowFormatter().addStyleName(i, css.Selection());
            }
            */
            for (int j = 0; j < flexTable.getCellCount(i); j++ ) {
                if (flexTable.getCellFormatter().getStyleName(i, j).contains(this.css.InputError())) {
                    flexTable.getCellFormatter().removeStyleName(i, j, this.css.InputError());
                    flexTable.getCellFormatter().addStyleName(i, j, css.InputError());
                }
                if (flexTable.getCellFormatter()
                             .getStyleName(i, j)
                             .contains(this.css.InputWarning())) {
                    flexTable.getCellFormatter().removeStyleName(i, j, this.css.InputWarning());
                    flexTable.getCellFormatter().addStyleName(i, j, css.InputWarning());
                }
            }
        }

        this.css = css;

        flexTable.setStyleName(css.Table());

    }
    

    FlexTable table() {
        return flexTable;
    }


    ScrollPanel scrollView() {
        return scrollView;
    }

    ColumnarCSS css() {
        return css;
    }

    @Override
    public void onResize() {
        super.onResize();

        Element svEl = inner.getWidgetContainerElement(scrollView);
      

        if (CSSUtils.getWidth(svEl) > 0) {

            if (scrollView.getMaximumVerticalScrollPosition() == 0)
                columnar.setWidth(CSSUtils.getWidth(svEl) - 1);
            else
                columnar.setWidth(CSSUtils.getWidth(svEl) -
                               NativeVerticalScrollbar.getNativeScrollbarWidth() - 1);

            scrollView.setWidth(CSSUtils.getWidth(svEl) -
                                CSSUtils.getAddedBorderWidth(columnar.getElement()) + "px");

            if (CSSUtils.getHeight(inner) > 0) {
                int height = CSSUtils.getHeight(inner) -  CSSUtils.getHeight(header) -
                                CSSUtils.getAddedBorderHeight(columnar.getElement());
                /*
                 * This check is here only for Unit Testing.  If not done Unit test on the
                 * table will fail here with assertion check from the widget.
                 */
                if(height > 0)
                    scrollView.setHeight(height + "px");
            }

        }
    }
    
    
    

}
