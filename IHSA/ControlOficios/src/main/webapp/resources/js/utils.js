/* 
 * Utilidades de JavaScript para el proyecto Control de Oficios.
 * 
 */

/**
 * Función para activar un botón al presionar Enter sobre un componente de UI (ej. <ice:selectOneMenu>).
 * 
 * Ejemplo de uso: onkeypress="return submitEnter('formOficios:botonBuscar',event);"
 * 
 */
function submitEnter(commandId, e) {

    var keycode;
    if (window.event) {
        keycode = window.event.keyCode;
    } else if (e) {
        keycode = e.which;
    } else {
        return true;
    }

    if (keycode == 13) {
        document.getElementById(commandId).click();
        return false;
    } else {
        return true;
    }
}


/**
 * Bloquea la pantalla durante un proceso largo.
 * 
 */
/*function bloquearPantalla(bloquear) {
 if (bloquear == 'true') {
 document.getElementById('LoadingDiv').style.display='block';
 } else {
 document.getElementById('LoadingDiv').style.display='none';
 }
 }*/


function bloquearPantalla(bloquear) {
    if (bloquear == 'true') {
        $(modalEnProceso).modal('show');
    } else {
        $(modalEnProceso).modal('hide');
        //$('body').removeClass('modal-open');

        // para remover backdrop remanente
        $('.modal-backdrop').fadeOut(300);

        //$('.modal-backdrop').remove();
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
