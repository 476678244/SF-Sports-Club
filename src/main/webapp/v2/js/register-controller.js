(function(){
  'use strict';
  var sfSport = window.sfSport;
  sfSport.controller('RegisterController', function (
    $scope,
    $routeParams,
    $location,
    UserInfo,
    ActivityManager
  ) {
    $scope.subscribeTypeSet = {};
    $scope.clickType = function (id) {
      if ($scope.subscribeTypeSet[id]) {
        delete $scope.subscribeTypeSet[id];
      }
      else {
        $scope.subscribeTypeSet[id] = true;
      }
    };
    $scope.createUser = function () {
      var type
        , types = "";
      for (type in $scope.subscribeTypeSet) {
        types += (type + '/');
      }
      ActivityManager.addUser(
        $scope.email,
        $scope.fullname,
        "avatar/" + $scope.email + ".jpg",
        types
      ).then(function(user){
         UserInfo.setUser(user);
        // $location.path('/');
      });
    };
  });
})();
