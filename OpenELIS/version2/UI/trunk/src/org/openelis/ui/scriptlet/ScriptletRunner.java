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

import java.util.ArrayList;

/**
 * This class will run a set of Scriptlets on a Scriptlet object. If a scriptlet
 * changes field/meta in the object while running, this class will re-run all
 * the scriptlets to ensure those changes are propagated to every scriptlet.
 */
public class ScriptletRunner<T extends ScriptletObject> {

    private ArrayList<ScriptletInt<T>> scriptlets;

    public ScriptletRunner() {
        scriptlets = new ArrayList<ScriptletInt<T>>();
    }

    /**
     * Adds scriptlet(s) to list of scriptlets that will be run.
     */
    public void add(ScriptletInt<T>... scripts) {
        for (ScriptletInt<T> script : scripts)
            scriptlets.add(script);
    }

    /**
     * Removes the specified scriptlet from the list of running scriptlets.
     */
    public void remove(ScriptletInt<T> script) {
        scriptlets.remove(script);
    }

    public boolean containsScriptlet(ScriptletInt<T> scriptlet) {
        return scriptlets.contains(scriptlet);
    }

    /**
     * Runs the scriptlets.
     */
    public T run(T so) {
        int i, v;

        so.setStatus(ScriptletObject.Status.EXECUTING);

        /*
         * only allow max loop iterations so we will not go in infinite loop
         */
        for (v = 1; v <= scriptlets.size(); v++ ) {
            so.setChangeVersion(v);
            for (i = 0; i < scriptlets.size(); i++ ) {
                /*
                 * set id and run; id just needs to be unique for this runner
                 * list and is not globally unique
                 */
                so.setRunningScriptletId(i);

                scriptlets.get(i).run(so);
                if (so.getStatus() == ScriptletObject.Status.FAILED)
                    return so;
            }
            /*
             * delete all the field/meta data for the previous version. We need
             * to re-run the scripts if there were changes added in this
             * iteration
             */
            so.clearChanges(v - 1);
            if (so.getChangeCount() == 0)
                break;
        }

        so.setStatus(ScriptletObject.Status.FINISHED);

        return so;
    }
}
