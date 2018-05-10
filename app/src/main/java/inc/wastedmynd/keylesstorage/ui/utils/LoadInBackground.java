/**
 * <div>
 * <p>
 * created on 11 Apr 2015 at 8:51:36 AM
 * </p>
 * </div>
 * Copyright &#xfffd; 2015 wastedMind Media Projects.
 * <p>
 * Copyright . All Rights Reserved.
 * </p>
 *
 * <div>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * </div>
 *
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * </p>
 *
 * <div>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </div>
 *
 * <div>
 * <p>
 * email at
 * </p>
 * <p>
 * kin.afro@gmail.com
 * </p>
 * </div>
 *
 * @author sizwe
 */

package inc.wastedmynd.keylesstorage.ui.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * <div> <a field="Copyrights"></a> <h3>Copyright</h3>
 * <p>
 * created on 11 Apr 2015 at 8:51:36 AM
 * </p>
 * </div>
 * <p/>
 * <p>
 * Copyright &#xfffd; 2015 wastedMind Media Projects. All Rights Reserved.
 * </p>
 * <p/>
 * <div> <a field="ContactInfo"></a>
 * <h3>Contact Information</h3>
 * <p/>
 * <p>
 * Email : kin.afro@gmail.com
 * </p>
 * </div>
 * <p/>
 * <div> <a field="ImportantNote"></a>
 * <h3>Important Note</h3>
 * <p>
 * <!--Note-->
 * </p>
 * </div>
 * 
 * @author sizwe
 */
public class LoadInBackground extends AsyncTask<ImageTools, BitmapDrawable, Void>
{
    private volatile WeakReference<View> viewReference;
    private volatile ImageTools mTools;
    
    // Decode image in background.
    @Override
    protected Void doInBackground(ImageTools... params)
    {
        for (ImageTools tools : params)
        {
            mTools = tools;
            // Use a WeakReference to ensure the View can be garbage collected
            viewReference = new WeakReference<View>(mTools.getView());
            
            doInBackground();
        }
        
        return null;
    }
    
    private synchronized void doInBackground()
    {
        Bitmap result = null;
        BitmapDrawable drawable;
        
        boolean hasDimensionRequest;
        
        hasDimensionRequest = (mTools.getReqHeight() > 0 && mTools.getReqWidth() > 0);
        
        if (mTools.getResources() != null)
        {
            if (mTools.getResId() > ImageTools.INVALID_RESOURCE_ID)
            {
                result = BitmapFactory.decodeResource(mTools.getResources(), mTools.getResId());
                
                if (hasDimensionRequest)
                
                result = ImageTools.decodeSampledBitmapFromResource(mTools.getResources(), mTools.getResId(),
                        mTools.getReqWidth(), mTools.getReqHeight());
                
            }
        }
        else if (!TextUtils.isEmpty(mTools.getImageLocation()))
        {
            result = BitmapFactory.decodeFile(mTools.getImageLocation());
            
            if (hasDimensionRequest)
            
            if (result != null) result = ImageTools.decodeSampledBitmapFromFile(new File(mTools.getImageLocation()),
                    mTools.getReqWidth(), mTools.getReqHeight());
            
        }
        else if (mTools.getBytes() != null)
        {
            result = BitmapFactory.decodeByteArray(mTools.getBytes(), 0, mTools.getBytes().length);
            
            if (hasDimensionRequest)
            
            if (result != null) result = ImageTools.decodeSampledBitmapFromByte(mTools.getBytes(),
                    mTools.getReqWidth(), mTools.getReqHeight());
            
        }
        else if (mTools.getBitmap() != null)
        {
            result = mTools.getBitmap();
            
            if (hasDimensionRequest)
            
            result = ImageTools.decodeSampledBitmapFromBitmap(mTools.getBitmap(), mTools.getReqWidth(),
                    mTools.getReqHeight());
            
        }
        
        if (mTools.isCropped)
        {
            result = ImageTools.getCroppedBitmap(result);
        }
        
        if (mTools.isBlured)
        {
            result = ImageTools.BlurImage(viewReference.get().getContext(), result);
        }
        
        drawable = new BitmapDrawable(mTools.getView().getContext().getResources(),result);
        
        boolean isColorFilted = (mTools.getColorFilter() != null);
        
        if (isColorFilted) drawable.setColorFilter(mTools.getColorFilter());
        
        publishProgress(drawable);
        
    }
    
    public void doIn_UI_Thread()
    {
        doInBackground();
    }
    
    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onProgressUpdate(BitmapDrawable... drawable)
    {
        if (viewReference != null && drawable != null)
        {
            final View imageView = viewReference.get();
            if (imageView != null)
            {
                imageView.setBackground(drawable[0]);
            }
        }
        
    }
    
}
