
import java.nio.file.Files;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONPath;

import me.hd.wauxv.plugin.api.callback.PluginCallBack;

void sendMusic(String talker, String title) {
    get("https://api.vkeys.cn/v2/music/netease?word=" + title + "&choose=1", null, new PluginCallBack.HttpCallback() {
        public void onSuccess(int respCode, String respContent) {
            JSONObject jsonObject = JSON.parseObject(respContent);
            String name = JSONPath.eval(jsonObject, "$.data.song");
            String singer = JSONPath.eval(jsonObject, "$.data.singer");
            String cover = JSONPath.eval(jsonObject, "$.data.cover");
            String link = JSONPath.eval(jsonObject, "$.data.link");
            String url = JSONPath.eval(jsonObject, "$.data.url");

            byte[] thumbData;
            download(cover, cacheDir + "/thumbImg.png", null, new PluginCallBack.DownloadCallback() {
                public void onSuccess(File file) {
                    thumbData = Files.readAllBytes(file.toPath());
                    shareMusic(talker, name, singer, link, url, thumbData, "wx8dd6ecd81906fd84");
                    file.delete();
                }
        
                public void onError(Exception e) {
                    thumbData = null;
                    shareMusic(talker, name, singer, link, url, thumbData, "wx8dd6ecd81906fd84");
                }
            });

        }

        public void onError(Exception e) {
            sendText(talker, "[落月API]点歌异常:" + e.getMessage());
        }
    });
}

void onHandleMsg(Object msgInfoBean) {
    if (msgInfoBean.isText()) {
        String content = msgInfoBean.getContent();
        String talker = msgInfoBean.getTalker();
        if (content.startsWith("/点歌 ")) {
            String title = content.substring(4);
            sendMusic(talker, title);
        }
    }
}
