package com.routing


class DeliveryBoy(order: Order, maxTime: Int)(implicit graph: GraphSupport) {

  def isCapableOfDelivery(order: Order, toShopTime: Int): Boolean = {
    val deliveryTime: Int = graph.getOrderDeliveryTime(order)
    deliveryTime + toShopTime <= maxTime
  }
}
