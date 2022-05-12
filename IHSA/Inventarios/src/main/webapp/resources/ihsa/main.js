function crearBotones() {
    ice.ace.jq('.buttons-section button, .hbutton').each(function () {
        createButton(ice.ace.jq(this));
    });
    ice.ace.jq('.hbutton-bu').each(function () {
        createButton(ice.ace.jq(this), 'ui-icon-triangle-1-e');
    });
    ice.ace.jq('.hbutton-re').each(function () {
        createButton(ice.ace.jq(this), 'ui-icon-arrowreturnthick-1-s');
    });
    ice.ace.jq('.hbutton-cr').each(function () {
        createButton(ice.ace.jq(this), 'ui-icon-plus');
    });
    ice.ace.jq('.hbutton-pro').each(function () {
        createButton(ice.ace.jq(this), 'ui-icon-gear');
    });
    ice.ace.jq('.hbutton-guardar').each(function () {
        createButton(ice.ace.jq(this), 'ui-icon-disk');
    });
    ice.ace.jq('.hbutton-reg').each(function () {
        createButton(ice.ace.jq(this), 'ui-icon-arrowreturnthick-1-w');
    });
    ice.ace.jq('.hbutton-imp').each(function () {
        createButton(ice.ace.jq(this), 'ui-icon-print');
    });
    ice.ace.jq('.hbutton-buscar').each(function () {
        createButton(ice.ace.jq(this), 'ui-icon-search');
    });
    ice.ace.jq('.hbutton-actualizar').each(function () {
        createButton(ice.ace.jq(this), 'ui-icon-refresh');
    });
    ice.ace.jq('.hbutton-cerrado').each(function () {
        createButton(ice.ace.jq(this), ' ui-icon-mail-closed');
    });
    ice.ace.jq('.hbutton-eliminado').each(function () {
        createButton(ice.ace.jq(this), 'ui-icon-trash');
    });
}

function createButton(element, icon) {
    icon = icon || element.data('icon');
    var options = {};
    if (icon) {
        options.icons = {
            primary: icon
        };
    }

    element.button(options);
}

function cargarBuscar() {
    ice.ace.jq('#btnBuscar').unbind('click').click(function () {
        ice.ace.jq('#filtros').slideToggle();
    });
}

function mostrarDialogoAjax(data) {
    if (data.status === 'success') {
        fijarTituloDialogo(data);
        crearDialogo.show();
        crearWidgets();
    }
}

function mostrarDialogoCampo() {
    crearDialogoCampo.show();
}

function mostrarDialogoSolicitarMaterial() {
    crearDialogoMaterial.show();
}

function cerrarDialogoSolicitarMaterial() {
    crearDialogoMaterial.hide();
}

function mostrarDialogoAutorizarMaterial() {
    dialogoSolicitarAutorizar.show();
}
function cerrarDialogoAutorizarMaterial() {
    dialogoSolicitarAutorizar.hide();
}

function mostrarDialogoFormatosSalidaMaterial() {
    dialogoDatosProcesoFolio.show();
}

function cerrarDialogoFormatosSalidaMaterial() {
    dialogoDatosProcesoFolio.hide();
}

function mostrarDialogoCampoRefrescar() {
    location.reload();
}

function mostrarDialogo(dialogo) {
    dialogo.show();
}
function cerrarDialogo(dialogo) {
    dialogo.hide();
}


function fijarTituloDialogo(data) {
    if (data.status !== 'success')
        return;
    var titulo = ice.ace.jq('span[id$="hdTituloDialogo"]').text();
    crearDialogo.jq.parent().find('span.ui-dialog-title').text(titulo);
    regresarSegundoDialogo();
}

function fijarTituloDialogoArticulo(data) {
    if (data.status !== 'success')
        return;
    mostrarCategorias();
}

var originalClick;
function regresarSegundoDialogo() {
    var close = ice.ace.jq('.ui-dialog a.ui-dialog-titlebar-close:visible');
    if (close.length === 0)
        return;
    if (!originalClick) {
        originalClick = close.data("events").click[0];
    }
    var cancelarButton = close.parent().parent().find('.ui-dialog-content>span:visible').find('.hbutton.cancelar');
    if (cancelarButton.length === 0) {
        close.unbind('click').bind('click', originalClick.handler);
        return;
    }
    close.unbind('click').bind('click', function (e) {
        e.preventDefault();
        cancelarButton.trigger('click');
    });
}

function ajaxGuardar(data) {
    if (data.status === 'success' && ice.ace.jq(data.source).parent().find('.ui-state-error:visible').length === 0) {
        crearDialogo.hide();
    }
}

function imprimir(data) {
    if (data.status !== 'success')
        return;
    window.print();
}

function conciliar(data) {
    if (data.status !== 'success')
        return;
    ice.ace.jq('input[id$=txtArticulo]').focus();
}

