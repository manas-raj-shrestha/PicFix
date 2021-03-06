package com.hackathon.picfix.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorFilter;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hackathon.picfix.filters.Blur;
import com.hackathon.picfix.filters.Brightness;
import com.hackathon.picfix.filters.Flea;
import com.hackathon.picfix.transform.FlipImage;
import com.hackathon.picfix.transform.RotationEffect;
import com.hackathon.picfix.filters.Shading;
import com.hackathon.picfix.filters.Sketch;
import com.hackathon.picfix.filters.Snow;
import com.hackathon.picfix.filters.TintImage;
import com.hackathon.picfix.filters.WaterMark;
import com.hackathon.picfix.filters.BlackFilter;
import com.hackathon.picfix.filters.Hue;
import com.hackathon.picfix.filters.Saturation;
import com.hackathon.picfix.interfaces.CustomFrameInterface;
import com.hackathon.picfix.interfaces.PicFixViewInterface;
import com.hackathon.picfix.utils.BitmapBuilder;

import java.io.ByteArrayOutputStream;

/**
 * extends {@link ImageView} class to apply all the image editor function
 * {@link PicFixViewInterface} is implemented to pass all the editor function
 */
public class PicFixImageView extends ImageView implements PicFixViewInterface, CustomFrameInterface {

    private Integer[] mFrames;
    private RelativeLayout.LayoutParams mOverlayParams;
    private PicFixImageView imgOverlayFrame;
    private float blurRadius = 1;
    private final Drawable drawable;
    private final Context context;
    private final Bitmap definedBitmap;

    public PicFixImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        drawable = getDrawable();
        this.context = context;
        definedBitmap = ((BitmapDrawable) this.getDrawable()).getBitmap();
    }


    @Override
    public void setRotationTo(float rotationDegree) {
        Bitmap rotatedBitmap = RotationEffect.getRotatedBitmap(definedBitmap, rotationDegree);
        this.setImageBitmap(rotatedBitmap);
    }

    @Override
    public void setBlur(float radius) {

        if (radius < 1) {
            this.blurRadius = 1;
        } else {
            this.blurRadius = radius;
        }
        Bitmap blurredBitmap = Blur.blur(context, definedBitmap, this.blurRadius);
        this.setImageBitmap(blurredBitmap);

    }

    @Override
    public void setBrightness(int brightnessValue) {
        Bitmap brightBitmap = Brightness.getBrightnessEffect(definedBitmap, brightnessValue);
        // return final image
        this.setImageBitmap(brightBitmap);
    }

    @Override
    public void setShading(int shadingColor) {

        Bitmap shadedBitmap = Shading.getShadingEffect(definedBitmap, shadingColor);
        this.setImageBitmap(shadedBitmap);

    }

    @Override
    public void applyBlackFilter() {

        Bitmap blackFiltered = BlackFilter.getBlackFilteredImage(definedBitmap);
        this.setImageBitmap(blackFiltered);

    }

    @Override
    public void applySaturationFilter(int saturationLevel) {

        Bitmap saturatedBitmap = Saturation.getSaturatedFilter(definedBitmap, saturationLevel);
        this.setImageBitmap(saturatedBitmap);
    }

    @Override
    public void applySnowEffect() {
        Bitmap snowEffectBitmap = Snow.getSnowEffectBitmap(definedBitmap);
        this.setImageBitmap(snowEffectBitmap);

    }

    @Override
    public void applyFleaEffect() {
        Bitmap fleaEffectBitmap = Flea.getFleaEffectBitmap(definedBitmap);
        this.setImageBitmap(fleaEffectBitmap);

    }

    @Override
    public void setTintImage(int tintDegree) {

        Bitmap tintedBitmap = TintImage.getTintImage(definedBitmap, tintDegree);
        this.setImageBitmap(tintedBitmap);
    }

    @Override
    public void flipImage(int flipType) {
        Bitmap flippedBitmap = FlipImage.getFlippedImage(definedBitmap, flipType);
        this.setImageBitmap(flippedBitmap);
    }

    @Override
    public void setWaterMark(String watermark, Point location, int color, int alpha, int size, boolean underline) {

        Bitmap waterMarkedBitmap = WaterMark.getWaterMarked(definedBitmap, watermark, location, color, alpha, size, underline);
        this.setImageBitmap(waterMarkedBitmap);
    }

    @Override
    public void setFrames(Integer[] framesId) {
        mFrames = framesId;
    }

    @Override
    public Integer[] getFrames() {
        return mFrames;
    }

    @Override
    public void createFrameOverlay(Context context, PicFixImageView frameImageView, PicFixImageView selectedImageView) {
//        mContext = context;
        imgOverlayFrame = frameImageView;
        mOverlayParams = new RelativeLayout.LayoutParams(selectedImageView.getWidth(), selectedImageView.getHeight());

        mOverlayParams.addRule(RelativeLayout.ALIGN_LEFT, selectedImageView.getId());
        mOverlayParams.addRule(RelativeLayout.ALIGN_RIGHT, selectedImageView.getId());
        mOverlayParams.addRule(RelativeLayout.ALIGN_TOP, selectedImageView.getId());
        mOverlayParams.addRule(RelativeLayout.ALIGN_BOTTOM, selectedImageView.getId());
        mOverlayParams.addRule(RelativeLayout.ABOVE, selectedImageView.getId());

        imgOverlayFrame.setImageResource(mFrames[0]);
        imgOverlayFrame.setScaleType(ScaleType.FIT_XY);
        imgOverlayFrame.setLayoutParams(mOverlayParams);

    }

    @Override
    public void setSelectedFrame(int position) {
        imgOverlayFrame.setImageBitmap(BitmapBuilder.decodeSampledBitmapFromResourcePreview(
                context.getResources(), mFrames[position]));
        imgOverlayFrame.setScaleType(ImageView.ScaleType.FIT_XY);
        imgOverlayFrame.setLayoutParams(mOverlayParams);

    }

    @Override
    public Bitmap getFramedBitmap(PicFixImageView selectedImageView, int position) {
        ByteArrayOutputStream bytesOfImage = new ByteArrayOutputStream();
        // compressing to 10 percent

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 2;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        Bitmap bitmapOverlayPicture = BitmapFactory.decodeResource(
                getResources(), mFrames[position], options);

        Bitmap bitmapSelectedPicture = ((BitmapDrawable) selectedImageView.getDrawable()).getBitmap();

        bitmapOverlayPicture = bitmapOverlayPicture.createScaledBitmap(bitmapOverlayPicture, bitmapSelectedPicture.getWidth(), bitmapSelectedPicture.getHeight(), true);

        Bitmap bitmap = BitmapBuilder.overlapBitmaps(bitmapSelectedPicture, bitmapOverlayPicture);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytesOfImage);

        return bitmap;
    }


    public void resizeImage(int width, int height) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(definedBitmap, width, height, true);

        this.setImageBitmap(resizedBitmap);
    }

    @Override
    public void doCrop(float startX, float startY, int width, int height) {
        Bitmap resizedBitmap = Bitmap.createBitmap(definedBitmap, (int) startX, (int) startY, width, height);

        this.setImageBitmap(resizedBitmap);
    }

    @Override
    public void sketch(int type, int threshold) {
        this.setImageBitmap(Sketch.changeToSketch(definedBitmap, type, threshold));
    }

    @Override
    public ColorFilter applyHue(int huelevel) {
        return Hue.adjustHue(huelevel);
    }
}
