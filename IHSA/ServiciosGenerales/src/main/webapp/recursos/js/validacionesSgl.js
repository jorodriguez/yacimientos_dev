/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/****************** CONSTANTES *****************/
var ESTATUS_POR_VISTO_BUENO = 415;
var ESTATUS_POR_APROBAR = 420;
var ESTATUS_POR_AUTORIZAR = 435;
var TIPO_ESPECIFICO_AEREAS = 3;
var TIPO_ESPECIFICO_TERRESTRES = 2;
var MINIMO_CARACTERES = 50;
/************************************************/



var isTerrestre;
var isAereo;
var seleccionNombreTab = "OFICINA";
var seleccionCiudadDestino = -1;
var seleccionOficinaDestino = -1;
var fechaSeleccinadaSalida;
var myPanel;
var _mensaje;
var _mensajeEspera;
var BASE = 10;

var _jsonActivo;

$(document).ready(function(){
    //--Controlar ffffffffffff

//quitar los espacios del menu 
    
/*$("#menu .liMenu").each(function(){
        //alert(String(this));        
        this.remove();
    });*/
/**$("#menu .liMenu").each(function(){
        
        //this.remove();
        //lert(this);
    });
     $("#menu .liMenu").each(function(){        
        //this.remove();
        alert(this.html());
    });*/
    
    
    
//alert("li's eliminados");
//alert(liEmpty));
//liEmpty.remove();
    
//if (typeof jQuery == 'undefined'){    
//    alert("Existió un error al cargar una libreria para el SIA, por favor contacte al equipo para avisar este conflicto..");
//}else{
//    alert("Se cargo el jQuery");
//    
//}
    
//función click
//    $("#sav5eSolicitud").click(function(){
//        alert('Seleccionar una ciudad');
//        //Guardar en variables el valor que tengan las cajas de texto
//        //Por medio de los id's
//        //Y tener mejor manipulación de dichos valores
//        
//        var seleccionOficinaOrigenDestinoAereo = $("#siCiudadOrigen").val();
//        
//        //validar viajes aereos
//        if(seleccionOficinaOrigenDestinoAereo == -1){
//            alert('Seleccionar una ciudad');
//            $("#mensajesSolicitud").val() = 'Seleccionar una ciudad';
//            $("#mensajesSolicitud").fadeIn("slow");
//            return false;
//        }else{
//            //$("#mensaje1").fadeOut(); //animacion de retardo$("#mensaje2").fadeIn("slow");
//            return true;
//        }
// 
//    });//click
});//ready



function validarSolicitud(){   
    
    //checar si es Terrestre o Aereo    
    var retorno = true;
    var hoy,fechaHoy,fseleccionada,fseleccionadaRegreso,arrFechaRegreso;
    var radio = jQuery("input[name$='frmTerrestreAereo:seleccionTerrestreAereo']':checked").val();
    
    //Verifica si es solicitud viaje redoondo
    var redondo = jQuery("input[name$='frmTerrestreAereo:seleccionViajeRedondo']':checked").val();
    
    //
    if (this.validarCombo("frmTerrestreAereo\\:oficinaOrigen", -1)){
        retorno = false;
        $("#mensajeErrorSeleccionOficinaOrigen").text("Seleccione la oficina de origen");
    } else{
        $("#mensajeErrorSeleccionOficinaOrigen").text("");
    }
    //validar viajes aereos    
    if (radio == "TERRESTRE"){             
        
        //Saber si es Oficina o Ciudad
        //OFICINA
        //validar combo de oficina
        if(this.validarCombo("frmTerrestreAereo\\:comboOficinaDestino", -1) ){                
            $("#mensajeErrorSeleccionComboOficinaDestino").text("Seleccione la ruta del viaje");                                
            retorno = false;
        //  alert("Por favor seleccione la oficina destino..");
        }else{
            $("#mensajeErrorSeleccionComboOficinaDestino").text("");                                
        }
        
        //Motivo de viaje
        if(this.validarCombo("frmTerrestreAereo\\:comboMotivoViaje", -1)){
            $("#mensajeErrorSeleccionMotivo").text("Seleccione el motivo de viaje");
            // alert("Por favor seleccione un motivo por el cual esta solicitando su viaje");                       
            retorno = false;
        }else{            
            $("#mensajeErrorSeleccionMotivo").text("");
        }
    }else{
        if(this.validarCombo("frmTerrestreAereo\\:comboSiCiudadOrigenAereo", -1)){           
            escribirMensaje("mensajeErrorSeleccionComboSiCiudadOrigenAereo","Seleccione una ciudad de origen");        
            return false;
        }else{
            escribirMensaje("mensajeErrorSeleccionComboSiCiudadOrigenAereo","");            
        }
        if(this.validarCombo("frmTerrestreAereo\\:comboSiCiudadDestinoAereo", -1)){            
            escribirMensaje("mensajeErrorSeleccionComboSiCiudadDestinoAereo","Seleccione una ciudad de destino");            
            retorno = false;
        }else{
            escribirMensaje("mensajeErrorSeleccionComboSiCiudadDestinoAereo","");            
        }
        
        if(retorno == true){
            var cd = jQuery("#frmTerrestreAereo\\:comboSiCiudadDestinoAereo option:selected");
            if(this.validarCombo("frmTerrestreAereo\\:comboSiCiudadOrigenAereo", cd.val())){
                escribirMensaje("mensajeErrorSeleccionComboSiCiudadDestinoAereo","La ciudad origen y destino no pueden ser las mismas");            
                retorno = false;
            }else{
                escribirMensaje("mensajeErrorSeleccionComboSiCiudadDestinoAereo","");            
            }
        }
    }
    //////////
    //////////
    if(retorno == true){
        var fechaSalida = $("#frmTerrestreAereo\\:fechaSalida").val();
        if( fechaSalida == ""){  
            $("#mensajeErrorSeleccionFechaSalida").text("La fecha de salida no puede estar vacia.");
            retorno = false;
        }else{
            
            hoy = new Date();
            fechaHoy = hoy.getDate() + '/' + (hoy.getMonth() + 1) +'/' + hoy.getFullYear() + '/' + hoy.getHours() + '/' + hoy.getMinutes();
            var arrHoy = fechaHoy.split("/");
            //Valdia que sea para mañana 
            var hora = $("#frmTerrestreAereo\\:horaSalida").val();
            var minuto = $("#frmTerrestreAereo\\:minutoSalida").val();
            // alert('hora: ' + hora + 'min: ' + minuto + '- - - ' + arrHoy[3]);
            fseleccionada = fechaSalida + '/'+hora+'/'+minuto;
            var arrFechaSelecionada = fseleccionada.split("/");
            retorno = validaFechaSalida(arrFechaSelecionada, arrHoy);         
            if(retorno == true){
                $("#mensajeErrorSeleccionFechaSalida").text("");
                $("#mensajeErrorSeleccionHoraSalida").text("");
            }
            // alert(redondo);
            if(redondo == 'redondo' && retorno == true){     
                var ha = $("#frmTerrestreAereo\\:horaRegreso").val();
                var mo = $("#frmTerrestreAereo\\:minutoRegreso").val();
                var fr = $("#frmTerrestreAereo\\:fechaRegreso").val();
                fseleccionadaRegreso =  fr+ '/'+ha+'/'+mo;
                arrFechaRegreso = fseleccionadaRegreso.split("/");
                
                if(fr == ""){
                    $("#mensajeErrorSeleccionFechaRegreso").text("La fecha de regreso no puede estar vacia.");
                    retorno = false;
                }else{
                    retorno = validaFechaRegreso(arrFechaSelecionada, arrFechaRegreso);    
                    if(retorno == true){
                        $("#mensajeErrorSeleccionFechaRegreso").text("");
                        $("#mensajeErrorSeleccionHoraRegreso").text("");
                    }    
                }            
            }
        }
    }
    
    
    ///GErencia
    ////
    var gerencia;
    var ele = $('#frmTerrestreAereo\\:nombreGerencia').get(0);
    if(typeof ele == "undefined"){
        gerencia = tomarValorSelccionadoCombo("frmTerrestreAereo\\:seleccionIdGerencia");
    }else{
        gerencia = $('#frmTerrestreAereo\\:nombreGerencia').text();
    }
    if(retorno == true){
        if(gerencia == ""){
            $("#mensajeErrorIdGerencia").text("Es necesario seleccionar una gerencia. \n Si no aparece una gerencia, favor de ponerse en contacto con el equipo de desarrollo del SIA");        
            retorno = false;
        }else{
            $("#mensajeErrorIdGerencia").text("");
            retorno = true;
        }
    }
    
    //valida la fecha de salida. para notificar a direccion
    /*if(retorno == true){
        if(validaFechaSalidaDireccion(arrFechaSelecionada, arrHoy) == true){
            //Motivo retraso   
            if (confirm("La solicitud de viaje no cumple con las políticas de horario, \n  por lo que deberá ser autorizada por dirección general. \n ¿Está seguro de continuar?")) {
                retorno = true;
            }else{
                retorno = false;
            } 
        }  
    }*/
    return retorno;
}


