/** Exhibit A - UIRF Open-source Based Public Software License.
* 
* The contents of this file are subject to the UIRF Open-source Based
* Public Software License(the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
* openelis.uhl.uiowa.edu
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations
* under the License.
* 
* The Original Code is OpenELIS code.
* 
* The Initial Developer of the Original Code is The University of Iowa.
* Portions created by The University of Iowa are Copyright 2006-2008. All
* Rights Reserved.
* 
* Contributor(s): ______________________________________.
* 
* Alternatively, the contents of this file marked
* "Separately-Licensed" may be used under the terms of a UIRF Software
* license ("UIRF Software License"), in which case the provisions of a
* UIRF Software License are applicable instead of those above. 
*/
package org.openelis.ui.widget;

import org.openelis.ui.event.BeforeCloseEvent;
import org.openelis.ui.resources.UIResources;
import org.openelis.ui.resources.WindowCSS;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.AbsolutePositionDropController;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class ModalWindow extends org.openelis.ui.widget.Window {
    
    private AbsolutePanel modalPanel;
    private AbsolutePanel modalGlass;
    private PickupDragController dragController;
    private AbsolutePositionDropController dropController;
    public static final int position=100;
    
    protected WindowCSS css;
    
    public ModalWindow() {
        this(true);
    }
    
    public ModalWindow(boolean resize) {
        super(resize);
        css = UIResources.INSTANCE.window();
        css.ensureInjected();
        
        modalGlass = new AbsolutePanel();
        modalGlass.setSize("100%", "100%");
        modalGlass.setStyleName(css.GlassPanel());
        RootLayoutPanel.get().add(modalGlass);
        RootLayoutPanel.get().setWidgetTopBottom(modalGlass, 0, Unit.PX, 0, Unit.PX);
        
        modalPanel = new AbsolutePanel();
        modalPanel.setSize("100%","100%");
        modalPanel.setStyleName(css.ModalPanel());
        RootLayoutPanel.get().add(modalPanel); 
        RootLayoutPanel.get().setWidgetTopBottom(modalPanel,0,Unit.PX,0,Unit.PX);
        
        modalPanel.add(this,position,position);
        
        dragController = new PickupDragController(modalPanel,true);
        dropController = new AbsolutePositionDropController(modalPanel);
        dragController.registerDropController(dropController);
        dragController.makeDraggable(this,view.getCap());
        dragController.setBehaviorDragProxy(true);
    }
    
    public void setContent(Widget content, int x, int y) {
        modalPanel.setWidgetPosition(this, x, y);        
        setContent(content);
    }
    
    @Override
    public void close() {
        if (getHandlerCount(BeforeCloseEvent.getType()) > 0) {
            BeforeCloseEvent<WindowInt> event = BeforeCloseEvent.fire(this, this);
            if (event != null && event.isCancelled())
                return;
        }
        
        if(modalGlass != null) {
            removeFromParent();
            RootLayoutPanel.get().remove(modalGlass);
            RootLayoutPanel.get().remove(modalPanel);
        }
      
        destroy();

        CloseEvent.fire(this, this);
    }

    /**
     * Trying this method to size table correctly for when Modal Window is set.
     */
    @Override
    protected void onAttach() {
        super.onAttach();
        onResize();
    }
}
