var json;
var proveedores;
var datosProveedores;

function setJson(json) {
    proveedores = json;
    if (proveedores != null) {
	$("#form1\\:autocomplete").autocomplete({
	    source: proveedores,
	    focus: function (event, ui) {
		// prevent autocomplete from updating the textbox
		event.preventDefault();
		// manually update the textbox
		$(this).val('');
	    },
	    minLength: 2,
	    select: function (event, ui) {
		bloquearPantalla();
		$("form1\\:autocomplete").val(ui.item.nombre);
		$("#form1\\:" + "hidenDes").val(ui.item.value);
		$("#form1\\:" + 'hidenNombreProv').val(ui.item.nombre);
		ejecutarFiltros('form1', 'btnFiltro');
		llenarDatosProveedor("form1", ui.item);
		return false;
	    }
	});
	$("#form1\\:autocomplete").css({
	    "text-align": "left",
	    "width": "400px;"
	});
	$("ul.ui-autocomplete").addClass("autocompletar");
    } else {
	alert('No se cargaron los proveedores, por favor, intente solicitar la OC/S otra vez.');
    }
}


function setJsonProveedor(json) {
    this.datosProveedores = json;
}

function datosProveedor(frmP, jSonProveedor) {
    llenarDatosProveedor(frmP, jSonProveedor);
}

function prepararAutocomplete(componente, hidenC) {
    $(componente).autocomplete({
	source: proveedores,
	focus: function (event, ui) {
	    // prevent autocomplete from updating the textbox
	    event.preventDefault();
	    // manually update the textbox
	    $(this).val('');
	},
	select: function (event, ui) {
	    bloquearPantalla();
	    $(componente).val(ui.item.nombre);
	    $("#form1\\:" + hidenC).val(ui.item.value);
	    $("#form1\\:" + 'hidenNombreProv').val(ui.item.nombre);
	    ejecutarFiltros('form1', 'btnFiltro');
	    llenarDatosProveedor("form1", ui.item);
	   // alert('asaskdhasdhaskashd');
	    return false;
	}
    })
    $("ul.ui-autocomplete").addClass("autocompletar");
//return true;
}
function autoCompletarProveedor(componente, hidenC, frm) {
    $(componente).autocomplete({
	source: this.datosProveedores,
	focus: function (event, ui) {
	    // prevent autocomplete from updating the textbox
	    event.preventDefault();
	    // manually update the textbox
	    $(this).val('');
	},
	select: function (event, ui) {
	    $(componente).val(ui.item.nombre);
	    $("#" + frm + "\\:" + hidenC).val(ui.item.value);
	    ejecutarFiltros(frm, 'btnBuscar');
	    return false;
	}
    })
    $(componente).css({
	"text-align": "left",
	"width": "400px;"
    });
    $("ul.ui-autocomplete").addClass("autocompletar");
//return true;
}
function autoCompletar(componente, hidenC, frm) {
    $(componente).autocomplete({
	source: proveedores,
	focus: function (event, ui) {
	    event.preventDefault();
	    $(this).val('');
	},
	select: function (event, ui) {
	    $(componente).val(ui.item.label);
	    $("#" + frm + "\\:" + hidenC).val(ui.item.value);
	    ejecutarFiltros(frm, 'btnBuscar');
	    return false;
	}
    })
    $(componente).css({
	"text-align": "left",
	"width": "400px;"
    });
    $("ul.ui-autocomplete").addClass("autocompletar");
//return true;
}

function obtenerNombreform(formObject) {
    do {
	formObject = formObject.parentNode;
    } while (formObject.tagName != "FORM");
    return formObject.name;
}

/// FIN

function ejecutarFiltros(frm, boton) {
    $("#" + frm + "\\:" + boton).click();
}

function llenarDatosProveedor(frm, jSon) {
    $("#" + frm + "\\:" + "pNombre").text(jSon.nombre);
    $("#" + frm + "\\:" + "rfc").text(jSon.rfc);
    $("#" + frm + "\\:" + "pCalle").text(jSon.calle);
    $("#" + frm + "\\:" + "pColonia").text(jSon.colonia);
    $("#" + frm + "\\:" + "pCiudad").text(jSon.ciudad);
    $("#" + frm + "\\:" + "pEstado").text(jSon.estado);
    $("#" + frm + "\\:" + "pPais").text(jSon.pais);
    $("#" + frm + "\\:" + "pTel").text(jSon.telefono);
    $("#" + frm + "\\:" + "pFax").text(jSon.fax);
}

function limpiarTabla(frm, componente) {
    $("#" + frm + "\\:" + componente).empty();
}
var statusUpdate = function statusUpdate(data) {
    if (data.status == 'error') {
	$("#areaBlock").css("display", "none");
    }
    if (data.status == 'begin') {
	$("#areaBlock").css("display", "block");
    }
    if (data.status == 'complete') {
	$("#areaBlock").css("display", "none");
    }
    if (data.status == 'success') {
	$("#areaBlock").css("display", "none");
    }
}
var status = function status(data) {
    $("#areaBlock").css("display", "none");
}
function bloquearPantalla() {
    jsf.ajax.addOnEvent(statusUpdate);
    jsf.ajax.addOnError(statusUpdate);
    return true;
}

function quitarBloquearPantalla() {
    jsf.ajax.addOnEvent(status);
    jsf.ajax.addOnError(status);
    return true;
}
$(document).init(function () {
    bloquearPantalla();
});
function areaBlockCSS() {
    $("#areaBlock").css({
	"display": "block",
	"right": "100px",
	"bottom": "155px",
	"position": "fixed",
	"z-index": "25006"
    });
}

function quitarAareaBlockCSS() {
    $("#areaBlock").css({
	"display": "block",
	"right": "100px",
	"bottom": "155px",
	"position": "fixed",
	"z-index": "25006"
    });
}