function validaFechaSalidaDireccion(arrFechaSelecionada, arrHoy){
    var v = validaFechaSalidaHoy(arrFechaSelecionada, arrHoy);
    if(v == false){
        v= validaFechaManana(arrFechaSelecionada, arrHoy);
    }
    return v;
}
function validaFechaSalidaHoy(arrFechaSelecionada, arrHoy){
    var va = false;
    if(parseInt(arrFechaSelecionada[0],BASE) == parseInt(arrHoy[0],BASE)){
        if(parseInt(arrFechaSelecionada[1],BASE) == parseInt(arrHoy[1],BASE)){
            if(parseInt(arrFechaSelecionada[2],BASE) == parseInt(arrHoy[2],BASE)){
                //motivo retraso
                va = true;
            }
        }
    }
    return va;
}
function validaFechaManana(arrFechaSelecionada, arrHoy){
    var va = false;
    if(parseInt(arrFechaSelecionada[2],BASE) == (parseInt(arrHoy[2],BASE))){
        if(parseInt(arrFechaSelecionada[1],BASE) == (parseInt(arrHoy[1],BASE))){
            if(parseInt(arrFechaSelecionada[0],BASE) == (parseInt(arrHoy[0],BASE)+1)){
                if(parseInt(arrHoy[3],BASE) > 15){
                    va = true;
                }
            }else if(parseInt(arrFechaSelecionada[0],BASE) > (parseInt(arrHoy[0] +1,BASE))){
                va = true;
            }
        }else if(parseInt(arrFechaSelecionada[1],BASE) > (parseInt(arrHoy[1],BASE))){
            va = true;
        }
    }else if(parseInt(arrFechaSelecionada[0],BASE) > (parseInt(arrHoy[0],BASE))){
        va = true;
    }
    
    return va;

}
function validaFechaSalida(arrFechaSelecionada,arrHoy){
    //     alert(arrFechaSelecionada + ' - - - - '+ arrHoy);
    var  va = true;
    if(parseInt(arrFechaSelecionada[2],BASE) == parseInt(arrHoy[2],BASE) ){
        if(parseInt(arrFechaSelecionada[1],BASE) == parseInt(arrHoy[1],BASE)){
            if(parseInt(arrFechaSelecionada[0],BASE) == parseInt(arrHoy[0],BASE)){
                if((parseInt(arrFechaSelecionada[3],BASE) == parseInt(arrHoy[3],BASE))){
                    if((parseInt(arrFechaSelecionada[4],BASE) == parseInt(arrHoy[4],BASE))){
                        $("#mensajeErrorSeleccionHoraSalida").text("La hora y minutos seleccionados no puede ser iguales al actual.");
                        va= false;
                    } else if ((parseInt(arrFechaSelecionada[4],BASE) < parseInt(arrHoy[4],BASE))){
                        $("#mensajeErrorSeleccionHoraSalida").text("Los minutos seleccionados no puede ser menor al actual.");
                        va = false;
                    }else {
                        $("#mensajeErrorSeleccionHoraSalida").text("");
                        va = true;
                    }
                }else if ((parseInt(arrFechaSelecionada[3],BASE) < parseInt(arrHoy[3],BASE))){
                    $("#mensajeErrorSeleccionHoraSalida").text("La hora y minutos seleccionados no puede ser menor al actual.");
                    va = false;
                }
                else {
                    $("#mensajeErrorSeleccionHoraSalida").text("");
                    va = true;
                }
            }else if(parseInt(arrFechaSelecionada[0],BASE) < parseInt(arrHoy[0],BASE)){
                // alert(parseInt(arrFechaSelecionada[0]));
                $("#mensajeErrorSeleccionFechaSalida").text("La fecha de salida seleccionada no puede ser menor a hoy.");
                va = false;
            } else if(parseInt(arrFechaSelecionada[0],BASE) > parseInt(arrHoy[0],BASE)){
                $("#mensajeErrorSeleccionFechaSalida").text("");
                va = true;
            }
        }else if (parseInt(arrFechaSelecionada[1],BASE) < parseInt(arrHoy[1],BASE)){
            $("#mensajeErrorSeleccionFechaSalida").text("La fecha de salida seleccionada no puede ser menor a hoy.");
            va = false;
        }else{
            $("#mensajeErrorSeleccionFechaSalida").text("");
            va = true;
        }
    }else if (parseInt(arrFechaSelecionada[2],BASE) < parseInt(arrHoy[2],BASE)){
        $("#mensajeErrorSeleccionFechaSalida").text("La fecha de salida seleccionada no puede ser menor a hoy.");
        va = false;
    } else {
        $("#mensajeErrorSeleccionFechaSalida").text(" ");
        va = true;
    } 
    //  alert(va);
    return va;
}

