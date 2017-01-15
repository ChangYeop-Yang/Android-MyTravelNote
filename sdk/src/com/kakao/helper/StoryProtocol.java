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

/**
 * Talk과 통신하기 위한 protocol
 * authorization을 talk을 통해 하는 경우
 * kakaolink를 사용하는 경우
 *
 * @author MJ
 */
public class StoryProtocol extends KakaoServiceProtocol{
    private static final int STORY_MIN_VERSION_SUPPORT_CAPRI = 80; // android 2.6.0
    private static final String INTENT_ACTION_LOGGED_IN_ACTIVITY = "com.kakao.story.intent.action.CAPRI_LOGGED_IN_ACTIVITY";

    public static Intent createLoggedInActivityIntent(final Context context, final String appKey, final String redirectURI) {
        Intent intent = new Intent()
                .setAction(INTENT_ACTION_LOGGED_IN_ACTIVITY)
                .addCategory(Intent.CATEGORY_DEFAULT)
                .putExtra(EXTRA_PROTOCOL_VERSION, PROTOCOL_VERSION)
                .putExtra(EXTRA_APPLICATION_KEY, appKey)
                .putExtra(EXTRA_REDIRECT_URI, redirectURI);
        return checkSupportedService(context, intent, STORY_MIN_VERSION_SUPPORT_CAPRI);
    }

    public static boolean existCapriLoginActivityInStory(final Context context) {
        Intent intent = new Intent()
            .setAction(INTENT_ACTION_LOGGED_IN_ACTIVITY)
            .addCategory(Intent.CATEGORY_DEFAULT);
        return checkSupportedService(context, intent, STORY_MIN_VERSION_SUPPORT_CAPRI) != null;
    }
}
