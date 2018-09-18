package com.ws;

/**
 * websocket的状态描述类
 *
 * @author hush23917
 * @create 2018/9/18
 */
public enum WsStatus {
    /**
     * 连接成功
     */
    CONNECT_SUCCESS,
    /**
     * 连接失败
     */
    CONNECT_FAIL,
    /**
     * 正在连接
     */
    CONNECTING;
}
