/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



function validaMail(frm, idCorreo) {
    var sel = 0;
    var v = false;
    $(".compania").each(function () {
        if ($(this).is(':checked')) {
            sel++;
        }
    });
    if (sel > 0) {
        var mail = $("#" + frm + "\\:" + idCorreo).val(); //document.getElementsByid(idCorreo).value;
        expr = /^([a-zA-Z0-9_\.\-])+\@(([a-z\-]{2,3})+\.)+([a-z]{2,4})+$/;
        if (!expr.test(mail)) {
            alert("Error: La dirección de correo " + mail + " es incorrecta.");
            v = false;
        } else {
            //alert("OK: La dirección de correo " + mail + " correcta.");
            v = true;
        }
    } else {
        alert("Seleccione la compania. ");
        v = false;
    }
    return v;
}


function verificaMail(frm, idCorreo) {
    var v = false;
    var mail = $("#" + frm + "\\:" + idCorreo).val();
    expr = /^([a-zA-Z0-9_\.\-])+\@(([a-z\-]{2,3})+\.)+([a-z]{2,4})+$/;
    if (!expr.test(mail)) {
        alert("Error: La dirección de correo " + mail + " es incorrecta.");
        v = false;
    } else {
        //alert("OK: La dirección de correo " + mail + " correcta.");
        v = true;
    }
    return v;
}

function validaAgregarRelacion() {
    var e = 0;
    if (validarCajaTexto("frmRelPro\\:autocomplete")) {
        if (validarCajaTexto("frmRelPro\\:numRef")) {

        } else {
            alert('Agregue un número de referencia');
            e++;
        }
    } else {
        alert('Seleccione un proveedor');
        e++;
    }
    return e > 0 ? false : true;
}



function validarCajaTexto(idComponente) {
    if ($("#" + idComponente).val() == "" || $("#" + idComponente).val() == undefined || $("#" + idComponente).val() == null) {
        return false;
    } else {
        return true;
    }
}



function validarTamTexto(idComponente, min, max) {
    if (($("#" + idComponente).val().length >= min) && ($("#" + idComponente).val().length <= max)) {
        return true;
    } else {
        return false;
    }
}

function dialogoCambiarUsuarioBloque(dialogo) {
    //    alert(dialogo);
    $("#" + dialogo).dialog({
        modal: true,
        closeOnEscape: false,
        hide: "explode",
        buttons: {
            Cerrar: function () {
                $("#" + dialogo).dialog("close");
            }
        }
    });
    $(".ui-dialog-titlebar-close").hide();
}


function mostrarDialogo(dialogo, forma, btnCerrar, btnGuardar) {
    $("#" + dialogo).dialog({
        modal: true,
        closeOnEscape: false,
        hide: "explode",
        resizable: false,
        draggable: false,
        height: 'auto',
        width: 'auto',
        buttons: {
            Guardar: function () {
                if (validaAgregarOT(forma, 'cajaNombre', 'cuentaTxt')) {
                    ejecutarBoton(forma, btnGuardar);
                    dialogoOK(dialogo);
                }
            },
            Cerrar: function () {
                ejecutarFiltros(forma, btnCerrar);
                $("#" + dialogo).dialog("close");
            }
        },
        open: function (event, ui) {
            // Get the dialog 
            var dialog = $(event.target).parents(".ui-dialog");
            // Get the buttons 
            var buttons = dialog.find(".ui-dialog-buttonpane").find(".ui-button");
            //
            var okButton = buttons[0];
            $(okButton).css({
                "color": "white",
                "background": "#0895d6"
            });
        }
    });
    $(".ui-dialog-titlebar-close").hide();
}


