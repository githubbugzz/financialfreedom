package test;

import com.ws.WsManager;

/**
 * WsManager测试类
 *
 * @author hush23917
 * @create 2018/9/18
 */
public class WSManagerTest {
    public static void main(String[] args) {
        WsManager wsManager = WsManager.getInstance();
        wsManager.init();
    }
}
