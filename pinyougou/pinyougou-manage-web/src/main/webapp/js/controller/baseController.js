app.controller("baseController", function ($scope) {
    //放置其它的controller中常用的方法

    // 初始化分页参数；只要渲染则会执行并调用onChange
    $scope.paginationConf = {
        currentPage:1,// 当前页号
        totalItems:10,// 总记录数
        itemsPerPage:10,// 页大小
        perPageOptions:[10, 20, 30, 40, 50],// 可选择的每页大小
        onChange: function () {// 当上述的参数发生变化了后触发
            $scope.reloadList();
        }
    };

    $scope.reloadList = function () {
        //$scope.findPage($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);

    };

    //选择了的id集合
    $scope.selectedIds = [];


    //点击复选框的事件
    $scope.updateSelection = function ($event, id) {
        if($event.target.checked){
            //如果选中则需要将id加入到数组
            $scope.selectedIds.push(id);
        } else {
            //如果没有选中则从数组中移除该id
            //找出id在数组里面的下标
            var index = $scope.selectedIds.indexOf(id);
            //从一个数组中删除；参数1：删除的元素的索引号，参数2：本次要删除的个数
            $scope.selectedIds.splice(index, 1);
        }
    };


});