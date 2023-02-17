

--------------------------------------------------------------------------------------
---------------------------DATA------------------------------------------------------
--------------------------------------------------------------------------------------

insert into c_cuenta(id,nombre,descripcion) values(1,'DEFAULT','Usuario de sistema');

insert into c_tipo_contacto(id,nombre) values(1,'Admin'), (2,'CONTACTO')

--adjunto credencial

insert into usuario(id,c_cuenta,c_tipo_contacto,nombre,email,clave,telefono,fecha_nacimiento,estado,municipio,seccion,localidad,emision,vigencia,registro,sexo,genero)
values(1,1,1,'SISTEMA','joel.rod.roj@hotmail.com','1','8110208406','1986-07-13'::date,19,'026','0647','0001',2017,2027,1,'H',1);



