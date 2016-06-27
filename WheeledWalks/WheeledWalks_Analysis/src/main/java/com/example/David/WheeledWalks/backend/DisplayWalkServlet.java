/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/

package com.example.David.WheeledWalks.backend;



import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.*;



public class DisplayWalkServlet extends HttpServlet {

    //private static final Logger log = Logger.getLogger(DisplayWalkServlet.class.getName());





    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try {
            req.getRequestDispatcher("/DisplayWalk.html").forward(req, resp);
        }catch (ServletException e){
            e.printStackTrace();
        }

    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
    }
}
