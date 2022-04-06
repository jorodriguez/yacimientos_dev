//alert("Un mensaje de prueba");
window.history.forward(1); 
document.attachEvent("onkeydown", my_onkeydown_handler); 
function my_onkeydown_handler() 
{ 
    switch (event.keyCode) 
    { 

        case 116 : // 'F5' 
            event.returnValue = false; 
            event.keyCode = 0; 
            window.status = "La tecla F5 está deshabilitada en esta página"; 
            break; 
    }

}