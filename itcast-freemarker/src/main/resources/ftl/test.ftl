<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Freemarker 测试</title>
</head>
<body>

<#--这是 freemarker 注释，不会输出到文件中-->
<h1>${name}:${message}</h1>
<br>
<hr>
assign指令
<br>
<#--简单类型-->
<#assign linkman="传智"/>
联系人：${linkman}
<br>
<hr>
<#--对象-->
<#assign info={"mobile":"1212121212", "address":"guangzhou"}/>
联系电话：${info.mobile}, 联系地址:${info.address}
<br>
<hr>
<#--include-->
<#include "header.ftl"/>
<br>
<hr>
<#--if-->
<#assign bool=true/>
<#if bool>
    boold的值为true
<#else>
    bool的值为false
</#if>
<br>
<hr>
<#--list-->
<#list goodsList as goods>
    ${goods_index}, 名称为：${goods.name}; 价格为${goods.price}<br>
</#list>
<br>
<hr>
<#--获取集合总记录数-->
总共${goodsList?size}条记录。

<br>
<hr>
<#--将字符串转换为json对象-->
<#assign str="{'id':123,'text','itcat'}"/>
<#assign jsonObj=str?eval/>
id为：${jsonObj.id}; text为：${jsonObj.text}
<br>
<hr>
<#--日期格式处理-->
当前日期：${today?date}<br>
当前时间：${today?time}<br>
当前日＋时间：${today?datetime}<br>
格式化显示当前日期时间：${today?string('yyyy年MM月dd日 HH:mm:ss')}<br>
<br>
<hr>
<#--数值显示处理-->
number=${number};
number?c=${number?c}
<br>
<hr>
<#--空值处理-->
${str! "str空值的默认显示值"}
<br>
${str1!"str1空值的默认显示值"}
<br>
<#--判断变量是否存在-->
<#if str??>
    str变量存在
<#else>
    str变量不存在
</#if>
<br>
<hr>


</body>
</html>