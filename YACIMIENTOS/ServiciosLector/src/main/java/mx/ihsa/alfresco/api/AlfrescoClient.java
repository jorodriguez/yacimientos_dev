package mx.ihsa.alfresco.api;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.exceptions.CmisContentAlreadyExistsException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import mx.ihsa.alfresco.api.model.ContainerEntry;
import mx.ihsa.alfresco.api.model.ContainerList;
import mx.ihsa.alfresco.api.model.NetworkEntry;
import mx.ihsa.alfresco.api.model.NetworkList;
import mx.ihsa.archivador.DocumentoAnexo;
import mx.ihsa.excepciones.LectorException;
import mx.ihsa.util.UtilLog4j;


/**
 *
 */
public class AlfrescoClient {

    private static final String CMIS_OBJECT_TYPE_ID = "cmis:objectTypeId";
    private static final String CMIS_NAME = "cmis:name";

    private Properties config;

    /**
     * Change these to match your environment
     */
    public static final String CMIS_URL = "/public/cmis/versions/1.1/atom";

    public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    public static final JsonFactory JSON_FACTORY = new JacksonFactory();

    private HttpRequestFactory requestFactory;
    private Session cmisSession;

    public static final String SITES_URL = "/public/alfresco/versions/1/sites/";
    public static final String NODES_URL = "/public/alfresco/versions/1/nodes/";

    private String homeNetwork;

    private final static UtilLog4j LOGGER = UtilLog4j.log;
    
    
    /**
     * Permite subir un archivo al gestor documenta.
     *
     * @param documento El documento que vamos a cargar al repositorio
     * @throws mx.ihsa.excepciones.LectorException
     */
    public void uploadFile(DocumentoAnexo documento) throws LectorException {

        LOGGER.info(this,"*** Uploading file to Alfresco repository ...");
        LOGGER.info(this,"*** Path : {0}, Name : {1}, Type : {2}",
                new Object[]{documento.getRuta(), documento.getNombreBase(), documento.getTipoMime()}
        );

        try {
            // Find the root folder of our target site
            String rootFolderId = getRootFolderId(getSite());

            // Create a new folder in the root folder
            Folder subFolder = createFolder(rootFolderId, documento.getRuta());

            // Create a test document in the subFolder
            createDocument(
                    DocumentParameter.builder()
                            .parentFolder(subFolder)
                            .fileContents(documento.getContenido())
                            .fileName(documento.getNombreBase())
                            .fileType(documento.getTipoMime())
                            .props(null)
                            .build()
            );

        } catch (IOException ex) {
            LOGGER.error(this,"Saving file : {0}", new Object[]{documento.getNombreBase()}, ex);

            throw new LectorException("Error al guardar el archivo " + documento.getNombreBase() + " : " + ex.getMessage());
        }

    }

    /**
     * Devolvemos el URL Atom para acceder al repositorio del gestor documental.
     *
     * @return Una cadena con el URL del repositorio.
     */
    public String getAtomPubURL() {
        String alfrescoAPIUrl = getAlfrescoAPIUrl();
        String atomPubURL;

        try {
            atomPubURL = alfrescoAPIUrl + getHomeNetwork() + CMIS_URL;
        } catch (IOException e) {
            LOGGER.warn(this, e);
            LOGGER.info(this,"Warning: Couldn't determine home network, defaulting to -default-");
            atomPubURL = alfrescoAPIUrl + "-default-" + CMIS_URL;
        }

        return atomPubURL;
    }

    /**
     * Obtiene una sesión CMIS conectándose al servidor Alfresco.
     *
     * @return Session La sesión CMIS
     */
    public Session getCmisSession() {
        if (cmisSession == null) {
            // default factory implementation
            SessionFactory factory = SessionFactoryImpl.newInstance();
            Map<String, String> parameter = new HashMap<>();

            // connection settings
            getRequestFactory();
            parameter.put(SessionParameter.ATOMPUB_URL, getAtomPubURL());
            parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
            parameter.put(SessionParameter.AUTH_HTTP_BASIC, "true");
            parameter.put(SessionParameter.USER, getUsername());
            parameter.put(SessionParameter.PASSWORD, getPassword());
            parameter.put(SessionParameter.OBJECT_FACTORY_CLASS, "org.alfresco.cmis.client.impl.AlfrescoObjectFactoryImpl");

            List<Repository> repositories = factory.getRepositories(parameter);

            cmisSession = repositories.get(0).createSession();
        }
        return this.cmisSession;
    }

