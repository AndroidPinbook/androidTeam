package uur.com.pinbook.RecyclerView.Model;

import java.io.Serializable;

/**
 * Created by ASUS on 17.2.2018.
 */

public class FeedPinItem implements Serializable {

    private String name;
    private String url;
    private String description;

    public FeedPinItem() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
