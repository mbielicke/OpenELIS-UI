package org.openelis.ui.server;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * This class provide the parse capability for file upload. This class works
 * with file upload widget to send files from the client to server.
 */

public class FileServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
                                                                                  IOException {
        List<FileItem> files;
        FileItemFactory factory;
        ServletFileUpload upload;

        try {
            factory = new DiskFileItemFactory();
            upload = new ServletFileUpload(factory);
            files = upload.parseRequest(req);
            
            System.out.print("files uploaded = ");
            if (files != null && files.size() > 0) {
            	System.out.println(files.size());
            	
            	if (req.getSession().getAttribute("upload") != null)  
            		((List<FileItem>)req.getSession().getAttribute("upload")).addAll(files);
            	else
            		req.getSession().setAttribute("upload", files);
            }
        } catch (Exception e) {
        	e.printStackTrace();
            throw (ServletException)e.getCause();
        }
        
        resp.getOutputStream().print("Files Uploaded");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
                                                                                   IOException {
        doGet(req, resp);
    }
}