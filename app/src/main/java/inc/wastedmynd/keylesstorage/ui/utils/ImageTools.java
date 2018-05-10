package inc.wastedmynd.keylesstorage.ui.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.graphics.ColorUtils;
import android.util.Log;
import android.view.View;

import java.io.File;

public class ImageTools
{
    public static ColorFilter getGrayScale()
    {
        ColorMatrix grayMatrix = new ColorMatrix();
        grayMatrix.setSaturation(0);
        ColorMatrixColorFilter grayscaleFilter = new ColorMatrixColorFilter(grayMatrix);
        return grayscaleFilter;
    }

    public static ColorFilter getNormalScale()
    {
        ColorMatrix grayMatrix = new ColorMatrix();
        grayMatrix.setSaturation(1.0f);
        ColorMatrixColorFilter grayscaleFilter = new ColorMatrixColorFilter(grayMatrix);
        return grayscaleFilter;
    }

    public static ColorFilter getNegitiveScale()
    {
        ColorMatrix grayMatrix = new ColorMatrix();
        grayMatrix.setSaturation(-0.1f);

        ColorMatrixColorFilter grayscaleFilter = new ColorMatrixColorFilter(grayMatrix);
        return grayscaleFilter;
    }

    @SuppressLint("NewApi")
    public static Bitmap getCroppedBitmap(Bitmap bitmap)
    {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);

        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);

        int halfWidth = bitmap.getWidth() / 2;
        int halfHeight = bitmap.getHeight() / 2;

        canvas.drawCircle(halfWidth, halfHeight, Math.max(halfWidth, halfHeight), paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    @SuppressLint("NewApi")
    public static Bitmap BlurImage(Context context, Bitmap input)
    {
        try
        {
            RenderScript rsScript = RenderScript.create(context);
            Allocation alloc = Allocation.createFromBitmap(rsScript, input);

            ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rsScript, Element.U8_4(rsScript));
            blur.setRadius(21);
            blur.setInput(alloc);

            Bitmap result = Bitmap.createBitmap(input.getWidth(), input.getHeight(), Config.ARGB_8888);
            Allocation outAlloc = Allocation.createFromBitmap(rsScript, result);

            blur.forEach(outAlloc);
            outAlloc.copyTo(result);

            rsScript.destroy();
            return result;
        }
        catch (Exception e)
        {
            return input;
        }

    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
    {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth)
        {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight)
    {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, resId, options), reqWidth, reqHeight, false);
    }

    public static Bitmap decodeSampledBitmapFromFile(File res, int reqWidth, int reqHeight)
    {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(res.getPath(), options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return Bitmap.createScaledBitmap(BitmapFactory.decodeFile(res.getPath(), options), reqWidth, reqHeight, false);
    }

    public static Bitmap decodeSampledBitmapFromByte(byte[] bytes, int reqWidth, int reqHeight)
    {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options), reqWidth, reqHeight, false);
    }

    public static Bitmap decodeSampledBitmapFromBitmap(Bitmap bitmap, int reqWidth, int reqHeight)
    {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, false);
    }

    // object declarations
    boolean isCropped = false;
    boolean isBlured = false;
    private ColorFilter colorFilter;
    private int reqWidth = 0, reqHeight = 0;
    private Bitmap mBitmap;
    private String mImageLocation;
    private Resources mResources;
    private int mResId;
    private byte[] mBytes;
    private View view;

    public static final int INVALID_RESOURCE_ID = -1;

    public void setImageLocation(String mImageLocation)
    {
        this.mImageLocation = mImageLocation;
    }

    public String getImageLocation()
    {
        return mImageLocation;
    }

    public void setResources(Resources mResources)
    {
        this.mResources = mResources;
    }

    public Resources getResources()
    {
        return mResources;
    }

    public int getResId()
    {
        return mResId;
    }

    public void setResId(int mResId)
    {
        this.mResId = mResId;
    }

    public void setBitmap(Bitmap mBitmap)
    {
        this.mBitmap = mBitmap;
    }

    public Bitmap getBitmap()
    {
        return mBitmap;
    }

    public int getReqHeight()
    {
        return reqHeight;
    }

    public void setReqHeight(int reqHeight)
    {
        this.reqHeight = reqHeight;
    }

    public int getReqWidth()
    {
        return reqWidth;
    }

    /**
     * @return the mBytes
     */
    public byte[] getBytes()
    {
        return mBytes;
    }

    /**
     * @param mBytes the mBytes to set
     */
    public void setBytes(byte[] mBytes)
    {
        this.mBytes = mBytes;
    }

    public void setReqWidth(int reqWidth)
    {
        this.reqWidth = reqWidth;
    }

    public void setColorFilter(ColorFilter colorFilter)
    {
        this.colorFilter = colorFilter;
    }

    public ColorFilter getColorFilter()
    {
        return colorFilter;
    }

    public void setView(View view)
    {
        this.view = view;
    }

    public View getView()
    {
        return view;
    }

    public void setCropped(boolean isCropped)
    {
        this.isCropped = isCropped;
    }

    public void setBlured(boolean isBlured)
    {
        this.isBlured = isBlured;
    }

    public ImageTools(View view, byte[] bytes)
    {
        setView(view);
        setBytes(bytes);
    }

    public ImageTools(View view, Resources res, int resId)
    {
        setView(view);
        setResources(res);
        setResId(resId);
    }

    public ImageTools(View view, Bitmap bitmap)
    {
        setView(view);
        setBitmap(bitmap);
    }

    public ImageTools(View view, String imageLocation)
    {
        setView(view);
        setImageLocation(imageLocation);
    }

    public void loadMyImageInTheBackground()
    {
        new LoadInBackground().execute(this);
    }

    public void loadMyImageInThe_UI_Thread()
    {
        new LoadInBackground().doIn_UI_Thread();
    }

    public static Canvas tintBitmap(Context context, Bitmap source, @ColorRes int resColorId, @ColorInt int color)
    {
        int mColor;

        try
        {
            mColor = context.getResources().getColor(resColorId);
        }
        catch (Exception e)
        {
            mColor = color;
        }

        Canvas canvas = new Canvas();

        try
        {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

            PorterDuffColorFilter colorFilter;

            colorFilter = new PorterDuffColorFilter(mColor, Mode.SRC_IN);

            paint.setColorFilter(colorFilter);

            canvas.drawBitmap(source, 0, 0, paint);
        }
        catch (Exception e)
        {
            Log.e("tintBitmap", e.toString());

            e.printStackTrace();
        }

        return canvas;
    }


    public static int getTextColorOnLuminance(int color)
    {
        int rColor;
        if (ColorUtils.calculateLuminance(color) < 0.5)
        {
            rColor = ColorUtils.compositeColors(Color.WHITE, color);

        }
        else
        {
            rColor = ColorUtils.compositeColors(Color.BLACK, color);
        }

        return rColor;
    }

    public static int getRandomInverseColor(final int fromColor)
    {
        int toColor;
        do
        {
            toColor = ImageTools.generateColor();

            if (ColorUtils.calculateLuminance(fromColor) < 0.5)//light tune
            {
                if (ColorUtils.calculateLuminance(toColor) < 0.5) toColor = fromColor;
            }
            else if (ColorUtils.calculateLuminance(toColor) > 0.5) //dark tune
                toColor = fromColor;

        } while (toColor == fromColor);

        return toColor;
    }


    /**
     * @return integer color,
     * generated based on {@code RandomUtils.randInt()};
     * that's passed to {@code Color.rgb()} params.
     **/
    public static int generateColor()
    {
        final int range = 255;

        int rRed = RandomUtils.randomInt(range),
                rGreen = RandomUtils.randomInt(range),
                rBlue = RandomUtils.randomInt(range);

        return Color.rgb(rRed, rGreen, rBlue);
    }
}