function validaFechaRegreso(arrFechaSelecionadaSalida, arrFechaSelecionadaRegreso){
    var va= false;

    if(parseInt(arrFechaSelecionadaRegreso[2],BASE) == parseInt(arrFechaSelecionadaSalida[2],BASE) ){
        if(parseInt(arrFechaSelecionadaRegreso[1],BASE) == parseInt(arrFechaSelecionadaSalida[1],BASE) ){
            if(parseInt(arrFechaSelecionadaRegreso[0],BASE) ==  parseInt(arrFechaSelecionadaSalida[0],BASE)){
                if((parseInt(arrFechaSelecionadaRegreso[3],BASE) ==  parseInt(arrFechaSelecionadaSalida[3],BASE))){ 
                    if((parseInt(arrFechaSelecionadaRegreso[4],BASE) >=  parseInt(arrFechaSelecionadaSalida[4],BASE))){
                        va = true;
                    }else{
                        $("#mensajeErrorSeleccionHoraRegreso").text("La hora y minutos seleccionados no puede ser menores a los de salida.");
                        va = false;
                    }
                }else if ((parseInt(arrFechaSelecionadaRegreso[3],BASE) <  parseInt(arrFechaSelecionadaSalida[3],BASE))){
                    $("#mensajeErrorSeleccionHoraRegreso").text("La hora y minutos seleccionados no puede ser menores a los de salida.");
                    va = false;
                }else if(parseInt(arrFechaSelecionadaRegreso[3],BASE) >  parseInt(arrFechaSelecionadaSalida[3],BASE)){
                    va = true;
                }
            }else if(parseInt(arrFechaSelecionadaRegreso[0],BASE) < parseInt(arrFechaSelecionadaSalida[0],BASE)){
                $("#mensajeErrorSeleccionFechaRegreso").text("La fecha seleccionada no puede ser menor a la de salida.");
                va = false;
            } else if(parseInt(arrFechaSelecionadaRegreso[0],BASE) >  parseInt(arrFechaSelecionadaSalida[0],BASE)){
                $("#mensajeErrorSeleccionHoraRegreso").text("");
                va = true;
            }	
        }else if(parseInt(arrFechaSelecionadaRegreso[1],BASE) < parseInt(arrFechaSelecionadaSalida[1],BASE)){
            $("#mensajeErrorSeleccionFechaRegreso").text("La fecha de regreso seleccionada no puede ser menor a la de salida.");
            va = false;
        } else if(parseInt(arrFechaSelecionadaRegreso[1],BASE) >  parseInt(arrFechaSelecionadaSalida[1],BASE)){
            va = true;
        }
    }else if (parseInt(arrFechaSelecionadaRegreso[2],BASE) < parseInt(arrFechaSelecionadaSalida[2],BASE)){
        $("#mensajeErrorSeleccionFechaRegreso").text("La fecha de salida seleccionada no puede ser menor a hoy.");
        va = false;
    } else {
        $("#mensajeErrorSeleccionFechaRegreso").text(" ");
        va = true;
    }    
    return va;
}

function seleccionarTabSolicitudViaje(seleccionNombreIndexTab){
    this.seleccionNombreTab = seleccionNombreIndexTab;
//alert(this.seleccionNombreTab );
}

function setSeleccionCiudadDestino(combo){          
    this.seleccionCiudadDestino = tomarValorSelccionadoCombo(combo);
}	

function setSeleccionOficinaDestino(combo){
    this.seleccionOficinaDestino = tomarValorSelccionadoCombo(combo);   
}

function escribirMensaje(componente,textMensaje){
    $("#"+componente).text(textMensaje);                    
}

/*funcion que recibe un id de un select o combo y compara un valor
 * Si lo encuentra seleccionado devuelve un True 
 * caso contrario devuelve un false
 * Usado en 
 * SelectOneMenu
 * * SelectOneRadio
 */
function validarCombo(formYid,valorAComparar){
    //oficinaOrigen
    var se = jQuery("#"+formYid+" option:selected");
    if(se.val() == valorAComparar){
        return true;
    }else{
        return false;
    }    
}

function tomarValorSelccionadoCombo(componenteCombo){
    return $("#"+componenteCombo+" option:selected"); //componenteCombo.options[componenteCombo.selectedIndex].value;
}

/* 
 * funcion que valida que una caja de texto contenga valor
 *  Retorna true si la caja evaluada esta vacia.
 **/

function validarCajaTexto(idComponente){
    if($("#"+idComponente).val() == "" || $("#"+idComponente).val() == undefined || $("#"+idComponente).val() == null){
        return true;
    }else{
        return false;
    }  
}

//******************* VALIDACIONES DE SG_MOTIVO RETRASO  ********************************/
/** VALIDAR  **/
//--Tipo define el tipo de la soliciitud que se validará
function validarFormularioMotivoRetrasoSolicitud(tipoSolicitudActiva){
    var retorno = false;
    //  var frm =  document.find('#sgMotivoRetrasoViajeTerrestre');
    var frm = $('#sgMotivoRetrasoViajeTerrestre').submit();
    if(frm != null ){
        if(tipoSolicitudActiva == 'TERRESTRE'){        
            if(this.validarCajaTexto("sgMotivoRetrasoViajeTerrestre\\:varInvitado")){     
                $("#mensajeSgInvitadoSelect").text("Por favor escriba el nombre de la persona a visitar");        
                retorno= false;
            }else{
                retorno= true;
                $("#mensajeSgInvitadoSelect").text("");        
            }
            if(this.validarCombo("sgMotivoRetrasoViajeTerrestre\\:sgLugarCombo",-1)){          
                $("#mensajeSgLugarCombo").text("Por favor escriba el lugar de la reunión");        
                retorno= false;
            }else{
                retorno= true;
                $("#mensajeSgLugarCombo").text("");        
            }      
        }        
        if(validaTamanioCaja("sgMotivoRetrasoViajeTerrestre\\:justificacionRetraso", 50, 250)){
            retorno= true;
            $("#mensajeJustificacionRetraso").text("");        
        }else{
            $("#mensajeJustificacionRetraso").text("Por favor escriba el motivo de retraso de mayor a 50 carácteres y menor de 250.");        
            retorno = false;
        }
    }else{
        retorno = true;
    }
    return retorno;
}


function contarCaracteres(frm, component){
    $("#"+frm+"\\:"+component).keydown(function() {
        var t = $("#"+frm+"\\:"+component).val().length;
        $("#"+frm+"\\:remLen").val(t+1);
    });
}



/**
 *Verifica si el contenido esta entre el valor minimo y maximo requerido
 *Agrer el id del formulario 
 *agregar el id de caja
 **/
function validaTamanioCaja(idCajaCompleto, valorMinimo, valorMaximo){
    var contenido =  $("#"+idCajaCompleto).val();
    //alert(contenido.length);
    if(contenido.length > valorMinimo && contenido.length < valorMaximo){
        return true;
    }else{
        return false;
    }
}




//******************* FIN - VALIDACIONES DE SG_MOTIVO RETRASO  ********************************/

//Crear viaje regreso
//modificado por joel rodriguez 
function validaCrearViajeRegreso(){
    var e = 0;
    var retorno = false;
    var hoy,fechaHoy,fseleccionada;
    var fechaSalida = $("#frmViajerRegresoTerrestre\\:fechaSalida").val();
    if( fechaSalida == ""){  
        $("#mensajeErrorSeleccionFechaSalida").text("La fecha de salida no puede estar vacia.");
        e++;
    }else{
        hoy = new Date();
        fechaHoy = hoy.getDate() + '/' + (hoy.getMonth() + 1) +'/' + hoy.getFullYear() + '/' + hoy.getHours() + '/' + hoy.getMinutes();
        var arrHoy = fechaHoy.split("/");
        //Valdia que sea para mañana 
        var hora = $("#frmViajerRegresoTerrestre\\:horaSalida").val();
        var minuto = $("#frmViajerRegresoTerrestre\\:minutoSalida").val();
        //alert('hora: ' + hora + 'min: ' + minuto + '- - - ' + arrHoy[3]);
        fseleccionada = fechaSalida + '/'+hora+'/'+minuto;
        var arrFechaSelecionada = fseleccionada.split("/");
        retorno = validaFechaSalida(arrFechaSelecionada, arrHoy);         
        if(retorno == true){
            $("#mensajeErrorSeleccionFechaSalida").text("");
            $("#mensajeErrorSeleccionHoraSalida").text("");
        }else{
            e++;
        }
    }
    
    if(e  == 0){
        retorno = true;
    }else{
        retorno = false;
    }
    return retorno;
}


