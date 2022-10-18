

function grafica(datos, convenio, forma, btnBuscar) {
    var fecha = datos.fecha;
    var total = datos.total;
    var chart = new Highcharts.Chart({
	chart: {
	    renderTo: 'graficaOCSConvenio',
	    zoomType: 'xy'
	},
	title: {
	    text: 'Ordenes de Compras/Servicios'
	},
	subtitle: {
	    text: 'Contrato: ' + convenio,
	    style: {
		fontSize: '13px',
		fontFamily: 'Verdana, sans-serif',
		textShadow: '0 0 1px gray'
	    }
	},
	xAxis: {
	    categories: fecha,
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
		    text: 'Total OC/S'
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
		type: 'column',
		data: total,
		cursor: 'pointer',
		point: {
		    events: {
			click: function () {
			    var fecha = this.category
			    $("#" + forma + "\\:" + btnBuscar).click();
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
	    }]
    });
}
