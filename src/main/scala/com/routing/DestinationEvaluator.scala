package com.routing

import org.neo4j.graphdb.traversal.{Evaluation, Evaluator}
import org.neo4j.graphdb.Path
import scala.collection.JavaConversions._

class DestinationEvaluator(maxTime: Int) extends Evaluator {

  def evaluate(path: Path): Evaluation = {
    if(path.startNode().getRelationships(DeliveryPath).toList.length > 0) Evaluation.INCLUDE_AND_CONTINUE
    else Evaluation.INCLUDE_AND_PRUNE
  }
}
