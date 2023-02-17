
---------------------------------------------------------------
--------------------------   DROP  ----------------------------
---------------------------------------------------------------

alter table c_cuenta drop column genero;
alter table c_cuenta drop column modifico;

alter table usuario drop column c_cuenta;
alter table usuario drop column c_tipo_contacto;


drop table log_lectura;

drop table c_cuenta;

drop table c_tipo_contacto;

drop table si_adjunto;

drop table usuario;
