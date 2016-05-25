/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iesvdc.lmsgi.controlador;

import com.iesvdc.lmsgi.modelo.s9apiUtil;
import com.iesvdc.lmsgi.modelo.DomUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
        
/**
 *
 * @author juang
 */
public class Controller {
    private String fullPath;
    private File file;
    private File fileXSD;
    private File fileDTD;
    private File fileXSL; // para hojas de transformación
    private File fileHTML; // para salida HTML en hojas de transformación 
    String lookandfeel;

    public Controller() {
        this.fullPath=null;
        this.fileDTD=null;
        this.fileXSD=null;
        this.fileXSL=null;
        this.fileHTML=null;
        this.file=null;
        //getProperties();
    }
    
    public Controller(File myfile){
        this.file=myfile;
        this.fullPath=null;
        this.fileDTD=null;
        this.fileXSD=null;
        this.fileXSL=null;
        this.fileHTML=null;
        //getProperties();
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFileXSD() {
        return fileXSD;
    }

    public void setFileXSD(File fileXSD) {
        this.fileXSD = fileXSD;
    }

    public File getFileDTD() {
        return fileDTD;
    }

    public void setFileDTD(File fileDTD) {
        this.fileDTD = fileDTD;
    }

    public File getFileXSL() {
        return fileXSL;
    }

    public void setFileXSL(File fileXSL) {
        this.fileXSL = fileXSL;
    }

    public File getFileHTML() {
        return fileHTML;
    }

    public void setFileHTML(File fileHTML) {
        this.fileHTML = fileHTML;
    }        
    
    private boolean save2File(File fichero, String contenido){
        boolean resultado=true;
        if (fichero!=null && contenido != null) {
            try (FileWriter fw = new FileWriter(fichero)) {             
                fw.write(contenido);
                fw.flush();
            } catch (IOException ex) {
                resultado=false;               
            }                     
        } else {
            resultado=false;
        }
        return resultado;        
    }
    
    public boolean save2XML(String contenido){        
        return save2File(this.file, contenido);
    }
    
    public boolean save2XSL(String contenido){
        return save2File(this.fileXSL, contenido);
    }
    
    public String xPathEvaluate(String xpath){
        return s9apiUtil.runXpath(this.file , xpath);
    }
    
    public String validateDTD(){
        String resultado="Procesando fichero "+this.file.getPath()+"\n";
        try {
            Document doc = DomUtil.parse(this.file, true);            
        } catch (ParserConfigurationException | IOException | SAXException ex) {
            resultado+=ex.getMessage();
        }
        return resultado;
    }
    
    public String validateXSD(){
        String resultado="Validación XSD correcta";
        try {
            // return s9apiUtil.validateXSD(this.file, this.fileXSD);
            Document doc = DomUtil.parseXSD(this.file, null);
        } catch (ParserConfigurationException | IOException | SAXException ex) {
           resultado=ex.getLocalizedMessage();
        }
        return resultado;
    }
    
    public String runXslTransform(){
        return s9apiUtil.xslTransform(
            this.file,
            this.fileXSL,
            this.fileHTML);
    }
    
    public boolean setProperties(){
        Properties prop = new Properties();
        OutputStream output = null;
        boolean correcto=true;

        try {
            output = new FileOutputStream("config.properties");
            // set the properties value
            if (this.lookandfeel!=null) {
                prop.setProperty("lookandfeel", this.lookandfeel);
            }
            else{
                prop.setProperty("lookandfeel", "Nimbus");
            }
            // save properties to project root folder
            prop.store(output, null);
            // output.close();
        } catch (IOException io) {
                correcto=false; 
        } finally {
            if (output != null) {
                try {
                   output.close();
                } catch (IOException e) {
                   correcto=false;
                }
            }
        }
        return correcto;
    }
    
    public boolean getProperties(){
        Properties prop = new Properties();
        InputStream input = null;
        boolean correcto=true;

        try {
            input = new FileInputStream("config.properties");
            prop.load(input);
            // get the properties value
            this.lookandfeel = prop.getProperty("lookandfeel");
            // save properties to project root folder
            //output.close();
            if (this.lookandfeel==null){
                this.lookandfeel="Nimbus";
            }
        } catch (IOException io) {
                correcto=false;
        } finally {
            if (input != null) {
                try {
                   input.close();
                } catch (IOException e) {
                   correcto=false;
                }
            }
        }     
        return correcto;
    }
    
    public String getLookAndFeel(){
        return this.lookandfeel;
    }
    
    public boolean setLookAndFeel(String laf){
        this.lookandfeel=laf;
        System.out.println("INFO:Controller:setLookAndFeel:"+laf);
        return setProperties();
    }
    
    public String getHTML() {
        String resultado;
        
        try {
            resultado= new String(Files.readAllBytes(this.fileHTML.toPath()));
        } catch (IOException ex) {
            resultado = "<html><body>Error leyendo HTML: "+ex.getLocalizedMessage()+"</body></html>";
        }
        return resultado;
    }
}