function mostrarDialogoUnaCaja(dialogo, forma, btnCerrar, btnGuardar, caja) {
    $("#" + dialogo).dialog({
        modal: true,
        closeOnEscape: false,
        hide: "explode",
        resizable: false,
        draggable: false,
        height: 'auto',
        width: 'auto',
        buttons: {
            Guardar: function () {
                if (validarCajaTexto(forma + '\\:' + caja)) {
                    ejecutarBoton(forma, btnGuardar);
                } else {
                    alert("Es necesario agregar un valor.");
                }
            },
            Cerrar: function () {
                ejecutarFiltros(forma, btnCerrar);
                $("#" + dialogo).dialog("close");
            }
        },
        open: function (event, ui) {
            // Get the dialog 
            var dialog = $(event.target).parents(".ui-dialog");
            // Get the buttons 
            var buttons = dialog.find(".ui-dialog-buttonpane").find(".ui-button");
            //
            var okButton = buttons[0];
            $(okButton).css({
                "color": "white",
                "background": "#0895d6"
            });
        }
    });
    $(".ui-dialog-titlebar-close").hide();
    return true;
}

function mostrarDialogoDosCaja(dialogo, forma, btnCerrar, btnGuardar, caja1, caja2) {
    $("#" + dialogo).dialog({
        modal: true,
        closeOnEscape: false,
        hide: "explode",
        resizable: false,
        draggable: false,
        height: 'auto',
        width: 'auto',
        buttons: {
            Guardar: function () {
                if (validarCajaTexto(forma + '\\:' + caja1)) {
                    if (validarCajaTexto(forma + '\\:' + caja2)) {
                        ejecutarBoton(forma, btnGuardar);
                    } else {
                        alert("Es necesario agregar el segundo valor.");
                    }
                } else {
                    alert("Es necesario agregar el primer valor.");
                }
            },
            Cerrar: function () {
                ejecutarFiltros(forma, btnCerrar);
                $("#" + dialogo).dialog("close");
            }
        },
        open: function (event, ui) {
            // Get the dialog 
            var dialog = $(event.target).parents(".ui-dialog");
            // Get the buttons 
            var buttons = dialog.find(".ui-dialog-buttonpane").find(".ui-button");
            //
            var okButton = buttons[0];
            $(okButton).css({
                "color": "white",
                "background": "#0895d6"
            });
        }
    });
    $(".ui-dialog-titlebar-close").hide();
    return true;
}

function mostrarDialogoModCadenas(dialogo, forma, btnCerrar, btnGuardar, caja1, caja2) {
    $("#" + dialogo).dialog({
        modal: true,
        closeOnEscape: false,
        hide: "explode",
        resizable: false,
        draggable: false,
        height: 'auto',
        width: 'auto',
        buttons: {
            Guardar: function () {
                if (validarCajaTexto(forma + '\\:' + caja1) || validarCajaTexto(forma + '\\:' + caja2)) {
                    ejecutarBoton(forma, btnGuardar);
                } else {
                    alert("Es necesario llenar al menos un campo");
                }
            },
            Cerrar: function () {
                ejecutarFiltros(forma, btnCerrar);
                $("#" + dialogo).dialog("close");
            }
        },
        open: function (event, ui) {
            // Get the dialog 
            var dialog = $(event.target).parents(".ui-dialog");
            // Get the buttons 
            var buttons = dialog.find(".ui-dialog-buttonpane").find(".ui-button");
            //
            var okButton = buttons[0];
            $(okButton).css({
                "color": "white",
                "background": "#0895d6"
            });
        }
    });
    $(".ui-dialog-titlebar-close").hide();
    return true;
}

