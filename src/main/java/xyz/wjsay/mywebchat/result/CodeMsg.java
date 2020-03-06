package xyz.wjsay.mywebchat.result;

public class CodeMsg {
    private int status;
    private String message;
    private Object data;

    private CodeMsg(int status, String message) {
        this.status = status;
        this.message = message;
    }

    private CodeMsg(Object data) {
        this.status = 0;
        this.message = "OK";
        this.data = data;  // 无法接收数组
    }

    public static CodeMsg success(Object data) {
        return new CodeMsg(data);
    }

    // 成功
    public static final CodeMsg SUCCESS = new CodeMsg(0, "OK");

    // 3001** 注册登陆
    public static final CodeMsg USER_EXIST  = new CodeMsg(300100, "用户已存在");
    public static final CodeMsg INFO_ERROR = new CodeMsg(300101, "用户名或密码错误");
    public static final CodeMsg NO_LOGIN = new CodeMsg(300102, "未登录");

    // 3005**
    public static final CodeMsg SERVER_ERROR = new CodeMsg(300500, "服务器内部错误");
    public static final CodeMsg ARGUMENT_ILLEGAL = new CodeMsg(300501, "请求不合法");

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public static CodeMsg getUserExist() {
        return USER_EXIST;
    }

    public static CodeMsg getServerError() {
        return SERVER_ERROR;
    }
}
