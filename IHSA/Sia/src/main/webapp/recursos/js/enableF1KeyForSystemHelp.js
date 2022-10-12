/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
var isCtrl = false;

$(document).keyup(function (e) {
	if(e.which == 17) isCtrl=false;
}).keydown(function (e) {
	if(e.which == 17) isCtrl=true;
	if(e.which == 112 && isCtrl == true) {
            //Aqui deberia poder mandar a llamar un método del Bean que me mandara a la página de consultarArbolAyudas
		//run code for CTRL+F1 -- ie, save!
		return false;
	}
});

