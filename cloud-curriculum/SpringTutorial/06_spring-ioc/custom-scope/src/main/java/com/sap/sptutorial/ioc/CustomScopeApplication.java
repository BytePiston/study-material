package com.sap.sptutorial.ioc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.devtools.filewatch.ChangedFile;
import org.springframework.boot.devtools.filewatch.ChangedFiles;
import org.springframework.boot.devtools.filewatch.FileChangeListener;
import org.springframework.boot.devtools.filewatch.FileSystemWatcher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.yaml.snakeyaml.Yaml;

import com.sap.sptutorial.ioc.model.Customer;
import com.sap.sptutorial.ioc.model.Purchase;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomScopeApplication {
	private static Map<String, Purchase> purchases = new HashMap<>();

	public static void main(String[] args) {
		if (args.length != 1)
			return;

		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
			PurchaseScope purchaseScope = new PurchaseScope();
			context.getBeanFactory().registerScope("purchase", purchaseScope);
			context.register(Purchase.class);
			context.register(Customer.class);
			context.refresh();

			listenToPuchases(args[0], purchaseScope, context);
		}
	}

	private static void listenToPuchases(String path, PurchaseScope purchaseScope, ApplicationContext context) {
		FileSystemWatcher fileSystemWatcher = new FileSystemWatcher(true, 1000, 200);
		fileSystemWatcher.addListener(new FileChangeListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onChange(Set<ChangedFiles> changedFilesSet) {
				changedFilesSet.forEach(changedFiles -> {
					changedFiles.forEach(changedFile -> {
						ChangedFile.Type type = changedFile.getType();
						String name = changedFile.getRelativeName();
						log.info("change detected name: {}, type: {}", name, type);
						switch (type) {
						case ADD:
							Purchase purchase = context.getBean(Purchase.class, name);
							Yaml yaml = new Yaml();
							try {
								Map<String, Object> content = (Map<String, Object>) yaml
										.load(new FileReader(changedFile.getFile()));
								purchase.initFromYaml(content);
								purchases.put(name, purchase);
								log.info("Purchase: {}", purchase);
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							}
							break;
						case DELETE:
							if (purchases.containsKey(name)) {
								context.getAutowireCapableBeanFactory().destroyBean(purchases.get(name));
							}
							break;
						case MODIFY:
							if (purchases.containsKey(name)) {

							}
							break;
						}
					});
				});
			}
		});
		fileSystemWatcher.addSourceFolder(new File(path));
		fileSystemWatcher.start();
		while (true)
			;
	}
}
