package org.weitblicker;

import java.io.IOException;
import java.security.Principal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import org.weitblicker.database.User;

import javax.ws.rs.Priorities;


@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

	public static String TOKEN_NAME = "wb-server-session";
	
	private static Map<String, Session> loggedin = new HashMap<>();
	private static Calendar calendar = Calendar.getInstance();

	static class Session{
		
		Session(User user, String token){
			this.user = user;
			this.token = token;
			stamp = calendar.getTime();
		}
		
		boolean isExpired(){
			Calendar expireCal = Calendar.getInstance();
			expireCal.setTime(stamp);
			expireCal.add(Calendar.HOUR_OF_DAY, 1);
		    expire = expireCal.getTime(); 
		    
		    boolean expired = calendar.getTime().after(expire);
		    if(!expired)
		    	stamp = calendar.getTime();
		    return expired;
		}
		
		public String toString(){
			return "Session for user " + user + ". "
					+ "\n The session expires at " + expire.toString() + "."
					+ "\n The session is granted with the token: " + token;
		}
		
		int getExpiringTime(){
			if(!isExpired()){
				return (int) ((expire.getTime() - stamp.getTime()) / 1000.0);
			}
			return 0;
		}
		
		String token;
		User user;
		Date stamp;
		Date expire;
	}
	
    
    public static void login(String token, User user){    	
    	loggedin.put(token, new Session(user, token));
    }
    
    public static void logout(String token){
    	loggedin.remove(token);
    }
    
    public static User getUser(String token){
    	return loggedin.get(token).user;
    }
    public static Session getSession(String token){
    	return loggedin.get(token);
    }
	
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {	
    	
    	System.out.println("cookies...");
    	Cookie sessionCookie = requestContext.getCookies().get(TOKEN_NAME);
    	requestContext.getCookies();
    	for(Map.Entry<String, Cookie> entry: requestContext.getCookies().entrySet()){
    		System.out.println("key:" + entry.getKey() + " value:" + entry.getValue().getValue());
    	}
    	
    	
    	
    	
    	System.out.println("Authentication with token");

        // Get the HTTP Authorization header from the request
        String authorizationHeader = 
            requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if(sessionCookie == null){
        	throw new NotAuthorizedException("Authorization cookie must be provided");
        }
        
        // Extract the token from the HTTP Authorization header
        String token = sessionCookie.getValue();

        try {
            // Validate the token
            Session session = validateToken(token);
            
            System.out.println("Found session: \n" + session);
            
    		requestContext.setSecurityContext(new SecurityContext() {
    			
    		    @Override
    		    public Principal getUserPrincipal() {
    		        return session.user;
    		    }
    		
    		    @Override
    		    public boolean isUserInRole(String role) {
    		        return true; // TODO
    		    }
    		
    		    @Override
    		    public boolean isSecure() {
    		        return false; // TODO
    		    }
    		
    		    @Override
    		    public String getAuthenticationScheme() {
    		        return null; // TODO
    		    }
    		});

        } catch (NotAuthorizedException e) {
        	System.out.println(e.getMessage());
            requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED).build());
        } catch (Exception e) {
        	e.printStackTrace();
            requestContext.abortWith(
                Response.status(Response.Status.INTERNAL_SERVER_ERROR).build());
        }
        
    }
    

    private Session validateToken(String token) throws NotAuthorizedException {
        // Check if it was issued by the server and if it's not expired or unknown
        // Throw an Exception if the token is invalid
    	if(!loggedin.containsKey(token)){
            throw new NotAuthorizedException("Token is unknown!");
    	}
    	if(loggedin.get(token).isExpired()){
            throw new NotAuthorizedException("Token is is expired!");
    	}
    	return loggedin.get(token);
    }
}