 //控制层 
app.controller('goodsController' ,function($scope,$controller   ,goodsService,uploadService,itemCatService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.msg);
				}
			}		
		);				
	}
	//添加
	$scope.add=function(){
        //将编辑器中的值赋予entity
        $scope.entity.goodsDesc.introduction = editor.html();

        goodsService.add( $scope.entity ).success(
			function(response){
				if(response.success){
                    alert(response.msg);
					//成功清空entity
                    $scope.entity ={};
                    //清空editor
                    editor.html("");
				}else{
					alert(response.msg);
				}
			}
		);
	}

	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	$scope.uploadFile = function () {
        uploadService.uploadFile().success(
            function (response) {
                if (response.success){
                    $scope.image_entity.url = response.msg;
                    alert(image_entity.url);
                }else{
                    alert(response.msg)
                }
            }
        )
    }

    //初始化
    $scope.entity = {goodsDesc:{itemImages:[]}}

    $scope.add_image_entity = function () {
	    //添加图片
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }

    //清除图片信息
    $scope.remove_image_entity = function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index,1);
    }

    //一级列表展示
    $scope.findByParentId = function () {
        itemCatService.findByParentId(0).success(
            function (response) {
                $scope.itemCat1List = response;
            }
        )
    }

    //二级列表展示
    $scope.$watch('entity.goods.category1Id',function (newValue,oldValue){
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCat2List = response;
                $scope.itemCat3List = null;
                $scope.entity.goods.typeTemplateId = null;
            }
        )
	})
	//三级列表展示
    $scope.$watch('entity.goods.category2Id',function (newValue,oldValue){
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCat3List = response;
                $scope.entity.goods.typeTemplateId = null;
            }
        )
	})

    //模板id
    $scope.$watch('entity.goods.category3Id',function (newValue,oldValue){
        itemCatService.findOne(newValue).success(
            function (response) {
                $scope.entity.goods.typeTemplateId = response.typeId;
            }
        )
    });


});
