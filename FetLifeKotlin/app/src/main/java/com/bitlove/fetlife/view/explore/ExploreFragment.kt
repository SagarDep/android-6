package com.bitlove.fetlife.view.explore

import com.bitlove.fetlife.R
import com.bitlove.fetlife.databinding.FragmentCardListBinding
import com.bitlove.fetlife.model.dataobject.wrapper.ExploreStory
import com.bitlove.fetlife.view.generic.CardListFragment
import com.bitlove.fetlife.viewmodel.explore.ExploreViewModel

class ExploreFragment : CardListFragment<ExploreStory, FragmentCardListBinding, ExploreViewModel>() {

    override fun getLayoutRes(): Int {
        return R.layout.fragment_card_list
    }

    override fun getViewModelClass(): Class<ExploreViewModel> {
        return ExploreViewModel::class.java
    }
}