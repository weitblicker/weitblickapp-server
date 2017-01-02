package org.weitblicker;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.grizzly.http.util.Header;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;


import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.weitblicker.database.PersistenceHelper;
import org.weitblicker.database.User;

import javax.ws.rs.core.UriBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Scanner;

/**
 * Main Class for Weitblick App Server
 * @author Sebastian Pütz <spuetz@uos.de>, benedikt, nizzke, Janis
 * @since 14.10.2016
 */

public class Main
{

    public static HttpServer startServer()
    {
        // create a resource config that scans for JAX-RS resources and providers
        // in org.weitblicker, org.weitblicker.rest, org.weitblicker.rest packages
        final ResourceConfig rc = new ResourceConfig().packages("org.weitblicker", "org.weitblicker.rest", "org.weitblicker.backend");
        //final ResourceConfig resourceConfig = new ResourceConfig(BackendEndpoint.class);
        rc.register(MultiPartFeature.class);
        
        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(Options.BASE_URI), rc);
    }
    
    public static URI getBaseURI(){
    	return UriBuilder.fromUri(Options.BASE_URI).build();
    }
    
    static HttpServer startRedirectToHttpsServer() throws IOException{  	
    	
    	URI uri = UriBuilder.fromUri(getBaseURI()).scheme("http").port(80).build();
        
    	HttpServer redirector = GrizzlyHttpServerFactory.createHttpServer(uri);
        ServerConfiguration serverConf = redirector.getServerConfiguration();
        serverConf.addHttpHandler(new HttpHandler() {	
			    @Override
			    public void service(Request request, Response response) throws Exception {
			        response.setStatus(HttpStatus.MOVED_PERMANENTLY_301);
			        URI baseUri = getBaseURI();
			        URI newURI = UriBuilder.fromUri(request.getRequestURI())
			        		.scheme(baseUri.getScheme())
			        		.port(baseUri.getPort())
			        		.build();
			        response.setHeader(Header.Location, newURI.toString());
			    }
			});
        
        return redirector;
    }
    
    
    static HttpServer startSecureServer() throws IOException
    {
        // create a resource config that scans for JAX-RS resources and providers
        // in org.weitblicker and org.weitblicker.rest packages
        final ResourceConfig rc = new ResourceConfig().packages("org.weitblicker", "org.weitblicker.rest", "org.weitblicker.backend");
        rc.register(MultiPartFeature.class);
        

        SSLContextConfigurator sslCon = new SSLContextConfigurator();

        sslCon.setKeyStoreFile(Options.KEYSTORE_LOCATION);
        sslCon.setKeyStorePass(Options.KEYSTORE_PASSWORD);
        
        SSLEngineConfigurator sslConf = new SSLEngineConfigurator(sslCon, false, false, false);
                
        HttpServer grizzlyServer = GrizzlyHttpServerFactory.createHttpServer(
			getBaseURI(),
			rc,
			true,
			sslConf);
        

        return grizzlyServer;
    }
    
    private static void createInitialAdmin(){
    	// TODO verify the inputs.
    	Scanner scanner = new Scanner(System.in);
    	System.out.println("In the following you have to create an admin account:");
    	System.out.println("user name:");
    	String username = scanner.next();
    	System.out.println("email:");
    	String email = scanner.next();
    	System.out.println("password");
    	String password = scanner.next();
    	scanner.close();
    	
    	System.out.println("username: " + username);
    	System.out.println("email: " + email);
    	System.out.println("password: " + password);
    	
    	User admin = new User();
    	admin.setName(username);
    	admin.setEmail(email);
    	admin.setPassword(password);
    	admin.setRole(PersistenceHelper.ADMIN_ROLE_NAME);
    	PersistenceHelper.addUser(admin);
    	
    	System.out.println("The admin account has been created.");
    	
    }

    public static void main(String[] args) throws IOException
    {
    	
        final HttpServer server = Options.USE_SSL ? startSecureServer() : startServer();
        final HttpServer redirector = Options.USE_SSL ? startRedirectToHttpsServer() : null;

        if(!server.isStarted()){
        	System.out.println("start server...");
        	server.start();
        }
        
        if(redirector != null && !redirector.isStarted()){
        	System.out.println("start redirector to https...");
        	server.start();
        }
        
        if(PersistenceHelper.getAdmins().isEmpty()){
        	createInitialAdmin();
        }
        
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", Options.BASE_URI));
    	Scanner scanner = new Scanner(System.in);
    	scanner.next();
        server.shutdown();
        if(redirector != null){
        	System.out.println("shutdown redirector to https");
        	redirector.shutdown();
        }
        scanner.close();
    }
}

