/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.diegoferreiracaetano.sample.output;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;



import java.io.IOException;

import br.com.diegoferreiracaetano.output.OutputDriver;

/**
 * Skeleton of the main Android Things activity. Implement your device's logic
 * in this class.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 *
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 */
public class OutputActivity extends Activity {
    private static final String TAG = OutputActivity.class.getSimpleName();
    private OutputDriver mOutputRepeat;
    private OutputDriver mOutputTimeout;
    private int cont = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mOutputRepeat = new OutputDriver(BoardDefaults.getGPIOForLEDRepeat(), KeyEvent.KEYCODE_0);
            mOutputRepeat.setDebounceDelay(1000);
            mOutputRepeat.register();
            sendTurn();

            mOutputTimeout = new OutputDriver(BoardDefaults.getGPIOForLEDTimeout(), KeyEvent.KEYCODE_1);
            mOutputTimeout.register();
            sendTimeout();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendTimeout(){
        mOutputTimeout.toggleTimeOut(2000);
    }

    private void sendTurn(){
        mOutputRepeat.toggleRepeat();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        //event from repetion
        if (keyCode == KeyEvent.KEYCODE_0 && event.getRepeatCount() == 0) {

            //break repetion 10
            if(cont == 10){
                mOutputRepeat.removeCallbackRepetion();
            }
            Log.d(TAG,"turn on "+cont);
            cont++;
            return true;
        }


        if (keyCode == KeyEvent.KEYCODE_1 && event.getRepeatCount() == 0) {

            //turn off led 2000 miliseconds
            mOutputTimeout.removeCallbackRepetion();
            return true;
        }


        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_0) {
            Log.d(TAG,"turn off");
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mOutputRepeat != null) {
            mOutputRepeat.unregister();
            try {
                mOutputRepeat.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing driver", e);
            } finally{
                mOutputRepeat = null;
            }
        }


        if (mOutputTimeout != null) {
            mOutputTimeout.unregister();
            try {
                mOutputTimeout.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing driver", e);
            } finally{
                mOutputTimeout = null;
            }
        }
    }
}
