function crearBotones() {
    $('.buttons-section button, .hbutton').each(function () {
        createButton($(this));
    });
    $('.hbutton-bu').each(function () {
        createButton($(this), 'ui-icon-triangle-1-e');
    });
    $('.hbutton-re').each(function () {
        createButton($(this), 'ui-icon-arrowreturnthick-1-s');
    });
    $('.hbutton-cr').each(function () {
        createButton($(this), 'ui-icon-plus');
    });
    $('.hbutton-pro').each(function () {
        createButton($(this), 'ui-icon-gear');
    });
    $('.hbutton-guardar').each(function () {
        createButton($(this), 'ui-icon-disk');
    });
    $('.hbutton-reg').each(function () {
        createButton($(this), 'ui-icon-arrowreturnthick-1-w');
    });
    $('.hbutton-imp').each(function () {
        createButton($(this), 'ui-icon-print');
    });
    $('.hbutton-buscar').each(function () {
        createButton($(this), 'ui-icon-search');
    });
    $('.hbutton-actualizar').each(function () {
        createButton($(this), 'ui-icon-refresh');
    });
    $('.hbutton-cerrado').each(function () {
        createButton($(this), ' ui-icon-mail-closed');
    });
    $('.hbutton-eliminado').each(function () {
        createButton($(this), 'ui-icon-trash');
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
    $('#btnBuscar').unbind('click').click(function () {
        $('#filtros').slideToggle();
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
    var titulo = $('span[id$="hdTituloDialogo"]').text();
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
    var close = $('.ui-dialog a.ui-dialog-titlebar-close:visible');
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
    if (data.status === 'success' && $(data.source).parent().find('.ui-state-error:visible').length === 0) {
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
    $('input[id$=txtArticulo]').focus();
}

function seleccionarGeneral(todos, seleccion) {
    if ($('.' + todos).is(":checked")) {
        $('.' + seleccion).prop("checked", "checked");
    } else {
        $('.' + seleccion).prop("checked", "");
    }
}

function mostrarConfirmar(elemento, dialogo) {
    window.dialogo = dialogo || eliminarDialogo;
    window.dialogo.show();
    window.confirmarElemento = $(elemento).next();
}

function confirmar() {
    $(window.confirmarElemento).click();
    window.dialogo.hide();
}

//Implementacion de Ajax monitor
var mustShow = false;
jsf.ajax.addOnEvent(function (data) {
    // Can be "begin", "complete" and "success"
    var ajaxstatus = data.status;
    var ajaxloader = $('#ice-monitor-over, .ice-sub-mon');
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
//$(document).ready(function () {
//    crearBotones();
//    crearWidgets();
//    cargarBuscar();
//    $('div[id$=nuevoDialogo] input:not(input[type=submit])').bind('keyup', function (e) {
//        if (e.keyCode !== 13)
//            return;
//        $(this).parents('div[id$=nuevoDialogo]').find('input[type=submit]').trigger('click');
//    });
//});
function crearWidgets() {
    $('.articuloAutoComplete')
            .unbind('blur').bind('blur', function () {
        if ($.trim($(this).val()) === '') {
            $(this).next().val('');
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
            var almacenId = $('.almacen-origen').val();
            var campo = $('#frmCampoSalir\\:cmdCampo').text();
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

            $.ajax({
                url: url,
                data: params,
                success: function (data) {
                    response($.map(data, function (item) {
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
            $(this).next().val(ui.item.value);
            $(this).val(ui.item.label).trigger('change').trigger('blur');
            return false;
        }
    })
            .each(function () {
                var autocompleteObject = $(this).data("autocomplete");
                var renderItemOriginal = autocompleteObject._renderItem;
                autocompleteObject._renderItem = function (ul, item) {
                    //Add the .ui-state-disabled class and don't wrap in <a> if value is empty
                    if (item.value === 0) {
                        return $('<li class="ui-state-disabled item-empty">' + item.label + '</li>').appendTo(ul);
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
    $('select[id$=smFiltroAlmacenDestino] option[value=' + $('select[id$=smFiltroAlmacen]').val() + ']').attr('disabled', true);
}

function selecionoCategoriaRaiz(elemento) {
    $(elemento).find("~ select").remove();
    $(elemento).parents('table').eq(0).find('[id$=txtNombre]').val('');
    $(elemento).parent().find('input:hidden').val('');
    crearSkuCodigo($(this));
    if (!$(elemento).val())
        return;
    $(elemento).parent().find('input:hidden').val($(elemento).val()).trigger('change');
    agregarCombo($(elemento));
}

function agregarCombo(combo) {
    var ajaxloader = $('#ice-monitor-over, .ice-sub-mon');
    ajaxloader.show();
    var relativePath = window.location.href.indexOf('inventarios') == -1 ? '../' : '../../'
    $.ajax({
        url: relativePath + 'api/articulo/subcategorias',
        data: {categoriaId: $(combo).val()},
        success: function (data) {
            ajaxloader.hide();
            if (data.length == 0)
                return;
            data.unshift({id: '', nombre: '- Seleccione -'});
            var nuevoCombo = $('<select>', {
                'class': 'ui-widget ui-inputfield ui-state-default ui-state-optional',
                change: function () {
                    $(this).find("~ select").remove();
                    if (!$(this).val()) {
                        $(this).parent().find('input:hidden').val($(this).prev().val()).trigger('change');
                        crearSkuCodigo($(this));
                        return;
                    }
                    agregarCombo($(this));
                }
            });
            $(data).each(function () {
                var option = $('<option>', {value: this.id, text: this.nombre});
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
    var campoNombre = $(combo).parents('table').eq(0).find('[id$=txtNombre]');
    var nombre = '';
    combo.parent().find('select').each(function (index) {
        nombre = nombre + (index === 0 ? '' : ' / ') + $(this).find('option:selected').text()
    });
    campoNombre.val(nombre);
    combo.parent().find('input:hidden').val(combo.val()).trigger('change');
    crearSkuCodigo(combo);
}

function crearSkuCodigo(combo) {
    var sku = '';
    var ids = '';
    combo.parent().find('select').each(function (index) {
        sku = sku + (index === 0 ? '' : '-') + (index == 0 ? getCodigoPrincipal($(this)) : $(this).find('option:selected').data('codigo'));
        ids = ids + (index === 0 ? '' : ',') + $(this).val();
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
    var code = $('[id$=hdCategoriasIds]').val();
    var codes = code.split(',');
    var combo = $('[id$=smCategoriasPrincipales]');
    combo.val('');
    combo.find('option[value=' + codes.shift() + ']').prop('selected', true);
    combo.data('codes', codes);
    selecionoCategoriaRaiz(combo);
}

function guardarUnidadEmbebida() {
    var contenedor = $('[id$=unidadComponente]');
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

