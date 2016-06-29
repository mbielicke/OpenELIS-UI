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
package org.openelis.ui.scriptlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;


/**
 * This class is used to manage data between screens, beans and scriptlets. The
 * class is extended by scriptlet packages to manage additional data.
 */
public class ScriptletObject implements Serializable {

    private static final long serialVersionUID = 1L;

    public static enum Status {
        EXECUTING, FINISHED, FAILED
    }

    private int                  runningScriptletId, version;
    private Status               status;
    private HashSet<Change>      changes;
    private ArrayList<Exception> exceptions;

    public ScriptletObject() {
        changes = new HashSet<Change>();
        runningScriptletId = -1;
        version = 0;
    }

    /**
     * Sets the field/meta key that was changed by the specified scriptlet.
     */
    public void setChange(String meta) {
        changes.add(new Change(runningScriptletId, version, meta));
    }

    public void setChanges(String... metas) {
        for (String meta : metas)
            setChange(meta);
    }

    /**
     * Returns a list of fields/meta keys that were changed by any scriptlets
     * except the current one. This is useful when the scriptlet is interested
     * in other scriptlet changes and not its own.
     */
    public ArrayList<String> getChanges() {
        ArrayList<String> other;

        other = new ArrayList<String>();
        for (Change c : changes)
            if (runningScriptletId != c.id)
                other.add(c.meta);

        return other;
    }

    /**
     * Returns a list of all the field/meta keys that were changed by all
     * scriptlets.
     */
    public ArrayList<String> getAllChanges() {
        ArrayList<String> all;

        all = new ArrayList<String>();
        for (Change c : changes)
            all.add(c.meta);

        return all;
    }
    
    /**
     * Adds the specified exception to list of exceptions
     */
    public void addException(Exception e) {
        if (exceptions == null)
            exceptions = new ArrayList<Exception>();

        exceptions.add(e);
    }

    public ArrayList<Exception> getExceptions() {
        return exceptions;
    }

    /**
     * Manages the status of the scriptlet
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    /**
     * Sets the unique id of the script that is currently running. The id 0 is
     * reserved for setting the initial meta.
     */
    protected void setRunningScriptletId(int id) {
        runningScriptletId = id;
    }

    protected int getRunningScriptletId() {
        return runningScriptletId;
    }
    
    /**
     * Sets the version for current field/meta key. The version is used to determine
     * the fields/meta keys that scriptlets need to process next.

     * The initial value of version=0 is used for setChange/SetChanges calls from
     * screen/bean. All calls from scriptlet will have versions starting from 1.
     */
    protected void setChangeVersion(int version) {
        this.version = version;
    }

    /**
     * Clears all the field/meta keys changes for specified version.
     */
    protected void clearChanges(int version) {
        Iterator<Change> c;
        
        for (c = changes.iterator(); c.hasNext();) {
            if (c.next().version == version)
                c.remove();
        }
    }

    protected int getChangeCount() {
        return changes.size();
    }

    /*
     * Simple class to manage our change meta data for each scriptlet
     */
    private class Change {
        public int    id, version;
        public String meta;

        public Change(int id, int version, String meta) {
            this.id = id;
            this.version = version;
            this.meta = meta;
        }

        public boolean equals(Object o) {
            return (o instanceof Change && id == ((Change)o).id && version == ((Change)o).version && meta.equals( ((Change)o).meta));
        }
    }
}