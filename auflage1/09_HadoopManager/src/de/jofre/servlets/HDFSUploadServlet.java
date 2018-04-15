package de.jofre.servlets;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import de.jofre.hadoopcontroller.HDFS;
import de.jofre.hadoopcontroller.HadoopProperties;

public class HDFSUploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    private static final String DATA_DIRECTORY = "E:\\ulTemp";
    private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 10; // 10 MB

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Überprüfe, ob eine Datei hochgeladen werden soll
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);

        if (!isMultipart) {
            return;
        }
        
        // Hole das Zielverzeichnis aus dem Request
        String targetDir = (String)request.getParameter("targetDir");

        // Initialisiere die Entgegennahme der Datei
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax(MAX_REQUEST_SIZE);

        try {
            // Verarbeite den Request
            List<FileItem> items = upload.parseRequest(request);
            Iterator<FileItem> iter = items.iterator();
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();

                if (!item.isFormField()) {
                    String fileName = new File(item.getName()).getName();
                    String filePath = DATA_DIRECTORY + File.separator + fileName;
                    File uploadedFile = new File(filePath);
                    
                    // Speichere die Datei im angegebenen, lokalen Verzeichnis
                    item.write(uploadedFile);
                    
                    // Lege Dateien im HDFS ab
            		HDFS hdfsc = new HDFS();
            		hdfsc.init(HadoopProperties.get("hdfs_address"), HadoopProperties.get("hadoop_user"));
            		hdfsc.uploadFile(filePath, targetDir);
                }
            }

            // Ist die Datei hochgeladen, leite zu hdfs.jsp weiter
            getServletContext().getRequestDispatcher("/hdfs.jsp?currentDir="+targetDir).forward(
                    request, response);

        } catch (FileUploadException ex) {
            throw new ServletException(ex);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }

    }
}