//Crear viaje
function validaCrearViaje(){
    
    

    var e = 0;
    var hoy,fechaHoy,fseleccionada;
    var redondo = jQuery("input[name$='frmCompletarViaje\\:seleccionViajeRedondo']':checked").val();
    var retorno = false;
    
    var fechaSalida = $("#frmCompletarViaje\\:fechaSalida").val();
    //    alert(fechaSalida);
    if(fechaSalida == ""){
        //     alert("La fecha no puede estar vacia");
        $("#mensajeErrorSeleccionFechaRegreso").text("La fecha de salida no puede estar vacia.");
        e++;
    }else{
        hoy = new Date();
        fechaHoy = hoy.getDate() + '/' + (hoy.getMonth() + 1) +'/' + hoy.getFullYear() + '/' + hoy.getHours() + '/' + hoy.getMinutes();
        var arrHoy = fechaHoy.split("/");
        //Valdia que sea para mañana 
        var hora = $("#frmCompletarViaje\\:horaSalida").val();
        var minuto = $("#frmCompletarViaje\\:minutoSalida").val();
        //alert('hora: ' + hora + 'min: ' + minuto + '- - - ' + arrHoy[3]);
        fseleccionada = fechaSalida + '/'+hora+'/'+minuto;
        var arrFechaSelecionada = fseleccionada.split("/");
        
        retorno = validaFechaSalida(arrFechaSelecionada, arrHoy);
    }
    if(retorno){
        $("#mensajeErrorSeleccionFechaSalida").text("");
        $("#mensajeErrorSeleccionHoraSalida").text("");
    }else{
        e++;
    }
    //alert(e);
    //   alert(redondo);
    if(retorno){
        if(redondo == 'redondo'){
            var ha = $("#frmCompletarViaje\\:horaRegreso").val();
            var mo = $("#frmCompletarViaje\\:minutoRegreso").val();
            var fr = $("#frmCompletarViaje\\:fechaRegreso").val();
            var fseleccionadaRegreso =  fr+ '/'+ha+'/'+mo;
            var     arrFechaRegreso = fseleccionadaRegreso.split("/");
            //  alert(fr);
            if(fr == ""){
                $("#mensajeErrorSeleccionFechaRegreso").text("La fecha de regreso no puede estar vacia.\n");
                e++;
            }else{
                retorno = validaFechaRegreso(arrFechaSelecionada, arrFechaRegreso);    
                if(retorno == true){
                    $("#mensajeErrorSeleccionFechaRegreso").text("");
                    $("#mensajeErrorSeleccionHoraRegreso").text("");
                }else{
                    e++;
                }    
            }
        }
    }
    //Vlaida oficina 
    
    if(validarCombo("frmCompletarViaje\\:selectOneMenuOficina", -1)){
        e ++;
        $("#mensajeErrorSeleccionTelefono").text("");
        $("#mensajeErrorSeleccionOficina").text("Es necesario seleccionar una oficina\n");
    }else{
        $("#mensajeErrorSeleccionOficina").text("");
        //Vlaida  vahiculo
        if(validarCombo("frmCompletarViaje\\:selectOneMenuVehiculo", -1)){
            e++;
            $("#mensajeErrorSeleccionVehiculo").text("Es neceario seleccionar un vehículo \n");
        }else{
            $("#mensajeErrorSeleccionVehiculo").text("");
        }
        $(".tel").each(function(){
            var self = $(this),
            thisVal = self.val();
            if($.trim(thisVal) == "" || thisVal.length == 0 || thisVal.length < 10){
                e++;
                $("#mensajeErrorSeleccionTelefono").text("Es necesario agregar el teléfono (al menos 10 numeros) del responsable del viaje \n");
            }else if(isNaN($.trim(thisVal))){
                e++;
                $("#mensajeErrorSeleccionTelefono").text("Es necesario agregar solo números para el teléfono \n");
            }
        });
        //Vlaida  ruta
        if(validarCombo("frmCompletarViaje\\:rutaViaje", -1)){
            e++;
            $("#mensajeErrorSeleccionRuta").text("Es neceario seleccionar la ruta \n");
        }else{
            $("#mensajeErrorSeleccionRuta").text("");
        }
    }
    
    if(e == 0 && validarViajeDirectoForm()){
        $("#mensajeErrorSeleccionTelefono").text("");
        retorno = true;
    }else{
        retorno = false;
    }
    return retorno;
}

function validarViajeDirectoForm(){
		if(validarCombo("frmCompletarViaje\\:rutaViaje", 1) || validarCombo("frmCompletarViaje\\:rutaViaje", 7)){
			return validaViajeDirecto();
		}else{
			return true;
		}
}

function validaViajeDirecto(){
	var ret = true;
	if($(".checkDirecto").is(":checked")){
		if(!confirm("¿Está seguro que el viaje ES DIRECTO..?")){
			ret = false;
			$(".checkDirecto").prop("checked", "");		
		}
	}else{
		if(!confirm("¿Está seguro que el viaje NO ES DIRECTO..?")){
			ret = false;
			$(".checkDirecto").prop("checked", "true");
		}
	}
	return ret;
}


/***************************************************************************/
/* VALIDACIONES PARA APROBACIONES DE FLUJO - APROBAR ESTATUS APROBACIÓN */
/*--------------------------------------------------------------------------*/
function validarAprobacion(json,mensaje,mensajeEspera){               
    var todoBien = true;
    $("#mensajeHidden").text("");
    //validar fechas
    //        alert(json.idTipoEspecifico);
    var hoy = new Date();
    var horaActual = new Date();    
    horaActual.setTime(hoy.getTime());
    
    var fechaSalida  = new Date(json.fechaSalida);    
    //    var horaSalida = new Date(json.horaSalida);        
    var fechaActual = getFechaHoySinHora();
    
    //    alert(json.idTipoSolicitud +" esca"+json.countEscalas);    
    if(json.viajerosCount == 0){
        //alert("Imposible aprobar una solicitud sin viajeros");        
        $("#mensajeHidden").text("Imposible aprobar una solicitud sin viajeros");                
        todoBien=false;
    }else{
        $("#mensajeHidden").text("");        
        if((fechaActual > fechaSalida)){
            $("#mensajeHidden").text("Imposible aprobar una solicitud con fecha pasada ");
            todoBien=false;
        }else{
            $("#mensajeHidden").text("");
            if(json.idEstatus  == this.ESTATUS_POR_APROBAR && json.idTipoEspecifico != this.TIPO_ESPECIFICO_TERRESTRES){
                //validar que tenga escalas
                if(json.countEscalas == 0){
                    $("#mensajeHidden").text("Imposible aprobar una solicitud sin escalas en el itinerario");                    
                    todoBien=false;
                }
            }
        }    
    }     
    if(todoBien){
        //tomar valores para usarlos en el dialog
        //        alert(this._mensajeEspera);
        this._jsonActivo = json;
        this._mensaje = mensaje;
        this._mensajeEspera = mensajeEspera+" "+this._jsonActivo.codigo;        
    
    }    
    return todoBien;
// alert("No se puede aprobar una solicitud con fecha pasada : "+hoy.toString()+" no puede ser menor a "+json.fechaSalida.toString());    
}

function preguntarConfirmacionAprobacion(){    
    //hacer las pereguntas
   //   alert("¿"+this._mensaje+" "+this._jsonActivo.codigo+"?  "+this._jsonActivo.mensajePoliticaColorSemaforo);
    $("#popupMotivoCancelacion\\:textoConfirmacion").text("¿"+this._mensaje+" "+this._jsonActivo.codigo+"?");             
    //alert(this._jsonActivo.motivoAutorizarJustificacion);
    //$("#popupMotivoCancelacion\\:textoConfirmacion").text("¿"+mensaje);             
    if((this._jsonActivo.motivoAutorizarJustificacion != "") 
        && (this._idTipoEspecifico != this.TIPO_ESPECIFICO_TERRESTRES) 
        && (this._jsonActivo.idEstatus != this.ESTATUS_POR_AUTORIZAR)){
        //Mostrar el motivo de retraso con la politica de semaforo        
        $("#popupMotivoCancelacion\\:textoRecomendacion").text(this._jsonActivo.mensajePoliticaColorSemaforo);        
        $("#popupMotivoCancelacion\\:panelMotivoRet").show();        
    }else{
        $("#popupMotivoCancelacion\\:panelMotivoRet").hide();
    }     
    dialogConfirmacionAprobacion.show();    
}

