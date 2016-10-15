package models.db

import java.util.UUID

/**
  * Created by Bulat on 03.10.2016.
  */
case class DataFlowNode(diagramId: UUID, id: UUID, category: String, data: String)
