/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.sgl.vehiculo.impl;

import com.google.common.base.Joiner;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sia.constantes.Constantes;
import sia.modelo.SgCursoManejo;
import sia.modelo.SgTipoEspecifico;
import sia.modelo.Usuario;
import sia.modelo.cursoManejo.vo.CursoManejoVo;
import sia.modelo.sistema.AbstractFacade;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sgl.impl.SgTipoEspecificoImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author jevazquez
 */
@Stateless 
public class SgCursoManejoImpl extends AbstractFacade<SgCursoManejo>{

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    @Inject
    private UsuarioImpl usuarioRemote;
    @Inject
    private SgTipoEspecificoImpl sgTipoEspecificoRemote;
    @Inject
    private SiParametroImpl parametroRemote;
    @Inject
    private SiManejoFechaImpl siManejoFechaLocal;

    public SgCursoManejoImpl() {
        super(SgCursoManejo.class);
    }

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    private String queryBase() {
        String sql = "SELECT c.ID, c.FECHA_EXPEDICION,c.FECHA_VENCIMIENTO,c.VIGENTE,u.ID,u.NOMBRE,\n" //0-5
                + " CASE WHEN t.ID is null THEN 0 ELSE t.ID END,CASE WHEN t.NOMBRE is NULL THEN '' ELSE t.NOMBRE END, \n" //6-7
                + " CASE WHEN a.ID is NULL THEN 0 ELSE a.ID END,CASE WHEN a.NOMBRE is NULL THEN '' ELSE a.NOMBRE END, \n"//8-9
                + " CASE WHEN c.NUM_CURSO is NULL THEN 0 ELSE c.NUM_CURSO END,"//10
                + " ofi.id, ofi.NOMBRE, ap.id, ap.NOMBRE, u.gerencia" //11-14
                + "  FROM SG_CURSO_MANEJO c \n"
                + "  INNER JOIN USUARIO u ON u.ID = c.USUARIO AND u.ELIMINADO = ?  and u.interno = ?\n"
                + "  INNER JOIN SG_TIPO_ESPECIFICO t ON t.ID = c.SG_TIPO_ESPECIFICO and t.ELIMINADO = ? \n"
                + "  INNER join SG_OFICINA ofi on ofi.ID = u.SG_OFICINA and ofi.ELIMINADO = ? \n"
                + "  INNER join AP_CAMPO ap on ap.ID =  ofi.AP_CAMPO and ap.ELIMINADO = ? \n"
                + "  LEFT Join SI_ADJUNTO a ON a.ID = c.SI_ADJUNTO AND a.ELIMINADO = ? \n";
        return sql;
    }

    
    public List<CursoManejoVo> relacionUsuarioCursos(boolean todos, boolean activos, boolean porVencer) {//agregar id de curso para filtrar
        UtilLog4j.log.info("UsuariosCursosActivos");
        StringBuilder sb = new StringBuilder();
        sb.append(queryBase())
                .append("   WHERE c.ELIMINADO = ?");

        if (porVencer) {
            String d2 = Constantes.FMT_yyyy_MM_dd.format(siManejoFechaLocal.fechaSumarMes(new Date(), 2)) + "";
            sb.append(" and c.FECHA_VENCIMIENTO BETWEEN current_date and  '").append(d2).append("' :: date");
        } else if (!todos) {
            if (activos) {
                sb.append(" and c.FECHA_VENCIMIENTO >= current_date");
            } else {
                sb.append(" and c.FECHA_VENCIMIENTO < current_date");
            }
        }

        List<Object[]> lo = em.createNativeQuery(sb.toString())
                .setParameter(1, Constantes.NO_ELIMINADO)
                .setParameter(2, Constantes.TRUE)
                .setParameter(3, Constantes.NO_ELIMINADO)
                .setParameter(4, Constantes.NO_ELIMINADO)
                .setParameter(5, Constantes.NO_ELIMINADO)
                .setParameter(6, Constantes.NO_ELIMINADO)
                .setParameter(7, Constantes.NO_ELIMINADO)
                .getResultList();

        return castCursoManejoVo(lo);
    }

    
    public List<CursoManejoVo> usuariosCursosActivosByVencimeintoAndCampo(Date di, Date dv, int apCampo, boolean todos, boolean activo, String order) {
        UtilLog4j.log.info("UsuariosCursosActivosByVencimeinto");
        StringBuilder sb = new StringBuilder();
        String add = "";
        String dateInicio = Constantes.FMT_yyyy_MM_dd.format(di);
        String dateFin = Constantes.FMT_yyyy_MM_dd.format(dv);
        if (!todos) {
            if (activo) {
                add = " and c.VIGENTE = true";
            } else {
                add = " and c.VIGENTE = false";
            }
        }
        
        sb.append(queryBase())
                .append("   WHERE c.ELIMINADO = ? and c.FECHA_VENCIMIENTO BETWEEN '")
                .append(Constantes.FMT_yyyy_MM_dd.format(di))
                .append("' and '")
                .append(Constantes.FMT_yyyy_MM_dd.format(dv))
                .append("' and ofi.AP_CAMPO = ?")
                .append(add)
                .append(order);

        List<Object[]> lo = em.createNativeQuery(sb.toString())
                .setParameter(1, Constantes.NO_ELIMINADO)
                .setParameter(2, Constantes.TRUE)
                .setParameter(3, Constantes.NO_ELIMINADO)
                .setParameter(4, Constantes.NO_ELIMINADO)
                .setParameter(5, Constantes.NO_ELIMINADO)
                .setParameter(6, Constantes.NO_ELIMINADO)
                .setParameter(7, Constantes.NO_ELIMINADO)
                .setParameter(8, apCampo)
                .getResultList();

        return castCursoManejoVo(lo);
    }

    
    public List<CursoManejoVo> usuariosCursosActivosByVencimeintoMAyorOMenor(Date d, int apCampo, boolean todos, boolean activo, boolean mayorQue) {
        UtilLog4j.log.info("UsuariosCursosActivosByVencimeinto");
        StringBuilder sb = new StringBuilder();
        String add = "";
        String fecha = "";
        if (!todos) {
            if (activo) {
                add = " and c.VIGENTE = true";
            } else {
                add = " and c.VIGENTE = false";
            }
        }
        if (mayorQue) {
            fecha = " and c.FECHA_VENCIMIENTO >= '" + Constantes.FMT_yyyy_MM_dd.format(d) + "'";
        } else {
            fecha = " and c.FECHA_VENCIMIENTO < '" + Constantes.FMT_yyyy_MM_dd.format(d) + "'";
        }
        sb.append(queryBase())
                .append("   WHERE c.ELIMINADO = ?  and ofi.AP_CAMPO = ?")
                .append(add)
                .append(fecha)
                .append("   order by ofi.id,c.FECHA_VENCIMIENTO");

        List<Object[]> lo = em.createNativeQuery(sb.toString())
                .setParameter(1, Constantes.NO_ELIMINADO)
                .setParameter(2, Constantes.TRUE)
                .setParameter(3, Constantes.NO_ELIMINADO)
                .setParameter(4, Constantes.NO_ELIMINADO)
                .setParameter(5, Constantes.NO_ELIMINADO)
                .setParameter(6, Constantes.NO_ELIMINADO)
                .setParameter(7, Constantes.NO_ELIMINADO)
                .setParameter(8, apCampo)
                .getResultList();

        return castCursoManejoVo(lo);
    }

    
    public List<CursoManejoVo> usuariosCursosActivosByUsuarioUId(String usuario) {
        UtilLog4j.log.info("UsuariosCursosActivosByUsuarioUId");
        StringBuilder sb = new StringBuilder();
        sb.append(queryBase())
                .append("   WHERE c.ELIMINADO = ? and u.id = ?");

        List<Object[]> lo = em.createNativeQuery(sb.toString())
                .setParameter(1, Constantes.NO_ELIMINADO)
                .setParameter(2, Constantes.TRUE)
                .setParameter(3, Constantes.NO_ELIMINADO)
                .setParameter(4, Constantes.NO_ELIMINADO)
                .setParameter(5, Constantes.NO_ELIMINADO)
                .setParameter(6, Constantes.NO_ELIMINADO)
                .setParameter(7, Constantes.NO_ELIMINADO)
                .setParameter(8, usuario)
                .getResultList();

        return castCursoManejoVo(lo);
    }

    
    public List<CursoManejoVo> usuariosCursosActivosByNumCurso(int idCurso) {
        UtilLog4j.log.info("UsuariosCursosActivosByNumCurso");
        StringBuilder sb = new StringBuilder();
        sb.append(queryBase())
                .append("   WHERE c.ELIMINADO = ? and c.NUM_CURSO = ?");

        List<Object[]> lo = em.createNativeQuery(sb.toString())
                .setParameter(1, Constantes.NO_ELIMINADO)
                .setParameter(2, Constantes.TRUE)
                .setParameter(3, Constantes.NO_ELIMINADO)
                .setParameter(4, Constantes.NO_ELIMINADO)
                .setParameter(5, Constantes.NO_ELIMINADO)
                .setParameter(6, Constantes.NO_ELIMINADO)
                .setParameter(7, Constantes.NO_ELIMINADO)
                .setParameter(8, idCurso)
                .getResultList();

        return castCursoManejoVo(lo);
    }

    
    public CursoManejoVo usuariosCursosActivosByidCurso(int idCurso) {
        UtilLog4j.log.info("UsuariosCursosActivosByidCurso");
        StringBuilder sb = new StringBuilder();
        sb.append(queryBase())
                .append("   WHERE c.ELIMINADO = ? and c.id = ?");

        Object[] o = (Object[]) em.createNativeQuery(sb.toString())
                .setParameter(1, Constantes.NO_ELIMINADO)
                .setParameter(2, Constantes.TRUE)
                .setParameter(3, Constantes.NO_ELIMINADO)
                .setParameter(4, Constantes.NO_ELIMINADO)
                .setParameter(5, Constantes.NO_ELIMINADO)
                .setParameter(6, Constantes.NO_ELIMINADO)
                .setParameter(7, Constantes.NO_ELIMINADO)
                .setParameter(8, idCurso)
                .getSingleResult();

        return castCursoManejoVo(o);
    }

