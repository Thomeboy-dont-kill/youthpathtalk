# 接口文档

## 基础信息

| 项目 | 说明 |
| :--- | :--- |
| **网关地址** | `http://localhost:10010` |
| **协议** | HTTP/1.1 |
| **字符编码** | UTF-8 |
| **内容类型** | `application/json` |

> 所有接口的请求 URL 均以网关地址为前缀，例如完整请求路径为 `http://localhost:10010/xxx`。

## 通用错误码表
|错误码|错误信息|
|:---:|:---|
|10000|出错啦，后台小哥正在努力修复中...|
|10001|参数错误|
>注意：
上述错误码为全局通用错误码。各个模块有自己的错误码表，其中错误码的前缀用于标识所属模块，如果一个模块的请求返回的错误码的前缀标识的是另一个模块，说明在RPC时，下游服务出现业务异常。

错误码前缀 AUTH- 表示认证模块自身错误，USER- 表示调用用户模块时透传的错误。
## 认证模块

## 错误码表
|错误码|错误信息|
|:---:|:---|
|AUTH-20001|验证码错误或已过期|
|AUTH-20002|验证码发送频繁|
|AUTH-20003|验证码发送类型不合法|
|AUTH-20004|手机号已注册|
|AUTH-20005|手机号未注册|

### 发送验证码到指定手机号 `POST /code/send`

**功能说明**
用户注册或短信登录发送验证码用于认证，验证码过期时间五分钟，限制发送验证码动作间隔一分钟。

---

#### 请求参数
|参数名|类型|必填|描述|可选值|
|:---|:---:|:---:|:---|:---|
|phone|string|是|手机号，11位数字|-|
|type|string|是|验证码类型|**LOGIN**:登录验证码<br/>**REGISTER**:注册验证码|

#### 请求示例

```json
{
    "phone": "18523307475",
    "type": "REGISTER"
}
```

#### 响应示例（成功）

```json
{
    "isSuccess": true,
    "data": null,
    "errorCode": null,
    "errorMessage": null
}
```

#### 响应示例（失败）

```json
{
    "isSuccess": false,
    "data": null,
    "errorCode": "AUTH-20002",
    "errorMessage": "验证码发送频繁"
}
```

### 注册用户并获取token `POST /auth/register`

**功能说明**
用户注册成功即登录，并返回token

---

#### 请求参数
|参数名|类型|必填|描述|可选值|
|:---|:---:|:---:|:---|:---|
|username|string|是|用户名，长度4-20位，支持中文、英文、数字、下划线和短横线|-|
|password|string|是|密码，8-20位，必须包含大小写字母、数字和特殊字符(@$!%*?&)|-|
|phone|string|是|手机号，11位数字|-|
|userType|string|是|用户类型|**GRAD**:0-考研党<br/>**CIVIL**:1-考公党<br/>**WORK**:2-工作党<br/>**OTHERS**:3-其他|
|verifyCode|string|是|用户输入的验证码|-|

#### 请求示例

```json
{
    "username": "Julien",
    "password": "Julien123!",
    "phone": "18888888888",
    "userType": "WORK",
    "verifyCode": "264514"
}
```

#### 响应示例（成功）

```json
{
    "isSuccess": true,
    "data": "ba94ecc5-6f65-437f-be5d-053d6e8db9de",
    "errorCode": null,
    "errorMessage": null
}
```

#### 响应示例（失败）

```json
{
    "isSuccess": false,
    "data": null,
    "errorCode": "AUTH-20001",
    "errorMessage": "验证码错误或已过期"
}
```

### 密码登录并获取token `POST /auth/login`

**功能说明**
用户通过账号密码登录，并返回token

---

#### 请求参数
|参数名|类型|必填|描述|可选值|
|:---|:---:|:---:|:---|:---|
|username|string|是|用户名，长度4-20位，支持中文、英文、数字、下划线和短横线|-|
|password|string|是|密码，8-20位，必须包含大小写字母、数字和特殊字符(@$!%*?&)|-|

