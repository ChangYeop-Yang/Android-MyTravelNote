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
package com.kakao.internal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import com.kakao.KakaoParameterException;

/**
 * link, button, image type click시 취할 action
 */

public class Action {
    public enum ACTION_TYPE {
        WEB("web"),
        APP("app");
        private final String value;

        ACTION_TYPE(String value) {
            this.value = value;
        }
    }

    private ACTION_TYPE type;
    private AppActionInfo[] appActionInfos;
    private String url;


    private Action(final ACTION_TYPE type, final String url, final AppActionInfo[] appActionInfos) throws KakaoParameterException {
        if (type == null) {
            throw new KakaoParameterException(KakaoParameterException.ERROR_CODE.CORE_PARAMETER_MISSING, "action needs type.");
        }
        this.type = type;

        if (type == ACTION_TYPE.WEB  && !TextUtils.isEmpty(url)) {
            this.url = url;
        }

        if (type == ACTION_TYPE.APP && !(appActionInfos == null || appActionInfos.length == 0)) {
            this.appActionInfos = appActionInfos;
        }
    }

    public static Action newActionApp(final AppActionInfo[] appActionInfos) throws KakaoParameterException {
        return new Action(ACTION_TYPE.APP, null, appActionInfos);
    }

    public static Action newActionWeb(final String url) throws KakaoParameterException {
        return new Action(ACTION_TYPE.WEB, url, null);
    }

    public JSONObject createJSONObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(KakaoTalkLinkProtocol.ACTION_TYPE, type.value);
        if (url != null) {
            json.put(KakaoTalkLinkProtocol.ACTION_URL, url);
        }
        if (appActionInfos != null) {
            JSONArray jsonObjs = new JSONArray();
            for (AppActionInfo obj : appActionInfos) {
                jsonObjs.put(obj.createJSONObject());
            }
            json.put(KakaoTalkLinkProtocol.ACTION_ACTIONINFO, jsonObjs);
        }
        return json;
    }
}
