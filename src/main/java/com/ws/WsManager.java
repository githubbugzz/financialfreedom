package com.ws;

import com.neovisionaries.ws.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * websocket的管理类
 *
 * @author hush23917
 * @create 2018/9/18
 */
public class WsManager {
    private static final Logger logger = LoggerFactory.getLogger(WsManager.class);
    //将其设置为一个单利模式
    /**
     * 声明一个私有化的waManager（单利模式）
     */
    private static final int FRAME_QUEUE_SIZE = 5;
    private static final int CONNECT_TIMEOUT = 5000;
    private static  WsManager wsManager;
    private String url="wss://stream.binance.com:9443/ws/bnbbtc@depth";
    private WebSocket ws;
    private WsStatus wsStatus;
    private WsManager(){//将构造方法进行私有化
    }
    public static WsManager getInstance(){//getInstance（）
        if(wsManager == null){
            synchronized (WsManager.class){
                if(wsManager == null){
                    wsManager = new WsManager();
                }
            }
        }
        return wsManager;
    }

    /**
     * 初始化，会直接进行ws连接
     */
    public void init(){
        try {
            String configURL = "wss://stream.binance.com:9443/ws/bnbbtc@depth";
            ws = getWebSocket();
            setStatus(WsStatus.CONNECTING);
            logger.debug("第一次连接");
        }catch (Exception e){e.printStackTrace();}
    }
    class WsListener extends WebSocketAdapter{
        @Override
        public void onTextMessage(WebSocket websocket, String text) throws Exception {
            super.onTextMessage(websocket, text);
            logger.debug(text);
        }
        @Override
        public void onConnected(WebSocket websocket, Map<String, List<String>> headers)
                throws Exception {
            super.onConnected(websocket, headers);
            logger.debug("连接成功");
            setStatus(WsStatus.CONNECT_SUCCESS);
        }
        @Override
        public void onConnectError(WebSocket websocket, WebSocketException exception)
                throws Exception {
            super.onConnectError(websocket, exception);
            logger.debug("连接错误");
            setStatus(WsStatus.CONNECT_FAIL);
            reconnect();
        }
        @Override
        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer)
                throws Exception {
            super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
            logger.debug("断开连接");
            setStatus(WsStatus.CONNECT_FAIL);
            reconnect();
        }
    }
    public void setStatus(WsStatus wsStatus){this.wsStatus = wsStatus;}
    public WsStatus getWsStatus(){return this.wsStatus;}
    /**
     * 主动断开连接
     */
    public void disconnect(){
        if(ws != null){ws.disconnect();}
    }
    /**
     * 重新连接
     */
    private int reconnectCount = 0;//重连次数
    public void reconnect(){
        if(!isNetConnect()){
            reconnectCount = 0;
            logger.debug("网络不可用");
            return;
        }
        if(ws != null&&
                !ws.isOpen()&&//ws不可用
                wsStatus!=WsStatus.CONNECTING){//不是正在重连状态
            reconnectCount++;
            wsStatus = WsStatus.CONNECTING;
            logger.debug("准备开始第%d次重连 -- url:%s", reconnectCount, url);
            //用一个线程执行一个重新连接的任务
            new Thread(runnable).start();
        }
    }
    private Runnable runnable = new Runnable() {
        public void run() {
            try {
                ws = getWebSocket();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private WebSocket getWebSocket() throws IOException {
        return new WebSocketFactory().createSocket(url, CONNECT_TIMEOUT)
                .setFrameQueueSize(FRAME_QUEUE_SIZE)//设置帧队列最大值为5
                .setMissingCloseFrameAllowed(false)//设置不允许服务端关闭连接却未发送关闭帧
                .addListener(new WsListener())//添加回调监听
                .connectAsynchronously();//异步连接
    }

    private boolean isNetConnect(){
        return true;
    }
}