#### 请求示例

```json
{
    "username": "Julien",
    "password": "Julien123!"
}
```

#### 响应示例（成功）

```json
{
    "isSuccess": true,
    "data": "77365244-cbb6-464b-8e05-0bca8e497745",
    "errorCode": null,
    "errorMessage": null
}
```

#### 响应示例（失败）

```json
{
    "isSuccess": false,
    "data": null,
    "errorCode": "USER-20005",
    "errorMessage": "用户名或密码错误"
}
```

### 短信登录并获取token `POST /auth/login/message`

**功能说明**
用户通过短信登录，并返回token

---

#### 请求参数
|参数名|类型|必填|描述|可选值|
|:---|:---:|:---:|:---|:---|
|phone|string|是|手机号，11位数字|-|
|verifyCode|string|是|用户输入的验证码|-|

#### 请求示例

```json
{
    "phone": "18888888888",
    "verifyCode":"387548"
}
```

#### 响应示例（成功）

```json
{
    "isSuccess": true,
    "data": "1a54b43b-8920-4b1d-9ff3-ee44aa443d40",
    "errorCode": null,
    "errorMessage": null
}
```

#### 响应示例（失败）

```json
{
    "isSuccess": false,
    "data": null,
    "errorCode": "AUTH-20001",
    "errorMessage": "验证码错误或已过期"
}
```

### 登出 `GET /auth/logout`

**功能说明**
用户退出登录

---

#### 请求参数
|参数名|类型|必填|描述|可选值|
|:---|:---:|:---:|:---|:---|
|-|-|-|-|-|

#### 响应示例（成功）

```json
{
    "isSuccess": true,
    "data": null,
    "errorCode": null,
    "errorMessage": null
}
```

#### 响应示例（失败）

```json
{
    "isSuccess": false,
    "data": null,
    "errorCode": "10000",
    "errorMessage": "出错啦，后台小哥正在努力修复中..."
}
```

## 用户模块

## 错误码表
|错误码|错误信息|
|:---:|:---|
|USER-20001|用户名已注册|
|USER-20002|手机号已注册|
|USER-20003|账号异常|
|USER-20004|用户角色重复创建|
|USER-20005|用户名或密码错误|
|USER-20006|用户未登录|
|USER-20007|无权限|

### 查看最近浏览记录 `GET /user/browse/history/recent`

**功能说明**
用户登录之后可查看自己最近的浏览记录（不超过50条，超过五十条，最早的浏览记录被淘汰）。

---

#### 请求参数
|参数名|类型|必填|描述|可选值|
|:---|:---:|:---:|:---|:---|

#### 响应示例（成功）

```json
{
    "isSuccess": true,
    "data": [
        {
            "id": 5,
            "userId": 1,
            "username": "Julien",
            "userAvatar": "/default-avatar.png",
            "universityName": null,
            "boardType": 2,
            "boardTypeName": "工作",
            "title": "大三怎么找实习",
            "preview": "找不到实习了，寄",
            "viewCount": 5,
            "likeCount": 0,
            "commentCount": 0,
            "favoriteCount": 0,
            "isTop": 0,
            "isEssence": 0,
            "createTime": "2026-04-06T22:56:00",
            "browseTime": "2026-04-06T16:36:19.691"
        }
    ],
    "errorCode": null,
    "errorMessage": null
}
```

#### 响应示例（失败）

```json
{
    "isSuccess": false,
    "data": null,
    "errorCode": "10000",
    "errorMessage": "出错啦，后台小哥正在努力修复中..."
}
```

### 查看点赞记录 `GET /user/like/history`

**功能说明**
用户登录之后可查看自己点赞帖子的记录（分页显示，默认页码为1，页大小为20，限制页码不小于1，页在1-50之间）。

---

#### 请求参数
|参数名|类型|必填|描述|可选值|
|:---|:---:|:---:|:---|:---|
|pageNo|Integer|否|页码，默认1，限制不小于1|-|
|pageSize|Integer|否|页大小，默认20，限制在1-50之间|-|

