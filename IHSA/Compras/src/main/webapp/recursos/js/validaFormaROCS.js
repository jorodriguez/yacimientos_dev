/* global dialogoDevolverVariasOCS */

var rfcIhsa = 'IHI070320FI3';
var rfcOilServ = 'OIL140522MA0';
var rfcPetroIn = 'PET140522TK8';
var mensajeNoSeleccion = 'Es necesario seleccionar al menos un elemento de la lista.';
function validarPopUpAF(forma, control, msg, msg2) {
    if (!validarCajaTexto(forma + "\\:" + control)) {
        return confirmar(forma, msg);
    }
    bootbox.alert(msg2);
    return false;
}

function validarPopUpPS(forma, control, msg, msg2) {
    if (this.validarCombo(forma + "\\:" + control, 0)) {
        return confirmar(forma, msg);
    }
    bootbox.alert(msg2);
    return false;
}

function resetTipoRequi(forma) {
    this.setValorCombo(forma + "\\:idGerencia", 0);
//$("#" + forma).submit();
}

function resetComboGerencia(forma) {
    this.setValorCombo(forma + "\\:idProyectoOt", 0);
//$("#" + forma).submit();
}

function setValorCombo(formYid, valor) {
    var se = jQuery("#" + formYid);
    se.val(valor);
}

function validaRequisicion(forma) {
    var e = 0;
    var tipo = jQuery("input[name$='" + forma + "\\:tipoReq']':checked").val();
    //bootbox.alert(tipo);
    var retorno = false;
    if (this.validarCombo(forma + "\\:idGerencia", 0)) {
        if (this.validarCombo(forma + "\\:idProyectoOt", 0)) {
            if (tipo == "PS") {
                if (this.validarCombo(forma + "\\:unidaCosto", 0)) {
                    if (this.validarCombo(forma + "\\:userRevisa", 'revisa')) {
                        if (this.validarCombo(forma + "\\:userAAprueba", 'aprueba')) {
                            $("#mensajeCombo").text("");
                        } else {
                            bootbox.alert("Por favor seleccione quien aprobará la requisición");
                            $("#mensajeCombo").text("Por favor seleccione quien aprobará la requisición");
                            e++;
                        }
                    } else {
                        bootbox.alert("Por favor seleccione a quien revisará la requisición");
                        $("#mensajeCombo").text("Por favor seleccione quien revisará la requisición");
                        e++;
                    }
                } else {
                    bootbox.alert("Por favor seleccione el tipo de tarea");
                    $("#mensajeCombo").text("Por favor seleccione el tipo de tarea");
                    e++;
                }
            } else {
                if (this.validarCombo(forma + "\\:userRevisa", 'revisa')) {
                    if (this.validarCombo(forma + "\\:userAAprueba", 'aprueba')) {
                        $("#mensajeCombo").text("");
                    } else {
                        bootbox.alert("Por favor seleccione a quien aprobará la requisición");
                        $("#mensajeCombo").text("Por favor seleccione quien aprobará la requisición");
                        e++;
                    }
                } else {
                    bootbox.alert("Por favor seleccione a quien revisará la requisición");
                    $("#mensajeCombo").text("Por favor seleccione quien revisará la requisición");
                    e++;
                }
            }
        } else {
            bootbox.alert("Por favor seleccione el proyecto OT");
            $("#mensajeCombo").text("Por favor seleccione el proyecto OT");
            e++;
        }
    } else {
        bootbox.alert("Por favor seleccione la gerencia");
        $("#mensajeCombo").text("Por favor seleccione la gerencia");
        e++;
    }


    if (e == 0) {
        retorno = true;
    }
    return retorno;
}

function validaNotasOCS(forma) {
    var e = 0;
    var retorno = false;
    //bootbox.alert(tipo);
    //    if (!validarCajaTexto(forma +"\\:titulo")) {
    if (!validarCajaTexto(forma + "\\:nota")) {
        $("#mensajeCombo").text("");
        e = 0;
    } else {
        e++;
        bootbox.alert("Es necesario agregar una nota.");
    }
//    }else{
//        e++;
//        bootbox.alert("Es necesario agregar un titulo.");
//    }
    if (e == 0) {
        retorno = true;
    }
    return retorno;
}

function validaItemOCS(forma, tipo) {
    var e = 0;
    var retorno = false;
    if ((tipo == 'PS' && this.validarCombo(forma + "\\:nombreTipoObra", 0) || tipo != 'PS')) {
        if (($("#" + forma + "\\:descripcion").length > 0 && !validarCajaTexto(forma + "\\:descripcion")) || $("#" + forma + "\\:descripcion").length < 1) {
            if (($("#" + forma + "\\:precioUnitario").length > 0 && !validarCajaTexto(forma + "\\:precioUnitario")) || $("#" + forma + "\\:precioUnitario").length < 1) {
                if (($("#" + forma + "\\:precioUnitario").length > 0 && validarCajaTextoMontos(forma + "\\:precioUnitario")) || $("#" + forma + "\\:precioUnitario").length < 1) {
                    if (($("#" + forma + "\\:cantidad").length > 0 && !validarCajaTexto(forma + "\\:cantidad")) || $("#" + forma + "\\:cantidad").length < 1) {
                        if (($("#" + forma + "\\:cantidad").length > 0 && validarCajaTextoCantidad(forma + "\\:cantidad")) || $("#" + forma + "\\:cantidad").length < 1) {
                            if (($("#" + forma + "\\:cantidad").val() * 1) > 0) {
                                $("#mensajeCombo").text("");
                                e = 0;
                            } else {
                                e++;
                                bootbox.alert("La cantidad debe ser mayor a cero.");
                            }
                        } else {
                            e++;
                            bootbox.alert("La cantidad es incorrecta, sólo se aceptan dígitos y un punto decimal.");
                        }
                    } else {
                        e++;
                        bootbox.alert("Es necesario agregar la cantidad");
                    }
                } else {
                    e++;
                    bootbox.alert("El precio unitario es incorrecto, sólo se acepta dígitos y un punto decimal.");
                }
            } else {
                e++;
                bootbox.alert("Es necesario agregar el precio unitario");
            }
        } else {
            e++;
            bootbox.alert("Es necesario agregar la descripción.");
        }
    } else {
        bootbox.alert("Por favor seleccione la tarea");
        $("#mensajeCombo").text("Por favor seleccione la tarea.");
        e++;
    }
    if (e == 0) {
        retorno = true;
    }
    return retorno;
}

function validaItemReqProyOT(forma, tipoReq, tipoApc) {
    if (validaItemReq(forma, tipoReq, tipoApc)) {
        if (tipoApc == 'C' && !this.validarCombo(forma + "\\:idProyectoOtDt", -2)) {
            $(".todosMM").prop("checked", "");
            mostrarDiv('panelCrearArticuloMulti');
            ocultarDiv('panelCrearArticuloReq');
            ocultarDiv('btnfrmItemReq');
            mostrarDiv('btnfrmItemReqCont');
            return false;
        } else {
            return true;
        }
    } else {
        return false;
    }
}

function validaItemReq(forma, tipoReq, tipoApc) {
    var e = 0;
    var retorno = false;
    if ((tipoApc == 'C' && this.validarCombo(forma + "\\:idActividad", 0)) || (tipoApc == 'N')) {
        if ((tipoApc == 'C' && this.validarCombo(forma + "\\:idProyectoOtDt", 0)) || (tipoApc == 'N')) {
            if ((tipoApc == 'C' && this.validarCombo(forma + "\\:unidaCostoDtll", 0)) || (tipoApc == 'N')) {
                if ((tipoApc == 'C' && this.validarCombo(forma + "\\:nombreTipoObraDt", 0)) || (tipoApc == 'N' && tipoReq == 'PS' && this.validarCombo(forma + "\\:nombreTipoObraDt", 0)) || (tipoApc == 'N' && tipoReq == 'AF')) {
                    if ((tipoApc == 'C' && ((this.validarCombo(forma + "\\:idProyectoOtDt", -2) && this.validarCombo(forma + "\\:centroCostoID", -1)) || (!this.validarCombo(forma + "\\:idProyectoOtDt", -2) && this.validarCombo(forma + "\\:centroCosto2ID", -1)))) || (tipoApc == 'N')) {
                        if (($("#" + forma + "\\:cantidad").length > 0 && validarCajaTextoCantidad(forma + "\\:cantidad")) || $("#" + forma + "\\:cantidad").length < 1) {
                            if (($("#" + forma + "\\:cantidad").val() * 1) > 0) {
                                $("#mensajeCombo").text("");
                                e = 0;
                            } else {
                                e++;
                                bootbox.alert("La cantidad debe ser mayor a cero.");
                            }
                        } else {
                            e++;
                            bootbox.alert("La cantidad es incorrecta, sólo se aceptan dígitos y un punto decimal.");
                        }
                    } else {
                        e++;
                        bootbox.alert("Por favor seleccione una Subtarea.");
                    }
                } else {
                    e++;
                    bootbox.alert("Por favor seleccione una Tarea.");
                }
            } else {
                e++;
                bootbox.alert("Por favor seleccione una Subactividad.");
            }
        } else {
            e++;
            bootbox.alert("Por favor seleccione un Proyecto OT.");
        }
    } else {
        bootbox.alert("Por favor seleccione una Actividad.");
        e++;
    }
    if (e == 0) {
        retorno = true;
    }
    return retorno;
}

