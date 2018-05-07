app.controller('itemController' ,function($scope){	
	 
	$scope.specificationItems={};
	
    $scope.addNum = function(x){
		$scope.num += x;
		if($scope.num<1){
			$scope.num = 1 ;
		}
	}
	
	//选择规格
	$scope.selectSpecification=function(key,value){  
		$scope.specificationItems[key] = value;
		searchSku();	
	};

	//规格选中提示
	$scope.isSelected = function(key,value){
		if($scope.specificationItems[key]==value){
			return true;
		}else{
			return false;
		}
	}
	
	$scope.sku={};
	
	//加载sku集合
	$scope.loadSku = function(){
		$scope.sku = skuList[0];
		//深克隆
		$scope.specificationItems = JSON.parse(JSON.stringify($scope.sku.spec));
	}
			
	//匹配对象
	matchObject = function(map1,map2){
		for (var k in map1){
			if(map1[k]!=map2[k]){
				return false;
			}
		}
		for (var k in map2){
			if(map2[k]!=map1[k]){
				return false;
			}
		}
		
		return true;
	};
	
	//查询规格是否匹配
	searchSku = function(){
		for(var i = 0;i<skuList.length;i++){
			if(matchObject(skuList[i].spec,$scope.specificationItems)){
				$scope.sku=skuList[i];
			}
		}
	};
	
	//添加条件
	$scope.addToCart = function(){
		alert($scope.sku.id);
	}
	
	
	
});	