function mostrarDialogoAgregarCadena(dialogo, forma, btnCerrar, btnGuardar, solicita, revisa, aprueba) {
    $("#" + dialogo).dialog({
        modal: true,
        closeOnEscape: false,
        hide: "explode",
        resizable: false,
        draggable: false,
        height: 'auto',
        width: 'auto',
        buttons: {
            Guardar: function () {
                if (validarCajaTexto(forma + '\\:' + solicita)) {
                    if (validarCajaTexto(forma + '\\:' + revisa)) {
                        if (validarCajaTexto(forma + '\\:' + aprueba)) {
                            ejecutarBoton(forma, btnGuardar);
                        } else {
                            alert("Es necesario agregar el tercervalor.");
                        }
                    } else {
                        alert("Es necesario agregar el segundo valor.");
                    }
                } else {
                    alert("Es necesario agregar el primer valor.");
                }
            },
            Cerrar: function () {
                ejecutarFiltros(forma, btnCerrar);
                $("#" + dialogo).dialog("close");
            }
        },
        open: function (event, ui) {
            // Get the dialog 
            var dialog = $(event.target).parents(".ui-dialog");
            // Get the buttons 
            var buttons = dialog.find(".ui-dialog-buttonpane").find(".ui-button");
            //
            var okButton = buttons[0];
            $(okButton).css({
                "color": "white",
                "background": "#0895d6"
            });
        }
    });
    $(".ui-dialog-titlebar-close").hide();
    return true;
}

function limpiarComponenteCaja() {
    $(".iceInpTxt").val("");
}



function mostrarDialogoCombo(dialogo, forma, btnCerrar, btnGuardar, cmb) {
    var c = forma + ':' + cmb;
    $("#" + dialogo).dialog({
        modal: true,
        closeOnEscape: false,
        hide: "explode",
        resizable: false,
        draggable: false,
        height: 'auto',
        width: 'auto',
        buttons: {
            Guardar: function () {
                if (valorCombo(c, -1)) {
                    ejecutarBoton(forma, btnGuardar);
                    //     dialogoOK(dialogo, forma);
                } else {
                    alert("Es necesario seleccionar un valor");
                }
            },
            Cerrar: function () {
                ejecutarBoton(forma, btnCerrar);
                $("#" + dialogo).dialog("close");
            }
        },
        open: function (event, ui) {
            // Get the dialog 
            var dialog = $(event.target).parents(".ui-dialog");
            // Get the buttons 
            var buttons = dialog.find(".ui-dialog-buttonpane").find(".ui-button");
            var okButton = buttons[0];
            $(okButton).css({
                "color": "white",
                "background": "#0895d6"
            });
        }
    });

    $(".ui-dialog-titlebar-close").hide();
}

////Diaolog para usuario-bloque}


function mostrarDialogoBloqueUsuario(dialogo, forma, btnCerrar, btnGuardar, cajaUsuario, cajaPuesto, cmb) {
    var cu = forma + "\\:" + cajaUsuario;
    var cp = forma + "\\:" + cajaPuesto;
    var combo = forma + "\\:" + cmb;

    $("#" + dialogo).dialog({
        modal: true,
        closeOnEscape: false,
        hide: "explode",
        resizable: false,
        draggable: false,
        height: 'auto',
        width: 'auto',
        buttons: {
            Guardar: function () {
                if (validarCajaTexto(cu)) {
                    if (validarCombo(combo, -1)) {
                        if (validarCajaTexto(cp)) {
                            ejecutarBoton(forma, btnGuardar);
                        } else {
                            alert("Es necesario seleccionar un puesto.");
                        }
                    } else {
                        alert("Es necesario seleccionar un bloque.");
                    }
                } else {
                    alert("Es necesario agregar un usuario.");
                }
            },
            Cerrar: function () {
                ejecutarBoton(forma, btnCerrar);
                $("#" + dialogo).dialog("close");
            }
        },
        open: function (event, ui) {
            // Get the dialog 
            var dialog = $(event.target).parents(".ui-dialog");
            // Get the buttons 
            var buttons = dialog.find(".ui-dialog-buttonpane").find(".ui-button");
            var okButton = buttons[0];
            $(okButton).css({
                "color": "white",
                "background": "#0895d6"
            });
        }
    });
    $(".ui-dialog-titlebar-close").hide();
}


