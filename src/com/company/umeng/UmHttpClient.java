package com.company.umeng;

import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by umeng on 5/13/16.
 */
public class UmHttpClient {
    public static String SUB_URL;
    public static String SDK_VERSION;
    public static String SERVER_ADDRESS = "";
    private static final String END = "\r\n";
    private static final String TAG = "Umeng Request ";
    //multipart post boundary
    private static String boundary = UUID.randomUUID().toString();

    public static String ACCESS_TOKEN = null;

    public static int CONNECTION_TIME_OUT = 7000;

    public static int SOCKET_TIME_OUT = 7000;

    public static String APP_KEY = null;

    public static String APP_SECRECT = null;

    public enum HttpMethod {
        POST, GET, PUT, DELETE
    }


    public String sentRequest(String subUrl, HttpMethod httpMethod, Map<String, Object> data) {
        if(subUrl==null){
            return null;
        }
        SUB_URL = subUrl;
        String result = null;
        try {
        if (httpMethod == HttpMethod.GET || httpMethod == HttpMethod.DELETE) {
            subUrl = subUrl + buildParameter(ACCESS_TOKEN, APP_KEY, data,httpMethod);
            System.out.println("Umeng rest url:" + SERVER_ADDRESS+subUrl);
        } else {
            subUrl = subUrl + buildParameter(ACCESS_TOKEN, APP_KEY, data,httpMethod);
            System.out.println("Umeng rest url:" + SERVER_ADDRESS+subUrl);
        }
        HttpsURLConnection urlConnection;
        OutputStream outputStream;

            urlConnection = (HttpsURLConnection) new URL((SERVER_ADDRESS+subUrl).trim()).openConnection();
            urlConnection.setReadTimeout(SOCKET_TIME_OUT);
            urlConnection.setConnectTimeout(CONNECTION_TIME_OUT);
            setRequestMethod(httpMethod, urlConnection);
            if(ACCESS_TOKEN!=null){
                urlConnection.setRequestProperty("accesstoken",ACCESS_TOKEN);
            }
            if (HttpMethod.POST == httpMethod || HttpMethod.PUT == httpMethod) {
                outputStream = new DataOutputStream(urlConnection.getOutputStream());
                String dataString = makePostBody(data);
                outputStream.write(dataString.getBytes());
                outputStream.flush();
                outputStream.close();
            }

            if (urlConnection.getResponseCode() == 200) {
                System.out.println("HTTP 200:"+urlConnection.getResponseMessage());
                result = convertStreamToString(urlConnection.getInputStream());
            }else{
                System.out.println(TAG+" Response code:"+urlConnection.getResponseCode()+" Msg:"+urlConnection.getResponseMessage());
            }
        } catch (MalformedURLException e) {
            System.out.println(TAG+e.getMessage());
        } catch (IOException e) {
            System.out.println(TAG+e.getMessage());
        } catch (NoSuchAlgorithmException e){
            System.out.println(TAG+e.getMessage());
        }
        return result;
    }

    /**
     * @param accessToken
     * @param appKey
     * @param data
     * @return
     */
    private String buildParameter(String accessToken, String appKey, Map<String, Object> data,HttpMethod method) throws NoSuchAlgorithmException{
        StringBuilder sb = new StringBuilder();
        if (appKey != null && !appKey.equals("")) {
            sb.append("?ak=" + APP_KEY);
        } else {
            System.out.println(TAG+" umeng app key is empty or null");
        }
//        if (accessToken != null && !accessToken.equals("")) {
//            sb.append("&access_token=" + ACCESS_TOKEN);
//        }
        if(method.equals(HttpMethod.GET) || method.equals(HttpMethod.DELETE)){
            if (data != null && !data.isEmpty()) {
                sb.append(makeUrl(data));
            }
        }
        // remove this line before upload
        sb.append("&test=1").append("&sdkv="+SDK_VERSION);
        //adding anti-spam
        sb.append("&_t_=").append(System.currentTimeMillis()/1000).append("&_s_=").append(appendEncryData(method,data,SUB_URL)).append("&_e_=md5");
        return sb.toString();
    }

    private String makeUrl(Map<String,Object> data){
        StringBuilder stringBuilder = new StringBuilder();
        if (data != null && !data.isEmpty()) {
            Set<String> keySet = data.keySet();
            for (String key : keySet) {
                Object value = data.get(key);
                if (value instanceof String) {
                    stringBuilder.append("&" + key + "=" + value);
                } else if (value instanceof Integer) {
                    stringBuilder.append("&" + key + "=" + value);
                } else if (value instanceof Float){
                    stringBuilder.append("&" + key + "=" +value);
                }
            }
        }
        return stringBuilder.toString().trim();
    }

    private String makePostBody(Map<String,Object> data){
        StringBuilder stringBuilder = new StringBuilder(makeUrl(data));
        stringBuilder.deleteCharAt(0);
        return stringBuilder.toString().trim();
    }

