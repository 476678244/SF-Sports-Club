/**
 * Created by wuzonghan on 17/1/23.
 */
(function () {
  'use strict';
  var sfSport = window.sfSport;
  sfSport.controller('MetricsController', function ($scope,
                                                    $routeParams,
                                                    $route,
                                                    $location,
                                                    UserInfo,
                                                    ActivityManager,
                                                    $rootScope,
                                                    $window) {
    UserInfo.checkLogin();

    // fix jump to middle page issue
    $window.scrollTo(0, 0);

    $scope.role = ' Player';

    $scope.activity = $routeParams.type;
    $scope.username = $routeParams.username;

    $scope.backToLastPage = function () {
      $window.history.back();
    }

    // login check
    ActivityManager.getUser({ username: UserInfo.getUser().username }).then(function (user) {
      UserInfo.setUser(user);
      $scope.avatar = UserInfo.getUser().avatar;
      ActivityManager.getJoiningTypes({ username: user.username }).then(function (joiningTypes) {
        $rootScope.joiningTypes = _.map(joiningTypes, function (sportName) {
          return { name: sportName, id: sportName.replace(/\s/g, '') };
        });
      });
    });

    ActivityManager.getUser({ username: $scope.username }).then(function (user) {
      $scope.avatarMetrics = user.avatar;
      $scope.fullnameMetrics = user.fullname;
    });

    ActivityManager.getMetrics({ activityType: $scope.activity, username: $scope.username }).then(function (metrics) {
      $scope.continuousTimes = metrics.continuousTimes;
      $scope.totalTimes = metrics.totalTimes;
      if (metrics.organizer) {
        $scope.role = ' Organizer';
      }
      var userScore = metrics.userScoreVO
      $scope.scores = [
        {
          "key": "attack", "score": userScore.attack
        },
        {
          "key": "defend", "score": userScore.defend
        },
        {
          "key": "skill", "score": userScore.skill
        },
        {
          "key": "speed", "score": userScore.speed
        },
        {
          "key": "stamina", "score": userScore.stamina
        },
        {
          "key": "strength", "score": userScore.strength
        }
      ]
      var a = 0
    });

    $scope.editing = false;

    $scope.startEditing = function () {
      $scope.editing = true;
      $scope.scores.forEach(function (e) {
        e.value = e.score
        e.options = {
          floor: 0,ceil: 100,step: 1,minLimit: 30,maxLimit: 99
        }
      })
    }

    $scope.finishEditing = function () {
      $scope.scores.forEach(function (e) {
        $scope.scoreMap[e.key] = e.value
      })
      ActivityManager.upsertScore($scope.username, $scope.activity, JSON.stringify($scope.scoreMap))
        .then(function (resp) {
          $scope.editing = false;
          $route.reload();
      })
    }

    // default value
    $scope.scoreMap = {"attack":77,"defend":77,"skill":77,"speed":77,"stamina":77,"strength":77}

    enableIconRotate()
  });
})();

