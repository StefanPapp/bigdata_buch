package de.jofre.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.jofre.hadoopcontroller.HDFS;
import de.jofre.hadoopcontroller.HadoopProperties;

public class HDFSDownloadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	static final int BUFFER_SIZE = 16384;
	
	private final static Logger log = Logger.getLogger(HDFSDownloadServlet.class
			.getName());
 
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        File file = getFileToDownload(request);
        prepareResponseFor(response, file);
        streamFileTo(response, file);
    }
 
    private File getFileToDownload(HttpServletRequest request) {
    	
    	File result = null;
    	
    	// Hole die Referenz auf die herunterzuladende Datei aus dem
    	// entsprechenden URL-Parameter.
    	String dlFile = (String)request.getParameter("dl");
    	
    	// Wurde eine Datei gefunden?
    	if ((dlFile != null) && (!dlFile.trim().equals(""))) {
    		
    		try {
				dlFile = URLDecoder.decode(dlFile, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				log.log(Level.WARNING, "Fehler beim dekodieren der Download-URL.");
				e.printStackTrace();
			}
    		
    		// Initialisiere den Zugriff auf das HDFS
    		HDFS hdfsc = new HDFS();
    		hdfsc.init(HadoopProperties.get("hdfs_address"), HadoopProperties.get("hadoop_user"));
    		
    		// Lade die Datei aus dem HDFS in ein lokales Verzeichnis
    		// und liefere sie zurück.
    		String downloadedFile = hdfsc.downloadFile(dlFile);
    		if (downloadedFile != null) {
    			result = new File(downloadedFile);
    		}
    		
    	}
    	
    	return result;
    }
 
    private void streamFileTo(HttpServletResponse response, File file)
            throws IOException, FileNotFoundException {
        OutputStream os = response.getOutputStream();
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = 0;
        while ((bytesRead = fis.read(buffer)) > 0) {
            os.write(buffer, 0, bytesRead);
        }
        os.flush();
        fis.close();
    }
 
    private void prepareResponseFor(HttpServletResponse response, File file) {
        StringBuilder type = new StringBuilder("attachment; filename=");
        type.append(file.getName());
        response.setContentLength((int) file.length());
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", type.toString());
    }

}
