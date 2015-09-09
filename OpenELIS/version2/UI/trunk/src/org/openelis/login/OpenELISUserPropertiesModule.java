package org.openelis.login;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;

import org.jboss.security.auth.spi.UsersRolesLoginModule;

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

public class OpenELISUserPropertiesModule extends UsersRolesLoginModule {

    protected static Logger       log          = Logger.getLogger("login");

    protected static LoginAttempt loginAttempt = new OpenELISUserPropertiesModule.LoginAttempt();

    /* time in milli to lockout user */
    protected static int          loginLockoutTime = 1000 * 60 * 10,
                                                   /*
                                                    * # of tries before ip
                                                    * lockout
                                                    */
                                                   loginIPRetryCount = 7,
                                                   /*
                                                    * # of tries before username
                                                    * lockout
                                                    */
                                                   loginNameRetryCount = 4;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map sharedState, Map options) {
        //addValidOptions(new String[] {"loginLockoutTime", "loginIPRetryCount", "loginNameRetryCount"});
        super.initialize(subject, callbackHandler, sharedState, options);

        if (options.containsKey("loginLockoutTime"))
            loginLockoutTime = Integer.parseInt((String)options.get("loginLockoutTime"));

        if (options.containsKey("loginIPRetryCount"))
            loginIPRetryCount = Integer.parseInt((String)options.get("loginIPRetryCount"));

        if (options.containsKey("loginNameRetryCount"))
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
            throw e;
        }

        if (!loginAttempt.isValid(getUsername(), getIP())) {
            log.severe("failing because of too many attempts");
            throw new LoginException("User " + getUsername() + " is locked out");
        }

        loginAttempt.success(getUsername(), getIP());

        return true;
    }

    /*
     * Simple class to limit failed login attempts by user or ip address.
     * Setting loginNameRetryCount or loginIPRetryCount to -1 disables recording
     * of failed attempts and allows unlimited tries.
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
            if (la != null && la.lastTime >= cutoff &&
                la.tries >= loginIPRetryCount)
                return false;

            la = failed.get(name);
            if (la != null && la.lastTime >= cutoff &&
                la.tries >= loginNameRetryCount)
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

            log.info("Login attempt for '" + name + "' - " + ipAddress +
                     " succeeded");
        }

        /**
         * Adds a new failed attempt for user and ip address.
         */
        public void fail(String name, String ipAddress) {
            long now;
            LoginAttempt li, ln;

            if (loginNameRetryCount == -1 || loginIPRetryCount == -1) {
                log.severe("Login attempt for '" + name + "' - " + ipAddress +
                           " failed ");
                return;
            }

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

            log.severe("Login attempt for '" + name + "' [" + ln.tries +
                       " of " + loginNameRetryCount + "]" + " - " + ipAddress +
                       " [" + li.tries + " of " + loginIPRetryCount +
                       "] failed ");
        }
    }
}
