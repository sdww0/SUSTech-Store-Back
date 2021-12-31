# SUSTech-Store-Back


## 各个版本

| 名字   |            | 版本  | 附加说明                           |
| ------ | ---------- | ----- | ---------------------------------- |
| 数据库 | postgresql | 11.13 | 数据存在名为project数据库的store下 |
| java   |            | 11    |                                    |
|        |            |       |                                    |

其余见pom.xml

## 运行提醒：

先安装各个版本的数据库等，先配好环境

在sql下能找到createtable.sql，运行前先运行这个sql配置好数据库

**数据库说明：**

1. 用户名：postgres
2. 密码：详见application.properties
3. 数据库:project
4. schema:store

**运行说明**

1. 运行SUSTechStore即可

**配置说明**

1. application.properties中配置目标数据库等

## 查看接口信息

localhost:8081/swagger-ui.html

采用swagger

## 状态码

| 名称                     | 是多少 | 消息                 |
| ------------------------ | ------ | -------------------- |
| SUCCESS                  | 2000   | 成功                 |
| QUERY_NOT_LOGIN_USER     | 2001   | 查询用户不是登录用户 |
| QUERY_IS_LOGIN_USER      | 2002   | 查询用户是登录用户   |
| CHECK_CODE_WRONG         | 4000   | 验证码出错           |
| PARAM_NOT_VALID          | 4001   | 用户填写的参数有误   |
| ACCESS_DENIED            | 4003   | 权限不足不允许访问   |
| USER_NOT_LOGIN           | 4010   | 用户未登录           |
| USER_NOT_ACTIVATE        | 4011   | 用户未激活           |
| USER_NOT_FOUND           | 4012   | 用户不存在           |
| LOGIN_FAIL               | 4013   | 用户名或密码错误     |
| USER_BANNED              | 4014   | 用户封禁中           |
| EMAIL_EXIST              | 4020   | 注册邮箱已存在       |
| EMAIL_NOT_FOUND          | 4021   | 邮箱不存在           |
| ACTIVATE_CODE_ILLEGAL    | 4022   | 激活码不存在         |
| USER_ALREADY_ACTIVATE    | 4023   | 用户已经激活         |
| ADDRESS_NOT_EXISTS       | 4030   | 用户地址不存在       |
| GOODS_NOT_FOUND          | 4050   | 商品不存在           |
| GOODS_OFF_SHELL          | 4051   | 商品下架             |
| ADD_GOODS_FAILED         | 4060   | 添加商品失败         |
| DEAL_NOT_EXISTS          | 4070   | 订单不存在           |
| DEAL_ADD_FAIL            | 4071   | 添加订单失败         |
| STAGE_WRONG              | 4072   | 订单不能跨越阶段     |
| NOT_ENOUGH_MONEY         | 4073   | 没有足够的钱         |
| ALREADY_COMMENT          | 4074   | 已经评价             |
| CHAT_ALREADY_EXISTS      | 4090   | 聊天已存在           |
| COMPLAIN_FAIL            | 4100   | 投诉失败             |
| COMPLAIN_USER_NOT_EXISTS | 4101   | 举报用户不存在       |

其余见ResultCode