package com.sap.icd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sap.icd.odata4.example.repo.CustomerRepository;

@RestController
@RequestMapping("ext")
public class ExtensionTest {
    @Autowired
    private CustomerRepository customerRepository;
}
