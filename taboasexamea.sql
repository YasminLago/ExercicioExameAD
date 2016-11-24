alter session set nls_date_format = 'dd.mm.yyyy hh24:mi:ss';

drop table vendas;
drop table clientes;
drop table produtos;

create table produtos(codigop varchar2(3),nomep varchar2(15),prezo integer,stock integer, primary key (codigop));
insert into produtos values ('p1','tornillos',10,1000);
insert into produtos values ('p2','arandelas',20,500);
insert into produtos values ('p3','tuercas',30,100);



create table clientes(codigoc varchar2(3),nomec varchar2(15),direc varchar2(15),gasto integer, primary key (codigoc));
insert into clientes values ('c1','juan','r/burgos',0);
insert into clientes values ('c2','ana','r/urzaiz',0);
insert into clientes values ('c3','luis','r/faisan',0);


create table vendas(codigoc varchar2(3),codigop varchar2(3), data varchar2(10), total integer,  primary key (codigoc,codigop,data),foreign key (codigop) references produtos(codigop), foreign key (codigoc) references clientes(codigoc) );


commit;
select * from clientes;
select * from produtos;
select * from vendas;

/*
@/home/oracle/NetBeansProjects/Examen1ev/taboasexamea

*/
