app.controller('indexController' ,function($scope,loginService) {
    $scope.showloginName = function () {
        loginService.loginName().success(function (response) {
            $scope.loginName = response.loginName;
        })
    }
})
