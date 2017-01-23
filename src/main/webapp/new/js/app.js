(function(){
  'use strict';
  var sfSport = angular.module('sfSport', ['ngRoute', 'ngQuickDate', 'ngDialog', 'ngFileUpload', '720kb.tooltips']);
  window.sfSport = sfSport;
  // Route
  sfSport.config(['$routeProvider', function($routeProvider){
    $routeProvider.when('/', {
      templateUrl : './tmpl/login.html',
      controller  : 'LoginController'
    }).when('/myInfo', {
      templateUrl : './tmpl/myInfo.html',
      controller  : 'MyInfoController'
    }).when('/join/:sport', {
      templateUrl : './tmpl/join.html',
      controller  : 'JoinController'
    }).when('/join/:sport/allEvents/:allEvents', {
      templateUrl : './tmpl/join.html',
      controller  : 'JoinController'
    }).when('/join', {
      redirectTo : '/join/soccer'
    }).when('/detail/:sport/:ordinal', {
      templateUrl : './tmpl/detail.html',
      controller  : 'DetailController'
    }).when('/create/:sport', {
      templateUrl : './tmpl/create.html',
      controller  : 'CreateController'
    }).when('/register', {
  	  templateUrl : './tmpl/register.html',
  	  controller  : 'RegisterController'
  	});
  }]);
  sfSport.config(function(ngQuickDateDefaultsProvider) {
    // Configure with icons from font-awesome
    return ngQuickDateDefaultsProvider.set({
      closeButtonHtml: "<i></i>",
      nextLinkHtml: "&gt;",
      prevLinkHtml: "&lt;"
    });
  });

  // Cross Domain, ONLY FOR DEVELOP
  sfSport.factory('httpInterceptor', function(){
    return {
      request: function(config){
        if (/teamdivider/.test(config.url)) {
          config.url = window.API_SERVER_ADDRESS + config.url;
        }
        return config;
      }
    };
  });
  sfSport.config(['$httpProvider', function($httpProvider){
    // To fix that IE have cache on AJAX request 
    $httpProvider.defaults.cache = false;
    if (!$httpProvider.defaults.headers.get) {
      $httpProvider.defaults.headers.get = {};
    }
    $httpProvider.defaults.headers.get['If-Modified-Since'] = '0';
    $httpProvider.interceptors.push('httpInterceptor');
  }]);

  // Directives
  sfSport.directive('includeReplace', function () {
      return {
          require: 'ngInclude',
          restrict: 'A',
          link: function (scope, el, attrs) {
              el.replaceWith(el.children());
          }
      };
  });
  sfSport.directive('sfWindowHeight', function($window){
    return {
      restrict: 'A', 
      link: function(scope, elements, attrs){
        var ruleName = (attrs.sfWindowHeight ? attrs.sfWindowHeight + '-' : '') +
          'height';
        $($window).on('resize', function(){
          elements.css(ruleName, $window.innerHeight + 'px');
        }).triggerHandler('resize');
      }
    };
  });
  sfSport.directive('fallbackSrc', function () {
    // http://stackoverflow.com/questions/16349578/angular-directive-for-a-fallback-image
    var fallbackSrc = {
      link: function postLink(scope, iElement, iAttrs) {
        iElement.bind('error', function() {
          angular.element(this).attr("src", iAttrs.fallbackSrc);
        });
      }
    };
    return fallbackSrc;
  });
  sfSport.directive('sidebarAvatar', function(){
    return {
      restrict: 'E',
      scope: {
        avatar: '=avatar'
      },
      template: (
        '<span class="sidebar-avatar-ctnr" tooltips tooltip-title="PROFILE" tooltip-side="right">' + 
          '<a href="#/myInfo" class="sidebar-avatar-link">' + 
            '<img class="sidebar-avatar-img"' +
              'ng-src="{{ avatar }}"' +
              'fallback-src="./img/avatar/default.svg">' +
          '</a>' +
        '</span>'
      ),
      link: function (scope, elems, attrs) {
        var avatar = elems.find('img');
        avatar.on('error', function(){
          elems.children().addClass('blink');
        });
      }
    };
  });
  sfSport.directive('sfMemberElement', function(){
    return {
      restrict: 'E',
      scope: {
        member: '=member',
        hasCar: '=car',
        type: '=type',
        ordinal: '=ordinal'
      },
      templateUrl: './tmpl/member-element.html',
      controller: function($scope, $element, $route, UserInfo, ActivityManager, sfDialog){
        var params = {
          type: $scope.type,
          ordinal: $scope.ordinal,
          driver: $scope.member.username,
          passenger: UserInfo.getUser().username
        };
        var isUserInCarParams = {
          type: $scope.type,
          ordinal: $scope.ordinal,
          username: UserInfo.getUser().username
        }
        var enabled = true;
        $scope.click = function() {
          // just support clicking for one time
          if (!enabled) {
            return ;
          } 
          // confirm at first
          var warningMsg = 'You are jumping in or out of this car.' + 
            ' Do you need to send email notification to driver and passengers?';
          sfDialog.confirm(warningMsg).then(function(confirmation){
            enabled = false;
            if (confirmation) {
              params.notification = true;
            }
            ActivityManager.isUserInCar(isUserInCarParams).then(function(inCar) {
              if (!inCar) {
                byHisCar();
              } else {
                notByHisCar();
              }
            });
          });
        };
        var byHisCar = function() {
          ActivityManager.byHisCar(params).then(function(resp) {
            if (resp.result === 'success') {
              $route.reload();
            } else {
              sfDialog.alert(resp.result);
            }
          });
        };
        var notByHisCar = function() {
          ActivityManager.notByHisCar(params).then(function(resp) {
            if (resp.result === 'success') {
              $route.reload();
            } else {
              sfDialog.alert(resp.result);
            }
          });
        }
      }
    };
  });
  sfSport.directive('sfSoccerField', function($window, $timeout){
    return {
      restrict: 'E',
      scope: {
        teams: '=teams'
      },
      templateUrl: './tmpl/soccer-field.html',
      link: function(scope, elems, attrs){
        var divide = function(arr, length, vacancyOnFront){
          var ret = [];
          _.each(arr, function(item, i){
            if (
              i === 0 ||
              (vacancyOnFront && i % length === arr.length % length) ||
              (!vacancyOnFront && i%length === 0)
            ) {
              ret.push([]);
            }
            ret[ret.length - 1].push(arr[i]);
          });
          return ret;
        };
        var WIDTH = 720
          , HEIGHT = 1150
          , WH_RATIO = WIDTH / HEIGHT
          , PADDING = [40, 15]
          , NUM_PER_ROW = 3
          , jqElem = $(elems[0])
          , jqField = jqElem.find('.soccer-field')
          , jqWin = $($window);

        jqWin.on('resize', function(){
          var width = jqField.width()
            , height = width / WH_RATIO
            , zoom = width / WIDTH
            , padding = _.map(PADDING, function(len){ return len * zoom; })
            , jqHalf = jqField.find('.half')
            ;

          jqField.css({
            height  : height + 'px',
            padding : _.map(padding, function(num){ return num + 'px'; }).join(' ')
          });

          jqHalf.css({
            width  : (width - padding[1] * 2) + 'px',
            height : (height / 2 - padding[0]) + 'px'
          });
        });

        $timeout(function(){
          jqWin.trigger('resize');
          // ugly fix: make the scroll bar show up, then calculate the demension again.
          $timeout(function(){ jqWin.trigger('resize'); }); 
        });

        scope.$watch('teams', function(teams){
          var part1, part2;
          if (teams && teams.length &&
            (teams[0].length || teams[1].length || (teams[2] && teams[2].length))
          ) {
            part1 = divide(teams[0], NUM_PER_ROW);
            part2 = divide(teams[1].reverse(), NUM_PER_ROW, true);
            scope.positions = [ part1, part2 ];
            scope.rowHeight = [
              (1 / part1.length * 100).toString() + '%',
              (1 / part2.length * 100).toString() + '%'
            ];
            scope.empty = false;
          }
          else {
            scope.empty = true;
          }
          $timeout(function(){ jqWin.trigger('resize'); }); 
        });
      }
    };
  });

  sfSport.filter('sfdate', function(dateFilter){
    return function(date, format, timezone){
      var obj = null;
      obj = new Date(date);
      var time = obj.getTime();
      if (time !== time) {
        obj = null;
      }
      if (obj) {
        return dateFilter(obj, format, timezone);
      }
      return date.toString();
    };
  });

  // Factories
  sfSport.factory('sfFileUpload', ['Upload', function(Upload){
    return {
      alert: function (msg) {
        var teamp;
      }
    };
  }]);

  // Factories
  sfSport.factory('sfDialog', ['ngDialog', function(ngDialog){
    return {
      alert: function (msg) {
        return ngDialog.openConfirm({
          template : './tmpl/dialogs/alert.html',
          data : { msg : msg }
        });
      },
      confirm: function (msg) {
        return ngDialog.openConfirm({
          template : './tmpl/dialogs/confirm.html',
          data : { msg : msg }
        });
      },
      prompt: function (msg, options) {
        return ngDialog.openConfirm({
          template : './tmpl/dialogs/prompt.html',
          data : { msg : msg , options: options },
          controller : ['$scope', function ($scope) {
            $scope.submit = function () {
              $scope.confirm($scope.input);
            };
          }]
        });
      }
    };
  }]);

  sfSport.factory('EmailSender', ['$http', '$q', function($http, $q){
    var URLS = {
      invite    : '/teamdivider/emailInvite',
      encourage : '/teamdivider/scheduleEncourageEmail',
      start_off : '/teamdivider/scheduleGoEmail'
    };
    return {
      send: function (type, activityType, eventId) {
        var me = this;
        return $q(function(resolve, reject){
          $http.get(URLS[type], {
            params : {
              activityType: activityType,
              eventId : eventId,
              token : "token"
            },
            transformResponse : [function (data) {
              return data;
            }]
          }).success(function(result){
            resolve(result);
          }).error(function(){
            reject();
          });
        });
      }
    };
  }]);

  sfSport.factory('ActivityManager', ['$http', '$q', function($http, $q){
    var cache = {};

    var generate = function (url, formatter) {
      return function (params, enableCache) {
        return $q(function(resolve, reject){
          var cacheKey = url + (params ? ':' + JSON.stringify(params) : '');
          if (enableCache && cache[cacheKey]) {
            resolve(cache[cacheKey]);
          }
          else {
            $http.get(url, { params: params }).success(function(resp){
              cache[cacheKey] = formatter(resp, params);
              resolve(cache[cacheKey]);
            }).error(function(){
              reject();
            });
          }
        });
      };
    };

    return {
      // param: enableCache
      getActivityTypes: generate('/teamdivider/activityTypes', function(resp){
        return _.map(resp, function(item){
          return item.name;
        });
      }),
      getJoiningTypes: generate('/teamdivider/activityTypes/joining', function(resp){
        return _.map(resp, function(item){
          return item.name;
        });
      }),
      getActivityType: function (params) {
        return $q(function(resolve, reject){
          $http.get('/teamdivider/activityType', { params: params }).success(function(resp){
            resolve(resp);
          }).error(function(){
            reject();
          });
        });
      },
      // param: {activity}, enableCache
      getEvents: generate('/teamdivider/activityTypes', function(resp, params){
        var activity
          , events = [];
        try {
          activity = _.find(resp, function(item){
            return item.name === params.activity;
          });
          events = activity.events;
        }
        catch (ignore) {}
        return events;
      }),
      // param: {activity, ordinal}, enableCache
      getContinousTimes: generate('/teamdivider/activityEvent/continousTimes', function(resp, params){
        var activity
          , evt = {};
        try {
          evt = resp;
        }
        catch (ignore) {}
        return evt;
      }),
      // param: {activity, ordinal}, enableCache
      getEventDetail: generate('/teamdivider/activityEvent', function(resp, params){
        var activity
          , evt = {};
        try {
          evt = resp;
        }
        catch (ignore) {}
        return evt;
      }),
      // param: {activity}, enableCache
      getLatestEvent: generate('/teamdivider/activityTypes', function(resp, params){
        var activity
          , latest = null;
        try {
          activity = _.find(resp, function(item){
            return item.name === params.activity;
          });
          if (activity.latestEvent) {
        	  latest = activity.latestEvent;
          } else {
        	  latest = {};
          }
          latest.activity = activity;
        }
        catch (ignore) {}
        return latest;
      }),
      joinEvent: function (params) {
        return $q(function(resolve, reject){
          $http.get('/teamdivider/enrollActivityEvent', { params: params }).success(function(resp){
            resolve(resp);
          }).error(function(){
            reject();
          });
        });
      },
      quitEvent: function (params) {
        return $q(function(resolve, reject){
          $http.get('/teamdivider/quitActivityEvent', { params: params }).success(function(resp){
            resolve(resp);
          }).error(function(){
            reject();
          });
        });
      },
      withCar: function (params) {
        return $q(function(resolve, reject){
          $http.get('/teamdivider/yesDrivingCar', { params: params }).success(function(resp){
            resolve(resp);
          }).error(function(){
            reject();
          });
        });
      },
      withoutCar : function (params) {
        return $q(function(resolve, reject){
          $http.get('/teamdivider/noDrivingCar', { params: params }).success(function(resp){
            resolve(resp);
          }).error(function(){
            reject();
          });
        });
      },
      group: function(id, number, user, regroup){
        return $q(function(resolve, reject){
          $http.get('/teamdivider/fendui', {
            params: {
              eventId: id,
              numberTeams: number,
              username: user,
              reFenDui: !!regroup
            }
          }).success(function(resp){
            var ret = _.map(resp, function(team){
              var addGuestsToMemberArray = function (guests, memberArray) {
                for (var i = 0 ; i < guests.length ; i++) {
                  var length = memberArray.length;
                  var guestMember = {};
                  guestMember.fullname = guests[i];
                  guestMember.avatar = "avatar/default.svg";
                  memberArray[length] = guestMember;
                  length ++;
                }
              };
              var addMembersToMemberArray = function (members, memberArray) {
                for (var i = 0 ; i < members.length ; i++) {
                  var length = memberArray.length;
                  memberArray[memberArray.length] = members[i];
                  length ++;
                }
              };
              var memberArray = [];
              addMembersToMemberArray(team.members, memberArray);
              addGuestsToMemberArray(team.guests, memberArray);
              return memberArray;
            });
            resolve(ret);
          }).error(function(){
            reject();
          });
        });
      },
      createEvent: function (activity, name, startTime, goTime, description) {
        if (startTime instanceof Date) {
          startTime = startTime.toGMTString();
        }
        if (goTime instanceof Date) {
          goTime = goTime.toGMTString();
        }
        return $q(function(resolve, reject){
          $http.post('/teamdivider/addActivityEvent', {
	          activityType: activity,
	          name: name,
	          time: startTime,
	          goTime: goTime,
	          description: description
          }).success(function(resp){
            resolve(resp);
          }).error(function(){
            reject();
          });
        });
      },
      addUser: function (email, fullname, avator, types) {
        return $q(function(resolve, reject){
          $http.post('/teamdivider/addUserWithSubscribing', {
            username: email,
            fullname: fullname,
            avator: avator,
	          types: types
          }).success(function(resp){
            resolve(resp);
          }).error(function(){
            reject();
          });
        });
      },
      addGuest: function (guestName, type, ordinal) {
        return $q(function(resolve, reject){
          $http.post('/teamdivider/addGuest', {
            guest: guestName,
            type: type,
            eventId: ordinal 
          }).success(function(){
            resolve();
          }).error(function(){
            reject();
          });
        });
      },
      removeGuest: function (guestName, type, ordinal) {
        return $q(function(resolve, reject){
          $http.post('/teamdivider/removeGuest', {
            guest: guestName,
            type: type,
            eventId: ordinal 
          }).success(function(){
            resolve();
          }).error(function(){
            reject();
          });
        });
      },
      watch: function (username, type) {
        return $q(function(resolve, reject){
          $http.post('/teamdivider/userSubscribe', {
            type: type,
            username: username
          }).success(function(ret){
            resolve(ret);
          }).error(function(){
            reject();
          });
        });
      },
      unwatch: function (username, type) {
        return $q(function(resolve, reject){
          $http.post('/teamdivider/userUnsubscribe', {
            type: type,
            username: username 
          }).success(function(ret){
            resolve(ret);
          }).error(function(){
            reject();
          });
        });
      },
      updateUser: function (username, fullname, types) {
        return $q(function(resolve, reject){
          $http.post('/teamdivider/updateUser', {
            username: username,
            fullname: fullname,
            types: types
          }).success(function(resp){
            resolve(resp);
          }).error(function(){
            reject();
          });
        });
      },
      getUser: function (params) {
        return $q(function(resolve, reject){
          $http.get('/teamdivider/user', { params: params }).success(function(resp){
            var users = resp;
            resolve(users[0]);
          }).error(function(){
            reject();
          });
        });
      },
      byHisCar: function (params) {
        return $q(function(resolve, reject){
          $http.get('/teamdivider/byHisCar', { params: params }).success(function(resp){
            resolve(resp);
          }).error(function(){
            reject();
          });
        });
      },
      notByHisCar: function (params) {
        return $q(function(resolve, reject){
          $http.get('/teamdivider/notByHisCar', { params: params }).success(function(resp){
            resolve(resp);
          }).error(function(){
            reject();
          });
        });
      },
      isUserInCar: function (params) {
        return $q(function(resolve, reject){
          $http.get('/teamdivider/isUserInCar', { params: params }).success(function(resp){
            resolve(resp);
          }).error(function(){
            reject();
          });
        });
      },
      deleteEvent: function (params) {
        return $q(function(resolve, reject){
          $http.get('/teamdivider/deleteActivityEvent', { params: params }).success(function(resp){
            resolve(resp);
          }).error(function(){
            reject();
          });
        });
      }
    };
  }]);
  sfSport.factory('UserInfo', ['$rootScope', '$location', function($rootScope, $location){
    var key = 'userInfo';
    return {
      getUser: function(){
        var user,
            userStr = localStorage.getItem(key);
        try {
          user = JSON.parse(userStr);
        }
        catch (e) {

        }
        return user;
      },
      setUser: function(newUser){
        var oldUser = this.getUser(),
            oldUserStr = JSON.stringify(oldUser),
            newUserStr = JSON.stringify(newUser);
        if (newUser) {
          if (newUserStr !== oldUserStr) {
            localStorage.setItem(key, newUserStr);
            $location.url('/myInfo');
          }
        }
        else {
          this.clearUser();
        }
      },
      setUserOnly: function(newUser) {
        var newUserStr = JSON.stringify(newUser);
        localStorage.setItem(key, newUserStr);
      },
      clearUser: function(){
        var oldUser = this.getUser();
        if (oldUser) {
          localStorage.removeItem(key);
          $location.url('/');
        }
      },
      checkLogin: function(){
        var currentUrl = $location.url(),
            user = this.getUser();
        var absUrl = $location.absUrl();
        window.location.href = absUrl.replace(/new/, "v2");
        if (user && currentUrl === '/') {
          $location.url('/join');
        }
        if (!user && currentUrl !== '/') {
          $location.url('/');
        }
      }
    };
  }]);
  sfSport.factory('ExpirableStorage', function(){
    return {
      set : function (key, value, time) {
        var data = {
          value : value
        };
        if (time) {
          data.expireTime = time + Date.now();
        }
        else {
          data.permanent = true;
        }
        localStorage.setItem(key, JSON.stringify(data));
      },
      get : function (key) {
        var dataStr = localStorage.getItem(key)
          , data;
        try {
          data = JSON.parse(dataStr);
          if (data.permanent || Date.now() < data.expireTime) {
            return data.value;
          }
          else {
            localStorage.removeItem(key);
            return null;
          }
        }
        catch (e) {
          return null;
        }
      }
    };
  });

  // Bootstrap
  sfSport.run(function($rootScope, ActivityManager, $routeParams, UserInfo){
    ActivityManager.getActivityTypes(null, true).then(function(data){
      $rootScope.sports = _.map(data, function(sportName){
        return { name : sportName, id : sportName.replace(/\s/g, '') };
      });
    });
    $rootScope.logout = function(){
      UserInfo.clearUser();
    };
  });

})();
