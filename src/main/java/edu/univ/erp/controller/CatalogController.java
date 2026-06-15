package edu.univ.erp.controller;

import edu.univ.erp.api.catalog.CatalogApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final CatalogApi catalogApi;

    public CatalogController(CatalogApi catalogApi) {
        this.catalogApi = catalogApi;
    }

    @GetMapping
    public ResponseEntity<?> getCatalog() {
        return ResponseEntity.ok(catalogApi.getCatalog());
    }
}
