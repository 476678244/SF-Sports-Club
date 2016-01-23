(function(){
  'use strict';
  var sfSport = window.sfSport;
  sfSport.controller('MyInfoController', function (
    $scope,
    $routeParams,
    $route,
    $location,
    $window,
    $rootScope,
    UserInfo,
    ActivityManager,
    Upload
  ) {
    UserInfo.checkLogin();

    ActivityManager.getUser({username : UserInfo.getUser().username}).then(function(user) {
      UserInfo.setUser(user);
      $scope.email = UserInfo.getUser().username;
      $scope.avatar = UserInfo.getUser().avatar;
      $scope.fullname = UserInfo.getUser().fullname;
    });

    ActivityManager.getJoiningTypes({username : UserInfo.getUser().username}).then(function(joiningTypes) {
      $rootScope.joiningTypes = _.map(joiningTypes, function(sportName){
        return { name : sportName, id : sportName.replace(/\s/g, '') };
      });
    });

    $scope.subscribeTypeSet = {};
    $scope.uploadMessage = "";

    for (var type in UserInfo.getUser().subscribedTypes) {
      $scope.subscribeTypeSet[UserInfo.getUser().subscribedTypes[type]] = true;  
    }

    $scope.clickType = function (id) {
      if ($scope.viewing) {
        $location.path('/join/' + id);
      }
      if ($scope.subscribeTypeSet[id]) {
        delete $scope.subscribeTypeSet[id];
      }
      else {
        $scope.subscribeTypeSet[id] = true;
      }
    };

    $scope.updateUser = function () {
      var type , types = "";
      for (type in $scope.subscribeTypeSet) {
        types += (type + '/');
      }
      ActivityManager.updateUser(
        $scope.email,
        $scope.fullname,
        types
      ).then(function(resp){
        var user = resp;
        if (user) {
          UserInfo.setUser(user);
        }
        $location.path('/join');
      });
    };

    $scope.$watch('files', function () {
      $scope.upload($scope.files);
    });

    $scope.upload = function (files) {
      if (files && files.length) {
        for (var i = 0; i < files.length; i++) {
          var file = files[i];
          Upload.upload({
            url: '/teamdivider/v2/uploadHeadPicure',
            fields: {'username': $scope.email},
            file: file
          }).progress(function (evt) {
            var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
            if (progressPercentage === 100) {
              $scope.uploadMessage = "The image has been successfully uploaded," +
               " It will take a while to take effect.";
            } else {
              $scope.uploadMessage = "Uploading:" + progressPercentage + "%";
            }
            console.log('progress: ' + progressPercentage + '% ' + evt.config.file.name);
          }).success(function (data, status, headers, config) {
            if (data) {
              var user = data;
              UserInfo.setUser(user);
              //$window.location.reload(false);
              $route.reload();
            }
            console.log('file ' + config.file.name + 'uploaded. Response: ' + data);
          });
        }
      }
    }; 

    $scope.viewing = true;

    $scope.canEdit = function() {
      return !$scope.viewing;
    }

    $scope.toEdit = function() {
      $scope.viewing = false;
    }

    $scope.toView = function() {
      $scope.viewing = true;
    }
  });
})();