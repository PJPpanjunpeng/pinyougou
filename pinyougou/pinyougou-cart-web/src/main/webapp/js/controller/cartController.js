app.controller("cartController", function ($scope, cartService) {

    $scope.getUsername = function () {
        cartService.getUsername().success(function (response) {
            $scope.username = response.username;
        });
    };

    //查询购物车列表
    $scope.findCartList = function () {
        cartService.findCartList().success(function (response) {
            $scope.cartList = response;
            //计算总数量和价格
            $scope.totalValue = cartService.sumTotalValue(response);
        });

    };

    //增减购物车购买商品数量
    $scope.addCartToCartList = function (itemId, num) {
        cartService.addCartToCartList(itemId, num).success(function (response) {
            if(response.success){
                //刷新列表
                $scope.findCartList();
            } else {
                alert(response.message);
            }
        });

    };
});