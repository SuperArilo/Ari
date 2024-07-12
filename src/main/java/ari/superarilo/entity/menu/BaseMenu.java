package ari.superarilo.entity.menu;

import ari.superarilo.entity.menu.home.RenderItem;

import java.util.List;
import java.util.Map;

public class BaseMenu {
    private String title;
    private List<String> layout;
    private Map<String, RenderItem> items;
    public BaseMenu() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getLayout() {
        return layout;
    }

    public void setLayout(List<String> layout) {
        this.layout = layout;
    }

    public Map<String, RenderItem> getItems() {
        return items;
    }

    public void setItems(Map<String, RenderItem> items) {
        this.items = items;
    }
}
