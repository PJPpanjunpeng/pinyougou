
//定义服务；参数1：服务名称，参数2：注入的其它服务
app.service("brandService", function ($http) {

    //获取品牌所有数据；this表示当前的service--->brandService
    this.findAll = function () {
        return $http.get("../brand/findAll.do");
    };


    this.findPage = function (page, rows) {
        return $http.get("../brand/findPage.do?page=" + page + "&rows=" + rows);
    };

    //新增数据
    this.add = function (entity) {
        return $http.post("../brand/add.do", entity);

    };

    //更新数据
    this.update = function (entity) {
        return $http.post("../brand/update.do", entity);
    };

    //根据id查询
    this.findOne = function (id) {
        return $http.get("../brand/findOne.do?id=" + id);

    };

    //删除
    this.delete = function (selectedIds) {
        return $http.get("../brand/delete.do?ids=" + selectedIds);

    };

    //根据条件分页查询数据
    this.search = function (page, rows, searchEntity) {

        //post的参数1：是一个地址；在地址中携带的参数在后台的任何映射都可以接收到
        //post的参数2：提交的表单项；只能使用postMapping接收到
        return $http.post("../brand/search.do?page=" + page + "&rows=" + rows, searchEntity);
    };

    //查询格式化的品牌数据
    this.selectOptionList = function () {
        return $http.get("../brand/selectOptionList.do");

    };

});



