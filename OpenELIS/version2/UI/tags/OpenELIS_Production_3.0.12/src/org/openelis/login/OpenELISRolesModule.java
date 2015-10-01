package org.openelis.login;

import org.jboss.security.auth.spi.DatabaseServerLoginModule;

/**
 * JBOSS does not allow the passage of additional parameters in the context from
 * servlet to the bean. To overcome this limitation, we pass username;session-id;locale
 * in the security principal name.
 * This class overrides the get username to allow database login. Please see the
 * jboss login-config.xml in conf directory, application policy for openelis.
 * 
 *   <application-policy name="openelis">
 *        <authentication>
 *            ...
 *            <login-module code="org.openelis.utils.OpenELISRolesModule"
 *               flag="required">
 * 
 */

public class OpenELISRolesModule extends DatabaseServerLoginModule {

    protected String getUsername() {
        String name, parts[];

        name = super.getUsername();
        if (name != null) {
            parts = name.split(";", 3);
            if (parts.length == 3)
                return parts[0];
        }
        return name;
    }
    
}
