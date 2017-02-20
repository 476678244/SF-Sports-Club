(function(){
  'use strict';
  var sfSport = window.sfSport;
  sfSport.controller('JoinController', function (
    $scope,
    $routeParams,
    $route,
    $location,
    UserInfo,
    ActivityManager,
    $rootScope
  ) {
    UserInfo.checkLogin();

    $scope.activity = $routeParams.sport;
    $scope.showAll = $routeParams.allEvents;
    if (!$scope.showAll) {
      $scope.showAll = false;
    }
    $scope.joinClubButton = "Loading...";
    ActivityManager.getUser({username : UserInfo.getUser().username}).then(function(user) {
      UserInfo.setUser(user);
      $scope.avatar = UserInfo.getUser().avatar;
      ActivityManager.getJoiningTypes({username : user.username}).then(function(joiningTypes) {
        $rootScope.joiningTypes = _.map(joiningTypes, function(sportName){
          return { name : sportName, id : sportName.replace(/\s/g, '') };
        });
      });
    });

    ActivityManager.getActivityType({activityType: $scope.activity, allEvents: $scope.showAll}).then(function(types){
      if (types.length > 0) {
        $scope.allEvents = types[0].events;
        $scope.latest = types[0].latestEvent;
        $scope.organizers = types[0].organizers;
        $scope.subscribers = types[0].subscribers;
      }
      $scope.joinClubButton = "Join Club";
    });

    $scope.isOrganizer = function(username){
      var ret = false;
      try {
        username = username || UserInfo.getUser().username;
        ret = !!_.find($scope.organizers, function(organizer){
          return organizer.username === username;
        });
      }
      catch (ignore) {}
      return ret;
    };

    $scope.isWatcher = function(username){
      var ret = false;
      try {
        username = username || UserInfo.getUser().username;
        ret = !!_.find($scope.subscribers, function(subscriber){
          return subscriber.username === username;
        });
      }
      catch (ignore) {}
      return ret;
    };

    $scope.viewLatest = function () {
      var activity, id, username, navigateTo;
      activity   = $scope.activity;
      id         = $scope.latest.ordinal;
      username   = UserInfo.getUser().username;
      navigateTo = '/detail/' + activity + '/' + id;
      if (navigateTo) {
        $location.path(navigateTo);
      }
    };

    $scope.toCreatePage = function () {
      var activity, navigateTo;
      activity   = $scope.activity;
      navigateTo = '/create/' + activity;
      if (navigateTo) {
        $location.path(navigateTo);
      }
    };

    $scope.join = function () {
      var activity, id, username, navigateTo;
      if (!arguments.length) {
        activity   = $scope.activity;
        id         = $scope.latest.ordinal;
        username   = UserInfo.getUser().username;
        navigateTo = '/detail/' + activity + '/' + id;
      }
      else {
        activity   = arguments[0];
        id         = arguments[1];
        username   = arguments[2];
        navigateTo = arguments[3];
      }
      ActivityManager.joinEvent({
        activityType : activity,
        username : username,
        eventId : id 
      }).then(function(){
      if (navigateTo) {
        $location.path(navigateTo);
      }
      });
    };

    $scope.watch = function (yesWatch) {
      var apiCall = ActivityManager.watch;
      if (!yesWatch) {
        apiCall = ActivityManager.unwatch;
      }
      apiCall(UserInfo.getUser().username,$scope.activity).then(function(user){
        UserInfo.setUserOnly(user);
        $route.reload();
      });
    };

    $scope.changeType = function(type) {
      ActivityManager.getActivityType({activityType: type, allEvents: $scope.showAll}).then(function(types){
        if (types.length > 0) {
          $scope.allEvents = types[0].events;
          $scope.latest = types[0].latestEvent;
          $scope.organizers = types[0].organizers;
          $scope.subscribers = types[0].subscribers;
          $scope.activity = type;
        }
      });
    };

    $scope.showNote = function () {
      if ($scope.activity == "football") {
        return true
      }
      return false
    }
  });
})();
