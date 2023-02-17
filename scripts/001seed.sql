

CREATE DATABASE CONTACTOS;


CREATE TABLE c_cuenta (
    id SERIAL NOT NULL primary key,
    nombre varchar(128) not null,    
    descripcion text,
    logo text,
    genero integer,
    fecha_genero timestamp not null default current_timestamp,    
    modifico integer,
    fecha_modifico timestamp,    
    eliminado boolean default false
);


CREATE TABLE c_tipo_contacto (
    id SERIAL NOT NULL primary key,
    nombre varchar(128) not null,    
    genero integer,
    fecha_genero timestamp not null default current_timestamp,    
    modifico integer,
    fecha_modifico timestamp,    
    eliminado boolean default false
);

CREATE TABLE usuario (
    id SERIAL NOT NULL primary key,
    c_cuenta integer not null references c_cuenta(id),    
    c_tipo_contacto integer not null references c_tipo_contacto(id),    
    nombre varchar(128) not null,        
    email varchar(120) not null, 
    clave varchar(80) not null,   
    telefono  varchar(25) not null,        
    fecha_nacimiento Date not null ,
    domicilio text,        
    curp varchar(20), 
    foto text,         
    anio_registro varchar(12),      
    anio_emision integer,          
    estado integer not null,
    municipio varchar(10) not null,
    seccion varchar(10) not null,
    localidad varchar(10) not null,
    emision integer not null,
    vigencia integer not null,
    registro integer not null references usuario(id),
    genero integer not null references usuario(id),
    sexo varchar(1) CHECK (sexo = 'M' or sexo = 'H') not null,        
    fecha_genero timestamp not null default current_timestamp,        
    modifico integer references usuario(id),
    fecha_modifico timestamp,        
    eliminado boolean default false
);


CREATE TABLE log_lectura (
    id SERIAL NOT NULL primary key, 
    foto text,
    usuario integer not null references usuario(id),
    c_cuenta integer not null references c_cuenta(id),    
    lectura text not null,
    genero integer not null references usuario(id),
    fecha_genero timestamp not null default current_timestamp,    
    modifico integer references usuario(id),
    fecha_modifico timestamp,    
    eliminado boolean default false
);

--------------------------------------------------------------------------------------
--------------------------- PREDATA ---------------------------------------------
--------------------------------------------------------------------------------------



insert into c_cuenta(id,nombre,descripcion) values(1,'DEFAULT','Usuario de sistema');

insert into c_tipo_contacto(id,nombre) values(1,'Admin'), (2,'CONTACTO')

--adjunto credencial

insert into usuario(id,c_cuenta,c_tipo_contacto,nombre,email,clave,telefono,fecha_nacimiento,estado,municipio,seccion,localidad,emision,vigencia,registro,sexo,genero)
values(1,1,1,'SISTEMA','joel.rod.roj@hotmail.com','1','8110208406','1986-07-13'::date,19,'026','0647','0001',2017,2027,1,'H',1);


update c_cuenta set genero = 1;
update c_tipo_contacto set genero = 1;


--------------------------------------------------------------------------------------
--------------------------- RESTRICCIONES---------------------------------------------
--------------------------------------------------------------------------------------



alter table c_cuenta alter column genero set not null;

ALTER TABLE c_cuenta ADD CONSTRAINT fk_cuenta_genero FOREIGN KEY (genero) REFERENCES usuario(id);
ALTER TABLE c_cuenta ADD CONSTRAINT fk_cuenta_modifico FOREIGN KEY (modifico) REFERENCES usuario(id);

ALTER TABLE c_tipo_contacto ADD CONSTRAINT fk_tipo_contacto_genero FOREIGN KEY (genero) REFERENCES usuario(id);
ALTER TABLE c_tipo_contacto ADD CONSTRAINT fk_tipo_contacto_modifico FOREIGN KEY (modifico) REFERENCES usuario(id);

alter table c_cuenta alter column genero set not null;
alter table c_tipo_contacto alter column genero set not null;