function valorCombo(formaycmb, valorComparar) {
    //alert(cmb);
    var se = $("select[name='" + formaycmb + "'] option:selected").val();
    if (se != valorComparar) {
        return true;
    } else {
        return false;
    }
}
function validarCombo(formYid, valorAComparar) {
    //oficinaOrigen
    //  alert('Algos: ' + formYid );
    var se = jQuery("#" + formYid + " option:selected");
    // alert('valor: ' + se.val());
    if (se.val() != valorAComparar) {
        return true;
    } else {
        return false;
    }
}



function validaAgregarOT(forma, nombre, cuenta) {
    var val = false;
    if (validarCajaTexto(forma + '\\:' + nombre)) {
        if (validarCajaTexto(forma + '\\:' + cuenta)) {
            val = true;
        } else {
            alert('Es necesario agregar la cuenta contable');
        }
    } else {
        alert('Es necesario agregar el nombre');
    }
    return val;
}


function ejecutarBoton(frm, boton) {
    $("#" + frm + "\\:" + boton).click();
}
function validaAsignar() {
    var val = false;
    if (validarCombo("form1\\:listaAnalistas", -1)) {
        $("#msgAsignaRequisicion").text("");
        val = true;
    } else {
        alert("Es necesario seleccionar al menos un analista \n");
    }

    return val;
}



////////////////////////////////////////////////////////////////////////



function dialogoOK(dialogo) {
    $("#" + dialogo).dialog("close");
    $("#" + 'dialogOK').dialog({
        closeOnEscape: false,
        hide: "explode",
        resizable: false,
        draggable: false,
        height: 'auto',
        width: 'auto',
        buttons: {
            Cerrar: function () {
                $("#" + 'dialogOK').dialog("close");
            }
        }
    });
    $(".ui-dialog-titlebar-close").hide();
    setTimeout('cerrarDialogo(dialogOK)', 5000);
}

function cerrarDialogo(dialogo) {
    $("#" + dialogo).dialog("close");
}

function alertaGeneral(mensaje) {
    alert(mensaje);

}

/////////////////////
function confirmar(mensaje) {
    if (confirm(mensaje)) {
        return  true;
    } else {
        return false;
    }
}

function llenarEtiqueta(forma, etiqueta, valor) {
    //   alert(valor);
    $("#" + forma + "\\:" + etiqueta).text(valor);
}


function seleccionarTodo(todos, seleccion) {
    if ($("." + todos).is(":checked")) {
        $("." + seleccion).prop("checked", "checked");
    } else {
        $("." + seleccion).prop("checked", "");
    }
}



function validaPasar(seleccion) {
    var val = false;
    if (verificaSeleccion(seleccion) > 0) {
        if (validarCajaTexto("frmCambioUsuarioOrden\\:autoUsuarioAprobara")) {
            if (confirm("La operación va a pasar las filas seleccionadas a otro usuario.  ¿Está seguro?")) {
                //bloquearPantalla();
                $(".todos ").prop("checked", "");
                val = true;
            } else {
                val = false;
            }
        } else {
            alert("Es necesario seleccionar al usuario que recibirá el trabajo.");
        }
    } else {
        alert("Es necesario seleccionar al menos una fila");
    }
    return val;
}

