package mx.ihsa.util;

import com.google.common.base.Preconditions;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import static mx.ihsa.util.UtilLog4j.log;

/**
 *
 * @author mrojas
 */
public final class Env {

    private static final String REQUIERE_CONTEXTO = "Require Context";

    public static final String USUARIO_ID = "#Usuario_ID";
    public static final String COMPANIA_ID = "#Compania_ID";
    public static final String PROYECTO_ID = "#Proyecto_ID";

    public static final String NOMBRE_USUARIO = "#NombreUsuario";
    public static final String CODIGO_USUARIO = "#CodigoUsuario";
    public static final String NOMBRE_PROYECTO = "#NombreProyecto";
    public static final String NOMBRE_COMPANIA = "#NombreCompania";
    public static final String CODIGO_COMPANIA = "#CodigoCompania";
    public static final String PAIS_COMPANIA = "#PaisCompania";

    public static final String SIS_ADMIN = "#SisAdmin";

    public static final String SESSION_ID = "#Session_ID";
    public static final String LOCALE = "locale";

    public static final String CLIENT_INFO = "#ClientInfo";
    public static final String PUNTO_ENTRADA = "#PuntoEntrada";

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

    private Env() {
    }

    public static String getContext(Properties ctx, String context) {
        Preconditions.checkArgument(ctx != null && context != null, REQUIERE_CONTEXTO);
        return ctx.getProperty(context, "");
    }

    public static int getContextAsInt(Properties ctx, String context) {
        Preconditions.checkArgument(ctx != null && context != null, REQUIERE_CONTEXTO);

        int retVal = 0;

        String s = getContext(ctx, context);

        if (s.length() != 0) {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                log.error("", e);
            }
        }

        return retVal;
    }	//	getContextAsInt

    public static int getDdCompaniaId(Properties ctx) {
        return Env.getContextAsInt(ctx, COMPANIA_ID);
    }

    public static int getDdProyectoId(Properties ctx) {
        return Env.getContextAsInt(ctx, PROYECTO_ID);
    }

    public static int getDdUsuarioId(Properties ctx) {
        return Env.getContextAsInt(ctx, USUARIO_ID);
    }

    public static boolean isSisAdmin(Properties ctx) {
        return "true".equals(Env.getContext(ctx, SIS_ADMIN));
    }

    public static String getClientInfo(Properties ctx) {
        return Env.getContext(ctx, CLIENT_INFO);
    }

    public static String getPuntoEntrada(Properties ctx) {
        return Env.getContext(ctx, PUNTO_ENTRADA);
    }

    /**
     * Set Global Context to Value
     *
     * @param ctx context
     * @param context context key
     * @param value context value
     */
    public static void setContext(Properties ctx, String context, String value) {
        Preconditions.checkArgument(ctx != null && context != null, REQUIERE_CONTEXTO);

        if (value == null || value.length() == 0) {
            ctx.remove(context);
        } else {
            ctx.setProperty(context, value);
        }
    }

    public static void setContext(Properties ctx, String context, Object value) {
        Preconditions.checkArgument(ctx != null, REQUIERE_CONTEXTO);

        if (value == null) {
            ctx.remove(context);
        } else {
            ctx.setProperty(context, value.toString());
        }
    }

    /**
     * Set Global Context to Value
     *
     * @param ctx context
     * @param context context key
     * @param value context value
     */
    public static void setContext(Properties ctx, String context, LocalDateTime value) {
        Preconditions.checkArgument(ctx != null && context != null, "");

        if (value == null) {
            ctx.remove(context);
        } else {	//	JDBC Format	2005-05-09 00:00:00.0
            ctx.setProperty(context, LocalDateTime.now().format(formatter));

        }
    }

    /**
     * Set Global Context to (int) Value
     *
     * @param ctx context
     * @param context context key
     * @param value context value
     */
    public static void setContext(Properties ctx, String context, int value) {
        Preconditions.checkArgument(ctx != null && context != null, REQUIERE_CONTEXTO);
        ctx.setProperty(context, String.valueOf(value));
    }

    /**
     * Set Global Context to true/false
     *
     * @param ctx context
     * @param context context key
     * @param value context value
     */
    public static void setContext(Properties ctx, String context, boolean value) {
        setContext(ctx, context, String.valueOf(value));
    }

    public static void removeContext(Properties ctx, String context) {
        if (ctx.contains(ctx)) {
            ctx.remove(context);
        }
    }

}
