package models.ast

import scala.collection.mutable

/**
  * Created by Bulat on 22.10.2016.
  */
trait CodeGenerator {
  def visit[T <: ASTNode](node: T): Unit
  def toSourceCode: String
}

class PythonGenerator extends CodeGenerator {
  val header =
    """import mist
      |class Job:
      |\tdef __init__(self, job):
      |\t\tjob.sendResult(self.perform(job))
      |\tdef perform(self, job):
      |\t\t""".stripMargin

  var stack = mutable.Stack[String]()

  override def visit[T <: ASTNode](node: T): Unit = {
    stack = mutable.Stack[String]()
    fillStack(node)
    stack = stack.reverse
  }

  def fillStack[T <: ASTNode](node: T) = {
    stack.push(dispatch(node))
    node.children foreach visit
  }

  def dispatch[T <: ASTNode](node: T): String = node match {
    case x: DataNode =>
      s"${getVariable(x)} = job.sc.textFile(job.parameters.apply(${x.id.toString}))"
    case x: MapNode =>
      s"${getVariable(x)} = ${getVariable(x.children.head)}.map(${x.function})"
    case x: ReduceNode =>
      s"${getVariable(x)} = ${getVariable(x.children.head)}.reduce(${x.function})"
  }

  def getVariable[T <: ASTNode](node: T): String = node match {
    case x: DataNode =>
      s"datanodes[${x.id.toString}]"
    case x: MapNode =>
      s"mapnodes[${node.id.toString}]"
    case x: ReduceNode =>
      s"reducenodes[${node.id.toString}]"
  }

  override def toSourceCode: String = {
    val sources = stack mkString "\n\t\t"
    header + sources
  }

}