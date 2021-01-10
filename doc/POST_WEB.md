## 配置转发到WEB后，测试或者转发时会向配置的token即url发送POST请求

url:
https://api.sl.allmything.com/api/msg/pushMsg?token=p9EM2K4Po01UIJr3sISbRmBFYWCHOGQaqwqk6cgxdsfyevTXtz8hVUlNAunD5i

### 请求体如下
> post form
参数：

|  key   | 类型  |  说明  |
|  ----  | ----  | ----  |
| from  | string  | 来源手机号 |
| content  | string  | 短信内容 |

#### 有一个已经实现好的站点[消息通知](https://msg.allmything.com)