    private String appendEncryData(HttpMethod httpMethod,Map<String,Object> data,String subUrl) throws NoSuchAlgorithmException{
        String result="";
        if(httpMethod.equals(HttpMethod.POST) || httpMethod.equals(HttpMethod.PUT)){
            //System.out.println(httpMethod.name().toUpperCase()+ ":" + subUrl + ":" + (System.currentTimeMillis() / 1000 + ":" + sortParaMeter(data) + ":" + UmHttpClient.APP_SECRECT));
            result = MD5Util.getMD5(httpMethod.name().toUpperCase()+ ":" + subUrl + ":" + (System.currentTimeMillis() / 1000 + ":" + sortParaMeter(data) + ":" + UmHttpClient.APP_SECRECT));
        }else{
            //System.out.println(httpMethod.name().toUpperCase()+ ":" + subUrl + ":" + (System.currentTimeMillis() / 1000 + ":" + UmHttpClient.APP_SECRECT));
            result = MD5Util.getMD5(httpMethod.name().toUpperCase() + ":" + subUrl + ":" + System.currentTimeMillis() / 1000 + ":" + UmHttpClient.APP_SECRECT);
        }
        return result.trim();
    }

    private void setRequestMethod(HttpMethod httpMethod, HttpURLConnection urlConnection) throws ProtocolException {
        switch (httpMethod) {
            case POST:
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setDoOutput(true);
                break;
            case PUT:
                urlConnection.setRequestMethod("PUT");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setDoOutput(true);
                break;
            case GET:
                urlConnection.setRequestMethod("GET");
                break;
            case DELETE:
                urlConnection.setRequestMethod("DELETE");
                break;
            default:
                urlConnection.setRequestMethod("GET");
                break;
        }
    }

    /**
     * 此方法一封装AES加密算法
     *
     * @param data
     * @param url
     * @return getting access token for the application
     */
    public String accessTokenRequest(Map<String, Object> data, String url, String APP_SECRET) {
        String stringData = "";
        try {
            JSONObject jsonObject = new JSONObject(data);
            stringData = jsonObject.toString();
            String encry_data = AESUtils.getEncryptedMap(stringData, APP_SECRET);
            data.put("encrypted_data", encry_data);
            return sentRequest(url, HttpMethod.POST, data);
        } catch (JSONException e){
            System.out.println("Umeng access token request error:"+e.getMessage());
        }
        return stringData.trim();
    }



    private void addBinaryParams(String name, OutputStream outputStream, byte[] data) throws IOException {
        addFilePart(name, data, boundary, outputStream);
        finishWrite(outputStream, boundary);
    }

    private void addFilePart(final String fieldName, byte[] data, String boundary, OutputStream outputStream)
            throws IOException {
        StringBuilder writer = new StringBuilder();
        writer.append("--").append(boundary).append(END)
                .append("Content-Disposition: form-data; name=\"")
                .append("content").append("\"; filename=\"").append(fieldName)
                .append("\"").append(END).append("Content-Type: ")
                .append("application/octet-stream").append(END)
                .append("Content-Transfer-Encoding: binary").append(END)
                .append(END);
        outputStream.write(writer.toString().getBytes());
        outputStream.write(data);
        outputStream.write(END.getBytes());
    }

    private void addBodyParams(OutputStream outputStream, Map<String, Object> bodyMaps)
            throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        if (bodyMaps != null) {
            Set<String> keys = bodyMaps.keySet();
            for (String key : keys) {
                Object value = bodyMaps.get(key);
                // 列表类型,即同名的多个参数。
                if (isListParams(value)) {
                    if (value != null) {
                        addListParams(stringBuilder, key, (List<String>) value);
                    }
                } else {
                    if (bodyMaps.get(key) != null) {
                        addFormField(stringBuilder, key, bodyMaps.get(key).toString(), boundary);
                    }
                }
            }
            outputStream.write(stringBuilder.toString().getBytes());
        }
    }
//
//
    private void finishWrite(OutputStream outputStream, String boundary) throws IOException {
        outputStream.write(END.getBytes());
        outputStream.write(("--" + boundary + "--").getBytes());
        outputStream.write(END.getBytes());
        outputStream.flush();
        outputStream.close();
    }
//
    private boolean isListParams(Object param) {
        return (param instanceof List<?>);
    }

    private void addFormField(StringBuilder writer, final String name, final String value, String boundary) {
        writer.append("--").append(boundary).append(END)
                .append("Content-Disposition: form-data; name=\"").append(name)
                .append("\"").append(END)
                .append("Content-Type: text/plain; charset=").append("UTF-8")
                .append(END).append("Content-Transfer-Encoding: 8bit")
                .append(END).append(END).append(value).append(END);
    }

    private void addListParams(StringBuilder stringBuilder, String key, List<String> param) {
        // Prepare Category Array
        for (String value : param) {
            addFormField(stringBuilder, key, value, boundary);
        }
    }

    private static String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is), 8192);
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {

            return null;
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return sb.toString();
    }

    private String sortParaMeter(Map<String,Object> map){
        StringBuilder stringBuilder = new StringBuilder();
        TreeMap<String,Object> stringObjectTreeMap = new TreeMap<String, Object>(map);

        for(Map.Entry<String,Object> entry:stringObjectTreeMap.entrySet()){
            if (isListParams(entry.getValue())){
                for (String temp: (List<String>)entry.getValue()) {
                    stringBuilder.append(entry.getKey()).append("=").append(temp).append("&");
                }

            }else {
                stringBuilder.append(entry.toString());
                stringBuilder.append("&");
            }
//            builder.appendQueryParameter(entry.getKey(),entry.getValue().toString());
        }
        String para = stringBuilder.toString();
        if(para!=null && !para.equals("")){
            stringBuilder.deleteCharAt(stringBuilder.toString().length()-1);
        }
        //Log.d("para",stringBuilder.toString());
//        stringBuilder.append(builder);
        return stringBuilder.toString().trim();
    }

}