function validarBuscarHistorial(filtro) {
    var e = 0
    var v;
    var val = false;
    if (filtro == 'filtro') {
        if (validarCajaTexto("form1\\:autocomplete")) {
            $("#form1\\:hidenDes").val(-1);
        }
        if (validarCombo("form1\\:cmbStatus", -1)) {
            v = $("#form1\\:cmbStatus option:selected");
            $("#form1\\:dataTableRequisicionesSolicitadas\\:estatusRequisicion").text(v.text());
        }
        if (!validarCajaTexto("form1\\:inicio")) {
            if (validarCajaTexto("form1\\:fin")) {
                bootbox.alert("Seleccione la fecha de fin");
                e++;
            }
        } else {
            bootbox.alert("Seleccione la fecha inicio");
            e++;
        }
    } else {
        e = 0;
    }
    if (e == 0) {
        val = true;
    }
    return val;
}

function validarBuscarHistorialOrden() {
    var v;
    if (validarCajaTexto("form1\\:autocomplete")) {
        $("#form1\\:hidenDes").val(-1);
    }
    if (validarCajaTexto("form1\\:autocompleteProveedor")) {
        $("#form1\\:hidenDesProveedor").val(-1);
    }
    if (validarCombo("form1\\:cmbStatus", -1)) {
        v = $("#form1\\:cmbStatus option:selected");
        $("#form1\\:dataTableOrdenesSolicitadas\\:estatusOrden").text(v.text());
    }
    return true;
}
function validarCajaTexto(idComponente) {
    var texto = $("#" + idComponente).val();
    texto = texto.trim();
    if (texto == "" || texto == undefined || texto == null) {
        return true;
    } else {
        return false;
    }
}

function validarCajaTextoMontos(idComponente) {
    var patt = /^[-+]?\d+(\.\d{1,8})?$/;
    return patt.test($("#" + idComponente).val());
}

function validarCajaTextoCantidad(idComponente) {
    var patt = /^\d+(\.\d{1,8})?$/;
    return patt.test($("#" + idComponente).val());
}

function validarCombo(formYid, valorAComparar) {
//oficinaOrigen
    var se = jQuery("#" + formYid + " option:selected");
    if (se.val() != valorAComparar) {
        return true;
    } else {
        return false;
    }
}

function validaSolcitaOrden(rfcCompania) {
    var e = 0;
    var retorno = false;
    var sel = 0;
    $(".seleccion").each(function () {
        if ($(this).is(':checked')) {
            sel++;
        }
    });
    /*
     if(sel > 0){
     bootbox.alert("Seleccion si");
     }else{
     bootbox.alert("Seleccion NO");
     }
     */
    if (sel > 0) {
        if (validarCombo("form1\\:cmbRevisa", -1)) {
            if (validarCombo("form1\\:cmbAprueba", -1)) {
                $("#msgSolicitaOrden").text("");
            } else {
                $("#msgSolicitaOrden").text("Por favor seleccione al usuario que aprobará la OC/S");
                e++;
            }
        } else {
            $("#msgSolicitaOrden").text("Por favor seleccione al usuario que revisará la OC/S");
            e++;
        }
        if (e == 0) {
            if (validarCombo("form1\\:cmbTerminoPago", -1) && validarCombo("form1\\:cmbTerminoPago", "")) {
                $("#msgSolicitaOrden").text("");
            } else {
                $("#msgSolicitaOrden").text("Por favor seleccione el termino de pago.");
                e++;
            }
        }
//}
    } else {
        $("#msgSolicitaOrden").text("Es necesario seleccionar al menos un contacto \n");
        e++;
    }


    if (e == 0) {
        confirmarBootbox('form1', "¿Está seguro de solicitar la orden...? ");
    }
    return retorno;
}

function mensajeEliminar() {
    $("#frmSubirArchivo\\:mensajeArc").empty();
    //$("#frmSubirArchivo\\:mensajeArc").text("Se eliminó el archivo");
    $(".msgArchivo").text("Se eliminó el archivo");
    $("#Cabecera\\:errorTag").empty();
    $("#frmCabOrden\\:errorTag").empty();
    $(".iceMsgsInfo").remove();
}

function mensajeArchivoAgregado(frm, componente, mensaje) {
//iceMsgsInfo
    $(".iceMsgsInfo").remove();
    $("#errorTag").empty();
    $(".errorTag").empty();
    $(".msgArchivo").text(mensaje);
}

function validaAsignar() {
    $("#errorTag").text("");
    var val = false;
    if (verificaSeleccion("requisiciones") > 0) {
        if (validarCombo("form1\\:listaAnalistas", -1)) {
            $("#msgAsignaRequisicion").text("");
            bloquearPantalla();
            val = true;
        } else {
            $("#msgAsignaRequisicion").text("Es necesario seleccionar al menos un analista \n");
        }
    } else {
        $("#msgAsignaRequisicion").text("Es necesario seleccionar al menos una requisición \n");
    }
    return val;
}

function validaAsignarRequi(frm, seleccion) {
    var val = false;
    if (verificaSeleccion(seleccion) > 0) {
        if (validarCombo("form1\\:listaAnalistas", -1)) {
            if (confirmar(frm, "La operación asignará la o las requisiciones seleccinadas. ¿Está seguro?.")) {
//     bloquearPantalla();
                $(".todos ").prop("checked", "");
                val = true;
            } else {
                val = false;
            }
        } else {
            bootbox.alert("Es necesario seleccionar al menos un analista \n");
        }
    } else {
        bootbox.alert("Es necesario seleccionar al menos una requisición \n");
    }
    return val;
}

function validaAsignarRRequi(frm) {
    var val = false;
    if (validarCombo("form1\\:listaAnalistas", -1)) {
        if (confirmar(frm, "La operación asignará la o las requisiciones seleccinadas. ¿Está seguro?.")) {
            $(".todos ").prop("checked", "");
            val = true;
        } else {
            val = false;
        }
    } else {
        bootbox.alert("Es necesario seleccionar al menos un analista \n");
    }
    return val;
}


function validaAutorizar(frm, seleccion) {
    var val = false;
    if (verificaSeleccion(seleccion) > 0) {
        if (confirmar(frm, "La operación va a autorizar la(s) OC/S seleccionada(s). ¿Está seguro?")) {
//     bloquearPantalla();
            $(".todos ").prop("checked", "");
            val = true;
        } else {
            val = false;
        }
    } else {
        bootbox.alert(mensajeNoSeleccion);
    }
    return val;
}

function validarDevolucion(frm, seleccion) {
    var val = false;
    if (verificaSeleccion(seleccion) > 0) {
        $(dialogoDevolverVariasOCS).modal('show');
        //if (confirmar(frm, "La operación va a Devolver la(s) OC/S seleccionada(s). ¿Está seguro?")) {
//     bloquearPantalla();
        $(".todos ").prop("checked", "");
        val = true;
        //} else {
        // val = false;
        //}
    } else {
        bootbox.alert(mensajeNoSeleccion);
    }
    return val;
}

