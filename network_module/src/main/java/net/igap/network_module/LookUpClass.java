/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.igap.network_module;

import android.util.Log;
import android.util.SparseArray;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class LookUpClass {

    private static LookUpClass instance;
    private SparseArray<Class<?>> classes;
    public static HashMap<Integer, String> lookupMap = new HashMap<>();
    public static HashMap<Integer, Integer> priorityActionId = new HashMap<>();
    public static ArrayList<String> unSecure = new ArrayList<>();
    public static ArrayList<String> unSecureResponseActionId = new ArrayList<>();
    public static ArrayList<String> unLogin = new ArrayList<>();// list of actionId that can be doing without secure
    public static ArrayList<String> waitingActionIds = new ArrayList<>();
    public static ArrayList<String> generalImmovableClasses = new ArrayList<>();
    public static ArrayList<Integer> forcePriorityActionId = new ArrayList<>();
    public static ArrayList<Integer> ignoreErrorCodes = new ArrayList<>();

    public static LookUpClass getInstance() {
        if (instance == null) {
            instance = new LookUpClass();
        }
        return instance;
    }

    public LookUpClass() {
        classes = new SparseArray<>();
        classes.put(IG_RPC.Error.actionId, IG_RPC.Error.class);
        classes.put(IG_RPC.Res_User_Login.actionId, IG_RPC.Res_User_Login.class);
    }

    public boolean validObject(int actionId) {
        return classes.get(actionId) != null;
    }

    public AbstractObject deserializeObject(int actionId, byte[] message) {
        Class<?> resClass = classes.get(actionId);
        if (resClass != null) {
            try {
                AbstractObject object = (AbstractObject) resClass.newInstance();
                object.readParams(message);

                return object;
            } catch (IllegalAccessException e) {
                // FileLog.e(e);
            } catch (InstantiationException e) {
                //FileLog.e(e);
            } catch (Exception e) {
                //FileLog.e(e);
            }
        }

        Log.e(getClass().getSimpleName(), "IllegalAccessException getClassInstance: " + actionId);
        return null;
    }

    public AbstractObject getClassInstance(int actionId) {
        Class<?> objClass = classes.get(actionId);
        if (objClass != null) {
            AbstractObject response;
            try {
                response = (AbstractObject) objClass.newInstance();
            } catch (Throwable e) {
                e.printStackTrace();
                return null;
            }
            return response;
        }

        Log.e(getClass().getSimpleName(), "getClassInstance: " + actionId);

        return null;
    }

    public static void fillArrays() {
        LookUpClass.fillLookUpClassArray();
        LookUpClass.fillUnSecureList();
        LookUpClass.fillUnSecureServerActionId();
        LookUpClass.fillUnLoginList();
        LookUpClass.fillImmovableClasses();
        LookUpClass.fillWaitingRequestActionIdAllowed();
        LookUpClass.fillPriorityActionId();
        LookUpClass.fillForcePriorityActionId();
        LookUpClass.fillIgnoreErrorCodes();
    }

    /**
     * fill static hashMap with actionId and proto class name
     */
    private static void fillLookUpClassArray() {

    }

    /**
     * list of actionId that can be doing without secure
     * (for send request)
     */
    private static void fillUnSecureList() {
        unSecure.add("2");
    }

    /**
     * list of actionIds that allowed continue processing even communication is not secure
     * (for receive response)
     */
    private static void fillUnSecureServerActionId() {
        unSecureResponseActionId.add("30001");
        unSecureResponseActionId.add("30002");
        unSecureResponseActionId.add("30003");
    }

    /**
     * list of actionId that can be doing without login
     * (for send request)
     */
    private static void fillUnLoginList() {
        unLogin.add("100");
        unLogin.add("101");
        unLogin.add("102");
        unLogin.add("500");
        unLogin.add("501");
        unLogin.add("502");
        unLogin.add("503");
        unLogin.add("131");
        unLogin.add("132");
        unLogin.add("138");
        unLogin.add("139");
        unLogin.add("140");
        unLogin.add("802");
        unLogin.add("506");
    }

    /**
     * list off classes(fragments) that don't have any animations for open and close state
     */
    private static void fillImmovableClasses() {
//        generalImmovableClasses.add(FragmentShowAvatars.class.getName());
//        generalImmovableClasses.add(FragmentShowContent.class.getName());
    }

    /**
     * list of actionId that will be storing in waitingActionIds list
     * and after that user login send this request again
     * (for send request)
     */
    private static void fillWaitingRequestActionIdAllowed() {
        waitingActionIds.add("201");
        waitingActionIds.add("310");
        waitingActionIds.add("410");
        //waitingActionIds.add("700");
        //waitingActionIds.add("701");
        //waitingActionIds.add("702");
        //waitingActionIds.add("703");
        //waitingActionIds.add("705");
    }


    private static void fillPriorityActionId() {
        priorityActionId.put(700, 50);
        priorityActionId.put(701, 50);
        priorityActionId.put(702, 50);
        priorityActionId.put(703, 50);
        priorityActionId.put(704, 50);
    }

    private static void fillForcePriorityActionId() {
        forcePriorityActionId.add(210);
        forcePriorityActionId.add(319);
    }

    private static void fillIgnoreErrorCodes() {
        ignoreErrorCodes.add(5); // timeout
        ignoreErrorCodes.add(617); // get room history not found
    }

    /**
     * search in lookupMap and get key from value after replace "." with ""
     *
     * @param className current class name
     * @return id
     */

    public static int getActionId(String className) {

        Iterator keys = lookupMap.keySet().iterator();
        while (keys.hasNext()) {

            int id = (int) keys.next();
            String lookupMapValue = lookupMap.get(id);
            lookupMapValue = "Request" + lookupMapValue.replace(".", "");

            if (lookupMapValue.equals(className)) {
                return id;
            }
        }
        return -1;
    }
}
