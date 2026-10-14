drop procedure if exists sp_create_trabajo_completo;

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
    declare v_id_cliente varchar(36);
    declare v_id_carro varchar(36);
    declare exit handler for sqlexception
    begin
        rollback;
        resignal;
    end;

    start transaction;

    set v_id_carro = (select id_carro from Carro where placa = placa_p limit 1);

    if v_id_carro is null then
        set v_id_cliente = uuid();
        insert into Cliente(id_cliente, nombre, telefono, direccion)
        values (v_id_cliente, nombre_cliente_p, telefono_p, direccion_p);

        set v_id_carro = uuid();
        insert into Carro(id_carro, placa, marca, modelo, anio, color, id_cliente)
        values (v_id_carro, placa_p, marca_p, modelo_p, anio_p, color_p, v_id_cliente);
    end if;

    insert into TrabajoARealizar(id_trabajo, descripcion, fecha_ingreso, trabajo_status, costo, id_carro)
    values (uuid(), descripcion_p, fecha_ingreso_p, true, costo_estimado_p, v_id_carro);

    commit;
end;