function validarCancelacion(frm, seleccion) {
    var val = false;
    if (verificaSeleccion(seleccion) > 0) {
        $(dialogoCancelarVariasOCS).modal('show');
        //if (confirmar(frm, "La operación va a Devolver la(s) OC/S seleccionada(s). ¿Está seguro?")) {
//     bloquearPantalla();
        $(".todos ").prop("checked", "");
        val = true;
        //} else {
        // val = false;
        //}
    } else {
        bootbox.alert(mensajeNoSeleccion);
    }
    return val;
}


function validaRevCostos(frm, seleccion) {
    var val = false;
    if (verificaSeleccion(seleccion) > 0) {
        if (confirmar(frm, "La operación pasará la requisición. ¿Está seguro?.")) {
//     bloquearPantalla();
            $(".todos ").prop("checked", "");
            val = true;
        } else {
            val = false;
        }
    } else {
        bootbox.alert("Es necesario seleccionar al menos una requisición");
    }
    return val;
}

function validaRevisarRequi(frm, seleccion) {
    var val = false;
    if (verificaSeleccion(seleccion) > 0) {
        if (confirmar(frm, "La operación va a marcar como revisada(s) la(s) requisición(es). ¿Está seguro?.")) {
//     bloquearPantalla();
            $(".todos ").prop("checked", "");
            val = true;
        } else {
            val = false;
        }
    } else {
        bootbox.alert("Es necesario seleccionar al menos una requisición");
    }
    return val;
}

function validaDevRequi(frm, seleccion) {
    var val = false;
    if (verificaSeleccion(seleccion) > 0) {
        $(dialogoDevVariasReq).modal('show');
        val = true;
    } else {
        bootbox.alert("Es necesario seleccionar al menos una requisición");
    }
    return val;
}

function validaCancelRequi(frm, seleccion) {
    var val = false;
    if (verificaSeleccion(seleccion) > 0) {
        $(dialogoCancelarVariasReq).modal('show');
        val = true;
    } else {
        bootbox.alert("Es necesario seleccionar al menos una requisición");
    }
    return val;
}

function validaAceptarRequi(frm, seleccion) {
    var val = false;
    if (verificaSeleccion(seleccion) > 0) {
        if (confirmar(frm, "¿Está seguro de aceptar la requisición...?")) {
//     bloquearPantalla();
            $(".todos ").prop("checked", "");
            val = true;
        } else {
            val = false;
        }
    } else {
        bootbox.alert("Es necesario seleccionar al menos una requisición");
    }
    return val;
}

function validaAprobarRequi(frm, seleccion) {
    var val = false;
    if (verificaSeleccion(seleccion) > 0) {
        if (confirmar(frm, "Está a punto de aprobar una requisición, por lo tanto el sistema almacenará sus datos para soportar su aprobación y aclaraciones posteriores.")) {
//     bloquearPantalla();
            $(".todos ").prop("checked", "");
            val = true;
        } else {
            val = false;
        }
    } else {
        bootbox.alert("Es necesario seleccionar al menos una requisición");
    }
    return val;
}

function validaGenerarOrden(frm, seleccion, mensaje) {
    var val = false;
    if (verificaSeleccion(seleccion) > 0) {
        confirmarBootbox(frm, mensaje);
    } else {
        bootbox.alert(mensajeNoSeleccion);
    }
    return val;
}
function verificaSeleccion(clase) {
    var sel = 0;
    $("." + clase).each(function () {
        if ($(this).is(':checked')) {
            sel++;
        }
    });
    return sel;
}


function dialogoOK(dialogo, dialogoCerrar) {
    $("#" + dialogoCerrar).dialog("close");
    $("#" + dialogo).dialog({
        hide: "explode",
        resizable: false,
        draggable: false,
        modal: true,
        closeOnEscape: false,
        buttons: {
            Cerrar: function () {
                $("#" + dialogo).dialog("close");
            }
        }
    });
    $(".ui-dialog-titlebar-close").hide();
    setTimeout('cerrarDialogo()', 5000);
}

function cerrarDialogo(dialogo) {
    $("#" + 'dialogOK').dialog("close");
}



function dialogoBad(dialogo) {
    $("#" + dialogo).dialog({
        closeOnEscape: false
    });
    $(".ui-dialog-titlebar-close").hide();
}

function dialogoCrearRequisicion(dialogo) {
//    bootbox.alert(dialogo);
    $("#" + dialogo).dialog({
        hide: "explode",
        resizable: false,
        draggable: false,
        height: 500,
        width: 850,
        closeOnEscape: false,
        modal: true,
        buttons: {
            Cerrar: function () {
                $("#frmDialogoCrearReq\\:btnLimpiar").click();
                $("#" + dialogo).dialog("close");
            }
        }
    });
    $(".ui-dialog-titlebar-close").hide();
}

// OC/S
function dialogoItemsOtraOrden(dialogo, btnLimpiar, forma) {
//    bootbox.alert(dialogo);
    $("#" + dialogo).dialog({
        hide: "explode",
        resizable: false,
        draggable: false,
        height: 550,
        width: 900,
        closeOnEscape: false,
        modal: true,
        buttons: {
            Cerrar: function () {
                $("#" + forma + "\\:" + btnLimpiar).click();
                $("#" + dialogo).dialog("close");
            }
        }
    });
    $(".ui-dialog-titlebar-close").hide();
}



function mostrarDialogo(dialogo, mensaje) {
    $("#" + dialogo).tooltip({
        hide: "explode",
        resizable: false,
        draggable: false,
        show: {
            hide: "explode"
        }
    });
    $("#form1\\:mensajeDialogo").text(mensaje);
}


function confirmar(frm, mensaje) {
    return   confirmarBootbox(frm, mensaje);
}

function limpiarCampos() {
    $(".itemAFClean").val('');
}


function confirmarBootbox(frm, mensaje) {
    var res = false;
    //    
    bootbox.confirm({
        message: mensaje,
        callback: function (result) {
            if (result)
                $('#' + frm).submit();
        },
        buttons: {
            cancel: {
                className: 'separaBoton btn',
                label: "No"
            },
            confirm: {
                className: "btn btn-info",
                label: "Si"
            }
        },
        onEscape: function () {
            // user pressed escape
        },
        closeButton: false
    });
    return res;
}


function confirmarCambiarBloque(frm, mensaje) {
    var res = false;
    bootbox.confirm({
        message: mensaje,
        callback: function (result) {
            if (result)
                $('#' + frm).submit();
        },
        buttons: {
            cancel: {
                className: 'separaBoton btn',
                label: "No"
            },
            confirm: {
                className: "btn btn-info",
                label: "Si"
            }
        },
        onEscape: function () {
            // user pressed escape
        },
        closeButton: false
    });
    return res;
}
function seleccionarTodo(todos, seleccion) {
    if ($("." + todos).is(":checked")) {
        $("." + seleccion).prop("checked", "checked");
    } else {
        $("." + seleccion).prop("checked", "");
    }
}

function limpiarTodos() {
    if ($(".todos").is(":checked")) {
        $(".todos").prop("checked", "");
    }
    seleccionarTodo('todos', 'seleccion');
}

function seleccionarGeneral(todos, seleccion, divAuto, divOperacion) {
    if ($("." + todos).is(":checked")) {
        $("." + seleccion).prop("checked", "checked");
        // mostrarDiv(divAuto);
        //ocultarDiv(divOperacion);
    } else {
        $("." + seleccion).prop("checked", "");
        //ocultarDiv(divAuto);
    }
}

function mostrarUsuarioNoNavision() {     
  const auto = document.querySelectorAll("#usuarioBeneficiado input");
  const input = auto[0];
  const NoNavi = document.getElementById("inputUsuarioNoNavision");
  const inpNoNav = document.querySelector("#inputUsuarioNoNavision input");
  input.value = "";
  inpNoNav.value = "";

  if(NoNavi.style.display == "none"){
    NoNavi.style.display = "inline";
    input.disabled = true;
  }
  else{    
    NoNavi.style.display = "none";
    input.disabled = false;
  }
  
  
}

function seleccionarGeneralConConvenio(todos, seleccion) {
    if ($("." + todos).is(":checked")) {
        $("." + seleccion).prop("checked", "checked");
        // mostrarDiv(divAuto);
        //ocultarDiv(divOperacion);
    } else {
        $("." + seleccion).prop("checked", "");
        //ocultarDiv(divAuto);
    }
}
function seleccionarGeneralReq(todos, seleccion) {
    if ($("." + todos).is(":checked")) {
        $("." + seleccion).prop("checked", "checked");
    } else {
        $("." + seleccion).prop("checked", "");
    }
}

