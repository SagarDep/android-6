package com.bitlove.fetlife.view.navigation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.bitlove.fetlife.R
import com.bitlove.fetlife.getSafeColor
import com.bitlove.fetlife.inTransaction
import com.bitlove.fetlife.model.dataobject.wrapper.ExploreStory
import com.bitlove.fetlife.logic.dataholder.CardViewDataHolder
import com.bitlove.fetlife.logic.viewmodel.CardDetailViewModel
import com.bitlove.fetlife.model.dataobject.wrapper.Content
import com.bitlove.fetlife.model.dataobject.wrapper.ExploreEvent
import com.bitlove.fetlife.model.dataobject.wrapper.Favorite
import com.bitlove.fetlife.view.generic.CardDetailFragment
import com.bitlove.fetlife.view.generic.CardSwipeFragment
import com.bitlove.fetlife.view.generic.ResourceActivity
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.IconicsDrawable
import kotlinx.android.synthetic.main.include_appbar.*

class PhoneCardActivity : AppCompatActivity(), NavigationCallback {

    companion object {
        private const val EXTRA_CARD_TYPE = "EXTRA_CARD_TYPE"
        private const val EXTRA_CARD_IDS = "EXTRA_CARD_IDS"
        private const val EXTRA_SELECTED_POSITION = "EXTRA_SELECTED_POSITION"
        private const val EXTRA_SCREEN_TITLE = "EXTRA_SCREEN_TITLE"

        fun start(context: Context, cardList: List<CardViewDataHolder>, position: Int, screenTitle: String? = null) {
            val cardTemplate = cardList.firstOrNull()?: return
            val intent = Intent(context, PhoneCardActivity::class.java)
            val cardType = when (cardTemplate) {
                is Content -> CardDetailViewModel.CardType.CONTENT
                is ExploreStory -> CardDetailViewModel.CardType.EXPLORE_STORY
                is ExploreEvent -> CardDetailViewModel.CardType.EXPLORE_EVENT
                is Favorite -> CardDetailViewModel.CardType.FAVORITE
                else -> throw IllegalArgumentException()
            }
            intent.putExtra(EXTRA_CARD_TYPE, cardType)

            val cardIds = ArrayList<String>()
            for (card in cardList) {
                if (card == null) continue
                cardIds.add(card.getLocalId()!!)
            }
            intent.putExtra(EXTRA_CARD_IDS,cardIds)
            intent.putExtra(EXTRA_SELECTED_POSITION,position)
            intent.putExtra(EXTRA_SCREEN_TITLE,screenTitle)

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    private lateinit var cardType : CardDetailViewModel.CardType
    private var cardIds: ArrayList<String> = ArrayList()
    private var selectedPosition: Int = 0

//    override fun onResourceCreate(savedInstanceState: Bundle?) {
//        cardType = intent.getSerializableExtra(EXTRA_CARD_TYPE) as CardDetailViewModel.CardType
//        cardIds = intent.getStringArrayListExtra(EXTRA_CARD_IDS)
//        selectedPosition = intent.getIntExtra(EXTRA_SELECTED_POSITION,-1)
//
//        setContentView(R.layout.activity_phone_fragment)
//
//        setSupportActionBar(toolbar)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        supportActionBar!!.setHomeAsUpIndicator(IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_arrow_back).color(getSafeColor(R.color.toolbar_icon_color)).sizeDp(18))
//        supportActionBar!!.setDisplayShowHomeEnabled(true)
//
//        title = intent.getStringExtra(EXTRA_SCREEN_TITLE)?:title
//
////        if (savedInstanceState == null) {
//////            supportFragmentManager.inTransaction { replace(R.id.fragment_container, CardSwipeFragment.newInstance(cardType, cardIds,selectedPosition)) }
////            supportFragmentManager.inTransaction { replace(R.id.fragment_container, CardDetailFragment.newInstance(cardIds[0],cardType)) }
////        }
//    }

    override fun onCardNavigate(cardList: List<CardViewDataHolder>, position: Int, screenTitle: String?) {
        if (cardList.firstOrNull() != null) {
            PhoneCardActivity.start(this, cardList, position, screenTitle)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}