function confirmarAprobacion(){
   //   alert(this._mensajeEspera);
    dialogConfirmacionAprobacion.hide();        
    return true;
}
/* Confirmo la aprobacion y justifico */
function confirmarAprobacionJustificacion(){
    if(validarCajaTexto("popupMotivoCancelacion\\:motivoRetrasoAprobacion")){
        $("#mensajeHiddenCancelacion").text("Por favor escriba el motivo en la caja de texto");                   
        return false;
    }else{                  
        motivoRetrasoAprobacion.hide();            
        return true;
    }   
}


function confirmacionCancelacionSolicitudPorViolacionAprobacion(mensajeEspera){
    var retorno = false;
    var e = 0;
    if(validarCajaTexto("popupMotivoCancelacion\\:motivoRetrasoAprobacion")){
        $("#mensajeJustificacion").text("Es necesario agregar un mensaje para la opcion seleccionada");
        e++;
    }
    if(e == 0){
        $("#mensajeJustificacion").text("");
        dialogConfirmacionViolacion.hide();
        retorno = true;
    }else{
        retorno = false;
    }
    return retorno;
}

function validarAutorizacion(json,mensaje,mensajeEspera){
    /*$("#popupMotivoCancelacion\\:textoConfirmacion").text("¿"+mensaje+" "+json.codigo+"?");  
        $("#popupMotivoCancelacion\\:textoViolacion").text(json.textoViolacion);    
        dialogConfirmacionAprobacion.show();          
        
        return false;*/
    var retorno=false;
    
    if(validarAprobacion(json)){
        if(!confirm("¿"+mensaje+" "+json.codigo+"?")){
            retorno = false;
        }else{
            //prender espera de correo
            retorno=true;
        }              
    }else{
        retorno = false;
    }        
    return retorno;
}



function mostrarPanelConfirmacionAprobacion(mensajeConfirmacion,mostrarMensajePolitica){
    $("#popupMotivoCancelacion\\:textoConfirmacion").text(mensajeConfirmacion);            
    if(mostrarMensajePolitica){
        $("#popupMotivoCancelacion\\:contenedorPolicitica").style.display = 'inline';        
    }else{
        $("#popupMotivoCancelacion\\:contenedorPolicitica").style.display = 'none';        
    }
    dialogConfirmacionAprobacion.show();
}

function mostrarEsperaEnviandoCorreo(mensajeEspera){
    mostrarDialogEsperar(mensajeEspera+"...");
}


function cancelarViaje(json,mensaje){       
    if( !confirm("¿"+mensaje+" "+json.codigo+"?")){
        return false;
    }            
    else{
        return true;
    } 
}

/**Retorna la fecha actual sin hora
 */
function getFechaHoySinHora(){
    var fechaActual = new Date();  
    fechaActual.setHours(0, 0, 0, 0);    
    return fechaActual;
}

function convertToDate(dateString){
    return Date.parse(dateString);    
}


function diferenciasFechas(fecha1,fecha2){
    var dia= 60 * 60 * 24 * 1000;
    var diferenciaEntreFecha1MenosFecha2 = Math.abs(fecha1-fecha2);
    return diferenciaEntreFecha1MenosFecha2;
}
/*var objetoAprobacion = function validarAprobacion(){
      init : function (){
          
      }       
      validarFechasFueras(){};
       
   }*/

function validarMotivoCancelacion(){
    if(validarCajaTexto("popupMotivoCancelacion\\:cancelacionText")){
        $("#mensajeHiddenCancelacion").text("Por favor escriba el motivo de cancelación ");                   
        return false;
    }else{                          
        return true;
    }
}
function mostrarNotificacionViajeSalir(){
    if(confirm('La operación va a poner el viaje en proceso, ¿está seguro?')) {
        dialogoViajeSalir.show(); 
        return true;
    }else{
        return false;
    }
}
function mostrarNotificacion(){
    if(confirm('La operación va a agregar viajero al viaje, ¿está seguro?')) {
        dialogoAgregarViajeroAViaje.show(); 
        return true;
    }else{
        return false;
    }
}

function mostrarDialogoCancelar(mensajeConfirmar, mensajePanel){
    if(confirm(mensajeConfirmar)) {
        $("#popupMotivoCancelacion\\:mensajeEspera").text(mensajePanel);        
//        myPanel.show();    
        return true;
    }else{
        return false;
    }
}

/* FIN - VALIDACIONES PARA APROBACIONES DE FLUJO - APROBAR ESTATUS APROBACIÓN */
/****************************************************************************/


/***************** ACCION DE COMPONENTES ***************************************/
function mostrarDialogJustificacionSemaforo(json){             
    cargarDialogSemaforo(json.nombreDestino,json.semaforo,json.horaMinimaSemaforoActual, json.horaMaximaSemaforoActual,json.mensajePoliticaColorSemaforo);
}

function mostrarDialogJustifocacionSemaforoDesdeBandeja(json){
    var semaforo = json.semaforoVo;
    
    cargarDialogSemaforo(json.semaforoVo.nombreRuta,json.semaforoVo.color,json.semaforoVo.horaMinima, json.semaforoVo.horaMaxima,json.semaforoVo.descripcion);    
}

function cargarDialogSemaforo(nombreDestino,semaforo,horaMinima,horaMaxima,mensajePoliticaSemaforo){
    $("#popupMotivoCancelacion\\:mensajeTextArea").text(mensajePoliticaSemaforo);  
    $("#popupMotivoCancelacion\\:tituloMensaje").text(nombreDestino);  
    $("#popupMotivoCancelacion\\:colorSemaforo").text(semaforo);      
    $("#popupMotivoCancelacion\\:horaMinimaSemaforo").text(horaMinima);      
    $("#popupMotivoCancelacion\\:horaMaximaSemaforo").text(horaMaxima);          
    myPanelGeneral.show();    
}

function mostrarDialogEsperar(mensajeEspera){           
    $("#popupMotivoCancelacion\\:mensajeEspera").text(mensajeEspera); 
}

function cerrarDialogJustificacionSemaforo(){    
    myPanelGeneral.hide();
}
/***************** FIN -  ACCION DE COMPONENTES ***************************************/


