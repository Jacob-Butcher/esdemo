package com.example.esDemo.es;

import com.example.esDemo.model.Product;

import java.util.List;

public interface EsService {

    List<Product> searchByKeyword();
}
