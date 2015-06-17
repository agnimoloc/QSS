package com.churpi.qualityss.service;

import java.io.UnsupportedEncodingException;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

public class JsonObjectRequestResponseString extends JsonRequest<JSONObject> {

	/** Charset for request. */
    private static final String PROTOCOL_CHARSET = "utf-8";

    /** Content type for request. */
    private static final String PROTOCOL_CONTENT_TYPE =
        String.format("application/x-www-form-urlencoded; charset=%s", PROTOCOL_CHARSET);
    
	public JsonObjectRequestResponseString(int method, String url,
			JSONObject jsonRequest, Listener<JSONObject> listener, ErrorListener errorListener) {
		super(method, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener,
				errorListener);
	}
	
	/*@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		if(headers != null){
			if(super.getHeaders() != null && super.getHeaders().keySet() != null){
				for(String key :super.getHeaders().keySet()){
					if(!headers.containsKey(key)){
						headers.put(key, super.getHeaders().get(key));	
					}
				}
			}
			return headers;
		}
		return super.getHeaders();
	}*/
	
	@Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
		try {
			String jsonString =
					new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			JSONObject json = new JSONObject();
			try{
				json = new JSONObject(jsonString);
			}catch (JSONException je) {
				json.put("value", jsonString);
			}
			return Response.success(json,
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JSONException je) {
			
			return Response.error(new ParseError(je));
		}
	}

}
