package azynias.study.ObjectModels;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Albedo on 7/20/2017.
 */

public class KanjiDetailsWrapper implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<Kanji> kanjiDetails;

    public KanjiDetailsWrapper(ArrayList<Kanji> items) {
        this.kanjiDetails = items;
    }

    public ArrayList<Kanji> getItemDetails() {
        return kanjiDetails;
    }
}