function seleccionarGeneral(todos, seleccion) {
    if (ice.ace.jq('.'+todos).is(":checked")) {
        ice.ace.jq('.' + seleccion).prop("checked", "checked");
    } else {
        ice.ace.jq('.' + seleccion).prop("checked", ""); 
    }
}

function mostrarConfirmar(elemento, dialogo) {
    window.dialogo = dialogo || eliminarDialogo;
    window.dialogo.show();
    window.confirmarElemento = ice.ace.jq(elemento).next();
}

function confirmar() {
    ice.ace.jq(window.confirmarElemento).click();
    window.dialogo.hide();
}

//Implementacion de Ajax monitor
var mustShow = false;
jsf.ajax.addOnEvent(function (data) {
    // Can be "begin", "complete" and "success"
    var ajaxstatus = data.status;
    var ajaxloader = ice.ace.jq('#ice-monitor-over, .ice-sub-mon');
    var delay = 300;
    switch (ajaxstatus) {
        // This is called right before ajax request is been sent.
        case 'begin':
            mustShow = true;
            setTimeout(function () {
                if (!mustShow)
                    return;
                ajaxloader.show();
            }, delay);
            break;
        case 'complete':

            // This is called right after ajax response is received.
            mustShow = false;
            ajaxloader.hide();
            break;
        case 'success':
            crearBotones();
            crearWidgets();
            cargarBuscar();
            break;
    }
});
//Cargar widgets y manejadores de eventos al cargar la pagina
ice.ace.jq(document).ready(function () {
    crearBotones();
    crearWidgets();
    cargarBuscar();
    ice.ace.jq('div[id$=nuevoDialogo] input:not(input[type=submit])').bind('keyup', function (e) {
        if (e.keyCode !== 13)
            return;
        ice.ace.jq(this).parents('div[id$=nuevoDialogo]').find('input[type=submit]').trigger('click');
    });
});
function crearWidgets() {
    ice.ace.jq('.articuloAutoComplete')
            .unbind('blur').bind('blur', function () {
        if (ice.ace.jq.trim(ice.ace.jq(this).val()) === '') {
            ice.ace.jq(this).next().val('');
        }
    }).unbind('keypress').bind('keypress', function (e) {
        var code = (e.keyCode ? e.keyCode : e.which);
        if (code === 13) { //Enter keycode
            e.preventDefault();
            return false;
        }
        return true;
    }).autocomplete({
        minLength: 2,
        source: function (request, response) {
            var almacenId = ice.ace.jq('.almacen-origen').val();
            var campo = ice.ace.jq('#frmCampoSalir\\:cmdCampo').text();
            var url = '../../api/articulo';
            var params = 'palabra=' + encodeURIComponent(request.term) + '&campo=' + campo;
            if (almacenId !== undefined) {
                if (almacenId != '') {
                    url = '../../api/articulo/almacen';
                    params = 'nombreArticulo=' + encodeURIComponent(request.term) + '&almacenId=' + almacenId + '&campo=' + campo;
                } else {
                    alert('Por favor seleccione un almacén.');
                    return;
                }
            }

            ice.ace.jq.ajax({
                url: url,
                data: params,
                success: function (data) {
                    response(ice.ace.jq.map(data, function (item) {
                        return {
                            label: item.nombre,
                            value: item.id
                        };
                    }));
                }
            });
        },
        focus: function (event, ui) {
            return false;
        },
        select: function (event, ui) {
            ice.ace.jq(this).next().val(ui.item.value);
            ice.ace.jq(this).val(ui.item.label).trigger('change').trigger('blur');
            return false;
        }
    })
            .each(function () {
                var autocompleteObject = ice.ace.jq(this).data("autocomplete");
                var renderItemOriginal = autocompleteObject._renderItem;
                autocompleteObject._renderItem = function (ul, item) {
                    //Add the .ui-state-disabled class and don't wrap in <a> if value is empty
                    if (item.value === 0) {
                        return ice.ace.jq('<li class="ui-state-disabled item-empty">' + item.label + '</li>').appendTo(ul);
                    } else {
                        renderItemOriginal.call(this, ul, item);
                    }
                };
            });
}

function crearWidgetsAjax(data) {
    if (data.status !== 'success')
        return;
    crearWidgets();
}

function desabilitarAlmacenDestino(data) {
    if (data.status !== 'success')
        return;
    ice.ace.jq('select[id$=smFiltroAlmacenDestino] option[value=' + ice.ace.jq('select[id$=smFiltroAlmacen]').val() + ']').attr('disabled', true);
}

function selecionoCategoriaRaiz(elemento) {
    ice.ace.jq(elemento).find("~ select").remove();
    ice.ace.jq(elemento).parents('table').eq(0).find('[id$=txtNombre]').val('');
    ice.ace.jq(elemento).parent().find('input:hidden').val('');
    crearSkuCodigo(ice.ace.jq(this));
    if (!ice.ace.jq(elemento).val())
        return;
    ice.ace.jq(elemento).parent().find('input:hidden').val(ice.ace.jq(elemento).val()).trigger('change');
    agregarCombo(ice.ace.jq(elemento));
}

