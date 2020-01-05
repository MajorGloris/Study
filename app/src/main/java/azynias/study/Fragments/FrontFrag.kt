package azynias.study.Fragments

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import azynias.study.DataHandlers.TierDBHandler
import azynias.study.DataHandlers.UserPreferences
import azynias.study.ObjectModels.Bracket
import azynias.study.R
import kotlinx.android.synthetic.main.fragment_front.*


class FrontFrag : Fragment() {
    var due = 0

    fun init() {
        due = TierDBHandler.getInstance(activity).itemsDueToday() // activity serves as context here
    }

    fun studyCard() {
        val bracket = UserPreferences.getInstance().studyBracket
        study_kanji_chars.text = bracket.toString()
        study_items.text = "You have: ${bracket.kanjiChars.size} items"
    }

    fun progCard() {
        val tier = UserPreferences.getInstance().tier
        val bracket = UserPreferences.getInstance().bracket

        title_progress_dashb_frag.text = "PROGRESS"
        items_overdue_dashb_frag.text = "$due items are due today."
        tierbracket_dashb_frag.text = "$tier $bracket"
        grade_dashb_frag.text = "Your grade is: ${UserPreferences.getInstance().grade}% efficiency "
    }

    fun reviewCard() {
        val chars: ArrayList<String> = UserPreferences.getInstance().reviewBracket.kanjiChars
        val characters = StringBuilder()

        chars.forEach {
            characters.append("$it ")
        }
        review_chars_dashb_frag.text = characters.toString()
        review_items.text = "${chars.size} items"
        due_review_items.text = "$due of which are due."
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_rage_comic_details, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        init()

    }
}