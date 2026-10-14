-- =====================================================================
-- Base de datos de la versión WEB del taller.
-- Se puede ejecutar varias veces: NO borra datos existentes.
-- Los usuarios base (administrador y empleado) los crea la aplicación al
-- iniciar, con los datos de application.properties (contraseña con BCrypt).
-- =====================================================================
create database if not exists sistema_taller_electromecanico;
use sistema_taller_electromecanico;

create table if not exists Usuario(
    nombre varchar(70) not null check(char_length(nombre) <= 70),
    usuario varchar(70) not null unique check(char_length(usuario) <= 70),
    password varchar(70) not null check(char_length(password) <= 70),
    rol varchar(20) not null check(rol in ('ADMINISTRADOR', 'EMPLEADO')),
    user_status boolean not null default true,
    id_usuario varchar(36) not null,
    constraint pk_usuario primary key (id_usuario)
);

create table if not exists Cliente(
    nombre_cliente varchar(100) not null check(char_length(nombre_cliente) <= 100),
    telefono varchar(20) not null check(char_length(telefono) <= 20),
    direccion varchar(150) null check(char_length(direccion) <= 150),
    id_cliente int not null auto_increment,
    constraint pk_cliente primary key (id_cliente)
);

create table if not exists Carro(
    placa varchar(15) not null unique check(char_length(placa) <= 15),
    marca varchar(50) not null check(char_length(marca) <= 50),
    modelo varchar(50) not null check(char_length(modelo) <= 50),
    anio int not null check(anio between 1900 and 2100),
    color varchar(30) null check(char_length(color) <= 30),
    id_cliente int not null,
    constraint fk_carro_cliente
        foreign key (id_cliente) references Cliente(id_cliente)
        on delete restrict
        on update cascade,
    id_carro int not null auto_increment,
    constraint pk_carro primary key (id_carro)
);

create table if not exists TrabajoARealizar(
    descripcion varchar(255) not null check(char_length(descripcion) <= 255),
    fecha_ingreso date not null default (curdate()),
    estado varchar(20) not null default 'Pendiente'
        check(estado in ('Pendiente', 'En proceso', 'Finalizado', 'Entregado')),
    costo_estimado decimal(10,2) not null default 0.00 check(costo_estimado >= 0),
    id_carro int not null,
    constraint fk_trabajo_carro
        foreign key (id_carro) references Carro(id_carro)
        on delete restrict
        on update cascade,
    id_trabajo int not null auto_increment,
    constraint pk_trabajo primary key (id_trabajo)
);

drop procedure if exists sp_login_usuario; -- ya no se usa: la web valida con BCrypt

create or replace view view_read_trabajo as
	select
		t.id_trabajo as ID,
		t.descripcion as Trabajo,
		t.fecha_ingreso as Ingreso,
		t.estado as Estado,
		t.costo_estimado as Costo,
		t.id_carro as id_carro,
		ca.placa as Placa,
		ca.marca as Marca,
		ca.modelo as Modelo,
		ca.anio as Anio,
		ca.color as Color,
		cl.id_cliente as id_cliente,
		cl.nombre_cliente as Cliente,
		cl.telefono as Telefono,
		cl.direccion as Direccion
	from TrabajoARealizar t
		inner join Carro ca
			on t.id_carro = ca.id_carro
		inner join Cliente cl
			on ca.id_cliente = cl.id_cliente;

-- Registra cliente + carro + trabajo en una sola operacion.
-- Si la placa ya existe, se reutiliza ese carro (y su cliente) y solo se agrega el trabajo.
drop procedure if exists sp_create_trabajo_completo;
delimiter $$
	create procedure sp_create_trabajo_completo(
		in nombre_cliente_p varchar(100),
		in telefono_p varchar(20),
		in direccion_p varchar(150),
		in placa_p varchar(15),
		in marca_p varchar(50),
		in modelo_p varchar(50),
		in anio_p int,
		in color_p varchar(30),
		in descripcion_p varchar(255),
		in fecha_ingreso_p date,
		in estado_p varchar(20),
		in costo_estimado_p decimal(10,2)
	)
	begin
		declare v_id_cliente int;
		declare v_id_carro int;
		declare exit handler for sqlexception
		begin
			rollback;
			resignal;
		end;

		start transaction;

		set v_id_carro = (select id_carro from Carro where placa = placa_p limit 1);

		if v_id_carro is null then
			insert into Cliente(nombre_cliente, telefono, direccion)
			values (nombre_cliente_p, telefono_p, direccion_p);
			set v_id_cliente = last_insert_id();

			insert into Carro(placa, marca, modelo, anio, color, id_cliente)
			values (placa_p, marca_p, modelo_p, anio_p, color_p, v_id_cliente);
			set v_id_carro = last_insert_id();
		end if;

		insert into TrabajoARealizar(descripcion, fecha_ingreso, estado, costo_estimado, id_carro)
		values (descripcion_p, fecha_ingreso_p, estado_p, costo_estimado_p, v_id_carro);

		commit;
	end$$
delimiter ;

-- Actualiza trabajo + carro + cliente asociados al trabajo.
drop procedure if exists sp_update_trabajo_completo;
delimiter $$
	create procedure sp_update_trabajo_completo(
		in id_trabajo_p int,
		in nombre_cliente_p varchar(100),
		in telefono_p varchar(20),
		in direccion_p varchar(150),
		in placa_p varchar(15),
		in marca_p varchar(50),
		in modelo_p varchar(50),
		in anio_p int,
		in color_p varchar(30),
		in descripcion_p varchar(255),
		in fecha_ingreso_p date,
		in estado_p varchar(20),
		in costo_estimado_p decimal(10,2)
	)
	begin
		declare v_id_cliente int;
		declare v_id_carro int;
		declare exit handler for sqlexception
		begin
			rollback;
			resignal;
		end;

		start transaction;

		select t.id_carro, ca.id_cliente
		into v_id_carro, v_id_cliente
		from TrabajoARealizar t
			inner join Carro ca
				on t.id_carro = ca.id_carro
		where t.id_trabajo = id_trabajo_p;

		update Cliente
		set nombre_cliente = nombre_cliente_p,
			telefono = telefono_p,
			direccion = direccion_p
		where id_cliente = v_id_cliente;

		update Carro
		set placa = placa_p,
			marca = marca_p,
			modelo = modelo_p,
			anio = anio_p,
			color = color_p
		where id_carro = v_id_carro;

		update TrabajoARealizar
		set descripcion = descripcion_p,
			fecha_ingreso = fecha_ingreso_p,
			estado = estado_p,
			costo_estimado = costo_estimado_p
		where id_trabajo = id_trabajo_p;

		commit;
	end$$
delimiter ;

-- Elimina solo el trabajo; el cliente y el carro se conservan como historial.
drop procedure if exists sp_delete_trabajo;
delimiter $$
	create procedure sp_delete_trabajo(
		in id_trabajo_p int
	)
	begin
		delete from TrabajoARealizar where id_trabajo = id_trabajo_p;
	end$$
delimiter ;
