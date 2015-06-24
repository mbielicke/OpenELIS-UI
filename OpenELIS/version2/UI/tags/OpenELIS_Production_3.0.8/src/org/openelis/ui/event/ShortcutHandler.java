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
package org.openelis.ui.event;

import com.google.gwt.user.client.ui.Focusable;

/**
 * This class will fire events to registered objects for StateChange actions
 */
public abstract class ShortcutHandler implements Focusable {
	
    public abstract void onShortcut();

    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.Focusable#getTabIndex()
     */
    @Override
    public int getTabIndex() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.Focusable#setAccessKey(char)
     */
    @Override
    public void setAccessKey(char key) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.Focusable#setFocus(boolean)
     */
    @Override
    public void setFocus(boolean focused) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.Focusable#setTabIndex(int)
     */
    @Override
    public void setTabIndex(int index) {
        // TODO Auto-generated method stub
        
    }
}
