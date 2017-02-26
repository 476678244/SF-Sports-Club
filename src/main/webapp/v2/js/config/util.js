/**
 * Created by wuzonghan on 17/2/26.
 */
function enhance$scopeWithSideBar($scope) {
  $scope.sideNav = false;
  $scope.showSideNav = function () {
    $scope.sideNav = true;
  };
  $scope.hideSideNav = function () {
    $scope.sideNav = false;
  };
}