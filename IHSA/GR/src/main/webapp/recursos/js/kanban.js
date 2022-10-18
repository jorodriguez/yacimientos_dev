// <![CDATA[ 
var viajeroIDTxt = "";
var viajeIdTxt = "";
var solViajeIdTxt = "";
function draggableInit() {
    //alert("AA");
    $(".solicitudDraggable").bind('dragstart', function (event) {
        if (solViajeIdTxt != 'NO999@' && $(this).attr('id').search('solViaje') > -1) {
            //alert("solicitudDraggable::"+solViajeIdTxt+"::"+viajeroIDTxt);
            solViajeIdTxt = $(this).attr('id');
            viajeroIDTxt = 'NO999@';
            event.originalEvent.dataTransfer.setData("text/plain", event.target.getAttribute('id'));
        }
    });

    $(".viajeroDraggable").bind('dragstart', function (event) {
        if (viajeroIDTxt != 'NO999@' && $(this).attr('id').search('viajero') > -1) {
            //alert("viajeroDraggable::"+solViajeIdTxt+"::"+viajeroIDTxt);
            viajeroIDTxt = $(this).attr('id');
            solViajeIdTxt = 'NO999@';
            event.originalEvent.dataTransfer.setData("text/plain", event.target.getAttribute('id'));
        }
    });

    $(".bodyViajeItem").bind('dragstart', function (event) {
        if ($(this).attr('id').search('viaje') > -1) {
            viajeIdTxt = $(this).attr('id');
            solViajeIdTxt = 'NO999@';
            viajeroIDTxt = 'NO999@';
            event.originalEvent.dataTransfer.setData("text/plain", event.target.getAttribute('id'));
        }
    });

    $('.panel-body').bind('dragover', function (event) {
        event.preventDefault();
    });

    $('.bodyViajeItem').bind('dragover', function (event) {
        event.preventDefault();
    });

    $('.bodyViajeCreado').bind('drop', function (event) {
        event.preventDefault();
    });

    $('.bodyNoDragable').bind('drop', function (event) {
        event.preventDefault();
    });

    $('.bodyViajeItem').bind('drop', function (event) {
        viajeIdTxt = $(this).attr('id');
        var viajeroID = 0;
        var solViajeID = 0;
        var msgConfir;
        //alert(viajeIdTxt+"::"+solViajeIdTxt+"::"+viajeroIDTxt);
        if (viajeroIDTxt === 'NO999@') {
            solViajeID = Number(solViajeIdTxt.replace('solViaje', ''));
            viajeroID = 0;
            msgConfir = 'Se agregaran los viajeros al viaje.';
        } else if (solViajeIdTxt === 'NO999@') {
            viajeroID = Number(viajeroIDTxt.replace('viajero', ''));
            solViajeID = 0;
            msgConfir = 'Se agregara el viajero al viaje.';
        }
        var viajeId = Number(viajeIdTxt.replace('viaje', ''));
        //alert(viajeId+"::"+solViajeID+"::"+viajeroID);
        if (viajeId > 0 && (solViajeID > 0 || viajeroID > 0)) {
            var urlAjax = '/ServiciosGenerales/resources/serviciosWebSGyL/agregarViajeroViaje/'
                    + viajeId + '/' + solViajeID + '/' + viajeroID + '/' + $('.usrSesion').val();
            $('#processing-modal').modal('toggle'); //before post
            jQuery.ajax({
                url: urlAjax,
                async: true,
                success: function (result) {
                    $('#processing-modal').modal('toggle'); // after post
                    ejecutarBoton('frmCrearViaje', 'btnRecargarSol');
                },
                error: function (request) {
                    $('#processing-modal').modal('toggle'); // after post
                    if (request.responseText + '' === 'ErrorViaje') {
                        bootbox.alert('El viaje no fue ubicado correctamente, intentelo de nuevo.');
                    } else if (request.responseText + '' === 'ErrorLibres') {
                        bootbox.alert('El viaje no cuenta con lugares libres para mas viajeros.');
                    } else if (request.responseText + '' === 'ErrorVroCantidad') {
                        bootbox.alert('El viaje no cuenta con lugares libres para mas viajeros.');
                    } else if (request.responseText + '' === 'ErrorVroRuta') {
                        bootbox.alert('No coincide la ruta del viaje con la del viajero.');
                    } else if (request.responseText + '' === 'ErrorSolCantidad') {
                        bootbox.alert('El viaje no cuenta con suficientes lugares libres para todos los viajeros de la solicitud.');
                    } else if (request.responseText + '' === 'ErrorSolRuta') {
                        bootbox.alert('No coincide la ruta del viaje con la ruta  de la solicitud.');
                    } else if (request.responseText + '' === 'ErrorFechaSalida') {
                        bootbox.alert('La fecha de salida de la solicitud es menor a la del viaje.');
                    } else if (request.responseText + '' === 'ERRORLOG') {
                        bootbox.alert('Ocurrió una excepción, favor de comunicar a sia@ihsa.mx');
                    } else if (request.responseText + '' === 'ErrorRutaDirecto') {
                        urlAjax = urlAjax + '/' + 'true'
                        bootbox.confirm({
                            message: 'La ruta del viaje y la ruta de la solicitud no coinciden. El viaje sera con escala..? ',
                            callback: function (result) {
                                if (result) {
                                    $('#processing-modal').modal('toggle'); //before post
                                    jQuery.ajax({
                                        url: urlAjax,
                                        async: true,
                                        success: function (result) {
                                            $('#processing-modal').modal('toggle'); // after post
                                            ejecutarBoton('frmCrearViaje', 'btnRecargarSol');
                                        },
                                        error: function (request) {
                                            $('#processing-modal').modal('toggle'); // after post
                                            bootbox.alert('Ocurrio un error al mover el viaje, favor de intentarlo de nuevo.');
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });

            event.preventDefault();
            solViajeIdTxt = "";
            viajeroIDTxt = "";
        }
    });

    $('.bodyViajeProgramado').bind('drop', function (event) {
        var msgConfirviaje;
        var viajeId = Number(viajeIdTxt.replace('viaje', ''));
        solViajeIdTxt = "";
        viajeIdTxt = "";
        viajeroIDTxt = "";
        msgConfirviaje = 'Se cambiara el estatus del viaje, a viaje por salir';
        if (viajeId > 0) {
            var urlAjax = '/ServiciosGenerales/resources/serviciosWebSGyL/moverViajeAProgramado/'
                    + viajeId + '/' + $('.usrSesion').val();
            $('#processing-modal').modal('toggle'); //before post
            jQuery.ajax({
                url: urlAjax,
                async: true,
                success: function (result) {
                    $('#processing-modal').modal('toggle'); // after post
                    ejecutarBoton('frmCrearViaje', 'btnRecargarSol');
                },
                error: function (request) {
                    $('#processing-modal').modal('toggle'); // after post
                    if (request.responseText + '' === 'Fechas') {
                        bootbox.alert('Ocurrio un error al mover el viaje. La fecha programada del viaje es menor a la actual.');
                    } else if (request.responseText + '' === 'ok') {
                      $('#processing-modal').modal('toggle'); // after post
                      ejecutarBoton('frmCrearViaje', 'btnRecargarSol');
                      
                    } else {
                       bootbox.alert('Ocurrio un error al mover el viaje, favor de intentarlo de nuevo.');
                    }
                }
            });

            // Post data           
            event.preventDefault();
        }
    });
}

function confirmarBootbox(mensaje) {
    var res = false;
    bootbox.confirm({
        message: mensaje,
        callback: function (result) {
            if (result)
                return true;
            ;
        }
    });
    return res;
}
// ]]>

