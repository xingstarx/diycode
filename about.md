# 相关事项

### Module 说明

| 名称           | 简介                            |
| ------------ | ----------------------------- |
| diycode-sdk  | SDK，提供认证和各类数据获取与创建。           |
| diycode-test | 测试，对sdk中各类方法进行测试，同时也是简单的调用示例。 |
| diycode-app  | 应用，完整的应用，主要负责UI方面内容。          |



## diycode-sdk 

对 diycode api 的再次封装，从标准的 http 接口转化为用户可以直接调用的接口，在封装过程中会屏蔽掉部分细节，让其更加容易使用。

sdk 的结构非常简单，并没有复杂的结构，主要需要关注以下的几个类(接口)。

| 名称               | 类型             | 简介                                     |
| ---------------- | -------------- | -------------------------------------- |
| *DiycodeService* | Interface (接口) | 使用 retrofit2 定义的标准请求接口                 |
| *DiycodeAPI*     | Interface (接口) | 定了了面向用户(上层用户)的标准接口                     |
| **Diycode**      | Class (类)      | 实现了 DiycodeAPI， 具体实现使用 DiycodeService。 |
| utils            | Package (包)    | 工具类合集                                  |
| bean             | Package (包)    | 实体类合集                                  |
| event            | Package (包)    | 事件合集(EventBus 3.0)                     |

**为了让逻辑更加清晰，也更方便，sdk 没有设置回调接口(避免多层回调的情况)，所有返回数据均使用 Event 来接收，使用起来非常简单，下面用登录逻辑作为示例：**

1. 注册 EventBus

```java
@Override
protected void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
}

@Override
protected void onStop() {
    super.onStop();
    EventBus.getDefault().unregister(this);
}
```

2. 添加接收事件的方法

```java
@Subscribe(threadMode = ThreadMode.MAIN)
public void onLoginEvent(LoginEvent event) {
    String state = "当前状态：";
    if (event.isOk()) {
        Toast.makeText(this, "登录成功", Toast.LENGTH_LONG).show();
    } else {
        Toast.makeText(this, "登录失败", Toast.LENGTH_LONG).show();
    }
}
```

3. 调用登录逻辑

```java
// 初始化 Diycode, 初始化仅需执行一次，client_id 请到 https://www.diycode.cc/oauth/applications/new 自行申请
Diycode.getInstance().init(this.getApplicationContext(), client_id, client_secret);

// 获取 Diycode
Diycode mDiycode = Diycode.getInstance();

mDiycode.login(name, password);
```

整个逻辑非常简单，核心代码不到 20 行就能实现登录登录，登录完成后，用户的 token 会被 sdk 缓存起来，在请求其他数据的时候，会自动附加上 token。

### 接口 和 事件

**1. 接口对应的事件命名规则**

由于所有信息回调都是基于 EventBus 的，为了方便用户知道哪个方法对应的那个事件的回调，所有事件的命名都是非常有规律的，例如：

| 接口名称      | 对应事件           |
| --------- | -------------- |
| login     | LoginEvent     |
| hello     | HelloEvent     |
| getTopics | GetTopicsEvent |

> 方法名+Event = 信息回调的Event

除此之外，还会在接口注释上进行标注，如果找不到的话，可以去看注释，例如：

```java
/**
 * 登录时调用
 * 返回一个 token，用于获取各类私有信息使用，该 token 用 LoginEvent 接收。
 *
 * @param user_name 用户名
 * @param password  密码
 * @see LoginEvent <-- 此处标注了该方法应该使用哪个 Event 接收
 */
void login(@NonNull String user_name, @NonNull String password);
```

**2.事件(Event)的一些特点**

因为涉及到了网络请求，因此不可能所有的请求都会成功，某些请求会失败，而且失败的情况有很多种，例如：网络没有连接、没有权限，服务器异常等。

为了区分这些情况，我为所有的事件(Event)都添加了一些状态信息，你可以用以下方法获取。

| 方法                | 简介                             |
| ----------------- | ------------------------------ |
| event.isOk();     | 判断是否请求成功，如果事件中包括有实体类，说明实体类不为空。 |
| evenr.getState(); | 获取随着网络传回来的请求码，可以根据请求码判断是哪种情况。  |

当然了，返回码是一个数字，并不直观，如果想要获取该请求码的描述的话可以像下面这样：

```java
String describe = CodeDescribe.getDescribe(event.getState());
```

另外，在网络不好的情况下，可能会出现多次重复请求，之后一次性返回多次请求结果情况，对于这种情况，会为每一次请求添加一个id，用以区分是哪一次的请求的返回结果。