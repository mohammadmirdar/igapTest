package net.igap.network_module;

import android.app.Application;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;

import androidx.annotation.Nullable;


import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketFrame;

import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoMessageContainer;
import net.iGap.proto.ProtoRequest;
import net.iGap.proto.ProtoResponse;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import net.iga.common.DispatchQueue;
import net.iga.common.HelperClassNamePreparation;
import net.iga.common.HelperNumerical;
import net.iga.common.HelperString;
import net.iga.common.KeyStore;


import javax.inject.Inject;

public class RequestManager {
    private static volatile RequestManager instance;
    private DispatchQueue networkQueue = new DispatchQueue("networkQueue");
    private String TAG = "RequestManager";

    private ConcurrentHashMap<String, RequestWrapper> requestQueueMap = new ConcurrentHashMap<>();
    private HashMap<Integer, List<RequestWrapper>> requestsByUId = new HashMap<>();
    private AtomicBoolean pullRequestQueueRunned = new AtomicBoolean(false);
    // private Handler handler = new Handler();
    private boolean isSecure;
    private boolean userLogin;
    private boolean configLoaded;
    private WebSocketClient socketClient;
    private static int lastClassUniqueId;
    private KeyGenerator keyGenerator;
    private boolean inSecuring;


    public static RequestManager getInstance(int account) {
        RequestManager localInstance = instance;
        if (localInstance == null) {
            synchronized (RequestManager.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new RequestManager(account);
                }
            }
        }
        return localInstance;
    }

    public RequestManager(int currentAccount) {
        //super(currentAccount);
        socketClient = WebSocketClient.getInstance();
        keyGenerator = KeyGenerator.getInstance(currentAccount);
    }

    private void getConfig() {
        if (configLoaded) {
            return;
        }
        configLoaded = true;

    }

    public static int getLastClassUniqueId() {
        return lastClassUniqueId++;
    }

    public String sendRequest(AbstractObject request, OnResponse onResponse) {
        return sendRequest(new RequestWrapper(request, onResponse));
    }

    public boolean isSecure() {
        return isSecure;
    }

    public boolean isUserLogin() {
        return userLogin;
    }

    public String sendRequest(final RequestWrapper requestWrapper) {
        final String randomId = HelperString.generateKey();
        networkQueue.postRunnable(() -> {
            prepareRequest(randomId, requestWrapper);
            //FileLog.e("RequestManager sendRequest with id -> " + randomId + " with action id -> " + requestWrapper.actionId + " protoObject -> " + (requestWrapper.protoObject != null ? requestWrapper.protoObject.getClass().getSimpleName() : "NULL"));
        });
        return randomId;
    }

    public boolean cancelRequest(String id) {
        RequestWrapper requestWrapper = requestQueueMap.get(id);
        if (requestWrapper == null)
            return false;
        requestQueueMap.remove(id);
        return true;
    }

    private void prepareRequest(String randomId, RequestWrapper requestWrapper) {
        if (!pullRequestQueueRunned.get()) {
            pullRequestQueueRunned.getAndSet(true);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    requestQueuePullFunction();
                }
            }, 1000);
        }

        requestWrapper.setRandomId(randomId);
        ProtoRequest.Request.Builder requestBuilder = ProtoRequest.Request.newBuilder();
        requestBuilder.setId(randomId);

        try {
            Object protoObject = requestWrapper.getProtoObject();
            Object protoInstance = null;
            try {
                Method setRequestMethod = protoObject.getClass().getMethod("setRequest", ProtoRequest.Request.Builder.class);
                protoInstance = setRequestMethod.invoke(protoObject, requestBuilder);
                Method method2 = protoInstance.getClass().getMethod("build");
                protoInstance = method2.invoke(protoInstance);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                //FileLog.e(e);
                //HelperLog.getInstance().setErrorLog(e);
            }
            requestQueueMap.put(randomId, requestWrapper);

            byte[] actionId = HelperNumerical.intToByteArray(requestWrapper.actionId);
            actionId = HelperNumerical.orderBytesToLittleEndian(actionId);

            Method toByteArrayMethod = protoInstance.getClass().getMethod("toByteArray");
            byte[] payload = (byte[]) toByteArrayMethod.invoke(protoInstance);
            byte[] message = HelperNumerical.appendByteArrays(actionId, payload);

            if (isSecure) {
                if (userLogin || LookUpClass.unLogin.contains(requestWrapper.actionId + "")) {
                    message = AESCrypt.encrypt(keyGenerator.getSymmetricKey(), message);
                    if (KeyStore.FILE_LOG_ENABLE) {
//                        FileLog.i("MSGR " + "prepareRequest: " + G.lookupMap.get(30000 + requestWrapper.actionId));
                    }
                    WebSocketClient.getInstance().sendBinary(message, requestWrapper);
                }
            } else if (LookUpClass.unSecure.contains(requestWrapper.actionId + "")) {
                WebSocketClient.getInstance().sendBinary(message, requestWrapper);
            } else {
                timeOutImmediately(randomId, false);
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | GeneralSecurityException | NullPointerException e) {
//            FileLog.e(e);
//            HelperLog.getInstance().setErrorLog(e);
        } catch (Exception e) {
//            FileLog.e(e);
//            HelperLog.getInstance().setErrorLog(e);
        }
    }

    private void requestQueuePullFunction() {
        for (Map.Entry<String, RequestWrapper> entry : requestQueueMap.entrySet()) {
            String key = entry.getKey();
            RequestWrapper requestWrapper = entry.getValue();
            boolean delete;
            if (requestWrapper.actionId == 102) {
                delete = timeDifference(requestWrapper.getTime(), (10 * DateUtils.SECOND_IN_MILLIS));
            } else {
                delete = timeDifference(requestWrapper.getTime(), KeyStore.TIME_OUT_MS);
            }
            if (delete) {
                requestQueueMapRemover(key);
            }
        }

        if (requestQueueMap.size() > 0) {
            //  application.runOnUiThread(this::requestQueuePullFunction, KeyStore.TIME_OUT_DELAY_MS);
        } else {
            pullRequestQueueRunned.getAndSet(false);
        }

    }

    private void requestQueueMapRemover(String key) {
        try {
            RequestWrapper requestWrapper = requestQueueMap.remove(key);
            if (requestWrapper != null) {
                if (requestWrapper.onResponse != null) {
                    IG_RPC.TimeOut_error error = new IG_RPC.TimeOut_error();
                    requestWrapper.onResponse.onReceived(null, error);
                } else {
                    int actionId = requestWrapper.getActionId();
                    String className = LookUpClass.lookupMap.get(actionId + KeyStore.LOOKUP_MAP_RESPONSE_OFFSET);
                    String responseClassName = HelperClassNamePreparation.preparationResponseClassName(className);


                    ProtoResponse.Response.Builder responseBuilder = ProtoResponse.Response.newBuilder();
                    responseBuilder.setTimestamp((int) System.currentTimeMillis());
                    responseBuilder.setId(key);
                    responseBuilder.build();

                    ProtoError.ErrorResponse.Builder errorBuilder = ProtoError.ErrorResponse.newBuilder();
                    errorBuilder.setResponse(responseBuilder);
                    errorBuilder.setMajorCode(5);
                    errorBuilder.setMinorCode(1);
                    errorBuilder.build();

                    Class<?> c = Class.forName(responseClassName);
                    Object object;
                    try {
                        object = c.getConstructor(int.class, Object.class, Object.class).newInstance(actionId, errorBuilder, requestWrapper.identity);
                    } catch (NoSuchMethodException e) {
                        object = c.getConstructor(int.class, Object.class, String.class).newInstance(actionId, errorBuilder, requestWrapper.identity);
                    }
                    Method setTimeoutMethod = object.getClass().getMethod("timeOut");
                    setTimeoutMethod.invoke(object);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void timeOutImmediately(@Nullable String keyRandomId, boolean allRequest) {
        for (Map.Entry<String, RequestWrapper> entry : requestQueueMap.entrySet()) {
            String key = entry.getKey();
            if (allRequest || key.equals(keyRandomId)) {
                requestQueueMapRemover(key);
            }
        }
    }

    private boolean timeDifference(long beforeTime, long config) {
        if (beforeTime == 0) {
            return false;
        }

        long difference;

        long currentTime = System.currentTimeMillis();
        difference = (currentTime - beforeTime);

        return difference >= config;
    }


    public void unpack(byte[] message) {
        int actionId = getId(message);
        byte[] payload = getProtoInfo(message);
        String className = getClassName(actionId);
        Log.e("dfjshfjshfjsdhf", "unpack1: " + actionId);

        if (!isSecure && LookUpClass.unSecureResponseActionId.contains(Integer.toString(actionId))) {

            if (actionId == IG_RPC.Connection_securing_response.actionId) {
                IG_RPC.Connection_securing_response response = IG_RPC.Connection_securing_response.deserializeObject(actionId, payload);
                if (response != null) {
                    KeyStore.currentTime = response.responseResponse.getTimestamp();
                    KeyStore.serverHeartBeatTiming = (response.heartbeatInterval * 1000);

                    if (inSecuring) {
                        return;
                    }

                    inSecuring = true;

                    keyGenerator.initKey(response.symmetricKeyLength);
                    byte[] encryption = keyGenerator.getEncryptedKey(response.publicKey, response.secondaryChunkSize);

                    if (encryption != null) {
                        IG_RPC.Connection_symmetric_key req = new IG_RPC.Connection_symmetric_key();
                        req.symmetricKey = ByteString.copyFrom(encryption);
                        req.version = 2;
                        RequestWrapper requestWrapper = new RequestWrapper(req, null);
                        prepareRequest(HelperString.generateKey(), requestWrapper);
                        // requestWrapper.setMessageId(HelperString.generateKey());
                        //socketClient.sendBinary(requestWrapper.readMessage(), requestWrapper);
                    }
                }
            } else if (actionId == IG_RPC.Connection_symmetric_key_response.actionId) {
                IG_RPC.Connection_symmetric_key_response response = IG_RPC.Connection_symmetric_key_response.deserializeObject(actionId, payload);
                if (response != null) {
                    if (response.status.getNumber() == KeyStore.REJECT) {
                        socketClient.disconnectSocket(true);
                    } else if (response.status.getNumber() == KeyStore.ACCEPT) {
                        setSecure(true);
                        WebSocket.useMask = false;
                        keyGenerator.securingDone(response.symmetricIvSize, response.symmetricMethod);
                        String sm = response.symmetricMethod;
                        KeyStore.ivSize = response.symmetricIvSize;
                        KeyStore.symmetricMethod = sm.split("-")[2];
                        // must delete in refactor and replace with selectAndSendRequest();
                    }
                }
            }


            return;
        }

        final LookUpClass lookUp = LookUpClass.getInstance();
        boolean isRpc = lookUp.validObject(actionId);

        if (isRpc) {
            AbstractObject object = lookUp.deserializeObject(actionId, payload);
            if (object != null) {
                String resId = object.getResId() != null && object.getResId().equals("") ? null : object.getResId();
                if (resId != null) {
                    RequestWrapper requestWrapper = requestQueueMap.get(resId);
                    if (requestWrapper != null && requestWrapper.onResponse != null) {
                        if (actionId == 0) {
                            requestWrapper.onResponse.onReceived(null, object);
                        } else {
                            requestWrapper.onResponse.onReceived(object, null);
                        }
                        requestQueueMap.remove(resId);
                        return;
                    }
                } else {
                    if (actionId != 0) {
                        //MessageController.getInstance(AccountManager.selectedAccount).onUpdate(object);
                    }
                }
            }
        }

        if (className == null) {
            return;
        }

        String protoClassName = HelperClassNamePreparation.preparationProtoClassName(className);
        Object protoObject = fillProtoClassData(protoClassName, payload);
        String responseId = getResponseId(protoObject);

        //FileLog.i("RequestManager unpack responseId -> " + responseId + " action id -> " + actionId + " class name -> " + className);

        if (responseId == null) {
            if (actionId == 0) {
                instanceResponseClass(actionId, protoObject, null, "error");
            } else {
                instanceResponseClass(actionId, protoObject, null, "handler");
            }
        } else {
            if (!requestQueueMap.containsKey(responseId)) {
                return;
            }

            try {
                RequestWrapper requestWrapper = requestQueueMap.remove(responseId);
                if (actionId == 0) { // error
                    if (requestWrapper != null) {
                        int finalAct = requestWrapper.getActionId() + KeyStore.LOOKUP_MAP_RESPONSE_OFFSET;
                        instanceResponseClass(finalAct, protoObject, requestWrapper.identity, "error");
                    }
                } else {
                    if (requestWrapper != null) {
                        instanceResponseClass(actionId, protoObject, requestWrapper.identity, "handler");
                    }
                }
            } catch (Exception e) {
                //FileLog.e(e);
            }
        }
    }

    private String getResponseId(Object protoObject) {
        String responseId = null;
        try {
            Method method = protoObject.getClass().getMethod("getResponse");
            ProtoResponse.Response response = (ProtoResponse.Response) method.invoke(protoObject);
            if (response.getId().equals("")) {
                return null;
            }
            responseId = response.getId();
        } catch (SecurityException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return responseId;
    }

    private int getId(byte[] byteArray) {
        byteArray = Arrays.copyOfRange(byteArray, 0, 2);
        byteArray = HelperNumerical.orderBytesToLittleEndian(byteArray);

        int value = 0;
        for (int i = 0; i < byteArray.length; i++) {
            value += ((int) byteArray[i] & 0xffL) << (8 * i);
        }
        return value;
    }

    private String getClassName(int value) {
        if (!LookUpClass.lookupMap.containsKey(value))
            return null;

        return LookUpClass.lookupMap.get(value);
    }

    private byte[] getProtoInfo(byte[] byteArray) {
        byteArray = Arrays.copyOfRange(byteArray, 2, byteArray.length);
        return byteArray;
    }

    private Object fillProtoClassData(String protoClassName, byte[] protoMessage) {
        Object object3 = null;
        try {
            Class<?> c = Class.forName(protoClassName);
            Constructor<?> constructor = c.getDeclaredConstructor();
            constructor.setAccessible(true);
            Object object1 = constructor.newInstance();
            Method method1 = object1.getClass().getMethod("newBuilder");
            Object object2 = method1.invoke(object1);
            Method method2 = object2.getClass().getMethod("mergeFrom", byte[].class);
            object3 = method2.invoke(object2, protoMessage);
            Method method3 = object3.getClass().getMethod("build");
            method3.invoke(object3);
        } catch (InstantiationException e) {
            //HelperLog.getInstance().setErrorLog(e);
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            //HelperLog.getInstance().setErrorLog(e);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // HelperLog.getInstance().setErrorLog(e);
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // HelperLog.getInstance().setErrorLog(e);
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // HelperLog.getInstance().setErrorLog(e);
            e.printStackTrace();
        }

        return object3;
    }

    private void instanceResponseClass(int actionId, Object protoObject, Object identity, String optionalMethod) {
        try {
            String className = getClassName(actionId);

            if (className == null) {
                return;
            }

            String responseClassName = HelperClassNamePreparation.preparationResponseClassName(className);
            Class<?> responseClass = Class.forName(responseClassName);
            Constructor<?> constructor;

            try {
                constructor = responseClass.getDeclaredConstructor(int.class, Object.class, Object.class);
            } catch (NoSuchMethodException e) {
                constructor = responseClass.getDeclaredConstructor(int.class, Object.class, String.class);
            }

            constructor.setAccessible(true);
            Object object = constructor.newInstance(actionId, protoObject, identity);

            if (optionalMethod != null) {
                responseClass.getMethod(optionalMethod).invoke(object);
            }
        } catch (InstantiationException e) {
            //HelperLog.getInstance().setErrorLog(e);
            //FileLog.e(e);
        } catch (IllegalAccessException e) {
            //HelperLog.getInstance().setErrorLog(e);
            //FileLog.e(e);
        } catch (ClassNotFoundException e) {
            // HelperLog.getInstance().setErrorLog(e);
            // FileLog.e(e);
        } catch (NoSuchMethodException e) {
            //  HelperLog.getInstance().setErrorLog(e);
            // FileLog.e(e);
        } catch (InvocationTargetException e) {
            //  FileLog.e(e);
        }
    }

    public void onBinaryReceived(byte[] binary) {
        if (isSecure) {
            try {
                byte[] iv = HelperNumerical.getIv(binary, KeyStore.ivSize);
                byte[] binaryDecode = HelperNumerical.getMessage(binary);
                binaryDecode = AESCrypt.decrypt(keyGenerator.getSymmetricKey(), iv, binaryDecode);
                unpack(binaryDecode);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
        } else {
            unpack(binary);
        }
    }


    public void setUserLogin(boolean userLogin) {
        this.userLogin = userLogin;
        //     G.runOnUiThread(() -> getEventManager().postEvent(EventManager.USER_LOGIN_CHANGED));
    }

    public void setPullRequestQueueRunned(boolean runned) {
        pullRequestQueueRunned.set(runned);
    }

    public void onFrameSent(WebSocketFrame frame) {
        String id = ((RequestWrapper) frame.getRequestWrapper()).getRandomId();
        RequestWrapper requestWrapper = requestQueueMap.get(id);
        if (requestWrapper != null) {
            requestWrapper.setTime(System.currentTimeMillis());
            requestQueueMap.put(requestWrapper.getRandomId(), requestWrapper);
        }
    }

    public void bindRequestToUniqueId(final String reqId, final int uid) {
        networkQueue.postRunnable(() -> {
            RequestWrapper request = requestQueueMap.get(reqId);
            if (request != null) {
                List<RequestWrapper> list = new ArrayList<>();
                list.add(request);
                requestsByUId.put(uid, list);
            }
        });
    }

    protected void setSecure(boolean isSecure) {
        this.isSecure = isSecure;
        inSecuring = false;

        Log.i(TAG, "setSecure: " + isSecure);

        if (!isSecure) {
            keyGenerator.resetKeys();
        }

        if (!configLoaded && isSecure) {
            getConfig();
        }
    }

    public void cancelRequestByUniqueId(int fragmentUniqueId) {
        networkQueue.postRunnable(() -> {
            List<RequestWrapper> requests = requestsByUId.get(fragmentUniqueId);
            if (requests != null) {
                for (int i = 0; i < requests.size(); i++) {
                    RequestWrapper req = requests.get(i);
                    req.canceled = true;
                    cancelRequest(req.randomId);
                    requestsByUId.remove(fragmentUniqueId);
                }
            }
        });
    }
}
