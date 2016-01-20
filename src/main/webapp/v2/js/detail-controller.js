(function(){
  'use strict';
  var sfSport = window.sfSport;
  sfSport.controller('DetailController', function(
    $scope, 
    $routeParams,
    $route,
    $location,
    UserInfo,
    ActivityManager,
    EmailSender,
    sfDialog,
    ExpirableStorage,
    $rootScope
  ) {
    UserInfo.checkLogin();

    $scope.avatar = UserInfo.getUser().avatar;
    $scope.timestamp = Date.now();

    ActivityManager.getJoiningTypes({username : UserInfo.getUser().username}).then(function(joiningTypes) {
      $rootScope.joiningTypes = _.map(joiningTypes, function(sportName){
        return { name : sportName, id : sportName.replace(/\s/g, '') };
      });
    });

    var isUserInGroup = function (username, group) {
      var user = UserInfo.getUser();
      username = username || (user ? user.username : '');
      var ret = false;
      try {
        ret = !!_.find(group, function (user) {
          return user.username === username;
        });
      }
      catch (ignore) {}
      return ret;
    };

    $scope.activity = $routeParams.sport;
    $scope.ordinal = $routeParams.ordinal;

    $scope.$watch('groupNumber', function(value){
      if (!$scope.supportFendui()) {
        return;
      }
      ActivityManager.group(
        $scope.ordinal,
        value,
        UserInfo.getUser().username
      ).then(function(resp){
        $scope.teams = resp;
      });
    });

    $scope.groupNumber = 2;

    $scope.group = function(num){
      $scope.groupNumber = num;
    };

    $scope.regroup = function(){
      ActivityManager.group(
        $scope.ordinal,
        $scope.groupNumber,
        UserInfo.getUser().username,
        true
      ).then(function(resp){
        $scope.teams = resp;
      });
    };

    $scope.isOrganizer = function(username){
      return isUserInGroup(username, $scope.detail.organizers);
    };

    $scope.isMember = function(username){
      return isUserInGroup(username, $scope.detail.members);
    };

    $scope.willTakeCar = function(username){
      return isUserInGroup(username, $scope.detail.drivingCarMembers);
    };

    $scope.join = function (join) {
      var activity   = $scope.detail.type
        , id         = $scope.detail.ordinal
        , username   = UserInfo.getUser().username
        , operation  = join ? ActivityManager.joinEvent : ActivityManager.quitEvent
        ;

      operation({
        activityType : activity,
        username : username,
        eventId : id 
      }).then(function(detail){
        if (detail.result != '') {
          sfDialog.alert(detail.result);
          return;
        }
        //$route.reload();
        _.each(detail.guests, function(guest){
          detail.members.push({
            fullname : guest,
            avatar   : 'avatar/default.svg',
            username : 'guest'
          });
        });
        $scope.detail = detail;
        assignContinousTimesToMembers($scope.detail);
        assignPassengersToMembers($scope.detail);
        });
    };


    $scope.takeCar = function (willTakeCar) {
      var action = willTakeCar ? ActivityManager.withCar : ActivityManager.withoutCar;
      action({
        activityType : $scope.activity,
        username : UserInfo.getUser().username,
        eventId : $scope.ordinal
      }).then(function(){
        $route.reload();
      });
    };

    $scope.sendMail = function (type) {
      var warningMsg = {
        invite    : 'Send invitation email to all club members right now.',
        encourage : 'Send "encourag to join!" email to all club members right now.',
        start_off : 'Will send "Go Now" email to joining members exactly at go time. '
      };
      sfDialog.confirm(warningMsg[type]).then(function(confirmation){
        if (confirmation) {
          EmailSender.send(type, $scope.activity, $scope.ordinal).then(
            function(result){
              sfDialog.alert(result);
            },
            function(){
              sfDialog.alert('Some error happened.');
            }
          );
        }
      });
    };

    $scope.manageGuest = function (add) {
      var msg = 'Input name to ' + (add ? 'add' : 'remove') + ' guest player';
      sfDialog.prompt(msg, { required: true }).then(function(guestName){
        var action = add ? ActivityManager.addGuest : ActivityManager.removeGuest;
          action(guestName, $scope.activity, $scope.ordinal).then(function(){
            $route.reload();
          });
      });
    };
      
    ActivityManager.getEventDetail({
      activityType : $scope.activity,
      eventId: $scope.ordinal
    }).then(function(detail){
      _.each(detail.guests, function(guest){
        detail.members.push({
          fullname : guest,
          avatar   : 'avatar/default.svg',
          username : 'guest'
        });
      });
      $scope.detail = detail;
      assignContinousTimesToMembers($scope.detail);
      assignPassengersToMembers($scope.detail);
    });

    var assignContinousTimesToMembers = function (detail) {
      if (detail.continousTimes) {
        _.each(detail.members, function(member) {
          member.continousTimes = detail.continousTimes[member.username];
        });
      }
    };

    var assignPassengersToMembers = function (detail) {
      if (detail.carPassengers) {
        // for members, assign passengers string
        _.each(detail.members, function(member) {
          // default no passengers string
          member.passengers = "";
          // check if username in detail.carPassengers
          if (detail.carPassengers[member.username]) {
            _.each(detail.carPassengers[member.username], function(fullname) {
              member.passengers += fullname + "~";
            });
          } else {
            member.passengers = "No passengers now, click car icon to join!";
          }
        });
      }
    };

    $scope.supportFendui = function () {
      if ($scope.activity == 'soccer') {
        return true;
      }
      return false;
    };

    $scope.deleteEvent = function () {
      var warningMsg = 'This will delete this activity event, is it ok?';
      var params = {
        type: $scope.activity,
        ordinal: $scope.ordinal
      };
      sfDialog.confirm(warningMsg).then(function(confirmation){
        if (confirmation) {
          ActivityManager.deleteEvent(params).then(function(resp){
            $location.path('/join/' + $scope.activity + '/allEvents/true');
          });
        }
      });
    };

  });
})();
