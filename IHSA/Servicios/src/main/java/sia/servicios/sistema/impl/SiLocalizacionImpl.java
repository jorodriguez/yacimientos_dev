/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sistema.impl;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.json.JSONException;
import sia.constantes.Constantes;
import sia.modelo.SiLocalizacion;
import sia.modelo.Usuario;
import sia.modelo.gr.vo.GrMapaGPSVO;
import sia.modelo.sistema.AbstractFacade;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Stateless 
public class SiLocalizacionImpl extends AbstractFacade<SiLocalizacion> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SiLocalizacionImpl() {
        super(SiLocalizacion.class);
    }

    
    public int guardar(String sesion, JsonObject datos) {
        SiLocalizacion siLocalizacion = new SiLocalizacion();
        //
        try {
            if (datos.get("sesion").getAsString() != null) {
                siLocalizacion.setGenero(new Usuario(datos.get("sesion").getAsString()));
            } else {
                siLocalizacion.setGenero(new Usuario(sesion));
            }
            //
            siLocalizacion.setLongitud(datos.get("longitud").getAsString());
            siLocalizacion.setLatitud(datos.get("latitud").getAsString());
            siLocalizacion.setTelefonoId(datos.get("telefonoId").getAsString());
            siLocalizacion.setTelefonoSimOperador(datos.get("simOperatorName").getAsString());
            siLocalizacion.setTelefonoSimSerie(datos.get("simSerialNumber").getAsString());
        } catch (JSONException e) {
            UtilLog4j.log.debug(this, "", e);
        }
        //

        siLocalizacion.setFechaGenero(new Date());
        siLocalizacion.setHoraGenero(new Date());
        siLocalizacion.setEliminado(Constantes.NO_ELIMINADO);
        create(siLocalizacion);
        //
        return siLocalizacion.getId();
    }

    
    public List<GrMapaGPSVO> obtenerCoordenadas(int viajeID) {
        List<GrMapaGPSVO> rets = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" select TELEFONO_ID, LONGITUD, LATITUD, FECHA_GENERO, HORA, MINUTO ");
            sb.append(" from ( ");
            sb.append(" select first 5 lo.TELEFONO_ID, lo.LONGITUD, lo.LATITUD, lo.FECHA_GENERO, EXTRACT (HOUR FROM lo.HORA_GENERO) as HORA, EXTRACT (MINUTE FROM lo.HORA_GENERO) as MINUTO  ");
            sb.append(" from SG_VIAJE v ");
            sb.append(" inner join SG_ASIGNAR_ACCESORIO aa on aa.USUARIO = v.RESPONSABLE and aa.ELIMINADO = 'False' ");

            sb.append(" inner join SG_accesorio a on aa.sg_accesorio = a.id and a.ELIMINADO = 'False' ");
            sb.append(" inner join SG_LINEA l on a.SG_LINEA = l.id and l.ELIMINADO = 'False' ");

            sb.append(" inner join SI_LOCALIZACION lo on lo.TELEFONO_ID = l.EMEI and lo.ELIMINADO = 'False' and lo.FECHA_GENERO = v.FECHA_SALIDA and lo.HORA_GENERO >= v.HORA_SALIDA ");
            sb.append("         and ((v.FECHA_LLEGADA is null) or (v.FECHA_LLEGADA is not null and lo.FECHA_GENERO <= v.FECHA_LLEGADA and lo.HORA_GENERO <= v.HORA_LLEGADA )) ");
            sb.append(" where v.id = ").append(viajeID);
            sb.append(" group by lo.TELEFONO_ID, lo.LONGITUD, lo.LATITUD, lo.FECHA_GENERO, HORA, MINUTO ");
            sb.append(" order by lo.FECHA_GENERO DESC, HORA DESC, MINUTO DESC ");
            sb.append(" ) ");
            sb.append(" ORDER BY HORA, MINUTO ");

            List<Object[]> lo = em.createNativeQuery(sb.toString()).getResultList();
            if (lo != null) {
                rets = new ArrayList<>();
                for (Object[] objects : lo) {
                    rets.add(castMapa(objects));
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            rets = new ArrayList<>();
        }
        return rets;
    }

    private GrMapaGPSVO castMapa(Object[] obj) {
        GrMapaGPSVO o = new GrMapaGPSVO((String) obj[0], (String) obj[1], (String) obj[2]);
        //, new StringBuilder().append(Constantes.FMT_yyyy_MM_dd.format((Date) obj[4])).append("T")
        //.append(Constantes.FMT_HHmmss.format((Date) obj[5])).append(".511Z").toString());
        return o;
    }
}
