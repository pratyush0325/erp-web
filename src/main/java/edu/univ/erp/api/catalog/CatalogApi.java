package edu.univ.erp.api.catalog;

import edu.univ.erp.data.CatalogStore;
import edu.univ.erp.domain.CatalogItem;
import java.util.List;

public class CatalogApi {

    private final CatalogStore catalogStore = new CatalogStore();

    /**
     * Gets the full list of catalog items for the UI.
     *
     * @return A list of CatalogItem objects.
     */
    public List<CatalogItem> getCatalog() {
        // For now, this is a simple pass-through.
        // Later, this is where you could add caching or other logic.
        return catalogStore.getCatalogItems();
    }
}