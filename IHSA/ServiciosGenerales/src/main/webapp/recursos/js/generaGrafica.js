
function generaGrafica(datos, forma, grafica, combo){
    alert('datos  : :: : ' + datos);
    if(datos != null){
        pagoServicio(pagos, combo, forma, grafica);                     
    }else{
        alert('No hay datos');
    }
}


function pagoServicio(datos, forma, grafica, combo){
    var nombre   = tomarValorSelccionadoCombo(forma +"\\:"+combo);
    alert (forma + ' ' + combo + ' ' +forma +' '+ grafica);
    var servicio = datos.Servicio;        
    //
    var chart = new Highcharts.Chart({
        chart: {
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false
        },
        title: {
            text: 'Vehículo: ' + nombre.text()
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: true,
                    format: '<b>{point.name}</b>: {point.percentage:.1f} %',
                    style: {
                        color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                    }
                }
            }
        },
        series: [{
            type: 'pie',
            name: 'Browser share',
            data: servicio,
                
                    name: 'Otro',
                    y: 12.8,
                    sliced: true,
                    selected: true
                
	}]
  });
}
