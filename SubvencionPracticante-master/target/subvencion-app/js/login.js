// login.js - scripts para login.jsp
document.addEventListener("DOMContentLoaded",function(){
  console.log("login.js cargado");
  var f = document.querySelector('form');
  if(f) f.addEventListener('submit', function(e){ e.preventDefault(); /* validar o enviar con fetch si quieres */ });
});
