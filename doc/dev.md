


# Code

## Framework
* Storage - JPA+Spring+H2
* Service - SpringMVC+Entity

## Code Style

* fluent
* chain
* [horstmann/Allman](https://en.wikipedia.org/wiki/Indent_style) like ident style


# Data Format

## i518 记事本
```
^(?:标题|Caption):(?<title>[^\r\n]*)[\r\n]+
(?<date>[0-9-]+)
\((?<week>[^)]+)\)
(?<time>[0-9:]+)\s*(?<weather>[^\r\n]+)[\r\n]+
```

## i518 短信
```
^
(?<type>收件人|发件人|To|From)[：:]
(?<target>[^\r\n]*)[\r\n]+
(?<date>[^\s]+)\s+(?<time>[^\r\n]*)[\r\n]
```

## 豌豆荚导出短信

```
sms,submit,xx,	+12345,,2014. 2.28 10:10,23,"content"
信息类型,操作类型,目标名,号码,未知,日期,内容
```

信息类型只见 sms
submit 为发送
deliver 为接收