function changeMultiItemReq(todos) {
    $("." + todos).prop("checked", "");
}


function validateMultiItemReq(seleccion) {
    if ($("." + seleccion + ":checkbox:checked").length == 0) {
        bootbox.alert("Es necesario seleccionar por lo menos un proyecto OT.");
        return false;
    } else {
        return true;
    }
}

function alertaGeneral(mensaje) {
    bootbox.alert(mensaje);
}

function confirmarBloquearPantallaSeleccionarFila(mensaje, json) {
//   bootbox.alert(json.consecutivo);
    $("#form1\\:" + "idCampoHide").val(json.consecutivo);
    ejecutarFiltros('form1', 'btnBuscar');
    if (confirm(mensaje)) {
//  bloquearPantalla();
        return true;
    } else {
        return false;
    }
}
function confirmarBloquearPantalla(mensaje) {
    if (confirm(mensaje)) {
//bloquearPantalla();
        return true;
    } else {
        return false;
    }
}

function validaCancelar(forma, componente, mensajeConfirmar, mensajeMotivo) {
    if (validarCajaTexto(forma + "\\:" + componente)) {
        bootbox.alert(mensajeMotivo);
        return false;
    } else {
        confirmarBootbox(forma, mensajeConfirmar);
    }
    return false;
}

function validarNavCode() {
//bootbox.alert($("#formPopupCompletarOrden\\:navCodeID").val());
    if (validarCajaTexto("formPopupCompletarOrden\\:navCodeID")) {
        bootbox.alert("Es necesario capturar un código de NAVISION válido.");
        return false;
    } else {
        if (validarTamTexto("formPopupCompletarOrden\\:navCodeID", 10, 20)) {
            return confirmar('formPopupCompletarOrden', '¿Está usted seguro de querer enviar la OC/S al proveedor? ');
        }
        bootbox.alert("Es necesario capturar un código de NAVISION válido. Mayor o igual a 10 caracteres y menor o igual a 20 caracteres");
        return false;
    }
}

function validarTamTexto(idComponente, min, max) {
//bootbox.alert($("#" + idComponente).val().length);
    if (($("#" + idComponente).val().length >= min) && ($("#" + idComponente).val().length <= max)) {
        return true;
    } else {
        return false;
    }
}

//&& cval != 'Seleccionar'  se quito seleccionar para que lo haga cuando le da clic en seleccionar
function seleccionarFila(tabla) {
    var forma = tabla.split("\\");
    var cvalUncl, cval;
    $("#" + tabla + " tbody tr td").click(function () {
        cvalUncl = $(this).text();
        //       bootbox.alert(cvalUncl);
    });
// dbl clicl
    /* $("#"+tabla+" tbody tr td").dblclick(function(){
     cval = $(this).text();
     });
     $("#"+tabla+" tbody tr").dblclick(function() {
     //  bloquearPantalla();
     if ($(this).hasClass('filaSeleccionada')) $(this).removeClass('filaSeleccionada');
     else {
     $(this).siblings('.filaSeleccionada').removeClass('filaSeleccionada');
     $(this).addClass('filaSeleccionada');
     if(cval != 'Solicitar' && cval != 'Cargar ETS' && cval != 'Modificar' && cval != 'Editar' && cval != 'Eliminar'
     && cval != 'Revisar' && cval != 'Devolver' && cval != 'Generar Nota' && cval != 'Cancelar' && cval != 'Aprobar' 
     && cval != 'Visto bueno' && cval != 'Revisada'  && cval != 'Generar Orden' 
     && cval != 'Devolver Req.' && cval != 'Finalizar Req.'){
     var $objCeldas=$(this).find('td');
     $objCeldas.each(function(iIndiceCelda,objCeldaFila){
     if(iIndiceCelda == 0){
     var id = $(objCeldaFila).text();
     $("#"+forma[0]+"\\:" + "idCampoHide").val(id);
     ejecutarFiltros(forma[0], "btnBuscar");
     }
     });
     }
     }
     });*/
// escucha un click
//    $("#"+tabla+" tbody tr").click(function() {
//        //    bootbox.alert('Hola bola 1 ');  || cvalUncl == 'Cargar ETS'
//        //        if(cvalUncl == 'Solicitar'  || cvalUncl == 'Modificar' || cvalUncl == 'Editar' || cvalUncl == 'Eliminar'
//        //            || cvalUncl == 'Revisar' || cvalUncl == 'Devolver' || cvalUncl == 'Generar Nota' || cvalUncl == 'Cancelar' || cvalUncl == 'Aprobar' 
//        //            || cvalUncl == 'Visto bueno' || cvalUncl == 'Revisada'  || cvalUncl == 'Generar Orden' 
//        //            || cvalUncl == 'Devolver Req.' || cvalUncl == 'Finalizar Req.' || cvalUncl == 'Seleccionar'){
//        var val = '';
//        var q = $("#"+forma[0]+"\\:" + "idCampoHide").val(val);
//        alert(q);
//        ejecutarFiltros(forma[0], "btnLimpiarOCS");
//        if ($(this).hasClass('filaSeleccionada')) $(this).removeClass('filaSeleccionada');
//        else {
//            $(this).siblings('.filaSeleccionada').removeClass('filaSeleccionada');
//            $(this).addClass('filaSeleccionada');
//        }
//    //}
//    });
}


function seleccionarFilaConCheck(tabla) {
    var forma = tabla.split("\\");
    var cval;
    $("#" + tabla + " tbody tr td").click(function () {
        cval = $(this).text();
        // bootbox.alert(cval);
    });
    $("#" + tabla + " tbody tr").click(function () {
        if (cval == 'Solicitar' || cval == 'Cargar ETS' || cval == 'Modificar' || cval == 'Eliminar' || cval == 'Editar' || cval == 'Eliminar'
                || cval == 'Revisar' || cval == 'Devolver' || cval == 'Generar Nota' || cval == 'Cancelar' || cval == 'Aprobar'
                || cval == 'Visto bueno' || cval == 'Revisada' || cval == '') {
            var val = '';
            $("#" + forma[0] + "\\:" + "idCampoHide").val(val);
            ejecutarFiltros(forma[0], "btnLimpiarOCS");
            if ($(this).hasClass('filaSeleccionada'))
                $(this).removeClass('filaSeleccionada');
            else {
                $(this).siblings('.filaSeleccionada').removeClass('filaSeleccionada');
                $(this).addClass('filaSeleccionada');
            }
        }
    });
}
/**
 * @dialogo: dialogo
 * @mensaje: Contenido a mostrar en el dialogo
 * @cajaDialogo: id del componente 'p' a dentro.
 */
function mostrarDialogoConMensaje(dialogo, mensaje, cajaDialogo) {
//   bootbox.alert(mensaje);
//    llenarEtiqueta(forma +"\\:"+cajaDialogo, mensaje);
    llenarEtiqueta(cajaDialogo, mensaje);
    $("#" + dialogo).dialog({
        modal: true,
        height: 'auto',
        hide: "explode",
        resizable: false,
        draggable: false,
        width: "91.6%",
        buttons: {
            Cerrar: function () {
                llenarEtiqueta(cajaDialogo, '');
                $("#" + dialogo).dialog("close");
            }
        }
    });
    $(".ui-dialog-titlebar-close").hide();
    return false;
}

function llenarEtiqueta(ele, mensaje) {
    $("#" + ele).text(mensaje);
}


function dialogoAyudaRequisicion(dialogo) {
//    bootbox.alert(dialogo);
    $("#" + dialogo).dialog({
        hide: "explode",
        resizable: false,
        draggable: false,
        height: 450,
        width: 700,
        modal: true,
        buttons: {
            Cerrar: function () {
                //$("#frmCerrarAyuda\\:btnCerrar" ).click(); 
                $("#" + dialogo).dialog("close");
            }
        }
    });
    //        closeOnEscape: false
    $(".ui-dialog").css({
        "z-index": "25003",
        "opacity": "0.93"
    });
    $(".ui-dialog-titlebar-close").hide();
}

