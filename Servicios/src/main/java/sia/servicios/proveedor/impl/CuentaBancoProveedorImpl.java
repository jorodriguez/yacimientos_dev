/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.servicios.proveedor.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import sia.constantes.Constantes;
import sia.modelo.CuentaBancoProveedor;
import sia.modelo.Moneda;
import sia.modelo.Proveedor;
import sia.modelo.Usuario;
import sia.modelo.proveedor.Vo.CuentaBancoVO;
import sia.modelo.sistema.AbstractFacade;
import sia.servicios.catalogos.impl.MonedaImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@LocalBean 
public class CuentaBancoProveedorImpl extends AbstractFacade<CuentaBancoProveedor> {

    @PersistenceContext(unitName = "Sia-ServiciosPU")
    private EntityManager em;

    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CuentaBancoProveedorImpl() {
        super(CuentaBancoProveedor.class);
    }
    @Inject
    private MonedaImpl monedaServicioRemoto;
    @Inject
    private ProveedorServicioImpl proveedorServicioRemoto;

    
    public List<CuentaBancoProveedor> traerCuenta(String rfc) {
        return em.createQuery("SELECT f FROM CuentaBancoProveedor f WHERE f.proveedor.rfc = :rfc").setParameter("rfc", rfc).getResultList();
    }

    
    public void guardarModificacionCuenta(CuentaBancoProveedor cuentaBancoProveedor, int moneda) {
        cuentaBancoProveedor.setMoneda(this.monedaServicioRemoto.find(moneda));
        this.edit(cuentaBancoProveedor);
    }

    
    public void guardarCuenta(int idP, CuentaBancoProveedor cuentaBancoProveedor, int moneda) {
        cuentaBancoProveedor.setProveedor(this.proveedorServicioRemoto.find(idP));
        cuentaBancoProveedor.setMoneda(this.monedaServicioRemoto.find(moneda));
        this.create(cuentaBancoProveedor);
    }

    
    public List<CuentaBancoVO> traerCuentas(int idProveedor, String compania) {
        List<CuentaBancoVO> lst = new ArrayList<CuentaBancoVO>();
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" select a.ID, a.PROVEEDOR, a.CUENTA, a.BANCO, a.MONEDA, a.CLABE, a.PLAZA, a.NUMERO_PLAZA, a.SUCURSAL, a.NUMERO_SUCURSAL, a.ELIMINADO, a.NACIONAL, a.SWIFT, a.ABA, m.NOMBRE ");
            //                   0         1          2         3        4         5         6          7             8              9                10           11        12      13      14                                  
            sb.append(" from CUENTA_BANCO_PROVEEDOR a ");
            sb.append(" inner join PV_PROVEEDOR_COMPANIA pc on pc.PROVEEDOR = a.PROVEEDOR and pc.ELIMINADO = 'False' and pc.COMPANIA = '").append(compania).append("' ");
            sb.append(" inner join MONEDA m on m.id = a.MONEDA and m.ELIMINADO = 'False' and m.COMPANIA = '").append(compania).append("' ");
            sb.append(" where a.PROVEEDOR =  ").append(idProveedor);
            sb.append(" and a.ELIMINADO = 'False' ");

            List<Object[]> lista = em.createNativeQuery(sb.toString()).getResultList();
            for (Object[] o : lista) {
                if (o != null) {
                    lst.add(castCuenta(o));
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
        }
        return lst;
    }

    
    public CuentaBancoVO traerCuenta(int idCuenta) {
        CuentaBancoVO vo = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(" select a.ID, a.PROVEEDOR, a.CUENTA, a.BANCO, a.MONEDA, a.CLABE, a.PLAZA, a.NUMERO_PLAZA, a.SUCURSAL, a.NUMERO_SUCURSAL, a.ELIMINADO, a.NACIONAL, a.SWIFT, a.ABA, m.NOMBRE ");
            //                   0         1          2         3        4         5         6          7             8              9                10           11        12      13      14                                  
            sb.append(" from CUENTA_BANCO_PROVEEDOR a ");
            sb.append(" inner join MONEDA m on m.id = a.MONEDA and m.ELIMINADO = 'False' ");
            sb.append(" where a.ID =  ").append(idCuenta);
            sb.append(" and a.ELIMINADO = 'False' ");

            Object[] obj = (Object[]) em.createNativeQuery(sb.toString()).getSingleResult();

            if (obj != null) {
                vo = castCuenta(obj);

            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
        }
        return vo;
    }

    private CuentaBancoVO castCuenta(Object[] objects) {
        CuentaBancoVO cuenta = new CuentaBancoVO();
        cuenta.setIdCuentaBanco((Integer) objects[0]);
        cuenta.setIdProveedor((Integer) objects[1]);
        cuenta.setBanco((String) objects[3]);
        cuenta.setCuenta((String) objects[2]);
        cuenta.setMoneda((String) objects[14]);
        cuenta.setIdMoneda((Integer) objects[4]);
        cuenta.setClabe((String) objects[5]);
        cuenta.setSwift((String) objects[12]);
        cuenta.setAba((String) objects[13]);
        return cuenta;
    }

    
    public void guardar(int idProveedor, List<CuentaBancoVO> cuentas, String sesion, String empresa) {
        try {
            for (CuentaBancoVO cuenta : cuentas) {
                if (!cuenta.getBanco().isEmpty()) {
                    CuentaBancoProveedor cuentaBancoProveedor = buscarPorProveedorCuenta(idProveedor, cuenta.getCuenta());
                    if (cuentaBancoProveedor == null) {
                        cuentaBancoProveedor = new CuentaBancoProveedor();
                    cuentaBancoProveedor.setProveedor(new Proveedor(idProveedor));
                        cuentaBancoProveedor.setGenero(new Usuario(sesion));
                        cuentaBancoProveedor.setFechaGenero(new Date());
                        cuentaBancoProveedor.setHoraGenero(new Date());
                    } else {
                        cuentaBancoProveedor.setModifico(new Usuario(sesion));
                        cuentaBancoProveedor.setFechaModifico(new Date());
                        cuentaBancoProveedor.setHoraModifico(new Date());
                    }
                    cuentaBancoProveedor.setBanco(cuenta.getBanco());
                    cuentaBancoProveedor.setCuenta(cuenta.getCuenta());
                    cuentaBancoProveedor.setClabe(cuenta.getClabe());
                    if (!cuenta.getClabe().isEmpty()) {
                        cuentaBancoProveedor.setMoneda(new Moneda(Constantes.UNO, empresa));
                    } else {
                        cuentaBancoProveedor.setMoneda(new Moneda(Constantes.DOS, empresa));
                    }
                    cuentaBancoProveedor.setSwift(cuenta.getSwift());
                    cuentaBancoProveedor.setAba(cuenta.getAba());

                    cuentaBancoProveedor.setEliminado(Constantes.NO_ELIMINADO);
                    edit(cuentaBancoProveedor);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
    }

    private CuentaBancoProveedor buscarPorProveedorCuenta(int proveedor, String cuenta) {
        try {
            String c = "select * from cuenta_banco_proveedor  where proveedor = ?1 and cuenta = ?2 and eliminado  = 'False' ";
            Query q = em.createNativeQuery(c, CuentaBancoProveedor.class);
            q.setParameter(1, proveedor);
            q.setParameter(2, cuenta);
            return (CuentaBancoProveedor) q.getSingleResult();
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return null;
        }
    }

}