#### 响应示例（成功）

```json
{
    "isSuccess": true,
    "data": {
        "total": 1,
        "pageNo": 1,
        "pageSize": 20,
        "records": [
            {
                "id": 5,
                "userId": 1,
                "username": "Julien",
                "userAvatar": "/default-avatar.png",
                "universityName": null,
                "boardType": 2,
                "boardTypeName": "工作",
                "title": "大三怎么找实习",
                "preview": "找不到实习了，寄",
                "viewCount": 5,
                "likeCount": 1,
                "commentCount": 0,
                "favoriteCount": 0,
                "isTop": 0,
                "isEssence": 0,
                "createTime": "2026-04-06T22:56:00"
            }
        ]
    },
    "errorCode": null,
    "errorMessage": null
}
```

#### 响应示例（失败）

```json
{
    "isSuccess": false,
    "data": null,
    "errorCode": "USER-20006",
    "errorMessage": "用户未登录"
}
```

## 帖子模块

## 错误码表
|错误码|错误信息|
|:---:|:---|
|POST-20001|用户未登录|
|POST-20002|无权限|
|POST-20003|用户没有持有者权限|
|POST-20004|帖子不存在或已删除|
|POST-20005|帖子不存在或状态异常|
|POST-20006|点赞失败，请稍后重试|


### 发帖 `POST /post/publish`

**功能说明**
用户发帖子

---

#### 请求参数
|参数名|类型|必填|描述|可选值|
|:---|:---:|:---:|:---|:---|
|boardType|string|是|模块类型|**GRAD**:0-考研<br/>**CIVIL**:1-考公<br/>**WORK**:2-工作|
|title|string|是|标题长度不能超过100个字符|-|
|content|string|是|内容|-|

#### 请求示例

```json
{
    "boardType":"WORK",
    "title":"大三如何找实习",
    "content":"找不到实习了，寄"
}
```

#### 响应示例（成功）

```json
{
    "isSuccess": true,
    "data": null,
    "errorCode": null,
    "errorMessage": null
}
```

#### 响应示例（失败）

```json
{
    "isSuccess": false,
    "data": null,
    "errorCode": "10000",
    "errorMessage": "出错啦，后台小哥正在努力修复中..."
}
```

### 分页查询 `POST /post/list`

**功能说明**
无限滚动加载帖子列表（支持游标分页）：首次加载什么都不要传，当用户滑动到底部时自动加载下一页的内容，从此每次请求必须携带参数lastIsTop，lastIsEssence，lastCreateTime，lastId且非空，size可以自定义但是10,20,30或50，如果不指定则默认10。每条帖子只展示部分内容，可点击查看详情。

---

#### 请求参数
|参数名|类型|必填|描述|可选值|
|:---|:---:|:---:|:---|:---|
|lastIsTop|string|否|前一个帖子是否为置顶贴|**FALSE**:0-否<br/>**TRUE**:1-是|
|lastIsEssence|string|否|前一个帖子是否为精华贴|**FALSE**:0-否<br/>**TRUE**:1-是|
|lastCreateTime|LocalDateTime|否|前一个帖子的创建时间|-|
|lastId|Long|否|前一个帖子的id|-|
|size|Integer|否|分页大小，默认10|10,20,30,50|

#### 响应示例（首次加载）

```json
{
    "isSuccess": true,
    "data": {
        "list": [
            {
                "id": 2001,
                "userId": 2001,
                "username": "Thome",
                "userAvatar": "/default-avatar.png",
                "universityName": null,
                "boardType": 2,
                "boardTypeName": "工作",
                "title": "惨遭hr刷kpi",
                "preview": "本来就已经走投无路了，还拿我刷kpi，很火大",
                "viewCount": 4,
                "likeCount": 1,
                "commentCount": 0,
                "favoriteCount": 0,
                "isTop": 0,
                "isEssence": 0,
                "createTime": "2026-04-24T10:30:30"
            }
        ],
        "hasNext": false
    },
    "errorCode": null,
    "errorMessage": null
}
```

