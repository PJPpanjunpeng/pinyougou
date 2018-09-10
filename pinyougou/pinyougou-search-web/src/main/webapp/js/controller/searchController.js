app.controller("searchController", function ($scope, searchService) {

    //搜索对象
    $scope.searchMap = {"keywords":"", "category":"","brand":"", "spec":{}, "price":"", "pageNo":1, "pageSize":20};

    $scope.search = function () {
        searchService.search($scope.searchMap).success(function (response) {
            $scope.resultMap = response;

            //构建分页导航条
            buildPageInfo();
        });

    };

    //添加过滤条件
    $scope.addSearchItem = function (key, value) {
        if ("category" == key || "brand" == key || "price" == key) {
            //如果点击的是品牌或者分类的话
            $scope.searchMap[key] = value;
        } else {
            //规格
            $scope.searchMap.spec[key] = value;
        }
        //点击过滤条件后需要重新搜索
        $scope.search();
    };

    //移除过滤条件
    $scope.removeSearchItem = function (key) {
        if ("brand" == key || "category" == key || "price" == key) {
            //如果点击的是品牌或者分类的话
            $scope.searchMap[key] = "";
        } else {
            //规格
            delete $scope.searchMap.spec[key];
        }
        //点击过滤条件后需要重新搜索
        $scope.search();
    };

    //构建页面分页导航条信息
    bulidPageInfo = function () {
        //定义要在页面显示的页号的集合
        $scope.pageNoList = [];

        //要在导航条中显示的总页号个数
        var showPageCount = 5;

        //起始页号
        var showPageNo = 1;
        //结束页号
        var endPageNo = $scope.resultMap.totalPages;

        //如果总页数大于要显示的页数才有需要处理显示页号数；否则直接显示所有页号
        if ($scope.resultMap.totalPages > showPageCount) {

            //计算当前页左右间隔页数
            var interval = Math.floor((showPageCount / 2));

            //根据间隔得出起始、结束页号
            startPageNo = parseInt($scope.searchMap.pageNo) - interval;
            endPageNo = parseInt($scope.searchMap.pageNo) + interval;

            //处理页号越界
            if (startPageNo > 0) {
                //起始页号是正确的，但是结束页号需要再次判断
                if (endPageNo > $scope.resultMap.totalPages) {
                    startPageNo = startPageNo - (endPageNo - $scope.resultMap.totalPages);
                    endPageNo = $scope.resultMap.totalPages;
                }
            } else {
                //起始页号已经出现问题（小于1）
                //endPageNo = endPageNo - (startPageNo - 1);
                endPageNo = showPageCount;
                startPageNo = 1;
            }
        }

        //导航条中的前面3个点
        $scope.frontDot = false;
        if (startPageNo > 1) {
            $scope.frontDot = true;
        }

        //导航条中的后面3个点
        $scope.backDot = false;
        if (endPageNo < $scope.resultMap.totalPages) {
            $scope.backDot = true;
        }

        //设置要显示的页号
        for (var i = startPageNo; i <= endPageNo; i++) {
            $scope.pageNoList.push(i);
        }
    };

    //判断是否为当前页
    $scope.isCurrentPage = function (pageNo) {
        return $scope.searchMap.pageNo == pageNo;
        /*var tmp = parseInt($scope.searchMap.pageNo);
        return tmp == pageNo;*/
    };

    //根据页号查询
    $scope.queryByPageNo = function (pageNo) {
        if (0 < pageNo && pageNo <= $scope.resultMap.totalPages) {
            $scope.searchMap.pageNo = pageNo;
            $scope.search();
        }
    };
});