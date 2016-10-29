package com.sitthiphong.smartgardencare.libs;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

/**
 * Created by Sitthiphong on 10/29/2016 AD.
 */

public class BlurImage {
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public Bitmap make(Context context, Bitmap bitmap, int radius){

        try {
            bitmap = RGB565toARGB888(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Bitmap bitmapBlur = Bitmap.createBitmap(
                bitmap.getWidth(), bitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript renderScript = RenderScript.create(context);

        Allocation blurInput = Allocation.createFromBitmap(renderScript, bitmap);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmapBlur);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));

        blur.setInput(blurInput);
        blur.setRadius(radius); // radius must be 0 < r <= 25
        blur.forEach(blurOutput);

        blurOutput.copyTo(bitmapBlur);
        renderScript.destroy();
        return bitmapBlur;
    }
    public Bitmap RGB565toARGB888(Bitmap img) throws Exception {
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];

        //Get JPEG pixels.  Each int is the color values for one pixel.
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

        //Create a Bitmap of the appropriate format.
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        //Set RGB pixels.
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
    }
}
