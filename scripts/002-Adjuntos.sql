
--------------------------   0002 ----------------------------
--------------------------   ADJUNTOS ----------------------------


CREATE TABLE si_adjunto (
    id serial not null primary key,
    c_cuenta integer not null references c_cuenta(id),                    
    uuid varchar(64),    
    nombre varchar(1024),
    descripcion varchar(1024),    
    tipo_archivo varchar(75),
    peso varchar(10),
    url text,        
    genero integer not null references usuario(id),
    fecha_genero timestamp not null default current_timestamp,    
    modifico integer references usuario(id),
    fecha_modifico timestamp,    
    eliminado boolean default false
);

--------------------------------------------------------------------------------------
--------------------------- ALTERS --------------------------------------------------
--------------------------------------------------------------------------------------


ALTER TABLE usuario ADD COLUMN SI_ADJUNTO INTEGER REFERENCES SI_ADJUNTO(ID);


--------------------------------------------------------------------------------------
--------------------------- RESTRICCIONES---------------------------------------------
--------------------------------------------------------------------------------------

alter table usuario alter column genero set not null;

