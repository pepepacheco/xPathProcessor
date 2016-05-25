/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iesvdc.lmsgi.modelo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
// import javax.xml.transform.stream.StreamSource;
// import net.sf.saxon.s9api.DocumentBuilder;

import net.sf.saxon.s9api.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author juang
 */
public class s9apiUtil {
    public static String runXpath(File fileXML, String stringXP) {
        String resultado="";
        try {
            Processor proc = new Processor(false);
            XPathCompiler xpath = proc.newXPathCompiler();
            DocumentBuilder builder = proc.newDocumentBuilder();
            builder.setLineNumbering(true);
            builder.setWhitespaceStrippingPolicy(WhitespaceStrippingPolicy.ALL);
            
            XdmNode documentoXML = builder.build(fileXML);
            XPathSelector selector = xpath.compile(stringXP).load();
            selector.setContextItem(documentoXML);
            XdmValue evaluate = selector.evaluate();

            for (XdmItem item: evaluate) {
               resultado+=item.getStringValue()+"\n";
            }
            
        } catch (SaxonApiException ex) {
            Logger.getLogger(s9apiUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resultado;
    }
    
    public static String validateXSD(File fileXML, File fileXSD){
        String resultado="Error al procesar el archivo";
        if (fileXML==null) resultado+= "No tengo el XML";
        if (fileXSD==null) resultado+= "No tengo el XSD";
        System.out.println("XML="+fileXML.getAbsolutePath()+"\nXSD="+fileXSD.getAbsolutePath()+"\n");
        if (fileXML!=null && fileXSD!=null) {
            try {
                Processor proc = new Processor(true);
                SchemaManager sm = proc.getSchemaManager();
                sm.load(new StreamSource(new File(fileXSD.getAbsolutePath())));
                SchemaValidator validator = sm.newSchemaValidator();
                XdmNode source = proc.newDocumentBuilder().build(new StreamSource(fileXML));
                proc.writeXdmValue(source, validator);
                resultado="Validation succeeded";
            } catch (SaxonApiException /*| IOException*/ ex) {
                resultado=ex.getLocalizedMessage()+"\n"+ex.getMessage()+"\n";
            }
        }
        return resultado;
    }
    
    public static String xslTransform(
            File xmlFile, 
            File xslFile,
            File htmlOut) {
        String resultado="Transformación completada correctamente.";
        if (xmlFile!= null && xslFile!=null && htmlOut!=null) {
            try {                        
                Processor proc = new Processor(false);
                XsltCompiler comp = proc.newXsltCompiler();
                XsltExecutable exp = comp.compile(new StreamSource(xslFile));
                XdmNode source = proc.newDocumentBuilder().build(new StreamSource(xmlFile));
                Serializer out = proc.newSerializer(htmlOut);
                out.setOutputProperty(Serializer.Property.METHOD, "html");
                out.setOutputProperty(Serializer.Property.INDENT, "yes");
                XsltTransformer trans = exp.load();
                trans.setInitialContextNode(source);
                trans.setDestination(out);
                trans.transform();                 
                resultado = new String(Files.readAllBytes(htmlOut.toPath()));
            } catch (IOException | SaxonApiException ex) {
                // Logger.getLogger(s9apiUtil.class.getName()).log(Level.SEVERE, null, ex);
                resultado=ex.getLocalizedMessage();
            }
        } else {
            resultado="Error procesando ficheros.";
        }
        return resultado;
    }
    
}
