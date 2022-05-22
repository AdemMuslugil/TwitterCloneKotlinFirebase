package com.adem.twitterclonekotlinfirebase.model

import android.net.Uri
import com.google.firebase.Timestamp

class TwitterModel (

    var mail:String,
    var tweet:String,
    var date:Timestamp,
    var downloadUrl: String?,
    var tweetOwner:String?
        )
