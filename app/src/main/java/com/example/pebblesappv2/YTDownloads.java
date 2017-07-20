package com.example.pebblesappv2;

import io.realm.RealmList;
import io.realm.RealmObject;

public class YTDownloads extends RealmObject {
    private String video_title;
    private String video_id;
    private RealmList<YTTags> video_tags;
    private int id;

    public RealmList<YTTags> getVideo_tags() {
        return video_tags;
    }

    public void setVideo_tags(RealmList<YTTags> video_tags) {
        this.video_tags = video_tags;
    }

    public String getVideo_title() {
        return video_title;
    }

    public void setVideo_title(String video_title) {
        this.video_title = video_title;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
