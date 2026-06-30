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

### 删除指定浏览记录 `DELETE /user/browse/history/recent/{postId}`

**功能说明**
用户登录之后可删除自己浏览记录列表中指定浏览记录。

---

#### Path 参数

|参数名|类型|必填|描述|
|:---|:---:|:---:|:---|
|postId|Long|是|被删除浏览记录的帖子的ID|

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

### 清空浏览记录 `DELETE /user/browse/history/recent`

**功能说明**
用户登录之后可清空自己的浏览记录。

---

#### 请求参数
|参数名|类型|必填|描述|可选值|
|:---|:---:|:---:|:---|:---|

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

### 查看收藏记录 `GET /user/favorite/history`

**功能说明**
用户登录之后可查看自己收藏帖子的记录（分页显示，默认页码为1，页大小为20，限制页码不小于1，页在1-50之间）。

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
                "favoriteCount": 1,
                "isTop": 0,
                "isEssence": 0,
                "createTime": "2026-04-24T10:30:30"
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

### 查看创作者周榜 `GET /user/creator/weekly/rank`

**功能说明**
查看创作者周榜，显示按热度排序，显示用户名，用户头像，大学名称，用户类型，热度，点击可查看详情，默认显示10条，可查看1-20条。

---

#### 请求参数
|参数名|类型|必填|描述|可选值|
|:---|:---:|:---:|:---|:---|
|limit|Integer|否|显示周榜条数，1-20之间，默认10|:---|

#### 响应示例（成功）

