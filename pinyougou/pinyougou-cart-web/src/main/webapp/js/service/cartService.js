app.service("cartService", function ($http) {
    this.getUsername = function () {
        return $http.get("cart/getUsername.do?t=" + Math.random());
    };

});