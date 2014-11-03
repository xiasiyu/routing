package com.routing

import org.neo4j.graphdb._
import org.neo4j.tooling.GlobalGraphOperations
import scala.collection.JavaConversions._

class GraphSupport(val graph: GraphDatabaseService) extends TransactionSupport {

  def findNode(name: String, label: Label) = {
    GlobalGraphOperations.at(graph).getAllNodesWithLabel(label).toList.find(_.getProperty("name") == name)
  }

  def createNode(name: String, label: Label): Node = {
    val node: Node = graph.createNode(label)
    node.setProperty("name", name)
    node
  }

  def setOrder(shop: String, destination: String, timeFromShopToDestination: Int, numbers: Int) = transaction(graph) {
    val shopNode = findNode(shop, Shop).getOrElse(createNode(shop, Shop))
    val destinationNode = findNode(destination, Destination)

    if(!destinationNode.isDefined) {
      val node = createNode(destination, Destination)
      val to = shopNode.createRelationshipTo(node, DeliveryPath)
      to.setProperty("time", timeFromShopToDestination)
      to.setProperty("numbers", numbers)
      buildDestinationPath(node)
    }
  }

  def buildDestinationPath(destination: Node) = {
    val dests = GlobalGraphOperations.at(graph).getAllNodesWithLabel(Destination).toList
    for {
      node <- dests
      if !node.getProperty("name").toString.equals(destination.getProperty("name").toString)
    } node.createRelationshipTo(destination, DeliveryPath).setProperty("time", 5)//TODO: 5替代任意两个送货地点之间的时间计算
  }

  def getOrderDeliveryTime(order: Order): Int = transaction(graph) {
    val shopNode = findNode(order.shop, Shop).get
    shopNode.getRelationships(Direction.OUTGOING, DeliveryPath).toList.find(_.getEndNode.getProperty("name") == order.destination).get.getProperty("time").toString.toInt
  }

  def getCapableDeliveryOrder(orders: List[Order], time: Int): List[String] = transaction(graph) {
    val shopNode = findNode(orders(0).shop, Shop).get
    val evaluator: DestinationEvaluator = new DestinationEvaluator(time)

    val list: List[Path] = graph.traversalDescription().depthFirst()
      .relationships(DeliveryPath)
      .evaluator(evaluator)
      .traverse(shopNode).toList

    val allLists = list.filter(getTotalTime(_) <= time)
    allLists.maxBy(_.length()).nodes().toList.map(_.getProperty("name").toString)
  }

  def getTotalTime(path: Path): Int = {
    path.relationships().foldLeft(0) {
      (result, rel) =>
        result + rel.getProperty("time").toString.toInt
    }
  }

  def close() {
    graph.shutdown()
  }
}

object Shop extends Label {
  def name: String = "Shop"
}

object Destination extends Label {
  def name: String = "Destination"
}

object DeliveryPath extends RelationshipType {
  def name: String = "DeliveryPath"
}

