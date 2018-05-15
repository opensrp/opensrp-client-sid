package shared.customshadows;

import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowTextView;
import org.smartregister.view.customcontrols.CustomFontTextView;

@Implements(CustomFontTextView.class)
public class FontTextViewShadow extends ShadowTextView {

//    public void customFontTextView(Context context, AttributeSet attrs, int defStyle) {
//         do nothing
//    }

//    public void setFontVariant(final FontVariant variant) {
//        // do nothing
//    }
}