function validaFormularioCrearEscala(){
    var retorno = false;
    var valor = 0;
    if (this.validarCombo("formPopupCreateSgDetalleItinerario\\:siCiudadOrigen", -1)){
        
        valor ++;
        $("#siCiudadOrigen").text("Seleccione la ciudad de origen");
    } else{
        retorno = true;
        $("#siCiudadOrigen").text("");
    }
    //Destino
    if (this.validarCombo("formPopupCreateSgDetalleItinerario\\:siCiudadDestino", -1)){
        valor ++;
        $("#siCiudadDestino").text("Seleccione la ciudad de destino");
    } else{
        retorno = true;
        $("#siCiudadDestino").text("");
    }
    
    if (this.validarCombo("formPopupCreateSgDetalleItinerario\\:sgAerolinea", -1)){
        valor ++;
        $("#sgAerolinea").text("Seleccione la aerolinea");
    } else{
        retorno = true;
        $("#sgAerolinea").text("");
    }
    //Valida origen destino
    var cd = jQuery("#formPopupCreateSgDetalleItinerario\\:siCiudadOrigen option:selected");
    if(this.validarCombo("formPopupCreateSgDetalleItinerario\\:siCiudadDestino", cd.val())){
        escribirMensaje("siCiudadDestino","La ciudad origen y destino no pueden ser las mismas");            
        valor ++;
    }else{
        escribirMensaje("siCiudadDestino","");            
    }
    //
    var fseleccionadaRegreso;
    var fechaSalida = $("#formPopupCreateSgDetalleItinerario\\:fechaSalida").val();
    if( fechaSalida == ""){  
        $("#mensajeErrorSeleccionFechaSalida").text("La fecha de salida no puede estar vacia.");
        valor ++;
    }else{
        var hoy = new Date();
        var fechaHoy = hoy.getDate() + '/' + (hoy.getMonth() + 1) +'/' + hoy.getFullYear() + '/' + hoy.getHours() + '/' + hoy.getMinutes();
        var arrHoy = fechaHoy.split("/");
        //Valdia que sea para mañana 
        var hora = $("#formPopupCreateSgDetalleItinerario\\:horaSalida").val();
        var minuto = $("#formPopupCreateSgDetalleItinerario\\:minutoSalida").val();
        // alert('hora: ' + hora + 'min: ' + minuto + '- - - ' + arrHoy[3]);
        var fseleccionada = fechaSalida + '/'+hora+'/'+minuto;
        var arrFechaSelecionada = fseleccionada.split("/");
        retorno = validaFechaSalida(arrFechaSelecionada, arrHoy);         
        if(retorno == true){
            $("#mensajeErrorSeleccionFechaSalida").text("");
            $("#mensajeErrorSeleccionHoraSalida").text("");
        }else{
            valor ++;
        //$("#mensajehoraSalida").text("La fecha y hora de salida no puede ser menor a la actual");
        
        }
        // alert(redondo);
        if(fechaSalida !=  ''){     
            var ha = $("#formPopupCreateSgDetalleItinerario\\:horaRegreso").val();
            var mo = $("#formPopupCreateSgDetalleItinerario\\:minutoRegreso").val();
            var fr = $("#formPopupCreateSgDetalleItinerario\\:fechaLlegada").val();
            fseleccionadaRegreso =  fr+ '/'+ha+'/'+mo;
            var arrFechaRegreso = fseleccionadaRegreso.split("/");
            if(fr == ""){
                $("#mensajeErrorSeleccionFechaRegreso").text("La fecha de llegada no puede estar vacia.");
                retorno = false;
            }else{
                retorno = validaFechaRegreso(arrFechaSelecionada, arrFechaRegreso);    
                if(retorno == true){
                    $("#mensajeErrorSeleccionFechaRegreso").text("");
                    $("#mensajeErrorSeleccionHoraRegreso").text("");
                    $("#mensajeErrorSeleccionFechaSalida").text("");
                }else{
                    valor ++;
                }
            }
        }else{
            valor++;            
        }
    }
    if(valor > 0){
        retorno = false
    }else{
        retorno = true;
    }
    return retorno;
}


//******** validaciones de vehiculo **************
//--Validacion de cambio de semaforo
function validarMotivoCambio(){   
    //    alert("ccc");
    var todoBien = true;
    //    alert($("#popupCambiarOficina").html());
    if(validarCombo("popupCambiarOficina\\:idOficinaDestinoCambioVehiculo",-1)){            
        $("#msjCambioVehiculo").text("Por favor seleccione una oficina de destino");        
        //alert("Por favor seleccione una oficina de destino");
        todoBien=false;   
    }else{
        if(validarCajaTexto("popupCambiarOficina\\:motivoCambioVehiculo")){
            $("#msjCambioVehiculo").text("Por favor escribe el motivo de cambio");        
            //  alert("Por favor escribe el motivo de cambio");
            todoBien=false;    
        }
    }
    if(todoBien){
        if(!confirm("¿Estas seguro de cambiar el vehiculo de oficina ?")){
            return false;  
        }else{
            
        }
    }else{
        return false;
    }
    
    
    return todoBien;

}
//***************VALIDACIONES EN VEHICULO**********************************
function validarModificacionKilomentraje(){
    var ret = true; 
        
    var kilometrajeActual = $("#popupModifyKilometraje\\:txtKilometrajeActual").val();    
    var kilometrajeNuevo = $("#popupModifyKilometraje\\:txtKilometrajeNuevo").val();    
    var kilometrajeTope = $("#popupModifyKilometraje\\:txtKilometrajeTope").val();    
    if(validarCajaTexto("popupModifyKilometraje\\:txtKilometrajeNuevo")){
        $("#errorSpan").text("Por favor escriba el nuevo kilometraje..");
        ret = false;     
    }else{
        //|| kilometrajeNuevo < kilometrajeOld
           
        if(parseInt(kilometrajeNuevo) == 0){
            $("#errorSpan").text("El kilometraje nuevo debe ser mayor a 0 ");            
            ret = false;     
        }else{
            $("#errorSpan").text("");
            if(parseInt(kilometrajeTope) >= parseInt(kilometrajeNuevo)){
                $("#errorSpan").text("El kilometraje nuevo no debe ser menor o igual a "+kilometrajeTope);
                ret = false;     
            }else{
                $("#errorSpan").text("");
                if(validarCajaTexto("popupModifyKilometraje\\:motivoModificacionKilometraje")){
                    //alert("Por favor escriba el motivo de modificacion de kilometraje.. ");
                    $("#errorSpan").text("Por favor escriba el motivo de modificacion de kilometraje.. ");
                    ret = false;
                }else $("#errorSpan").text("");     
            }
        }
    }
    return ret;
}




//*************** FIN - VALIDACIONES EN VEHICULOS *************************

//*************** INICIO- VALIDACIONES EN ESTANCIAS *************************
var fechaIngresoStaff;
function validaFechaIngresoHuesped(fechaSolicitud){
    alert(fechaIngresoStaff, fechaSolicitud)
    $("#fechaIngresoStaff").text("Valida las fechas de ingreso ");        
}

function validaRegistroHuesped(){
    var e = 0;
    var retorno = false;
    var tipoEspècificio = $("#popupRegistroHuespedStaff\\:tipoHuesped").val();
    if(validarCombo("popupRegistroHuespedStaff\\:tipoHuesped", -1)){
        $("#mensajeErrorSeleccionTipoHuesped").text("Por favor seleccione el tipo de huesped ");
        e++;
    }else if(validarCombo("popupRegistroHuespedStaff\\:tipo", -1)){
        $("#mensajeErrorSeleccionStaff").text("Por favor seleccione el staff house");
        e++;
    } 
    
    if(e == 0){
        $("#mensajeErrorSeleccionTipoHuesped").text("");
        $("#mensajeErrorSeleccionFechaIngreso").text("");
        $("#mensajeErrorSeleccionFechaSalida").text("");
        $("#mensajeErrorSeleccionFechaSalidaPropuesta").text("");
        $("#mensajeErrorSeleccionStaff").text("");
        retorno = true; 
    }else{
        retorno = false;
    }
    return retorno;
}

