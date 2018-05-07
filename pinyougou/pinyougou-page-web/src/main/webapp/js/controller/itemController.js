app.controller('itemController' ,function($scope){	
	 
	$scope.specificationItems={};
	
    $scope.addNum = function(x){
		$scope.num += x;
		if($scope.num<1){
			$scope.num = 1 ;
		}
	}
	
	//ѡ����
	$scope.selectSpecification=function(key,value){  
		$scope.specificationItems[key] = value;
		searchSku();	
	};

	//���ѡ����ʾ
	$scope.isSelected = function(key,value){
		if($scope.specificationItems[key]==value){
			return true;
		}else{
			return false;
		}
	}
	
	$scope.sku={};
	
	//����sku����
	$scope.loadSku = function(){
		$scope.sku = skuList[0];
		//���¡
		$scope.specificationItems = JSON.parse(JSON.stringify($scope.sku.spec));
	}
			
	//ƥ�����
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
	
	//��ѯ����Ƿ�ƥ��
	searchSku = function(){
		for(var i = 0;i<skuList.length;i++){
			if(matchObject(skuList[i].spec,$scope.specificationItems)){
				$scope.sku=skuList[i];
			}
		}
	};
	
	//�������
	$scope.addToCart = function(){
		alert($scope.sku.id);
	}
	
	
	
});	