#### 响应示例（失败）

```json
{
    "isSuccess": false,
    "data": null,
    "errorCode": "10000",
    "errorMessage": "出错啦，后台小哥正在努力修复中..."
}
```

### 查询帖子详情 `GET /post/{id}`

**功能说明**
查看帖子详情。

---

#### 请求参数
|参数名|类型|必填|描述|可选值|
|:---|:---:|:---:|:---|:---|
|id|Long|否|要查询的帖子ID|-|

#### 响应示例

```json
{
    "isSuccess": true,
    "data": {
        "userId": 1,
        "username": "Julien",
        "userAvatar": "/default-avatar.png",
        "universityName": null,
        "boardType": 2,
        "boardTypeName": "工作",
        "title": "大三如何找实习",
        "viewCount": 0,
        "likeCount": 0,
        "commentCount": 0,
        "favoriteCount": 0,
        "createTime": "2026-04-06T22:56:00",
        "updateTime": "2026-03-21T15:41:24",
        "content": "找不到实习了，寄"
    },
    "errorCode": null,
    "errorMessage": null
}
```

#### 响应示例（失败）

```json
{
    "isSuccess": false,
    "data": null,
    "errorCode": "10000",
    "errorMessage": "出错啦，后台小哥正在努力修复中..."
}
```

### 更新帖子 `POST /post/update`

**功能说明**
更新帖子。

---

#### 请求参数
|参数名|类型|必填|描述|可选值|
|:---|:---:|:---:|:---|:---|
|id|Long|是|要更新的帖子ID|-|
|boardType|string|是|模块类型|**GRAD**:0-考研<br/>**CIVIL**:1-考公<br/>**WORK**:2-工作|
|title|string|是|标题长度不能超过100个字符|-|
|content|string|是|内容|-|

#### 请求示例

```json
{
    "id":8,
    "boardType":"WORK",
    "title":"大三怎么找实习",
    "content":"找不到实习了，寄"
}
```

#### 响应示例（成功）

```json
{
    "isSuccess": true,
    "data": null,
    "errorCode": null,
    "errorMessage": null
}
```

#### 响应示例（失败）

```json
{
    "isSuccess": false,
    "data": null,
    "errorCode": "POST-20001",
    "errorMessage": "用户未登录"
}
```

### 删除帖子 `DELETE /post/{id}`

**功能说明**
删除帖子。

---

#### 请求参数
|参数名|类型|必填|描述|可选值|
|:---|:---:|:---:|:---|:---|
|id|Long|是|要删除的帖子ID|-|

#### 响应示例（成功）

```json
{
    "isSuccess": true,
    "data": null,
    "errorCode": null,
    "errorMessage": null
}
```

#### 响应示例（失败）

```json
{
    "isSuccess": false,
    "data": null,
    "errorCode": "POST-20003",
    "errorMessage": "帖子不存在或已删除"
}
```

### 点赞帖子 `POST /post/{id}/like`

**功能说明**
点赞帖子。

---

#### 请求参数
|参数名|类型|必填|描述|可选值|
|:---|:---:|:---:|:---|:---|

#### 响应示例（成功）

```json
{
    "isSuccess": true,
    "data": {
        "liked": false,
        "likeCount": 0
    },
    "errorCode": null,
    "errorMessage": null
}
```

#### 响应示例（失败）

```json
{
    "isSuccess": false,
    "data": null,
    "errorCode": "POST-20004",
    "errorMessage": "帖子不存在或状态异常"
}
```

### 查看热榜 `GET /post/hot/board`

**功能说明**
查看帖子热榜，显示按热度排序，显示标题，点击可查看详情，默认显示10条，点击“更多”查看20条。

---

