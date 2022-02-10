package com.sap.sptutorial.auth;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("deal")
public class DealController {
	private DealRepository dealRepository;
	private Deal secretDeal;

	public DealRepository getDealRepository() {
		return dealRepository;
	}

	@Autowired
	public DealController(DealRepository dealRepository) {
		this.dealRepository = dealRepository;
		this.secretDeal = Deal.builder().id("Treckie").title("Captain Picard's Chair").dealPriceAmount(756.777)
				.normalPriceAmount(999.999).build();
	}

	@GetMapping
	public Collection<Deal> getAllDeals() {
		return dealRepository.findDeals(0, Integer.MAX_VALUE);
	}

	@GetMapping(path = "{id}")
	public Deal getDealById(@PathVariable("id") String id) {
		return dealRepository.findDeal(id);
	}

	@PostMapping
	public Deal createDeal(@RequestBody Deal newdeal) {
		dealRepository.add(newdeal);
		return newdeal;
	}

	@GetMapping(path = "secret")
	public Deal getSecretDeal() {
		return secretDeal;
	}
}
