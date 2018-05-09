package shared;

import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartregister.gizi.BuildConfig;

import shared.customshadows.FontTextViewShadow;

/**
 * Created by sid-tech on 2/23/18
 */

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, application = GiziApplicationTest.class, shadows = {FontTextViewShadow.class})
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
public abstract class BaseUnitTest {
    public static class INT_TEST_CONSTANTS {
//        public static final int INT_1 = 1;
//        public static final int INT_2 = 2;
//        public static final int INT_3 = 3;
    }

    public static class STRING_TEST_CONSTANTS {
//        public static final String EMPTY_STRING = "";
    }
}
