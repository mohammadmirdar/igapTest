/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iga.common;

/**
 * Helper Class for preparation class name for using dynamic create class
 */
public class HelperClassNamePreparation {

    public static String preparationProtoClassName(String className) {

        String protoClassName;
        String packageName = "net.iGap.proto.";
        className = className.replace(".", "$"); // example : ProtoConnectionSecuring.ConnectionSymmetricKeyResponse =>
        protoClassName = packageName + className; // example : ChatClearMessage => net.iGap.proto.ProtoConnectionSecuring$ConnectionSymmetricKeyResponse

        return protoClassName;
    }

    public static String preparationResponseClassName(String className) {

        String packageName = "net.iGap.response.";
        String responseClass = className.split("\\.")[1]; // example : ProtoConnectionSecuring.ConnectionSymmetricKeyResponse => ConnectionSymmetricKeyResponse
        String responseClassName = packageName + responseClass; // example : net.iGap.response.ConnectionSymmetricKeyResponse

        return responseClassName;
    }

    public static String preparationRequestClassName(String className) {

        String requestClassName;
        String packageName = "net.iGap.request.";
        String firstWordClassName = "Request";

        className = className.replace(".", ""); // example : Chat.Clear.Message => ChatClearMessage
        requestClassName = packageName + firstWordClassName + className; // example : ChatClearMessage => net.iGap.request.RequestChatClearMessage

        return requestClassName;
    }
}
