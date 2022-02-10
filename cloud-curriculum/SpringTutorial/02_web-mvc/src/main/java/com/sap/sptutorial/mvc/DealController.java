package com.sap.sptutorial.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("deal")
public class DealController {
	private DealRepository dealRepository;

	@Autowired
	public DealController(DealRepository dealRepository) {
		this.dealRepository = dealRepository;
	}

	@GetMapping
	public String getDeals(Model model) {
		model.addAttribute("title", "Hello Walldorf!");
		model.addAttribute("deals", dealRepository.findDeals(0, Integer.MAX_VALUE));
		model.addAttribute("newdeal", Deal.builder().build());
		return "deals";
	}

	@PostMapping
	public String createDeal(@ModelAttribute Deal newdeal) {
		dealRepository.add(newdeal);
		return "redirect:/deal";
	}
}
