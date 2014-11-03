package com.routing


class Order(val shop: String, val destination: String, val timeFromShopToDestination: Int, val numbers: Int)(implicit graph: GraphSupport) {
  graph.setOrder(shop, destination, timeFromShopToDestination, numbers)

}
