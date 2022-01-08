package net.igap.network_module;

import android.util.Log;

import com.google.protobuf.ByteString;

import net.iGap.proto.ProtoConnectionSecuring;
import net.iGap.proto.ProtoResponse;
import net.iGap.proto.ProtoUserRegister;

import java.util.List;

public class IG_RPC {


    public static class Error extends AbstractObject {
        public static int actionId = 0;
        public int minor;
        public int major;

        @Override
        public void readParams(byte[] message) throws Exception {
            net.iGap.proto.ProtoError.ErrorResponse response = net.iGap.proto.ProtoError.ErrorResponse.parseFrom(message);
            resId = response.getResponse().getId();
            minor = response.getMinorCode();
            major = response.getMajorCode();
        }
    }

    public static class TimeOut_error extends Error {
        public TimeOut_error() {
            Log.e("IG_RPC_timeout", "TimeOut_error");
            major = 5;
            minor = 1;
        }
    }
    public static class Connection_securing_response extends AbstractObject {
        public static final int actionId = 30001;

        public String publicKey;
        public int symmetricKeyLength;
        public int heartbeatInterval;
        public int secondaryChunkSize;
        public String primaryNodeName;
        ProtoResponse.Response responseResponse;

        public static Connection_securing_response deserializeObject(int constructor, byte[] message) {
            if (constructor != actionId || message == null) {
                return null;
            }

            Connection_securing_response object = null;
            try {
                object = new Connection_securing_response();
                object.readParams(message);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return object;
        }

        @Override
        public void readParams(byte[] message) throws Exception {
            ProtoConnectionSecuring.ConnectionSecuringResponse response = ProtoConnectionSecuring.ConnectionSecuringResponse.parseFrom(message);

            responseResponse = response.getResponse();
            publicKey = response.getPublicKey();
            symmetricKeyLength = response.getSymmetricKeyLength();
            heartbeatInterval = response.getHeartbeatInterval();
            primaryNodeName = response.getPrimaryNodeName();
            secondaryChunkSize = response.getSecondaryChunkSize();
        }
    }

    public static class Connection_symmetric_key extends AbstractObject {
        public static final int actionId = 2;

        public ByteString symmetricKey;
        public int version;

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {
            return Connection_symmetric_key_response.deserializeObject(constructor, message);
        }

        @Override
        public Object getProtoObject() {
            ProtoConnectionSecuring.ConnectionSymmetricKey.Builder builder = ProtoConnectionSecuring.ConnectionSymmetricKey.newBuilder();
            builder.setSymmetricKey(symmetricKey);
            builder.setVersion(version);
            return builder;
        }

        @Override
        public int getActionId() {
            return actionId;
        }
    }

    public static class Connection_symmetric_key_response extends AbstractObject {
        public static final int actionId = 30002;

        public int symmetricIvSize;
        public String symmetricMethod;
        public ProtoConnectionSecuring.ConnectionSymmetricKeyResponse.Status status;

        public static Connection_symmetric_key_response deserializeObject(int constructor, byte[] message) {
            if (constructor != actionId || message == null) {
                return null;
            }

            Connection_symmetric_key_response object = null;
            try {
                object = new Connection_symmetric_key_response();
                object.readParams(message);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return object;
        }

        @Override
        public void readParams(byte[] message) throws Exception {
            ProtoConnectionSecuring.ConnectionSymmetricKeyResponse response = ProtoConnectionSecuring.ConnectionSymmetricKeyResponse.parseFrom(message);

            status = response.getStatus();
            symmetricIvSize = response.getSymmetricIvSize();
            symmetricMethod = response.getSymmetricMethod();
        }
    }

    public static class User_Login extends AbstractObject {
        public static int actionId = 100;
        public long phoneNumber;
        public String countryCode;
        public int methodValue;
        public net.iGap.proto.ProtoRequest.Request.Builder buildForValue;
        public int appId;


        @Override
        public int getActionId() {
            return actionId;
        }

        @Override
        public AbstractObject deserializeResponse(int constructor, byte[] message) {
            return Res_User_Login.deserializeObject(constructor, message);
        }

        @Override
        public Object getProtoObject() {
            ProtoUserRegister.UserRegister.Builder builder = ProtoUserRegister.UserRegister.newBuilder();
            builder.setPhoneNumber(phoneNumber);
            builder.setCountryCode(countryCode);
            builder.setAppId(appId);
            builder.setPreferenceMethodValue(methodValue);
            builder.setRequest(buildForValue);
            return builder;
        }
    }

    public static class Res_User_Login extends AbstractObject {
        public static int actionId = 30100;
        public String userNameR;
        public long userIdR;
        public ProtoUserRegister.UserRegisterResponse.Method methodValue;
        public List<Long> smsNumbers;
        public String regex;
        public int verifyCodeDigitCount;
        public String authorHashR;
        public boolean callMethodSupported;
        public long resendCodeDelay;


        public static Res_User_Login deserializeObject(int constructor, byte[] message) {
            if (constructor != actionId || message == null) {
                return null;
            }

            Res_User_Login object = null;
            try {
                object = new Res_User_Login();
                object.readParams(message);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return object;
        }

        @Override
        public void readParams(byte[] message) throws Exception {
            ProtoUserRegister.UserRegisterResponse response = ProtoUserRegister.UserRegisterResponse.parseFrom(message);
            resId = response.getResponse().getId();
            userNameR = response.getUsername();
            userIdR = response.getUserId();
            methodValue = response.getMethod();
            smsNumbers = response.getSmsNumberList();
            regex = response.getVerifyCodeRegex();
            verifyCodeDigitCount = response.getVerifyCodeDigitCount();
            authorHashR = response.getAuthorHash();
            callMethodSupported = response.getCallMethodSupported();
            resendCodeDelay = response.getResendDelay();
        }
    }

}