function dialgoAgregarArchivo(dialogo) {
//    bootbox.alert(dialogo);
    $("#" + dialogo).dialog({
        hide: "explode",
        resizable: false,
        draggable: false,
        height: 120,
        width: 600,
        modal: true,
        buttons: {
            Cerrar: function () {
                $("#frmSubirArchivoNoti\\:btnCerrar").click();
                $("#" + dialogo).dialog("close");
            }
        }
    });
    //        closeOnEscape: false
    $(".ui-dialog").css({
        "z-index": "25003",
        "opacity": "0.93"
    });
    $(".ui-dialog-titlebar-close").hide();
}

/**************************************/

function validaBuscar(inicio, fin) {
    var v = 0;
    if (!validarCajaTexto(inicio)) {
        if (!validarCajaTexto(fin)) {

        } else {
            bootbox.alert("Seleccione la fecha de fin")
            v++;
        }
    } else {
        bootbox.alert("Seleccione la fecha de inicio")
        v++;
    }
    return v == 0 ? true : false;
}

function validaBuscarProveedor(inicio, fin, proveedor, sel) {
    var v = 0;
    var panel = $("input[name='" + sel + "']:checked").val();
    // bootbox.alert(panel);
    if (!validarCajaTexto(inicio)) {
        if (!validarCajaTexto(fin)) {
            if (panel == 'PROVEE') {
                if (!validarCajaTexto(proveedor)) {

                } else {
                    bootbox.alert("Agregue un proveedor")
                    v++;
                }
            }
        } else {
            bootbox.alert("Seleccione la fecha de fin")
            v++;
        }
    } else {
        bootbox.alert("Seleccione la fecha de inicio")
        v++;
    }
    return v == 0 ? true : false;
}

function llenarDatosCompradores(datosCompradores, inicio, fin, autotozada) {
    if (datosCompradores != null) {
        grafica(datosCompradores, inicio, fin, autotozada);
    } else {
        bootbox.alert('No hay datos para las fechas seleccionadas');
    }
}
function grafica(datos, inicio, fin, autorizada) {
    var compradores = datos.Comprador;
    var total = datos.total;
    var totalDolar = datos.totalDolar;
    var tipo = autorizada ? 'Solicitadas' : 'Devueltas'
    //     bootbox.alert(compradores);
    //     bootbox.alert(total);
    //bootbox.alert(datos.jfechas)

    var chart = new Highcharts.Chart({
        chart: {
            renderTo: 'grafica',
            zoomType: 'xy'
        },
        title: {
            text: 'Gráfica de OC/S (' + tipo + ')'
        },
        subtitle: {
            text: 'Del ' + inicio + ' al ' + fin,
            style: {
                fontSize: '13px',
                fontFamily: 'Verdana, sans-serif',
                textShadow: '0 0 1px gray'
            }
        },
        xAxis: {
            categories: compradores,
            labels: {
                style: {
                    fontSize: '13px',
                    fontFamily: 'Verdana, sans-serif'
                }
            }
        },
        yAxis: [{// primary yAxis
                min: 0,
                title: {
                    text: 'Total OC/S'
                }
            }, {// Secondary yAxis
                min: 0,
                title: {
                    text: 'Dólares'
                },
                labels: {
                    style: {
                        color: Highcharts.getOptions().colors[0]
                    }
                },
                opposite: true
            }],
        tooltip: {
            shared: true,
            valueDecimals: 2
        },
        legend: {
            enabled: true,
            layout: 'vertical',
            align: 'left',
            x: 100,
            verticalAlign: 'top',
            y: 50,
            floating: true
        },
        series: [{
                type: 'line',
                yAxis: 1,
                name: 'Dólares',
                data: totalDolar
            }, {
                colorByPoint: true,
                name: 'Total de OC/S',
                type: 'column',
                data: total,
                dataLabels: {
                    enabled: true,
                    rotation: 0,
                    color: 'blue',
                    align: 'center',
                    x: 0,
                    y: 5,
                    style: {
                        fontSize: '13px',
                        fontFamily: 'Verdana, sans-serif',
                        textShadow: '0 0 1px black'
                    }
                }
            }]
    });
}

function llenarRequiscionesCompradores(datosCompradores, dias) {
    if (datosCompradores != null) {
        graficaRequisicion(datosCompradores, dias);
    } else {
        bootbox.alert('No hay datos para las fechas seleccionadas');
    }
}

function graficaRequisicion(datos, dias) {
    var compradores = datos.Comprador;
    var total = datos.total;
    var totalOcs = datos.totalOcs;
    //
    var fecha = new Date();
    var milisegundos = parseInt(35 * 24 * 60 * 60 * 1000);
    //Obtenemos los milisegundos desde media noche del 1/1/1970
    var tiempo = fecha.getTime();
    //Calculamos los milisegundos sobre la fecha que hay que sumar o restar...
    milisegundos = parseInt(-dias * 24 * 60 * 60 * 1000);
    //Modificamos la fecha actual
    fecha.setTime(tiempo + milisegundos);
    //
    var chart = new Highcharts.Chart({
        chart: {
            renderTo: 'graficaSinSolicitar',
            zoomType: 'xy'
        },
        title: {
            text: 'Requisiciones asignadas y OC/S sin solicitar'
        },
        subtitle: {
            text: 'Asignadas antes del: ' + fecha.getDate() + '/' + (parseInt(fecha.getMonth()) + 1) + '/' + fecha.getFullYear(),
            style: {
                fontSize: '13px',
                fontFamily: 'Verdana, sans-serif',
                textShadow: '0 0 1px gray'
            }
        },
        xAxis: {
            categories: compradores,
            labels: {
                /*rotation: -55,*/
                style: {
                    fontSize: '13px',
                    fontFamily: 'Verdana, sans-serif'
                }
            }
        },
        yAxis: [{// primary yAxis
                min: 0,
                title: {
                    text: 'Total Requisiciones'
                }
            }],
        legend: {
            enabled: true,
            layout: 'vertical',
            align: 'left',
            x: 100,
            verticalAlign: 'top',
            y: 50,
            floating: true
        },
        tooltip: {
            valueDecimals: 2
        },
        series: [{
                colorByPoint: false,
                name: 'Requisiciones asignadas',
                type: 'column',
                data: total,
                cursor: 'pointer',
                point: {
                    events: {
                        click: function () {
                            
                            var comprador = this.category                            
                            $("#frmRep\\:tbReportes\\:" + "hidenTitulo").val('Requisiciones asignadas');                            
                            $("#frmRep\\:tbReportes\\:" + "hdComprador").val(comprador);                            
                            $("#frmRep\\:tbReportes\\:btnBuscarPorComprador").click();                            
                        }
                    }
                },
                dataLabels: {
                    enabled: true,
                    rotation: 0,
                    color: 'blue',
                    align: 'center',
                    x: 0,
                    y: 5,
                    style: {
                        fontSize: '13px',
                        fontFamily: 'Verdana, sans-serif',
                        textShadow: '0 0 1px black'
                    }
                }
            }, {
                name: 'Ocs sin solicitar',
                data: totalOcs,
                type: 'column',
                cursor: 'pointer',
                dataLabels: {
                    enabled: true,
                    rotation: 0,
                    color: 'blue',
                    align: 'center',
                    x: 0,
                    y: 5,
                    style: {
                        fontSize: '13px',
                        fontFamily: 'Verdana, sans-serif',
                        textShadow: '0 0 1px black'
                    }
                },
                point: {
                    events: {
                        click: function () {                            
                            var comprador = this.category
                            $("#frmRep\\:tbReportes\\:" + "hidenTitulo").val('Ocs sin solicitar');
                            $("#frmRep\\:tbReportes\\:" + "hdComprador").val(comprador);
                            $("#frmRep\\:tbReportes\\:btnBuscarOCSPorComprador").click();
                        }
                    }
                }
            }]
    });
}

function llenarDatosOCSGerencia(datos, inicio, fin, status) {
    if (datos != null) {
        graficaGerencia(datos, inicio, fin, status);
    } else {
        bootbox.alert('No hay datos para las fechas seleccionadas');
    }
}

function limpiarLista(frm) {
    $("#" + frm + "\\:" + "btnLimpiar").click();
}