function validaRegistroHuespedHotel(){
    var e = 0;
    var retorno = false;
    var tipoEspècificio = $("#popupRegistroHuespedHotel\\:tipoHuespedHotel").val();
    if(validarCombo("popupRegistroHuespedHotel\\:tipoHuesped", -1)){
        $("#msgRegistroHuespedHotel").text("Por favor seleccione el tipo de huesped ");
        e++;
    }else if(validarCombo("popupRegistroHuespedHotel\\:hotelSom", -1)){
        $("#mensajeErrorSeleccionHotel").text("Por favor seleccione el hotel");
        e++;
    } else if(validarCombo("popupRegistroHuespedHotel\\:tipoHabitacion", -1)){
        $("#mensajeErrorSeleccionHab").text("Por favor seleccione la habitación");
        $("#mensajeErrorSeleccionHotel").text("");
        e++;
    } else if(validarCajaTexto("popupRegistroHuespedHotel\\:numResrvacion")){
        $("#mensajeErrorNumReser").text("Por favor agregue el número de reservación");
        e++;
    } 
    
    if(e == 0){
        $("#msgRegistroHuespedHotel").text("");
        $("#mensajeErrorSeleccionFechaIngreso").text("");
        $("#mensajeErrorSeleccionFechaSalida").text("");
        $("#mensajeErrorSeleccionFechaSalidaPropuesta").text("");
        $("#mensajeErrorSeleccionHotel").text("");
        $("#mensajeErrorSeleccionHab").text("");
        $("#mensajeErrorNumReser").text("");
        retorno = true; 
    }else{
        retorno = false;
    }
    return retorno;
}

function validaCrearSolicitudEstancia(){
    var e = 0;
    var retorno = false;
    if(validarCombo("popupCrearEditarSolicitudEstancia\\:idGerencia", -1)){
        $("#msgRegistroGerencia").text("Por favor seleccione la gerencia");
        e++;
    }else if(validarCombo("popupCrearEditarSolicitudEstancia\\:idOficina", -1)){
        $("#mensajeErrorSeleccionOficina").text("Por favor seleccione la oficinca que atenderá la solicitud");       
        $("#msgRegistroGerencia").text("");     
        e++;
    }else if(validarCombo("popupCrearEditarSolicitudEstancia\\:idMotivo", -1)){
        $("#mensajeErrorSeleccionMotivo").text("Por favor seleccione el motivo ");       
        $("#mensajeErrorSeleccionOficina").text("");     
        e++;
    }else if($("#popupCrearEditarSolicitudEstancia\\:fechaIngreso").val() == ""){
        $("#mensajeErrorSeleccionFechaIngreso").text("Por favor seleccione la fecha de inicio de la estancia");            
        $("#mensajeErrorSeleccionMotivo").text("");     
        e++;
    } else if($("#popupCrearEditarSolicitudEstancia\\:fechaSalida").val() == ""){
        $("#mensajeErrorSeleccionSalida").text("Por favor seleccione la fecha de salida ");
        $("#mensajeErrorSeleccionFechaIngreso").text("");     
        e++;
    } 
    if(e == 0){
        $("#msgRegistroGerencia").text("");
        $("#mensajeErrorSeleccionOficina").text("");
        $("#mensajeErrorSeleccionMotivo").text("");
        $("#mensajeErrorSeleccionFechaIngreso").text("");
        $("#mensajeErrorSeleccionSalida").text("");
        retorno = true; 
    }else{
        retorno = false;
    }
    return retorno;
    
}

function mostrarJustificaiconCompleta(){
    
    
}

/*function mostrarBotonPasarSolicitudesViajes(){    
     $("#panelPasarSolicitudes").show();
}*/




function confirmar(mensaje){       
    if( confirm("¿"+mensaje+" ?")){
        return true;
    }            
    else{
        return false;
    } 
}

// **********************************************************  ******************************************** //
function llenarPagos(pagos, nombre, forma, grafica, btnBuscarServicio, hidenServicio){
  //alert('datos  : :: : ' + pagos);
    if(pagos != null){
        pagoServicio(pagos, nombre, forma, grafica, btnBuscarServicio, hidenServicio);                     
    }else{
        alert('No hay datos');
    }
}

