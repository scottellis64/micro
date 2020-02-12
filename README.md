This is a Spring Boot application that is composed of 3 microservices, and a utility module that provides 
JWT validation.  There is no way to actually log in yet, as this application exists only to demonstrate 
an issue that occurs when spring security configuration attempts to forward to the login page.

The setup is like this:

- service-registry: a Eureka service registry microservice running on port 8761 that all other microservices communicate with
- web: a simple web application running on port 8082 with two end points
   * / - a simple web page that cannot be reached without first being authenticated.
   * /login - the login page that doesn't have an actual login form that can be seen because the user does not have to be authenticated
- gateway: a Zuul gateway instance running on port 8080 with one path that leads to the web application
   * there is a single route called 'web' that forwards request to the web microservice.
   * you can reach the web application directly at localhost:8082 but it won't look right because the page
     assumes that are accessing the page through the gateway, so all styling is missing

Run these services in order, then try the login page at http://localhost:8080/web/login.  
You should be able to see this page.

Now try to view http://localhost:8080/web.

Spring security configuration determines rightly that there is no JWT bearer token and attempts to 
redirect the browser to the login page, but the url is wrong in a few different ways.

- The url it attempts is http://localhost:8082/web/login
- The desired url is http://localhost:8080/web/login

The port number it actually uses is the port number of the web microservice.
If the port number were that of the gateway, then this issue would be resolved.
 


