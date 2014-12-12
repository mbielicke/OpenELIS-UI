package org.openelis.login;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;

import org.jboss.security.auth.spi.LdapExtLoginModule;

/**
 * JBOSS does not allow the passage of additional parameters in the context from
 * servlet to the bean. To overcome this limitation, we pass
 * username;session-id;locale in the security principal name. This class
 * overrides the get username to allow ldap login. Please see the jboss
 * login-config.xml in conf directory, application policy for openelis.
 * 
 * <application-policy name="openelis"> <authentication> ... <login-module
 * code="org.openelis.utils.OpenELISLDAPModule" flag="required">
 * 
 */

public class OpenELISLDAPModule extends LdapExtLoginModule {

    protected static Logger       log          = Logger.getLogger("login");
    
    protected static LoginAttempt loginAttempt = new OpenELISLDAPModule.LoginAttempt();
    
   // protected static ActiveDirectoryLog adLogger = new ActiveDirectoryLog();
    
                                  /* time in milli to lockout user */
    protected static int          loginLockoutTime = 1000 * 60 * 10,
                                  /* # of tries before ip lockout */
                                  loginIPRetryCount = 7, 
                                  /* # of tries before username lockout */
                                  loginNameRetryCount = 4; 
    
    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
        super.initialize(subject, callbackHandler, sharedState, options);
        
        if(options.containsKey("loginLockoutTime"))
            loginLockoutTime = Integer.parseInt((String)options.get("loginLockoutTime"));
        
        if(options.containsKey("loginIPRetryCount"))
            loginIPRetryCount = Integer.parseInt((String)options.get("loginIPRetryCount"));
        
        if(options.containsKey("loginNameRetryCount"))
            loginNameRetryCount = Integer.parseInt((String)options.get("loginNameRetryCount"));
    }

    protected String getUsername() {
        String name, parts[];

        name = super.getUsername();
        if (name != null) {
            parts = name.split(";", 4);
            if (parts.length == 4)
                return parts[0];
        }
        return name;
    }
    

    protected String getIP() {
        String name, parts[];

        name = super.getUsername();
        if (name != null) {
            parts = name.split(";", 4);
            if (parts.length == 4)
                return parts[3];
        }
        return "undefined";

    }
    
    

    @Override
    public boolean login() throws LoginException {

        try {
            super.login();
        } catch (LoginException e) {
            loginAttempt.fail(getUsername(), getIP());
            e.printStackTrace();
            throw e;
        }
        
        //Temp logger for copying LDAP to Active Directory
        //adLogger.log(getUsername(),getUsernameAndPassword()[1]);

        if ( !loginAttempt.isValid(getUsername(), getIP())) {
            log.severe("failing becuase of too many attempts");
            throw new LoginException("User " + getUsername() + " is locked out");
        }

        loginAttempt.success(getUsername(), getIP());

        return true;

    }

    /*
     * Simple class to manage login attempts
     */
    public static class LoginAttempt {
        int                                            tries;
        long                                           lastTime;

        protected static HashMap<String, LoginAttempt> failed = new HashMap<String, LoginAttempt>();

        /**
         * Checks to see if the user from ip address has exceeded the number of
         * attempts trying to login into the system.
         */
        public boolean isValid(String name, String ipAddress) {
            long cutoff;
            LoginAttempt la;

            cutoff = System.currentTimeMillis() - loginLockoutTime;

            la = failed.get(ipAddress);
            if (la != null && la.lastTime >= cutoff && la.tries >= loginIPRetryCount)
                return false;
            
            la = failed.get(name);
            if (la != null && la.lastTime >= cutoff && la.tries >= loginNameRetryCount)
                return false;

            return true;
        }

        /**
         * Clears the failed list for the user and ip address. TODO: need a
         * sliding window remove for clearing the ip address for better
         * security.
         */
        public void success(String name, String ipAddress) {
            failed.remove(ipAddress);
            failed.remove(name);

            log.info("Login attempt for '" + name + "' - " + ipAddress + " succeeded");
        }

        /**
         * Adds/increments the number of failed attempts from user and ip
         * address.
         */
        public void fail(String name, String ipAddress) {
            long now;
            LoginAttempt li, ln;

            now = System.currentTimeMillis();

            li = failed.get(ipAddress);
            if (li == null) {
                li = new LoginAttempt();
                failed.put(ipAddress, li);
            }
            li.lastTime = now;
            li.tries = Math.min(li.tries + 1, 9999);

            ln = failed.get(name);
            if (ln == null) {
                ln = new LoginAttempt();
                failed.put(name, ln);
            }
            ln.lastTime = now;
            ln.tries = Math.min(ln.tries + 1, 9999);

            log.severe("Login attempt for '" + name + "' [" + ln.tries + " of " +
                       loginNameRetryCount + "]" + " - " + ipAddress + " [" + li.tries + " of " +
                       loginIPRetryCount + "] failed ");
        }
    }

}
