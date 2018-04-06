package com.bitlove.fetlife.model.dataobject.wrapper

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.Relation
import com.bitlove.fetlife.model.dataobject.SyncObject
import com.bitlove.fetlife.model.dataobject.entity.ContentEntity
import com.bitlove.fetlife.model.dataobject.entity.MemberEntity
import com.bitlove.fetlife.model.dataobject.entity.ReactionEntity
import com.bitlove.fetlife.model.db.dao.ContentDao
import com.bitlove.fetlife.viewmodel.generic.AvatarViewDataHolder
import com.bitlove.fetlife.viewmodel.generic.CardViewDataHolder
import com.bitlove.fetlife.viewmodel.generic.ReactionViewDataHolder

class Content : CardViewDataHolder(), SyncObject<ContentEntity> {

    enum class TYPE {CONVERSATION, PICTURE}

    @Embedded lateinit var contentEntity: ContentEntity

    @Relation(parentColumn = "memberId", entityColumn = "dbId", entity = MemberEntity::class)
    var ownerSingleItemList: List<MemberEntity>? = null

    @Relation(parentColumn = "dbId", entityColumn = "contentId", entity = ReactionEntity::class)
    var reactions: List<Reaction>? = null

    @Ignore var commentList: List<Reaction>? = null
    @Ignore var loveList: List<Reaction>? = null

    override fun getTitle(): String? {
        return contentEntity?.subject
    }

    override fun displayComments(): Boolean? {
        return when(contentEntity?.type) {
            TYPE.CONVERSATION.toString() -> true
            else -> false
        }
    }

    override fun getSupportingText(): String? {
        return contentEntity?.body
    }

    override fun getLocalId(): String? {
        return contentEntity?.dbId
    }

    override fun getRemoteId(): String? {
        return contentEntity?.networkId
    }

    override fun getAvatar(): AvatarViewDataHolder? {
        val memberEntity = ownerSingleItemList?.firstOrNull() ?: return null
        val member = Member(memberEntity)
        if (contentEntity?.type == TYPE.CONVERSATION.toString()) {
            member.subLine = contentEntity?.subject
        }
        return member
    }

    override fun getMediaUrl(): String? {
        return contentEntity?.pictureVariants?.huge?.url
    }

    override fun getMediaAspectRatio(): Float? {
        val picVariant = contentEntity?.pictureVariants?.huge
        return if (picVariant != null) {
            picVariant.width.toFloat() / picVariant.height.toFloat()
        } else {
            null
        }
    }

    override fun isDeletable(): Boolean? {
        return when(contentEntity?.type) {
            TYPE.CONVERSATION.toString() -> true
            else -> false
        }
    }

    override fun hasNewComment(): Boolean? {
        return contentEntity?.hasNewComments
    }

    override fun getCommentCount(): String? {
        return when(contentEntity?.type) {
            TYPE.CONVERSATION.toString() -> contentEntity?.messageCount?.toString()
            else -> contentEntity?.commentCount?.toString()
        }
    }

    override fun getComments(): List<Reaction> {
        if (commentList == null) {
            populateComments()
        }
        return commentList!!
    }

    override fun getLoves(): List<Reaction> {
        if (loveList == null) {
            populateLoves()
        }
        return loveList!!
    }

    override fun isLoved(): Boolean? {
        return contentEntity?.loved
    }

    override fun getLoveCount(): String? {
        return contentEntity?.loveCount?.toString()
    }

    private fun populateComments() {
        var reactions = reactions ?: return
        commentList = reactions.filter { it.reactionEntity?.type == Reaction.TYPE.COMMENT.toString() }
    }

    private fun populateLoves() {
        var reactions = reactions ?: return
        loveList = reactions.filter { it.reactionEntity?.type == Reaction.TYPE.LOVE.toString() }
    }

    override fun getEntity(): ContentEntity {
        return contentEntity
    }

    override fun getDao(): ContentDao {
        return getDataBase().contentDao()
    }

}