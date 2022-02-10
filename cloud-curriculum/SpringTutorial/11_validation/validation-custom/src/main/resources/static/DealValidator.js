function checkDeal(deal) {
	return deal.availablePerCustomer && deal.numberAvailable
			&& deal.availablePerCustomer <= deal.numberAvailable;
}

function checkRedeemableFrom(redeemableFrom) {
	return redeemableFrom && redeemableFrom.getDayOfMonth() == 1;
}