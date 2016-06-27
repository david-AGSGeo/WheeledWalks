package com.example.David.WheeledWalks.backend;

import com.google.appengine.api.backends.BackendServiceFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by David on 14/05/2016.
 */
public class WarmupServlet extends HttpServlet{

    private static final Logger log = Logger.getLogger(WarmupServlet.class.getName());


    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.service(req, resp);
        //initialise background servlet WalkProcessingServlet
        String backendAddress = BackendServiceFactory.getBackendService()
                .getBackendAddress("walkprocessing");
        log.info("got backend Address: " + backendAddress);
        URL url = new URL("HTTP://" +backendAddress + "/mapping/WalkProcessingServlet");
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        log.info("Sent GET request to WalkProcessingServlet");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }



    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

}

}
