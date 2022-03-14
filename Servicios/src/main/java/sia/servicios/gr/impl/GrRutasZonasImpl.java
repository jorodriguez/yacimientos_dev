/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.gr.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import sia.constantes.Constantes;
import sia.modelo.GrRutasZonas;
import sia.modelo.SgEstadoSemaforo;
import sia.modelo.SgSemaforo;
import sia.modelo.Usuario;
import sia.modelo.gr.vo.GrPuntoVO;
import sia.modelo.gr.vo.GrRutaZonasVO;
import sia.modelo.gr.vo.MapaVO;
import sia.modelo.sgl.semaforo.vo.SgEstadoSemaforoVO;
import sia.modelo.sgl.viaje.vo.RutaTerrestreVo;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.sgl.semaforo.impl.SgEstadoSemaforoImpl;
import sia.servicios.sgl.semaforo.impl.SgSemaforoImpl;
import sia.servicios.sgl.viaje.impl.SgRutaTerrestreImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@LocalBean 
public class GrRutasZonasImpl extends AbstractFacade<GrRutasZonas> {

    @Inject
    private GrMapaImpl grMapaRemote;
    @Inject
    private SgRutaTerrestreImpl sgRutaTerrestreRemote;
    @Inject
    private GrPuntoImpl grPuntoRemote;
    @Inject
    private SgEstadoSemaforoImpl sgEstadoSemaforoRemote;
    @Inject
    private SgSemaforoImpl sgSemaforoRemote;

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GrRutasZonasImpl() {
        super(GrRutasZonas.class);
    }

    
    public List<GrRutaZonasVO> zonasPorRuta(RutaTerrestreVo ruta, boolean conSemaforo) {
        List<GrRutaZonasVO> lst = new ArrayList<GrRutaZonasVO>();
        try {
            StringBuilder sb = new StringBuilder(500);
            sb.append(
                    "select a.ID,a.CANCELA,a.FECHA_GENERO,a.HORA_GENERO,a.ELIMINADO,m.ID,m.NOMBRE,\n"
                    + " m.DESCRIPCION,m.GENERO,m.FECHA_GENERO,m.HORA_GENERO,m.MODIFICO,m.FECHA_MODIFICO,\n"
                    + " m.HORA_MODIFICO, a.GR_PUNTO, a.SECUENCIA, a.CANCELASN, a.codigo, m.codigo\n "
                    + "from GR_RUTAS_ZONAS a \n"
                    + " inner join GR_MAPA m on m.id = a.GR_MAPA and m.eliminado = 'False' \n"
                    + "where a.SG_RUTA_TERRESTRE = ? \n"
                    + " and a.eliminado = 'False' \n"
                    + " order by cast(a.SECUENCIA as integer)"
            );

            UtilLog4j.log.info(this, "Q: : : : : : : : : : " + sb.toString());

            List<Object[]> lo = 
                    em.createNativeQuery(sb.toString())
                            .setParameter(1, ruta.getId())
                            .getResultList();
            
            if (lo != null) {
                GrRutaZonasVO rutaZona = null;
                lst = new ArrayList<GrRutaZonasVO>();
                for (Object[] objects : lo) {
                    rutaZona = new GrRutaZonasVO();
                    rutaZona.setRuta(ruta);
                    rutaZona.setId((Integer) objects[0]);
                    rutaZona.setCancelasr((Boolean) objects[1]);
                    rutaZona.setFechaGenero((Date) objects[2]);
                    rutaZona.setHoraGenero((Date) objects[3]);
                    rutaZona.setActiva(!(Boolean) objects[4]);
                    rutaZona.setZona(castMapa(objects, conSemaforo));
                    rutaZona.setIdPunto((Integer) objects[14] == null ? 0 : (Integer) objects[14]);
                    rutaZona.setPunto(grPuntoRemote.getPunto(rutaZona.getIdPunto()));
                    rutaZona.setSecuencia((String) objects[15]);
                    rutaZona.setCancelasn(((Boolean) objects[16]));
                    rutaZona.setCodigo(objects[17] == null ? Constantes.VACIO : (String) objects[17]);
                    lst.add(rutaZona);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
        }
        return lst;
    }

    
    public GrRutasZonas getByRutaIDZonaID(int rutaID, int zonaID) {
        GrRutasZonas r = null;
        try {
            StringBuilder sb = new StringBuilder(200);
            sb.append(
                    " select * \n"
                    + " from GR_RUTAS_ZONAS  \n"
                    + " where SG_RUTA_TERRESTRE = ? \n"
                    + " and GR_MAPA = ?"
                    + " order by id desc "
            );
            
            UtilLog4j.log.info(this, "Q: : : : : : : : : : " + sb.toString());
            List<GrRutasZonas> lo = 
                    em.createNativeQuery(sb.toString(), "GrRutasZonas_map")
                            .setParameter(1, rutaID)
                            .setParameter(2, zonaID)
                            .getResultList();
            if (lo != null && !lo.isEmpty()) {
                r = lo.get(0);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
        }
        return r;
    }

    
    public GrRutaZonasVO zonaRutaPorID(int rutaZonaID, boolean conSemaforo) {
        GrRutaZonasVO rutaZona = null;
        try {
            StringBuilder sb = new StringBuilder(512);
            sb.append(
                    "select a.ID,a.CANCELA,a.FECHA_GENERO,a.HORA_GENERO,a.ELIMINADO,m.ID,m.NOMBRE,\n"
                    + " m.DESCRIPCION,m.GENERO,m.FECHA_GENERO,m.HORA_GENERO,m.MODIFICO,m.FECHA_MODIFICO,\n"
                    + " m.HORA_MODIFICO, a.GR_PUNTO, a.SECUENCIA,a.CANCELASN, a.codigo, m.codigo\n "
                    + "from GR_RUTAS_ZONAS a \n"
                    + " inner join GR_MAPA m on m.id = a.GR_MAPA \n"
                    + "where a.ID = ?");

            UtilLog4j.log.info(this, "Q: : : : : : : : : : " + sb.toString());

            Object[] obj = 
                    (Object[]) em.createNativeQuery(sb.toString())
                            .setParameter(1, rutaZonaID)
                            .getSingleResult();

            if (obj != null) {
                rutaZona = new GrRutaZonasVO();
                rutaZona.setId((Integer) obj[0]);
                rutaZona.setCancelasr((Boolean) obj[1]);
                rutaZona.setFechaGenero((Date) obj[2]);
                rutaZona.setHoraGenero((Date) obj[3]);
                rutaZona.setActiva(!(Boolean) obj[4]);
                rutaZona.setZona(castMapa(obj, conSemaforo));
                rutaZona.setIdPunto((Integer) obj[14] == null ? 0 : (Integer) obj[14]);
                rutaZona.setPunto(grPuntoRemote.getPunto(rutaZona.getIdPunto()));
                rutaZona.setSecuencia((String) obj[15]);
                rutaZona.setCancelasn((Boolean) obj[16]);
                rutaZona.setCodigo(obj[17] == null ? Constantes.VACIO : (String) obj[17]);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
        }
        return rutaZona;
    }

    private MapaVO castMapa(Object[] obj, boolean conSemaforo) {
        MapaVO o = new MapaVO();
        o.setId((Integer) obj[5]);
        o.setNombre((String) obj[6]);
        o.setDescripcion((String) obj[7]);
        o.setCodigo((String) obj[18] == null ? Constantes.VACIO : (String) obj[18]);
        if (conSemaforo) {
            o.setSemaforoActual(sgEstadoSemaforoRemote.getSemaforoZona(o.getId()));
        }
        return o;
    }

    
    public GrRutasZonas crearRutaZona(int rutaID, int idZona, boolean cancelasr, boolean cancelasn, String usrID, int idPunto, String seq) {
        GrRutasZonas nuevo = null;
        try {
            nuevo = this.getByRutaIDZonaID(rutaID, idZona);
            if (nuevo == null) {
                nuevo = new GrRutasZonas();
                nuevo.setGrMapa(grMapaRemote.find(idZona));
                nuevo.setSgRutaTerrestre(sgRutaTerrestreRemote.find(rutaID));
                nuevo.setCodigo(new StringBuilder().append(nuevo.getGrMapa().getCodigo()).toString());
                nuevo.setGenero(new Usuario(usrID));
                nuevo.setFechaGenero(new Date());
                nuevo.setHoraGenero(new Date());
                nuevo.setSecuencia(seq);
                nuevo.setEliminado(Constantes.BOOLEAN_FALSE);
                nuevo.setCancelasr(cancelasr);
                nuevo.setCancelasn(cancelasn);
                if (idPunto > 0) {
                    nuevo.setGrPunto(grPuntoRemote.find(idPunto));
                }
                this.create(nuevo);
                List<SgEstadoSemaforoVO> semaforos = sgEstadoSemaforoRemote.getEstadoSemaforos(idZona);
                if (semaforos == null || semaforos.isEmpty()) {
                    SgSemaforo semaf = sgSemaforoRemote.find(Constantes.ID_COLOR_SEMAFORO_AMARILLO);
                    SgEstadoSemaforo nuevoSemaforo = new SgEstadoSemaforo();
                    nuevoSemaforo.setSgSemaforo(semaf);
                    nuevoSemaforo.setGrMapa(grMapaRemote.find(idZona));
                    nuevoSemaforo.setJustificacion("Nueva relación Ruta-Zona");
                    nuevoSemaforo.setGenero(new Usuario(usrID));
                    nuevoSemaforo.setFechaGenero(new Date());
                    nuevoSemaforo.setHoraGenero(new Date());
                    nuevoSemaforo.setFechaInicio(new Date());
                    nuevoSemaforo.setHoraInicio(new Date());
                    nuevoSemaforo.setEliminado(Constantes.BOOLEAN_FALSE);
                    sgEstadoSemaforoRemote.create(nuevoSemaforo);
                }
            } else {
                if (nuevo.getCodigo() == null || nuevo.getCodigo().isEmpty()) {
                    nuevo.setCodigo(new StringBuilder().append(nuevo.getGrMapa().getCodigo()).toString());
                }
                nuevo.setModifico(new Usuario(usrID));
                nuevo.setFechaModifico(new Date());
                nuevo.setHoraModifico(new Date());
                nuevo.setSecuencia(seq);
                nuevo.setEliminado(Constantes.BOOLEAN_FALSE);
                nuevo.setCancelasr(cancelasr ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);
                nuevo.setCancelasn(cancelasn ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);
                if ((nuevo.getGrPunto() == null && idPunto > 0)
                        || (idPunto > 0 && nuevo.getGrPunto().getId() != idPunto)) {
                    nuevo.setGrPunto(grPuntoRemote.find(idPunto));
                } else if (nuevo.getGrPunto() != null && idPunto == 0) {
                    nuevo.setGrPunto(null);
                }
                this.edit(nuevo);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            nuevo = null;
        }
        return nuevo;
    }

    
    public GrRutasZonas guardarRutaZona(GrRutaZonasVO vo, String usrID) {
        //TODO : podria implementarse algo de esta lógica a manera de trigger?
        GrRutasZonas nuevo = null;
        boolean guarda = false;
        try {
            nuevo = find(vo.getId());
            if ((!vo.isActiva() && nuevo.isEliminado())
                    || (vo.isActiva() && nuevo.isEliminado())) {
                nuevo.setEliminado(vo.isActiva() ? Constantes.BOOLEAN_FALSE : Constantes.BOOLEAN_TRUE);
                guarda = true;
            }

            if ((nuevo.isCancelasr()) || (!vo.isCancelasr() && nuevo.isCancelasr())
                    || (vo.isCancelasr() && nuevo.isCancelasr())) {
                nuevo.setCancelasr(vo.isCancelasr() ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);
                guarda = true;
            }

            if ((nuevo.isCancelasn()) || (!vo.isCancelasn() && nuevo.isCancelasn())
                    || (vo.isCancelasn() && !nuevo.isCancelasn())) {
                nuevo.setCancelasn(vo.isCancelasn() ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);
                guarda = true;
            }

            if ((vo.getIdPunto() > 0 && nuevo.getGrPunto() == null)
                    || (vo.getIdPunto() > 0 && nuevo.getGrPunto() != null && nuevo.getGrPunto().getId() != vo.getIdPunto())) {
                nuevo.setGrPunto(grPuntoRemote.find(vo.getIdPunto()));
                guarda = true;
            } else if (vo.getIdPunto() == 0 && nuevo.getGrPunto() != null) {
                nuevo.setGrPunto(null);
                guarda = true;
            }

            if ((vo.getSecuencia() != null && nuevo.getSecuencia() == null) || (vo.getSecuencia() == null && nuevo.getSecuencia() != null)
                    || (vo.getSecuencia() != null && nuevo.getSecuencia() != null && !nuevo.getSecuencia().equals(vo.getSecuencia()))) {
                nuevo.setSecuencia(vo.getSecuencia());
                guarda = true;
            }

            if (nuevo.getCodigo() == null || nuevo.getCodigo().isEmpty()) {
                nuevo.setCodigo(new StringBuilder().append(vo.getZona().getCodigo()).toString());
                guarda = true;
            }

            if (guarda) {
                nuevo.setModifico(new Usuario(usrID));
                nuevo.setFechaModifico(new Date());
                nuevo.setHoraModifico(new Date());
                this.edit(nuevo);
            }

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            nuevo = null;
        }
        return nuevo;
    }

    /**
     *
     * @param ruta
     * @return
     */
    
    public List<GrRutaZonasVO> traerPuntoPorRuta(int ruta) {
        clearQuery();
        query.append("select rz.id, m.id, m.nombre, p.id,  p.NOMBRE, rz.codigo, m.codigo  from GR_RUTAS_ZONAS rz");
        query.append("	    inner join GR_MAPA m on rz.GR_MAPA = m.ID");
        query.append("	    inner join GR_PUNTO p on rz.GR_PUNTO = p.ID");
        query.append("  where rz.SG_RUTA_TERRESTRE = ").append(ruta);
        query.append("  and rz.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append('\'');
        //
        List<Object[]> obj = em.createNativeQuery(query.toString()).getResultList();
        List<GrRutaZonasVO> lp = new ArrayList<GrRutaZonasVO>();
        if (obj != null) {
            for (Object[] obj1 : obj) {
                GrRutaZonasVO grRutaZonasVO = new GrRutaZonasVO();
                grRutaZonasVO.setId((Integer) obj1[0]);
                grRutaZonasVO.setCodigo((String) obj1[5] == null ? Constantes.VACIO : (String) obj1[5]);
                grRutaZonasVO.setZona(new MapaVO());
                grRutaZonasVO.getZona().setId((Integer) obj1[1]);
                grRutaZonasVO.getZona().setNombre((String) obj1[2]);
                grRutaZonasVO.getZona().setCodigo((String) obj1[6] == null ? Constantes.VACIO: (String) obj1[6]);
                grRutaZonasVO.setPunto(new GrPuntoVO());
                grRutaZonasVO.getPunto().setId((Integer) obj1[3]);
                grRutaZonasVO.getPunto().setNombre((String) obj1[4]);
                //
                lp.add(grRutaZonasVO);
            }
        }

        return lp;
    }

//    
//    public List<GrRutaZonasVO> busquedaZonaRuta(String codigo) {
//        clearQuery();
//        query.append(" SELECT CODIGO, NOMBRE, ID FROM ");
//        query.append(" ( ");
//        query.append(" select a.CODIGO, m.NOMBRE, m.ID ");
//        query.append(" from GR_RUTAS_ZONAS a ");
//        query.append(" inner join GR_MAPA m on m.id = a.GR_MAPA ");
//        query.append(" where a.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
//        query.append(" and a.CODIGO is not null ");
//        query.append(" union  ");
//        query.append(" select a.CODIGO, a.NOMBRE, a.ID ");
//        query.append(" from GR_MAPA a ");
//        query.append(" where a.ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("'");
//        query.append(" and a.ID not in (select GR_MAPA from GR_RUTAS_ZONAS where ELIMINADO = '").append(Constantes.NO_ELIMINADO).append("' ))");
//        if (codigo != null && !codigo.isEmpty()) {
//            query.append(" where upper(CODIGO) = upper('").append(codigo).append("')");
//        }
//        query.append(" order by NOMBRE ");
//
//        //
//        List<Object[]> obj = em.createNativeQuery(query.toString()).getResultList();
//        List<GrRutaZonasVO> lp = new ArrayList<GrRutaZonasVO>();
//        if (obj != null) {
//            for (Object[] obj1 : obj) {
//                GrRutaZonasVO grRutaZonasVO = new GrRutaZonasVO();
//                grRutaZonasVO.setZona(new MapaVO());
//                grRutaZonasVO.getZona().setId((Integer) obj1[2]);
//                grRutaZonasVO.getZona().setNombre((String) obj1[1]);
//                grRutaZonasVO.getZona().setCodigo((String) obj1[0]);
//                lp.add(grRutaZonasVO);
//            }
//        }
//        return lp;
//    }

    /**
     *
     * @param ruta
     * @param codigo
     */
    
    public void modificarCodigos(int ruta, String codigo) {
        try {
            RutaTerrestreVo voRuta = new RutaTerrestreVo();
            voRuta.setId(ruta);
            List<GrRutaZonasVO> lp = this.zonasPorRuta(voRuta, false);
            for (GrRutaZonasVO vo : lp) {
                GrRutasZonas obj = this.find(vo.getId());
                obj.setCodigo(new StringBuilder().append(codigo).append(obj.getGrMapa().getCodigo()).toString());
                edit(obj);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
        }
    }
}
