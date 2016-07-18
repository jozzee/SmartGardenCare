package com.sitthiphong.smartgardencare.listener;

import com.sitthiphong.smartgardencare.bean.SubscribeBean;

/**
 * Created by jozze on 17/7/2559.
 */
public class SubscribeCallBackListener {
    public static OnSubscribeRawDataListCallBackListener onSubscribeRawDataListCallBackListener = null;

    public interface OnSubscribeRawDataListCallBackListener{
        public void onSubRawDataListCallBackListener(SubscribeBean subscribeBean);
    }
    public void setOnSubscribeRawDataListCallBackListener(OnSubscribeRawDataListCallBackListener listener){
        onSubscribeRawDataListCallBackListener = listener;
    }

}
