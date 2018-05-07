app.controller('contentController' ,function($scope,contentService){

    //定义一个广告数组,接收不同状态的广告数据
    $scope.contentList = [];
    //查询广告
    $scope.findByCategoryId = function (categoryId) {
        contentService.findByCategoryId(categoryId).success(
            function (response) {
                $scope.contentList[categoryId] = response;
            }
        )
    }

    $scope.search = function () {
        location.href = "http://localhost:9104/search.html#?keywords="+$scope.keywords;
    }
})