package com.routing

import org.neo4j.graphdb.GraphDatabaseService

trait TransactionSupport {

  protected def transaction[A <: Any](db: GraphDatabaseService)(dbOp: => A): A = {
    val tx = db.beginTx()
    try {
      val result = dbOp
      tx.success()
      result
    } finally {
      tx.close()
    }
  }
}
