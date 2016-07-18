package org.openelis.ui.scriptlet;

import java.io.Serializable;
import java.util.ArrayList;

public class ScriptletObject implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    public static enum Status {EXECUTING,FINISHED,FAILED}
    
    protected Status status;
    
    protected String changed;
    
    protected ArrayList<String> rerun;
    
    protected ArrayList<Exception> exceptions;
   
    
    public ScriptletObject() {
        rerun = new ArrayList<String>();
    }
    
    public void addRerun(String... metas) {
        for(String meta : metas)
            rerun.add(meta);
    }
    
    public ArrayList<String> getRerun() {
        return rerun;
    }
    
    public void setChanged(String changed) {
        this.changed = changed;
    }
    
    public String getChanged() {
        return changed;
    }
    
    public void addException(Exception e) {
        if(exceptions == null) 
            exceptions = new ArrayList<Exception>();
        
        exceptions.add(e);
    }
    
    public ArrayList<Exception> getExceptions() {
        return exceptions;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public Status getStatus() {
        return status;
    }

}
