package cordova.plugin.payu;

import androidx.annotation.Nullable;

import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import com.payu.base.models.ErrorResponse;
import com.payu.base.models.PayUPaymentParams;
import com.payu.checkoutpro.PayUCheckoutPro;
import com.payu.checkoutpro.utils.PayUCheckoutProConstants;
import com.payu.ui.model.listeners.PayUCheckoutProListener;
import com.payu.ui.model.listeners.PayUHashGenerationListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.Toast;
import android.util.Log;
import org.json.JSONArray;

import java.security.Key;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.HashMap;
/**
 * This class echoes a string called from JavaScript.
 */
public class PayU extends CordovaPlugin implements PayUHashGenerationListener{
    private final String testSalt = "67njRYSI";
    PayUHashGenerationListener Listener;
    private final String testMerchantSecretKey = "e425e539233044146a2d185a346978794afd7c66";
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        }else if(action.equals("startPayment")){
            String amount = args.getString(0);
            String transactionId = args.getString(1);
			boolean isProd = args.getBoolean(2);
            String prodInfo = args.getString(3);
               
            String key = args.getString(4);
            String phone = args.getString(5);
            String firstName = args.getString(6);
            String email = args.getString(7);
            String sUrl = args.getString(8);
            String fUrl = args.getString(9);
            String userCredentials = args.getString(10);
            String addParam1 = args.getString(11);
            String addParam2 = args.getString(12);
            String addParam3 = args.getString(13);
            String addParam4 = args.getString(14);
            String addParam5 = args.getString(15);
            String addParam6 = args.getString(16);
            String addParam7 = args.getString(17);    

            this.startPayment(amount,transactionId,isProd,prodInfo,key,phone,firstName,email,
            sUrl,fUrl,userCredentials,addParam1,addParam2,addParam3,addParam4,addParam5,
            addParam6,addParam7,callbackContext);
            return true;

        }else if(action.equals("setHash")){
             String hashName = args.getString(0);
             String hash = args.getString(1);
             this.setHash(hashName,hash,callbackContext);
             return true;

        }
        return false;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
  
    private void startPayment(String amt,String transactionId,boolean isProd,String prodInfo,String key,String phone,String firstName,String email,String sUrl,String fUrl,
	String userCredentials,String addParam1,String addParam2,String addParam3,String addParam4,String addParam5,
            String addParam6,String addParam7,CallbackContext callbackContext){
		
        HashMap<String, Object> additionalParams = new HashMap<>();
        additionalParams.put(PayUCheckoutProConstants.CP_UDF1, addParam1);
        additionalParams.put(PayUCheckoutProConstants.CP_UDF2, addParam2);
        additionalParams.put(PayUCheckoutProConstants.CP_UDF3, addParam3);
        additionalParams.put(PayUCheckoutProConstants.CP_UDF4, addParam4);
        additionalParams.put(PayUCheckoutProConstants.CP_UDF5, addParam5);
        if(addParam6!=null){
             additionalParams.put(PayUCheckoutProConstants.CP_VAS_FOR_MOBILE_SDK, addParam6);
        }
        if(addParam7!=null){
             additionalParams.put(PayUCheckoutProConstants.CP_PAYMENT_RELATED_DETAILS_FOR_MOBILE_SDK, addParam7);
        }
	    PayUPaymentParams.Builder builder = new PayUPaymentParams.Builder();
        builder.setAmount(amt)
                .setIsProduction(isProd)
                .setProductInfo(prodInfo)
                .setKey(key)
                .setPhone(phone)
                .setTransactionId(transactionId)
                .setFirstName(firstName)
                .setEmail(email)
                .setSurl(sUrl)
                .setFurl(fUrl)
                .setUserCredential(userCredentials)
                .setAdditionalParams(additionalParams);
        PayUPaymentParams payUPaymentParams = builder.build();
        PayUCheckoutPro.open(
                this.cordova.getActivity(),
                payUPaymentParams,
                new PayUCheckoutProListener() {
                    @Override
                    public void onPaymentSuccess(Object response) {
                        HashMap<String,Object> result = (HashMap<String, Object>) response;
                        String payuResponse = (String)result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE);
                        String merchantResponse = (String) result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE);
						JSONObject reply = new JSONObject();
                         try {
                              reply.put("payuResponse",payuResponse);
                              reply.put("merchantResponse",merchantResponse);
                              reply.put("payUResponseCode",0);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
					    callbackContext.success(reply);
                    }

                    @Override
                    public void onPaymentFailure(Object response) {
                        HashMap<String,Object> result = (HashMap<String, Object>) response;
                        String payuResponse = (String)result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE);
                        String merchantResponse = (String) result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE);
                        JSONObject reply = new JSONObject();
                         try {
                              reply.put("payuResponse",payuResponse);
                              reply.put("merchantResponse",merchantResponse);
                              reply.put("payUResponseCode",1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
					    callbackContext.error(reply);
                    }

                    @Override
                    public void onPaymentCancel(boolean isTxnInitiated) {
                        JSONObject reply = new JSONObject();
                         try {
                              reply.put("payUResponse",isTxnInitiated);
                              reply.put("payUResponseCode",2);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
					    callbackContext.success(reply);
                    }

                    @Override
                    public void onError(ErrorResponse errorResponse) {
                        String errorMessage = errorResponse.getErrorMessage();
                         JSONObject reply = new JSONObject();
                         try {
                              reply.put("payUResponse",errorMessage);
                              reply.put("payUResponseCode",3);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
					    callbackContext.error(reply);
                    }

                    @Override
                    public void setWebViewProperties(@Nullable WebView webView, @Nullable Object o) {
                        
                    }

                    @Override
                    public void generateHash(HashMap<String, String> valueMap, PayUHashGenerationListener hashGenerationListener) {
                            String hashName = valueMap.get(PayUCheckoutProConstants.CP_HASH_NAME);
                            String hashData = valueMap.get(PayUCheckoutProConstants.CP_HASH_STRING);
                            // JSONObject reply = new JSONObject();
                            // try {
                            //         reply.put("payUResponse","hash value not found");
                            //         reply.put("payUResponseCode",1);
                            //     } catch (JSONException e) {
                            //         e.printStackTrace();
                            //     }

                           // String errorMessage = errorResponse.getErrorMessage();
                            JSONObject reply = new JSONObject();
                            try {
                                reply.put("hashName",hashName);
                                reply.put("hashData",hashData);
                                reply.put("payUResponseCode",4);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            Listener = hashGenerationListener;
                           
                            callbackContext.success(reply);


                        // if (!TextUtils.isEmpty(hashName) && !TextUtils.isEmpty(hashData)) {
                        //     //Do not generate hash from local, it needs to be calculated from server side only. Here, hashString contains hash created from your server side.
                        //    String hash = hashString;
                        //    HashMap<String, String> dataMap = new HashMap<>();
                        //    dataMap.put(hashName, hash);
                        //    hashGenerationListener.onHashGenerated(dataMap);
                        // }

                        //   String hashName = valueMap.get(PayUCheckoutProConstants.CP_HASH_NAME);
                        // String hashData = valueMap.get(PayUCheckoutProConstants.CP_HASH_STRING);
                        // if (!TextUtils.isEmpty(hashName) && !TextUtils.isEmpty(hashData)) {
                        //     //Generate Hash from your backend here
                        //     String hash = null;
                        //     if (hashName.equalsIgnoreCase(PayUCheckoutProConstants.CP_LOOKUP_API_HASH)){
                        //         //Calculate HmacSHA1 HASH for calculating Lookup API Hash
                        //         ///Do not generate hash from local, it needs to be calculated from server side only. Here, hashString contains hash created from your server side.

                        //         hash = calculateHmacSHA1Hash(hashData, testMerchantSecretKey);
                        //     } else {

                        //         //Calculate SHA-512 Hash here
                        //         hash = calculateHash(hashData + testSalt);
                        //     }
                        //     Log.d("hash",hash);
                        //     HashMap<String, String> dataMap = new HashMap<>();
                        //     dataMap.put(hashName, hash);
                        //     hashGenerationListener.onHashGenerated(dataMap);
                        // }
                    }

                    
                }
        );
    }

    public void setHash(String hashName, String hashData, CallbackContext callbackContext){
       // Toast.makeText(this.cordova.getActivity(),"hash called",Toast.LENGTH_LONG).show();
                         
            if (!TextUtils.isEmpty(hashName) && !TextUtils.isEmpty(hashData)) {
                // //Generate Hash from your backend here
                // String hash = null;
                // if (hashName.equalsIgnoreCase(PayUCheckoutProConstants.CP_LOOKUP_API_HASH)){
                //     //Calculate HmacSHA1 HASH for calculating Lookup API Hash
                //     ///Do not generate hash from local, it needs to be calculated from server side only. Here, hashString contains hash created from your server side.

                //     hash = calculateHmacSHA1Hash(hashData, testMerchantSecretKey);
                // } else {

                //     //Calculate SHA-512 Hash here
                //     hash = calculateHash(hashData + testSalt);
                // }
               // Toast.makeText(this.cordova.getActivity(),hash,Toast.LENGTH_LONG).show();
                //Toast.makeText(this.cordova.getActivity(),Listener.toString(),Toast.LENGTH_LONG).show();
                HashMap<String, String> dataMap = new HashMap<>();
                dataMap.put(hashName, hashData);
                Listener.onHashGenerated(dataMap);
            }

        // HashMap<String, String> dataMap = new HashMap<>();
        // dataMap.put(hashName, hash);
        // Listener.onHashGenerated(dataMap);
       // callbackContext.success("success");
    }

     private String calculateHash(String hashString) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            messageDigest.update(hashString.getBytes());
            byte[] mdbytes = messageDigest.digest();
            return getHexString(mdbytes);
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    private String getHexString(byte[] array){
        StringBuilder hash = new StringBuilder();
        for (byte hashByte : array) {
            hash.append(Integer.toString((hashByte & 0xff) + 0x100, 16).substring(1));
        }
        return hash.toString();
    }

    private String calculateHmacSHA1Hash(String data, String key) {
        String HMAC_SHA1_ALGORITHM = "HmacSHA1";
        String result = null;
        JSONObject response = new JSONObject();
        try {
            response.put("hello",result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            Key signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(data.getBytes());
            result = getHexString(rawHmac);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public void onHashGenerated(HashMap<String, String> hashMap) {
        
    }
}
