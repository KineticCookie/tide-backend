package models.ast

import java.util.UUID

/**
  * Created by Bulat on 15.10.2016.
  */
abstract class ASTNode(val id: UUID, val category: String, val children: Seq[ASTNode])

class DataNode(id: UUID, val dataSource: String, children: Seq[ASTNode]) extends ASTNode(id, "Data", children) {

}

class MapNode(id: UUID, val function: String, children: Seq[ASTNode]) extends ASTNode(id, "Map", children) {

}

class ReduceNode(id: UUID, val function: String, children: Seq[ASTNode]) extends ASTNode(id, "Reduce", children) {

}