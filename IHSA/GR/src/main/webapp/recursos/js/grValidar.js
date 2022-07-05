function cerrarDialogoModal(dialogo) {
    $(dialogo).modal('hide');
}

function abrirDialogoModal(dialogo) {
    $(dialogo).modal('show');
}

function cerrarDialogoPopUpFE() {
    cerrarDialogoModal(dialogoPopUpFE);
}

function cerrarDialogoZonas() {
    cerrarDialogoModal(dialogoZonas);
}

function abrirDialogoZonas() {
    abrirDialogoModal(dialogoZonas);
}

function cerrarDialogoLlegadaPS() {
    cerrarDialogoModal(dialogoPopUpLlegadaPS);
}

function abrirDialogoLlegadaPS() {
    abrirDialogoModal(dialogoPopUpLlegadaPS);
}

function cerrarDialogoDetalleInt() {
    cerrarDialogoModal(dialogoPopUpDetalleInt);
}

function abrirDialogoDetalleInt() {
    abrirDialogoModal(dialogoPopUpDetalleInt);
}

function cerrarDialogoAddComentario() {
    cerrarDialogoModal(dialogoPopUpAddComentario);
}

function abrirDialogoAddComentario() {
    abrirDialogoModal(dialogoPopUpAddComentario);
}

function cerrarDialogoGRAutoriza() {
    cerrarDialogoModal(dialogoPopUpSS);
}

function abrirDialogoGRAutoriza() {
    abrirDialogoModal(dialogoPopUpSS);
}

function abrirDialogoGRViajeDet() {
    abrirDialogoModal(dialogoPopUpViajeDet);
}

function cerrarDialogoGRViajeDet() {
    cerrarDialogoModal(dialogoPopUpViajeDet);
}

function cerrarDialogoInterceptarViaje() {
    cerrarDialogoModal(dialogoPopUpInterseccion);
}

function abrirDialogoInterceptarViaje() {
    abrirDialogoModal(dialogoPopUpInterseccion);
}

function cerrarDialogoCrearViaje() {
    cerrarDialogoModal(dialogoPopUpCrearViaje);
    abrirDialogoModal(dialogoPopUpCrearViajeExito);
}

function cerrarDialogoSemaforos() {
    cerrarDialogoModal(dialogoSemaforos);
}

function abrirDialogoSemaforos() {
    abrirDialogoModal(dialogoSemaforos);
}

function cerrarDialogoMapaGPS() {
    cerrarDialogoModal(dialogoMapaGPS);
}

function abrirDialogoMapaGPS() {
    abrirDialogoModal(dialogoMapaGPS);    
}

function cerrarDialogoPuntos() {
    cerrarDialogoModal(dialogoPuntos);
}

function abrirDialogoEditRutaCodigosHorarios() {
    abrirDialogoModal(dialogoEditRutaCodigosHorarios);
}

function cerrarDialogoEditRutaCodigosHorarioss() {
    cerrarDialogoModal(dialogoEditRutaCodigosHorarios);
}

function abrirDialogoPuntos() {
    abrirDialogoModal(dialogoPuntos);
}

function cerrarDialogoRutas() {
    cerrarDialogoModal(dialogoRutas);
}

function abrirDialogoRutas() {
    abrirDialogoModal(dialogoRutas);
}

function cerrarDialogoEditRutaZonas() {
    cerrarDialogoModal(dialogoEditRutaZona);
}

function abrirDialogoEditRutaZonas() {
    abrirDialogoModal(dialogoEditRutaZona);
}

function cerrarDialogoPopUpFEedit() {
    cerrarDialogoModal(dialogoPopUpFEedit);
}


function abrirDialogoPopUpFE() {
    abrirDialogoModal(dialogoPopUpFE);
}

function abrirDialogoCrearViaje() {
    abrirDialogoModal(dialogoPopUpCrearViaje);
}

function abrirDialogoRutaDet() {
    abrirDialogoModal(dialogoPopUpRutasSectores);
}

function cerrarDialogoRutaDet() {
    cerrarDialogoModal(dialogoPopUpRutasSectores);
}

function abrirDialogoPopUpFEedit() {
    abrirDialogoModal(dialogoPopUpFEedit);
}


function cerrarDialogoRecomendaciones() {
    cerrarDialogoModal(dialogoRecomendaciones);
}

function abrirDialogoRecomendaciones() {
    abrirDialogoModal(dialogoRecomendaciones);
}
function cerrarDialogoSitiosRecomendados() {
    cerrarDialogoModal(dialogoSitiosRecomendados);
}

function abrirDialogoSitiosRecomendados() {
    abrirDialogoModal(dialogoSitiosRecomendados);
}
function cerrarDialogoSituacionRiesgo() {
    cerrarDialogoModal(dialogoSituacionRiesgo);
}

function abrirDialogoSituacionRiesgo() {
    abrirDialogoModal(dialogoSituacionRiesgo);
}  

function abrirDialogoImagen() {
    abrirDialogoModal(dialogoVerImagen);
}

function abrirDialogoImagenMapa() {
    abrirDialogoModal(dialogoVerImagenMapa);
}


function cerrarDialogoImagen() {
    cerrarDialogoModal(dialogoVerImagen);
}

