(function(){
  var ENV = localStorage.getItem('ENV')
    , addr;
  switch (ENV) {
    case 'luke':
      addr = 'http://10.129.115.2:8080';
      break;
    case 'zonghan':
      addr = 'http://127.0.0.1:8080';
      break;
    case 'production_as_remote':
      addr = 'http://114.115.222.201:8080';
      break;
    default:
      addr = '';
  }
  console.log('Current environment is: ' + (ENV || 'production'));
  window.API_SERVER_ADDRESS = addr;
})();
