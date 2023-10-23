package reverse.retrofit;

import jadx.api.DecompilationMode;
import jadx.api.JadxArgs;
import jadx.api.JadxDecompiler;
import jadx.api.JavaClass;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        Pattern methodPattern = Pattern.compile("\\.method.+?\\.end method", Pattern.DOTALL);

        JadxArgs jadxArgs = new JadxArgs();
        jadxArgs.setDecompilationMode(DecompilationMode.FALLBACK);
        jadxArgs.setAllowInlineKotlinLambda(false);
        jadxArgs.setInlineAnonymousClasses(false);
        jadxArgs.setSkipResources(true);
//        jadxArgs.setInputFile(new File("cinema.apk"));
        jadxArgs.setInputFile(new File("cinema.apk"));
        try (JadxDecompiler jadx = new JadxDecompiler(jadxArgs)) {
            jadx.load();
            for (JavaClass cls : jadx.getClasses()) {
                String clsSmali = cls.getSmali();
                if (clsSmali.contains(".annotation runtime Lretrofit2")) {
                    //System.out.println(cls.getSmali());
                    Matcher matcher = methodPattern.matcher(clsSmali);
                    while (matcher.find()) {
                        for (int j = 0; j <= matcher.groupCount(); j++) {
                            String methodSmali = matcher.group(j);
                            HashMap<String, Object> parsedMethod = parseSmaliMethod(methodSmali);

                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static HashMap<String, Object> parseSmaliMethod(String methodSmali) {
        HashMap<String, Object> parsedMethod = new HashMap<>();
        ArrayList<HashMap<String, String>> paramList = new ArrayList<>();
        String name = null;
        String url = null;
        String returnValue = null;
        String httpMethod = null;
        String httpHeader = null;

        Pattern methodNamePattern = Pattern.compile("\\.method.+?(\\w+)\\(", Pattern.DOTALL);
        Pattern methodReturnPattern = Pattern.compile("\\.method.+?\\(.*?\\)(.+?);", Pattern.DOTALL);
        Pattern methodParamPattern = Pattern.compile("\\.param\\sp.+?#\\s(.+?);.*?\\.annotation\\sruntime\\s(.+?);.+?(value\\s=\\s(.+?)\\s)*\\.end\\sannotation", Pattern.DOTALL);
        Pattern methodHTTPMethod = Pattern.compile("\\.annotation\\sruntime\\s.+?retrofit2/http/(PUT|POST).+?value\\s=\\s\"(.+?)\"\\s+\\.end\\sannotation", Pattern.DOTALL);
        Pattern headersHTTPMethod = Pattern.compile("\\.annotation\\sruntime\\sLretrofit2/http/Headers;\\s+value\\s=\\s.+?\"(.+?)\".+?\\.end\\sannotation", Pattern.DOTALL);
        Matcher nameMatcher = methodNamePattern.matcher(methodSmali);
        Matcher returnMatcher = methodReturnPattern.matcher(methodSmali);
        Matcher httpMethodMatcher = methodHTTPMethod.matcher(methodSmali);
        Matcher paramMatcher = methodParamPattern.matcher(methodSmali);
        Matcher headerHTTPMatcher = headersHTTPMethod.matcher(methodSmali);

        //System.out.println("-----------------------");
        //System.out.println(methodSmali);

        if (nameMatcher.find()) {
            name = nameMatcher.group(1);
        }
        if (returnMatcher.find()) {
            returnValue = returnMatcher.group(1);
        }
        if (httpMethodMatcher.find()) {
            httpMethod = httpMethodMatcher.group(1);
            url = httpMethodMatcher.group(2);
        }
        if (headerHTTPMatcher.find()) {
            httpHeader = headerHTTPMatcher.group(1);
        }
        while (paramMatcher.find()) {
            HashMap<String, String> paramDetails = new HashMap<>();
            paramDetails.put("class", paramMatcher.group(1));
            paramDetails.put("component", paramMatcher.group(2));
            paramDetails.put("value", paramMatcher.group(4));
            paramList.add(paramDetails);
        }
        parsedMethod.put("name", name);
        parsedMethod.put("return", returnValue);
        parsedMethod.put("httpMethod", httpMethod);
        parsedMethod.put("httpHeader", httpHeader);
        parsedMethod.put("url", url);
        parsedMethod.put("paramList", paramList);
        System.out.println(parsedMethod);

        return parsedMethod;
    }
}