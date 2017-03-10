/**
 * Created by wuzonghan on 17/2/26.
 */
function enhance$scopeWithSideBar($scope) {
  $(document).ready(function() {
    $(".button-collapse").sideNav()
  });
}

function enableIconRotate() {
  $(document).ready(function () {
    $(".rotate").click(function(){
      $(this).toggleClass("icon-to-opposite")
    })
  })
}
