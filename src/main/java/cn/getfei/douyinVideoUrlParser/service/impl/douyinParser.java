package cn.getfei.douyinVideoUrlParser.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cn.getfei.douyinVideoUrlParser.entity.Video;
import cn.getfei.douyinVideoUrlParser.service.IParserService;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;

@Service
public class douyinParser implements IParserService{

	@Override
	public List<Video> parse(List<Video> videos) {
		List<Video> parsedVideos=new ArrayList<>();
		videos.stream().forEach(video->{
			String srcUrl=video.getSrcUrl();
			String urlProcess = urlProcess(srcUrl);
	        String desUrl = dyDecode(urlProcess);
	        video.setDesUrl(desUrl);
		});
		return parsedVideos;
	}
	
	private static final String DYAPI = "https://aweme.snssdk.com/web/api/v2/aweme/iteminfo/?item_ids=ITEM_IDS&dytk=DYTK";

    /**
     * tips:空串判断
     *
     * @Param str
     * @return
     */
    private static boolean isEmpty(Object str) {
        return str == null || "".equals(str);
    }

    /**
     * tips:是否有中文字符
     *
     * @param str
     * @return
     */
    private static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * tips:处理文字
     *
     * @param var
     * @return
     */
    private static final String urlProcess(String var) {
        if (var.contains("douyin") || var.contains("iesdouyin")) {
            if (!isContainChinese(var)) return var;
            int start = var.indexOf("http");
            int end = var.lastIndexOf("/");
            var = var.substring(start, end);
            return var;
        } else return "";
    }

    private static String getURI(String url) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpContext httpContext = new BasicHttpContext();
        HttpClientContext clientContext = HttpClientContext.adapt(httpContext);
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("user-agent", "Mozilla/5.0 (Linux; U; Android 5.0; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
        try {
            httpClient.execute(httpGet, httpContext);
            return clientContext.getTargetHost() + ((HttpUriRequest) clientContext.getRequest()).getURI().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private static String dyDecode(String var) {
        Document doc = null;
        try {
            doc = Jsoup.connect(var).cookie("cookie", "tt_webid=6711334817457341965; _ga=GA1.2.611157811.1562604418; _gid=GA1.2.1578330356.1562604418; _ba=BA0.2-20190709-51")
                    .header("user-agent", "Mozilla/5.0 (Linux; U; Android 5.0; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1")
                    .timeout(12138).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 解析网页标签
        Elements elem = doc.getElementsByTag("script");
        String url1 = elem.toString();
        //正则
        String aweme_id = "itemId: \"([0-9]+)\"";
        String dytk = "dytk: \"(.*)\"";
        Pattern r = Pattern.compile(aweme_id);
        Matcher m = r.matcher(url1);
        while (m.find()) {
            aweme_id = m.group().replaceAll("itemId: ", "").replaceAll("\"", "");
        }
        Pattern r1 = Pattern.compile(dytk);
        Matcher m1 = r1.matcher(url1);
        while (m1.find()) {
            dytk = m1.group().replaceAll("dytk: ", "").replaceAll("\"", "");
        }
        try {
            String result2 = HttpRequest.get(DYAPI.replaceAll("ITEM_IDS", aweme_id).replaceAll("DYTK", dytk))
                    .header(Header.USER_AGENT, "Mozilla/5.0 (Linux; U; Android 5.0; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1")
                    .timeout(12138)
                    .execute().body();
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = jsonParser.parse(result2).getAsJsonObject();
            return getURI(jsonObject.get("item_list").getAsJsonArray().get(0).getAsJsonObject().get("video").getAsJsonObject().get("play_addr").getAsJsonObject().get("url_list").getAsJsonArray().get(1).getAsString());
        } catch (Exception e) {
            return "";
        }
    }

}