#### 请求参数
|参数名|类型|必填|描述|可选值|
|:---|:---:|:---:|:---|:---|
|limit|Integer|否|显示热榜条数，1-20之间，默认10|:---|
```

#### 响应示例（成功）

{
    "isSuccess": true,
    "data": [
        {
            "id": 5,
            "title": "大三怎么找实习"
        }
    ],
    "errorCode": null,
    "errorMessage": null
}

#### 响应示例（失败）

```json
{
    "isSuccess": false,
    "data": null,
    "errorCode": "10000",
    "errorMessage": "出错啦，后台小哥正在努力修复中..."
}
```

### 查看创作者周榜 `GET /user/creator/weekly/rank`

**功能说明**
查看创作者周榜，显示按热度排序，显示用户名，用户头像，大学名称，用户类型，热度，点击可查看详情，默认显示10条，可查看1-100条。

---

#### 请求参数
|参数名|类型|必填|描述|可选值|
|:---|:---:|:---:|:---|:---|
|limit|Integer|否|显示周榜条数，1-100之间，默认10|:---|

#### 响应示例（成功）

{
    "isSuccess": true,
    "data": [
        {
            "userId": 1,
            "username": "Julien",
            "userAvatar": "/default-avatar.png",
            "universityName": null,
            "type": "工作党",
            "score": 0.0
        }
    ],
    "errorCode": null,
    "errorMessage": null
}

#### 响应示例（失败）

```json
{
    "isSuccess": false,
    "data": null,
    "errorCode": "10000",
    "errorMessage": "出错啦，后台小哥正在努力修复中..."
}
```

## 搜索模块

## 错误码表
|错误码|错误信息|
|:---:|:---|

### 搜索帖子 `POST /search/post`

**功能说明**
用户通过关键词或者标签搜索帖子

---

#### 请求参数
|参数名|类型|必填|描述|可选值|
|:---|:---:|:---:|:---|:---|
|keyword|string|是|用户搜索关键词|-|
|boardType|string|否|标签：板块类型|**GRAD**:0-考研<br/>**CIVIL**:1-考公<br/>**WORK**:2-工作|
|userId|long|否|标签：作者|-|
|searchAfter|array|否|游标分页的上一次查询返回的searchAfter值|-|
|size|Integer|否|分页大小：默认10|10,20,30,50|

#### 请求示例

```json
{
  "keyword": "实习",
  "boardType": "WORK",
  "userId": 1,
  "size": 10
}
```

#### 响应示例（成功）

```json
{
    "isSuccess": true,
    "data": {
        "list": [
            {
                "id": 5,
                "userId": 1,
                "username": "Julien",
                "userAvatar": "/default-avatar.png",
                "universityName": null,
                "boardType": 2,
                "boardTypeName": "工作",
                "title": "大三怎么找<em>实习</em>",
                "preview": "找不到<em>实习</em>了，寄",
                "viewCount": 22,
                "likeCount": 1,
                "commentCount": 0,
                "favoriteCount": 0,
                "isTop": 0,
                "isEssence": 0,
                "createTime": "2026-04-06T22:56:00"
            }
        ],
        "searchAfter": [
            1.3785722,
            1775487360000,
            5
        ]
    },
    "errorCode": null,
    "errorMessage": null
}
```

#### 响应示例（失败）

```json
{
    "isSuccess": false,
    "data": null,
    "errorCode": "10000",
    "errorMessage": "出错啦，后台小哥正在努力修复中..."
}
```

### 自动补全 `GET /search/post/suggest`

**功能说明**
用户输入关键词自动补全

---

#### 请求参数
|参数名|类型|必填|描述|可选值|
|:---|:---:|:---:|:---|:---|
|keyword|string|是|用户搜索关键词|-|

#### 响应示例（成功）

```json
{
    "isSuccess": true,
    "data": [
        {
            "id": 5,
            "title": "大三怎么找实习"
        }
    ],
    "errorCode": null,
    "errorMessage": null
}
```

#### 响应示例（失败）

```json
{
    "isSuccess": false,
    "data": null,
    "errorCode": "10000",
    "errorMessage": "出错啦，后台小哥正在努力修复中..."
}
```