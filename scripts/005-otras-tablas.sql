
-------------------- OTROS --------------------

CREATE TABLE dd_sesion (
    id serial NOT NULL PRIMARY KEY,
    c_cuenta integer not null references c_cuenta(id),                    
    sesion_id varchar(64) NOT NULL,
    fecha_inicio timestamp without time zone DEFAULT now() NOT NULL,
    fecha_fin timestamp without time zone,
    punto_acceso varchar(64) NOT NULL,
    datos_cliente text NOT NULL,    
    genero integer not null references usuario(id),
    fecha_genero timestamp not null default current_timestamp,    
    modifico integer references usuario(id),
    fecha_modifico timestamp,    
    eliminado boolean default false  
);


CREATE TABLE si_modulo (
    id serial NOT NULL PRIMARY KEY,        
    nombre VARCHAR(32),
    ruta VARCHAR(64),    
    icono VARCHAR(128),
    rutaservlet VARCHAR(128),
    tooltip VARCHAR(128),
    extralinkrender TEXT,
    genero integer not null references usuario(id),
    fecha_genero timestamp not null default current_timestamp,    
    modifico integer references usuario(id),
    fecha_modifico timestamp,    
    eliminado boolean default false
);


CREATE TABLE si_opcion (
    id SERIAL NOT NULL PRIMARY KEY,
    si_modulo integer REFERENCES SI_MODULO(ID),    
    nombre VARCHAR(64),
    pagina VARCHAR(256),        
    posicion integer,
    si_opcion integer REFERENCES SI_MODULO(ID),
    paginalistener VARCHAR(256),
    icono VARCHAR(64),
    genero integer not null references usuario(id),
    fecha_genero timestamp not null default current_timestamp,    
    modifico integer references usuario(id),
    fecha_modifico timestamp,    
    eliminado boolean default false
);

CREATE TABLE si_rol (
    id serial NOT NULL PRIMARY KEY,    
    nombre character varying(25),    
    si_modulo integer not null references si_modulo(id),
    codigo character varying(8) DEFAULT ''::character varying NOT NULL,
    genero integer not null references usuario(id),
    fecha_genero timestamp not null default current_timestamp,    
    modifico integer references usuario(id),
    fecha_modifico timestamp,    
    eliminado boolean default false    
);


CREATE TABLE si_rel_rol_opcion (
    id integer NOT NULL PRIMARY KEY,    
    si_rol integer not null references si_rol(id),
    si_opcion integer not null references si_opcion(id),
    acceso_rapido boolean DEFAULT false,    
    genero integer not null references usuario(id),
    fecha_genero timestamp not null default current_timestamp,    
    modifico integer references usuario(id),
    fecha_modifico timestamp,    
    eliminado boolean default false    
);


CREATE TABLE si_usuario_rol (
    id serial NOT NULL PRIMARY KEY,
    usuario integer not null references usuario(id),
    si_rol integer NOT NULL references si_rol(id),        
    c_cuenta integer not null references c_cuenta(id),                    
    genero integer not null references usuario(id),
    fecha_genero timestamp not null default current_timestamp,    
    modifico integer references usuario(id),
    fecha_modifico timestamp,    
    eliminado boolean default false    
);


CREATE TABLE si_plantilla_html (
    id serial NOT NULL PRIMARY KEY,
    c_cuenta integer not null references c_cuenta(id),                    
    nombre VARCHAR(20),
    descripcion VARCHAR(150),
    inicio text,
    fin text,
    genero integer not null references usuario(id),
    fecha_genero timestamp not null default current_timestamp,    
    modifico integer references usuario(id),
    fecha_modifico timestamp,    
    eliminado boolean default false    
);