function pagoServicio(datos, nombre, forma, grafica, btnBuscarServicio,  hidenServicio){
    // alert (forma + ' ' + combo + ' ' +forma +' '+ grafica);
	// var nombre   = tomarValorSelccionadoCombo(forma +"\\:"+combo);
    // alert (forma + ' ' + combo + ' ' +forma +' '+ grafica);
    var servicio = datos.Servicio;        
    var total =datos.total; 
    //
    var chart = new Highcharts.Chart({
        chart: {
            renderTo: grafica,
            zoomType: 'xy'            
        },
        title: {
            text: nombre
        },
        xAxis: {
            categories:servicio,
            labels: {
                rotation: -0,
                style: {
                    fontSize: '10px',
                    fontFamily: 'Verdana, sans-serif'
                }
            }
        },
        yAxis: [{// primary yAxis
            min: 0,            
            title: {
                text: 'Total'
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
            name: 'Totales',
            colorByPoint: true,
            type: 'column',
            data: total,
            cursor: 'pointer',
            point: {
                events: {
                    click: function() {
                        var servicio = this.category
                   //     alert ('Comprador: '+ servicio);
                        $("#" + forma+"\\:" +  hidenServicio).val(servicio);
                        $("#" + forma+"\\:" + btnBuscarServicio).click();
                    }
                }
            },
            dataLabels: {
                enabled: true,
                rotation: 10,
                color: 'blue',
                rotation: -55,
                align: 'center',
                x: 5,
                y: -15,
                style: {
                    fontSize: '11px',
                    fontFamily: 'Verdana, sans-serif'
                }
            }
        }]
    });
}


function llenarPagoStaffServicio(datos, nombre, grafica, forma, btnBuscar, tipo){
    var fecha = datos.fecha;        
    var importe =datos.importe; 
   // alert(grafica);
    //
    var chart = new Highcharts.Chart({
        chart: {
            renderTo: grafica, 
            type: tipo,  
            zoomType: 'xy'          
        },
        title: {
            text: nombre
        },
        xAxis: {
            categories:fecha,
            labels: {
                rotation: -55,
                style: {
                    fontSize: '9px',
                    fontFamily: 'Verdana, sans-serif'
                }
            }
        },
        yAxis: [{// primary yAxis
            min: 0,            
            title: {
                text: 'Total'
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
            colorByPoint: true,
            name: 'Total',  
            data: importe,
            dataLabels: {
                enabled: true,
                rotation: 0,
                color: 'blue',
                align: 'center',
                x: 0,
                y: 5,
                style: {
                    fontSize: '10px',
                    fontFamily: 'Verdana, sans-serif'
                }
            }
        }]
    });
}


function ejecutarBoton(frm, boton) {
    $("#" + frm + "\\:" + boton).click();
}




///////////////////////DIALOGOS

function dialogoCambiarBloque(dialogo, forma, btnCerrar, btnGuardar, caja){    
    $("#" + dialogo).dialog({
        modal: true,
        hide: "explode",
        resizable: false,
        draggable: false,
        height: 'auto',
        width: 'auto',
        buttons: {
            Cerrar: function() {
                $( "#" + dialogo ).dialog( "close" );
            }
        },
        open: function(event, ui) { 
            // Get the dialog 
            var dialog = $(event.target).parents(".ui-dialog");
            // Get the buttons 
            var buttons = dialog.find(".ui-dialog-buttonpane").find(".ui-button");
            //
            var okButton = buttons[0]; 
            $(okButton).css({
                "color": "white", 
                "background" : "#0895d6"
            }); 
        }
    });        
    $(".ui-dialog-titlebar-close").hide();
    return true;
}

//
//
//Otemp

function autocompletarUsuario(frm, caja, cajaOculta, tipo, json) {
  alert(tipo );
    if(tipo == 19){
        if(json != null){
            autocompletar(frm, caja, cajaOculta, json);
        }else{
            alert('No se cargaron los invitados, favor de ingresar nuevamente a la opción.');
        }
    }else{
        if(json != null){
            autocompletar(frm, caja, cajaOculta, json);
        }else{
            alert('No se cargaron los usuarios, favor de ingresar nuevamente a la opción.');
        }     
    }
}
function autocompletar(frm, caja, cajaOculta, json){
    $("#" + frm + "\\:" + caja).autocomplete({
        source: json,
        focus: function(event, ui) {
            event.preventDefault();
            $(this).val('');
        },   
        minLength: 2,
        select: function(event, ui) {
            $("#" + frm + "\\:" + caja).val(ui.item.label);
            $("#" + frm + "\\:" + cajaOculta).val(ui.item.value);
            return false;
        }
    });
    $("#" + frm + "\\:" + caja).css({
        "text-align": "left",
        "width": "300px;"
    });
    $("ul.ui-autocomplete").addClass("autocompletar");
}

function llenarProveedor(forma, json, nombreMostrar) {
    proveedores = json;
    if(proveedores != null){
      $("#"+ forma + "\\:autocomplete" ).autocomplete({
	source: proveedores,
                        focus: function(event, ui) {
                            // prevent autocomplete from updating the textbox
                            event.preventDefault();
                            // manually update the textbox
                          //  $(this).val('');
                        },   
                        minLength: 2,
                        select: function(event, ui) {
			    $("#"+ forma+"\\:" + nombreMostrar).text(ui.item.label);
                            $("#"+ forma+"\\:autocomplete").val(ui.item.nombre);
                            $("#"+ forma+"\\:" + "hidenDes").val(ui.item.value);
			    return false;
                        }
                    });
                    $("#"+ forma+"\\:autocomplete").css({
                        "text-align": "left",
                        "width": "400px",
			"border-color": "green"
                    });
		    $("ul.ui-autocomplete").css({
                        "text-align": "left",
                        "width": "400px",
			"margin": "8px",
			"font-size": "11px",
			"text-align": "left",
			"overflow": "scroll",
			"overflow-style": "scrollbar ,marquee",
			"height": "200px",
			"z-index" : "1045"
                    });
                    //$("ul.ui-autocomplete").addClass('autocompletar');
                }else{
                    alert('No se cargaron los datos, por favor, intente otra vez.');
                }
            }

            

function llenarInvitado(forma, json, nombreMostrar) {
    proveedores = json;
    if(proveedores != null){
      $("#"+ forma + "\\:autocompleteInvi" ).autocomplete({
	source: proveedores,
                        focus: function(event, ui) {
                            // prevent autocomplete from updating the textbox
                            event.preventDefault();
                            // manually update the textbox
                          //  $(this).val('');
                        },   
                        minLength: 2,
                        select: function(event, ui) {
			    $("#"+ forma+"\\:" + nombreMostrar).text(ui.item.label);
                            $("#"+ forma+"\\:autocompleteInvi").val(ui.item.nombre);
                            $("#"+ forma+"\\:" + "hidenDesInv").val(ui.item.value);
			    return false;
                        }
                    });
                    $("#"+ forma+"\\:autocompleteInvi").css({
                        "text-align": "left",
                        "width": "400px",
			"border-color": "green"
                    });
		    $("ul.ui-autocomplete").css({
                        "text-align": "left",
                        "width": "400px",
			"margin": "8px",
			"font-size": "11px",
			"text-align": "left",
			"overflow": "scroll",
			"overflow-style": "scrollbar ,marquee",
			"height": "200px",
			"z-index" : "1045"
                    });
                    //$("ul.ui-autocomplete").addClass('autocompletar');
                }else{
                    alert('No se cargaron los datos, por favor, intente otra vez.');
    }
}    

function llenarRuta(forma, json, hidenId, btnEjecutar,autocompleteId,limpiarDespues) {
    origen = json; 
    if(origen != null){
      $("#"+ forma + "\\:"+autocompleteId ).autocomplete({
	source: origen,
                        focus: function(event, ui) {
                            // prevent autocomplete from updating the textbox
                            event.preventDefault();
                            // manually update the textbox
                          //  $(this).val('');
                        },   
                        minLength: 0,
                        select: function(event, ui) {
			  if(limpiarDespues == 1){
                            $("#"+ forma+"\\:"+ autocompleteId).val(ui.item.label);
			  } else {
			    $("#"+ forma+"\\:"+ autocompleteId).val(ui.item.nombre);
			  }
                            $("#"+ forma+"\\:" + hidenId).val(ui.item.value);
			    if(btnEjecutar !=null){
			    ejecutarBoton(forma,btnEjecutar);
			    }
			    return false;
                        }
                    });
     
                    $("#"+ forma+"\\:"+autocompleteId).css({
                        "text-align": "left",
                        "width": "320px",
			"border-color": "#ADD8E6"
                    });
		    $("ul.ui-autocomplete").css({
                        "text-align": "left",
                        "width": "320px",
			"margin": "8px",
			"font-size": "14px",
			"text-align": "left",
			"overflow": "scroll",
			"overflow-style": "scrollbar ,marquee",
			"height": "200px",
			"z-index" : "1045"
                    });
                    //$("ul.ui-autocomplete").addClass('autocompletar');
                }else{
                    alert('No se cargaron los datos, por favor, intente otra vez.');
                }
            }


/***************** FIN -  CERRAR DIALOGO  ***************************************/

function cerrarPop(dialogoPop){
  //alert('asasdasd ' +  dialogoPop);
    dialogoPop.hide();
}
function abrirDialogModal(dialog){
  $(dialog).modal('show');
}

function cerrarDialogModal(dialog){
  alert('prueba');
  $(dialog).modal('hide');
}

function cerrarDialogoCrearViaje(){
	$(dialogoPopUpCrearViaje).modal('hide');
	draggableInit();
	return true;
}

function cerrarDialogoAdmistrarViajeros(){
	$(dialogoPopUpAddOrRemoveViajeros).modal('hide');
	draggableInit();
	return true;
}

function cerrarDialogoInterceptarViajeTV() {
    cerrarDialogoModal(dialogoPopUpTVInterseccion);
}

function abrirDialogoInterceptarViajeTV() {
    abrirDialogoModal(dialogoPopUpTVInterseccion);
}

function cerrarDialogoInfoViajeTV() {
    cerrarDialogoModal(dialogoPopUpInfoViaje);
}

function abrirDialogoInfoViajeTV() {
    abrirDialogoModal(dialogoPopUpInfoViaje);
}

function cerrarDialogoModal(dialogo) {
    $(dialogo).modal('hide');
}

function abrirDialogoModal(dialogo) {
    $(dialogo).modal('show');
}


function mostrarNotificacionViajeSalir(){
    if(confirm('La operación va a poner el viaje en proceso, ¿está seguro?')) {
        dialogoViajeSalir.show(); 
        return true;
    }else{
        return false;
    }
}

function initControlChofer() {
    $('.bootstrap-switch-handle-off.bootstrap-switch-default').text('Sin chofer');
    $('.bootstrap-switch-handle-on.bootstrap-switch-primary').text('Con chofer');
    $('.bootstrap-switch.bootstrap-switch-wrapper').css('width', '200px');
    $('.bootstrap-switch-container').css('width', '300px');
    $('.bootstrap-switch-handle-on.bootstrap-switch-primary').css('width', '100px');
    $('.bootstrap-switch-label').css('width', '100px');
    $('.bootstrap-switch-handle-off.bootstrap-switch-default').css('width', '100px');
    if ($('.valConChofer').is(':checked')) {
        $('.bootstrap-switch-container').css('margin-left', '0px');
    } else {
        $('.bootstrap-switch-container').css('margin-left', '-100px');
    }
}