function limpiarListaLlenarJsonProveedor(frm) {
    limpiarLista(frm);
}

function llenarProveedor(frm, json) {
    proveedores = json;
    if (proveedores != null) {
        $("#" + frm + "\\:autocomplete").autocomplete({
            source: proveedores,
            minLength: 2,
            select: function (event, ui) {
                $("#" + frm + "\\:autocomplete").val(ui.item.nombre);
                $("#" + frm + "\\:hidenDes").val(ui.item.value);
                return false;
            }
        });
        $("#" + frm + "\\:autocomplete").css({
            "text-align": "left",
            "width": "400px;"
        });
        $("ul.ui-autocomplete").addClass("autocompletar");
    } else {
        bootbox.alert('No se cargaron los proveedores, por favor, intente solicitar la OC/S otra vez.');
    }
}
function graficaGerencia(datos, inicio, fin, autorizada) {
    var compradores = datos.Gerencia;
    var total = datos.total;
    var totalDolar = datos.totalDolar;
    var status = $("#frmRep\\:tbReportes\\:cmbStatus option:selected").text();
    //     bootbox.alert(compradores);
    //     bootbox.alert(total);
    //bootbox.alert(datos.jfechas)

    var chart = new Highcharts.Chart({
        chart: {
            renderTo: 'graficaOrdenPorGerencia',
            zoomType: 'xy'
        },
        title: {
            text: 'Gráfica de OC/S Solicitadas por gerencia'
        },
        subtitle: {
            text: 'Del ' + inicio + ' al ' + fin,
            style: {
                fontSize: '13px',
                fontFamily: 'Verdana, sans-serif',
                textShadow: '0 0 1px gray'
            }
        },
        xAxis: {
            categories: compradores,
            labels: {
                style: {
                    fontSize: '13px',
                    fontFamily: 'Verdana, sans-serif'
                }
            }
        },
        yAxis: [{// primary yAxis
                min: 0,
                title: {
                    text: 'Total OC/S'
                }
            }, {// Secondary yAxis
                min: 0,
                title: {
                    text: 'Dólares'
                },
                labels: {
                    style: {
                        color: Highcharts.getOptions().colors[0]
                    }
                },
                opposite: true
            }],
        tooltip: {
            valueDecimals: 2,
            shared: true
        },
        legend: {
            enabled: true,
            layout: 'vertical',
            align: 'left',
            x: 100,
            verticalAlign: 'top',
            y: 50,
            floating: true
        },
        series: [{
                type: 'line',
                yAxis: 1,
                name: 'Dólares',
                data: totalDolar
            }, {
                colorByPoint: true,
                name: 'Total de OC/S',
                type: 'column',
                data: total,
                dataLabels: {
                    enabled: true,
                    rotation: 0,
                    color: 'blue',
                    align: 'center',
                    x: 0,
                    y: 5,
                    style: {
                        fontSize: '13px',
                        fontFamily: 'Verdana, sans-serif',
                        textShadow: '0 0 1px black'
                    }
                }
            }]
    });
}


function regresaFechaInicioMes(frm, componente) {
    var hoy = new Date();
    var mes = (hoy.getMonth() + 1);
    var completoMes;
    if (mes >= 10) {
        completoMes = mes;
    } else {
        completoMes = '0' + mes;
    }
    var fechaInicioMes = '01' + '/' + completoMes + '/' + hoy.getFullYear();
    $("#" + frm + "\\:" + componente).val(fechaInicioMes);
}

function ponerTituloTabla(cmb, ele) {
    var valor = $("#" + cmb + " option:selected").text();
    llenarEtiqueta(ele, valor);
}

function quitarGrafica(grafica) {
    grafica.css({
        "diplay": "none"
    });
}
//graficaOCSSolDevCan

function graficaOCSSolDevCan(datos, inicio, fin) {
    var compradores = datos.analista;
    var totalSol = datos.totalSol;
    var totalDev = datos.totalDev;
    var totalCan = datos.totalCan;
    var chart = new Highcharts.Chart({
        chart: {
            renderTo: 'graficaSolDevCan',
            type: 'column',
            zoomType: 'xy'
        },
        title: {
            text: 'Estadística de OC/S'
        },
        subtitle: {
            text: 'Del ' + inicio + ' al ' + fin,
            style: {
                fontSize: '13px',
                fontFamily: 'Verdana, sans-serif',
                textShadow: '0 0 1px gray'
            }
        },
        xAxis: {
            categories: compradores,
            labels: {
                rotation: -55
            }
        },
        yAxis: [{// primary yAxis
                min: 0,
                title: {
                    text: 'Total'
                },
                labels: {
                    style: {
                        color: Highcharts.getOptions().colors[0]
                    }
                }
            }],
        tooltip: {
            valueDecimals: 0
        },
        legend: {
            enabled: true,
            layout: 'horizontal',
            align: 'bottom',
            x: 100,
            verticalAlign: 'top',
            y: 50,
            floating: true
        },
        series: [{
                name: 'Solicitadas',
                data: totalSol,
                dataLabels: {
                    enabled: true,
                    rotation: 0,
                    color: 'blue',
                    align: 'center',
                    x: 0,
                    y: 5,
                    style: {
                        fontSize: '13px',
                        fontFamily: 'Verdana, sans-serif',
                        textShadow: '0 0 1px black'
                    }
                },
                point: {
                    events: {
                        click: function () {
                            var comprador = this.category
                            $("#frmRep\\:tbReportes\\:hidenIndTab").val(1);
                            $("#frmRep\\:tbReportes\\:hidenComprador").val(comprador);
                            $("#frmRep\\:tbReportes\\:btnPorComprador").click();
                        }
                    }
                }
            }, {
                name: 'Devueltas',
                data: totalDev,
                dataLabels: {
                    enabled: true,
                    rotation: 0,
                    color: 'black',
                    align: 'center',
                    x: 0,
                    y: 5,
                    style: {
                        fontSize: '13px',
                        fontFamily: 'Verdana, sans-serif',
                        textShadow: '0 0 1px black'
                    }
                },
                point: {
                    events: {
                        click: function () {
                            var comprador = this.category
                            $("#frmRep\\:tbReportes\\:hidenIndTab").val(2);
                            $("#frmRep\\:tbReportes\\:hidenComprador").val(comprador);
                            $("#frmRep\\:tbReportes\\:btnPorComprador").click();
                        }
                    }
                }
            }, {
                name: 'Canceladas',
                data: totalCan,
                dataLabels: {
                    enabled: true,
                    rotation: 0,
                    color: 'green',
                    align: 'center',
                    x: 0,
                    y: 5,
                    style: {
                        fontSize: '13px',
                        fontFamily: 'Verdana, sans-serif',
                        textShadow: '0 0 1px black'
                    }
                },
                point: {
                    events: {
                        click: function () {
                            var comprador = this.category
                            $("#frmRep\\:tbReportes\\:hidenIndTab").val(3);
                            $("#frmRep\\:tbReportes\\:hidenComprador").val(comprador);
                            $("#frmRep\\:tbReportes\\:btnPorComprador").click();
                        }
                    }
                }
            }]
    });
}




function cambiarTab(form, btn, tab, indice) {
//bootbox.alert(indice);
    $("#" + form + "\\:" + tab).val(indice);
    ejecutarFiltros(form, btn);
}

function cerrarDialogoModal(dialogo) {
    $(dialogo).modal('hide');
}

function abrirDialogoModal(dialogo) {
    $(dialogo).modal('show');
}

function activarTab(tab, indice, divMostrar, divOcultar, divOperacion, divAuto) {
    $("#" + tab).tabs({
        active: indice
    });
    ///
    mostrarDiv(divMostrar);
    ////                   
    ocultarDiv(divOcultar);
    ////                   
    mostrarDiv(divOperacion);
    ////                   
    ocultarDiv(divAuto);
}

function regresar(divTabla, divDatos, divOperacion, divAuto) {

    mostrarDiv(divTabla);
    ////                   
    mostrarDiv(divAuto);
    ////                   
    ocultarDiv(divDatos);
    ////                   
    ocultarDiv(divOperacion);
//
//limpiar seleccio
}

function mostrarDiv(divM) {
    $("#" + divM).css({
        "display": "block"
    });
}

function ocultarDiv(divOcultar) {
    $("#" + divOcultar).css({
        "display": "none"
    });
}


