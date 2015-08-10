sfSport.controller('LoginController', function($scope, $http, $location, UserInfo){
  UserInfo.checkLogin();
  var registerButtonClicked = false;
  $scope.clickRegister = function() {
    registerButtonClicked = true;
    $location.url('/register');
  }
  $scope.login = function(){
	if (registerButtonClicked) {
		registerButtonClicked = false;
		return;
	}
    var msg = 'Sorry, you are not our member.\nClick "REGISTER" to join.';
    $scope.message = '';
    if ($scope.email) {
      $http.get('/teamdivider/userLogin', {
        params : {username: $scope.email}
      }).success(function(ret){
        var member = ret;
        if (member) {
          UserInfo.setUser(member);
        }
        else {
          $scope.message = msg;
        }
      }).error(function(){
        $scope.message = msg;
      });
    }
  };
});