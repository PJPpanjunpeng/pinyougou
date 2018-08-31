
//注册处理器
//app.controller("brandController", function ($scope, $http, brandService) {
app.controller("brandController", function ($scope, $http, $controller, brandService) {

    //继承 baseController
    $controller("baseController", {$scope:$scope});

    //查询所有列表数据并绑定到 list 对象
    $scope.findAll = function () {
        brandService.findAll().success(function (response) {
            $scope.list = response;
        });
    };
    //初始化分页参数
    $scope.paginationConf = {
        currentPage:1,//当前页号
        totalItems:10,//总记录数
        itemsPerPage:10,//页大小
        perPageOptions:[10, 20, 30, 40, 50],//可选择的每页大小
        onChange: function () {//当上述的参数发生变化了后触发
            $scope.reloadList();
        }
    };
    //加载表格数据
    $scope.reloadList = function () {
        //$scope.findPage($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    };
    //分页查询
    $scope.findPage = function (page, rows) {
        $http.get("../brand/findPage.do?page=" + page + "&rows=" +
            rows).success(function (response) {
            //更新记录列表
            $scope.list = response.rows;
            //更新总记录数
            $scope.paginationConf.totalItems = response.total;
        });
    };

    //保存品牌数据
    $scope.save = function () {
        var obj;
        if ($scope.entity.id != null) {
            obj = brandService.update($scope.entity);
        } else {
            obj = brandService.add($scope.entity);
        }
        obj.success(function (response) {
            if (response.success) {
                //重新加载列表
                $scope.reloadList();
                //$scope.entity = {};
            } else {
                alert(response.message);
            }
        });
    };

    //根据主键查询
    $scope.findOne = function (id) {
        brandService.findOne(id).success(function (resposnse) {
            $scope.entity = resposnse;
        })

    };


    //定义一个放置选择了id的数组
    $scope.selectedIds = [];

    $scope.updateSelection = function ($event, id) {
        if ($event.target.checked) {
            $scope.selectedIds.push(id);
        } else {
            var index = $scope.selectedIds.indexOf(id);
            //从数组中移除，参数一删除位置，参数二删除个数
            $scope.selectedIds.splice(index, 1)
        }
    }
    //批量删除
    $scope.delete = function () {
        if ($scope.selectedIds.length < 1) {
            alert("请选择要删除的记录")
            return;
        }
        if (confirm("确定要要删除选择的记录吗？")) {
            brandService.delete($scope.selectedIds).success(function (response) {
                if (response.success) {
                    $scope.reloadList();
                    $scope.selectedIds= [];
                } else {
                    alert(response.message);
                }
            });
        }
    };

    //搜索
    //定义一个空的搜索条件对象
    $scope.searchEntity = {};

    $scope.search = function (page, rows) {
        brandService.search($scope.searchEntity, page, rows).success(function (response) {
            $scope.list = response.rows;
            $scope.paginationConf.totalItems = response.total;
        });
    };
});


//注册处理器
app.controller("brandController", function ($scope, $http, $controller, brandService) {

    //继承某个controller；参数1：要继承的controller名称;参数2：上下文
    $controller("baseController", {$scope:$scope});

    //获取品牌所有数据
    $scope.findAll = function () {
        brandService.findAll().success(function (response) {
            //将返回的数据设置到一个上下文变量
            $scope.list = response;
        }).error(function () {
            alert("获取品牌列表失败！");
        });

    };

    $scope.findPage = function (page, rows) {
        brandService.findPage(page, rows).success(function (response) {

            //修改列表为最新的返回数据列表
            $scope.list = response.rows;

            //设置总记录数
            $scope.paginationConf.totalItems = response.total;
        });

    };

    //保存品牌数据
    $scope.save = function () {

        var obj;
        if ($scope.entity.id != null) {
            //修改
            obj = brandService.update($scope.entity);
        } else {
            obj = brandService.add($scope.entity);
        }

        obj.success(function (response) {
            if(response.success){
                //刷新列表
                $scope.reloadList();
            } else {
                alert(response.message);
            }
        });

    };

    //根据id查询
    $scope.findOne = function (id) {

        brandService.findOne(id).success(function (response) {
            $scope.entity = response;
        });

    };


    //删除
    $scope.delete = function () {
        if($scope.selectedIds.length < 1){
            alert("请先选择要删除的记录");
            return;//不再执行后面的代码
        }
        //confirm 如果点击了弹出的对话框中的 确定则返回true，否则返回false
        if(confirm("确定要删除选择了的那些记录吗？")){
            brandService.delete($scope.selectedIds).success(function (response) {
                if(response.success){
                    //刷新列表
                    $scope.reloadList();
                    //清空当时选择的id
                    $scope.selectedIds = [];
                } else {
                    alert(response.message);
                }
            }).error(function () {
                alert("删除失败！");
            });
        }

    };

    //初始化查询对象;如果不初始化在后台则不认为它是一个json对象转换的时候则报错
    $scope.searchEntity = {};

    //根据条件分页查询数据
    $scope.search = function (page, rows) {

        //post的参数1：是一个地址；在地址中携带的参数在后台的任何映射都可以接收到
        //post的参数2：提交的表单项；只能使用postMapping接收到
        brandService.search(page, rows, $scope.searchEntity).success(function (response) {
            //设置列表
            $scope.list = response.rows;

            //更新总记录数
            $scope.paginationConf.totalItems = response.total;
        });
    };
});

