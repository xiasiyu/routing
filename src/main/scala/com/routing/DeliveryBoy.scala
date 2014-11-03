package com.routing


class DeliveryBoy(maxTime: Int)(implicit graph: GraphSupport) {

  def isCapableOfDelivery(order: Order, toShopTime: Int): Boolean = {
    val deliveryTime: Int = graph.getOrderDeliveryTime(order)
    deliveryTime + toShopTime <= maxTime
  }

  def delivery(orders: List[Order], toShopTime: Int): List[String] = {
    graph.getCapableDeliveryOrder(orders, maxTime - toShopTime)
  }
}
