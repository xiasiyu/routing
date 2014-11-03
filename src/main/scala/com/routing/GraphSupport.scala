package com.routing

import org.neo4j.graphdb._
import org.neo4j.tooling.GlobalGraphOperations
import scala.collection.JavaConversions._

class GraphSupport(val graph: GraphDatabaseService) {

  def findNode(name: String, label: Label) = {
    GlobalGraphOperations.at(graph).getAllNodesWithLabel(label).toList.find(_.getProperty("name") == name)
  }

  def createNode(name: String, label: Label): Node = {
    val node: Node = graph.createNode(label)
    node.setProperty("name", name)
    node
  }

  def setOrder(shop: String, destination: String, timeFromShopToDestination: Int, numbers: Int) {
    val tx = graph.beginTx()
    val shopNode = findNode(shop, Shop).getOrElse(createNode(shop, Shop))
    val destinationNode = findNode(destination, Destination).getOrElse(createNode(destination, Destination))

    val to = shopNode.createRelationshipTo(destinationNode, DeliveryPath)
    to.setProperty("time", timeFromShopToDestination)
    to.setProperty("numbers", numbers)
    tx.success()
    tx.close()
  }

  def getOrderDeliveryTime(order: Order): Int = {
    val tx: Transaction = graph.beginTx()
    val shopNode = findNode(order.shop, Shop).get
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

