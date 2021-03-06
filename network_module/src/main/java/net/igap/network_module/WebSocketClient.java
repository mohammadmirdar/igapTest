/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the kianiranian Company - http://www.kianiranian.com/
 * All rights reserved.
 */

package net.igap.network_module;



import android.text.format.DateUtils;
import android.util.Log;


import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketCloseCode;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketState;

import net.iga.common.KeyStore;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class WebSocketClient {

    private static WebSocketClient instance;

    private WebSocket webSocketClient;
    private boolean autoConnect;


    private WebSocketClient() {
        autoConnect = true;
        try {
            this.webSocketClient = new WebSocketFactory().setConnectionTimeout((int) (10 * DateUtils.SECOND_IN_MILLIS)).createSocket("wss://secure.igap.net/hybrid/");
            this.webSocketClient.setPingInterval(60 * DateUtils.SECOND_IN_MILLIS);
            this.webSocketClient.addListener(new WebSocketAdapter() {

                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                    Log.wtf(this.getClass().getName(), "onConnected");

                    // HelperConnectionState.connectionState(ConnectionState.CONNECTING);
                    checkFirstResponse();
                    //   G.runOnUiThread(() -> EventManager.getInstance(AccountManager.selectedAccount).postEvent(EventManager.SOCKET_CONNECT_OK, ""));


                    super.onConnected(websocket, headers);
                }

                @Override
                public void onBinaryMessage(WebSocket websocket, final byte[] binary) throws Exception {
                    RequestManager.getInstance(0).onBinaryReceived(binary);
                }

                @Override
                public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
                    /*resetMainInfo();
                    reconnect(true);*/
                    Log.wtf(this.getClass().getName(), "onError" + cause.getMessage());


                    super.onError(websocket, cause);
                }

                @Override
                public void onFrameSent(WebSocket websocket, WebSocketFrame frame) throws Exception {
                    RequestManager.getInstance(0).onFrameSent(frame);
                }

                @Override
                public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception {
                    super.onStateChanged(websocket, newState);
                    Log.wtf(this.getClass().getName(), "onStateChanged" + "newState: " + newState.name());
                }

                @Override
                public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                    Log.wtf(this.getClass().getName(), "onDisconnected");
                    RequestManager.getInstance(0).timeOutImmediately(null, true);
                    resetMainInfo();

                    if (autoConnect)
                        // G.handler.postDelayed(() -> connect(true), DateUtils.SECOND_IN_MILLIS);
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                connect(true);
                            }
                        },DateUtils.SECOND_IN_MILLIS);
                    //G.runOnUiThread(() -> EventManager.getInstance(AccountManager.selectedAccount).postEvent(EventManager.SOCKET_DISCONNECT, ""));


                    super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
                }

                @Override
                public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
                    Log.wtf(this.getClass().getName(), "onConnectError");
                    resetMainInfo();

                    if (autoConnect)
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                connect(true);
                            }
                        },DateUtils.SECOND_IN_MILLIS);

                    //G.runOnUiThread(() -> EventManager.getInstance(AccountManager.selectedAccount).postEvent(EventManager.SOCKET_CONNECT_ERROR, exception.getError().name() + ": " + exception.getMessage()));

                    super.onConnectError(websocket, exception);
                }
            });

            //HelperConnectionState.connectionState(ConnectionState.CONNECTING);
            this.webSocketClient.connectAsynchronously();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized static WebSocketClient getInstance() {
        if (instance == null) {
            instance = new WebSocketClient();
        }

        return instance;
    }

    public void sendBinary(byte[] message, RequestWrapper requestWrapper) {
        webSocketClient.sendBinary(message, requestWrapper);
    }

    /**
     * role back main data for preparation reconnecting to socket
     */
    private void resetWebsocketInfo() {

        /**
         * when secure is false set useMask true otherwise set false
         */
        RequestManager.getInstance(0).setSecure(false);
        // WebSocket.useMask = true;
        RequestManager.getInstance(0).setUserLogin(false);
    }

    /**
     * reset some info just for 'RealmRoom' after connection is lost
     */
    private void resetMainInfo() {
        //RealmRoom.clearAllActions();
        resetWebsocketInfo();
    }

    /**
     * if not secure yet send fake message to server for securing preparation
     */
    private void checkFirstResponse() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                int count = 0;

                while (KeyStore.symmetricKey == null && isConnect()) {

                    if (count < 3) {
                        count++;
                        try {
                            Thread.sleep((int) (10 * DateUtils.SECOND_IN_MILLIS));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (KeyStore.symmetricKey == null && isConnect()) {
                            if (webSocketClient != null) {
                                webSocketClient.sendText("i need 30001");
                            }
                        }
                    } else {
                        if (webSocketClient != null) {
                            webSocketClient.disconnect();
                        }
                    }
                }
            }
        });
        thread.start();
    }

    public boolean isAutoConnect() {
        return autoConnect;
    }

    public boolean isConnect() {
        return webSocketClient != null && webSocketClient.isOpen();
    }

    public void connect(boolean autoConnect) {
        this.autoConnect = autoConnect;

        if (webSocketClient.getState() != WebSocketState.CONNECTING && webSocketClient.getState() != WebSocketState.OPEN) {
            resetWebsocketInfo();
            // HelperConnectionState.connectionState(ConnectionState.CONNECTING);
            if (webSocketClient.getState() == WebSocketState.CLOSED) {
                try {
                    webSocketClient = webSocketClient.recreate();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            webSocketClient.connectAsynchronously();
        } else {
            //  G.runOnUiThread(() -> EventManager.getInstance(AccountManager.selectedAccount).postEvent(EventManager.SOCKET_CONNECT_DENY, "state of socket is : " + webSocketClient.getState().name()));
        }
    }

    public void disconnectSocket(boolean autoConnect) {
        this.autoConnect = autoConnect;
        webSocketClient.disconnect(WebSocketCloseCode.NORMAL, null, 0);
    }
}
