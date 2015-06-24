package org.openelis.ui.scriptlet;

public interface ScriptletInt<T extends ScriptletObject> {
    
    public T run(T data);
    
    
}