    /**
     * Utiliza autenfificación básica para crear una fábrica de solicitudes
     * HTTP.
     *
     * @return HttpRequestFactory
     */
    public HttpRequestFactory getRequestFactory() {
        if (this.requestFactory == null) {
            this.requestFactory = HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                
                public void initialize(HttpRequest request) throws IOException {
                    request.setParser(new JsonObjectParser(new JacksonFactory()));
                    request.getHeaders().setBasicAuthentication(getUsername(), getPassword());
                }
            });
        }
        return this.requestFactory;
    }

    public String getAlfrescoAPIUrl() {
        return config.getProperty("host") + "/api/";
    }

    public String getUsername() {
        return config.getProperty("username");
    }

    public String getPassword() {
        return config.getProperty("password");
    }

    /**
     * Obtiene el acceso al directorio raíz del sitio y agrega un nuevo
     * directorio para posteriormente crear un nuevo documento dentro del mismo.
     *
     * @param parentFolderId El identificador del directorio padre.
     * @param folderName El nombre del folder a crear.
     * @return Folder El directorio recién creado.
     *
     * @author jpotts
     *
     */
    public Folder createFolder(String parentFolderId, String folderName) {
        Session cmis = getCmisSession();
        Folder rootFolder = (Folder) cmis.getObject(parentFolderId);

        Folder subFolder;
        List<String> pathElements = new ArrayList<>();

        // must check if there are path delimiters in the "folderName", if so
        // we should create the required path
        if (folderName != null && folderName.contains("/")) {
            pathElements.addAll(
                    ImmutableSet.copyOf(
                            Splitter.on('/').trimResults().omitEmptyStrings().split(folderName)
                    ).asList()
            );
        }

        StringBuilder basePath = new StringBuilder();
        basePath.append(rootFolder.getPath());
        Folder currentFolder = rootFolder;

        for (String pathElement : pathElements) {
            Folder targetFolder = (Folder) getObject(currentFolder, pathElement);

            if (targetFolder == null) {
                Map<String, Object> props = new HashMap<>();
                props.put(CMIS_OBJECT_TYPE_ID, "cmis:folder");
                props.put(CMIS_NAME, pathElement);
                subFolder = currentFolder.createFolder(props);
                String subFolderId = subFolder.getId();
                LOGGER.info(this,"Created new folder: {0}", new Object[]{subFolderId});
                currentFolder = subFolder;
            } else {
                currentFolder = targetFolder;
            }
        }

        return currentFolder;
    }

    /**
     * Obtiene un objeto CMIS por medio del nombre de un directorio específico.
     *
     * @param parentFolder El directorio padre en donde pudiera existir el
     * objeto.
     * @param objectName El nombre del objeto que estamos buscando
     * @return El objeto CMIS en caso de existir, null en caso contrario.
     */
    private CmisObject getObject(Folder parentFolder, String objectName) {
        CmisObject object = null;

        try {
            StringBuilder path2Object = new StringBuilder(parentFolder.getPath());

            if (path2Object.charAt(path2Object.length() - 1) != '/') {
                path2Object.append('/');
            }

            path2Object.append(objectName);
            object = getCmisSession().getObjectByPath(path2Object.toString());
        } catch (CmisObjectNotFoundException e) {
            LOGGER.debug(this, e.getMessage());
        }

        return object;
    }

    /**
     * Obtiene un objeto a partir de la ruta completa del mismo.
     *
     * @param fullObjectPath La ruta completa del objeto, incluyento su nombre.
     * @return El objeto recuperado si existe, null en caso contrario.
     * @throws LectorException
     */
    public byte[] getObjectFromPath(String fullObjectPath) throws LectorException {
        byte[] retVal = null;
        CmisObject document = null;

        try {
            Folder folder = (Folder) getCmisSession().getObject(getRootFolderId(getSite()));
            document = getObject(folder, fullObjectPath);
        } catch (IOException e) {
            throw new LectorException(e);
        }

        if (document != null) {
            try (InputStream inputStream = ((Document) document).getContentStream().getStream()) {
                retVal = ByteStreams.toByteArray(inputStream);
            } catch (CmisObjectNotFoundException | IOException e) {
                LOGGER.warn(this, "Object not found: {0}", new Object[]{fullObjectPath}, e);
            }
        }

        return retVal;

    }

    /**
     * Elimina un objeto del repositorio a partir de su ruta completa.
     *
     * @param fullObjectPath La ruta completa del objeto, incluyendo su nombre.
     * @return True en caso de poder borrarlo, false en caso contrario.
     */
    public boolean deleteObjectFromPath(String fullObjectPath) {
        boolean retVal = false;

        try {
            Folder folder = (Folder) getCmisSession().getObject(getRootFolderId(getSite()));
            CmisObject document = getObject(folder, fullObjectPath);

            if (document != null) {
                document.delete(true);
                retVal = true;
            }

        } catch (CmisObjectNotFoundException | IOException e) {
            LOGGER.warn(this,"*** While recovering {0}", new Object[]{fullObjectPath}, e);
        }

        return retVal;
    }

    /**
     * Devuelve la red base del gestor documental.
     *
     * @return El identificador de la red.
     * @throws IOException
     */
    public String getHomeNetwork() throws IOException {
        if (this.homeNetwork == null) {
            GenericUrl url = new GenericUrl(getAlfrescoAPIUrl());

            HttpRequest request = getRequestFactory().buildGetRequest(url);

            NetworkList networkList = request.execute().parseAs(NetworkList.class);
            LOGGER.info(this,"Found networks: {0}", new Object[]{networkList.list.pagination.totalItems});

            for (NetworkEntry networkEntry : networkList.list.entries) {
                if (networkEntry.entry.homeNetwork) {
                    this.homeNetwork = networkEntry.entry.id;
                    break;
                }
            }

            if (this.homeNetwork == null) {
                this.homeNetwork = "-default-";
            }

            LOGGER.info(this,"Your home network appears to be: {0}", new Object[]{homeNetwork});
        }

        return this.homeNetwork;
    }

    /**
     * Crea un documento en el repositorio del gestor documental.
     *
     * @param parameterObject El objeto parámetro con los datos para crear el
     * documento
     * @return El documento recién creado en el repositorio.
     * @throws FileNotFoundException
     */
    public Document createDocument(DocumentParameter parameterObject) throws FileNotFoundException {

        Map<String, Object> props = parameterObject.getProps();
        final Session cmis = getCmisSession();
        Document document = null;

        // create a map of properties if one wasn't passed in
        if (props == null) {
            props = new HashMap<>();
        }

        // Add the object type ID if it wasn't already
        if (props.get(CMIS_OBJECT_TYPE_ID) == null) {
            props.put(CMIS_OBJECT_TYPE_ID, "cmis:document");
        }

        // Add the name if it wasn't already
        if (props.get(CMIS_NAME) == null) {
            props.put(CMIS_NAME, parameterObject.getFileName());
        }

        try (ByteArrayInputStream bais = new ByteArrayInputStream(parameterObject.getFileContents())) {

            ContentStream contentStream
                    = cmis.getObjectFactory().
                            createContentStream(
                                    parameterObject.getFileName(),
                                    parameterObject.getFileContents().length,
                                    parameterObject.getFileType(),
                                    bais
                            );

            document = parameterObject.getParentFolder().createDocument(props, contentStream, null);

            LOGGER.debug(this,"Created new document: {0}", new Object[]{document.getId()});

        } catch (CmisContentAlreadyExistsException | IOException ccaee) {
            LOGGER.info(this,"Document already exists: {0}, {1}", new Object[]{parameterObject.getFileName(), ccaee.getMessage()});
            try {
                document
                        = (Document) cmis.getObjectByPath(
                                parameterObject.getParentFolder().getPath()
                                + "/" + parameterObject.getFileName()
                        );
            } catch (Exception e) {
                LOGGER.warn(this,"Error with document {0} - {1}", new Object[]{parameterObject.getFileName(), e});
            }
        }

        return document;
    }

    /**
     * Use the REST API to find the documentLibrary folder for the target site
     *
     * @param site
     * @return root folder id
     * @throws java.io.IOException
     *
     */
    public String getRootFolderId(final String site) throws IOException {

        final GenericUrl containersUrl = new GenericUrl(getAlfrescoAPIUrl()
                + getHomeNetwork()
                + SITES_URL
                + site
                + "/containers");

        LOGGER.info(this, containersUrl.toString());

        final HttpRequest request = getRequestFactory().buildGetRequest(containersUrl);
        final ContainerList containerList = request.execute().parseAs(ContainerList.class);
        String rootFolderId = null;

        for (ContainerEntry containerEntry : containerList.list.entries) {
            if (containerEntry.entry.folderId.equals("documentLibrary")) {
                rootFolderId = containerEntry.entry.id;
                break;
            }
        }
        return rootFolderId;
    }

    /**
     * Use the REST API to "like" an object
     *
     * @param objectId
     * @throws IOException
     */
    public void like(final String objectId) throws IOException {
        final GenericUrl likeUrl = new GenericUrl(getAlfrescoAPIUrl()
                + getHomeNetwork()
                + NODES_URL
                + objectId
                + "/ratings");
        final HttpContent body = new ByteArrayContent("application/json", "{\"id\": \"likes\", \"myRating\": true}".getBytes());
        final HttpRequest request = getRequestFactory().buildPostRequest(likeUrl, body);
        request.execute();
        LOGGER.debug(this,"You liked: {0}", new Object[]{objectId});
    }

    /**
     * Use the REST API to comment on an object
     *
     * @param objectId
     * @param comment
     * @throws IOException
     */
    public void comment(final String objectId, final String comment) throws IOException {
        final GenericUrl commentUrl
                = new GenericUrl(
                        getAlfrescoAPIUrl()
                        + getHomeNetwork()
                        + NODES_URL
                        + objectId
                        + "/comments"
                );
        
        final HttpContent body
                = new ByteArrayContent("application/json", ("{\"content\": \"" + comment + "\"}").getBytes());
        final HttpRequest request = getRequestFactory().buildPostRequest(commentUrl, body);
        request.execute();
        
        LOGGER.debug(this,"You commented on: {0}", new Object[]{objectId});
    }

    public String getSite() {
        return config.getProperty("site");
    }

    public String getFolderName() {
        return config.getProperty("folder_name");
    }

    public void setConfig(Properties config) {
        this.config = config;
    }

}
