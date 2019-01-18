package init.app.util;

import init.app.component.UTF8ResourceBundleControl;

import java.util.Locale;
import java.util.ResourceBundle;

public class TranslationUtil {
    final static public String RESOURCE_BUNDLE_EXCEPTION = "i18n.exception";
    final static public String DEFAULT_LANGUAGE_CODE = Locale.ENGLISH.getLanguage();

    final static private ResourceBundle.Control resourceControl = new UTF8ResourceBundleControl();

    public static String translateException(Locale locale, String resourceBundleKey) {
        return ResourceBundle.getBundle(RESOURCE_BUNDLE_EXCEPTION, locale, resourceControl).getString(resourceBundleKey);
    }
}
