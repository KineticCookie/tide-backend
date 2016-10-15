package models.ast

import java.util.UUID

/**
  * Created by Bulat on 15.10.2016.
  */
abstract class AstNode(val id: UUID, val category: String, val parent: Option[AstNode])

class DataNode(id: UUID, val dataSource: String) extends AstNode(id, "Data", None) {

}

class MapNode(id: UUID, parent: AstNode, val function: String) extends AstNode(id, "Map", Some(parent)) {

}

class ReduceNode(id: UUID, parent: AstNode, val function: String) extends AstNode(id, "Reduce", Some(parent)) {

}