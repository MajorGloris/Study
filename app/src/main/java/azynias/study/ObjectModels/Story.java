package azynias.study.ObjectModels;

import java.util.Date;

/**
 * Created by Albedo on 6/20/2017.
 */

public class Story {
    private String story;
    private Date date;

    public Story(String story, Date date) {
        this.story = story;
        this.date = date;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


}
