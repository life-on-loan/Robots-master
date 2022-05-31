package localization;

import java.util.*;
import java.util.function.Consumer;

public class LanguageManager {
    private static final String resource = "LanguageResource";
    private final List<ResourceItem> items;
    private static ResourceBundle resourceBundle;

    public LanguageManager() {
        items = new LinkedList<>();
        resourceBundle = ResourceBundle.getBundle(resource, Locale.getDefault());
    }
    public Locale getLocale(){
        return Locale.getDefault();
    }

    public void setLocale(Locale locale){
        Locale.setDefault(locale);
    }

    public void bindField(String key, Consumer<String> setter) {
        resourceBundle = ResourceBundle.getBundle(resource, getLocale());
        var item = new ResourceItem(key, setter);
        items.add(item);
        updateItem(item);
    }

    public String getString(String key) {
        resourceBundle = ResourceBundle.getBundle(resource, getLocale());
        return resourceBundle.getString(key);
    }

    public void changeLocale(Locale locale) {
        setLocale(locale);
        resourceBundle = ResourceBundle.getBundle(resource, locale);
        items.forEach(this::updateItem);
    }

    private void updateItem(ResourceItem item) {
        resourceBundle = ResourceBundle.getBundle(resource, getLocale());
        item.setter.accept(getString(item.key));
    }

    private static class ResourceItem {
        private final String key;
        private final Consumer<String> setter;

        private ResourceItem(String key, Consumer<String> setter) {
            this.key = key;
            this.setter = setter;
        }
    }
}
