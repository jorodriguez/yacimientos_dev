/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.ihsa.procesador.bean;

import mx.ihsa.sistema.bean.backing.Sesion;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import javax.faces.view.ViewScoped;
import javax.imageio.stream.FileImageOutputStream;
import javax.inject.Named;
import lector.vision.Item;
import lector.vision.api.service.LectorService;
import org.primefaces.event.CaptureEvent;

/**
 *
 * @author jorodriguez
 */
@Named
@ViewScoped
public class CamaraView implements Serializable {

    @Inject
    private Sesion sesion;
    
    private String filename;
    
    @Inject
    private LectorService lectorService;
    
    private List<Item> listaTexto;
    

    public CamaraView() {
    }

    @PostConstruct
    public void iniciar() {
        System.out.println("@Postconstruc"+this.getClass().getCanonicalName());
        //loaders
    }
    
    private String getRandomImageName() {
        int i = (int) (Math.random() * 10000000);

        return String.valueOf(i);
    }
    
    
    
    public void oncapture(CaptureEvent captureEvent) {
        filename = getRandomImageName();
        byte[] data = captureEvent.getData();       
        

        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        String newFileName = externalContext.getRealPath("") + File.separator + "resources" + File.separator + "demo"
                + File.separator + "images" + File.separator + "photocam" + File.separator + filename + ".jpeg";

        FileImageOutputStream imageOutput;
        
        try {
            imageOutput = new FileImageOutputStream(new File(newFileName));
            imageOutput.write(data, 0, data.length);
            imageOutput.close();            
            
            // listaTexto= lectorService.getTexto(newFileName);    
            
        }
        catch (IOException e) {
            throw new FacesException("Error in writing captured image.", e);
        }
    } 
    public String getFilename() {
        return filename;
    }

    public List<Item> getListaTexto() {
        return listaTexto;
    }
     

}
