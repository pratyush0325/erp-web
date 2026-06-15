package edu.univ.erp.api.catalog;

import edu.univ.erp.data.CatalogStore;
import edu.univ.erp.domain.CatalogItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogApi {

    private final CatalogStore catalogStore;

    public CatalogApi(CatalogStore catalogStore) {
        this.catalogStore = catalogStore;
    }

    public List<CatalogItem> getCatalog() {
        return catalogStore.getCatalogItems();
    }
}
