package no.kristiania.http;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class QueryString {
    private Map<String, String> parameters = new LinkedHashMap<>();

    public QueryString(String queryString) {
        if (queryString.isEmpty()) return;
        for (String parameter : queryString.split("&")) {
            int equalPos = parameter.indexOf("=");
            String value = URLDecoder.decode(parameter.substring(equalPos+1), StandardCharsets.UTF_8);
            String parameterName = parameter.substring(0, equalPos);
            parameters.put(parameterName, value);
        }

    }

    public String getParameter(String name) {
       return parameters.get(name);
    }

    public void addParameter(String key, String value) {
        parameters.put(key,value);
    }

    public String getQueryString() {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> parameter : parameters.entrySet()){
            if (result.length() > 0){
                result.append("&");
            }
            result.append(parameter.getKey())
                    .append("=")
                    .append(parameter.getValue());
        }
        return result.toString();
    }
}
