package inc.wastedmynd.keylesstorage.ui.utils;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import inc.wastedmynd.keylesstorage.R;


public class TextColorizer
{
    
    private int resid;
    
    private String text;
    
    public TextColorizer(String text, int resid)
    {
        setText(text);
        
        setResid(resid);
    }
    
    public String getText()
    {
        return text;
    }
    
    public void setText(String text)
    {
        this.text = text;
    }
    
    public int getResid()
    {
        return resid;
    }
    
    public void setResid(int resid)
    {
        this.resid = resid;
    }
    
    public static void setColorForeground(SpannableStringBuilder builder, int color, int start, int end)
    {
        builder.setSpan(new ForegroundColorSpan(color), start, end, 0);
    }

    public static void setColorForeground(SpannableStringBuilder builder, int color, int start)
    {
        builder.setSpan(new ForegroundColorSpan(color), start,builder.length(), 0);
    }

    public static void setColorForeground_ResourceColor(Context context, SpannableStringBuilder builder, int resid, int start)
    {
        setColorForeground_ResourceColor(context, builder, resid, start, builder.length());
    }
    
    public static void setColorForeground_ResourceColor(Context context, SpannableStringBuilder builder, int resid, int start, int end)
    {
        int color = (resid >= 0x01000000) ? context.getResources().getColor(resid) : context.getResources().getColor(
                R.color.colorPrimary);
        
        setColorForeground(builder, color, start, end);
    }
    
}
