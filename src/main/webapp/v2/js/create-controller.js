(function(){
  'use strict';
  var sfSport = window.sfSport;
  sfSport.controller('CreateController', function(
    $scope,
    $routeParams,
    $route,
    $location,
    UserInfo,
    ActivityManager,
    $rootScope
  ) {
    UserInfo.checkLogin();
    enhance$scopeWithSideBar($scope)

    $scope.avatar = UserInfo.getUser().avatar;
    $scope.activity = $routeParams.sport;
    $scope.title = 'Create Event';

    ActivityManager.getJoiningTypes({username : UserInfo.getUser().username}).then(function(joiningTypes) {
      $rootScope.joiningTypes = _.map(joiningTypes, function(sportName){
        return { name : sportName, id : sportName.replace(/\s/g, '') };
      });
      $(".button-collapse").sideNav()
    });

    var capitalize = function (word) {
      return word.substring(0,1).toUpperCase() + word.substring(1).toLowerCase();
    };
    var padWithZero = function (number) {
      number = parseInt(number, 10);
      if (number < 10) {
        return '0' + number; 
      }
      else {
        return number.toString();
      }
    };

    $scope.onStartTimeChange = function () {
      if ($scope.startTime) {
        var goTime = new Date($scope.startTime);
        goTime.setMinutes(goTime.getMinutes() - 30);
        $scope.goTime = goTime;
        if (!$scope.name) {
          $scope.name = capitalize($scope.activity) +
            ' Activity (' + padWithZero($scope.startTime.getMonth() + 1) + '/' +
            padWithZero($scope.startTime.getDate()) + ')';
        }
      }
    };

    $scope.createEvent = function () {
      ActivityManager.createEvent(
        $scope.activity,
        $scope.name,
        $scope.startTime,
        $scope.goTime,
        $scope.description
      ).then(function(resp){
        $location.path('/detail/' + $scope.activity + '/' + resp.ordinal);
      });
    };
  });
})();