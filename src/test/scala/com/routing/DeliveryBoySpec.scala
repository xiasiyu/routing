package com.routing

import org.scalatest._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.neo4j.graphdb.factory.GraphDatabaseFactory
import scala.reflect.io.Path
import scala.util.Try

@RunWith(classOf[JUnitRunner])
class DeliveryBoySpec extends FunSpec with Matchers with BeforeAndAfterAll {

  implicit val graph = new GraphDatabaseFactory().newEmbeddedDatabase("test_routing")

  private var graphSupport: GraphSupport = _

  override protected def beforeAll() = {
    graphSupport = new GraphSupport(graph)
  }

  override protected def afterAll() = {
    graphSupport.close()
    val path = Path("test_routing")
    Try(path.deleteRecursively())
  }

  describe("only one shop") {

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

      val boy = new DeliveryBoy(maxDeliveryTime)(graphSupport)

      boy.isCapableOfDelivery(order, timeFromDeliveryBoyToShop) should be(true)

    }

    it("multiple order for one shop and one delivery boy") {
      //需要在计算过程中知道某两个位置之间时间
      //见GraphSupport: 39行

      val shop = "Coco"
      val destination1 = "神华大厦"
      val destination2 = "神华大厦2"
      val destination3 = "神华大厦3"

      val timeFromDeliveryBoyToShop = 2
      val timeFromShopToDestination1 = 3
      val timeFromShopToDestination2 = 5
      val timeFromShopToDestination3 = 7


      val maxTime = 10

      val order1 = new Order(shop, destination1, timeFromShopToDestination1, 1)(graphSupport)
      val order2 = new Order(shop, destination2, timeFromShopToDestination2, 1)(graphSupport)
      val order3 = new Order(shop, destination3, timeFromShopToDestination3, 1)(graphSupport)

      val boy = new DeliveryBoy(maxTime)(graphSupport)
      val orders = List(order1, order2, order3)

      boy.delivery(orders, timeFromDeliveryBoyToShop) should be(List(shop, destination1, destination2))
    }
  }
}