function validarCajaTexto(idComponente) {
    var texto = $(idComponente).val();
    texto = texto.trim();
    if (texto == "" || texto == undefined || texto == null) {
	return true;
    } else {
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

function mostrarPanelAdArch(){
	mostrarDiv('adArchPanelDi');
	ocultarDiv('adArchPanelOc');
       $("#frmMsg\\:fileEntryComp").required = 'true';
}

function ocultarPanelAdArch(){
	mostrarDiv('adArchPanelOc');
	ocultarDiv('adArchPanelDi');
       $("#frmMsg\\:fileEntryComp").required = 'false';
}


function mostrarDiv(divM) {
    $("#" + divM).css({"display": "block"});
}

function ocultarDiv(divOcultar) {
    $("#" + divOcultar).css({
	"display": "none"
    });
}

function validarCajaTextoMontos(idComponente) {
    var patt = /^[-+]?\d+(\.\d{1,8})?$/;
    return patt.test($("#" + idComponente).val());
}

function validarCajaTextoCantidad(idComponente) {    
    var patt = /^\d+$/;    
    return patt.test($("#" + idComponente).val());
}

function validarCombo(formYid, valorAComparar) {
    //oficinaOrigen
    var se = jQuery(formYid + " option:selected");
    if (se.val() != valorAComparar) {
	return true;
    } else {
	return false;
    }
}

function validaValorMapa() {
    if (validarCombo("#frmPopUpFE\\:idMapa", 0)) {
	return true;
    } else {
	bootbox.alert("Es necesario seleccionar un mapa.");
    }
    return false;
}

function validaValorTitulo(forma, elemento) {
    if (validarCajaTexto("#"+forma+"\\:"+elemento)) {    	
	bootbox.alert("Es necesario capturar un titulo para registrar el archivo.");
	return false;	
    } 
    return true;
}

function validaValorSitio() {
    if (validarCajaTexto("#frmSitiosReco\\:nombreSitioID")) {    	
	bootbox.alert("Es necesario capturar el nombre del sitio.");	
	return false;	
    } else if (validarCajaTexto("#frmSitiosReco\\:descSitioID")) {    	
	bootbox.alert("Es necesario capturar una descripción del sitio.");	
	return false;	
    } else if (validarCajaTexto("#frmSitiosReco\\:ligaID")) {    	
	bootbox.alert("Es necesario capturar la liga del sitio.");	
	return false;	
    }
    return true;
}

function validaValorZona() {
    if (validarCajaTexto("#frmZonas\\:nombreZonaID")) {    	
	bootbox.alert("Es necesario capturar el nombre de la zona.");	
	return false;	
    } else if (validarCajaTexto("#frmZonas\\:descZonaID")) {    	
	bootbox.alert("Es necesario capturar una descripción de la zona.");	
	return false;	
    } 
    return true;
}

function validaValorSemaforo() {    	
	    	if(validarCombo("#frmSemaforos\\:idSemaforo", 0)){
			if (!validarCajaTexto("#frmSemaforos\\:semJustID")) {
				if(validarCajaTexto("#frmSemaforos\\:idBusquedaZonas")){    	
					bootbox.alert("Es necesario seleccionar una zona.");
					return false;	
				}
			    }else{
				bootbox.alert("Es necesario capturar una justificación para el semáforo.");	
				return false;	
				} 
		}else{
			bootbox.alert("Es necesario seleccionar un semaforo.");
			return false;	
		}
    return true;
}

function validaValorPunto() {
    if (validarCajaTexto("#frmPuntos\\:nombrePuntoID")) {    	
	bootbox.alert("Es necesario capturar el nombre de la zona.");	
	return false;	
    } else if (validarCajaTexto("#frmPuntos\\:descPuntoID")) {    	
	bootbox.alert("Es necesario capturar una descripción de la zona.");	
	return false;	
    } 
    return true;
}


function validaValorZonaRuta() {
    if (validarCombo("#frmRutas\\:idMapaRuta", 0)) {     
	return true;	
    } else {    	
	bootbox.alert("Es necesario seleccionar la zona.");	
	return false;	
    } 
    return true;
}


function validaValorMsg() {
    if (validarCajaTexto("#frmMsg\\:txtMsgCentops")) {    	
	bootbox.alert("Es necesario capturar el mensaje para CENTOPS.");
	return false;	
    } 
    return true;
}

function editZona() {
	mostrarDiv('editRutaZonaID');
	ocultarDiv('listRutaZonas');
	abrirDialogoRutas();
}

function limpiarBusquedaRutas() {
	$("#frmSemaforos\\:idBusquedaZonas").val('');
}


function editZonaGuardar() {
	mostrarDiv('listRutaZonas');
	ocultarDiv('editRutaZonaID');	
	abrirDialogoRutas();
}

function confirmar(frm, mensaje) {
    return   confirmarBootbox(frm, mensaje);
}

function validaFinalizarViaje(frm) {
    var val = false;
	if (confirmar(frm, "¿Está seguro de finalizar el viaje..?")) {
	    val = true;
	} else {
	    val = false;
	}
    return val;
}

function validaSelectViajero() {
	var tipo = jQuery("input[name$='frmPnlViajeEmer\\:selectViajero']':checked").val();
	if (tipo == 1) {
		mostrarDiv('busquedaEmpleado');
		ocultarDiv('busquedaInvitado');	
	} else {
		mostrarDiv('busquedaInvitado');
		ocultarDiv('busquedaEmpleado');	
    }
}

function validarSecuencia(){    
    if(validarCajaTextoCantidad('frmRutas\\:valSecID')){        
        return true;        
    } else {
        bootbox.alert("Es necesario capturar la secuencia. Debe ser un valor numérico entero.");
        return false;        
    }
    
    
}