function cerrarDevolver() {
    cerrarDialogoModal(dialogoDevReq);
    regresar('divTabla', 'divDatos', 'divOperacion', 'divAutoriza');
}

function cerrarCancelar() {
    ocultarDiv('dialogoCancelarReq');
    regresar('divTabla', 'divDatos', 'divOperacion', 'divAutoriza');
}

function iniciarTab(tab, indice) {
    $("#" + tab).tabs({
        active: indice
    });
}


function quitarValor(frm, componente) {
    $("#" + frm + '\\:' + componente).text('');
    ocultarDiv('divNombreProv');
}
function confirmarAccion(frm, mensaje, valor, componente) {
    return   confirmarConParametro(frm, mensaje, valor, componente);
}

function confirmarConParametro(frm, mensaje, valor, componente) {
    var res = false;
    //    $("#frmOCSSolDevCan\\:" + "hidenComprador").val(comprador);
    $("#" + frm + "\\:" + componente).val(valor);
    bootbox.confirm({
        message: mensaje,
        callback: function (result) {
            if (result) {
                //                    
                $('#' + frm).submit();
            }
        },
        buttons: {
            cancel: {
                className: "btn btn-default ",
                label: 'No'
            },
            confirm: {
                className: "btn btn-info",
                label: "Si"
            }
        },
        onEscape: function () {
            // user pressed escape
        },
        closeButton: false
    });
    return res;
}

function cerrarEditCategoria() {
    cerrarDialogoModal(dialogoCategoriaRequi);
}

function cerrarEditCategoriaGerencia() {
    cerrarDialogoModal(dialogoCategoriaRequi);
    regresar('divTabla', 'divDatos', 'divOperacion', 'divAutoriza');
}

function cerrarNRDO() {
    cerrarDialogoModal(dialogoCrearReq);
    bootbox.alert('Se generó correctamente la requisición.<br/> Por favor verifique la cadena de aprobación,la tarea y la unidad de medida de los items de la requisición generada.');
}

function errorNRDO() {
    bootbox.alert('Ocurrió un error y no fue posible crear la requisicion.');
}

function regresarSolitar() {
    cerrarDialogoModal(dialogoSolicitandoReq);
    regresar('divTabla', 'divDatos', 'divOperacion', 'divAutoriza');
}
function mostrarMensajeError(divError, componente, mensaje) {
    mostrarDiv(divError);
    agregarValor(componente, mensaje);
}

function agregarValor(componente, mensaje) {
    $("#" + componente).text(mensaje);
}
function validaMotivo(forma, campoID, msgConf, msg) {
    var retorno = false;
    if (!this.validarCajaTexto(forma + "\\:" + campoID)) {
        retorno = confirm(msg);
    } else {
        alert("Es necesario capturar el motivo por el cual devolverá " + msgConf);
    }

    return retorno;
}

function cerrarEnvioPDF() {
    cerrarDialogoModal(dialogoEnviarProveedor);
    iniciarTab("tipoTarea", 0);
}


function validaOcultaFecha(conFecha) {
    var conF = jQuery("input[name$='" + conFecha + "']':checked").val();
    if (conF === 'Si') {
        mostrarDiv('divConFecha');
    } else {
        ocultarDiv('divConFecha');
    }
}

function validaBuscarAvanzada(conFecha, inicio, fin) {
    var v = 0;
    /*var conF = jQuery("input[name$='" + conFecha + "']':checked").val();*/
    var conF = $("#" + conFecha + "").val();
    if (conF === 'Si') {
        if (!validarCajaTexto(inicio)) {
            if (!validarCajaTexto(fin)) {

            } else {
                bootbox.alert("Seleccione la fecha de fin")
                v++;
            }
        } else {
            bootbox.alert("Seleccione la fecha de inicio")
            v++;
        }
    }
    return v == 0 ? true : false;
}

function colapsarPanel(elemento, panel) {
    var $this = $("#" + panel);
    if ($this.hasClass('in')) {
        minimizarPanel(elemento, panel);
    } else {
        expandirPanel(elemento, panel);
    }
}

function minimizarPanel(elemento, panel) {
    var $this = $("#" + panel);
    if ($this.hasClass('in')) {
        $this.removeClass("in");
        $("#" + elemento).removeClass('glyphicon-minus').addClass('glyphicon-plus');
    }
}

function expandirPanel(elemento, panel) {
    var $this = $("#" + panel);
    if ($this.hasClass('in')) {
    } else {
        $this.addClass("in");
        $("#" + elemento).removeClass('glyphicon-plus').addClass('glyphicon-minus');
    }
}

function crearNuevoItemJS() {
    var se = $("#frmItemReq\\:idBusquedaAvArticulos");
    se.val("");
    abrirDialogoModal(dialogoItemsRequi);
    minimizarPanel('artFrecImg', 'collapsePanelArtFre');
    minimizarPanel('busAvaImg', 'collapsePanelBusquedaAvanzada');
    return true;
}

function marcarBusqueda() {
    var textoBda = $("#frmItemReq\\:idBusquedaAvArticulos").val();
    textoBda = textoBda.replace(/ /gi, function myFunction(x) {
        return "%";
    });
    var arr = textoBda.split("%");
    for (jj = 0; jj < arr.length; jj++) {
        var textoMB = arr[jj];
        textoMB = textoMB.trim();
        if (textoMB != "" && textoMB != undefined && textoMB != null) {
            marcarBusquedaNombre(textoMB);
            marcarBusquedaCodigo(textoMB);
        }
    }
    marcarBusquedaNombreColor();
    marcarBusquedaCodigoColor();
    return true;
}

function crearNuevoItemJSOrden() {
    var se = $("#frmItemOrden\\:idBusquedaAvArticulos");
    se.val("");
    abrirDialogoModal(dialogoModItemOCS);
    minimizarPanel('artFrecImg', 'collapsePanelArtFre');
    minimizarPanel('busAvaImg', 'collapsePanelBusquedaAvanzada');
    return true;
}

function marcarBusquedaOrden() {
    var textoBda = $("#frmItemOrden\\:idBusquedaAvArticulos").val();
    textoBda = textoBda.replace(/ /gi, function myFunction(x) {
        return "%";
    });
    var arr = textoBda.split("%");
    for (jj = 0; jj < arr.length; jj++) {
        var textoMB = arr[jj];
        textoMB = textoMB.trim();
        if (textoMB != "" && textoMB != undefined && textoMB != null) {
            marcarBusquedaNombre(textoMB);
            marcarBusquedaCodigo(textoMB);
        }

    }
    marcarBusquedaNombreColor();
    marcarBusquedaCodigoColor();
    return true;
}

function marcarBusquedaNombre(textoBdaN) {
    var nombres = $("td.resultadoBdaNombre > span");
    for (i = 0; i < nombres.length; i++) {
        var texto = nombres[i].innerHTML;
        texto = texto.toUpperCase();
        textoBdaN = textoBdaN.toUpperCase();
        //var newTexto = texto.replace(textoBdaN, "<em style='background-color: rgb(255, 255, 102); font-style: inherit; color: rgb(0, 0, 0);'>"+textoBdaN+'</em>');                        
        var newTexto = texto.replace(textoBdaN, "@@1@@" + textoBdaN + '@@2@@');
        nombres[i].innerHTML = newTexto;
    }
    return true;
}

function marcarBusquedaNombreColor() {
    var nombres = $("td.resultadoBdaNombre > span");
    for (i = 0; i < nombres.length; i++) {
        var texto = nombres[i].innerHTML;
        texto = texto.toUpperCase();
        var newTexto1 = texto.replace(/@@1@@/g, "<em style='background-color: rgb(255, 255, 102); font-style: inherit; color: rgb(0, 0, 0);'>");
        var newTexto2 = newTexto1.replace(/@@2@@/g, '</em>');
        nombres[i].innerHTML = newTexto2;
    }
    return true;
}

function marcarBusquedaCodigo(textoBdaC) {
    var nombres = $("td.resultadoBdaCodigo > span");
    for (i = 0; i < nombres.length; i++) {
        var texto = nombres[i].innerHTML;
        texto = texto.toUpperCase();
        textoBdaC = textoBdaC.toUpperCase();
        var newTexto = texto.replace(textoBdaC, "@@1@@" + textoBdaC + '@@2@@');
        nombres[i].innerHTML = newTexto;
    }
    return true;
}

