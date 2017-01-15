/**
 * Copyright 2014 Daum Kakao Corp.
 *
 * Redistribution and modification in source or binary forms are not permitted without specific prior written permission. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kakao.helper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Talk과 통신하기 위한 protocol
 * authorization을 talk을 통해 하는 경우
 * kakaolink를 사용하는 경우
 *
 * @author MJ
 */
public class TalkProtocol extends KakaoServiceProtocol{
    // capri login, kakaolink3.5
    private static final int TALK_MIN_VERSION_SUPPORT_CAPRI = 139; // android 4.2.0
    private static final String INTENT_ACTION_LOGGED_OUT_ACTIVITY = "com.kakao.talk.intent.action.CAPRI_LOGGED_OUT_ACTIVITY";
    private static final String INTENT_ACTION_LOGGED_IN_ACTIVITY = "com.kakao.talk.intent.action.CAPRI_LOGGED_IN_ACTIVITY";

    //request
    private static final String EXTRA_PROTOCOL_VERSION = "com.kakao.sdk.talk.protocol.version";
    private static final int PROTOCOL_VERSION = 1;

    private static final String EXTRA_APPLICATION_KEY = "com.kakao.sdk.talk.appKey";
    private static final String EXTRA_REDIRECT_URI = "com.kakao.sdk.talk.redirectUri";
    private static final String EXTRA_KA_HEADER = "com.kakao.sdk.talk.kaHeader";

    //response
    public static final String EXTRA_REDIRECT_URL = "com.kakao.sdk.talk.redirectUrl";
    public static final String EXTRA_ERROR_DESCRIPTION = "com.kakao.sdk.talk.error.description";
    public static final String EXTRA_ERROR_TYPE = "com.kakao.sdk.talk.error.type";

    public static Intent createLoggedOutActivityIntent(final Context context, final String appKey, final String redirectURI) {
        Intent intent = new Intent()
            .setAction(INTENT_ACTION_LOGGED_OUT_ACTIVITY)
            .addCategory(Intent.CATEGORY_DEFAULT)
            .putExtra(EXTRA_PROTOCOL_VERSION, PROTOCOL_VERSION)
            .putExtra(EXTRA_APPLICATION_KEY, appKey)
            .putExtra(EXTRA_REDIRECT_URI, redirectURI)
            .putExtra(EXTRA_KA_HEADER, SystemInfo.getKAHeader());
        return checkSupportedService(context, intent, TALK_MIN_VERSION_SUPPORT_CAPRI);
    }

    public static Intent createLoggedInActivityIntent(final Context context, final String appKey, final String redirectURI) {
        Intent intent = new Intent()
                .setAction(INTENT_ACTION_LOGGED_IN_ACTIVITY)
                .addCategory(Intent.CATEGORY_DEFAULT)
                .putExtra(EXTRA_PROTOCOL_VERSION, PROTOCOL_VERSION)
                .putExtra(EXTRA_APPLICATION_KEY, appKey)
                .putExtra(EXTRA_REDIRECT_URI, redirectURI)
                .putExtra(EXTRA_KA_HEADER, SystemInfo.getKAHeader());
        return checkSupportedService(context, intent, TALK_MIN_VERSION_SUPPORT_CAPRI);
    }

    public static boolean existCapriLoginActivityInTalk(final Context context) {
        Intent intent = new Intent()
            .setAction(INTENT_ACTION_LOGGED_IN_ACTIVITY)
            .addCategory(Intent.CATEGORY_DEFAULT);
        return checkSupportedService(context, intent, TALK_MIN_VERSION_SUPPORT_CAPRI) != null;
    }

    public static Intent createKakakoTalkLinkIntent(final Context context, final String linkMessage) {
        final Uri kakaoLinkUri = Uri.parse(linkMessage);
        final Intent intent = new Intent(Intent.ACTION_SEND, kakaoLinkUri);
        // kakaolink를 지원하는 kakaotalk이 install 되어 있는지.
        return checkSupportedService(context, intent, TalkProtocol.TALK_MIN_VERSION_SUPPORT_CAPRI);
    }
}
