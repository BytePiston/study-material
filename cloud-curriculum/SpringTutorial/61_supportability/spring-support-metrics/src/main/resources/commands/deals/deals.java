package commands.deals;

import org.crsh.cli.*;
import org.crsh.command.*;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Component;

import com.sap.sptutorial.supportability.metrics.Deal;
import com.sap.sptutorial.supportability.metrics.DealController;

public class deals extends BaseCommand {

	@Command
	@Usage("Work with Deals")
	public String get(@Usage("Enter Deal Id") @Argument String dealId, InvocationContext context) {
		BeanFactory beanFactory = (BeanFactory) context.getAttributes().get("spring.beanfactory");
		DealController dealController = beanFactory.getBean(DealController.class);
		Deal deal;
		try {
			deal = dealController.getDealById(dealId);
		} catch (Exception ex) {
			return "deal not found";
		}
		return (deal != null) ? "deal: " + deal.toString() : "deal not found";
	}
}