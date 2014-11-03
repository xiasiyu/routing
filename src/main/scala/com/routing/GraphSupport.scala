package com.routing

import org.neo4j.graphdb._
import org.neo4j.tooling.GlobalGraphOperations
import scala.collection.JavaConversions._

class GraphSupport(val graph: GraphDatabaseService) {

  def findShop(shop: String):Option[Node] = {
    GlobalGraphOperations.at(graph).getAllNodesWithLabel(Shop).toList.find(_.getProperty("name") == shop)
  }

  def findDestination(destination: String):Option[Node] = {
    GlobalGraphOperations.at(graph).getAllNodesWithLabel(Destination).toList.find(_.getProperty("name") == destination)
  }

  def setOrder(shop: String, destination: String, timeFromShopToDestination: Int, numbers: Int) {
    val tx = graph.beginTx()
    val shopNode = findShop(shop).getOrElse({
      val node = graph.createNode(Shop)
      node.setProperty("name", shop)
      node
    })

    val destinationNode = findDestination(destination).getOrElse({
      val node = graph.createNode(Destination)
      node.setProperty("name", destination)
      node
    })

    val to = shopNode.createRelationshipTo(destinationNode, DeliveryPath)
    to.setProperty("time", timeFromShopToDestination)
    to.setProperty("numbers", numbers)
    tx.success()
    tx.close()
  }

  def getOrderDeliveryTime(order: Order): Int = {
    val tx: Transaction = graph.beginTx()
    val shopNode = findShop(order.shop).get
    val time = shopNode.getRelationships(Direction.OUTGOING, DeliveryPath).toList.find(_.getEndNode.getProperty("name") == order.destination).get.getProperty("time").toString.toInt
    tx.success()
    tx.close()
    time
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

