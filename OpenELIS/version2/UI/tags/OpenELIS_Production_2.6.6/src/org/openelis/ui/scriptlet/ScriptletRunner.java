package org.openelis.ui.scriptlet;

import java.util.ArrayList;

/**
 * This class will run a set of Scriptlets on a Scriptlet object 
 * and rerun them if one Scriptlets marks another pass is needed.
 */
public class ScriptletRunner<T extends ScriptletObject> {

    private ArrayList<ScriptletInt<T>> scriptlets;
    
    public ScriptletRunner() {
        scriptlets = new ArrayList<ScriptletInt<T>>();
    }
    
    public void add(ScriptletInt<T>... scripts) {
        for(ScriptletInt<T> script : scripts) 
            scriptlets.add(script);
    }

    public void remove(ScriptletInt<T> script) {
        scriptlets.remove(script);
    }
    
    public T run(T so) {
        so.setStatus(ScriptletObject.Status.EXECUTING);
        
        for(ScriptletInt<T> script : scriptlets) {
            so = script.run(so);
            if(so.getStatus() == ScriptletObject.Status.FAILED)
                return so;
        }
        
        if(so.getRerun().size() > 0) {
            so.setChanged(so.getRerun().remove(0));
            run(so);
        }
        
        so.setStatus(ScriptletObject.Status.FINISHED);
 
        return so;
    }
    
    public int numberOfScriptlets() {
        return scriptlets.size();
    }
    
    public boolean containsScriptlet(ScriptletInt<T> scriptlet) {
        return scriptlets.contains(scriptlet);
    }
}