function validaEliminar(seleccion) {
    var val = false;
    if (verificaSeleccion(seleccion) > 0) {
        if (confirm("La operación va a Eliminar las cadenas de Mando seleccionadas.  ¿Está seguro?")) {
            //bloquearPantalla();
            $(".todos ").prop("checked", "");
            val = true;
        } else {
            val = false;
        }

    } else {
        alert("Es necesario seleccionar al menos una fila");
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
function validaRadio(frm, radio) {
    var che = 0

    if ($("#" + frm + "\\:" + radio.is(':checked'))) {
        che++;
    }
    alert(che);
    return che;
}

function cambiarValorCombo(forma, btn) {
    $("#" + forma + "\\:" + btn).click();
}

function validaBuscarOrden(frm, campo) {
    var valor = $("input:radio[id=#" + frm + "\\:" + campo + "]:checked").val()
    //alert(valor);
    if (valor) {
        return true;
    } else {
        alert("Seleccione el bloque . . . ");
        return false;
    }
}

function validaModificarUsuario(frmModUsr, cmbCampo, cmbOficina, cmbGerencia, cmbNomina) {
    // alert("asajdlsajdlsd");
    if (validarCombo(frmModUsr + "\\:" + cmbCampo, -1)) {
        if (validarCombo(frmModUsr + "\\:" + cmbOficina, -1)) {
            if (validarCombo(frmModUsr + "\\:" + cmbGerencia, -1)) {
                if (validarCombo(frmModUsr + "\\:" + cmbNomina, -1)) {
                    return true;
                } else {
                    alert("Seleccione una nomina");
                    return false;
                }
            } else {
                alert("Seleccione una gerencia");
                return false;
            }
        } else {
            alert("Seleccione una Oficina");
            return false;
        }
    } else {
        alert("Seleccione una Campo");
        return false;
    }
}

//
function validaCategoriaAdjunto(frm, cmbGerencia, cmbCategoria) {
    if (validarCombo(frm + '\\:' + cmbGerencia, -1)) {
        if (validarCombo(frm + '\\:' + cmbCategoria, -1)) {
            return true;
        } else {
            alert('Seleccione una categoría');
        }
    } else {
        alert('Seleccione una gerencia');
    }
    return false;
}
function validaRelacionCampo() {
    var e = 0;
    if (validarCajaTexto("frmRelCampoPro\\:autocomplete")) {
        e++;
    } else {
        alert('Seleccione un proveedor');
    }
    return e > 0 ? true : false;
}


function mostrarDialogoContacto(dialogo, forma, btnCerrar, btnGuardar) {
    $("#" + dialogo).dialog({
        modal: true,
        closeOnEscape: false,
        hide: "explode",
        resizable: false,
        draggable: false,
        height: 'auto',
        width: 'auto',
        buttons: {
            Guardar: function () {
                ejecutarBoton(forma, btnGuardar);
                dialogoOK(dialogo);
            },
            Cerrar: function () {
                ejecutarFiltros(forma, btnCerrar);
                $("#" + dialogo).dialog("close");
            }
        },
        open: function (event, ui) {
            // Get the dialog 
            var dialog = $(event.target).parents(".ui-dialog");
            // Get the buttons 
            var buttons = dialog.find(".ui-dialog-buttonpane").find(".ui-button");
            //
            var okButton = buttons[0];
            $(okButton).css({
                "color": "white",
                "background": "#0895d6"
            });
        }
    });
    $(".ui-dialog-titlebar-close").hide();
}

function mostrarDialogoModificarProveedor(dialogo, forma, btnCerrar, btnGuardar, caja) {
    // alert(caja);
    $("#" + dialogo).dialog({
        modal: true,
        closeOnEscape: false,
        hide: "explode",
        resizable: false,
        draggable: false,
        height: 'auto',
        width: 'auto',
        buttons: {
            Guardar: function () {
                if (validarCajaTexto(forma + "\\:" + caja)) {
                    ejecutarBoton(forma, btnGuardar);
                    dialogoOK(dialogo);
                } else {
                    alert('Agregue el nombre del proveedor.');
                }
            },
            Cerrar: function () {
                ejecutarFiltros(forma, btnCerrar);
                $("#" + dialogo).dialog("close");
            }
        },
        open: function (event, ui) {
            // Get the dialog 
            var dialog = $(event.target).parents(".ui-dialog");
            // Get the buttons 
            var buttons = dialog.find(".ui-dialog-buttonpane").find(".ui-button");
            //
            var okButton = buttons[0];
            $(okButton).css({
                "color": "white",
                "background": "#0895d6"
            });
        }
    });
    $(".ui-dialog-titlebar-close").hide();
}
function validaRol(seleccion) {
    var val = false;
    if (verificaSeleccion(seleccion) > 0) {
        if (confirm("La operación va a añadir el  rol seleccionado. ¿Está seguro?")) {
            //     bloquearPantalla();
            $(".todos ").prop("checked", "");
            val = true;
        } else {
            val = false;
        }
    } else {
        alert("Es necesario seleccionar al menos una opcion");
    }
    return val;
}


function refrescar() {
    location.reload();
    return true;
}



function cerrarDialogoBootstrap(dialogo) {
    $(dialogo).modal('hide');
}


function cerrarDialogoModal(dialogo) {
    $(dialogo).modal('hide');
}

function abrirDialogoModal(dialogo) {
    $(dialogo).modal('show');
}

function abrirDialogoBootstrap(dialogo) {
    $(dialogo).modal('show');
}

function validarAgregarArticulo(elemento) {
    v = true;
    if (!validarCajaTexto(elemento)) {
        v = false;
        alert('Es necesario agregar el artículo.');
    } else if (!validarTamTexto(elemento, 0, 2048)) {
        alert("Se requiere que el nombre del nuevo articulo no sea mayor a 2048 caracteres.")
        return false;
    } else {
        abrirDialogoBootstrap(registrarArticulo);
    }
    return v;
}

function validaGuardarCategoria(eleNombre, eleCodigo) {
    v = true;
    if (validarCajaTexto(eleNombre)) {
        if (validarCajaTexto(eleCodigo)) {
            v = true;
        } else {
            v = false;
            alert('Es necesario agregar el código.');
        }
    } else {
        v = false;
        alert('Es necesario agregar la categoría.');
    }
    return v;
}
function validaCambiarArticulo(seleccion) {
    if (verificaSeleccion(seleccion) > 0) {
        abrirDialogoBootstrap(dialogoCambiarArticulos);
    } else {
        alert('Es necesario seleccionar al menos un elemento de la lista.');
    }
    return false;
}

function validaAgregarArticuloCampo(seleccion) {
    v = false;
    if (verificaSeleccion(seleccion) > 0) {
        v = true;
    } else {
        alert('Es necesario seleccionar al menos un elemento de la lista.');
    }
    return v;
}

function cerrarDialogoCrearMoneda() {
    cerrarDialogoModal(dialogoPopUpMoneda);
}

function abrirDialogoCrearMoneda() {
    abrirDialogoModal(dialogoPopUpMoneda);
}

function cerrarDialogoCrearParidad() {
    cerrarDialogoModal(dialogoPopUpParidad);
}

function abrirDialogoCrearParidad() {
    abrirDialogoModal(dialogoPopUpParidad);
}

function cerrarDialogoCrearParidadValor() {
    cerrarDialogoModal(dialogoPopUpParidadValor);
}

function abrirDialogoCrearParidadValor() {
    abrirDialogoModal(dialogoPopUpParidadValor);
}

function cerrarDialogoCrearImpuesto() {
    cerrarDialogoModal(dialogoPopUpImpuesto);
}

function abrirDialogoCrearImpuesto() {
    abrirDialogoModal(dialogoPopUpImpuesto);
}

function abrirDialogoCrearVetado() {
    abrirDialogoModal(dialogoPopUpVetado);
}

function cerrarDialogoCrearVetado() {
    cerrarDialogoModal(dialogoPopUpVetado);
}

function getMesActual() {
    switch (new Date().getMonth()) {
        case 0:
            $('.tabEnero').addClass("active");
            $('#Enero').addClass('active');
            break;
        case 1:
            $('.tabFebrero').addClass("active");
            $('#Febrero').addClass('active');
            break;
        case 2:
            $('.tabMarzo').addClass('active');
            $('#Marzo').addClass('active');
            break;
        case 3:
            $('.tabAbril').addClass("active");
            $('#Abril').addClass('active');
            break;
        case 4:
            $('.tabMayo').addClass("active");
            $('#Mayo').addClass('active');
            break;
        case 5:
            $('.tabJunio').addClass("active");
            $('#Junio').addClass('active');
            break;
        case 6:
            $('.tabJulio').addClass("active");
            $('#Julio').addClass('active');
            break;
        case 7:
            $('.tabAgosto').addClass("active");
            $('#Agosto').addClass('active');
            break;
        case 8:
            $('.tabSeptiembre').addClass("active");
            $('#Septiembre').addClass('active');
            break;
        case 9:
            $('.tabOctubre').addClass("active");
            $('#Octubre').addClass('active');
            break;
        case 10:
            $('.tabNoviembre').addClass("active");
            $('#Noviembre').addClass('active');
            break;
        case 11:
            $('.tabDiciembre').addClass("active");
            $('#Diciembre').addClass('active');
            break;
    }
}

function mostrarDiv(divM) {
    $("#" + divM).css({"display": "block"});
}

function ocultarDiv(divOcultar) {
    $("#" + divOcultar).css({
        "display": "none"
    });
}

function enviarNotificacionNuevoItem() {
    cerrarDialogoBootstrap(registrarArticulo);
    abrirDialogoBootstrap(dialogoNotificarNuevoItem);
    return true;
}

function validarUsuarioNotificacion(elemento) {
    v = true;
    if (!validarCajaTexto(elemento)) {
        v = false;
        alert('Es necesario seleccionar un usuario para la notificación.');
    }
    return v;
}

function marcarBusqueda() {
    var textoBda = $("#frmCategoria\\:idBusquedaAvArticulos").val();
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


function cambiarColor(elemento) {
    //alert('ele ' + elemento);
    $('#' + elemento).removeClass('btn btn-warning');
    $('#' + elemento).addClass('btn btn-primary');
}

function minimizarArtSat() {
    $('#collapseOnePSat').removeClass('panel-collapse collapse in');
    $('#collapseOnePSat').addClass('panel-collapse collapse');
    $('#collapseOnePSat').style.display = 'none';
    $('#collapseOnePSat').style.height = '0px;';
}

function muestraPresupuesto(divMostrar, divOcultar, divOperacion, divAuto) {

    ///
    mostrarDiv(divMostrar);
    ////                   
    ocultarDiv(divOcultar);
    ////                   
    mostrarDiv(divOperacion);
    ////                   
    ocultarDiv(divAuto);
}

function validarSelects(forma) {
    var selects = $("#" + forma + " .selectt");
    for (i = 0; i < selects.length; i++) {
        if (validarSE(selects[i], 0)) {
            return true;
        }
    }
    return false;
}

function validarForma(forma) { 

    if (validarTxts(forma)) {
        alert("Los datos marcados en rojo son requeridos.");
        return false;
    }

    if (validarSelects(forma)) {
        alert("Los datos marcados en rojo son requeridos.");
        return false;
    }
    return true;
}

function validarSE(componente, valorAComparar) {
    if ($(componente).val() != valorAComparar) {
        $(componente).removeClass("errorSIA");
        return false;
    } else {
        $(componente).addClass("errorSIA");
        return true;
    }
}

function validarTxts(forma) {    
	var texts = $("#"+forma+" .texOb");
	for (i = 0; i < texts.length; i++) {
    		if(validarComponenteTexto(texts[i])){
			return true;
		}
	}
	return false;
}

function validarComponenteTexto(componente){
    if(($(componente).val() == "" || $(componente).val() == undefined || $(componente).val() == null)){
	$(componente).addClass("errorSIA");
	return true;
    }else{
	$(componente).removeClass("errorSIA");
        return false;
    }
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