    public List<CursoManejoVo> castCursoManejoVo(List<Object[]> lo) {
        List<CursoManejoVo> cursos = new ArrayList<CursoManejoVo>();

        if (lo != null && !lo.isEmpty()) {
            CursoManejoVo c;

            for (Object[] ob : lo) {
                c = new CursoManejoVo();
                c.setIdCursoManejo((Integer) ob[0]);
                c.setFechaExpedicion((Date) ob[1]);
                c.setFechaVencimiento((Date) ob[2]);
                c.setVigente((boolean) ob[3]);
                c.setIdUsuario((String) ob[4]);
                c.setNameUser((String) ob[5]);
                c.setIdSgTipoEspecifico((Integer) ob[6]);
                c.setNumCurso((Integer) ob[10]);
                c.setIdSgOficina((Integer) ob[11]);
                c.setOficina((String) ob[12]);
                c.setIdApCampo((Integer) ob[13]);
                c.setCampo((String) ob[14]);
                c.setGerencia((Integer) ob[15]);

                c.setIdAdjunto((Integer) ob[8]);
                cursos.add(c);
            }

        }

        return cursos;
    }

    public CursoManejoVo castCursoManejoVo(Object[] o) {
        CursoManejoVo c = new CursoManejoVo();

        if (o != null) {

            c = new CursoManejoVo();
            c.setIdCursoManejo((Integer) o[0]);
            c.setFechaExpedicion((Date) o[1]);
            c.setFechaVencimiento((Date) o[2]);
            c.setVigente((o[3].toString().equals(Constantes.BOOLEAN_TRUE)));
            c.setIdUsuario((String) o[4]);
            c.setNameUser((String) o[5]);
            c.setIdSgTipoEspecifico((Integer) o[6]);
            c.setNumCurso((Integer) o[10]);

            c.setIdAdjunto((Integer) o[8]);

        }

        return c;
    }

    
    public List<Object[]> traerUsuarioConCurso() {
        List<Object[]> usuarios = null;

        try {
            StringBuilder sb = new StringBuilder();

            sb.append("SELECT c.id, u.ID, u.NOMBRE from SG_CURSO_MANEJO c\n"
                    + " INNER join USUARIO u on u.ID = c.USUARIO and u.ELIMINADO = ? \n"
                    + " where  c.ELIMINADO = ? and u.interno = ? ");

            usuarios = em.createNativeQuery(sb.toString())
                    .setParameter(1, Constantes.NO_ELIMINADO)
                    .setParameter(2, Constantes.NO_ELIMINADO)
                    .setParameter(3, Constantes.TRUE)
                    .getResultList();

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion los usuarios " + e.getMessage(), e);
        }

        return usuarios;
    }

    
    public void nuevoCursoManejo(Date fechaExpedicion, Date fechaVencimiento, String usuario, int idcurso, String genero) {

        try {
            SgCursoManejo cursoManejo = new SgCursoManejo();
            SgTipoEspecifico especifico = sgTipoEspecificoRemote.find(Constantes.SG_TIPO_ESPECIFICO_EMPLEADO);
            cursoManejo.setEliminado(Constantes.BOOLEAN_FALSE);
            cursoManejo.setFechaExpedicion(fechaExpedicion);
            cursoManejo.setFechaGenero(new Date());
            cursoManejo.setFechaVencimiento(fechaVencimiento);
            cursoManejo.setUsuario(new Usuario(usuario));
            cursoManejo.setGenero(new Usuario(genero));
            cursoManejo.setHoraGenero(new Date());

            if (idcurso > 0) {
                cursoManejo.setNumCurso(idcurso);
            }

            cursoManejo.setSgTipoEspecifico(especifico);
            cursoManejo.setVigente(Constantes.BOOLEAN_TRUE);

            create(cursoManejo);

        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
    }

    
    public void actualizarCurso(int idCurso, String idUsuario, Date fechaExpedicion, Date fechaVencimiento, int numCurso) {
        SgCursoManejo cursoManejo = find(idCurso);

        if (cursoManejo != null) {
            if (fechaVencimiento.after(cursoManejo.getFechaVencimiento())) {
                cursoManejo.setFechaModifico(new Date());
                cursoManejo.setHoraModifico(new Date());
                cursoManejo.setModifico(new Usuario(idUsuario));

                if (numCurso > 0) {
                    cursoManejo.setNumCurso(numCurso);
                }

                cursoManejo.setFechaExpedicion(fechaExpedicion);
                cursoManejo.setFechaVencimiento(fechaVencimiento);
                cursoManejo.setVigente(Constantes.TRUE);

                edit(cursoManejo);
            }

        }

    }

    
    public int cursoExiste(int numCurso, String idUsuario) {
        StringBuilder sb = new StringBuilder();

        int cursos = 0;
        Object[] o;
        sb.append("SELECT c.id FROM SG_CURSO_MANEJO c\n"
                + " INNER join USUARIO u on u.ID = c.USUARIO and u.ELIMINADO = ? \n"
                + " where  c.ELIMINADO = ? and ( u.nombre = ? OR  c.NUM_CURSO = ?) limit 1");
        try {
            cursos = (Integer) em.createNativeQuery(sb.toString())
                    .setParameter(1, Constantes.NO_ELIMINADO)
                    .setParameter(2, Constantes.NO_ELIMINADO)
                    .setParameter(3, idUsuario)
                    .setParameter(4, numCurso)
                    .getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }

        return cursos;
    }

    
    public void quitarVigenciaCursoVencido(int idCurso, String idUsuario) {
        SgCursoManejo cursoManejo = find(idCurso);

        if (cursoManejo != null) {
            cursoManejo.setFechaModifico(new Date());
            cursoManejo.setHoraModifico(new Date());
            cursoManejo.setModifico(new Usuario(idUsuario));
            cursoManejo.setVigente(Constantes.FALSE);

            edit(cursoManejo);
        }
    }

    
    public File crearArchivo(int idCampo, File fileTem) {
        try {

            List<UsuarioVO> usuarios = usuarioRemote.usuariosSinCurso(idCampo);

            if (usuarios != null && !usuarios.isEmpty()) {
                String REPOSITORYPATH = parametroRemote.find(1).getUploadDirectory();

                fileTem = new File(REPOSITORYPATH + "UsuariosTemporal.xlsx");

                //FileInputStream input_document = new FileInputStream(fileTem);
                Workbook workbook = new XSSFWorkbook();
                Sheet pagina = workbook.createSheet("Usuarios");
                pagina.setDefaultColumnWidth(35);

                String[] titulos = {"Nombre", "Campo", "Oficina", "Fecha del Curso", "Fecha de Vencimiento", "Numero tarjeta"};

                Row filaTitulos = pagina.createRow(Constantes.CERO);

                for (int i = 0; i < titulos.length; i++) {
                    Cell celdaTitulo = filaTitulos.createCell(i);
                    celdaTitulo.setCellValue(titulos[i]);
                }

                int count = 1;

                for (UsuarioVO u : usuarios) {
                    Row filaDatos = pagina.createRow(count);
                    Cell celdaDatos = filaDatos.createCell(0);
                    celdaDatos.setCellValue(u.getNombre());
                    celdaDatos = filaDatos.createCell(1);
                    celdaDatos.setCellValue(u.getCampo());
                    celdaDatos = filaDatos.createCell(2);
                    celdaDatos.setCellValue(u.getOficina());
                    count++;
                }

                FileOutputStream file = new FileOutputStream(fileTem);
                workbook.write(file);
                file.close();

            }

        } catch (IOException e) {
            UtilLog4j.log.error(e);
        }

        return fileTem;
    }

    /**
     *
     * @param cursoManejo
     */
    
    public void insertarCursoNuevo(CursoManejoVo vo, String idGenero) {
        try {
            SgCursoManejo cursoManejo = new SgCursoManejo();
            cursoManejo.setUsuario(usuarioRemote.buscarPorNombre(vo.getNameUser()));
            cursoManejo.setFechaExpedicion(vo.getFechaExpedicion());
            cursoManejo.setFechaVencimiento(vo.getFechaVencimiento());

            if (vo.getNumCurso() > 0) {
                cursoManejo.setNumCurso(vo.getNumCurso());
            }

            cursoManejo.setGenero(new Usuario(idGenero));
            cursoManejo.setFechaGenero(new Date());
            cursoManejo.setHoraGenero(new Date());
            cursoManejo.setEliminado(Constantes.FALSE);
            cursoManejo.setVigente(Constantes.TRUE);
            cursoManejo.setSgTipoEspecifico(sgTipoEspecificoRemote.find(Constantes.SG_TIPO_ESPECIFICO_EMPLEADO));
            create(cursoManejo);
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
    }

    public List<CursoManejoVo> validarExisteCurso(List<String> cursos) {
        boolean devuelve = false;
        List<CursoManejoVo> valCursos = new ArrayList<CursoManejoVo>();

        if (!cursos.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            Joiner joiner = Joiner.on(',').skipNulls();
            String user = joiner.join(cursos);
            sb.append(queryBase())
                    .append(" Where u.nombre in (")
                    .append(user)
                    .append(")");
            System.out.println(sb.toString());

            List<Object[]> o = em.createNativeQuery(sb.toString())
                    .setParameter(1, Constantes.NO_ELIMINADO)
                    .setParameter(2, Constantes.TRUE)
                    .setParameter(3, Constantes.NO_ELIMINADO)
                    .setParameter(4, Constantes.NO_ELIMINADO)
                    .setParameter(5, Constantes.NO_ELIMINADO)
                    .setParameter(6, Constantes.NO_ELIMINADO)
                    .getResultList();

        }

        return valCursos;
    }

}
