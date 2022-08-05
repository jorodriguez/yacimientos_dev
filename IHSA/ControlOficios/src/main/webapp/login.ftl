<!DOCTYPE html>
<html>
    <head>
        <title>Control de Oficios</title>

        <script type="text/javascript">
            function validaUser(){
                var e =0; 
                var user = document.getElementById("user").value; 
                var pass = document.getElementById("pass").value;
            
                if(user.trim() == "" || pass.trim == ""){ 
                    alert('Ingrese Usuario y contraseña para continuar');
                    e++;
                }

                if(e==0){ 
                    return true;  
                } else {
                    return false;
                }
            }
            </script>

        <link href="/resources/boot/css/bootstrap.min.css" rel="stylesheet" id="bootstrap-css">
        <link rel="stylesheet" type="text/css" href="/resources/font-awesome/css/font-awesome.min.css"></link>
        <script type="text/javascript" src="/resources/boot/js/jquery1.11.0.js"></script>
        <script type="text/javascript" src="/resources/boot/js/bootstrap.js"></script>
        </head>
    <body>
        <div id="login-overlay" class="modal-dialog" style="margin-top: 7%">
            <div class="modal-content">
                <div class="modal-header">
                    <div class="row">
                        <div class="col-lg-4">
                            <img src="/Sia/resources/imagenes/logo.png" 
                                class="img-responsive pull-left"/>
                        </div>
                        <div class="col-lg-8">
                            <h4>Control de Oficios : Introduzca sus datos para poder descargar el archivo</h4>
                        </div>
                    </div>
                </div>

                <div class="modal-body">
                    <div class="row">
                        <div class="col-lg-2 col-xs-12"></div>
                        <div class="col-lg-8 col-xs-12">
                            <div class="well">
                                <form action="DACOF" class="loginForm" method="post">
                                    <div class="form-group">
                                        <label for="usuario" 
                                               class="control-label">Usuario</label>
                                        <div class="input-group">
                                            <div class="input-group-addon">
                                                <i class="fa fa-user"></i>
                                                </div>
                                            <input type="text" id="user" class="form-control" placeholder="Usuario" name ="user">
                                            </div>
                                        </div>
                                    <div class="form-group">
                                        <label for="clave" class="control-label">Contrase&ntilde;a</label>
                                        <div class="input-group">
                                            <div class="input-group-addon">
                                                <i class="fa fa-lock"></i>
                                                </div> 
                                            <input type="password" id="pass" class="form-control" placeholder="Contraseña" name ="pass">
                                            </div>
                                        </div>

                                    <input type="submit" 
                                           id="descargar" 
                                           onclick="validaUser();" 
                                           class="btn btn-primary btn-block" 
                                           value="Descargar" 
                                           >
                                    
                                    <input type="hidden" name="uui" value="${uui}" ></input>
                                    <input type="hidden" name="1D40c1D40c" value="${idDoc}" ></input>
                                    <input type="hidden" name="1D07IC10" value="${idOficio}" ></input>
                                    <input type="hidden" name="C4WZ4P0" value="${apCampo}" ></input>

                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
            </div>

<!--    <div class="container">
    <div class="row">
        <div class="col-md-4 col-md-offset-4 text-center">
            <div class="search-box">
            
                <div class="caption">
                    <h3>Favor de Ingresar sus datos para poder descargar el archivo</h3>
                </div>
                <form action="/ControlOficios/AbrirArchivo" class="loginForm" method="post">
                    <input type="hidden" name="uui" value="${uui}" ></input>
                    <input type="hidden" name="1D40c1D40c" value="${idDoc}" ></input>
                    <input type="hidden" name="1D07IC10" value="${idOficio}" ></input>
                    <input type="hidden" name="C4WZ4P0" value="${apCampo}" ></input>
                    <div class="input-group">
                        <input type="text" id="user" class="form-control" placeholder="Usuario" name ="user">
                        <input type="password" id="pass" class="form-control" placeholder="contraseña" name ="pass">
                        <input type="submit" id="descargar" onclick="validaUser();" class="form-control" value="Descargar" style="background-color: #326da3; color: white;">
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>-->
        </body>
    </html>