function agregarCombo(combo) {
    var ajaxloader = ice.ace.jq('#ice-monitor-over, .ice-sub-mon');
    ajaxloader.show();
    var relativePath = window.location.href.indexOf('inventarios') == -1 ? '../' : '../../'
    ice.ace.jq.ajax({
        url: relativePath + 'api/articulo/subcategorias',
        data: {categoriaId: ice.ace.jq(combo).val()},
        success: function (data) {
            ajaxloader.hide();
            if (data.length == 0)
                return;
            data.unshift({id: '', nombre: '- Seleccione -'});
            var nuevoCombo = ice.ace.jq('<select>', {
                'class': 'ui-widget ui-inputfield ui-state-default ui-state-optional',
                change: function () {
                    ice.ace.jq(this).find("~ select").remove();
                    if (!ice.ace.jq(this).val()) {
                        ice.ace.jq(this).parent().find('input:hidden').val(ice.ace.jq(this).prev().val()).trigger('change');
                        crearSkuCodigo(ice.ace.jq(this));
                        return;
                    }
                    agregarCombo(ice.ace.jq(this));
                }
            });
            ice.ace.jq(data).each(function () {
                var option = ice.ace.jq('<option>', {value: this.id, text: this.nombre});
                option.data('codigo', this.codigo);
                nuevoCombo.append(option);
            });
            combo.after(nuevoCombo);
            var codes = combo.data('codes');
            if (codes) {
                combo.data('codes', '');
                nuevoCombo.find('option[value=' + codes.shift() + ']').prop('selected', true);
                if (nuevoCombo.val()) {
                    nuevoCombo.data('codes', codes);
                    nuevoCombo.trigger('change');
                }
            }
        }
    });
    var campoNombre = ice.ace.jq(combo).parents('table').eq(0).find('[id$=txtNombre]');
    var nombre = '';
    combo.parent().find('select').each(function (index) {
        nombre = nombre + (index === 0 ? '' : ' / ') + ice.ace.jq(this).find('option:selected').text()
    });
    campoNombre.val(nombre);
    combo.parent().find('input:hidden').val(combo.val()).trigger('change');
    crearSkuCodigo(combo);
}

function crearSkuCodigo(combo) {
    var sku = '';
    var ids = '';
    combo.parent().find('select').each(function (index) {
        sku = sku + (index === 0 ? '' : '-') + (index == 0 ? getCodigoPrincipal(ice.ace.jq(this)) : ice.ace.jq(this).find('option:selected').data('codigo'));
        ids = ids + (index === 0 ? '' : ',') + ice.ace.jq(this).val();
    });
    combo.parents('table').eq(0).find('[id$=hdCategoriasIds]').val(ids).trigger('change');
    combo.parents('table').eq(0).find('[id$=txtCodigo]').val(sku).trigger('change');
}

function mostrarEditarArticulo(data) {
    if (data.status !== 'success')
        return;
    mostrarDialogoAjax(data);
    mostrarCategorias();
}

function mostrarCategorias() {
    var code = ice.ace.jq('[id$=hdCategoriasIds]').val();
    var codes = code.split(',');
    var combo = ice.ace.jq('[id$=smCategoriasPrincipales]');
    combo.val('');
    combo.find('option[value=' + codes.shift() + ']').prop('selected', true);
    combo.data('codes', codes);
    selecionoCategoriaRaiz(combo);
}

function guardarUnidadEmbebida() {
    var contenedor = ice.ace.jq('[id$=unidadComponente]');
    var txtNombre = contenedor.find('[id$=txtNombre]');
    if (!txtNombre.val().trim())
        contenedor.find('[id$=btnGuardar]').click();
    else
        contenedor.find('[id$=btnGuardarImmediate]').click();
}

function getCodigoPrincipal(combo) {
    var codigos = combo.parents('table').eq(0).find('[id$=hdCodigosPrincipales]').text().split('-');
    return codigos[combo.find('option:selected').index() - 1];
}

function verificaSeleccion(clase) {
    var crear = false;
    var allCheckBox = document.getElementsByClassName(clase);
    for (var i = 0; i < allCheckBox.length; i++) {
        if (allCheckBox[i].checked) {
            crear = true;
            break;
        }
    }
    if (!crear) {
        alert('Seleccione una celda');
    }
    return crear;
}

function quitaSeleccion(clase) {
    var crear = false;
    var allCheckBox = document.getElementsByClassName(clase);
    for (var i = 0; i < allCheckBox.length; i++) {
        if (allCheckBox[i].checked) {
            break;
        }
    }
    return crear;
}

