/**
 * Created by wuzonghan on 17/1/23.
 */
(function(){
  'use strict';
  var sfSport = window.sfSport;
  sfSport.controller('MetricsController', function (
    $scope,
    $routeParams,
    $route,
    $location,
    UserInfo,
    ActivityManager,
    $rootScope
  ) {
    UserInfo.checkLogin();

    $scope.role = 'Club Member';

    $scope.activity = $routeParams.type;
    $scope.username = $routeParams.username;

    // login check
    ActivityManager.getUser({username : UserInfo.getUser().username}).then(function(user) {
      UserInfo.setUser(user);
      $scope.avatar = UserInfo.getUser().avatar;
      ActivityManager.getJoiningTypes({username : user.username}).then(function(joiningTypes) {
        $rootScope.joiningTypes = _.map(joiningTypes, function(sportName){
          return { name : sportName, id : sportName.replace(/\s/g, '') };
        });
      });
    });

    ActivityManager.getUser({username : $scope.username}).then(function(user) {
      $scope.avatarMetrics = user.avatar;
      $scope.fullnameMetrics = user.fullname;
    });

    ActivityManager.getMetrics({activityType: $scope.activity, username : $scope.username}).then(function(metrics) {
      $scope.continuousTimes = metrics.continuousTimes;
      $scope.totalTimes = metrics.totalTimes;
      if (metrics.organizer) {
        $scope.role = 'Club Organizer';
      }

    });

  });
})();

