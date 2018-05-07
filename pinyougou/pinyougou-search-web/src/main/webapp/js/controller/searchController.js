app.controller('searchController',function($scope,$location,searchService){

    //定义搜索对象
    $scope.searchMap = {'keywords':'','category':'','brand':'','spec':{},
        'price':'','pageNo':1,'pageSize':40,'sort':'','sortField':''};
    //搜索
    $scope.search=function(){
        //转换为integet类型,解决model绑定的生成数据为string类型的错误
        $scope.searchMap.pageNo = parseInt( $scope.searchMap.pageNo);
        searchService.search( $scope.searchMap ).success(
            function(response){
                $scope.resultMap=response;//搜索返回的结果
                //调用创建页码数组
                bulidPageLabel();
            }
        );
    }
    //构建页码数组
    bulidPageLabel = function () {
        //初始化数组
        $scope.pageLabel =[];

        //定义起始页
        var firstPage = 1;
        var lastPage = $scope.resultMap.totalPages;

        //设置省略号的展示
        $scope.firstDopt = true;
        $scope.lastDopt = true;

        if ($scope.resultMap.totalPages>5){
            if ($scope.searchMap.pageNo<=3){
                lastPage = 5;
                $scope.firstDopt = false;
            }else if($scope.searchMap.pageNo>=$scope.resultMap.totalPages-2){
                firstPage = $scope.resultMap.totalPages-4;
                $scope.lastDopt = false;
            }else{
                firstPage = $scope.searchMap.pageNo - 2 ;
                lastPage = $scope.searchMap.pageNo + 2 ;
            }
        }else{
            $scope.firstDopt = false;
            $scope.lastDopt = false;
        }

        //存入页码
        for (var i =firstPage; i <=lastPage; i++) {
            $scope.pageLabel.push(i);
        }
    }
    //添加条件
    $scope.addSearchItem = function (key,value) {
        //判断
        if (key=='category' || key=='brand' || key=='price'){
            $scope.searchMap[key] = value;
        }else{
            $scope.searchMap.spec[key] = value;
        }
        //实时更新
        $scope.search();
    }

    //删除条件
    $scope.removeSearchItem = function (key) {
        //判断
        if (key=='category' || key=='brand' || key=='price'){
            $scope.searchMap[key] = '';
        }else{
            //delete删除
            delete  $scope.searchMap.spec[key];
        }
        //实时更新
        $scope.search();
    }

    $scope.queryByPage = function (pageNo) {
        //添加限制条件
        if (pageNo<1 || pageNo > $scope.resultMap.totalPages){
            return;
        }
        //修改当前页
       $scope.searchMap.pageNo = pageNo;
        //实时更新
        $scope.search();
    }

    //上下页的不可点击判断
    $scope.isTopPage = function () {
        if ( $scope.searchMap.pageNo==1){
            return true;
        }else{
            return false;
        }
    }
    $scope.isEndPage = function () {
        if ( $scope.searchMap.pageNo==$scope.resultMap.totalPages){
            return true;
        }else{
            return false;
        }
    }

    //排序条件设置
    $scope.sortSearch = function (sort,sortField){
        //传递条件
        $scope.searchMap.sort = sort;
        $scope.searchMap.sortField = sortField;

        $scope.search();
    }

    //隐藏品牌列表
    $scope.keywordIsBrand = function () {
        //遍历品牌列表
        for (var i = 0; i < $scope.resultMap.brandList.length; i++) {
            //判断keyword中是否包含brand
            if($scope.searchMap.keywords.indexOf( $scope.resultMap.brandList[i].text)>=0){
                return true;
            }
        }
        return false;
    }

    //首页跳转
    $scope.loadKeywords = function () {
        //获取请求中参数
        $scope.searchMap.keywords = $location.search()['keywords'];
        $scope.search();
    }
});
