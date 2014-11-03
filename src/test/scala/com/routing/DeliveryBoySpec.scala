package com.routing

import org.scalatest.{BeforeAndAfter, Matchers, FunSpec}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.neo4j.graphdb.factory.GraphDatabaseFactory

@RunWith(classOf[JUnitRunner])
class DeliveryBoySpec extends FunSpec with Matchers with BeforeAndAfter {

  implicit val graph = new GraphDatabaseFactory().newEmbeddedDatabase("test_routing")

  private var graphSupport: GraphSupport = _

  before {
    graphSupport = new GraphSupport(graph)
  }

  after {
    graphSupport.close()
  }

  describe("only one order") {

    it("should return false if the distance from destination to shop is less than 20 minutes") {
      //需要的输入:
      //1.快递员当前位置到商家的时间
      //2.订单商家到送货地址的时间
      //3.最大送货时间
      //4.商家名字,每个商家在系统中保持唯一
      //5.送货地址名字

      val shop = "Coco"
      val destination = "神华大厦"
      val timeFromDeliveryBoyToShop = 2
      val timeFromShopToDestination = 3

      val maxDeliveryTime = 6

      val order = new Order(shop, destination, timeFromShopToDestination, 5)(graphSupport) //5:订单数量

      val boy = new DeliveryBoy(order, maxDeliveryTime)(graphSupport)

      boy.isCapableOfDelivery(order, timeFromDeliveryBoyToShop) should be(true)

    }
  }
}