function marcarBusquedaCodigoColor() {
    var nombres = $("td.resultadoBdaCodigo > span");
    for (i = 0; i < nombres.length; i++) {
        var texto = nombres[i].innerHTML;
        texto = texto.toUpperCase();
        var newTexto1 = texto.replace(/@@1@@/g, "<em style='background-color: rgb(255, 255, 102); font-style: inherit; color: rgb(0, 0, 0);'>");
        var newTexto2 = newTexto1.replace(/@@2@@/g, '</em>');
        nombres[i].innerHTML = newTexto2;
    }
    return true;
}

function enviarNuevoItemReg() {
    cerrarDialogoModal(dialogoItemsRequi);
    abrirDialogoModal(dialogoItemsRequiNewArt);
    return true;
}

function cancelarNuevoItemReg() {
    cerrarDialogoModal(dialogoItemsRequiNewArt);
    abrirDialogoModal(dialogoItemsRequi);
    return true;
}

function validarDescArticulo() {
    if (validarCajaTexto("frmItemReqNewArt\\:descNewArt")) {
        bootbox.alert("Se requiere capturar el nombre del nuevo artículo.")
        return false;
    } else if (!validarTamTexto("frmItemReqNewArt\\:descNewArt", 0, 2048)) {
        bootbox.alert("Se requiere que el nombre del nuevo articulo no sea mayor a 2048 caracteres.")
        return false;
    }
    //else if (validarCajaTexto("frmItemReqNewArt\\:descNewArtUso")) {
    //    bootbox.alert("Se requiere capturar donde será utilizado el nuevo artículo.")
    //    return false;
    //}    
    return true;
}

function cambiarColor(elemento) {
    //alert('ele ' + elemento);
    $('#' + elemento).removeClass('glyphicon glyphicon-download btn btn-warning');
    $('#' + elemento).addClass('glyphicon glyphicon-download btn btn-primary');
}


function regresarEts(divDatos, divOperacion, divAuto, divTabla) {

    ocultarDiv(divTabla);
    ////                   
    ocultarDiv(divAuto);
    ////                   
    mostrarDiv(divDatos);
    ////                   
    mostrarDiv(divOperacion);
//
//limpiar seleccio
}

//<![CDATA[
function graficaTotales(datos, grafica, titulo) {
    var campos = datos.name;
    var total = datos.y;
    var chart = new Highcharts.Chart({
        chart: {
            renderTo: grafica,
            zoomType: 'xy'
        },
        title: {
            text: '' + titulo
        },
        xAxis: {
            categories: campos,
            labels: {
                style: {
                    fontSize: '13px',
                    fontFamily: 'Verdana, sans-serif'
                }
            }
        },
        yAxis: [{// primary yAxis
                min: 0,
                title: {
                    text: 'Total OC/S'
                }
            }],
        tooltip: {
            valueDecimals: 2,
            shared: true
        },
        legend: {
            enabled: true,
            layout: 'vertical',
            align: 'left',
            x: 100,
            verticalAlign: 'top',
            y: 50,
            floating: true
        },
        series: [{
                colorByPoint: true,
                name: 'Total de OC/S',
                type: 'column',
                data: total,
                dataLabels: {
                    enabled: true,
                    rotation: 0,
                    color: 'blue',
                    align: 'center',
                    x: 0,
                    y: 5,
                    style: {
                        fontSize: '13px',
                        fontFamily: 'Verdana, sans-serif',
                        textShadow: '0 0 1px black'
                    }
                }
            }]
    });
}
function graficaTotalesOld(datos, grafica, titulo) {

// Make monochrome colors
    var pieColors = (function () {
        var colors = [],
                base = Highcharts.getOptions().colors[0],
                i;

        for (i = 0; i < 10; i += 1) {
            colors.push(Highcharts.Color(base).brighten((i - 3) / 7).get());
        }
        return colors;
    }());
    new Highcharts.Chart({
        title: {
            text: titulo
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                colors: pieColors,
                colorByPoint: true,
                dataLabels: {
                    enabled: true,
                    style: {
                        color: (Highcharts.theme) || 'black'
                    },
                    distance: 1,
                    useHTML: false,
                    format: '<b>{point.name}</b><br> $ ' + '{point.y:,.3f}'
                },
                showInLegend: false
            }
        },
        chart: {
            renderTo: grafica,
            type: 'pie'
        },
        series: [{
                name: 'Total',
                data: datos,
                dataLabels: {
                    color: 'black'
                },
                point: {
                    events: {
                        click: function () {
                            // alert('Proveedor: ' + this.name);
                        }
                    }
                }
            }]
    });

}
function formatoMoneda(val) {
    val.replace(/\D/g, "")
            .replace(/([0-9])([0-9]{2})$/, '$1.$2')
            .replace(/\B(?=(\d{3})+(?!\d)\.?)/g, ",");
    return  val;
}
//    ]]>


function resetTabs() {
    $("#tabOCSProcCompras").tabs({active: 0});
    return true;
}

function validaExportarZip(frm, seleccion) {
    var val = false;
    if (verificaSeleccion(seleccion) > 0) {
        if (confirmar(frm, "La operación exportara los archivos de la(s) factura(s) seleccionada(s). ¿Está seguro?")) {
            $(".todos ").prop("checked", "");
            val = true;
        } else {
            val = false;
        }
    } else {
        bootbox.alert(mensajeNoSeleccion);
    }
    return val;
}

function graficaFacturadContenido(grafica, datos, titulo) {
    var proveedores = datos.proveedores;
    var facturado = datos.facturado;
    var contenido = datos.contenido;
    var porcentaje = datos.porcentaje;
    var chart = new Highcharts.Chart({
        chart: {
            renderTo: grafica,
            zoomType: 'xy',
            type: 'column'
        },
        title: {
            text: titulo
        },
        xAxis: {
            categories: proveedores,
            crosshair: true,
            labels: {
                rotation: 0
            }
        },
        yAxis: [{
                min: 0,
                title: {
                    text: 'Total'
                },
                labels: {
                    style: {
                        color: Highcharts.getOptions().colors[0]
                    }
                }
            }, {// Secondary yAxis
                title: {
                    text: 'Contenido Nacional',
                    style: {
                        color: 'red'
                    }
                },
                labels: {
                    format: '{value} %',
                    style: {
                        color: 'red'
                    }
                },
                opposite: true
            }],
        tooltip: {
            valueDecimals: 0,
            shared: true
        },
        legend: {
            enabled: true,
            layout: 'horizontal',
            align: 'center',
            x: 120,
            verticalAlign: 'top',
            y: 20,
            floating: true
        },
        series: [{
                name: '% C.N.',
                data: porcentaje,
                type: 'scatter',
                yAxis: 1,
                tooltip: {
                    valueSuffix: ' %'
                },
                dataLabels: {
                    enabled: true,
                    rotation: 0,
                    color: 'red',
                    align: 'center',
                    style: {
                        fontSize: '13px',
                        fontFamily: 'Verdana, sans-serif'
                    }
                }
            }, {
                name: 'Facturado',
                data: facturado,
                dataLabels: {
                    rotation: -45,
                    enabled: true,
                    color: 'blue',
                    align: 'center',
                    style: {
                        fontSize: '13px',
                        fontFamily: 'Verdana, sans-serif',
                        textShadow: '0 0 1px black'
                    }
                },
                point: {
                    events: {
                        click: function () {
                            //var comprador = this.category
                            // ("frmOCSSolDevCan", "btnBuscarPorComprador", "hidenTab", 1)
                        }
                    }
                }
            }, {
                name: 'Contenido',
                data: contenido,
                dataLabels: {
                    enabled: true,
                    rotation: -45,
                    color: 'black',
                    align: 'center',
                    style: {
                        fontSize: '13px',
                        fontFamily: 'Verdana, sans-serif',
                        textShadow: '0 0 1px black'
                    }
                },
                point: {
                    events: {
                        click: function () {
                            //var comprador = this.category
                            //cambiarTab("frmOCSSolDevCan", "btnBuscarPorComprador", "hidenTab", 2)                            
                        }
                    }
                }
            }
        ]
    });
}
