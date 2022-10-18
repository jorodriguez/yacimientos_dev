function ejecutarBoton(frm, boton) {
    $("#" + frm + "\\:" + boton).click();
}

function cerrarDialogoModal(dialogo) {
    $(dialogo).modal('hide');
}

function abrirDialogoModal(dialogo) {
    $(dialogo).modal('show');
}
/*
 * funcion que valida que una caja de texto contenga valor
 *  Retorna true si la caja evaluada esta vacia.
 **/
function validarCajaTexto(forma, idComponente){
  var componente = "#"+forma + "\\:" +idComponente;
    if($(componente).val() == "" || $(componente).val() == undefined || $(componente).val() == null){
        alert("Es necesario agregar valor.");
	return false;
    }else{
        return true;
    }
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

function validarComponenteTextoFecha(componente){
    if(($(componente).val() == "" || $(componente).val() == undefined || $(componente).val() == null)){
	$(componente).addClass("errorSIA");
	return true;
    }else{
	$(componente).removeClass("errorSIA");
        return false;
    }
}

function resetBorde(componente){
	$(componente).removeClass("errorSIA");
        return true;
}

function validarForma(forma) {
        if(validarTxts(forma)){
	        alert("Los datos marcados en rojo son requeridos.");
		return false;
	}
	if(validarSelects(forma)){
	        alert("Los datos marcados en rojo son requeridos.");
		return false;
	}	
	if(validarTxtsTels(forma)){
	        alert("El número telefónico no tiene el formato correcto. Deben ser 10 dígitos.");
		return false;
	}	
	if(validarTxtsMails(forma)){
	        alert("El correo electrónico no tiene el formato correcto.");
		return false;
	}	
	if(validarTxtsCURP(forma)){
	        alert("El CURP no tiene el formato correcto.");
		return false;
	}	
	if(validarTxtsRFC(forma)){
	        alert("El RFC no tiene el formato correcto.");
		return false;
	}     
	if(validarTxtsFecha(forma)){
	        alert("La(s) fecha(s) marcadas en rojo son requeridas.");
		return false;
	}     	

	return true;
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

function validarTxtsFecha(forma) {
	var texts = $("#"+forma+" .fechaOb :input");
	for (i = 0; i < texts.length; i++) {
    		if(validarComponenteTextoFecha(texts[i])){
			return true;
		}
	}
	return false;
}

function validarSelects(forma) {
	var selects = $("#"+forma+" .selectt");
	for (i = 0; i < selects.length; i++) {
    		if(validarSE(selects[i], 0)){
			return true;
		}
	}
	return false;
}

function validarSE(componente, valorAComparar) {
	if ($(componente).val() != valorAComparar) {
		return false;
	} else {
		$(componente).addClass("errorSIA");
		return true;
        }
}

function validarTxtsTels(forma) {
	var tels = $("#"+forma+" .tel");
	for (i = 0; i < tels.length; i++) {
    		if(validarComponenteTels(tels[i])){
			return true;
		}
	}
	return false;
}

function validarTxtsMails(forma) {
	var tels = $("#"+forma+" .mail");
	for (i = 0; i < tels.length; i++) {
    		if(validarComponenteMails(tels[i])){
			return true;
		}
	}
	return false;
}

function validarTxtsCURP(forma) {
	var tels = $("#"+forma+" .curp");
	for (i = 0; i < tels.length; i++) {
    		if(validarComponenteCURP(tels[i])){
			return true;
		}
	}
	return false;
}

function validarTxtsRFC(forma) {
	var tels = $("#"+forma+" .rfc");
	for (i = 0; i < tels.length; i++) {
    		if(validarComponenteRFC(tels[i])){
			return true;
		}
	}
	return false;
}

function validarComponenteRFC(componente){  
	if (numberRFC($(componente).val())) {
		return false;
	}else{
		$(componente).addClass("errorSIA");   
	        return true;
	}
}

function validarComponenteCURP(componente){  
	if (numberCURP($(componente).val())) {
		return false;
	}else{
		$(componente).addClass("errorSIA");   
	        return true;
	}
}

function validarComponenteMails(componente){
	if (eMail($(componente).val())) {
		return false;
	}else{
		$(componente).addClass("errorSIA");
	        return true;
	}
}

function validarComponenteTels(componente){
	if (phonenumber($(componente).val())) {
		return false;
	}else{
		$(componente).addClass("errorSIA");
	        return true;
	}
}

function numberRFC(inputtxt) {
  var patt = /[a-z0-9A-Z]{9,16}/;
  if(patt.test(inputtxt)) {
    return true;
  }  
  else {  
    return false;
  }
}

function numberCURP(inputtxt) {
  var patt = /[a-z0-9A-Z]{18}/;
  if(patt.test(inputtxt)) {
    return true;
  }  
  else {  
    return false;
  }
}

function phonenumber(inputtxt) {
  var patt = /^[-+]?\d{1,10}?$/;
  if(patt.test(inputtxt)) {
    return true;
  } else {
    return false;
  }
}

function eMail(inputtxt) {
  var patt = /\S+@\S+\.\S+/;
  if(patt.test(inputtxt)) {
    return true;
  } else {
    return false;
  }
}