```json
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
|POST-20007|收藏失败，请稍后重试|
|POST-20008|评论不存在|
|POST-20009|用户没有持有者权限|
|POST-20010|评论编辑功能失效|
|POST-20011|点赞失败，请稍后重试|

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
                "favoriteCount": 1,
                "isTop": 0,
                "isEssence": 0,
                "createTime": "2026-04-24T10:30:30",
                "liked": true,
                "favorited": true,
                "hotComment": null
            },
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
                "viewCount": 22,
                "likeCount": 2,
                "commentCount": 7,
                "favoriteCount": 1,
                "isTop": 0,
                "isEssence": 0,
                "createTime": "2026-04-06T22:56:00",
                "liked": true,
                "favorited": true,
                "hotComment": null
            }
        ],
        "hasNext": false,
        "cursor": null
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
        "content": "找不到实习了，寄",
        "liked":false,
        "favorited":false
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

#### Path 参数

|参数名|类型|必填|描述|
|:---|:---:|:---:|:---|
|id|Long|是|点赞的帖子的ID|

#### 响应示例（成功）

```json
{
    "isSuccess": true,
    "data": {
        "interacted": false,
        "count": 0
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

### 收藏帖子 `POST /post/{id}/favorite`

**功能说明**
收藏帖子。

---

#### Path 参数

|参数名|类型|必填|描述|
|:---|:---:|:---:|:---|
|id|Long|是|收藏的帖子的ID|

#### 响应示例（成功）

```json
{
    "isSuccess": true,
    "data": {
        "interacted": true,
        "count": 1
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

### 评论帖子 `POST /comment/create`

**功能说明**
用户对指定帖子发表一级评论（根评论）。

---

#### 请求参数
|参数名|类型|必填|描述|可选值|
|:---|:---:|:---:|:---|:---|
|postId|Long|是|帖子ID|:---|
|content|String|是|评论内容，最大长度500字符|:---|

#### 请求示例

```json
{
  "postId": 5,
  "content": {
    "content": [
      {
        "type": "mention",
        "attrs": {
          "userId": 2001,
          "username": "Thome"
        }
      },
      {
        "type": "text",
        "text": "你现在找到实习没"
      }
    ]
  }
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

### 点赞评论 `POST /comment/{id}/like`

**功能说明**
用户点赞评论。

---

#### Path 参数

|参数名|类型|必填|描述|
|:---|:---:|:---:|:---|
|id|Long|是|评论的id|

#### 响应示例（成功）

```json
{
    "isSuccess": true,
    "data": {
        "interacted": true,
        "count": 1
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

### 回复评论 `POST /comment/reply`

**功能说明**
用户回复评论。

---

#### 请求参数
|参数名|类型|必填|描述|可选值|
|:---|:---:|:---:|:---|:---|
|parentId|Long|是|被回复的评论的ID|:---|
|content|String|是|回复内容，最大长度500字符|:---|

#### 请求示例

```json
{
    "parentId":1,
    "content": {
        "type":"doc",
        "content": [
            {
                "type": "text",
                "text": "完了"
            }
        ]
    }
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

### 编辑评论 `POST /comment/update`

**功能说明**
用户编辑自己发布的评论，且只能在评论发表后 5 分钟之内 进行编辑。

---

#### 请求参数
|参数名|类型|必填|描述|可选值|
|:---|:---:|:---:|:---|:---|
|commentId|Long|是|要编辑的评论ID|-|
|content|RichTextDoc|是|富文本内容对象|-|
|content.type|String|是|文档类型，固定值|"doc"|
|content.content|Array[RichTextNode]|是|富文本节点数组|-|
|content.content[].type|String|是|节点类型|"text" / "mention"|
|content.content[].text|String|条件必填|文本内容（当 type = text 时必填）|-|
|content.content[].attrs|MentionAttrs|条件必填|提及属性（当 type = mention 时必填）|-|
|content.content[].attrs.userId|Long|条件必填|被提及用户ID（当 type = mention 时必填）|-|
|content.content[].attrs.username|String|条件必填|被提及用户名（当 type = mention 时必填）|-|

#### 请求示例

```json
{
  "commentId": 2001,
  "content": {
        "type":"doc",
        "content": [
            {
                "type": "text",
                "text": "完了"
            }
        ]
    }
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
    "errorCode": "POST-20010",
    "errorMessage": "评论编辑功能失效"
}
```

### 查看帖子评论列表 `POST /comment/list`

**功能说明**
根据帖子ID分页查询该帖下的所有一级评论（根评论），按热度（hot_score）降序排列。

---

#### 请求参数
|参数名|类型|必填|描述|可选值|
|:---|:---:|:---:|:---|:---|
|postId|Long|是|帖子ID|-|
|cursor|CommentCursor|否|游标对象（首页不传）|-|
|cursor.hotScore|BigDecimal|条件必填|上一页最后一条评论的热度分（首页不传）|-|
|cursor.id|Long|条件必填|上一页最后一条评论的ID（首页不传）|-|
|pageSize|PageSizeEnum|是|每页条数（默认10）|SIZE_10, SIZE_20, SIZE_30, SIZE_50|


#### 请求示例

```json
{
    "postId":5
}
```

#### 响应示例（成功）

```json
{
    "isSuccess": true,
    "data": {
        "list": [
            {
                "id": 1,
                "userId": 2001,
                "userName": "Thome",
                "userAvatar": "/default-avatar.png",
                "universityId": null,
                "universityName": null,
                "content": "{\"type\":\"doc\",\"content\": [{\"type\": \"text\",\"text\": \"不知道\"}]}",
                "likeCount": 1,
                "replyCount": 1,
                "createTime": "2026-06-16T21:29:37",
                "hotScore": 0.0174
            },
            {
                "id": 4002,
                "userId": 1,
                "userName": "Julien",
                "userAvatar": "/default-avatar.png",
                "universityId": null,
                "universityName": null,
                "content": "{\"type\":\"doc\",\"content\":[{\"type\":\"mention\",\"attrs\":{\"userId\":2001,\"username\":\"Thome\"}},{\"type\":\"text\",\"text\":\"你现在找到实习没\"}]}",
                "likeCount": 0,
                "replyCount": 0,
                "createTime": "2026-06-24T11:08:28",
                "hotScore": 0.0000
            }
        ],
        "hasNext": false,
        "cursor": null
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

### 查看评论回复列表 `POST /comment/reply/list`

**功能说明**
根据根评论ID分页查询该根评论下的所有回复（包括二级回复、三级回复及更深层级的回复），按创建时间（create_time）降序排列，最新回复在前。
- 回复层级：接口返回的是该根评论下的所有层级的回复（即 root_id = 根评论ID 的所有评论），而不仅仅是直接回复根评论的二级评论。
- 回复目标标识：每条回复包含 parentId（直接父评论ID）和 replyUserId/replyUserName（被回复的用户信息），用于前端展示“回复 @用户名”的交互。
- showReplyUser 字段：当 parentId 与 rootId 相同（即直接回复根评论）时，showReplyUser 为 false，表示不需要显示“回复 @根评论作者”；否则为 true，表示该回复是针对其他子评论的，前端需通过 replyUserName 展示“回复 @XXX”。
- 分页方式：基于游标（createTime + id）分页，避免深分页性能问题。首次请求不传 cursor，翻页时使用上一页返回的 cursor 值。
- 对status为0表示软删除状态的帖子显示content需要替换成“[评论已删除]”

---

#### 请求参数
|参数名|类型|必填|描述|可选值|
|:---|:---:|:---:|:---|:---|
|rootId|Long|是|根评论ID|-|
|cursor|CreateTimeIdCursor|否|游标对象（首页不传）|-|
|cursor.createTime|LocalDateTime|条件必填|上一页最后一条评论的创建时间（首页不传）|-|
|cursor.id|Long|条件必填|上一页最后一条评论的ID（首页不传）|-|
|pageSize|PageSizeEnum|是|每页条数（默认10）|SIZE_10, SIZE_20, SIZE_30, SIZE_50|


#### 请求示例

```json
{
    "rootId":1
}
```

#### 响应示例（成功）

```json
{
    "isSuccess": true,
    "data": {
        "list": [
            {
                "id": 2001,
                "userId": 1,
                "userName": "Julien",
                "userAvatar": "/default-avatar.png",
                "universityId": null,
                "universityName": null,
                "parentId": 1,
                "replyUserId": 2001,
                "replyUserName": "Thome",
                "showReplyUser": false,
                "content": "{\"type\":\"doc\",\"content\":[{\"type\":\"text\",\"text\":\"完了\"}]}",
                "likeCount": 0,
                "status": 1,
                "createTime": "2026-06-24T10:54:18"
            }
        ],
        "hasNext": false,
        "cursor": null
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

### 查看对话 `GET /comment/conversation/{startId}`

**功能说明**
根据起始评论ID，递归查询该评论及其所有后代回复（包括二级、三级及更深层级的回复），按创建时间（create_time）升序排列，返回完整的对话树列表。
适用于“查看对话”场景（禁止用于在根评论和二级评论（回复根评论的评论））。
- 返回结构：列表按时间升序排列，第一条记录即 startId 对应的评论本身。
- 字段说明：showReplyUser：布尔值，true 表示需要显示“回复 @用户名”（即该评论不是直接回复根评论，而是回复了其他子评论），false 表示无需显示（直接回复根评论的情况）。比如，如果是查看三级评论的对话，则startId为其parentId，即二级评论的Id，那么显示二级评论时不会显示”回复@根评论作者“。

---

#### Path 参数
|参数名|类型|必填|描述|可选值|
|:---|:---:|:---:|:---|:---|
|startId|Long|是|起始评论ID（应该取想要查看对话的那条评论的"parentId"）|-|

#### 响应示例（成功）

```json
{
    "isSuccess": true,
    "data": [
        {
            "id": 2001,
            "userId": 1,
            "userName": "Julien",
            "userAvatar": "/default-avatar.png",
            "universityId": null,
            "universityName": null,
            "rootId": 1,
            "parentId": 1,
            "replyUserId": 2001,
            "replyUserName": "Thome",
            "showReplyUser": false,
            "content": "{\"type\":\"doc\",\"content\":[{\"type\":\"text\",\"text\":\"完了\"}]}",
            "likeCount": 0,
            "status": 1,
            "createTime": "2026-06-24T10:54:18"
        },
        {
            "id": 4003,
            "userId": 2001,
            "userName": "Thome",
            "userAvatar": "/default-avatar.png",
            "universityId": null,
            "universityName": null,
            "rootId": 1,
            "parentId": 2001,
            "replyUserId": 1,
            "replyUserName": "Julien",
            "showReplyUser": true,
            "content": "{\"type\":\"doc\",\"content\":[{\"type\":\"text\",\"text\":\"没事，完的不是我\"}]}",
            "likeCount": 0,
            "status": 1,
            "createTime": "2026-06-24T11:50:04"
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

### 删除评论 `POST /comment/delete`

**功能说明**
用户删除评论。

---

#### 请求参数
|参数名|类型|必填|描述|可选值|
|:---|:---:|:---:|:---|:---|
|id|Long|是|要删除的评论ID）|-|

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

## 搜索模块

## 错误码表
|错误码|错误信息|
|:---:|:---|
|SEARCH-20001|用户未登录|
|SEARCH-20002|搜索过于频繁，请稍后再试|

### 搜索帖子 `POST /search/post`

**功能说明**
用户通过关键词或者标签搜索帖子，并动态返回板块类型与发布时间筛选项（facets）。。另外用户可以自定义发布时间区间，但是指定区间则不生成发布时间标签。用户可以自己选择对搜索结果的排序规则。默认”RELEVANCE（综合）“，仅对纯英文搜索且结果集为空时有纠错功能，比如用户搜索“reds”0条命中，则返回“redis”，前端提示用户“您要找的是不是: redis”，用户点击即可搜索“redis”。

---

#### 请求参数
|参数名|类型|必填|描述|可选值|
|:---|:---:|:---:|:---|:---|
|keyword|string|是|用户搜索关键词,不能为空白字符串|-|
|boardType|string|否|筛选项：板块类型|**GRAD**:"考研"<br/>**CIVIL**:"考公"<br/>**WORK**:"工作"|
|publishTimeRange|string|否|筛选项：发布时间|**ONE_DAY**:"最近一天"<br/>**ONE_WEEK**:"最近一周"<br/>**ONE_MONTH**:"最近一个月"<br/>**OLDER**:"更早"|
|startTime|Long|否|自定义帖子发布时间区间的起始时间|毫秒时间戳|
|endTime|Long|否|自定义帖子发布时间区间的结束时间|毫秒时间戳|
|sortType|String|否|搜索排序方式，默认 RELEVANCE|RELEVANCE（综合）<br/>CREATE_TIME（发布时间）<br/>VIEW_COUNT（浏览量）|
|searchAfter|array|否| 游标分页值，首次不传| searchAfter 的元素数量与排序方式相关，必须原样透传，不可修改。|
|size|Integer|否|分页大小：默认10|10,20,30,50|

**特殊规则**：  
- 当指定 startTime 或 endTime 时，publishTimeRange 将被忽略。  
- 首次请求无 `searchAfter`，后续请求必须携带上次返回的 `searchAfter` 值（原样使用）。

#### 请求示例

```json
{
    "keyword":"大三怎么找实习"
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
                "title": "<em>大三</em><em>怎么</em><em>找</em><em>实习</em>",
                "preview": "找不到实习了，寄",
                "viewCount": 22,
                "likeCount": 1,
                "commentCount": 0,
                "favoriteCount": 0,
                "isTop": 0,
                "isEssence": 0,
                "createTime": "2026-04-06T22:56:00",
                "liked": true,
                "favorited": true,
                "hotComment": null
            }
        ],
        "searchAfter": [
            5.3555846,
            1775487360000,
            5
        ],
        "facets": [
            {
                "type": "板块类型",
                "items": [
                    {
                        "key": "2",
                        "label": "工作",
                        "count": 1
                    }
                ]
            },
            {
                "type": "发布时间",
                "items": [
                    {
                        "key": "更早",
                        "label": "更早",
                        "count": 1
                    }
                ]
            }
        ],
        "suggestKeyword":null
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

### 获取搜索历史 `GET /search/history`

**功能说明**  
获取当前登录用户最近的搜索历史记录，按搜索时间倒序排列（最近搜索的在最前面）。

---

#### 请求参数

无

---

#### 特殊规则

- 需要登录后才能访问
- 最多返回最近20条搜索记录
- 返回顺序为最近搜索在前

---

#### 响应示例（成功）

```json
{
  "isSuccess": true,
  "data": [
    "大三",
    "实习"
  ],
  "errorCode": null,
  "errorMessage": null
}
```

---

#### 响应示例（失败）

```json
{
  "isSuccess": false,
  "data": null,
  "errorCode": "SEARCH-20001",
  "errorMessage": "用户未登录"
}
```

---

### 清空搜索历史 `DELETE /search/history`

**功能说明**  
清空当前登录用户的全部搜索历史记录。

---

#### 请求参数

无

---

#### 特殊规则

- 需要登录后才能访问
- 操作不可恢复

---

#### 响应示例（成功）

```json
{
  "isSuccess": true,
  "data": null,
  "errorCode": null,
  "errorMessage": null
}
```

---

#### 响应示例（失败）

```json
{
  "isSuccess": false,
  "data": null,
  "errorCode": "SEARCH-20001",
  "errorMessage": "用户未登录"
}
```

---

### 删除单条搜索历史 `DELETE /search/history/{keyword}`

**功能说明**  
删除当前登录用户指定的一条搜索历史记录。

---

#### Path 参数

|参数名|类型|必填|描述|
|:---|:---:|:---:|:---|
|keyword|string|是|需要删除的搜索关键词|

---

#### 特殊规则

- 需要登录后才能访问
- `keyword` 必须进行 URL Encode
- 删除不存在的搜索记录时仍返回成功

---

#### 响应示例（成功）

```json
{
  "isSuccess": true,
  "data": null,
  "errorCode": null,
  "errorMessage": null
}
```

---

#### 响应示例（失败）

```json
{
  "isSuccess": false,
  "data": null,
  "errorCode": "SEARCH-20001",
  "errorMessage": "用户未登录"
}
```

## 通知模块

## 错误码表
|错误码|错误信息|
|:---:|:---|
|NOTIFICATION-20001|用户未登录|
|NOTIFICATION-20002|通知类型不支持|
|NOTIFICATION-20003|通知分类不支持|

### 查看是否有未读通知 `GET /notification/unread/status`

**功能说明**
主页查看是否有未读通知，如果有未读通知则显示“红点”。未登录用户没有通知功能，降级显示为没有未读通知。

---

#### 请求参数

无

#### 响应示例（成功）

```json
{
    "isSuccess": true,
    "data": true,
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

### 查询各个通知分类未读通知数 `GET /notification/unread/count`

**功能说明**
用户点击进入通知系统之后，显示各类通知（目前只有三类：“回复与@”，“收到的赞”和“收藏”）的未读数。用户需先登录。

---

#### 请求参数

无

#### 响应示例（成功）

```json
{
    "isSuccess": true,
    "data": {
        "interaction": 2,
        "like": 1,
        "favorite": 1
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

### 查看某一类通知列表 `POST /notification/like`

**功能说明**
用户点击某一类通知（目前只有三类：“回复与@”，“收到的赞”和“收藏”）分页查看通知列表。用户需先登录。

---

#### 请求参数
|参数名|类型|必填|描述|可选值|
|:---|:---:|:---:|:---|:---|
|category|NotificationCategory|是|通知分类|"INTERACTION"（回复与@），"LIKE"（收到的赞），"FAVORITE"（收藏），"FOLLOW"（新增粉丝）|
|cursor|Long|否|游标，上一页最后一条通知的ID（首页不传）|-|
|limit|PageSizeEnum|是|每页条数（默认10）|"SIZE_10", "SIZE_20", "SIZE_30"|

#### 请求示例（成功）

```json
{
    "category":"INTERACTION"
}
```

#### 响应示例（成功）

```json
{
    "isSuccess": true,
    "data": {
        "list": [
            {
                "id": 8,
                "type": 4,
                "targetType": 2,
                "postId": 5,
                "rootId": 1,
                "commentId": 4003,
                "senderId": 2001,
                "senderName": "Thome",
                "senderAvatar": "/default-avatar.png",
                "targetTitle": "大三怎么找实习",
                "targetContent": "完了",
                "content": "没事，完的不是我",
                "isRead": 0,
                "createTime": "2026-06-24T11:50:05"
            },
            {
                "id": 1,
                "type": 3,
                "targetType": 2,
                "postId": 5,
                "rootId": null,
                "commentId": 1,
                "senderId": 2001,
                "senderName": "Thome",
                "senderAvatar": "/default-avatar.png",
                "targetTitle": "大三怎么找实习",
                "targetContent": null,
                "content": "不知道",
                "isRead": 0,
                "createTime": "2026-06-16T21:29:37"
            }
        ],
        "hasNext": false,
        "cursor": 1
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

### 一键已读某类通知 `POST /notification/read`

**功能说明**
用户点击某一类通知（目前只有三类：“回复与@”，“收到的赞”和“收藏”）分页查看通知列表即用户这一类通知全部已读，无需一个一个点击。用户需先登录。

---

#### 请求参数
|参数名|类型|必填|描述|可选值|
|:---|:---:|:---:|:---|:---|
|category|NotificationCategory|是|通知分类|"INTERACTION"（回复与@），"LIKE"（收到的赞），"FAVORITE"（收藏），"FOLLOW"（新增粉丝）|

#### 请求示例（成功）

```json
{
    "category":"INTERACTION"
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
