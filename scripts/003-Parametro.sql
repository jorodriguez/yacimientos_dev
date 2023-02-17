
--------------------------------------------------------------------------------------
--------------------------- SI_PARAMETRO --------------------------------------------------
--------------------------------------------------------------------------------------


CREATE TABLE si_parametro (
    id serial NOT NULL PRIMARY KEY,    
    upload_directory varchar(100),
    logo bytea,    
    tipo_almacen_adjuntos varchar(3) DEFAULT 'LFS'::bpchar NOT NULL,
    gest_doc_url_base varchar(400),
    gest_doc_prop_adic varchar(512),
    gest_doc_usuario varchar(20),
    gest_doc_clave varchar(50),
    directorio_usuarios varchar(512),
    genero integer not null references usuario(id),
    fecha_genero timestamp not null default current_timestamp,    
    modifico integer references usuario(id),
    fecha_modifico timestamp,    
    eliminado boolean default false    
);


INSERT INTO si_parametro ("id","upload_directory","tipo_almacen_adjuntos","gest_doc_url_base","gest_doc_prop_adic","gest_doc_usuario","gest_doc_clave","directorio_usuarios","genero") 
VALUES (1,'/ets/files/','ALF','http://192.168.254.82/alfresco','{"site":"sia-documentos", "folder_name":"ETS"}','sia','sia123','mpg-ihsa.net',1),
       (2,'/ets/files/','ALF','http://192.168.254.57/alfresco','{"site":"sia-documentos", "folder_name":"ETS"}','sia','sia123',null,1);

